package iae.home.utils.orm.task;

import iae.home.utils.task.Task;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public abstract class OrmLiteBaseTask<DBHelper extends OrmLiteSqliteOpenHelper,Result> extends Task<Void,Result> {
	protected DBHelper m_dbhelper=null;
	
	protected OrmLiteBaseTask(DBHelper dbhelper) {
		this.m_dbhelper=dbhelper;
	}
	
}
