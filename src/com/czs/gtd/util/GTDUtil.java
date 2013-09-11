package com.czs.gtd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.czs.gtd.R;
import com.czs.gtd.api.CornerListView;
import com.czs.gtd.api.CustomProgressDialog;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.data.TaskProperty;

public class GTDUtil
{
	private static CustomProgressDialog progressDialog = null;
	private static SharedPreferences sharedPreferences = null;
	public static String[] taskTypeName = null;
	public static Map<String, Integer> taskTypeMap = null;
	public static int[] taskTypeImgId = null;
	public static Map<String, Integer> taskTypePlay = null;

	public static void startProgressDialog(Context context, String tipString)
	{
		if (progressDialog == null)
		{
			progressDialog = CustomProgressDialog.createDialog(context);
			progressDialog.setMessage(tipString);
		}
		progressDialog.show();
	}

	public static void stopProgressDialog()
	{
		if (progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	static
	{
		taskTypeImgId = new int[]
		{ R.drawable.type_default_study, R.drawable.type_default_life, R.drawable.type_default_work, R.drawable.type_default_personal,
				R.drawable.type_default_other, R.drawable.task_type001, R.drawable.task_type002, R.drawable.task_type003, R.drawable.task_type004,
				R.drawable.task_type005, R.drawable.task_type006, R.drawable.task_type007, R.drawable.task_type008, R.drawable.task_type009,
				R.drawable.task_type010, R.drawable.task_type011, R.drawable.task_type012, R.drawable.task_type013, R.drawable.task_type014,
				R.drawable.task_type015, R.drawable.task_type016, R.drawable.task_type017, R.drawable.task_type018, R.drawable.task_type019,
				R.drawable.task_type020, R.drawable.task_type021, R.drawable.task_type022, R.drawable.task_type023, R.drawable.task_type024,
				R.drawable.task_type025, R.drawable.task_type026, R.drawable.task_type027, R.drawable.task_type028, R.drawable.task_type029,
				R.drawable.task_type030, R.drawable.task_type031, R.drawable.task_type032, R.drawable.task_type033, R.drawable.task_type034,
				R.drawable.task_type035, R.drawable.task_type036, R.drawable.task_type037, R.drawable.task_type038, R.drawable.task_type039,
				R.drawable.task_type040, R.drawable.task_type041, R.drawable.task_type042, R.drawable.task_type043, R.drawable.task_type044,
				R.drawable.task_type045, R.drawable.task_type046, R.drawable.task_type047, R.drawable.task_type048, R.drawable.task_type049,
				R.drawable.task_type050, R.drawable.task_type051, R.drawable.task_type052, R.drawable.task_type053, R.drawable.task_type054,
				R.drawable.task_type055, R.drawable.task_type056, R.drawable.task_type057, R.drawable.task_type058, R.drawable.task_type059,
				R.drawable.task_type060, R.drawable.task_type061, R.drawable.task_type062, R.drawable.task_type063, R.drawable.task_type064,
				R.drawable.task_type065, R.drawable.task_type066, R.drawable.task_type067, R.drawable.task_type068, R.drawable.task_type069,
				R.drawable.task_type070, R.drawable.task_type071, R.drawable.task_type072, R.drawable.task_type073, R.drawable.task_type074,
				R.drawable.task_type075, R.drawable.task_type076, R.drawable.task_type077, R.drawable.task_type078, R.drawable.task_type079,
				R.drawable.task_type080, R.drawable.task_type081, R.drawable.task_type082, R.drawable.task_type083, R.drawable.task_type084,
				R.drawable.task_type085, R.drawable.task_type086, R.drawable.task_type087, R.drawable.task_type088, R.drawable.task_type089,
				R.drawable.task_type090, R.drawable.task_type091, R.drawable.task_type092, R.drawable.task_type093, R.drawable.task_type094,
				R.drawable.task_type095, R.drawable.task_type096, R.drawable.task_type097, R.drawable.task_type098, R.drawable.task_type099,
				R.drawable.task_type100, R.drawable.task_type101, R.drawable.task_type102, R.drawable.task_type103, R.drawable.task_type104,
				R.drawable.task_type105, R.drawable.task_type106, R.drawable.task_type107, R.drawable.task_type108, R.drawable.task_type109,
				R.drawable.task_type110, R.drawable.task_type111, R.drawable.task_type112, R.drawable.task_type113, R.drawable.task_type114,
				R.drawable.task_type115 };
	}

	public static void refreshType(TaskService taskService)
	{
		taskTypeName = stringListToArray(taskService.getAllTaskTypeList());
		taskTypeMap = taskService.getAllTaskType();
		taskTypePlay = taskService.getAllTaskTypePlay();
	}

	public static void init(Context context)
	{
		refreshType(TaskService.getInstance(context));
		getSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		if (!sharedPreferences.contains(GTDConstants.XML_IS_EXIT))
		{
			// 第一次写入默认数据
			editor.putBoolean(GTDConstants.XML_SINGLE, true);
			editor.putBoolean(GTDConstants.XML_REPEAT, false);
			editor.putInt(GTDConstants.XML_REPEATVALUE, 30);
			editor.putBoolean(GTDConstants.XML_SINGLE_COVER, true);
			editor.putBoolean(GTDConstants.XML_ISUNFINISHDATED, true);
			editor.putBoolean(GTDConstants.XML_SHOW_TOMORROW, true);
			editor.putInt(GTDConstants.XML_RANK_VALUE, 0);
			editor.putBoolean(GTDConstants.XML_IS_VIP, false);
			editor.commit();
		}
	}

	public static String[] getPriorityStrings(Context context)
	{
		return context.getResources().getStringArray(R.array.prioritys);
	}

	public static void toastTip(Context context, int id)
	{
		Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show();
	}

	public static void toastTip(Context context, String string)
	{
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

	public static void setListViewHeight(CornerListView lv)
	{
		SimpleAdapter la = (SimpleAdapter) lv.getAdapter();
		if (null == la)
		{
			return;
		}
		// calculate height of all items.
		int h = 0;
		final int cnt = la.getCount();
		for (int i = 0; i < cnt; i++)
		{
			View item = la.getView(i, null, lv);
			item.measure(0, 0);
			h += item.getMeasuredHeight();
		}
		// reset ListView height
		ViewGroup.LayoutParams lp = lv.getLayoutParams();
		lp.height = h + (lv.getDividerHeight() * (cnt - 1));
		lv.setLayoutParams(lp);
	}

	public static SharedPreferences getSharedPreferences(Context context)
	{
		if (sharedPreferences == null)
		{
			sharedPreferences = context.getSharedPreferences(GTDConstants.XML_FILE_GTD, Context.MODE_PRIVATE);
		}
		return sharedPreferences;
	}

	public static String[] intToString(ArrayList<Integer> list)
	{
		String string[] = new String[list.size()];
		for (int i = 0; i < string.length; i++)
		{
			string[i] = Integer.toString(list.get(i));
		}
		return string;
	}

	public static String[] stringListToArray(ArrayList<String> list)
	{
		String string[] = new String[list.size()];
		for (int i = 0; i < string.length; i++)
		{
			string[i] = list.get(i);
		}
		return string;
	}

	public static void copyfile(File fromFile, File toFile, Context context, boolean isBackup)
	{
		if (!fromFile.exists())
		{
			if (isBackup)
			{
				toastTip(context, R.string.vip_no_data_record);
			} else
			{
				toastTip(context, R.string.vip_no_backup_data);
			}
			return;
		}
		if (!fromFile.isFile())
		{
			return;
		}
		if (!fromFile.canRead())
		{
			return;
		}
		if (!toFile.getParentFile().exists())
		{
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists())
		{
			toFile.delete();
		}
		try
		{
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0)
			{
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			if (isBackup)
			{
				toastTip(context, R.string.vip_backup_success);
			} else
			{
				toastTip(context, R.string.vip_recover_success);
			}

		} catch (Exception ex)
		{
			toastTip(context, R.string.vip_data_copy_error);
		}
	}

	public static String getType(TaskProperty taskProperty)
	{
		StringBuffer type = new StringBuffer(" ");
		switch (taskProperty)
		{
		case UNFINSH: //
			for (int i = 0; i < GTDUtil.taskTypeName.length; i++)
			{
				if ((GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i]) | 3) == 7)
				{
					type.append(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[i]) + ",");
				}
			}
			break;
		case FINISH:
			for (int i = 0; i < GTDUtil.taskTypeName.length; i++)
			{
				if ((GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i]) | 5) == 7)
				{
					type.append(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[i]) + ",");
				}
			}
			break;
		case DATED:
			for (int i = 0; i < GTDUtil.taskTypeName.length; i++)
			{
				if ((GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i]) | 6) == 7)
				{
					type.append(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[i]) + ",");
				}
			}
			break;
		}
		return type.substring(0, type.length() - 1);
	}

	public static String getPriority(TaskProperty taskProperty, SharedPreferences sharedPreferences)
	{
		StringBuffer priority = new StringBuffer(" ");
		switch (taskProperty)
		{
		case UNFINSH: //
			if (sharedPreferences.getBoolean("unfinish_pro0", true))
				priority.append("0,");
			if (sharedPreferences.getBoolean("unfinish_pro1", true))
				priority.append("1,");
			if (sharedPreferences.getBoolean("unfinish_pro2", true))
				priority.append("2,");
			if (sharedPreferences.getBoolean("unfinish_pro3", true))
				priority.append("3,");
			break;
		case FINISH:
			if (sharedPreferences.getBoolean("finish_pro0", true))
				priority.append("0,");
			if (sharedPreferences.getBoolean("finish_pro1", true))
				priority.append("1,");
			if (sharedPreferences.getBoolean("finish_pro2", true))
				priority.append("2,");
			if (sharedPreferences.getBoolean("finish_pro3", true))
				priority.append("3,");
			break;
		case DATED:
			if (sharedPreferences.getBoolean("dated_pro0", true))
				priority.append("0,");
			if (sharedPreferences.getBoolean("dated_pro1", true))
				priority.append("1,");
			if (sharedPreferences.getBoolean("dated_pro2", true))
				priority.append("2,");
			if (sharedPreferences.getBoolean("dated_pro3", true))
				priority.append("3,");
			break;
		}
		return priority.substring(0, priority.length() - 1);
	}
}
