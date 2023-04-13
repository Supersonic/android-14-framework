package android.media;

import android.Manifest;
import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioAttributes;
import android.media.IAudioRoutesObserver;
import android.media.IAudioService;
import android.media.IMediaRouterClient;
import android.media.IMediaRouterService;
import android.media.IRemoteVolumeObserver;
import android.media.MediaRouter;
import android.media.MediaRouterClientState;
import android.media.session.MediaSession;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.DisplayAddress;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes2.dex */
public class MediaRouter {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE = 1;
    public static final int CALLBACK_FLAG_PASSIVE_DISCOVERY = 8;
    public static final int CALLBACK_FLAG_PERFORM_ACTIVE_SCAN = 1;
    public static final int CALLBACK_FLAG_REQUEST_DISCOVERY = 4;
    public static final int CALLBACK_FLAG_UNFILTERED_EVENTS = 2;
    private static final boolean DEBUG_RESTORE_ROUTE = true;
    public static final String MIRRORING_GROUP_ID = "android.media.mirroring_group";
    static final int ROUTE_TYPE_ANY = 8388615;
    public static final int ROUTE_TYPE_LIVE_AUDIO = 1;
    public static final int ROUTE_TYPE_LIVE_VIDEO = 2;
    public static final int ROUTE_TYPE_REMOTE_DISPLAY = 4;
    public static final int ROUTE_TYPE_USER = 8388608;
    static Static sStatic;
    private static final String TAG = "MediaRouter";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final HashMap<Context, MediaRouter> sRouters = new HashMap<>();

    /* loaded from: classes2.dex */
    public static abstract class VolumeCallback {
        public abstract void onVolumeSetRequest(RouteInfo routeInfo, int i);

        public abstract void onVolumeUpdateRequest(RouteInfo routeInfo, int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class Static implements DisplayManager.DisplayListener {
        boolean mActivelyScanningWifiDisplays;
        final IAudioService mAudioService;
        RouteInfo mBluetoothA2dpRoute;
        final boolean mCanConfigureWifiDisplays;
        IMediaRouterClient mClient;
        MediaRouterClientState mClientState;
        RouteInfo mDefaultAudioVideo;
        boolean mDiscoverRequestActiveScan;
        int mDiscoveryRequestRouteTypes;
        final DisplayManager mDisplayService;
        final Handler mHandler;
        boolean mIsBluetoothA2dpOn;
        final IMediaRouterService mMediaRouterService;
        final String mPackageName;
        String mPreviousActiveWifiDisplayAddress;
        final Resources mResources;
        RouteInfo mSelectedRoute;
        final RouteCategory mSystemCategory;
        final CopyOnWriteArrayList<CallbackInfo> mCallbacks = new CopyOnWriteArrayList<>();
        final ArrayList<RouteInfo> mRoutes = new ArrayList<>();
        final ArrayList<RouteCategory> mCategories = new ArrayList<>();
        final AudioRoutesInfo mCurAudioRoutesInfo = new AudioRoutesInfo();
        int mCurrentUserId = -1;
        SparseIntArray mStreamVolume = new SparseIntArray();
        final IAudioRoutesObserver.Stub mAudioRoutesObserver = new IAudioRoutesObserver.Stub() { // from class: android.media.MediaRouter.Static.1
            @Override // android.media.IAudioRoutesObserver
            public void dispatchAudioRoutesChanged(final AudioRoutesInfo newRoutes) {
                try {
                    Static r0 = Static.this;
                    r0.mIsBluetoothA2dpOn = r0.mAudioService.isBluetoothA2dpOn();
                } catch (RemoteException e) {
                    Log.m109e(MediaRouter.TAG, "Error querying Bluetooth A2DP state", e);
                }
                Static.this.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.Static.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Static.this.updateAudioRoutes(newRoutes);
                    }
                });
            }
        };

        Static(Context appContext) {
            this.mPackageName = appContext.getPackageName();
            this.mResources = appContext.getResources();
            this.mHandler = new Handler(appContext.getMainLooper());
            IBinder b = ServiceManager.getService("audio");
            this.mAudioService = IAudioService.Stub.asInterface(b);
            this.mDisplayService = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
            this.mMediaRouterService = IMediaRouterService.Stub.asInterface(ServiceManager.getService(Context.MEDIA_ROUTER_SERVICE));
            RouteCategory routeCategory = new RouteCategory((int) C4057R.string.default_audio_route_category_name, 3, false);
            this.mSystemCategory = routeCategory;
            routeCategory.mIsSystem = true;
            this.mCanConfigureWifiDisplays = appContext.checkPermission(Manifest.C0000permission.CONFIGURE_WIFI_DISPLAY, Process.myPid(), Process.myUid()) == 0;
        }

