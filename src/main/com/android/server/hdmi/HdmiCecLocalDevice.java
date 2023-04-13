package com.android.server.hdmi;

import android.hardware.hdmi.DeviceFeatures;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.hdmi.HdmiCecController;
import com.android.server.hdmi.HdmiControlService;
import com.android.server.location.gnss.hal.GnssNative;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public abstract class HdmiCecLocalDevice extends HdmiLocalDevice {
    @VisibleForTesting
    final ArrayList<HdmiCecFeatureAction> mActions;
    @GuardedBy({"mLock"})
    public int mActiveRoutingPath;
    public final ArrayBlockingQueue<HdmiCecController.Dumpable> mActiveSourceHistory;
    public final HdmiCecMessageCache mCecMessageCache;
    @GuardedBy({"mLock"})
    public HdmiDeviceInfo mDeviceInfo;
    public final Handler mHandler;
    public int mLastKeyRepeatCount;
    public int mLastKeycode;
    public PendingActionClearedCallback mPendingActionClearedCallback;
    public int mPreferredAddress;
    public HdmiCecStandbyModeHandler mStandbyHandler;

    /* loaded from: classes.dex */
    public interface PendingActionClearedCallback {
        void onCleared(HdmiCecLocalDevice hdmiCecLocalDevice);
    }

    public boolean canGoToStandby() {
        return true;
    }

    public abstract int getPreferredAddress();

    public abstract List<Integer> getRcFeatures();

    public abstract int getRcProfile();

    public int handleActiveSource(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleGiveAudioStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleGiveSystemAudioModeStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleImageViewOn(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleInactiveSource(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleInitiateArc(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleMenuStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRecordStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRecordTvScreen(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleReportArcInitiate(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleReportArcTermination(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleReportAudioStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleReportPowerStatus(HdmiCecMessage hdmiCecMessage) {
        return -1;
    }

    public int handleReportShortAudioDescriptor(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRequestActiveSource(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRequestArcInitiate(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRequestArcTermination(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRequestShortAudioDescriptor(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRoutingChange(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleRoutingInformation(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleSetAudioVolumeLevel(SetAudioVolumeLevelMessage setAudioVolumeLevelMessage) {
        return -2;
    }

    public int handleSetOsdName(HdmiCecMessage hdmiCecMessage) {
        return -1;
    }

    public int handleSetStreamPath(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleSetSystemAudioMode(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleSystemAudioModeRequest(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleSystemAudioModeStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleTerminateArc(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleTextViewOn(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleTimerClearedStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public int handleTimerStatus(HdmiCecMessage hdmiCecMessage) {
        return -2;
    }

    public boolean isInputReady(int i) {
        return true;
    }

    public abstract void onAddressAllocated(int i, int i2);

    public void onHotplug(int i, boolean z) {
    }

    public void onInitializeCecComplete(int i) {
    }

    public void onStandby(boolean z, int i) {
    }

    public void preprocessBufferedMessages(List<HdmiCecMessage> list) {
    }

    public void sendStandby(int i) {
    }

    public abstract void setPreferredAddress(int i);

    /* loaded from: classes.dex */
    public static class ActiveSource {
        public int logicalAddress;
        public int physicalAddress;

        public ActiveSource() {
            invalidate();
        }

        public ActiveSource(int i, int i2) {
            this.logicalAddress = i;
            this.physicalAddress = i2;
        }

        /* renamed from: of */
        public static ActiveSource m50of(ActiveSource activeSource) {
            return new ActiveSource(activeSource.logicalAddress, activeSource.physicalAddress);
        }

        /* renamed from: of */
        public static ActiveSource m51of(int i, int i2) {
            return new ActiveSource(i, i2);
        }

        public boolean isValid() {
            return HdmiUtils.isValidAddress(this.logicalAddress);
        }

        public void invalidate() {
            this.logicalAddress = -1;
            this.physicalAddress = GnssNative.GNSS_AIDING_TYPE_ALL;
        }

        public boolean equals(int i, int i2) {
            return this.logicalAddress == i && this.physicalAddress == i2;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ActiveSource) {
                ActiveSource activeSource = (ActiveSource) obj;
                return activeSource.logicalAddress == this.logicalAddress && activeSource.physicalAddress == this.physicalAddress;
            }
            return false;
        }

        public int hashCode() {
            return (this.logicalAddress * 29) + this.physicalAddress;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            int i = this.logicalAddress;
            String format = i == -1 ? "invalid" : String.format("0x%02x", Integer.valueOf(i));
            sb.append("(");
            sb.append(format);
            int i2 = this.physicalAddress;
            String format2 = i2 != 65535 ? String.format("0x%04x", Integer.valueOf(i2)) : "invalid";
            sb.append(", ");
            sb.append(format2);
            sb.append(")");
            return sb.toString();
        }
    }

    public HdmiCecLocalDevice(HdmiControlService hdmiControlService, int i) {
        super(hdmiControlService, i);
        this.mLastKeycode = -1;
        this.mLastKeyRepeatCount = 0;
        this.mActiveSourceHistory = new ArrayBlockingQueue<>(10);
        this.mCecMessageCache = new HdmiCecMessageCache();
        this.mActions = new ArrayList<>();
        this.mHandler = new Handler() { // from class: com.android.server.hdmi.HdmiCecLocalDevice.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i2 = message.what;
                if (i2 == 1) {
                    HdmiCecLocalDevice.this.handleDisableDeviceTimeout();
                } else if (i2 != 2) {
                } else {
                    HdmiCecLocalDevice.this.handleUserControlReleased();
                }
            }
        };
    }

    public static HdmiCecLocalDevice create(HdmiControlService hdmiControlService, int i) {
        if (i != 0) {
            if (i != 4) {
                if (i != 5) {
                    return null;
                }
                return new HdmiCecLocalDeviceAudioSystem(hdmiControlService);
            }
            return new HdmiCecLocalDevicePlayback(hdmiControlService);
        }
        return new HdmiCecLocalDeviceTv(hdmiControlService);
    }

    public void init() {
        assertRunOnServiceThread();
        this.mPreferredAddress = getPreferredAddress();
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
            handleDisableDeviceTimeout();
        }
        this.mPendingActionClearedCallback = null;
    }

    @VisibleForTesting
    public int dispatchMessage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int destination = hdmiCecMessage.getDestination();
        if (destination == this.mDeviceInfo.getLogicalAddress() || destination == 15) {
            if (this.mService.isPowerStandby() && !this.mService.isWakeUpMessageReceived() && this.mStandbyHandler.handleCommand(hdmiCecMessage)) {
                return -1;
            }
            this.mCecMessageCache.cacheMessage(hdmiCecMessage);
            return onMessage(hdmiCecMessage);
        }
        return -2;
    }

    @VisibleForTesting
    public boolean isAlreadyActiveSource(HdmiDeviceInfo hdmiDeviceInfo, int i, IHdmiControlCallback iHdmiControlCallback) {
        ActiveSource activeSource = getActiveSource();
        if (hdmiDeviceInfo.getDevicePowerStatus() == 0 && activeSource.isValid() && i == activeSource.logicalAddress) {
            invokeCallback(iHdmiControlCallback, 0);
            return true;
        }
        return false;
    }

    public void clearDeviceInfoList() {
        assertRunOnServiceThread();
        this.mService.getHdmiCecNetwork().clearDeviceList();
    }

    public final int onMessage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (dispatchMessageToAction(hdmiCecMessage)) {
            return -1;
        }
        if (hdmiCecMessage instanceof SetAudioVolumeLevelMessage) {
            return handleSetAudioVolumeLevel((SetAudioVolumeLevelMessage) hdmiCecMessage);
        }
        int opcode = hdmiCecMessage.getOpcode();
        if (opcode != 4) {
            if (opcode != 10) {
                if (opcode != 13) {
                    if (opcode != 15) {
                        if (opcode != 50) {
                            if (opcode != 122) {
                                if (opcode != 137) {
                                    if (opcode != 53) {
                                        if (opcode != 54) {
                                            if (opcode != 125) {
                                                if (opcode == 126) {
                                                    return handleSystemAudioModeStatus(hdmiCecMessage);
                                                }
                                                switch (opcode) {
                                                    case 67:
                                                        return handleTimerClearedStatus(hdmiCecMessage);
                                                    case 68:
                                                        return handleUserControlPressed(hdmiCecMessage);
                                                    case 69:
                                                        return handleUserControlReleased();
                                                    case 70:
                                                        return handleGiveOsdName(hdmiCecMessage);
                                                    case 71:
                                                        return handleSetOsdName(hdmiCecMessage);
                                                    default:
                                                        switch (opcode) {
                                                            case 112:
                                                                return handleSystemAudioModeRequest(hdmiCecMessage);
                                                            case 113:
                                                                return handleGiveAudioStatus(hdmiCecMessage);
                                                            case 114:
                                                                return handleSetSystemAudioMode(hdmiCecMessage);
                                                            default:
                                                                switch (opcode) {
                                                                    case 128:
                                                                        return handleRoutingChange(hdmiCecMessage);
                                                                    case 129:
                                                                        return handleRoutingInformation(hdmiCecMessage);
                                                                    case 130:
                                                                        return handleActiveSource(hdmiCecMessage);
                                                                    case 131:
                                                                        return handleGivePhysicalAddress(hdmiCecMessage);
                                                                    case 132:
                                                                        return handleReportPhysicalAddress(hdmiCecMessage);
                                                                    case 133:
                                                                        return handleRequestActiveSource(hdmiCecMessage);
                                                                    case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE /* 134 */:
                                                                        return handleSetStreamPath(hdmiCecMessage);
                                                                    default:
                                                                        switch (opcode) {
                                                                            case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES /* 140 */:
                                                                                return handleGiveDeviceVendorId(hdmiCecMessage);
                                                                            case FrameworkStatsLog.f364xfb4318d7 /* 141 */:
                                                                                return handleMenuRequest(hdmiCecMessage);
                                                                            case 142:
                                                                                return handleMenuStatus(hdmiCecMessage);
                                                                            case 143:
                                                                                return handleGiveDevicePowerStatus(hdmiCecMessage);
                                                                            case 144:
                                                                                return handleReportPowerStatus(hdmiCecMessage);
                                                                            case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__CAN_INTERACT_ACROSS_PROFILES_TRUE /* 145 */:
                                                                                return handleGetMenuLanguage(hdmiCecMessage);
                                                                            default:
                                                                                switch (opcode) {
                                                                                    case FrameworkStatsLog.f421x729e24be /* 157 */:
                                                                                        return handleInactiveSource(hdmiCecMessage);
                                                                                    case FrameworkStatsLog.f419x663f9746 /* 158 */:
                                                                                        return handleCecVersion();
                                                                                    case FrameworkStatsLog.f420x89db317 /* 159 */:
                                                                                        return handleGetCecVersion(hdmiCecMessage);
                                                                                    case FrameworkStatsLog.f418x97ec91aa /* 160 */:
                                                                                        return handleVendorCommandWithId(hdmiCecMessage);
                                                                                    default:
                                                                                        switch (opcode) {
                                                                                            case FrameworkStatsLog.f380x2165d62a /* 163 */:
                                                                                                return handleReportShortAudioDescriptor(hdmiCecMessage);
                                                                                            case FrameworkStatsLog.f376xd07885aa /* 164 */:
                                                                                                return handleRequestShortAudioDescriptor(hdmiCecMessage);
                                                                                            case FrameworkStatsLog.f383xde3a78eb /* 165 */:
                                                                                                return handleGiveFeatures(hdmiCecMessage);
                                                                                            default:
                                                                                                switch (opcode) {
                                                                                                    case FrameworkStatsLog.f392xcd34d435 /* 192 */:
                                                                                                        return handleInitiateArc(hdmiCecMessage);
                                                                                                    case FrameworkStatsLog.f390xde8506f2 /* 193 */:
                                                                                                        return handleReportArcInitiate(hdmiCecMessage);
                                                                                                    case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_ERROR /* 194 */:
                                                                                                        return handleReportArcTermination(hdmiCecMessage);
                                                                                                    case FrameworkStatsLog.f411x277c884 /* 195 */:
                                                                                                        return handleRequestArcInitiate(hdmiCecMessage);
                                                                                                    case FrameworkStatsLog.f410xb766d392 /* 196 */:
                                                                                                        return handleRequestArcTermination(hdmiCecMessage);
                                                                                                    case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_PARAM /* 197 */:
                                                                                                        return handleTerminateArc(hdmiCecMessage);
                                                                                                    default:
                                                                                                        return -2;
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                            }
                                            return handleGiveSystemAudioModeStatus(hdmiCecMessage);
                                        }
                                        return handleStandby(hdmiCecMessage);
                                    }
                                    return handleTimerStatus(hdmiCecMessage);
                                }
                                return handleVendorCommand(hdmiCecMessage);
                            }
                            return handleReportAudioStatus(hdmiCecMessage);
                        }
                        return handleSetMenuLanguage(hdmiCecMessage);
                    }
                    return handleRecordTvScreen(hdmiCecMessage);
                }
                return handleTextViewOn(hdmiCecMessage);
            }
            return handleRecordStatus(hdmiCecMessage);
        }
        return handleImageViewOn(hdmiCecMessage);
    }

    public final boolean dispatchMessageToAction(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        Iterator it = new ArrayList(this.mActions).iterator();
        while (true) {
            boolean z = false;
            while (it.hasNext()) {
                boolean processCommand = ((HdmiCecFeatureAction) it.next()).processCommand(hdmiCecMessage);
                if (z || processCommand) {
                    z = true;
                }
            }
            return z;
        }
    }

    public int handleGivePhysicalAddress(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int physicalAddress = this.mService.getPhysicalAddress();
        if (physicalAddress == 65535) {
            this.mService.maySendFeatureAbortCommand(hdmiCecMessage, 5);
            return -1;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(this.mDeviceInfo.getLogicalAddress(), physicalAddress, this.mDeviceType));
        return -1;
    }

    public int handleGiveDeviceVendorId(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int vendorId = this.mService.getVendorId();
        if (vendorId == 1) {
            this.mService.maySendFeatureAbortCommand(hdmiCecMessage, 5);
            return -1;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildDeviceVendorIdCommand(this.mDeviceInfo.getLogicalAddress(), vendorId));
        return -1;
    }

    public int handleGetCecVersion(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildCecVersion(hdmiCecMessage.getDestination(), hdmiCecMessage.getSource(), this.mService.getCecVersion()));
        return -1;
    }

    public int handleCecVersion() {
        assertRunOnServiceThread();
        return -1;
    }

    public int handleGetMenuLanguage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        Slog.w("HdmiCecLocalDevice", "Only TV can handle <Get Menu Language>:" + hdmiCecMessage.toString());
        return -2;
    }

    public int handleSetMenuLanguage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        Slog.w("HdmiCecLocalDevice", "Only Playback device can handle <Set Menu Language>:" + hdmiCecMessage.toString());
        return -2;
    }

    public int handleGiveOsdName(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        buildAndSendSetOsdName(hdmiCecMessage.getSource());
        return -1;
    }

    public void buildAndSendSetOsdName(int i) {
        final HdmiCecMessage buildSetOsdNameCommand = HdmiCecMessageBuilder.buildSetOsdNameCommand(this.mDeviceInfo.getLogicalAddress(), i, this.mDeviceInfo.getDisplayName());
        if (buildSetOsdNameCommand != null) {
            this.mService.sendCecCommand(buildSetOsdNameCommand, new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDevice.2
                @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
                public void onSendCompleted(int i2) {
                    if (i2 != 0) {
                        HdmiLogger.debug("Failed to send cec command " + buildSetOsdNameCommand, new Object[0]);
                    }
                }
            });
            return;
        }
        Slog.w("HdmiCecLocalDevice", "Failed to build <Get Osd Name>:" + this.mDeviceInfo.getDisplayName());
    }

    public int handleReportPhysicalAddress(HdmiCecMessage hdmiCecMessage) {
        int source = hdmiCecMessage.getSource();
        if (hasAction(DeviceDiscoveryAction.class)) {
            Slog.i("HdmiCecLocalDevice", "Ignored while Device Discovery Action is in progress: " + hdmiCecMessage);
            return -1;
        }
        HdmiDeviceInfo cecDeviceInfo = this.mService.getHdmiCecNetwork().getCecDeviceInfo(source);
        if (cecDeviceInfo != null && cecDeviceInfo.getDisplayName().equals(HdmiUtils.getDefaultDeviceName(source))) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildGiveOsdNameCommand(this.mDeviceInfo.getLogicalAddress(), source));
        }
        return -1;
    }

    public DeviceFeatures computeDeviceFeatures() {
        return DeviceFeatures.NO_FEATURES_SUPPORTED;
    }

    public final void updateDeviceFeatures() {
        setDeviceInfo(getDeviceInfo().toBuilder().setDeviceFeatures(computeDeviceFeatures()).build());
    }

    public final DeviceFeatures getDeviceFeatures() {
        updateDeviceFeatures();
        return getDeviceInfo().getDeviceFeatures();
    }

    public int handleGiveFeatures(HdmiCecMessage hdmiCecMessage) {
        if (this.mService.getCecVersion() < 6) {
            return 0;
        }
        reportFeatures();
        return -1;
    }

    public void reportFeatures() {
        int logicalAddress;
        ArrayList arrayList = new ArrayList();
        for (HdmiCecLocalDevice hdmiCecLocalDevice : this.mService.getAllCecLocalDevices()) {
            arrayList.add(Integer.valueOf(hdmiCecLocalDevice.mDeviceType));
        }
        int rcProfile = getRcProfile();
        List<Integer> rcFeatures = getRcFeatures();
        DeviceFeatures deviceFeatures = getDeviceFeatures();
        synchronized (this.mLock) {
            logicalAddress = this.mDeviceInfo.getLogicalAddress();
        }
        HdmiControlService hdmiControlService = this.mService;
        hdmiControlService.sendCecCommand(ReportFeaturesMessage.build(logicalAddress, hdmiControlService.getCecVersion(), arrayList, rcProfile, rcFeatures, deviceFeatures));
    }

    public int handleStandby(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (this.mService.isCecControlEnabled() && !this.mService.isProhibitMode() && this.mService.isPowerOnOrTransient()) {
            this.mService.standby();
            return -1;
        }
        return 1;
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0071  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0080  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int handleUserControlPressed(HdmiCecMessage hdmiCecMessage) {
        int i;
        assertRunOnServiceThread();
        this.mHandler.removeMessages(2);
        if (this.mService.isPowerOnOrTransient() && isPowerOffOrToggleCommand(hdmiCecMessage)) {
            this.mService.standby();
            return -1;
        } else if (this.mService.isPowerStandbyOrTransient() && isPowerOnOrToggleCommand(hdmiCecMessage)) {
            this.mService.wakeUp();
            return -1;
        } else if (this.mService.getHdmiCecVolumeControl() == 0 && isVolumeOrMuteCommand(hdmiCecMessage)) {
            return 4;
        } else {
            if (isPowerOffOrToggleCommand(hdmiCecMessage) || isPowerOnOrToggleCommand(hdmiCecMessage)) {
                return -1;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            byte[] params = hdmiCecMessage.getParams();
            int cecKeycodeAndParamsToAndroidKey = HdmiCecKeycode.cecKeycodeAndParamsToAndroidKey(params);
            int i2 = this.mLastKeycode;
            if (i2 != -1) {
                if (cecKeycodeAndParamsToAndroidKey == i2) {
                    i = this.mLastKeyRepeatCount + 1;
                    this.mLastKeycode = cecKeycodeAndParamsToAndroidKey;
                    this.mLastKeyRepeatCount = i;
                    if (cecKeycodeAndParamsToAndroidKey == -1) {
                        injectKeyEvent(uptimeMillis, 0, cecKeycodeAndParamsToAndroidKey, i);
                        Handler handler = this.mHandler;
                        handler.sendMessageDelayed(Message.obtain(handler, 2), 550L);
                        return -1;
                    } else if (params.length > 0) {
                        return handleUnmappedCecKeycode(params[0]);
                    } else {
                        return 3;
                    }
                }
                injectKeyEvent(uptimeMillis, 1, i2, 0);
            }
            i = 0;
            this.mLastKeycode = cecKeycodeAndParamsToAndroidKey;
            this.mLastKeyRepeatCount = i;
            if (cecKeycodeAndParamsToAndroidKey == -1) {
            }
        }
    }

    public int handleUnmappedCecKeycode(int i) {
        if (i == 101) {
            this.mService.getAudioManager().adjustStreamVolume(3, -100, 1);
            return -1;
        } else if (i == 102) {
            this.mService.getAudioManager().adjustStreamVolume(3, 100, 1);
            return -1;
        } else {
            return 3;
        }
    }

    public int handleUserControlReleased() {
        assertRunOnServiceThread();
        this.mHandler.removeMessages(2);
        this.mLastKeyRepeatCount = 0;
        if (this.mLastKeycode != -1) {
            injectKeyEvent(SystemClock.uptimeMillis(), 1, this.mLastKeycode, 0);
            this.mLastKeycode = -1;
        }
        return -1;
    }

    public static void injectKeyEvent(long j, int i, int i2, int i3) {
        KeyEvent obtain = KeyEvent.obtain(j, j, i, i2, i3, 0, -1, 0, 8, 33554433, null);
        InputManager.getInstance().injectInputEvent(obtain, 0);
        obtain.recycle();
    }

    public static boolean isPowerOnOrToggleCommand(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        if (hdmiCecMessage.getOpcode() == 68) {
            byte b = params[0];
            return b == 64 || b == 109 || b == 107;
        }
        return false;
    }

    public static boolean isPowerOffOrToggleCommand(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        if (hdmiCecMessage.getOpcode() == 68) {
            byte b = params[0];
            return b == 108 || b == 107;
        }
        return false;
    }

    public static boolean isVolumeOrMuteCommand(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        if (hdmiCecMessage.getOpcode() == 68) {
            byte b = params[0];
            return b == 66 || b == 65 || b == 67 || b == 101 || b == 102;
        }
        return false;
    }

    public int handleGiveDevicePowerStatus(HdmiCecMessage hdmiCecMessage) {
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPowerStatus(this.mDeviceInfo.getLogicalAddress(), hdmiCecMessage.getSource(), this.mService.getPowerStatus()));
        return -1;
    }

    public int handleMenuRequest(HdmiCecMessage hdmiCecMessage) {
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportMenuStatus(this.mDeviceInfo.getLogicalAddress(), hdmiCecMessage.getSource(), 0));
        return -1;
    }

    public int handleVendorCommand(HdmiCecMessage hdmiCecMessage) {
        return !this.mService.invokeVendorCommandListenersOnReceived(this.mDeviceType, hdmiCecMessage.getSource(), hdmiCecMessage.getDestination(), hdmiCecMessage.getParams(), false) ? 4 : -1;
    }

    public int handleVendorCommandWithId(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        HdmiUtils.threeBytesToInt(params);
        if (hdmiCecMessage.getDestination() != 15 && hdmiCecMessage.getSource() != 15) {
            return !this.mService.invokeVendorCommandListenersOnReceived(this.mDeviceType, hdmiCecMessage.getSource(), hdmiCecMessage.getDestination(), params, true) ? 4 : -1;
        }
        Slog.v("HdmiCecLocalDevice", "Wrong broadcast vendor command. Ignoring");
        return -1;
    }

    public final void handleAddressAllocated(int i, List<HdmiCecMessage> list, int i2) {
        assertRunOnServiceThread();
        preprocessBufferedMessages(list);
        this.mPreferredAddress = i;
        updateDeviceFeatures();
        if (this.mService.getCecVersion() >= 6) {
            reportFeatures();
        }
        onAddressAllocated(i, i2);
        setPreferredAddress(i);
    }

    public int getType() {
        return this.mDeviceType;
    }

    public HdmiDeviceInfo getDeviceInfo() {
        HdmiDeviceInfo hdmiDeviceInfo;
        synchronized (this.mLock) {
            hdmiDeviceInfo = this.mDeviceInfo;
        }
        return hdmiDeviceInfo;
    }

    public void setDeviceInfo(HdmiDeviceInfo hdmiDeviceInfo) {
        synchronized (this.mLock) {
            this.mDeviceInfo = hdmiDeviceInfo;
        }
    }

    public boolean isAddressOf(int i) {
        assertRunOnServiceThread();
        return i == this.mDeviceInfo.getLogicalAddress();
    }

    public void addAndStartAction(HdmiCecFeatureAction hdmiCecFeatureAction) {
        assertRunOnServiceThread();
        this.mActions.add(hdmiCecFeatureAction);
        if (this.mService.isPowerStandby() || !this.mService.isAddressAllocated()) {
            Slog.i("HdmiCecLocalDevice", "Not ready to start action. Queued for deferred start:" + hdmiCecFeatureAction);
            return;
        }
        hdmiCecFeatureAction.start();
    }

    public void addAvcAudioStatusAction(int i) {
        if (hasAction(AbsoluteVolumeAudioStatusAction.class)) {
            return;
        }
        addAndStartAction(new AbsoluteVolumeAudioStatusAction(this, i));
    }

    public void removeAvcAudioStatusAction() {
        removeAction(AbsoluteVolumeAudioStatusAction.class);
    }

    public void updateAvcVolume(int i) {
        for (AbsoluteVolumeAudioStatusAction absoluteVolumeAudioStatusAction : getActions(AbsoluteVolumeAudioStatusAction.class)) {
            absoluteVolumeAudioStatusAction.updateVolume(i);
        }
    }

    public void queryAvcSupport(final int i) {
        assertRunOnServiceThread();
        if (this.mService.getCecVersion() >= 6) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildGiveFeatures(getDeviceInfo().getLogicalAddress(), i));
        }
        if (getActions(SetAudioVolumeLevelDiscoveryAction.class).stream().noneMatch(new Predicate() { // from class: com.android.server.hdmi.HdmiCecLocalDevice$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$queryAvcSupport$0;
                lambda$queryAvcSupport$0 = HdmiCecLocalDevice.lambda$queryAvcSupport$0(i, (SetAudioVolumeLevelDiscoveryAction) obj);
                return lambda$queryAvcSupport$0;
            }
        })) {
            addAndStartAction(new SetAudioVolumeLevelDiscoveryAction(this, i, new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiCecLocalDevice.3
                public void onComplete(int i2) {
                    if (i2 == 0) {
                        HdmiCecLocalDevice.this.getService().checkAndUpdateAbsoluteVolumeControlState();
                    }
                }
            }));
        }
    }

    public static /* synthetic */ boolean lambda$queryAvcSupport$0(int i, SetAudioVolumeLevelDiscoveryAction setAudioVolumeLevelDiscoveryAction) {
        return setAudioVolumeLevelDiscoveryAction.getTargetAddress() == i;
    }

    public void startQueuedActions() {
        assertRunOnServiceThread();
        Iterator it = new ArrayList(this.mActions).iterator();
        while (it.hasNext()) {
            HdmiCecFeatureAction hdmiCecFeatureAction = (HdmiCecFeatureAction) it.next();
            if (!hdmiCecFeatureAction.started()) {
                Slog.i("HdmiCecLocalDevice", "Starting queued action:" + hdmiCecFeatureAction);
                hdmiCecFeatureAction.start();
            }
        }
    }

    public <T extends HdmiCecFeatureAction> boolean hasAction(Class<T> cls) {
        assertRunOnServiceThread();
        Iterator<HdmiCecFeatureAction> it = this.mActions.iterator();
        while (it.hasNext()) {
            if (it.next().getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public <T extends HdmiCecFeatureAction> List<T> getActions(Class<T> cls) {
        assertRunOnServiceThread();
        ArrayList emptyList = Collections.emptyList();
        Iterator<HdmiCecFeatureAction> it = this.mActions.iterator();
        while (it.hasNext()) {
            HdmiCecFeatureAction next = it.next();
            if (next.getClass().equals(cls)) {
                if (emptyList.isEmpty()) {
                    emptyList = new ArrayList();
                }
                emptyList.add(next);
            }
        }
        return emptyList;
    }

    public void removeAction(HdmiCecFeatureAction hdmiCecFeatureAction) {
        assertRunOnServiceThread();
        hdmiCecFeatureAction.finish(false);
        this.mActions.remove(hdmiCecFeatureAction);
        checkIfPendingActionsCleared();
    }

    public <T extends HdmiCecFeatureAction> void removeAction(Class<T> cls) {
        assertRunOnServiceThread();
        removeActionExcept(cls, null);
    }

    public <T extends HdmiCecFeatureAction> void removeActionExcept(Class<T> cls, HdmiCecFeatureAction hdmiCecFeatureAction) {
        assertRunOnServiceThread();
        Iterator<HdmiCecFeatureAction> it = this.mActions.iterator();
        while (it.hasNext()) {
            HdmiCecFeatureAction next = it.next();
            if (next != hdmiCecFeatureAction && next.getClass().equals(cls)) {
                next.finish(false);
                it.remove();
            }
        }
        checkIfPendingActionsCleared();
    }

    public void checkIfPendingActionsCleared() {
        PendingActionClearedCallback pendingActionClearedCallback;
        if (!this.mActions.isEmpty() || (pendingActionClearedCallback = this.mPendingActionClearedCallback) == null) {
            return;
        }
        this.mPendingActionClearedCallback = null;
        pendingActionClearedCallback.onCleared(this);
    }

    public void assertRunOnServiceThread() {
        if (Looper.myLooper() != this.mService.getServiceLooper()) {
            throw new IllegalStateException("Should run on service thread.");
        }
    }

    public final HdmiControlService getService() {
        return this.mService;
    }

    public final boolean isConnectedToArcPort(int i) {
        assertRunOnServiceThread();
        return this.mService.isConnectedToArcPort(i);
    }

    public ActiveSource getActiveSource() {
        return this.mService.getLocalActiveSource();
    }

    public void setActiveSource(ActiveSource activeSource, String str) {
        setActiveSource(activeSource.logicalAddress, activeSource.physicalAddress, str);
    }

    public void setActiveSource(HdmiDeviceInfo hdmiDeviceInfo, String str) {
        setActiveSource(hdmiDeviceInfo.getLogicalAddress(), hdmiDeviceInfo.getPhysicalAddress(), str);
    }

    public void setActiveSource(int i, int i2, String str) {
        this.mService.setActiveSource(i, i2, str);
        this.mService.setLastInputForMhl(-1);
    }

    public int getActivePath() {
        int i;
        synchronized (this.mLock) {
            i = this.mActiveRoutingPath;
        }
        return i;
    }

    public void setActivePath(int i) {
        synchronized (this.mLock) {
            this.mActiveRoutingPath = i;
        }
        this.mService.setActivePortId(pathToPortId(i));
    }

    public int getActivePortId() {
        int pathToPortId;
        synchronized (this.mLock) {
            pathToPortId = this.mService.pathToPortId(this.mActiveRoutingPath);
        }
        return pathToPortId;
    }

    public void setActivePortId(int i) {
        setActivePath(this.mService.portIdToPath(i));
    }

    public int getPortId(int i) {
        return this.mService.pathToPortId(i);
    }

    public HdmiCecMessageCache getCecMessageCache() {
        assertRunOnServiceThread();
        return this.mCecMessageCache;
    }

    public int pathToPortId(int i) {
        assertRunOnServiceThread();
        return this.mService.pathToPortId(i);
    }

    public void disableDevice(boolean z, final PendingActionClearedCallback pendingActionClearedCallback) {
        removeAction(AbsoluteVolumeAudioStatusAction.class);
        removeAction(SetAudioVolumeLevelDiscoveryAction.class);
        removeAction(ActiveSourceAction.class);
        this.mPendingActionClearedCallback = new PendingActionClearedCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDevice.4
            @Override // com.android.server.hdmi.HdmiCecLocalDevice.PendingActionClearedCallback
            public void onCleared(HdmiCecLocalDevice hdmiCecLocalDevice) {
                HdmiCecLocalDevice.this.mHandler.removeMessages(1);
                pendingActionClearedCallback.onCleared(hdmiCecLocalDevice);
            }
        };
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(Message.obtain(handler, 1), 5000L);
    }

    public final void handleDisableDeviceTimeout() {
        assertRunOnServiceThread();
        Iterator<HdmiCecFeatureAction> it = this.mActions.iterator();
        while (it.hasNext()) {
            it.next().finish(false);
            it.remove();
        }
        PendingActionClearedCallback pendingActionClearedCallback = this.mPendingActionClearedCallback;
        if (pendingActionClearedCallback != null) {
            pendingActionClearedCallback.onCleared(this);
        }
    }

    public void sendKeyEvent(int i, boolean z) {
        assertRunOnServiceThread();
        if (!HdmiCecKeycode.isSupportedKeycode(i)) {
            Slog.w("HdmiCecLocalDevice", "Unsupported key: " + i);
            return;
        }
        List actions = getActions(SendKeyAction.class);
        int findKeyReceiverAddress = findKeyReceiverAddress();
        if (findKeyReceiverAddress == -1 || findKeyReceiverAddress == this.mDeviceInfo.getLogicalAddress()) {
            Slog.w("HdmiCecLocalDevice", "Discard key event: " + i + ", pressed:" + z + ", receiverAddr=" + findKeyReceiverAddress);
        } else if (!actions.isEmpty()) {
            ((SendKeyAction) actions.get(0)).processKeyEvent(i, z);
        } else if (z) {
            addAndStartAction(new SendKeyAction(this, findKeyReceiverAddress, i));
        }
    }

    public void sendVolumeKeyEvent(int i, boolean z) {
        assertRunOnServiceThread();
        if (this.mService.getHdmiCecVolumeControl() == 0) {
            return;
        }
        if (!HdmiCecKeycode.isVolumeKeycode(i)) {
            Slog.w("HdmiCecLocalDevice", "Not a volume key: " + i);
            return;
        }
        List actions = getActions(SendKeyAction.class);
        final int findAudioReceiverAddress = findAudioReceiverAddress();
        if (findAudioReceiverAddress == -1 || this.mService.getAllCecLocalDevices().stream().anyMatch(new Predicate() { // from class: com.android.server.hdmi.HdmiCecLocalDevice$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$sendVolumeKeyEvent$1;
                lambda$sendVolumeKeyEvent$1 = HdmiCecLocalDevice.lambda$sendVolumeKeyEvent$1(findAudioReceiverAddress, (HdmiCecLocalDevice) obj);
                return lambda$sendVolumeKeyEvent$1;
            }
        })) {
            Slog.w("HdmiCecLocalDevice", "Discard volume key event: " + i + ", pressed:" + z + ", receiverAddr=" + findAudioReceiverAddress);
        } else if (!actions.isEmpty()) {
            ((SendKeyAction) actions.get(0)).processKeyEvent(i, z);
        } else if (z) {
            addAndStartAction(new SendKeyAction(this, findAudioReceiverAddress, i));
        }
    }

    public static /* synthetic */ boolean lambda$sendVolumeKeyEvent$1(int i, HdmiCecLocalDevice hdmiCecLocalDevice) {
        return hdmiCecLocalDevice.getDeviceInfo().getLogicalAddress() == i;
    }

    public int findKeyReceiverAddress() {
        Slog.w("HdmiCecLocalDevice", "findKeyReceiverAddress is not implemented");
        return -1;
    }

    public int findAudioReceiverAddress() {
        Slog.w("HdmiCecLocalDevice", "findAudioReceiverAddress is not implemented");
        return -1;
    }

    public void invokeCallback(IHdmiControlCallback iHdmiControlCallback, int i) {
        assertRunOnServiceThread();
        if (iHdmiControlCallback == null) {
            return;
        }
        try {
            iHdmiControlCallback.onComplete(i);
        } catch (RemoteException e) {
            Slog.e("HdmiCecLocalDevice", "Invoking callback failed:" + e);
        }
    }

    public void sendUserControlPressedAndReleased(int i, int i2) {
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildUserControlPressed(this.mDeviceInfo.getLogicalAddress(), i, i2));
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildUserControlReleased(this.mDeviceInfo.getLogicalAddress(), i));
    }

    public void addActiveSourceHistoryItem(ActiveSource activeSource, boolean z, String str) {
        ActiveSourceHistoryRecord activeSourceHistoryRecord = new ActiveSourceHistoryRecord(activeSource, z, str);
        if (this.mActiveSourceHistory.offer(activeSourceHistoryRecord)) {
            return;
        }
        this.mActiveSourceHistory.poll();
        this.mActiveSourceHistory.offer(activeSourceHistoryRecord);
    }

    public ArrayBlockingQueue<HdmiCecController.Dumpable> getActiveSourceHistory() {
        return this.mActiveSourceHistory;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("mDeviceType: " + this.mDeviceType);
        indentingPrintWriter.println("mPreferredAddress: " + this.mPreferredAddress);
        indentingPrintWriter.println("mDeviceInfo: " + this.mDeviceInfo);
        indentingPrintWriter.println("mActiveSource: " + getActiveSource());
        indentingPrintWriter.println(String.format("mActiveRoutingPath: 0x%04x", Integer.valueOf(this.mActiveRoutingPath)));
    }

    /* loaded from: classes.dex */
    public static final class ActiveSourceHistoryRecord extends HdmiCecController.Dumpable {
        public final ActiveSource mActiveSource;
        public final String mCaller;
        public final boolean mIsActiveSource;

        public ActiveSourceHistoryRecord(ActiveSource activeSource, boolean z, String str) {
            this.mActiveSource = activeSource;
            this.mIsActiveSource = z;
            this.mCaller = str;
        }

        @Override // com.android.server.hdmi.HdmiCecController.Dumpable
        public void dump(IndentingPrintWriter indentingPrintWriter, SimpleDateFormat simpleDateFormat) {
            indentingPrintWriter.print("time=");
            indentingPrintWriter.print(simpleDateFormat.format(new Date(this.mTime)));
            indentingPrintWriter.print(" active source=");
            indentingPrintWriter.print(this.mActiveSource);
            indentingPrintWriter.print(" isActiveSource=");
            indentingPrintWriter.print(this.mIsActiveSource);
            indentingPrintWriter.print(" from=");
            indentingPrintWriter.println(this.mCaller);
        }
    }
}
