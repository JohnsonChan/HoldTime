package com.czs.gtd.api;

import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import uncle.android.holdtime.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;


public class DatePickerPopupWindow extends PickerPopupWindow implements OnWheelChangedListener
{
	public interface onDatePickedListener
	{
		public void onDatePicked(int year, int month, int day);
	}

	private onDatePickedListener listener;
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private String dayFormat;
	private int minYear;
	private Date maxDate;
	private String monthFormat;

	public DatePickerPopupWindow(Context context, int title, Date initDate, Date min, Date max, onDatePickedListener listener)
	{
		super(context);
		this.setTitle(context.getString(title));
		this.listener = listener;
		View view = LayoutInflater.from(context).inflate(R.layout.date_picker_popupwindow, null);
		this.setContentView(view);
		year = (WheelView) view.findViewById(R.id.wv_date_picker_year);
		month = (WheelView) view.findViewById(R.id.wv_date_picker_month);
		day = (WheelView) view.findViewById(R.id.wv_date_picker_day);

		Calendar calendarMin = Calendar.getInstance();
		calendarMin.setTime(min);
		this.maxDate = max;
		Calendar calendarMax = Calendar.getInstance();
		calendarMax.setTime(maxDate);
		String yearFormat = context.getResources().getString(R.string.date_picker_format_year);
		monthFormat = context.getResources().getString(R.string.date_picker_format_month);
		dayFormat = context.getResources().getString(R.string.date_picker_format_day);
		minYear = calendarMin.get(Calendar.YEAR);
		year.setViewAdapter(new NumericWheelAdapter(context, minYear, calendarMax.get(Calendar.YEAR), yearFormat));
		year.addChangingListener(this);

		month.setViewAdapter(new NumericWheelAdapter(context, 1, 12, monthFormat));
		month.addChangingListener(this);

		updateDays();

		Calendar calendarInit = Calendar.getInstance();
		calendarInit.setTime(initDate);
		int initYear = calendarInit.get(Calendar.YEAR);
		int initMonth = calendarInit.get(Calendar.MONTH);
		int initDay = calendarInit.get(Calendar.DATE);
		year.setCurrentItem(initYear - minYear);
		month.setCurrentItem(initMonth);
		day.setCurrentItem(initDay - 1);
	}

	private void updateMonth()
	{
		Calendar calendarMax = Calendar.getInstance();
		calendarMax.setTime(maxDate);
		int maxMonth;
		if (minYear + year.getCurrentItem() == calendarMax.get(Calendar.YEAR))
		{
			maxMonth = calendarMax.get(Calendar.MONTH) + 1;
		} else
		{
			maxMonth = 12;
		}
		month.setViewAdapter(new NumericWheelAdapter(context, 1, maxMonth, monthFormat));
		int curMonth = Math.min(maxMonth, month.getCurrentItem() + 1);
		month.setCurrentItem(curMonth - 1, true);
	}

	private void updateDays()
	{
		Calendar calendarMax = Calendar.getInstance();
		calendarMax.setTime(maxDate);
		int maxDays;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, minYear + year.getCurrentItem());
		calendar.set(Calendar.MONTH, month.getCurrentItem());
		if (minYear + year.getCurrentItem() == calendarMax.get(Calendar.YEAR) && month.getCurrentItem() == calendarMax.get(Calendar.MONTH))
		{
			maxDays = Math.min(calendar.getActualMaximum(Calendar.DAY_OF_MONTH), calendarMax.get(Calendar.DATE));
		} else
		{
			maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		day.setViewAdapter(new NumericWheelAdapter(context, 1, maxDays, dayFormat));
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
	}

	@Override
	protected String getTitle()
	{
		return "";
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue)
	{
		if (wheel == year)
		{
			updateMonth();
		}
		updateDays();
	}

	@Override
	protected void onLeftButtonClick()
	{
		if (listener != null)
		{
			listener.onDatePicked(minYear + year.getCurrentItem(), month.getCurrentItem() + 1, day.getCurrentItem() + 1);
		}
		super.onLeftButtonClick();

	}
}
