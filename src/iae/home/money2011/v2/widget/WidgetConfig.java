package iae.home.money2011.v2.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.j256.ormlite.dao.Dao;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import iae.home.money2011.v2.GenericActivity;
import iae.home.money2011.v2.LookupArrayAdapter;
import iae.home.money2011.v2.R;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.LookupEntity;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;

public class WidgetConfig extends GenericActivity<Void> {
	private int m_appwidgetId=AppWidgetManager.INVALID_APPWIDGET_ID;
	private AccountFilter m_filter=null;
	private String[] m_currencyList;
	private AccountEntity[] m_accountList;
	private Boolean m_loaded=false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
        setContentView(R.layout.widget_config);

        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, Void>() {
			
			@Override
			public void onTaskComplete(Task<Void, Void> task, Void result) {
				if(task.isCancelled()) {
					Toast.makeText(WidgetConfig.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					setResult(RESULT_CANCELED);
					finish();
				} else {
					FilterLoad tckLoad=(FilterLoad)task;
					m_loaded=true;
					m_currencyList=tckLoad.getCurrencyCodeList();
					m_accountList=tckLoad.getAccountList();
					fillFilter();
				}				
			}
		});
        
        if(savedInstanceState!=null) {
        	m_appwidgetId=savedInstanceState.getInt("id");
        	m_filter=(AccountFilter)savedInstanceState.getSerializable("filter");
        	m_loaded=savedInstanceState.getBoolean("load");
        	if(m_loaded) {
        		m_accountList=(AccountEntity[])savedInstanceState.getSerializable("account");
        		m_currencyList=(String[])savedInstanceState.getSerializable("currency");
        	} else {
        		fillData();
        	}
        } else {
        	Bundle extras = getIntent().getExtras();
        	if (extras != null) {
        		m_appwidgetId = extras.getInt(
        	            AppWidgetManager.EXTRA_APPWIDGET_ID, 
        	            AppWidgetManager.INVALID_APPWIDGET_ID);        		
        	}
        	
        	m_filter=new AccountFilter();
        	m_filter.load(this, m_appwidgetId);
        	fillData();
        }
                
	}
	
    @Override
    protected void onResume() {
		super.onResume();
		
		if(m_loaded)
			fillFilter();
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("id", m_appwidgetId);
    	outState.putSerializable("filter", m_filter);
    	outState.putBoolean("load", m_loaded);
    	if(m_loaded) {
    		outState.putSerializable("account", m_accountList);
    		outState.putSerializable("currency", m_currencyList);
    	}
    }
	
    private void fillData() {
    	if(!m_taskManager.isWorking())
    		m_taskManager.setupTask(new FilterLoad(getHelper(), this));
    }
    
    private void fillFilter() {
    	if(m_filter.getAccountId()>=0) {
    		for (AccountEntity a : m_accountList) {
				if(a.getId().equals(m_filter.getAccountId())) {
					m_filter.setCurrency(a.getCurrency());
					break;
				}
			}
    	}
    	
    	Spinner spnCurrency=(Spinner)findViewById(R.id.widg_conf_spn_currency);
    	ArrayAdapter<String> spnCurrencyAdapter=(ArrayAdapter<String>)spnCurrency.getAdapter();
    	if(spnCurrencyAdapter==null) {
    		spnCurrencyAdapter=new ArrayAdapter<String>(this, 
    			android.R.layout.simple_spinner_item,
    			m_currencyList);
    		spnCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
    		spnCurrency.setAdapter(spnCurrencyAdapter);
    	}
    	
    	Integer select=0;
    	Integer count=spnCurrencyAdapter.getCount();
    	for(int indx=0; indx < count; indx++) {
    		if(spnCurrencyAdapter.getItem(indx).equals(m_filter.getCurrency())) {
    			select=indx;
    			break;
    		}
    	}
    	spnCurrency.setSelection(select);
//    	m_filter.setCurrency(spnCurrencyAdapter.getItem(select));
    	
    	Spinner spnAccounts=(Spinner)findViewById(R.id.widg_conf_spn_accounts);
    	LookupArrayAdapter spnAccountAdapter=(LookupArrayAdapter)spnAccounts.getAdapter();
    	if(spnAccountAdapter==null) {
    		spnAccountAdapter=new LookupArrayAdapter(this, getAccountList());
    		spnAccounts.setAdapter(spnAccountAdapter);
    	} else {
    		spnAccountAdapter.setNewDataset(getAccountList());
    	}
    	
    	select=0;
    	count=spnAccountAdapter.getCount();
    	for(int indx=0; indx < count; indx++) {
    		if(((ILookupEntity)spnAccountAdapter.getItem(indx)).getId().equals(m_filter.getAccountId())) {
    			select=indx;
    			break;
    		}
    	}
    	spnAccounts.setSelection(select);
    	//m_filter.setAccountId(((ILookupEntity)spnAccountAdapter.getItem(select)).getId());

        spnCurrency.setOnItemSelectedListener(m_currencyListener);
        spnAccounts.setOnItemSelectedListener(m_accountListener);
        
        Button btnOk=(Button)findViewById(R.id.widg_conf_btnOk);
        btnOk.setOnClickListener(m_btnOkClick);
    }
    
    private View.OnClickListener m_btnOkClick=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.i(this.getClass().getName(),"Save button");			
			final Context context=WidgetConfig.this;
			m_filter.save(context, m_appwidgetId);
			
			Map<String,String> mapEvent=new HashMap<String, String>();
			if(m_filter.getAccountId()<0)
				mapEvent.put("state", "All");
			else
				mapEvent.put("state", "Select");
			
			FlurryAgent.onEvent("on_WidgetConfig",mapEvent);
			
            //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AccountFilter.addWidget(context, m_appwidgetId);
            WidgetProvider.updateAppWidget(context,  m_appwidgetId);
			
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, m_appwidgetId);
            setResult(RESULT_OK, resultValue);
            finish();			
		}
	};
	
	private OnItemSelectedListener m_currencyListener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			String selCur=(String)parent.getAdapter().getItem(position);
			if(!m_filter.getCurrency().equals(selCur)) {
				m_filter.setCurrency(selCur);
				m_filter.setAccountId(-1);

				Spinner spnAccounts=(Spinner)findViewById(R.id.widg_conf_spn_accounts);
				spnAccounts.setAdapter(new LookupArrayAdapter(WidgetConfig.this, getAccountList()));

				spnAccounts.setSelection(0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnItemSelectedListener m_accountListener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			m_filter.setAccountId(((ILookupEntity)parent.getAdapter().getItem(position)).getId());			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};

	private ILookupEntity[] getAccountList() {
		int cnt=0;
		String currency=m_filter.getCurrency();
		
		for (AccountEntity a : m_accountList) {
			if(a.getCurrency().equals(currency))
				cnt++;
		}
		
		ILookupEntity[] ret=new LookupEntity[cnt+1];
		ret[0]=new LookupEntity(-1, getResources().getString(R.string.all_accounts).toString(),0);
		
		cnt=1;
		for (AccountEntity a : m_accountList) {
			if(a.getCurrency().equals(currency)) {
				ret[cnt]=new LookupEntity(a.getId(), a.getName(),0);
				cnt++;
			}
		}
		
		return ret;
	}
    
	protected class FilterLoad extends GenericTask<Void> {
		private String[] m_currency_list;
		private AccountEntity[] m_account_list;

		protected FilterLoad(DatabaseHelper dbhelper, Context context) {
			super(dbhelper, context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			publishProgress(R.string.task_load);
			try {
				Dao<AccountEntity,Integer> daoAccount=m_dbhelper.getAccountsDao();
				List<AccountEntity> listAccounts=
						daoAccount
							.queryBuilder()
								.orderBy(AccountEntity.NAME, true)
							.where()								
								.eq(AccountEntity.DELETED, false)
						.query();
				m_account_list=new AccountEntity[listAccounts.size()];
				listAccounts.toArray(m_account_list);
				
				HashSet<String> hashCurrency=new HashSet<String>();
				for (AccountEntity a : m_account_list) {
					if(!hashCurrency.contains(a.getCurrency())) {
						hashCurrency.add(a.getCurrency());
					}
				}
				
				m_currency_list=new String[hashCurrency.size()];
				
				int indx=0;
				for (String c : hashCurrency) {
					m_currency_list[indx++]=c;
				}
				
				
			} catch(Exception e) {
				Log.e(this.getClass().getName(),"Load",e);
			}
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getCurrencyCodeList() {
			return m_currency_list;
		}
		
		public AccountEntity[] getAccountList() {
			return m_account_list;
		}
	}	
}
