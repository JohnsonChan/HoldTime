package com.czs.gtd;

import android.app.Application;
import com.czs.gtd.util.GTDUtil;

public class GTDApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		GTDUtil.init(getApplicationContext());
	}
}
