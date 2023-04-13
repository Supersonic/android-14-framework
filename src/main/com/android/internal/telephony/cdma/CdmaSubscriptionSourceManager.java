package com.android.internal.telephony.cdma;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.telephony.Rlog;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class CdmaSubscriptionSourceManager extends Handler {
    public static final int SUBSCRIPTION_FROM_NV = 1;
    public static final int SUBSCRIPTION_FROM_RUIM = 0;
    public static final int SUBSCRIPTION_SOURCE_UNKNOWN = -1;
    private static CdmaSubscriptionSourceManager sInstance;
    private static int sReferenceCount;
    private static final Object sReferenceCountMonitor = new Object();
    private CommandsInterface mCi;
    private RegistrantList mCdmaSubscriptionSourceChangedRegistrants = new RegistrantList();
    private AtomicInteger mCdmaSubscriptionSource = new AtomicInteger(0);

    private CdmaSubscriptionSourceManager(Context context, CommandsInterface commandsInterface) {
        this.mCi = commandsInterface;
        commandsInterface.registerForCdmaSubscriptionChanged(this, 1, null);
        this.mCi.registerForOn(this, 3, null);
        int i = getDefault(context);
        log("cdmaSSM constructor: " + i);
        this.mCdmaSubscriptionSource.set(i);
        this.mCi.registerForSubscriptionStatusChanged(this, 4, null);
    }

    @UnsupportedAppUsage
    public static CdmaSubscriptionSourceManager getInstance(Context context, CommandsInterface commandsInterface, Handler handler, int i, Object obj) {
        synchronized (sReferenceCountMonitor) {
            if (sInstance == null) {
                sInstance = new CdmaSubscriptionSourceManager(context, commandsInterface);
            }
            sReferenceCount++;
        }
        sInstance.registerForCdmaSubscriptionSourceChanged(handler, i, obj);
        return sInstance;
    }

    public void dispose(Handler handler) {
        this.mCdmaSubscriptionSourceChangedRegistrants.remove(handler);
        synchronized (sReferenceCountMonitor) {
            int i = sReferenceCount - 1;
            sReferenceCount = i;
            if (i <= 0) {
                this.mCi.unregisterForCdmaSubscriptionChanged(this);
                this.mCi.unregisterForOn(this);
                this.mCi.unregisterForSubscriptionStatusChanged(this);
                sInstance = null;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1 || i == 2) {
            log("CDMA_SUBSCRIPTION_SOURCE event = " + message.what);
            handleGetCdmaSubscriptionSource((AsyncResult) message.obj);
        } else if (i == 3) {
            this.mCi.getCdmaSubscriptionSource(obtainMessage(2));
        } else if (i == 4) {
            log("EVENT_SUBSCRIPTION_STATUS_CHANGED");
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult.exception == null) {
                int i2 = ((int[]) asyncResult.result)[0];
                log("actStatus = " + i2);
                if (i2 == 1) {
                    Rlog.v("CdmaSSM", "get Cdma Subscription Source");
                    this.mCi.getCdmaSubscriptionSource(obtainMessage(2));
                    return;
                }
                return;
            }
            logw("EVENT_SUBSCRIPTION_STATUS_CHANGED, Exception:" + asyncResult.exception);
        } else {
            super.handleMessage(message);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getCdmaSubscriptionSource() {
        log("getcdmasubscriptionSource: " + this.mCdmaSubscriptionSource.get());
        return this.mCdmaSubscriptionSource.get();
    }

    public static int getDefault(Context context) {
        int i = Settings.Global.getInt(context.getContentResolver(), "subscription_mode", 0);
        Rlog.d("CdmaSSM", "subscriptionSource from settings: " + i);
        return i;
    }

    private void registerForCdmaSubscriptionSourceChanged(Handler handler, int i, Object obj) {
        this.mCdmaSubscriptionSourceChangedRegistrants.add(new Registrant(handler, i, obj));
    }

    private void handleGetCdmaSubscriptionSource(AsyncResult asyncResult) {
        Object obj;
        if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
            int i = ((int[]) obj)[0];
            if (i != this.mCdmaSubscriptionSource.get()) {
                log("Subscription Source Changed : " + this.mCdmaSubscriptionSource + " >> " + i);
                this.mCdmaSubscriptionSource.set(i);
                this.mCdmaSubscriptionSourceChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
                return;
            }
            return;
        }
        logw("Unable to get CDMA Subscription Source, Exception: " + asyncResult.exception + ", result: " + asyncResult.result);
    }

    private void log(String str) {
        Rlog.d("CdmaSSM", str);
    }

    private void logw(String str) {
        Rlog.w("CdmaSSM", str);
    }
}
