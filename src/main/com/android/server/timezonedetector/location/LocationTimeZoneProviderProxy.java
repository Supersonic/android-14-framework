package com.android.server.timezonedetector.location;

import android.content.Context;
import android.service.timezone.TimeZoneProviderEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.server.timezonedetector.Dumpable;
import java.util.Objects;
/* loaded from: classes2.dex */
public abstract class LocationTimeZoneProviderProxy implements Dumpable {
    public final Context mContext;
    @GuardedBy({"mSharedLock"})
    public Listener mListener;
    public final Object mSharedLock;
    public final ThreadingDomain mThreadingDomain;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onProviderBound();

        void onProviderUnbound();

        void onReportTimeZoneProviderEvent(TimeZoneProviderEvent timeZoneProviderEvent);
    }

    @GuardedBy({"mSharedLock"})
    public abstract void onDestroy();

    @GuardedBy({"mSharedLock"})
    public abstract void onInitialize();

    public abstract void setRequest(TimeZoneProviderRequest timeZoneProviderRequest);

    public LocationTimeZoneProviderProxy(Context context, ThreadingDomain threadingDomain) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(threadingDomain);
        this.mThreadingDomain = threadingDomain;
        this.mSharedLock = threadingDomain.getLockObject();
    }

    public void initialize(Listener listener) {
        Objects.requireNonNull(listener);
        synchronized (this.mSharedLock) {
            if (this.mListener != null) {
                throw new IllegalStateException("listener already set");
            }
            this.mListener = listener;
            onInitialize();
        }
    }

    public void destroy() {
        synchronized (this.mSharedLock) {
            onDestroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleTimeZoneProviderEvent$0(TimeZoneProviderEvent timeZoneProviderEvent) {
        this.mListener.onReportTimeZoneProviderEvent(timeZoneProviderEvent);
    }

    public final void handleTimeZoneProviderEvent(final TimeZoneProviderEvent timeZoneProviderEvent) {
        this.mThreadingDomain.post(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneProviderProxy.this.lambda$handleTimeZoneProviderEvent$0(timeZoneProviderEvent);
            }
        });
    }
}
