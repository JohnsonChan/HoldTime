package uncle.android.holdtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.czs.gtd.api.CornerListView;
import com.czs.gtd.api.DimPopupWindow;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.util.ExitUtil;
import com.czs.gtd.util.GTDUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreActivity extends Activity
{
    private CornerListView extendCornerListView = null;
    private int[] extendStringIds = null;
    private int[] extendIcons = null;
    private boolean isShare = false;
    private DimPopupWindow vipPopupWindow = null;
    private Button exitButton = null;
//	private String isBlock = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ExitUtil.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        exitButton = (Button) findViewById(R.id.btn_more_exit);
        exitButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences sharedPreferences = GTDUtil.getSharedPreferences(MoreActivity.this);
                Editor editor = sharedPreferences.edit();
                editor.putBoolean(GTDConstants.XML_IS_EXIT, true);
                editor.commit();
                ExitUtil.exit();
            }
        });
        extend();
        intro();
//        isBlock = AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID,this).getConfig("isBlock", "false");
    }
    
    private void extend()
    {
        extendCornerListView = null;
        extendStringIds = null;
        extendStringIds = new int[] { R.string.more_share_app, R.string.more_go_grade, R.string.more_feedback, R.string.more_check_version };
        extendIcons = null;
        extendIcons = new int[] { R.drawable.more_share_img, R.drawable.more_grade, R.drawable.more_feedback_img, R.drawable.more_check_img };
        List<Map<String, Object>> funcListItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < extendStringIds.length; i++)
        {
            Map<String, Object> funcListItem = new HashMap<String, Object>();
            funcListItem.put("extendIcons", extendIcons[i]);
            funcListItem.put("extendStringIds", getString(extendStringIds[i]));
            funcListItems.add(funcListItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, funcListItems, R.layout.activity_more_item, new String[] { "extendIcons",
                "extendStringIds" }, new int[] { R.id.more_icon_iv, R.id.more_content_tv });
        extendCornerListView = (CornerListView) findViewById(R.id.more_extend);
        extendCornerListView.setAdapter(simpleAdapter);
        extendCornerListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                switch (arg2)
                {
                    case 0: // ����
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.more_share_content));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, getTitle()));
                        isShare = true;
                    break;
                    case 1: // ����
                        Intent gradeIntent = new Intent(Intent.ACTION_VIEW);
                        gradeIntent.setData(Uri.parse("market://details?id=" + MoreActivity.this.getApplicationInfo().packageName));
                        gradeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(gradeIntent);
                    break;
                    case 2: // �����
                    break;
                    case 3: // ������
                    break;
                }
            }
        });
        GTDUtil.setListViewHeight(extendCornerListView);
    }
    
    private void intro()
    {
        CornerListView introCornerListView = (CornerListView) findViewById(R.id.more_intro);
        int[] introStringIds = new int[] { R.string.more_app_help, R.string.more_app_use, R.string.more_app_about };
        int[] introIcons = new int[] { R.drawable.more_app_help_book, R.drawable.more_app_use, R.drawable.more_about_img };
        List<Map<String, Object>> numListItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < introStringIds.length; i++)
        {
            Map<String, Object> numListItem = new HashMap<String, Object>();
            numListItem.put("introIcons", introIcons[i]);
            numListItem.put("introStringIds", getString(introStringIds[i]));
            numListItems.add(numListItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, numListItems, R.layout.activity_more_item, new String[] { "introIcons",
                "introStringIds" }, new int[] { R.id.more_icon_iv, R.id.more_content_tv });
        introCornerListView.setAdapter(simpleAdapter);
        introCornerListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                switch (arg2)
                {
                    case 0: // Ӧ���ֲ�
                        startActivity(new Intent(MoreActivity.this, IntroduceActivity.class));
                    break;
                    case 1: // Ӧ���ֲ�
                        showPopupWindow();
                    break;
                    case 2: // ����Ӧ��
                        startActivity(new Intent(MoreActivity.this, AboutActivity.class));
                    break;
                }
            }
        });
        GTDUtil.setListViewHeight(introCornerListView);
    }
    
    @Override
    protected void onStart()
    {
        if (isShare == true)
        {
            isShare = false;
        }
        super.onStart();
    }
    

    
    private void showPopupWindow()
    {
        View view = getLayoutInflater().inflate(R.layout.use_popup, null, false);
        this.vipPopupWindow = new DimPopupWindow(MoreActivity.this, R.style.wxf_anim_fade, Gravity.CENTER);
        this.vipPopupWindow.setContentView(view);
        this.vipPopupWindow.setBackDismiss(true);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.vipPopupWindow.setWindowWidth((int) (dm.widthPixels * 0.9));
        this.vipPopupWindow.setWindowHeight((int) (dm.heightPixels * 0.9));
        ImageView closeImageView = (ImageView) view.findViewById(R.id.iv_use_popup_close);
//        TextView useVipTextView = (TextView)view.findViewById(R.id.tv_use_vip);
//        TextView bodyTextView = (TextView)view.findViewById(R.id.tv_body_text);
//        if (!GTDConstants.TRUE.equals(isBlock))
//		{
//        	useVipTextView.setVisibility(View.GONE);
//        	bodyTextView.setVisibility(View.GONE);
//		}
        closeImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MoreActivity.this.vipPopupWindow.dismiss();
                MoreActivity.this.vipPopupWindow = null;
            }
        });
        this.vipPopupWindow.show(MoreActivity.this);
    }
}
