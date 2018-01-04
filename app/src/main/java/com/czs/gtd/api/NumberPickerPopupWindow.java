package com.czs.gtd.api;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import uncle.android.holdtime.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;



public class NumberPickerPopupWindow extends PickerPopupWindow
{
	public interface NumberPickListener
	{
		public void onNumberPick(int number);
	}

	private NumberPickListener listener;
	private WheelView numberPicker;
	private NumericWheelAdapter adapter;
	private int minValue;
	private int maxValue;

	public NumberPickerPopupWindow(Context context, String title, int minValue, int maxValue, NumberPickListener listener)
	{
		super(context);
		setTitle(title);

		View view = LayoutInflater.from(context).inflate(R.layout.numberpicker_popwindow, null);
		this.maxValue = maxValue;
		this.minValue = minValue;

		this.setContentView(view);
		numberPicker = (WheelView) view.findViewById(R.id.cv_numberpicker);
		numberPicker.setFocusable(true);
		numberPicker.setFocusableInTouchMode(true);
		this.listener = listener;
		adapter = new NumericWheelAdapter(context, minValue, maxValue);
		numberPicker.setViewAdapter(adapter);
	}

	@Override
	protected void onLeftButtonClick()
	{
		numberPicker.getCurrentItem();
		listener.onNumberPick(numberPicker.getCurrentItem() + minValue);
		super.onLeftButtonClick();
	}

	@Override
	protected String getTitle()
	{
		return "";
	}

	public void setInitValue(int i)
	{
		if (i >= minValue && i <= maxValue)
		{

			numberPicker.setCurrentItem(i - minValue, false);
		}

	}

}
