package android.content;

import android.annotation.SystemApi;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.QueuedWork;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.util.Log;
/* loaded from: classes.dex */
public abstract class BroadcastReceiver {
    private boolean mDebugUnregister;
    private PendingResult mPendingResult;

    public abstract void onReceive(Context context, Intent intent);

    /* loaded from: classes.dex */
    public static class PendingResult {
        public static final int TYPE_COMPONENT = 0;
        public static final int TYPE_REGISTERED = 1;
        public static final int TYPE_UNREGISTERED = 2;
        boolean mAbortBroadcast;
        final boolean mAssumeDeliveredHint;
        boolean mFinished;
        final int mFlags;
        final boolean mInitialStickyHint;
        final boolean mOrderedHint;
        String mReceiverClassName;
        int mResultCode;
        String mResultData;
        Bundle mResultExtras;
        final int mSendingUser;
        final String mSentFromPackage;
        final int mSentFromUid;
        final IBinder mToken;
        final int mType;

        public PendingResult(int resultCode, String resultData, Bundle resultExtras, int type, boolean ordered, boolean sticky, IBinder token, int userId, int flags) {
            this(resultCode, resultData, resultExtras, type, ordered, sticky, guessAssumeDelivered(type, ordered), token, userId, flags, -1, null);
        }

        public PendingResult(int resultCode, String resultData, Bundle resultExtras, int type, boolean ordered, boolean sticky, boolean assumeDelivered, IBinder token, int userId, int flags, int sentFromUid, String sentFromPackage) {
            this.mResultCode = resultCode;
            this.mResultData = resultData;
            this.mResultExtras = resultExtras;
            this.mType = type;
            this.mOrderedHint = ordered;
            this.mInitialStickyHint = sticky;
            this.mAssumeDeliveredHint = assumeDelivered;
            this.mToken = token;
            this.mSendingUser = userId;
            this.mFlags = flags;
            this.mSentFromUid = sentFromUid;
            this.mSentFromPackage = sentFromPackage;
        }

        public static boolean guessAssumeDelivered(int type, boolean ordered) {
            if (type == 0) {
                return false;
            }
            if (ordered && type != 2) {
                return false;
            }
            return true;
        }

        public final void setResultCode(int code) {
            checkSynchronousHint();
            this.mResultCode = code;
        }

        public final int getResultCode() {
            return this.mResultCode;
        }

        public final void setResultData(String data) {
            checkSynchronousHint();
            this.mResultData = data;
        }

        public final String getResultData() {
            return this.mResultData;
        }

        public final void setResultExtras(Bundle extras) {
            checkSynchronousHint();
            this.mResultExtras = extras;
        }

        public final Bundle getResultExtras(boolean makeMap) {
            Bundle e = this.mResultExtras;
            if (makeMap) {
                if (e == null) {
                    Bundle e2 = new Bundle();
                    this.mResultExtras = e2;
                    return e2;
                }
                return e;
            }
            return e;
        }

        public final void setResult(int code, String data, Bundle extras) {
            checkSynchronousHint();
            this.mResultCode = code;
            this.mResultData = data;
            this.mResultExtras = extras;
        }

        public final boolean getAbortBroadcast() {
            return this.mAbortBroadcast;
        }

        public final void abortBroadcast() {
            checkSynchronousHint();
            this.mAbortBroadcast = true;
        }

        public final void clearAbortBroadcast() {
            this.mAbortBroadcast = false;
        }

        public final void finish() {
            if (Trace.isTagEnabled(64L)) {
                Trace.traceCounter(64L, "PendingResult#finish#ClassName:" + this.mReceiverClassName, 1);
            }
            if (this.mType == 0) {
                final IActivityManager mgr = ActivityManager.getService();
                if (QueuedWork.hasPendingWork()) {
                    QueuedWork.queue(new Runnable() { // from class: android.content.BroadcastReceiver.PendingResult.1
                        @Override // java.lang.Runnable
                        public void run() {
                            PendingResult.this.sendFinished(mgr);
                        }
                    }, false);
                    return;
                } else {
                    sendFinished(mgr);
                    return;
                }
            }
            sendFinished(ActivityManager.getService());
        }

        public void setExtrasClassLoader(ClassLoader cl) {
            Bundle bundle = this.mResultExtras;
            if (bundle != null) {
                bundle.setClassLoader(cl);
            }
        }

