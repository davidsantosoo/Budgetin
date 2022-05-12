package com.hfad.budgetin.ui.Stock;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.budgetin.R;
import com.hfad.budgetin.adapter.TransactionStockAdapter;

import com.hfad.budgetin.model.StockData;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionStockActivity extends AppCompatActivity {


    private TextView totalTransactionStock,filterEdt;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TransactionStockAdapter transactionStockAdapter;
     List<StockData> dataList;
    Context context;
    private FirebaseAuth mAuth;
    SearchView searchView;
    private String onlineUserId = "";
    private DatabaseReference todayRef;
    private Button datePickBtn,cariBtn;
    Calendar cal = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_stock);
        context = this;
        totalTransactionStock = findViewById(R.id.totalWeekSpentOn);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);


        filterEdt = findViewById(R.id.filterDate);
        datePickBtn = findViewById(R.id.datePickBtn);
        cariBtn = findViewById(R.id.cari);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        todayRef = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        
        dataList = new ArrayList<>();
        transactionStockAdapter =  new TransactionStockAdapter(TransactionStockActivity.this,dataList);
        recyclerView.setAdapter(transactionStockAdapter);

        readTransaction();

        datePickBtn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog =new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    cal.set(year, month, dayOfMonth);
                    filterEdt.setTextColor(Color.parseColor("#ffbf5d"));
                    filterEdt.setText(dateFormat.format(cal.getTime()));

                }
            }, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
            transactionStockAdapter.getFilter().filter(newText.toString());
                return false;
            }
        });
        cariBtn.setOnClickListener(v -> FilterDate(dateFormat.format(cal.getTime())));


    }

    private void FilterDate(String newText){
        List<StockData> filteredList =  new ArrayList<>();
        for (StockData data: dataList){
            if (data.getItemDate().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(data);
            }
        }
        transactionStockAdapter.filterList(filteredList);
        }

    private void readTransaction() {
        todayRef = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        Query query = todayRef.orderByChild("itemNames");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    StockData data = dataSnapshot.getValue(StockData.class);
                    dataList.add(data);
                }
                transactionStockAdapter = new TransactionStockAdapter(context,dataList);
                recyclerView.setAdapter(transactionStockAdapter);
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemPrice");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;

                        totalTransactionStock.setText("Stock Purchase: " + formatRupiah(totalAmount));
                    }
                }
                else {
                    totalTransactionStock.setText("Stock Purchase: Rp " + String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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