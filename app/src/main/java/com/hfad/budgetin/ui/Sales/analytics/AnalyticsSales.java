package com.hfad.budgetin.ui.Sales.analytics;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.SalesData;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class AnalyticsSales extends AppCompatActivity {
    String onlineUserId = "";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference salesRef;
    LineChart lineChart;
    PieChart pieChart;
    PieData pieData;
    Button  MonthBtn, yearBtn;
    TextView tvTotalSalesPrice, tvTotalSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_analytics_sales);
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        salesRef = firebaseDatabase.getReference("Sales").child(onlineUserId);

        lineChart = findViewById(R.id.lineChart);
        tvTotalSalesPrice = findViewById(R.id.totalBudgetAmountTextView);
        tvTotalSales = findViewById(R.id.totalStock);
        pieChart = findViewById(R.id.pieChart);
        MonthBtn = findViewById(R.id.btnFragmentMonthly);
        yearBtn = findViewById(R.id.btnFragmentYear);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);
        MonthBtn.setOnClickListener(v -> replaceFragment(new MonthlySalesChartFragment()));
        yearBtn.setOnClickListener(v -> replaceFragment(new YearSalesChartFragment()));

        getStockAmount();
        getStockTotal();
        LoadPieChart();

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.frameLayout, new MonthlySalesChartFragment()).commit();
        }
    }

    private void getStockAmount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);
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
                        tvTotalSalesPrice.setText("Total Sales: " + formatRupiah(totalAmount));
                    }
                } else {
                    tvTotalSalesPrice.setText("Total Sales:Rp " + String.valueOf(0));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getStockTotal() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);
        Query query = reference.orderByChild("itemNames");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemStock");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        tvTotalSales.setText("Total item : " + totalAmount + "pcs");
                    }
                } else {
                    tvTotalSales.setText("Total item : 0 pcs");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void LoadPieChart() {
        salesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();

                if (snapshot.hasChildren()) {
                    int stock =0;
                    String val ="Basketball";
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        SalesData salesData = ds.getValue(SalesData.class);

                        if (salesData.getItemCategory().equalsIgnoreCase(val)){
                            ArrayList<SalesData> array = new ArrayList<>();
                            SalesData dataItem = ds.getValue(SalesData.class);
                            array.add(dataItem);
                            for (SalesData data1 : array){
                                stock += data1.getItemStock();
                            }
                        }else {
                            pieEntries.add(new PieEntry(salesData.getItemStock(),salesData.getItemCategory()));
                        }

                    }
                    pieEntries.add(new PieEntry(stock, val));
                    displayPieChart(pieEntries);
                } else {
                    pieChart.clear();
                    pieChart.invalidate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void displayPieChart(ArrayList<PieEntry> pieEntries) {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setValueFormatter(new PieChartValueFormatter());
        pieDataSet.setColor(Color.parseColor("#193651"));
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10);
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
        pieChart.getDescription().setEnabled(false);
        pieData = new PieData(pieDataSet);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.parseColor("#193651"));
        legend.setTextSize(12);
        pieChart.animateY(2500, Easing.EaseInOutQuad);
        pieChart.setData(pieData);
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