        public void sendFinished(IActivityManager am) {
            synchronized (this) {
                if (this.mFinished) {
                    throw new IllegalStateException("Broadcast already finished");
                }
                this.mFinished = true;
                try {
                    Bundle bundle = this.mResultExtras;
                    if (bundle != null) {
                        bundle.setAllowFds(false);
                    }
                    if (!this.mAssumeDeliveredHint) {
                        if (this.mOrderedHint) {
                            am.finishReceiver(this.mToken, this.mResultCode, this.mResultData, this.mResultExtras, this.mAbortBroadcast, this.mFlags);
                        } else {
                            am.finishReceiver(this.mToken, 0, null, null, false, this.mFlags);
                        }
                    }
                } catch (RemoteException e) {
                }
            }
        }

        public int getSendingUserId() {
            return this.mSendingUser;
        }

        public int getSentFromUid() {
            return this.mSentFromUid;
        }

        public String getSentFromPackage() {
            return this.mSentFromPackage;
        }

        void checkSynchronousHint() {
            if (this.mOrderedHint || this.mInitialStickyHint) {
                return;
            }
            RuntimeException e = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
            e.fillInStackTrace();
            Log.m109e("BroadcastReceiver", e.getMessage(), e);
        }
    }

    public final PendingResult goAsync() {
        PendingResult res = this.mPendingResult;
        this.mPendingResult = null;
        if (res != null && Trace.isTagEnabled(64L)) {
            res.mReceiverClassName = getClass().getName();
            Trace.traceCounter(64L, "BroadcastReceiver#goAsync#ClassName:" + res.mReceiverClassName, 1);
        }
        return res;
    }

    public IBinder peekService(Context myContext, Intent service) {
        IActivityManager am = ActivityManager.getService();
        try {
            service.prepareToLeaveProcess(myContext);
            IBinder binder = am.peekService(service, service.resolveTypeIfNeeded(myContext.getContentResolver()), myContext.getOpPackageName());
            return binder;
        } catch (RemoteException e) {
            return null;
        }
    }

    public final void setResultCode(int code) {
        checkSynchronousHint();
        this.mPendingResult.mResultCode = code;
    }

    public final int getResultCode() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mResultCode;
        }
        return 0;
    }

    public final void setResultData(String data) {
        checkSynchronousHint();
        this.mPendingResult.mResultData = data;
    }

    public final String getResultData() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mResultData;
        }
        return null;
    }

    public final void setResultExtras(Bundle extras) {
        checkSynchronousHint();
        this.mPendingResult.mResultExtras = extras;
    }

    public final Bundle getResultExtras(boolean makeMap) {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult == null) {
            return null;
        }
        Bundle e = pendingResult.mResultExtras;
        if (makeMap) {
            if (e == null) {
                PendingResult pendingResult2 = this.mPendingResult;
                Bundle e2 = new Bundle();
                pendingResult2.mResultExtras = e2;
                return e2;
            }
            return e;
        }
        return e;
    }

    public final void setResult(int code, String data, Bundle extras) {
        checkSynchronousHint();
        this.mPendingResult.mResultCode = code;
        this.mPendingResult.mResultData = data;
        this.mPendingResult.mResultExtras = extras;
    }

    public final boolean getAbortBroadcast() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mAbortBroadcast;
        }
        return false;
    }

    public final void abortBroadcast() {
        checkSynchronousHint();
        this.mPendingResult.mAbortBroadcast = true;
    }

    public final void clearAbortBroadcast() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            pendingResult.mAbortBroadcast = false;
        }
    }

    public final boolean isOrderedBroadcast() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mOrderedHint;
        }
        return false;
    }

    public final boolean isInitialStickyBroadcast() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mInitialStickyHint;
        }
        return false;
    }

    public final void setOrderedHint(boolean isOrdered) {
    }

    public final void setPendingResult(PendingResult result) {
        this.mPendingResult = result;
    }

    public final PendingResult getPendingResult() {
        return this.mPendingResult;
    }

    @SystemApi
    public final UserHandle getSendingUser() {
        return UserHandle.m145of(getSendingUserId());
    }

    public int getSendingUserId() {
        return this.mPendingResult.mSendingUser;
    }

    public int getSentFromUid() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mSentFromUid;
        }
        return -1;
    }

    public String getSentFromPackage() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult != null) {
            return pendingResult.mSentFromPackage;
        }
        return null;
    }

    public final void setDebugUnregister(boolean debug) {
        this.mDebugUnregister = debug;
    }

    public final boolean getDebugUnregister() {
        return this.mDebugUnregister;
    }

    void checkSynchronousHint() {
        PendingResult pendingResult = this.mPendingResult;
        if (pendingResult == null) {
            throw new IllegalStateException("Call while result is not pending");
        }
        if (pendingResult.mOrderedHint || this.mPendingResult.mInitialStickyHint) {
            return;
        }
        RuntimeException e = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
        e.fillInStackTrace();
        Log.m109e("BroadcastReceiver", e.getMessage(), e);
    }
}
