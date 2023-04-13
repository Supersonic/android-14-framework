package com.android.server.audio;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioSystem;
import android.media.INativeSpatializerCallback;
import android.media.ISpatializer;
import android.media.ISpatializerCallback;
import android.media.ISpatializerHeadToSoundStagePoseCallback;
import android.media.ISpatializerHeadTrackerAvailableCallback;
import android.media.ISpatializerHeadTrackingCallback;
import android.media.ISpatializerHeadTrackingModeCallback;
import android.media.ISpatializerOutputCallback;
import android.media.MediaMetrics;
import android.media.Spatializer;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes.dex */
public class SpatializerHelper {
    public final AudioSystemAdapter mASA;
    public final AudioService mAudioService;
    public HelperDynamicSensorCallback mDynSensorCallback;
    public SensorManager mSensorManager;
    public ISpatializer mSpat;
    public SpatializerCallback mSpatCallback;
    public static final SparseIntArray SPAT_MODE_FOR_DEVICE_TYPE = new SparseIntArray(15) { // from class: com.android.server.audio.SpatializerHelper.1
        {
            append(2, 1);
            append(3, 0);
            append(4, 0);
            append(8, 0);
            append(13, 1);
            append(12, 1);
            append(11, 1);
            append(22, 0);
            append(5, 1);
            append(6, 1);
            append(19, 1);
            append(23, 0);
            append(26, 0);
            append(27, 1);
            append(30, 0);
        }
    };
    public static final int[] WIRELESS_TYPES = {7, 8, 26, 27, 30};
    public static final AudioAttributes DEFAULT_ATTRIBUTES = new AudioAttributes.Builder().setUsage(1).build();
    public static final AudioFormat DEFAULT_FORMAT = new AudioFormat.Builder().setEncoding(2).setSampleRate(48000).setChannelMask(252).build();
    public static final AudioDeviceAttributes[] ROUTING_DEVICES = new AudioDeviceAttributes[1];
    public int mState = 0;
    public boolean mFeatureEnabled = false;
    public int mSpatLevel = 0;
    public int mCapableSpatLevel = 0;
    public boolean mTransauralSupported = false;
    public boolean mBinauralSupported = false;
    public boolean mIsHeadTrackingSupported = false;
    public int[] mSupportedHeadTrackingModes = new int[0];
    public int mActualHeadTrackingMode = -2;
    public int mDesiredHeadTrackingMode = 1;
    public boolean mHeadTrackerAvailable = false;
    public int mDesiredHeadTrackingModeWhenEnabled = 1;
    public int mSpatOutput = 0;
    public SpatializerHeadTrackingCallback mSpatHeadTrackingCallback = new SpatializerHeadTrackingCallback();
    public final ArrayList<Integer> mSACapableDeviceTypes = new ArrayList<>(0);
    @GuardedBy({"this"})
    public final ArrayList<SADeviceState> mSADevices = new ArrayList<>(0);
    public final RemoteCallbackList<ISpatializerCallback> mStateCallbacks = new RemoteCallbackList<>();
    public final RemoteCallbackList<ISpatializerHeadTrackingModeCallback> mHeadTrackingModeCallbacks = new RemoteCallbackList<>();
    public final RemoteCallbackList<ISpatializerHeadTrackerAvailableCallback> mHeadTrackerCallbacks = new RemoteCallbackList<>();
    public final RemoteCallbackList<ISpatializerHeadToSoundStagePoseCallback> mHeadPoseCallbacks = new RemoteCallbackList<>();
    public final RemoteCallbackList<ISpatializerOutputCallback> mOutputCallbacks = new RemoteCallbackList<>();

    public static String spatStateString(int i) {
        return i != 0 ? i != 1 ? i != 3 ? i != 4 ? i != 5 ? i != 6 ? "invalid state" : "STATE_DISABLED_AVAILABLE" : "STATE_ENABLED_AVAILABLE" : "STATE_ENABLED_UNAVAILABLE" : "STATE_DISABLED_UNAVAILABLE" : "STATE_NOT_SUPPORTED" : "STATE_UNINITIALIZED";
    }

    public static void logd(String str) {
        Log.i("AS.SpatializerHelper", str);
    }

    public SpatializerHelper(AudioService audioService, AudioSystemAdapter audioSystemAdapter, boolean z) {
        this.mAudioService = audioService;
        this.mASA = audioSystemAdapter;
        SADeviceState.sHeadTrackingEnabledDefault = z;
    }

