package com.hfad.budgetin.ui.Stock;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.StockData;
import com.hfad.budgetin.ui.Sales.analytics.PieChartValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class AnalyticsStock extends AppCompatActivity {
    private String onlineUserId = "";
    private FirebaseUser user;
    private String userID;
    private DatabaseReference userRef;
    PieChart pieChart;
    PieData pieData;
    TextView tvTotalStockPrice, tvTotalStock;
    int totalStockShoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_stock);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        userID = user.getUid();
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tvTotalStockPrice = findViewById(R.id.totalBudgetAmountTextView);
        tvTotalStock = findViewById(R.id.totalStock);
        pieChart = findViewById(R.id.pieChart);
        getStockAmount();
        getStockTotal();
    }

    private void getStockAmount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        Query query = reference.orderByChild("itemNames");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemPrice");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        tvTotalStockPrice.setText("Total Purchase: " + formatRupiah(totalAmount));
                    }
                } else {

                    tvTotalStockPrice.setText("Total Purchase:Rp " + String.valueOf(0));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getStockTotal() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        Query query = reference.orderByChild("itemNames");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int a = totalStockShoes;
                System.out.println(a);
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemStock");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        tvTotalStock.setText("Total item : " + totalAmount + "pcs");
                    }
                } else {
                    tvTotalStock.setText("Total item : 0 pcs");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        StockData stockData = ds.getValue(StockData.class);
                        int stock = stockData.getItemStock();
                        pieEntries.add(new PieEntry(stock, stockData.getItemCategory()));
                    }
                    displayChart(pieEntries);
                } else {
                    pieChart.clear();
                    pieChart.invalidate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void displayChart(ArrayList<PieEntry> pieEntries) {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setValueFormatter(new PieChartValueFormatter());
        pieDataSet.setColor(Color.parseColor("#193651"));
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12);
        final int[] MY_COLORS = {Color.rgb(192, 0, 0),
                Color.rgb(255, 0, 0),
                Color.rgb(255, 192, 0),
                Color.rgb(127, 127, 127),
                Color.rgb(146, 208, 80),
                Color.rgb(0, 176, 80),
                Color.rgb(79, 129, 189)};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        pieDataSet.setColors(colors);
        pieChart.clear();
        pieData = new PieData(pieDataSet);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(2500, Easing.EaseInOutQuad);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12);
        legend.setTextColor(Color.parseColor("#ffbf5d"));
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.invalidate();
    }

    private String formatRupiah(int number) {
        Locale localeID = new Locale("IND", "ID");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(localeID);
        String formatRupiah = numberFormat.format(number);
        String[] split = formatRupiah.split(",");
        int length = split[0].length();
        return split[0].substring(0, 2) + ". " + split[0].substring(2, length);
    }
}