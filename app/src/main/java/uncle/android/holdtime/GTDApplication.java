package uncle.android.holdtime;

import android.app.Application;

import com.czs.gtd.util.GTDUtil;
import com.google.android.gms.ads.MobileAds;

public class GTDApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		GTDUtil.init(getApplicationContext());
	}
}
