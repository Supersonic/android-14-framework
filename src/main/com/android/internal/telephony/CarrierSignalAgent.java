package com.android.internal.telephony;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class CarrierSignalAgent extends Handler {
    private static final Map<String, String> COMPAT_ACTION_TO_NEW_MAP;
    private static final String LOG_TAG;
    private static final Map<String, String> NEW_ACTION_TO_COMPAT_MAP;
    private static final Set<String> VALID_CARRIER_SIGNAL_ACTIONS;
    private static final boolean VDBG;
    private boolean mDefaultNetworkAvail;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private final Phone mPhone;
    private Map<String, Set<ComponentName>> mCachedWakeSignalConfigs = new HashMap();
    private Map<String, Set<ComponentName>> mCachedNoWakeSignalConfigs = new HashMap();
    private final LocalLog mErrorLocalLog = new LocalLog(16);

    static {
        String simpleName = CarrierSignalAgent.class.getSimpleName();
        LOG_TAG = simpleName;
        VDBG = Rlog.isLoggable(simpleName, 2);
        VALID_CARRIER_SIGNAL_ACTIONS = Set.of("android.telephony.action.CARRIER_SIGNAL_PCO_VALUE", "android.telephony.action.CARRIER_SIGNAL_REDIRECTED", "android.telephony.action.CARRIER_SIGNAL_REQUEST_NETWORK_FAILED", "android.telephony.action.CARRIER_SIGNAL_RESET", "android.telephony.action.CARRIER_SIGNAL_DEFAULT_NETWORK_AVAILABLE");
        Map<String, String> of = Map.of("android.telephony.action.CARRIER_SIGNAL_PCO_VALUE", "com.android.internal.telephony.CARRIER_SIGNAL_PCO_VALUE", "android.telephony.action.CARRIER_SIGNAL_REDIRECTED", "com.android.internal.telephony.CARRIER_SIGNAL_REDIRECTED", "android.telephony.action.CARRIER_SIGNAL_REQUEST_NETWORK_FAILED", "com.android.internal.telephony.CARRIER_SIGNAL_REQUEST_NETWORK_FAILED", "android.telephony.action.CARRIER_SIGNAL_RESET", "com.android.internal.telephony.CARRIER_SIGNAL_RESET", "android.telephony.action.CARRIER_SIGNAL_DEFAULT_NETWORK_AVAILABLE", "com.android.internal.telephony.CARRIER_SIGNAL_DEFAULT_NETWORK_AVAILABLE");
        NEW_ACTION_TO_COMPAT_MAP = of;
        COMPAT_ACTION_TO_NEW_MAP = (Map) of.entrySet().stream().collect(Collectors.toMap(new Function() { // from class: com.android.internal.telephony.CarrierSignalAgent$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (String) ((Map.Entry) obj).getValue();
            }
        }, new Function() { // from class: com.android.internal.telephony.CarrierSignalAgent$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (String) ((Map.Entry) obj).getKey();
            }
        }));
    }

    public CarrierSignalAgent(Phone phone) {
        this.mPhone = phone;
        loadCarrierConfig();
        ((CarrierConfigManager) phone.getContext().getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(phone.getContext().getMainExecutor(), new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.CarrierSignalAgent$$ExternalSyntheticLambda2
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                CarrierSignalAgent.this.lambda$new$0(i, i2, i3, i4);
            }
        });
        phone.getCarrierActionAgent().registerForCarrierAction(3, this, 0, null, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        if (i == this.mPhone.getPhoneId()) {
            loadCarrierConfig();
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != 0) {
            return;
        }
        AsyncResult asyncResult = (AsyncResult) message.obj;
        if (asyncResult.exception != null) {
            String str = LOG_TAG;
            Rlog.e(str, "Register default network exception: " + asyncResult.exception);
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mPhone.getContext().getSystemService(ConnectivityManager.class);
        if (((Boolean) asyncResult.result).booleanValue()) {
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.internal.telephony.CarrierSignalAgent.1
                @Override // android.net.ConnectivityManager.NetworkCallback
                public void onAvailable(Network network) {
                    if (CarrierSignalAgent.this.mDefaultNetworkAvail) {
                        return;
                    }
                    CarrierSignalAgent carrierSignalAgent = CarrierSignalAgent.this;
                    carrierSignalAgent.log("Default network available: " + network);
                    Intent intent = new Intent("android.telephony.action.CARRIER_SIGNAL_DEFAULT_NETWORK_AVAILABLE");
                    intent.putExtra("android.telephony.extra.DEFAULT_NETWORK_AVAILABLE", true);
                    CarrierSignalAgent.this.notifyCarrierSignalReceivers(intent);
                    CarrierSignalAgent.this.mDefaultNetworkAvail = true;
                }

                @Override // android.net.ConnectivityManager.NetworkCallback
                public void onLost(Network network) {
                    CarrierSignalAgent carrierSignalAgent = CarrierSignalAgent.this;
                    carrierSignalAgent.log("Default network lost: " + network);
                    Intent intent = new Intent("android.telephony.action.CARRIER_SIGNAL_DEFAULT_NETWORK_AVAILABLE");
                    intent.putExtra("android.telephony.extra.DEFAULT_NETWORK_AVAILABLE", false);
                    CarrierSignalAgent.this.notifyCarrierSignalReceivers(intent);
                    CarrierSignalAgent.this.mDefaultNetworkAvail = false;
                }
            };
            this.mNetworkCallback = networkCallback;
            connectivityManager.registerDefaultNetworkCallback(networkCallback, this.mPhone);
            log("Register default network");
            return;
        }
        ConnectivityManager.NetworkCallback networkCallback2 = this.mNetworkCallback;
        if (networkCallback2 != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback2);
            this.mNetworkCallback = null;
            this.mDefaultNetworkAvail = false;
            log("unregister default network");
        }
    }

    private void loadCarrierConfig() {
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{"carrier_app_wake_signal_config", "carrier_app_no_wake_signal_config"});
        if (carrierConfigSubset.isEmpty()) {
            return;
        }
        synchronized (this.mCachedWakeSignalConfigs) {
            log("Loading carrier config: carrier_app_wake_signal_config");
            Map<String, Set<ComponentName>> parseAndCache = parseAndCache(carrierConfigSubset.getStringArray("carrier_app_wake_signal_config"));
            if (!this.mCachedWakeSignalConfigs.isEmpty() && !parseAndCache.equals(this.mCachedWakeSignalConfigs)) {
                if (VDBG) {
                    log("carrier config changed, reset receivers from old config");
                }
                this.mPhone.getCarrierActionAgent().sendEmptyMessage(2);
            }
            this.mCachedWakeSignalConfigs = parseAndCache;
        }
        synchronized (this.mCachedNoWakeSignalConfigs) {
            log("Loading carrier config: carrier_app_no_wake_signal_config");
            Map<String, Set<ComponentName>> parseAndCache2 = parseAndCache(carrierConfigSubset.getStringArray("carrier_app_no_wake_signal_config"));
            if (!this.mCachedNoWakeSignalConfigs.isEmpty() && !parseAndCache2.equals(this.mCachedNoWakeSignalConfigs)) {
                if (VDBG) {
                    log("carrier config changed, reset receivers from old config");
                }
                this.mPhone.getCarrierActionAgent().sendEmptyMessage(2);
            }
            this.mCachedNoWakeSignalConfigs = parseAndCache2;
        }
    }

    private Map<String, Set<ComponentName>> parseAndCache(String[] strArr) {
        String[] split;
        HashMap hashMap = new HashMap();
        if (!ArrayUtils.isEmpty(strArr)) {
            for (String str : strArr) {
                if (!TextUtils.isEmpty(str)) {
                    String[] split2 = str.trim().split("\\s*:\\s*", 2);
                    if (split2.length == 2) {
                        ComponentName unflattenFromString = ComponentName.unflattenFromString(split2[0]);
                        if (unflattenFromString == null) {
                            loge("Invalid component name: " + split2[0]);
                        } else {
                            for (String str2 : split2[1].split("\\s*,\\s*")) {
                                if (!VALID_CARRIER_SIGNAL_ACTIONS.contains(str2)) {
                                    Map<String, String> map = COMPAT_ACTION_TO_NEW_MAP;
                                    if (map.containsKey(str2)) {
                                        str2 = map.get(str2);
                                    } else {
                                        loge("Invalid signal name: " + str2);
                                    }
                                }
                                Set set = (Set) hashMap.get(str2);
                                if (set == null) {
                                    set = new HashSet();
                                    hashMap.put(str2, set);
                                }
                                set.add(unflattenFromString);
                                if (VDBG) {
                                    logv("Add config {signal: " + str2 + " componentName: " + unflattenFromString + "}");
                                }
                            }
                        }
                    } else {
                        loge("invalid config format: " + str);
                    }
                }
            }
        }
        return hashMap;
    }

    public boolean hasRegisteredReceivers(String str) {
        return this.mCachedWakeSignalConfigs.containsKey(str) || this.mCachedNoWakeSignalConfigs.containsKey(str);
    }

    private void broadcast(Intent intent, Set<ComponentName> set, boolean z) {
        PackageManager packageManager = this.mPhone.getContext().getPackageManager();
        for (ComponentName componentName : set) {
            Intent intent2 = new Intent(intent);
            if (z) {
                intent2.setComponent(componentName);
            } else {
                intent2.setPackage(componentName.getPackageName());
            }
            if (z && packageManager.queryBroadcastReceivers(intent2, InboundSmsTracker.DEST_PORT_FLAG_NO_PORT).isEmpty()) {
                loge("Carrier signal receivers are configured but unavailable: " + intent2.getComponent());
            } else if (!z && !packageManager.queryBroadcastReceivers(intent2, InboundSmsTracker.DEST_PORT_FLAG_NO_PORT).isEmpty()) {
                loge("Runtime signals shouldn't be configured in Manifest: " + intent2.getComponent());
            } else {
                SubscriptionManager.putSubscriptionIdExtra(intent2, this.mPhone.getSubId());
                intent2.addFlags(268435456);
                if (!z) {
                    intent2.setFlags(16);
                }
                Intent intent3 = null;
                try {
                    if (this.mPhone.getContext().getPackageManager().getApplicationInfo(componentName.getPackageName(), 0).targetSdkVersion <= 30) {
                        intent3 = createCompatIntent(intent2);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
                if (intent3 != null) {
                    intent2 = intent3;
                }
                try {
                    this.mPhone.getContext().sendBroadcastAsUser(intent2, UserHandle.ALL);
                    log("Sending signal " + intent2.getAction() + " to the carrier signal receiver: " + intent2.getComponent());
                } catch (ActivityNotFoundException e) {
                    loge("Send broadcast failed: " + e);
                }
            }
        }
    }

    public void notifyCarrierSignalReceivers(Intent intent) {
        synchronized (this.mCachedWakeSignalConfigs) {
            Set<ComponentName> set = this.mCachedWakeSignalConfigs.get(intent.getAction());
            if (!ArrayUtils.isEmpty(set)) {
                broadcast(intent, set, true);
            }
        }
        synchronized (this.mCachedNoWakeSignalConfigs) {
            Set<ComponentName> set2 = this.mCachedNoWakeSignalConfigs.get(intent.getAction());
            if (!ArrayUtils.isEmpty(set2)) {
                broadcast(intent, set2, false);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static Intent createCompatIntent(Intent intent) {
        char c;
        String str = NEW_ACTION_TO_COMPAT_MAP.get(intent.getAction());
        if (str == null) {
            Rlog.i(LOG_TAG, "intent action " + intent.getAction() + " does not have a compat alternative for component " + intent.getComponent());
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setAction(str);
        for (String str2 : intent.getExtras().keySet()) {
            str2.hashCode();
            switch (str2.hashCode()) {
                case -1619711317:
                    if (str2.equals("android.telephony.extra.APN_PROTOCOL")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1150077747:
                    if (str2.equals("android.telephony.extra.APN_TYPE")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -835597273:
                    if (str2.equals("android.telephony.extra.DEFAULT_NETWORK_AVAILABLE")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -263064687:
                    if (str2.equals("android.telephony.extra.PCO_ID")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 160882922:
                    if (str2.equals("android.telephony.extra.DATA_FAIL_CAUSE")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 845302377:
                    if (str2.equals("android.telephony.extra.REDIRECTION_URL")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 1367216923:
                    if (str2.equals("android.telephony.extra.PCO_VALUE")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    int intExtra = intent.getIntExtra("android.telephony.extra.APN_PROTOCOL", -1);
                    intent2.putExtra("apnProtoInt", intExtra);
                    intent2.putExtra("apnProto", ApnSetting.getProtocolStringFromInt(intExtra));
                    break;
                case 1:
                    int intExtra2 = intent.getIntExtra("android.telephony.extra.APN_TYPE", 17);
                    intent2.putExtra("apnTypeInt", intExtra2);
                    intent2.putExtra("apnType", ApnSetting.getApnTypesStringFromBitmask(intExtra2));
                    break;
                case 2:
                    intent2.putExtra("defaultNetworkAvailable", intent.getBooleanExtra("android.telephony.extra.DEFAULT_NETWORK_AVAILABLE", false));
                    break;
                case 3:
                    intent2.putExtra("pcoId", intent.getIntExtra("android.telephony.extra.PCO_ID", -1));
                    break;
                case 4:
                    intent2.putExtra("errorCode", intent.getIntExtra("android.telephony.extra.DATA_FAIL_CAUSE", -1));
                    break;
                case 5:
                    intent2.putExtra("redirectionUrl", intent.getStringExtra("android.telephony.extra.REDIRECTION_URL"));
                    break;
                case 6:
                    intent2.putExtra("pcoValue", intent.getByteArrayExtra("android.telephony.extra.PCO_VALUE"));
                    break;
            }
        }
        return intent2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        String str2 = LOG_TAG;
        Rlog.d(str2, "[" + this.mPhone.getPhoneId() + "]" + str);
    }

    private void loge(String str) {
        this.mErrorLocalLog.log(str);
        String str2 = LOG_TAG;
        Rlog.e(str2, "[" + this.mPhone.getPhoneId() + "]" + str);
    }

    private void logv(String str) {
        String str2 = LOG_TAG;
        Rlog.v(str2, "[" + this.mPhone.getPhoneId() + "]" + str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        printWriter.println("mCachedWakeSignalConfigs:");
        indentingPrintWriter.increaseIndent();
        for (Map.Entry<String, Set<ComponentName>> entry : this.mCachedWakeSignalConfigs.entrySet()) {
            printWriter.println("signal: " + entry.getKey() + " componentName list: " + entry.getValue());
        }
        indentingPrintWriter.decreaseIndent();
        printWriter.println("mCachedNoWakeSignalConfigs:");
        indentingPrintWriter.increaseIndent();
        for (Map.Entry<String, Set<ComponentName>> entry2 : this.mCachedNoWakeSignalConfigs.entrySet()) {
            printWriter.println("signal: " + entry2.getKey() + " componentName list: " + entry2.getValue());
        }
        indentingPrintWriter.decreaseIndent();
        printWriter.println("mDefaultNetworkAvail: " + this.mDefaultNetworkAvail);
        printWriter.println("error log:");
        indentingPrintWriter.increaseIndent();
        this.mErrorLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
    }
}
