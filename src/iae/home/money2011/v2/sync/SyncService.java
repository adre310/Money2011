package iae.home.money2011.v2.sync;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.widget.WidgetProvider;
import iae.home.utils.orm.service.OrmLiteBaseIntentService;
import iae.home.x10.SyncServerException;
import iae.home.x10.client.RestClient;
import iae.home.x10.client.SyncServerClient;

public class SyncService extends OrmLiteBaseIntentService<DatabaseHelper> {
	
	public SyncService() {
		super("syncserver");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		  Context context = getApplicationContext();
		  WidgetProvider.updateAllAppWidget(context);
		
		  ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		  if (networkInfo == null || !networkInfo.isConnected())
		        return;
		
		  DeviceInfo info=DeviceInfo.getInstance(context);
		  RestClient.AGENT_NAME=info.getPackageName();
		  RestClient.AGENT_VERSION=info.getVersion();
		  
	      SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
    	  String sUsername=preferences.getString("online_username", "");
    	  String sPassword=preferences.getString("online_password", "");
	          	  
	      if(preferences.getBoolean("online_enable", false) && !sUsername.trim().equals("")) {	    	   	  
	    	  FlurryAgent.onStartSession(context, "B32VKDDEAEMMD4LIRPXH");
	      	  FlurryAgent.setUserId(preferences.getString("online_username", "").trim());		   

	    	  try {
	    		  SyncServerClient ssc=new SyncServerClient(new SyncModelImpl(getHelper(), context), sUsername, sPassword);
	    		  ssc.call();
	    		  showToast( "Sync: Successful");
				  Map<String,String> mapEvent=new HashMap<String, String>();
				  mapEvent.put("status", "Successful");
				  FlurryAgent.onEvent("on_Synchronization", mapEvent);
				  Log.i(this.getClass().getName(),"Successful");
	    	  } catch(SyncServerException sse) {
	    		  showToast("Sync error: "+sse.getLocalizedMessage());
				  Map<String,String> mapEvent=new HashMap<String, String>();
				  mapEvent.put("status", sse.getMessage());
				  FlurryAgent.onEvent("on_Synchronization", mapEvent);
	    	  } catch(java.net.UnknownHostException e) {
	    		  showToast("Sync: DNS error");
				  Map<String,String> mapEvent=new HashMap<String, String>();
				  mapEvent.put("status", "DNS Error");
				  FlurryAgent.onEvent("on_Synchronization", mapEvent);
				  FlurryAgent.onError("Synchronization", e.getLocalizedMessage(),this.getClass().getName());
				  Log.e(this.getClass().getName(),"Не удалось синхронизироваться", e);
				  DefaultExceptionHandler.reportException(e);	    		  
	    	  } catch(org.apache.http.conn.ConnectTimeoutException e) {
	    		  showToast("Sync: Server timeout");
				  Map<String,String> mapEvent=new HashMap<String, String>();
				  mapEvent.put("status", "Server timeout");
				  FlurryAgent.onEvent("on_Synchronization", mapEvent);
				  FlurryAgent.onError("Synchronization", e.getLocalizedMessage(),this.getClass().getName());
				  Log.e(this.getClass().getName(),"Не удалось синхронизироваться", e);
				  DefaultExceptionHandler.reportException(e);				  
	    	  } catch(javax.net.ssl.SSLException e) {
	    		  showToast("Sync: Server timeout");
				  Map<String,String> mapEvent=new HashMap<String, String>();
				  mapEvent.put("status", "Server timeout");
				  FlurryAgent.onEvent("on_Synchronization", mapEvent);
				  FlurryAgent.onError("Synchronization", e.getLocalizedMessage(),this.getClass().getName());
				  Log.e(this.getClass().getName(),"Не удалось синхронизироваться", e);
				  DefaultExceptionHandler.reportException(e);				  
			  } catch(Exception e) {
				  showToast("Sync error");
				  Map<String,String> mapEvent=new HashMap<String, String>();
				  mapEvent.put("status", "Error");
				  FlurryAgent.onEvent("on_Synchronization", mapEvent);
				  FlurryAgent.onError("Synchronization", e.getLocalizedMessage(),this.getClass().getName());
				  Log.e(this.getClass().getName(),"Не удалось синхронизироваться", e);
				  DefaultExceptionHandler.reportException(e);
			  } finally {
				 FlurryAgent.onEndSession(context);
				 Log.i(this.getClass().getName(),"finish");
			  }
	      }
	}
}
