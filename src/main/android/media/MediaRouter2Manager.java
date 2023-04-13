package android.media;

import android.app.job.JobInfo;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.IMediaRouter2Manager;
import android.media.IMediaRouterService;
import android.media.MediaRouter2Manager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes2.dex */
public final class MediaRouter2Manager {
    public static final int REQUEST_ID_NONE = 0;
    private static final String TAG = "MR2Manager";
    public static final int TRANSFER_TIMEOUT_MS = 30000;
    private static MediaRouter2Manager sInstance;
    private static final Object sLock = new Object();
    private final Client mClient;
    final Handler mHandler;
    private final IMediaRouterService mMediaRouterService;
    private final MediaSessionManager mMediaSessionManager;
    private final AtomicInteger mScanRequestCount = new AtomicInteger(0);
    final CopyOnWriteArrayList<CallbackRecord> mCallbackRecords = new CopyOnWriteArrayList<>();
    private final Object mRoutesLock = new Object();
    private final Map<String, MediaRoute2Info> mRoutes = new HashMap();
    final ConcurrentMap<String, RouteDiscoveryPreference> mDiscoveryPreferenceMap = new ConcurrentHashMap();
    private final ConcurrentMap<String, RouteListingPreference> mPackageToRouteListingPreferenceMap = new ConcurrentHashMap();
    private final AtomicInteger mNextRequestId = new AtomicInteger(1);
    private final CopyOnWriteArrayList<TransferRequest> mTransferRequests = new CopyOnWriteArrayList<>();

