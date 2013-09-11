package com.czs.gtd.api;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.czs.gtd.R;

public class TimePickerPopupWindow extends PickerPopupWindow {
    public interface TimePickedListener {
        public void onTimePicked(int hour, int minute);
    }

    private WheelView hour;
    private WheelView minute;
    private TimePickedListener listener;

    public TimePickerPopupWindow(Context context, int initHour, int initMinute,
            TimePickedListener listener) {
        super(context);
        View view = LayoutInflater.from(context).inflate(
                R.layout.time_picker_popupwindow, null);
        hour = (WheelView) view.findViewById(R.id.wv_time_picker_hour);
        minute = (WheelView) view.findViewById(R.id.wv_time_picker_minute);
        hour.setViewAdapter(new NumericWheelAdapter(context, 0, 23,"%02d"));
        minute.setViewAdapter(new NumericWheelAdapter(context, 0, 59, "%02d"));
        minute.setCyclic(true);
        hour.setCurrentItem(initHour, false);
        minute.setCurrentItem(initMinute, false);
        this.listener = listener;
        this.setContentView(view);
    }

    @Override
    protected void onLeftButtonClick() {
        int hour = this.hour.getCurrentItem();
        int minute = this.minute.getCurrentItem();
        if (this.listener != null) {
            this.listener.onTimePicked(hour, minute);
        }
        super.onLeftButtonClick();

    }

    @Override
    protected String getTitle() {
        return "";
    }

}
