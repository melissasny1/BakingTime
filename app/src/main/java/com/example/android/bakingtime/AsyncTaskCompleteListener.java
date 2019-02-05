package com.example.android.bakingtime;

/**
 * Callback mechanism to abstract the AsyncTasks into a separate, re-usable
 * and testable class, yet still retain a hook back into the calling activity.
 *
 * @param <T>
 */

interface AsyncTaskCompleteListener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */

    void onTaskComplete(T result);
}
