package com.czs.gtd;

import java.util.Random;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.util.ExitUtil;
import com.czs.gtd.util.GTDUtil;

public class SplashActivity extends Activity
{
    private AlphaAnimation animation = null;     // 闪屏
    private TextView wisdomTexView = null; // 名言警局
    private SharedPreferences sharedPreferences = null;  
    private RelativeLayout backgroundRelativeLayout = null; // 背景
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ExitUtil.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        wisdomTexView = (TextView)findViewById(R.id.tv_wisdom);
        backgroundRelativeLayout = (RelativeLayout)findViewById(R.id.rlt_splash_body);
        
        // 名言
        String wisdoms[] = getResources().getStringArray(R.array.wisdom);
        Random random = new Random(System.currentTimeMillis());
        wisdomTexView.setText(wisdoms[random.nextInt(wisdoms.length)]);
        
        // 背景
        int ids[] = {R.drawable.hold_splash_bg1,R.drawable.hold_splash_bg2,R.drawable.hold_splash_bg3,R.drawable.hold_splash_bg4,R.drawable.hold_splash_bg5 };
        backgroundRelativeLayout.setBackgroundResource(ids[random.nextInt(5)]);
        
        animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000 * (random.nextInt(5) + 1));
        animation.setAnimationListener(new AnimationListener()
        {
            public void onAnimationStart(Animation animation)
            {
            }
            
            public void onAnimationRepeat(Animation animation)
            {
            }
            
            public void onAnimationEnd(Animation animation)
            {
                sharedPreferences = GTDUtil.getSharedPreferences(SplashActivity.this);
                if (sharedPreferences.contains(GTDConstants.XML_IS_EXIT)) // 判断是否第一次登录
                {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));   
                }
                else
                {
                    Editor editor = sharedPreferences.edit();
                    editor.putBoolean(GTDConstants.XML_IS_EXIT, false);
                    editor.commit();
                    startActivity(new Intent(SplashActivity.this, IntroduceActivity.class).putExtra(GTDConstants.KEY_INTRO_IS_FIRST, true));
                }
                unregisterReceiver(broadcastReceiver);
                finish();
            }
        });
        wisdomTexView.setAnimation(animation);
   
    }
    
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    };
    
    protected void onDestroy()
    {
        super.onDestroy();
        animation = null;
        wisdomTexView = null;
    };
    
    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("");
        this.registerReceiver(this.broadcastReceiver, filter);
    }
}
