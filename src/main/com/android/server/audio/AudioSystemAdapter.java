package com.android.server.audio;

import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioMixerAttributes;
import android.media.AudioSystem;
import android.media.IDevicesForAttributesCallback;
import android.media.audiopolicy.AudioMix;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes.dex */
public class AudioSystemAdapter implements AudioSystem.RoutingUpdateCallback, AudioSystem.VolumeRangeInitRequestCallback {
    @GuardedBy({"sRoutingListenerLock"})
    public static OnRoutingUpdatedListener sRoutingListener;
    public static AudioSystemAdapter sSingletonDefaultAdapter;
    @GuardedBy({"sVolRangeInitReqListenerLock"})
    public static OnVolRangeInitRequestListener sVolRangeInitReqListener;
    @GuardedBy({"sDeviceCacheLock"})
    public ConcurrentHashMap<Pair<AudioAttributes, Boolean>, ArrayList<AudioDeviceAttributes>> mDevicesForAttrCache;
    public int[] mMethodCacheHit;
    public static final Object sDeviceCacheLock = new Object();
    public static final Object sRoutingListenerLock = new Object();
    public static final Object sVolRangeInitReqListenerLock = new Object();
    public String[] mMethodNames = {"getDevicesForAttributes"};
    public ConcurrentHashMap<Pair<AudioAttributes, Boolean>, ArrayList<AudioDeviceAttributes>> mLastDevicesForAttr = new ConcurrentHashMap<>();
    @GuardedBy({"sDeviceCacheLock"})
    public long mDevicesForAttributesCacheClearTimeMs = System.currentTimeMillis();
    public final ArrayMap<IBinder, List<Pair<AudioAttributes, Boolean>>> mRegisteredAttributesMap = new ArrayMap<>();
    public final RemoteCallbackList<IDevicesForAttributesCallback> mDevicesForAttributesCallbacks = new RemoteCallbackList<>();

    /* loaded from: classes.dex */
    public interface OnRoutingUpdatedListener {
        void onRoutingUpdatedFromNative();
    }

    /* loaded from: classes.dex */
    public interface OnVolRangeInitRequestListener {
        void onVolumeRangeInitRequestFromNative();
    }

    public void onRoutingUpdated() {
        OnRoutingUpdatedListener onRoutingUpdatedListener;
        invalidateRoutingCache();
        synchronized (sRoutingListenerLock) {
            onRoutingUpdatedListener = sRoutingListener;
        }
        if (onRoutingUpdatedListener != null) {
            onRoutingUpdatedListener.onRoutingUpdatedFromNative();
        }
        synchronized (this.mRegisteredAttributesMap) {
            int beginBroadcast = this.mDevicesForAttributesCallbacks.beginBroadcast();
            for (int i = 0; i < beginBroadcast; i++) {
                IDevicesForAttributesCallback broadcastItem = this.mDevicesForAttributesCallbacks.getBroadcastItem(i);
                List<Pair<AudioAttributes, Boolean>> list = this.mRegisteredAttributesMap.get(broadcastItem.asBinder());
                if (list == null) {
                    throw new IllegalStateException("Attribute list must not be null");
                }
                for (Pair<AudioAttributes, Boolean> pair : list) {
                    ArrayList<AudioDeviceAttributes> devicesForAttributes = getDevicesForAttributes((AudioAttributes) pair.first, ((Boolean) pair.second).booleanValue());
                    if (!this.mLastDevicesForAttr.containsKey(pair) || !sameDeviceList(devicesForAttributes, this.mLastDevicesForAttr.get(pair))) {
                        try {
                            broadcastItem.onDevicesForAttributesChanged((AudioAttributes) pair.first, ((Boolean) pair.second).booleanValue(), devicesForAttributes);
                        } catch (RemoteException unused) {
                        }
                    }
                }
            }
            this.mDevicesForAttributesCallbacks.finishBroadcast();
        }
    }

    public static void setRoutingListener(OnRoutingUpdatedListener onRoutingUpdatedListener) {
        synchronized (sRoutingListenerLock) {
            sRoutingListener = onRoutingUpdatedListener;
        }
    }

    public void addOnDevicesForAttributesChangedListener(AudioAttributes audioAttributes, boolean z, IDevicesForAttributesCallback iDevicesForAttributesCallback) {
        Pair<AudioAttributes, Boolean> pair = new Pair<>(audioAttributes, Boolean.valueOf(z));
        synchronized (this.mRegisteredAttributesMap) {
            List<Pair<AudioAttributes, Boolean>> list = this.mRegisteredAttributesMap.get(iDevicesForAttributesCallback.asBinder());
            if (list == null) {
                list = new ArrayList<>();
                this.mRegisteredAttributesMap.put(iDevicesForAttributesCallback.asBinder(), list);
                this.mDevicesForAttributesCallbacks.register(iDevicesForAttributesCallback);
            }
            if (!list.contains(pair)) {
                list.add(pair);
            }
        }
        getDevicesForAttributes(audioAttributes, z);
    }

