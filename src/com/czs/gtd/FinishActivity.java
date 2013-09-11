package com.czs.gtd;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
public class FinishActivity extends Activity
{
    private static ArrayList<ArrayList<Task>> taskMap = new ArrayList<ArrayList<Task>>();
    private SharedPreferences sharedPreferences = null;
    private ExpandableListView expandableListView = null;
    private ExpandableListAdapter adapter = null;
    private TaskService taskService = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ExitUtil.getInstance().addActivity(this);
        taskService = TaskService.getInstance(this);
        sharedPreferences = GTDUtil.getSharedPreferences(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GTDConstants.FINISH_EXPAND);
        intentFilter.addAction(GTDConstants.FINISH_FLOD);
        registerReceiver(finishBroadcastReceiver, intentFilter);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unfinish);
        expandableListView = (ExpandableListView) findViewById(R.id.elv_unfinish_list);
        expandableListView.setGroupIndicator(null);
        adapter = new MyExpandableListAdapter(this);
        expandableListView.setAdapter(adapter);
        
        registerForContextMenu(expandableListView);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        getTaskMap();
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
            inflater.inflate(R.menu.finish_menu, menu);
        }
        else
        {
            menu.setHeaderTitle(getString(R.string.menu_select_your_action));
            inflater.inflate(R.menu.finish_menu, menu);
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
                case R.id.freset:
                    taskMap.get(groud).get(child).setFinish(0);
                    taskService.update(taskMap.get(groud).get(child));
                    getTaskMap();
                    Evernote.update(FinishActivity.this);
                    NotificationReceiver.setSingleRemind(FinishActivity.this);
                break;
                case R.id.fdelete:
                    taskService.deleteTaskById(taskMap.get(groud).get(child).getId());
                    getTaskMap();
                    Evernote.update(FinishActivity.this);
                    NotificationReceiver.setSingleRemind(FinishActivity.this);
                break;
            }
            
        }
        else
        {
            switch (item.getItemId())
            {
                case R.id.freset:
                    taskService.updateOneDay(taskMap.get(groud).get(0).getDate().toString(), 0);
                    getTaskMap();
                    NotificationReceiver.setSingleRemind(FinishActivity.this);
                    Evernote.update(FinishActivity.this);
                break;
                case R.id.fdelete:
                    taskService.deleteDay(taskMap.get(groud).get(0).getDate().toString());
                    getTaskMap();
                    NotificationReceiver.setSingleRemind(FinishActivity.this);
                    Evernote.update(FinishActivity.this);
                break;
            }
        }
        return true;
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
            
            TextView tv = (TextView) glayout.findViewById(R.id.gtv);
            tv.setText(((ArrayList<Task>) getGroup(groupPosition)).get(0).getDate().toString());
            ImageView iv = (ImageView) glayout.findViewById(R.id.giv);
            // �жϷ����Ƿ�չ�����ֱ��벻ͬ��ͼƬ��Դ
            if (isExpanded)
            {
                iv.setImageResource(R.drawable.task_box_collapse);
            }
            else
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
    
    private BroadcastReceiver finishBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (GTDConstants.FINISH_EXPAND.equals(intent.getAction()))
            {
                for (int i = 0; i < taskMap.size(); i++)
                {
                    expandableListView.expandGroup(i);
                }
            }
            else
            {
                for (int i = 0; i < taskMap.size(); i++)
                {
                    expandableListView.collapseGroup(i);
                }
            }
            ((MyExpandableListAdapter) adapter).notifyDataSetChanged();
        }
    };
    
    private void getTaskMap()
    {
        taskMap = taskService.finish(GTDUtil.getType(TaskProperty.FINISH), GTDUtil.getPriority(TaskProperty.FINISH, sharedPreferences));
        ((MyExpandableListAdapter) adapter).notifyDataSetChanged();
    }
}
