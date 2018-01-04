package uncle.android.holdtime;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.data.Task;
import com.czs.gtd.util.GTDUtil;
import com.czs.gtd.util.TaskService;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver
{
	private static int CYCLE_ID = 0;
	private static int SINGLE_ID = 1;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) // 锟斤拷锟斤拷锟斤拷锟斤拷
		{
		    SharedPreferences sharedPreferences = GTDUtil.getSharedPreferences(context);
			if (sharedPreferences.getBoolean(GTDConstants.XML_SINGLE, false))
			{
				setSingleRemind(context);
			}
			if (sharedPreferences.getBoolean(GTDConstants.XML_REPEAT, false))
			{
				setCycleRemind(context);
			}
		} else if (action.equals(GTDConstants.ACTION_CYCLE_REMIND)) // 锟斤拷锟斤拷锟斤拷锟斤拷
		{
			TaskService taskService = TaskService.getInstance(context);
			String temp = taskService.datedNum(context);
			if (temp != null)
			{
				showNotification(context, temp, CYCLE_ID);
			}
			setCycleRemind(context); // 锟斤拷一锟斤拷锟斤拷锟斤拷
		}
		if (action.equals(GTDConstants.ACTION_SINGLE_REMIND)) // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
		{
			showNotification(context, intent.getStringExtra(GTDConstants.KEY_TASK_CONTENT), intent.getIntExtra(GTDConstants.KEY_TASK_ID, SINGLE_ID));
			setSingleRemind(context); //
		}
	}

	private void showNotification(Context context, String content, int notificationId)
	{
		String click = context.getResources().getString(R.string.notification_click);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, content, System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
//		notification.setLatestEventInfo(context, content, click, pending);
		notification.icon = R.drawable.notification_icon;
		notificationManager.notify(notificationId, notification);
	}

	public static void setCycleRemind(Context context)
	{
	    SharedPreferences sharedPreferences = GTDUtil.getSharedPreferences(context);
		if (sharedPreferences.getBoolean(GTDConstants.XML_REPEAT, false))
		{
			int repeatvalue = sharedPreferences.getInt(GTDConstants.XML_REPEATVALUE, 30);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(GTDConstants.ACTION_CYCLE_REMIND);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatvalue * 60000, pendingIntent);
		} else
		{
			cancelCycleRemind(context);
		}
	}

	public static void cancelCycleRemind(Context context)
	{
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(GTDConstants.ACTION_CYCLE_REMIND);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}

	public static void setSingleRemind(Context context)
	{
		TaskService taskService = TaskService.getInstance(context);
		SharedPreferences sharedPreferences = GTDUtil.getSharedPreferences(context);
		Task task = null;
		if (sharedPreferences.getBoolean(GTDConstants.XML_SINGLE, false) && (task = taskService.nextRemindTask()) != null)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(task.getDate());
			calendar.set(Calendar.HOUR_OF_DAY, task.getTime().getHours());
			calendar.set(Calendar.MINUTE, task.getTime().getMinutes()); 
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(GTDConstants.ACTION_SINGLE_REMIND);
			intent.putExtra(GTDConstants.KEY_TASK_CONTENT, task.getContent()); 
			if (sharedPreferences.getBoolean(GTDConstants.XML_SINGLE_COVER, true))  
			{
				intent.putExtra(GTDConstants.KEY_TASK_ID, SINGLE_ID);
			} else
			{
				intent.putExtra(GTDConstants.KEY_TASK_ID, task.getId());
			}
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		} else
		{
			cancelSingleRemind(context);
		}
	}

	public static void cancelSingleRemind(Context context)
	{
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(GTDConstants.ACTION_SINGLE_REMIND);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
}
