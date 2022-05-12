package com.hfad.budgetin.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.Spinner;
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
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.SalesData;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class TransactionSalesAdapter extends RecyclerView.Adapter<TransactionSalesAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<SalesData> salesDataList;
    private List<SalesData> salesDataListFull;
    private String post_key = "";
    private String name = "";
    private String size = "";
    private String code = "";
    private String category = "";
    private String paymentMethod ="";
    private String date ="" ;
    private int amount = 0;
    private int qty = 0;
    Locale id  = new Locale("in","ID");
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public TransactionSalesAdapter(Context mContext, List<SalesData> salesDataList){
        this.mContext = mContext;
        this.salesDataList = salesDataList;
        salesDataListFull = new ArrayList<>(salesDataList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_sales,parent,false);
        return new TransactionSalesAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionSalesAdapter.ViewHolder holder, int position) {
        final SalesData data = salesDataList.get(position);
        holder.itemName.setText("Item Name: " + data.getItemNames());
        holder.itemQuantity.setText("Quantity: " + data.getItemStock());
        holder.itemPrice.setText("Price: " + formatRupiah(data.getItemPrice()));
        holder.itemPaymentMethod.setText("Payment Method: " + data.getItemPaymentMethod());
        holder.itemCode.setText("Item Code: " + data.getItemCode());
        holder.itemDate.setText("Date: " + data.getItemDate());
        holder.itemSize.setText("Size: " + data.getItemSize());
        holder.itemCategory.setText("Category: "+data.getItemCategory());

        switch (data.getItemPaymentMethod()) {
            case "Cash":
                holder.imageViewSales.setImageResource(R.drawable.ic_cash_blue);
                break;
            case "E-wallet":
                holder.imageViewSales.setImageResource(R.drawable.ic_wallet_blue);
                break;

        }

        holder.itemView.setOnClickListener(v -> {
            post_key = data.getItemId();
            name = data.getItemNames();
            amount = data.getItemPrice();
            size = data.getItemSize();
            code = data.getItemCode();
            paymentMethod = data.getItemPaymentMethod();
            qty = data.getItemStock();
            date  =data.getItemDate();
            category = data.getItemCategory();
            updateSales();
        });
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recylerview_anim_1));

    }

    private void updateSales() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.update_sales_layout, null);
        myDialog.setView(mView);

        final AlertDialog dialog = myDialog.create();
        final Button dateBtn = mView.findViewById(R.id.calendarBtn);
        final TextView mItemName = mView.findViewById(R.id.itemName);
        final EditText mItemUpdateName = mView.findViewById(R.id.itemNamesUpdate);
        final EditText mCategory = mView.findViewById(R.id.itemCategoryUpdate);
        final EditText mAmount = mView.findViewById(R.id.itemPriceUpdate);
        final EditText mDate = mView.findViewById(R.id.dateEdt);
        final EditText mQuantity = mView.findViewById(R.id.itemQuantityUpdate);
        final EditText mCode = mView.findViewById(R.id.itemCodeUpdate);
        final Spinner mPaymentMethod = mView.findViewById(R.id.itemSalesSpinnerUpdate);
        final EditText mSize = mView.findViewById(R.id.itemSizeUpdate);

        mItemName.setText(name);
        mPaymentMethod.setSelected(Boolean.parseBoolean(String.valueOf(paymentMethod)));

        mCategory.setText(String.valueOf(category));
        mCategory.setSelection(String.valueOf(category).length());

        mItemUpdateName.setText(String.valueOf(name));
        mItemUpdateName.setSelection(String.valueOf(name).length());

        mQuantity.setText(String.valueOf(qty));
        mQuantity.setSelection(String.valueOf(qty).length());

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mSize.setText(size);
        mSize.setSelection(size.length());

        mCode.setText(code);
        mCode.setSelection(code.length());

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,day);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",id);
                mDate.setTextColor(Color.parseColor("#193651"));
                mDate.setText(dateFormat.format(cal.getTime()));
            }
        };
        dateBtn.setOnClickListener(v -> new DatePickerDialog(mContext,date,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show());
        Button delBut = mView.findViewById(R.id.deleteBtn);
        Button updBut = mView.findViewById(R.id.updateBtn);
        updBut.setOnClickListener(v -> {
            category = mCategory.getText().toString();
            amount = Integer.parseInt(mAmount.getText().toString());
            name = mItemName.getText().toString();
            name = mItemUpdateName.getText().toString();
            code = mCode.getText().toString();
            qty  = Integer.parseInt(mQuantity.getText().toString());
            size = mSize.getText().toString();
            paymentMethod = mPaymentMethod.getSelectedItem().toString();
            SalesData salesData = new SalesData(post_key,name,code,size,dateFormat.format(cal.getTime()),paymentMethod,amount,qty,category);
            DatabaseReference salesRef = FirebaseDatabase.getInstance().getReference("Sales").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            salesRef.child(post_key).setValue(salesData).addOnCompleteListener(new OnCompleteListener<Void>() {
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
            DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference("Sales").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            incomeRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
      return salesDataList.size();
    }

    @Override
    public Filter getFilter() {
        return filtered;
    }
    Filter filtered = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String input = constraint.toString().toLowerCase();
            List<SalesData> filterList = new ArrayList<>();
            if (input.length() == 0 || input.isEmpty()) {
                filterList.addAll(salesDataListFull);
            } else {
                for (SalesData salesData : salesDataListFull) {
                    if (salesData.getItemNames().toLowerCase().contains(input)) {
                        filterList.add(salesData);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            salesDataList.clear();
            salesDataList.addAll((Collection<? extends SalesData>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView itemName, itemPrice, itemQuantity, itemPaymentMethod, itemCode, itemDate, itemSize,itemCategory;
        public ImageView imageViewSales;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.txtItemName);
            itemPrice = itemView.findViewById(R.id.txtItemPrice);
            itemDate = itemView.findViewById(R.id.txtItemDate);
            itemQuantity = itemView.findViewById(R.id.txtQuantity);
            itemCategory = itemView.findViewById(R.id.txtItemCategory);
            itemPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            itemCode = itemView.findViewById(R.id.txtItemCode);
            itemSize = itemView.findViewById(R.id.txtItemSize);
            imageViewSales = itemView.findViewById(R.id.imageViewSales);
        }
    }
    public void  filterList(List<SalesData> filteredList){
        salesDataList.clear();
        salesDataList = filteredList;
        notifyDataSetChanged();
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
