package com.android.server.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRoutesInfo;
import android.media.IAudioRoutesObserver;
import android.media.IAudioService;
import android.media.MediaRoute2Info;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.media.DeviceRouteController;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioPoliciesDeviceRouteController implements DeviceRouteController {
    public final AudioManager mAudioManager;
    public final AudioRoutesObserver mAudioRoutesObserver;
    public final IAudioService mAudioService;
    public final Context mContext;
    public MediaRoute2Info mDeviceRoute;
    public int mDeviceVolume;
    public final DeviceRouteController.OnDeviceRouteChangedListener mOnDeviceRouteChangedListener;
    public MediaRoute2Info mSelectedRoute;

    public final boolean isDeviceRouteType(int i) {
        return i == 2 || i == 3 || i == 4 || i == 9 || i == 11 || i == 13;
    }

    @VisibleForTesting
    public AudioPoliciesDeviceRouteController(Context context, AudioManager audioManager, IAudioService iAudioService, DeviceRouteController.OnDeviceRouteChangedListener onDeviceRouteChangedListener) {
        AudioRoutesInfo audioRoutesInfo = null;
        AudioRoutesObserver audioRoutesObserver = new AudioRoutesObserver();
        this.mAudioRoutesObserver = audioRoutesObserver;
        Objects.requireNonNull(context);
        Objects.requireNonNull(audioManager);
        Objects.requireNonNull(iAudioService);
        Objects.requireNonNull(onDeviceRouteChangedListener);
        this.mContext = context;
        this.mOnDeviceRouteChangedListener = onDeviceRouteChangedListener;
        this.mAudioManager = audioManager;
        this.mAudioService = iAudioService;
        try {
            audioRoutesInfo = iAudioService.startWatchingRoutes(audioRoutesObserver);
        } catch (RemoteException e) {
            Slog.w("APDeviceRoutesController", "Cannot connect to audio service to start listen to routes", e);
        }
        this.mDeviceRoute = createRouteFromAudioInfo(audioRoutesInfo);
    }

    @Override // com.android.server.media.DeviceRouteController
    public synchronized boolean selectRoute(Integer num) {
        if (num == null) {
            this.mSelectedRoute = null;
            return true;
        } else if (isDeviceRouteType(num.intValue())) {
            this.mSelectedRoute = createRouteFromAudioInfo(num.intValue());
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.server.media.DeviceRouteController
    public synchronized MediaRoute2Info getDeviceRoute() {
        MediaRoute2Info mediaRoute2Info = this.mSelectedRoute;
        if (mediaRoute2Info != null) {
            return mediaRoute2Info;
        }
        return this.mDeviceRoute;
    }

    @Override // com.android.server.media.DeviceRouteController
    public synchronized boolean updateVolume(int i) {
        if (this.mDeviceVolume == i) {
            return false;
        }
        this.mDeviceVolume = i;
        if (this.mSelectedRoute != null) {
            this.mSelectedRoute = new MediaRoute2Info.Builder(this.mSelectedRoute).setVolume(i).build();
        }
        this.mDeviceRoute = new MediaRoute2Info.Builder(this.mDeviceRoute).setVolume(i).build();
        return true;
    }

    public final MediaRoute2Info createRouteFromAudioInfo(AudioRoutesInfo audioRoutesInfo) {
        int i;
        if (audioRoutesInfo != null) {
            int i2 = audioRoutesInfo.mainType;
            if ((i2 & 2) != 0) {
                i = 4;
            } else if ((i2 & 1) != 0) {
                i = 3;
            } else if ((i2 & 4) != 0) {
                i = 13;
            } else if ((i2 & 8) != 0) {
                i = 9;
            } else if ((i2 & 16) != 0) {
                i = 11;
            }
            return createRouteFromAudioInfo(i);
        }
        i = 2;
        return createRouteFromAudioInfo(i);
    }

    public final MediaRoute2Info createRouteFromAudioInfo(int i) {
        MediaRoute2Info build;
        int i2 = (i == 3 || i == 4) ? 17040110 : i != 9 ? i != 11 ? i != 13 ? 17040107 : 17040108 : 17040111 : 17040109;
        synchronized (this) {
            build = new MediaRoute2Info.Builder("DEVICE_ROUTE", this.mContext.getResources().getText(i2).toString()).setVolumeHandling(this.mAudioManager.isVolumeFixed() ? 0 : 1).setVolume(this.mDeviceVolume).setVolumeMax(this.mAudioManager.getStreamMaxVolume(3)).setType(i).addFeature("android.media.route.feature.LIVE_AUDIO").addFeature("android.media.route.feature.LIVE_VIDEO").addFeature("android.media.route.feature.LOCAL_PLAYBACK").setConnectionState(2).build();
        }
        return build;
    }

    /* loaded from: classes2.dex */
    public class AudioRoutesObserver extends IAudioRoutesObserver.Stub {
        public AudioRoutesObserver() {
        }

        public void dispatchAudioRoutesChanged(AudioRoutesInfo audioRoutesInfo) {
            boolean z;
            MediaRoute2Info createRouteFromAudioInfo = AudioPoliciesDeviceRouteController.this.createRouteFromAudioInfo(audioRoutesInfo);
            synchronized (AudioPoliciesDeviceRouteController.this) {
                AudioPoliciesDeviceRouteController.this.mDeviceRoute = createRouteFromAudioInfo;
                z = AudioPoliciesDeviceRouteController.this.mSelectedRoute == null;
            }
            if (z) {
                AudioPoliciesDeviceRouteController.this.mOnDeviceRouteChangedListener.onDeviceRouteChanged(createRouteFromAudioInfo);
            }
        }
    }
}
