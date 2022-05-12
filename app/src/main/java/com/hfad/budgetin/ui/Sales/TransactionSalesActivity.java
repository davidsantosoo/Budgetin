package com.hfad.budgetin.ui.Sales;

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
import com.hfad.budgetin.adapter.TransactionSalesAdapter;
import com.hfad.budgetin.model.SalesData;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionSalesActivity extends AppCompatActivity {
    private TextView totalTransactionSales, filterEdt;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TransactionSalesAdapter transactionSalesAdapter;
    private List<SalesData> salesDataList;
    Context context;
    SearchView searchView;
    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference reference;
    private Button datePickBtn, cariBtn;
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_sales);
        context = this;

        totalTransactionSales = findViewById(R.id.totalSalesOn);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        filterEdt = findViewById(R.id.filterDate);
        datePickBtn = findViewById(R.id.datePickBtn);
        cariBtn = findViewById(R.id.cari);
        searchView = findViewById(R.id.searchView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);

        salesDataList = new ArrayList<>();
        transactionSalesAdapter = new TransactionSalesAdapter(TransactionSalesActivity.this, salesDataList);
        recyclerView.setAdapter(transactionSalesAdapter);

        readTransaction();
        datePickBtn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                cal.set(year, month, dayOfMonth);
                filterEdt.setTextColor(Color.parseColor("#193651"));
                filterEdt.setText(dateFormat.format(cal.getTime()));

            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionSalesAdapter.getFilter().filter(newText);

                return false;
            }
        });
        cariBtn.setOnClickListener(v -> FilterDate(dateFormat.format(cal.getTime())));

    }




    private void FilterDate(String newText) {
        List<SalesData> filteredList = new ArrayList<>();
        for (SalesData data : salesDataList) {
            if (data.getItemDate().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(data);
            }
        }
        transactionSalesAdapter.filterList(filteredList);
    }



    private void readTransaction() {
        reference = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);
        Query query = reference.orderByChild("itemNames");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                salesDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SalesData data = dataSnapshot.getValue(SalesData.class);
                    salesDataList.add(data);
                }
                transactionSalesAdapter = new TransactionSalesAdapter(context, salesDataList);
                recyclerView.setAdapter(transactionSalesAdapter);
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemPrice");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;

                        totalTransactionSales.setText("Total Sales: " + formatRupiah(totalAmount));
                    }
                } else {
                    totalTransactionSales.setText("Total Sales: Rp " + String.valueOf(0));
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