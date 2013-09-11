package com.czs.gtd;

import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.util.GTDUtil;
import com.czs.gtd.util.TaskService;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

@SuppressLint("HandlerLeak")
public class Evernote extends AppWidgetProvider
{
    public static final String UPDATE_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        super.onDeleted(context, appWidgetIds);
    }
    
    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
    }
    
    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }
    
    public static void update(Context context)
    {
        context.sendBroadcast(new Intent(UPDATE_ACTION));
    }
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (UPDATE_ACTION.equals(action))
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            SharedPreferences sharedPreferences = GTDUtil.getSharedPreferences(context);
            TaskService taskService = TaskService.getInstance(context);
            remoteViews.setTextViewText(R.id.evernote,
                    taskService.formatTasks(sharedPreferences.getBoolean(GTDConstants.XML_SHOW_TOMORROW, false), context));
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, Evernote.class);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
        super.onReceive(context, intent);
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        remoteViews.setOnClickPendingIntent(R.id.layoutWidgetMain, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
