package iae.home.utils.activity;

import iae.home.utils.task.AsyncTaskManager;
import android.app.ListActivity;
import android.os.Bundle;

public class AsyncListActivity<Params, Result> extends ListActivity {
	protected AsyncTaskManager<Params, Result> m_taskManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        m_taskManager=new AsyncTaskManager<Params, Result>(this);
        m_taskManager.handleRetainedTask(getLastNonConfigurationInstance());
    
    }	

    @Override
    public Object onRetainNonConfigurationInstance() {
    	return m_taskManager.retainTask();
    }

}
