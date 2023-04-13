package com.android.server.hdmi;

import android.hardware.hdmi.DeviceFeatures;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiPortInfo;
import android.hardware.hdmi.HdmiRecordSources;
import android.hardware.hdmi.HdmiTimerRecordSources;
import android.hardware.hdmi.IHdmiControlCallback;
import android.media.AudioDescriptor;
import android.media.AudioDeviceAttributes;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.hdmi.DeviceDiscoveryAction;
import com.android.server.hdmi.HdmiCecLocalDevice;
import com.android.server.hdmi.HdmiControlService;
import com.android.server.location.gnss.hal.GnssNative;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class HdmiCecLocalDeviceTv extends HdmiCecLocalDevice {
    public boolean mArcEstablished;
    public final SparseBooleanArray mArcFeatureEnabled;
    public final DelayedMessageBuffer mDelayedMessageBuffer;
    @GuardedBy({"mLock"})
    public int mPrevPortId;
    public SelectRequestBuffer mSelectRequestBuffer;
    public boolean mSkipRoutingControl;
    @GuardedBy({"mLock"})
    public boolean mSystemAudioControlFeatureEnabled;
    @GuardedBy({"mLock"})
    public boolean mSystemAudioMute;
    @GuardedBy({"mLock"})
    public int mSystemAudioVolume;
    public final TvInputManager.TvInputCallback mTvInputCallback;
    public final HashMap<String, Integer> mTvInputs;

    public static boolean isTailOfActivePath(int i, int i2) {
        if (i2 == 0) {
            return false;
        }
        for (int i3 = 12; i3 >= 0; i3 -= 4) {
            int i4 = (i2 >> i3) & 15;
            if (i4 == 0) {
                return true;
            }
            if (((i >> i3) & 15) != i4) {
                return false;
            }
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int findAudioReceiverAddress() {
        return 5;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int getPreferredAddress() {
        return 0;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int getRcProfile() {
        return 0;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleMenuStatus(HdmiCecMessage hdmiCecMessage) {
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRecordStatus(HdmiCecMessage hdmiCecMessage) {
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleTimerStatus(HdmiCecMessage hdmiCecMessage) {
        return -1;
    }

    public final boolean isDirectConnectAddress(int i) {
        return (61440 & i) == i;
    }

    public final void addTvInput(String str, int i) {
        assertRunOnServiceThread();
        this.mTvInputs.put(str, Integer.valueOf(i));
    }

    public final void removeTvInput(String str) {
        assertRunOnServiceThread();
        this.mTvInputs.remove(str);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public boolean isInputReady(int i) {
        assertRunOnServiceThread();
        return this.mTvInputs.containsValue(Integer.valueOf(i));
    }

    public HdmiCecLocalDeviceTv(HdmiControlService hdmiControlService) {
        super(hdmiControlService, 0);
        this.mArcEstablished = false;
        this.mArcFeatureEnabled = new SparseBooleanArray();
        this.mSystemAudioVolume = -1;
        this.mSystemAudioMute = false;
        this.mDelayedMessageBuffer = new DelayedMessageBuffer(this);
        this.mTvInputCallback = new TvInputManager.TvInputCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceTv.1
            @Override // android.media.tv.TvInputManager.TvInputCallback
            public void onInputAdded(String str) {
                HdmiDeviceInfo hdmiDeviceInfo;
                TvInputInfo tvInputInfo = HdmiCecLocalDeviceTv.this.mService.getTvInputManager().getTvInputInfo(str);
                if (tvInputInfo == null || (hdmiDeviceInfo = tvInputInfo.getHdmiDeviceInfo()) == null) {
                    return;
                }
                HdmiCecLocalDeviceTv.this.addTvInput(str, hdmiDeviceInfo.getId());
                if (hdmiDeviceInfo.isCecDevice()) {
                    HdmiCecLocalDeviceTv.this.processDelayedActiveSource(hdmiDeviceInfo.getLogicalAddress());
                }
            }

            @Override // android.media.tv.TvInputManager.TvInputCallback
            public void onInputRemoved(String str) {
                HdmiCecLocalDeviceTv.this.removeTvInput(str);
            }
        };
        this.mTvInputs = new HashMap<>();
        this.mPrevPortId = -1;
        this.mSystemAudioControlFeatureEnabled = hdmiControlService.getHdmiCecConfig().getIntValue("system_audio_control") == 1;
        this.mStandbyHandler = new HdmiCecStandbyModeHandler(hdmiControlService, this);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onAddressAllocated(int i, int i2) {
        assertRunOnServiceThread();
        for (HdmiPortInfo hdmiPortInfo : this.mService.getPortInfo()) {
            this.mArcFeatureEnabled.put(hdmiPortInfo.getId(), hdmiPortInfo.isArcSupported());
        }
        this.mService.registerTvInputCallback(this.mTvInputCallback);
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress(), this.mDeviceType));
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildDeviceVendorIdCommand(getDeviceInfo().getLogicalAddress(), this.mService.getVendorId()));
        this.mService.getHdmiCecNetwork().addCecSwitch(this.mService.getHdmiCecNetwork().getPhysicalAddress());
        this.mTvInputs.clear();
        boolean z = false;
        this.mSkipRoutingControl = i2 == 3;
        if (i2 != 0 && i2 != 1) {
            z = true;
        }
        launchRoutingControl(z);
        resetSelectRequestBuffer();
        launchDeviceDiscovery();
        startQueuedActions();
        if (this.mDelayedMessageBuffer.isBuffered(130)) {
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildRequestActiveSource(getDeviceInfo().getLogicalAddress()));
    }

    public void setSelectRequestBuffer(SelectRequestBuffer selectRequestBuffer) {
        assertRunOnServiceThread();
        this.mSelectRequestBuffer = selectRequestBuffer;
    }

    public final void resetSelectRequestBuffer() {
        assertRunOnServiceThread();
        setSelectRequestBuffer(SelectRequestBuffer.EMPTY_BUFFER);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void setPreferredAddress(int i) {
        Slog.w("HdmiCecLocalDeviceTv", "Preferred addres will not be stored for TV");
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    @VisibleForTesting
    public int dispatchMessage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (this.mService.isPowerStandby() && !this.mService.isWakeUpMessageReceived() && this.mStandbyHandler.handleCommand(hdmiCecMessage)) {
            return -1;
        }
        return super.onMessage(hdmiCecMessage);
    }

    public void deviceSelect(int i, IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        HdmiDeviceInfo deviceInfo = this.mService.getHdmiCecNetwork().getDeviceInfo(i);
        if (deviceInfo == null) {
            invokeCallback(iHdmiControlCallback, 3);
            return;
        }
        int logicalAddress = deviceInfo.getLogicalAddress();
        if (isAlreadyActiveSource(deviceInfo, logicalAddress, iHdmiControlCallback)) {
            return;
        }
        if (logicalAddress == 0) {
            handleSelectInternalSource();
            setActiveSource(logicalAddress, this.mService.getPhysicalAddress(), "HdmiCecLocalDeviceTv#deviceSelect()");
            setActivePath(this.mService.getPhysicalAddress());
            invokeCallback(iHdmiControlCallback, 0);
        } else if (!this.mService.isCecControlEnabled()) {
            setActiveSource(deviceInfo, "HdmiCecLocalDeviceTv#deviceSelect()");
            invokeCallback(iHdmiControlCallback, 6);
        } else {
            removeAction(DeviceSelectActionFromTv.class);
            addAndStartAction(new DeviceSelectActionFromTv(this, deviceInfo, iHdmiControlCallback));
        }
    }

    public final void handleSelectInternalSource() {
        assertRunOnServiceThread();
        if (!this.mService.isCecControlEnabled() || getActiveSource().logicalAddress == getDeviceInfo().getLogicalAddress()) {
            return;
        }
        updateActiveSource(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress(), "HdmiCecLocalDeviceTv#handleSelectInternalSource()");
        if (this.mSkipRoutingControl) {
            this.mSkipRoutingControl = false;
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress()));
    }

    public void updateActiveSource(int i, int i2, String str) {
        assertRunOnServiceThread();
        updateActiveSource(HdmiCecLocalDevice.ActiveSource.m51of(i, i2), str);
    }

    public void updateActiveSource(HdmiCecLocalDevice.ActiveSource activeSource, String str) {
        assertRunOnServiceThread();
        if (getActiveSource().equals(activeSource)) {
            return;
        }
        setActiveSource(activeSource, str);
        int i = activeSource.logicalAddress;
        if (this.mService.getHdmiCecNetwork().getCecDeviceInfo(i) == null || i == getDeviceInfo().getLogicalAddress() || this.mService.pathToPortId(activeSource.physicalAddress) != getActivePortId()) {
            return;
        }
        setPrevPortId(getActivePortId());
    }

    public int getPrevPortId() {
        int i;
        synchronized (this.mLock) {
            i = this.mPrevPortId;
        }
        return i;
    }

    public void setPrevPortId(int i) {
        synchronized (this.mLock) {
            this.mPrevPortId = i;
        }
    }

    public void updateActiveInput(int i, boolean z) {
        assertRunOnServiceThread();
        setActivePath(i);
        if (z) {
            HdmiDeviceInfo cecDeviceInfo = this.mService.getHdmiCecNetwork().getCecDeviceInfo(getActiveSource().logicalAddress);
            if (cecDeviceInfo == null && (cecDeviceInfo = this.mService.getDeviceInfoByPort(getActivePortId())) == null) {
                cecDeviceInfo = HdmiDeviceInfo.hardwarePort(i, getActivePortId());
            }
            this.mService.invokeInputChangeListener(cecDeviceInfo);
        }
    }

    public void doManualPortSwitching(int i, IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        if (!this.mService.isValidPortId(i)) {
            invokeCallback(iHdmiControlCallback, 6);
        } else if (i == getActivePortId()) {
            invokeCallback(iHdmiControlCallback, 0);
        } else {
            getActiveSource().invalidate();
            if (!this.mService.isCecControlEnabled()) {
                setActivePortId(i);
                invokeCallback(iHdmiControlCallback, 6);
                return;
            }
            int portIdToPath = getActivePortId() != -1 ? this.mService.portIdToPath(getActivePortId()) : getDeviceInfo().getPhysicalAddress();
            setActivePath(portIdToPath);
            if (this.mSkipRoutingControl) {
                this.mSkipRoutingControl = false;
            } else {
                startRoutingControl(portIdToPath, this.mService.portIdToPath(i), iHdmiControlCallback);
            }
        }
    }

    public void startRoutingControl(int i, int i2, IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        if (i == i2) {
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildRoutingChange(getDeviceInfo().getLogicalAddress(), i, i2));
        removeAction(RoutingControlAction.class);
        addAndStartAction(new RoutingControlAction(this, i2, iHdmiControlCallback));
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int findKeyReceiverAddress() {
        if (getActiveSource().isValid()) {
            return getActiveSource().logicalAddress;
        }
        HdmiDeviceInfo deviceInfoByPath = this.mService.getHdmiCecNetwork().getDeviceInfoByPath(getActivePath());
        if (deviceInfoByPath != null) {
            return deviceInfoByPath.getLogicalAddress();
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleActiveSource(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int source = hdmiCecMessage.getSource();
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
        HdmiDeviceInfo cecDeviceInfo = this.mService.getHdmiCecNetwork().getCecDeviceInfo(source);
        if (cecDeviceInfo == null) {
            if (handleNewDeviceAtTheTailOfActivePath(twoBytesToInt)) {
                return -1;
            }
            HdmiLogger.debug("Device info %X not found; buffering the command", Integer.valueOf(source));
            this.mDelayedMessageBuffer.add(hdmiCecMessage);
            return -1;
        } else if (isInputReady(cecDeviceInfo.getId()) || cecDeviceInfo.getDeviceType() == 5) {
            this.mService.getHdmiCecNetwork().updateDevicePowerStatus(source, 0);
            ActiveSourceHandler.create(this, null).process(HdmiCecLocalDevice.ActiveSource.m51of(source, twoBytesToInt), cecDeviceInfo.getDeviceType());
            return -1;
        } else {
            HdmiLogger.debug("Input not ready for device: %X; buffering the command", Integer.valueOf(cecDeviceInfo.getId()));
            this.mDelayedMessageBuffer.add(hdmiCecMessage);
            return -1;
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleInactiveSource(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (getActiveSource().logicalAddress == hdmiCecMessage.getSource() && !isProhibitMode()) {
            int prevPortId = getPrevPortId();
            if (prevPortId != -1) {
                HdmiDeviceInfo cecDeviceInfo = this.mService.getHdmiCecNetwork().getCecDeviceInfo(hdmiCecMessage.getSource());
                if (cecDeviceInfo == null || this.mService.pathToPortId(cecDeviceInfo.getPhysicalAddress()) == prevPortId) {
                    return -1;
                }
                doManualPortSwitching(prevPortId, null);
                setPrevPortId(-1);
            } else {
                getActiveSource().invalidate();
                setActivePath(GnssNative.GNSS_AIDING_TYPE_ALL);
                this.mService.invokeInputChangeListener(HdmiDeviceInfo.INACTIVE_DEVICE);
            }
            return -1;
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRequestActiveSource(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (getDeviceInfo().getLogicalAddress() == getActiveSource().logicalAddress) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(getDeviceInfo().getLogicalAddress(), getActivePath()));
            return -1;
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleGetMenuLanguage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (broadcastMenuLanguage(this.mService.getLanguage())) {
            return -1;
        }
        Slog.w("HdmiCecLocalDeviceTv", "Failed to respond to <Get Menu Language>: " + hdmiCecMessage.toString());
        return -1;
    }

    public boolean broadcastMenuLanguage(String str) {
        assertRunOnServiceThread();
        HdmiCecMessage buildSetMenuLanguageCommand = HdmiCecMessageBuilder.buildSetMenuLanguageCommand(getDeviceInfo().getLogicalAddress(), str);
        if (buildSetMenuLanguageCommand != null) {
            this.mService.sendCecCommand(buildSetMenuLanguageCommand);
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleReportPhysicalAddress(HdmiCecMessage hdmiCecMessage) {
        super.handleReportPhysicalAddress(hdmiCecMessage);
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
        int source = hdmiCecMessage.getSource();
        byte b = hdmiCecMessage.getParams()[2];
        if (!this.mService.getHdmiCecNetwork().isInDeviceList(source, twoBytesToInt)) {
            handleNewDeviceAtTheTailOfActivePath(twoBytesToInt);
        }
        startNewDeviceAction(HdmiCecLocalDevice.ActiveSource.m51of(source, twoBytesToInt), b);
        return -1;
    }

    public void startNewDeviceAction(HdmiCecLocalDevice.ActiveSource activeSource, int i) {
        for (NewDeviceAction newDeviceAction : getActions(NewDeviceAction.class)) {
            if (newDeviceAction.isActionOf(activeSource)) {
                return;
            }
        }
        addAndStartAction(new NewDeviceAction(this, activeSource.logicalAddress, activeSource.physicalAddress, i));
    }

    public final boolean handleNewDeviceAtTheTailOfActivePath(int i) {
        if (isTailOfActivePath(i, getActivePath())) {
            int portIdToPath = this.mService.portIdToPath(getActivePortId());
            setActivePath(portIdToPath);
            startRoutingControl(getActivePath(), portIdToPath, null);
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRoutingChange(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        byte[] params = hdmiCecMessage.getParams();
        if (HdmiUtils.isAffectingActiveRoutingPath(getActivePath(), HdmiUtils.twoBytesToInt(params))) {
            getActiveSource().invalidate();
            removeAction(RoutingControlAction.class);
            addAndStartAction(new RoutingControlAction(this, HdmiUtils.twoBytesToInt(params, 2), null));
            return -1;
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleReportAudioStatus(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (this.mService.getHdmiCecVolumeControl() == 0) {
            return 4;
        }
        setAudioStatus(HdmiUtils.isAudioStatusMute(hdmiCecMessage), HdmiUtils.getAudioStatusVolume(hdmiCecMessage));
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleTextViewOn(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (getAutoWakeup()) {
            this.mService.wakeUp();
            return -1;
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleImageViewOn(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        return handleTextViewOn(hdmiCecMessage);
    }

    public final void launchDeviceDiscovery() {
        assertRunOnServiceThread();
        clearDeviceInfoList();
        addAndStartAction(new DeviceDiscoveryAction(this, new DeviceDiscoveryAction.DeviceDiscoveryCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceTv.2
            @Override // com.android.server.hdmi.DeviceDiscoveryAction.DeviceDiscoveryCallback
            public void onDeviceDiscoveryDone(List<HdmiDeviceInfo> list) {
                for (HdmiDeviceInfo hdmiDeviceInfo : list) {
                    HdmiCecLocalDeviceTv.this.mService.getHdmiCecNetwork().addCecDevice(hdmiDeviceInfo);
                }
                for (HdmiCecLocalDevice hdmiCecLocalDevice : HdmiCecLocalDeviceTv.this.mService.getAllCecLocalDevices()) {
                    HdmiCecLocalDeviceTv.this.mService.getHdmiCecNetwork().addCecDevice(hdmiCecLocalDevice.getDeviceInfo());
                }
                HdmiCecLocalDeviceTv.this.mSelectRequestBuffer.process();
                HdmiCecLocalDeviceTv.this.resetSelectRequestBuffer();
                if (HdmiCecLocalDeviceTv.this.getActions(HotplugDetectionAction.class).isEmpty()) {
                    HdmiCecLocalDeviceTv hdmiCecLocalDeviceTv = HdmiCecLocalDeviceTv.this;
                    hdmiCecLocalDeviceTv.addAndStartAction(new HotplugDetectionAction(hdmiCecLocalDeviceTv));
                }
                if (HdmiCecLocalDeviceTv.this.getActions(PowerStatusMonitorAction.class).isEmpty()) {
                    HdmiCecLocalDeviceTv hdmiCecLocalDeviceTv2 = HdmiCecLocalDeviceTv.this;
                    hdmiCecLocalDeviceTv2.addAndStartAction(new PowerStatusMonitorAction(hdmiCecLocalDeviceTv2));
                }
                HdmiDeviceInfo avrDeviceInfo = HdmiCecLocalDeviceTv.this.getAvrDeviceInfo();
                if (avrDeviceInfo != null) {
                    HdmiCecLocalDeviceTv.this.onNewAvrAdded(avrDeviceInfo);
                } else {
                    HdmiCecLocalDeviceTv.this.setSystemAudioMode(false);
                }
            }
        }));
    }

    public void onNewAvrAdded(HdmiDeviceInfo hdmiDeviceInfo) {
        assertRunOnServiceThread();
        addAndStartAction(new SystemAudioAutoInitiationAction(this, hdmiDeviceInfo.getLogicalAddress()));
        if (!isDirectConnectAddress(hdmiDeviceInfo.getPhysicalAddress())) {
            startArcAction(false);
        } else if (isConnected(hdmiDeviceInfo.getPortId()) && isArcFeatureEnabled(hdmiDeviceInfo.getPortId()) && !hasAction(SetArcTransmissionStateAction.class)) {
            startArcAction(true);
        }
    }

    public void changeSystemAudioMode(boolean z, IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        if (!this.mService.isCecControlEnabled() || hasAction(DeviceDiscoveryAction.class)) {
            setSystemAudioMode(false);
            invokeCallback(iHdmiControlCallback, 6);
            return;
        }
        HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
        if (avrDeviceInfo == null) {
            setSystemAudioMode(false);
            invokeCallback(iHdmiControlCallback, 3);
            return;
        }
        addAndStartAction(new SystemAudioActionFromTv(this, avrDeviceInfo.getLogicalAddress(), z, iHdmiControlCallback));
    }

    public void setSystemAudioMode(boolean z) {
        if (!isSystemAudioControlFeatureEnabled() && z) {
            HdmiLogger.debug("Cannot turn on system audio mode because the System Audio Control feature is disabled.", new Object[0]);
            return;
        }
        HdmiLogger.debug("System Audio Mode change[old:%b new:%b]", Boolean.valueOf(this.mService.isSystemAudioActivated()), Boolean.valueOf(z));
        updateAudioManagerForSystemAudio(z);
        synchronized (this.mLock) {
            if (this.mService.isSystemAudioActivated() != z) {
                this.mService.setSystemAudioActivated(z);
                this.mService.announceSystemAudioModeChange(z);
            }
            if (z && !this.mArcEstablished) {
                startArcAction(true);
            } else if (!z) {
                startArcAction(false);
            }
        }
    }

    public final void updateAudioManagerForSystemAudio(boolean z) {
        HdmiLogger.debug("[A]UpdateSystemAudio mode[on=%b] output=[%X]", Boolean.valueOf(z), Integer.valueOf(this.mService.getAudioManager().setHdmiSystemAudioSupported(z)));
    }

    public boolean isSystemAudioActivated() {
        if (hasSystemAudioDevice()) {
            return this.mService.isSystemAudioActivated();
        }
        return false;
    }

    public void setSystemAudioControlFeatureEnabled(boolean z) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mSystemAudioControlFeatureEnabled = z;
        }
        if (hasSystemAudioDevice()) {
            changeSystemAudioMode(z, null);
        }
    }

    public boolean isSystemAudioControlFeatureEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSystemAudioControlFeatureEnabled;
        }
        return z;
    }

    public void enableArc(List<byte[]> list) {
        assertRunOnServiceThread();
        HdmiLogger.debug("Set Arc Status[old:%b new:true]", Boolean.valueOf(this.mArcEstablished));
        enableAudioReturnChannel(true);
        notifyArcStatusToAudioService(true, list);
        this.mArcEstablished = true;
    }

    public void disableArc() {
        assertRunOnServiceThread();
        HdmiLogger.debug("Set Arc Status[old:%b new:false]", Boolean.valueOf(this.mArcEstablished));
        enableAudioReturnChannel(false);
        notifyArcStatusToAudioService(false, new ArrayList());
        this.mArcEstablished = false;
    }

    public void enableAudioReturnChannel(boolean z) {
        assertRunOnServiceThread();
        HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
        if (avrDeviceInfo == null || avrDeviceInfo.getPortId() == -1) {
            return;
        }
        this.mService.enableAudioReturnChannel(avrDeviceInfo.getPortId(), z);
    }

    public boolean isConnected(int i) {
        assertRunOnServiceThread();
        return this.mService.isConnected(i);
    }

    public final void notifyArcStatusToAudioService(boolean z, List<byte[]> list) {
        this.mService.getAudioManager().setWiredDeviceConnectionState(new AudioDeviceAttributes(2, 10, "", "", new ArrayList(), (List) list.stream().map(new Function() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceTv$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                AudioDescriptor lambda$notifyArcStatusToAudioService$0;
                lambda$notifyArcStatusToAudioService$0 = HdmiCecLocalDeviceTv.lambda$notifyArcStatusToAudioService$0((byte[]) obj);
                return lambda$notifyArcStatusToAudioService$0;
            }
        }).collect(Collectors.toList())), z ? 1 : 0);
    }

    public static /* synthetic */ AudioDescriptor lambda$notifyArcStatusToAudioService$0(byte[] bArr) {
        return new AudioDescriptor(1, 0, bArr);
    }

    public boolean isArcEstablished() {
        assertRunOnServiceThread();
        if (this.mArcEstablished) {
            for (int i = 0; i < this.mArcFeatureEnabled.size(); i++) {
                if (this.mArcFeatureEnabled.valueAt(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isArcFeatureEnabled(int i) {
        assertRunOnServiceThread();
        return this.mArcFeatureEnabled.get(i);
    }

    public void startArcAction(boolean z) {
        startArcAction(z, null);
    }

    public void startArcAction(boolean z, IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
        if (avrDeviceInfo == null) {
            Slog.w("HdmiCecLocalDeviceTv", "Failed to start arc action; No AVR device.");
            invokeCallback(iHdmiControlCallback, 3);
        } else if (!canStartArcUpdateAction(avrDeviceInfo.getLogicalAddress(), z)) {
            Slog.w("HdmiCecLocalDeviceTv", "Failed to start arc action; ARC configuration check failed.");
            if (z && !isConnectedToArcPort(avrDeviceInfo.getPhysicalAddress())) {
                displayOsd(1);
            }
            invokeCallback(iHdmiControlCallback, 6);
        } else if (z && this.mService.earcBlocksArcConnection()) {
            Slog.i("HdmiCecLocalDeviceTv", "ARC connection blocked because eARC connection is established or being established.");
            invokeCallback(iHdmiControlCallback, 6);
        } else if (z) {
            removeAction(RequestArcTerminationAction.class);
            if (hasAction(RequestArcInitiationAction.class)) {
                ((RequestArcInitiationAction) getActions(RequestArcInitiationAction.class).get(0)).addCallback(iHdmiControlCallback);
            } else {
                addAndStartAction(new RequestArcInitiationAction(this, avrDeviceInfo.getLogicalAddress(), iHdmiControlCallback));
            }
        } else {
            removeAction(RequestArcInitiationAction.class);
            if (hasAction(RequestArcTerminationAction.class)) {
                ((RequestArcTerminationAction) getActions(RequestArcTerminationAction.class).get(0)).addCallback(iHdmiControlCallback);
            } else {
                addAndStartAction(new RequestArcTerminationAction(this, avrDeviceInfo.getLogicalAddress(), iHdmiControlCallback));
            }
        }
    }

    public void setAudioStatus(boolean z, int i) {
        if (!isSystemAudioActivated() || this.mService.getHdmiCecVolumeControl() == 0) {
            return;
        }
        synchronized (this.mLock) {
            this.mSystemAudioMute = z;
            this.mSystemAudioVolume = i;
            if (z) {
                i = 101;
            }
            displayOsd(2, i);
        }
    }

    public void changeVolume(int i, int i2, int i3) {
        assertRunOnServiceThread();
        if (getAvrDeviceInfo() == null || i2 == 0 || !isSystemAudioActivated() || this.mService.getHdmiCecVolumeControl() == 0) {
            return;
        }
        int scaleToCecVolume = VolumeControlAction.scaleToCecVolume(i + i2, i3);
        synchronized (this.mLock) {
            int i4 = this.mSystemAudioVolume;
            if (scaleToCecVolume == i4) {
                this.mService.setAudioStatus(false, VolumeControlAction.scaleToCustomVolume(i4, i3));
                return;
            }
            List actions = getActions(VolumeControlAction.class);
            if (actions.isEmpty()) {
                addAndStartAction(new VolumeControlAction(this, getAvrDeviceInfo().getLogicalAddress(), i2 > 0));
            } else {
                ((VolumeControlAction) actions.get(0)).handleVolumeChange(i2 > 0);
            }
        }
    }

    public void changeMute(boolean z) {
        assertRunOnServiceThread();
        if (getAvrDeviceInfo() == null || this.mService.getHdmiCecVolumeControl() == 0) {
            return;
        }
        HdmiLogger.debug("[A]:Change mute:%b", Boolean.valueOf(z));
        synchronized (this.mLock) {
            if (this.mSystemAudioMute == z) {
                HdmiLogger.debug("No need to change mute.", new Object[0]);
            } else if (!isSystemAudioActivated()) {
                HdmiLogger.debug("[A]:System audio is not activated.", new Object[0]);
            } else {
                removeAction(VolumeControlAction.class);
                sendUserControlPressedAndReleased(getAvrDeviceInfo().getLogicalAddress(), HdmiCecKeycode.getMuteKey(z));
            }
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleInitiateArc(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (this.mService.earcBlocksArcConnection()) {
            Slog.i("HdmiCecLocalDeviceTv", "ARC connection blocked because eARC connection is established or being established.");
            return 1;
        } else if (!canStartArcUpdateAction(hdmiCecMessage.getSource(), true)) {
            HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
            if (avrDeviceInfo == null) {
                this.mDelayedMessageBuffer.add(hdmiCecMessage);
                return -1;
            } else if (isConnectedToArcPort(avrDeviceInfo.getPhysicalAddress())) {
                return 4;
            } else {
                displayOsd(1);
                return 4;
            }
        } else {
            addAndStartAction(new SetArcTransmissionStateAction(this, hdmiCecMessage.getSource(), true));
            return -1;
        }
    }

    public final boolean canStartArcUpdateAction(int i, boolean z) {
        HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
        if (avrDeviceInfo != null && i == avrDeviceInfo.getLogicalAddress() && isConnectedToArcPort(avrDeviceInfo.getPhysicalAddress())) {
            if (z) {
                return isConnected(avrDeviceInfo.getPortId()) && isArcFeatureEnabled(avrDeviceInfo.getPortId()) && isDirectConnectAddress(avrDeviceInfo.getPhysicalAddress());
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleTerminateArc(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (this.mService.isPowerStandbyOrTransient()) {
            disableArc();
            return -1;
        }
        addAndStartAction(new SetArcTransmissionStateAction(this, hdmiCecMessage.getSource(), false));
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSetSystemAudioMode(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        boolean parseCommandParamSystemAudioStatus = HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage);
        if (!isMessageForSystemAudio(hdmiCecMessage)) {
            if (getAvrDeviceInfo() == null) {
                this.mDelayedMessageBuffer.add(hdmiCecMessage);
            } else {
                HdmiLogger.warning("Invalid <Set System Audio Mode> message:" + hdmiCecMessage, new Object[0]);
                return 4;
            }
        } else if (parseCommandParamSystemAudioStatus && !isSystemAudioControlFeatureEnabled()) {
            HdmiLogger.debug("Ignoring <Set System Audio Mode> message because the System Audio Control feature is disabled: %s", hdmiCecMessage);
            return 4;
        }
        removeAction(SystemAudioAutoInitiationAction.class);
        addAndStartAction(new SystemAudioActionFromAvr(this, hdmiCecMessage.getSource(), parseCommandParamSystemAudioStatus, null));
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSystemAudioModeStatus(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (!isMessageForSystemAudio(hdmiCecMessage)) {
            HdmiLogger.warning("Invalid <System Audio Mode Status> message:" + hdmiCecMessage, new Object[0]);
            return -1;
        }
        boolean isSystemAudioControlFeatureEnabled = isSystemAudioControlFeatureEnabled();
        boolean parseCommandParamSystemAudioStatus = HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage);
        HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
        if (avrDeviceInfo == null) {
            setSystemAudioMode(false);
        } else if (parseCommandParamSystemAudioStatus != isSystemAudioControlFeatureEnabled) {
            addAndStartAction(new SystemAudioActionFromTv(this, avrDeviceInfo.getLogicalAddress(), isSystemAudioControlFeatureEnabled, null));
        } else {
            setSystemAudioMode(isSystemAudioControlFeatureEnabled);
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRecordTvScreen(HdmiCecMessage hdmiCecMessage) {
        List actions = getActions(OneTouchRecordAction.class);
        if (!actions.isEmpty()) {
            if (((OneTouchRecordAction) actions.get(0)).getRecorderAddress() != hdmiCecMessage.getSource()) {
                announceOneTouchRecordResult(hdmiCecMessage.getSource(), 48);
                return 2;
            }
            return 2;
        }
        int source = hdmiCecMessage.getSource();
        return startOneTouchRecord(source, this.mService.invokeRecordRequestListener(source));
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleTimerClearedStatus(HdmiCecMessage hdmiCecMessage) {
        announceTimerRecordingResult(hdmiCecMessage.getSource(), hdmiCecMessage.getParams()[0] & 255);
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSetAudioVolumeLevel(SetAudioVolumeLevelMessage setAudioVolumeLevelMessage) {
        if (this.mService.isSystemAudioActivated()) {
            return 1;
        }
        this.mService.setStreamMusicVolume(setAudioVolumeLevelMessage.getAudioVolumeLevel(), 0);
        return -1;
    }

    public void announceOneTouchRecordResult(int i, int i2) {
        this.mService.invokeOneTouchRecordResult(i, i2);
    }

    public void announceTimerRecordingResult(int i, int i2) {
        this.mService.invokeTimerRecordingResult(i, i2);
    }

    public void announceClearTimerRecordingResult(int i, int i2) {
        this.mService.invokeClearTimerRecordingResult(i, i2);
    }

    public final boolean isMessageForSystemAudio(HdmiCecMessage hdmiCecMessage) {
        return this.mService.isCecControlEnabled() && hdmiCecMessage.getSource() == 5 && (hdmiCecMessage.getDestination() == 0 || hdmiCecMessage.getDestination() == 15) && getAvrDeviceInfo() != null;
    }

    public HdmiDeviceInfo getAvrDeviceInfo() {
        assertRunOnServiceThread();
        return this.mService.getHdmiCecNetwork().getCecDeviceInfo(5);
    }

    public boolean hasSystemAudioDevice() {
        return getSafeAvrDeviceInfo() != null;
    }

    public HdmiDeviceInfo getSafeAvrDeviceInfo() {
        return this.mService.getHdmiCecNetwork().getSafeCecDeviceInfo(5);
    }

    public AudioDeviceAttributes getSystemAudioOutputDevice() {
        return HdmiControlService.AUDIO_OUTPUT_DEVICE_HDMI_ARC;
    }

    public void handleRemoveActiveRoutingPath(int i) {
        assertRunOnServiceThread();
        if (isTailOfActivePath(i, getActivePath())) {
            startRoutingControl(getActivePath(), this.mService.portIdToPath(getActivePortId()), null);
        }
    }

    public void launchRoutingControl(boolean z) {
        assertRunOnServiceThread();
        if (getActivePortId() != -1 && getActivePortId() != 0) {
            if (z || isProhibitMode()) {
                return;
            }
            int portIdToPath = this.mService.portIdToPath(getActivePortId());
            setActivePath(portIdToPath);
            startRoutingControl(getActivePath(), portIdToPath, null);
            return;
        }
        int physicalAddress = this.mService.getPhysicalAddress();
        setActivePath(physicalAddress);
        if (z || this.mDelayedMessageBuffer.isBuffered(130)) {
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(getDeviceInfo().getLogicalAddress(), physicalAddress));
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onHotplug(int i, boolean z) {
        assertRunOnServiceThread();
        if (!z) {
            this.mService.getHdmiCecNetwork().removeCecSwitches(i);
        }
        if (getAvrDeviceInfo() != null && i == getAvrDeviceInfo().getPortId()) {
            HdmiLogger.debug("Port ID:%d, 5v=%b", Integer.valueOf(i), Boolean.valueOf(z));
            if (!z) {
                setSystemAudioMode(false);
            } else {
                onNewAvrAdded(getAvrDeviceInfo());
            }
        }
        List actions = getActions(HotplugDetectionAction.class);
        if (actions.isEmpty()) {
            return;
        }
        ((HotplugDetectionAction) actions.get(0)).pollAllDevicesNow();
    }

    public boolean getAutoWakeup() {
        assertRunOnServiceThread();
        return this.mService.getHdmiCecConfig().getIntValue("tv_wake_on_one_touch_play") == 1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void disableDevice(boolean z, HdmiCecLocalDevice.PendingActionClearedCallback pendingActionClearedCallback) {
        assertRunOnServiceThread();
        this.mService.unregisterTvInputCallback(this.mTvInputCallback);
        removeAction(DeviceDiscoveryAction.class);
        removeAction(HotplugDetectionAction.class);
        removeAction(PowerStatusMonitorAction.class);
        removeAction(OneTouchRecordAction.class);
        removeAction(TimerRecordingAction.class);
        removeAction(NewDeviceAction.class);
        removeAction(AbsoluteVolumeAudioStatusAction.class);
        disableSystemAudioIfExist();
        disableArcIfExist();
        super.disableDevice(z, pendingActionClearedCallback);
        clearDeviceInfoList();
        getActiveSource().invalidate();
        setActivePath(GnssNative.GNSS_AIDING_TYPE_ALL);
        checkIfPendingActionsCleared();
    }

    public final void disableSystemAudioIfExist() {
        assertRunOnServiceThread();
        if (getAvrDeviceInfo() == null) {
            return;
        }
        removeAction(SystemAudioActionFromAvr.class);
        removeAction(SystemAudioActionFromTv.class);
        removeAction(SystemAudioAutoInitiationAction.class);
        removeAction(VolumeControlAction.class);
        if (this.mService.isCecControlEnabled()) {
            return;
        }
        setSystemAudioMode(false);
    }

    public final void forceDisableArcOnAllPins() {
        for (HdmiPortInfo hdmiPortInfo : this.mService.getPortInfo()) {
            if (isArcFeatureEnabled(hdmiPortInfo.getId())) {
                this.mService.enableAudioReturnChannel(hdmiPortInfo.getId(), false);
            }
        }
    }

    public final void disableArcIfExist() {
        assertRunOnServiceThread();
        HdmiDeviceInfo avrDeviceInfo = getAvrDeviceInfo();
        if (avrDeviceInfo == null) {
            return;
        }
        removeAllRunningArcAction();
        if (!hasAction(RequestArcTerminationAction.class) && isArcEstablished()) {
            addAndStartAction(new RequestArcTerminationAction(this, avrDeviceInfo.getLogicalAddress()));
        }
        forceDisableArcOnAllPins();
    }

    public final void removeAllRunningArcAction() {
        removeAction(RequestArcTerminationAction.class);
        removeAction(RequestArcInitiationAction.class);
        removeAction(SetArcTransmissionStateAction.class);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onStandby(boolean z, int i) {
        assertRunOnServiceThread();
        if (this.mService.isCecControlEnabled()) {
            boolean z2 = this.mService.getHdmiCecConfig().getIntValue("tv_send_standby_on_sleep") == 1;
            if (z || !z2) {
                return;
            }
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 15));
        }
    }

    public boolean isProhibitMode() {
        return this.mService.isProhibitMode();
    }

    public boolean isPowerStandbyOrTransient() {
        return this.mService.isPowerStandbyOrTransient();
    }

    public void displayOsd(int i) {
        assertRunOnServiceThread();
        this.mService.displayOsd(i);
    }

    public void displayOsd(int i, int i2) {
        assertRunOnServiceThread();
        this.mService.displayOsd(i, i2);
    }

    public int startOneTouchRecord(int i, byte[] bArr) {
        assertRunOnServiceThread();
        if (!this.mService.isCecControlEnabled()) {
            Slog.w("HdmiCecLocalDeviceTv", "Can not start one touch record. CEC control is disabled.");
            announceOneTouchRecordResult(i, 51);
            return 1;
        } else if (!checkRecorder(i)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + i);
            announceOneTouchRecordResult(i, 49);
            return 1;
        } else if (!checkRecordSource(bArr)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid record source." + Arrays.toString(bArr));
            announceOneTouchRecordResult(i, 50);
            return 2;
        } else {
            addAndStartAction(new OneTouchRecordAction(this, i, bArr));
            Slog.i("HdmiCecLocalDeviceTv", "Start new [One Touch Record]-Target:" + i + ", recordSource:" + Arrays.toString(bArr));
            return -1;
        }
    }

    public void stopOneTouchRecord(int i) {
        assertRunOnServiceThread();
        if (!this.mService.isCecControlEnabled()) {
            Slog.w("HdmiCecLocalDeviceTv", "Can not stop one touch record. CEC control is disabled.");
            announceOneTouchRecordResult(i, 51);
        } else if (!checkRecorder(i)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + i);
            announceOneTouchRecordResult(i, 49);
        } else {
            removeAction(OneTouchRecordAction.class);
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildRecordOff(getDeviceInfo().getLogicalAddress(), i));
            Slog.i("HdmiCecLocalDeviceTv", "Stop [One Touch Record]-Target:" + i);
        }
    }

    public final boolean checkRecorder(int i) {
        return this.mService.getHdmiCecNetwork().getCecDeviceInfo(i) != null && HdmiUtils.isEligibleAddressForDevice(1, i);
    }

    public final boolean checkRecordSource(byte[] bArr) {
        return bArr != null && HdmiRecordSources.checkRecordSource(bArr);
    }

    public void startTimerRecording(int i, int i2, byte[] bArr) {
        assertRunOnServiceThread();
        if (!this.mService.isCecControlEnabled()) {
            Slog.w("HdmiCecLocalDeviceTv", "Can not start one touch record. CEC control is disabled.");
            announceTimerRecordingResult(i, 3);
        } else if (!checkRecorder(i)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + i);
            announceTimerRecordingResult(i, 1);
        } else if (!checkTimerRecordingSource(i2, bArr)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid record source." + Arrays.toString(bArr));
            announceTimerRecordingResult(i, 2);
        } else {
            addAndStartAction(new TimerRecordingAction(this, i, i2, bArr));
            Slog.i("HdmiCecLocalDeviceTv", "Start [Timer Recording]-Target:" + i + ", SourceType:" + i2 + ", RecordSource:" + Arrays.toString(bArr));
        }
    }

    public final boolean checkTimerRecordingSource(int i, byte[] bArr) {
        return bArr != null && HdmiTimerRecordSources.checkTimerRecordSource(i, bArr);
    }

    public void clearTimerRecording(int i, int i2, byte[] bArr) {
        assertRunOnServiceThread();
        if (!this.mService.isCecControlEnabled()) {
            Slog.w("HdmiCecLocalDeviceTv", "Can not start one touch record. CEC control is disabled.");
            announceClearTimerRecordingResult(i, FrameworkStatsLog.f379x24956b9a);
        } else if (!checkRecorder(i)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + i);
            announceClearTimerRecordingResult(i, FrameworkStatsLog.f418x97ec91aa);
        } else if (!checkTimerRecordingSource(i2, bArr)) {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid record source." + Arrays.toString(bArr));
            announceClearTimerRecordingResult(i, FrameworkStatsLog.f416x35de4f00);
        } else {
            sendClearTimerMessage(i, i2, bArr);
        }
    }

    public final void sendClearTimerMessage(final int i, int i2, byte[] bArr) {
        HdmiCecMessage buildClearDigitalTimer;
        if (i2 == 1) {
            buildClearDigitalTimer = HdmiCecMessageBuilder.buildClearDigitalTimer(getDeviceInfo().getLogicalAddress(), i, bArr);
        } else if (i2 == 2) {
            buildClearDigitalTimer = HdmiCecMessageBuilder.buildClearAnalogueTimer(getDeviceInfo().getLogicalAddress(), i, bArr);
        } else if (i2 == 3) {
            buildClearDigitalTimer = HdmiCecMessageBuilder.buildClearExternalTimer(getDeviceInfo().getLogicalAddress(), i, bArr);
        } else {
            Slog.w("HdmiCecLocalDeviceTv", "Invalid source type:" + i);
            announceClearTimerRecordingResult(i, FrameworkStatsLog.f416x35de4f00);
            return;
        }
        this.mService.sendCecCommand(buildClearDigitalTimer, new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceTv.3
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i3) {
                if (i3 != 0) {
                    HdmiCecLocalDeviceTv.this.announceClearTimerRecordingResult(i, FrameworkStatsLog.f416x35de4f00);
                }
            }
        });
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public List<Integer> getRcFeatures() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(this.mService.getHdmiCecConfig().getIntValue("rc_profile_tv")));
        return arrayList;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public DeviceFeatures computeDeviceFeatures() {
        int i;
        Iterator<HdmiPortInfo> it = this.mService.getPortInfo().iterator();
        while (true) {
            if (!it.hasNext()) {
                i = 0;
                break;
            } else if (isArcFeatureEnabled(it.next().getId())) {
                i = 1;
                break;
            }
        }
        return DeviceFeatures.NO_FEATURES_SUPPORTED.toBuilder().setRecordTvScreenSupport(1).setArcTxSupport(i).setSetAudioVolumeLevelSupport(1).build();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void sendStandby(int i) {
        HdmiDeviceInfo deviceInfo = this.mService.getHdmiCecNetwork().getDeviceInfo(i);
        if (deviceInfo == null) {
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), deviceInfo.getLogicalAddress()));
    }

    public void processAllDelayedMessages() {
        assertRunOnServiceThread();
        this.mDelayedMessageBuffer.processAllMessages();
    }

    public void processDelayedMessages(int i) {
        assertRunOnServiceThread();
        this.mDelayedMessageBuffer.processMessagesForDevice(i);
    }

    public void processDelayedActiveSource(int i) {
        assertRunOnServiceThread();
        this.mDelayedMessageBuffer.processActiveSource(i);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        super.dump(indentingPrintWriter);
        indentingPrintWriter.println("mArcEstablished: " + this.mArcEstablished);
        indentingPrintWriter.println("mArcFeatureEnabled: " + this.mArcFeatureEnabled);
        indentingPrintWriter.println("mSystemAudioMute: " + this.mSystemAudioMute);
        indentingPrintWriter.println("mSystemAudioControlFeatureEnabled: " + this.mSystemAudioControlFeatureEnabled);
        indentingPrintWriter.println("mSkipRoutingControl: " + this.mSkipRoutingControl);
        indentingPrintWriter.println("mPrevPortId: " + this.mPrevPortId);
    }
}
