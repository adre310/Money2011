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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import iae.home.money2011.v2.R;
import iae.home.money2011.v2.bll.CategoryAction;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.bll.PayAction;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.PayEntity;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.utils.text.textUtils;

public class aPayEdit extends GenericActivity<PayEntity>{
    static final int DATE_DIALOG_ID = 1;

	private Boolean m_isloaded=false;
	private PayConnect m_con=null;
	private PayEntity m_entity=null;
	private ILookupEntity[] m_categorylist=null;

	private EditText m_eValue;
	private Spinner m_eDebit;
	private Button m_eDate;
	private Spinner m_eCategory;
	private EditText m_eNotes;
	private CheckBox m_checkSystem;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_edit);

        startAdView((LinearLayout)findViewById(R.id.pay_edit_main));
        
        if(savedInstanceState!=null) {
        	m_con=(PayConnect)savedInstanceState.getSerializable("id");
        	m_isloaded=savedInstanceState.getBoolean("load");
        	if(m_isloaded) {
        		m_entity=(PayEntity)savedInstanceState.getSerializable("entity");
        		m_categorylist=(ILookupEntity[])savedInstanceState.getSerializable("categories");
        	} else {
        		fillData();
        	}
        } else {
            Bundle extras = getIntent().getExtras();
            if(extras==null) {
            	Log.e(this.getClass().getName(),"Ошибка в передаче параметров");
            	finish();
            }
            m_con=(PayConnect)extras.getSerializable("id");
            m_isloaded=false;
        	fillData();
        }

        m_checkSystem=(CheckBox)findViewById(R.id.pay_chkSystem);
        
        m_eNotes=(EditText)findViewById(R.id.pay_edtNotes);
        m_eValue=(EditText)findViewById(R.id.pay_edtValue);
        
        m_eDebit=(Spinner)findViewById(R.id.pay_chkDebit);
        ArrayAdapter<String> adptDept=new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		new String[] {
        			getResources().getString(R.string.withdrawal).toString(),
        			getResources().getString(R.string.deposit).toString()
        			} );
        adptDept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        m_eDebit.setAdapter(adptDept);
        
        m_eDate=(Button)findViewById(R.id.pay_edtDate);
        m_eDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID);
			}
		});
        
        m_eCategory=(Spinner)findViewById(R.id.pay_edtCategory);
        
        Button btnSave=(Button)findViewById(R.id.pay_btnSave);
        btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveForm();
				m_taskManager.setupTask(new PaySave(m_entity, getHelper(),aPayEdit.this));				
			}
		});
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, PayEntity>() {
			
			@Override
			public void onTaskComplete(Task<Void, PayEntity> task, PayEntity result) {
				if(task.isCancelled()) {
					Toast.makeText(aPayEdit.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					if(task instanceof PayLoad) {
						finish();
					}
				} else {
					if(task instanceof PayLoad) {
						PayLoad tckLoad=(PayLoad)task;
						m_entity=result;
						m_categorylist=tckLoad.Categories;
						m_isloaded=true;
						if(!tckLoad.Successful()) {
							Toast.makeText(aPayEdit.this, tckLoad.getErrorMessage(), Toast.LENGTH_LONG).show();
							finish();
						} else {						
							fillForm();
						}
					} else if(task instanceof PaySave) {
						PaySave tckSave=(PaySave)task;
						if(!tckSave.Successful()) { 
							Toast.makeText(aPayEdit.this, tckSave.getErrorMessage(), Toast.LENGTH_LONG).show();
						} else {						
							startService(new Intent(aPayEdit.this, SyncService.class));						
							finish();
						}
					}
				}
			}
		});
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
    	outState.putSerializable("id", m_con);
    	outState.putBoolean("load", m_isloaded);
    	if(m_isloaded) {
    		saveForm();
    		outState.putSerializable("entity", m_entity);
    		outState.putSerializable("categories", m_categorylist);
    	}
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
    
   private void fillData() {
		if(!m_taskManager.isWorking()) {
			m_taskManager.setupTask(new PayLoad(m_con, getHelper(),this));
		}
	}

   private void fillForm() {
	    DecimalFormat df=new DecimalFormat("0.00");
	    DecimalFormatSymbols dfs=new DecimalFormatSymbols();
	    dfs.setDecimalSeparator('.');
	    df.setDecimalFormatSymbols(dfs);
	   
	    m_eNotes.setText(m_entity.getDescription());
	    m_checkSystem.setChecked(m_entity.getSystem());
	    
	    Double val=m_entity.getValue();
	    if(val<0.01 && val>(-0.01)) {
			m_eValue.setText("0.00");
			m_eDebit.setSelection(0);	    	
	    } else
		if(val > 0) {
			m_eValue.setText(df.format(val));
			m_eDebit.setSelection(1);
		} else {
			m_eValue.setText(df.format(-val));
			m_eDebit.setSelection(0);
		}
		
		m_eDate.setText(textUtils.DateToString(m_entity.getDate()));
		
		LookupInit.Init(this, m_eCategory, m_categorylist, 
				(m_entity.getCategory()!=null)?m_entity.getCategory().getId():null, 
				new LookupItemSelectedListener.OnChangeListener() {
					
					@Override
					public void onChange(ILookupEntity lookup) {
						if(lookup==null)
							m_entity.setCategory((CategoryEntity)null);
						else
							m_entity.setCategory(new CategoryEntity(lookup.getId()));				
					}
				});
	}
    
	private void saveForm() {
	    //DecimalFormat df=new DecimalFormat("0.00");
	    //DecimalFormatSymbols dfs=new DecimalFormatSymbols();
	    //dfs.setDecimalSeparator('.');
	    //df.setDecimalFormatSymbols(dfs);

		m_entity.setDescription(m_eNotes.getText().toString());
		m_entity.setSystem(m_checkSystem.isChecked());
		
	    try { 
	    	Double tmpValue=Double.parseDouble(m_eValue.getText().toString()); //(Double)df.parse(m_eValue.getText().toString());
	    	if(m_eDebit.getSelectedItemPosition()==0){
	    		m_entity.setValue(-tmpValue);
	    	} else {
	    		m_entity.setValue(tmpValue);			
	    	}
	    
	    } catch(Exception ex) {
	    	Log.e(this.getClass().getName(), "Ощибки при сохранении формы", ex);
			DefaultExceptionHandler.reportException(ex);
	    }
	}
	
	
	protected class PayLoad extends GenericTask<PayEntity> {
		public ILookupEntity[] Categories=null;

		private final PayConnect m_con;
		protected PayLoad(PayConnect con,DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_con=con;
		}

		@Override
		protected PayEntity doInBackground(Void... params) {
			publishProgress(R.string.task_load);
			try {
				PayEntity pay=PayAction.getPayByIdOrDefault(m_dbhelper, m_con.getPayId(), m_con.getBudgetId());
				Categories=CategoryAction.getLookup(m_dbhelper, aPayEdit.this, false);
				
				return pay;
			} catch(SQLException e) {
				setErrorMessage(m_context.getString(R.string.unknown_error, e.getLocalizedMessage()));
				Log.e(this.getClass().getName(),"Не смогли загрузить платеж", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
	}
	
	protected class PaySave extends GenericTask<PayEntity> {
		private final PayEntity m_pay;
		
		protected PaySave(PayEntity pay, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_pay=pay;
		}

		@Override
		protected PayEntity doInBackground(Void... params) {
			publishProgress(R.string.task_save);
			try {
				return PayAction.updatePayInTransaction(m_dbhelper, m_pay); 
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не смогли сохранить платеж", e);
				DefaultExceptionHandler.reportException(e);
				return m_entity;
			}
		}
	}
}
