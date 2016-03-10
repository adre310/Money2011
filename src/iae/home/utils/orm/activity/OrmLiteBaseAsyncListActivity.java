package iae.home.utils.orm.activity;

import iae.home.utils.task.AsyncTaskManager;
import android.os.Bundle;

//import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class OrmLiteBaseAsyncListActivity<DBHelper extends OrmLiteSqliteOpenHelper,Result> extends OrmLiteBaseListActivity<DBHelper> {
	protected AsyncTaskManager<Void,Result> m_taskManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        m_taskManager=new AsyncTaskManager<Void, Result>(this);
        m_taskManager.handleRetainedTask(getLastNonConfigurationInstance());
     }	

    @Override
    public Object onRetainNonConfigurationInstance() {
    	return m_taskManager.retainTask();
    }
}