    public void removeOnDevicesForAttributesChangedListener(IDevicesForAttributesCallback iDevicesForAttributesCallback) {
        synchronized (this.mRegisteredAttributesMap) {
            if (!this.mRegisteredAttributesMap.containsKey(iDevicesForAttributesCallback.asBinder())) {
                Log.w("AudioSystemAdapter", "listener to be removed is not found.");
                return;
            }
            this.mRegisteredAttributesMap.remove(iDevicesForAttributesCallback.asBinder());
            this.mDevicesForAttributesCallbacks.unregister(iDevicesForAttributesCallback);
        }
    }

    public final boolean sameDeviceList(List<AudioDeviceAttributes> list, List<AudioDeviceAttributes> list2) {
        for (AudioDeviceAttributes audioDeviceAttributes : list) {
            if (!list2.contains(audioDeviceAttributes)) {
                return false;
            }
        }
        for (AudioDeviceAttributes audioDeviceAttributes2 : list2) {
            if (!list.contains(audioDeviceAttributes2)) {
                return false;
            }
        }
        return true;
    }

    public void onVolumeRangeInitializationRequested() {
        OnVolRangeInitRequestListener onVolRangeInitRequestListener;
        synchronized (sVolRangeInitReqListenerLock) {
            onVolRangeInitRequestListener = sVolRangeInitReqListener;
        }
        if (onVolRangeInitRequestListener != null) {
            onVolRangeInitRequestListener.onVolumeRangeInitRequestFromNative();
        }
    }

    public static void setVolRangeInitReqListener(OnVolRangeInitRequestListener onVolRangeInitRequestListener) {
        synchronized (sVolRangeInitReqListenerLock) {
            sVolRangeInitReqListener = onVolRangeInitRequestListener;
        }
    }

    public static final synchronized AudioSystemAdapter getDefaultAdapter() {
        AudioSystemAdapter audioSystemAdapter;
        synchronized (AudioSystemAdapter.class) {
            if (sSingletonDefaultAdapter == null) {
                AudioSystemAdapter audioSystemAdapter2 = new AudioSystemAdapter();
                sSingletonDefaultAdapter = audioSystemAdapter2;
                AudioSystem.setRoutingCallback(audioSystemAdapter2);
                AudioSystem.setVolumeRangeInitRequestCallback(sSingletonDefaultAdapter);
                synchronized (sDeviceCacheLock) {
                    sSingletonDefaultAdapter.mDevicesForAttrCache = new ConcurrentHashMap<>(AudioSystem.getNumStreamTypes());
                    sSingletonDefaultAdapter.mMethodCacheHit = new int[1];
                }
            }
            audioSystemAdapter = sSingletonDefaultAdapter;
        }
        return audioSystemAdapter;
    }

    public final void invalidateRoutingCache() {
        synchronized (sDeviceCacheLock) {
            if (this.mDevicesForAttrCache != null) {
                this.mDevicesForAttributesCacheClearTimeMs = System.currentTimeMillis();
                this.mLastDevicesForAttr.putAll(this.mDevicesForAttrCache);
                this.mDevicesForAttrCache.clear();
            }
        }
    }

    public ArrayList<AudioDeviceAttributes> getDevicesForAttributes(AudioAttributes audioAttributes, boolean z) {
        return getDevicesForAttributesImpl(audioAttributes, z);
    }

    public final ArrayList<AudioDeviceAttributes> getDevicesForAttributesImpl(AudioAttributes audioAttributes, boolean z) {
        Pair<AudioAttributes, Boolean> pair = new Pair<>(audioAttributes, Boolean.valueOf(z));
        synchronized (sDeviceCacheLock) {
            ArrayList<AudioDeviceAttributes> arrayList = this.mDevicesForAttrCache.get(pair);
            if (arrayList == null) {
                ArrayList<AudioDeviceAttributes> devicesForAttributes = AudioSystem.getDevicesForAttributes(audioAttributes, z);
                this.mDevicesForAttrCache.put(pair, devicesForAttributes);
                return devicesForAttributes;
            }
            int[] iArr = this.mMethodCacheHit;
            iArr[0] = iArr[0] + 1;
            return arrayList;
        }
    }

