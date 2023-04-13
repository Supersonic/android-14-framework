package com.android.server.audio;

import android.app.compat.CompatChanges;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioRoutesInfo;
import android.media.AudioSystem;
import android.media.BluetoothProfileConnectionInfo;
import android.media.IAudioRoutesObserver;
import android.media.ICapturePresetDevicesRoleDispatcher;
import android.media.ICommunicationDeviceDispatcher;
import android.media.IStrategyNonDefaultDevicesDispatcher;
import android.media.IStrategyPreferredDevicesDispatcher;
import android.media.MediaMetrics;
import android.media.audiopolicy.AudioProductStrategy;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.PrintWriterPrinter;
import com.android.internal.annotations.GuardedBy;
import com.android.server.audio.AudioDeviceBroker;
import com.android.server.audio.AudioDeviceInventory;
import com.android.server.audio.BtHelper;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class AudioDeviceBroker {
    public static final Set<Integer> MESSAGES_MUTE_MUSIC;
    @GuardedBy({"sLastDeviceConnectionMsgTimeLock"})
    public static long sLastDeviceConnectMsgTime;
    public AudioDeviceInfo mActiveCommunicationDevice;
    public final AudioService mAudioService;
    @GuardedBy({"mDeviceStateLock"})
    public boolean mBluetoothA2dpEnabled;
    public boolean mBluetoothScoOn;
    public PowerManager.WakeLock mBrokerEventWakeLock;
    public BrokerHandler mBrokerHandler;
    public BrokerThread mBrokerThread;
    public final Context mContext;
    public AudioDeviceAttributes mPreferredCommunicationDevice;
    public final SystemServerAdapter mSystemServer;
    public static final Object sLastDeviceConnectionMsgTimeLock = new Object();
    public static final int[] VALID_COMMUNICATION_DEVICE_TYPES = {2, 7, 3, 22, 1, 4, 23, 26, 11, 27, 5, 9, 19};
    public int mCommunicationStrategyId = -1;
    public int mAccessibilityStrategyId = -1;
    public final Object mDeviceStateLock = new Object();
    public final Object mSetModeLock = new Object();
    public AudioModeInfo mAudioModeOwner = new AudioModeInfo(0, 0, 0);
    public final RemoteCallbackList<ICommunicationDeviceDispatcher> mCommDevDispatchers = new RemoteCallbackList<>();
    @GuardedBy({"mDeviceStateLock"})
    public int mCurCommunicationPortId = -1;
    public AtomicBoolean mMusicMuted = new AtomicBoolean(false);
    @GuardedBy({"mDeviceStateLock"})
    public final LinkedList<CommunicationRouteClient> mCommunicationRouteClients = new LinkedList<>();
    public final BtHelper mBtHelper = new BtHelper(this);
    public final AudioDeviceInventory mDeviceInventory = new AudioDeviceInventory(this);

    public static boolean isMessageHandledUnderWakelock(int i) {
        return i == 2 || i == 29 || i == 31 || i == 35 || i == 49 || i == 6 || i == 7 || i == 10 || i == 11;
    }

    static {
        HashSet hashSet = new HashSet();
        MESSAGES_MUTE_MUSIC = hashSet;
        hashSet.add(7);
        hashSet.add(11);
        hashSet.add(29);
        hashSet.add(5);
    }

    /* loaded from: classes.dex */
    public static final class AudioModeInfo {
        public final int mMode;
        public final int mPid;
        public final int mUid;

        public AudioModeInfo(int i, int i2, int i3) {
            this.mMode = i;
            this.mPid = i2;
            this.mUid = i3;
        }

        public String toString() {
            return "AudioModeInfo: mMode=" + AudioSystem.modeToString(this.mMode) + ", mPid=" + this.mPid + ", mUid=" + this.mUid;
        }
    }

    public AudioDeviceBroker(Context context, AudioService audioService) {
        this.mContext = context;
        this.mAudioService = audioService;
        this.mSystemServer = SystemServerAdapter.getDefaultAdapter(context);
        init();
    }

    public final void initRoutingStrategyIds() {
        List<AudioProductStrategy> audioProductStrategies = AudioProductStrategy.getAudioProductStrategies();
        this.mCommunicationStrategyId = -1;
        this.mAccessibilityStrategyId = -1;
        for (AudioProductStrategy audioProductStrategy : audioProductStrategies) {
            if (this.mCommunicationStrategyId == -1 && audioProductStrategy.getAudioAttributesForLegacyStreamType(0) != null) {
                this.mCommunicationStrategyId = audioProductStrategy.getId();
            }
            if (this.mAccessibilityStrategyId == -1 && audioProductStrategy.getAudioAttributesForLegacyStreamType(10) != null) {
                this.mAccessibilityStrategyId = audioProductStrategy.getId();
            }
        }
    }

    public final void init() {
        setupMessaging(this.mContext);
        initRoutingStrategyIds();
        this.mPreferredCommunicationDevice = null;
        updateActiveCommunicationDevice();
        this.mSystemServer.registerUserStartedReceiver(this.mContext);
    }

    public Context getContext() {
        return this.mContext;
    }

    public void onSystemReady() {
        synchronized (this.mSetModeLock) {
            synchronized (this.mDeviceStateLock) {
                this.mAudioModeOwner = this.mAudioService.getAudioModeOwner();
                this.mBtHelper.onSystemReady();
            }
        }
    }

    public void onAudioServerDied() {
        sendMsgNoDelay(1, 0);
    }

    public void setForceUse_Async(int i, int i2, String str) {
        sendIILMsgNoDelay(4, 2, i, i2, str);
    }

    public void toggleHdmiIfConnected_Async() {
        sendMsgNoDelay(6, 2);
    }

    public void disconnectAllBluetoothProfiles() {
        synchronized (this.mDeviceStateLock) {
            this.mBtHelper.disconnectAllBluetoothProfiles();
        }
    }

    public void receiveBtEvent(Intent intent) {
        synchronized (this.mSetModeLock) {
            synchronized (this.mDeviceStateLock) {
                this.mBtHelper.receiveBtEvent(intent);
            }
        }
    }

    public void setBluetoothA2dpOn_Async(boolean z, String str) {
        synchronized (this.mDeviceStateLock) {
            if (this.mBluetoothA2dpEnabled == z) {
                return;
            }
            this.mBluetoothA2dpEnabled = z;
            this.mBrokerHandler.removeMessages(5);
            sendIILMsgNoDelay(5, 2, 1, this.mBluetoothA2dpEnabled ? 0 : 10, str);
        }
    }

    public void setSpeakerphoneOn(IBinder iBinder, int i, boolean z, String str) {
        postSetCommunicationDeviceForClient(new CommunicationDeviceInfo(iBinder, i, new AudioDeviceAttributes(2, ""), z, -1, str, false));
    }

    public boolean setCommunicationDevice(IBinder iBinder, int i, AudioDeviceInfo audioDeviceInfo, String str) {
        boolean z;
        CommunicationDeviceInfo communicationDeviceInfo = new CommunicationDeviceInfo(iBinder, i, audioDeviceInfo != null ? new AudioDeviceAttributes(audioDeviceInfo) : null, audioDeviceInfo != null, -1, str, true);
        postSetCommunicationDeviceForClient(communicationDeviceInfo);
        synchronized (communicationDeviceInfo) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = 0;
            while (communicationDeviceInfo.mWaitForStatus) {
                try {
                    communicationDeviceInfo.wait(BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS - j);
                } catch (InterruptedException unused) {
                    j = System.currentTimeMillis() - currentTimeMillis;
                    if (j >= BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS) {
                        communicationDeviceInfo.mStatus = false;
                        communicationDeviceInfo.mWaitForStatus = false;
                    }
                }
            }
            z = communicationDeviceInfo.mStatus;
        }
        return z;
    }

    @GuardedBy({"mDeviceStateLock"})
    public boolean onSetCommunicationDeviceForClient(CommunicationDeviceInfo communicationDeviceInfo) {
        if (!communicationDeviceInfo.mOn) {
            CommunicationRouteClient communicationRouteClientForPid = getCommunicationRouteClientForPid(communicationDeviceInfo.mPid);
            if (communicationRouteClientForPid == null) {
                return false;
            }
            AudioDeviceAttributes audioDeviceAttributes = communicationDeviceInfo.mDevice;
            if (audioDeviceAttributes != null && !audioDeviceAttributes.equals(communicationRouteClientForPid.getDevice())) {
                return false;
            }
        }
        setCommunicationRouteForClient(communicationDeviceInfo.mCb, communicationDeviceInfo.mPid, communicationDeviceInfo.mOn ? communicationDeviceInfo.mDevice : null, communicationDeviceInfo.mScoAudioMode, communicationDeviceInfo.mEventSource);
        return true;
    }

    @GuardedBy({"mDeviceStateLock"})
    public void setCommunicationRouteForClient(IBinder iBinder, int i, AudioDeviceAttributes audioDeviceAttributes, int i2, String str) {
        CommunicationRouteClient removeCommunicationRouteClient;
        EventLogger eventLogger = AudioService.sDeviceLogger;
        eventLogger.enqueue(new EventLogger.StringEvent("setCommunicationRouteForClient for pid: " + i + " device: " + audioDeviceAttributes + " from API: " + str).printLog("AS.AudioDeviceBroker"));
        boolean isBluetoothScoRequested = isBluetoothScoRequested();
        CommunicationRouteClient communicationRouteClientForPid = getCommunicationRouteClientForPid(i);
        AudioDeviceAttributes device = communicationRouteClientForPid != null ? communicationRouteClientForPid.getDevice() : null;
        if (audioDeviceAttributes != null) {
            removeCommunicationRouteClient = addCommunicationRouteClient(iBinder, i, audioDeviceAttributes);
            if (removeCommunicationRouteClient == null) {
                Log.w("AS.AudioDeviceBroker", "setCommunicationRouteForClient: could not add client for pid: " + i + " and device: " + audioDeviceAttributes);
            }
        } else {
            removeCommunicationRouteClient = removeCommunicationRouteClient(iBinder, true);
        }
        if (removeCommunicationRouteClient == null) {
            return;
        }
        boolean isBluetoothScoRequested2 = isBluetoothScoRequested();
        if (!isBluetoothScoRequested2 || (isBluetoothScoRequested && isBluetoothScoActive())) {
            if (!isBluetoothScoRequested2 && isBluetoothScoRequested) {
                this.mBtHelper.stopBluetoothSco(str);
            }
        } else if (!this.mBtHelper.startBluetoothSco(i2, str)) {
            Log.w("AS.AudioDeviceBroker", "setCommunicationRouteForClient: failure to start BT SCO for pid: " + i);
            if (device != null) {
                addCommunicationRouteClient(iBinder, i, device);
            } else {
                removeCommunicationRouteClient(iBinder, true);
            }
            postBroadcastScoConnectionState(0);
        }
        updateCommunicationRoute(str);
    }

    @GuardedBy({"mDeviceStateLock"})
    public final CommunicationRouteClient topCommunicationRouteClient() {
        Iterator<CommunicationRouteClient> it = this.mCommunicationRouteClients.iterator();
        while (it.hasNext()) {
            CommunicationRouteClient next = it.next();
            if (next.getPid() == this.mAudioModeOwner.mPid) {
                return next;
            }
        }
        if (this.mCommunicationRouteClients.isEmpty() || this.mAudioModeOwner.mPid != 0) {
            return null;
        }
        return this.mCommunicationRouteClients.get(0);
    }

    @GuardedBy({"mDeviceStateLock"})
    public final AudioDeviceAttributes requestedCommunicationDevice() {
        CommunicationRouteClient communicationRouteClient = topCommunicationRouteClient();
        if (communicationRouteClient != null) {
            return communicationRouteClient.getDevice();
        }
        return null;
    }

    public static boolean isValidCommunicationDevice(AudioDeviceInfo audioDeviceInfo) {
        for (int i : VALID_COMMUNICATION_DEVICE_TYPES) {
            if (audioDeviceInfo.getType() == i) {
                return true;
            }
        }
        return false;
    }

    public static List<AudioDeviceInfo> getAvailableCommunicationDevices() {
        AudioDeviceInfo[] devicesStatic;
        ArrayList arrayList = new ArrayList();
        for (AudioDeviceInfo audioDeviceInfo : AudioManager.getDevicesStatic(2)) {
            if (isValidCommunicationDevice(audioDeviceInfo)) {
                arrayList.add(audioDeviceInfo);
            }
        }
        return arrayList;
    }

    public static /* synthetic */ boolean lambda$getCommunicationDeviceOfType$0(int i, AudioDeviceInfo audioDeviceInfo) {
        return audioDeviceInfo.getType() == i;
    }

    public final AudioDeviceInfo getCommunicationDeviceOfType(final int i) {
        return getAvailableCommunicationDevices().stream().filter(new Predicate() { // from class: com.android.server.audio.AudioDeviceBroker$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getCommunicationDeviceOfType$0;
                lambda$getCommunicationDeviceOfType$0 = AudioDeviceBroker.lambda$getCommunicationDeviceOfType$0(i, (AudioDeviceInfo) obj);
                return lambda$getCommunicationDeviceOfType$0;
            }
        }).findFirst().orElse(null);
    }

    public AudioDeviceInfo getCommunicationDevice() {
        AudioDeviceInfo audioDeviceInfo;
        synchronized (this.mDeviceStateLock) {
            updateActiveCommunicationDevice();
            audioDeviceInfo = this.mActiveCommunicationDevice;
            if (audioDeviceInfo != null && audioDeviceInfo.getType() == 13) {
                audioDeviceInfo = getCommunicationDeviceOfType(2);
            }
            if ((audioDeviceInfo == null || !isValidCommunicationDevice(audioDeviceInfo)) && (audioDeviceInfo = getCommunicationDeviceOfType(1)) == null) {
                List<AudioDeviceInfo> availableCommunicationDevices = getAvailableCommunicationDevices();
                if (!availableCommunicationDevices.isEmpty()) {
                    audioDeviceInfo = availableCommunicationDevices.get(0);
                }
            }
        }
        return audioDeviceInfo;
    }

    @GuardedBy({"mDeviceStateLock"})
    public void updateActiveCommunicationDevice() {
        AudioDeviceAttributes preferredCommunicationDevice = preferredCommunicationDevice();
        if (preferredCommunicationDevice == null) {
            ArrayList devicesForAttributes = AudioSystem.getDevicesForAttributes(AudioProductStrategy.getAudioAttributesForStrategyWithLegacyStreamType(0), false);
            if (devicesForAttributes.isEmpty()) {
                if (this.mAudioService.isPlatformVoice()) {
                    Log.w("AS.AudioDeviceBroker", "updateActiveCommunicationDevice(): no device for phone strategy");
                }
                this.mActiveCommunicationDevice = null;
                return;
            }
            preferredCommunicationDevice = (AudioDeviceAttributes) devicesForAttributes.get(0);
        }
        this.mActiveCommunicationDevice = AudioManager.getDeviceInfoFromTypeAndAddress(preferredCommunicationDevice.getType(), preferredCommunicationDevice.getAddress());
    }

    public final boolean isDeviceRequestedForCommunication(int i) {
        boolean z;
        synchronized (this.mDeviceStateLock) {
            AudioDeviceAttributes requestedCommunicationDevice = requestedCommunicationDevice();
            z = requestedCommunicationDevice != null && requestedCommunicationDevice.getType() == i;
        }
        return z;
    }

    public final boolean isDeviceOnForCommunication(int i) {
        boolean z;
        synchronized (this.mDeviceStateLock) {
            AudioDeviceAttributes preferredCommunicationDevice = preferredCommunicationDevice();
            z = preferredCommunicationDevice != null && preferredCommunicationDevice.getType() == i;
        }
        return z;
    }

    public final boolean isDeviceActiveForCommunication(int i) {
        AudioDeviceAttributes audioDeviceAttributes;
        AudioDeviceInfo audioDeviceInfo = this.mActiveCommunicationDevice;
        return audioDeviceInfo != null && audioDeviceInfo.getType() == i && (audioDeviceAttributes = this.mPreferredCommunicationDevice) != null && audioDeviceAttributes.getType() == i;
    }

    public boolean isSpeakerphoneOn() {
        return isDeviceOnForCommunication(2);
    }

    public final boolean isSpeakerphoneActive() {
        return isDeviceActiveForCommunication(2);
    }

    public boolean isBluetoothScoRequested() {
        return isDeviceRequestedForCommunication(7);
    }

    public boolean isBluetoothScoOn() {
        return isDeviceOnForCommunication(7);
    }

    public boolean isBluetoothScoActive() {
        return isDeviceActiveForCommunication(7);
    }

    public boolean isDeviceConnected(AudioDeviceAttributes audioDeviceAttributes) {
        boolean isDeviceConnected;
        synchronized (this.mDeviceStateLock) {
            isDeviceConnected = this.mDeviceInventory.isDeviceConnected(audioDeviceAttributes);
        }
        return isDeviceConnected;
    }

    public void setWiredDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i, String str) {
        synchronized (this.mDeviceStateLock) {
            this.mDeviceInventory.setWiredDeviceConnectionState(audioDeviceAttributes, i, str);
        }
    }

    public void setTestDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i) {
        synchronized (this.mDeviceStateLock) {
            this.mDeviceInventory.setTestDeviceConnectionState(audioDeviceAttributes, i);
        }
    }

    /* loaded from: classes.dex */
    public static final class BleVolumeInfo {
        public final int mIndex;
        public final int mMaxIndex;
        public final int mStreamType;

        public BleVolumeInfo(int i, int i2, int i3) {
            this.mIndex = i;
            this.mMaxIndex = i2;
            this.mStreamType = i3;
        }
    }

    /* loaded from: classes.dex */
    public static final class BtDeviceChangedData {
        public final String mEventSource;
        public final BluetoothProfileConnectionInfo mInfo;
        public final BluetoothDevice mNewDevice;
        public final BluetoothDevice mPreviousDevice;

        public BtDeviceChangedData(BluetoothDevice bluetoothDevice, BluetoothDevice bluetoothDevice2, BluetoothProfileConnectionInfo bluetoothProfileConnectionInfo, String str) {
            this.mNewDevice = bluetoothDevice;
            this.mPreviousDevice = bluetoothDevice2;
            this.mInfo = bluetoothProfileConnectionInfo;
            this.mEventSource = str;
        }

        public String toString() {
            return "BtDeviceChangedData profile=" + BluetoothProfile.getProfileName(this.mInfo.getProfile()) + ", switch device: [" + this.mPreviousDevice + "] -> [" + this.mNewDevice + "]";
        }
    }

    /* loaded from: classes.dex */
    public static final class BtDeviceInfo {
        public final int mAudioSystemDevice;
        public final int mCodec;
        public final BluetoothDevice mDevice;
        public final String mEventSource;
        public final boolean mIsLeOutput;
        public final int mMusicDevice;
        public final int mProfile;
        public final int mState;
        public final boolean mSupprNoisy;
        public final int mVolume;

        public BtDeviceInfo(BtDeviceChangedData btDeviceChangedData, BluetoothDevice bluetoothDevice, int i, int i2, int i3) {
            this.mDevice = bluetoothDevice;
            this.mState = i;
            this.mProfile = btDeviceChangedData.mInfo.getProfile();
            this.mSupprNoisy = btDeviceChangedData.mInfo.isSuppressNoisyIntent();
            this.mVolume = btDeviceChangedData.mInfo.getVolume();
            this.mIsLeOutput = btDeviceChangedData.mInfo.isLeOutput();
            this.mEventSource = btDeviceChangedData.mEventSource;
            this.mAudioSystemDevice = i2;
            this.mMusicDevice = 0;
            this.mCodec = i3;
        }

        public BtDeviceInfo(BluetoothDevice bluetoothDevice, int i) {
            this.mDevice = bluetoothDevice;
            this.mProfile = i;
            this.mEventSource = "";
            this.mMusicDevice = 0;
            this.mCodec = 0;
            this.mAudioSystemDevice = 0;
            this.mState = 0;
            this.mSupprNoisy = false;
            this.mVolume = -1;
            this.mIsLeOutput = false;
        }

        public BtDeviceInfo(BluetoothDevice bluetoothDevice, int i, int i2, int i3, int i4) {
            this.mDevice = bluetoothDevice;
            this.mProfile = i;
            this.mEventSource = "";
            this.mMusicDevice = i3;
            this.mCodec = 0;
            this.mAudioSystemDevice = i4;
            this.mState = i2;
            this.mSupprNoisy = false;
            this.mVolume = -1;
            this.mIsLeOutput = false;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (obj instanceof BtDeviceInfo) {
                BtDeviceInfo btDeviceInfo = (BtDeviceInfo) obj;
                return this.mProfile == btDeviceInfo.mProfile && this.mDevice.equals(btDeviceInfo.mDevice);
            }
            return false;
        }
    }

    public BtDeviceInfo createBtDeviceInfo(BtDeviceChangedData btDeviceChangedData, BluetoothDevice bluetoothDevice, int i) {
        int a2dpCodec;
        int i2;
        int profile = btDeviceChangedData.mInfo.getProfile();
        if (profile != 2) {
            a2dpCodec = 0;
            if (profile == 11) {
                i2 = -2147352576;
            } else if (profile == 26) {
                i2 = 536870914;
            } else if (profile == 21) {
                i2 = 134217728;
            } else if (profile == 22) {
                i2 = btDeviceChangedData.mInfo.isLeOutput() ? 536870912 : -1610612736;
            } else {
                throw new IllegalArgumentException("Invalid profile " + btDeviceChangedData.mInfo.getProfile());
            }
        } else {
            synchronized (this.mDeviceStateLock) {
                a2dpCodec = this.mBtHelper.getA2dpCodec(bluetoothDevice);
            }
            i2 = 128;
        }
        return new BtDeviceInfo(btDeviceChangedData, bluetoothDevice, i, i2, a2dpCodec);
    }

    public final void btMediaMetricRecord(BluetoothDevice bluetoothDevice, String str, BtDeviceChangedData btDeviceChangedData) {
        new MediaMetrics.Item("audio.device.queueOnBluetoothActiveDeviceChanged").set(MediaMetrics.Property.STATE, str).set(MediaMetrics.Property.STATUS, Integer.valueOf(btDeviceChangedData.mInfo.getProfile())).set(MediaMetrics.Property.NAME, TextUtils.emptyIfNull(bluetoothDevice.getName())).record();
    }

    public void queueOnBluetoothActiveDeviceChanged(BtDeviceChangedData btDeviceChangedData) {
        BluetoothDevice bluetoothDevice;
        if (btDeviceChangedData.mInfo.getProfile() == 2 && (bluetoothDevice = btDeviceChangedData.mPreviousDevice) != null && bluetoothDevice.equals(btDeviceChangedData.mNewDevice)) {
            new MediaMetrics.Item("audio.device.queueOnBluetoothActiveDeviceChanged_update").set(MediaMetrics.Property.NAME, TextUtils.emptyIfNull(btDeviceChangedData.mNewDevice.getName())).set(MediaMetrics.Property.STATUS, Integer.valueOf(btDeviceChangedData.mInfo.getProfile())).record();
            synchronized (this.mDeviceStateLock) {
                postBluetoothA2dpDeviceConfigChange(btDeviceChangedData.mNewDevice);
            }
            return;
        }
        synchronized (this.mDeviceStateLock) {
            BluetoothDevice bluetoothDevice2 = btDeviceChangedData.mPreviousDevice;
            if (bluetoothDevice2 != null) {
                btMediaMetricRecord(bluetoothDevice2, "disconnected", btDeviceChangedData);
                sendLMsgNoDelay(45, 2, createBtDeviceInfo(btDeviceChangedData, btDeviceChangedData.mPreviousDevice, 0));
            }
            BluetoothDevice bluetoothDevice3 = btDeviceChangedData.mNewDevice;
            if (bluetoothDevice3 != null) {
                btMediaMetricRecord(bluetoothDevice3, "connected", btDeviceChangedData);
                sendLMsgNoDelay(45, 2, createBtDeviceInfo(btDeviceChangedData, btDeviceChangedData.mNewDevice, 2));
            }
        }
    }

    public void setBluetoothScoOn(boolean z, String str) {
        synchronized (this.mDeviceStateLock) {
            this.mBluetoothScoOn = z;
            postUpdateCommunicationRouteClient(str);
        }
    }

    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) {
        AudioRoutesInfo startWatchingRoutes;
        synchronized (this.mDeviceStateLock) {
            startWatchingRoutes = this.mDeviceInventory.startWatchingRoutes(iAudioRoutesObserver);
        }
        return startWatchingRoutes;
    }

    public AudioRoutesInfo getCurAudioRoutes() {
        AudioRoutesInfo curAudioRoutes;
        synchronized (this.mDeviceStateLock) {
            curAudioRoutes = this.mDeviceInventory.getCurAudioRoutes();
        }
        return curAudioRoutes;
    }

    public boolean isBluetoothA2dpOn() {
        boolean z;
        synchronized (this.mDeviceStateLock) {
            z = this.mBluetoothA2dpEnabled;
        }
        return z;
    }

    public void postSetAvrcpAbsoluteVolumeIndex(int i) {
        sendIMsgNoDelay(15, 0, i);
    }

    public void postSetHearingAidVolumeIndex(int i, int i2) {
        sendIIMsgNoDelay(14, 0, i, i2);
    }

    public void postSetLeAudioVolumeIndex(int i, int i2, int i3) {
        sendLMsgNoDelay(46, 0, new BleVolumeInfo(i, i2, i3));
    }

    public void postSetModeOwner(int i, int i2, int i3) {
        sendLMsgNoDelay(16, 0, new AudioModeInfo(i, i2, i3));
    }

    public void postBluetoothA2dpDeviceConfigChange(BluetoothDevice bluetoothDevice) {
        sendLMsgNoDelay(11, 2, bluetoothDevice);
    }

    public void startBluetoothScoForClient(IBinder iBinder, int i, int i2, String str) {
        postSetCommunicationDeviceForClient(new CommunicationDeviceInfo(iBinder, i, new AudioDeviceAttributes(16, ""), true, i2, str, false));
    }

    public void stopBluetoothScoForClient(IBinder iBinder, int i, String str) {
        postSetCommunicationDeviceForClient(new CommunicationDeviceInfo(iBinder, i, new AudioDeviceAttributes(16, ""), false, -1, str, false));
    }

    public int setPreferredDevicesForStrategySync(int i, List<AudioDeviceAttributes> list) {
        return this.mDeviceInventory.setPreferredDevicesForStrategySync(i, list);
    }

    public int removePreferredDevicesForStrategySync(int i) {
        return this.mDeviceInventory.removePreferredDevicesForStrategySync(i);
    }

    public int setDeviceAsNonDefaultForStrategySync(int i, AudioDeviceAttributes audioDeviceAttributes) {
        return this.mDeviceInventory.setDeviceAsNonDefaultForStrategySync(i, audioDeviceAttributes);
    }

    public int removeDeviceAsNonDefaultForStrategySync(int i, AudioDeviceAttributes audioDeviceAttributes) {
        return this.mDeviceInventory.removeDeviceAsNonDefaultForStrategySync(i, audioDeviceAttributes);
    }

    public void registerStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) {
        this.mDeviceInventory.registerStrategyPreferredDevicesDispatcher(iStrategyPreferredDevicesDispatcher);
    }

    public void unregisterStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) {
        this.mDeviceInventory.unregisterStrategyPreferredDevicesDispatcher(iStrategyPreferredDevicesDispatcher);
    }

    public void registerStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) {
        this.mDeviceInventory.registerStrategyNonDefaultDevicesDispatcher(iStrategyNonDefaultDevicesDispatcher);
    }

    public void unregisterStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) {
        this.mDeviceInventory.unregisterStrategyNonDefaultDevicesDispatcher(iStrategyNonDefaultDevicesDispatcher);
    }

    public int setPreferredDevicesForCapturePresetSync(int i, List<AudioDeviceAttributes> list) {
        return this.mDeviceInventory.setPreferredDevicesForCapturePresetSync(i, list);
    }

    public int clearPreferredDevicesForCapturePresetSync(int i) {
        return this.mDeviceInventory.clearPreferredDevicesForCapturePresetSync(i);
    }

    public void registerCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) {
        this.mDeviceInventory.registerCapturePresetDevicesRoleDispatcher(iCapturePresetDevicesRoleDispatcher);
    }

    public void unregisterCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) {
        this.mDeviceInventory.unregisterCapturePresetDevicesRoleDispatcher(iCapturePresetDevicesRoleDispatcher);
    }

    public void registerCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher iCommunicationDeviceDispatcher) {
        this.mCommDevDispatchers.register(iCommunicationDeviceDispatcher);
    }

    public void unregisterCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher iCommunicationDeviceDispatcher) {
        this.mCommDevDispatchers.unregister(iCommunicationDeviceDispatcher);
    }

    @GuardedBy({"mDeviceStateLock"})
    public final void dispatchCommunicationDevice() {
        AudioDeviceInfo communicationDevice = getCommunicationDevice();
        int id = communicationDevice != null ? communicationDevice.getId() : 0;
        if (id == this.mCurCommunicationPortId) {
            return;
        }
        this.mCurCommunicationPortId = id;
        int beginBroadcast = this.mCommDevDispatchers.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mCommDevDispatchers.getBroadcastItem(i).dispatchCommunicationDeviceChanged(id);
            } catch (RemoteException unused) {
            }
        }
        this.mCommDevDispatchers.finishBroadcast();
    }

    public void postAccessoryPlugMediaUnmute(int i) {
        this.mAudioService.postAccessoryPlugMediaUnmute(i);
    }

    public int getVssVolumeForDevice(int i, int i2) {
        return this.mAudioService.getVssVolumeForDevice(i, i2);
    }

    public int getMaxVssVolumeForStream(int i) {
        return this.mAudioService.getMaxVssVolumeForStream(i);
    }

    public int getDeviceForStream(int i) {
        return this.mAudioService.getDeviceForStream(i);
    }

    public void postApplyVolumeOnDevice(int i, int i2, String str) {
        this.mAudioService.postApplyVolumeOnDevice(i, i2, str);
    }

    public void postSetVolumeIndexOnDevice(int i, int i2, int i3, String str) {
        this.mAudioService.postSetVolumeIndexOnDevice(i, i2, i3, str);
    }

    public void postObserveDevicesForAllStreams() {
        this.mAudioService.postObserveDevicesForAllStreams();
    }

    public boolean isInCommunication() {
        return this.mAudioService.isInCommunication();
    }

    public boolean hasMediaDynamicPolicy() {
        return this.mAudioService.hasMediaDynamicPolicy();
    }

    public ContentResolver getContentResolver() {
        return this.mAudioService.getContentResolver();
    }

    public void checkMusicActive(int i, String str) {
        this.mAudioService.checkMusicActive(i, str);
    }

    public void checkVolumeCecOnHdmiConnection(int i, String str) {
        this.mAudioService.postCheckVolumeCecOnHdmiConnection(i, str);
    }

    public boolean hasAudioFocusUsers() {
        return this.mAudioService.hasAudioFocusUsers();
    }

    public void postBroadcastScoConnectionState(int i) {
        sendIMsgNoDelay(3, 2, i);
    }

    public void postBroadcastBecomingNoisy() {
        sendMsgNoDelay(12, 0);
    }

    @GuardedBy({"mDeviceStateLock"})
    public void postBluetoothActiveDevice(BtDeviceInfo btDeviceInfo, int i) {
        sendLMsg(7, 2, btDeviceInfo, i);
    }

    public void postSetWiredDeviceConnectionState(AudioDeviceInventory.WiredDeviceConnectionState wiredDeviceConnectionState, int i) {
        sendLMsg(2, 2, wiredDeviceConnectionState, i);
    }

    public void postBtProfileDisconnected(int i) {
        sendIMsgNoDelay(22, 2, i);
    }

    public void postBtProfileConnected(int i, BluetoothProfile bluetoothProfile) {
        sendILMsgNoDelay(23, 2, i, bluetoothProfile);
    }

    public void postCommunicationRouteClientDied(CommunicationRouteClient communicationRouteClient) {
        sendLMsgNoDelay(34, 2, communicationRouteClient);
    }

    public void postSaveSetPreferredDevicesForStrategy(int i, List<AudioDeviceAttributes> list) {
        sendILMsgNoDelay(32, 2, i, list);
    }

    public void postSaveRemovePreferredDevicesForStrategy(int i) {
        sendIMsgNoDelay(33, 2, i);
    }

    public void postSaveSetDeviceAsNonDefaultForStrategy(int i, AudioDeviceAttributes audioDeviceAttributes) {
        sendILMsgNoDelay(47, 2, i, audioDeviceAttributes);
    }

    public void postSaveRemoveDeviceAsNonDefaultForStrategy(int i, AudioDeviceAttributes audioDeviceAttributes) {
        sendILMsgNoDelay(48, 2, i, audioDeviceAttributes);
    }

    public void postSaveSetPreferredDevicesForCapturePreset(int i, List<AudioDeviceAttributes> list) {
        sendILMsgNoDelay(37, 2, i, list);
    }

    public void postSaveClearPreferredDevicesForCapturePreset(int i) {
        sendIMsgNoDelay(38, 2, i);
    }

    public void postUpdateCommunicationRouteClient(String str) {
        sendLMsgNoDelay(43, 2, str);
    }

    public void postSetCommunicationDeviceForClient(CommunicationDeviceInfo communicationDeviceInfo) {
        sendLMsgNoDelay(42, 2, communicationDeviceInfo);
    }

    public void postScoAudioStateChanged(int i) {
        sendIMsgNoDelay(44, 2, i);
    }

    /* loaded from: classes.dex */
    public static final class CommunicationDeviceInfo {
        public final IBinder mCb;
        public final AudioDeviceAttributes mDevice;
        public final String mEventSource;
        public final boolean mOn;
        public final int mPid;
        public final int mScoAudioMode;
        public boolean mStatus = false;
        public boolean mWaitForStatus;

        public CommunicationDeviceInfo(IBinder iBinder, int i, AudioDeviceAttributes audioDeviceAttributes, boolean z, int i2, String str, boolean z2) {
            this.mCb = iBinder;
            this.mPid = i;
            this.mDevice = audioDeviceAttributes;
            this.mOn = z;
            this.mScoAudioMode = i2;
            this.mEventSource = str;
            this.mWaitForStatus = z2;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (obj instanceof CommunicationDeviceInfo) {
                CommunicationDeviceInfo communicationDeviceInfo = (CommunicationDeviceInfo) obj;
                return this.mCb.equals(communicationDeviceInfo.mCb) && this.mPid == communicationDeviceInfo.mPid;
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CommunicationDeviceInfo mCb=");
            sb.append(this.mCb.toString());
            sb.append(" mPid=");
            sb.append(this.mPid);
            sb.append(" mDevice=[");
            AudioDeviceAttributes audioDeviceAttributes = this.mDevice;
            sb.append(audioDeviceAttributes != null ? audioDeviceAttributes.toString() : "null");
            sb.append("] mOn=");
            sb.append(this.mOn);
            sb.append(" mScoAudioMode=");
            sb.append(this.mScoAudioMode);
            sb.append(" mEventSource=");
            sb.append(this.mEventSource);
            sb.append(" mWaitForStatus=");
            sb.append(this.mWaitForStatus);
            sb.append(" mStatus=");
            sb.append(this.mStatus);
            return sb.toString();
        }
    }

    public void setBluetoothA2dpOnInt(boolean z, boolean z2, String str) {
        String str2 = "setBluetoothA2dpOn(" + z + ") from u/pid:" + Binder.getCallingUid() + "/" + Binder.getCallingPid() + " src:" + str;
        synchronized (this.mDeviceStateLock) {
            this.mBluetoothA2dpEnabled = z;
            this.mBrokerHandler.removeMessages(5);
            onSetForceUse(1, this.mBluetoothA2dpEnabled ? 0 : 10, z2, str2);
        }
    }

    public boolean handleDeviceConnection(AudioDeviceAttributes audioDeviceAttributes, boolean z) {
        boolean handleDeviceConnection;
        synchronized (this.mDeviceStateLock) {
            handleDeviceConnection = this.mDeviceInventory.handleDeviceConnection(audioDeviceAttributes, z, false);
        }
        return handleDeviceConnection;
    }

    public void handleFailureToConnectToBtHeadsetService(int i) {
        sendMsg(9, 0, i);
    }

    public void handleCancelFailureToConnectToBtHeadsetService() {
        this.mBrokerHandler.removeMessages(9);
    }

    public void postReportNewRoutes(boolean z) {
        sendMsgNoDelay(z ? 36 : 13, 1);
    }

    public boolean hasScheduledA2dpConnection(BluetoothDevice bluetoothDevice) {
        return this.mBrokerHandler.hasEqualMessages(7, new BtDeviceInfo(bluetoothDevice, 2));
    }

    public void setA2dpTimeout(String str, int i, int i2) {
        sendILMsg(10, 2, i, str, i2);
    }

    public void setLeAudioTimeout(String str, int i, int i2) {
        sendILMsg(49, 2, i, str, i2);
    }

    public void setAvrcpAbsoluteVolumeSupported(boolean z) {
        synchronized (this.mDeviceStateLock) {
            this.mBtHelper.setAvrcpAbsoluteVolumeSupported(z);
        }
    }

    public void clearAvrcpAbsoluteVolumeSupported() {
        setAvrcpAbsoluteVolumeSupported(false);
        this.mAudioService.setAvrcpAbsoluteVolumeSupported(false);
    }

    public boolean getBluetoothA2dpEnabled() {
        boolean z;
        synchronized (this.mDeviceStateLock) {
            z = this.mBluetoothA2dpEnabled;
        }
        return z;
    }

    public void broadcastStickyIntentToCurrentProfileGroup(Intent intent) {
        this.mSystemServer.broadcastStickyIntentToCurrentProfileGroup(intent);
    }

    public void dump(final PrintWriter printWriter, final String str) {
        if (this.mBrokerHandler != null) {
            printWriter.println(str + "Message handler (watch for unhandled messages):");
            BrokerHandler brokerHandler = this.mBrokerHandler;
            PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
            brokerHandler.dump(printWriterPrinter, str + "  ");
        } else {
            printWriter.println("Message handler is null");
        }
        this.mDeviceInventory.dump(printWriter, str);
        printWriter.println("\n" + str + "Communication route clients:");
        this.mCommunicationRouteClients.forEach(new Consumer() { // from class: com.android.server.audio.AudioDeviceBroker$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AudioDeviceBroker.lambda$dump$1(printWriter, str, (AudioDeviceBroker.CommunicationRouteClient) obj);
            }
        });
        printWriter.println("\n" + str + "Computed Preferred communication device: " + preferredCommunicationDevice());
        printWriter.println("\n" + str + "Applied Preferred communication device: " + this.mPreferredCommunicationDevice);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Active communication device: ");
        sb.append((Object) (this.mActiveCommunicationDevice == null ? "None" : new AudioDeviceAttributes(this.mActiveCommunicationDevice)));
        printWriter.println(sb.toString());
        printWriter.println(str + "mCommunicationStrategyId: " + this.mCommunicationStrategyId);
        printWriter.println(str + "mAccessibilityStrategyId: " + this.mAccessibilityStrategyId);
        printWriter.println("\n" + str + "mAudioModeOwner: " + this.mAudioModeOwner);
        this.mBtHelper.dump(printWriter, str);
    }

    public static /* synthetic */ void lambda$dump$1(PrintWriter printWriter, String str, CommunicationRouteClient communicationRouteClient) {
        printWriter.println("  " + str + "pid: " + communicationRouteClient.getPid() + " device: " + communicationRouteClient.getDevice() + " cb: " + communicationRouteClient.getBinder());
    }

    public final void onSetForceUse(int i, int i2, boolean z, String str) {
        if (i == 1) {
            postReportNewRoutes(z);
        }
        AudioService.sForceUseLogger.enqueue(new AudioServiceEvents$ForceUseEvent(i, i2, str));
        new MediaMetrics.Item("audio.forceUse." + AudioSystem.forceUseUsageToString(i)).set(MediaMetrics.Property.EVENT, "onSetForceUse").set(MediaMetrics.Property.FORCE_USE_DUE_TO, str).set(MediaMetrics.Property.FORCE_USE_MODE, AudioSystem.forceUseConfigToString(i2)).record();
        AudioSystem.setForceUse(i, i2);
    }

    public final void onSendBecomingNoisyIntent() {
        AudioService.sDeviceLogger.enqueue(new EventLogger.StringEvent("broadcast ACTION_AUDIO_BECOMING_NOISY").printLog("AS.AudioDeviceBroker"));
        this.mSystemServer.sendDeviceBecomingNoisyIntent();
    }

    public final void setupMessaging(Context context) {
        this.mBrokerEventWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "handleAudioDeviceEvent");
        BrokerThread brokerThread = new BrokerThread();
        this.mBrokerThread = brokerThread;
        brokerThread.start();
        waitForBrokerHandlerCreation();
    }

    public final void waitForBrokerHandlerCreation() {
        synchronized (this) {
            while (this.mBrokerHandler == null) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                    Log.e("AS.AudioDeviceBroker", "Interruption while waiting on BrokerHandler");
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class BrokerThread extends Thread {
        public BrokerThread() {
            super("AudioDeviceBroker");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (AudioDeviceBroker.this) {
                AudioDeviceBroker.this.mBrokerHandler = new BrokerHandler();
                AudioDeviceBroker.this.notify();
            }
            Looper.loop();
        }
    }

    /* loaded from: classes.dex */
    public class BrokerHandler extends Handler {
        public BrokerHandler() {
        }

        /* JADX WARN: Removed duplicated region for block: B:142:0x0240  */
        @Override // android.os.Handler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void handleMessage(Message message) {
            boolean onSetCommunicationDeviceForClient;
            int i = message.what;
            if (i != 22) {
                if (i != 23) {
                    int i2 = -1;
                    switch (i) {
                        case 1:
                            synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                    AudioDeviceBroker.this.initRoutingStrategyIds();
                                    AudioDeviceBroker.this.updateActiveCommunicationDevice();
                                    AudioDeviceBroker.this.mDeviceInventory.onRestoreDevices();
                                    AudioDeviceBroker.this.mBtHelper.onAudioServerDiedRestoreA2dp();
                                    AudioDeviceBroker.this.updateCommunicationRoute("MSG_RESTORE_DEVICES");
                                }
                                break;
                            }
                        case 2:
                            synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                AudioDeviceBroker.this.mDeviceInventory.onSetWiredDeviceConnectionState((AudioDeviceInventory.WiredDeviceConnectionState) message.obj);
                            }
                            break;
                        case 3:
                            synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                AudioDeviceBroker.this.mBtHelper.onBroadcastScoConnectionState(message.arg1);
                            }
                            break;
                        case 4:
                        case 5:
                            AudioDeviceBroker.this.onSetForceUse(message.arg1, message.arg2, i == 5, (String) message.obj);
                            break;
                        case 6:
                            synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                AudioDeviceBroker.this.mDeviceInventory.onToggleHdmi();
                            }
                            break;
                        case 7:
                            synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                    BtDeviceInfo btDeviceInfo = (BtDeviceInfo) message.obj;
                                    AudioDeviceInventory audioDeviceInventory = AudioDeviceBroker.this.mDeviceInventory;
                                    if (btDeviceInfo.mProfile != 22 || btDeviceInfo.mIsLeOutput) {
                                        i2 = AudioDeviceBroker.this.mAudioService.getBluetoothContextualVolumeStream();
                                    }
                                    audioDeviceInventory.onSetBtActiveDevice(btDeviceInfo, i2);
                                    int i3 = btDeviceInfo.mProfile;
                                    if (i3 == 22 || i3 == 21) {
                                        AudioDeviceBroker.this.onUpdateCommunicationRouteClient("setBluetoothActiveDevice");
                                    }
                                }
                                break;
                            }
                            break;
                        default:
                            switch (i) {
                                case 9:
                                    synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                            AudioDeviceBroker.this.mBtHelper.resetBluetoothSco();
                                        }
                                        break;
                                    }
                                case 10:
                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                        AudioDeviceBroker.this.mDeviceInventory.onMakeA2dpDeviceUnavailableNow((String) message.obj, message.arg1);
                                    }
                                    break;
                                case 11:
                                    BluetoothDevice bluetoothDevice = (BluetoothDevice) message.obj;
                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                        AudioDeviceBroker.this.mDeviceInventory.onBluetoothA2dpDeviceConfigChange(new BtHelper.BluetoothA2dpDeviceInfo(bluetoothDevice, -1, AudioDeviceBroker.this.mBtHelper.getA2dpCodec(bluetoothDevice)), 0);
                                    }
                                    break;
                                case 12:
                                    AudioDeviceBroker.this.onSendBecomingNoisyIntent();
                                    break;
                                case 13:
                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                        AudioDeviceBroker.this.mDeviceInventory.onReportNewRoutes();
                                    }
                                    break;
                                case 14:
                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                        AudioDeviceBroker.this.mBtHelper.setHearingAidVolume(message.arg1, message.arg2, AudioDeviceBroker.this.mDeviceInventory.isHearingAidConnected());
                                    }
                                    break;
                                case 15:
                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                        AudioDeviceBroker.this.mBtHelper.setAvrcpAbsoluteVolumeIndex(message.arg1);
                                    }
                                    break;
                                case 16:
                                    synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                            AudioDeviceBroker.this.mAudioModeOwner = (AudioModeInfo) message.obj;
                                            if (AudioDeviceBroker.this.mAudioModeOwner.mMode != 1) {
                                                AudioDeviceBroker.this.onUpdateCommunicationRouteClient("setNewModeOwner");
                                            }
                                        }
                                        break;
                                    }
                                default:
                                    switch (i) {
                                        case 32:
                                            AudioDeviceBroker.this.mDeviceInventory.onSaveSetPreferredDevices(message.arg1, (List) message.obj);
                                            break;
                                        case 33:
                                            AudioDeviceBroker.this.mDeviceInventory.onSaveRemovePreferredDevices(message.arg1);
                                            break;
                                        case 34:
                                            synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                                synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                    AudioDeviceBroker.this.onCommunicationRouteClientDied((CommunicationRouteClient) message.obj);
                                                }
                                                break;
                                            }
                                        case 35:
                                            AudioDeviceBroker.this.checkMessagesMuteMusic(0);
                                            break;
                                        case 36:
                                            break;
                                        case 37:
                                            AudioDeviceBroker.this.mDeviceInventory.onSaveSetPreferredDevicesForCapturePreset(message.arg1, (List) message.obj);
                                            break;
                                        case 38:
                                            AudioDeviceBroker.this.mDeviceInventory.onSaveClearPreferredDevicesForCapturePreset(message.arg1);
                                            break;
                                        default:
                                            switch (i) {
                                                case 42:
                                                    CommunicationDeviceInfo communicationDeviceInfo = (CommunicationDeviceInfo) message.obj;
                                                    synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                            onSetCommunicationDeviceForClient = AudioDeviceBroker.this.onSetCommunicationDeviceForClient(communicationDeviceInfo);
                                                        }
                                                    }
                                                    synchronized (communicationDeviceInfo) {
                                                        if (communicationDeviceInfo.mWaitForStatus) {
                                                            communicationDeviceInfo.mStatus = onSetCommunicationDeviceForClient;
                                                            communicationDeviceInfo.mWaitForStatus = false;
                                                            communicationDeviceInfo.notify();
                                                        }
                                                    }
                                                    break;
                                                case 43:
                                                    synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                            AudioDeviceBroker.this.onUpdateCommunicationRouteClient((String) message.obj);
                                                        }
                                                        break;
                                                    }
                                                case 44:
                                                    synchronized (AudioDeviceBroker.this.mSetModeLock) {
                                                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                            AudioDeviceBroker.this.mBtHelper.onScoAudioStateChanged(message.arg1);
                                                        }
                                                        break;
                                                    }
                                                case 45:
                                                    BtDeviceInfo btDeviceInfo2 = (BtDeviceInfo) message.obj;
                                                    if (btDeviceInfo2.mDevice != null) {
                                                        EventLogger eventLogger = AudioService.sDeviceLogger;
                                                        eventLogger.enqueue(new EventLogger.StringEvent("msg: onBluetoothActiveDeviceChange  state=" + btDeviceInfo2.mState + " addr=" + btDeviceInfo2.mDevice.getAddress() + " prof=" + btDeviceInfo2.mProfile + " supprNoisy=" + btDeviceInfo2.mSupprNoisy + " src=" + btDeviceInfo2.mEventSource).printLog("AS.AudioDeviceBroker"));
                                                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                            AudioDeviceBroker.this.mDeviceInventory.setBluetoothActiveDevice(btDeviceInfo2);
                                                        }
                                                        break;
                                                    }
                                                    break;
                                                case 46:
                                                    BleVolumeInfo bleVolumeInfo = (BleVolumeInfo) message.obj;
                                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                        AudioDeviceBroker.this.mBtHelper.setLeAudioVolume(bleVolumeInfo.mIndex, bleVolumeInfo.mMaxIndex, bleVolumeInfo.mStreamType);
                                                    }
                                                    break;
                                                case 47:
                                                    AudioDeviceBroker.this.mDeviceInventory.onSaveSetDeviceAsNonDefault(message.arg1, (AudioDeviceAttributes) message.obj);
                                                    break;
                                                case 48:
                                                    AudioDeviceBroker.this.mDeviceInventory.onSaveRemoveDeviceAsNonDefault(message.arg1, (AudioDeviceAttributes) message.obj);
                                                    break;
                                                case 49:
                                                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                                                        AudioDeviceBroker.this.mDeviceInventory.onMakeLeAudioDeviceUnavailableNow((String) message.obj, message.arg1);
                                                    }
                                                    break;
                                                default:
                                                    Log.wtf("AS.AudioDeviceBroker", "Invalid message " + message.what);
                                                    break;
                                            }
                                    }
                            }
                    }
                } else if (message.arg1 != 1) {
                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                        AudioDeviceBroker.this.mBtHelper.onBtProfileConnected(message.arg1, (BluetoothProfile) message.obj);
                    }
                } else {
                    synchronized (AudioDeviceBroker.this.mSetModeLock) {
                        synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                            AudioDeviceBroker.this.mBtHelper.onHeadsetProfileConnected((BluetoothHeadset) message.obj);
                        }
                    }
                }
            } else if (message.arg1 != 1) {
                synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                    AudioDeviceBroker.this.mBtHelper.onBtProfileDisconnected(message.arg1);
                    AudioDeviceBroker.this.mDeviceInventory.onBtProfileDisconnected(message.arg1);
                }
            } else {
                synchronized (AudioDeviceBroker.this.mSetModeLock) {
                    synchronized (AudioDeviceBroker.this.mDeviceStateLock) {
                        AudioDeviceBroker.this.mBtHelper.disconnectHeadset();
                    }
                }
            }
            if (AudioDeviceBroker.MESSAGES_MUTE_MUSIC.contains(Integer.valueOf(message.what))) {
                AudioDeviceBroker.this.sendMsg(35, 0, 100);
            }
            if (AudioDeviceBroker.isMessageHandledUnderWakelock(message.what)) {
                try {
                    AudioDeviceBroker.this.mBrokerEventWakeLock.release();
                } catch (Exception e) {
                    Log.e("AS.AudioDeviceBroker", "Exception releasing wakelock", e);
                }
            }
        }
    }

    public final void sendMsg(int i, int i2, int i3) {
        sendIILMsg(i, i2, 0, 0, null, i3);
    }

    public final void sendILMsg(int i, int i2, int i3, Object obj, int i4) {
        sendIILMsg(i, i2, i3, 0, obj, i4);
    }

    public final void sendLMsg(int i, int i2, Object obj, int i3) {
        sendIILMsg(i, i2, 0, 0, obj, i3);
    }

    public final void sendMsgNoDelay(int i, int i2) {
        sendIILMsg(i, i2, 0, 0, null, 0);
    }

    public final void sendIMsgNoDelay(int i, int i2, int i3) {
        sendIILMsg(i, i2, i3, 0, null, 0);
    }

    public final void sendIIMsgNoDelay(int i, int i2, int i3, int i4) {
        sendIILMsg(i, i2, i3, i4, null, 0);
    }

    public final void sendILMsgNoDelay(int i, int i2, int i3, Object obj) {
        sendIILMsg(i, i2, i3, 0, obj, 0);
    }

    public final void sendLMsgNoDelay(int i, int i2, Object obj) {
        sendIILMsg(i, i2, 0, 0, obj, 0);
    }

    public final void sendIILMsgNoDelay(int i, int i2, int i3, int i4, Object obj) {
        sendIILMsg(i, i2, i3, i4, obj, 0);
    }

    public final void sendIILMsg(int i, int i2, int i3, int i4, Object obj, int i5) {
        if (i2 == 0) {
            this.mBrokerHandler.removeMessages(i);
        } else if (i2 == 1 && this.mBrokerHandler.hasMessages(i)) {
            return;
        }
        if (isMessageHandledUnderWakelock(i)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    this.mBrokerEventWakeLock.acquire(5000L);
                } catch (Exception e) {
                    Log.e("AS.AudioDeviceBroker", "Exception acquiring wakelock", e);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        if (MESSAGES_MUTE_MUSIC.contains(Integer.valueOf(i))) {
            checkMessagesMuteMusic(i);
        }
        synchronized (sLastDeviceConnectionMsgTimeLock) {
            long uptimeMillis = SystemClock.uptimeMillis() + i5;
            if (i == 2 || i == 7 || i == 49 || i == 10 || i == 11) {
                long j = sLastDeviceConnectMsgTime;
                if (j >= uptimeMillis) {
                    uptimeMillis = j + 30;
                }
                sLastDeviceConnectMsgTime = uptimeMillis;
            }
            BrokerHandler brokerHandler = this.mBrokerHandler;
            brokerHandler.sendMessageAtTime(brokerHandler.obtainMessage(i, i3, i4, obj), uptimeMillis);
        }
    }

    public static <T> boolean hasIntersection(Set<T> set, Set<T> set2) {
        for (T t : set) {
            if (set2.contains(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean messageMutesMusic(int i) {
        if (i == 0) {
            return false;
        }
        return ((i == 7 || i == 29 || i == 11) && AudioSystem.isStreamActive(3, 0) && hasIntersection(AudioDeviceInventory.DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG_SET, this.mAudioService.getDeviceSetForStream(3))) ? false : true;
    }

    public final void checkMessagesMuteMusic(int i) {
        boolean messageMutesMusic = messageMutesMusic(i);
        if (!messageMutesMusic) {
            Iterator<Integer> it = MESSAGES_MUTE_MUSIC.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int intValue = it.next().intValue();
                if (this.mBrokerHandler.hasMessages(intValue) && messageMutesMusic(intValue)) {
                    messageMutesMusic = true;
                    break;
                }
            }
        }
        if (messageMutesMusic != this.mMusicMuted.getAndSet(messageMutesMusic)) {
            this.mAudioService.setMusicMute(messageMutesMusic);
        }
    }

    /* loaded from: classes.dex */
    public class CommunicationRouteClient implements IBinder.DeathRecipient {
        public final IBinder mCb;
        public AudioDeviceAttributes mDevice;
        public final int mPid;

        public CommunicationRouteClient(IBinder iBinder, int i, AudioDeviceAttributes audioDeviceAttributes) {
            this.mCb = iBinder;
            this.mPid = i;
            this.mDevice = audioDeviceAttributes;
        }

        public boolean registerDeathRecipient() {
            try {
                this.mCb.linkToDeath(this, 0);
                return true;
            } catch (RemoteException unused) {
                Log.w("AS.AudioDeviceBroker", "CommunicationRouteClient could not link to " + this.mCb + " binder death");
                return false;
            }
        }

        public void unregisterDeathRecipient() {
            try {
                this.mCb.unlinkToDeath(this, 0);
            } catch (NoSuchElementException unused) {
                Log.w("AS.AudioDeviceBroker", "CommunicationRouteClient could not not unregistered to binder");
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AudioDeviceBroker.this.postCommunicationRouteClientDied(this);
        }

        public IBinder getBinder() {
            return this.mCb;
        }

        public int getPid() {
            return this.mPid;
        }

        public AudioDeviceAttributes getDevice() {
            return this.mDevice;
        }
    }

    @GuardedBy({"mDeviceStateLock"})
    public final void onCommunicationRouteClientDied(CommunicationRouteClient communicationRouteClient) {
        if (communicationRouteClient == null) {
            return;
        }
        Log.w("AS.AudioDeviceBroker", "Communication client died");
        setCommunicationRouteForClient(communicationRouteClient.getBinder(), communicationRouteClient.getPid(), null, -1, "onCommunicationRouteClientDied");
    }

    @GuardedBy({"mDeviceStateLock"})
    public final AudioDeviceAttributes preferredCommunicationDevice() {
        AudioDeviceAttributes headsetAudioDevice;
        if (!(this.mBluetoothScoOn && this.mBtHelper.isBluetoothScoOn()) || (headsetAudioDevice = this.mBtHelper.getHeadsetAudioDevice()) == null) {
            AudioDeviceAttributes requestedCommunicationDevice = requestedCommunicationDevice();
            if (requestedCommunicationDevice == null || requestedCommunicationDevice.getType() == 7) {
                return null;
            }
            return requestedCommunicationDevice;
        }
        return headsetAudioDevice;
    }

    @GuardedBy({"mDeviceStateLock"})
    public final void updateCommunicationRoute(String str) {
        AudioDeviceAttributes preferredCommunicationDevice = preferredCommunicationDevice();
        EventLogger eventLogger = AudioService.sDeviceLogger;
        eventLogger.enqueue(new EventLogger.StringEvent("updateCommunicationRoute, preferredCommunicationDevice: " + preferredCommunicationDevice + " eventSource: " + str));
        if (preferredCommunicationDevice == null || preferredCommunicationDevice.getType() != 7) {
            AudioSystem.setParameters("BT_SCO=off");
        } else {
            AudioSystem.setParameters("BT_SCO=on");
        }
        if (preferredCommunicationDevice == null) {
            AudioDeviceAttributes defaultCommunicationDevice = getDefaultCommunicationDevice();
            if (defaultCommunicationDevice != null) {
                setPreferredDevicesForStrategySync(this.mCommunicationStrategyId, Arrays.asList(defaultCommunicationDevice));
                setPreferredDevicesForStrategySync(this.mAccessibilityStrategyId, Arrays.asList(defaultCommunicationDevice));
            } else {
                removePreferredDevicesForStrategySync(this.mCommunicationStrategyId);
                removePreferredDevicesForStrategySync(this.mAccessibilityStrategyId);
            }
        } else {
            setPreferredDevicesForStrategySync(this.mCommunicationStrategyId, Arrays.asList(preferredCommunicationDevice));
            setPreferredDevicesForStrategySync(this.mAccessibilityStrategyId, Arrays.asList(preferredCommunicationDevice));
        }
        onUpdatePhoneStrategyDevice(preferredCommunicationDevice);
    }

    @GuardedBy({"mDeviceStateLock"})
    public final void onUpdateCommunicationRouteClient(String str) {
        updateCommunicationRoute(str);
        CommunicationRouteClient communicationRouteClient = topCommunicationRouteClient();
        if (communicationRouteClient != null) {
            setCommunicationRouteForClient(communicationRouteClient.getBinder(), communicationRouteClient.getPid(), communicationRouteClient.getDevice(), -1, str);
        }
    }

    @GuardedBy({"mDeviceStateLock"})
    public final void onUpdatePhoneStrategyDevice(AudioDeviceAttributes audioDeviceAttributes) {
        boolean isSpeakerphoneActive = isSpeakerphoneActive();
        this.mPreferredCommunicationDevice = audioDeviceAttributes;
        updateActiveCommunicationDevice();
        if (isSpeakerphoneActive != isSpeakerphoneActive()) {
            try {
                this.mContext.sendBroadcastAsUser(new Intent("android.media.action.SPEAKERPHONE_STATE_CHANGED").setFlags(1073741824), UserHandle.ALL);
            } catch (Exception e) {
                Log.w("AS.AudioDeviceBroker", "failed to broadcast ACTION_SPEAKERPHONE_STATE_CHANGED: " + e);
            }
        }
        this.mAudioService.postUpdateRingerModeServiceInt();
        dispatchCommunicationDevice();
    }

    public final CommunicationRouteClient removeCommunicationRouteClient(IBinder iBinder, boolean z) {
        Iterator<CommunicationRouteClient> it = this.mCommunicationRouteClients.iterator();
        while (it.hasNext()) {
            CommunicationRouteClient next = it.next();
            if (next.getBinder() == iBinder) {
                if (z) {
                    next.unregisterDeathRecipient();
                }
                this.mCommunicationRouteClients.remove(next);
                return next;
            }
        }
        return null;
    }

    @GuardedBy({"mDeviceStateLock"})
    public final CommunicationRouteClient addCommunicationRouteClient(IBinder iBinder, int i, AudioDeviceAttributes audioDeviceAttributes) {
        removeCommunicationRouteClient(iBinder, true);
        CommunicationRouteClient communicationRouteClient = new CommunicationRouteClient(iBinder, i, audioDeviceAttributes);
        if (communicationRouteClient.registerDeathRecipient()) {
            this.mCommunicationRouteClients.add(0, communicationRouteClient);
            return communicationRouteClient;
        }
        return null;
    }

    @GuardedBy({"mDeviceStateLock"})
    public final CommunicationRouteClient getCommunicationRouteClientForPid(int i) {
        Iterator<CommunicationRouteClient> it = this.mCommunicationRouteClients.iterator();
        while (it.hasNext()) {
            CommunicationRouteClient next = it.next();
            if (next.getPid() == i) {
                return next;
            }
        }
        return null;
    }

    @GuardedBy({"mDeviceStateLock"})
    public final boolean communnicationDeviceCompatOn() {
        AudioModeInfo audioModeInfo = this.mAudioModeOwner;
        return (audioModeInfo.mMode != 3 || CompatChanges.isChangeEnabled(243827847L, audioModeInfo.mUid) || this.mAudioModeOwner.mUid == 1000) ? false : true;
    }

    @GuardedBy({"mDeviceStateLock"})
    public AudioDeviceAttributes getDefaultCommunicationDevice() {
        if (communnicationDeviceCompatOn()) {
            AudioDeviceAttributes deviceOfType = this.mDeviceInventory.getDeviceOfType(134217728);
            return deviceOfType == null ? this.mDeviceInventory.getDeviceOfType(536870912) : deviceOfType;
        }
        return null;
    }

    public UUID getDeviceSensorUuid(AudioDeviceAttributes audioDeviceAttributes) {
        UUID deviceSensorUuid;
        synchronized (this.mDeviceStateLock) {
            deviceSensorUuid = this.mDeviceInventory.getDeviceSensorUuid(audioDeviceAttributes);
        }
        return deviceSensorUuid;
    }

    public void dispatchPreferredMixerAttributesChangedCausedByDeviceRemoved(AudioDeviceInfo audioDeviceInfo) {
        this.mAudioService.dispatchPreferredMixerAttributesChanged(new AudioAttributes.Builder().setUsage(1).build(), audioDeviceInfo.getId(), null);
    }
}
