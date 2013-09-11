package com.czs.gtd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.czs.gtd.util.ExitUtil;

public class AboutActivity extends Activity
{
	private Button backButton = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		ExitUtil.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		backButton = (Button)findViewById(R.id.btn_about_back);
		backButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
}
