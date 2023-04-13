package com.android.ims;

import android.content.Context;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.telephony.SubscriptionManager;
import android.util.Log;
/* loaded from: classes.dex */
public abstract class ImsCallbackAdapterManager<T extends IInterface> {
    private static final String TAG = "ImsCallbackAM";
    private final Context mContext;
    private final Object mLock;
    private final RemoteCallbackList<T> mRemoteCallbacks = new RemoteCallbackList<>();
    private final int mSlotId;
    private final int mSubId;

    public abstract void registerCallback(T t);

    public abstract void unregisterCallback(T t);

    public ImsCallbackAdapterManager(Context context, Object lock, int slotId, int subId) {
        this.mContext = context;
        this.mLock = lock;
        this.mSlotId = slotId;
        this.mSubId = subId;
    }

    public final void addCallback(T localCallback) {
        synchronized (this.mLock) {
            registerCallback(localCallback);
            Log.i("ImsCallbackAM [" + this.mSlotId + "]", "Local callback added: " + localCallback);
            this.mRemoteCallbacks.register(localCallback);
        }
    }

    public void addCallbackForSubscription(T localCallback, int subId) {
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            Log.w("ImsCallbackAM [" + this.mSlotId + ", " + this.mSubId + "]", "add callback: invalid subId.");
        } else if (this.mSubId != subId) {
            Log.w("ImsCallbackAM [" + this.mSlotId + ", " + this.mSubId + "]", "add callback: inactive subID detected: " + subId);
            throw new IllegalStateException("ImsService is not available for the subscription specified.");
        } else {
            synchronized (this.mLock) {
                addCallback(localCallback);
            }
        }
    }

    public final void removeCallback(T localCallback) {
        Log.i("ImsCallbackAM [" + this.mSlotId + "]", "Local callback removed: " + localCallback);
        synchronized (this.mLock) {
            if (this.mRemoteCallbacks.unregister(localCallback)) {
                unregisterCallback(localCallback);
            }
        }
    }

    public final void close() {
        synchronized (this.mLock) {
            int lastCallbackIndex = this.mRemoteCallbacks.getRegisteredCallbackCount() - 1;
            for (int ii = lastCallbackIndex; ii >= 0; ii--) {
                T callbackItem = this.mRemoteCallbacks.getRegisteredCallbackItem(ii);
                unregisterCallback(callbackItem);
                this.mRemoteCallbacks.unregister(callbackItem);
            }
            Log.i("ImsCallbackAM [" + this.mSlotId + "]", "Closing connection and clearing callbacks");
        }
    }
}
