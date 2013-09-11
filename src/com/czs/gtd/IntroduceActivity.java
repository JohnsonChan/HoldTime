package com.czs.gtd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.czs.gtd.api.MyScrollLayout;
import com.czs.gtd.api.OnViewChangeListener;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.util.ExitUtil;

public class IntroduceActivity extends Activity implements OnViewChangeListener
{
    private MyScrollLayout mScrollLayout;
    private ImageView[] imgs;
    private int count;
    private int currentItem;
    private LinearLayout pointLLayout;
    private LinearLayout animLayout;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ExitUtil.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initView();
    }
    
    private void initView()
    {
        mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
        pointLLayout = (LinearLayout) findViewById(R.id.llayout);
        animLayout = (LinearLayout) findViewById(R.id.animLayout);
        count = mScrollLayout.getChildCount();
        imgs = new ImageView[count];
        for (int i = 0; i < count; i++)
        {
            imgs[i] = (ImageView) pointLLayout.getChildAt(i);
            imgs[i].setEnabled(true);
            imgs[i].setTag(i);
        }
        currentItem = 0;
        imgs[currentItem].setEnabled(false);
        mScrollLayout.SetOnViewChangeListener(this);
    }
    
    
    @Override
    public void OnViewChange(int position)
    {
        setcurrentPoint(position);
    }
    
    private void setcurrentPoint(int position)
    {
    	if (position > count - 1)
    	{
    		mScrollLayout.setVisibility(View.GONE);
            pointLLayout.setVisibility(View.GONE);
            animLayout.setVisibility(View.VISIBLE);
            if (getIntent().getBooleanExtra(GTDConstants.KEY_INTRO_IS_FIRST, false))
            {
               startActivity(new Intent(IntroduceActivity.this, MainActivity.class));
            }
            IntroduceActivity.this.finish();
    	}
        if (position < 0 || position > count - 1 || currentItem == position)
        {
            return;
        }
        imgs[currentItem].setEnabled(true);
        imgs[position].setEnabled(false);
        currentItem = position;
    }
}