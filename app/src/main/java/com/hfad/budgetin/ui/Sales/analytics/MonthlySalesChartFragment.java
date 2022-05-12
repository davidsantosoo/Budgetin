package com.hfad.budgetin.ui.Sales.analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.SalesData;

import java.util.ArrayList;


public class MonthlySalesChartFragment extends Fragment implements View.OnClickListener {
    EditText inputSalesYear;
    Button searchYearBtn;
    View view;
    String onlineUserId = "";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference salesRef;
    LineChart lineChart;
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monthly_sales_chart, container, false);
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        salesRef = firebaseDatabase.getReference("Sales").child(onlineUserId);
        lineChart = view.findViewById(R.id.lineChart);
        inputSalesYear = view.findViewById(R.id.inputYear);
        searchYearBtn = view.findViewById(R.id.searchMonth);
        searchYearBtn.setOnClickListener(this);
        return view;
    }


    private void loadLineChart() {
        salesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries = new ArrayList<>();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        SalesData salesData = ds.getValue(SalesData.class);
                        String date = salesData.getItemDate();
                        String[] date1 = date.split("-");
                        int month = Integer.parseInt(date1[1]);
                        int year = Integer.parseInt(date1[2]);

                        XAxis xAxis = lineChart.getXAxis();
                        YAxis yAxisRight = lineChart.getAxisRight();
                        yAxisRight.setEnabled(false);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setValueFormatter(new MyAxisValueFormatter());
                        xAxis.setGranularity(1f);

                        int inputYear = Integer.parseInt(inputSalesYear.getText().toString());
                        if (year == inputYear) {
                            entries.add(new Entry(month, salesData.getItemPrice()));
                            Toast.makeText(getActivity(), "monthly report in "+inputYear, Toast.LENGTH_SHORT).show();
                        }
                    }


                    showLineChart(entries);

                } else {

                    lineChart.clear();
                    lineChart.invalidate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showLineChart(ArrayList<Entry> entries) {
        LineDataSet lineDataSet = new LineDataSet(entries, "Monthly Sales");
        lineDataSet.setCircleColor(Color.parseColor("#193651"));
        lineDataSet.setColor(Color.parseColor("#193651"));
        lineDataSet.setFillColor(Color.parseColor("#193651"));
        lineDataSet.setValueTextSize(10);
        lineDataSet.setLineWidth(5);
        lineDataSet.setDrawFilled(true);
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        lineChart.clear();
        Legend legend = lineChart.getLegend();
        legend.setTextSize(12);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(2000);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchMonth:
                loadLineChart();
                break;
        }

    }

}

