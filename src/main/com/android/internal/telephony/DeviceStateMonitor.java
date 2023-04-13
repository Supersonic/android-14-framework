package com.android.internal.telephony;

import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.util.ConnectivitySettingsUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class DeviceStateMonitor extends Handler {
    @VisibleForTesting
    static final int CELL_INFO_INTERVAL_SHORT_MS = 2000;
    protected static final boolean DBG = false;
    @VisibleForTesting
    static final int EVENT_CHARGING_STATE_CHANGED = 4;
    @VisibleForTesting
    static final int EVENT_SCREEN_STATE_CHANGED = 2;
    @VisibleForTesting
    static final int EVENT_WIFI_CONNECTION_CHANGED = 7;
    private final BroadcastReceiver mBroadcastReceiver;
    private int mCellInfoMinInterval;
    private final DisplayManager.DisplayListener mDisplayListener;
    private boolean mIsAlwaysSignalStrengthReportingEnabled;
    private boolean mIsAutomotiveProjectionActive;
    private boolean mIsCharging;
    private boolean mIsLowDataExpected;
    private boolean mIsPowerSaveOn;
    private boolean mIsRadioOn;
    private boolean mIsScreenOn;
    private boolean mIsTetheringOn;
    private boolean mIsWifiConnected;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private final Phone mPhone;
    private int mUnsolicitedResponseFilter;
    private final NetworkRequest mWifiNetworkRequest;
    protected static final String TAG = DeviceStateMonitor.class.getSimpleName();
    @VisibleForTesting
    static final int CELL_INFO_INTERVAL_LONG_MS = 10000;
    private static final int[] LINK_CAPACITY_DOWNLINK_THRESHOLDS = {100, 500, 1000, GbaManager.REQUEST_TIMEOUT_MS, CELL_INFO_INTERVAL_LONG_MS, 20000, 50000, 75000, 100000, 200000, 500000, 1000000, 1500000, 2000000};
    private static final int[] LINK_CAPACITY_UPLINK_THRESHOLDS = {100, 500, 1000, GbaManager.REQUEST_TIMEOUT_MS, CELL_INFO_INTERVAL_LONG_MS, 20000, 50000, 75000, 100000, 200000, 500000};
    private final LocalLog mLocalLog = new LocalLog(64);
    private final RegistrantList mPhysicalChannelConfigRegistrants = new RegistrantList();

    private String deviceTypeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "UNKNOWN" : "LOW_DATA_EXPECTED" : "CHARGING_STATE" : "POWER_SAVE_MODE";
    }

    public DeviceStateMonitor(Phone phone) {
        NetworkRequest build = new NetworkRequest.Builder().addTransportType(1).addCapability(12).removeCapability(13).build();
        this.mWifiNetworkRequest = build;
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.internal.telephony.DeviceStateMonitor.1
            Set<Network> mWifiNetworks = new HashSet();

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                synchronized (this.mWifiNetworks) {
                    if (this.mWifiNetworks.size() == 0) {
                        DeviceStateMonitor.this.obtainMessage(7, 1, 0).sendToTarget();
                        DeviceStateMonitor.this.log("Wifi (default) connected", true);
                    }
                    this.mWifiNetworks.add(network);
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                synchronized (this.mWifiNetworks) {
                    this.mWifiNetworks.remove(network);
                    if (this.mWifiNetworks.size() == 0) {
                        DeviceStateMonitor.this.obtainMessage(7, 0, 0).sendToTarget();
                        DeviceStateMonitor.this.log("Wifi (default) disconnected", true);
                    }
                }
            }
        };
        this.mNetworkCallback = networkCallback;
        this.mCellInfoMinInterval = 2000;
        this.mUnsolicitedResponseFilter = -1;
        DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() { // from class: com.android.internal.telephony.DeviceStateMonitor.2
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                boolean isScreenOn = DeviceStateMonitor.this.isScreenOn();
                Message obtainMessage = DeviceStateMonitor.this.obtainMessage(2);
                obtainMessage.arg1 = isScreenOn ? 1 : 0;
                DeviceStateMonitor.this.sendMessage(obtainMessage);
            }
        };
        this.mDisplayListener = displayListener;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.DeviceStateMonitor.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Message obtainMessage;
                DeviceStateMonitor.this.log("received: " + intent, true);
                String action = intent.getAction();
                action.hashCode();
                int i = 0;
                char c = 65535;
                switch (action.hashCode()) {
                    case -1754841973:
                        if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -54942926:
                        if (action.equals("android.os.action.DISCHARGING")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 948344062:
                        if (action.equals("android.os.action.CHARGING")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1779291251:
                        if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("tetherArray");
                        if (stringArrayListExtra != null && stringArrayListExtra.size() > 0) {
                            i = 1;
                        }
                        DeviceStateMonitor deviceStateMonitor = DeviceStateMonitor.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Tethering ");
                        sb.append(i == 0 ? ConnectivitySettingsUtils.PRIVATE_DNS_MODE_OFF_STRING : "on");
                        deviceStateMonitor.log(sb.toString(), true);
                        obtainMessage = DeviceStateMonitor.this.obtainMessage(5);
                        obtainMessage.arg1 = i;
                        break;
                    case 1:
                        obtainMessage = DeviceStateMonitor.this.obtainMessage(4);
                        obtainMessage.arg1 = 0;
                        break;
                    case 2:
                        obtainMessage = DeviceStateMonitor.this.obtainMessage(4);
                        obtainMessage.arg1 = 1;
                        break;
                    case 3:
                        obtainMessage = DeviceStateMonitor.this.obtainMessage(3);
                        obtainMessage.arg1 = DeviceStateMonitor.this.isPowerSaveModeOn() ? 1 : 0;
                        DeviceStateMonitor deviceStateMonitor2 = DeviceStateMonitor.this;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Power Save mode ");
                        sb2.append(obtainMessage.arg1 != 1 ? ConnectivitySettingsUtils.PRIVATE_DNS_MODE_OFF_STRING : "on");
                        deviceStateMonitor2.log(sb2.toString(), true);
                        break;
                    default:
                        DeviceStateMonitor.this.log("Unexpected broadcast intent: " + intent, false);
                        return;
                }
                DeviceStateMonitor.this.sendMessage(obtainMessage);
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mPhone = phone;
        ((DisplayManager) phone.getContext().getSystemService("display")).registerDisplayListener(displayListener, null);
        this.mIsPowerSaveOn = isPowerSaveModeOn();
        this.mIsCharging = isDeviceCharging();
        this.mIsScreenOn = isScreenOn();
        this.mIsRadioOn = isRadioOn();
        this.mIsAutomotiveProjectionActive = isAutomotiveProjectionActive();
        this.mIsTetheringOn = false;
        this.mIsLowDataExpected = false;
        log("DeviceStateMonitor mIsTetheringOn=" + this.mIsTetheringOn + ", mIsScreenOn=" + this.mIsScreenOn + ", mIsCharging=" + this.mIsCharging + ", mIsPowerSaveOn=" + this.mIsPowerSaveOn + ", mIsLowDataExpected=" + this.mIsLowDataExpected + ", mIsAutomotiveProjectionActive=" + this.mIsAutomotiveProjectionActive + ", mIsWifiConnected=" + this.mIsWifiConnected + ", mIsAlwaysSignalStrengthReportingEnabled=" + this.mIsAlwaysSignalStrengthReportingEnabled + ", mIsRadioOn=" + this.mIsRadioOn, false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.os.action.CHARGING");
        intentFilter.addAction("android.os.action.DISCHARGING");
        intentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
        phone.getContext().registerReceiver(broadcastReceiver, intentFilter, null, phone);
        phone.mCi.registerForRilConnected(this, 0, null);
        phone.mCi.registerForAvailable(this, 6, null);
        phone.mCi.registerForOn(this, 9, null);
        phone.mCi.registerForOffOrNotAvailable(this, 10, null);
        ((ConnectivityManager) phone.getContext().getSystemService("connectivity")).registerNetworkCallback(build, networkCallback);
        ((UiModeManager) phone.getContext().getSystemService("uimode")).addOnProjectionStateChangedListener(1, phone.getContext().getMainExecutor(), new UiModeManager.OnProjectionStateChangedListener() { // from class: com.android.internal.telephony.DeviceStateMonitor$$ExternalSyntheticLambda0
            public final void onProjectionStateChanged(int i, Set set) {
                DeviceStateMonitor.this.lambda$new$0(i, set);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, Set set) {
        Message obtainMessage = obtainMessage(1);
        obtainMessage.arg1 = Math.min(set.size(), 1);
        sendMessage(obtainMessage);
    }

    private boolean isLowDataExpected() {
        return ((this.mIsCharging || this.mIsTetheringOn || this.mIsScreenOn) && this.mIsRadioOn) ? false : true;
    }

    @VisibleForTesting
    public int computeCellInfoMinInterval() {
        boolean z = this.mIsScreenOn;
        if (!z || this.mIsWifiConnected) {
            if (z && this.mIsCharging) {
                return 2000;
            }
            return CELL_INFO_INTERVAL_LONG_MS;
        }
        return 2000;
    }

    private boolean shouldEnableSignalStrengthReports() {
        return shouldEnableHighPowerConsumptionIndications() || (this.mIsAlwaysSignalStrengthReportingEnabled && this.mIsRadioOn);
    }

    private boolean shouldEnableFullNetworkStateReports() {
        return shouldEnableNrTrackingIndications();
    }

    private boolean shouldEnableDataCallDormancyChangedReports() {
        return shouldEnableNrTrackingIndications();
    }

    private boolean shouldEnableLinkCapacityEstimateReports() {
        return shouldEnableHighPowerConsumptionIndications();
    }

    private boolean shouldEnablePhysicalChannelConfigReports() {
        return shouldEnableNrTrackingIndications();
    }

    private boolean shouldEnableBarringInfoReports() {
        return shouldEnableHighPowerConsumptionIndications();
    }

    public boolean shouldEnableHighPowerConsumptionIndications() {
        return (this.mIsCharging || this.mIsScreenOn || this.mIsTetheringOn || this.mIsAutomotiveProjectionActive) && this.mIsRadioOn;
    }

    private boolean shouldEnableNrTrackingIndications() {
        int i = Settings.Global.getInt(this.mPhone.getContext().getContentResolver(), "nr_nsa_tracking_screen_off_mode", 0);
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return shouldEnableHighPowerConsumptionIndications();
                }
                return true;
            } else if (this.mPhone.getServiceState().getNrState() == 3) {
                return true;
            }
        }
        return shouldEnableHighPowerConsumptionIndications();
    }

    public void setAlwaysReportSignalStrength(boolean z) {
        Message obtainMessage = obtainMessage(8);
        obtainMessage.arg1 = z ? 1 : 0;
        sendMessage(obtainMessage);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        log("handleMessage msg=" + message, false);
        int i = message.what;
        switch (i) {
            case 0:
            case 6:
                onReset();
                return;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                onUpdateDeviceState(i, message.arg1 != 0);
                return;
            case 7:
                onUpdateDeviceState(i, message.arg1 != 0);
                return;
            case 9:
                onUpdateDeviceState(i, true);
                return;
            case 10:
                onUpdateDeviceState(i, false);
                return;
            default:
                throw new IllegalStateException("Unexpected message arrives. msg = " + message.what);
        }
    }

    private void onUpdateDeviceState(int i, boolean z) {
        boolean shouldEnableBarringInfoReports = shouldEnableBarringInfoReports();
        boolean shouldEnableHighPowerConsumptionIndications = shouldEnableHighPowerConsumptionIndications();
        switch (i) {
            case 1:
                if (this.mIsAutomotiveProjectionActive != z) {
                    this.mIsAutomotiveProjectionActive = z;
                    break;
                } else {
                    return;
                }
            case 2:
                if (this.mIsScreenOn != z) {
                    this.mIsScreenOn = z;
                    break;
                } else {
                    return;
                }
            case 3:
                if (this.mIsPowerSaveOn != z) {
                    this.mIsPowerSaveOn = z;
                    sendDeviceState(0, z);
                    break;
                } else {
                    return;
                }
            case 4:
                if (this.mIsCharging != z) {
                    this.mIsCharging = z;
                    sendDeviceState(1, z);
                    break;
                } else {
                    return;
                }
            case 5:
                if (this.mIsTetheringOn != z) {
                    this.mIsTetheringOn = z;
                    break;
                } else {
                    return;
                }
            case 6:
            default:
                return;
            case 7:
                if (this.mIsWifiConnected != z) {
                    this.mIsWifiConnected = z;
                    break;
                } else {
                    return;
                }
            case 8:
                if (this.mIsAlwaysSignalStrengthReportingEnabled != z) {
                    this.mIsAlwaysSignalStrengthReportingEnabled = z;
                    break;
                } else {
                    return;
                }
            case 9:
            case 10:
                if (this.mIsRadioOn != z) {
                    this.mIsRadioOn = z;
                    break;
                } else {
                    return;
                }
        }
        boolean shouldEnableHighPowerConsumptionIndications2 = shouldEnableHighPowerConsumptionIndications();
        if (shouldEnableHighPowerConsumptionIndications != shouldEnableHighPowerConsumptionIndications2) {
            this.mPhone.notifyDeviceIdleStateChanged(!shouldEnableHighPowerConsumptionIndications2);
        }
        int computeCellInfoMinInterval = computeCellInfoMinInterval();
        if (this.mCellInfoMinInterval != computeCellInfoMinInterval) {
            this.mCellInfoMinInterval = computeCellInfoMinInterval;
            setCellInfoMinInterval(computeCellInfoMinInterval);
            log("CellInfo Min Interval Updated to " + computeCellInfoMinInterval, true);
        }
        if (this.mIsLowDataExpected != isLowDataExpected()) {
            boolean z2 = !this.mIsLowDataExpected;
            this.mIsLowDataExpected = z2;
            sendDeviceState(2, z2);
        }
        int i2 = shouldEnableSignalStrengthReports() ? 33 : 32;
        if (shouldEnableFullNetworkStateReports()) {
            i2 |= 2;
        }
        if (shouldEnableDataCallDormancyChangedReports()) {
            i2 |= 4;
        }
        if (shouldEnableLinkCapacityEstimateReports()) {
            i2 |= 8;
        }
        if (shouldEnablePhysicalChannelConfigReports()) {
            i2 |= 16;
        }
        boolean shouldEnableBarringInfoReports2 = shouldEnableBarringInfoReports();
        if (shouldEnableBarringInfoReports2) {
            i2 |= 64;
        }
        int i3 = i2 & 16;
        if (i3 != (this.mUnsolicitedResponseFilter & 16)) {
            this.mPhysicalChannelConfigRegistrants.notifyResult(Boolean.valueOf(i3 != 0));
        }
        setUnsolResponseFilter(i2, false);
        if (!shouldEnableBarringInfoReports2 || shouldEnableBarringInfoReports) {
            return;
        }
        this.mPhone.mCi.getBarringInfo(null);
    }

    private void onReset() {
        log("onReset.", true);
        sendDeviceState(1, this.mIsCharging);
        sendDeviceState(2, this.mIsLowDataExpected);
        sendDeviceState(0, this.mIsPowerSaveOn);
        setUnsolResponseFilter(this.mUnsolicitedResponseFilter, true);
        setLinkCapacityReportingCriteria();
        setCellInfoMinInterval(this.mCellInfoMinInterval);
    }

    private void sendDeviceState(int i, boolean z) {
        log("send type: " + deviceTypeToString(i) + ", state=" + z, true);
        this.mPhone.mCi.sendDeviceState(i, z, null);
    }

    private void setUnsolResponseFilter(int i, boolean z) {
        if (z || i != this.mUnsolicitedResponseFilter) {
            log("old filter: " + this.mUnsolicitedResponseFilter + ", new filter: " + i, true);
            this.mPhone.mCi.setUnsolResponseFilter(i, null);
            this.mUnsolicitedResponseFilter = i;
        }
    }

    private void setLinkCapacityReportingCriteria() {
        Phone phone = this.mPhone;
        int[] iArr = LINK_CAPACITY_DOWNLINK_THRESHOLDS;
        int[] iArr2 = LINK_CAPACITY_UPLINK_THRESHOLDS;
        phone.setLinkCapacityReportingCriteria(iArr, iArr2, 1);
        this.mPhone.setLinkCapacityReportingCriteria(iArr, iArr2, 2);
        this.mPhone.setLinkCapacityReportingCriteria(iArr, iArr2, 3);
        this.mPhone.setLinkCapacityReportingCriteria(iArr, iArr2, 4);
        if (this.mPhone.getHalVersion(4).greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            this.mPhone.setLinkCapacityReportingCriteria(iArr, iArr2, 6);
        }
    }

    private void setCellInfoMinInterval(int i) {
        this.mPhone.setCellInfoMinInterval(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPowerSaveModeOn() {
        boolean isPowerSaveMode = ((PowerManager) this.mPhone.getContext().getSystemService("power")).isPowerSaveMode();
        log("isPowerSaveModeOn=" + isPowerSaveMode, true);
        return isPowerSaveMode;
    }

    private boolean isDeviceCharging() {
        boolean isCharging = ((BatteryManager) this.mPhone.getContext().getSystemService("batterymanager")).isCharging();
        log("isDeviceCharging=" + isCharging, true);
        return isCharging;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScreenOn() {
        Display[] displays = ((DisplayManager) this.mPhone.getContext().getSystemService("display")).getDisplays();
        if (displays != null) {
            for (Display display : displays) {
                if (display.getState() == 2) {
                    log("Screen on for display=" + display, true);
                    return true;
                }
            }
            log("Screens all off", true);
            return false;
        }
        log("No displays found", true);
        return false;
    }

    private boolean isRadioOn() {
        return this.mPhone.isRadioOn();
    }

    private boolean isAutomotiveProjectionActive() {
        UiModeManager uiModeManager = (UiModeManager) this.mPhone.getContext().getSystemService("uimode");
        if (uiModeManager == null) {
            return false;
        }
        boolean z = (uiModeManager.getActiveProjectionTypes() & 1) != 0;
        log("isAutomotiveProjectionActive=" + z, true);
        return z;
    }

    public void registerForPhysicalChannelConfigNotifChanged(Handler handler, int i, Object obj) {
        this.mPhysicalChannelConfigRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForPhysicalChannelConfigNotifChanged(Handler handler) {
        this.mPhysicalChannelConfigRegistrants.remove(handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str, boolean z) {
        if (z) {
            this.mLocalLog.log(str);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mIsTetheringOn=" + this.mIsTetheringOn);
        indentingPrintWriter.println("mIsScreenOn=" + this.mIsScreenOn);
        indentingPrintWriter.println("mIsCharging=" + this.mIsCharging);
        indentingPrintWriter.println("mIsPowerSaveOn=" + this.mIsPowerSaveOn);
        indentingPrintWriter.println("mIsLowDataExpected=" + this.mIsLowDataExpected);
        indentingPrintWriter.println("mIsAutomotiveProjectionActive=" + this.mIsAutomotiveProjectionActive);
        indentingPrintWriter.println("mUnsolicitedResponseFilter=" + this.mUnsolicitedResponseFilter);
        indentingPrintWriter.println("mIsWifiConnected=" + this.mIsWifiConnected);
        indentingPrintWriter.println("mIsAlwaysSignalStrengthReportingEnabled=" + this.mIsAlwaysSignalStrengthReportingEnabled);
        indentingPrintWriter.println("mIsRadioOn=" + this.mIsRadioOn);
        indentingPrintWriter.println("Local logs:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.flush();
    }
}
