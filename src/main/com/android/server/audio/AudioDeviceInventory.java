package com.android.server.audio;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioDevicePort;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioPort;
import android.media.AudioRoutesInfo;
import android.media.AudioSystem;
import android.media.IAudioRoutesObserver;
import android.media.ICapturePresetDevicesRoleDispatcher;
import android.media.IStrategyNonDefaultDevicesDispatcher;
import android.media.IStrategyPreferredDevicesDispatcher;
import android.media.MediaMetrics;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.SafeCloseable;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.audio.AudioDeviceBroker;
import com.android.server.audio.AudioDeviceInventory;
import com.android.server.audio.BtHelper;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class AudioDeviceInventory {
    public static final Set<Integer> BECOMING_NOISY_INTENT_DEVICES_SET;
    public static final Set<Integer> DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG_SET;
    public AudioDeviceBroker mDeviceBroker;
    public final Object mDevicesLock = new Object();
    @GuardedBy({"mDevicesLock"})
    public final LinkedHashMap<String, DeviceInfo> mConnectedDevices = new LinkedHashMap<String, DeviceInfo>() { // from class: com.android.server.audio.AudioDeviceInventory.1
        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public DeviceInfo put(String str, DeviceInfo deviceInfo) {
            DeviceInfo deviceInfo2 = (DeviceInfo) super.put((C04991) str, (String) deviceInfo);
            record("put", true, str, deviceInfo);
            return deviceInfo2;
        }

        @Override // java.util.HashMap, java.util.Map
        public DeviceInfo putIfAbsent(String str, DeviceInfo deviceInfo) {
            DeviceInfo deviceInfo2 = (DeviceInfo) super.putIfAbsent((C04991) str, (String) deviceInfo);
            if (deviceInfo2 == null) {
                record("putIfAbsent", true, str, deviceInfo);
            }
            return deviceInfo2;
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public DeviceInfo remove(Object obj) {
            DeviceInfo deviceInfo = (DeviceInfo) super.remove(obj);
            if (deviceInfo != null) {
                record("remove", false, (String) obj, deviceInfo);
            }
            return deviceInfo;
        }

        @Override // java.util.HashMap, java.util.Map
        public boolean remove(Object obj, Object obj2) {
            boolean remove = super.remove(obj, obj2);
            if (remove) {
                record("remove", false, (String) obj, (DeviceInfo) obj2);
            }
            return remove;
        }

        public final void record(String str, boolean z, String str2, DeviceInfo deviceInfo) {
            new MediaMetrics.Item("audio.device." + AudioSystem.getDeviceName(deviceInfo.mDeviceType)).set(MediaMetrics.Property.ADDRESS, deviceInfo.mDeviceAddress).set(MediaMetrics.Property.EVENT, str).set(MediaMetrics.Property.NAME, deviceInfo.mDeviceName).set(MediaMetrics.Property.STATE, z ? "connected" : "disconnected").record();
        }
    };
    @GuardedBy({"mDevicesLock"})
    public final ArrayMap<Integer, String> mApmConnectedDevices = new ArrayMap<>();
    public final ArrayMap<Integer, List<AudioDeviceAttributes>> mPreferredDevices = new ArrayMap<>();
    public final ArrayMap<Integer, List<AudioDeviceAttributes>> mNonDefaultDevices = new ArrayMap<>();
    public final ArrayMap<Integer, List<AudioDeviceAttributes>> mPreferredDevicesForCapturePreset = new ArrayMap<>();
    public final AudioRoutesInfo mCurAudioRoutes = new AudioRoutesInfo();
    public final RemoteCallbackList<IAudioRoutesObserver> mRoutesObservers = new RemoteCallbackList<>();
    public final RemoteCallbackList<IStrategyPreferredDevicesDispatcher> mPrefDevDispatchers = new RemoteCallbackList<>();
    public final RemoteCallbackList<IStrategyNonDefaultDevicesDispatcher> mNonDefDevDispatchers = new RemoteCallbackList<>();
    public final RemoteCallbackList<ICapturePresetDevicesRoleDispatcher> mDevRoleCapturePresetDispatchers = new RemoteCallbackList<>();
    public final AudioSystemAdapter mAudioSystem = AudioSystemAdapter.getDefaultAdapter();

    public AudioDeviceInventory(AudioDeviceBroker audioDeviceBroker) {
        this.mDeviceBroker = audioDeviceBroker;
    }

    /* loaded from: classes.dex */
    public static class DeviceInfo {
        public final String mDeviceAddress;
        public int mDeviceCodecFormat;
        public final String mDeviceName;
        public final int mDeviceType;
        public final UUID mSensorUuid;

        public DeviceInfo(int i, String str, String str2, int i2, UUID uuid) {
            this.mDeviceType = i;
            this.mDeviceName = str == null ? "" : str;
            this.mDeviceAddress = str2 == null ? "" : str2;
            this.mDeviceCodecFormat = i2;
            this.mSensorUuid = uuid;
        }

        public DeviceInfo(int i, String str, String str2, int i2) {
            this(i, str, str2, i2, null);
        }

        public String toString() {
            return "[DeviceInfo: type:0x" + Integer.toHexString(this.mDeviceType) + " (" + AudioSystem.getDeviceName(this.mDeviceType) + ") name:" + this.mDeviceName + " addr:" + this.mDeviceAddress + " codec: " + Integer.toHexString(this.mDeviceCodecFormat) + " sensorUuid: " + Objects.toString(this.mSensorUuid) + "]";
        }

        public String getKey() {
            return makeDeviceListKey(this.mDeviceType, this.mDeviceAddress);
        }

        public static String makeDeviceListKey(int i, String str) {
            return "0x" + Integer.toHexString(i) + XmlUtils.STRING_ARRAY_SEPARATOR + str;
        }
    }

    /* loaded from: classes.dex */
    public class WiredDeviceConnectionState {
        public final AudioDeviceAttributes mAttributes;
        public final String mCaller;
        public boolean mForTest = false;
        public final int mState;

        public WiredDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i, String str) {
            this.mAttributes = audioDeviceAttributes;
            this.mState = i;
            this.mCaller = str;
        }
    }

    public void dump(final PrintWriter printWriter, final String str) {
        printWriter.println("\n" + str + "BECOMING_NOISY_INTENT_DEVICES_SET=");
        BECOMING_NOISY_INTENT_DEVICES_SET.forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AudioDeviceInventory.lambda$dump$0(printWriter, (Integer) obj);
            }
        });
        printWriter.println("\n" + str + "Preferred devices for strategy:");
        this.mPreferredDevices.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AudioDeviceInventory.lambda$dump$1(printWriter, str, (Integer) obj, (List) obj2);
            }
        });
        printWriter.println("\n" + str + "Non-default devices for strategy:");
        this.mNonDefaultDevices.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda3
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AudioDeviceInventory.lambda$dump$2(printWriter, str, (Integer) obj, (List) obj2);
            }
        });
        printWriter.println("\n" + str + "Connected devices:");
        this.mConnectedDevices.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AudioDeviceInventory.lambda$dump$3(printWriter, str, (String) obj, (AudioDeviceInventory.DeviceInfo) obj2);
            }
        });
        printWriter.println("\n" + str + "APM Connected device (A2DP sink only):");
        this.mApmConnectedDevices.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda5
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AudioDeviceInventory.lambda$dump$4(printWriter, str, (Integer) obj, (String) obj2);
            }
        });
        this.mPreferredDevicesForCapturePreset.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AudioDeviceInventory.lambda$dump$5(printWriter, str, (Integer) obj, (List) obj2);
            }
        });
    }

    public static /* synthetic */ void lambda$dump$0(PrintWriter printWriter, Integer num) {
        printWriter.print(" 0x" + Integer.toHexString(num.intValue()));
    }

    public static /* synthetic */ void lambda$dump$1(PrintWriter printWriter, String str, Integer num, List list) {
        printWriter.println("  " + str + "strategy:" + num + " device:" + list);
    }

    public static /* synthetic */ void lambda$dump$2(PrintWriter printWriter, String str, Integer num, List list) {
        printWriter.println("  " + str + "strategy:" + num + " device:" + list);
    }

    public static /* synthetic */ void lambda$dump$3(PrintWriter printWriter, String str, String str2, DeviceInfo deviceInfo) {
        printWriter.println("  " + str + deviceInfo.toString());
    }

    public static /* synthetic */ void lambda$dump$4(PrintWriter printWriter, String str, Integer num, String str2) {
        printWriter.println("  " + str + " type:0x" + Integer.toHexString(num.intValue()) + " (" + AudioSystem.getDeviceName(num.intValue()) + ") addr:" + str2);
    }

    public static /* synthetic */ void lambda$dump$5(PrintWriter printWriter, String str, Integer num, List list) {
        printWriter.println("  " + str + "capturePreset:" + num + " devices:" + list);
    }

    public void onRestoreDevices() {
        synchronized (this.mDevicesLock) {
            for (DeviceInfo deviceInfo : this.mConnectedDevices.values()) {
                this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(deviceInfo.mDeviceType, deviceInfo.mDeviceAddress, deviceInfo.mDeviceName), 1, deviceInfo.mDeviceCodecFormat);
            }
        }
        synchronized (this.mPreferredDevices) {
            this.mPreferredDevices.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda13
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    AudioDeviceInventory.this.lambda$onRestoreDevices$6((Integer) obj, (List) obj2);
                }
            });
        }
        synchronized (this.mNonDefaultDevices) {
            this.mNonDefaultDevices.forEach(new BiConsumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda14
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    AudioDeviceInventory.this.lambda$onRestoreDevices$7((Integer) obj, (List) obj2);
                }
            });
        }
        synchronized (this.mPreferredDevicesForCapturePreset) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onRestoreDevices$6(Integer num, List list) {
        this.mAudioSystem.setDevicesRoleForStrategy(num.intValue(), 1, list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onRestoreDevices$7(Integer num, List list) {
        this.mAudioSystem.setDevicesRoleForStrategy(num.intValue(), 2, list);
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public void onSetBtActiveDevice(AudioDeviceBroker.BtDeviceInfo btDeviceInfo, int i) {
        String address = btDeviceInfo.mDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        String str = address;
        AudioService.sDeviceLogger.enqueue(new EventLogger.StringEvent("BT connected: addr=" + str + " profile=" + btDeviceInfo.mProfile + " state=" + btDeviceInfo.mState + " codec=" + AudioSystem.audioFormatToString(btDeviceInfo.mCodec)));
        new MediaMetrics.Item("audio.device.onSetBtActiveDevice").set(MediaMetrics.Property.STATUS, Integer.valueOf(btDeviceInfo.mProfile)).set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(btDeviceInfo.mAudioSystemDevice)).set(MediaMetrics.Property.ADDRESS, str).set(MediaMetrics.Property.ENCODING, AudioSystem.audioFormatToString(btDeviceInfo.mCodec)).set(MediaMetrics.Property.EVENT, "onSetBtActiveDevice").set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(i)).set(MediaMetrics.Property.STATE, btDeviceInfo.mState == 2 ? "connected" : "disconnected").record();
        synchronized (this.mDevicesLock) {
            DeviceInfo deviceInfo = this.mConnectedDevices.get(DeviceInfo.makeDeviceListKey(btDeviceInfo.mAudioSystemDevice, str));
            boolean z = true;
            boolean z2 = deviceInfo != null;
            boolean z3 = z2 && btDeviceInfo.mState != 2;
            if (z2 || btDeviceInfo.mState != 2) {
                z = false;
            }
            int i2 = btDeviceInfo.mProfile;
            if (i2 != 2) {
                if (i2 != 11) {
                    if (i2 != 26) {
                        if (i2 != 21) {
                            if (i2 != 22) {
                                throw new IllegalArgumentException("Invalid profile " + BluetoothProfile.getProfileName(btDeviceInfo.mProfile));
                            }
                        } else if (z3) {
                            lambda$disconnectHearingAid$13(str);
                        } else if (z) {
                            makeHearingAidDeviceAvailable(str, BtHelper.getName(btDeviceInfo.mDevice), i, "onSetBtActiveDevice");
                        }
                    }
                    if (z3) {
                        makeLeAudioDeviceUnavailableNow(str, btDeviceInfo.mAudioSystemDevice);
                    } else if (z) {
                        String name = BtHelper.getName(btDeviceInfo.mDevice);
                        int i3 = btDeviceInfo.mVolume;
                        makeLeAudioDeviceAvailable(str, name, i, i3 == -1 ? -1 : i3 * 10, btDeviceInfo.mAudioSystemDevice, "onSetBtActiveDevice");
                    }
                } else if (z3) {
                    lambda$disconnectA2dpSink$11(str);
                } else if (z) {
                    makeA2dpSrcAvailable(str);
                }
            } else if (z3) {
                makeA2dpDeviceUnavailableNow(str, deviceInfo.mDeviceCodecFormat);
            } else if (z) {
                int i4 = btDeviceInfo.mVolume;
                if (i4 != -1) {
                    this.mDeviceBroker.postSetVolumeIndexOnDevice(3, i4 * 10, btDeviceInfo.mAudioSystemDevice, "onSetBtActiveDevice");
                }
                makeA2dpDeviceAvailable(str, BtHelper.getName(btDeviceInfo.mDevice), "onSetBtActiveDevice", btDeviceInfo.mCodec);
            }
        }
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public void onBluetoothA2dpDeviceConfigChange(BtHelper.BluetoothA2dpDeviceInfo bluetoothA2dpDeviceInfo, int i) {
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.onBluetoothA2dpDeviceConfigChange").set(MediaMetrics.Property.EVENT, BtHelper.a2dpDeviceEventToString(i));
        BluetoothDevice btDevice = bluetoothA2dpDeviceInfo.getBtDevice();
        if (btDevice == null) {
            item.set(MediaMetrics.Property.EARLY_RETURN, "btDevice null").record();
            return;
        }
        int volume = bluetoothA2dpDeviceInfo.getVolume();
        int codec = bluetoothA2dpDeviceInfo.getCodec();
        String address = btDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        EventLogger eventLogger = AudioService.sDeviceLogger;
        eventLogger.enqueue(new EventLogger.StringEvent("onBluetoothA2dpDeviceConfigChange addr=" + address + " event=" + BtHelper.a2dpDeviceEventToString(i)));
        synchronized (this.mDevicesLock) {
            if (this.mDeviceBroker.hasScheduledA2dpConnection(btDevice)) {
                eventLogger.enqueue(new EventLogger.StringEvent("A2dp config change ignored (scheduled connection change)").printLog("AS.AudioDeviceInventory"));
                item.set(MediaMetrics.Property.EARLY_RETURN, "A2dp config change ignored").record();
                return;
            }
            String makeDeviceListKey = DeviceInfo.makeDeviceListKey(128, address);
            DeviceInfo deviceInfo = this.mConnectedDevices.get(makeDeviceListKey);
            if (deviceInfo == null) {
                Log.e("AS.AudioDeviceInventory", "invalid null DeviceInfo in onBluetoothA2dpDeviceConfigChange");
                item.set(MediaMetrics.Property.EARLY_RETURN, "null DeviceInfo").record();
                return;
            }
            item.set(MediaMetrics.Property.ADDRESS, address).set(MediaMetrics.Property.ENCODING, AudioSystem.audioFormatToString(codec)).set(MediaMetrics.Property.INDEX, Integer.valueOf(volume)).set(MediaMetrics.Property.NAME, deviceInfo.mDeviceName);
            if (i == 1) {
                if (volume != -1) {
                    this.mDeviceBroker.postSetVolumeIndexOnDevice(3, volume * 10, 128, "onBluetoothA2dpDeviceConfigChange");
                }
            } else if (i == 0 && deviceInfo.mDeviceCodecFormat != codec) {
                deviceInfo.mDeviceCodecFormat = codec;
                this.mConnectedDevices.replace(makeDeviceListKey, deviceInfo);
            }
            if (this.mAudioSystem.handleDeviceConfigChange(128, address, BtHelper.getName(btDevice), codec) != 0) {
                eventLogger.enqueue(new EventLogger.StringEvent("APM handleDeviceConfigChange failed for A2DP device addr=" + address + " codec=" + AudioSystem.audioFormatToString(codec)).printLog("AS.AudioDeviceInventory"));
                setBluetoothActiveDevice(new AudioDeviceBroker.BtDeviceInfo(btDevice, 2, 0, this.mDeviceBroker.getDeviceForStream(3), 128));
            } else {
                eventLogger.enqueue(new EventLogger.StringEvent("APM handleDeviceConfigChange success for A2DP device addr=" + address + " codec=" + AudioSystem.audioFormatToString(codec)).printLog("AS.AudioDeviceInventory"));
            }
            item.record();
        }
    }

    public void onMakeA2dpDeviceUnavailableNow(String str, int i) {
        synchronized (this.mDevicesLock) {
            makeA2dpDeviceUnavailableNow(str, i);
        }
    }

    public void onMakeLeAudioDeviceUnavailableNow(String str, int i) {
        synchronized (this.mDevicesLock) {
            makeLeAudioDeviceUnavailableNow(str, i);
        }
    }

    public void onReportNewRoutes() {
        AudioRoutesInfo audioRoutesInfo;
        int beginBroadcast = this.mRoutesObservers.beginBroadcast();
        if (beginBroadcast > 0) {
            new MediaMetrics.Item("audio.device.onReportNewRoutes").set(MediaMetrics.Property.OBSERVERS, Integer.valueOf(beginBroadcast)).record();
            synchronized (this.mCurAudioRoutes) {
                audioRoutesInfo = new AudioRoutesInfo(this.mCurAudioRoutes);
            }
            while (beginBroadcast > 0) {
                beginBroadcast--;
                try {
                    this.mRoutesObservers.getBroadcastItem(beginBroadcast).dispatchAudioRoutesChanged(audioRoutesInfo);
                } catch (RemoteException unused) {
                }
            }
        }
        this.mRoutesObservers.finishBroadcast();
        this.mDeviceBroker.postObserveDevicesForAllStreams();
    }

    static {
        HashSet hashSet = new HashSet();
        DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG_SET = hashSet;
        hashSet.add(4);
        hashSet.add(8);
        Integer valueOf = Integer.valueOf((int) IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES);
        hashSet.add(valueOf);
        hashSet.addAll(AudioSystem.DEVICE_OUT_ALL_USB_SET);
        HashSet hashSet2 = new HashSet();
        BECOMING_NOISY_INTENT_DEVICES_SET = hashSet2;
        hashSet2.add(4);
        hashSet2.add(8);
        hashSet2.add(1024);
        hashSet2.add(Integer.valueOf((int) IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES));
        hashSet2.add(valueOf);
        hashSet2.add(134217728);
        hashSet2.add(536870912);
        hashSet2.add(536870914);
        hashSet2.addAll(AudioSystem.DEVICE_OUT_ALL_A2DP_SET);
        hashSet2.addAll(AudioSystem.DEVICE_OUT_ALL_USB_SET);
        hashSet2.addAll(AudioSystem.DEVICE_OUT_ALL_BLE_SET);
    }

    public void onSetWiredDeviceConnectionState(final WiredDeviceConnectionState wiredDeviceConnectionState) {
        AudioDeviceInfo audioDeviceInfo;
        int internalType = wiredDeviceConnectionState.mAttributes.getInternalType();
        AudioService.sDeviceLogger.enqueue(new EventLogger.Event(wiredDeviceConnectionState) { // from class: com.android.server.audio.AudioServiceEvents$WiredDevConnectEvent
            public final AudioDeviceInventory.WiredDeviceConnectionState mState;

            {
                this.mState = wiredDeviceConnectionState;
            }

            @Override // com.android.server.utils.EventLogger.Event
            public String eventToString() {
                return "setWiredDeviceConnectionState( type:" + Integer.toHexString(this.mState.mAttributes.getInternalType()) + " state:" + AudioSystem.deviceStateToString(this.mState.mState) + " addr:" + this.mState.mAttributes.getAddress() + " name:" + this.mState.mAttributes.getName() + ") from " + this.mState.mCaller;
            }
        });
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.onSetWiredDeviceConnectionState").set(MediaMetrics.Property.ADDRESS, wiredDeviceConnectionState.mAttributes.getAddress()).set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(internalType)).set(MediaMetrics.Property.STATE, wiredDeviceConnectionState.mState == 0 ? "disconnected" : "connected");
        if (wiredDeviceConnectionState.mState == 0 && AudioSystem.DEVICE_OUT_ALL_USB_SET.contains(Integer.valueOf(wiredDeviceConnectionState.mAttributes.getInternalType()))) {
            AudioDeviceInfo[] devicesStatic = AudioManager.getDevicesStatic(2);
            int length = devicesStatic.length;
            for (int i = 0; i < length; i++) {
                audioDeviceInfo = devicesStatic[i];
                if (audioDeviceInfo.getInternalType() == wiredDeviceConnectionState.mAttributes.getInternalType()) {
                    break;
                }
            }
        }
        audioDeviceInfo = null;
        synchronized (this.mDevicesLock) {
            boolean z = true;
            if (wiredDeviceConnectionState.mState == 0 && DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG_SET.contains(Integer.valueOf(internalType))) {
                this.mDeviceBroker.setBluetoothA2dpOnInt(true, false, "onSetWiredDeviceConnectionState state DISCONNECTED");
            }
            AudioDeviceAttributes audioDeviceAttributes = wiredDeviceConnectionState.mAttributes;
            if (wiredDeviceConnectionState.mState != 1) {
                z = false;
            }
            if (!handleDeviceConnection(audioDeviceAttributes, z, wiredDeviceConnectionState.mForTest)) {
                item.set(MediaMetrics.Property.EARLY_RETURN, "change of connection state failed").record();
                return;
            }
            if (wiredDeviceConnectionState.mState != 0) {
                if (DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG_SET.contains(Integer.valueOf(internalType))) {
                    this.mDeviceBroker.setBluetoothA2dpOnInt(false, false, "onSetWiredDeviceConnectionState state not DISCONNECTED");
                }
                this.mDeviceBroker.checkMusicActive(internalType, wiredDeviceConnectionState.mCaller);
            }
            if (internalType == 1024) {
                this.mDeviceBroker.checkVolumeCecOnHdmiConnection(wiredDeviceConnectionState.mState, wiredDeviceConnectionState.mCaller);
            }
            if (wiredDeviceConnectionState.mState == 0 && AudioSystem.DEVICE_OUT_ALL_USB_SET.contains(Integer.valueOf(wiredDeviceConnectionState.mAttributes.getInternalType()))) {
                this.mDeviceBroker.dispatchPreferredMixerAttributesChangedCausedByDeviceRemoved(audioDeviceInfo);
            }
            sendDeviceConnectionIntent(internalType, wiredDeviceConnectionState.mState, wiredDeviceConnectionState.mAttributes.getAddress(), wiredDeviceConnectionState.mAttributes.getName());
            updateAudioRoutes(internalType, wiredDeviceConnectionState.mState);
            item.record();
        }
    }

    public void onToggleHdmi() {
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.onToggleHdmi").set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(1024));
        synchronized (this.mDevicesLock) {
            if (this.mConnectedDevices.get(DeviceInfo.makeDeviceListKey(1024, "")) == null) {
                Log.e("AS.AudioDeviceInventory", "invalid null DeviceInfo in onToggleHdmi");
                item.set(MediaMetrics.Property.EARLY_RETURN, "invalid null DeviceInfo").record();
                return;
            }
            setWiredDeviceConnectionState(new AudioDeviceAttributes(1024, ""), 0, PackageManagerShellCommandDataLoader.PACKAGE);
            setWiredDeviceConnectionState(new AudioDeviceAttributes(1024, ""), 1, PackageManagerShellCommandDataLoader.PACKAGE);
            item.record();
        }
    }

    public void onSaveSetPreferredDevices(int i, List<AudioDeviceAttributes> list) {
        this.mPreferredDevices.put(Integer.valueOf(i), list);
        List<AudioDeviceAttributes> list2 = this.mNonDefaultDevices.get(Integer.valueOf(i));
        if (list2 != null) {
            list2.removeAll(list);
            if (list2.isEmpty()) {
                this.mNonDefaultDevices.remove(Integer.valueOf(i));
            } else {
                this.mNonDefaultDevices.put(Integer.valueOf(i), list2);
            }
            dispatchNonDefaultDevice(i, list2);
        }
        dispatchPreferredDevice(i, list);
    }

    public void onSaveRemovePreferredDevices(int i) {
        this.mPreferredDevices.remove(Integer.valueOf(i));
        dispatchPreferredDevice(i, new ArrayList());
    }

    public void onSaveSetDeviceAsNonDefault(int i, AudioDeviceAttributes audioDeviceAttributes) {
        List<AudioDeviceAttributes> list = this.mNonDefaultDevices.get(Integer.valueOf(i));
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.contains(audioDeviceAttributes)) {
            list.add(audioDeviceAttributes);
        }
        this.mNonDefaultDevices.put(Integer.valueOf(i), list);
        dispatchNonDefaultDevice(i, list);
        List<AudioDeviceAttributes> list2 = this.mPreferredDevices.get(Integer.valueOf(i));
        if (list2 != null) {
            list2.remove(audioDeviceAttributes);
            this.mPreferredDevices.put(Integer.valueOf(i), list2);
            dispatchPreferredDevice(i, list2);
        }
    }

    public void onSaveRemoveDeviceAsNonDefault(int i, AudioDeviceAttributes audioDeviceAttributes) {
        List<AudioDeviceAttributes> list = this.mNonDefaultDevices.get(Integer.valueOf(i));
        if (list != null) {
            list.remove(audioDeviceAttributes);
            this.mNonDefaultDevices.put(Integer.valueOf(i), list);
            dispatchNonDefaultDevice(i, list);
        }
    }

    public void onSaveSetPreferredDevicesForCapturePreset(int i, List<AudioDeviceAttributes> list) {
        this.mPreferredDevicesForCapturePreset.put(Integer.valueOf(i), list);
        dispatchDevicesRoleForCapturePreset(i, 1, list);
    }

    public void onSaveClearPreferredDevicesForCapturePreset(int i) {
        this.mPreferredDevicesForCapturePreset.remove(Integer.valueOf(i));
        dispatchDevicesRoleForCapturePreset(i, 1, new ArrayList());
    }

    public int setPreferredDevicesForStrategySync(int i, List<AudioDeviceAttributes> list) {
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            EventLogger eventLogger = AudioService.sDeviceLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("setPreferredDevicesForStrategySync, strategy: " + i + " devices: " + list).printLog("AS.AudioDeviceInventory"));
            int devicesRoleForStrategy = this.mAudioSystem.setDevicesRoleForStrategy(i, 1, list);
            if (create != null) {
                create.close();
            }
            if (devicesRoleForStrategy == 0) {
                this.mDeviceBroker.postSaveSetPreferredDevicesForStrategy(i, list);
            }
            return devicesRoleForStrategy;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public int removePreferredDevicesForStrategySync(int i) {
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            EventLogger eventLogger = AudioService.sDeviceLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("removePreferredDevicesForStrategySync, strategy: " + i).printLog("AS.AudioDeviceInventory"));
            int clearDevicesRoleForStrategy = this.mAudioSystem.clearDevicesRoleForStrategy(i, 1);
            if (create != null) {
                create.close();
            }
            if (clearDevicesRoleForStrategy == 0) {
                this.mDeviceBroker.postSaveRemovePreferredDevicesForStrategy(i);
            }
            return clearDevicesRoleForStrategy;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public int setDeviceAsNonDefaultForStrategySync(int i, AudioDeviceAttributes audioDeviceAttributes) {
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(audioDeviceAttributes);
            EventLogger eventLogger = AudioService.sDeviceLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("setDeviceAsNonDefaultForStrategySync, strategy: " + i + " device: " + audioDeviceAttributes).printLog("AS.AudioDeviceInventory"));
            int devicesRoleForStrategy = this.mAudioSystem.setDevicesRoleForStrategy(i, 2, arrayList);
            if (create != null) {
                create.close();
            }
            if (devicesRoleForStrategy == 0) {
                this.mDeviceBroker.postSaveSetDeviceAsNonDefaultForStrategy(i, audioDeviceAttributes);
            }
            return devicesRoleForStrategy;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public int removeDeviceAsNonDefaultForStrategySync(int i, AudioDeviceAttributes audioDeviceAttributes) {
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(audioDeviceAttributes);
            EventLogger eventLogger = AudioService.sDeviceLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("removeDeviceAsNonDefaultForStrategySync, strategy: " + i + " devices: " + audioDeviceAttributes).printLog("AS.AudioDeviceInventory"));
            int removeDevicesRoleForStrategy = this.mAudioSystem.removeDevicesRoleForStrategy(i, 2, arrayList);
            if (create != null) {
                create.close();
            }
            if (removeDevicesRoleForStrategy == 0) {
                this.mDeviceBroker.postSaveRemoveDeviceAsNonDefaultForStrategy(i, audioDeviceAttributes);
            }
            return removeDevicesRoleForStrategy;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void registerStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) {
        this.mPrefDevDispatchers.register(iStrategyPreferredDevicesDispatcher);
    }

    public void unregisterStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) {
        this.mPrefDevDispatchers.unregister(iStrategyPreferredDevicesDispatcher);
    }

    public void registerStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) {
        this.mNonDefDevDispatchers.register(iStrategyNonDefaultDevicesDispatcher);
    }

    public void unregisterStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) {
        this.mNonDefDevDispatchers.unregister(iStrategyNonDefaultDevicesDispatcher);
    }

    public int setPreferredDevicesForCapturePresetSync(int i, List<AudioDeviceAttributes> list) {
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            int devicesRoleForCapturePreset = this.mAudioSystem.setDevicesRoleForCapturePreset(i, 1, list);
            if (create != null) {
                create.close();
            }
            if (devicesRoleForCapturePreset == 0) {
                this.mDeviceBroker.postSaveSetPreferredDevicesForCapturePreset(i, list);
            }
            return devicesRoleForCapturePreset;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public int clearPreferredDevicesForCapturePresetSync(int i) {
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            int clearDevicesRoleForCapturePreset = this.mAudioSystem.clearDevicesRoleForCapturePreset(i, 1);
            if (create != null) {
                create.close();
            }
            if (clearDevicesRoleForCapturePreset == 0) {
                this.mDeviceBroker.postSaveClearPreferredDevicesForCapturePreset(i);
            }
            return clearDevicesRoleForCapturePreset;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void registerCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) {
        this.mDevRoleCapturePresetDispatchers.register(iCapturePresetDevicesRoleDispatcher);
    }

    public void unregisterCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) {
        this.mDevRoleCapturePresetDispatchers.unregister(iCapturePresetDevicesRoleDispatcher);
    }

    public boolean isDeviceConnected(AudioDeviceAttributes audioDeviceAttributes) {
        boolean z;
        String makeDeviceListKey = DeviceInfo.makeDeviceListKey(audioDeviceAttributes.getInternalType(), audioDeviceAttributes.getAddress());
        synchronized (this.mDevicesLock) {
            z = this.mConnectedDevices.get(makeDeviceListKey) != null;
        }
        return z;
    }

    public boolean handleDeviceConnection(AudioDeviceAttributes audioDeviceAttributes, boolean z, boolean z2) {
        int internalType = audioDeviceAttributes.getInternalType();
        String address = audioDeviceAttributes.getAddress();
        String name = audioDeviceAttributes.getName();
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.handleDeviceConnection").set(MediaMetrics.Property.ADDRESS, address).set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(internalType)).set(MediaMetrics.Property.MODE, z ? "connect" : "disconnect").set(MediaMetrics.Property.NAME, name);
        synchronized (this.mDevicesLock) {
            String makeDeviceListKey = DeviceInfo.makeDeviceListKey(internalType, address);
            DeviceInfo deviceInfo = this.mConnectedDevices.get(makeDeviceListKey);
            boolean z3 = deviceInfo != null;
            if (z && !z3) {
                int deviceConnectionState = z2 ? 0 : this.mAudioSystem.setDeviceConnectionState(audioDeviceAttributes, 1, 0);
                if (deviceConnectionState != 0) {
                    String str = "not connecting device 0x" + Integer.toHexString(internalType) + " due to command error " + deviceConnectionState;
                    Slog.e("AS.AudioDeviceInventory", str);
                    item.set(MediaMetrics.Property.EARLY_RETURN, str).set(MediaMetrics.Property.STATE, "disconnected").record();
                    return false;
                }
                this.mConnectedDevices.put(makeDeviceListKey, new DeviceInfo(internalType, name, address, 0));
                this.mDeviceBroker.postAccessoryPlugMediaUnmute(internalType);
                item.set(MediaMetrics.Property.STATE, "connected").record();
                return true;
            } else if (!z && z3) {
                this.mAudioSystem.setDeviceConnectionState(audioDeviceAttributes, 0, 0);
                this.mConnectedDevices.remove(makeDeviceListKey);
                item.set(MediaMetrics.Property.STATE, "connected").record();
                return true;
            } else {
                Log.w("AS.AudioDeviceInventory", "handleDeviceConnection() failed, deviceKey=" + makeDeviceListKey + ", deviceSpec=" + deviceInfo + ", connect=" + z);
                item.set(MediaMetrics.Property.STATE, "disconnected").record();
                return false;
            }
        }
    }

    public final void disconnectA2dp() {
        synchronized (this.mDevicesLock) {
            final ArraySet arraySet = new ArraySet();
            this.mConnectedDevices.values().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda11
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectA2dp$8(arraySet, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            new MediaMetrics.Item("audio.device.disconnectA2dp").record();
            if (arraySet.size() > 0) {
                final int checkSendBecomingNoisyIntentInt = checkSendBecomingNoisyIntentInt(128, 0, 0);
                arraySet.stream().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda12
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AudioDeviceInventory.this.lambda$disconnectA2dp$9(checkSendBecomingNoisyIntentInt, (String) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$disconnectA2dp$8(ArraySet arraySet, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == 128) {
            arraySet.add(deviceInfo.mDeviceAddress);
        }
    }

    public final void disconnectA2dpSink() {
        synchronized (this.mDevicesLock) {
            final ArraySet arraySet = new ArraySet();
            this.mConnectedDevices.values().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectA2dpSink$10(arraySet, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            new MediaMetrics.Item("audio.device.disconnectA2dpSink").record();
            arraySet.stream().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda10
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioDeviceInventory.this.lambda$disconnectA2dpSink$11((String) obj);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$disconnectA2dpSink$10(ArraySet arraySet, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == -2147352576) {
            arraySet.add(deviceInfo.mDeviceAddress);
        }
    }

    public final void disconnectHearingAid() {
        synchronized (this.mDevicesLock) {
            final ArraySet arraySet = new ArraySet();
            this.mConnectedDevices.values().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda15
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectHearingAid$12(arraySet, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            new MediaMetrics.Item("audio.device.disconnectHearingAid").record();
            if (arraySet.size() > 0) {
                checkSendBecomingNoisyIntentInt(134217728, 0, 0);
                arraySet.stream().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda16
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AudioDeviceInventory.this.lambda$disconnectHearingAid$13((String) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$disconnectHearingAid$12(ArraySet arraySet, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == 134217728) {
            arraySet.add(deviceInfo.mDeviceAddress);
        }
    }

    public synchronized void onBtProfileDisconnected(int i) {
        if (i == 2) {
            disconnectA2dp();
        } else if (i == 11) {
            disconnectA2dpSink();
        } else if (i == 26) {
            disconnectLeAudioBroadcast();
        } else if (i == 21) {
            disconnectHearingAid();
        } else if (i == 22) {
            disconnectLeAudioUnicast();
        } else {
            Log.e("AS.AudioDeviceInventory", "onBtProfileDisconnected: Not a valid profile to disconnect " + BluetoothProfile.getProfileName(i));
        }
    }

    public void disconnectLeAudio(final int i) {
        if (i != 536870912 && i != 536870914) {
            Log.e("AS.AudioDeviceInventory", "disconnectLeAudio: Can't disconnect not LE Audio device " + i);
            return;
        }
        synchronized (this.mDevicesLock) {
            final ArraySet arraySet = new ArraySet();
            this.mConnectedDevices.values().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectLeAudio$14(i, arraySet, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            new MediaMetrics.Item("audio.device.disconnectLeAudio").record();
            if (arraySet.size() > 0) {
                final int checkSendBecomingNoisyIntentInt = checkSendBecomingNoisyIntentInt(i, 0, 0);
                arraySet.stream().forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda8
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AudioDeviceInventory.this.lambda$disconnectLeAudio$15(i, checkSendBecomingNoisyIntentInt, (String) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$disconnectLeAudio$14(int i, ArraySet arraySet, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == i) {
            arraySet.add(deviceInfo.mDeviceAddress);
        }
    }

    public void disconnectLeAudioUnicast() {
        disconnectLeAudio(536870912);
    }

    public void disconnectLeAudioBroadcast() {
        disconnectLeAudio(536870914);
    }

    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) {
        AudioRoutesInfo audioRoutesInfo;
        synchronized (this.mCurAudioRoutes) {
            audioRoutesInfo = new AudioRoutesInfo(this.mCurAudioRoutes);
            this.mRoutesObservers.register(iAudioRoutesObserver);
        }
        return audioRoutesInfo;
    }

    public AudioRoutesInfo getCurAudioRoutes() {
        return this.mCurAudioRoutes;
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    @VisibleForTesting
    public int setBluetoothActiveDevice(AudioDeviceBroker.BtDeviceInfo btDeviceInfo) {
        int i;
        int i2;
        synchronized (this.mDevicesLock) {
            if (btDeviceInfo.mSupprNoisy || !((((i2 = btDeviceInfo.mProfile) == 22 || i2 == 26) && btDeviceInfo.mIsLeOutput) || i2 == 21 || i2 == 2)) {
                i = 0;
            } else {
                i = checkSendBecomingNoisyIntentInt(btDeviceInfo.mAudioSystemDevice, btDeviceInfo.mState == 2 ? 1 : 0, btDeviceInfo.mMusicDevice);
            }
            this.mDeviceBroker.postBluetoothActiveDevice(btDeviceInfo, i);
            if (btDeviceInfo.mProfile == 21 && btDeviceInfo.mState == 2) {
                this.mDeviceBroker.setForceUse_Async(1, 0, "HEARING_AID set to CONNECTED");
            }
        }
        return i;
    }

    public int setWiredDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i, String str) {
        int checkSendBecomingNoisyIntentInt;
        synchronized (this.mDevicesLock) {
            checkSendBecomingNoisyIntentInt = checkSendBecomingNoisyIntentInt(audioDeviceAttributes.getInternalType(), i, 0);
            this.mDeviceBroker.postSetWiredDeviceConnectionState(new WiredDeviceConnectionState(audioDeviceAttributes, i, str), checkSendBecomingNoisyIntentInt);
        }
        return checkSendBecomingNoisyIntentInt;
    }

    public void setTestDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i) {
        WiredDeviceConnectionState wiredDeviceConnectionState = new WiredDeviceConnectionState(audioDeviceAttributes, i, "com.android.server.audio");
        wiredDeviceConnectionState.mForTest = true;
        onSetWiredDeviceConnectionState(wiredDeviceConnectionState);
    }

    @GuardedBy({"mDevicesLock"})
    public final void makeA2dpDeviceAvailable(String str, String str2, String str3, int i) {
        this.mDeviceBroker.setBluetoothA2dpOnInt(true, true, str3);
        int deviceConnectionState = this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(128, str, str2), 1, i);
        if (deviceConnectionState != 0) {
            EventLogger eventLogger = AudioService.sDeviceLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("APM failed to make available A2DP device addr=" + str + " error=" + deviceConnectionState).printLog("AS.AudioDeviceInventory"));
        } else {
            EventLogger eventLogger2 = AudioService.sDeviceLogger;
            eventLogger2.enqueue(new EventLogger.StringEvent("A2DP device addr=" + str + " now available").printLog("AS.AudioDeviceInventory"));
        }
        this.mAudioSystem.setParameters("A2dpSuspended=false");
        DeviceInfo deviceInfo = new DeviceInfo(128, str2, str, i, UuidUtils.uuidFromAudioDeviceAttributes(new AudioDeviceAttributes(128, str)));
        String key = deviceInfo.getKey();
        this.mConnectedDevices.put(key, deviceInfo);
        this.mApmConnectedDevices.put(128, key);
        this.mDeviceBroker.postAccessoryPlugMediaUnmute(128);
        setCurrentAudioRouteNameIfPossible(str2, true);
    }

    @GuardedBy({"mDevicesLock"})
    public final void makeA2dpDeviceUnavailableNow(String str, int i) {
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.a2dp." + str).set(MediaMetrics.Property.ENCODING, AudioSystem.audioFormatToString(i)).set(MediaMetrics.Property.EVENT, "makeA2dpDeviceUnavailableNow");
        if (str == null) {
            item.set(MediaMetrics.Property.EARLY_RETURN, "address null").record();
            return;
        }
        String makeDeviceListKey = DeviceInfo.makeDeviceListKey(128, str);
        this.mConnectedDevices.remove(makeDeviceListKey);
        if (!makeDeviceListKey.equals(this.mApmConnectedDevices.get(128))) {
            EventLogger eventLogger = AudioService.sDeviceLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("A2DP device " + str + " made unavailable, was not used").printLog("AS.AudioDeviceInventory"));
            item.set(MediaMetrics.Property.EARLY_RETURN, "A2DP device made unavailable, was not used").record();
            return;
        }
        this.mDeviceBroker.clearAvrcpAbsoluteVolumeSupported();
        int deviceConnectionState = this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(128, str), 0, i);
        if (deviceConnectionState != 0) {
            EventLogger eventLogger2 = AudioService.sDeviceLogger;
            eventLogger2.enqueue(new EventLogger.StringEvent("APM failed to make unavailable A2DP device addr=" + str + " error=" + deviceConnectionState).printLog("AS.AudioDeviceInventory"));
        } else {
            EventLogger eventLogger3 = AudioService.sDeviceLogger;
            eventLogger3.enqueue(new EventLogger.StringEvent("A2DP device addr=" + str + " made unavailable").printLog("AS.AudioDeviceInventory"));
        }
        this.mApmConnectedDevices.remove(128);
        setCurrentAudioRouteNameIfPossible(null, true);
        item.record();
    }

    @GuardedBy({"mDevicesLock"})
    /* renamed from: makeA2dpDeviceUnavailableLater */
    public final void lambda$disconnectA2dp$9(String str, int i) {
        this.mAudioSystem.setParameters("A2dpSuspended=true");
        String makeDeviceListKey = DeviceInfo.makeDeviceListKey(128, str);
        DeviceInfo deviceInfo = this.mConnectedDevices.get(makeDeviceListKey);
        int i2 = deviceInfo != null ? deviceInfo.mDeviceCodecFormat : 0;
        this.mConnectedDevices.remove(makeDeviceListKey);
        this.mDeviceBroker.setA2dpTimeout(str, i2, i);
    }

    @GuardedBy({"mDevicesLock"})
    public final void makeA2dpSrcAvailable(String str) {
        this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(-2147352576, str), 1, 0);
        this.mConnectedDevices.put(DeviceInfo.makeDeviceListKey(-2147352576, str), new DeviceInfo(-2147352576, "", str, 0));
    }

    @GuardedBy({"mDevicesLock"})
    /* renamed from: makeA2dpSrcUnavailable */
    public final void lambda$disconnectA2dpSink$11(String str) {
        this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(-2147352576, str), 0, 0);
        this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(-2147352576, str));
    }

    @GuardedBy({"mDevicesLock"})
    public final void makeHearingAidDeviceAvailable(String str, String str2, int i, String str3) {
        this.mDeviceBroker.postSetHearingAidVolumeIndex(this.mDeviceBroker.getVssVolumeForDevice(i, 134217728), i);
        this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(134217728, str, str2), 1, 0);
        this.mConnectedDevices.put(DeviceInfo.makeDeviceListKey(134217728, str), new DeviceInfo(134217728, str2, str, 0));
        this.mDeviceBroker.postAccessoryPlugMediaUnmute(134217728);
        this.mDeviceBroker.postApplyVolumeOnDevice(i, 134217728, "makeHearingAidDeviceAvailable");
        setCurrentAudioRouteNameIfPossible(str2, false);
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.makeHearingAidDeviceAvailable");
        MediaMetrics.Key key = MediaMetrics.Property.ADDRESS;
        if (str == null) {
            str = "";
        }
        item.set(key, str).set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(134217728)).set(MediaMetrics.Property.NAME, str2).set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(i)).record();
    }

    @GuardedBy({"mDevicesLock"})
    /* renamed from: makeHearingAidDeviceUnavailable */
    public final void lambda$disconnectHearingAid$13(String str) {
        this.mAudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(134217728, str), 0, 0);
        this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(134217728, str));
        setCurrentAudioRouteNameIfPossible(null, false);
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.makeHearingAidDeviceUnavailable");
        MediaMetrics.Key key = MediaMetrics.Property.ADDRESS;
        if (str == null) {
            str = "";
        }
        item.set(key, str).set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(134217728)).record();
    }

    public boolean isHearingAidConnected() {
        synchronized (this.mDevicesLock) {
            for (DeviceInfo deviceInfo : this.mConnectedDevices.values()) {
                if (deviceInfo.mDeviceType == 134217728) {
                    return true;
                }
            }
            return false;
        }
    }

    @GuardedBy({"mDevicesLock"})
    public final void makeLeAudioDeviceAvailable(String str, String str2, int i, int i2, int i3, String str3) {
        if (i3 != 0) {
            this.mDeviceBroker.setBluetoothA2dpOnInt(true, false, str3);
            int deviceConnectionState = AudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(i3, str, str2), 1, 0);
            if (deviceConnectionState != 0) {
                EventLogger eventLogger = AudioService.sDeviceLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("APM failed to make available LE Audio device addr=" + str + " error=" + deviceConnectionState).printLog("AS.AudioDeviceInventory"));
            } else {
                EventLogger eventLogger2 = AudioService.sDeviceLogger;
                eventLogger2.enqueue(new EventLogger.StringEvent("LE Audio device addr=" + str + " now available").printLog("AS.AudioDeviceInventory"));
            }
            this.mConnectedDevices.put(DeviceInfo.makeDeviceListKey(i3, str), new DeviceInfo(i3, str2, str, 0));
            this.mDeviceBroker.postAccessoryPlugMediaUnmute(i3);
            setCurrentAudioRouteNameIfPossible(str2, false);
        }
        if (i == -1) {
            return;
        }
        if (i2 == -1) {
            i2 = this.mDeviceBroker.getVssVolumeForDevice(i, i3);
        }
        this.mDeviceBroker.postSetLeAudioVolumeIndex(i2, this.mDeviceBroker.getMaxVssVolumeForStream(i), i);
        this.mDeviceBroker.postApplyVolumeOnDevice(i, i3, "makeLeAudioDeviceAvailable");
    }

    @GuardedBy({"mDevicesLock"})
    public final void makeLeAudioDeviceUnavailableNow(String str, int i) {
        if (i != 0) {
            int deviceConnectionState = AudioSystem.setDeviceConnectionState(new AudioDeviceAttributes(i, str), 0, 0);
            if (deviceConnectionState != 0) {
                EventLogger eventLogger = AudioService.sDeviceLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("APM failed to make unavailable LE Audio device addr=" + str + " error=" + deviceConnectionState).printLog("AS.AudioDeviceInventory"));
            } else {
                EventLogger eventLogger2 = AudioService.sDeviceLogger;
                eventLogger2.enqueue(new EventLogger.StringEvent("LE Audio device addr=" + str + " made unavailable").printLog("AS.AudioDeviceInventory"));
            }
            this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(i, str));
        }
        setCurrentAudioRouteNameIfPossible(null, false);
    }

    @GuardedBy({"mDevicesLock"})
    /* renamed from: makeLeAudioDeviceUnavailableLater */
    public final void lambda$disconnectLeAudio$15(String str, int i, int i2) {
        this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(i, str));
        this.mDeviceBroker.setLeAudioTimeout(str, i, i2);
    }

    @GuardedBy({"mDevicesLock"})
    public final void setCurrentAudioRouteNameIfPossible(String str, boolean z) {
        synchronized (this.mCurAudioRoutes) {
            if (TextUtils.equals(this.mCurAudioRoutes.bluetoothName, str)) {
                return;
            }
            if (str != null || !isCurrentDeviceConnected()) {
                this.mCurAudioRoutes.bluetoothName = str;
                this.mDeviceBroker.postReportNewRoutes(z);
            }
        }
    }

    @GuardedBy({"mDevicesLock"})
    public final boolean isCurrentDeviceConnected() {
        return this.mConnectedDevices.values().stream().anyMatch(new Predicate() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda17
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isCurrentDeviceConnected$16;
                lambda$isCurrentDeviceConnected$16 = AudioDeviceInventory.this.lambda$isCurrentDeviceConnected$16((AudioDeviceInventory.DeviceInfo) obj);
                return lambda$isCurrentDeviceConnected$16;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isCurrentDeviceConnected$16(DeviceInfo deviceInfo) {
        return TextUtils.equals(deviceInfo.mDeviceName, this.mCurAudioRoutes.bluetoothName);
    }

    @GuardedBy({"mDevicesLock"})
    public final int checkSendBecomingNoisyIntentInt(int i, int i2, int i3) {
        MediaMetrics.Item item = new MediaMetrics.Item("audio.device.checkSendBecomingNoisyIntentInt").set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(i)).set(MediaMetrics.Property.STATE, i2 == 1 ? "connected" : "disconnected");
        int i4 = 0;
        if (i2 != 0) {
            Log.i("AS.AudioDeviceInventory", "not sending NOISY: state=" + i2);
            item.set(MediaMetrics.Property.DELAY_MS, 0).record();
            return 0;
        }
        Set<Integer> set = BECOMING_NOISY_INTENT_DEVICES_SET;
        if (!set.contains(Integer.valueOf(i))) {
            Log.i("AS.AudioDeviceInventory", "not sending NOISY: device=0x" + Integer.toHexString(i) + " not in set " + set);
            item.set(MediaMetrics.Property.DELAY_MS, 0).record();
            return 0;
        }
        HashSet hashSet = new HashSet();
        for (DeviceInfo deviceInfo : this.mConnectedDevices.values()) {
            int i5 = deviceInfo.mDeviceType;
            if ((Integer.MIN_VALUE & i5) == 0 && BECOMING_NOISY_INTENT_DEVICES_SET.contains(Integer.valueOf(i5))) {
                hashSet.add(Integer.valueOf(deviceInfo.mDeviceType));
                Log.i("AS.AudioDeviceInventory", "NOISY: adding 0x" + Integer.toHexString(deviceInfo.mDeviceType));
            }
        }
        if (i3 == 0) {
            i3 = this.mDeviceBroker.getDeviceForStream(3);
            Log.i("AS.AudioDeviceInventory", "NOISY: musicDevice changing from NONE to 0x" + Integer.toHexString(i3));
        }
        boolean isInCommunication = this.mDeviceBroker.isInCommunication();
        boolean isSingleAudioDeviceType = AudioSystem.isSingleAudioDeviceType(hashSet, i);
        boolean hasMediaDynamicPolicy = this.mDeviceBroker.hasMediaDynamicPolicy();
        if ((i == i3 || isInCommunication) && isSingleAudioDeviceType && !hasMediaDynamicPolicy && i3 != 32768) {
            if (!this.mAudioSystem.isStreamActive(3, 0) && !this.mDeviceBroker.hasAudioFocusUsers()) {
                AudioService.sDeviceLogger.enqueue(new EventLogger.StringEvent("dropping ACTION_AUDIO_BECOMING_NOISY").printLog("AS.AudioDeviceInventory"));
                item.set(MediaMetrics.Property.DELAY_MS, 0).record();
                return 0;
            }
            this.mDeviceBroker.postBroadcastBecomingNoisy();
            i4 = 1000;
        } else {
            Log.i("AS.AudioDeviceInventory", "not sending NOISY: device:0x" + Integer.toHexString(i) + " musicDevice:0x" + Integer.toHexString(i3) + " inComm:" + isInCommunication + " mediaPolicy:" + hasMediaDynamicPolicy + " singleDevice:" + isSingleAudioDeviceType);
        }
        item.set(MediaMetrics.Property.DELAY_MS, Integer.valueOf(i4)).record();
        return i4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x002c, code lost:
        if (r10 != 262145) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void sendDeviceConnectionIntent(int i, int i2, String str, String str2) {
        Intent intent = new Intent();
        if (i != -2113929216) {
            if (i == 4) {
                intent.setAction("android.intent.action.HEADSET_PLUG");
                intent.putExtra("microphone", 1);
            } else {
                if (i != 8) {
                    if (i != 1024) {
                        if (i != 131072) {
                            if (i == 67108864) {
                                intent.setAction("android.intent.action.HEADSET_PLUG");
                                intent.putExtra("microphone", AudioSystem.getDeviceConnectionState(-2113929216, "") != 1 ? 0 : 1);
                            } else if (i != 262144) {
                            }
                        }
                    }
                    configureHdmiPlugIntent(intent, i2);
                }
                intent.setAction("android.intent.action.HEADSET_PLUG");
                intent.putExtra("microphone", 0);
            }
        } else if (AudioSystem.getDeviceConnectionState(67108864, "") != 1) {
            return;
        } else {
            intent.setAction("android.intent.action.HEADSET_PLUG");
            intent.putExtra("microphone", 1);
        }
        if (intent.getAction() == null) {
            return;
        }
        intent.putExtra("state", i2);
        intent.putExtra("address", str);
        intent.putExtra("portName", str2);
        intent.addFlags(1073741824);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mDeviceBroker.broadcastStickyIntentToCurrentProfileGroup(intent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void updateAudioRoutes(int i, int i2) {
        int i3 = 4;
        if (i != 4) {
            if (i != 8) {
                if (i != 1024) {
                    if (i != 4096) {
                        if (i != 16384) {
                            if (i != 131072) {
                                if (i != 67108864) {
                                    if (i != 262144 && i != 262145) {
                                        i3 = 0;
                                    }
                                }
                            }
                        }
                        i3 = 16;
                    }
                }
                i3 = 8;
            }
            i3 = 2;
        } else {
            i3 = 1;
        }
        synchronized (this.mCurAudioRoutes) {
            if (i3 == 0) {
                return;
            }
            AudioRoutesInfo audioRoutesInfo = this.mCurAudioRoutes;
            int i4 = audioRoutesInfo.mainType;
            int i5 = i2 != 0 ? i4 | i3 : (~i3) & i4;
            if (i5 != i4) {
                audioRoutesInfo.mainType = i5;
                this.mDeviceBroker.postReportNewRoutes(false);
            }
        }
    }

    public final void configureHdmiPlugIntent(Intent intent, int i) {
        intent.setAction("android.media.action.HDMI_AUDIO_PLUG");
        intent.putExtra("android.media.extra.AUDIO_PLUG_STATE", i);
        if (i != 1) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        int listAudioPorts = AudioSystem.listAudioPorts(arrayList, new int[1]);
        if (listAudioPorts != 0) {
            Log.e("AS.AudioDeviceInventory", "listAudioPorts error " + listAudioPorts + " in configureHdmiPlugIntent");
            return;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            AudioDevicePort audioDevicePort = (AudioPort) it.next();
            if (audioDevicePort instanceof AudioDevicePort) {
                AudioDevicePort audioDevicePort2 = audioDevicePort;
                if (audioDevicePort2.type() == 1024 || audioDevicePort2.type() == 262144 || audioDevicePort2.type() == 262145) {
                    int[] filterPublicFormats = AudioFormat.filterPublicFormats(audioDevicePort2.formats());
                    if (filterPublicFormats.length > 0) {
                        ArrayList arrayList2 = new ArrayList(1);
                        for (int i2 : filterPublicFormats) {
                            if (i2 != 0) {
                                arrayList2.add(Integer.valueOf(i2));
                            }
                        }
                        intent.putExtra("android.media.extra.ENCODINGS", arrayList2.stream().mapToInt(new ToIntFunction() { // from class: com.android.server.audio.AudioDeviceInventory$$ExternalSyntheticLambda0
                            @Override // java.util.function.ToIntFunction
                            public final int applyAsInt(Object obj) {
                                int intValue;
                                intValue = ((Integer) obj).intValue();
                                return intValue;
                            }
                        }).toArray());
                    }
                    int i3 = 0;
                    for (int i4 : audioDevicePort2.channelMasks()) {
                        int channelCountFromOutChannelMask = AudioFormat.channelCountFromOutChannelMask(i4);
                        if (channelCountFromOutChannelMask > i3) {
                            i3 = channelCountFromOutChannelMask;
                        }
                    }
                    intent.putExtra("android.media.extra.MAX_CHANNEL_COUNT", i3);
                }
            }
        }
    }

    public final void dispatchPreferredDevice(int i, List<AudioDeviceAttributes> list) {
        int beginBroadcast = this.mPrefDevDispatchers.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mPrefDevDispatchers.getBroadcastItem(i2).dispatchPrefDevicesChanged(i, list);
            } catch (RemoteException unused) {
            }
        }
        this.mPrefDevDispatchers.finishBroadcast();
    }

    public final void dispatchNonDefaultDevice(int i, List<AudioDeviceAttributes> list) {
        int beginBroadcast = this.mNonDefDevDispatchers.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mNonDefDevDispatchers.getBroadcastItem(i2).dispatchNonDefDevicesChanged(i, list);
            } catch (RemoteException unused) {
            }
        }
        this.mNonDefDevDispatchers.finishBroadcast();
    }

    public final void dispatchDevicesRoleForCapturePreset(int i, int i2, List<AudioDeviceAttributes> list) {
        int beginBroadcast = this.mDevRoleCapturePresetDispatchers.beginBroadcast();
        for (int i3 = 0; i3 < beginBroadcast; i3++) {
            try {
                this.mDevRoleCapturePresetDispatchers.getBroadcastItem(i3).dispatchDevicesRoleChanged(i, i2, list);
            } catch (RemoteException unused) {
            }
        }
        this.mDevRoleCapturePresetDispatchers.finishBroadcast();
    }

    public UUID getDeviceSensorUuid(AudioDeviceAttributes audioDeviceAttributes) {
        String makeDeviceListKey = DeviceInfo.makeDeviceListKey(audioDeviceAttributes.getInternalType(), audioDeviceAttributes.getAddress());
        synchronized (this.mDevicesLock) {
            DeviceInfo deviceInfo = this.mConnectedDevices.get(makeDeviceListKey);
            if (deviceInfo == null) {
                return null;
            }
            return deviceInfo.mSensorUuid;
        }
    }

    public AudioDeviceAttributes getDeviceOfType(int i) {
        synchronized (this.mDevicesLock) {
            for (DeviceInfo deviceInfo : this.mConnectedDevices.values()) {
                if (deviceInfo.mDeviceType == i) {
                    return new AudioDeviceAttributes(deviceInfo.mDeviceType, deviceInfo.mDeviceAddress, deviceInfo.mDeviceName);
                }
            }
            return null;
        }
    }

    @VisibleForTesting
    public boolean isA2dpDeviceConnected(BluetoothDevice bluetoothDevice) {
        boolean z;
        String makeDeviceListKey = DeviceInfo.makeDeviceListKey(128, bluetoothDevice.getAddress());
        synchronized (this.mDevicesLock) {
            z = this.mConnectedDevices.get(makeDeviceListKey) != null;
        }
        return z;
    }
}
