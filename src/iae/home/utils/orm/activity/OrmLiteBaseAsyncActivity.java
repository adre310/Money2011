package iae.home.utils.orm.activity;

import iae.home.utils.task.AsyncTaskManager;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class OrmLiteBaseAsyncActivity <DBHelper extends OrmLiteSqliteOpenHelper,Result> extends OrmLiteBaseActivity<DBHelper> {
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
