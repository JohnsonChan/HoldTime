package com.czs.gtd.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ExitUtil
{
	private static List<Activity> mList = new ArrayList<Activity>();

	private static ExitUtil instance;

	private ExitUtil()
	{

	}

	public static ExitUtil getInstance()
	{
		if (null == instance)
		{
			instance = new ExitUtil();
		}
		return instance;
	}

	public void remove(Activity activity)
	{

		mList.remove(activity);

	}

	public void addActivity(Activity activity)
	{
		mList.add(activity);
	}

	public static void exit()
	{
		try
		{
			for (Activity activity : mList)
			{
				if (activity != null)
					activity.finish();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

}