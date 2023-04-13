package android.filterfw.core;

import android.filterfw.core.GraphRunner;
import android.p008os.AsyncTask;
import android.util.Log;
/* loaded from: classes.dex */
public class AsyncRunner extends GraphRunner {
    private static final String TAG = "AsyncRunner";
    private boolean isProcessing;
    private GraphRunner.OnRunnerDoneListener mDoneListener;
    private Exception mException;
    private boolean mLogVerbose;
    private AsyncRunnerTask mRunTask;
    private SyncRunner mRunner;
    private Class mSchedulerClass;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RunnerResult {
        public Exception exception;
        public int status;

        private RunnerResult() {
            this.status = 0;
        }
    }

    /* loaded from: classes.dex */
    private class AsyncRunnerTask extends AsyncTask<SyncRunner, Void, RunnerResult> {
        private static final String TAG = "AsyncRunnerTask";

        private AsyncRunnerTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public RunnerResult doInBackground(SyncRunner... runner) {
            RunnerResult result = new RunnerResult();
            try {
            } catch (Exception exception) {
                result.exception = exception;
                result.status = 6;
            }
            if (runner.length > 1) {
                throw new RuntimeException("More than one runner received!");
            }
            runner[0].assertReadyToStep();
            if (AsyncRunner.this.mLogVerbose) {
                Log.m106v(TAG, "Starting background graph processing.");
            }
            AsyncRunner.this.activateGlContext();
            if (AsyncRunner.this.mLogVerbose) {
                Log.m106v(TAG, "Preparing filter graph for processing.");
            }
            runner[0].beginProcessing();
            if (AsyncRunner.this.mLogVerbose) {
                Log.m106v(TAG, "Running graph.");
            }
            result.status = 1;
            while (!isCancelled() && result.status == 1) {
                if (!runner[0].performStep()) {
                    result.status = runner[0].determinePostRunState();
                    if (result.status == 3) {
                        runner[0].waitUntilWake();
                        result.status = 1;
                    }
                }
            }
            if (isCancelled()) {
                result.status = 5;
            }
            try {
                AsyncRunner.this.deactivateGlContext();
            } catch (Exception exception2) {
                result.exception = exception2;
                result.status = 6;
            }
            if (AsyncRunner.this.mLogVerbose) {
                Log.m106v(TAG, "Done with background graph processing.");
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public void onCancelled(RunnerResult result) {
            onPostExecute(result);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public void onPostExecute(RunnerResult result) {
            if (AsyncRunner.this.mLogVerbose) {
                Log.m106v(TAG, "Starting post-execute.");
            }
            AsyncRunner.this.setRunning(false);
            if (result == null) {
                result = new RunnerResult();
                result.status = 5;
            }
            AsyncRunner.this.setException(result.exception);
            if (result.status == 5 || result.status == 6) {
                if (AsyncRunner.this.mLogVerbose) {
                    Log.m106v(TAG, "Closing filters.");
                }
                try {
                    AsyncRunner.this.mRunner.close();
                } catch (Exception exception) {
                    result.status = 6;
                    AsyncRunner.this.setException(exception);
                }
            }
            if (AsyncRunner.this.mDoneListener != null) {
                if (AsyncRunner.this.mLogVerbose) {
                    Log.m106v(TAG, "Calling graph done callback.");
                }
                AsyncRunner.this.mDoneListener.onRunnerDone(result.status);
            }
            if (AsyncRunner.this.mLogVerbose) {
                Log.m106v(TAG, "Completed post-execute.");
            }
        }
    }

    public AsyncRunner(FilterContext context, Class schedulerClass) {
        super(context);
        this.mSchedulerClass = schedulerClass;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    public AsyncRunner(FilterContext context) {
        super(context);
        this.mSchedulerClass = SimpleScheduler.class;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    @Override // android.filterfw.core.GraphRunner
    public void setDoneCallback(GraphRunner.OnRunnerDoneListener listener) {
        this.mDoneListener = listener;
    }

    public synchronized void setGraph(FilterGraph graph) {
        if (isRunning()) {
            throw new RuntimeException("Graph is already running!");
        }
        this.mRunner = new SyncRunner(this.mFilterContext, graph, this.mSchedulerClass);
    }

    @Override // android.filterfw.core.GraphRunner
    public FilterGraph getGraph() {
        SyncRunner syncRunner = this.mRunner;
        if (syncRunner != null) {
            return syncRunner.getGraph();
        }
        return null;
    }

    @Override // android.filterfw.core.GraphRunner
    public synchronized void run() {
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Running graph.");
        }
        setException(null);
        if (isRunning()) {
            throw new RuntimeException("Graph is already running!");
        }
        if (this.mRunner == null) {
            throw new RuntimeException("Cannot run before a graph is set!");
        }
        this.mRunTask = new AsyncRunnerTask();
        setRunning(true);
        this.mRunTask.execute(this.mRunner);
    }

    @Override // android.filterfw.core.GraphRunner
    public synchronized void stop() {
        AsyncRunnerTask asyncRunnerTask = this.mRunTask;
        if (asyncRunnerTask != null && !asyncRunnerTask.isCancelled()) {
            if (this.mLogVerbose) {
                Log.m106v(TAG, "Stopping graph.");
            }
            this.mRunTask.cancel(false);
        }
    }

    @Override // android.filterfw.core.GraphRunner
    public synchronized void close() {
        if (isRunning()) {
            throw new RuntimeException("Cannot close graph while it is running!");
        }
        if (this.mLogVerbose) {
            Log.m106v(TAG, "Closing filters.");
        }
        this.mRunner.close();
    }

    @Override // android.filterfw.core.GraphRunner
    public synchronized boolean isRunning() {
        return this.isProcessing;
    }

    @Override // android.filterfw.core.GraphRunner
    public synchronized Exception getError() {
        return this.mException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setRunning(boolean running) {
        this.isProcessing = running;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setException(Exception exception) {
        this.mException = exception;
    }
}
