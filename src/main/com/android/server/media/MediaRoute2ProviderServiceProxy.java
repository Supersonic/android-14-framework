package com.android.server.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.IMediaRoute2ProviderService;
import android.media.IMediaRoute2ProviderServiceCallback;
import android.media.MediaRoute2ProviderInfo;
import android.media.RouteDiscoveryPreference;
import android.media.RoutingSessionInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.media.MediaRoute2Provider;
import com.android.server.media.MediaRoute2ProviderServiceProxy;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public final class MediaRoute2ProviderServiceProxy extends MediaRoute2Provider implements ServiceConnection {
    public static final boolean DEBUG = Log.isLoggable("MR2ProviderSvcProxy", 3);
    public Connection mActiveConnection;
    public boolean mBound;
    public boolean mConnectionReady;
    public final Context mContext;
    public final Handler mHandler;
    public boolean mIsManagerScanning;
    public RouteDiscoveryPreference mLastDiscoveryPreference;
    @GuardedBy({"mLock"})
    public final List<RoutingSessionInfo> mReleasingSessions;
    public boolean mRunning;
    public final int mUserId;

    public MediaRoute2ProviderServiceProxy(Context context, ComponentName componentName, int i) {
        super(componentName);
        this.mLastDiscoveryPreference = null;
        this.mReleasingSessions = new ArrayList();
        Objects.requireNonNull(context, "Context must not be null.");
        this.mContext = context;
        this.mUserId = i;
        this.mHandler = new Handler(Looper.myLooper());
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + getDebugString());
    }

    public void setManagerScanning(boolean z) {
        if (this.mIsManagerScanning != z) {
            this.mIsManagerScanning = z;
            updateBinding();
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void requestCreateSession(long j, String str, String str2, Bundle bundle) {
        if (this.mConnectionReady) {
            this.mActiveConnection.requestCreateSession(j, str, str2, bundle);
            updateBinding();
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void releaseSession(long j, String str) {
        if (this.mConnectionReady) {
            this.mActiveConnection.releaseSession(j, str);
            updateBinding();
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void updateDiscoveryPreference(RouteDiscoveryPreference routeDiscoveryPreference) {
        this.mLastDiscoveryPreference = routeDiscoveryPreference;
        if (this.mConnectionReady) {
            this.mActiveConnection.updateDiscoveryPreference(routeDiscoveryPreference);
        }
        updateBinding();
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void selectRoute(long j, String str, String str2) {
        if (this.mConnectionReady) {
            this.mActiveConnection.selectRoute(j, str, str2);
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void deselectRoute(long j, String str, String str2) {
        if (this.mConnectionReady) {
            this.mActiveConnection.deselectRoute(j, str, str2);
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void transferToRoute(long j, String str, String str2) {
        if (this.mConnectionReady) {
            this.mActiveConnection.transferToRoute(j, str, str2);
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void setRouteVolume(long j, String str, int i) {
        if (this.mConnectionReady) {
            this.mActiveConnection.setRouteVolume(j, str, i);
            updateBinding();
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void setSessionVolume(long j, String str, int i) {
        if (this.mConnectionReady) {
            this.mActiveConnection.setSessionVolume(j, str, i);
            updateBinding();
        }
    }

    @Override // com.android.server.media.MediaRoute2Provider
    public void prepareReleaseSession(String str) {
        synchronized (this.mLock) {
            Iterator<RoutingSessionInfo> it = this.mSessionInfos.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                RoutingSessionInfo next = it.next();
                if (TextUtils.equals(next.getId(), str)) {
                    this.mSessionInfos.remove(next);
                    this.mReleasingSessions.add(next);
                    break;
                }
            }
        }
    }

    public boolean hasComponentName(String str, String str2) {
        return this.mComponentName.getPackageName().equals(str) && this.mComponentName.getClassName().equals(str2);
    }

    public void start() {
        if (this.mRunning) {
            return;
        }
        if (DEBUG) {
            Slog.d("MR2ProviderSvcProxy", this + ": Starting");
        }
        this.mRunning = true;
        updateBinding();
    }

    public void stop() {
        if (this.mRunning) {
            if (DEBUG) {
                Slog.d("MR2ProviderSvcProxy", this + ": Stopping");
            }
            this.mRunning = false;
            updateBinding();
        }
    }

    public void rebindIfDisconnected() {
        if (this.mActiveConnection == null && shouldBind()) {
            unbind();
            bind();
        }
    }

    public final void updateBinding() {
        if (shouldBind()) {
            bind();
        } else {
            unbind();
        }
    }

    public final boolean shouldBind() {
        if (this.mRunning) {
            RouteDiscoveryPreference routeDiscoveryPreference = this.mLastDiscoveryPreference;
            return ((routeDiscoveryPreference == null || routeDiscoveryPreference.getPreferredFeatures().isEmpty()) && getSessionInfos().isEmpty() && !this.mIsManagerScanning) ? false : true;
        }
        return false;
    }

    public final void bind() {
        if (this.mBound) {
            return;
        }
        boolean z = DEBUG;
        if (z) {
            Slog.d("MR2ProviderSvcProxy", this + ": Binding");
        }
        Intent intent = new Intent("android.media.MediaRoute2ProviderService");
        intent.setComponent(this.mComponentName);
        try {
            boolean bindServiceAsUser = this.mContext.bindServiceAsUser(intent, this, 67108865, new UserHandle(this.mUserId));
            this.mBound = bindServiceAsUser;
            if (bindServiceAsUser || !z) {
                return;
            }
            Slog.d("MR2ProviderSvcProxy", this + ": Bind failed");
        } catch (SecurityException e) {
            if (DEBUG) {
                Slog.d("MR2ProviderSvcProxy", this + ": Bind failed", e);
            }
        }
    }

    public final void unbind() {
        if (this.mBound) {
            if (DEBUG) {
                Slog.d("MR2ProviderSvcProxy", this + ": Unbinding");
            }
            this.mBound = false;
            disconnect();
            this.mContext.unbindService(this);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        boolean z = DEBUG;
        if (z) {
            Slog.d("MR2ProviderSvcProxy", this + ": Connected");
        }
        if (this.mBound) {
            disconnect();
            IMediaRoute2ProviderService asInterface = IMediaRoute2ProviderService.Stub.asInterface(iBinder);
            if (asInterface != null) {
                Connection connection = new Connection(asInterface);
                if (connection.register()) {
                    this.mActiveConnection = connection;
                    return;
                } else if (z) {
                    Slog.d("MR2ProviderSvcProxy", this + ": Registration failed");
                    return;
                } else {
                    return;
                }
            }
            Slog.e("MR2ProviderSvcProxy", this + ": Service returned invalid binder");
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        if (DEBUG) {
            Slog.d("MR2ProviderSvcProxy", this + ": Service disconnected");
        }
        disconnect();
    }

    @Override // android.content.ServiceConnection
    public void onBindingDied(ComponentName componentName) {
        if (DEBUG) {
            Slog.d("MR2ProviderSvcProxy", this + ": Service binding died");
        }
        unbind();
        if (shouldBind()) {
            bind();
        }
    }

    public final void onConnectionReady(Connection connection) {
        if (this.mActiveConnection == connection) {
            this.mConnectionReady = true;
            RouteDiscoveryPreference routeDiscoveryPreference = this.mLastDiscoveryPreference;
            if (routeDiscoveryPreference != null) {
                updateDiscoveryPreference(routeDiscoveryPreference);
            }
        }
    }

    public final void onConnectionDied(Connection connection) {
        if (this.mActiveConnection == connection) {
            if (DEBUG) {
                Slog.d("MR2ProviderSvcProxy", this + ": Service connection died");
            }
            disconnect();
        }
    }

    public final void onProviderUpdated(Connection connection, MediaRoute2ProviderInfo mediaRoute2ProviderInfo) {
        if (this.mActiveConnection != connection) {
            return;
        }
        if (DEBUG) {
            Slog.d("MR2ProviderSvcProxy", this + ": updated");
        }
        setAndNotifyProviderState(mediaRoute2ProviderInfo);
    }

    public final void onSessionCreated(Connection connection, long j, RoutingSessionInfo routingSessionInfo) {
        if (this.mActiveConnection != connection) {
            return;
        }
        if (routingSessionInfo == null) {
            Slog.w("MR2ProviderSvcProxy", "onSessionCreated: Ignoring null session sent from " + this.mComponentName);
            return;
        }
        RoutingSessionInfo assignProviderIdForSession = assignProviderIdForSession(routingSessionInfo);
        final String id = assignProviderIdForSession.getId();
        synchronized (this.mLock) {
            if (!this.mSessionInfos.stream().anyMatch(new Predicate() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onSessionCreated$0;
                    lambda$onSessionCreated$0 = MediaRoute2ProviderServiceProxy.lambda$onSessionCreated$0(id, (RoutingSessionInfo) obj);
                    return lambda$onSessionCreated$0;
                }
            }) && !this.mReleasingSessions.stream().anyMatch(new Predicate() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onSessionCreated$1;
                    lambda$onSessionCreated$1 = MediaRoute2ProviderServiceProxy.lambda$onSessionCreated$1(id, (RoutingSessionInfo) obj);
                    return lambda$onSessionCreated$1;
                }
            })) {
                this.mSessionInfos.add(assignProviderIdForSession);
                this.mCallback.onSessionCreated(this, j, assignProviderIdForSession);
                return;
            }
            Slog.w("MR2ProviderSvcProxy", "onSessionCreated: Duplicate session already exists. Ignoring.");
        }
    }

    public static /* synthetic */ boolean lambda$onSessionCreated$0(String str, RoutingSessionInfo routingSessionInfo) {
        return TextUtils.equals(routingSessionInfo.getId(), str);
    }

    public static /* synthetic */ boolean lambda$onSessionCreated$1(String str, RoutingSessionInfo routingSessionInfo) {
        return TextUtils.equals(routingSessionInfo.getId(), str);
    }

    public final int findSessionByIdLocked(RoutingSessionInfo routingSessionInfo) {
        for (int i = 0; i < this.mSessionInfos.size(); i++) {
            if (TextUtils.equals(this.mSessionInfos.get(i).getId(), routingSessionInfo.getId())) {
                return i;
            }
        }
        return -1;
    }

    public final void onSessionsUpdated(Connection connection, List<RoutingSessionInfo> list) {
        if (this.mActiveConnection != connection) {
            return;
        }
        synchronized (this.mLock) {
            int i = 0;
            for (RoutingSessionInfo routingSessionInfo : list) {
                if (routingSessionInfo != null) {
                    RoutingSessionInfo assignProviderIdForSession = assignProviderIdForSession(routingSessionInfo);
                    int findSessionByIdLocked = findSessionByIdLocked(assignProviderIdForSession);
                    if (findSessionByIdLocked < 0) {
                        this.mSessionInfos.add(i, assignProviderIdForSession);
                        dispatchSessionCreated(0L, assignProviderIdForSession);
                        i++;
                    } else if (findSessionByIdLocked < i) {
                        Slog.w("MR2ProviderSvcProxy", "Ignoring duplicate session ID: " + assignProviderIdForSession.getId());
                    } else {
                        this.mSessionInfos.set(findSessionByIdLocked, assignProviderIdForSession);
                        Collections.swap(this.mSessionInfos, findSessionByIdLocked, i);
                        dispatchSessionUpdated(assignProviderIdForSession);
                        i++;
                    }
                }
            }
            for (int size = this.mSessionInfos.size() - 1; size >= i; size--) {
                dispatchSessionReleased(this.mSessionInfos.remove(size));
            }
        }
    }

    public final void onSessionReleased(Connection connection, RoutingSessionInfo routingSessionInfo) {
        boolean z;
        if (this.mActiveConnection != connection) {
            return;
        }
        if (routingSessionInfo == null) {
            Slog.w("MR2ProviderSvcProxy", "onSessionReleased: Ignoring null session sent from " + this.mComponentName);
            return;
        }
        RoutingSessionInfo assignProviderIdForSession = assignProviderIdForSession(routingSessionInfo);
        synchronized (this.mLock) {
            Iterator<RoutingSessionInfo> it = this.mSessionInfos.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                RoutingSessionInfo next = it.next();
                if (TextUtils.equals(next.getId(), assignProviderIdForSession.getId())) {
                    this.mSessionInfos.remove(next);
                    z = true;
                    break;
                }
            }
            if (!z) {
                for (RoutingSessionInfo routingSessionInfo2 : this.mReleasingSessions) {
                    if (TextUtils.equals(routingSessionInfo2.getId(), assignProviderIdForSession.getId())) {
                        this.mReleasingSessions.remove(routingSessionInfo2);
                        return;
                    }
                }
            }
            if (!z) {
                Slog.w("MR2ProviderSvcProxy", "onSessionReleased: Matching session info not found");
            } else {
                this.mCallback.onSessionReleased(this, assignProviderIdForSession);
            }
        }
    }

    public final void dispatchSessionCreated(long j, RoutingSessionInfo routingSessionInfo) {
        Handler handler = this.mHandler;
        final MediaRoute2Provider.Callback callback = this.mCallback;
        Objects.requireNonNull(callback);
        handler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$$ExternalSyntheticLambda4
            public final void accept(Object obj, Object obj2, Object obj3) {
                MediaRoute2Provider.Callback.this.onSessionCreated((MediaRoute2ProviderServiceProxy) obj, ((Long) obj2).longValue(), (RoutingSessionInfo) obj3);
            }
        }, this, Long.valueOf(j), routingSessionInfo));
    }

    public final void dispatchSessionUpdated(RoutingSessionInfo routingSessionInfo) {
        Handler handler = this.mHandler;
        final MediaRoute2Provider.Callback callback = this.mCallback;
        Objects.requireNonNull(callback);
        handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                MediaRoute2Provider.Callback.this.onSessionUpdated((MediaRoute2ProviderServiceProxy) obj, (RoutingSessionInfo) obj2);
            }
        }, this, routingSessionInfo));
    }

    public final void dispatchSessionReleased(RoutingSessionInfo routingSessionInfo) {
        Handler handler = this.mHandler;
        final MediaRoute2Provider.Callback callback = this.mCallback;
        Objects.requireNonNull(callback);
        handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$$ExternalSyntheticLambda3
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                MediaRoute2Provider.Callback.this.onSessionReleased((MediaRoute2ProviderServiceProxy) obj, (RoutingSessionInfo) obj2);
            }
        }, this, routingSessionInfo));
    }

    public final RoutingSessionInfo assignProviderIdForSession(RoutingSessionInfo routingSessionInfo) {
        return new RoutingSessionInfo.Builder(routingSessionInfo).setOwnerPackageName(this.mComponentName.getPackageName()).setProviderId(getUniqueId()).build();
    }

    public final void onRequestFailed(Connection connection, long j, int i) {
        if (this.mActiveConnection != connection) {
            return;
        }
        if (j == 0) {
            Slog.w("MR2ProviderSvcProxy", "onRequestFailed: Ignoring requestId REQUEST_ID_NONE");
        } else {
            this.mCallback.onRequestFailed(this, j, i);
        }
    }

    public final void disconnect() {
        Connection connection = this.mActiveConnection;
        if (connection != null) {
            this.mConnectionReady = false;
            connection.dispose();
            this.mActiveConnection = null;
            setAndNotifyProviderState(null);
            synchronized (this.mLock) {
                for (RoutingSessionInfo routingSessionInfo : this.mSessionInfos) {
                    this.mCallback.onSessionReleased(this, routingSessionInfo);
                }
                this.mSessionInfos.clear();
                this.mReleasingSessions.clear();
            }
        }
    }

    public String toString() {
        return getDebugString();
    }

    public final String getDebugString() {
        Object[] objArr = new Object[4];
        objArr[0] = this.mComponentName.getPackageName();
        objArr[1] = Boolean.valueOf(this.mBound);
        objArr[2] = Boolean.valueOf(this.mActiveConnection != null);
        objArr[3] = Boolean.valueOf(this.mConnectionReady);
        return TextUtils.formatSimple("ProviderServiceProxy - package: %s, bound: %b, connection (active:%b, ready:%b)", objArr);
    }

    /* loaded from: classes2.dex */
    public final class Connection implements IBinder.DeathRecipient {
        public final ServiceCallbackStub mCallbackStub = new ServiceCallbackStub(this);
        public final IMediaRoute2ProviderService mService;

        public Connection(IMediaRoute2ProviderService iMediaRoute2ProviderService) {
            this.mService = iMediaRoute2ProviderService;
        }

        public boolean register() {
            try {
                this.mService.asBinder().linkToDeath(this, 0);
                this.mService.setCallback(this.mCallbackStub);
                MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaRoute2ProviderServiceProxy.Connection.this.lambda$register$0();
                    }
                });
                return true;
            } catch (RemoteException unused) {
                binderDied();
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$register$0() {
            MediaRoute2ProviderServiceProxy.this.onConnectionReady(this);
        }

        public void dispose() {
            this.mService.asBinder().unlinkToDeath(this, 0);
            this.mCallbackStub.dispose();
        }

        public void requestCreateSession(long j, String str, String str2, Bundle bundle) {
            try {
                this.mService.requestCreateSession(j, str, str2, bundle);
            } catch (RemoteException unused) {
                Slog.e("MR2ProviderSvcProxy", "requestCreateSession: Failed to deliver request.");
            }
        }

        public void releaseSession(long j, String str) {
            try {
                this.mService.releaseSession(j, str);
            } catch (RemoteException unused) {
                Slog.e("MR2ProviderSvcProxy", "releaseSession: Failed to deliver request.");
            }
        }

        public void updateDiscoveryPreference(RouteDiscoveryPreference routeDiscoveryPreference) {
            try {
                this.mService.updateDiscoveryPreference(routeDiscoveryPreference);
            } catch (RemoteException unused) {
                Slog.e("MR2ProviderSvcProxy", "updateDiscoveryPreference: Failed to deliver request.");
            }
        }

        public void selectRoute(long j, String str, String str2) {
            try {
                this.mService.selectRoute(j, str, str2);
            } catch (RemoteException e) {
                Slog.e("MR2ProviderSvcProxy", "selectRoute: Failed to deliver request.", e);
            }
        }

        public void deselectRoute(long j, String str, String str2) {
            try {
                this.mService.deselectRoute(j, str, str2);
            } catch (RemoteException e) {
                Slog.e("MR2ProviderSvcProxy", "deselectRoute: Failed to deliver request.", e);
            }
        }

        public void transferToRoute(long j, String str, String str2) {
            try {
                this.mService.transferToRoute(j, str, str2);
            } catch (RemoteException e) {
                Slog.e("MR2ProviderSvcProxy", "transferToRoute: Failed to deliver request.", e);
            }
        }

        public void setRouteVolume(long j, String str, int i) {
            try {
                this.mService.setRouteVolume(j, str, i);
            } catch (RemoteException e) {
                Slog.e("MR2ProviderSvcProxy", "setRouteVolume: Failed to deliver request.", e);
            }
        }

        public void setSessionVolume(long j, String str, int i) {
            try {
                this.mService.setSessionVolume(j, str, i);
            } catch (RemoteException e) {
                Slog.e("MR2ProviderSvcProxy", "setSessionVolume: Failed to deliver request.", e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$1() {
            MediaRoute2ProviderServiceProxy.this.onConnectionDied(this);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRoute2ProviderServiceProxy.Connection.this.lambda$binderDied$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$postProviderUpdated$2(MediaRoute2ProviderInfo mediaRoute2ProviderInfo) {
            MediaRoute2ProviderServiceProxy.this.onProviderUpdated(this, mediaRoute2ProviderInfo);
        }

        public void postProviderUpdated(final MediaRoute2ProviderInfo mediaRoute2ProviderInfo) {
            MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRoute2ProviderServiceProxy.Connection.this.lambda$postProviderUpdated$2(mediaRoute2ProviderInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$postSessionCreated$3(long j, RoutingSessionInfo routingSessionInfo) {
            MediaRoute2ProviderServiceProxy.this.onSessionCreated(this, j, routingSessionInfo);
        }

        public void postSessionCreated(final long j, final RoutingSessionInfo routingSessionInfo) {
            MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRoute2ProviderServiceProxy.Connection.this.lambda$postSessionCreated$3(j, routingSessionInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$postSessionsUpdated$4(List list) {
            MediaRoute2ProviderServiceProxy.this.onSessionsUpdated(this, list);
        }

        public void postSessionsUpdated(final List<RoutingSessionInfo> list) {
            MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRoute2ProviderServiceProxy.Connection.this.lambda$postSessionsUpdated$4(list);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$postSessionReleased$5(RoutingSessionInfo routingSessionInfo) {
            MediaRoute2ProviderServiceProxy.this.onSessionReleased(this, routingSessionInfo);
        }

        public void postSessionReleased(final RoutingSessionInfo routingSessionInfo) {
            MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRoute2ProviderServiceProxy.Connection.this.lambda$postSessionReleased$5(routingSessionInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$postRequestFailed$6(long j, int i) {
            MediaRoute2ProviderServiceProxy.this.onRequestFailed(this, j, i);
        }

        public void postRequestFailed(final long j, final int i) {
            MediaRoute2ProviderServiceProxy.this.mHandler.post(new Runnable() { // from class: com.android.server.media.MediaRoute2ProviderServiceProxy$Connection$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRoute2ProviderServiceProxy.Connection.this.lambda$postRequestFailed$6(j, i);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public static final class ServiceCallbackStub extends IMediaRoute2ProviderServiceCallback.Stub {
        public final WeakReference<Connection> mConnectionRef;

        public ServiceCallbackStub(Connection connection) {
            this.mConnectionRef = new WeakReference<>(connection);
        }

        public void dispose() {
            this.mConnectionRef.clear();
        }

        public void notifyProviderUpdated(MediaRoute2ProviderInfo mediaRoute2ProviderInfo) {
            Connection connection = this.mConnectionRef.get();
            if (connection != null) {
                connection.postProviderUpdated(mediaRoute2ProviderInfo);
            }
        }

        public void notifySessionCreated(long j, RoutingSessionInfo routingSessionInfo) {
            Connection connection = this.mConnectionRef.get();
            if (connection != null) {
                connection.postSessionCreated(j, routingSessionInfo);
            }
        }

        public void notifySessionsUpdated(List<RoutingSessionInfo> list) {
            Connection connection = this.mConnectionRef.get();
            if (connection != null) {
                connection.postSessionsUpdated(list);
            }
        }

        public void notifySessionReleased(RoutingSessionInfo routingSessionInfo) {
            Connection connection = this.mConnectionRef.get();
            if (connection != null) {
                connection.postSessionReleased(routingSessionInfo);
            }
        }

        public void notifyRequestFailed(long j, int i) {
            Connection connection = this.mConnectionRef.get();
            if (connection != null) {
                connection.postRequestFailed(j, i);
            }
        }
    }
}
