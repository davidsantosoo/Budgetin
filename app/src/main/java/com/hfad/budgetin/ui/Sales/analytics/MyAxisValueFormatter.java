package com.hfad.budgetin.ui.Sales.analytics;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyAxisValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value);

    }

}
