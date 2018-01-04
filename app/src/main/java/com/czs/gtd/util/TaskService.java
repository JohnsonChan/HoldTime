package com.czs.gtd.util;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.czs.gtd.data.Task;

import uncle.android.holdtime.R;

public class TaskService
{
	public static DataBaseOpenHelper dbOpenHelper;
	public static SQLiteDatabase db;
	public ComparatorDate comparatorDate = new ComparatorDate();
	public ComparatorTime comparatorTime = new ComparatorTime();
	private static TaskService taskService = null;

	private TaskService(Context context)
	{
		dbOpenHelper = new DataBaseOpenHelper(context);
		db = dbOpenHelper.getWritableDatabase();
	}

	public static TaskService getInstance(Context context)
	{
		if (taskService == null)
		{
			taskService = new TaskService(context);
		}
		return taskService;
	}
	
	public static void removeInstance()
	{
		taskService = null;
	}

	public void deleteAllTask()
	{
		db.execSQL("delete from task ");
	}

	public void updateTaskType(String oldName, String newName, Integer id)
	{
		Integer oldId = GTDUtil.taskTypeMap.get(oldName);
		db.execSQL("update task_type set id=?, name=? where name=?", new Object[]
		{ id, newName, oldName });
		db.execSQL("update task set type=? where type=?", new Object[]
		{ id, oldId });
		GTDUtil.refreshType(taskService);
	}

	public void updateTaskTypePlay(String name, Integer play)
	{
		db.execSQL("update task_type set play=? where name=?", new Object[]
		{ play, name });
	}

	public boolean taskTypeIsExist(String name, int id)
	{
		Cursor cursor = db.rawQuery("select name from task_type where name='" + name + "' and id=" + id, null);
		if (cursor.moveToNext())
		{
			return true; 
		} else
		{
			return false;
		}
	}

	public void addTaskType(String name, Integer id)
	{
		db.execSQL("insert into task_type(id, name) values(?,?)", new Object[]
		{ id, name }); 
		GTDUtil.refreshType(taskService);
	}

	public void delTaskType(String name, String ids)
	{
		db.execSQL("delete from task_type where name in(" + name + ")");
		db.execSQL("delete from task where type in(" + ids + ")"); 
		GTDUtil.refreshType(taskService);
	}

	public Map<String, Integer> getAllTaskTypePlay()
	{
		Map<String, Integer> taskTypes = new HashMap<String, Integer>();
		Cursor cursor;
		cursor = db.rawQuery("select * from task_type", null);
		while (cursor.moveToNext())
		{
			taskTypes.put(cursor.getString(1), cursor.getInt(2));
		}
		return taskTypes;
	}

	public Map<String, Integer> getAllTaskType()
	{
		Map<String, Integer> taskTypes = new HashMap<String, Integer>();
		Cursor cursor;
		cursor = db.rawQuery("select * from task_type", null);
		while (cursor.moveToNext())
		{
			taskTypes.put(cursor.getString(1), cursor.getInt(0));
		}
		return taskTypes;
	}

	public ArrayList<String> getAllTaskTypeList()
	{
		ArrayList<String> taskTypes = new ArrayList<String>();
		Cursor cursor;
		cursor = db.rawQuery("select * from task_type", null);
		while (cursor.moveToNext())
		{
			taskTypes.add(cursor.getString(1));
		}
		return taskTypes;
	}

	public ArrayList<Integer> getAllTaskTypeIdList(boolean isAll)
	{

		ArrayList<Integer> taskTypes = new ArrayList<Integer>();
		Cursor cursor;
		if (isAll == true)
		{
			cursor = db.rawQuery("select * from task_type", null);
		} else
		{
			cursor = db.rawQuery("select * from task_type where id>-1", null);
		}

		while (cursor.moveToNext())
		{
			taskTypes.add(cursor.getInt(0));
		}
		return taskTypes;
	}

	public void save(Task task)
	{
		db.execSQL("insert into task(type, priority, date, time, context, finish) values(?, ?, ?, ?, ?, ?)", new Object[]
		{ task.getType(), task.getPriority(), task.getDate(), task.getTime(), task.getContent(), task.getFinish() });
	}

	public void update(Task task)
	{
		db.execSQL("update task set type=?, priority=?, date=?, time=?, context=?, finish=? where id=?", new Object[]
		{ task.getType(), task.getPriority(), task.getDate(), task.getTime(), task.getContent(), task.getFinish(), task.getId() });
	}

	public void deleteDay(String date)
	{
		db.execSQL("delete from task where date=?", new Object[]
		{ date });
	}

	public void deleteTaskById(Integer id)
	{
		db.execSQL("delete from task where id=?", new Object[]
		{ id });
	}

