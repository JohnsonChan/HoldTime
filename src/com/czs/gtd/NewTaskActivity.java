package com.czs.gtd;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.czs.gtd.api.DatePickerPopupWindow;
import com.czs.gtd.api.DatePickerPopupWindow.onDatePickedListener;
import com.czs.gtd.api.ItemPickPopupWindow;
import com.czs.gtd.api.ItemPickPopupWindow.ItemSelectListener;
import com.czs.gtd.api.TimePickerPopupWindow;
import com.czs.gtd.api.TimePickerPopupWindow.TimePickedListener;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.data.Task;
import com.czs.gtd.util.ExitUtil;
import com.czs.gtd.util.GTDUtil;
import com.czs.gtd.util.TaskService;

public class NewTaskActivity extends Activity
{
    private java.util.Date minDate = null;
    private java.util.Date maxDate = null;
    private Task task;
    private TextView titleTextView = null;
    private EditText priorityEditText; 
    private EditText typeEditText; 
    private EditText timeEditText; 
    private EditText dateEditText; 
    private EditText contentEditText; 
    private Button okButton;
    private Button cancelButton;
    private Calendar calendar;
    private TaskService taskService = null;
    private String priorityStrings[] = null;
    private int currentIndex = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ExitUtil.getInstance().addActivity(this);
        taskService = TaskService.getInstance(this);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -50);
        minDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 100);
        maxDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -50);
        priorityStrings = getResources().getStringArray(R.array.prioritys);
        
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new);
        initWidget();
        if (getIntent().getBooleanExtra(GTDConstants.KEY_TASK_STATE, false)) 
        {
            task = (Task) getIntent().getSerializableExtra(GTDConstants.KEY_TASK);
            update();
        }
        else
        {
            task = new Task();
            initData();
        }
        initEvent();
        contentEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                if (!contentEditText.getText().toString().equals(""))
                {
                    okButton.setBackgroundResource(R.drawable.title_right_btn_bg);
                }
                else {
                    okButton.setBackgroundResource(R.drawable.mm_title_act_btn_disable);
                }
            }
        });
    }
    
    private void initWidget()
    {
        priorityEditText = (EditText) findViewById(R.id.et_set_pro);
        typeEditText = (EditText) findViewById(R.id.et_set_type);
        dateEditText = (EditText) findViewById(R.id.et_set_date);
        timeEditText = (EditText) findViewById(R.id.et_set_time);
        okButton = (Button) findViewById(R.id.btn_add_confirm);
        cancelButton = (Button) findViewById(R.id.btn_add_cancel);
        contentEditText = (EditText) findViewById(R.id.et_set_content);
        titleTextView = (TextView) findViewById(R.id.tv_add_title);
        titleTextView.setFocusable(true);
    }
    
    private void initData()
    {
        priorityEditText.setText(priorityStrings[0]);
        task.setPriority(0);
        typeEditText.setText(GTDUtil.taskTypeName[0]);
        task.setType(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[0]));
        task.setDate(Date.valueOf(calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH)));
        dateEditText.setText(task.getDate().toString());
        task.setTime(Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + 0));
        timeEditText.setText(task.getTime().toString().subSequence(0, 5));
    }
    
    private void initEvent()
    {
        priorityEditText.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemPickPopupWindow itemPickPopupWindow = new ItemPickPopupWindow(NewTaskActivity.this, R.string.new_set_remind_date,
                        getResources().getStringArray(R.array.prioritys), new ItemSelectListener()
                        {
                            @Override
                            public void onItemSelect(ItemPickPopupWindow window, int itemIndex)
                            {
                                priorityEditText.setText(priorityStrings[itemIndex]);
                                task.setPriority(itemIndex);
                            }
                        });
                itemPickPopupWindow.setInitItem(task.getPriority());
                itemPickPopupWindow.setTitle(getString(R.string.new_set_pro));
                itemPickPopupWindow.show(NewTaskActivity.this);
            }
        });
        
        typeEditText.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemPickPopupWindow itemPickPopupWindow = new ItemPickPopupWindow(NewTaskActivity.this, R.string.new_set_remind_date,
                        GTDUtil.taskTypeName, new ItemSelectListener()
                        {
                            @Override
                            public void onItemSelect(ItemPickPopupWindow window, int itemIndex)
                            {
                                typeEditText.setText(GTDUtil.taskTypeName[itemIndex]);
                                task.setType(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[itemIndex]));
                                currentIndex = itemIndex;
                            }
                        });
                itemPickPopupWindow.setInitItem(currentIndex);
                itemPickPopupWindow.setTitle(getString(R.string.new_set_type));
                itemPickPopupWindow.show(NewTaskActivity.this);
            }
        });
        
        timeEditText.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TimePickerPopupWindow timePickerPopupWindow = new TimePickerPopupWindow(NewTaskActivity.this, task.getTime().getHours(), task
                        .getTime().getMinutes(), new TimePickedListener()
                {
                    @Override
                    public void onTimePicked(int hour, int minute)
                    {
                        task.setTime(Time.valueOf(hour + ":" + minute + ":00"));
                        timeEditText.setText(task.getTime().toString().subSequence(0, 5));
                    }
                });
                timePickerPopupWindow.setTitle(getString(R.string.new_set_remind_time));
                timePickerPopupWindow.show(NewTaskActivity.this);
            }
        });
        
        dateEditText.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerPopupWindow datePickerPopupWindow = new DatePickerPopupWindow(NewTaskActivity.this, R.string.new_set_remind_date, task
                        .getDate(), minDate, maxDate, new onDatePickedListener()
                {
                    @Override
                    public void onDatePicked(int year, int month, int day)
                    {
                        task.setDate(Date.valueOf(Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day)));
                        dateEditText.setText(task.getDate().toString());
                    }
                });
                datePickerPopupWindow.show(NewTaskActivity.this);
            }
        });
        
        okButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String contextString = null;
                Calendar calendar = Calendar.getInstance();
                Date currentDate = Date.valueOf(calendar.get(Calendar.YEAR) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH));
                Time currentTime = Time.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":"
                        + calendar.get(Calendar.SECOND));
                contextString = contentEditText.getText().toString();
                if (contextString.equals(""))
                {
                    Toast.makeText(NewTaskActivity.this, R.string.new_content_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (task.getDate().compareTo(currentDate) < 0)
                {
                    Toast.makeText(NewTaskActivity.this, R.string.new_date_out, Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    if (task.getDate().compareTo(currentDate) == 0 && task.getTime().compareTo(currentTime) < 0)
                    {
                        Toast.makeText(NewTaskActivity.this, R.string.new_time_out, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                task.setFinish(0);
                task.setContent(contextString);
                if (getIntent().getBooleanExtra(GTDConstants.KEY_TASK_STATE, false))
                {
                    taskService.update(task);
                }
                else
                {
                    taskService.save(task);
                }
                NotificationReceiver.setSingleRemind(NewTaskActivity.this);
                Evernote.update(NewTaskActivity.this);
                finish();
            }
        });
        
        cancelButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
    
    @SuppressLint("UseSparseArrays")
	private void update()
    {
        titleTextView.setText(task.getContent());
        priorityEditText.setText(priorityStrings[task.getPriority()]);
        dateEditText.setText(task.getDate().toString());
        timeEditText.setText(task.getTime().toString().substring(0, 5));
        contentEditText.setText(task.getContent());
        int selectId = task.getType();
        HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
        for (int j = 0; j < GTDUtil.taskTypeName.length; j++)
        {
            tempMap.put(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[j]), GTDUtil.taskTypeName[j]);
        }
        String tempString = tempMap.get(selectId);
        for (int i = 0; i < GTDUtil.taskTypeName.length; i++)
        {
            if (tempString.equals(GTDUtil.taskTypeName[i]))
            {
                currentIndex = i;
                break;
            }
        }
        tempMap.clear();
        tempMap = null;
        typeEditText.setText(GTDUtil.taskTypeName[currentIndex]);
    }
}
