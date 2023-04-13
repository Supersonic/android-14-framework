package com.android.server.companion.virtual.audio;

import android.annotation.RequiresPermission;
import android.companion.virtual.audio.IAudioConfigChangedCallback;
import android.companion.virtual.audio.IAudioRoutingCallback;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecordingConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.companion.virtual.GenericWindowPolicyController;
import com.android.server.companion.virtual.audio.AudioPlaybackDetector;
import com.android.server.companion.virtual.audio.AudioRecordingDetector;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class VirtualAudioController implements AudioPlaybackDetector.AudioPlaybackCallback, AudioRecordingDetector.AudioRecordingCallback, GenericWindowPolicyController.RunningAppsChangedListener {
    public final AudioPlaybackDetector mAudioPlaybackDetector;
    public final AudioRecordingDetector mAudioRecordingDetector;
    @GuardedBy({"mCallbackLock"})
    public IAudioConfigChangedCallback mConfigChangedCallback;
    public final Context mContext;
    public GenericWindowPolicyController mGenericWindowPolicyController;
    @GuardedBy({"mCallbackLock"})
    public IAudioRoutingCallback mRoutingCallback;
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    public final Runnable mUpdateAudioRoutingRunnable = new Runnable() { // from class: com.android.server.companion.virtual.audio.VirtualAudioController$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            VirtualAudioController.this.notifyAppsNeedingAudioRoutingChanged();
        }
    };
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArraySet<Integer> mRunningAppUids = new ArraySet<>();
    @GuardedBy({"mLock"})
    public ArraySet<Integer> mPlayingAppUids = new ArraySet<>();
    public final Object mCallbackLock = new Object();

    public VirtualAudioController(Context context) {
        this.mContext = context;
        this.mAudioPlaybackDetector = new AudioPlaybackDetector(context);
        this.mAudioRecordingDetector = new AudioRecordingDetector(context);
    }

    public void startListening(GenericWindowPolicyController genericWindowPolicyController, IAudioRoutingCallback iAudioRoutingCallback, IAudioConfigChangedCallback iAudioConfigChangedCallback) {
        this.mGenericWindowPolicyController = genericWindowPolicyController;
        genericWindowPolicyController.registerRunningAppsChangedListener(this);
        synchronized (this.mCallbackLock) {
            this.mRoutingCallback = iAudioRoutingCallback;
            this.mConfigChangedCallback = iAudioConfigChangedCallback;
        }
        synchronized (this.mLock) {
            this.mRunningAppUids.clear();
            this.mPlayingAppUids.clear();
        }
        if (iAudioConfigChangedCallback != null) {
            this.mAudioPlaybackDetector.register(this);
            this.mAudioRecordingDetector.register(this);
        }
    }

    public void stopListening() {
        if (this.mHandler.hasCallbacks(this.mUpdateAudioRoutingRunnable)) {
            this.mHandler.removeCallbacks(this.mUpdateAudioRoutingRunnable);
        }
        this.mAudioPlaybackDetector.unregister();
        this.mAudioRecordingDetector.unregister();
        GenericWindowPolicyController genericWindowPolicyController = this.mGenericWindowPolicyController;
        if (genericWindowPolicyController != null) {
            genericWindowPolicyController.unregisterRunningAppsChangedListener(this);
            this.mGenericWindowPolicyController = null;
        }
        synchronized (this.mCallbackLock) {
            this.mRoutingCallback = null;
            this.mConfigChangedCallback = null;
        }
    }

    @Override // com.android.server.companion.virtual.GenericWindowPolicyController.RunningAppsChangedListener
    public void onRunningAppsChanged(ArraySet<Integer> arraySet) {
        synchronized (this.mLock) {
            if (this.mRunningAppUids.equals(arraySet)) {
                return;
            }
            this.mRunningAppUids.clear();
            this.mRunningAppUids.addAll((ArraySet<? extends Integer>) arraySet);
            ArraySet<Integer> arraySet2 = this.mPlayingAppUids;
            ArraySet<Integer> findPlayingAppUids = findPlayingAppUids(((AudioManager) this.mContext.getSystemService(AudioManager.class)).getActivePlaybackConfigurations(), this.mRunningAppUids);
            this.mPlayingAppUids = findPlayingAppUids;
            if (!findPlayingAppUids.isEmpty()) {
                Slog.i("VirtualAudioController", "Audio is playing, do not change rerouted apps");
            } else if (!arraySet2.isEmpty()) {
                Slog.i("VirtualAudioController", "The last playing app removed, delay change rerouted apps");
                if (this.mHandler.hasCallbacks(this.mUpdateAudioRoutingRunnable)) {
                    this.mHandler.removeCallbacks(this.mUpdateAudioRoutingRunnable);
                }
                this.mHandler.postDelayed(this.mUpdateAudioRoutingRunnable, 2000L);
            } else {
                notifyAppsNeedingAudioRoutingChanged();
            }
        }
    }

    @Override // com.android.server.companion.virtual.audio.AudioPlaybackDetector.AudioPlaybackCallback
    public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> list) {
        List<AudioPlaybackConfiguration> findPlaybackConfigurations;
        updatePlayingApplications(list);
        synchronized (this.mLock) {
            findPlaybackConfigurations = findPlaybackConfigurations(list, this.mRunningAppUids);
        }
        synchronized (this.mCallbackLock) {
            IAudioConfigChangedCallback iAudioConfigChangedCallback = this.mConfigChangedCallback;
            if (iAudioConfigChangedCallback != null) {
                try {
                    iAudioConfigChangedCallback.onPlaybackConfigChanged(findPlaybackConfigurations);
                } catch (RemoteException e) {
                    Slog.e("VirtualAudioController", "RemoteException when calling onPlaybackConfigChanged", e);
                }
            }
        }
    }

    @Override // com.android.server.companion.virtual.audio.AudioRecordingDetector.AudioRecordingCallback
    @RequiresPermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void onRecordingConfigChanged(List<AudioRecordingConfiguration> list) {
        List<AudioRecordingConfiguration> findRecordingConfigurations;
        synchronized (this.mLock) {
            findRecordingConfigurations = findRecordingConfigurations(list, this.mRunningAppUids);
        }
        synchronized (this.mCallbackLock) {
            IAudioConfigChangedCallback iAudioConfigChangedCallback = this.mConfigChangedCallback;
            if (iAudioConfigChangedCallback != null) {
                try {
                    iAudioConfigChangedCallback.onRecordingConfigChanged(findRecordingConfigurations);
                } catch (RemoteException e) {
                    Slog.e("VirtualAudioController", "RemoteException when calling onRecordingConfigChanged", e);
                }
            }
        }
    }

    public final void updatePlayingApplications(List<AudioPlaybackConfiguration> list) {
        synchronized (this.mLock) {
            ArraySet<Integer> findPlayingAppUids = findPlayingAppUids(list, this.mRunningAppUids);
            if (this.mPlayingAppUids.equals(findPlayingAppUids)) {
                return;
            }
            this.mPlayingAppUids = findPlayingAppUids;
            notifyAppsNeedingAudioRoutingChanged();
        }
    }

    public final void notifyAppsNeedingAudioRoutingChanged() {
        int[] iArr;
        if (this.mHandler.hasCallbacks(this.mUpdateAudioRoutingRunnable)) {
            this.mHandler.removeCallbacks(this.mUpdateAudioRoutingRunnable);
        }
        synchronized (this.mLock) {
            iArr = new int[this.mRunningAppUids.size()];
            for (int i = 0; i < this.mRunningAppUids.size(); i++) {
                iArr[i] = this.mRunningAppUids.valueAt(i).intValue();
            }
        }
        synchronized (this.mCallbackLock) {
            IAudioRoutingCallback iAudioRoutingCallback = this.mRoutingCallback;
            if (iAudioRoutingCallback != null) {
                try {
                    iAudioRoutingCallback.onAppsNeedingAudioRoutingChanged(iArr);
                } catch (RemoteException e) {
                    Slog.e("VirtualAudioController", "RemoteException when calling updateReroutingApps", e);
                }
            }
        }
    }

    public static ArraySet<Integer> findPlayingAppUids(List<AudioPlaybackConfiguration> list, ArraySet<Integer> arraySet) {
        ArraySet<Integer> arraySet2 = new ArraySet<>();
        for (AudioPlaybackConfiguration audioPlaybackConfiguration : list) {
            if (arraySet.contains(Integer.valueOf(audioPlaybackConfiguration.getClientUid())) && audioPlaybackConfiguration.getPlayerState() == 2) {
                arraySet2.add(Integer.valueOf(audioPlaybackConfiguration.getClientUid()));
            }
        }
        return arraySet2;
    }

    public static List<AudioPlaybackConfiguration> findPlaybackConfigurations(List<AudioPlaybackConfiguration> list, ArraySet<Integer> arraySet) {
        ArrayList arrayList = new ArrayList();
        for (AudioPlaybackConfiguration audioPlaybackConfiguration : list) {
            if (arraySet.contains(Integer.valueOf(audioPlaybackConfiguration.getClientUid()))) {
                arrayList.add(audioPlaybackConfiguration);
            }
        }
        return arrayList;
    }

    @RequiresPermission("android.permission.MODIFY_AUDIO_ROUTING")
    public static List<AudioRecordingConfiguration> findRecordingConfigurations(List<AudioRecordingConfiguration> list, ArraySet<Integer> arraySet) {
        ArrayList arrayList = new ArrayList();
        for (AudioRecordingConfiguration audioRecordingConfiguration : list) {
            if (arraySet.contains(Integer.valueOf(audioRecordingConfiguration.getClientUid()))) {
                arrayList.add(audioRecordingConfiguration);
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    public boolean hasPendingRunnable() {
        return this.mHandler.hasCallbacks(this.mUpdateAudioRoutingRunnable);
    }

    @VisibleForTesting
    public void addPlayingAppsForTesting(int i) {
        synchronized (this.mLock) {
            this.mPlayingAppUids.add(Integer.valueOf(i));
        }
    }
}
