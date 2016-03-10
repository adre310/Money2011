package iae.home.money2011.v2;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class aSettings extends PreferenceActivity {
	@Override
	public void onStart()
	{
	   super.onStart();
	   FlurryAgent.onStartSession(this, "B32VKDDEAEMMD4LIRPXH");
	}
	
	@Override
	public void onStop()
	{
	   super.onStop();
	   FlurryAgent.onEndSession(this);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Note that none of the preferences are actually defined here.
        // They're all in the XML file res/xml/preferences.xml.
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
