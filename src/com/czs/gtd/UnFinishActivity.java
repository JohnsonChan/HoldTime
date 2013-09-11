package com.czs.gtd;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.data.Task;
import com.czs.gtd.data.TaskProperty;
import com.czs.gtd.util.ExitUtil;
import com.czs.gtd.util.GTDUtil;
import com.czs.gtd.util.TaskService;

@SuppressLint("HandlerLeak")
public class UnFinishActivity extends Activity
{
	private ArrayList<ArrayList<Task>> taskMap = new ArrayList<ArrayList<Task>>();
	private SharedPreferences sharedPreferences = null;
	private ExpandableListView expandableListView;
	private ExpandableListAdapter expandableListAdapter;
	private TaskService taskService = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		ExitUtil.getInstance().addActivity(this);
		taskService = TaskService.getInstance(this);
		sharedPreferences = GTDUtil.getSharedPreferences(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unfinish);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GTDConstants.UNFINISH_EXPAND);
		intentFilter.addAction(GTDConstants.UNFINISH_FLOD);
		registerReceiver(unFinishBroadcastReceiver, intentFilter);

		expandableListView = (ExpandableListView) findViewById(R.id.elv_unfinish_list);
		expandableListView.setGroupIndicator(null);
		expandableListAdapter = new MyExpandableListAdapter(this);
		expandableListView.setAdapter(expandableListAdapter);
		registerForContextMenu(expandableListView); // 注册上下文
	}

	@Override
	protected void onResume()
	{
		getTaskMap();
		super.onResume();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
	{
		MenuInflater inflater = getMenuInflater();
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
		{
			menu.setHeaderTitle(getString(R.string.menu_select_your_action));
			inflater.inflate(R.menu.child_menu, menu);
			menu.setHeaderIcon(R.drawable.ic_launcher);
		} else
		{
			menu.setHeaderTitle(getString(R.string.menu_select_your_action));
			inflater.inflate(R.menu.group_menu, menu);
			menu.setHeaderIcon(R.drawable.ic_launcher);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
		int groud = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
		{
			switch (item.getItemId())
			{
			case R.id.cfinish:
				taskMap.get(groud).get(child).setFinish(1);
				taskService.update(taskMap.get(groud).get(child));
				NotificationReceiver.setSingleRemind(UnFinishActivity.this);
				int score = sharedPreferences.getInt(GTDConstants.XML_RANK_VALUE, 0);
				boolean isvip = sharedPreferences.getBoolean(GTDConstants.XML_IS_VIP, false);
				if (isvip)
				{
				    score = score + GTDConstants.VIP_SCORE;
				}
				else {
				    score = score + GTDConstants.NOT_VIP_SCORE;
                }
				Editor editor = sharedPreferences.edit();
				editor.putInt(GTDConstants.XML_RANK_VALUE, score);
				editor.commit();
				getTaskMap();
				Evernote.update(UnFinishActivity.this);
				break;
			case R.id.cchange:
				startActivity(new Intent(UnFinishActivity.this, NewTaskActivity.class)
						.putExtra(GTDConstants.KEY_TASK, taskMap.get(groud).get(child))
						.putExtra(GTDConstants.KEY_TASK, taskMap.get(groud).get(child)).putExtra(GTDConstants.KEY_TASK_STATE, true));
				break;
			case R.id.ccreate:
				startActivity(new Intent(UnFinishActivity.this, NewTaskActivity.class));
				break;
			case R.id.cdelete:
				taskService.deleteTaskById(taskMap.get(groud).get(child).getId());
				getTaskMap();
				Evernote.update(UnFinishActivity.this);
				NotificationReceiver.setSingleRemind(UnFinishActivity.this);
				break;
			}
		} else
		{
			switch (item.getItemId())
			{
			case R.id.gfinish:
				taskService.updateOneDay(taskMap.get(groud).get(0).getDate().toString(), 1);
				getTaskMap();
				Evernote.update(UnFinishActivity.this);
				NotificationReceiver.setSingleRemind(UnFinishActivity.this);
				break;
			case R.id.gcreate:
				startActivity(new Intent(UnFinishActivity.this, NewTaskActivity.class));
				break;
			case R.id.gdelete:
				taskService.deleteDay(taskMap.get(groud).get(0).getDate().toString());
				getTaskMap();
			    Evernote.update(UnFinishActivity.this);
				NotificationReceiver.setSingleRemind(UnFinishActivity.this);
				break;
			}
		}
		return true;
	}

	private BroadcastReceiver unFinishBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (GTDConstants.UNFINISH_EXPAND.equals(intent.getAction()))
			{
				for (int i = 0; i < taskMap.size(); i++)
				{
					expandableListView.expandGroup(i);
				}
			} else
			{
				for (int i = 0; i < taskMap.size(); i++)
				{
					expandableListView.collapseGroup(i);
				}
			}
			((MyExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(unFinishBroadcastReceiver);
		unFinishBroadcastReceiver = null;
	}

	// 获得显示数据
	private void getTaskMap()
	{
		taskMap = taskService.unFinish(GTDUtil.getType(TaskProperty.UNFINSH), GTDUtil.getPriority(TaskProperty.UNFINSH, sharedPreferences),
				sharedPreferences.getBoolean(GTDConstants.XML_ISUNFINISHDATED, true));
		((MyExpandableListAdapter) expandableListAdapter).notifyDataSetChanged();
	}

	class MyExpandableListAdapter extends BaseExpandableListAdapter
	{
		private Context context;

		public MyExpandableListAdapter(Context mContext)
		{
			super();
			this.context = mContext;
		}

		public Object getChild(int groupPosition, int childPosition)
		{
			return taskMap.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		public int getChildrenCount(int groupPosition)
		{
			return taskMap.get(groupPosition).size();
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
		{
			RelativeLayout clayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.activity_main_body_item, null);
			TextView tv = (TextView) clayout.findViewById(R.id.ctv);
			Task task = (Task) getChild(groupPosition, childPosition);
			tv.setText(task.getTime().toString().subSequence(0, 5) + "   " + task.getContent());

			ImageView priority = (ImageView) clayout.findViewById(R.id.priority);
			ImageView type = (ImageView) clayout.findViewById(R.id.civ);
			type.setImageResource(task.getType());
			switch (task.getPriority())
			{
			case 0:
				priority.setImageResource(R.drawable.pro_important_urgent);
				break;
			case 1:
				priority.setImageResource(R.drawable.pro_urgent_img);
				break;
			case 2:
				priority.setImageResource(R.drawable.pro_important_img);
				break;
			case 3:
				priority.setImageResource(R.drawable.pro_not_not_img);
				break;
			}
			return clayout;
		}

		public Object getGroup(int groupPosition)
		{
			return taskMap.get(groupPosition);
		}

		public int getGroupCount()
		{
			return taskMap.size();
		}

		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		@SuppressWarnings("unchecked")
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
		{
			RelativeLayout glayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.activity_main_body_group, null);
			Calendar calendar = Calendar.getInstance();
			TextView tv = (TextView) glayout.findViewById(R.id.gtv);
			if (((ArrayList<Task>) getGroup(groupPosition))
					.get(0)
					.getDate()
					.compareTo(
							Date.valueOf(calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
									+ calendar.get(Calendar.DAY_OF_MONTH))) == 0)
			{
				tv.setText(getString(R.string.app_today));
			} else if (((ArrayList<Task>) getGroup(groupPosition))
					.get(0)
					.getDate()
					.compareTo(
							Date.valueOf(calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
									+ (calendar.get(Calendar.DAY_OF_MONTH) + 1))) == 0)
			{
				tv.setText(getString(R.string.app_tomorrow));
			} else
			{
				tv.setText(((ArrayList<Task>) getGroup(groupPosition)).get(0).getDate().toString());
			}

			ImageView iv = (ImageView) glayout.findViewById(R.id.giv);
			// 判断分组是否展开，分别传入不同的图片资源
			if (isExpanded)
			{
				iv.setImageResource(R.drawable.task_box_collapse);
			} else
			{
				iv.setImageResource(R.drawable.task_box_fold);
			}
			return glayout;
		}
		

		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

		public boolean hasStableIds()
		{
			return true;
		}
	}
	
	
}
