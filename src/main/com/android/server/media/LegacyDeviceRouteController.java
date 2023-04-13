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
public final class LegacyDeviceRouteController implements DeviceRouteController {
    public final AudioManager mAudioManager;
    public final AudioRoutesObserver mAudioRoutesObserver;
    public final IAudioService mAudioService;
    public final Context mContext;
    public MediaRoute2Info mDeviceRoute;
    public int mDeviceVolume;
    public final DeviceRouteController.OnDeviceRouteChangedListener mOnDeviceRouteChangedListener;

    @Override // com.android.server.media.DeviceRouteController
    public boolean selectRoute(Integer num) {
        return false;
    }

    @VisibleForTesting
    public LegacyDeviceRouteController(Context context, AudioManager audioManager, IAudioService iAudioService, DeviceRouteController.OnDeviceRouteChangedListener onDeviceRouteChangedListener) {
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
            Slog.w("LDeviceRouteController", "Cannot connect to audio service to start listen to routes", e);
        }
        this.mDeviceRoute = createRouteFromAudioInfo(audioRoutesInfo);
    }

    @Override // com.android.server.media.DeviceRouteController
    public synchronized MediaRoute2Info getDeviceRoute() {
        return this.mDeviceRoute;
    }

    @Override // com.android.server.media.DeviceRouteController
    public synchronized boolean updateVolume(int i) {
        if (this.mDeviceVolume == i) {
            return false;
        }
        this.mDeviceVolume = i;
        this.mDeviceRoute = new MediaRoute2Info.Builder(this.mDeviceRoute).setVolume(i).build();
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0038 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final MediaRoute2Info createRouteFromAudioInfo(AudioRoutesInfo audioRoutesInfo) {
        int i;
        int i2;
        MediaRoute2Info build;
        if (audioRoutesInfo != null) {
            int i3 = audioRoutesInfo.mainType;
            i = 17040110;
            if ((i3 & 2) != 0) {
                i2 = 4;
            } else if ((i3 & 1) != 0) {
                i2 = 3;
            } else if ((i3 & 4) != 0) {
                i2 = 13;
                i = 17040108;
            } else if ((i3 & 8) != 0) {
                i2 = 9;
                i = 17040109;
            } else if ((i3 & 16) != 0) {
                i2 = 11;
                i = 17040111;
            }
            synchronized (this) {
                build = new MediaRoute2Info.Builder("DEVICE_ROUTE", this.mContext.getResources().getText(i).toString()).setVolumeHandling(this.mAudioManager.isVolumeFixed() ? 0 : 1).setVolume(this.mDeviceVolume).setVolumeMax(this.mAudioManager.getStreamMaxVolume(3)).setType(i2).addFeature("android.media.route.feature.LIVE_AUDIO").addFeature("android.media.route.feature.LIVE_VIDEO").addFeature("android.media.route.feature.LOCAL_PLAYBACK").setConnectionState(2).build();
            }
            return build;
        }
        i = 17040107;
        i2 = 2;
        synchronized (this) {
        }
    }

    public final void notifyDeviceRouteUpdate(MediaRoute2Info mediaRoute2Info) {
        this.mOnDeviceRouteChangedListener.onDeviceRouteChanged(mediaRoute2Info);
    }

    /* loaded from: classes2.dex */
    public class AudioRoutesObserver extends IAudioRoutesObserver.Stub {
        public AudioRoutesObserver() {
        }

        public void dispatchAudioRoutesChanged(AudioRoutesInfo audioRoutesInfo) {
            MediaRoute2Info createRouteFromAudioInfo = LegacyDeviceRouteController.this.createRouteFromAudioInfo(audioRoutesInfo);
            synchronized (LegacyDeviceRouteController.this) {
                LegacyDeviceRouteController.this.mDeviceRoute = createRouteFromAudioInfo;
            }
            LegacyDeviceRouteController.this.notifyDeviceRouteUpdate(createRouteFromAudioInfo);
        }
    }
}
