package com.hfad.budgetin.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.budgetin.R;
import com.hfad.budgetin.databinding.ActivityProfileBinding;
import com.hfad.budgetin.model.User;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding bind;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, salesRef, stockRef;
    private String userID;
    private TextView tvSales, tvStock;
    private String onlineUserId = "";
    private int TotalAmountSale =0;
    private int TotalAmountStock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        userID = user.getUid();
        tvSales = findViewById(R.id.totalSales);
        tvStock = findViewById(R.id.totalStock);
        final TextView usernameTxt = findViewById(R.id.txtUsername);
        final TextView emailTxt = findViewById(R.id.txtEmail);
        final TextView locTxt = findViewById(R.id.txtLocation);
        mAuth = FirebaseAuth.getInstance();
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        salesRef = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);
        stockRef = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        getSalesAmount();
        getStockAmount();

        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String username = userProfile.myUsername;
                    String email = userProfile.myEmail;
                    String location = userProfile.myLocation;
                    usernameTxt.setText(username + " Store");
                    emailTxt.setText(email);
                    locTxt.setText(location);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something Wrong Happen!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getStockAmount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);
        Query query= reference.orderByChild("itemNames");
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
                        tvStock.setText(formatRupiah(totalAmount));
                    }
                }
                else {
                    tvStock.setText("Rp "+String.valueOf(0));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getSalesAmount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);
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
                        tvSales.setText(formatRupiah(totalAmount));
                    }
                }
                else {
                    tvSales.setText("Rp "+String.valueOf(0));
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

    public void resetPassword(View view) {
        Intent i = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(i);
    }

    public void logOut(View v) {
         FirebaseAuth.getInstance().signOut();
         startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }
}