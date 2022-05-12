package com.hfad.budgetin.ui.Sales.analytics;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class PieChartValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return (int) value + "pcs";
    }
}
