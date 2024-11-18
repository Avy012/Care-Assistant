package com.example.lighthouseofmemory;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;
public class EventDecorator implements DayViewDecorator {
    private final int color; // 색상
    private final HashSet<CalendarDay> dates; // 적용할 날짜

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day); // 이 데코레이터가 적용될 날짜
    }

    @Override
    public void decorate(DayViewFacade view) {
        Drawable background = new ColorDrawable(color); // 배경 색상 설정
        view.setBackgroundDrawable(background); // 배경 적용
    }
}