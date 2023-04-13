package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.AsyncResult;
import android.os.Message;
import android.os.SystemClock;
import android.os.WorkSource;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.telephony.Rlog;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
/* loaded from: classes.dex */
public class RILRequest {
    Object[] mArguments;
    String mClientId;
    RILRequest mNext;
    @UnsupportedAppUsage
    int mRequest;
    @UnsupportedAppUsage
    Message mResult;
    @UnsupportedAppUsage
    int mSerial;
    long mStartTimeMs;
    int mWakeLockType;
    WorkSource mWorkSource;
    static Random sRandom = new Random();
    static AtomicInteger sNextSerial = new AtomicInteger(0);
    private static Object sPoolSync = new Object();
    private static RILRequest sPool = null;
    private static int sPoolSize = 0;

    public int getSerial() {
        return this.mSerial;
    }

    public int getRequest() {
        return this.mRequest;
    }

    public Message getResult() {
        return this.mResult;
    }

    @UnsupportedAppUsage
    private static RILRequest obtain(int i, Message message) {
        RILRequest rILRequest;
        synchronized (sPoolSync) {
            rILRequest = sPool;
            if (rILRequest != null) {
                sPool = rILRequest.mNext;
                rILRequest.mNext = null;
                sPoolSize--;
            } else {
                rILRequest = null;
            }
        }
        if (rILRequest == null) {
            rILRequest = new RILRequest();
        }
        rILRequest.mSerial = sNextSerial.getAndUpdate(new IntUnaryOperator() { // from class: com.android.internal.telephony.RILRequest$$ExternalSyntheticLambda0
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i2) {
                int lambda$obtain$0;
                lambda$obtain$0 = RILRequest.lambda$obtain$0(i2);
                return lambda$obtain$0;
            }
        });
        rILRequest.mRequest = i;
        rILRequest.mResult = message;
        rILRequest.mWakeLockType = -1;
        rILRequest.mWorkSource = null;
        rILRequest.mStartTimeMs = SystemClock.elapsedRealtime();
        if (message == null || message.getTarget() != null) {
            return rILRequest;
        }
        throw new NullPointerException("Message target must not be null");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$obtain$0(int i) {
        return (i + 1) % KeepaliveStatus.INVALID_HANDLE;
    }

    public static RILRequest obtain(int i, Message message, WorkSource workSource) {
        RILRequest obtain = obtain(i, message);
        if (workSource != null) {
            obtain.mWorkSource = workSource;
            obtain.mClientId = obtain.getWorkSourceClientId();
        } else {
            Rlog.e("RilRequest", "null workSource " + i);
        }
        return obtain;
    }

    public static RILRequest obtain(int i, Message message, WorkSource workSource, Object... objArr) {
        RILRequest obtain = obtain(i, message, workSource);
        obtain.mArguments = objArr;
        return obtain;
    }

    public String getWorkSourceClientId() {
        WorkSource workSource = this.mWorkSource;
        if (workSource != null && !workSource.isEmpty()) {
            if (this.mWorkSource.size() > 0) {
                return this.mWorkSource.getUid(0) + ":" + this.mWorkSource.getPackageName(0);
            }
            List workChains = this.mWorkSource.getWorkChains();
            if (workChains != null && !workChains.isEmpty()) {
                return ((WorkSource.WorkChain) workChains.get(0)).toString();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void release() {
        synchronized (sPoolSync) {
            int i = sPoolSize;
            if (i < 4) {
                this.mNext = sPool;
                sPool = this;
                sPoolSize = i + 1;
                this.mResult = null;
                int i2 = this.mWakeLockType;
                if (i2 != -1 && i2 == 0) {
                    Rlog.e("RilRequest", "RILRequest releasing with held wake lock: " + serialString());
                }
                this.mArguments = null;
            }
        }
    }

    private RILRequest() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void resetSerial() {
        sNextSerial.set(sRandom.nextInt(KeepaliveStatus.INVALID_HANDLE));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public String serialString() {
        StringBuilder sb = new StringBuilder(8);
        String num = Integer.toString(this.mSerial % 10000);
        sb.append('[');
        int length = num.length();
        for (int i = 0; i < 4 - length; i++) {
            sb.append('0');
        }
        sb.append(num);
        sb.append(']');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void onError(int i, Object obj) {
        CommandException fromRilErrno = CommandException.fromRilErrno(i);
        Message message = this.mResult;
        Rlog.d("RilRequest", serialString() + "< " + RILUtils.requestToString(this.mRequest) + " error: " + fromRilErrno + " ret=" + RIL.retToString(this.mRequest, obj) + " result=" + message);
        if (message == null || message.getTarget() == null) {
            return;
        }
        AsyncResult.forMessage(message, obj, fromRilErrno);
        message.sendToTarget();
    }

    public String toString() {
        return serialString() + ": " + RILUtils.requestToString(this.mRequest);
    }
}
