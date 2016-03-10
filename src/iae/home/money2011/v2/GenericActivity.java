package iae.home.money2011.v2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.utils.orm.activity.OrmLiteBaseAsyncActivity;

public class GenericActivity<Result> extends OrmLiteBaseAsyncActivity<DatabaseHelper, Result> {
	protected AdView m_ad_view=null;
	
	@Override
	public void onStart()
	{
	   super.onStart();
	   SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
	   FlurryAgent.setUseHttps(true);
	   FlurryAgent.onStartSession(this, "B32VKDDEAEMMD4LIRPXH");
	   FlurryAgent.onPageView();
	      
	   if(preferences.getBoolean("online_enable", false)) {
		   FlurryAgent.setUserId(preferences.getString("online_username", "").trim());		   
	   }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m_ad_view = new AdView(this, AdSize.BANNER, "a14ed8d228a8bee");
	}
	
	@Override
	public void onStop()
	{
	   super.onStop();
	   FlurryAgent.onEndSession(this);
	}

	@Override
	public void onDestroy() {
	    if (m_ad_view != null) {
	    	m_ad_view.destroy();
	      }
		super.onDestroy();
	}
	
	protected void startAdView(LinearLayout layout) 
	{
		layout.addView(m_ad_view,0);
		m_ad_view.loadAd(new AdRequest());
	}
}
