package iae.home.utils.task;

import android.os.AsyncTask;

public abstract class Task<Params,Result> extends AsyncTask<Params,String,Result> {
    protected String m_ProgressMessage;
    protected IProgressTracker<Result> m_tracker=null;

    /* UI Thread */
    @Override
    protected void onCancelled() {
	// Detach from progress tracker
    	m_tracker = null;
    }

    /* UI Thread */
    @Override
    protected void onProgressUpdate(String... values) {
    	// Update progress message 
    	m_ProgressMessage = values[0];
    	// And send it to progress tracker
    	if (m_tracker != null) {
    		m_tracker.onProgress(m_ProgressMessage);
    	}
    }

    /* UI Thread */
    @Override
    protected void onPostExecute(Result result) {
    	// And send it to progress tracker
    	if (m_tracker != null) {
    		m_tracker.onComplete(result);
    	}
    	// Detach from progress tracker
    	m_tracker = null;
    }
    
	public void setProgressTracker(IProgressTracker<Result> tracker) {
		m_tracker=tracker;
	}
}