    public synchronized void init(boolean z, String str) {
        byte[] supportedLevels;
        byte[] supportedModes;
        loglogi("init effectExpected=" + z);
        if (!z) {
            loglogi("init(): setting state to STATE_NOT_SUPPORTED due to effect not expected");
            this.mState = 1;
        } else if (this.mState != 0) {
            throw new IllegalStateException(logloge("init() called in state " + this.mState));
        } else {
            SpatializerCallback spatializerCallback = new SpatializerCallback();
            this.mSpatCallback = spatializerCallback;
            ISpatializer spatializer = AudioSystem.getSpatializer(spatializerCallback);
            if (spatializer == null) {
                loglogi("init(): No Spatializer found");
                this.mState = 1;
                return;
            }
            resetCapabilities();
            try {
                supportedLevels = spatializer.getSupportedLevels();
            } catch (RemoteException unused) {
                resetCapabilities();
            }
            if (supportedLevels != null && supportedLevels.length != 0 && (supportedLevels.length != 1 || supportedLevels[0] != 0)) {
                int length = supportedLevels.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    byte b = supportedLevels[i];
                    loglogi("init(): found support for level: " + ((int) b));
                    if (b == 1) {
                        loglogi("init(): setting capable level to LEVEL_MULTICHANNEL");
                        this.mCapableSpatLevel = b;
                        break;
                    }
                    i++;
                }
                boolean isHeadTrackingSupported = spatializer.isHeadTrackingSupported();
                this.mIsHeadTrackingSupported = isHeadTrackingSupported;
                if (isHeadTrackingSupported) {
                    byte[] supportedHeadTrackingModes = spatializer.getSupportedHeadTrackingModes();
                    ArrayList arrayList = new ArrayList(0);
                    for (byte b2 : supportedHeadTrackingModes) {
                        if (b2 != 0 && b2 != 1) {
                            if (b2 == 2 || b2 == 3) {
                                arrayList.add(Integer.valueOf(headTrackingModeTypeToSpatializerInt(b2)));
                            } else {
                                Log.e("AS.SpatializerHelper", "Unexpected head tracking mode:" + ((int) b2), new IllegalArgumentException("invalid mode"));
                            }
                        }
                    }
                    this.mSupportedHeadTrackingModes = new int[arrayList.size()];
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        this.mSupportedHeadTrackingModes[i2] = ((Integer) arrayList.get(i2)).intValue();
                    }
                    this.mActualHeadTrackingMode = headTrackingModeTypeToSpatializerInt(spatializer.getActualHeadTrackingMode());
                } else {
                    this.mDesiredHeadTrackingModeWhenEnabled = -2;
                    this.mDesiredHeadTrackingMode = -2;
                }
                for (byte b3 : spatializer.getSupportedModes()) {
                    if (b3 == 0) {
                        this.mBinauralSupported = true;
                    } else if (b3 != 1) {
                        logloge("init(): Spatializer reports unknown supported mode:" + ((int) b3));
                    } else {
                        this.mTransauralSupported = true;
                    }
                }
                if (!this.mBinauralSupported && !this.mTransauralSupported) {
                    this.mState = 1;
                    try {
                        spatializer.release();
                    } catch (RemoteException unused2) {
                    }
                    return;
                }
                int i3 = 0;
                while (true) {
                    SparseIntArray sparseIntArray = SPAT_MODE_FOR_DEVICE_TYPE;
                    if (i3 >= sparseIntArray.size()) {
                        break;
                    }
                    int valueAt = sparseIntArray.valueAt(i3);
                    if ((valueAt == 0 && this.mBinauralSupported) || (valueAt == 1 && this.mTransauralSupported)) {
                        this.mSACapableDeviceTypes.add(Integer.valueOf(sparseIntArray.keyAt(i3)));
                    }
                    i3++;
                }
                if (str != null) {
                    setSADeviceSettings(str);
                }
                addCompatibleAudioDevice(new AudioDeviceAttributes(2, ""), false);
                addCompatibleAudioDevice(new AudioDeviceAttributes(8, ""), false);
                try {
                    spatializer.release();
                } catch (RemoteException unused3) {
                    if (this.mCapableSpatLevel == 0) {
                        this.mState = 1;
                        return;
                    }
                    this.mState = 3;
                    this.mASA.getDevicesForAttributes(DEFAULT_ATTRIBUTES, false).toArray(ROUTING_DEVICES);
                    return;
                }
            }
            logloge("init(): found Spatializer is useless");
            this.mState = 1;
            try {
                spatializer.release();
            } catch (RemoteException unused4) {
            }
        }
    }

    public synchronized void reset(boolean z) {
        loglogi("Resetting featureEnabled=" + z);
        releaseSpat();
        this.mState = 0;
        this.mSpatLevel = 0;
        this.mActualHeadTrackingMode = -2;
        init(true, null);
        setSpatializerEnabledInt(z);
    }

    public final void resetCapabilities() {
        this.mCapableSpatLevel = 0;
        this.mBinauralSupported = false;
        this.mTransauralSupported = false;
        this.mIsHeadTrackingSupported = false;
        this.mSupportedHeadTrackingModes = new int[0];
    }

    public synchronized void onRoutingUpdated() {
        boolean z;
        if (this.mFeatureEnabled) {
            int i = this.mState;
            if (i != 0) {
                byte b = 1;
                if (i != 1) {
                    AudioSystemAdapter audioSystemAdapter = this.mASA;
                    AudioAttributes audioAttributes = DEFAULT_ATTRIBUTES;
                    ArrayList<AudioDeviceAttributes> devicesForAttributes = audioSystemAdapter.getDevicesForAttributes(audioAttributes, false);
                    AudioDeviceAttributes[] audioDeviceAttributesArr = ROUTING_DEVICES;
                    devicesForAttributes.toArray(audioDeviceAttributesArr);
                    AudioDeviceAttributes audioDeviceAttributes = audioDeviceAttributesArr[0];
                    if (audioDeviceAttributes == null) {
                        logloge("onRoutingUpdated: device is null, no Spatial Audio");
                        setDispatchAvailableState(false);
                        return;
                    }
                    if (isWireless(audioDeviceAttributes.getType())) {
                        addWirelessDeviceIfNew(audioDeviceAttributesArr[0]);
                    }
                    Pair<Boolean, Boolean> evaluateState = evaluateState(audioDeviceAttributesArr[0]);
                    if (((Boolean) evaluateState.second).booleanValue()) {
                        z = canBeSpatializedOnDevice(audioAttributes, DEFAULT_FORMAT, audioDeviceAttributesArr);
                        loglogi("onRoutingUpdated: can spatialize media 5.1:" + z + " on device:" + audioDeviceAttributesArr[0]);
                        setDispatchAvailableState(z);
                    } else {
                        loglogi("onRoutingUpdated: device:" + audioDeviceAttributesArr[0] + " not available for Spatial Audio");
                        setDispatchAvailableState(false);
                        z = false;
                    }
                    boolean z2 = z && ((Boolean) evaluateState.first).booleanValue();
                    if (z2) {
                        loglogi("Enabling Spatial Audio since enabled for media device:" + audioDeviceAttributesArr[0]);
                    } else {
                        loglogi("Disabling Spatial Audio since disabled for media device:" + audioDeviceAttributesArr[0]);
                    }
                    if (this.mSpat != null) {
                        if (!z2) {
                            b = 0;
                        }
                        loglogi("Setting spatialization level to: " + ((int) b));
                        try {
                            this.mSpat.setLevel(b);
                        } catch (RemoteException e) {
                            Log.e("AS.SpatializerHelper", "onRoutingUpdated() Can't set spatializer level", e);
                            postReset();
                            return;
                        }
                    }
                    setDispatchFeatureEnabledState(z2, "onRoutingUpdated");
                    int i2 = this.mDesiredHeadTrackingMode;
                    if (i2 != -2 && i2 != -1) {
                        postInitSensors();
                    }
                }
            }
        }
    }

    public final void postReset() {
        this.mAudioService.postResetSpatializer();
    }

    /* loaded from: classes.dex */
    public final class SpatializerCallback extends INativeSpatializerCallback.Stub {
        public SpatializerCallback() {
        }

        public void onLevelChanged(byte b) {
            SpatializerHelper.loglogi("SpatializerCallback.onLevelChanged level:" + ((int) b));
            synchronized (SpatializerHelper.this) {
                SpatializerHelper.this.mSpatLevel = SpatializerHelper.spatializationLevelToSpatializerInt(b);
            }
            SpatializerHelper.this.postInitSensors();
        }

        public void onOutputChanged(int i) {
            int i2;
            SpatializerHelper.loglogi("SpatializerCallback.onOutputChanged output:" + i);
            synchronized (SpatializerHelper.this) {
                i2 = SpatializerHelper.this.mSpatOutput;
                SpatializerHelper.this.mSpatOutput = i;
            }
            if (i2 != i) {
                SpatializerHelper.this.dispatchOutputUpdate(i);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class SpatializerHeadTrackingCallback extends ISpatializerHeadTrackingCallback.Stub {
        public SpatializerHeadTrackingCallback() {
        }

        public void onHeadTrackingModeChanged(byte b) {
            int i;
            int i2;
            synchronized (this) {
                i = SpatializerHelper.this.mActualHeadTrackingMode;
                SpatializerHelper.this.mActualHeadTrackingMode = SpatializerHelper.headTrackingModeTypeToSpatializerInt(b);
                i2 = SpatializerHelper.this.mActualHeadTrackingMode;
            }
            SpatializerHelper.loglogi("SpatializerHeadTrackingCallback.onHeadTrackingModeChanged mode:" + Spatializer.headtrackingModeToString(i2));
            if (i != i2) {
                SpatializerHelper.this.dispatchActualHeadTrackingMode(i2);
            }
        }

        public void onHeadToSoundStagePoseUpdated(float[] fArr) {
            if (fArr == null) {
                Log.e("AS.SpatializerHelper", "SpatializerHeadTrackingCallback.onHeadToStagePoseUpdatednull transform");
            } else if (fArr.length != 6) {
                Log.e("AS.SpatializerHelper", "SpatializerHeadTrackingCallback.onHeadToStagePoseUpdated invalid transform length" + fArr.length);
            } else {
                SpatializerHelper.this.dispatchPoseUpdate(fArr);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class HelperDynamicSensorCallback extends SensorManager.DynamicSensorCallback {
        public HelperDynamicSensorCallback() {
        }

        @Override // android.hardware.SensorManager.DynamicSensorCallback
        public void onDynamicSensorConnected(Sensor sensor) {
            SpatializerHelper.this.postInitSensors();
        }

        @Override // android.hardware.SensorManager.DynamicSensorCallback
        public void onDynamicSensorDisconnected(Sensor sensor) {
            SpatializerHelper.this.postInitSensors();
        }
    }

    public synchronized List<AudioDeviceAttributes> getCompatibleAudioDevices() {
        ArrayList arrayList;
        arrayList = new ArrayList();
        Iterator<SADeviceState> it = this.mSADevices.iterator();
        while (it.hasNext()) {
            SADeviceState next = it.next();
            if (next.mEnabled) {
                arrayList.add(next.getAudioDeviceAttributes());
            }
        }
        return arrayList;
    }

    public synchronized void addCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes) {
        addCompatibleAudioDevice(audioDeviceAttributes, true);
    }

    @GuardedBy({"this"})
    public final void addCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes, boolean z) {
        if (isDeviceCompatibleWithSpatializationModes(audioDeviceAttributes)) {
            loglogi("addCompatibleAudioDevice: dev=" + audioDeviceAttributes);
            SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
            if (findDeviceStateForAudioDeviceAttributes != null) {
                if (!z || findDeviceStateForAudioDeviceAttributes.mEnabled) {
                    findDeviceStateForAudioDeviceAttributes = null;
                } else {
                    findDeviceStateForAudioDeviceAttributes.mEnabled = true;
                }
            } else {
                int canonicalDeviceType = getCanonicalDeviceType(audioDeviceAttributes.getType());
                if (canonicalDeviceType == 0) {
                    Log.e("AS.SpatializerHelper", "addCompatibleAudioDevice with incompatible AudioDeviceAttributes " + audioDeviceAttributes);
                    return;
                }
                findDeviceStateForAudioDeviceAttributes = new SADeviceState(canonicalDeviceType, audioDeviceAttributes.getAddress());
                this.mSADevices.add(findDeviceStateForAudioDeviceAttributes);
            }
            if (findDeviceStateForAudioDeviceAttributes != null) {
                onRoutingUpdated();
                this.mAudioService.persistSpatialAudioDeviceSettings();
                logDeviceState(findDeviceStateForAudioDeviceAttributes, "addCompatibleAudioDevice");
            }
        }
    }

    public final void logDeviceState(SADeviceState sADeviceState, String str) {
        String deviceName = AudioSystem.getDeviceName(AudioDeviceInfo.convertDeviceTypeToInternalDevice(sADeviceState.mDeviceType));
        new MediaMetrics.Item("audio.spatializer.device." + deviceName).set(MediaMetrics.Property.ADDRESS, sADeviceState.mDeviceAddress).set(MediaMetrics.Property.ENABLED, sADeviceState.mEnabled ? "true" : "false").set(MediaMetrics.Property.EVENT, TextUtils.emptyIfNull(str)).set(MediaMetrics.Property.HAS_HEAD_TRACKER, sADeviceState.mHasHeadTracker ? "true" : "false").set(MediaMetrics.Property.HEAD_TRACKER_ENABLED, sADeviceState.mHeadTrackerEnabled ? "true" : "false").record();
    }

    public synchronized void removeCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes) {
        loglogi("removeCompatibleAudioDevice: dev=" + audioDeviceAttributes);
        SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
        if (findDeviceStateForAudioDeviceAttributes != null && findDeviceStateForAudioDeviceAttributes.mEnabled) {
            findDeviceStateForAudioDeviceAttributes.mEnabled = false;
            onRoutingUpdated();
            this.mAudioService.persistSpatialAudioDeviceSettings();
            logDeviceState(findDeviceStateForAudioDeviceAttributes, "removeCompatibleAudioDevice");
        }
    }

    public static int getCanonicalDeviceType(int i) {
        if (isWireless(i)) {
            return i;
        }
        int i2 = SPAT_MODE_FOR_DEVICE_TYPE.get(i, Integer.MIN_VALUE);
        if (i2 == 1) {
            return 2;
        }
        return i2 == 0 ? 4 : 0;
    }

    @GuardedBy({"this"})
    public final SADeviceState findDeviceStateForAudioDeviceAttributes(AudioDeviceAttributes audioDeviceAttributes) {
        int type = audioDeviceAttributes.getType();
        boolean isWireless = isWireless(type);
        int canonicalDeviceType = getCanonicalDeviceType(type);
        Iterator<SADeviceState> it = this.mSADevices.iterator();
        while (it.hasNext()) {
            SADeviceState next = it.next();
            if (next.mDeviceType == canonicalDeviceType && (!isWireless || audioDeviceAttributes.getAddress().equals(next.mDeviceAddress))) {
                return next;
            }
        }
        return null;
    }

    public final synchronized Pair<Boolean, Boolean> evaluateState(AudioDeviceAttributes audioDeviceAttributes) {
        int type = audioDeviceAttributes.getType();
        if (!this.mSACapableDeviceTypes.contains(Integer.valueOf(type))) {
            Log.i("AS.SpatializerHelper", "Device incompatible with Spatial Audio dev:" + audioDeviceAttributes);
            Boolean bool = Boolean.FALSE;
            return new Pair<>(bool, bool);
        } else if (SPAT_MODE_FOR_DEVICE_TYPE.get(type, Integer.MIN_VALUE) == Integer.MIN_VALUE) {
            Log.e("AS.SpatializerHelper", "no spatialization mode found for device type:" + type);
            Boolean bool2 = Boolean.FALSE;
            return new Pair<>(bool2, bool2);
        } else {
            SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
            if (findDeviceStateForAudioDeviceAttributes == null) {
                Log.i("AS.SpatializerHelper", "no spatialization device state found for Spatial Audio device:" + audioDeviceAttributes);
                Boolean bool3 = Boolean.FALSE;
                return new Pair<>(bool3, bool3);
            }
            return new Pair<>(Boolean.valueOf(findDeviceStateForAudioDeviceAttributes.mEnabled), Boolean.TRUE);
        }
    }

    public final synchronized void addWirelessDeviceIfNew(AudioDeviceAttributes audioDeviceAttributes) {
        if (isDeviceCompatibleWithSpatializationModes(audioDeviceAttributes)) {
            if (findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes) == null) {
                int canonicalDeviceType = getCanonicalDeviceType(audioDeviceAttributes.getType());
                if (canonicalDeviceType == 0) {
                    Log.e("AS.SpatializerHelper", "addWirelessDeviceIfNew with incompatible AudioDeviceAttributes " + audioDeviceAttributes);
                    return;
                }
                SADeviceState sADeviceState = new SADeviceState(canonicalDeviceType, audioDeviceAttributes.getAddress());
                this.mSADevices.add(sADeviceState);
                this.mAudioService.persistSpatialAudioDeviceSettings();
                logDeviceState(sADeviceState, "addWirelessDeviceIfNew");
            }
        }
    }

    public synchronized boolean isEnabled() {
        int i = this.mState;
        return (i == 0 || i == 1 || i == 3 || i == 6) ? false : true;
    }

    public synchronized boolean isAvailable() {
        int i = this.mState;
        return (i == 0 || i == 1 || i == 3 || i == 4) ? false : true;
    }

    public synchronized boolean isAvailableForDevice(AudioDeviceAttributes audioDeviceAttributes) {
        if (audioDeviceAttributes.getRole() != 2) {
            return false;
        }
        return findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes) != null;
    }

    public final synchronized boolean canBeSpatializedOnDevice(AudioAttributes audioAttributes, AudioFormat audioFormat, AudioDeviceAttributes[] audioDeviceAttributesArr) {
        if (isDeviceCompatibleWithSpatializationModes(audioDeviceAttributesArr[0])) {
            return AudioSystem.canBeSpatialized(audioAttributes, audioFormat, audioDeviceAttributesArr);
        }
        return false;
    }

    public final boolean isDeviceCompatibleWithSpatializationModes(AudioDeviceAttributes audioDeviceAttributes) {
        byte b = (byte) SPAT_MODE_FOR_DEVICE_TYPE.get(audioDeviceAttributes.getType(), -1);
        return (b == 0 && this.mBinauralSupported) || (b == 1 && this.mTransauralSupported);
    }

    public synchronized void setFeatureEnabled(boolean z) {
        loglogi("setFeatureEnabled(" + z + ") was featureEnabled:" + this.mFeatureEnabled);
        if (this.mFeatureEnabled == z) {
            return;
        }
        this.mFeatureEnabled = z;
        if (z) {
            int i = this.mState;
            if (i == 1) {
                Log.e("AS.SpatializerHelper", "Can't enabled Spatial Audio, unsupported");
                return;
            }
            if (i == 0) {
                init(true, null);
            }
            setSpatializerEnabledInt(true);
        } else {
            setSpatializerEnabledInt(false);
        }
    }

    public synchronized void setSpatializerEnabledInt(boolean z) {
        int i = this.mState;
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    if (i == 4 || i == 5) {
                        if (!z) {
                            releaseSpat();
                            setDispatchFeatureEnabledState(false, "setSpatializerEnabledInt");
                        }
                    } else if (i != 6) {
                    }
                }
                if (z) {
                    createSpat();
                    onRoutingUpdated();
                }
            } else if (z) {
                Log.e("AS.SpatializerHelper", "Can't enable when unsupported");
            }
        } else if (z) {
            throw new IllegalStateException("Can't enable when uninitialized");
        }
    }

    public synchronized int getCapableImmersiveAudioLevel() {
        return this.mCapableSpatLevel;
    }

    public synchronized void registerStateCallback(ISpatializerCallback iSpatializerCallback) {
        this.mStateCallbacks.register(iSpatializerCallback);
    }

    public synchronized void unregisterStateCallback(ISpatializerCallback iSpatializerCallback) {
        this.mStateCallbacks.unregister(iSpatializerCallback);
    }

    public final synchronized void setDispatchFeatureEnabledState(boolean z, String str) {
        if (z) {
            int i = this.mState;
            if (i == 3) {
                this.mState = 4;
            } else if (i == 4 || i == 5) {
                loglogi("setDispatchFeatureEnabledState(" + z + ") no dispatch: mState:" + spatStateString(this.mState) + " src:" + str);
                return;
            } else if (i == 6) {
                this.mState = 5;
            } else {
                throw new IllegalStateException("Invalid mState:" + this.mState + " for enabled true");
            }
        } else {
            int i2 = this.mState;
            if (i2 != 3) {
                if (i2 == 4) {
                    this.mState = 3;
                } else if (i2 == 5) {
                    this.mState = 6;
                } else if (i2 != 6) {
                    throw new IllegalStateException("Invalid mState:" + this.mState + " for enabled false");
                }
            }
            loglogi("setDispatchFeatureEnabledState(" + z + ") no dispatch: mState:" + spatStateString(this.mState) + " src:" + str);
            return;
        }
        loglogi("setDispatchFeatureEnabledState(" + z + ") mState:" + spatStateString(this.mState));
        int beginBroadcast = this.mStateCallbacks.beginBroadcast();
        for (int i3 = 0; i3 < beginBroadcast; i3++) {
            try {
                this.mStateCallbacks.getBroadcastItem(i3).dispatchSpatializerEnabledChanged(z);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchSpatializerEnabledChanged", e);
            }
        }
        this.mStateCallbacks.finishBroadcast();
    }

    public final synchronized void setDispatchAvailableState(boolean z) {
        int i = this.mState;
        if (i == 0 || i == 1) {
            throw new IllegalStateException("Should not update available state in state:" + this.mState);
        }
        if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    if (i == 6) {
                        if (z) {
                            loglogi("setDispatchAvailableState(" + z + ") no dispatch: mState:" + spatStateString(this.mState));
                            return;
                        }
                        this.mState = 3;
                    }
                } else if (z) {
                    loglogi("setDispatchAvailableState(" + z + ") no dispatch: mState:" + spatStateString(this.mState));
                    return;
                } else {
                    this.mState = 4;
                }
            } else if (z) {
                this.mState = 5;
            } else {
                loglogi("setDispatchAvailableState(" + z + ") no dispatch: mState:" + spatStateString(this.mState));
                return;
            }
        } else if (z) {
            this.mState = 6;
        } else {
            loglogi("setDispatchAvailableState(" + z + ") no dispatch: mState:" + spatStateString(this.mState));
            return;
        }
        loglogi("setDispatchAvailableState(" + z + ") mState:" + spatStateString(this.mState));
        int beginBroadcast = this.mStateCallbacks.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mStateCallbacks.getBroadcastItem(i2).dispatchSpatializerAvailableChanged(z);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchSpatializerEnabledChanged", e);
            }
        }
        this.mStateCallbacks.finishBroadcast();
    }

    public final void createSpat() {
        if (this.mSpat == null) {
            SpatializerCallback spatializerCallback = new SpatializerCallback();
            this.mSpatCallback = spatializerCallback;
            ISpatializer spatializer = AudioSystem.getSpatializer(spatializerCallback);
            this.mSpat = spatializer;
            try {
                if (this.mIsHeadTrackingSupported) {
                    this.mActualHeadTrackingMode = headTrackingModeTypeToSpatializerInt(spatializer.getActualHeadTrackingMode());
                    this.mSpat.registerHeadTrackingCallback(this.mSpatHeadTrackingCallback);
                }
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Can't configure head tracking", e);
                this.mState = 1;
                this.mCapableSpatLevel = 0;
                this.mActualHeadTrackingMode = -2;
            }
        }
    }

    public final void releaseSpat() {
        ISpatializer iSpatializer = this.mSpat;
        if (iSpatializer != null) {
            this.mSpatCallback = null;
            try {
                if (this.mIsHeadTrackingSupported) {
                    iSpatializer.registerHeadTrackingCallback((ISpatializerHeadTrackingCallback) null);
                }
                this.mHeadTrackerAvailable = false;
                this.mSpat.release();
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Can't set release spatializer cleanly", e);
            }
            this.mSpat = null;
        }
    }

    public synchronized boolean canBeSpatialized(AudioAttributes audioAttributes, AudioFormat audioFormat) {
        int i = this.mState;
        if (i == 0 || i == 1 || i == 3 || i == 4) {
            logd("canBeSpatialized false due to state:" + this.mState);
            return false;
        }
        int usage = audioAttributes.getUsage();
        if (usage != 1 && usage != 14) {
            logd("canBeSpatialized false due to usage:" + audioAttributes.getUsage());
            return false;
        }
        AudioDeviceAttributes[] audioDeviceAttributesArr = new AudioDeviceAttributes[1];
        this.mASA.getDevicesForAttributes(audioAttributes, false).toArray(audioDeviceAttributesArr);
        boolean canBeSpatializedOnDevice = canBeSpatializedOnDevice(audioAttributes, audioFormat, audioDeviceAttributesArr);
        logd("canBeSpatialized usage:" + audioAttributes.getUsage() + " format:" + audioFormat.toLogFriendlyString() + " returning " + canBeSpatializedOnDevice);
        return canBeSpatializedOnDevice;
    }

    public synchronized void registerHeadTrackingModeCallback(ISpatializerHeadTrackingModeCallback iSpatializerHeadTrackingModeCallback) {
        this.mHeadTrackingModeCallbacks.register(iSpatializerHeadTrackingModeCallback);
    }

    public synchronized void unregisterHeadTrackingModeCallback(ISpatializerHeadTrackingModeCallback iSpatializerHeadTrackingModeCallback) {
        this.mHeadTrackingModeCallbacks.unregister(iSpatializerHeadTrackingModeCallback);
    }

    public synchronized void registerHeadTrackerAvailableCallback(ISpatializerHeadTrackerAvailableCallback iSpatializerHeadTrackerAvailableCallback, boolean z) {
        if (z) {
            this.mHeadTrackerCallbacks.register(iSpatializerHeadTrackerAvailableCallback);
        } else {
            this.mHeadTrackerCallbacks.unregister(iSpatializerHeadTrackerAvailableCallback);
        }
    }

    public synchronized int[] getSupportedHeadTrackingModes() {
        return this.mSupportedHeadTrackingModes;
    }

    public synchronized int getActualHeadTrackingMode() {
        return this.mActualHeadTrackingMode;
    }

    public synchronized int getDesiredHeadTrackingMode() {
        return this.mDesiredHeadTrackingMode;
    }

    public synchronized void setGlobalTransform(float[] fArr) {
        if (fArr.length != 6) {
            throw new IllegalArgumentException("invalid array size" + fArr.length);
        } else if (checkSpatForHeadTracking("setGlobalTransform")) {
            try {
                this.mSpat.setGlobalTransform(fArr);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error calling setGlobalTransform", e);
            }
        }
    }

    public synchronized void recenterHeadTracker() {
        if (checkSpatForHeadTracking("recenterHeadTracker")) {
            try {
                this.mSpat.recenterHeadTracker();
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error calling recenterHeadTracker", e);
            }
        }
    }

    public synchronized void setDesiredHeadTrackingMode(@Spatializer.HeadTrackingModeSet int i) {
        if (checkSpatForHeadTracking("setDesiredHeadTrackingMode")) {
            if (i != -1) {
                this.mDesiredHeadTrackingModeWhenEnabled = i;
            }
            try {
                if (this.mDesiredHeadTrackingMode != i) {
                    this.mDesiredHeadTrackingMode = i;
                    dispatchDesiredHeadTrackingMode(i);
                }
                Log.i("AS.SpatializerHelper", "setDesiredHeadTrackingMode(" + Spatializer.headtrackingModeToString(i) + ")");
                this.mSpat.setDesiredHeadTrackingMode(spatializerIntToHeadTrackingModeType(i));
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error calling setDesiredHeadTrackingMode", e);
            }
        }
    }

    public synchronized void setHeadTrackerEnabled(boolean z, AudioDeviceAttributes audioDeviceAttributes) {
        if (!this.mIsHeadTrackingSupported) {
            Log.v("AS.SpatializerHelper", "no headtracking support, ignoring setHeadTrackerEnabled to " + z + " for " + audioDeviceAttributes);
        }
        SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
        if (findDeviceStateForAudioDeviceAttributes == null) {
            return;
        }
        if (!findDeviceStateForAudioDeviceAttributes.mHasHeadTracker) {
            Log.e("AS.SpatializerHelper", "Called setHeadTrackerEnabled enabled:" + z + " device:" + audioDeviceAttributes + " on a device without headtracker");
            return;
        }
        Log.i("AS.SpatializerHelper", "setHeadTrackerEnabled enabled:" + z + " device:" + audioDeviceAttributes);
        findDeviceStateForAudioDeviceAttributes.mHeadTrackerEnabled = z;
        this.mAudioService.persistSpatialAudioDeviceSettings();
        logDeviceState(findDeviceStateForAudioDeviceAttributes, "setHeadTrackerEnabled");
        AudioDeviceAttributes[] audioDeviceAttributesArr = ROUTING_DEVICES;
        AudioDeviceAttributes audioDeviceAttributes2 = audioDeviceAttributesArr[0];
        if (audioDeviceAttributes2 != null && audioDeviceAttributes2.getType() == audioDeviceAttributes.getType() && audioDeviceAttributesArr[0].getAddress().equals(audioDeviceAttributes.getAddress())) {
            setDesiredHeadTrackingMode(z ? this.mDesiredHeadTrackingModeWhenEnabled : -1);
            if (z && !this.mHeadTrackerAvailable) {
                postInitSensors();
            }
        }
    }

    public synchronized boolean hasHeadTracker(AudioDeviceAttributes audioDeviceAttributes) {
        boolean z = false;
        if (!this.mIsHeadTrackingSupported) {
            Log.v("AS.SpatializerHelper", "no headtracking support, hasHeadTracker always false for " + audioDeviceAttributes);
            return false;
        }
        SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
        if (findDeviceStateForAudioDeviceAttributes != null && findDeviceStateForAudioDeviceAttributes.mHasHeadTracker) {
            z = true;
        }
        return z;
    }

    public synchronized boolean setHasHeadTracker(AudioDeviceAttributes audioDeviceAttributes) {
        if (!this.mIsHeadTrackingSupported) {
            Log.v("AS.SpatializerHelper", "no headtracking support, setHasHeadTracker always false for " + audioDeviceAttributes);
            return false;
        }
        SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
        if (findDeviceStateForAudioDeviceAttributes != null) {
            if (!findDeviceStateForAudioDeviceAttributes.mHasHeadTracker) {
                findDeviceStateForAudioDeviceAttributes.mHasHeadTracker = true;
                this.mAudioService.persistSpatialAudioDeviceSettings();
                logDeviceState(findDeviceStateForAudioDeviceAttributes, "setHasHeadTracker");
            }
            return findDeviceStateForAudioDeviceAttributes.mHeadTrackerEnabled;
        }
        Log.e("AS.SpatializerHelper", "setHasHeadTracker: device not found for:" + audioDeviceAttributes);
        return false;
    }

    public synchronized boolean isHeadTrackerEnabled(AudioDeviceAttributes audioDeviceAttributes) {
        boolean z = false;
        if (!this.mIsHeadTrackingSupported) {
            Log.v("AS.SpatializerHelper", "no headtracking support, isHeadTrackerEnabled always false for " + audioDeviceAttributes);
            return false;
        }
        SADeviceState findDeviceStateForAudioDeviceAttributes = findDeviceStateForAudioDeviceAttributes(audioDeviceAttributes);
        if (findDeviceStateForAudioDeviceAttributes != null && findDeviceStateForAudioDeviceAttributes.mHasHeadTracker && findDeviceStateForAudioDeviceAttributes.mHeadTrackerEnabled) {
            z = true;
        }
        return z;
    }

    public synchronized boolean isHeadTrackerAvailable() {
        return this.mHeadTrackerAvailable;
    }

    public final boolean checkSpatForHeadTracking(String str) {
        int i = this.mState;
        if (i == 0 || i == 1) {
            return false;
        }
        if ((i == 3 || i == 4 || i == 5 || i == 6) && this.mSpat == null) {
            Log.e("AS.SpatializerHelper", "checkSpatForHeadTracking(): native spatializer should not be null in state: " + this.mState);
            postReset();
            return false;
        }
        return this.mIsHeadTrackingSupported;
    }

    public final void dispatchActualHeadTrackingMode(int i) {
        int beginBroadcast = this.mHeadTrackingModeCallbacks.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mHeadTrackingModeCallbacks.getBroadcastItem(i2).dispatchSpatializerActualHeadTrackingModeChanged(i);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchSpatializerActualHeadTrackingModeChanged(" + i + ")", e);
            }
        }
        this.mHeadTrackingModeCallbacks.finishBroadcast();
    }

    public final void dispatchDesiredHeadTrackingMode(int i) {
        int beginBroadcast = this.mHeadTrackingModeCallbacks.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mHeadTrackingModeCallbacks.getBroadcastItem(i2).dispatchSpatializerDesiredHeadTrackingModeChanged(i);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchSpatializerDesiredHeadTrackingModeChanged(" + i + ")", e);
            }
        }
        this.mHeadTrackingModeCallbacks.finishBroadcast();
    }

    public final void dispatchHeadTrackerAvailable(boolean z) {
        int beginBroadcast = this.mHeadTrackerCallbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mHeadTrackerCallbacks.getBroadcastItem(i).dispatchSpatializerHeadTrackerAvailable(z);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchSpatializerHeadTrackerAvailable(" + z + ")", e);
            }
        }
        this.mHeadTrackerCallbacks.finishBroadcast();
    }

    public synchronized void registerHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback iSpatializerHeadToSoundStagePoseCallback) {
        this.mHeadPoseCallbacks.register(iSpatializerHeadToSoundStagePoseCallback);
    }

    public synchronized void unregisterHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback iSpatializerHeadToSoundStagePoseCallback) {
        this.mHeadPoseCallbacks.unregister(iSpatializerHeadToSoundStagePoseCallback);
    }

    public final void dispatchPoseUpdate(float[] fArr) {
        int beginBroadcast = this.mHeadPoseCallbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mHeadPoseCallbacks.getBroadcastItem(i).dispatchPoseChanged(fArr);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchPoseChanged", e);
            }
        }
        this.mHeadPoseCallbacks.finishBroadcast();
    }

    public synchronized void setEffectParameter(int i, byte[] bArr) {
        int i2 = this.mState;
        if (i2 == 0 || i2 == 1) {
            throw new IllegalStateException("Can't set parameter key:" + i + " without a spatializer");
        }
        if ((i2 == 3 || i2 == 4 || i2 == 5 || i2 == 6) && this.mSpat == null) {
            Log.e("AS.SpatializerHelper", "setParameter(" + i + "): null spatializer in state: " + this.mState);
            return;
        }
        try {
            this.mSpat.setParameter(i, bArr);
        } catch (RemoteException e) {
            Log.e("AS.SpatializerHelper", "Error in setParameter for key:" + i, e);
        }
    }

    public synchronized void getEffectParameter(int i, byte[] bArr) {
        int i2 = this.mState;
        if (i2 == 0 || i2 == 1) {
            throw new IllegalStateException("Can't get parameter key:" + i + " without a spatializer");
        }
        if ((i2 == 3 || i2 == 4 || i2 == 5 || i2 == 6) && this.mSpat == null) {
            Log.e("AS.SpatializerHelper", "getParameter(" + i + "): null spatializer in state: " + this.mState);
            return;
        }
        try {
            this.mSpat.getParameter(i, bArr);
        } catch (RemoteException e) {
            Log.e("AS.SpatializerHelper", "Error in getParameter for key:" + i, e);
        }
    }

    public synchronized int getOutput() {
        int i = this.mState;
        if (i == 0 || i == 1) {
            throw new IllegalStateException("Can't get output without a spatializer");
        }
        if ((i == 3 || i == 4 || i == 5 || i == 6) && this.mSpat == null) {
            throw new IllegalStateException("null Spatializer for getOutput");
        }
        try {
        } catch (RemoteException e) {
            Log.e("AS.SpatializerHelper", "Error in getOutput", e);
            return 0;
        }
        return this.mSpat.getOutput();
    }

    public synchronized void registerSpatializerOutputCallback(ISpatializerOutputCallback iSpatializerOutputCallback) {
        this.mOutputCallbacks.register(iSpatializerOutputCallback);
    }

    public synchronized void unregisterSpatializerOutputCallback(ISpatializerOutputCallback iSpatializerOutputCallback) {
        this.mOutputCallbacks.unregister(iSpatializerOutputCallback);
    }

    public final void dispatchOutputUpdate(int i) {
        int beginBroadcast = this.mOutputCallbacks.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mOutputCallbacks.getBroadcastItem(i2).dispatchSpatializerOutputChanged(i);
            } catch (RemoteException e) {
                Log.e("AS.SpatializerHelper", "Error in dispatchOutputUpdate", e);
            }
        }
        this.mOutputCallbacks.finishBroadcast();
    }

    public final void postInitSensors() {
        this.mAudioService.postInitSpatializerHeadTrackingSensors();
    }

    public synchronized void onInitSensors() {
        int i;
        int i2;
        HelperDynamicSensorCallback helperDynamicSensorCallback;
        boolean z = true;
        boolean z2 = this.mFeatureEnabled && this.mSpatLevel != 0;
        String str = z2 ? "initializing" : "releasing";
        if (this.mSpat == null) {
            logloge("not " + str + " sensors, null spatializer");
        } else if (!this.mIsHeadTrackingSupported) {
            logloge("not " + str + " sensors, spatializer doesn't support headtracking");
        } else {
            if (z2) {
                if (this.mSensorManager == null) {
                    try {
                        this.mSensorManager = (SensorManager) this.mAudioService.mContext.getSystemService("sensor");
                        HelperDynamicSensorCallback helperDynamicSensorCallback2 = new HelperDynamicSensorCallback();
                        this.mDynSensorCallback = helperDynamicSensorCallback2;
                        this.mSensorManager.registerDynamicSensorCallback(helperDynamicSensorCallback2);
                    } catch (Exception e) {
                        Log.e("AS.SpatializerHelper", "Error with SensorManager, can't initialize sensors", e);
                        this.mSensorManager = null;
                        this.mDynSensorCallback = null;
                        return;
                    }
                }
                i = getHeadSensorHandleUpdateTracker();
                loglogi("head tracker sensor handle initialized to " + i);
                i2 = getScreenSensorHandle();
                Log.i("AS.SpatializerHelper", "found screen sensor handle initialized to " + i2);
            } else {
                SensorManager sensorManager = this.mSensorManager;
                if (sensorManager != null && (helperDynamicSensorCallback = this.mDynSensorCallback) != null) {
                    sensorManager.unregisterDynamicSensorCallback(helperDynamicSensorCallback);
                    this.mSensorManager = null;
                    this.mDynSensorCallback = null;
                }
                i = -1;
                i2 = -1;
            }
            try {
                Log.i("AS.SpatializerHelper", "setScreenSensor:" + i2);
                this.mSpat.setScreenSensor(i2);
            } catch (Exception e2) {
                Log.e("AS.SpatializerHelper", "Error calling setScreenSensor:" + i2, e2);
            }
            try {
                Log.i("AS.SpatializerHelper", "setHeadSensor:" + i);
                this.mSpat.setHeadSensor(i);
                if (this.mHeadTrackerAvailable != (i != -1)) {
                    if (i == -1) {
                        z = false;
                    }
                    this.mHeadTrackerAvailable = z;
                    dispatchHeadTrackerAvailable(z);
                }
            } catch (Exception e3) {
                Log.e("AS.SpatializerHelper", "Error calling setHeadSensor:" + i, e3);
            }
            setDesiredHeadTrackingMode(this.mDesiredHeadTrackingMode);
        }
    }

    public static int headTrackingModeTypeToSpatializerInt(byte b) {
        if (b != 0) {
            if (b != 1) {
                if (b != 2) {
                    if (b == 3) {
                        return 2;
                    }
                    throw new IllegalArgumentException("Unexpected head tracking mode:" + ((int) b));
                }
                return 1;
            }
            return -1;
        }
        return 0;
    }

    public static byte spatializerIntToHeadTrackingModeType(int i) {
        if (i != -1) {
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        return (byte) 3;
                    }
                    throw new IllegalArgumentException("Unexpected head tracking mode:" + i);
                }
                return (byte) 2;
            }
            return (byte) 0;
        }
        return (byte) 1;
    }

    public static int spatializationLevelToSpatializerInt(byte b) {
        if (b != 0) {
            if (b != 1) {
                if (b == 2) {
                    return 2;
                }
                throw new IllegalArgumentException("Unexpected spatializer level:" + ((int) b));
            }
            return 1;
        }
        return 0;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("SpatializerHelper:");
        printWriter.println("\tmState:" + this.mState);
        printWriter.println("\tmSpatLevel:" + this.mSpatLevel);
        printWriter.println("\tmCapableSpatLevel:" + this.mCapableSpatLevel);
        printWriter.println("\tmIsHeadTrackingSupported:" + this.mIsHeadTrackingSupported);
        StringBuilder sb = new StringBuilder();
        for (int i : this.mSupportedHeadTrackingModes) {
            sb.append(Spatializer.headtrackingModeToString(i));
            sb.append(" ");
        }
        printWriter.println("\tsupported head tracking modes:" + ((Object) sb));
        printWriter.println("\tmDesiredHeadTrackingMode:" + Spatializer.headtrackingModeToString(this.mDesiredHeadTrackingMode));
        printWriter.println("\tmActualHeadTrackingMode:" + Spatializer.headtrackingModeToString(this.mActualHeadTrackingMode));
        printWriter.println("\theadtracker available:" + this.mHeadTrackerAvailable);
        printWriter.println("\tsupports binaural:" + this.mBinauralSupported + " / transaural:" + this.mTransauralSupported);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("\tmSpatOutput:");
        sb2.append(this.mSpatOutput);
        printWriter.println(sb2.toString());
        printWriter.println("\tdevices:");
        Iterator<SADeviceState> it = this.mSADevices.iterator();
        while (it.hasNext()) {
            printWriter.println("\t\t" + it.next());
        }
    }

    /* loaded from: classes.dex */
    public static final class SADeviceState {
        public static boolean sHeadTrackingEnabledDefault = false;
        public final String mDeviceAddress;
        public final int mDeviceType;
        public boolean mEnabled = true;
        public boolean mHasHeadTracker = false;
        public boolean mHeadTrackerEnabled;

        public SADeviceState(int i, String str) {
            this.mDeviceType = i;
            if (SpatializerHelper.isWireless(i)) {
                Objects.requireNonNull(str);
            } else {
                str = "";
            }
            this.mDeviceAddress = str;
            this.mHeadTrackerEnabled = sHeadTrackingEnabledDefault;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && SADeviceState.class == obj.getClass()) {
                SADeviceState sADeviceState = (SADeviceState) obj;
                return this.mDeviceType == sADeviceState.mDeviceType && this.mDeviceAddress.equals(sADeviceState.mDeviceAddress) && this.mEnabled == sADeviceState.mEnabled && this.mHasHeadTracker == sADeviceState.mHasHeadTracker && this.mHeadTrackerEnabled == sADeviceState.mHeadTrackerEnabled;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mDeviceType), this.mDeviceAddress, Boolean.valueOf(this.mEnabled), Boolean.valueOf(this.mHasHeadTracker), Boolean.valueOf(this.mHeadTrackerEnabled));
        }

        public String toString() {
            return "type: " + this.mDeviceType + " addr: " + this.mDeviceAddress + " enabled: " + this.mEnabled + " HT: " + this.mHasHeadTracker + " HTenabled: " + this.mHeadTrackerEnabled;
        }

        public String toPersistableString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mDeviceType);
            sb.append(",");
            sb.append(this.mDeviceAddress);
            sb.append(",");
            sb.append(this.mEnabled ? "1" : "0");
            sb.append(",");
            sb.append(this.mHasHeadTracker ? "1" : "0");
            sb.append(",");
            sb.append(this.mHeadTrackerEnabled ? "1" : "0");
            return sb.toString();
        }

        public static SADeviceState fromPersistedString(String str) {
            if (str == null || str.isEmpty()) {
                return null;
            }
            String[] split = TextUtils.split(str, ",");
            if (split.length != 5) {
                return null;
            }
            try {
                SADeviceState sADeviceState = new SADeviceState(Integer.parseInt(split[0]), split[1]);
                sADeviceState.mEnabled = Integer.parseInt(split[2]) == 1;
                sADeviceState.mHasHeadTracker = Integer.parseInt(split[3]) == 1;
                sADeviceState.mHeadTrackerEnabled = Integer.parseInt(split[4]) == 1;
                return sADeviceState;
            } catch (NumberFormatException e) {
                Log.e("AS.SpatializerHelper", "unable to parse setting for SADeviceState: " + str, e);
                return null;
            }
        }

        public AudioDeviceAttributes getAudioDeviceAttributes() {
            int i = this.mDeviceType;
            String str = this.mDeviceAddress;
            if (str == null) {
                str = "";
            }
            return new AudioDeviceAttributes(2, i, str);
        }
    }

    public synchronized String getSADeviceSettings() {
        StringBuilder sb;
        sb = new StringBuilder(this.mSADevices.size() * 25);
        for (int i = 0; i < this.mSADevices.size(); i++) {
            sb.append(this.mSADevices.get(i).toPersistableString());
            if (i != this.mSADevices.size() - 1) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    public synchronized void setSADeviceSettings(String str) {
        Objects.requireNonNull(str);
        for (String str2 : TextUtils.split(str, "\\|")) {
            SADeviceState fromPersistedString = SADeviceState.fromPersistedString(str2);
            if (fromPersistedString != null) {
                int i = fromPersistedString.mDeviceType;
                if (i == getCanonicalDeviceType(i) && isDeviceCompatibleWithSpatializationModes(fromPersistedString.getAudioDeviceAttributes())) {
                    this.mSADevices.add(fromPersistedString);
                    logDeviceState(fromPersistedString, "setSADeviceSettings");
                }
            }
        }
    }

    public static boolean isWireless(int i) {
        for (int i2 : WIRELESS_TYPES) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public final int getHeadSensorHandleUpdateTracker() {
        AudioDeviceAttributes audioDeviceAttributes = ROUTING_DEVICES[0];
        if (audioDeviceAttributes == null) {
            return -1;
        }
        UUID deviceSensorUuid = this.mAudioService.getDeviceSensorUuid(audioDeviceAttributes);
        int i = -1;
        for (Sensor sensor : this.mSensorManager.getDynamicSensorList(37)) {
            UUID uuid = sensor.getUuid();
            if (uuid.equals(deviceSensorUuid)) {
                int handle = sensor.getHandle();
                if (setHasHeadTracker(audioDeviceAttributes)) {
                    return handle;
                }
                return -1;
            } else if (uuid.equals(UuidUtils.STANDALONE_UUID)) {
                i = sensor.getHandle();
            }
        }
        return i;
    }

    public final int getScreenSensorHandle() {
        Sensor defaultSensor = this.mSensorManager.getDefaultSensor(11);
        if (defaultSensor != null) {
            return defaultSensor.getHandle();
        }
        return -1;
    }

    public static void loglogi(String str) {
        AudioService.sSpatialLogger.enqueueAndLog(str, 0, "AS.SpatializerHelper");
    }

    public static String logloge(String str) {
        AudioService.sSpatialLogger.enqueueAndLog(str, 1, "AS.SpatializerHelper");
        return str;
    }
}
