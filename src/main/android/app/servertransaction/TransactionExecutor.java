package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.IBinder;
import android.util.IntArray;
import android.util.Slog;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class TransactionExecutor {
    private static final boolean DEBUG_RESOLVER = false;
    private static final String TAG = "TransactionExecutor";
    private ClientTransactionHandler mTransactionHandler;
    private PendingTransactionActions mPendingActions = new PendingTransactionActions();
    private TransactionExecutorHelper mHelper = new TransactionExecutorHelper();

    public TransactionExecutor(ClientTransactionHandler clientTransactionHandler) {
        this.mTransactionHandler = clientTransactionHandler;
    }

    public void execute(ClientTransaction transaction) {
        Map<IBinder, ClientTransactionItem> activitiesToBeDestroyed;
        ClientTransactionItem destroyItem;
        IBinder token = transaction.getActivityToken();
        if (token != null && (destroyItem = (activitiesToBeDestroyed = this.mTransactionHandler.getActivitiesToBeDestroyed()).get(token)) != null) {
            if (transaction.getLifecycleStateRequest() == destroyItem) {
                activitiesToBeDestroyed.remove(token);
            }
            if (this.mTransactionHandler.getActivityClient(token) == null) {
                Slog.m90w(TAG, TransactionExecutorHelper.tId(transaction) + "Skip pre-destroyed transaction:\n" + TransactionExecutorHelper.transactionToString(transaction, this.mTransactionHandler));
                return;
            }
        }
        executeCallbacks(transaction);
        executeLifecycleState(transaction);
        this.mPendingActions.clear();
    }

    public void executeCallbacks(ClientTransaction transaction) {
        int closestPreExecutionState;
        List<ClientTransactionItem> callbacks = transaction.getCallbacks();
        if (callbacks == null || callbacks.isEmpty()) {
            return;
        }
        IBinder token = transaction.getActivityToken();
        ActivityThread.ActivityClientRecord r = this.mTransactionHandler.getActivityClient(token);
        ActivityLifecycleItem finalStateRequest = transaction.getLifecycleStateRequest();
        int finalState = finalStateRequest != null ? finalStateRequest.getTargetState() : -1;
        int lastCallbackRequestingState = TransactionExecutorHelper.lastCallbackRequestingState(transaction);
        int size = callbacks.size();
        int i = 0;
        while (i < size) {
            ClientTransactionItem item = callbacks.get(i);
            int postExecutionState = item.getPostExecutionState();
            if (item.shouldHaveDefinedPreExecutionState() && (closestPreExecutionState = this.mHelper.getClosestPreExecutionState(r, item.getPostExecutionState())) != -1) {
                cycleToPath(r, closestPreExecutionState, transaction);
            }
            item.execute(this.mTransactionHandler, token, this.mPendingActions);
            item.postExecute(this.mTransactionHandler, token, this.mPendingActions);
            if (r == null) {
                r = this.mTransactionHandler.getActivityClient(token);
            }
            if (postExecutionState != -1 && r != null) {
                boolean shouldExcludeLastTransition = i == lastCallbackRequestingState && finalState == postExecutionState;
                cycleToPath(r, postExecutionState, shouldExcludeLastTransition, transaction);
            }
            i++;
        }
    }

    private void executeLifecycleState(ClientTransaction transaction) {
        IBinder token;
        ActivityThread.ActivityClientRecord r;
        ActivityLifecycleItem lifecycleItem = transaction.getLifecycleStateRequest();
        if (lifecycleItem == null || (r = this.mTransactionHandler.getActivityClient((token = transaction.getActivityToken()))) == null) {
            return;
        }
        cycleToPath(r, lifecycleItem.getTargetState(), true, transaction);
        lifecycleItem.execute(this.mTransactionHandler, token, this.mPendingActions);
        lifecycleItem.postExecute(this.mTransactionHandler, token, this.mPendingActions);
    }

    public void cycleToPath(ActivityThread.ActivityClientRecord r, int finish, ClientTransaction transaction) {
        cycleToPath(r, finish, false, transaction);
    }

    private void cycleToPath(ActivityThread.ActivityClientRecord r, int finish, boolean excludeLastState, ClientTransaction transaction) {
        int start = r.getLifecycleState();
        IntArray path = this.mHelper.getLifecyclePath(start, finish, excludeLastState);
        performLifecycleSequence(r, path, transaction);
    }

    private void performLifecycleSequence(ActivityThread.ActivityClientRecord r, IntArray path, ClientTransaction transaction) {
        int size = path.size();
        for (int i = 0; i < size; i++) {
            int state = path.get(i);
            switch (state) {
                case 1:
                    this.mTransactionHandler.handleLaunchActivity(r, this.mPendingActions, -1, null);
                    break;
                case 2:
                    this.mTransactionHandler.handleStartActivity(r, this.mPendingActions, null);
                    break;
                case 3:
                    this.mTransactionHandler.handleResumeActivity(r, false, r.isForward, false, "LIFECYCLER_RESUME_ACTIVITY");
                    break;
                case 4:
                    this.mTransactionHandler.handlePauseActivity(r, false, false, 0, false, this.mPendingActions, "LIFECYCLER_PAUSE_ACTIVITY");
                    break;
                case 5:
                    this.mTransactionHandler.handleStopActivity(r, 0, this.mPendingActions, false, "LIFECYCLER_STOP_ACTIVITY");
                    break;
                case 6:
                    this.mTransactionHandler.handleDestroyActivity(r, false, 0, false, "performLifecycleSequence. cycling to:" + path.get(size - 1));
                    break;
                case 7:
                    this.mTransactionHandler.performRestartActivity(r, false);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected lifecycle state: " + state);
            }
        }
    }
}
