package com.android.location.provider;

import android.location.Address;
import android.location.GeocoderParams;
import android.location.IGeocodeListener;
import android.location.IGeocodeProvider;
import android.os.IBinder;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class GeocodeProvider {
    private IGeocodeProvider.Stub mProvider = new IGeocodeProvider.Stub() { // from class: com.android.location.provider.GeocodeProvider.1
        public void getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, IGeocodeListener listener) {
            List<Address> results = new ArrayList<>();
            String error = GeocodeProvider.this.onGetFromLocation(latitude, longitude, maxResults, params, results);
            try {
                listener.onResults(error, results);
            } catch (RemoteException e) {
            }
        }

        public void getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, IGeocodeListener listener) {
            List<Address> results = new ArrayList<>();
            String error = GeocodeProvider.this.onGetFromLocationName(locationName, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude, maxResults, params, results);
            try {
                listener.onResults(error, results);
            } catch (RemoteException e) {
            }
        }
    };

    public abstract String onGetFromLocation(double d, double d2, int i, GeocoderParams geocoderParams, List<Address> list);

    public abstract String onGetFromLocationName(String str, double d, double d2, double d3, double d4, int i, GeocoderParams geocoderParams, List<Address> list);

    public IBinder getBinder() {
        return this.mProvider;
    }
}