	public ArrayList<ArrayList<Task>> unFinish(String type, String priority, Boolean unFinishDated)
	{
		Cursor cursor = db.rawQuery("select * from task where type in(" + type + ") and priority in(" + priority + ") and finish=0", null);

		if (unFinishDated == true)
		{
			return cursorToList(cursor);
		} else
		{
			Calendar calendar = Calendar.getInstance();
			return cursorToListUnfinish(
					cursor,
					Date.valueOf(calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH)));
		}
	}

	public String formatTasks(Boolean tomorrowFlag, Context context)
	{
		Calendar calendar = Calendar.getInstance();
		int num=0;
		Time time = Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + "00");
		Cursor cursorToday = db.rawQuery(
				"select * from task where date=? and finish=?",
				new String[]
				{
						Date.valueOf(
								calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
										+ calendar.get(Calendar.DAY_OF_MONTH)).toString(), String.valueOf(0) });
		String string;
		ArrayList<Task> today = cursorToOneDay(cursorToday, time, true);
		cursorToday.close();
		Collections.sort(today, comparatorTime);
		string = "<" + context.getString(R.string.desktop_today) +">\n";

		for (int i = 0; i < today.size(); i++)
		{
			string = string + "\t" + (i + 1) + "." + formatString(today.get(i).getContent()) + "\n";
			num++;
			if (num >= 8)
			{
			    return string;
			}
		}
		if (today.size() == 0)
		{
			string = string + "\t" + context.getString(R.string.deskto_no_tips) + "\n";
		}
		if (tomorrowFlag == true)
		{
			Cursor cursorTomorrow = db.rawQuery(
					"select * from task where date=? and finish=?",
					new String[]
					{
							Date.valueOf(
									calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
											+ (calendar.get(Calendar.DAY_OF_MONTH) + 1)).toString(), String.valueOf(0) });
			ArrayList<Task> tomorrow = cursorToOneDay(cursorTomorrow, time, true);
			cursorTomorrow.close();
			Collections.sort(tomorrow, comparatorTime);
			string = string + "<" +  context.getString(R.string.desktop_tomorrow) +  ">\n";
			for (int i = 0; i < tomorrow.size(); i++)
			{
				string = string + "\t" + (i + 1) + "." + formatString(tomorrow.get(i).getContent()) + "\n";
				num++;
				if (num >= 7)
	            {
	                return string;
	            }
			}
			if (tomorrow.size() == 0)
			{
				string = string + "\t" + context.getString(R.string.deskto_no_tips) + "\n";
			}
		}
		return string;
	}

	private String formatString(String string)
	{
		if (string.length() > 7)
		{
			string = string.substring(0, 7) + "...";
		}
		return string;
	}

	public ArrayList<Task> cursorToOneDay(Cursor cursor, Time time, Boolean flag)
	{
		ArrayList<Task> tasks = new ArrayList<Task>();
		while (cursor.moveToNext())
		{
			Task task = new Task(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), Date.valueOf(cursor.getString(3)), Time.valueOf(cursor
					.getString(4)), cursor.getString(5), cursor.getInt(6));
			if (flag == false)
			{
				if (time.compareTo(task.getTime()) > 0)
				{
					continue;
				}
			}
			tasks.add(task);
		}
		return tasks;
	}

	public ArrayList<ArrayList<Task>> finish(String type, String priority)
	{
		Cursor cursor = db.rawQuery("select * from task where type in(" + type + ") and priority in(" + priority + ") and finish=1", null);
		return cursorToList(cursor);
	}

	public ArrayList<ArrayList<Task>> dated(Date date, String type, String priority)
	{
		Cursor cursor = db.rawQuery("select * from task where type in(" + type + ") and priority in(" + priority + ") and finish=0", null);
		return cursorToList(cursor, date);
	}

	public void updateOneDay(String date, Integer finish)
	{
		db.execSQL("update task set finish=? where date=?", new Object[]
		{ String.valueOf(finish), date });
	}

	protected ArrayList<ArrayList<Task>> cursorToList(Cursor cursor)
	{
		Boolean flag = false;
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		while (cursor.moveToNext())
		{
			Task task = new Task(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), Date.valueOf(cursor.getString(3)), Time.valueOf(cursor
					.getString(4)), cursor.getString(5), cursor.getInt(6));

			for (int i = 0; i < tasks.size(); i++)
			{
				if (tasks.get(i).get(0).getDate().equals(task.getDate()))
				{
					tasks.get(i).add(task);
					flag = true;
				}
			}

			if (flag == false)
			{
				ArrayList<Task> newDay = new ArrayList<Task>();
				newDay.add(task);
				tasks.add(newDay);
			}

			flag = false;
		}
		cursor.close();
		sort(tasks);
		return tasks;
	}

	protected ArrayList<ArrayList<Task>> cursorToList(Cursor cursor, Date date)
	{
		Boolean flag = false;
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		Calendar calendar = Calendar.getInstance();
		Time time = Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + "00");
		while (cursor.moveToNext())
		{
			Task task = new Task(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), Date.valueOf(cursor.getString(3)), Time.valueOf(cursor
					.getString(4)), cursor.getString(5), cursor.getInt(6));
			if (task.getDate().compareTo(date) > 0)
			{
				continue;
			} else if (task.getDate().compareTo(date) == 0)
			{
				if (task.getTime().compareTo(time) >= 0)
				{
					continue;
				}
			}
			for (int i = 0; i < tasks.size(); i++)
			{
				if (tasks.get(i).get(0).getDate().equals(task.getDate()))
				{
					tasks.get(i).add(task);
					flag = true;
				}
			}
			if (flag == false)
			{
				ArrayList<Task> newDay = new ArrayList<Task>();
				newDay.add(task);
				tasks.add(newDay);
			}
			flag = false;
		}
		cursor.close();
		sort(tasks);
		return tasks;
	}

	protected ArrayList<ArrayList<Task>> cursorToListUnfinish(Cursor cursor, Date date)
	{
		Boolean flag = false;
		ArrayList<ArrayList<Task>> tasks = new ArrayList<ArrayList<Task>>();
		Calendar calendar = Calendar.getInstance();
		Time time = Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + "00");
		while (cursor.moveToNext())
		{
			Task task = new Task(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), Date.valueOf(cursor.getString(3)), Time.valueOf(cursor
					.getString(4)), cursor.getString(5), cursor.getInt(6));
			if (task.getDate().compareTo(date) < 0) 
			{
				continue;
			} else if (task.getDate().compareTo(date) == 0)
			{
				if (task.getTime().compareTo(time) <= 0)
				{
					continue;
				}
			}
			for (int i = 0; i < tasks.size(); i++)
			{
				if (tasks.get(i).get(0).getDate().equals(task.getDate()))
				{
					tasks.get(i).add(task);
					flag = true;
				}
			}
			if (flag == false)
			{
				ArrayList<Task> newDay = new ArrayList<Task>();
				newDay.add(task);
				tasks.add(newDay);
			}
			flag = false;
		}
		cursor.close();
		sort(tasks);
		return tasks;
	}

	public ArrayList<ArrayList<Task>> futureUnfinish()
	{
		Calendar calendar = Calendar.getInstance();
		Cursor cursor = db.rawQuery("select * from task where finish=?", new String[]
		{ String.valueOf(0) });
		return cursorToListUnfinish(
				cursor,
				Date.valueOf(calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
						+ calendar.get(Calendar.DAY_OF_MONTH)));
	}

	public void sort(ArrayList<ArrayList<Task>> tasks)
	{
		Collections.sort(tasks, comparatorDate);
		for (int i = 0; i < tasks.size(); i++)
		{
			Collections.sort(tasks.get(i), comparatorTime);
		}
	}

	public String datedNum(Context context)
	{
		Calendar calendar = Calendar.getInstance();
		Cursor cursor = db.rawQuery(
				"select * from task where date=? and finish=?",
				new String[]
				{
						Date.valueOf(
								calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
										+ calendar.get(Calendar.DAY_OF_MONTH)).toString(), String.valueOf(0) });
		if (cursor.getCount() == 0)
		{
			return null;
		}
		Time time = Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + "00");
		int datedNum = 0;
		while (cursor.moveToNext())
		{
			if (time.compareTo(Time.valueOf(cursor.getString(4))) > 0)
			{
				datedNum++;
			}
		}
		return context.getString(R.string.notification_cycle, cursor.getCount(), datedNum);
	}

	public Task nextRemindTask()
	{
		Calendar calendar = Calendar.getInstance();
		Time currentTime = Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + "59");
		Task task = null;
		int days = 0; 
		while (task == null && days < 5)
		{
			Cursor cursor = db.rawQuery(
					"select * from task where date=? and finish=?",
					new String[]
					{
							Date.valueOf(
									calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
											+ calendar.get(Calendar.DAY_OF_MONTH)).toString(), String.valueOf(0) });
			while (cursor.moveToNext() && task == null)
			{
				if (currentTime.compareTo(Time.valueOf(cursor.getString(4))) < 0)
				{
					task = new Task(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), Date.valueOf(cursor.getString(3)), Time.valueOf(cursor
							.getString(4)), cursor.getString(5), cursor.getInt(6));
				}
			}
			if (task == null)
			{
				calendar.add(Calendar.DATE, 1);
				days++;
			}
		}
		return task;
	}

	class ComparatorDate implements Comparator<Object>
	{
		@SuppressWarnings("unchecked")
		public int compare(Object arg0, Object arg1)
		{
			ArrayList<Task> user0 = (ArrayList<Task>) arg0;
			ArrayList<Task> user1 = (ArrayList<Task>) arg1;
			if (user0.get(0).getDate().compareTo(user1.get(0).getDate()) > 0)
			{
				return 1;
			} else
			{
				return -1;
			}
		}
	}

	class ComparatorTime implements Comparator<Object>
	{
		public int compare(Object arg0, Object arg1)
		{
			Task user0 = (Task) arg0;
			Task user1 = (Task) arg1;
			if (user0.getTime().compareTo(user1.getTime()) > 0)
			{
				return 1;
			} else
			{
				return -1;
			}
		}
	}
}
