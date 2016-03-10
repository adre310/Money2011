package iae.home.money2011.v2;

import java.sql.SQLException;

import com.nullwire.trace.DefaultExceptionHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import iae.home.money2011.v2.R;
import iae.home.money2011.v2.bll.CategoryAction;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.style.StyleUtility;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;

public class aCategoryList extends GenericActivity<CategoryEntity[]> {
	private ListView m_lvCategoryList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

    	startAdView((LinearLayout)findViewById(R.id.cat_list_main));
        
        m_lvCategoryList=(ListView)findViewById(R.id.lvCategoryList);
        m_lvCategoryList.setOnCreateContextMenuListener(this);
        m_lvCategoryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View v, int position, long id) {
				
				CategoryEntity category=(CategoryEntity)m_lvCategoryList.getAdapter().getItem(position);

				Intent intent=new Intent(aCategoryList.this,aCategoryEdit.class);
	    		intent.putExtra("id", category.getId());
	    		startActivity(intent);
				
				//editCategory(getResources().getString(R.string.category_title_edit).toString(),category);
			}
		});
           
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, CategoryEntity[]>() {
			
			@Override
			public void onTaskComplete(Task<Void, CategoryEntity[]> task,
					CategoryEntity[] result) {
				if(task.isCancelled()) {
					Toast.makeText(aCategoryList.this, getResources().getString(R.string.task_canceled).toString(), Toast.LENGTH_LONG);
					if(task instanceof CategoryListLoad) {
						finish();
					}
				} else {
					GenericTask<CategoryEntity[]> gtask=(GenericTask<CategoryEntity[]>)task;
					if(!gtask.Successful()) {
						Toast.makeText(aCategoryList.this, gtask.getErrorMessage(), Toast.LENGTH_LONG).show();
					}
					
					if(!(task instanceof CategoryListLoad)) {
						startService(new Intent(aCategoryList.this, SyncService.class));
					}
					
					if(result != null)
						m_lvCategoryList.setAdapter(new CategoryListAdapter(aCategoryList.this, result));
				}
			}
		});
	}	
	
    @Override
    protected void onResume() {
		super.onResume();
		
		fillData();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.category_list_menu, menu);
        
       return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
    	case R.id.mn_cat_list_add:
/*    		
    		CategoryEntity cat=new CategoryEntity(-1);
    		cat.setDefault(false);
    		cat.setDeleted(false);
    		editCategory(getResources().getString(R.string.category_title_add).toString(), cat);
*/
    		Intent intent=new Intent(this,aCategoryEdit.class);
    		intent.putExtra("id", -1);
    		startActivity(intent);
    		return true;
    	}
    	return true;
    }    
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.category_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		CategoryEntity category=(CategoryEntity)m_lvCategoryList.getAdapter().getItem(info.position);
		
		switch (item.getItemId()) {
		case R.id.mn_cat_list_cntx_default:
			if(!m_taskManager.isWorking())
				m_taskManager.setupTask(new CategoryDefault(category, getHelper(), this));
			return true;
		case R.id.mn_cat_list_cntx_delete:
			if(!m_taskManager.isWorking()) {
				category.setDeleted(true);
				m_taskManager.setupTask(new CategorySave(category, getHelper(), this));
			}
			break;
		}
		return super.onContextItemSelected(item);
	}    

/*	
    private void editCategory(String title, CategoryEntity category) {
		AlertDialog.Builder builder;
		AlertDialog dialog;
		
		final EditText edit=new EditText(aCategoryList.this);
		edit.setText(category.getName());
		edit.setTag(category);

		builder=new AlertDialog.Builder(aCategoryList.this);
		builder.setView(edit);
		builder.setTitle(title);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CategoryEntity cat=(CategoryEntity)edit.getTag();
				String catName=edit.getText().toString().trim();
				if(catName.equals("")) {
					Toast.makeText(aCategoryList.this, getResources().getString(R.string.category_name_is_not_empty).toString(), Toast.LENGTH_LONG).show();
				} else {
					cat.setName(catName);
					cat.setDeleted(false);
					if(!m_taskManager.isWorking())
						m_taskManager.setupTask(new CategorySave(cat, getHelper(),aCategoryList.this));
				}
			}
		});
		
		dialog=builder.create();
		dialog.show();
    }
*/
	
    private void fillData() {
    	if(!m_taskManager.isWorking())
    		m_taskManager.setupTask(new CategoryListLoad(getHelper(),this));
    }
    
	protected class CategoryListAdapter extends ArrayAdapter<CategoryEntity> {
		private final Activity m_context;
		
		public CategoryListAdapter(Activity context, CategoryEntity[] objects) {
			super(context, android.R.layout.simple_list_item_1, objects);
			m_context=context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CheckedTextView rowView=(CheckedTextView)convertView;
			if(rowView==null) {
				LayoutInflater inflater = m_context.getLayoutInflater();
				rowView=(CheckedTextView)inflater.inflate(android.R.layout.simple_list_item_checked, null, true);
			}
			
			CategoryEntity it=getItem(position);
			rowView.setText(it.getName());
			rowView.setChecked(it.getDefault());
			StyleUtility.applyStyle(it.getThemeId(), rowView);
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
			CategoryEntity it=getItem(position);
			return StyleUtility.getItemViewType(it.getThemeId());
    	}    	
	}

	protected class CategoryDefault extends GenericTask<CategoryEntity[]> {
		private final CategoryEntity m_entity;

		protected CategoryDefault(CategoryEntity category, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper, context);
			m_entity=category;
		}

		@Override
		protected CategoryEntity[] doInBackground(Void... params) {
			publishProgress(R.string.task_save);
			try {
				CategoryAction.setDefaultCategory(m_dbhelper, m_entity.getId());
								
				return CategoryAction.getCategoryList(m_dbhelper);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(), "Не смогли сохранить категорию", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
	}
	
	protected class CategorySave extends GenericTask<CategoryEntity[]> {
		private final CategoryEntity m_entity;
		
		protected CategorySave(CategoryEntity category, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_entity=category;
		}

		@Override
		protected CategoryEntity[] doInBackground(Void... arg0) {
			publishProgress(R.string.task_save);
			try {
				if(m_entity.getDeleted() || CategoryAction.ValidateCategory(m_dbhelper, m_entity))
					CategoryAction.updateCategoryInTransaction(m_dbhelper, m_entity);
				else
					setErrorMessage(R.string.category_name_not_unique);
				return CategoryAction.getCategoryList(m_dbhelper);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(), "Не смогли сохранить категорию", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}		
	}

	protected class CategoryListLoad extends GenericTask<CategoryEntity[]> {

		protected CategoryListLoad(DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
		}

		@Override
		protected CategoryEntity[] doInBackground(Void... arg0) {
			publishProgress(R.string.task_load);
			try {				
				return CategoryAction.getCategoryList(m_dbhelper);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(), "Не смогли получить список категорий", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
		
	}
}
