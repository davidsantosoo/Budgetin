package com.hfad.budgetin.ui.Sales.analytics;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class YearSalesChartFragment extends Fragment {

    View view;
    String onlineUserId = "";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference salesRef;
    LineChart lineChart;
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lineChart = getView().findViewById(R.id.lineChart);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onlineUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        salesRef = firebaseDatabase.getReference("Sales").child(onlineUserId);
        loadLineChart();
        view= inflater.inflate(R.layout.fragment_year_sales_chart, container, false);
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
                        int year = Integer.parseInt(date1[2]);
                        XAxis xAxis  = lineChart.getXAxis();
                        YAxis yAxisRight = lineChart.getAxisRight();
                        YAxis yAxisLeft = lineChart.getAxisRight();
                        yAxisLeft.getMaxWidth();
                        yAxisRight.setEnabled(false);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setValueFormatter(new MyAxisValueFormatter());
                        xAxis.setGranularity(1f);


                        entries.add(new Entry(year,salesData.getItemPrice()));


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
        LineDataSet lineDataSet = new LineDataSet(entries, "Year Sales");
        lineDataSet.setCircleColor(Color.parseColor("#193651"));
        lineDataSet.setColor(Color.parseColor("#193651"));
        lineDataSet.setFillColor(Color.parseColor("#193651"));
        lineDataSet.setValueTextSize(10);
        lineDataSet.setLineWidth(5);
        lineDataSet.setDrawFilled(true);;
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        Legend legend = lineChart.getLegend();
        legend.setTextSize(12);
        lineChart.clear();
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(2000);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }
}