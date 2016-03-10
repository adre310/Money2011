package iae.home.utils.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;

public class AsyncTaskManager<Params,Result> implements OnCancelListener, IProgressTracker<Result> {
    private final String TAG;
	private final ProgressDialog m_progress;
	private Task<Params,Result> m_task=null;
	private OnTaskCompleted<Params, Result> m_oncompleted=null;
	
	public AsyncTaskManager(Context context) {
    	// Setup progress dialog
		TAG=this.getClass().getName();
    	m_progress = new ProgressDialog(context);
    	m_progress.setIndeterminate(true);
    	m_progress.setCancelable(true);
    	m_progress.setOnCancelListener(this);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		Log.i(TAG, "onCancel");
		if(m_task != null) {
			m_task.cancel(true);
			if(m_oncompleted !=null)
				m_oncompleted.onTaskComplete(m_task, null);
			m_task=null;
		}
	}
	
	@Override
	public void onProgress(String message) {
		Log.i(TAG, "onProgress - "+message);
		// Show dialog if it wasn't shown yet or was removed on configuration (rotation) change
		if (!m_progress.isShowing()) {
		    m_progress.show();
		}
		// Show current message in progress dialog
		m_progress.setMessage(message);
	}

	@Override
	public void onComplete(Result result) {
		Log.i(TAG, "onComplete");
		// Notify activity about completion
		if(m_oncompleted !=null)
			m_oncompleted.onTaskComplete(m_task, result);
		
		// Reset task
		m_task = null;
		
		// Close progress dialog		
		if (m_progress.isShowing()) {
			m_progress.dismiss();
		}
	}

	public void setOnTaskCompletedListener(OnTaskCompleted<Params, Result> listener) {
		m_oncompleted=listener;
	}

    public void setupTask(Task<Params,Result> task, Params... params) {
		Log.i(TAG, "setupTask");
    	// Keep task
    	m_task = task;
    	// Wire task to tracker (this)
    	m_task.setProgressTracker(this);
    	// Start task
    	m_task.execute(params);
    }

    public Object retainTask() {
		Log.i(TAG, "retainTask");
    	// Detach task from tracker (this) before retain
    	if (m_task != null) {
    	    m_task.setProgressTracker(null);
    	}
    	// Retain task
    	return m_task;
    }

    public void handleRetainedTask(Object instance) {
		Log.i(TAG, "handleRetainedTask");
    	// Restore retained task and attach it to tracker (this)
    	if (instance instanceof Task) {
    	    m_task = (Task<Params,Result>) instance;
    	    m_task.setProgressTracker(this);
    		Log.i(TAG, "handleRetainedTask - "+m_task.getClass().getSimpleName());
    	}
    }
    
    public boolean isWorking() {
    	// Track current status
    	return m_task != null;
    }
    
 }
