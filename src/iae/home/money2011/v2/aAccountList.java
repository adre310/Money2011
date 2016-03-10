package iae.home.money2011.v2;

import java.sql.SQLException;
import java.util.List;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;
import com.nullwire.trace.ExceptionHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import iae.home.money2011.v2.R;
import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.sync.DeviceInfo;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.utils.text.textUtils;

public class aAccountList extends GenericActivity<List<AccountEntity>> {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	
	private static final int ACTIVITY_SECURITY=123456;
	
	private boolean is_authorization=false; 
	
	private ListView m_lvBudgetList;

    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean("auth", is_authorization);
        Log.i(this.getClass().getName(),"save is_authorization = "+Boolean.toString(is_authorization));
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_list);
    	startAdView((LinearLayout)findViewById(R.id.bud_list_main));

        ExceptionHandler.register(this);
        
        if(savedInstanceState!=null) {
        	is_authorization=savedInstanceState.getBoolean("auth", false);
            Log.i(this.getClass().getName(),"restore is_authorization = "+Boolean.toString(is_authorization));
        }
        	
        m_lvBudgetList=(ListView)findViewById(R.id.lvBudgetList);
        m_lvBudgetList.setOnCreateContextMenuListener(this);
        m_lvBudgetList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View v, int position, long id) {
				AccountEntity it=(AccountEntity)m_lvBudgetList.getAdapter().getItem(position);
				Intent intent=new Intent(aAccountList.this,aAccountView.class);
				intent.putExtra("id", it.getId());
				startActivityForResult(intent, ACTIVITY_EDIT);
			}
		});
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, List<AccountEntity>>() {

			@Override
			public void onTaskComplete(Task<Void, List<AccountEntity>> task,
					List<AccountEntity> result) {
				if(task.isCancelled()) {
					Toast.makeText(aAccountList.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					finish();
				} else {
					if(task instanceof BudgetListDelete) {
				        startService(new Intent(aAccountList.this, SyncService.class));
					}
					m_lvBudgetList.setAdapter(new BudgetListAdapter(aAccountList.this, result));
				}
			}
		});

        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
  	  	String sUsername=preferences.getString("online_username", "");
  	  	
	    if(preferences.getBoolean("online_enable", false) && !sUsername.trim().equals("")) {	    	  		 	  
	    	FlurryAgent.setUserId(sUsername);
	    }

        Log.i(this.getClass().getName(),"create is_authorization = "+Boolean.toString(is_authorization));
        if(preferences.getBoolean("auth_enable", false) && !preferences.getString("auth_pin_code", "").trim().equals("") && !is_authorization) {
        	is_authorization=false;
        	startActivityForResult(new Intent(this,aInitialAuthorization.class), ACTIVITY_SECURITY);
        } else {
        	is_authorization=true;
        }
        
        if(savedInstanceState==null) {
        	startService(new Intent(this, SyncService.class));
        }
    }

    @Override
    protected void onResume() {
		super.onResume();

		if(is_authorization) {
			fillData();
		}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if((requestCode==ACTIVITY_SECURITY) && (resultCode!=1)) {
			is_authorization=false;
			finish();
		} else {
        	is_authorization=true;
    		fillData();
		}	
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.budget_list_menu, menu);
        
       return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent menuIntent;

    	switch (item.getItemId()) {
    	case R.id.mn_bud_list_add:
    		menuIntent=new Intent(this,aAccountCreate.class);
    		startActivityForResult(menuIntent, ACTIVITY_CREATE);
    		return true;
		case R.id.mn_bud_list_categories:
			menuIntent=new Intent(this,aCategoryList.class);
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			return true;
		case R.id.mn_bud_list_settings:
			menuIntent=new Intent(this,aSettings.class);
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			return true;
		case R.id.mn_bud_list_about:
			menuIntent=new Intent(this,aAbout.class);
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			return true;
		case R.id.mn_bud_list_register:
			menuIntent=new Intent(this,aRegisterWeb.class);
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			return true;
		case R.id.mn_bud_list_sync:
	        startService(new Intent(this, SyncService.class));
			break;
    	case R.id.mn_bud_list_report_all_category:
    		menuIntent=new Intent(this, aGraphicsPie.class);
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
    		break;
    	case R.id.mn_bud_list_report_monthly:
    		menuIntent=new Intent(this, aGraphicsBar.class);
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
    		break;
    	}
    	return true;
    }
 
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.budget_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent menuIntent;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		AccountEntity it=(AccountEntity)m_lvBudgetList.getAdapter().getItem(info.position);
		
		switch (item.getItemId()) {
		case R.id.mn_bud_list_edit:
			menuIntent=new Intent(aAccountList.this,aAccountEdit.class);
			menuIntent.putExtra("id", it.getId());
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			return true;
		case R.id.mn_bud_list_delete:			
			if(!m_taskManager.isWorking())
				m_taskManager.setupTask(new BudgetListDelete(getHelper(), this, it));
			return true;
		case R.id.mn_bud_list_transfer:
			menuIntent=new Intent(aAccountList.this,aTransfer.class);
			menuIntent.putExtra("id", it.getId());
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			break;
		case R.id.mn_bud_list_merge:
			menuIntent=new Intent(aAccountList.this,aMerge.class);
			menuIntent.putExtra("id", it.getId());
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
			break;
		}
		return super.onContextItemSelected(item);
	}	
    
    private void fillData() {
    	if(!m_taskManager.isWorking()) {
    		showWhatsNewsDialog();
    		m_taskManager.setupTask(new BudgetListLoad(getHelper(),this));
    	}
    }
    
    private void showWhatsNewsDialog() {
    	ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected())
		        return;
    	
    	DeviceInfo info=DeviceInfo.getInstance(this);
    	SharedPreferences prefs=this.getSharedPreferences("iae.home.money2011.v2.vers", 0);
    	