    public static MediaRouter2Manager getInstance(Context context) {
        MediaRouter2Manager mediaRouter2Manager;
        Objects.requireNonNull(context, "context must not be null");
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new MediaRouter2Manager(context);
            }
            mediaRouter2Manager = sInstance;
        }
        return mediaRouter2Manager;
    }

    private MediaRouter2Manager(Context context) {
        IMediaRouterService asInterface = IMediaRouterService.Stub.asInterface(ServiceManager.getService(Context.MEDIA_ROUTER_SERVICE));
        this.mMediaRouterService = asInterface;
        this.mMediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        this.mHandler = new Handler(context.getMainLooper());
        Client client = new Client();
        this.mClient = client;
        try {
            asInterface.registerManager(client, context.getPackageName());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void registerCallback(Executor executor, Callback callback) {
        Objects.requireNonNull(executor, "executor must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        CallbackRecord callbackRecord = new CallbackRecord(executor, callback);
        if (!this.mCallbackRecords.addIfAbsent(callbackRecord)) {
            Log.m104w(TAG, "Ignoring to register the same callback twice.");
        }
    }

    public void unregisterCallback(Callback callback) {
        Objects.requireNonNull(callback, "callback must not be null");
        if (!this.mCallbackRecords.remove(new CallbackRecord(null, callback))) {
            Log.m104w(TAG, "unregisterCallback: Ignore unknown callback. " + callback);
        }
    }

    public void registerScanRequest() {
        if (this.mScanRequestCount.getAndIncrement() == 0) {
            try {
                this.mMediaRouterService.startScan(this.mClient);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterScanRequest() {
        if (this.mScanRequestCount.updateAndGet(new IntUnaryOperator() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda5
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i) {
                return MediaRouter2Manager.lambda$unregisterScanRequest$0(i);
            }
        }) == 0) {
            try {
                this.mMediaRouterService.stopScan(this.mClient);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$unregisterScanRequest$0(int count) {
        if (count == 0) {
            throw new IllegalStateException("No active scan requests to unregister.");
        }
        return count - 1;
    }

    public MediaController getMediaControllerForRoutingSession(RoutingSessionInfo sessionInfo) {
        for (MediaController controller : this.mMediaSessionManager.getActiveSessions(null)) {
            if (areSessionsMatched(controller, sessionInfo)) {
                return controller;
            }
        }
        return null;
    }

    public List<MediaRoute2Info> getAvailableRoutes(String packageName) {
        Objects.requireNonNull(packageName, "packageName must not be null");
        List<RoutingSessionInfo> sessions = getRoutingSessions(packageName);
        return getAvailableRoutes(sessions.get(sessions.size() - 1));
    }

    public List<MediaRoute2Info> getTransferableRoutes(String packageName) {
        Objects.requireNonNull(packageName, "packageName must not be null");
        List<RoutingSessionInfo> sessions = getRoutingSessions(packageName);
        return getTransferableRoutes(sessions.get(sessions.size() - 1));
    }

    public List<MediaRoute2Info> getAvailableRoutes(RoutingSessionInfo sessionInfo) {
        return getFilteredRoutes(sessionInfo, true, null);
    }

    public List<MediaRoute2Info> getTransferableRoutes(final RoutingSessionInfo sessionInfo) {
        return getFilteredRoutes(sessionInfo, false, new Predicate() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return MediaRouter2Manager.lambda$getTransferableRoutes$1(RoutingSessionInfo.this, (MediaRoute2Info) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getTransferableRoutes$1(RoutingSessionInfo sessionInfo, MediaRoute2Info route) {
        return sessionInfo.isSystemSession() ^ route.isSystemRoute();
    }

    private List<MediaRoute2Info> getSortedRoutes(RouteDiscoveryPreference preference) {
        ArrayList<MediaRoute2Info> routes;
        List<MediaRoute2Info> copyOf;
        if (!preference.shouldRemoveDuplicates()) {
            synchronized (this.mRoutesLock) {
                copyOf = List.copyOf(this.mRoutes.values());
            }
            return copyOf;
        }
        final Map<String, Integer> packagePriority = new ArrayMap<>();
        int count = preference.getDeduplicationPackageOrder().size();
        for (int i = 0; i < count; i++) {
            packagePriority.put(preference.getDeduplicationPackageOrder().get(i), Integer.valueOf(count - i));
        }
        synchronized (this.mRoutesLock) {
            routes = new ArrayList<>(this.mRoutes.values());
        }
        routes.sort(Comparator.comparingInt(new ToIntFunction() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda9
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return MediaRouter2Manager.lambda$getSortedRoutes$2(packagePriority, (MediaRoute2Info) obj);
            }
        }));
        return routes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$getSortedRoutes$2(Map packagePriority, MediaRoute2Info r) {
        return -((Integer) packagePriority.getOrDefault(r.getPackageName(), 0)).intValue();
    }

    private List<MediaRoute2Info> getFilteredRoutes(RoutingSessionInfo sessionInfo, boolean includeSelectedRoutes, Predicate<MediaRoute2Info> additionalFilter) {
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        List<MediaRoute2Info> routes = new ArrayList<>();
        Set<String> deduplicationIdSet = new ArraySet<>();
        String packageName = sessionInfo.getClientPackageName();
        RouteDiscoveryPreference discoveryPreference = this.mDiscoveryPreferenceMap.getOrDefault(packageName, RouteDiscoveryPreference.EMPTY);
        for (MediaRoute2Info route : getSortedRoutes(discoveryPreference)) {
            if (route.isVisibleTo(packageName)) {
                boolean transferableRoutesContainRoute = sessionInfo.getTransferableRoutes().contains(route.getId());
                boolean selectedRoutesContainRoute = sessionInfo.getSelectedRoutes().contains(route.getId());
                if (transferableRoutesContainRoute || (includeSelectedRoutes && selectedRoutesContainRoute)) {
                    routes.add(route);
                } else if (route.hasAnyFeatures(discoveryPreference.getPreferredFeatures()) && (discoveryPreference.getAllowedPackages().isEmpty() || (route.getPackageName() != null && discoveryPreference.getAllowedPackages().contains(route.getPackageName())))) {
                    if (additionalFilter == null || additionalFilter.test(route)) {
                        if (discoveryPreference.shouldRemoveDuplicates()) {
                            if (Collections.disjoint(deduplicationIdSet, route.getDeduplicationIds())) {
                                deduplicationIdSet.addAll(route.getDeduplicationIds());
                            }
                        }
                        routes.add(route);
                    }
                }
            }
        }
        return routes;
    }

    public RouteDiscoveryPreference getDiscoveryPreference(String packageName) {
        Objects.requireNonNull(packageName, "packageName must not be null");
        return this.mDiscoveryPreferenceMap.getOrDefault(packageName, RouteDiscoveryPreference.EMPTY);
    }

    public RouteListingPreference getRouteListingPreference(String packageName) {
        Preconditions.checkArgument(!TextUtils.isEmpty(packageName));
        return this.mPackageToRouteListingPreferenceMap.get(packageName);
    }

    public RoutingSessionInfo getSystemRoutingSession(String packageName) {
        try {
            return this.mMediaRouterService.getSystemSessionInfoForPackage(this.mClient, packageName);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public RoutingSessionInfo getRoutingSessionForMediaController(MediaController mediaController) {
        MediaController.PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
        if (playbackInfo == null) {
            return null;
        }
        if (playbackInfo.getPlaybackType() == 1) {
            return getSystemRoutingSession(mediaController.getPackageName());
        }
        for (RoutingSessionInfo sessionInfo : getRemoteSessions()) {
            if (areSessionsMatched(mediaController, sessionInfo)) {
                return sessionInfo;
            }
        }
        return null;
    }

    public List<RoutingSessionInfo> getRoutingSessions(String packageName) {
        Objects.requireNonNull(packageName, "packageName must not be null");
        List<RoutingSessionInfo> sessions = new ArrayList<>();
        sessions.add(getSystemRoutingSession(packageName));
        for (RoutingSessionInfo sessionInfo : getRemoteSessions()) {
            if (TextUtils.equals(sessionInfo.getClientPackageName(), packageName)) {
                sessions.add(sessionInfo);
            }
        }
        return sessions;
    }

    public List<RoutingSessionInfo> getRemoteSessions() {
        try {
            return this.mMediaRouterService.getRemoteSessions(this.mClient);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public List<MediaRoute2Info> getAllRoutes() {
        List<MediaRoute2Info> routes = new ArrayList<>();
        synchronized (this.mRoutesLock) {
            routes.addAll(this.mRoutes.values());
        }
        return routes;
    }

    public void transfer(String packageName, MediaRoute2Info route) {
        Objects.requireNonNull(packageName, "packageName must not be null");
        Objects.requireNonNull(route, "route must not be null");
        List<RoutingSessionInfo> sessionInfos = getRoutingSessions(packageName);
        RoutingSessionInfo targetSession = sessionInfos.get(sessionInfos.size() - 1);
        transfer(targetSession, route);
    }

    public void transfer(RoutingSessionInfo sessionInfo, MediaRoute2Info route) {
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        Objects.requireNonNull(route, "route must not be null");
        Log.m106v(TAG, "Transferring routing session. session= " + sessionInfo + ", route=" + route);
        synchronized (this.mRoutesLock) {
            if (!this.mRoutes.containsKey(route.getId())) {
                Log.m104w(TAG, "transfer: Ignoring an unknown route id=" + route.getId());
                notifyTransferFailed(sessionInfo, route);
            } else if (sessionInfo.getTransferableRoutes().contains(route.getId())) {
                transferToRoute(sessionInfo, route);
            } else {
                requestCreateSession(sessionInfo, route);
            }
        }
    }

    public void setRouteVolume(MediaRoute2Info route, int volume) {
        Objects.requireNonNull(route, "route must not be null");
        if (route.getVolumeHandling() == 0) {
            Log.m104w(TAG, "setRouteVolume: the route has fixed volume. Ignoring.");
        } else if (volume < 0 || volume > route.getVolumeMax()) {
            Log.m104w(TAG, "setRouteVolume: the target volume is out of range. Ignoring");
        } else {
            try {
                int requestId = this.mNextRequestId.getAndIncrement();
                this.mMediaRouterService.setRouteVolumeWithManager(this.mClient, requestId, route, volume);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    public void setSessionVolume(RoutingSessionInfo sessionInfo, int volume) {
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        if (sessionInfo.getVolumeHandling() == 0) {
            Log.m104w(TAG, "setSessionVolume: the route has fixed volume. Ignoring.");
        } else if (volume < 0 || volume > sessionInfo.getVolumeMax()) {
            Log.m104w(TAG, "setSessionVolume: the target volume is out of range. Ignoring");
        } else {
            try {
                int requestId = this.mNextRequestId.getAndIncrement();
                this.mMediaRouterService.setSessionVolumeWithManager(this.mClient, requestId, sessionInfo.getId(), volume);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateRoutesOnHandler(List<MediaRoute2Info> routes) {
        synchronized (this.mRoutesLock) {
            this.mRoutes.clear();
            for (MediaRoute2Info route : routes) {
                this.mRoutes.put(route.getId(), route);
            }
        }
        notifyRoutesUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createSessionOnHandler(int requestId, RoutingSessionInfo sessionInfo) {
        TransferRequest matchingRequest = null;
        Iterator<TransferRequest> it = this.mTransferRequests.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TransferRequest request = it.next();
            if (request.mRequestId == requestId) {
                matchingRequest = request;
                break;
            }
        }
        if (matchingRequest == null) {
            return;
        }
        this.mTransferRequests.remove(matchingRequest);
        MediaRoute2Info requestedRoute = matchingRequest.mTargetRoute;
        if (sessionInfo == null) {
            notifyTransferFailed(matchingRequest.mOldSessionInfo, requestedRoute);
        } else if (!sessionInfo.getSelectedRoutes().contains(requestedRoute.getId())) {
            Log.m104w(TAG, "The session does not contain the requested route. (requestedRouteId=" + requestedRoute.getId() + ", actualRoutes=" + sessionInfo.getSelectedRoutes() + NavigationBarInflaterView.KEY_CODE_END);
            notifyTransferFailed(matchingRequest.mOldSessionInfo, requestedRoute);
        } else if (!TextUtils.equals(requestedRoute.getProviderId(), sessionInfo.getProviderId())) {
            Log.m104w(TAG, "The session's provider ID does not match the requested route's. (requested route's providerId=" + requestedRoute.getProviderId() + ", actual providerId=" + sessionInfo.getProviderId() + NavigationBarInflaterView.KEY_CODE_END);
            notifyTransferFailed(matchingRequest.mOldSessionInfo, requestedRoute);
        } else {
            notifyTransferred(matchingRequest.mOldSessionInfo, sessionInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleFailureOnHandler(int requestId, int reason) {
        TransferRequest matchingRequest = null;
        Iterator<TransferRequest> it = this.mTransferRequests.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TransferRequest request = it.next();
            if (request.mRequestId == requestId) {
                matchingRequest = request;
                break;
            }
        }
        if (matchingRequest != null) {
            this.mTransferRequests.remove(matchingRequest);
            notifyTransferFailed(matchingRequest.mOldSessionInfo, matchingRequest.mTargetRoute);
            return;
        }
        notifyRequestFailed(reason);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleSessionsUpdatedOnHandler(RoutingSessionInfo sessionInfo) {
        Iterator<TransferRequest> it = this.mTransferRequests.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TransferRequest request = it.next();
            String sessionId = request.mOldSessionInfo.getId();
            if (TextUtils.equals(sessionId, sessionInfo.getId()) && sessionInfo.getSelectedRoutes().contains(request.mTargetRoute.getId())) {
                this.mTransferRequests.remove(request);
                notifyTransferred(request.mOldSessionInfo, sessionInfo);
                break;
            }
        }
        notifySessionUpdated(sessionInfo);
    }

    private void notifyRoutesUpdated() {
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onRoutesUpdated();
                }
            });
        }
    }

    void notifySessionUpdated(final RoutingSessionInfo sessionInfo) {
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onSessionUpdated(sessionInfo);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifySessionReleased(final RoutingSessionInfo session) {
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onSessionReleased(session);
                }
            });
        }
    }

    void notifyRequestFailed(final int reason) {
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onRequestFailed(reason);
                }
            });
        }
    }

    void notifyTransferred(final RoutingSessionInfo oldSession, final RoutingSessionInfo newSession) {
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onTransferred(oldSession, newSession);
                }
            });
        }
    }

    void notifyTransferFailed(final RoutingSessionInfo sessionInfo, final MediaRoute2Info route) {
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onTransferFailed(sessionInfo, route);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDiscoveryPreference(final String packageName, final RouteDiscoveryPreference preference) {
        if (preference == null) {
            this.mDiscoveryPreferenceMap.remove(packageName);
            return;
        }
        RouteDiscoveryPreference prevPreference = this.mDiscoveryPreferenceMap.put(packageName, preference);
        if (Objects.equals(preference, prevPreference)) {
            return;
        }
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onDiscoveryPreferenceChanged(packageName, preference);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRouteListingPreference(final String packageName, final RouteListingPreference routeListingPreference) {
        RouteListingPreference oldRouteListingPreference;
        if (routeListingPreference == null) {
            oldRouteListingPreference = this.mPackageToRouteListingPreferenceMap.remove(packageName);
        } else {
            oldRouteListingPreference = this.mPackageToRouteListingPreferenceMap.put(packageName, routeListingPreference);
        }
        if (Objects.equals(oldRouteListingPreference, routeListingPreference)) {
            return;
        }
        Iterator<CallbackRecord> it = this.mCallbackRecords.iterator();
        while (it.hasNext()) {
            final CallbackRecord record = it.next();
            record.mExecutor.execute(new Runnable() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter2Manager.CallbackRecord.this.mCallback.onRouteListingPreferenceUpdated(packageName, routeListingPreference);
                }
            });
        }
    }

    public List<MediaRoute2Info> getSelectedRoutes(RoutingSessionInfo sessionInfo) {
        List<MediaRoute2Info> list;
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        synchronized (this.mRoutesLock) {
            Stream<String> stream = sessionInfo.getSelectedRoutes().stream();
            Map<String, MediaRoute2Info> map = this.mRoutes;
            Objects.requireNonNull(map);
            list = (List) stream.map(new MediaRouter2$RoutingController$$ExternalSyntheticLambda2(map)).filter(new MediaRouter2$RoutingController$$ExternalSyntheticLambda3()).collect(Collectors.toList());
        }
        return list;
    }

    public List<MediaRoute2Info> getSelectableRoutes(RoutingSessionInfo sessionInfo) {
        List<MediaRoute2Info> list;
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        final List<String> selectedRouteIds = sessionInfo.getSelectedRoutes();
        synchronized (this.mRoutesLock) {
            Stream<String> filter = sessionInfo.getSelectableRoutes().stream().filter(new Predicate() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return MediaRouter2Manager.lambda$getSelectableRoutes$11(selectedRouteIds, (String) obj);
                }
            });
            Map<String, MediaRoute2Info> map = this.mRoutes;
            Objects.requireNonNull(map);
            list = (List) filter.map(new MediaRouter2$RoutingController$$ExternalSyntheticLambda2(map)).filter(new MediaRouter2$RoutingController$$ExternalSyntheticLambda3()).collect(Collectors.toList());
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getSelectableRoutes$11(List selectedRouteIds, String routeId) {
        return !selectedRouteIds.contains(routeId);
    }

    public List<MediaRoute2Info> getDeselectableRoutes(RoutingSessionInfo sessionInfo) {
        List<MediaRoute2Info> list;
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        final List<String> selectedRouteIds = sessionInfo.getSelectedRoutes();
        synchronized (this.mRoutesLock) {
            Stream<String> filter = sessionInfo.getDeselectableRoutes().stream().filter(new Predicate() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda12
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean contains;
                    contains = selectedRouteIds.contains((String) obj);
                    return contains;
                }
            });
            Map<String, MediaRoute2Info> map = this.mRoutes;
            Objects.requireNonNull(map);
            list = (List) filter.map(new MediaRouter2$RoutingController$$ExternalSyntheticLambda2(map)).filter(new MediaRouter2$RoutingController$$ExternalSyntheticLambda3()).collect(Collectors.toList());
        }
        return list;
    }

    public void selectRoute(RoutingSessionInfo sessionInfo, MediaRoute2Info route) {
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        Objects.requireNonNull(route, "route must not be null");
        if (sessionInfo.getSelectedRoutes().contains(route.getId())) {
            Log.m104w(TAG, "Ignoring selecting a route that is already selected. route=" + route);
        } else if (!sessionInfo.getSelectableRoutes().contains(route.getId())) {
            Log.m104w(TAG, "Ignoring selecting a non-selectable route=" + route);
        } else {
            try {
                int requestId = this.mNextRequestId.getAndIncrement();
                this.mMediaRouterService.selectRouteWithManager(this.mClient, requestId, sessionInfo.getId(), route);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    public void deselectRoute(RoutingSessionInfo sessionInfo, MediaRoute2Info route) {
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        Objects.requireNonNull(route, "route must not be null");
        if (!sessionInfo.getSelectedRoutes().contains(route.getId())) {
            Log.m104w(TAG, "Ignoring deselecting a route that is not selected. route=" + route);
        } else if (!sessionInfo.getDeselectableRoutes().contains(route.getId())) {
            Log.m104w(TAG, "Ignoring deselecting a non-deselectable route=" + route);
        } else {
            try {
                int requestId = this.mNextRequestId.getAndIncrement();
                this.mMediaRouterService.deselectRouteWithManager(this.mClient, requestId, sessionInfo.getId(), route);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    public void releaseSession(RoutingSessionInfo sessionInfo) {
        Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
        try {
            int requestId = this.mNextRequestId.getAndIncrement();
            this.mMediaRouterService.releaseSessionWithManager(this.mClient, requestId, sessionInfo.getId());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    private void transferToRoute(RoutingSessionInfo session, MediaRoute2Info route) {
        int requestId = createTransferRequest(session, route);
        try {
            this.mMediaRouterService.transferToRouteWithManager(this.mClient, requestId, session.getId(), route);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    private void requestCreateSession(RoutingSessionInfo oldSession, MediaRoute2Info route) {
        if (TextUtils.isEmpty(oldSession.getClientPackageName())) {
            Log.m104w(TAG, "requestCreateSession: Can't create a session without package name.");
            notifyTransferFailed(oldSession, route);
            return;
        }
        int requestId = createTransferRequest(oldSession, route);
        try {
            this.mMediaRouterService.requestCreateSessionWithManager(this.mClient, requestId, oldSession, route);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    private int createTransferRequest(RoutingSessionInfo session, MediaRoute2Info route) {
        int requestId = this.mNextRequestId.getAndIncrement();
        TransferRequest transferRequest = new TransferRequest(requestId, session, route);
        this.mTransferRequests.add(transferRequest);
        Message timeoutMessage = PooledLambda.obtainMessage(new BiConsumer() { // from class: android.media.MediaRouter2Manager$$ExternalSyntheticLambda13
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((MediaRouter2Manager) obj).handleTransferTimeout((MediaRouter2Manager.TransferRequest) obj2);
            }
        }, this, transferRequest);
        this.mHandler.sendMessageDelayed(timeoutMessage, JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
        return requestId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTransferTimeout(TransferRequest request) {
        boolean removed = this.mTransferRequests.remove(request);
        if (removed) {
            notifyTransferFailed(request.mOldSessionInfo, request.mTargetRoute);
        }
    }

    private boolean areSessionsMatched(MediaController mediaController, RoutingSessionInfo sessionInfo) {
        String volumeControlId;
        MediaController.PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
        if (playbackInfo == null || (volumeControlId = playbackInfo.getVolumeControlId()) == null) {
            return false;
        }
        if (TextUtils.equals(volumeControlId, sessionInfo.getId())) {
            return true;
        }
        return TextUtils.equals(volumeControlId, sessionInfo.getOriginalId()) && TextUtils.equals(mediaController.getPackageName(), sessionInfo.getOwnerPackageName());
    }

    /* loaded from: classes2.dex */
    public interface Callback {
        default void onRoutesUpdated() {
        }

        default void onSessionUpdated(RoutingSessionInfo session) {
        }

        default void onSessionReleased(RoutingSessionInfo session) {
        }

        default void onTransferred(RoutingSessionInfo oldSession, RoutingSessionInfo newSession) {
        }

        default void onTransferFailed(RoutingSessionInfo session, MediaRoute2Info route) {
        }

        default void onPreferredFeaturesChanged(String packageName, List<String> preferredFeatures) {
        }

        default void onDiscoveryPreferenceChanged(String packageName, RouteDiscoveryPreference discoveryPreference) {
            onPreferredFeaturesChanged(packageName, discoveryPreference.getPreferredFeatures());
        }

        default void onRouteListingPreferenceUpdated(String packageName, RouteListingPreference routeListingPreference) {
        }

        default void onRequestFailed(int reason) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public final class CallbackRecord {
        public final Callback mCallback;
        public final Executor mExecutor;

        CallbackRecord(Executor executor, Callback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return (obj instanceof CallbackRecord) && this.mCallback == ((CallbackRecord) obj).mCallback;
        }

        public int hashCode() {
            return this.mCallback.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class TransferRequest {
        public final RoutingSessionInfo mOldSessionInfo;
        public final int mRequestId;
        public final MediaRoute2Info mTargetRoute;

        TransferRequest(int requestId, RoutingSessionInfo oldSessionInfo, MediaRoute2Info targetRoute) {
            this.mRequestId = requestId;
            this.mOldSessionInfo = oldSessionInfo;
            this.mTargetRoute = targetRoute;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class Client extends IMediaRouter2Manager.Stub {
        Client() {
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifySessionCreated(int requestId, RoutingSessionInfo session) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((MediaRouter2Manager) obj).createSessionOnHandler(((Integer) obj2).intValue(), (RoutingSessionInfo) obj3);
                }
            }, MediaRouter2Manager.this, Integer.valueOf(requestId), session));
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifySessionUpdated(RoutingSessionInfo session) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda6
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((MediaRouter2Manager) obj).handleSessionsUpdatedOnHandler((RoutingSessionInfo) obj2);
                }
            }, MediaRouter2Manager.this, session));
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifySessionReleased(RoutingSessionInfo session) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda2
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((MediaRouter2Manager) obj).notifySessionReleased((RoutingSessionInfo) obj2);
                }
            }, MediaRouter2Manager.this, session));
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifyRequestFailed(int requestId, int reason) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((MediaRouter2Manager) obj).handleFailureOnHandler(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
                }
            }, MediaRouter2Manager.this, Integer.valueOf(requestId), Integer.valueOf(reason)));
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifyDiscoveryPreferenceChanged(String packageName, RouteDiscoveryPreference discoveryPreference) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((MediaRouter2Manager) obj).updateDiscoveryPreference((String) obj2, (RouteDiscoveryPreference) obj3);
                }
            }, MediaRouter2Manager.this, packageName, discoveryPreference));
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifyRouteListingPreferenceChange(String packageName, RouteListingPreference routeListingPreference) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda4
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((MediaRouter2Manager) obj).updateRouteListingPreference((String) obj2, (RouteListingPreference) obj3);
                }
            }, MediaRouter2Manager.this, packageName, routeListingPreference));
        }

        @Override // android.media.IMediaRouter2Manager
        public void notifyRoutesUpdated(List<MediaRoute2Info> routes) {
            MediaRouter2Manager.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.media.MediaRouter2Manager$Client$$ExternalSyntheticLambda5
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((MediaRouter2Manager) obj).updateRoutesOnHandler((List) obj2);
                }
            }, MediaRouter2Manager.this, routes));
        }
    }
}
