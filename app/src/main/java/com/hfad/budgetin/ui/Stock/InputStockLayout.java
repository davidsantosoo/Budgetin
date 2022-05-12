package com.hfad.budgetin.ui.Stock;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.StockData;
import com.hfad.budgetin.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InputStockLayout extends AppCompatActivity {
    ProgressBar progressBar;
    public static final int PICK_IMAGE_REQUEST = 1;
    private Button  chooseImageBtn,cancelBtn;
    private Uri imageUri;
    private ImageView imageView;
    private StorageReference mStorageRef;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference stockRef;
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_stock_layout);
        context = this;
        progressBar = findViewById(R.id.progressBar);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Stock");
        stockRef = FirebaseDatabase.getInstance().getReference("Stock").child(onlineUserId);

        chooseImageBtn = findViewById(R.id.choose_image_btn);
        imageView = findViewById(R.id.iv_stock);
        Button dateBtn = findViewById(R.id.calendarBtn);
        EditText dateEdt = findViewById(R.id.dateEdt);
        EditText category = findViewById(R.id.todayItemCategory);
        EditText itemName = findViewById(R.id.todayItemName);
        EditText amount = findViewById(R.id.todayAmount);
        Button cancel = findViewById(R.id.cancelBtn);
        Button save = findViewById(R.id.saveBtn);
        EditText code = findViewById(R.id.todayCode);
        EditText quantity = findViewById(R.id.todayStock);
        EditText size = findViewById(R.id.todayItemSize);

        chooseImageBtn.setOnClickListener(v -> openFileChooser());

        dateBtn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    cal.set(year, month, dayOfMonth);
                    dateEdt.setTextColor(Color.parseColor("#193651"));
                    dateEdt.setText(dateFormat.format(cal.getTime()));

                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        save.setOnClickListener(v -> {
            String ItemNames = itemName.getText().toString();
            String Amount = amount.getText().toString();
            String codes = code.getText().toString();
            String qty = quantity.getText().toString();
            String ItemCategory = category.getText().toString();
            String sizes = size.getText().toString();

            if (TextUtils.isEmpty(ItemNames)) {
                itemName.setError("Amount is Required");
                itemName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(codes)) {
                code.setError("Amount is Required");
                code.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(Amount)) {
                amount.setError("Amount is Required");
                amount.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(sizes)) {
                size.setError("Amount is Required");
                size.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(qty)) {
                quantity.setError("Please Input Your Item Quantity");
                quantity.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(codes)) {
                code.setError("Please Input Your Item Code");
                code.requestFocus();
                return;
            }
            if (ItemCategory.equals("Select Item")) {
                Toast.makeText(InputStockLayout.this, "Select a Valid Item", Toast.LENGTH_SHORT).show();
            } else {
                loader.setMessage("Adding a  Item");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                String itemId = stockRef.push().getKey();
                int finalAmount = ((Integer.valueOf(Amount))) * Integer.valueOf(qty);
                if (imageUri !=null) {
                    StorageReference fileRef = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
                    fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0),500);
                        Toast.makeText(InputStockLayout.this, "Upload Successful",Toast.LENGTH_SHORT).show();
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Uri downloadedUrl = uri;
                            StockData data = new StockData(ItemCategory, dateFormat.format(cal.getTime()), itemId, ItemNames, codes, sizes,
                                    Integer.parseInt(String.valueOf(finalAmount)), Integer.parseInt(qty),downloadedUrl.toString() );

                            stockRef.child(itemId).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(InputStockLayout.this, " Item Added Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(InputStockLayout.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(InputStockLayout.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    loader.dismiss();
                                }
                            });
                        });

                    }).addOnFailureListener(e -> Toast.makeText(InputStockLayout.this, e.getMessage(),Toast.LENGTH_SHORT).show()).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });



                }else {

                    Toast.makeText(InputStockLayout.this,"No File Selected", Toast.LENGTH_SHORT).show();

                }

            }

        });

        cancel.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setMessage("Are you sure want to cancel?" + "\n"+
                        " Your data won't be saved!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputStockLayout.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show());
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime  = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).fit().centerCrop().into(imageView);

        }
    }
}