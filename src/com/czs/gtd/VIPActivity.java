package com.czs.gtd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.czs.gtd.api.CornerListView;
import com.czs.gtd.data.TaskProperty;
import com.czs.gtd.util.ExitUtil;
import com.czs.gtd.util.GTDUtil;
import com.czs.gtd.util.TaskService;

@SuppressLint("CommitPrefEdits")
public class VIPActivity extends Activity
{
    private Button backButton = null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    private TaskService taskService = null;
    private CornerListView typeCornerListView = null;
    private int[] typeStringIds = null;
    private int[] typeIcons = null;
    private CornerListView showCornerListView = null;
    private int[] showStringIds = null;
    private int[] showIcons = null;
    private CornerListView dataCornerListView = null;
    private int[] dataStringIds = null;
    private int[] dataIcons = null;
    boolean[] multFlags = null; 
    private AlertDialog mDialog = null;
    int selectItem = -1; 
    int imageId = R.drawable.type_default_other; 
    int tempImageId = -1; 
    boolean isAdd = true; 
    LinearLayout linearLayout = null;
    ImageView imageView = null; 
    Builder imageBuilder = null;
    LinearLayout addLinearLayout = null;
    TaskProperty taskProperty; 
    int priority = 0; 
    boolean isRemind = true;
    boolean isUnfinishDated = true; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	taskService = TaskService.getInstance(this);
        ExitUtil.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        sharedPreferences = GTDUtil.getSharedPreferences(VIPActivity.this);
        editor = sharedPreferences.edit();
        init();
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
    }
    
    public void init()
    {
        backButton = (Button) findViewById(R.id.btn_vip_back);
        backButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        type();
        GTDUtil.setListViewHeight(typeCornerListView);
        show();
        GTDUtil.setListViewHeight(showCornerListView);
        data();
        GTDUtil.setListViewHeight(dataCornerListView);
    }
    
    private void type()
    {
        typeCornerListView = null;
        typeStringIds = null;
        typeStringIds = new int[] { R.string.vip_add_type, R.string.vip_del_tasktype, R.string.vip_chg_tasktype };
        typeIcons = null;
        typeIcons = new int[] { R.drawable.vip_add_img, R.drawable.vip_del_img, R.drawable.vip_change_img };
        List<Map<String, Object>> funcListItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < typeStringIds.length; i++)
        {
            Map<String, Object> funcListItem = new HashMap<String, Object>();
            funcListItem.put("typeIcons", typeIcons[i]);
            funcListItem.put("typeStringIds", getString(typeStringIds[i]));
            funcListItems.add(funcListItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, funcListItems, R.layout.activity_more_item,
                new String[] { "typeIcons", "typeStringIds" }, new int[] { R.id.more_icon_iv, R.id.more_content_tv });
        typeCornerListView = (CornerListView) findViewById(R.id.more_type);
        typeCornerListView.setAdapter(simpleAdapter);
        typeCornerListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                switch (arg2)
                {
                    case 0: 
                        showDialog(1);
                    break;
                    case 1:
                        showDialog(2);
                    break;
                    case 2: 
                        showDialog(3);
                    break;
                }
            }
        });
    }
    
    private void show()
    {
        showCornerListView = null;
        showStringIds = null;
        showStringIds = new int[] { R.string.vip_unfinish_type, R.string.vip_unfinish_pro, R.string.vip_finish_type, R.string.vip_finish_pro,
                R.string.vip_dated_type, R.string.vip_dated_pro };
        showIcons = null;
        showIcons = new int[] { R.drawable.vip_unfinish_type_img, R.drawable.vip_unfinish_pro_img, R.drawable.vip_finish_type_img, R.drawable.vip_finish_pro_img,
                R.drawable.vip_dated_type_img, R.drawable.vip_dated_pro_img };
        List<Map<String, Object>> numListItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < showStringIds.length; i++)
        {
            Map<String, Object> numListItem = new HashMap<String, Object>();
            numListItem.put("showIcons", showIcons[i]);
            numListItem.put("showStringIds", getString(showStringIds[i]));
            numListItems.add(numListItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, numListItems, R.layout.activity_more_item,
                new String[] { "showIcons", "showStringIds" }, new int[] { R.id.more_icon_iv, R.id.more_content_tv });
        showCornerListView = (CornerListView) findViewById(R.id.more_show);
        showCornerListView.setAdapter(simpleAdapter);
        showCornerListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                switch (arg2)
                {
                    case 0: 
                        taskProperty = TaskProperty.UNFINSH;
                        showDialog(5);
                    break;
                    case 1: 
                        taskProperty = TaskProperty.UNFINSH;
                        priority = sharedPreferences.getInt("unfinish_pro", 0);
                        showDialog(6);
                    break;
                    case 2: 
                        taskProperty = TaskProperty.FINISH;
                        showDialog(5);
                    break;
                    case 3: 
                        taskProperty = TaskProperty.FINISH;
                        priority = sharedPreferences.getInt("finish_pro", 0);
                        showDialog(6);
                    break;
                    case 4: 
                        taskProperty = TaskProperty.DATED;
                        showDialog(5);
                    break;
                    case 5: 
                        taskProperty = TaskProperty.DATED;
                        priority = sharedPreferences.getInt("dated_pro", 0);
                        showDialog(6);
                    break;
                }
            }
        });
    }
    
    private void data()
    {
        dataCornerListView = null;
        dataStringIds = null;
        dataStringIds = new int[] { R.string.vip_data_backup, R.string.vip_data_recovery, R.string.vip_data_clear };
        dataIcons = null;
        dataIcons = new int[] { R.drawable.more_backup_img, R.drawable.more_data_recovery_img, R.drawable.vip_clear_data };
        List<Map<String, Object>> personListItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < dataStringIds.length; i++)
        {
            Map<String, Object> personListItem = new HashMap<String, Object>();
            personListItem.put("dataIcons", dataIcons[i]);
            personListItem.put("dataStringIds", getString(dataStringIds[i]));
            personListItems.add(personListItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, personListItems, R.layout.activity_more_item, new String[] { "dataIcons",
                "dataStringIds" }, new int[] { R.id.more_icon_iv, R.id.more_content_tv });
        dataCornerListView = (CornerListView) findViewById(R.id.more_data);
        dataCornerListView.setAdapter(simpleAdapter);
        dataCornerListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                switch (arg2)
                {
                    case 0: 
                        File ugeekFile = new File(getCacheDir().getParent() + "/databases/gtd.db");
                        File backupFile = new File(getCacheDir().getAbsolutePath() + "/backup");
                        GTDUtil.copyfile(ugeekFile, backupFile, VIPActivity.this, true);
                    break;
                    case 1: 
                        File fromFile = new File(getCacheDir().getAbsolutePath() + "/backup");
                        File toFile = new File(getCacheDir().getParent() + "/databases/gtd.db");
                        GTDUtil.copyfile(fromFile, toFile, VIPActivity.this, false);
                    break;
                    case 2: 
                        TaskService taskService = TaskService.getInstance(VIPActivity.this);
                        taskService.deleteAllTask();
                    break;
                }
            }
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case 1: 
            {
                addLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_changetype, null);
                imageView = (ImageView) addLinearLayout.getChildAt(0);
                imageView.setOnClickListener(new View.OnClickListener() 
                        {
                            @Override
                            public void onClick(View v)
                            {
                                showDialog(4);
                            }
                        });
                final Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_launcher);
                if (isAdd == true) 
                {
                    builder.setTitle(getString(R.string.vip_add_type));
                    imageView.setImageResource(R.drawable.type_default_other);
                }
                else
                {
                    builder.setTitle(getString(R.string.vip_change_type));
                    imageId = GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[selectItem]);
                    imageView.setImageResource(imageId);
                    TextView textView = (TextView) addLinearLayout.getChildAt(1);
                    textView.setText(GTDUtil.taskTypeName[selectItem]);
                }
                builder.setView(addLinearLayout);
                builder.setPositiveButton(getString(R.string.app_confirm), new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TextView textView = (TextView) addLinearLayout.getChildAt(1);
                        if (taskService.taskTypeIsExist(textView.getText().toString(), imageId))
                        {
                            GTDUtil.toastTip(VIPActivity.this, textView.getText().toString() + getString(R.string.vip_type_exist));
                            return;
                        }
                        if (textView.getText().toString().equals(""))
                        {
                            GTDUtil.toastTip(VIPActivity.this, R.string.vip_type_not_null);
                            return;
                        }
                        if (isAdd == true) 
                        {
                            taskService.addTaskType(textView.getText().toString(), imageId);
                        }
                        else
                        {
                            taskService.updateTaskType(GTDUtil.taskTypeName[selectItem], textView.getText().toString(), imageId);
                        }
                        isAdd = true;
                    }
                });
                builder.setNegativeButton(getString(R.string.app_cancel), new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        isAdd = true;
                        dialog.dismiss();
                    }
                });
                this.mDialog = builder.create();
                this.mDialog.show();
                break; 
            }
            case 2: 
            {
                if (GTDUtil.taskTypeName.length == 0) 
                {
                    GTDUtil.toastTip(VIPActivity.this, R.string.vip_no_type_del);
                    break;
                }
                multFlags = new boolean[GTDUtil.taskTypeName.length];
                for (int i = 0; i < multFlags.length; i++)
                {
                    multFlags[i] = false;
                }
                this.mDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.vip_del_type)).setIcon(R.drawable.ic_launcher)
                        .setMultiChoiceItems(GTDUtil.taskTypeName, multFlags, new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked)
                            {
                            }
                        }).setPositiveButton(getString(R.string.app_confirm), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String names = "";
                                String ids = "";
                                for (int i = 0; i < multFlags.length; i++)
                                {
                                    if (multFlags[i])
                                    {
                                        names += "'" + GTDUtil.taskTypeName[i] + "',";
                                        ids += "" + Integer.toString(GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[i])) + ",";
                                    }
                                }
                                names = names.substring(0, names.length() - 1);
                                ids = ids.substring(0, ids.length() - 1);
                                taskService.delTaskType(names, ids);
                            }
                        }).setNegativeButton(getString(R.string.app_cancel), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).create();
                this.mDialog.show();
            }
            break;
            case 3: 
            {
                if (GTDUtil.taskTypeName.length == 0)
                {
                    GTDUtil.toastTip(VIPActivity.this, R.string.vip_no_type_del);
                    break;
                }
                this.mDialog = new AlertDialog.Builder(this).setSingleChoiceItems(GTDUtil.taskTypeName, -1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        VIPActivity.this.selectItem = which;
                    }
                }).setPositiveButton(getString(R.string.app_confirm), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        isAdd = false;
                        imageId = GTDUtil.taskTypeMap.get(GTDUtil.taskTypeName[selectItem]);
                        showDialog(1);
                    }
                }).setNegativeButton(getString(R.string.app_cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                }).create();
                this.mDialog.setTitle(getString(R.string.vip_change_type));
                this.mDialog.setIcon(R.drawable.ic_launcher);
                this.mDialog.show();
                break;
            }
            case 4: 
            {
                tempImageId = imageId;
                // 图锟斤拷选锟斤拷曰锟斤拷锟�
                linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.gridview, null);
                GridView gridview = (GridView) linearLayout.getChildAt(1);
                // 锟斤拷啥锟教拷锟斤拷椋拷锟斤拷锟阶拷锟斤拷锟斤拷
                ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
                for (int i = 0; i < GTDUtil.taskTypeImgId.length; i++)
                {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemImage", GTDUtil.taskTypeImgId[i]);// 锟斤拷锟酵硷拷锟斤拷锟皆达拷锟絀D
                    lstImageItem.add(map);
                }
                SimpleAdapter saImageItems = new SimpleAdapter(this, // 没什么锟斤拷锟斤拷
                        lstImageItem,// 锟斤拷锟斤拷锟皆�
                        R.layout.gridview_item,// night_item锟斤拷XML实锟斤拷
                        new String[] { "ItemImage" }, new int[] { R.id.ItemImage });
                // 锟斤拷硬锟斤拷锟斤拷锟绞�
                gridview.setAdapter(saImageItems);
                // 锟斤拷锟斤拷锟较拷锟斤拷锟�
                imageBuilder = new AlertDialog.Builder(this);
                gridview.setOnItemClickListener(new ItemClickListener(linearLayout.getChildAt(0)));
                imageBuilder.setView(linearLayout);
                imageBuilder.setPositiveButton(getString(R.string.app_confirm), new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        imageId = tempImageId;
                        imageView.setImageResource(imageId);
                    }
                });
                imageBuilder.setNegativeButton(getString(R.string.app_cancel), new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                imageBuilder.create().show();
                break;
            }
            case 5: // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷示锟斤拷锟斤拷
            {
                if (GTDUtil.taskTypeName.length == 0) // 锟叫讹拷锟斤拷锟斤拷锟斤拷锟斤拷锟角凤拷为锟斤拷
                {
                    GTDUtil.toastTip(VIPActivity.this, R.string.vip_no_type_del);
                    break;
                }
                multFlags = null; //
                multFlags = new boolean[GTDUtil.taskTypeName.length];
                switch (taskProperty)
                // 锟斤拷取
                {
                    case UNFINSH: //
                        for (int i = 0; i < multFlags.length; i++)
                        {
                            if ((GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i]) | 3) == 7)
                            {
                                multFlags[i] = true;
                            }
                            else
                            {
                                multFlags[i] = false;
                            }
                        }
                    break;
                    case FINISH:
                        for (int i = 0; i < multFlags.length; i++)
                        {
                            if ((GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i]) | 5) == 7)
                            {
                                multFlags[i] = true;
                            }
                            else
                            {
                                multFlags[i] = false;
                            }
                        }
                    break;
                    case DATED:
                        for (int i = 0; i < multFlags.length; i++)
                        {
                            if ((GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i]) | 6) == 7)
                            {
                                multFlags[i] = true;
                            }
                            else
                            {
                                multFlags[i] = false;
                            }
                        }
                    break;
                }
                this.mDialog = null;
                this.mDialog = new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
                        .setMultiChoiceItems(GTDUtil.taskTypeName, multFlags, new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked)
                            {
                            }
                        }).setPositiveButton(getString(R.string.app_confirm), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (taskProperty)
                                {
                                    case UNFINSH:
                                        for (int i = 0; i < multFlags.length; i++)
                                        {
                                            if (multFlags[i])
                                            {
                                                taskService.updateTaskTypePlay(GTDUtil.taskTypeName[i],
                                                        (GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i])) | ((~3) & 7));
                                            }
                                            else
                                            {
                                                taskService.updateTaskTypePlay(GTDUtil.taskTypeName[i],
                                                        (GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i])) & 3);
                                            }
                                        }
                                        GTDUtil.refreshType(taskService);
                                    break;
                                    case FINISH:
                                        for (int i = 0; i < multFlags.length; i++)
                                        {
                                            if (multFlags[i])
                                            {
                                                taskService.updateTaskTypePlay(GTDUtil.taskTypeName[i],
                                                        (GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i])) | ((~5) & 7));
                                            }
                                            else
                                            {
                                                taskService.updateTaskTypePlay(GTDUtil.taskTypeName[i],
                                                        (GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i])) & 5);
                                            }
                                        }
                                        GTDUtil.refreshType(taskService);
                                    break;
                                    case DATED:
                                        for (int i = 0; i < multFlags.length; i++)
                                        {
                                            if (multFlags[i])
                                            {
                                                taskService.updateTaskTypePlay(GTDUtil.taskTypeName[i],
                                                        (GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i])) | ((~6) & 7));
                                            }
                                            else
                                            {
                                                taskService.updateTaskTypePlay(GTDUtil.taskTypeName[i],
                                                        (GTDUtil.taskTypePlay.get(GTDUtil.taskTypeName[i])) & 6);
                                            }
                                        }
                                        GTDUtil.refreshType(taskService);
                                    break;
                                }
                            }
                        }).setNegativeButton(getString(R.string.app_cancel), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).create();
                switch (taskProperty)
                {
                    case UNFINSH:
                        mDialog.setTitle(getString(R.string.vip_unfinish_show_type));
                    break;
                    case FINISH:
                        mDialog.setTitle(getString(R.string.vip_finish_show_type));
                    break;
                    case DATED:
                        mDialog.setTitle(getString(R.string.vip_dated_show_type));
                    break;
                }
                this.mDialog.show();
                break;
            }
            case 6: // 锟斤拷锟斤拷锟斤拷锟饺硷拷
            {
                multFlags = null;
                multFlags = new boolean[4];
                switch (taskProperty)
                // 锟斤拷取
                {
                    case UNFINSH: //
                        multFlags[0] = sharedPreferences.getBoolean("unfinish_pro0", true);
                        multFlags[1] = sharedPreferences.getBoolean("unfinish_pro1", true);
                        multFlags[2] = sharedPreferences.getBoolean("unfinish_pro2", true);
                        multFlags[3] = sharedPreferences.getBoolean("unfinish_pro3", true);
                    break;
                    case FINISH:
                        multFlags[0] = sharedPreferences.getBoolean("finish_pro0", true);
                        multFlags[1] = sharedPreferences.getBoolean("finish_pro1", true);
                        multFlags[2] = sharedPreferences.getBoolean("finish_pro2", true);
                        multFlags[3] = sharedPreferences.getBoolean("finish_pro3", true);
                    break;
                    case DATED:
                        multFlags[0] = sharedPreferences.getBoolean("dated_pro0", true);
                        multFlags[1] = sharedPreferences.getBoolean("dated_pro1", true);
                        multFlags[2] = sharedPreferences.getBoolean("dated_pro2", true);
                        multFlags[3] = sharedPreferences.getBoolean("dated_pro3", true);
                    break;
                }
                this.mDialog = null;
                this.mDialog = new AlertDialog.Builder(this)
                        .setMultiChoiceItems(GTDUtil.getPriorityStrings(VIPActivity.this), multFlags,
                                new DialogInterface.OnMultiChoiceClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                                    {
                                    }
                                }).setPositiveButton(getString(R.string.app_confirm), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                editor = sharedPreferences.edit();
                                switch (taskProperty)
                                {
                                    case UNFINSH:
                                        editor.putBoolean("unfinish_pro0", multFlags[0]);
                                        editor.putBoolean("unfinish_pro1", multFlags[1]);
                                        editor.putBoolean("unfinish_pro2", multFlags[2]);
                                        editor.putBoolean("unfinish_pro3", multFlags[3]);
                                        editor.commit();
                                    break;
                                    case FINISH:
                                        editor.putBoolean("finish_pro0", multFlags[0]);
                                        editor.putBoolean("finish_pro1", multFlags[1]);
                                        editor.putBoolean("finish_pro2", multFlags[2]);
                                        editor.putBoolean("finish_pro3", multFlags[3]);
                                        editor.commit();
                                    break;
                                    case DATED:
                                        editor.putBoolean("dated_pro0", multFlags[0]);
                                        editor.putBoolean("dated_pro1", multFlags[1]);
                                        editor.putBoolean("dated_pro2", multFlags[2]);
                                        editor.putBoolean("dated_pro3", multFlags[3]);
                                        editor.commit();
                                    break;
                                }
                            }
                        }).setNegativeButton(getString(R.string.app_cancel), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).create();
                mDialog.setIcon(R.drawable.ic_launcher);
                switch (taskProperty)
                {
                    case UNFINSH:
                        mDialog.setTitle(getString(R.string.vip_unfinish_show_pro));
                    break;
                    case FINISH:
                        mDialog.setTitle(getString(R.string.vip_finish_show_pro));
                    break;
                    case DATED:
                        mDialog.setTitle(getString(R.string.vip_dated_show_pro));
                    break;
                }
                this.mDialog.show();
                break;
            }
        }
        return mDialog;
    }
    
    class ItemClickListener implements OnItemClickListener
    {
        View view = null;
        
        public ItemClickListener(View view)
        {
            this.view = view;
        }
        
        @SuppressWarnings("unchecked")
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                View arg1,// The view within the AdapterView that was clicked
                int arg2,// The position of the view in the adapter
                long arg3// The row id of the item that was clicked
        )
        {
            HashMap<String, Integer> item = (HashMap<String, Integer>) arg0.getItemAtPosition(arg2);
            tempImageId = (Integer) item.get("ItemImage");
            LinearLayout linearLayout = (LinearLayout) view;
            ImageView imageView = (ImageView) linearLayout.getChildAt(1);
            imageView.setImageResource(tempImageId);
        }
    }
}
