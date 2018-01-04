package com.czs.gtd.api;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import uncle.android.holdtime.R;


/**
 * Բ��ListView
 */
public class CornerListView extends ListView
{

	public CornerListView(Context context)
	{
		super(context);
	}

	public CornerListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public CornerListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int itemnum = pointToPosition(x, y);

			if (itemnum == AdapterView.INVALID_POSITION)
			{
				break;
			}
			else
			{
				if (itemnum == 0)
				{
					if (itemnum == (getAdapter().getCount() - 1))
					{
						setSelector(R.drawable.corner_single);  // һ��
					} else
					{
						setSelector(R.drawable.corner_top);   // ��
					}
				} 
				else if (itemnum == (getAdapter().getCount() - 1))
				{
					setSelector(R.drawable.corner_bottom);   // ��
				}
				else
				{
					setSelector(R.drawable.corner_medile);  // ��
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}