package iae.home.money2011.v2;

import java.sql.SQLException;

import com.nullwire.trace.DefaultExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import iae.home.money2011.v2.R;
import iae.home.money2011.v2.bll.BaseAction;
import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.utils.text.CurrencyCode;

public class aAccountEdit extends GenericActivity<AccountEntity> {
	private Integer m_id;
	private AccountEntity m_entity=null;
	private Boolean m_isloaded;
	
	private EditText m_eName;
	private EditText m_eNotes;
	private Spinner m_eCurrency;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_edit);
    	startAdView((LinearLayout)findViewById(R.id.bud_edit_main));
        
        if(savedInstanceState!=null) {
        	m_id=savedInstanceState.getInt("id");
        	m_isloaded=savedInstanceState.getBoolean("load");
        	if(m_isloaded) {
        		m_entity=(AccountEntity)savedInstanceState.getSerializable("entity");
        	} else {
        		fillData();
        	}
        } else {
            Bundle extras = getIntent().getExtras();
            if(extras==null) {
            	Log.e(this.getClass().getName(),"Ошибка в передаче параметров");
            	finish();
            }
            m_id=extras.getInt("id");
            m_isloaded=false;
            fillData();
        }
        
        m_eName=(EditText)findViewById(R.id.bud_edit_name);
        m_eNotes=(EditText)findViewById(R.id.bud_edit_notes);
        
        m_eCurrency=(Spinner)findViewById(R.id.bud_edit_currency);
        m_eCurrency.setAdapter(new CurrencyArrayAdapter(this));
        m_eCurrency.setOnItemSelectedListener(new CurrencySelectedListener());
        
        Button btnSave=(Button)findViewById(R.id.bud_edit_save);
        btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				saveForm();
				if(m_entity.getName().trim().equals("")) {
					Toast.makeText(aAccountEdit.this, getResources().getString(R.string.budhet_name_is_not_empty), Toast.LENGTH_LONG).show();
				} else {
					if(!m_taskManager.isWorking())
						m_taskManager.setupTask(new BudgetSave(m_entity, getHelper(), aAccountEdit.this));
				}
			}
		});
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, AccountEntity>() {
			
			@Override
			public void onTaskComplete(Task<Void, AccountEntity> task,
					AccountEntity result) {
				if(task.isCancelled()) {
					Toast.makeText(aAccountEdit.this, getResources().getString(R.string.task_canceled).toString(), Toast.LENGTH_LONG);
					if(task instanceof BudgetLoad) {
						finish();
					}
				} else {
					if(task instanceof BudgetLoad) {
						m_entity=result;
						m_isloaded=true;
						fillForm();
					} else if(task instanceof BudgetSave) {
						BudgetSave tskSave=(BudgetSave)task;
						if(tskSave.Successful()) {
							startService(new Intent(aAccountEdit.this, SyncService.class));
							
							finish();
						} else
							Toast.makeText(aAccountEdit.this, tskSave.getErrorMessage(), Toast.LENGTH_LONG).show();
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
    		outState.putSerializable("entity", m_entity);
    	}
    }
    
    private void saveForm() {
    	m_entity.setName(m_eName.getText().toString().trim());
    	m_entity.setDescription(m_eNotes.getText().toString());
    }
    
	private void fillForm() {
		m_eName.setText(m_entity.getName());
		m_eNotes.setText(m_entity.getDescription());
		
    	String curCode=m_entity.getCurrency();
    	
    	for(int p=0;p<m_eCurrency.getCount();p++) {
    		CurrencyCode code=(CurrencyCode)m_eCurrency.getAdapter().getItem(p);
    		if(code.getCode().equals(curCode)) {
    			m_eCurrency.setSelection(p);
    			break;
    		}
    	}
	}
	
	private void fillData() {
		if(!m_taskManager.isWorking())
			m_taskManager.setupTask(new BudgetLoad(m_id, getHelper(), this));
	}

    protected class BudgetSave extends GenericTask<AccountEntity> {
    	private final AccountEntity m_entity;
    	
		protected BudgetSave(AccountEntity budget, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper, context);
			m_entity=budget;
		}

		@Override
		protected AccountEntity doInBackground(Void... paramArrayOfParams) {
			publishProgress(R.string.task_save);
			try {
				return BaseAction.updateObjectInTransaction(m_dbhelper.getConnectionSource(), m_dbhelper.getAccountsDao(), m_entity);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не удалось сохранить бюджет", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
    }
    
    protected class BudgetLoad extends GenericTask<AccountEntity> {
		private final Integer m_id;
		
		protected BudgetLoad(Integer Id, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_id=Id;
		}

		@Override
		protected AccountEntity doInBackground(Void... paramArrayOfParams) {
			publishProgress(R.string.task_load);
			try {				
				return AccountActions.getAccountById(m_dbhelper, m_id);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не удалось получить бюджет", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
    }
    
    protected class CurrencySelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			try {
				m_entity.setCurrency(((CurrencyCode)aAccountEdit.this.m_eCurrency.getAdapter().getItem(position)).getCode());
			} catch(Exception e) {
				DefaultExceptionHandler.reportException(e);				
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
    	
    }

}
