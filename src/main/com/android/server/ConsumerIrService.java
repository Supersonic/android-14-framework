package com.android.server;

import android.annotation.EnforcePermission;
import android.annotation.RequiresNoPermission;
import android.content.Context;
import android.hardware.IConsumerIrService;
import android.hardware.p001ir.ConsumerIrFreqRange;
import android.hardware.p001ir.IConsumerIr;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
/* loaded from: classes.dex */
public class ConsumerIrService extends IConsumerIrService.Stub {
    public final Context mContext;
    public final boolean mHasNativeHal;
    public final PowerManager.WakeLock mWakeLock;
    public final Object mHalLock = new Object();
    public IConsumerIr mAidlService = null;

    private static native boolean getHidlHalService();

    private static native int[] halGetCarrierFrequencies();

    private static native int halTransmit(int i, int[] iArr);

    public ConsumerIrService(Context context) {
        this.mContext = context;
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "ConsumerIrService");
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(true);
        boolean halService = getHalService();
        this.mHasNativeHal = halService;
        if (context.getPackageManager().hasSystemFeature("android.hardware.consumerir")) {
            if (!halService) {
                throw new RuntimeException("FEATURE_CONSUMER_IR present, but no IR HAL loaded!");
            }
        } else if (halService) {
            throw new RuntimeException("IR HAL present, but FEATURE_CONSUMER_IR is not set!");
        }
    }

    @RequiresNoPermission
    public boolean hasIrEmitter() {
        return this.mHasNativeHal;
    }

    public final boolean getHalService() {
        IConsumerIr asInterface = IConsumerIr.Stub.asInterface(ServiceManager.waitForDeclaredService(IConsumerIr.DESCRIPTOR + "/default"));
        this.mAidlService = asInterface;
        if (asInterface != null) {
            return true;
        }
        return getHidlHalService();
    }

    public final void throwIfNoIrEmitter() {
        if (!this.mHasNativeHal) {
            throw new UnsupportedOperationException("IR emitter not available");
        }
    }

    @EnforcePermission("android.permission.TRANSMIT_IR")
    public void transmit(String str, int i, int[] iArr) {
        super.transmit_enforcePermission();
        long j = 0;
        for (int i2 : iArr) {
            if (i2 <= 0) {
                throw new IllegalArgumentException("Non-positive IR slice");
            }
            j += i2;
        }
        if (j > 2000000) {
            throw new IllegalArgumentException("IR pattern too long");
        }
        throwIfNoIrEmitter();
        synchronized (this.mHalLock) {
            IConsumerIr iConsumerIr = this.mAidlService;
            if (iConsumerIr != null) {
                try {
                    iConsumerIr.transmit(i, iArr);
                } catch (RemoteException unused) {
                    Slog.e("ConsumerIrService", "Error transmitting frequency: " + i);
                }
            } else {
                int halTransmit = halTransmit(i, iArr);
                if (halTransmit < 0) {
                    Slog.e("ConsumerIrService", "Error transmitting: " + halTransmit);
                }
            }
        }
    }

    @EnforcePermission("android.permission.TRANSMIT_IR")
    public int[] getCarrierFrequencies() {
        super.getCarrierFrequencies_enforcePermission();
        throwIfNoIrEmitter();
        synchronized (this.mHalLock) {
            IConsumerIr iConsumerIr = this.mAidlService;
            if (iConsumerIr != null) {
                try {
                    ConsumerIrFreqRange[] carrierFreqs = iConsumerIr.getCarrierFreqs();
                    if (carrierFreqs.length <= 0) {
                        Slog.e("ConsumerIrService", "Error getting carrier frequencies.");
                    }
                    int[] iArr = new int[carrierFreqs.length * 2];
                    for (int i = 0; i < carrierFreqs.length; i++) {
                        int i2 = i * 2;
                        ConsumerIrFreqRange consumerIrFreqRange = carrierFreqs[i];
                        iArr[i2] = consumerIrFreqRange.minHz;
                        iArr[i2 + 1] = consumerIrFreqRange.maxHz;
                    }
                    return iArr;
                } catch (RemoteException unused) {
                    return null;
                }
            }
            return halGetCarrierFrequencies();
        }
    }
}
