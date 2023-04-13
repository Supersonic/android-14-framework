package com.android.server.media;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.media.MediaRoute2Info;
import android.os.UserHandle;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface BluetoothRouteController {

    /* loaded from: classes2.dex */
    public interface BluetoothRoutesUpdatedListener {
        void onBluetoothRoutesUpdated(List<MediaRoute2Info> list);
    }

    List<MediaRoute2Info> getAllBluetoothRoutes();

    MediaRoute2Info getSelectedRoute();

    List<MediaRoute2Info> getTransferableRoutes();

    boolean selectRoute(String str);

    void start(UserHandle userHandle);

    void stop();

    void transferTo(String str);

    boolean updateVolumeForDevices(int i, int i2);

    static BluetoothRouteController createInstance(Context context, BluetoothRoutesUpdatedListener bluetoothRoutesUpdatedListener) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(bluetoothRoutesUpdatedListener);
        BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService("bluetooth")).getAdapter();
        if (adapter == null) {
            return new NoOpBluetoothRouteController();
        }
        if (MediaFeatureFlagManager.getInstance().getBoolean("BluetoothRouteController__enable_legacy_bluetooth_routes_controller", true)) {
            return new LegacyBluetoothRouteController(context, adapter, bluetoothRoutesUpdatedListener);
        }
        return new AudioPoliciesBluetoothRouteController(context, adapter, bluetoothRoutesUpdatedListener);
    }

    /* loaded from: classes2.dex */
    public static class NoOpBluetoothRouteController implements BluetoothRouteController {
        @Override // com.android.server.media.BluetoothRouteController
        public MediaRoute2Info getSelectedRoute() {
            return null;
        }

        @Override // com.android.server.media.BluetoothRouteController
        public boolean selectRoute(String str) {
            return false;
        }

        @Override // com.android.server.media.BluetoothRouteController
        public void start(UserHandle userHandle) {
        }

        @Override // com.android.server.media.BluetoothRouteController
        public void stop() {
        }

        @Override // com.android.server.media.BluetoothRouteController
        public void transferTo(String str) {
        }

        @Override // com.android.server.media.BluetoothRouteController
        public boolean updateVolumeForDevices(int i, int i2) {
            return false;
        }

        @Override // com.android.server.media.BluetoothRouteController
        public List<MediaRoute2Info> getTransferableRoutes() {
            return Collections.emptyList();
        }

        @Override // com.android.server.media.BluetoothRouteController
        public List<MediaRoute2Info> getAllBluetoothRoutes() {
            return Collections.emptyList();
        }
    }
}
