package iae.home.money2011.v2;

import iae.home.money2011.v2.sync.DeviceInfo;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;

import android.os.Bundle;
import android.widget.TextView;

public class aAbout extends GenericActivity<Void> {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        TextView ver=(TextView)findViewById(R.id.about_ver_name);
        DeviceInfo info=DeviceInfo.getInstance(this);
        		
		try {
			ver.setText("Ver: "+info.getVersion());

		} catch(Exception e) {
			FlurryAgent.onError("About", e.getLocalizedMessage(),"");
			DefaultExceptionHandler.reportException(e);
		}
	}
}
