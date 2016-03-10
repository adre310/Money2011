package iae.home.utils.task;

public interface IProgressTracker<Result> {
    // Updates progress message
    void onProgress(String message);
    // Notifies about task completeness
    void onComplete(Result result);
}