//    	if(!prefs.getString("vers", "").equals(info.getVersion())) {
    		SharedPreferences.Editor editor=prefs.edit();
    		editor.putString("vers", info.getVersion());
    		editor.commit();

    		AlertDialog.Builder builder=new AlertDialog.Builder(this); 

    		WebView web=new WebView(this);
    		web.getSettings().setJavaScriptEnabled(true);
    		web.setWebViewClient(new MyWebViewClient());
    		
    		web.loadUrl("http://adre310.x10.mx/whatnews/money2011/"+info.getVersion());
    		builder
    			.setCancelable(true)/*
    			.setPositiveButton("OK", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();					
					}
				})*/
				.setView(web);
    		AlertDialog alertDialog=builder.create();
    		alertDialog.show();

    		//    		startActivity(new Intent(this,aWhatNews.class));
//    	}
    }
    
    protected static class BudgetListViewHolder {
		protected TextView tvName;
		protected TextView tvBalance;
	}
    
    protected class BudgetListAdapter extends ArrayAdapter<AccountEntity> {
    	private final Activity m_context;
	
		public BudgetListAdapter(Activity context, List<AccountEntity> objects) {
			super(context,R.layout.budget_row, objects);
			m_context=context;
		}
    	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BudgetListViewHolder holder;
			// Recycle existing view if passed as parameter
			// This will save memory and time on Android
			// This only works if the base layout for all classes are the same
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = m_context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.budget_row, null, true);
				holder = new BudgetListViewHolder();
				holder.tvName=(TextView)rowView.findViewById(R.id.tvBudRowName);
				holder.tvBalance=(TextView)rowView.findViewById(R.id.tvBudRowBalance);				
				rowView.setTag(holder);
			} else {
				holder = (BudgetListViewHolder) rowView.getTag();
			}

			AccountEntity item=getItem(position);
			
			holder.tvName.setText(item.getName());
			holder.tvBalance.setText(textUtils.CurrencyToString(item.getBalance(),item.getCurrency()));
			return rowView;
		}	
    }
        
    protected class BudgetListLoad extends GenericTask<List<AccountEntity>> {

		protected BudgetListLoad(DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
		}

		@Override
		protected List<AccountEntity> doInBackground(Void... arg0) {
			publishProgress(R.string.task_load);
			try {
				return AccountActions.getAccountList(m_dbhelper);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не удалось получить список бюджетов", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}   	
    }
    
    protected class BudgetListDelete extends GenericTask<List<AccountEntity>> {
    	private final AccountEntity m_entity;

    	protected BudgetListDelete(DatabaseHelper dbhelper, Activity context, AccountEntity account) {
			super(dbhelper,context);
			m_entity=account;
		}

		@Override
		protected List<AccountEntity> doInBackground(Void... arg0) {
			publishProgress(R.string.task_delete);
			try {
				AccountActions.deleteAccount(m_dbhelper,m_entity);
				return AccountActions.getAccountList(m_dbhelper);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не удалось получить список бюджетов", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}   	
    }
 
	protected class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        if (Uri.parse(url).getHost().equals("adre310.x10.mx")) {
	            // This is my web site, so do not override; let my WebView load the page
	            return false;
	        }
	        return true;
	    }
	    
	    public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
	    	FlurryAgent.onError(description, Integer.toString(errorCode), failingUrl);
	    }
	    
	    public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
	    	handler.proceed();
	    }
	}
    
}