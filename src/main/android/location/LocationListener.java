package android.location;

import android.p008os.Bundle;
import java.util.List;
/* loaded from: classes2.dex */
public interface LocationListener {
    void onLocationChanged(Location location);

    default void onLocationChanged(List<Location> locations) {
        int size = locations.size();
        for (int i = 0; i < size; i++) {
            onLocationChanged(locations.get(i));
        }
    }

    default void onFlushComplete(int requestCode) {
    }

    @Deprecated
    default void onStatusChanged(String provider, int status, Bundle extras) {
    }

    default void onProviderEnabled(String provider) {
    }

    default void onProviderDisabled(String provider) {
    }
}
