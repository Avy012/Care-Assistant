package com.example.lighthouseofmemory;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class CircleDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private final Drawable backgroundDrawable;

    public CircleDecorator(CalendarDay date, int color) {
        this.date = date;
        this.backgroundDrawable = new ColorDrawable(color);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(backgroundDrawable);

    }
}
