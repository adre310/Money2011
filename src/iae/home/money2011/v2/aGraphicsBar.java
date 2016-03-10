package iae.home.money2011.v2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.charts.ChartUtility;
import iae.home.money2011.v2.charts.GraphicsBarFilter;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.LookupEntity;
import iae.home.money2011.v2.datamodel.PayEntity;
import iae.home.money2011.view.Panel;
import iae.home.money2011.view.Panel.OnPanelListener;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;

import android.content.Context;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class aGraphicsBar extends GenericActivity<Void> {
	
	private GraphicsBarFilter m_filter=new GraphicsBarFilter();
	private String[] m_currencyList;
	private AccountEntity[] m_accountList;
	private XYMultipleSeriesDataset m_dataset;
	private Boolean m_loaded=false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphics_bar);
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, Void>() {
			
			@Override
			public void onTaskComplete(Task<Void, Void> task, Void result) {
				if(task.isCancelled()) {
					Toast.makeText(aGraphicsBar.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					finish();
				} else {
					GraphLoad tckLoad=(GraphLoad)task;
					m_loaded=true;
					m_currencyList=tckLoad.getCurrencyCodeList();
					m_accountList=tckLoad.getAccountList();
					m_dataset=tckLoad.getSeriesDataset();
					fillGraph();
				}
				
			}
		});
        
        if(savedInstanceState==null) {
            Bundle extras = getIntent().getExtras();
            if(extras!=null) {
            	Integer id=extras.getInt("id", -1);
            	if( id >= 0) {
            		m_filter.setCurrency("");
            		m_filter.setAccountId(id);
            	} else {
            		m_filter.setAccountId(-1);
            	}
            } else {
        		m_filter.setAccountId(-1);            	
            }
        	
        	m_loaded=false;
        	loadData();
        } else {
        	m_filter=(GraphicsBarFilter)savedInstanceState.getSerializable("filter");
        	m_loaded=savedInstanceState.getBoolean("load");
        	if(m_loaded) {
        		m_accountList=(AccountEntity[])savedInstanceState.getSerializable("account");
        		m_currencyList=(String[])savedInstanceState.getSerializable("currency");
        		m_dataset=(XYMultipleSeriesDataset)savedInstanceState.getSerializable("dataset");
        		fillGraph();
        	} else {
        		loadData();
        	}
        }
        
        Panel panelFilter=(Panel)findViewById(R.id.grph_bar_filter);
        panelFilter.setOnPanelListener(m_panelListener);
	}

    @Override
    protected void onResume() {
		super.onResume();
		
		if(m_loaded)
			fillGraph();
    }
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putSerializable("filter", m_filter);
    	outState.putBoolean("load", m_loaded);
    	if(m_loaded) {
    		outState.putSerializable("account", m_accountList);
    		outState.putSerializable("currency", m_currencyList);
    		outState.putSerializable("dataset", m_dataset);
    	}
    }
    
    private void loadData() {
    	if(!m_taskManager.isWorking())
    		m_taskManager.setupTask(new GraphLoad(getHelper(), this, m_filter));
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
    	
    	Spinner spnCurrency=(Spinner)findViewById(R.id.grph_bar_spn_currency);
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
    	
    	Spinner spnAccounts=(Spinner)findViewById(R.id.grph_bar_spn_accounts);
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
    	
    	Spinner spnDeposit=(Spinner)findViewById(R.id.grph_bar_spn_deposit);
        ArrayAdapter<String> spnDepositAdapter=new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		new String[] {
        			getResources().getString(R.string.withdrawal).toString(),
        			getResources().getString(R.string.deposit).toString()
        			} );
        spnDepositAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        spnDeposit.setAdapter(spnDepositAdapter);
        spnDeposit.setSelection(m_filter.getMode());
        
        spnCurrency.setOnItemSelectedListener(m_currencyListener);
        spnAccounts.setOnItemSelectedListener(m_accountListener);
        spnDeposit.setOnItemSelectedListener(m_depositListner);
    }
        
	private void fillGraph() {

		XYMultipleSeriesRenderer renderer = ChartUtility.buildBarRenderer(m_dataset.getSeriesCount());
	    renderer.setXLabelsAngle(35);
        renderer.setXLabels(1);
		for(int m=0;m<12;m++)
			renderer.addXTextLabel(m+1, DateUtils.getMonthString(m, DateUtils.LENGTH_MEDIUM));
			
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	    // renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    
	    GraphicalView View=new GraphicalView(this, new BarChart(m_dataset,renderer,Type.STACKED));
		LinearLayout parentView=(LinearLayout)findViewById(R.id.grph_bar_draw);
		parentView.removeAllViews();
		parentView.addView(View);
	}
	
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
	
	private OnItemSelectedListener m_currencyListener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			String selCur=(String)parent.getAdapter().getItem(position);
			if(!m_filter.getCurrency().equals(selCur)) {
				m_filter.setCurrency(selCur);
				m_filter.setAccountId(-1);

				Spinner spnAccounts=(Spinner)findViewById(R.id.grph_bar_spn_accounts);
				spnAccounts.setAdapter(new LookupArrayAdapter(aGraphicsBar.this, getAccountList()));

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
	
	private OnItemSelectedListener m_depositListner=new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			m_filter.setMode(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	}; 
	
	private OnPanelListener m_panelListener=new OnPanelListener() {
		
		@Override
		public void onPanelOpened(Panel panel) {
			Log.i(this.getClass().getName(),"panel open");
			fillFilter();			
		}
		
		@Override
		public void onPanelClosed(Panel panel) {
			Log.i(this.getClass().getName(),"panel close");
			loadData();
		}
	}; 
	
	protected class GraphLoad extends GenericTask<Void> {
		private final GraphicsBarFilter m_filter;
		private XYMultipleSeriesDataset m_series;
		private String[] m_currency_list;
		private AccountEntity[] m_account_list;
		
		protected GraphLoad(DatabaseHelper dbhelper, Context context, GraphicsBarFilter filter) {
			super(dbhelper, context);
			m_filter=filter;
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
				
				Dao<PayEntity,Integer> daoPay=m_dbhelper.getPaysDao();
				Where<PayEntity, Integer> wherePay=daoPay.queryBuilder()
						.where()
							.eq(PayEntity.DELETED, false)
						.and()
							.eq(PayEntity.IS_SYSTEM, false);
				
				if(m_filter.getMode()==GraphicsBarFilter.DEPOSIT)
					wherePay=wherePay.and().gt(PayEntity.VALUE, 0D);
				else
					wherePay=wherePay.and().lt(PayEntity.VALUE, 0D);
				
				if(m_filter.getAccountId() >=0)
					wherePay=wherePay.and().eq(PayEntity.ACCOUNT, m_filter.getAccountId());
				else {
					HashSet<Integer> inAccList=new HashSet<Integer>();
					for (AccountEntity a : m_account_list) {
						if(a.getCurrency().equals(m_filter.getCurrency())) {
							inAccList.add(a.getId());
						}
					}
					wherePay=wherePay.and().in(PayEntity.ACCOUNT, inAccList);
				}
				
				List<PayEntity> listPay=wherePay.query();
				
				HashMap<Integer, double[]> mapChart=new HashMap<Integer, double[]>();

				for(PayEntity it : listPay) {
					Integer year=it.getDate().getYear()+1900;
					Integer month=it.getDate().getMonth();
					Double value=(it.getValue()<0)?(-it.getValue()):(it.getValue());
					if(mapChart.containsKey(year)) {
						mapChart.get(year)[month]+=value;
					} else {
						double[] monthArr=new double[12];
						monthArr[month]=value;
						mapChart.put(year, monthArr);
					}
				}

			    m_series = new XYMultipleSeriesDataset();
			    for (Integer key :  mapChart.keySet()) {
			        CategorySeries series = new CategorySeries(String.valueOf(key));
			        double[] v = mapChart.get(key);
			        int seriesLength = v.length;
			        for (int k = 0; k < seriesLength; k++) {
			          series.add(v[k]);
			        }
			        m_series.addSeries(series.toXYSeries());
			      }
				
			} catch(Exception e) {
				Log.e(this.getClass().getName(),"Load",e);
			}
			// TODO Auto-generated method stub
			return null;
		}

		public XYMultipleSeriesDataset getSeriesDataset() {
			return m_series;
		}
		
		public String[] getCurrencyCodeList() {
			return m_currency_list;
		}
		
		public AccountEntity[] getAccountList() {
			return m_account_list;
		}
	}
}
