package iae.home.money2011.v2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;

import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;

public class aMerge extends GenericActivity<ILookupEntity[]> {

	private Boolean m_isloaded=false;
	private Integer m_id=-1;
	private ILookupEntity[] m_accounts;
	private Integer m_select_account=-1;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merge_account);

        startAdView((LinearLayout)findViewById(R.id.merge_main));
        
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
					Toast.makeText(aMerge.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					if(task instanceof MergeLoad) {
						finish();
					}
				} else {
					if(task instanceof MergeLoad) {
						MergeLoad tckLoad=(MergeLoad)task;
						m_accounts=result;
						m_isloaded=true;
						if(!tckLoad.Successful()) {
							Toast.makeText(aMerge.this, tckLoad.getErrorMessage(), Toast.LENGTH_LONG).show();
							finish();
						} else {						
							fillForm();
						}
					} else if(task instanceof MergeSave) {
						MergeSave tckSave=(MergeSave)task;
						if(!tckSave.Successful()) {
							Toast.makeText(aMerge.this, tckSave.getErrorMessage(), Toast.LENGTH_LONG).show();
						} else {
							startService(new Intent(aMerge.this, SyncService.class));						
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
    	}
    }

	private void fillForm() {
        LookupInit.Init(this, 
        		(Spinner)findViewById(R.id.merge_spn_accounts), m_accounts, 
        		m_select_account, 
        		new LookupItemSelectedListener.OnChangeListener() {
					
					@Override
					public void onChange(ILookupEntity lookup) {
						if(lookup!=null)
							m_select_account=lookup.getId();						
					}
				});
		
        ((Button)findViewById(R.id.merge_btn_save)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveForm();
				if(m_select_account==-1) {
					Toast.makeText(aMerge.this, R.string.transfer_account_not_selected, Toast.LENGTH_LONG);
				} else {
					if(!m_taskManager.isWorking())
						m_taskManager.setupTask(new MergeSave(getHelper(), aMerge.this, m_select_account, m_id));
				}
			}
		});
	}
	
	private void saveForm() {
	}
	private void fillData() {
		if(!m_taskManager.isWorking())
			m_taskManager.setupTask(new MergeLoad(getHelper(), this, m_id));
	}

	
	protected class MergeLoad extends GenericTask<ILookupEntity[]> {
		private final Integer m_id;
		
		protected MergeLoad(DatabaseHelper dbhelper, Context context, Integer id) {
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
	
	protected class MergeSave extends GenericTask<ILookupEntity[]> {
		private final Integer m_from;
		private final Integer m_to;

		protected MergeSave(DatabaseHelper dbhelper, Context context, Integer idFrom, Integer idTo) {
			super(dbhelper, context);
			m_from=idFrom;
			m_to=idTo;
		}

		@Override
		protected ILookupEntity[] doInBackground(Void... arg0) {
			try {
				AccountActions.mergeAccount(m_dbhelper, m_from, m_to);
				return AccountActions.getTransferAccountList(m_dbhelper, m_id);
			} catch(Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				DefaultExceptionHandler.reportException(e);				
				return null;
			}
		}
		
	}

}
