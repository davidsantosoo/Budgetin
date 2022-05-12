package com.hfad.budgetin.ui.Sales;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.SalesData;
import com.hfad.budgetin.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InputSalesLayout extends AppCompatActivity {
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private String item = "";
    private int amount = 0;
    Context context;
    private DatabaseReference salesRef;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Calendar cal = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_sales_layout);
        context = this;
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        salesRef = FirebaseDatabase.getInstance().getReference("Sales").child(onlineUserId);

        Button dateBtn = findViewById(R.id.calendarBtn);
        EditText dateEdt = findViewById(R.id.dateEdt);
        Spinner itemSpinner = findViewById(R.id.itemSalesSpinner);
        EditText itemCategory = findViewById(R.id.salesCategory);
        EditText itemName = findViewById(R.id.salesItem);
        EditText amount = findViewById(R.id.salesPrice);
        Button cancel = findViewById(R.id.cancelBtn);
        Button save = findViewById(R.id.saveBtn);
        EditText code = findViewById(R.id.salesItemCode);
        EditText quantity = findViewById(R.id.salesQuantity);
        EditText size = findViewById(R.id.salesSize);
        dateBtn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    cal.set(year, month, dayOfMonth);
                    dateEdt.setTextColor(Color.parseColor("#ffbf5d"));
                    dateEdt.setText(dateFormat.format(cal.getTime()));
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        save.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            String Amount = amount.getText().toString();
            String codes = code.getText().toString();
            String qty = quantity.getText().toString();
            String ItemPaymentMethod = itemSpinner.getSelectedItem().toString();
            String category = itemCategory.getText().toString();
            String sizes = size.getText().toString();

            if (TextUtils.isEmpty(name)) {
                itemName.setError("Item Name is Required");
                itemName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(Amount)) {
                amount.setError("Amount is Required");
                amount.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(codes)) {
                code.setError("Code is Required");
                code.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(sizes)) {
                size.setError("Size is Required");
                size.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(qty)) {
                quantity.setError("Please Input Your Item Quantity");
                quantity.requestFocus();
                return;
            }

            if (ItemPaymentMethod.equals("Select Item")) {
                Toast.makeText(InputSalesLayout.this, "Select a Valid Payment Method", Toast.LENGTH_SHORT).show();
            } else {
                loader.setMessage("Adding a  Item");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                String id = salesRef.push().getKey();
                int finalAmount = ((Integer.valueOf(Amount))) * Integer.valueOf(qty);

                SalesData data = new SalesData(id, name, codes, sizes, dateFormat.format(cal.getTime()), ItemPaymentMethod,
                        Integer.parseInt(String.valueOf(finalAmount)), Integer.parseInt(qty), category);
                salesRef.child(id).setValue(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(InputSalesLayout.this, " Item Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InputSalesLayout.this, MainActivity.class));
                    } else {
                        Toast.makeText(InputSalesLayout.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                });
            }

        });

        cancel.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setMessage("Are you sure want to cancel?" + "\n" +
                        " Your data won't be saved!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputSalesLayout.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show());
    }

}