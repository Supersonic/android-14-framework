package com.android.server.hdmi;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.hdmi.DeviceFeatures;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.SystemProperties;
import android.sysprop.HdmiProperties;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.hdmi.DeviceDiscoveryAction;
import com.android.server.hdmi.HdmiCecLocalDevice;
import com.android.server.hdmi.HdmiUtils;
import com.android.server.location.gnss.hal.GnssNative;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class HdmiCecLocalDeviceAudioSystem extends HdmiCecLocalDeviceSource {
    public static final HashMap<Integer, List<Integer>> AUDIO_CODECS_MAP = mapAudioCodecWithAudioFormat();
    public boolean mArcEstablished;
    public boolean mArcIntentUsed;
    public final DelayedMessageBuffer mDelayedMessageBuffer;
    @GuardedBy({"mLock"})
    public final HashMap<Integer, String> mPortIdToTvInputs;
    @GuardedBy({"mLock"})
    public boolean mSystemAudioControlFeatureEnabled;
    public final TvInputManager.TvInputCallback mTvInputCallback;
    @GuardedBy({"mLock"})
    public final HashMap<String, HdmiDeviceInfo> mTvInputsToDeviceInfo;
    public Boolean mTvSystemAudioModeSupport;

    /* loaded from: classes.dex */
    public interface TvSystemAudioModeSupportedCallback {
        void onResult(boolean z);
    }

    public void switchToAudioInput() {
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public /* bridge */ /* synthetic */ ArrayBlockingQueue getActiveSourceHistory() {
        return super.getActiveSourceHistory();
    }

    public HdmiCecLocalDeviceAudioSystem(HdmiControlService hdmiControlService) {
        super(hdmiControlService, 5);
        this.mTvSystemAudioModeSupport = null;
        this.mArcEstablished = false;
        this.mArcIntentUsed = ((String) HdmiProperties.arc_port().orElse("0")).contains("tvinput");
        this.mPortIdToTvInputs = new HashMap<>();
        this.mTvInputsToDeviceInfo = new HashMap<>();
        this.mDelayedMessageBuffer = new DelayedMessageBuffer(this);
        this.mTvInputCallback = new TvInputManager.TvInputCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem.1
            @Override // android.media.tv.TvInputManager.TvInputCallback
            public void onInputAdded(String str) {
                HdmiCecLocalDeviceAudioSystem.this.addOrUpdateTvInput(str);
            }

            @Override // android.media.tv.TvInputManager.TvInputCallback
            public void onInputRemoved(String str) {
                HdmiCecLocalDeviceAudioSystem.this.removeTvInput(str);
            }

            @Override // android.media.tv.TvInputManager.TvInputCallback
            public void onInputUpdated(String str) {
                HdmiCecLocalDeviceAudioSystem.this.addOrUpdateTvInput(str);
            }
        };
        this.mRoutingControlFeatureEnabled = this.mService.getHdmiCecConfig().getIntValue("routing_control") == 1;
        this.mSystemAudioControlFeatureEnabled = this.mService.getHdmiCecConfig().getIntValue("system_audio_control") == 1;
        this.mStandbyHandler = new HdmiCecStandbyModeHandler(hdmiControlService, this);
    }

    public final void addOrUpdateTvInput(String str) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            TvInputInfo tvInputInfo = this.mService.getTvInputManager().getTvInputInfo(str);
            if (tvInputInfo == null) {
                return;
            }
            HdmiDeviceInfo hdmiDeviceInfo = tvInputInfo.getHdmiDeviceInfo();
            if (hdmiDeviceInfo == null) {
                return;
            }
            this.mPortIdToTvInputs.put(Integer.valueOf(hdmiDeviceInfo.getPortId()), str);
            this.mTvInputsToDeviceInfo.put(str, hdmiDeviceInfo);
            if (hdmiDeviceInfo.isCecDevice()) {
                processDelayedActiveSource(hdmiDeviceInfo.getLogicalAddress());
            }
        }
    }

    public final void removeTvInput(String str) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            if (this.mTvInputsToDeviceInfo.get(str) == null) {
                return;
            }
            this.mPortIdToTvInputs.remove(Integer.valueOf(this.mTvInputsToDeviceInfo.get(str).getPortId()));
            this.mTvInputsToDeviceInfo.remove(str);
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public boolean isInputReady(int i) {
        assertRunOnServiceThread();
        return this.mTvInputsToDeviceInfo.get(this.mPortIdToTvInputs.get(Integer.valueOf(i))) != null;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public DeviceFeatures computeDeviceFeatures() {
        return DeviceFeatures.NO_FEATURES_SUPPORTED.toBuilder().setArcRxSupport(SystemProperties.getBoolean("persist.sys.hdmi.property_arc_support", true) ? 1 : 0).build();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public void onHotplug(int i, boolean z) {
        assertRunOnServiceThread();
        if (this.mService.getPortInfo(i).getType() == 1) {
            this.mCecMessageCache.flushAll();
            if (z) {
                return;
            }
            if (isSystemAudioActivated()) {
                this.mTvSystemAudioModeSupport = null;
                checkSupportAndSetSystemAudioMode(false);
            }
            if (isArcEnabled()) {
                setArcStatus(false);
            }
        } else if (z || this.mPortIdToTvInputs.get(Integer.valueOf(i)) == null) {
        } else {
            HdmiDeviceInfo hdmiDeviceInfo = this.mTvInputsToDeviceInfo.get(this.mPortIdToTvInputs.get(Integer.valueOf(i)));
            if (hdmiDeviceInfo == null) {
                return;
            }
            this.mService.getHdmiCecNetwork().removeCecDevice(this, hdmiDeviceInfo.getLogicalAddress());
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public void disableDevice(boolean z, HdmiCecLocalDevice.PendingActionClearedCallback pendingActionClearedCallback) {
        terminateAudioReturnChannel();
        super.disableDevice(z, pendingActionClearedCallback);
        assertRunOnServiceThread();
        this.mService.unregisterTvInputCallback(this.mTvInputCallback);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onStandby(boolean z, int i) {
        assertRunOnServiceThread();
        this.mService.setActiveSource(-1, GnssNative.GNSS_AIDING_TYPE_ALL, "HdmiCecLocalDeviceAudioSystem#onStandby()");
        this.mTvSystemAudioModeSupport = null;
        synchronized (this.mLock) {
            this.mService.writeStringSystemProperty("persist.sys.hdmi.last_system_audio_control", isSystemAudioActivated() ? "true" : "false");
        }
        terminateSystemAudioMode();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onAddressAllocated(int i, int i2) {
        assertRunOnServiceThread();
        HdmiControlService hdmiControlService = this.mService;
        if (i2 == 0) {
            hdmiControlService.setAndBroadcastActiveSource(hdmiControlService.getPhysicalAddress(), getDeviceInfo().getDeviceType(), 15, "HdmiCecLocalDeviceAudioSystem#onAddressAllocated()");
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(getDeviceInfo().getLogicalAddress(), this.mService.getPhysicalAddress(), this.mDeviceType));
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildDeviceVendorIdCommand(getDeviceInfo().getLogicalAddress(), this.mService.getVendorId()));
        this.mService.registerTvInputCallback(this.mTvInputCallback);
        initArcOnFromAvr();
        if (!this.mService.isScreenOff()) {
            systemAudioControlOnPowerOn(SystemProperties.getInt("persist.sys.hdmi.system_audio_control_on_power_on", 0), SystemProperties.getBoolean("persist.sys.hdmi.last_system_audio_control", true));
        }
        this.mService.getHdmiCecNetwork().clearDeviceList();
        launchDeviceDiscovery();
        startQueuedActions();
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int findKeyReceiverAddress() {
        if (getActiveSource().isValid()) {
            return getActiveSource().logicalAddress;
        }
        return -1;
    }

    @VisibleForTesting
    public void systemAudioControlOnPowerOn(int i, boolean z) {
        if (i == 0 || (i == 1 && z && isSystemAudioControlFeatureEnabled())) {
            addAndStartAction(new SystemAudioInitiationActionFromAvr(this));
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int getPreferredAddress() {
        assertRunOnServiceThread();
        return SystemProperties.getInt("persist.sys.hdmi.addr.audiosystem", 15);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void setPreferredAddress(int i) {
        assertRunOnServiceThread();
        this.mService.writeStringSystemProperty("persist.sys.hdmi.addr.audiosystem", String.valueOf(i));
    }

    public void processDelayedActiveSource(int i) {
        assertRunOnServiceThread();
        this.mDelayedMessageBuffer.processActiveSource(i);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource, com.android.server.hdmi.HdmiCecLocalDevice
    public int handleActiveSource(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int source = hdmiCecMessage.getSource();
        if (HdmiUtils.getLocalPortFromPhysicalAddress(HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams()), this.mService.getPhysicalAddress()) == -1) {
            return super.handleActiveSource(hdmiCecMessage);
        }
        HdmiDeviceInfo cecDeviceInfo = this.mService.getHdmiCecNetwork().getCecDeviceInfo(source);
        if (cecDeviceInfo == null) {
            HdmiLogger.debug("Device info %X not found; buffering the command", Integer.valueOf(source));
            this.mDelayedMessageBuffer.add(hdmiCecMessage);
        } else if (!isInputReady(cecDeviceInfo.getPortId())) {
            HdmiLogger.debug("Input not ready for device: %X; buffering the command", Integer.valueOf(cecDeviceInfo.getId()));
            this.mDelayedMessageBuffer.add(hdmiCecMessage);
        } else {
            this.mDelayedMessageBuffer.removeActiveSource();
            return super.handleActiveSource(hdmiCecMessage);
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleInitiateArc(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        HdmiLogger.debug("HdmiCecLocalDeviceAudioSystemStub handleInitiateArc", new Object[0]);
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleReportArcInitiate(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleReportArcTermination(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        processArcTermination();
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleGiveAudioStatus(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (isSystemAudioControlFeatureEnabled() && this.mService.getHdmiCecVolumeControl() == 1) {
            reportAudioStatus(hdmiCecMessage.getSource());
            return -1;
        }
        return 4;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleGiveSystemAudioModeStatus(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        boolean isSystemAudioActivated = isSystemAudioActivated();
        if (!isSystemAudioActivated && hdmiCecMessage.getSource() == 0 && hasAction(SystemAudioInitiationActionFromAvr.class)) {
            isSystemAudioActivated = true;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportSystemAudioMode(getDeviceInfo().getLogicalAddress(), hdmiCecMessage.getSource(), isSystemAudioActivated));
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRequestArcInitiate(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        removeAction(ArcInitiationActionFromAvr.class);
        if (this.mService.readBooleanSystemProperty("persist.sys.hdmi.property_arc_support", true)) {
            if (!isDirectConnectToTv()) {
                HdmiLogger.debug("AVR device is not directly connected with TV", new Object[0]);
                return 1;
            }
            addAndStartAction(new ArcInitiationActionFromAvr(this));
            return -1;
        }
        return 0;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRequestArcTermination(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (SystemProperties.getBoolean("persist.sys.hdmi.property_arc_support", true)) {
            if (!isArcEnabled()) {
                HdmiLogger.debug("ARC is not established between TV and AVR device", new Object[0]);
                return 1;
            } else if (!getActions(ArcTerminationActionFromAvr.class).isEmpty() && !((ArcTerminationActionFromAvr) getActions(ArcTerminationActionFromAvr.class).get(0)).mCallbacks.isEmpty()) {
                removeAction(ArcTerminationActionFromAvr.class);
                addAndStartAction(new ArcTerminationActionFromAvr(this, ((ArcTerminationActionFromAvr) getActions(ArcTerminationActionFromAvr.class).get(0)).mCallbacks.get(0)));
                return -1;
            } else {
                removeAction(ArcTerminationActionFromAvr.class);
                addAndStartAction(new ArcTerminationActionFromAvr(this));
                return -1;
            }
        }
        return 0;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRequestShortAudioDescriptor(HdmiCecMessage hdmiCecMessage) {
        byte[] supportedShortAudioDescriptors;
        assertRunOnServiceThread();
        HdmiLogger.debug("HdmiCecLocalDeviceAudioSystemStub handleRequestShortAudioDescriptor", new Object[0]);
        if (isSystemAudioControlFeatureEnabled()) {
            if (isSystemAudioActivated()) {
                File file = new File("/vendor/etc/sadConfig.xml");
                List<HdmiUtils.DeviceConfig> list = null;
                if (file.exists()) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        list = HdmiUtils.ShortAudioDescriptorXmlParser.parse(fileInputStream);
                        fileInputStream.close();
                    } catch (IOException e) {
                        Slog.e("HdmiCecLocalDeviceAudioSystem", "Error reading file: " + file, e);
                    } catch (XmlPullParserException e2) {
                        Slog.e("HdmiCecLocalDeviceAudioSystem", "Unable to parse file: " + file, e2);
                    }
                }
                int[] parseAudioCodecs = parseAudioCodecs(hdmiCecMessage.getParams());
                if (list != null && list.size() > 0) {
                    supportedShortAudioDescriptors = getSupportedShortAudioDescriptorsFromConfig(list, parseAudioCodecs);
                } else {
                    AudioDeviceInfo systemAudioDeviceInfo = getSystemAudioDeviceInfo();
                    if (systemAudioDeviceInfo == null) {
                        return 5;
                    }
                    supportedShortAudioDescriptors = getSupportedShortAudioDescriptors(systemAudioDeviceInfo, parseAudioCodecs);
                }
                if (supportedShortAudioDescriptors.length == 0) {
                    return 3;
                }
                this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportShortAudioDescriptor(getDeviceInfo().getLogicalAddress(), hdmiCecMessage.getSource(), supportedShortAudioDescriptors));
                return -1;
            }
            return 1;
        }
        return 4;
    }

    @VisibleForTesting
    public byte[] getSupportedShortAudioDescriptors(AudioDeviceInfo audioDeviceInfo, int[] iArr) {
        ArrayList<byte[]> arrayList = new ArrayList<>(iArr.length);
        for (int i : iArr) {
            byte[] supportedShortAudioDescriptor = getSupportedShortAudioDescriptor(audioDeviceInfo, i);
            if (supportedShortAudioDescriptor != null) {
                if (supportedShortAudioDescriptor.length == 3) {
                    arrayList.add(supportedShortAudioDescriptor);
                } else {
                    HdmiLogger.warning("Dropping Short Audio Descriptor with length %d for requested codec %x", Integer.valueOf(supportedShortAudioDescriptor.length), Integer.valueOf(i));
                }
            }
        }
        return getShortAudioDescriptorBytes(arrayList);
    }

    public final byte[] getSupportedShortAudioDescriptorsFromConfig(List<HdmiUtils.DeviceConfig> list, int[] iArr) {
        HdmiUtils.DeviceConfig deviceConfig;
        byte[] bArr;
        String str = SystemProperties.get("persist.sys.hdmi.property_sytem_audio_mode_audio_port", "VX_AUDIO_DEVICE_IN_HDMI_ARC");
        Iterator<HdmiUtils.DeviceConfig> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                deviceConfig = null;
                break;
            }
            deviceConfig = it.next();
            if (deviceConfig.name.equals(str)) {
                break;
            }
        }
        if (deviceConfig == null) {
            Slog.w("HdmiCecLocalDeviceAudioSystem", "sadConfig.xml does not have required device info for " + str);
            return new byte[0];
        }
        HashMap hashMap = new HashMap();
        ArrayList<byte[]> arrayList = new ArrayList<>(iArr.length);
        for (HdmiUtils.CodecSad codecSad : deviceConfig.supportedCodecs) {
            hashMap.put(Integer.valueOf(codecSad.audioCodec), codecSad.sad);
        }
        for (int i = 0; i < iArr.length; i++) {
            if (hashMap.containsKey(Integer.valueOf(iArr[i])) && (bArr = (byte[]) hashMap.get(Integer.valueOf(iArr[i]))) != null && bArr.length == 3) {
                arrayList.add(bArr);
            }
        }
        return getShortAudioDescriptorBytes(arrayList);
    }

    public final byte[] getShortAudioDescriptorBytes(ArrayList<byte[]> arrayList) {
        byte[] bArr = new byte[arrayList.size() * 3];
        Iterator<byte[]> it = arrayList.iterator();
        int i = 0;
        while (it.hasNext()) {
            System.arraycopy(it.next(), 0, bArr, i, 3);
            i += 3;
        }
        return bArr;
    }

    @VisibleForTesting
    public byte[] getSupportedShortAudioDescriptor(AudioDeviceInfo audioDeviceInfo, int i) {
        byte[] bArr = new byte[3];
        int[] encodings = audioDeviceInfo.getEncodings();
        HashMap<Integer, List<Integer>> hashMap = AUDIO_CODECS_MAP;
        if (hashMap.containsKey(Integer.valueOf(i)) && encodings.length != 0) {
            List<Integer> list = hashMap.get(Integer.valueOf(i));
            for (int i2 : encodings) {
                if (list.contains(Integer.valueOf(i2))) {
                    bArr[0] = getFirstByteOfSAD(audioDeviceInfo, i);
                    bArr[1] = getSecondByteOfSAD(audioDeviceInfo);
                    switch (i) {
                        case 1:
                            if (i2 == 2) {
                                bArr[2] = 1;
                            } else if (i2 == 21) {
                                bArr[2] = 4;
                            } else {
                                bArr[2] = 0;
                            }
                            return bArr;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            bArr[2] = getThirdSadByteForCodecs2Through8(audioDeviceInfo);
                            return bArr;
                        case 8:
                        case 9:
                        default:
                            return null;
                        case 10:
                        case 11:
                        case 12:
                            bArr[2] = 0;
                            return bArr;
                    }
                }
            }
        }
        return null;
    }

    public static HashMap<Integer, List<Integer>> mapAudioCodecWithAudioFormat() {
        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
        hashMap.put(0, List.of(1));
        hashMap.put(1, List.of(3, 2, 4, 21, 22));
        hashMap.put(2, List.of(5));
        hashMap.put(3, List.of(11));
        hashMap.put(5, List.of(12));
        hashMap.put(4, List.of(9));
        hashMap.put(6, List.of(10));
        hashMap.put(7, List.of(7));
        hashMap.put(10, List.of(6, 18));
        hashMap.put(11, List.of(8));
        hashMap.put(12, List.of(14, 19));
        return hashMap;
    }

    public final byte getFirstByteOfSAD(AudioDeviceInfo audioDeviceInfo, int i) {
        return (byte) (((byte) ((getMaxNumberOfChannels(audioDeviceInfo) - 1) | 0)) | (i << 3));
    }

    public final byte getSecondByteOfSAD(AudioDeviceInfo audioDeviceInfo) {
        ArrayList arrayList = new ArrayList(Arrays.asList(32, 44, 48, 88, 96, Integer.valueOf((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__DOCSUI_PICK_RESULT), Integer.valueOf((int) FrameworkStatsLog.f392xcd34d435)));
        int[] sampleRates = audioDeviceInfo.getSampleRates();
        if (sampleRates.length == 0) {
            Slog.e("HdmiCecLocalDeviceAudioSystem", "Device supports arbitrary rates");
            return Byte.MAX_VALUE;
        }
        byte b = 0;
        for (int i : sampleRates) {
            if (arrayList.contains(Integer.valueOf(i))) {
                b = (byte) (b | (1 << arrayList.indexOf(Integer.valueOf(i))));
            }
        }
        return b;
    }

    public final int getMaxNumberOfChannels(AudioDeviceInfo audioDeviceInfo) {
        int i;
        int[] channelCounts = audioDeviceInfo.getChannelCounts();
        if (channelCounts.length == 0 || (i = channelCounts[channelCounts.length - 1]) > 8) {
            return 8;
        }
        return i;
    }

    public final byte getThirdSadByteForCodecs2Through8(AudioDeviceInfo audioDeviceInfo) {
        int i;
        int[] sampleRates = audioDeviceInfo.getSampleRates();
        if (sampleRates.length == 0) {
            i = FrameworkStatsLog.f392xcd34d435;
        } else {
            int i2 = 0;
            for (int i3 : sampleRates) {
                if (i2 < i3) {
                    i2 = i3;
                }
            }
            i = i2;
        }
        return (byte) (i / 8);
    }

    public final AudioDeviceInfo getSystemAudioDeviceInfo() {
        AudioManager audioManager = (AudioManager) this.mService.getContext().getSystemService(AudioManager.class);
        if (audioManager == null) {
            HdmiLogger.error("Error getting system audio device because AudioManager not available.", new Object[0]);
            return null;
        }
        AudioDeviceInfo[] devices = audioManager.getDevices(1);
        HdmiLogger.debug("Found %d audio input devices", Integer.valueOf(devices.length));
        for (AudioDeviceInfo audioDeviceInfo : devices) {
            HdmiLogger.debug("%s at port %s", audioDeviceInfo.getProductName(), audioDeviceInfo.getPort());
            HdmiLogger.debug("Supported encodings are %s", Arrays.stream(audioDeviceInfo.getEncodings()).mapToObj(new IntFunction() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return AudioFormat.toLogFriendlyEncoding(i);
                }
            }).collect(Collectors.joining(", ")));
            if (audioDeviceInfo.getType() == 10) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    public final int[] parseAudioCodecs(byte[] bArr) {
        int[] iArr = new int[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            if (b < 1 || b > 15) {
                b = 0;
            }
            iArr[i] = b;
        }
        return iArr;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSystemAudioModeRequest(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        boolean z = hdmiCecMessage.getParams().length != 0;
        if (hdmiCecMessage.getSource() == 0) {
            setTvSystemAudioModeSupport(true);
        } else if (z) {
            return handleSystemAudioModeOnFromNonTvDevice(hdmiCecMessage);
        }
        if (checkSupportAndSetSystemAudioMode(z)) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(getDeviceInfo().getLogicalAddress(), 15, z));
            if (z) {
                int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
                if (HdmiUtils.getLocalPortFromPhysicalAddress(twoBytesToInt, getDeviceInfo().getPhysicalAddress()) == -1 && this.mService.getHdmiCecNetwork().getSafeDeviceInfoByPath(twoBytesToInt) == null) {
                    switchInputOnReceivingNewActivePath(twoBytesToInt);
                }
            }
            return -1;
        }
        return 4;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSetSystemAudioMode(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        return !checkSupportAndSetSystemAudioMode(HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage)) ? 4 : -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSystemAudioModeStatus(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        return !checkSupportAndSetSystemAudioMode(HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage)) ? 4 : -1;
    }

    public void setArcStatus(boolean z) {
        assertRunOnServiceThread();
        HdmiLogger.debug("Set Arc Status[old:%b new:%b]", Boolean.valueOf(this.mArcEstablished), Boolean.valueOf(z));
        enableAudioReturnChannel(z);
        notifyArcStatusToAudioService(z);
        this.mArcEstablished = z;
    }

    public void processArcTermination() {
        setArcStatus(false);
        if (getLocalActivePort() == 17) {
            routeToInputFromPortId(getRoutingPort());
        }
    }

    public final void enableAudioReturnChannel(boolean z) {
        assertRunOnServiceThread();
        this.mService.enableAudioReturnChannel(Integer.parseInt((String) HdmiProperties.arc_port().orElse("0")), z);
    }

    public final void notifyArcStatusToAudioService(boolean z) {
        this.mService.getAudioManager().setWiredDeviceConnectionState(-2013265920, z ? 1 : 0, "", "");
    }

    public void reportAudioStatus(int i) {
        assertRunOnServiceThread();
        if (this.mService.getHdmiCecVolumeControl() == 0) {
            return;
        }
        int streamVolume = this.mService.getAudioManager().getStreamVolume(3);
        boolean isStreamMute = this.mService.getAudioManager().isStreamMute(3);
        int streamMaxVolume = this.mService.getAudioManager().getStreamMaxVolume(3);
        int streamMinVolume = this.mService.getAudioManager().getStreamMinVolume(3);
        int scaleToCecVolume = VolumeControlAction.scaleToCecVolume(streamVolume, streamMaxVolume);
        HdmiLogger.debug("Reporting volume %d (%d-%d) as CEC volume %d", Integer.valueOf(streamVolume), Integer.valueOf(streamMinVolume), Integer.valueOf(streamMaxVolume), Integer.valueOf(scaleToCecVolume));
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportAudioStatus(getDeviceInfo().getLogicalAddress(), i, scaleToCecVolume, isStreamMute));
    }

    public boolean checkSupportAndSetSystemAudioMode(boolean z) {
        if (!isSystemAudioControlFeatureEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot turn ");
            sb.append(z ? "on" : "off");
            sb.append("system audio mode because the System Audio Control feature is disabled.");
            HdmiLogger.debug(sb.toString(), new Object[0]);
            return false;
        }
        HdmiLogger.debug("System Audio Mode change[old:%b new:%b]", Boolean.valueOf(isSystemAudioActivated()), Boolean.valueOf(z));
        if (z) {
            this.mService.wakeUp();
        }
        setSystemAudioMode(z);
        return true;
    }

    public final void setSystemAudioMode(boolean z) {
        int pathToPortId = this.mService.pathToPortId(getActiveSource().physicalAddress);
        if (z && pathToPortId >= 0) {
            switchToAudioInput();
        }
        boolean z2 = this.mService.getHdmiCecConfig().getIntValue("system_audio_mode_muting") == 1;
        if (this.mService.getAudioManager().isStreamMute(3) == z && (z2 || z)) {
            this.mService.getAudioManager().adjustStreamVolume(3, z ? 100 : -100, 0);
        }
        updateAudioManagerForSystemAudio(z);
        synchronized (this.mLock) {
            if (isSystemAudioActivated() != z) {
                this.mService.setSystemAudioActivated(z);
                this.mService.announceSystemAudioModeChange(z);
            }
        }
        if (this.mArcIntentUsed && !z2 && !z && getLocalActivePort() == 17) {
            routeToInputFromPortId(getRoutingPort());
        }
        if (SystemProperties.getBoolean("persist.sys.hdmi.property_arc_support", true) && isDirectConnectToTv() && this.mService.isSystemAudioActivated() && !hasAction(ArcInitiationActionFromAvr.class)) {
            addAndStartAction(new ArcInitiationActionFromAvr(this));
        }
    }

    public boolean isDirectConnectToTv() {
        int physicalAddress = this.mService.getPhysicalAddress();
        return (61440 & physicalAddress) == physicalAddress;
    }

    public final void updateAudioManagerForSystemAudio(boolean z) {
        HdmiLogger.debug("[A]UpdateSystemAudio mode[on=%b] output=[%X]", Boolean.valueOf(z), Integer.valueOf(this.mService.getAudioManager().setHdmiSystemAudioSupported(z)));
    }

    public void onSystemAudioControlFeatureSupportChanged(boolean z) {
        setSystemAudioControlFeatureEnabled(z);
        if (z) {
            addAndStartAction(new SystemAudioInitiationActionFromAvr(this));
        }
    }

    public void setSystemAudioControlFeatureEnabled(boolean z) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mSystemAudioControlFeatureEnabled = z;
        }
    }

    public void setRoutingControlFeatureEnabled(boolean z) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mRoutingControlFeatureEnabled = z;
        }
    }

    public void doManualPortSwitching(int i, IHdmiControlCallback iHdmiControlCallback) {
        int physicalAddress;
        assertRunOnServiceThread();
        if (!this.mService.isValidPortId(i)) {
            invokeCallback(iHdmiControlCallback, 3);
        } else if (i == getLocalActivePort()) {
            invokeCallback(iHdmiControlCallback, 0);
        } else if (!this.mService.isCecControlEnabled()) {
            setRoutingPort(i);
            setLocalActivePort(i);
            invokeCallback(iHdmiControlCallback, 6);
        } else {
            if (getRoutingPort() != 0) {
                physicalAddress = this.mService.portIdToPath(getRoutingPort());
            } else {
                physicalAddress = getDeviceInfo().getPhysicalAddress();
            }
            int portIdToPath = this.mService.portIdToPath(i);
            if (physicalAddress == portIdToPath) {
                return;
            }
            setRoutingPort(i);
            setLocalActivePort(i);
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildRoutingChange(getDeviceInfo().getLogicalAddress(), physicalAddress, portIdToPath));
        }
    }

    public boolean isSystemAudioControlFeatureEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSystemAudioControlFeatureEnabled;
        }
        return z;
    }

    public boolean isSystemAudioActivated() {
        return this.mService.isSystemAudioActivated();
    }

    public void terminateSystemAudioMode() {
        removeAction(SystemAudioInitiationActionFromAvr.class);
        if (isSystemAudioActivated() && checkSupportAndSetSystemAudioMode(false)) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(getDeviceInfo().getLogicalAddress(), 15, false));
        }
    }

    public final void terminateAudioReturnChannel() {
        removeAction(ArcInitiationActionFromAvr.class);
        if (isArcEnabled() && this.mService.readBooleanSystemProperty("persist.sys.hdmi.property_arc_support", true)) {
            addAndStartAction(new ArcTerminationActionFromAvr(this));
        }
    }

    public void queryTvSystemAudioModeSupport(TvSystemAudioModeSupportedCallback tvSystemAudioModeSupportedCallback) {
        Boolean bool = this.mTvSystemAudioModeSupport;
        if (bool == null) {
            addAndStartAction(new DetectTvSystemAudioModeSupportAction(this, tvSystemAudioModeSupportedCallback));
        } else {
            tvSystemAudioModeSupportedCallback.onResult(bool.booleanValue());
        }
    }

    public int handleSystemAudioModeOnFromNonTvDevice(final HdmiCecMessage hdmiCecMessage) {
        if (!isSystemAudioControlFeatureEnabled()) {
            HdmiLogger.debug("Cannot turn onsystem audio mode because the System Audio Control feature is disabled.", new Object[0]);
            return 4;
        }
        this.mService.wakeUp();
        if (this.mService.pathToPortId(getActiveSource().physicalAddress) != -1) {
            setSystemAudioMode(true);
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(getDeviceInfo().getLogicalAddress(), 15, true));
            return -1;
        }
        queryTvSystemAudioModeSupport(new TvSystemAudioModeSupportedCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem.2
            @Override // com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback
            public void onResult(boolean z) {
                if (z) {
                    HdmiCecLocalDeviceAudioSystem.this.setSystemAudioMode(true);
                    HdmiCecLocalDeviceAudioSystem hdmiCecLocalDeviceAudioSystem = HdmiCecLocalDeviceAudioSystem.this;
                    hdmiCecLocalDeviceAudioSystem.mService.sendCecCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(hdmiCecLocalDeviceAudioSystem.getDeviceInfo().getLogicalAddress(), 15, true));
                    return;
                }
                HdmiCecLocalDeviceAudioSystem.this.mService.maySendFeatureAbortCommand(hdmiCecMessage, 4);
            }
        });
        return -1;
    }

    public void setTvSystemAudioModeSupport(boolean z) {
        this.mTvSystemAudioModeSupport = Boolean.valueOf(z);
    }

    @VisibleForTesting
    public boolean isArcEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mArcEstablished;
        }
        return z;
    }

    public final void initArcOnFromAvr() {
        removeAction(ArcTerminationActionFromAvr.class);
        if (SystemProperties.getBoolean("persist.sys.hdmi.property_arc_support", true) && isDirectConnectToTv() && !isArcEnabled()) {
            removeAction(ArcInitiationActionFromAvr.class);
            addAndStartAction(new ArcInitiationActionFromAvr(this));
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource
    public void switchInputOnReceivingNewActivePath(int i) {
        int pathToPortId = this.mService.pathToPortId(i);
        if (isSystemAudioActivated() && pathToPortId < 0) {
            routeToInputFromPortId(17);
        } else if (!this.mIsSwitchDevice || pathToPortId < 0) {
        } else {
            routeToInputFromPortId(pathToPortId);
        }
    }

    public void routeToInputFromPortId(int i) {
        if (!isRoutingControlFeatureEnabled()) {
            HdmiLogger.debug("Routing Control Feature is not enabled.", new Object[0]);
        } else if (this.mArcIntentUsed) {
            routeToTvInputFromPortId(i);
        }
    }

    public void routeToTvInputFromPortId(int i) {
        if (i < 0 || i >= 21) {
            HdmiLogger.debug("Invalid port number for Tv Input switching.", new Object[0]);
            return;
        }
        this.mService.wakeUp();
        if (getLocalActivePort() == i && i != 17) {
            HdmiLogger.debug("Not switching to the same port " + i + " except for arc", new Object[0]);
            return;
        }
        if (i == 0 && this.mService.isPlaybackDevice()) {
            switchToHomeTvInput();
        } else if (i == 17) {
            switchToTvInput((String) HdmiProperties.arc_port().orElse("0"));
            setLocalActivePort(i);
            return;
        } else {
            String str = this.mPortIdToTvInputs.get(Integer.valueOf(i));
            if (str != null) {
                switchToTvInput(str);
            } else {
                HdmiLogger.debug("Port number does not match any Tv Input.", new Object[0]);
                return;
            }
        }
        setLocalActivePort(i);
        setRoutingPort(i);
    }

    public final void switchToTvInput(String str) {
        try {
            this.mService.getContext().startActivity(new Intent("android.intent.action.VIEW", TvContract.buildChannelUriForPassthroughInput(str)).addFlags(268435456));
        } catch (ActivityNotFoundException e) {
            Slog.e("HdmiCecLocalDeviceAudioSystem", "Can't find activity to switch to " + str, e);
        }
    }

    public final void switchToHomeTvInput() {
        try {
            this.mService.getContext().startActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setFlags(872480768));
        } catch (ActivityNotFoundException e) {
            Slog.e("HdmiCecLocalDeviceAudioSystem", "Can't find activity to switch to HOME", e);
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDeviceSource
    public void handleRoutingChangeAndInformation(int i, HdmiCecMessage hdmiCecMessage) {
        int pathToPortId = this.mService.pathToPortId(i);
        if (pathToPortId > 0) {
            return;
        }
        if (pathToPortId < 0 && isSystemAudioActivated()) {
            handleRoutingChangeAndInformationForSystemAudio();
        } else if (pathToPortId == 0) {
            handleRoutingChangeAndInformationForSwitch(hdmiCecMessage);
        }
    }

    public final void handleRoutingChangeAndInformationForSystemAudio() {
        routeToInputFromPortId(17);
    }

    public final void handleRoutingChangeAndInformationForSwitch(HdmiCecMessage hdmiCecMessage) {
        if (getRoutingPort() == 0 && this.mService.isPlaybackDevice()) {
            routeToInputFromPortId(0);
            this.mService.setAndBroadcastActiveSourceFromOneDeviceType(hdmiCecMessage.getSource(), this.mService.getPhysicalAddress(), "HdmiCecLocalDeviceAudioSystem#handleRoutingChangeAndInformationForSwitch()");
            return;
        }
        int portIdToPath = this.mService.portIdToPath(getRoutingPort());
        if (portIdToPath == this.mService.getPhysicalAddress()) {
            HdmiLogger.debug("Current device can't assign valid physical addressto devices under it any more. It's physical address is " + portIdToPath, new Object[0]);
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildRoutingInformation(getDeviceInfo().getLogicalAddress(), portIdToPath));
        routeToInputFromPortId(getRoutingPort());
    }

    public final void launchDeviceDiscovery() {
        assertRunOnServiceThread();
        if (this.mService.isDeviceDiscoveryHandledByPlayback()) {
            return;
        }
        if (hasAction(DeviceDiscoveryAction.class)) {
            Slog.i("HdmiCecLocalDeviceAudioSystem", "Device Discovery Action is in progress. Restarting.");
            removeAction(DeviceDiscoveryAction.class);
        }
        addAndStartAction(new DeviceDiscoveryAction(this, new DeviceDiscoveryAction.DeviceDiscoveryCallback() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem.3
            @Override // com.android.server.hdmi.DeviceDiscoveryAction.DeviceDiscoveryCallback
            public void onDeviceDiscoveryDone(List<HdmiDeviceInfo> list) {
                for (HdmiDeviceInfo hdmiDeviceInfo : list) {
                    HdmiCecLocalDeviceAudioSystem.this.mService.getHdmiCecNetwork().addCecDevice(hdmiDeviceInfo);
                }
            }
        }));
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("HdmiCecLocalDeviceAudioSystem:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("isRoutingFeatureEnabled " + isRoutingControlFeatureEnabled());
        indentingPrintWriter.println("mSystemAudioControlFeatureEnabled: " + this.mSystemAudioControlFeatureEnabled);
        indentingPrintWriter.println("mTvSystemAudioModeSupport: " + this.mTvSystemAudioModeSupport);
        indentingPrintWriter.println("mArcEstablished: " + this.mArcEstablished);
        indentingPrintWriter.println("mArcIntentUsed: " + this.mArcIntentUsed);
        indentingPrintWriter.println("mRoutingPort: " + getRoutingPort());
        indentingPrintWriter.println("mLocalActivePort: " + getLocalActivePort());
        HdmiUtils.dumpMap(indentingPrintWriter, "mPortIdToTvInputs:", this.mPortIdToTvInputs);
        HdmiUtils.dumpMap(indentingPrintWriter, "mTvInputsToDeviceInfo:", this.mTvInputsToDeviceInfo);
        indentingPrintWriter.decreaseIndent();
        super.dump(indentingPrintWriter);
    }
}
