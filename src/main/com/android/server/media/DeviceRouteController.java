package com.android.server.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.IAudioService;
import android.media.MediaRoute2Info;
import android.os.ServiceManager;
/* loaded from: classes2.dex */
public interface DeviceRouteController {

    /* loaded from: classes2.dex */
    public interface OnDeviceRouteChangedListener {
        void onDeviceRouteChanged(MediaRoute2Info mediaRoute2Info);
    }

    MediaRoute2Info getDeviceRoute();

    boolean selectRoute(Integer num);

    boolean updateVolume(int i);

    static DeviceRouteController createInstance(Context context, OnDeviceRouteChangedListener onDeviceRouteChangedListener) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AudioManager.class);
        IAudioService asInterface = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
        if (MediaFeatureFlagManager.getInstance().getBoolean("BluetoothRouteController__enable_legacy_bluetooth_routes_controller", true)) {
            return new LegacyDeviceRouteController(context, audioManager, asInterface, onDeviceRouteChangedListener);
        }
        return new AudioPoliciesDeviceRouteController(context, audioManager, asInterface, onDeviceRouteChangedListener);
    }
}
