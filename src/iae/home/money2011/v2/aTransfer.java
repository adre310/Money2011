package iae.home.money2011.v2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;

public class aTransfer extends GenericActivity<ILookupEntity[]> {

	private Boolean m_isloaded=false;
	private Integer m_id=-1;
	private Double m_value=0D;
	private ILookupEntity[] m_accounts;
	private Integer m_select_account=-1;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer);

    	startAdView((LinearLayout)findViewById(R.id.transfer_main));
        
        if(savedInstanceState==null) {
            Bundle extras = getIntent().getExtras();
            if(extras==null) {
            	Log.e(this.getClass().getName(),"Ошибка в передаче параметров");
            	finish();
            }
            
            m_id=extras.getInt("id");
        	fillData();
        } else {
        	m_id=savedInstanceState.getInt("id");
        	m_isloaded=savedInstanceState.getBoolean("load");
        	if(m_isloaded) {
        		m_accounts=(ILookupEntity[])savedInstanceState.getSerializable("accounts");
        		m_value=savedInstanceState.getDouble("value");
        		m_select_account=savedInstanceState.getInt("selected");
        	} else {
        		fillData();
        	}
        }
        
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, ILookupEntity[]>() {
			
			@Override
			public void onTaskComplete(Task<Void, ILookupEntity[]> task,
					ILookupEntity[] result) {
				if(task.isCancelled()) {
					Toast.makeText(aTransfer.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					if(task instanceof TransferLoad) {
						finish();
					}
				} else {
					if(task instanceof TransferLoad) {
						TransferLoad tckLoad=(TransferLoad)task;
						m_accounts=result;
						m_isloaded=true;
						if(!tckLoad.Successful()) {
							Toast.makeText(aTransfer.this, tckLoad.getErrorMessage(), Toast.LENGTH_LONG).show();
							finish();
						} else {						
							fillForm();
						}
					} else if(task instanceof TransferSave) {
						TransferSave tckSave=(TransferSave)task;
						if(!tckSave.Successful()) {
							Toast.makeText(aTransfer.this, tckSave.getErrorMessage(), Toast.LENGTH_LONG).show();
						} else {
							startService(new Intent(aTransfer.this, SyncService.class));						
							finish();
						}
					}
				}				
			}
		});
	}
	

    @Override
    protected void onResume() {
		super.onResume();
		if(m_isloaded){
			fillForm();
		}
    }
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("id", m_id);
    	outState.putBoolean("load", m_isloaded);
    	if(m_isloaded) {
    		saveForm();
    		outState.putSerializable("accounts", m_accounts);
    		outState.putInt("selected", m_select_account);
    		outState.putDouble("value", m_value);
    	}
    }
    
	private void fillData() {
		if(!m_taskManager.isWorking())
			m_taskManager.setupTask(new TransferLoad(getHelper(), this, m_id));
	}
	
	private void fillForm() {
	    DecimalFormat df=new DecimalFormat("0.00");
	    DecimalFormatSymbols dfs=new DecimalFormatSymbols();
	    dfs.setDecimalSeparator('.');
	    df.setDecimalFormatSymbols(dfs);
				
        ((EditText)findViewById(R.id.transfer_ed_value)).setText(df.format(m_value));
        
        LookupInit.Init(this, 
        		(Spinner)findViewById(R.id.transfer_spn_accounts), m_accounts, 
        		m_select_account, 
        		new LookupItemSelectedListener.OnChangeListener() {
					
					@Override
					public void onChange(ILookupEntity lookup) {
						if(lookup!=null)
							m_select_account=lookup.getId();						
					}
				});
		
        ((Button)findViewById(R.id.transfer_btn_ok)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveForm();
				if(m_value==0D) {
					Toast.makeText(aTransfer.this, R.string.transfer_value_is_0, Toast.LENGTH_LONG);
				} else if(m_select_account==-1) {
					Toast.makeText(aTransfer.this, R.string.transfer_account_not_selected, Toast.LENGTH_LONG);
				} else {
					if(!m_taskManager.isWorking())
						m_taskManager.setupTask(new TransferSave(getHelper(), aTransfer.this, m_id, m_select_account, m_value));
				}
			}
		});
	}
	
	private void saveForm() {
		try {
			m_value=Double.parseDouble(((EditText)findViewById(R.id.transfer_ed_value)).getText().toString());
		} catch(Exception e) {
			DefaultExceptionHandler.reportException(e);
			m_value=0D;
		}
	}
	
	protected class TransferLoad extends GenericTask<ILookupEntity[]> {
		private final Integer m_id;
		
		protected TransferLoad(DatabaseHelper dbhelper, Context context, Integer id) {
			super(dbhelper, context);
			m_id=id;
		}

		@Override
		protected ILookupEntity[] doInBackground(Void... arg0) {
			publishProgress(R.string.task_load);
			try {
				return AccountActions.getTransferAccountList(m_dbhelper, m_id);
			} catch(Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				DefaultExceptionHandler.reportException(e);				
				return null;
			}
		}
		
	}
	
	protected class TransferSave extends GenericTask<ILookupEntity[]> {
		private final Integer m_from;
		private final Integer m_to;
		private final Double m_value;
		private final Date m_date;

		protected TransferSave(DatabaseHelper dbhelper, Context context, Integer idFrom, Integer idTo, Double value) {
			super(dbhelper, context);
			m_from=idFrom;
			m_to=idTo;
			m_value=value;
			m_date=new Date();
		}

		@Override
		protected ILookupEntity[] doInBackground(Void... params) {
			publishProgress(R.string.task_save);
			try {
				AccountActions.createTransfer(m_dbhelper, m_context, m_from, m_to, m_value, m_date);
				return AccountActions.getTransferAccountList(m_dbhelper, m_id);
			} catch(Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				DefaultExceptionHandler.reportException(e);				
				return null;
			}
		}
	}	
}
