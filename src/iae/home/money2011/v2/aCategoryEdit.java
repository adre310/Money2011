package iae.home.money2011.v2;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.nullwire.trace.DefaultExceptionHandler;

import iae.home.money2011.v2.aCategoryList.CategoryListLoad;
import iae.home.money2011.v2.aPayEdit.PayLoad;
import iae.home.money2011.v2.bll.CategoryAction;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.style.StyleUtility;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;

public class aCategoryEdit extends GenericActivity<CategoryEntity> {
	private Integer m_id;
	private CategoryEntity m_category;
	private Boolean m_load=false;
	
	private Spinner m_spnStyle;
	private EditText m_edtName;
	private Button m_btnSave;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_edit);

    	startAdView((LinearLayout)findViewById(R.id.cat_edit_main));
        
        m_spnStyle=(Spinner)findViewById(R.id.cat_edit_spn_style);
        m_edtName=(EditText)findViewById(R.id.cat_edit_name);
        m_btnSave=(Button)findViewById(R.id.cat_edit_btnSave);
        
        if(savedInstanceState==null) {
            Bundle extras = getIntent().getExtras();
    		if (extras != null) {
    			m_id = extras.getInt("id");
    		} else {
    			m_id=-1;
    		}

    		m_load=false;
    		fillData();
    		
        } else {
        	m_id=savedInstanceState.getInt("id");
        	m_load=savedInstanceState.getBoolean("load");
        	if(m_load) {
        		m_category=(CategoryEntity)savedInstanceState.getSerializable("entity");
        	} else {
        		fillData();
        	}
        }
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, CategoryEntity>() {
			
			@Override
			public void onTaskComplete(Task<Void, CategoryEntity> task,
					CategoryEntity result) {
				if(task.isCancelled()) {
					Toast.makeText(aCategoryEdit.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					if(task instanceof CategoryLoad) {
						finish();
					}
				} else {
					GenericTask<CategoryEntity> gtask=(GenericTask<CategoryEntity>)task;
					if(!gtask.Successful()) {
						Toast.makeText(aCategoryEdit.this, gtask.getErrorMessage(), Toast.LENGTH_LONG).show();
					} else {
					
						if(task instanceof CategorySave) {
							startService(new Intent(aCategoryEdit.this, SyncService.class));
							finish();
						}
					}
					m_load=true;
					m_category=result;
					fillForm();
				}				
			}
		});
	}

    @Override
    protected void onResume() {
		super.onResume();
		if(m_load){
			fillForm();
		}
    }
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("id", m_id);
    	outState.putBoolean("load", m_load);
    	if(m_load) {
    		saveForm();
    		outState.putSerializable("entity", m_category);
    	}
    }	
	
    private void fillForm() {
    	m_edtName.setText(m_category.getName().trim());
    	m_spnStyle.setAdapter(new StyleArrayAdapter(this, StyleUtility.getAvaliableList()));
    	SpinnerAdapter spnStyleAdapter=m_spnStyle.getAdapter();
    	
    	int select=0;
    	int count=spnStyleAdapter.getCount();
    	
    	for(int indx=0;indx<count;indx++) {
    		if(((Integer)spnStyleAdapter.getItem(indx)).equals(m_category.getThemeId())) {
    			select=indx;
    			break;
    		}
    	}
    	
    	m_spnStyle.setSelection(select);
    	m_spnStyle.setOnItemSelectedListener(m_styleListener);
    	
    	m_btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveForm();
				m_category.setModified(true);
				if(m_category.getName().trim().equals("")) {
					Toast.makeText(aCategoryEdit.this, getResources().getString(R.string.category_name_is_not_empty).toString(), Toast.LENGTH_LONG).show();
				} else {
					if(!m_taskManager.isWorking())
						m_taskManager.setupTask(new CategorySave(m_category, getHelper(), aCategoryEdit.this));
				}
			}
		});
    }
    
    private void saveForm() {
    	m_category.setName(m_edtName.getText().toString().trim());
    }
    
	private void fillData() {
		if(!m_taskManager.isWorking())
			m_taskManager.setupTask(new CategoryLoad(getHelper(), this, m_id));
	}

	
	private OnItemSelectedListener m_styleListener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			m_category.setThemeId((Integer)parent.getAdapter().getItem(position));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	protected class StyleArrayAdapter extends ArrayAdapter<Integer> {
		private final Activity m_context;
		
		public StyleArrayAdapter(Activity context, List<Integer> objects) {
			super(context, android.R.layout.simple_spinner_item, objects);
			m_context=context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			TextView rowView=(TextView)convertView;
			if(rowView==null) {
				LayoutInflater inflater=m_context.getLayoutInflater();
				rowView=(TextView)inflater.inflate(android.R.layout.simple_spinner_item, null, true);
			}
			
			Integer styleId=getItem(position);
			rowView.setText("Style "+styleId);
			StyleUtility.applyStyle(styleId, rowView);
			return rowView;
		}
		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent){
			CheckedTextView rowView=(CheckedTextView)convertView;
			if(rowView==null) {
				LayoutInflater inflater=m_context.getLayoutInflater();
				rowView=(CheckedTextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null, true);
			}
			
			Integer styleId=getItem(position);
			rowView.setText("Style "+styleId);
			StyleUtility.applyStyle(styleId, rowView);
			return rowView;
		}
		
    	@Override 
    	public int getViewTypeCount() 
    	{     
    		return StyleUtility.getViewTypeCount(); 
    	}  

    	@Override 
    	public int getItemViewType(int position) 
    	{
    		return position;
    	}    	
	}
	
	protected class CategoryLoad extends GenericTask<CategoryEntity> {
		private final Integer m_id;
		protected CategoryLoad(DatabaseHelper dbhelper, Context context, Integer id) {
			super(dbhelper, context);
			m_id=id;
		}

		@Override
		protected CategoryEntity doInBackground(Void... arg0) {
			publishProgress(R.string.task_load);
			try {
				if(m_id<0) {
					return new CategoryEntity(-1);
				} else
					return m_dbhelper.getCategoriesDao().queryForId(m_id);
			} catch(Exception ex) {
				return null;
			}
		}
	}
	
	protected class CategorySave extends GenericTask<CategoryEntity> {
		private final CategoryEntity m_entity;
		
		protected CategorySave(CategoryEntity category, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_entity=category;
		}

		@Override
		protected CategoryEntity doInBackground(Void... arg0) {
			publishProgress(R.string.task_save);
			try {
				if(m_entity.getDeleted() || CategoryAction.ValidateCategory(m_dbhelper, m_entity))
					CategoryAction.updateCategoryInTransaction(m_dbhelper, m_entity);
				else
					setErrorMessage(R.string.category_name_not_unique);
				return m_entity;
			} catch(SQLException e) {
				Log.e(this.getClass().getName(), "Не смогли сохранить категорию", e);
				DefaultExceptionHandler.reportException(e);
				return m_entity;
			}
		}		
	}
	
}
