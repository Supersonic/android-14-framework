package android.location;

import android.content.Context;
import android.location.IGeocodeListener;
import android.location.ILocationManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.provider.CallLog;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public final class Geocoder {
    private static final long TIMEOUT_MS = 60000;
    private final GeocoderParams mParams;
    private final ILocationManager mService;

    /* loaded from: classes2.dex */
    public interface GeocodeListener {
        void onGeocode(List<Address> list);

        default void onError(String errorMessage) {
        }
    }

    public static boolean isPresent() {
        ILocationManager lm = (ILocationManager) Objects.requireNonNull(ILocationManager.Stub.asInterface(ServiceManager.getService("location")));
        try {
            return lm.geocoderIsPresent();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Geocoder(Context context) {
        this(context, Locale.getDefault());
    }

    public Geocoder(Context context, Locale locale) {
        this.mParams = new GeocoderParams(context, locale);
        this.mService = ILocationManager.Stub.asInterface(ServiceManager.getService("location"));
    }

    @Deprecated
    public List<Address> getFromLocation(double latitude, double longitude, int maxResults) throws IOException {
        SynchronousGeocoder listener = new SynchronousGeocoder();
        getFromLocation(latitude, longitude, maxResults, listener);
        return listener.getResults();
    }

    public void getFromLocation(double latitude, double longitude, int maxResults, GeocodeListener listener) {
        Preconditions.checkArgumentInRange(latitude, -90.0d, 90.0d, CallLog.Locations.LATITUDE);
        Preconditions.checkArgumentInRange(longitude, -180.0d, 180.0d, CallLog.Locations.LONGITUDE);
        try {
        } catch (RemoteException e) {
            e = e;
        }
        try {
            this.mService.getFromLocation(latitude, longitude, maxResults, this.mParams, new GeocoderImpl(listener));
        } catch (RemoteException e2) {
            e = e2;
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public List<Address> getFromLocationName(String locationName, int maxResults) throws IOException {
        return getFromLocationName(locationName, maxResults, 0.0d, 0.0d, 0.0d, 0.0d);
    }

    public void getFromLocationName(String locationName, int maxResults, GeocodeListener listener) {
        getFromLocationName(locationName, maxResults, 0.0d, 0.0d, 0.0d, 0.0d, listener);
    }

    @Deprecated
    public List<Address> getFromLocationName(String locationName, int maxResults, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude) throws IOException {
        SynchronousGeocoder listener = new SynchronousGeocoder();
        getFromLocationName(locationName, maxResults, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude, listener);
        return listener.getResults();
    }

    public void getFromLocationName(String locationName, int maxResults, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, GeocodeListener listener) {
        Preconditions.checkArgument(locationName != null);
        Preconditions.checkArgumentInRange(lowerLeftLatitude, -90.0d, 90.0d, "lowerLeftLatitude");
        Preconditions.checkArgumentInRange(lowerLeftLongitude, -180.0d, 180.0d, "lowerLeftLongitude");
        Preconditions.checkArgumentInRange(upperRightLatitude, -90.0d, 90.0d, "upperRightLatitude");
        Preconditions.checkArgumentInRange(upperRightLongitude, -180.0d, 180.0d, "upperRightLongitude");
        try {
        } catch (RemoteException e) {
            e = e;
        }
        try {
            this.mService.getFromLocationName(locationName, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude, maxResults, this.mParams, new GeocoderImpl(listener));
        } catch (RemoteException e2) {
            e = e2;
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GeocoderImpl extends IGeocodeListener.Stub {
        private GeocodeListener mListener;

        GeocoderImpl(GeocodeListener listener) {
            this.mListener = (GeocodeListener) Objects.requireNonNull(listener);
        }

        @Override // android.location.IGeocodeListener
        public void onResults(String error, List<Address> addresses) throws RemoteException {
            if (this.mListener == null) {
                return;
            }
            GeocodeListener listener = this.mListener;
            this.mListener = null;
            if (error != null) {
                listener.onError(error);
                return;
            }
            if (addresses == null) {
                addresses = Collections.emptyList();
            }
            listener.onGeocode(addresses);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SynchronousGeocoder implements GeocodeListener {
        private final CountDownLatch mLatch = new CountDownLatch(1);
        private String mError = null;
        private List<Address> mResults = Collections.emptyList();

        SynchronousGeocoder() {
        }

        @Override // android.location.Geocoder.GeocodeListener
        public void onGeocode(List<Address> addresses) {
            this.mResults = addresses;
            this.mLatch.countDown();
        }

        @Override // android.location.Geocoder.GeocodeListener
        public void onError(String errorMessage) {
            this.mError = errorMessage;
            this.mLatch.countDown();
        }

        public List<Address> getResults() throws IOException {
            try {
                if (!this.mLatch.await(60000L, TimeUnit.MILLISECONDS)) {
                    this.mError = "Service not Available";
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (this.mError != null) {
                throw new IOException(this.mError);
            }
            return this.mResults;
        }
    }
}
