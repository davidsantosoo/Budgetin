package com.hfad.budgetin.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.StockData;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class TransactionStockAdapter extends RecyclerView.Adapter<TransactionStockAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    List<StockData> dataList;
    List<StockData> dataListFull;
    private Uri imageUri;
    private String post_key = "";
    private String category = "";
    private String name = "";
    private String code = "";
    private String size = "";
    private String image = "";
    private int qty = 0;
    private int amount = 0;
    Calendar cal = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");

    public TransactionStockAdapter(Context mContext, List<StockData> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        dataListFull = new ArrayList<>(dataList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_stock, parent, false);
        return new TransactionStockAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionStockAdapter.ViewHolder holder, int position) {
        final StockData data = dataList.get(position);

        holder.itemWeek.setText("Category: " + data.getItemCategory());
        holder.amountWeek.setText("Amount: " + formatRupiah(data.getItemPrice()));
        holder.dateWeek.setText("Date: " + data.getItemDate());
        holder.notesWeek.setText("Item Names: " + data.getItemNames());
        holder.itemCode.setText("CODE: " + data.getItemCode());
        holder.itemQuantity.setText("Stock: " + data.getItemStock());
        holder.itemSize.setText("Size: " + data.getItemSize());
        Picasso.get().load(data.getItemImage()).fit().centerCrop().into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            post_key = data.getId();
            category = data.getItemCategory();
            amount = data.getItemPrice();
            name = data.getItemNames();
            code = data.getItemCode();
            qty = data.getItemStock();
            size = data.getItemSize();
            image = data.getItemImage();
            updateData();
        });
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recylerview_anim_1));
    }

    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.update_stock_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mName = mView.findViewById(R.id.itemName);
        final ImageView mImageView = mView.findViewById(R.id.iv_stock_update);
        final EditText mCategoryUpdate = mView.findViewById(R.id.categoryUpdate);
        final Button dateBtn = mView.findViewById(R.id.calendarBtn);
        final EditText dateEdt = mView.findViewById(R.id.dateEdt);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNameUpdate = mView.findViewById(R.id.itemNameUpdate);
        final EditText mCode = mView.findViewById(R.id.code);
        final EditText mQty = mView.findViewById(R.id.qty);
        final EditText mSize = mView.findViewById(R.id.sizeUpdate);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            dateEdt.setTextColor(Color.parseColor("#193651"));
            dateEdt.setText(dateFormat.format(cal.getTime()));
        };
        dateBtn.setOnClickListener(v -> new DatePickerDialog(mContext, date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show());

        mName.setText(name);

        Picasso.get().load(image).into(mImageView);
        mCategoryUpdate.setText(String.valueOf(category));
        mCategoryUpdate.setSelection(String.valueOf(category).length());

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mNameUpdate.setText(name);
        mNameUpdate.setSelection(name.length());

        mSize.setText(size);
        mSize.setSelection(size.length());

        mCode.setText(code);
        mCode.setSelection(code.length());

        mQty.setText(String.valueOf(qty));
        mQty.setSelection(String.valueOf(qty).length());


        Button delBut = mView.findViewById(R.id.deleteBtn);
        Button updBut = mView.findViewById(R.id.updateBtn);

        updBut.setOnClickListener(v -> {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Stock");
            StorageReference fileRef = mStorageRef.child("Stock");
            category = mCategoryUpdate.getText().toString();
            amount = Integer.parseInt(mAmount.getText().toString());
            name = mName.getText().toString();
            name = mNameUpdate.getText().toString();
            code = mCode.getText().toString();
            qty = Integer.parseInt(mQty.getText().toString());
            size = mSize.getText().toString();


            StockData stockData = new StockData(category, dateFormat.format(cal.getTime()), post_key, name, code, size, amount, qty, image);
            DatabaseReference todayRef = FirebaseDatabase.getInstance().getReference("Stock").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            todayRef.child(post_key).setValue(stockData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });


            dialog.dismiss();
        });

        delBut.setOnClickListener(v -> {
            DatabaseReference todayRef = FirebaseDatabase.getInstance().getReference("Stock").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            todayRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void filterList(List<StockData> filteredList) {
        dataList.clear();
        dataList = filteredList;
        notifyDataSetChanged();

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String inputSearch = constraint.toString().toLowerCase();
            List<StockData> filteredList = new ArrayList<>();
            if (inputSearch.length() == 0 || inputSearch.isEmpty()) {
                filteredList.addAll(dataListFull);
            } else {
                for (StockData stockData : dataListFull) {
                    if (stockData.getItemNames().toLowerCase().contains(inputSearch)) {
                        filteredList.add(stockData);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataList.clear();
            dataList.addAll((Collection<? extends StockData>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemWeek, amountWeek, dateWeek, notesWeek, itemCode, itemQuantity, itemSize;
        public ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemWeek = itemView.findViewById(R.id.txtItemCategory);
            amountWeek = itemView.findViewById(R.id.txtItemPrice);
            dateWeek = itemView.findViewById(R.id.txtItemDate);
            notesWeek = itemView.findViewById(R.id.txtItemName);
            imageView = itemView.findViewById(R.id.iv_stock);
            itemCode = itemView.findViewById(R.id.txtItemCode);
            itemQuantity = itemView.findViewById(R.id.txtQuantity);
            itemSize = itemView.findViewById(R.id.txtItemSize);


        }

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
