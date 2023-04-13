package com.android.server.media;

import android.media.RouteDiscoveryPreference;
import com.android.internal.util.function.TriConsumer;
import com.android.server.media.MediaRouter2ServiceImpl;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class MediaRouter2ServiceImpl$$ExternalSyntheticLambda14 implements TriConsumer {
    public final void accept(Object obj, Object obj2, Object obj3) {
        ((MediaRouter2ServiceImpl.UserHandler) obj).notifyDiscoveryPreferenceChangedToManagers((String) obj2, (RouteDiscoveryPreference) obj3);
    }
}
