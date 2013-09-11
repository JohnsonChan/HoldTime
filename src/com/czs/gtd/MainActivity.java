package com.czs.gtd;

import java.util.ArrayList;
import java.util.List;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.data.TabItem;
import com.czs.gtd.util.ExitUtil;

public class MainActivity extends TabActivity
{
    private LayoutInflater layoutInflater;
    private List<TabItem> items = null;
    private TextView centerTextView = null;
    private ImageView foldImageView = null;
    private ImageView editImageView = null;
    private TabHost tabHost = null;
    private boolean datedIsOpen = false;
    private boolean finishIsOpen = false;
    private boolean unfinishIsOpen = false;
    private GestureDetector gestureDetector; // 用户滑动
    private int flaggingWidth = 0;
    private int currentTabID = 0;
    
    private class TabHostTouch extends SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())
                    && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1.getX() - e2.getX() >= flaggingWidth))
            {
                if (e1.getX() - e2.getX() <= (-flaggingWidth))
                {
                    currentTabID = tabHost.getCurrentTab() - 1;
                    if (currentTabID < 0)
                    {
                        currentTabID = 4;
                    }
                    tabHost.setCurrentTab(currentTabID);
                    return true;
                }
                else if (e1.getX() - e2.getX() >= flaggingWidth)
                {
                    currentTabID = tabHost.getCurrentTab() + 1;
                    if (currentTabID >= 5)
                    {
                        currentTabID = 0;
                    }
                    tabHost.setCurrentTab(currentTabID);
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (gestureDetector.onTouchEvent(event))
        {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        gestureDetector = new GestureDetector(new TabHostTouch());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        flaggingWidth = dm.widthPixels / 3;
        
        ExitUtil.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Tabhost);
        setContentView(R.layout.activity_main);
        layoutInflater = getLayoutInflater();
        tabHost = getTabHost();
        
        TabItem unfinishItem = new TabItem(getString(R.string.tab_unfinish), R.drawable.tab_unfinish, R.drawable.tab_item_bg, new Intent(this,
                UnFinishActivity.class));
        TabItem finishItem = new TabItem(getString(R.string.tab_finish), R.drawable.tab_finish, R.drawable.tab_item_bg, new Intent(this,
                FinishActivity.class));
        TabItem datedItem = new TabItem(getString(R.string.tab_dated), R.drawable.tab_dated, R.drawable.tab_item_bg, new Intent(this,
                DatedActivity.class));
        TabItem settingsItem = new TabItem(getString(R.string.tab_settings), R.drawable.tab_settings, R.drawable.tab_item_bg, new Intent(this,
                SettingsActivity.class));
        TabItem moreItem = new TabItem(getString(R.string.tab_more), R.drawable.tab_more, R.drawable.tab_item_bg,
                new Intent(this, MoreActivity.class));
        items = new ArrayList<TabItem>();
        items.add(unfinishItem);
        items.add(datedItem);
        items.add(finishItem);
        items.add(settingsItem);
        items.add(moreItem);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.tab_top);
        View titleView = layoutInflater.inflate(R.layout.activity_main_top, null);
        layout.addView(titleView);
        
        for (int i = 0; i < items.size(); i++)
        {
            View tabItem = layoutInflater.inflate(R.layout.activity_main_tab_item, null); // �������
            TextView textView = (TextView) tabItem.findViewById(R.id.tab_item_tv); // �����е�textview
            setTabItemTextView(textView, i);
            TabSpec tabSpec = tabHost.newTabSpec(items.get(i).getTitle()); // ��������������
            tabSpec.setIndicator(tabItem); //
            tabSpec.setContent(items.get(i).getIntent()); // ����intent
            tabHost.addTab(tabSpec);
        }
        
        tabHost.setCurrentTab(0);
        currentTabID = 0;
        centerTextView = (TextView) titleView.findViewById(R.id.tv_title_center); // ������ʾΪ
        centerTextView.setText(R.string.tab_unfinish);
        editImageView = (ImageView) findViewById(R.id.iv_edit_left);
        editImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
            }
        });
        
        foldImageView = (ImageView) findViewById(R.id.iv_fold_right);
        foldImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (tabHost.getCurrentTab())
                {
                    case 0:
                        unfinishIsOpen = unfinishIsOpen ? false : true;
                        foldImageView.setImageResource(unfinishIsOpen ? R.drawable.title_right_open : R.drawable.title_right_close);
                        sendBroadcast(new Intent().setAction(unfinishIsOpen ? GTDConstants.UNFINISH_EXPAND : GTDConstants.UNFINISH_FLOD));
                    break;
                    case 1:
                        datedIsOpen = datedIsOpen ? false : true;
                        foldImageView.setImageResource(datedIsOpen ? R.drawable.title_right_open : R.drawable.title_right_close);
                        sendBroadcast(new Intent().setAction(datedIsOpen ? GTDConstants.DATED_EXPAND : GTDConstants.DATED_FLOD));
                    break;
                    case 2:
                        finishIsOpen = finishIsOpen ? false : true;
                        foldImageView.setImageResource(finishIsOpen ? R.drawable.title_right_open : R.drawable.title_right_close);
                        sendBroadcast(new Intent().setAction(finishIsOpen ? GTDConstants.FINISH_EXPAND : GTDConstants.FINISH_FLOD));
                    break;
                }
            }
        });
        tabHost.setOnTabChangedListener(new OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                currentTabID = tabHost.getCurrentTab();
                centerTextView.setText(tabId);
                if (tabId.equals(getString(R.string.tab_settings)) || tabId.equals(getString(R.string.tab_more)))
                {
                    foldImageView.setVisibility(View.GONE);
                }
                else
                {
                    switch (tabHost.getCurrentTab())
                    {
                        case 0:
                            foldImageView.setImageResource(unfinishIsOpen ? R.drawable.title_right_open : R.drawable.title_right_close);
                        break;
                        case 1:
                            foldImageView.setImageResource(finishIsOpen ? R.drawable.title_right_open : R.drawable.title_right_close);
                        break;
                        case 2:
                            foldImageView.setImageResource(datedIsOpen ? R.drawable.title_right_open : R.drawable.title_right_close);
                        break;
                    }
                    foldImageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    protected void setTabItemTextView(TextView textView, int position)
    {
        textView.setPadding(3, 3, 3, 3);
        textView.setText(items.get(position).getTitle());
        textView.setBackgroundResource(items.get(position).getBg());
        textView.setCompoundDrawablesWithIntrinsicBounds(0, items.get(position).getIcon(), 0, 0); 
    }
}
