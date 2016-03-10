package iae.home.utils.task;

public interface OnTaskCompleted<Params,Result> {
	void onTaskComplete(Task<Params,Result> task, Result result);
}
