package com.android.server.location.injector;

import android.location.util.identity.CallerIdentity;
import com.android.server.location.LocationPermissions;
import com.android.server.location.injector.AppOpsHelper;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public abstract class LocationPermissionsHelper {
    public final AppOpsHelper mAppOps;
    public final CopyOnWriteArrayList<LocationPermissionsListener> mListeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface LocationPermissionsListener {
        void onLocationPermissionsChanged(int i);

        void onLocationPermissionsChanged(String str);
    }

    public abstract boolean hasPermission(String str, CallerIdentity callerIdentity);

    public LocationPermissionsHelper(AppOpsHelper appOpsHelper) {
        this.mAppOps = appOpsHelper;
        appOpsHelper.addListener(new AppOpsHelper.LocationAppOpListener() { // from class: com.android.server.location.injector.LocationPermissionsHelper$$ExternalSyntheticLambda0
            @Override // com.android.server.location.injector.AppOpsHelper.LocationAppOpListener
            public final void onAppOpsChanged(String str) {
                LocationPermissionsHelper.this.onAppOpsChanged(str);
            }
        });
    }

    public final void notifyLocationPermissionsChanged(String str) {
        Iterator<LocationPermissionsListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onLocationPermissionsChanged(str);
        }
    }

    public final void notifyLocationPermissionsChanged(int i) {
        Iterator<LocationPermissionsListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onLocationPermissionsChanged(i);
        }
    }

    public final void onAppOpsChanged(String str) {
        notifyLocationPermissionsChanged(str);
    }

    public final void addListener(LocationPermissionsListener locationPermissionsListener) {
        this.mListeners.add(locationPermissionsListener);
    }

    public final void removeListener(LocationPermissionsListener locationPermissionsListener) {
        this.mListeners.remove(locationPermissionsListener);
    }

    public final boolean hasLocationPermissions(int i, CallerIdentity callerIdentity) {
        if (i != 0 && hasPermission(LocationPermissions.asPermission(i), callerIdentity)) {
            return this.mAppOps.checkOpNoThrow(LocationPermissions.asAppOp(i), callerIdentity);
        }
        return false;
    }
}
