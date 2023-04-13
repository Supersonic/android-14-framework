package android.app.servertransaction;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.app.admin.DevicePolicyResources;
import android.p008os.IBinder;
import android.util.IntArray;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
/* loaded from: classes.dex */
public class TransactionExecutorHelper {
    private static final int DESTRUCTION_PENALTY = 10;
    private IntArray mLifecycleSequence = new IntArray(6);
    private static final String TAG = TransactionExecutorHelper.class.getSimpleName();
    private static final int[] ON_RESUME_PRE_EXCUTION_STATES = {2, 4};

    public IntArray getLifecyclePath(int start, int finish, boolean excludeLastState) {
        if (start == -1 || finish == -1) {
            throw new IllegalArgumentException("Can't resolve lifecycle path for undefined state");
        }
        if (start == 7 || finish == 7) {
            throw new IllegalArgumentException("Can't start or finish in intermittent RESTART state");
        }
        if (finish == 0 && start != finish) {
            throw new IllegalArgumentException("Can only start in pre-onCreate state");
        }
        this.mLifecycleSequence.clear();
        if (finish >= start) {
            if (start == 2 && finish == 5) {
                this.mLifecycleSequence.add(5);
            } else {
                for (int i = start + 1; i <= finish; i++) {
                    this.mLifecycleSequence.add(i);
                }
            }
        } else if (start == 4 && finish == 3) {
            this.mLifecycleSequence.add(3);
        } else if (start <= 5 && finish >= 2) {
            for (int i2 = start + 1; i2 <= 5; i2++) {
                this.mLifecycleSequence.add(i2);
            }
            this.mLifecycleSequence.add(7);
            for (int i3 = 2; i3 <= finish; i3++) {
                this.mLifecycleSequence.add(i3);
            }
        } else {
            for (int i4 = start + 1; i4 <= 6; i4++) {
                this.mLifecycleSequence.add(i4);
            }
            for (int i5 = 1; i5 <= finish; i5++) {
                this.mLifecycleSequence.add(i5);
            }
        }
        if (excludeLastState && this.mLifecycleSequence.size() != 0) {
            IntArray intArray = this.mLifecycleSequence;
            intArray.remove(intArray.size() - 1);
        }
        return this.mLifecycleSequence;
    }

    public int getClosestPreExecutionState(ActivityThread.ActivityClientRecord r, int postExecutionState) {
        switch (postExecutionState) {
            case -1:
                return -1;
            case 3:
                return getClosestOfStates(r, ON_RESUME_PRE_EXCUTION_STATES);
            default:
                throw new UnsupportedOperationException("Pre-execution states for state: " + postExecutionState + " is not supported.");
        }
    }

    public int getClosestOfStates(ActivityThread.ActivityClientRecord r, int[] finalStates) {
        if (finalStates == null || finalStates.length == 0) {
            return -1;
        }
        if (r == null) {
            Log.m104w(TAG, "ActivityClientRecord was null");
            return -1;
        }
        int currentState = r.getLifecycleState();
        int closestState = -1;
        int shortestPath = Integer.MAX_VALUE;
        for (int i = 0; i < finalStates.length; i++) {
            getLifecyclePath(currentState, finalStates[i], false);
            int pathLength = this.mLifecycleSequence.size();
            if (pathInvolvesDestruction(this.mLifecycleSequence)) {
                pathLength += 10;
            }
            if (shortestPath > pathLength) {
                shortestPath = pathLength;
                closestState = finalStates[i];
            }
        }
        return closestState;
    }

    public static ActivityLifecycleItem getLifecycleRequestForCurrentState(ActivityThread.ActivityClientRecord r) {
        int prevState = r.getLifecycleState();
        switch (prevState) {
            case 2:
                ActivityLifecycleItem lifecycleItem = StartActivityItem.obtain(null);
                return lifecycleItem;
            case 3:
            default:
                ActivityLifecycleItem lifecycleItem2 = ResumeActivityItem.obtain(false, false);
                return lifecycleItem2;
            case 4:
                ActivityLifecycleItem lifecycleItem3 = PauseActivityItem.obtain();
                return lifecycleItem3;
            case 5:
                ActivityLifecycleItem lifecycleItem4 = StopActivityItem.obtain(0);
                return lifecycleItem4;
        }
    }

    private static boolean pathInvolvesDestruction(IntArray lifecycleSequence) {
        int size = lifecycleSequence.size();
        for (int i = 0; i < size; i++) {
            if (lifecycleSequence.get(i) == 6) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int lastCallbackRequestingState(ClientTransaction transaction) {
        List<ClientTransactionItem> callbacks = transaction.getCallbacks();
        if (callbacks == null || callbacks.size() == 0) {
            return -1;
        }
        int lastRequestedState = -1;
        int lastRequestingCallback = -1;
        for (int i = callbacks.size() - 1; i >= 0; i--) {
            ClientTransactionItem callback = callbacks.get(i);
            int postExecutionState = callback.getPostExecutionState();
            if (postExecutionState != -1) {
                if (lastRequestedState != -1 && lastRequestedState != postExecutionState) {
                    break;
                }
                lastRequestedState = postExecutionState;
                lastRequestingCallback = i;
            }
        }
        return lastRequestingCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String transactionToString(ClientTransaction transaction, ClientTransactionHandler transactionHandler) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        String prefix = tId(transaction);
        transaction.dump(prefix, pw);
        pw.append((CharSequence) (prefix + "Target activity: ")).println(getActivityName(transaction.getActivityToken(), transactionHandler));
        return stringWriter.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String tId(ClientTransaction transaction) {
        return "tId:" + transaction.hashCode() + " ";
    }

    static String getActivityName(IBinder token, ClientTransactionHandler transactionHandler) {
        Activity activity = getActivityForToken(token, transactionHandler);
        if (activity != null) {
            return activity.getComponentName().getClassName();
        }
        return "Not found for token: " + token;
    }

    static String getShortActivityName(IBinder token, ClientTransactionHandler transactionHandler) {
        Activity activity = getActivityForToken(token, transactionHandler);
        if (activity != null) {
            return activity.getComponentName().getShortClassName();
        }
        return "Not found for token: " + token;
    }

    private static Activity getActivityForToken(IBinder token, ClientTransactionHandler transactionHandler) {
        if (token == null) {
            return null;
        }
        return transactionHandler.getActivity(token);
    }

    static String getStateName(int state) {
        switch (state) {
            case -1:
                return DevicePolicyResources.UNDEFINED;
            case 0:
                return "PRE_ON_CREATE";
            case 1:
                return "ON_CREATE";
            case 2:
                return "ON_START";
            case 3:
                return "ON_RESUME";
            case 4:
                return "ON_PAUSE";
            case 5:
                return "ON_STOP";
            case 6:
                return "ON_DESTROY";
            case 7:
                return "ON_RESTART";
            default:
                throw new IllegalArgumentException("Unexpected lifecycle state: " + state);
        }
    }
}
