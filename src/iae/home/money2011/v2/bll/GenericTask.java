package iae.home.money2011.v2.bll;

import android.content.Context;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.utils.orm.task.OrmLiteBaseTask;

public abstract class GenericTask<P> extends OrmLiteBaseTask<DatabaseHelper, P> {	
	protected Boolean m_isError;
	protected String m_errorMessage;
	protected final Context m_context; 
	protected GenericTask(DatabaseHelper dbhelper, Context context) {
		super(dbhelper);
		m_isError=false;
		m_context=context;
	}

	protected final void publishProgress (Integer resourceId) {
		publishProgress(m_context.getResources().getString(resourceId).toString());
	}

	protected void setErrorMessage(String message) {
		m_isError=true;
		m_errorMessage=message;
	}

	protected void setErrorMessage(Integer messageId) {
		m_isError=true;
		m_errorMessage=m_context.getResources().getString(messageId).toString();
	}
	
	public Boolean Successful() {
		return !m_isError;
	}
	
	public String getErrorMessage() {
		return m_errorMessage;
	}
}