        void startMonitoringRoutes(Context appContext) {
            RouteInfo routeInfo = new RouteInfo(this.mSystemCategory);
            this.mDefaultAudioVideo = routeInfo;
            routeInfo.mNameResId = C4057R.string.default_audio_route_name;
            this.mDefaultAudioVideo.mSupportedTypes = 3;
            this.mDefaultAudioVideo.updatePresentationDisplay();
            if (((AudioManager) appContext.getSystemService("audio")).isVolumeFixed()) {
                this.mDefaultAudioVideo.mVolumeHandling = 0;
            }
            this.mDefaultAudioVideo.mGlobalRouteId = MediaRouter.sStatic.mResources.getString(C4057R.string.default_audio_route_id);
            MediaRouter.addRouteStatic(this.mDefaultAudioVideo);
            MediaRouter.updateWifiDisplayStatus(this.mDisplayService.getWifiDisplayStatus());
            appContext.registerReceiver(new WifiDisplayStatusChangedReceiver(), new IntentFilter(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED));
            appContext.registerReceiver(new VolumeChangeReceiver(), new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
            this.mDisplayService.registerDisplayListener(this, this.mHandler);
            AudioRoutesInfo newAudioRoutes = null;
            try {
                this.mIsBluetoothA2dpOn = this.mAudioService.isBluetoothA2dpOn();
                newAudioRoutes = this.mAudioService.startWatchingRoutes(this.mAudioRoutesObserver);
            } catch (RemoteException e) {
            }
            if (newAudioRoutes != null) {
                updateAudioRoutes(newAudioRoutes);
            }
            rebindAsUser(UserHandle.myUserId());
            if (this.mSelectedRoute == null) {
                MediaRouter.selectDefaultRouteStatic();
            }
        }

        void updateAudioRoutes(AudioRoutesInfo newRoutes) {
            RouteInfo routeInfo;
            int name;
            boolean audioRoutesChanged = false;
            boolean forceUseDefaultRoute = false;
            if (newRoutes.mainType != this.mCurAudioRoutesInfo.mainType) {
                this.mCurAudioRoutesInfo.mainType = newRoutes.mainType;
                if ((newRoutes.mainType & 2) != 0 || (newRoutes.mainType & 1) != 0) {
                    name = C4057R.string.default_audio_route_name_headphones;
                } else if ((newRoutes.mainType & 4) != 0) {
                    name = C4057R.string.default_audio_route_name_dock_speakers;
                } else {
                    int name2 = newRoutes.mainType;
                    if ((name2 & 8) != 0) {
                        name = C4057R.string.default_audio_route_name_external_device;
                    } else {
                        int name3 = newRoutes.mainType;
                        if ((name3 & 16) != 0) {
                            name = C4057R.string.default_audio_route_name_usb;
                        } else {
                            name = C4057R.string.default_audio_route_name;
                        }
                    }
                }
                this.mDefaultAudioVideo.mNameResId = name;
                MediaRouter.dispatchRouteChanged(this.mDefaultAudioVideo);
                if ((newRoutes.mainType & 19) != 0) {
                    forceUseDefaultRoute = true;
                }
                audioRoutesChanged = true;
            }
            if (!TextUtils.equals(newRoutes.bluetoothName, this.mCurAudioRoutesInfo.bluetoothName)) {
                forceUseDefaultRoute = false;
                if (newRoutes.bluetoothName != null) {
                    RouteInfo routeInfo2 = this.mBluetoothA2dpRoute;
                    if (routeInfo2 == null) {
                        RouteInfo info = new RouteInfo(this.mSystemCategory);
                        info.mName = newRoutes.bluetoothName;
                        info.mDescription = this.mResources.getText(C4057R.string.bluetooth_a2dp_audio_route_name);
                        info.mSupportedTypes = 1;
                        info.mDeviceType = 3;
                        info.mGlobalRouteId = MediaRouter.sStatic.mResources.getString(C4057R.string.bluetooth_a2dp_audio_route_id);
                        this.mBluetoothA2dpRoute = info;
                        MediaRouter.addRouteStatic(info);
                    } else {
                        routeInfo2.mName = newRoutes.bluetoothName;
                        MediaRouter.dispatchRouteChanged(this.mBluetoothA2dpRoute);
                    }
                } else if (this.mBluetoothA2dpRoute != null) {
                    RouteInfo btRoute = this.mBluetoothA2dpRoute;
                    this.mBluetoothA2dpRoute = null;
                    MediaRouter.removeRouteStatic(btRoute);
                }
                audioRoutesChanged = true;
            }
            if (audioRoutesChanged) {
                Log.m106v(MediaRouter.TAG, "Audio routes updated: " + newRoutes + ", a2dp=" + isBluetoothA2dpOn());
                RouteInfo routeInfo3 = this.mSelectedRoute;
                if (routeInfo3 == null || routeInfo3.isDefault() || this.mSelectedRoute.isBluetooth()) {
                    if (forceUseDefaultRoute || (routeInfo = this.mBluetoothA2dpRoute) == null) {
                        MediaRouter.selectRouteStatic(1, this.mDefaultAudioVideo, false);
                    } else {
                        MediaRouter.selectRouteStatic(1, routeInfo, false);
                    }
                }
            }
            this.mCurAudioRoutesInfo.bluetoothName = newRoutes.bluetoothName;
        }

        int getStreamVolume(int streamType) {
            int idx = this.mStreamVolume.indexOfKey(streamType);
            if (idx < 0) {
                int volume = 0;
                try {
                    try {
                        volume = this.mAudioService.getStreamVolume(streamType);
                        this.mStreamVolume.put(streamType, volume);
                        return volume;
                    } catch (RemoteException e) {
                        Log.m109e(MediaRouter.TAG, "Error getting local stream volume", e);
                        return volume;
                    }
                } catch (Throwable th) {
                    return volume;
                }
            }
            return this.mStreamVolume.valueAt(idx);
        }

        boolean isBluetoothA2dpOn() {
            return this.mBluetoothA2dpRoute != null && this.mIsBluetoothA2dpOn;
        }

        void updateDiscoveryRequest() {
            int routeTypes = 0;
            int passiveRouteTypes = 0;
            boolean activeScan = false;
            boolean activeScanWifiDisplay = false;
            int count = this.mCallbacks.size();
            for (int i = 0; i < count; i++) {
                CallbackInfo cbi = this.mCallbacks.get(i);
                if ((cbi.flags & 5) != 0) {
                    routeTypes |= cbi.type;
                } else if ((cbi.flags & 8) != 0) {
                    passiveRouteTypes |= cbi.type;
                } else {
                    routeTypes |= cbi.type;
                }
                if ((1 & cbi.flags) != 0) {
                    activeScan = true;
                    if ((4 & cbi.type) != 0) {
                        activeScanWifiDisplay = true;
                    }
                }
            }
            if (routeTypes != 0 || activeScan) {
                routeTypes |= passiveRouteTypes;
            }
            if (this.mCanConfigureWifiDisplays) {
                RouteInfo routeInfo = this.mSelectedRoute;
                if (routeInfo != null && routeInfo.matchesTypes(4)) {
                    activeScanWifiDisplay = false;
                }
                if (activeScanWifiDisplay) {
                    if (!this.mActivelyScanningWifiDisplays) {
                        this.mActivelyScanningWifiDisplays = true;
                        this.mDisplayService.startWifiDisplayScan();
                    }
                } else if (this.mActivelyScanningWifiDisplays) {
                    this.mActivelyScanningWifiDisplays = false;
                    this.mDisplayService.stopWifiDisplayScan();
                }
            }
            if (routeTypes != this.mDiscoveryRequestRouteTypes || activeScan != this.mDiscoverRequestActiveScan) {
                this.mDiscoveryRequestRouteTypes = routeTypes;
                this.mDiscoverRequestActiveScan = activeScan;
                publishClientDiscoveryRequest();
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int displayId) {
            updatePresentationDisplays(displayId);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int displayId) {
            updatePresentationDisplays(displayId);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int displayId) {
            updatePresentationDisplays(displayId);
        }

        public void setRouterGroupId(String groupId) {
            IMediaRouterClient iMediaRouterClient = this.mClient;
            if (iMediaRouterClient != null) {
                try {
                    this.mMediaRouterService.registerClientGroupId(iMediaRouterClient, groupId);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        }

        public Display[] getAllPresentationDisplays() {
            try {
                return this.mDisplayService.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
            } catch (RuntimeException ex) {
                Log.m109e(MediaRouter.TAG, "Unable to get displays.", ex);
                return null;
            }
        }

        private void updatePresentationDisplays(int changedDisplayId) {
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                RouteInfo route = this.mRoutes.get(i);
                if (route.updatePresentationDisplay() || (route.mPresentationDisplay != null && route.mPresentationDisplay.getDisplayId() == changedDisplayId)) {
                    MediaRouter.dispatchRoutePresentationDisplayChanged(route);
                }
            }
        }

        void handleGroupRouteSelected(String routeId) {
            RouteInfo routeToSelect = isBluetoothA2dpOn() ? this.mBluetoothA2dpRoute : this.mDefaultAudioVideo;
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                RouteInfo route = this.mRoutes.get(i);
                if (TextUtils.equals(route.mGlobalRouteId, routeId)) {
                    routeToSelect = route;
                }
            }
            if (routeToSelect != this.mSelectedRoute) {
                MediaRouter.selectRouteStatic(routeToSelect.mSupportedTypes, routeToSelect, false);
            }
        }

        void setSelectedRoute(RouteInfo info, boolean explicit) {
            this.mSelectedRoute = info;
            publishClientSelectedRoute(explicit);
        }

        void rebindAsUser(int userId) {
            if (this.mCurrentUserId != userId || userId < 0 || this.mClient == null) {
                IMediaRouterClient iMediaRouterClient = this.mClient;
                if (iMediaRouterClient != null) {
                    try {
                        this.mMediaRouterService.unregisterClient(iMediaRouterClient);
                    } catch (RemoteException ex) {
                        ex.rethrowFromSystemServer();
                    }
                    this.mClient = null;
                }
                this.mCurrentUserId = userId;
                try {
                    Client client = new Client();
                    this.mMediaRouterService.registerClientAsUser(client, this.mPackageName, userId);
                    this.mClient = client;
                } catch (RemoteException ex2) {
                    Log.m109e(MediaRouter.TAG, "Unable to register media router client.", ex2);
                }
                publishClientDiscoveryRequest();
                publishClientSelectedRoute(false);
                updateClientState();
            }
        }

        void publishClientDiscoveryRequest() {
            IMediaRouterClient iMediaRouterClient = this.mClient;
            if (iMediaRouterClient != null) {
                try {
                    this.mMediaRouterService.setDiscoveryRequest(iMediaRouterClient, this.mDiscoveryRequestRouteTypes, this.mDiscoverRequestActiveScan);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        }

        void publishClientSelectedRoute(boolean explicit) {
            IMediaRouterClient iMediaRouterClient = this.mClient;
            if (iMediaRouterClient != null) {
                try {
                    IMediaRouterService iMediaRouterService = this.mMediaRouterService;
                    RouteInfo routeInfo = this.mSelectedRoute;
                    iMediaRouterService.setSelectedRoute(iMediaRouterClient, routeInfo != null ? routeInfo.mGlobalRouteId : null, explicit);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        }

        void updateClientState() {
            this.mClientState = null;
            IMediaRouterClient iMediaRouterClient = this.mClient;
            if (iMediaRouterClient != null) {
                try {
                    this.mClientState = this.mMediaRouterService.getState(iMediaRouterClient);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
            MediaRouterClientState mediaRouterClientState = this.mClientState;
            ArrayList<MediaRouterClientState.RouteInfo> globalRoutes = mediaRouterClientState != null ? mediaRouterClientState.routes : null;
            int globalRouteCount = globalRoutes != null ? globalRoutes.size() : 0;
            for (int i = 0; i < globalRouteCount; i++) {
                MediaRouterClientState.RouteInfo globalRoute = globalRoutes.get(i);
                RouteInfo route = findGlobalRoute(globalRoute.f274id);
                if (route == null) {
                    MediaRouter.addRouteStatic(makeGlobalRoute(globalRoute));
                } else {
                    updateGlobalRoute(route, globalRoute);
                }
            }
            int i2 = this.mRoutes.size();
            while (true) {
                int i3 = i2 - 1;
                if (i2 > 0) {
                    RouteInfo route2 = this.mRoutes.get(i3);
                    String globalRouteId = route2.mGlobalRouteId;
                    if (!route2.isDefault() && !route2.isBluetooth() && globalRouteId != null) {
                        int j = 0;
                        while (true) {
                            if (j < globalRouteCount) {
                                if (globalRouteId.equals(globalRoutes.get(j).f274id)) {
                                    break;
                                }
                                j++;
                            } else {
                                MediaRouter.removeRouteStatic(route2);
                                break;
                            }
                        }
                    }
                    i2 = i3;
                } else {
                    return;
                }
            }
        }

        void requestSetVolume(RouteInfo route, int volume) {
            IMediaRouterClient iMediaRouterClient;
            if (route.mGlobalRouteId != null && (iMediaRouterClient = this.mClient) != null) {
                try {
                    this.mMediaRouterService.requestSetVolume(iMediaRouterClient, route.mGlobalRouteId, volume);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        }

        void requestUpdateVolume(RouteInfo route, int direction) {
            IMediaRouterClient iMediaRouterClient;
            if (route.mGlobalRouteId != null && (iMediaRouterClient = this.mClient) != null) {
                try {
                    this.mMediaRouterService.requestUpdateVolume(iMediaRouterClient, route.mGlobalRouteId, direction);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        }

        RouteInfo makeGlobalRoute(MediaRouterClientState.RouteInfo globalRoute) {
            RouteInfo route = new RouteInfo(this.mSystemCategory);
            route.mGlobalRouteId = globalRoute.f274id;
            route.mName = globalRoute.name;
            route.mDescription = globalRoute.description;
            route.mSupportedTypes = globalRoute.supportedTypes;
            route.mDeviceType = globalRoute.deviceType;
            route.mEnabled = globalRoute.enabled;
            route.setRealStatusCode(globalRoute.statusCode);
            route.mPlaybackType = globalRoute.playbackType;
            route.mPlaybackStream = globalRoute.playbackStream;
            route.mVolume = globalRoute.volume;
            route.mVolumeMax = globalRoute.volumeMax;
            route.mVolumeHandling = globalRoute.volumeHandling;
            route.mPresentationDisplayId = globalRoute.presentationDisplayId;
            route.updatePresentationDisplay();
            return route;
        }

        void updateGlobalRoute(RouteInfo route, MediaRouterClientState.RouteInfo globalRoute) {
            boolean changed = false;
            boolean volumeChanged = false;
            boolean presentationDisplayChanged = false;
            if (!Objects.equals(route.mName, globalRoute.name)) {
                route.mName = globalRoute.name;
                changed = true;
            }
            if (!Objects.equals(route.mDescription, globalRoute.description)) {
                route.mDescription = globalRoute.description;
                changed = true;
            }
            int oldSupportedTypes = route.mSupportedTypes;
            if (oldSupportedTypes != globalRoute.supportedTypes) {
                route.mSupportedTypes = globalRoute.supportedTypes;
                changed = true;
            }
            if (route.mEnabled != globalRoute.enabled) {
                route.mEnabled = globalRoute.enabled;
                changed = true;
            }
            if (route.mRealStatusCode != globalRoute.statusCode) {
                route.setRealStatusCode(globalRoute.statusCode);
                changed = true;
            }
            if (route.mPlaybackType != globalRoute.playbackType) {
                route.mPlaybackType = globalRoute.playbackType;
                changed = true;
            }
            if (route.mPlaybackStream != globalRoute.playbackStream) {
                route.mPlaybackStream = globalRoute.playbackStream;
                changed = true;
            }
            if (route.mVolume != globalRoute.volume) {
                route.mVolume = globalRoute.volume;
                changed = true;
                volumeChanged = true;
            }
            if (route.mVolumeMax != globalRoute.volumeMax) {
                route.mVolumeMax = globalRoute.volumeMax;
                changed = true;
                volumeChanged = true;
            }
            if (route.mVolumeHandling != globalRoute.volumeHandling) {
                route.mVolumeHandling = globalRoute.volumeHandling;
                changed = true;
                volumeChanged = true;
            }
            if (route.mPresentationDisplayId != globalRoute.presentationDisplayId) {
                route.mPresentationDisplayId = globalRoute.presentationDisplayId;
                route.updatePresentationDisplay();
                changed = true;
                presentationDisplayChanged = true;
            }
            if (changed) {
                MediaRouter.dispatchRouteChanged(route, oldSupportedTypes);
            }
            if (volumeChanged) {
                MediaRouter.dispatchRouteVolumeChanged(route);
            }
            if (presentationDisplayChanged) {
                MediaRouter.dispatchRoutePresentationDisplayChanged(route);
            }
        }

        RouteInfo findGlobalRoute(String globalRouteId) {
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                RouteInfo route = this.mRoutes.get(i);
                if (globalRouteId.equals(route.mGlobalRouteId)) {
                    return route;
                }
            }
            return null;
        }

        boolean isPlaybackActive() {
            IMediaRouterClient iMediaRouterClient = this.mClient;
            if (iMediaRouterClient != null) {
                try {
                    return this.mMediaRouterService.isPlaybackActive(iMediaRouterClient);
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public final class Client extends IMediaRouterClient.Stub {
            Client() {
            }

            @Override // android.media.IMediaRouterClient
            public void onStateChanged() {
                Static.this.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.Static.Client.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Client client = Client.this;
                        if (client == Static.this.mClient) {
                            Static.this.updateClientState();
                        }
                    }
                });
            }

            @Override // android.media.IMediaRouterClient
            public void onRestoreRoute() {
                Static.this.mHandler.post(new Runnable() { // from class: android.media.MediaRouter$Static$Client$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaRouter.Static.Client.this.lambda$onRestoreRoute$0();
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onRestoreRoute$0() {
                if (this == Static.this.mClient && Static.this.mSelectedRoute != null) {
                    if (!Static.this.mSelectedRoute.isDefault() && !Static.this.mSelectedRoute.isBluetooth()) {
                        return;
                    }
                    if (Static.this.mSelectedRoute.isDefault() && Static.this.mBluetoothA2dpRoute != null) {
                        Log.m112d(MediaRouter.TAG, "onRestoreRoute() : selectedRoute=" + Static.this.mSelectedRoute + ", a2dpRoute=" + Static.this.mBluetoothA2dpRoute);
                    } else {
                        Log.m112d(MediaRouter.TAG, "onRestoreRoute() : route=" + Static.this.mSelectedRoute);
                    }
                    Static.this.mSelectedRoute.select();
                }
            }

            @Override // android.media.IMediaRouterClient
            public void onGroupRouteSelected(final String groupRouteId) {
                Static.this.mHandler.post(new Runnable() { // from class: android.media.MediaRouter$Static$Client$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaRouter.Static.Client.this.lambda$onGroupRouteSelected$1(groupRouteId);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onGroupRouteSelected$1(String groupRouteId) {
                if (this == Static.this.mClient) {
                    Static.this.handleGroupRouteSelected(groupRouteId);
                }
            }
        }
    }

    static String typesToString(int types) {
        StringBuilder result = new StringBuilder();
        if ((types & 1) != 0) {
            result.append("ROUTE_TYPE_LIVE_AUDIO ");
        }
        if ((types & 2) != 0) {
            result.append("ROUTE_TYPE_LIVE_VIDEO ");
        }
        if ((types & 4) != 0) {
            result.append("ROUTE_TYPE_REMOTE_DISPLAY ");
        }
        if ((8388608 & types) != 0) {
            result.append("ROUTE_TYPE_USER ");
        }
        return result.toString();
    }

    public MediaRouter(Context context) {
        synchronized (Static.class) {
            if (sStatic == null) {
                Context appContext = context.getApplicationContext();
                Static r2 = new Static(appContext);
                sStatic = r2;
                r2.startMonitoringRoutes(appContext);
            }
        }
    }

    public RouteInfo getDefaultRoute() {
        return sStatic.mDefaultAudioVideo;
    }

    public RouteInfo getFallbackRoute() {
        return sStatic.mBluetoothA2dpRoute != null ? sStatic.mBluetoothA2dpRoute : sStatic.mDefaultAudioVideo;
    }

    public RouteCategory getSystemCategory() {
        return sStatic.mSystemCategory;
    }

    public RouteInfo getSelectedRoute() {
        return getSelectedRoute(8388615);
    }

    public RouteInfo getSelectedRoute(int type) {
        if (sStatic.mSelectedRoute != null && (sStatic.mSelectedRoute.mSupportedTypes & type) != 0) {
            return sStatic.mSelectedRoute;
        }
        if (type == 8388608) {
            return null;
        }
        return sStatic.mDefaultAudioVideo;
    }

    public boolean isRouteAvailable(int types, int flags) {
        int count = sStatic.mRoutes.size();
        for (int i = 0; i < count; i++) {
            RouteInfo route = sStatic.mRoutes.get(i);
            if (route.matchesTypes(types) && ((flags & 1) == 0 || route != sStatic.mDefaultAudioVideo)) {
                return true;
            }
        }
        return false;
    }

    public void setRouterGroupId(String groupId) {
        sStatic.setRouterGroupId(groupId);
    }

    public void addCallback(int types, Callback cb) {
        addCallback(types, cb, 0);
    }

    public void addCallback(int types, Callback cb, int flags) {
        int index = findCallbackInfo(cb);
        if (index >= 0) {
            CallbackInfo info = sStatic.mCallbacks.get(index);
            info.type |= types;
            info.flags |= flags;
        } else {
            sStatic.mCallbacks.add(new CallbackInfo(cb, types, flags, this));
        }
        sStatic.updateDiscoveryRequest();
    }

    public void removeCallback(Callback cb) {
        int index = findCallbackInfo(cb);
        if (index >= 0) {
            sStatic.mCallbacks.remove(index);
            sStatic.updateDiscoveryRequest();
            return;
        }
        Log.m104w(TAG, "removeCallback(" + cb + "): callback not registered");
    }

    private int findCallbackInfo(Callback cb) {
        int count = sStatic.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            CallbackInfo info = sStatic.mCallbacks.get(i);
            if (info.f273cb == cb) {
                return i;
            }
        }
        return -1;
    }

    public void selectRoute(int types, RouteInfo route) {
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null.");
        }
        selectRouteStatic(types, route, true);
    }

    public void selectRouteInt(int types, RouteInfo route, boolean explicit) {
        selectRouteStatic(types, route, explicit);
    }

    static void selectRouteStatic(int types, RouteInfo route, boolean explicit) {
        Log.m106v(TAG, "Selecting route: " + route);
        RouteInfo oldRoute = sStatic.mSelectedRoute;
        RouteInfo currentSystemRoute = sStatic.isBluetoothA2dpOn() ? sStatic.mBluetoothA2dpRoute : sStatic.mDefaultAudioVideo;
        boolean wasDefaultOrBluetoothRoute = oldRoute != null && (oldRoute.isDefault() || oldRoute.isBluetooth());
        if (oldRoute == route && (!wasDefaultOrBluetoothRoute || route == currentSystemRoute)) {
            return;
        }
        if (!route.matchesTypes(types)) {
            Log.m104w(TAG, "selectRoute ignored; cannot select route with supported types " + typesToString(route.getSupportedTypes()) + " into route types " + typesToString(types));
            return;
        }
        if (sStatic.isPlaybackActive() && sStatic.mBluetoothA2dpRoute != null && (types & 1) != 0 && (route.isBluetooth() || route.isDefault())) {
            try {
                sStatic.mMediaRouterService.setBluetoothA2dpOn(sStatic.mClient, route.isBluetooth());
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error changing Bluetooth A2DP state", e);
            }
        } else {
            Log.m108i(TAG, "Skip setBluetoothA2dpOn(): types=" + types + ", isPlaybackActive()=" + sStatic.isPlaybackActive() + ", BT route=" + sStatic.mBluetoothA2dpRoute);
        }
        WifiDisplay activeDisplay = sStatic.mDisplayService.getWifiDisplayStatus().getActiveDisplay();
        boolean oldRouteHasAddress = (oldRoute == null || oldRoute.mDeviceAddress == null) ? false : true;
        boolean newRouteHasAddress = route.mDeviceAddress != null;
        if (activeDisplay != null || oldRouteHasAddress || newRouteHasAddress) {
            if (newRouteHasAddress && !matchesDeviceAddress(activeDisplay, route)) {
                if (sStatic.mCanConfigureWifiDisplays) {
                    sStatic.mDisplayService.connectWifiDisplay(route.mDeviceAddress);
                } else {
                    Log.m110e(TAG, "Cannot connect to wifi displays because this process is not allowed to do so.");
                }
            } else if (activeDisplay != null && !newRouteHasAddress) {
                sStatic.mDisplayService.disconnectWifiDisplay();
            }
        }
        sStatic.setSelectedRoute(route, explicit);
        if (oldRoute != null) {
            dispatchRouteUnselected(oldRoute.getSupportedTypes() & types, oldRoute);
            if (oldRoute.resolveStatusCode()) {
                dispatchRouteChanged(oldRoute);
            }
        }
        if (route != null) {
            if (route.resolveStatusCode()) {
                dispatchRouteChanged(route);
            }
            dispatchRouteSelected(route.getSupportedTypes() & types, route);
        }
        sStatic.updateDiscoveryRequest();
    }

    static void selectDefaultRouteStatic() {
        if (sStatic.isBluetoothA2dpOn() && sStatic.mSelectedRoute != null && !sStatic.mSelectedRoute.isBluetooth()) {
            selectRouteStatic(8388615, sStatic.mBluetoothA2dpRoute, false);
        } else {
            selectRouteStatic(8388615, sStatic.mDefaultAudioVideo, false);
        }
    }

    static boolean matchesDeviceAddress(WifiDisplay display, RouteInfo info) {
        boolean routeHasAddress = (info == null || info.mDeviceAddress == null) ? false : true;
        if (display == null && !routeHasAddress) {
            return true;
        }
        if (display == null || !routeHasAddress) {
            return false;
        }
        return display.getDeviceAddress().equals(info.mDeviceAddress);
    }

    public void addUserRoute(UserRouteInfo info) {
        addRouteStatic(info);
    }

    public void addRouteInt(RouteInfo info) {
        addRouteStatic(info);
    }

    static void addRouteStatic(RouteInfo info) {
        if (DEBUG) {
            Log.m112d(TAG, "Adding route: " + info);
        }
        RouteCategory cat = info.getCategory();
        if (!sStatic.mCategories.contains(cat)) {
            sStatic.mCategories.add(cat);
        }
        if (cat.isGroupable() && !(info instanceof RouteGroup)) {
            RouteGroup group = new RouteGroup(info.getCategory());
            group.mSupportedTypes = info.mSupportedTypes;
            sStatic.mRoutes.add(group);
            dispatchRouteAdded(group);
            group.addRoute(info);
            return;
        }
        sStatic.mRoutes.add(info);
        dispatchRouteAdded(info);
    }

    public void removeUserRoute(UserRouteInfo info) {
        removeRouteStatic(info);
    }

    public void clearUserRoutes() {
        int i = 0;
        while (i < sStatic.mRoutes.size()) {
            RouteInfo info = sStatic.mRoutes.get(i);
            if ((info instanceof UserRouteInfo) || (info instanceof RouteGroup)) {
                removeRouteStatic(info);
                i--;
            }
            i++;
        }
    }

    public void removeRouteInt(RouteInfo info) {
        removeRouteStatic(info);
    }

    static void removeRouteStatic(RouteInfo info) {
        if (DEBUG) {
            Log.m112d(TAG, "Removing route: " + info);
        }
        if (sStatic.mRoutes.remove(info)) {
            RouteCategory removingCat = info.getCategory();
            int count = sStatic.mRoutes.size();
            boolean found = false;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                RouteCategory cat = sStatic.mRoutes.get(i).getCategory();
                if (removingCat != cat) {
                    i++;
                } else {
                    found = true;
                    break;
                }
            }
            if (info.isSelected()) {
                selectDefaultRouteStatic();
            }
            if (!found) {
                sStatic.mCategories.remove(removingCat);
            }
            dispatchRouteRemoved(info);
        }
    }

    public int getCategoryCount() {
        return sStatic.mCategories.size();
    }

    public RouteCategory getCategoryAt(int index) {
        return sStatic.mCategories.get(index);
    }

    public int getRouteCount() {
        return sStatic.mRoutes.size();
    }

    public RouteInfo getRouteAt(int index) {
        return sStatic.mRoutes.get(index);
    }

    static int getRouteCountStatic() {
        return sStatic.mRoutes.size();
    }

    static RouteInfo getRouteAtStatic(int index) {
        return sStatic.mRoutes.get(index);
    }

    public UserRouteInfo createUserRoute(RouteCategory category) {
        return new UserRouteInfo(category);
    }

    public RouteCategory createRouteCategory(CharSequence name, boolean isGroupable) {
        return new RouteCategory(name, 8388608, isGroupable);
    }

    public RouteCategory createRouteCategory(int nameResId, boolean isGroupable) {
        return new RouteCategory(nameResId, 8388608, isGroupable);
    }

    public void rebindAsUser(int userId) {
        sStatic.rebindAsUser(userId);
    }

    static void updateRoute(RouteInfo info) {
        dispatchRouteChanged(info);
    }

    static void dispatchRouteSelected(int type, RouteInfo info) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.f273cb.onRouteSelected(cbi.router, type, info);
            }
        }
    }

    static void dispatchRouteUnselected(int type, RouteInfo info) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.f273cb.onRouteUnselected(cbi.router, type, info);
            }
        }
    }

    static void dispatchRouteChanged(RouteInfo info) {
        dispatchRouteChanged(info, info.mSupportedTypes);
    }

    static void dispatchRouteChanged(RouteInfo info, int oldSupportedTypes) {
        if (DEBUG) {
            Log.m112d(TAG, "Dispatching route change: " + info);
        }
        int newSupportedTypes = info.mSupportedTypes;
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            boolean oldVisibility = cbi.filterRouteEvent(oldSupportedTypes);
            boolean newVisibility = cbi.filterRouteEvent(newSupportedTypes);
            if (!oldVisibility && newVisibility) {
                cbi.f273cb.onRouteAdded(cbi.router, info);
                if (info.isSelected()) {
                    cbi.f273cb.onRouteSelected(cbi.router, newSupportedTypes, info);
                }
            }
            if (oldVisibility || newVisibility) {
                cbi.f273cb.onRouteChanged(cbi.router, info);
            }
            if (oldVisibility && !newVisibility) {
                if (info.isSelected()) {
                    cbi.f273cb.onRouteUnselected(cbi.router, oldSupportedTypes, info);
                }
                cbi.f273cb.onRouteRemoved(cbi.router, info);
            }
        }
    }

    static void dispatchRouteAdded(RouteInfo info) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.f273cb.onRouteAdded(cbi.router, info);
            }
        }
    }

    static void dispatchRouteRemoved(RouteInfo info) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.f273cb.onRouteRemoved(cbi.router, info);
            }
        }
    }

    static void dispatchRouteGrouped(RouteInfo info, RouteGroup group, int index) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(group)) {
                cbi.f273cb.onRouteGrouped(cbi.router, info, group, index);
            }
        }
    }

    static void dispatchRouteUngrouped(RouteInfo info, RouteGroup group) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(group)) {
                cbi.f273cb.onRouteUngrouped(cbi.router, info, group);
            }
        }
    }

    static void dispatchRouteVolumeChanged(RouteInfo info) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.f273cb.onRouteVolumeChanged(cbi.router, info);
            }
        }
    }

    static void dispatchRoutePresentationDisplayChanged(RouteInfo info) {
        Iterator<CallbackInfo> it = sStatic.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo cbi = it.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.f273cb.onRoutePresentationDisplayChanged(cbi.router, info);
            }
        }
    }

    static void systemVolumeChanged(int newValue) {
        RouteInfo selectedRoute = sStatic.mSelectedRoute;
        if (selectedRoute == null) {
            return;
        }
        if (selectedRoute.isBluetooth() || selectedRoute.isDefault()) {
            dispatchRouteVolumeChanged(selectedRoute);
        } else if (sStatic.mBluetoothA2dpRoute != null) {
            dispatchRouteVolumeChanged(sStatic.mIsBluetoothA2dpOn ? sStatic.mBluetoothA2dpRoute : sStatic.mDefaultAudioVideo);
        } else {
            dispatchRouteVolumeChanged(sStatic.mDefaultAudioVideo);
        }
    }

    static void updateWifiDisplayStatus(WifiDisplayStatus status) {
        WifiDisplay[] displays;
        WifiDisplay activeDisplay;
        WifiDisplay d;
        if (status.getFeatureState() == 3) {
            displays = status.getDisplays();
            activeDisplay = status.getActiveDisplay();
            if (!sStatic.mCanConfigureWifiDisplays) {
                if (activeDisplay != null) {
                    displays = new WifiDisplay[]{activeDisplay};
                } else {
                    displays = WifiDisplay.EMPTY_ARRAY;
                }
            }
        } else {
            displays = WifiDisplay.EMPTY_ARRAY;
            activeDisplay = null;
        }
        String activeDisplayAddress = activeDisplay != null ? activeDisplay.getDeviceAddress() : null;
        for (WifiDisplay d2 : displays) {
            if (shouldShowWifiDisplay(d2, activeDisplay)) {
                RouteInfo route = findWifiDisplayRoute(d2);
                if (route == null) {
                    route = makeWifiDisplayRoute(d2, status);
                    addRouteStatic(route);
                } else {
                    String address = d2.getDeviceAddress();
                    boolean disconnected = !address.equals(activeDisplayAddress) && address.equals(sStatic.mPreviousActiveWifiDisplayAddress);
                    updateWifiDisplayRoute(route, d2, status, disconnected);
                }
                if (d2.equals(activeDisplay)) {
                    selectRouteStatic(route.getSupportedTypes(), route, false);
                }
            }
        }
        int i = sStatic.mRoutes.size();
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                RouteInfo route2 = sStatic.mRoutes.get(i2);
                if (route2.mDeviceAddress != null && ((d = findWifiDisplay(displays, route2.mDeviceAddress)) == null || !shouldShowWifiDisplay(d, activeDisplay))) {
                    removeRouteStatic(route2);
                }
                i = i2;
            } else {
                sStatic.mPreviousActiveWifiDisplayAddress = activeDisplayAddress;
                return;
            }
        }
    }

    private static boolean shouldShowWifiDisplay(WifiDisplay d, WifiDisplay activeDisplay) {
        return d.isRemembered() || d.equals(activeDisplay);
    }

    static int getWifiDisplayStatusCode(WifiDisplay d, WifiDisplayStatus wfdStatus) {
        int newStatus;
        if (wfdStatus.getScanState() == 1) {
            newStatus = 1;
        } else if (d.isAvailable()) {
            newStatus = d.canConnect() ? 3 : 5;
        } else {
            newStatus = 4;
        }
        if (d.equals(wfdStatus.getActiveDisplay())) {
            int activeState = wfdStatus.getActiveDisplayState();
            switch (activeState) {
                case 0:
                    Log.m110e(TAG, "Active display is not connected!");
                    return newStatus;
                case 1:
                    return 2;
                case 2:
                    return 6;
                default:
                    return newStatus;
            }
        }
        return newStatus;
    }

    static boolean isWifiDisplayEnabled(WifiDisplay d, WifiDisplayStatus wfdStatus) {
        return d.isAvailable() && (d.canConnect() || d.equals(wfdStatus.getActiveDisplay()));
    }

    static RouteInfo makeWifiDisplayRoute(WifiDisplay display, WifiDisplayStatus wfdStatus) {
        RouteInfo newRoute = new RouteInfo(sStatic.mSystemCategory);
        newRoute.mDeviceAddress = display.getDeviceAddress();
        newRoute.mSupportedTypes = 7;
        newRoute.mVolumeHandling = 0;
        newRoute.mPlaybackType = 1;
        newRoute.setRealStatusCode(getWifiDisplayStatusCode(display, wfdStatus));
        newRoute.mEnabled = isWifiDisplayEnabled(display, wfdStatus);
        newRoute.mName = display.getFriendlyDisplayName();
        newRoute.mDescription = sStatic.mResources.getText(C4057R.string.wireless_display_route_description);
        newRoute.updatePresentationDisplay();
        newRoute.mDeviceType = 1;
        return newRoute;
    }

    private static void updateWifiDisplayRoute(RouteInfo route, WifiDisplay display, WifiDisplayStatus wfdStatus, boolean disconnected) {
        boolean changed = false;
        String newName = display.getFriendlyDisplayName();
        if (!route.getName().equals(newName)) {
            route.mName = newName;
            changed = true;
        }
        boolean enabled = isWifiDisplayEnabled(display, wfdStatus);
        boolean z = route.mEnabled != enabled;
        route.mEnabled = enabled;
        if (changed | z | route.setRealStatusCode(getWifiDisplayStatusCode(display, wfdStatus))) {
            dispatchRouteChanged(route);
        }
        if ((!enabled || disconnected) && route.isSelected()) {
            selectDefaultRouteStatic();
        }
    }

    private static WifiDisplay findWifiDisplay(WifiDisplay[] displays, String deviceAddress) {
        for (WifiDisplay d : displays) {
            if (d.getDeviceAddress().equals(deviceAddress)) {
                return d;
            }
        }
        return null;
    }

    private static RouteInfo findWifiDisplayRoute(WifiDisplay d) {
        int count = sStatic.mRoutes.size();
        for (int i = 0; i < count; i++) {
            RouteInfo info = sStatic.mRoutes.get(i);
            if (d.getDeviceAddress().equals(info.mDeviceAddress)) {
                return info;
            }
        }
        return null;
    }

    /* loaded from: classes2.dex */
    public static class RouteInfo {
        private static final int DEFAULT_PLAYBACK_MAX_VOLUME = 15;
        private static final int DEFAULT_PLAYBACK_VOLUME = 15;
        public static final int DEVICE_TYPE_BLUETOOTH = 3;
        public static final int DEVICE_TYPE_SPEAKER = 2;
        public static final int DEVICE_TYPE_TV = 1;
        public static final int DEVICE_TYPE_UNKNOWN = 0;
        public static final int PLAYBACK_TYPE_LOCAL = 0;
        public static final int PLAYBACK_TYPE_REMOTE = 1;
        public static final int PLAYBACK_VOLUME_FIXED = 0;
        public static final int PLAYBACK_VOLUME_VARIABLE = 1;
        public static final int STATUS_AVAILABLE = 3;
        public static final int STATUS_CONNECTED = 6;
        public static final int STATUS_CONNECTING = 2;
        public static final int STATUS_IN_USE = 5;
        public static final int STATUS_NONE = 0;
        public static final int STATUS_NOT_AVAILABLE = 4;
        public static final int STATUS_SCANNING = 1;
        final RouteCategory mCategory;
        CharSequence mDescription;
        String mDeviceAddress;
        String mGlobalRouteId;
        RouteGroup mGroup;
        Drawable mIcon;
        CharSequence mName;
        int mNameResId;
        Display mPresentationDisplay;
        private int mRealStatusCode;
        private int mResolvedStatusCode;
        private CharSequence mStatus;
        int mSupportedTypes;
        private Object mTag;
        VolumeCallbackInfo mVcb;
        int mPlaybackType = 0;
        int mVolumeMax = 15;
        int mVolume = 15;
        int mVolumeHandling = 1;
        int mPlaybackStream = 3;
        int mPresentationDisplayId = -1;
        boolean mEnabled = true;
        final IRemoteVolumeObserver.Stub mRemoteVolObserver = new IRemoteVolumeObserver.Stub() { // from class: android.media.MediaRouter.RouteInfo.1
            @Override // android.media.IRemoteVolumeObserver
            public void dispatchRemoteVolumeUpdate(final int direction, final int value) {
                MediaRouter.sStatic.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.RouteInfo.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (RouteInfo.this.mVcb != null) {
                            if (direction != 0) {
                                RouteInfo.this.mVcb.vcb.onVolumeUpdateRequest(RouteInfo.this.mVcb.route, direction);
                            } else {
                                RouteInfo.this.mVcb.vcb.onVolumeSetRequest(RouteInfo.this.mVcb.route, value);
                            }
                        }
                    }
                });
            }
        };
        int mDeviceType = 0;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface DeviceType {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface PlaybackType {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        private @interface PlaybackVolume {
        }

        public RouteInfo(RouteCategory category) {
            this.mCategory = category;
        }

        public CharSequence getName() {
            return getName(MediaRouter.sStatic.mResources);
        }

        public CharSequence getName(Context context) {
            return getName(context.getResources());
        }

        CharSequence getName(Resources res) {
            int i = this.mNameResId;
            if (i != 0) {
                return res.getText(i);
            }
            return this.mName;
        }

        public CharSequence getDescription() {
            return this.mDescription;
        }

        public CharSequence getStatus() {
            return this.mStatus;
        }

        boolean setRealStatusCode(int statusCode) {
            if (this.mRealStatusCode != statusCode) {
                this.mRealStatusCode = statusCode;
                return resolveStatusCode();
            }
            return false;
        }

        boolean resolveStatusCode() {
            int resId;
            int statusCode = this.mRealStatusCode;
            if (isSelected()) {
                switch (statusCode) {
                    case 1:
                    case 3:
                        statusCode = 2;
                        break;
                }
            }
            if (this.mResolvedStatusCode == statusCode) {
                return false;
            }
            this.mResolvedStatusCode = statusCode;
            switch (statusCode) {
                case 1:
                    resId = C4057R.string.media_route_status_scanning;
                    break;
                case 2:
                    resId = C4057R.string.media_route_status_connecting;
                    break;
                case 3:
                    resId = C4057R.string.media_route_status_available;
                    break;
                case 4:
                    resId = C4057R.string.media_route_status_not_available;
                    break;
                case 5:
                    resId = C4057R.string.media_route_status_in_use;
                    break;
                default:
                    resId = 0;
                    break;
            }
            this.mStatus = resId != 0 ? MediaRouter.sStatic.mResources.getText(resId) : null;
            return true;
        }

        public int getStatusCode() {
            return this.mResolvedStatusCode;
        }

        public int getSupportedTypes() {
            return this.mSupportedTypes;
        }

        public int getDeviceType() {
            return this.mDeviceType;
        }

        public boolean matchesTypes(int types) {
            return (this.mSupportedTypes & types) != 0;
        }

        public RouteGroup getGroup() {
            return this.mGroup;
        }

        public RouteCategory getCategory() {
            return this.mCategory;
        }

        public Drawable getIconDrawable() {
            return this.mIcon;
        }

        public void setTag(Object tag) {
            this.mTag = tag;
            routeUpdated();
        }

        public Object getTag() {
            return this.mTag;
        }

        public int getPlaybackType() {
            return this.mPlaybackType;
        }

        public int getPlaybackStream() {
            return this.mPlaybackStream;
        }

        public int getVolume() {
            if (this.mPlaybackType == 0) {
                return MediaRouter.sStatic.getStreamVolume(this.mPlaybackStream);
            }
            return this.mVolume;
        }

        public void requestSetVolume(int volume) {
            if (this.mPlaybackType == 0) {
                try {
                    MediaRouter.sStatic.mAudioService.setStreamVolumeWithAttribution(this.mPlaybackStream, volume, 0, ActivityThread.currentPackageName(), null);
                    return;
                } catch (RemoteException e) {
                    Log.m109e(MediaRouter.TAG, "Error setting local stream volume", e);
                    return;
                }
            }
            MediaRouter.sStatic.requestSetVolume(this, volume);
        }

        public void requestUpdateVolume(int direction) {
            if (this.mPlaybackType == 0) {
                try {
                    int volume = Math.max(0, Math.min(getVolume() + direction, getVolumeMax()));
                    MediaRouter.sStatic.mAudioService.setStreamVolumeWithAttribution(this.mPlaybackStream, volume, 0, ActivityThread.currentPackageName(), null);
                    return;
                } catch (RemoteException e) {
                    Log.m109e(MediaRouter.TAG, "Error setting local stream volume", e);
                    return;
                }
            }
            MediaRouter.sStatic.requestUpdateVolume(this, direction);
        }

        public int getVolumeMax() {
            if (this.mPlaybackType == 0) {
                try {
                    int volMax = MediaRouter.sStatic.mAudioService.getStreamMaxVolume(this.mPlaybackStream);
                    return volMax;
                } catch (RemoteException e) {
                    Log.m109e(MediaRouter.TAG, "Error getting local stream volume", e);
                    return 0;
                }
            }
            int volMax2 = this.mVolumeMax;
            return volMax2;
        }

        public int getVolumeHandling() {
            return this.mVolumeHandling;
        }

        public Display getPresentationDisplay() {
            return this.mPresentationDisplay;
        }

        public boolean updatePresentationDisplay() {
            Display display = choosePresentationDisplay();
            if (this.mPresentationDisplay != display) {
                this.mPresentationDisplay = display;
                return true;
            }
            return false;
        }

        private Display choosePresentationDisplay() {
            Display[] displays;
            if ((getSupportedTypes() & 2) == 0 || (displays = getAllPresentationDisplays()) == null || displays.length == 0) {
                return null;
            }
            if (this.mPresentationDisplayId >= 0) {
                for (Display display : displays) {
                    if (display.getDisplayId() == this.mPresentationDisplayId) {
                        return display;
                    }
                }
                return null;
            }
            if (getDeviceAddress() != null) {
                for (Display display2 : displays) {
                    if (display2.getType() == 3 && displayAddressEquals(display2)) {
                        return display2;
                    }
                }
            }
            for (Display display3 : displays) {
                if (display3.getType() == 2) {
                    return display3;
                }
            }
            for (Display display4 : displays) {
                if (display4.getType() == 1) {
                    return display4;
                }
            }
            if (this == getDefaultAudioVideo()) {
                return displays[0];
            }
            return null;
        }

        public Display[] getAllPresentationDisplays() {
            return MediaRouter.sStatic.getAllPresentationDisplays();
        }

        public RouteInfo getDefaultAudioVideo() {
            return MediaRouter.sStatic.mDefaultAudioVideo;
        }

        private boolean displayAddressEquals(Display display) {
            DisplayAddress displayAddress = display.getAddress();
            if (!(displayAddress instanceof DisplayAddress.Network)) {
                return false;
            }
            DisplayAddress.Network networkAddress = (DisplayAddress.Network) displayAddress;
            return getDeviceAddress().equals(networkAddress.toString());
        }

        public String getDeviceAddress() {
            return this.mDeviceAddress;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isConnecting() {
            return this.mResolvedStatusCode == 2;
        }

        public boolean isSelected() {
            return this == MediaRouter.sStatic.mSelectedRoute;
        }

        public boolean isDefault() {
            return this == MediaRouter.sStatic.mDefaultAudioVideo;
        }

        public boolean isBluetooth() {
            return this.mDeviceType == 3;
        }

        public void select() {
            MediaRouter.selectRouteStatic(this.mSupportedTypes, this, true);
        }

        void setStatusInt(CharSequence status) {
            if (!status.equals(this.mStatus)) {
                this.mStatus = status;
                RouteGroup routeGroup = this.mGroup;
                if (routeGroup != null) {
                    routeGroup.memberStatusChanged(this, status);
                }
                routeUpdated();
            }
        }

        void routeUpdated() {
            MediaRouter.updateRoute(this);
        }

        public String toString() {
            String supportedTypes = MediaRouter.typesToString(getSupportedTypes());
            return getClass().getSimpleName() + "{ name=" + ((Object) getName()) + ", description=" + ((Object) getDescription()) + ", status=" + ((Object) getStatus()) + ", category=" + getCategory() + ", supportedTypes=" + supportedTypes + ", presentationDisplay=" + this.mPresentationDisplay + " }";
        }
    }

    /* loaded from: classes2.dex */
    public static class UserRouteInfo extends RouteInfo {
        RemoteControlClient mRcc;
        SessionVolumeProvider mSvp;

        UserRouteInfo(RouteCategory category) {
            super(category);
            this.mSupportedTypes = 8388608;
            this.mPlaybackType = 1;
            this.mVolumeHandling = 0;
        }

        public void setName(CharSequence name) {
            this.mNameResId = 0;
            this.mName = name;
            routeUpdated();
        }

        public void setName(int resId) {
            this.mNameResId = resId;
            this.mName = null;
            routeUpdated();
        }

        public void setDescription(CharSequence description) {
            this.mDescription = description;
            routeUpdated();
        }

        public void setStatus(CharSequence status) {
            setStatusInt(status);
        }

        public void setRemoteControlClient(RemoteControlClient rcc) {
            this.mRcc = rcc;
            updatePlaybackInfoOnRcc();
        }

        public RemoteControlClient getRemoteControlClient() {
            return this.mRcc;
        }

        public void setIconDrawable(Drawable icon) {
            this.mIcon = icon;
        }

        public void setIconResource(int resId) {
            setIconDrawable(MediaRouter.sStatic.mResources.getDrawable(resId));
        }

        public void setVolumeCallback(VolumeCallback vcb) {
            this.mVcb = new VolumeCallbackInfo(vcb, this);
        }

        public void setPlaybackType(int type) {
            if (this.mPlaybackType != type) {
                this.mPlaybackType = type;
                configureSessionVolume();
            }
        }

        public void setVolumeHandling(int volumeHandling) {
            if (this.mVolumeHandling != volumeHandling) {
                this.mVolumeHandling = volumeHandling;
                configureSessionVolume();
            }
        }

        public void setVolume(int volume) {
            int volume2 = Math.max(0, Math.min(volume, getVolumeMax()));
            if (this.mVolume != volume2) {
                this.mVolume = volume2;
                SessionVolumeProvider sessionVolumeProvider = this.mSvp;
                if (sessionVolumeProvider != null) {
                    sessionVolumeProvider.setCurrentVolume(this.mVolume);
                }
                MediaRouter.dispatchRouteVolumeChanged(this);
                if (this.mGroup != null) {
                    this.mGroup.memberVolumeChanged(this);
                }
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestSetVolume(int volume) {
            if (this.mVolumeHandling == 1) {
                if (this.mVcb == null) {
                    Log.m110e(MediaRouter.TAG, "Cannot requestSetVolume on user route - no volume callback set");
                } else {
                    this.mVcb.vcb.onVolumeSetRequest(this, volume);
                }
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestUpdateVolume(int direction) {
            if (this.mVolumeHandling == 1) {
                if (this.mVcb == null) {
                    Log.m110e(MediaRouter.TAG, "Cannot requestChangeVolume on user route - no volumec callback set");
                } else {
                    this.mVcb.vcb.onVolumeUpdateRequest(this, direction);
                }
            }
        }

        public void setVolumeMax(int volumeMax) {
            if (this.mVolumeMax != volumeMax) {
                this.mVolumeMax = volumeMax;
                configureSessionVolume();
            }
        }

        public void setPlaybackStream(int stream) {
            if (this.mPlaybackStream != stream) {
                this.mPlaybackStream = stream;
                configureSessionVolume();
            }
        }

        private void updatePlaybackInfoOnRcc() {
            configureSessionVolume();
        }

        private void configureSessionVolume() {
            RemoteControlClient remoteControlClient = this.mRcc;
            if (remoteControlClient == null) {
                if (MediaRouter.DEBUG) {
                    Log.m112d(MediaRouter.TAG, "No Rcc to configure volume for route " + ((Object) getName()));
                    return;
                }
                return;
            }
            MediaSession session = remoteControlClient.getMediaSession();
            if (session == null) {
                if (MediaRouter.DEBUG) {
                    Log.m112d(MediaRouter.TAG, "Rcc has no session to configure volume");
                }
            } else if (this.mPlaybackType == 1) {
                int volumeControl = 0;
                switch (this.mVolumeHandling) {
                    case 1:
                        volumeControl = 2;
                        break;
                }
                SessionVolumeProvider sessionVolumeProvider = this.mSvp;
                if (sessionVolumeProvider == null || sessionVolumeProvider.getVolumeControl() != volumeControl || this.mSvp.getMaxVolume() != this.mVolumeMax) {
                    SessionVolumeProvider sessionVolumeProvider2 = new SessionVolumeProvider(volumeControl, this.mVolumeMax, this.mVolume);
                    this.mSvp = sessionVolumeProvider2;
                    session.setPlaybackToRemote(sessionVolumeProvider2);
                }
            } else {
                AudioAttributes.Builder bob = new AudioAttributes.Builder();
                bob.setLegacyStreamType(this.mPlaybackStream);
                session.setPlaybackToLocal(bob.build());
                this.mSvp = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public class SessionVolumeProvider extends VolumeProvider {
            SessionVolumeProvider(int volumeControl, int maxVolume, int currentVolume) {
                super(volumeControl, maxVolume, currentVolume);
            }

            @Override // android.media.VolumeProvider
            public void onSetVolumeTo(final int volume) {
                MediaRouter.sStatic.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.UserRouteInfo.SessionVolumeProvider.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (UserRouteInfo.this.mVcb != null) {
                            UserRouteInfo.this.mVcb.vcb.onVolumeSetRequest(UserRouteInfo.this.mVcb.route, volume);
                        }
                    }
                });
            }

            @Override // android.media.VolumeProvider
            public void onAdjustVolume(final int direction) {
                MediaRouter.sStatic.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.UserRouteInfo.SessionVolumeProvider.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (UserRouteInfo.this.mVcb != null) {
                            UserRouteInfo.this.mVcb.vcb.onVolumeUpdateRequest(UserRouteInfo.this.mVcb.route, direction);
                        }
                    }
                });
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class RouteGroup extends RouteInfo {
        final ArrayList<RouteInfo> mRoutes;
        private boolean mUpdateName;

        RouteGroup(RouteCategory category) {
            super(category);
            this.mRoutes = new ArrayList<>();
            this.mGroup = this;
            this.mVolumeHandling = 0;
        }

        @Override // android.media.MediaRouter.RouteInfo
        CharSequence getName(Resources res) {
            if (this.mUpdateName) {
                updateName();
            }
            return super.getName(res);
        }

        public void addRoute(RouteInfo route) {
            if (route.getGroup() != null) {
                throw new IllegalStateException("Route " + route + " is already part of a group.");
            }
            if (route.getCategory() != this.mCategory) {
                throw new IllegalArgumentException("Route cannot be added to a group with a different category. (Route category=" + route.getCategory() + " group category=" + this.mCategory + NavigationBarInflaterView.KEY_CODE_END);
            }
            int at = this.mRoutes.size();
            this.mRoutes.add(route);
            route.mGroup = this;
            this.mUpdateName = true;
            updateVolume();
            routeUpdated();
            MediaRouter.dispatchRouteGrouped(route, this, at);
        }

        public void addRoute(RouteInfo route, int insertAt) {
            if (route.getGroup() != null) {
                throw new IllegalStateException("Route " + route + " is already part of a group.");
            }
            if (route.getCategory() != this.mCategory) {
                throw new IllegalArgumentException("Route cannot be added to a group with a different category. (Route category=" + route.getCategory() + " group category=" + this.mCategory + NavigationBarInflaterView.KEY_CODE_END);
            }
            this.mRoutes.add(insertAt, route);
            route.mGroup = this;
            this.mUpdateName = true;
            updateVolume();
            routeUpdated();
            MediaRouter.dispatchRouteGrouped(route, this, insertAt);
        }

        public void removeRoute(RouteInfo route) {
            if (route.getGroup() != this) {
                throw new IllegalArgumentException("Route " + route + " is not a member of this group.");
            }
            this.mRoutes.remove(route);
            route.mGroup = null;
            this.mUpdateName = true;
            updateVolume();
            MediaRouter.dispatchRouteUngrouped(route, this);
            routeUpdated();
        }

        public void removeRoute(int index) {
            RouteInfo route = this.mRoutes.remove(index);
            route.mGroup = null;
            this.mUpdateName = true;
            updateVolume();
            MediaRouter.dispatchRouteUngrouped(route, this);
            routeUpdated();
        }

        public int getRouteCount() {
            return this.mRoutes.size();
        }

        public RouteInfo getRouteAt(int index) {
            return this.mRoutes.get(index);
        }

        public void setIconDrawable(Drawable icon) {
            this.mIcon = icon;
        }

        public void setIconResource(int resId) {
            setIconDrawable(MediaRouter.sStatic.mResources.getDrawable(resId));
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestSetVolume(int volume) {
            int maxVol = getVolumeMax();
            if (maxVol == 0) {
                return;
            }
            float scaledVolume = volume / maxVol;
            int routeCount = getRouteCount();
            for (int i = 0; i < routeCount; i++) {
                RouteInfo route = getRouteAt(i);
                int routeVol = (int) (route.getVolumeMax() * scaledVolume);
                route.requestSetVolume(routeVol);
            }
            int i2 = this.mVolume;
            if (volume != i2) {
                this.mVolume = volume;
                MediaRouter.dispatchRouteVolumeChanged(this);
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestUpdateVolume(int direction) {
            int maxVol = getVolumeMax();
            if (maxVol == 0) {
                return;
            }
            int routeCount = getRouteCount();
            int volume = 0;
            for (int i = 0; i < routeCount; i++) {
                RouteInfo route = getRouteAt(i);
                route.requestUpdateVolume(direction);
                int routeVol = route.getVolume();
                if (routeVol > volume) {
                    volume = routeVol;
                }
            }
            int i2 = this.mVolume;
            if (volume != i2) {
                this.mVolume = volume;
                MediaRouter.dispatchRouteVolumeChanged(this);
            }
        }

        void memberNameChanged(RouteInfo info, CharSequence name) {
            this.mUpdateName = true;
            routeUpdated();
        }

        void memberStatusChanged(RouteInfo info, CharSequence status) {
            setStatusInt(status);
        }

        void memberVolumeChanged(RouteInfo info) {
            updateVolume();
        }

        void updateVolume() {
            int routeCount = getRouteCount();
            int volume = 0;
            for (int i = 0; i < routeCount; i++) {
                int routeVol = getRouteAt(i).getVolume();
                if (routeVol > volume) {
                    volume = routeVol;
                }
            }
            int i2 = this.mVolume;
            if (volume != i2) {
                this.mVolume = volume;
                MediaRouter.dispatchRouteVolumeChanged(this);
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        void routeUpdated() {
            int types = 0;
            int count = this.mRoutes.size();
            if (count == 0) {
                MediaRouter.removeRouteStatic(this);
                return;
            }
            int maxVolume = 0;
            int i = 1;
            int i2 = 1;
            int i3 = 0;
            while (true) {
                int i4 = 0;
                if (i3 >= count) {
                    break;
                }
                RouteInfo route = this.mRoutes.get(i3);
                types |= route.mSupportedTypes;
                int routeMaxVolume = route.getVolumeMax();
                if (routeMaxVolume > maxVolume) {
                    maxVolume = routeMaxVolume;
                }
                i &= route.getPlaybackType() == 0 ? 1 : 0;
                if (route.getVolumeHandling() == 0) {
                    i4 = 1;
                }
                i2 &= i4;
                i3++;
            }
            int i5 = i ^ 1;
            this.mPlaybackType = i5;
            this.mVolumeHandling = i2 ^ 1;
            this.mSupportedTypes = types;
            this.mVolumeMax = maxVolume;
            this.mIcon = count == 1 ? this.mRoutes.get(0).getIconDrawable() : null;
            super.routeUpdated();
        }

        void updateName() {
            StringBuilder sb = new StringBuilder();
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                RouteInfo info = this.mRoutes.get(i);
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(info.getName());
            }
            this.mName = sb.toString();
            this.mUpdateName = false;
        }

        @Override // android.media.MediaRouter.RouteInfo
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append('[');
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.mRoutes.get(i));
            }
            sb.append(']');
            return sb.toString();
        }
    }

    /* loaded from: classes2.dex */
    public static class RouteCategory {
        final boolean mGroupable;
        boolean mIsSystem;
        CharSequence mName;
        int mNameResId;
        int mTypes;

        RouteCategory(CharSequence name, int types, boolean groupable) {
            this.mName = name;
            this.mTypes = types;
            this.mGroupable = groupable;
        }

        RouteCategory(int nameResId, int types, boolean groupable) {
            this.mNameResId = nameResId;
            this.mTypes = types;
            this.mGroupable = groupable;
        }

        public CharSequence getName() {
            return getName(MediaRouter.sStatic.mResources);
        }

        public CharSequence getName(Context context) {
            return getName(context.getResources());
        }

        CharSequence getName(Resources res) {
            int i = this.mNameResId;
            if (i != 0) {
                return res.getText(i);
            }
            return this.mName;
        }

        public List<RouteInfo> getRoutes(List<RouteInfo> out) {
            if (out == null) {
                out = new ArrayList();
            } else {
                out.clear();
            }
            int count = MediaRouter.getRouteCountStatic();
            for (int i = 0; i < count; i++) {
                RouteInfo route = MediaRouter.getRouteAtStatic(i);
                if (route.mCategory == this) {
                    out.add(route);
                }
            }
            return out;
        }

        public int getSupportedTypes() {
            return this.mTypes;
        }

        public boolean isGroupable() {
            return this.mGroupable;
        }

        public boolean isSystem() {
            return this.mIsSystem;
        }

        public String toString() {
            return "RouteCategory{ name=" + ((Object) getName()) + " types=" + MediaRouter.typesToString(this.mTypes) + " groupable=" + this.mGroupable + " }";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class CallbackInfo {

        /* renamed from: cb */
        public final Callback f273cb;
        public int flags;
        public final MediaRouter router;
        public int type;

        public CallbackInfo(Callback cb, int type, int flags, MediaRouter router) {
            this.f273cb = cb;
            this.type = type;
            this.flags = flags;
            this.router = router;
        }

        public boolean filterRouteEvent(RouteInfo route) {
            return filterRouteEvent(route.mSupportedTypes);
        }

        public boolean filterRouteEvent(int supportedTypes) {
            return ((this.flags & 2) == 0 && (this.type & supportedTypes) == 0) ? false : true;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        public abstract void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo);

        public abstract void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo);

        public abstract void onRouteGrouped(MediaRouter mediaRouter, RouteInfo routeInfo, RouteGroup routeGroup, int i);

        public abstract void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo);

        public abstract void onRouteSelected(MediaRouter mediaRouter, int i, RouteInfo routeInfo);

        public abstract void onRouteUngrouped(MediaRouter mediaRouter, RouteInfo routeInfo, RouteGroup routeGroup);

        public abstract void onRouteUnselected(MediaRouter mediaRouter, int i, RouteInfo routeInfo);

        public abstract void onRouteVolumeChanged(MediaRouter mediaRouter, RouteInfo routeInfo);

        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {
        }
    }

    /* loaded from: classes2.dex */
    public static class SimpleCallback extends Callback {
        @Override // android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter router, int type, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteAdded(MediaRouter router, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteRemoved(MediaRouter router, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteChanged(MediaRouter router, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteGrouped(MediaRouter router, RouteInfo info, RouteGroup group, int index) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUngrouped(MediaRouter router, RouteInfo info, RouteGroup group) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteVolumeChanged(MediaRouter router, RouteInfo info) {
        }
    }

    /* loaded from: classes2.dex */
    static class VolumeCallbackInfo {
        public final RouteInfo route;
        public final VolumeCallback vcb;

        public VolumeCallbackInfo(VolumeCallback vcb, RouteInfo route) {
            this.vcb = vcb;
            this.route = route;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class VolumeChangeReceiver extends BroadcastReceiver {
        VolumeChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                int streamType = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, -1);
                int newVolume = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, 0);
                MediaRouter.sStatic.mStreamVolume.put(streamType, newVolume);
                if (streamType != 3) {
                    return;
                }
                int oldVolume = intent.getIntExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, 0);
                if (newVolume != oldVolume) {
                    MediaRouter.systemVolumeChanged(newVolume);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class WifiDisplayStatusChangedReceiver extends BroadcastReceiver {
        WifiDisplayStatusChangedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED)) {
                MediaRouter.updateWifiDisplayStatus((WifiDisplayStatus) intent.getParcelableExtra(DisplayManager.EXTRA_WIFI_DISPLAY_STATUS, WifiDisplayStatus.class));
            }
        }
    }
}
