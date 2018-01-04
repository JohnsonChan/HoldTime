package com.czs.gtd.api;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import uncle.android.holdtime.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;



public class ItemPickPopupWindow extends PickerPopupWindow
{
	private ItemSelectListener listener;
	private WheelView wv;
	private ArrayWheelAdapter<String> adapter;

	public interface ItemSelectListener
	{
		void onItemSelect(ItemPickPopupWindow window, int itemIndex);
	}

	public ItemPickPopupWindow(Context context, int title, int itemArrayId, ItemSelectListener listener)
	{
		this(context, context.getResources().getString(title), context.getResources().getStringArray(itemArrayId), listener);
	}

	public ItemPickPopupWindow(Context context, String title, int itemArrayId, ItemSelectListener listener)
	{
		this(context, title, context.getResources().getStringArray(itemArrayId), listener);
	}

	public ItemPickPopupWindow(Context context, int title, String[] items, ItemSelectListener listener)
	{
		this(context, context.getResources().getString(title), items, listener);
	}

	public ItemPickPopupWindow(Context context, String title, String[] items, ItemSelectListener listener)
	{
		super(context);
		this.setTitle(title);
		View view = LayoutInflater.from(context).inflate(R.layout.item_picker_popwindow, null);
		this.setContentView(view);
		wv = (WheelView) view.findViewById(R.id.wv_item_picker);
		adapter = new ArrayWheelAdapter<String>(context, items);
		wv.setViewAdapter(adapter);
		this.listener = listener;
	}

	public void setTextSize(int textSize)
	{
		adapter.setTextSize(textSize);
	}

	public void setInitItem(int index)
	{
		wv.setCurrentItem(index);
	}

	@Override
	protected void onLeftButtonClick()
	{

		listener.onItemSelect(this, wv.getCurrentItem());
		super.onLeftButtonClick();
	}

	@Override
	protected String getTitle()
	{
		return "";
	}

}
