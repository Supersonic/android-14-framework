package android.net.wifi.sharedconnectivity.app;

import android.annotation.SystemApi;
import java.util.List;
@SystemApi
/* loaded from: classes2.dex */
public interface SharedConnectivityClientCallback {
    void onHotspotNetworkConnectionStatusChanged(HotspotNetworkConnectionStatus hotspotNetworkConnectionStatus);

    void onHotspotNetworksUpdated(List<HotspotNetwork> list);

    void onKnownNetworkConnectionStatusChanged(KnownNetworkConnectionStatus knownNetworkConnectionStatus);

    void onKnownNetworksUpdated(List<KnownNetwork> list);

    void onRegisterCallbackFailed(Exception exc);

    void onServiceConnected();

    void onServiceDisconnected();

    void onSharedConnectivitySettingsChanged(SharedConnectivitySettingsState sharedConnectivitySettingsState);
}
