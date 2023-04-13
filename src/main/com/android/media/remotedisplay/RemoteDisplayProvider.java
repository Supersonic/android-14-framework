package com.android.media.remotedisplay;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.IRemoteDisplayCallback;
import android.media.IRemoteDisplayProvider;
import android.media.RemoteDisplayState;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;
import java.util.Collection;
@SystemApi
/* loaded from: classes.dex */
public abstract class RemoteDisplayProvider {
    public static final int DISCOVERY_MODE_ACTIVE = 2;
    public static final int DISCOVERY_MODE_NONE = 0;
    public static final int DISCOVERY_MODE_PASSIVE = 1;
    private static final int MSG_ADJUST_VOLUME = 6;
    private static final int MSG_CONNECT = 3;
    private static final int MSG_DISCONNECT = 4;
    private static final int MSG_SET_CALLBACK = 1;
    private static final int MSG_SET_DISCOVERY_MODE = 2;
    private static final int MSG_SET_VOLUME = 5;
    public static final String SERVICE_INTERFACE = "com.android.media.remotedisplay.RemoteDisplayProvider";
    private IRemoteDisplayCallback mCallback;
    private final Context mContext;
    private final ProviderHandler mHandler;
    private PendingIntent mSettingsPendingIntent;
    private final ArrayMap<String, RemoteDisplay> mDisplays = new ArrayMap<>();
    private int mDiscoveryMode = 0;
    private final ProviderStub mStub = new ProviderStub();

    public RemoteDisplayProvider(Context context) {
        this.mContext = context;
        this.mHandler = new ProviderHandler(context.getMainLooper());
    }

    public final Context getContext() {
        return this.mContext;
    }

    public IBinder getBinder() {
        return this.mStub;
    }

    public void onDiscoveryModeChanged(int mode) {
    }

    public void onConnect(RemoteDisplay display) {
    }

    public void onDisconnect(RemoteDisplay display) {
    }

    public void onSetVolume(RemoteDisplay display, int volume) {
    }

    public void onAdjustVolume(RemoteDisplay display, int delta) {
    }

    public int getDiscoveryMode() {
        return this.mDiscoveryMode;
    }

    public Collection<RemoteDisplay> getDisplays() {
        return this.mDisplays.values();
    }

    public void addDisplay(RemoteDisplay display) {
        if (display == null) {
            throw new IllegalArgumentException("display cannot be null");
        }
        String displayId = display.getId();
        if (this.mDisplays.containsKey(displayId)) {
            throw new IllegalArgumentException("display already exists with id: " + displayId);
        }
        this.mDisplays.put(displayId, display);
        publishState();
    }

    public void updateDisplay(RemoteDisplay display) {
        if (display == null) {
            throw new IllegalArgumentException("display cannot be null");
        }
        String displayId = display.getId();
        if (this.mDisplays.get(displayId) != display) {
            throw new IllegalArgumentException("unexpected display with id: " + displayId);
        }
        publishState();
    }

    public void removeDisplay(RemoteDisplay display) {
        if (display == null) {
            throw new IllegalArgumentException("display cannot be null");
        }
        String displayId = display.getId();
        if (this.mDisplays.get(displayId) != display) {
            throw new IllegalArgumentException("unexpected display with id: " + displayId);
        }
        this.mDisplays.remove(displayId);
        publishState();
    }

    public RemoteDisplay findRemoteDisplay(String id) {
        return this.mDisplays.get(id);
    }

    public PendingIntent getSettingsPendingIntent() {
        if (this.mSettingsPendingIntent == null) {
            Intent settingsIntent = new Intent("android.settings.CAST_SETTINGS");
            settingsIntent.setFlags(337641472);
            this.mSettingsPendingIntent = PendingIntent.getActivity(this.mContext, 0, settingsIntent, 67108864, null);
        }
        return this.mSettingsPendingIntent;
    }

    void setCallback(IRemoteDisplayCallback callback) {
        this.mCallback = callback;
        publishState();
    }

    void setDiscoveryMode(int mode) {
        if (this.mDiscoveryMode != mode) {
            this.mDiscoveryMode = mode;
            onDiscoveryModeChanged(mode);
        }
    }

    void publishState() {
        if (this.mCallback != null) {
            RemoteDisplayState state = new RemoteDisplayState();
            int count = this.mDisplays.size();
            for (int i = 0; i < count; i++) {
                RemoteDisplay display = this.mDisplays.valueAt(i);
                state.displays.add(display.getInfo());
            }
            try {
                this.mCallback.onStateChanged(state);
            } catch (RemoteException e) {
            }
        }
    }

    /* loaded from: classes.dex */
    final class ProviderStub extends IRemoteDisplayProvider.Stub {
        ProviderStub() {
        }

        public void setCallback(IRemoteDisplayCallback callback) {
            RemoteDisplayProvider.this.mHandler.obtainMessage(1, callback).sendToTarget();
        }

        public void setDiscoveryMode(int mode) {
            RemoteDisplayProvider.this.mHandler.obtainMessage(2, mode, 0).sendToTarget();
        }

        public void connect(String id) {
            RemoteDisplayProvider.this.mHandler.obtainMessage(3, id).sendToTarget();
        }

        public void disconnect(String id) {
            RemoteDisplayProvider.this.mHandler.obtainMessage(4, id).sendToTarget();
        }

        public void setVolume(String id, int volume) {
            RemoteDisplayProvider.this.mHandler.obtainMessage(RemoteDisplayProvider.MSG_SET_VOLUME, volume, 0, id).sendToTarget();
        }

        public void adjustVolume(String id, int delta) {
            RemoteDisplayProvider.this.mHandler.obtainMessage(RemoteDisplayProvider.MSG_ADJUST_VOLUME, delta, 0, id).sendToTarget();
        }
    }

    /* loaded from: classes.dex */
    final class ProviderHandler extends Handler {
        public ProviderHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    RemoteDisplayProvider.this.setCallback((IRemoteDisplayCallback) msg.obj);
                    return;
                case 2:
                    RemoteDisplayProvider.this.setDiscoveryMode(msg.arg1);
                    return;
                case 3:
                    RemoteDisplay display = RemoteDisplayProvider.this.findRemoteDisplay((String) msg.obj);
                    if (display != null) {
                        RemoteDisplayProvider.this.onConnect(display);
                        return;
                    }
                    return;
                case 4:
                    RemoteDisplay display2 = RemoteDisplayProvider.this.findRemoteDisplay((String) msg.obj);
                    if (display2 != null) {
                        RemoteDisplayProvider.this.onDisconnect(display2);
                        return;
                    }
                    return;
                case RemoteDisplayProvider.MSG_SET_VOLUME /* 5 */:
                    RemoteDisplay display3 = RemoteDisplayProvider.this.findRemoteDisplay((String) msg.obj);
                    if (display3 != null) {
                        RemoteDisplayProvider.this.onSetVolume(display3, msg.arg1);
                        return;
                    }
                    return;
                case RemoteDisplayProvider.MSG_ADJUST_VOLUME /* 6 */:
                    RemoteDisplay display4 = RemoteDisplayProvider.this.findRemoteDisplay((String) msg.obj);
                    if (display4 != null) {
                        RemoteDisplayProvider.this.onAdjustVolume(display4, msg.arg1);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
