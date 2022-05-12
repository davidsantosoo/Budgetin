package com.hfad.budgetin.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.budgetin.R;
import com.hfad.budgetin.auth.LoginActivity;
import com.hfad.budgetin.auth.ProfileActivity;
import com.hfad.budgetin.model.User;
import com.hfad.budgetin.ui.Sales.InputSalesLayout;
import com.hfad.budgetin.ui.Sales.TransactionSalesActivity;
import com.hfad.budgetin.ui.Sales.analytics.AnalyticsSales;
import com.hfad.budgetin.ui.Stock.AnalyticsStock;
import com.hfad.budgetin.ui.Stock.InputStockLayout;
import com.hfad.budgetin.ui.Stock.TransactionStockActivity;
import com.hfad.budgetin.ui.tips.TipsActivity;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private LinearLayout inputStockBtn,
            transactionSalesBtn
            ,transactionStockBtn,
            stockAnalyticsBtn,
            inputSalesButton,
            salesAnalyticsBtn;
    private TextView tvSalesAmount, tvStockUser;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference userRef, salesRef, stockRef;
    private String onlineUserId = "";
    private FirebaseUser user;
    private String userID;



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        salesAnalyticsBtn  = findViewById(R.id.sales_analytics_btn);
        inputStockBtn = findViewById(R.id.input_stock_button);
        transactionSalesBtn = findViewById(R.id.transaction_sales_btn);
        transactionStockBtn = findViewById(R.id.transaction_stock_btn);
        stockAnalyticsBtn = findViewById(R.id.stock_analysis_button);
        tvSalesAmount = findViewById(R.id.tv_salesBalance);
        tvStockUser = findViewById(R.id.tv_stockBalance);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        salesRef = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);
        stockRef = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        inputSalesButton = findViewById(R.id.input_Sales_button);

        checkUser();
        getStockAmount();
        getSalesAmount();

        inputStockBtn.setOnClickListener(v -> startActivity(new
                Intent(MainActivity.this,
                InputStockLayout.class)));
        transactionSalesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionSalesActivity.class);
            startActivity(intent);

        });
        transactionStockBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionStockActivity.class);
            startActivity(intent);

        });
        stockAnalyticsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnalyticsStock.class);
            startActivity(intent);

        });
        salesAnalyticsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnalyticsSales.class);
            startActivity(intent);
        });

        inputSalesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InputSalesLayout.class);
            startActivity(intent);
        });

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setItemBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    break;
                case R.id.nav_home:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    break;
                case R.id.nav_tips:
                    startActivity(new Intent(MainActivity.this, TipsActivity.class));
                    break;

            }
            return true;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        userID = user.getUid();
        final TextView usernameTxt = findViewById(R.id.txtUsername);
        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String username = userProfile.myUsername;
                    usernameTxt.setText("Hello, "+ username + " Store");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something Wrong Happen!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void getStockAmount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stock")
                .child(onlineUserId);
        Query query= reference.orderByChild("itemNames");
        query.addValueEventListener(new ValueEventListener() {
            @Override //nampung data dari model
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemPrice");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        tvStockUser.setText(formatRupiah(totalAmount));
                    }
                }
                else {
                    tvStockUser.setText("Rp "+String.valueOf(0));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getSalesAmount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sales")
                .child(onlineUserId);
        Query query= reference.orderByChild("itemName");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("itemPrice");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        tvSalesAmount.setText(formatRupiah(totalAmount));
                    }
                }
                else {
                    tvSalesAmount.setText("Rp "+String.valueOf(0));
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