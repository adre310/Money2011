package iae.home.money2011.v2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PieChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.j256.ormlite.dao.Dao;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.bll.ReportByCategoryItem;
import iae.home.money2011.v2.charts.ChartUtility;
import iae.home.money2011.v2.charts.GraphicsPieFilter;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.LookupEntity;
import iae.home.money2011.view.Panel;
import iae.home.money2011.view.Panel.OnPanelListener;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.utils.text.textUtils;

public class aGraphicsPie extends GenericActivity<Void> {

	private GraphicsPieFilter m_filter=new GraphicsPieFilter();
	private String[] m_currencyList;
	private AccountEntity[] m_accountList;
	private ReportByCategoryItem[] m_dataset;
	private Boolean m_loaded=false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphics_pie);
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, Void>() {
			
			@Override
			public void onTaskComplete(Task<Void, Void> task, Void result) {
				if(task.isCancelled()) {
					Toast.makeText(aGraphicsPie.this, getResources().getString(R.string.task_canceled), Toast.LENGTH_LONG).show();
					finish();
				} else {
					GraphLoad tckLoad=(GraphLoad)task;
					m_loaded=true;
					m_currencyList=tckLoad.getCurrencyCodeList();
					m_accountList=tckLoad.getAccountList();
					m_dataset=tckLoad.getDataset();
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
        	m_filter=(GraphicsPieFilter)savedInstanceState.getSerializable("filter");
        	m_loaded=savedInstanceState.getBoolean("load");
        	if(m_loaded) {
        		m_accountList=(AccountEntity[])savedInstanceState.getSerializable("account");
        		m_currencyList=(String[])savedInstanceState.getSerializable("currency");
        		m_dataset=(ReportByCategoryItem[])savedInstanceState.getSerializable("dataset");
        		fillGraph();
        	} else {
        		loadData();
        	}
        }
        
        Panel panelFilter=(Panel)findViewById(R.id.grph_pie_filter);
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
    	
    	Spinner spnCurrency=(Spinner)findViewById(R.id.grph_pie_spn_currency);
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
    	
    	Spinner spnAccounts=(Spinner)findViewById(R.id.grph_pie_spn_accounts);
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
    	
    	Spinner spnDeposit=(Spinner)findViewById(R.id.grph_pie_spn_deposit);
        ArrayAdapter<String> spnDepositAdapter=new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		new String[] {
        			getResources().getString(R.string.withdrawal).toString(),
        			getResources().getString(R.string.deposit).toString()
        			} );
        spnDepositAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        spnDeposit.setAdapter(spnDepositAdapter);
        spnDeposit.setSelection(m_filter.getMode());
        
        Spinner spnViewmode=(Spinner)findViewById(R.id.grph_pie_spn_viewmode);
        ArrayAdapter<String> spnViewmodeAdapter=new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		new String[] {
        			getResources().getString(R.string.view_pie).toString(),
        			getResources().getString(R.string.view_bar).toString()
        			} );
        spnViewmodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        spnViewmode.setAdapter(spnViewmodeAdapter);
        spnViewmode.setSelection(m_filter.getViewMode());
        
        spnCurrency.setOnItemSelectedListener(m_currencyListener);
        spnAccounts.setOnItemSelectedListener(m_accountListener);
        spnDeposit.setOnItemSelectedListener(m_depositListner);
        spnViewmode.setOnItemSelectedListener(m_viewmodeListener);
		
        Button btnBeginDate=(Button)findViewById(R.id.grph_pie_begin);
        btnBeginDate.setText(textUtils.DateToString(m_filter.getBeginDate()));
        btnBeginDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	Calendar cal=Calendar.getInstance();
            	cal.setTime(m_filter.getBeginDate());
				DatePickerDialog dialog=new DatePickerDialog(
						aGraphicsPie.this,
						m_beginListener,
						cal.get(Calendar.YEAR), 
                        cal.get(Calendar.MONTH), 
                        cal.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});

        Button btnEndDate=(Button)findViewById(R.id.grph_pie_end);
        btnEndDate.setText(textUtils.DateToString(m_filter.getEndDate()));
        btnEndDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	Calendar cal=Calendar.getInstance();
            	cal.setTime(m_filter.getEndDate());
				DatePickerDialog dialog=new DatePickerDialog(
						aGraphicsPie.this,
						m_endListener,
						cal.get(Calendar.YEAR), 
                        cal.get(Calendar.MONTH), 
                        cal.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});
	}
	
	private AbstractChart genPieChart() {
		DefaultRenderer renderer=ChartUtility.buildDefaultRenderer(m_dataset.length, 20, 30, 15, 0);
		CategorySeries series=new CategorySeries("Report by All Categories");
		
		for(ReportByCategoryItem it : m_dataset) {
			series.add(it.getCategory(), it.getValue());
		}
		
		return new PieChart(series, renderer);
	}
	
	private AbstractChart genBarChart() {
	    XYMultipleSeriesRenderer renderer = ChartUtility.buildBarRenderer(1);
	    renderer.setYAxisMin(0D);
	    renderer.setXLabelsAngle(35);
        renderer.setXLabels(0);
        int lab=1;
		for(ReportByCategoryItem ce : m_dataset)
			renderer.addXTextLabel(lab++, ce.getCategory());
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	    // renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);
	    renderer.setShowLegend(false);
	    
	    XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();
	    CategorySeries series = new CategorySeries(/*getResources().getString(R.string.category).toString()*/"");

	    for(ReportByCategoryItem ce : m_dataset)
	    	series.add(ce.getValue());
	    
	    dataset.addSeries(series.toXYSeries());
	    
		return new BarChart(dataset,renderer,Type.STACKED);
	}
	
	private void fillGraph() {
		if((m_dataset==null)||(m_dataset.length==0)) {
			TextView View=new TextView(this);
			LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			LinearLayout parentView=(LinearLayout)findViewById(R.id.grph_pie_draw);
			parentView.removeAllViews();
			parentView.addView(View, 0, layoutParams);
			View.setText(R.string.no_data);
		} else {

			AbstractChart chart;
			if(m_filter.getViewMode()==GraphicsPieFilter.PIE)
				chart=genPieChart();
			else
				chart=genBarChart();
			
			GraphicalView View=new GraphicalView(this, chart);
			LinearLayout parentView=(LinearLayout)findViewById(R.id.grph_pie_draw);
			parentView.removeAllViews();
			parentView.addView(View);			
		}
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
				ret[cnt]=new LookupEntity(a.getId(), a.getName(), 0);
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

				Spinner spnAccounts=(Spinner)findViewById(R.id.grph_pie_spn_accounts);
				spnAccounts.setAdapter(new LookupArrayAdapter(aGraphicsPie.this, getAccountList()));

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
	
	private OnItemSelectedListener m_viewmodeListener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
			m_filter.setViewMode(position);
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private DatePickerDialog.OnDateSetListener m_beginListener=new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
        	Calendar cal=Calendar.getInstance();
        	cal.set(year,monthOfYear,dayOfMonth);
        	m_filter.setBeginDate(cal.getTime());
        	((Button)findViewById(R.id.grph_pie_begin))
        		.setText(textUtils.DateToString(m_filter.getBeginDate()));
		}
	};

	private DatePickerDialog.OnDateSetListener m_endListener=new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
        	Calendar cal=Calendar.getInstance();
        	cal.set(year,monthOfYear,dayOfMonth);
        	m_filter.setEndDate(cal.getTime());
        	((Button)findViewById(R.id.grph_pie_end))
    			.setText(textUtils.DateToString(m_filter.getEndDate()));
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
		private String[] m_currency_list;
		private AccountEntity[] m_account_list;
		private ReportByCategoryItem[] m_dataset;
		private final GraphicsPieFilter m_filter;

		protected GraphLoad(DatabaseHelper dbhelper, Context context, GraphicsPieFilter filter) {
			super(dbhelper, context);
			m_filter=filter;
		}

		@Override
		protected Void doInBackground(Void... params) {
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
				
				
				m_dataset=AccountActions.getReportItemsByPieFilter(m_dbhelper, m_filter);
			} catch(Exception e) {
				Log.e(this.getClass().getName(),"Load",e);
			}
			return null;
		}

		public String[] getCurrencyCodeList() {
			return m_currency_list;
		}
		
		public AccountEntity[] getAccountList() {
			return m_account_list;
		}

		public ReportByCategoryItem[] getDataset() {
			return m_dataset;
		}
	}
}
