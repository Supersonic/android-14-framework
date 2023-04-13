package com.android.server.hdmi;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.net.INetd;
import android.os.Binder;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.sysprop.HdmiProperties;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.LocalePicker;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.hdmi.DeviceDiscoveryAction;
import com.android.server.hdmi.HdmiCecLocalDevice;
import com.android.server.hdmi.HdmiControlService;
import com.android.server.location.gnss.hal.GnssNative;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
/* loaded from: classes.dex */
public class HdmiCecLocalDevicePlayback extends HdmiCecLocalDeviceSource {
    @VisibleForTesting
    static final long STANDBY_AFTER_HOTPLUG_OUT_DELAY_MS = 30000;
    public Handler mDelayedStandbyHandler;
    @VisibleForTesting
    protected HdmiProperties.playback_device_action_on_routing_control_values mPlaybackDeviceActionOnRoutingControl;
    public ActiveWakeLock mWakeLock;

    /* loaded from: classes.dex */
    public interface ActiveWakeLock {
        void acquire();

        boolean isHeld();

        void release();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int findKeyReceiverAddress() {
        return 0;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public /* bridge */ /* synthetic */ ArrayBlockingQueue getActiveSourceHistory() {
        return super.getActiveSourceHistory();
    }

    public HdmiCecLocalDevicePlayback(HdmiControlService hdmiControlService) {
        super(hdmiControlService, 4);
        this.mPlaybackDeviceActionOnRoutingControl = (HdmiProperties.playback_device_action_on_routing_control_values) HdmiProperties.playback_device_action_on_routing_control().orElse(HdmiProperties.playback_device_action_on_routing_control_values.NONE);
        this.mDelayedStandbyHandler = new Handler(hdmiControlService.getServiceLooper());
        this.mStandbyHandler = new HdmiCecStandbyModeHandler(hdmiControlService, this);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onAddressAllocated(int i, int i2) {
        assertRunOnServiceThread();
        HdmiControlService hdmiControlService = this.mService;
        if (i2 == 0) {
            hdmiControlService.setAndBroadcastActiveSource(hdmiControlService.getPhysicalAddress(), getDeviceInfo().getDeviceType(), 15, "HdmiCecLocalDevicePlayback#onAddressAllocated()");
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress(), this.mDeviceType));
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildDeviceVendorIdCommand(getDeviceInfo().getLogicalAddress(), this.mService.getVendorId()));
        buildAndSendSetOsdName(0);
        if (this.mService.audioSystem() == null) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildGiveSystemAudioModeStatus(getDeviceInfo().getLogicalAddress(), 5), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDevicePlayback.1
                @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
                public void onSendCompleted(int i3) {
                    if (i3 != 0) {
                        HdmiLogger.debug("AVR did not respond to <Give System Audio Mode Status>", new Object[0]);
                        HdmiCecLocalDevicePlayback.this.mService.setSystemAudioActivated(false);
                    }
                }
            });
        }
        launchDeviceDiscovery();
        startQueuedActions();
    }

    public final void launchDeviceDiscovery() {
        assertRunOnServiceThread();
        clearDeviceInfoList();
        addAndStartAction(new DeviceDiscoveryAction(this, new DeviceDiscoveryAction.DeviceDiscoveryCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDevicePlayback.2
            @Override // com.android.server.hdmi.DeviceDiscoveryAction.DeviceDiscoveryCallback
            public void onDeviceDiscoveryDone(List<HdmiDeviceInfo> list) {
                for (HdmiDeviceInfo hdmiDeviceInfo : list) {
                    HdmiCecLocalDevicePlayback.this.mService.getHdmiCecNetwork().addCecDevice(hdmiDeviceInfo);
                }
                for (HdmiCecLocalDevice hdmiCecLocalDevice : HdmiCecLocalDevicePlayback.this.mService.getAllCecLocalDevices()) {
                    HdmiCecLocalDevicePlayback.this.mService.getHdmiCecNetwork().addCecDevice(hdmiCecLocalDevice.getDeviceInfo());
                }
                if (HdmiCecLocalDevicePlayback.this.getActions(HotplugDetectionAction.class).isEmpty()) {
                    HdmiCecLocalDevicePlayback hdmiCecLocalDevicePlayback = HdmiCecLocalDevicePlayback.this;
                    hdmiCecLocalDevicePlayback.addAndStartAction(new HotplugDetectionAction(hdmiCecLocalDevicePlayback));
                }
            }
        }));
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int getPreferredAddress() {
        assertRunOnServiceThread();
        return SystemProperties.getInt("persist.sys.hdmi.addr.playback", 15);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void setPreferredAddress(int i) {
        assertRunOnServiceThread();
        this.mService.writeStringSystemProperty("persist.sys.hdmi.addr.playback", String.valueOf(i));
    }

    public void deviceSelect(int i, IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        if (i == getDeviceInfo().getId()) {
            this.mService.oneTouchPlay(iHdmiControlCallback);
            return;
        }
        HdmiDeviceInfo deviceInfo = this.mService.getHdmiCecNetwork().getDeviceInfo(i);
        if (deviceInfo == null) {
            invokeCallback(iHdmiControlCallback, 3);
        } else if (isAlreadyActiveSource(deviceInfo, deviceInfo.getLogicalAddress(), iHdmiControlCallback)) {
        } else {
            if (!this.mService.isCecControlEnabled()) {
                setActiveSource(deviceInfo, "HdmiCecLocalDevicePlayback#deviceSelect()");
                invokeCallback(iHdmiControlCallback, 6);
                return;
            }
            removeAction(DeviceSelectActionFromPlayback.class);
            addAndStartAction(new DeviceSelectActionFromPlayback(this, deviceInfo, iHdmiControlCallback));
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public void onHotplug(int i, boolean z) {
        assertRunOnServiceThread();
        this.mCecMessageCache.flushAll();
        if (z) {
            this.mDelayedStandbyHandler.removeCallbacksAndMessages(null);
            return;
        }
        getWakeLock().release();
        this.mService.getHdmiCecNetwork().removeDevicesConnectedToPort(i);
        this.mDelayedStandbyHandler.removeCallbacksAndMessages(null);
        this.mDelayedStandbyHandler.postDelayed(new DelayedStandbyRunnable(), 30000L);
    }

    /* loaded from: classes.dex */
    public class DelayedStandbyRunnable implements Runnable {
        public DelayedStandbyRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (HdmiCecLocalDevicePlayback.this.mService.getPowerManagerInternal().wasDeviceIdleFor(30000L)) {
                HdmiCecLocalDevicePlayback.this.mService.standby();
            } else {
                HdmiCecLocalDevicePlayback.this.mDelayedStandbyHandler.postDelayed(new DelayedStandbyRunnable(), 30000L);
            }
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onStandby(boolean z, int i) {
        assertRunOnServiceThread();
        if (this.mService.isCecControlEnabled()) {
            boolean isActiveSource = isActiveSource();
            char c = 65535;
            this.mService.setActiveSource(-1, GnssNative.GNSS_AIDING_TYPE_ALL, "HdmiCecLocalDevicePlayback#onStandby()");
            if (isActiveSource) {
                if (z) {
                    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildInactiveSource(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress()));
                } else if (i != 0) {
                    if (i != 1) {
                        return;
                    }
                    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 15));
                } else {
                    String stringValue = this.mService.getHdmiCecConfig().getStringValue("power_control_mode");
                    stringValue.hashCode();
                    switch (stringValue.hashCode()) {
                        case -1744153479:
                            if (stringValue.equals("to_tv_and_audio_system")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -1618876223:
                            if (stringValue.equals(INetd.IF_FLAG_BROADCAST)) {
                                c = 1;
                                break;
                            }
                            break;
                        case 3387192:
                            if (stringValue.equals("none")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 110530246:
                            if (stringValue.equals("to_tv")) {
                                c = 3;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 0));
                            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 5));
                            return;
                        case 1:
                            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 15));
                            return;
                        case 2:
                            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildInactiveSource(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress()));
                            return;
                        case 3:
                            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 0));
                            return;
                        default:
                            return;
                    }
                }
            }
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onInitializeCecComplete(int i) {
        if (i == 2 && !this.mService.getHdmiCecConfig().getStringValue("power_control_mode").equals("none")) {
            oneTouchPlay(new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiCecLocalDevicePlayback.3
                public void onComplete(int i2) {
                    if (i2 != 0) {
                        Slog.w("HdmiCecLocalDevicePlayback", "Failed to complete One Touch Play. result=" + i2);
                    }
                }
            });
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    @VisibleForTesting
    public void setActiveSource(int i, int i2, String str) {
        assertRunOnServiceThread();
        super.setActiveSource(i, i2, str);
        if (isActiveSource()) {
            getWakeLock().acquire();
        } else {
            getWakeLock().release();
        }
    }

    public final ActiveWakeLock getWakeLock() {
        assertRunOnServiceThread();
        if (this.mWakeLock == null) {
            if (SystemProperties.getBoolean("persist.sys.hdmi.keep_awake", true)) {
                this.mWakeLock = new SystemWakeLock();
            } else {
                this.mWakeLock = new ActiveWakeLock() { // from class: com.android.server.hdmi.HdmiCecLocalDevicePlayback.4
                    @Override // com.android.server.hdmi.HdmiCecLocalDevicePlayback.ActiveWakeLock
                    public void acquire() {
                    }

                    @Override // com.android.server.hdmi.HdmiCecLocalDevicePlayback.ActiveWakeLock
                    public boolean isHeld() {
                        return false;
                    }

                    @Override // com.android.server.hdmi.HdmiCecLocalDevicePlayback.ActiveWakeLock
                    public void release() {
                    }
                };
                HdmiLogger.debug("No wakelock is used to keep the display on.", new Object[0]);
            }
        }
        return this.mWakeLock;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public boolean canGoToStandby() {
        return !getWakeLock().isHeld();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource
    public void onActiveSourceLost() {
        assertRunOnServiceThread();
        this.mService.pauseActiveMediaSessions();
        String stringValue = this.mService.getHdmiCecConfig().getStringValue("power_state_change_on_active_source_lost");
        stringValue.hashCode();
        if (stringValue.equals("standby_now")) {
            this.mService.standby();
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleUserControlPressed(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        wakeUpIfActiveSource();
        return super.handleUserControlPressed(hdmiCecMessage);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSetMenuLanguage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (this.mService.getHdmiCecConfig().getIntValue("set_menu_language") == 0) {
            return 0;
        }
        try {
            String str = new String(hdmiCecMessage.getParams(), 0, 3, "US-ASCII");
            String localeToMenuLanguage = HdmiControlService.localeToMenuLanguage(this.mService.getContext().getResources().getConfiguration().locale);
            HdmiLogger.debug("handleSetMenuLanguage " + str + " cur:" + localeToMenuLanguage, new Object[0]);
            if (localeToMenuLanguage.equals(str)) {
                return -1;
            }
            for (LocalePicker.LocaleInfo localeInfo : LocalePicker.getAllAssetLocales(this.mService.getContext(), false)) {
                if (HdmiControlService.localeToMenuLanguage(localeInfo.getLocale()).equals(str)) {
                    startSetMenuLanguageActivity(localeInfo.getLocale());
                    return -1;
                }
            }
            Slog.w("HdmiCecLocalDevicePlayback", "Can't handle <Set Menu Language> of " + str);
            return 3;
        } catch (UnsupportedEncodingException e) {
            Slog.w("HdmiCecLocalDevicePlayback", "Can't handle <Set Menu Language>", e);
            return 3;
        }
    }

    public final void startSetMenuLanguageActivity(Locale locale) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                Context context = this.mService.getContext();
                Intent intent = new Intent();
                intent.putExtra("android.hardware.hdmi.extra.LOCALE", locale.toLanguageTag());
                intent.setComponent(ComponentName.unflattenFromString(context.getResources().getString(17039943)));
                intent.addFlags(268435456);
                context.startActivityAsUser(intent, context.getUser());
            } catch (ActivityNotFoundException unused) {
                Slog.e("HdmiCecLocalDevicePlayback", "unable to start HdmiCecSetMenuLanguageActivity");
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSetSystemAudioMode(HdmiCecMessage hdmiCecMessage) {
        boolean parseCommandParamSystemAudioStatus;
        if (hdmiCecMessage.getDestination() == 15 && hdmiCecMessage.getSource() == 5 && this.mService.audioSystem() == null && this.mService.isSystemAudioActivated() != (parseCommandParamSystemAudioStatus = HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage))) {
            this.mService.setSystemAudioActivated(parseCommandParamSystemAudioStatus);
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSystemAudioModeStatus(HdmiCecMessage hdmiCecMessage) {
        boolean parseCommandParamSystemAudioStatus;
        if (hdmiCecMessage.getDestination() == getDeviceInfo().getLogicalAddress() && hdmiCecMessage.getSource() == 5 && this.mService.isSystemAudioActivated() != (parseCommandParamSystemAudioStatus = HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage))) {
            this.mService.setSystemAudioActivated(parseCommandParamSystemAudioStatus);
            return -1;
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRoutingChange(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        handleRoutingChangeAndInformation(HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams(), 2), hdmiCecMessage);
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRoutingInformation(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        handleRoutingChangeAndInformation(HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams()), hdmiCecMessage);
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource
    public void handleRoutingChangeAndInformation(int i, HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (i != this.mService.getPhysicalAddress()) {
            setActiveSource(i, "HdmiCecLocalDevicePlayback#handleRoutingChangeAndInformation()");
            return;
        }
        if (!isActiveSource()) {
            setActiveSource(i, "HdmiCecLocalDevicePlayback#handleRoutingChangeAndInformation()");
        }
        int i2 = C09065.f1142x34455e89[this.mPlaybackDeviceActionOnRoutingControl.ordinal()];
        if (i2 == 1) {
            setAndBroadcastActiveSource(hdmiCecMessage, i, "HdmiCecLocalDevicePlayback#handleRoutingChangeAndInformation()");
        } else if (i2 != 2) {
        } else {
            this.mService.wakeUp();
        }
    }

    /* renamed from: com.android.server.hdmi.HdmiCecLocalDevicePlayback$5 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C09065 {

        /* renamed from: $SwitchMap$android$sysprop$HdmiProperties$playback_device_action_on_routing_control_values */
        public static final /* synthetic */ int[] f1142x34455e89;

        static {
            int[] iArr = new int[HdmiProperties.playback_device_action_on_routing_control_values.values().length];
            f1142x34455e89 = iArr;
            try {
                iArr[HdmiProperties.playback_device_action_on_routing_control_values.WAKE_UP_AND_SEND_ACTIVE_SOURCE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f1142x34455e89[HdmiProperties.playback_device_action_on_routing_control_values.WAKE_UP_ONLY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f1142x34455e89[HdmiProperties.playback_device_action_on_routing_control_values.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x000a  */
    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void preprocessBufferedMessages(List<HdmiCecMessage> list) {
        for (HdmiCecMessage hdmiCecMessage : list) {
            if (hdmiCecMessage.getOpcode() == 128 || hdmiCecMessage.getOpcode() == 134 || hdmiCecMessage.getOpcode() == 130) {
                removeAction(ActiveSourceAction.class);
                removeAction(OneTouchPlayAction.class);
                return;
            }
            while (r4.hasNext()) {
            }
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int findAudioReceiverAddress() {
        return this.mService.isSystemAudioActivated() ? 5 : 0;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public void disableDevice(boolean z, HdmiCecLocalDevice.PendingActionClearedCallback pendingActionClearedCallback) {
        assertRunOnServiceThread();
        removeAction(DeviceDiscoveryAction.class);
        removeAction(HotplugDetectionAction.class);
        removeAction(NewDeviceAction.class);
        super.disableDevice(z, pendingActionClearedCallback);
        clearDeviceInfoList();
        checkIfPendingActionsCleared();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        super.dump(indentingPrintWriter);
        indentingPrintWriter.println("isActiveSource(): " + isActiveSource());
    }

    /* loaded from: classes.dex */
    public class SystemWakeLock implements ActiveWakeLock {
        public final PowerManager.WakeLock mWakeLock;

        public SystemWakeLock() {
            PowerManager.WakeLock newWakeLock = HdmiCecLocalDevicePlayback.this.mService.getPowerManager().newWakeLock(1, "HdmiCecLocalDevicePlayback");
            this.mWakeLock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        }

        @Override // com.android.server.hdmi.HdmiCecLocalDevicePlayback.ActiveWakeLock
        public void acquire() {
            this.mWakeLock.acquire();
            HdmiLogger.debug("active source: %b. Wake lock acquired", Boolean.valueOf(HdmiCecLocalDevicePlayback.this.isActiveSource()));
        }

        @Override // com.android.server.hdmi.HdmiCecLocalDevicePlayback.ActiveWakeLock
        public void release() {
            this.mWakeLock.release();
            HdmiLogger.debug("Wake lock released", new Object[0]);
        }

        @Override // com.android.server.hdmi.HdmiCecLocalDevicePlayback.ActiveWakeLock
        public boolean isHeld() {
            return this.mWakeLock.isHeld();
        }
    }
}