    public int setDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i, int i2) {
        invalidateRoutingCache();
        return AudioSystem.setDeviceConnectionState(audioDeviceAttributes, i, i2);
    }

    public int handleDeviceConfigChange(int i, String str, String str2, int i2) {
        invalidateRoutingCache();
        return AudioSystem.handleDeviceConfigChange(i, str, str2, i2);
    }

    public int setDevicesRoleForStrategy(int i, int i2, List<AudioDeviceAttributes> list) {
        invalidateRoutingCache();
        return AudioSystem.setDevicesRoleForStrategy(i, i2, list);
    }

    public int removeDevicesRoleForStrategy(int i, int i2, List<AudioDeviceAttributes> list) {
        invalidateRoutingCache();
        return AudioSystem.removeDevicesRoleForStrategy(i, i2, list);
    }

    public int clearDevicesRoleForStrategy(int i, int i2) {
        invalidateRoutingCache();
        return AudioSystem.clearDevicesRoleForStrategy(i, i2);
    }

    public int setDevicesRoleForCapturePreset(int i, int i2, List<AudioDeviceAttributes> list) {
        invalidateRoutingCache();
        return AudioSystem.setDevicesRoleForCapturePreset(i, i2, list);
    }

    public int clearDevicesRoleForCapturePreset(int i, int i2) {
        invalidateRoutingCache();
        return AudioSystem.clearDevicesRoleForCapturePreset(i, i2);
    }

    public int setParameters(String str) {
        return AudioSystem.setParameters(str);
    }

    public boolean isMicrophoneMuted() {
        return AudioSystem.isMicrophoneMuted();
    }

    public int muteMicrophone(boolean z) {
        return AudioSystem.muteMicrophone(z);
    }

    public int setCurrentImeUid(int i) {
        return AudioSystem.setCurrentImeUid(i);
    }

    public boolean isStreamActive(int i, int i2) {
        return AudioSystem.isStreamActive(i, i2);
    }

    public boolean isStreamActiveRemotely(int i, int i2) {
        return AudioSystem.isStreamActiveRemotely(i, i2);
    }

    public int setStreamVolumeIndexAS(int i, int i2, int i3) {
        return AudioSystem.setStreamVolumeIndexAS(i, i2, i3);
    }

    public int setPhoneState(int i, int i2) {
        invalidateRoutingCache();
        return AudioSystem.setPhoneState(i, i2);
    }

    public int setAllowedCapturePolicy(int i, int i2) {
        return AudioSystem.setAllowedCapturePolicy(i, i2);
    }

    public int setForceUse(int i, int i2) {
        invalidateRoutingCache();
        return AudioSystem.setForceUse(i, i2);
    }

    public int registerPolicyMixes(ArrayList<AudioMix> arrayList, boolean z) {
        invalidateRoutingCache();
        return AudioSystem.registerPolicyMixes(arrayList, z);
    }

    public int setUidDeviceAffinities(int i, int[] iArr, String[] strArr) {
        invalidateRoutingCache();
        return AudioSystem.setUidDeviceAffinities(i, iArr, strArr);
    }

    public int removeUidDeviceAffinities(int i) {
        invalidateRoutingCache();
        return AudioSystem.removeUidDeviceAffinities(i);
    }

    public int setUserIdDeviceAffinities(int i, int[] iArr, String[] strArr) {
        invalidateRoutingCache();
        return AudioSystem.setUserIdDeviceAffinities(i, iArr, strArr);
    }

    public int removeUserIdDeviceAffinities(int i) {
        invalidateRoutingCache();
        return AudioSystem.removeUserIdDeviceAffinities(i);
    }

    public int setPreferredMixerAttributes(AudioAttributes audioAttributes, int i, int i2, AudioMixerAttributes audioMixerAttributes) {
        return AudioSystem.setPreferredMixerAttributes(audioAttributes, i, i2, audioMixerAttributes);
    }

    public int clearPreferredMixerAttributes(AudioAttributes audioAttributes, int i, int i2) {
        return AudioSystem.clearPreferredMixerAttributes(audioAttributes, i, i2);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("\nAudioSystemAdapter:");
        DateTimeFormatter withZone = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss:SSS").withLocale(Locale.US).withZone(ZoneId.systemDefault());
        synchronized (sDeviceCacheLock) {
            printWriter.println(" last cache clear time: " + withZone.format(Instant.ofEpochMilli(this.mDevicesForAttributesCacheClearTimeMs)));
            printWriter.println(" mDevicesForAttrCache:");
            ConcurrentHashMap<Pair<AudioAttributes, Boolean>, ArrayList<AudioDeviceAttributes>> concurrentHashMap = this.mDevicesForAttrCache;
            if (concurrentHashMap != null) {
                for (Map.Entry<Pair<AudioAttributes, Boolean>, ArrayList<AudioDeviceAttributes>> entry : concurrentHashMap.entrySet()) {
                    AudioAttributes audioAttributes = (AudioAttributes) entry.getKey().first;
                    try {
                        int volumeControlStream = audioAttributes.getVolumeControlStream();
                        printWriter.println("\t" + audioAttributes + " forVolume: " + entry.getKey().second + " stream: " + AudioSystem.STREAM_NAMES[volumeControlStream] + "(" + volumeControlStream + ")");
                        Iterator<AudioDeviceAttributes> it = entry.getValue().iterator();
                        while (it.hasNext()) {
                            printWriter.println("\t\t" + it.next());
                        }
                    } catch (IllegalArgumentException e) {
                        printWriter.println("\t dump failed for attributes: " + audioAttributes);
                        Log.e("AudioSystemAdapter", "dump failed", e);
                    }
                }
            }
        }
    }
}
