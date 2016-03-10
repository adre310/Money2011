package iae.home.money2011.v2;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

import com.nullwire.trace.DefaultExceptionHandler;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import iae.home.money2011.v2.R;
import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.utils.text.CurrencyCode;
import iae.home.utils.text.textUtils;

public class aAccountCreate extends GenericActivity<Void> {
    static final int DATE_DIALOG_ID = 1;
	
	private AccountCreateItem m_entity=null;

	private EditText m_eName;
	private EditText m_eBalance;
	private EditText m_eNotes;
	private Spinner m_eCurrency;
	private Button m_eDate;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_create);
    	startAdView((LinearLayout)findViewById(R.id.bud_cre_main));
        
        if(savedInstanceState!=null) {
        	m_entity=(AccountCreateItem)savedInstanceState.getSerializable("item");
        } else {
        	m_entity= new AccountCreateItem();
        }
        
        m_eName=(EditText)findViewById(R.id.bud_cre_name);
        
        m_eBalance=(EditText)findViewById(R.id.bud_cre_balance);
        
        m_eNotes=(EditText)findViewById(R.id.bud_cre_description);
        
        m_eCurrency=(Spinner)findViewById(R.id.bud_cre_currency);
        m_eCurrency.setAdapter(new CurrencyArrayAdapter(this));
        m_eCurrency.setOnItemSelectedListener(new CurrencySelectedListener());
        
        m_eDate=(Button)findViewById(R.id.bud_cre_date);
        m_eDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				showDialog(DATE_DIALOG_ID);
			}
		});
        
        Button btnSave=(Button)findViewById(R.id.bud_cre_save);
        btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveForm();
				if(m_entity.getName().trim().equals("")) {
					Toast.makeText(aAccountCreate.this, getResources().getString(R.string.budhet_name_is_not_empty).toString(), Toast.LENGTH_LONG).show();
				} else {
					if(!m_taskManager.isWorking())
						m_taskManager.setupTask(new BudgetCreate(m_entity, getHelper(), aAccountCreate.this));
				}
			}
		});
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, Void>() {
			
			@Override
			public void onTaskComplete(Task<Void, Void> task, Void result) {
				if(task.isCancelled()) {
					Toast.makeText(aAccountCreate.this, getResources().getString(R.string.task_canceled).toString(), Toast.LENGTH_LONG);
				} else {
					BudgetCreate tskCreate=(BudgetCreate)task;
					if(tskCreate.Successful()) {
						startService(new Intent(aAccountCreate.this, SyncService.class));
						
						finish();
					} else
						Toast.makeText(aAccountCreate.this, tskCreate.getErrorMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

    @Override
    protected void onResume() {
		super.onResume();
		fillForm();
    }
    
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	saveForm();
    	outState.putSerializable("item", m_entity);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
            	if(m_entity==null || m_entity.getDate()==null) {
                	Log.w(this.getClass().getName(),"onCreateDialog id: "+id+", m_entity==null || m_entity.getDate()==null");
                	return null;
            	}
            	
            	Calendar cal=Calendar.getInstance();
            	cal.setTime(m_entity.getDate());
            	
                return new DatePickerDialog(this,
                            m_DateSetListener,
                            cal.get(Calendar.YEAR), 
                            cal.get(Calendar.MONTH), 
                            cal.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }
    
    private DatePickerDialog.OnDateSetListener m_DateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
            	Calendar cal=Calendar.getInstance();
            	cal.set(year,monthOfYear,dayOfMonth);
            	m_entity.setDate(cal.getTime());
            	
                m_eDate.setText(textUtils.DateToString(m_entity.getDate()));
            }
        };
    
    private void saveForm() {
	    //DecimalFormat df=new DecimalFormat("0.00");
	    //DecimalFormatSymbols dfs=new DecimalFormatSymbols();
	    //dfs.setDecimalSeparator('.');
	    //df.setDecimalFormatSymbols(dfs);
	    
	    try {
	    	m_entity.setName(m_eName.getText().toString().trim());
	    	m_entity.setDescription(m_eNotes.getText().toString());
    		m_entity.setBalance(Double.parseDouble(m_eBalance.getText().toString()));
	    } catch(Exception e) {
	    	Log.e(this.getClass().getName(), "ќщибки при сохранении формы", e);
			DefaultExceptionHandler.reportException(e);
	    }
  	}
    
    private void fillForm() {
	    DecimalFormat df=new DecimalFormat("0.00");
	    DecimalFormatSymbols dfs=new DecimalFormatSymbols();
	    dfs.setDecimalSeparator('.');
	    df.setDecimalFormatSymbols(dfs);

	    m_eName.setText(m_entity.getName());
	    m_eNotes.setText(m_entity.getDescription());
    	m_eBalance.setText(df.format(m_entity.getBalance()));
    	m_eDate.setText(textUtils.DateToString(m_entity.getDate()));
    	
    	String curCode=m_entity.getCurrency();
    	
    	for(int p=0;p<m_eCurrency.getCount();p++) {
    		CurrencyCode code=(CurrencyCode)m_eCurrency.getAdapter().getItem(p);
    		if(code.getCode().equals(curCode)) {
    			m_eCurrency.setSelection(p);
    			break;
    		}
    	}
    }
    

    protected class CurrencySelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			m_entity.setCurrency(((CurrencyCode)aAccountCreate.this.m_eCurrency.getAdapter().getItem(position)).getCode());
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
    	
    }
    
    protected class BudgetCreate extends GenericTask<Void> {
    	private final AccountCreateItem m_entity;
    	
		protected BudgetCreate(AccountCreateItem Item, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_entity=Item;
		}

		@Override
		protected Void doInBackground(Void... paramArrayOfParams) {
			publishProgress(R.string.task_save);
			try {
					AccountActions.createAccount(
						m_dbhelper, 
						m_entity.getName(), 
						m_entity.getCurrency(),
						m_entity.getDescription(),
						m_entity.getDate(), 
						m_entity.getBalance());
			     
				return null;
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"ќшибка при создании бюджета",e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
    	
    }
}
