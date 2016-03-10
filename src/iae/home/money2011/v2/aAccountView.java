package iae.home.money2011.v2;

import java.sql.SQLException;
import java.util.List;

import com.nullwire.trace.DefaultExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
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
import iae.home.money2011.v2.bll.PayAction;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;
import iae.home.money2011.v2.style.StyleUtility;
import iae.home.money2011.v2.sync.SyncService;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.utils.text.textUtils;

public class aAccountView extends GenericActivity<AccountEntity> {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private Integer m_id=-1;
	private AccountEntity m_entity=null;
	//private Bundle m_savestate;
	
	private ListView m_lvPayList;
	private TextView m_tvName;
	private TextView m_tvNotes;
	private TextView m_tvBalance;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_view);
    	startAdView((LinearLayout)findViewById(R.id.bud_view_main));
//        m_savestate=savedInstanceState;
        if(savedInstanceState==null) {
            Bundle extras = getIntent().getExtras();
    		if (extras != null) {
    			m_id = extras.getInt("id");
    		} else {
    			m_id=-1;
    		}
        } else {
        	m_id=savedInstanceState.getInt("id");
        }

        m_lvPayList=(ListView)findViewById(R.id.lvPayList);
        m_lvPayList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View v, int position, long id) {
				PayEntity it=(PayEntity)m_lvPayList.getAdapter().getItem(position);
				Intent intent=new Intent(aAccountView.this,aPayEdit.class);
				intent.putExtra("id", new PayConnect(it.getId(), m_entity.getId()));
				startActivityForResult(intent, ACTIVITY_EDIT);				
			}
		});
        
        m_lvPayList.setOnCreateContextMenuListener(this);
        
        m_tvName=(TextView)findViewById(R.id.tvBudgetName);
        m_tvName.setText("");
        m_tvNotes=(TextView)findViewById(R.id.tvBudgetDescription);
        m_tvNotes.setText("");
        m_tvBalance=(TextView)findViewById(R.id.tvBudgetBalance);
        m_tvBalance.setText("");
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, AccountEntity>() {
			
			@Override
			public void onTaskComplete(Task<Void, AccountEntity> task,
					AccountEntity result) {
				if(task.isCancelled() || result==null) {
					Toast.makeText(aAccountView.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					finish();
				} else {
					if(task instanceof PayDelete) {
				        startService(new Intent(aAccountView.this, SyncService.class));
					}
					m_entity=result;
					m_lvPayList.setAdapter(new PayListAdapter(aAccountView.this, m_entity.getPaylist()));
					m_tvName.setText(m_entity.getName());
					m_tvNotes.setText(m_entity.getDescription());
					m_tvBalance.setText(textUtils.CurrencyToString(m_entity.getBalance(), m_entity.getCurrency()));
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
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("id", m_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.budget_view_menu, menu);
        
       return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent menuIntent;

    	switch (item.getItemId()) {
    	case R.id.mn_bud_view_addpay:
    		menuIntent=new Intent(this,aPayEdit.class);
    		menuIntent.putExtra("id", new PayConnect(m_entity.getId()));
    		startActivityForResult(menuIntent, ACTIVITY_CREATE);
    		return true;
    	case R.id.mn_bud_view_editbud:
    		menuIntent=new Intent(this,aAccountEdit.class);
    		menuIntent.putExtra("id", m_entity.getId());
    		startActivityForResult(menuIntent, ACTIVITY_EDIT);
    		return true;
    	case R.id.mn_bud_view_transfer:
    		menuIntent=new Intent(this,aTransfer.class);
    		menuIntent.putExtra("id", m_entity.getId());
    		startActivityForResult(menuIntent, ACTIVITY_EDIT);
    		return true;
    	case R.id.mn_bud_view_merge:
    		menuIntent=new Intent(this,aMerge.class);
    		menuIntent.putExtra("id", m_entity.getId());
    		startActivityForResult(menuIntent, ACTIVITY_EDIT);
    		return true;
    	case R.id.mn_bud_view_report_all_category:
    		menuIntent=new Intent(this, aGraphicsPie.class);
    		menuIntent.putExtra("id", m_entity.getId());
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
    		break;
    	case R.id.mn_bud_view_report_monthly:
    		menuIntent=new Intent(this, aGraphicsBar.class);
    		menuIntent.putExtra("id", m_entity.getId());
			startActivityForResult(menuIntent, ACTIVITY_EDIT);				
    		break;
    	}
    	return true;
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.budget_view_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent menuIntent;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		PayEntity it=(PayEntity)m_lvPayList.getAdapter().getItem(info.position);
		
		switch (item.getItemId()) {
			case R.id.mn_bud_view_editpay:
				menuIntent=new Intent(aAccountView.this,aPayEdit.class);
				menuIntent.putExtra("id", new PayConnect(it.getId(), m_entity.getId()));
				startActivityForResult(menuIntent, ACTIVITY_EDIT);				
				return true;
			case R.id.mn_bud_view_delpay:
				if(!m_taskManager.isWorking())
					m_taskManager.setupTask(new PayDelete(it.getId(), m_entity.getId(), getHelper(),this));
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void fillData() {
		if(!m_taskManager.isWorking())
			m_taskManager.setupTask(new AccountLoad(m_id, getHelper(),this));
	}
	
    protected static class PayListViewHolder {
		protected TextView tvValue;
		protected TextView tvDate;
		protected TextView tvCategory;
	}
	
    protected class PayListAdapter extends ArrayAdapter<PayEntity> {
		private final Activity m_context;
    	
    	public PayListAdapter(Activity context, List<PayEntity> objects) {
			super(context, R.layout.pay_row, objects);
			m_context=context;
		}

    	@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PayListViewHolder holder;
			// Recycle existing view if passed as parameter
			// This will save memory and time on Android
			// This only works if the base layout for all classes are the same
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = m_context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.pay_row, null, true);
				holder=new PayListViewHolder();
				holder.tvValue=(TextView)rowView.findViewById(R.id.payValue);
				holder.tvDate=(TextView)rowView.findViewById(R.id.payDate);
				holder.tvCategory=(TextView)rowView.findViewById(R.id.payCategory);
				rowView.setTag(holder);
			} else {
				holder=(PayListViewHolder)rowView.getTag();
			}
			
			PayEntity it=getItem(position);
			holder.tvValue.setText(textUtils.CurrencyToString(it.getValue(),m_entity.getCurrency()));

			if(it.getDate()!=null) {
				holder.tvDate.setText(textUtils.DateToString(it.getDate()));
			} else {
				holder.tvDate.setText("");
			}
			
			holder.tvCategory.setText(it.getCategoryName());
			StyleUtility.applyStyle(it.getCategoryTheme(), rowView);
			
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
			PayEntity it=getItem(position);
			return StyleUtility.getItemViewType(it.getCategoryTheme());
    	} 
    }

    protected class PayDelete extends GenericTask<AccountEntity>{
    	private final Integer m_payId;
    	private final Integer m_accountId;
    	
		protected PayDelete(Integer payId,Integer accountId, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_payId=payId;
			m_accountId=accountId;
		}

		@Override
		protected AccountEntity doInBackground(Void... paramArrayOfParams) {
			publishProgress(R.string.task_delete);
			try {
				return PayAction.deletePay(m_dbhelper, m_payId, m_accountId);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не удалось получить бюджет", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
    	
    }
    
    protected class AccountLoad extends GenericTask<AccountEntity> {
		private final Integer m_id;
		
		protected AccountLoad(Integer Id, DatabaseHelper dbhelper, Activity context) {
			super(dbhelper,context);
			m_id=Id;
		}

		@Override
		protected AccountEntity doInBackground(Void... paramArrayOfParams) {
			publishProgress(R.string.task_load);
			try {				
				return AccountActions.getAccountByIdWithPayList(m_dbhelper, m_id);
			} catch(SQLException e) {
				Log.e(this.getClass().getName(),"Не удалось получить бюджет", e);
				DefaultExceptionHandler.reportException(e);
				return null;
			}
		}
		
	}
}
