package com.czs.gtd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;
import com.czs.gtd.api.CornerListView;
import com.czs.gtd.api.DimPopupWindow;
import com.czs.gtd.api.ItemPickPopupWindow;
import com.czs.gtd.api.ItemPickPopupWindow.ItemSelectListener;
import com.czs.gtd.api.NumberPickerPopupWindow;
import com.czs.gtd.api.NumberPickerPopupWindow.NumberPickListener;
import com.czs.gtd.data.GTDConstants;
import com.czs.gtd.util.ExitUtil;
import com.czs.gtd.util.GTDUtil;

@SuppressLint("CommitPrefEdits")
public class SettingsActivity extends Activity implements UpdatePointsNotifier
{
	private List<Map<String, Object>> remindListItems = null;
	private List<Map<String, Object>> showListItems = null;
	private SharedPreferences sharedPreferences = null;
	private SharedPreferences.Editor editor = null;
	private SimpleAdapter remindSimpleAdapter = null;
	private SimpleAdapter showSimpleAdapter = null;
	private int[] showStringIds = null;
	private int[] showIcons = null;
	private int[] earnStringIds = null;
	private int[] earnIcons = null;
	private int remindTimes[] = null;
	private int currentIndex = 4;
	private List<Map<String, Object>> earnListItems = null;
	private SimpleAdapter earnSimpleAdapter = null;
	private int bitValues = 0;
	private DimPopupWindow vipPopupWindow = null;
	private int rankValues[] = null;
//	private String isBlock = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		ExitUtil.getInstance().addActivity(this);
		sharedPreferences = GTDUtil.getSharedPreferences(SettingsActivity.this);
		editor = sharedPreferences.edit();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		remind();
		show();
//		isBlock = AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, this).getConfig("isBlock", "false");
//		if (GTDConstants.TRUE.equals(isBlock))
//		{
			earn();
			GTDUtil.startProgressDialog(this, getString(R.string.settings_get_rank));
//		}
	}

	private final Handler handler = new Handler();

	private void remind()
	{
		remindTimes = new int[]
		{ 5, 10, 15, 20, 30, 40, 50, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 660, 720, 780, 840, 900, 960, 1020, 1080, 1140, 1200, 1260,
				1320, 1380, 1440 };
		CornerListView remindCornerListView = (CornerListView) findViewById(R.id.settings_remind);
		int[] remindStringIds = new int[]
		{ R.string.settings_single, R.string.settings_cycle, R.string.settings_cycle_time };
		int[] remindIcons = new int[]
		{
				sharedPreferences.getBoolean(GTDConstants.XML_SINGLE, false) ? R.drawable.settings_single_remind_icon_on
						: R.drawable.settings_single_remind_icon_off,
				sharedPreferences.getBoolean(GTDConstants.XML_REPEAT, false) ? R.drawable.settings_cycle_remind_icon_on
						: R.drawable.settings_cycle_remind_icon_off, R.drawable.settings_remind_time_icon };
		remindListItems = new ArrayList<Map<String, Object>>();
		String remindValues[] =
		{ getString(sharedPreferences.getBoolean(GTDConstants.XML_SINGLE, false) ? R.string.app_yes : R.string.app_no),
				getString(sharedPreferences.getBoolean(GTDConstants.XML_REPEAT, false) ? R.string.app_yes : R.string.app_no), "" };
		if (sharedPreferences.contains(GTDConstants.XML_REPEATVALUE))
		{
			int temp = sharedPreferences.getInt(GTDConstants.XML_REPEATVALUE, 30);
			for (int i = 0; i < remindTimes.length; i++)
			{
				if (remindTimes[i] == temp)
				{
					currentIndex = i;
					break;
				}
			}
			remindValues[2] = getResources().getStringArray(R.array.cycle_time)[currentIndex];
		}
		for (int i = 0; i < remindStringIds.length; i++)
		{
			Map<String, Object> funcListItem = new HashMap<String, Object>();
			funcListItem.put("remindIcons", remindIcons[i]);
			funcListItem.put("remindStringIds", getString(remindStringIds[i]));
			funcListItem.put("remindValues", remindValues[i]);
			remindListItems.add(funcListItem);
		}
		remindSimpleAdapter = new SimpleAdapter(this, remindListItems, R.layout.preference_item, new String[]
		{ "remindIcons", "remindStringIds", "remindValues" }, new int[]
		{ R.id.more_icon_iv, R.id.more_content_tv, R.id.more_num_tv });
		remindCornerListView.setAdapter(remindSimpleAdapter);
		remindCornerListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
				case 0: // 单个
					if (sharedPreferences.getBoolean(GTDConstants.XML_SINGLE, false))
					{
						remindListItems.get(0).put("remindIcons", R.drawable.settings_single_remind_icon_off);
						remindListItems.get(0).put("remindValues", getString(R.string.app_no));
						NotificationReceiver.cancelSingleRemind(SettingsActivity.this);
						editor.putBoolean(GTDConstants.XML_SINGLE, false);
						editor.commit();
						NotificationReceiver.cancelSingleRemind(SettingsActivity.this);
					} else
					{
						remindListItems.get(0).put("remindIcons", R.drawable.settings_single_remind_icon_on);
						remindListItems.get(0).put("remindValues", getString(R.string.app_yes));
						NotificationReceiver.setSingleRemind(SettingsActivity.this);
						editor.putBoolean(GTDConstants.XML_SINGLE, true);
						editor.commit();
						NotificationReceiver.setSingleRemind(SettingsActivity.this);
					}
					remindSimpleAdapter.notifyDataSetChanged();
					break;
				case 1: // 周期
					if (sharedPreferences.getBoolean(GTDConstants.XML_REPEAT, false))
					{
						remindListItems.get(1).put("remindIcons", R.drawable.settings_cycle_remind_icon_off);
						remindListItems.get(1).put("remindValues", getString(R.string.app_no));
						editor.putBoolean(GTDConstants.XML_REPEAT, false);
						editor.commit();
						NotificationReceiver.cancelCycleRemind(SettingsActivity.this);
					} else
					{
						remindListItems.get(1).put("remindIcons", R.drawable.settings_cycle_remind_icon_on);
						remindListItems.get(1).put("remindValues", getString(R.string.app_yes));
						editor.putBoolean(GTDConstants.XML_REPEAT, true);
						editor.commit();
						NotificationReceiver.setCycleRemind(SettingsActivity.this);
					}
					remindSimpleAdapter.notifyDataSetChanged();
					break;
				case 2: // 周期时间
					ItemPickPopupWindow itemPickPopupWindow = new ItemPickPopupWindow(SettingsActivity.this, R.string.settings_cycle_time,
							R.array.cycle_time, new ItemSelectListener()
							{
								@Override
								public void onItemSelect(ItemPickPopupWindow window, int itemIndex)
								{
									currentIndex = itemIndex;
									editor.putInt(GTDConstants.XML_REPEATVALUE, remindTimes[itemIndex]);
									editor.putBoolean(GTDConstants.XML_REPEAT, true);
									editor.commit();
									remindListItems.get(1).put("remindIcons", R.drawable.settings_cycle_remind_icon_on);
									remindListItems.get(1).put("remindValues", getString(R.string.app_yes));
									remindListItems.get(2).put("remindValues", getResources().getStringArray(R.array.cycle_time)[itemIndex]);
									remindSimpleAdapter.notifyDataSetChanged();
									NotificationReceiver.setCycleRemind(SettingsActivity.this);
								}
							});
					itemPickPopupWindow.setInitItem(currentIndex);
					itemPickPopupWindow.show(SettingsActivity.this);
					break;
				}
			}
		});
		GTDUtil.setListViewHeight(remindCornerListView);
	}

	private void show()
	{
		showStringIds = new int[]
		{ R.string.settings_notification_cover, R.string.settings_show_dated, R.string.settings_show_tomorrow };
		showIcons = new int[]
		{
				sharedPreferences.getBoolean(GTDConstants.XML_SINGLE_COVER, true) ? R.drawable.settings_cover_on : R.drawable.settings_cover_off,
				sharedPreferences.getBoolean(GTDConstants.XML_ISUNFINISHDATED, true) ? R.drawable.settings_unfinish_dated_yes
						: R.drawable.settings_unfinish_dated_no,
				sharedPreferences.getBoolean(GTDConstants.XML_SHOW_TOMORROW, true) ? R.drawable.settings_show_tom_on
						: R.drawable.settings_show_tom_off };
		String showValues[] =
		{ getString(sharedPreferences.getBoolean(GTDConstants.XML_SINGLE_COVER, true) ? R.string.app_yes : R.string.app_no),
				getString(sharedPreferences.getBoolean(GTDConstants.XML_ISUNFINISHDATED, true) ? R.string.app_yes : R.string.app_no),
				getString(sharedPreferences.getBoolean(GTDConstants.XML_SHOW_TOMORROW, true) ? R.string.app_yes : R.string.app_no) };
		showListItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < showStringIds.length; i++)
		{
			Map<String, Object> numListItem = new HashMap<String, Object>();
			numListItem.put("showIcons", showIcons[i]);
			numListItem.put("showStringIds", getString(showStringIds[i]));
			numListItem.put("showValues", showValues[i]);
			showListItems.add(numListItem);
		}

		showSimpleAdapter = new SimpleAdapter(this, showListItems, R.layout.preference_item, new String[]
		{ "showIcons", "showStringIds", "showValues" }, new int[]
		{ R.id.more_icon_iv, R.id.more_content_tv, R.id.more_num_tv });
		CornerListView showCornerListView = (CornerListView) findViewById(R.id.settings_show);
		showCornerListView.setAdapter(showSimpleAdapter);
		showCornerListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
				case 0: // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
					if (sharedPreferences.getBoolean(GTDConstants.XML_SINGLE_COVER, false))
					{
						editor.putBoolean(GTDConstants.XML_SINGLE_COVER, false);
						showListItems.get(0).put("showIcons", R.drawable.settings_cover_off);
						showListItems.get(0).put("showValues", getString(R.string.app_no));
						editor.commit();
					} else
					{
						editor.putBoolean(GTDConstants.XML_SINGLE_COVER, true);
						showListItems.get(0).put("showIcons", R.drawable.settings_cover_on);
						showListItems.get(0).put("showValues", getString(R.string.app_yes));
						editor.commit();
					}
					showSimpleAdapter.notifyDataSetChanged();
					break;
				case 1: // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷燃锟�
					if (sharedPreferences.getBoolean(GTDConstants.XML_ISUNFINISHDATED, false))
					{
						editor.putBoolean(GTDConstants.XML_ISUNFINISHDATED, false);
						showListItems.get(1).put("showIcons", R.drawable.settings_unfinish_dated_no);
						showListItems.get(1).put("showValues", getString(R.string.app_no));
						editor.commit();
					} else
					{
						editor.putBoolean(GTDConstants.XML_ISUNFINISHDATED, true);
						showListItems.get(1).put("showIcons", R.drawable.settings_unfinish_dated_yes);
						showListItems.get(1).put("showValues", getString(R.string.app_yes));
						editor.commit();
					}
					showSimpleAdapter.notifyDataSetChanged();
					break;
				case 2: // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷燃锟�
					if (sharedPreferences.getBoolean(GTDConstants.XML_SHOW_TOMORROW, false))
					{
						editor.putBoolean(GTDConstants.XML_SHOW_TOMORROW, false);
						showListItems.get(2).put("showIcons", R.drawable.settings_show_tom_off);
						showListItems.get(2).put("showValues", getString(R.string.app_no));
						editor.commit();
						Evernote.update(SettingsActivity.this);
					} else
					{
						editor.putBoolean(GTDConstants.XML_SHOW_TOMORROW, true);
						showListItems.get(2).put("showIcons", R.drawable.settings_show_tom_on);
						showListItems.get(2).put("showValues", getString(R.string.app_yes));
						editor.commit();
						Evernote.update(SettingsActivity.this);
					}
					showSimpleAdapter.notifyDataSetChanged();
					break;
				}
			}
		});
		GTDUtil.setListViewHeight(showCornerListView);
	}

	private void earn()
	{
		rankValues = new int[]
		{ 0, 1000, 2000, 3000, 5000, 8000, 13000, 21000, 34000, 55000, 89000, 144000 };
		CornerListView earnCornerListView = (CornerListView) findViewById(R.id.settings_earn);
		earnCornerListView.setVisibility(View.VISIBLE);
		earnStringIds = new int[]
		{ R.string.settings_vip, R.string.settings_rank, R.string.settings_game, R.string.settings_app, R.string.settings_bit,
				R.string.settings_strategy };
		earnIcons = new int[]
		{ R.drawable.settings_vip_icon, R.drawable.settings_rank_icon, R.drawable.settings_game_icon, R.drawable.settings_app_icon,
				R.drawable.settings_bit_icon, R.drawable.setings_strategy_icon };
		String earnValues[] =
		{ "", "", "", "", "", "" };
		earnListItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < earnStringIds.length; i++)
		{
			Map<String, Object> personListItem = new HashMap<String, Object>();
			personListItem.put("earnIcons", earnIcons[i]);
			personListItem.put("earnStringIds", getString(earnStringIds[i]));
			personListItem.put("earnValues", earnValues[i]);
			earnListItems.add(personListItem);
		}
		earnSimpleAdapter = new SimpleAdapter(this, earnListItems, R.layout.preference_item, new String[]
		{ "earnIcons", "earnStringIds", "earnValues" }, new int[]
		{ R.id.more_icon_iv, R.id.more_content_tv, R.id.more_num_tv });
		earnCornerListView.setAdapter(earnSimpleAdapter);
		earnCornerListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				switch (arg2)
				{
				case 0: // vip
					if (sharedPreferences.getBoolean(GTDConstants.XML_IS_VIP, false))
					{
						startActivity(new Intent(SettingsActivity.this, VIPActivity.class));
					} else
					{
						showVipPopupWindow();
					}
					break;
				case 1: // 绛夌骇
					int score = sharedPreferences.getInt(GTDConstants.XML_RANK_VALUE, 0);
					if (score > 0)
					{
						NumberPickerPopupWindow scoreToBit = new NumberPickerPopupWindow(SettingsActivity.this, getString(R.string.vip_score_to_bit),
								1, score, new NumberPickListener()
								{
									@Override
									public void onNumberPick(int number)
									{
										int score = sharedPreferences.getInt(GTDConstants.XML_RANK_VALUE, 0);
										score = score - number;
										editor.putInt(GTDConstants.XML_RANK_VALUE, score);
										editor.commit();
										AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).awardPoints(
												number, SettingsActivity.this);
									}
								});
						scoreToBit.show(SettingsActivity.this);
					} else
					{
						GTDUtil.toastTip(SettingsActivity.this, R.string.vip_score_no_enough);
					}
					break;
				case 2: // 娓告垙
					AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).showGameOffers(SettingsActivity.this);
					break;
				case 3:
					AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).showAppOffers(SettingsActivity.this);
					break;
				case 4: // 绛夌骇
					if (bitValues > 0)
					{
						NumberPickerPopupWindow numberPickerPopupWindow = new NumberPickerPopupWindow(SettingsActivity.this,
								getString(R.string.vip_bit_to_score), 1, bitValues, new NumberPickListener()
								{
									@Override
									public void onNumberPick(int number)
									{
										int score = sharedPreferences.getInt(GTDConstants.XML_RANK_VALUE, 0);
										score = score + number;
										editor.putInt(GTDConstants.XML_RANK_VALUE, score);
										editor.commit();
										AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).spendPoints(
												number, SettingsActivity.this);
									}
								});
						numberPickerPopupWindow.show(SettingsActivity.this);
					} else
					{
						GTDUtil.toastTip(SettingsActivity.this, R.string.vip_bit_no_enough);
					}
					break;
				case 5: // 绛夌骇
					showPopupWindow();
					break;
				}
			}
		});
		GTDUtil.setListViewHeight(earnCornerListView);
	}

	@Override
	public void getUpdatePoints(String arg0, int arg1)
	{
		bitValues = arg1;
		handler.post(mUpdateResults);
		GTDUtil.stopProgressDialog();
	}

	final Runnable mUpdateResults = new Runnable()
	{
		public void run()
		{
			if (earnSimpleAdapter != null)
			{
				showScore();
				earnListItems.get(4).put("earnValues", String.valueOf(bitValues));
				earnSimpleAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void getUpdatePointsFailed(String arg0)
	{
		GTDUtil.stopProgressDialog();
	}

	@Override
	protected void onStart()
	{
//		if (GTDConstants.TRUE.equals(isBlock))
//		{
			AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, this).getPoints(this);
//		}
		super.onStart();
	}

	private void showScore()
	{
		int score = sharedPreferences.getInt(GTDConstants.XML_RANK_VALUE, 0);
		int rank = 0;
		for (int i = rankValues.length - 1; i > 0; i--)
		{
			if (score >= rankValues[i])
			{
				rank = i;
				break;
			}
		}
		Random random = new Random();
		earnListItems.get(1).put("earnValues", getResources().getStringArray(R.array.hold_ranks)[rank] + ":" + score);
		earnListItems.get(5).put("earnValues", getString(R.string.settings_net_rank) + ":" + (50000 - score + random.nextInt(5)));
	}

	private void showVipPopupWindow()
	{
		this.vipPopupWindow = null;
		View view = getLayoutInflater().inflate(R.layout.vip_popup, null, false);
		TextView titleTextView = (TextView) view.findViewById(R.id.tv_wxf_vip_title);
		Button getMoreButton = (Button) view.findViewById(R.id.btn_wxf_get_bit);
		Button buyButton = (Button) view.findViewById(R.id.btn_wxf_buy_vip);
		Button cancelButton = (Button) view.findViewById(R.id.btn_wxf_not_buy);
		if (bitValues < 2000) // 姣旂壒甯佷笉澶�
		{
			titleTextView.setText(getString(R.string.vip_bit_no_enough));
			buyButton.setText(getString(R.string.settings_game));
		} else
		{
			getMoreButton.setVisibility(View.GONE);
		}

		this.vipPopupWindow = new DimPopupWindow(SettingsActivity.this, R.style.wxf_anim_fade, Gravity.CENTER);
		this.vipPopupWindow.setContentView(view);
		this.vipPopupWindow.setBackDismiss(true);
		cancelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SettingsActivity.this.vipPopupWindow.dismiss();
				SettingsActivity.this.vipPopupWindow = null;
			}
		});
		buyButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (bitValues < 2000)
				{
					AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).showGameOffers(SettingsActivity.this);
				} else
				{
					AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).spendPoints(2000,
							SettingsActivity.this); //
					editor.putBoolean(GTDConstants.XML_IS_VIP, true);
					editor.commit();
					startActivity(new Intent(SettingsActivity.this, VIPActivity.class));
				}
				SettingsActivity.this.vipPopupWindow.dismiss();
				SettingsActivity.this.vipPopupWindow = null;
			}
		});

		getMoreButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppConnect.getInstance(GTDConstants.WAPS_ID, GTDConstants.WAPS_PID, SettingsActivity.this).showAppOffers(SettingsActivity.this);
				SettingsActivity.this.vipPopupWindow.dismiss();
				SettingsActivity.this.vipPopupWindow = null;
			}
		});
		this.vipPopupWindow.show(SettingsActivity.this);
	}

	private void showPopupWindow()
	{
		this.vipPopupWindow = null;
		View view = getLayoutInflater().inflate(R.layout.hold_popup, null, false);
		this.vipPopupWindow = new DimPopupWindow(SettingsActivity.this, R.style.wxf_anim_fade, Gravity.CENTER);
		this.vipPopupWindow.setContentView(view);
		this.vipPopupWindow.setBackDismiss(true);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.vipPopupWindow.setWindowWidth((int) (dm.widthPixels * 0.9));
		this.vipPopupWindow.setWindowHeight((int) (dm.heightPixels * 0.9));
		ImageView closeImageView = (ImageView) view.findViewById(R.id.iv_hold_popup_close);
		closeImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SettingsActivity.this.vipPopupWindow.dismiss();
				SettingsActivity.this.vipPopupWindow = null;
			}
		});
		this.vipPopupWindow.show(SettingsActivity.this);
	}
}
