package com.android.server.p014wm;

import android.os.IBinder;
import java.util.Collection;
/* renamed from: com.android.server.wm.BackgroundActivityStartCallback */
/* loaded from: classes2.dex */
public interface BackgroundActivityStartCallback {
    boolean canCloseSystemDialogs(Collection<IBinder> collection, int i);

    boolean isActivityStartAllowed(Collection<IBinder> collection, int i, String str);
}
