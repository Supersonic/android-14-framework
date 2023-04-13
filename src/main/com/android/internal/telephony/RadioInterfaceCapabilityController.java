package com.android.internal.telephony;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;
/* loaded from: classes.dex */
public class RadioInterfaceCapabilityController extends Handler {
    private static final String LOG_TAG = RadioInterfaceCapabilityController.class.getSimpleName();
    private static RadioInterfaceCapabilityController sInstance;
    private final CommandsInterface mCommandsInterface;
    private final Object mLockRadioInterfaceCapabilities;
    private final RadioConfig mRadioConfig;
    private Set<String> mRadioInterfaceCapabilities;

    public static RadioInterfaceCapabilityController init(RadioConfig radioConfig, CommandsInterface commandsInterface) {
        RadioInterfaceCapabilityController radioInterfaceCapabilityController;
        synchronized (RadioInterfaceCapabilityController.class) {
            if (sInstance == null) {
                HandlerThread handlerThread = new HandlerThread("RHC");
                handlerThread.start();
                sInstance = new RadioInterfaceCapabilityController(radioConfig, commandsInterface, handlerThread.getLooper());
            } else {
                String str = LOG_TAG;
                Log.wtf(str, "init() called multiple times!  sInstance = " + sInstance);
            }
            radioInterfaceCapabilityController = sInstance;
        }
        return radioInterfaceCapabilityController;
    }

    public static RadioInterfaceCapabilityController getInstance() {
        if (sInstance == null) {
            Log.wtf(LOG_TAG, "getInstance null");
        }
        return sInstance;
    }

    @VisibleForTesting
    public RadioInterfaceCapabilityController(RadioConfig radioConfig, CommandsInterface commandsInterface, Looper looper) {
        super(looper);
        this.mLockRadioInterfaceCapabilities = new Object();
        this.mRadioConfig = radioConfig;
        this.mCommandsInterface = commandsInterface;
        register();
    }

    public Set<String> getCapabilities() {
        if (this.mRadioInterfaceCapabilities == null) {
            synchronized (this.mLockRadioInterfaceCapabilities) {
                if (this.mRadioInterfaceCapabilities == null) {
                    this.mRadioConfig.getHalDeviceCapabilities(obtainMessage(100));
                    try {
                        if (Looper.myLooper() != getLooper()) {
                            this.mLockRadioInterfaceCapabilities.wait(2000L);
                        }
                    } catch (InterruptedException unused) {
                    }
                    if (this.mRadioInterfaceCapabilities == null) {
                        loge("getRadioInterfaceCapabilities: Radio Capabilities not loaded in time");
                        return new ArraySet();
                    }
                }
            }
        }
        return this.mRadioInterfaceCapabilities;
    }

    private void setupCapabilities(AsyncResult asyncResult) {
        if (this.mRadioInterfaceCapabilities == null) {
            synchronized (this.mLockRadioInterfaceCapabilities) {
                if (this.mRadioInterfaceCapabilities == null) {
                    if (asyncResult.exception != null) {
                        loge("setupRadioInterfaceCapabilities: " + asyncResult.exception);
                    }
                    if (asyncResult.result == null) {
                        loge("setupRadioInterfaceCapabilities: ar.result is null");
                        return;
                    }
                    log("setupRadioInterfaceCapabilities: mRadioInterfaceCapabilities now setup");
                    Set<String> unmodifiableSet = Collections.unmodifiableSet((Set) asyncResult.result);
                    this.mRadioInterfaceCapabilities = unmodifiableSet;
                    if (unmodifiableSet != null) {
                        unregister();
                    }
                }
                this.mLockRadioInterfaceCapabilities.notify();
            }
        }
    }

    private void register() {
        CommandsInterface commandsInterface = this.mCommandsInterface;
        if (commandsInterface == null) {
            this.mRadioInterfaceCapabilities = Collections.unmodifiableSet(new ArraySet());
        } else {
            commandsInterface.registerForAvailable(this, 1, null);
        }
    }

    private void unregister() {
        this.mCommandsInterface.unregisterForAvailable(this);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1 || i == 5) {
            getCapabilities();
        } else if (i != 100) {
        } else {
            setupCapabilities((AsyncResult) message.obj);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("mRadioConfig=" + this.mRadioConfig);
    }

    private static void log(String str) {
        Rlog.d(LOG_TAG, str);
    }

    private static void loge(String str) {
        Rlog.e(LOG_TAG, str);
    }
}
