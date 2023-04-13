package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CarrierServiceBindHelper;
import com.android.internal.telephony.util.TelephonyUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class CarrierServiceBindHelper {
    @VisibleForTesting
    public static final int EVENT_MULTI_SIM_CONFIG_CHANGED = 2;
    @VisibleForTesting
    public static final int EVENT_PERFORM_IMMEDIATE_UNBIND = 1;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Context mContext;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
    public Handler mHandler;
    private final LocalLog mLocalLog;
    private final PackageChangeReceiver mPackageMonitor;
    private BroadcastReceiver mUserUnlockedReceiver;
    @VisibleForTesting
    public SparseArray<AppBinding> mBindings = new SparseArray<>();
    @VisibleForTesting
    public SparseArray<String> mLastSimState = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CarrierServiceChangeCallback implements TelephonyManager.CarrierPrivilegesCallback {
        final int mPhoneId;

        public void onCarrierPrivilegesChanged(Set<String> set, Set<Integer> set2) {
        }

        CarrierServiceChangeCallback(int i) {
            this.mPhoneId = i;
        }

        public void onCarrierServiceChanged(String str, int i) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("onCarrierServiceChanged, carrierServicePackageName=" + str + ", carrierServiceUid=" + i + ", mPhoneId=" + this.mPhoneId);
            Handler handler = CarrierServiceBindHelper.this.mHandler;
            handler.sendMessage(handler.obtainMessage(0, Integer.valueOf(this.mPhoneId)));
        }
    }

    public CarrierServiceBindHelper(Context context) {
        CarrierServicePackageMonitor carrierServicePackageMonitor = new CarrierServicePackageMonitor();
        this.mPackageMonitor = carrierServicePackageMonitor;
        this.mLocalLog = new LocalLog(100);
        this.mUserUnlockedReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.CarrierServiceBindHelper.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
                carrierServiceBindHelper.logdWithLocalLog("Received " + action);
                if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    for (int i = 0; i < CarrierServiceBindHelper.this.mBindings.size(); i++) {
                        CarrierServiceBindHelper.this.mBindings.get(i).rebind();
                    }
                }
            }
        };
        this.mHandler = new Handler() { // from class: com.android.internal.telephony.CarrierServiceBindHelper.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
                carrierServiceBindHelper.logdWithLocalLog("mHandler: " + message.what);
                int i = message.what;
                if (i == 0) {
                    AppBinding appBinding = CarrierServiceBindHelper.this.mBindings.get(((Integer) message.obj).intValue());
                    if (appBinding == null) {
                        return;
                    }
                    CarrierServiceBindHelper carrierServiceBindHelper2 = CarrierServiceBindHelper.this;
                    carrierServiceBindHelper2.logdWithLocalLog("Rebinding if necessary for phoneId: " + appBinding.getPhoneId());
                    appBinding.rebind();
                } else if (i != 1) {
                    if (i == 2) {
                        CarrierServiceBindHelper.this.updateBindingsAndSimStates();
                        return;
                    }
                    Log.e("CarrierSvcBindHelper", "Unsupported event received: " + message.what);
                } else {
                    AppBinding appBinding2 = CarrierServiceBindHelper.this.mBindings.get(((Integer) message.obj).intValue());
                    if (appBinding2 == null) {
                        return;
                    }
                    CarrierServiceBindHelper carrierServiceBindHelper3 = CarrierServiceBindHelper.this;
                    carrierServiceBindHelper3.logdWithLocalLog("Unbind immediate with phoneId: " + appBinding2.getPhoneId());
                    appBinding2.performImmediateUnbind();
                }
            }
        };
        this.mContext = context.createContextAsUser(Process.myUserHandle(), 0);
        updateBindingsAndSimStates();
        PhoneConfigurationManager.registerForMultiSimConfigChange(this.mHandler, 2, null);
        carrierServicePackageMonitor.register(context, this.mHandler.getLooper(), UserHandle.ALL);
        try {
            Context context2 = this.mContext;
            context2.createPackageContextAsUser(context2.getPackageName(), 0, UserHandle.SYSTEM).registerReceiver(this.mUserUnlockedReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"), null, this.mHandler);
        } catch (PackageManager.NameNotFoundException e) {
            logeWithLocalLog("Package name not found: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBindingsAndSimStates() {
        int size = this.mBindings.size();
        int activeModemCount = ((TelephonyManager) this.mContext.getSystemService("phone")).getActiveModemCount();
        for (int i = size; i < activeModemCount; i++) {
            this.mBindings.put(i, new AppBinding(i));
            this.mLastSimState.put(i, new String());
        }
        while (activeModemCount < size) {
            this.mBindings.get(activeModemCount).tearDown();
            this.mBindings.get(activeModemCount).unbind(true);
            this.mBindings.delete(activeModemCount);
            this.mLastSimState.delete(activeModemCount);
            activeModemCount++;
        }
    }

    public void updateForPhoneId(int i, String str) {
        logdWithLocalLog("update binding for phoneId: " + i + " simState: " + str);
        if (SubscriptionManager.isValidPhoneId(i) && !TextUtils.isEmpty(str) && i < this.mLastSimState.size() && !str.equals(this.mLastSimState.get(i))) {
            this.mLastSimState.put(i, str);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(0, Integer.valueOf(i)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AppBinding {
        private int bindCount;
        private String carrierPackage;
        private String carrierServiceClass;
        private CarrierServiceConnection connection;
        private long lastBindStartMillis;
        private long lastUnbindMillis;
        private final CarrierServiceChangeCallback mCarrierServiceChangeCallback;
        private long mUnbindScheduledUptimeMillis = -1;
        private int phoneId;
        private int unbindCount;

        public AppBinding(int i) {
            this.phoneId = i;
            CarrierServiceChangeCallback carrierServiceChangeCallback = new CarrierServiceChangeCallback(i);
            this.mCarrierServiceChangeCallback = carrierServiceChangeCallback;
            TelephonyManager telephonyManager = (TelephonyManager) CarrierServiceBindHelper.this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager != null) {
                telephonyManager.registerCarrierPrivilegesCallback(i, new HandlerExecutor(CarrierServiceBindHelper.this.mHandler), carrierServiceChangeCallback);
            }
        }

        public void tearDown() {
            CarrierServiceChangeCallback carrierServiceChangeCallback;
            TelephonyManager telephonyManager = (TelephonyManager) CarrierServiceBindHelper.this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager == null || (carrierServiceChangeCallback = this.mCarrierServiceChangeCallback) == null) {
                return;
            }
            telephonyManager.unregisterCarrierPrivilegesCallback(carrierServiceChangeCallback);
        }

        public int getPhoneId() {
            return this.phoneId;
        }

        public String getPackage() {
            return this.carrierPackage;
        }

        void rebind() {
            String str;
            Bundle bundle;
            String message;
            String carrierServicePackageNameForLogicalSlot = TelephonyManager.from(CarrierServiceBindHelper.this.mContext).getCarrierServicePackageNameForLogicalSlot(this.phoneId);
            if (carrierServicePackageNameForLogicalSlot == null) {
                CarrierServiceBindHelper.this.logdWithLocalLog("No carrier app for: " + this.phoneId);
                unbind(false);
                return;
            }
            CarrierServiceBindHelper.this.logdWithLocalLog("Found carrier app: " + carrierServicePackageNameForLogicalSlot);
            if (!TextUtils.equals(this.carrierPackage, carrierServicePackageNameForLogicalSlot)) {
                unbind(true);
            }
            Intent intent = new Intent("android.service.carrier.CarrierService");
            intent.setPackage(carrierServicePackageNameForLogicalSlot);
            ResolveInfo resolveService = CarrierServiceBindHelper.this.mContext.getPackageManager().resolveService(intent, 128);
            if (resolveService != null) {
                bundle = resolveService.serviceInfo.metaData;
                ComponentInfo componentInfo = TelephonyUtils.getComponentInfo(resolveService);
                str = new ComponentName(componentInfo.packageName, componentInfo.name).getClassName();
            } else {
                str = null;
                bundle = null;
            }
            if (bundle == null || !bundle.getBoolean("android.service.carrier.LONG_LIVED_BINDING", false)) {
                CarrierServiceBindHelper.this.logdWithLocalLog("Carrier app does not want a long lived binding");
                unbind(true);
                return;
            }
            if (!TextUtils.equals(this.carrierServiceClass, str)) {
                CarrierServiceBindHelper.this.logdWithLocalLog("CarrierService class changed, unbind immediately.");
                unbind(true);
            } else if (this.connection != null) {
                CarrierServiceBindHelper.this.logdWithLocalLog("CarrierService class unchanged with connection up, cancelScheduledUnbind");
                cancelScheduledUnbind();
                return;
            }
            this.carrierPackage = carrierServicePackageNameForLogicalSlot;
            this.carrierServiceClass = str;
            CarrierServiceBindHelper.this.logdWithLocalLog("Binding to " + this.carrierPackage + " for phone " + this.phoneId);
            this.bindCount = this.bindCount + 1;
            this.lastBindStartMillis = System.currentTimeMillis();
            this.connection = new CarrierServiceConnection();
            try {
            } catch (SecurityException e) {
                message = e.getMessage();
            }
            if (CarrierServiceBindHelper.this.mContext.bindService(intent, 67112961, new Executor() { // from class: com.android.internal.telephony.CarrierServiceBindHelper$AppBinding$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    CarrierServiceBindHelper.AppBinding.this.lambda$rebind$0(runnable);
                }
            }, this.connection)) {
                CarrierServiceBindHelper.this.logdWithLocalLog("service bound");
                return;
            }
            message = "bindService returned false";
            CarrierServiceBindHelper.this.logdWithLocalLog("Unable to bind to " + this.carrierPackage + " for phone " + this.phoneId + ". Error: " + message);
            unbind(true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$rebind$0(Runnable runnable) {
            CarrierServiceBindHelper.this.mHandler.post(runnable);
        }

        void unbind(boolean z) {
            CarrierServiceConnection carrierServiceConnection = this.connection;
            if (carrierServiceConnection == null) {
                return;
            }
            if (z || !carrierServiceConnection.connected) {
                CarrierServiceBindHelper.this.logdWithLocalLog("unbind immediately or with disconnected connection");
                cancelScheduledUnbind();
                performImmediateUnbind();
            } else if (this.mUnbindScheduledUptimeMillis == -1) {
                this.mUnbindScheduledUptimeMillis = SystemClock.uptimeMillis() + 30000;
                CarrierServiceBindHelper.this.logdWithLocalLog("Scheduling unbind in 30000 millis");
                Handler handler = CarrierServiceBindHelper.this.mHandler;
                handler.sendMessageAtTime(handler.obtainMessage(1, Integer.valueOf(this.phoneId)), this.mUnbindScheduledUptimeMillis);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void performImmediateUnbind() {
            this.unbindCount++;
            this.lastUnbindMillis = System.currentTimeMillis();
            this.carrierPackage = null;
            this.carrierServiceClass = null;
            if (this.connection != null) {
                CarrierServiceBindHelper.this.mContext.unbindService(this.connection);
                CarrierServiceBindHelper.this.logdWithLocalLog("Unbinding from carrier app");
                this.connection = null;
                this.mUnbindScheduledUptimeMillis = -1L;
            }
        }

        private void cancelScheduledUnbind() {
            CarrierServiceBindHelper.this.logdWithLocalLog("cancelScheduledUnbind");
            CarrierServiceBindHelper.this.mHandler.removeMessages(1);
            this.mUnbindScheduledUptimeMillis = -1L;
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.println("Carrier app binding for phone " + this.phoneId);
            printWriter.println("  connection: " + this.connection);
            printWriter.println("  bindCount: " + this.bindCount);
            printWriter.println("  lastBindStartMillis: " + this.lastBindStartMillis);
            printWriter.println("  unbindCount: " + this.unbindCount);
            printWriter.println("  lastUnbindMillis: " + this.lastUnbindMillis);
            printWriter.println("  mUnbindScheduledUptimeMillis: " + this.mUnbindScheduledUptimeMillis);
            printWriter.println("  mCarrierServiceChangeCallback: " + this.mCarrierServiceChangeCallback);
            printWriter.println();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CarrierServiceConnection implements ServiceConnection {
        private boolean connected;

        private CarrierServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("Connected to carrier app: " + componentName.flattenToString());
            this.connected = true;
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("Disconnected from carrier app: " + componentName.flattenToString());
            this.connected = false;
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("Binding from carrier app died: " + componentName.flattenToString());
            this.connected = false;
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("Null binding from carrier app: " + componentName.flattenToString());
            this.connected = false;
        }

        public String toString() {
            return "CarrierServiceConnection[connected=" + this.connected + "]";
        }
    }

    /* loaded from: classes.dex */
    private class CarrierServicePackageMonitor extends PackageChangeReceiver {
        private CarrierServicePackageMonitor() {
        }

        public void onPackageAdded(String str) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("onPackageAdded: " + str);
            evaluateBinding(str, true);
        }

        public void onPackageRemoved(String str) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("onPackageRemoved: " + str);
            evaluateBinding(str, true);
        }

        public void onPackageUpdateFinished(String str) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("onPackageUpdateFinished: " + str);
            evaluateBinding(str, true);
        }

        public void onPackageModified(String str) {
            CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
            carrierServiceBindHelper.logdWithLocalLog("onPackageModified: " + str);
            evaluateBinding(str, false);
        }

        public void onHandleForceStop(String[] strArr, boolean z) {
            if (z) {
                CarrierServiceBindHelper.this.logdWithLocalLog("onHandleForceStop: " + Arrays.toString(strArr));
                for (String str : strArr) {
                    evaluateBinding(str, true);
                }
            }
        }

        private void evaluateBinding(String str, boolean z) {
            for (int i = 0; i < CarrierServiceBindHelper.this.mBindings.size(); i++) {
                AppBinding appBinding = CarrierServiceBindHelper.this.mBindings.get(i);
                String str2 = appBinding.getPackage();
                boolean equals = str.equals(str2);
                if (equals) {
                    CarrierServiceBindHelper carrierServiceBindHelper = CarrierServiceBindHelper.this;
                    carrierServiceBindHelper.logdWithLocalLog(str + " changed and corresponds to a phone. Rebinding.");
                }
                if (str2 == null || equals) {
                    if (z) {
                        appBinding.unbind(true);
                    }
                    appBinding.rebind();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logdWithLocalLog(String str) {
        Log.d("CarrierSvcBindHelper", str);
        this.mLocalLog.log(str);
    }

    private void logeWithLocalLog(String str) {
        Log.e("CarrierSvcBindHelper", str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("CarrierServiceBindHelper:");
        for (int i = 0; i < this.mBindings.size(); i++) {
            this.mBindings.get(i).dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("CarrierServiceBindHelperLogs=");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }
}
