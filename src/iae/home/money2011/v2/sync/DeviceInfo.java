package iae.home.money2011.v2.sync;

import iae.home.x10.model.IDeviceInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;

public class DeviceInfo implements IDeviceInfo {
	private String m_package;
	private String m_packageVersion;
	private String m_phoneId;

	private static DeviceInfo m_info=null; 
		
	protected DeviceInfo(Context context) {
		PackageManager pm=context.getPackageManager();
		try {
			PackageInfo pi= pm.getPackageInfo(context.getPackageName(), 0);
			m_package=pi.packageName;
			m_packageVersion=pi.versionName;
			m_phoneId=((TelephonyManager)context.getSystemService("phone")).getDeviceId();
			
		} catch(Exception e) {
			FlurryAgent.onError("DeviceInfo", e.getLocalizedMessage(),"");
			DefaultExceptionHandler.reportException(e);
		}
	}
	
	public static DeviceInfo getInstance(Context context) {
		if(m_info==null)
			m_info=new DeviceInfo(context);
		return m_info;
	}
	
	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return m_package;
	}

	@Override
	public String getPhoneId() {
		// TODO Auto-generated method stub
		return m_phoneId;
	}

	@Override
	public String getPhoneModel() {
		// TODO Auto-generated method stub
		return android.os.Build.MODEL;
	}

	@Override
	public String getPhoneOS() {
		// TODO Auto-generated method stub
		return android.os.Build.VERSION.RELEASE;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return m_packageVersion;
	}
	
}
