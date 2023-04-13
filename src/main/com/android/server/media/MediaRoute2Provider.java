package com.android.server.media;

import android.content.ComponentName;
import android.media.MediaRoute2ProviderInfo;
import android.media.RouteDiscoveryPreference;
import android.media.RoutingSessionInfo;
import android.os.Bundle;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public abstract class MediaRoute2Provider {
    public Callback mCallback;
    public final ComponentName mComponentName;
    public boolean mIsSystemRouteProvider;
    public volatile MediaRoute2ProviderInfo mProviderInfo;
    public final String mUniqueId;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final List<RoutingSessionInfo> mSessionInfos = new ArrayList();

    /* loaded from: classes2.dex */
    public interface Callback {
        void onProviderStateChanged(MediaRoute2Provider mediaRoute2Provider);

        void onRequestFailed(MediaRoute2Provider mediaRoute2Provider, long j, int i);

        void onSessionCreated(MediaRoute2Provider mediaRoute2Provider, long j, RoutingSessionInfo routingSessionInfo);

        void onSessionReleased(MediaRoute2Provider mediaRoute2Provider, RoutingSessionInfo routingSessionInfo);

        void onSessionUpdated(MediaRoute2Provider mediaRoute2Provider, RoutingSessionInfo routingSessionInfo);
    }

    public abstract void deselectRoute(long j, String str, String str2);

    public abstract void prepareReleaseSession(String str);

    public abstract void releaseSession(long j, String str);

    public abstract void requestCreateSession(long j, String str, String str2, Bundle bundle);

    public abstract void selectRoute(long j, String str, String str2);

    public abstract void setRouteVolume(long j, String str, int i);

    public abstract void setSessionVolume(long j, String str, int i);

    public abstract void transferToRoute(long j, String str, String str2);

    public abstract void updateDiscoveryPreference(RouteDiscoveryPreference routeDiscoveryPreference);

    public MediaRoute2Provider(ComponentName componentName) {
        Objects.requireNonNull(componentName, "Component name must not be null.");
        this.mComponentName = componentName;
        this.mUniqueId = componentName.flattenToShortString();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public String getUniqueId() {
        return this.mUniqueId;
    }

    public MediaRoute2ProviderInfo getProviderInfo() {
        return this.mProviderInfo;
    }

    public List<RoutingSessionInfo> getSessionInfos() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mSessionInfos);
        }
        return arrayList;
    }

    public void setProviderState(MediaRoute2ProviderInfo mediaRoute2ProviderInfo) {
        if (mediaRoute2ProviderInfo == null) {
            this.mProviderInfo = null;
        } else {
            this.mProviderInfo = new MediaRoute2ProviderInfo.Builder(mediaRoute2ProviderInfo).setUniqueId(this.mComponentName.getPackageName(), this.mUniqueId).setSystemRouteProvider(this.mIsSystemRouteProvider).build();
        }
    }

    public void notifyProviderState() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onProviderStateChanged(this);
        }
    }

    public void setAndNotifyProviderState(MediaRoute2ProviderInfo mediaRoute2ProviderInfo) {
        setProviderState(mediaRoute2ProviderInfo);
        notifyProviderState();
    }
}
