package android.location;

import android.Manifest;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.content.AttributionSource;
import android.location.IGeocodeListener;
import android.location.IGnssAntennaInfoListener;
import android.location.IGnssMeasurementsListener;
import android.location.IGnssNavigationMessageListener;
import android.location.IGnssNmeaListener;
import android.location.IGnssStatusListener;
import android.location.ILocationCallback;
import android.location.ILocationListener;
import android.location.provider.IProviderRequestListener;
import android.location.provider.ProviderProperties;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.PackageTagsList;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface ILocationManager extends IInterface {
    void addGnssAntennaInfoListener(IGnssAntennaInfoListener iGnssAntennaInfoListener, String str, String str2, String str3) throws RemoteException;

    void addGnssMeasurementsListener(GnssMeasurementRequest gnssMeasurementRequest, IGnssMeasurementsListener iGnssMeasurementsListener, String str, String str2, String str3) throws RemoteException;

    void addGnssNavigationMessageListener(IGnssNavigationMessageListener iGnssNavigationMessageListener, String str, String str2, String str3) throws RemoteException;

    void addProviderRequestListener(IProviderRequestListener iProviderRequestListener) throws RemoteException;

    void addTestProvider(String str, ProviderProperties providerProperties, List<String> list, String str2, String str3) throws RemoteException;

    void flushGnssBatch() throws RemoteException;

    boolean geocoderIsPresent() throws RemoteException;

    PackageTagsList getAdasAllowlist() throws RemoteException;

    List<String> getAllProviders() throws RemoteException;

    String[] getBackgroundThrottlingWhitelist() throws RemoteException;

    String getBestProvider(Criteria criteria, boolean z) throws RemoteException;

    ICancellationSignal getCurrentLocation(String str, LocationRequest locationRequest, ILocationCallback iLocationCallback, String str2, String str3, String str4) throws RemoteException;

    String getExtraLocationControllerPackage() throws RemoteException;

    void getFromLocation(double d, double d2, int i, GeocoderParams geocoderParams, IGeocodeListener iGeocodeListener) throws RemoteException;

    void getFromLocationName(String str, double d, double d2, double d3, double d4, int i, GeocoderParams geocoderParams, IGeocodeListener iGeocodeListener) throws RemoteException;

    List<GnssAntennaInfo> getGnssAntennaInfos() throws RemoteException;

    int getGnssBatchSize() throws RemoteException;

    GnssCapabilities getGnssCapabilities() throws RemoteException;

    String getGnssHardwareModelName() throws RemoteException;

    LocationTime getGnssTimeMillis() throws RemoteException;

    int getGnssYearOfHardware() throws RemoteException;

    PackageTagsList getIgnoreSettingsAllowlist() throws RemoteException;

    Location getLastLocation(String str, LastLocationRequest lastLocationRequest, String str2, String str3) throws RemoteException;

    List<String> getProviderPackages(String str) throws RemoteException;

    ProviderProperties getProviderProperties(String str) throws RemoteException;

    List<String> getProviders(Criteria criteria, boolean z) throws RemoteException;

    boolean hasProvider(String str) throws RemoteException;

    void injectGnssMeasurementCorrections(GnssMeasurementCorrections gnssMeasurementCorrections) throws RemoteException;

    void injectLocation(Location location) throws RemoteException;

    boolean isAdasGnssLocationEnabledForUser(int i) throws RemoteException;

    boolean isAutomotiveGnssSuspended() throws RemoteException;

    boolean isExtraLocationControllerPackageEnabled() throws RemoteException;

    boolean isLocationEnabledForUser(int i) throws RemoteException;

    boolean isProviderEnabledForUser(String str, int i) throws RemoteException;

    boolean isProviderPackage(String str, String str2, String str3) throws RemoteException;

    void registerGnssNmeaCallback(IGnssNmeaListener iGnssNmeaListener, String str, String str2, String str3) throws RemoteException;

    void registerGnssStatusCallback(IGnssStatusListener iGnssStatusListener, String str, String str2, String str3) throws RemoteException;

    void registerLocationListener(String str, LocationRequest locationRequest, ILocationListener iLocationListener, String str2, String str3, String str4) throws RemoteException;

    void registerLocationPendingIntent(String str, LocationRequest locationRequest, PendingIntent pendingIntent, String str2, String str3) throws RemoteException;

    void removeGeofence(PendingIntent pendingIntent) throws RemoteException;

    void removeGnssAntennaInfoListener(IGnssAntennaInfoListener iGnssAntennaInfoListener) throws RemoteException;

    void removeGnssMeasurementsListener(IGnssMeasurementsListener iGnssMeasurementsListener) throws RemoteException;

    void removeGnssNavigationMessageListener(IGnssNavigationMessageListener iGnssNavigationMessageListener) throws RemoteException;

    void removeProviderRequestListener(IProviderRequestListener iProviderRequestListener) throws RemoteException;

    void removeTestProvider(String str, String str2, String str3) throws RemoteException;

    void requestGeofence(Geofence geofence, PendingIntent pendingIntent, String str, String str2) throws RemoteException;

    void requestListenerFlush(String str, ILocationListener iLocationListener, int i) throws RemoteException;

    void requestPendingIntentFlush(String str, PendingIntent pendingIntent, int i) throws RemoteException;

    void sendExtraCommand(String str, String str2, Bundle bundle) throws RemoteException;

    void setAdasGnssLocationEnabledForUser(boolean z, int i) throws RemoteException;

    void setAutomotiveGnssSuspended(boolean z) throws RemoteException;

    void setExtraLocationControllerPackage(String str) throws RemoteException;

    void setExtraLocationControllerPackageEnabled(boolean z) throws RemoteException;

    void setLocationEnabledForUser(boolean z, int i) throws RemoteException;

    void setTestProviderEnabled(String str, boolean z, String str2, String str3) throws RemoteException;

    void setTestProviderLocation(String str, Location location, String str2, String str3) throws RemoteException;

    void startGnssBatch(long j, ILocationListener iLocationListener, String str, String str2, String str3) throws RemoteException;

    void stopGnssBatch() throws RemoteException;

    void unregisterGnssNmeaCallback(IGnssNmeaListener iGnssNmeaListener) throws RemoteException;

    void unregisterGnssStatusCallback(IGnssStatusListener iGnssStatusListener) throws RemoteException;

    void unregisterLocationListener(ILocationListener iLocationListener) throws RemoteException;

    void unregisterLocationPendingIntent(PendingIntent pendingIntent) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ILocationManager {
        @Override // android.location.ILocationManager
        public Location getLastLocation(String provider, LastLocationRequest request, String packageName, String attributionTag) throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public ICancellationSignal getCurrentLocation(String provider, LocationRequest request, ILocationCallback callback, String packageName, String attributionTag, String listenerId) throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public void registerLocationListener(String provider, LocationRequest request, ILocationListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void unregisterLocationListener(ILocationListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void registerLocationPendingIntent(String provider, LocationRequest request, PendingIntent pendingIntent, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void unregisterLocationPendingIntent(PendingIntent pendingIntent) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void injectLocation(Location location) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void requestListenerFlush(String provider, ILocationListener listener, int requestCode) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void requestPendingIntentFlush(String provider, PendingIntent pendingIntent, int requestCode) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void requestGeofence(Geofence geofence, PendingIntent intent, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void removeGeofence(PendingIntent intent) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public boolean geocoderIsPresent() throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public void getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public GnssCapabilities getGnssCapabilities() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public int getGnssYearOfHardware() throws RemoteException {
            return 0;
        }

        @Override // android.location.ILocationManager
        public String getGnssHardwareModelName() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public List<GnssAntennaInfo> getGnssAntennaInfos() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public void registerGnssStatusCallback(IGnssStatusListener callback, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void unregisterGnssStatusCallback(IGnssStatusListener callback) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void registerGnssNmeaCallback(IGnssNmeaListener callback, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void unregisterGnssNmeaCallback(IGnssNmeaListener callback) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void addGnssMeasurementsListener(GnssMeasurementRequest request, IGnssMeasurementsListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void removeGnssMeasurementsListener(IGnssMeasurementsListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void injectGnssMeasurementCorrections(GnssMeasurementCorrections corrections) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void addGnssNavigationMessageListener(IGnssNavigationMessageListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void addGnssAntennaInfoListener(IGnssAntennaInfoListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void removeGnssAntennaInfoListener(IGnssAntennaInfoListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void addProviderRequestListener(IProviderRequestListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void removeProviderRequestListener(IProviderRequestListener listener) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public int getGnssBatchSize() throws RemoteException {
            return 0;
        }

        @Override // android.location.ILocationManager
        public void startGnssBatch(long periodNanos, ILocationListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void flushGnssBatch() throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void stopGnssBatch() throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public boolean hasProvider(String provider) throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public List<String> getAllProviders() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public List<String> getProviders(Criteria criteria, boolean enabledOnly) throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public String getBestProvider(Criteria criteria, boolean enabledOnly) throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public ProviderProperties getProviderProperties(String provider) throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public boolean isProviderPackage(String provider, String packageName, String attributionTag) throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public List<String> getProviderPackages(String provider) throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public void setExtraLocationControllerPackage(String packageName) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public String getExtraLocationControllerPackage() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public void setExtraLocationControllerPackageEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public boolean isExtraLocationControllerPackageEnabled() throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public boolean isProviderEnabledForUser(String provider, int userId) throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public boolean isLocationEnabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public void setLocationEnabledForUser(boolean enabled, int userId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public boolean isAdasGnssLocationEnabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public void setAdasGnssLocationEnabledForUser(boolean enabled, int userId) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public boolean isAutomotiveGnssSuspended() throws RemoteException {
            return false;
        }

        @Override // android.location.ILocationManager
        public void setAutomotiveGnssSuspended(boolean suspended) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void addTestProvider(String name, ProviderProperties properties, List<String> locationTags, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void removeTestProvider(String provider, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void setTestProviderLocation(String provider, Location location, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public void setTestProviderEnabled(String provider, boolean enabled, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public LocationTime getGnssTimeMillis() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public void sendExtraCommand(String provider, String command, Bundle extras) throws RemoteException {
        }

        @Override // android.location.ILocationManager
        public String[] getBackgroundThrottlingWhitelist() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public PackageTagsList getIgnoreSettingsAllowlist() throws RemoteException {
            return null;
        }

        @Override // android.location.ILocationManager
        public PackageTagsList getAdasAllowlist() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ILocationManager {
        public static final String DESCRIPTOR = "android.location.ILocationManager";
        static final int TRANSACTION_addGnssAntennaInfoListener = 28;
        static final int TRANSACTION_addGnssMeasurementsListener = 23;
        static final int TRANSACTION_addGnssNavigationMessageListener = 26;
        static final int TRANSACTION_addProviderRequestListener = 30;
        static final int TRANSACTION_addTestProvider = 54;
        static final int TRANSACTION_flushGnssBatch = 34;
        static final int TRANSACTION_geocoderIsPresent = 12;
        static final int TRANSACTION_getAdasAllowlist = 62;
        static final int TRANSACTION_getAllProviders = 37;
        static final int TRANSACTION_getBackgroundThrottlingWhitelist = 60;
        static final int TRANSACTION_getBestProvider = 39;
        static final int TRANSACTION_getCurrentLocation = 2;
        static final int TRANSACTION_getExtraLocationControllerPackage = 44;
        static final int TRANSACTION_getFromLocation = 13;
        static final int TRANSACTION_getFromLocationName = 14;
        static final int TRANSACTION_getGnssAntennaInfos = 18;
        static final int TRANSACTION_getGnssBatchSize = 32;
        static final int TRANSACTION_getGnssCapabilities = 15;
        static final int TRANSACTION_getGnssHardwareModelName = 17;
        static final int TRANSACTION_getGnssTimeMillis = 58;
        static final int TRANSACTION_getGnssYearOfHardware = 16;
        static final int TRANSACTION_getIgnoreSettingsAllowlist = 61;
        static final int TRANSACTION_getLastLocation = 1;
        static final int TRANSACTION_getProviderPackages = 42;
        static final int TRANSACTION_getProviderProperties = 40;
        static final int TRANSACTION_getProviders = 38;
        static final int TRANSACTION_hasProvider = 36;
        static final int TRANSACTION_injectGnssMeasurementCorrections = 25;
        static final int TRANSACTION_injectLocation = 7;
        static final int TRANSACTION_isAdasGnssLocationEnabledForUser = 50;
        static final int TRANSACTION_isAutomotiveGnssSuspended = 52;
        static final int TRANSACTION_isExtraLocationControllerPackageEnabled = 46;
        static final int TRANSACTION_isLocationEnabledForUser = 48;
        static final int TRANSACTION_isProviderEnabledForUser = 47;
        static final int TRANSACTION_isProviderPackage = 41;
        static final int TRANSACTION_registerGnssNmeaCallback = 21;
        static final int TRANSACTION_registerGnssStatusCallback = 19;
        static final int TRANSACTION_registerLocationListener = 3;
        static final int TRANSACTION_registerLocationPendingIntent = 5;
        static final int TRANSACTION_removeGeofence = 11;
        static final int TRANSACTION_removeGnssAntennaInfoListener = 29;
        static final int TRANSACTION_removeGnssMeasurementsListener = 24;
        static final int TRANSACTION_removeGnssNavigationMessageListener = 27;
        static final int TRANSACTION_removeProviderRequestListener = 31;
        static final int TRANSACTION_removeTestProvider = 55;
        static final int TRANSACTION_requestGeofence = 10;
        static final int TRANSACTION_requestListenerFlush = 8;
        static final int TRANSACTION_requestPendingIntentFlush = 9;
        static final int TRANSACTION_sendExtraCommand = 59;
        static final int TRANSACTION_setAdasGnssLocationEnabledForUser = 51;
        static final int TRANSACTION_setAutomotiveGnssSuspended = 53;
        static final int TRANSACTION_setExtraLocationControllerPackage = 43;
        static final int TRANSACTION_setExtraLocationControllerPackageEnabled = 45;
        static final int TRANSACTION_setLocationEnabledForUser = 49;
        static final int TRANSACTION_setTestProviderEnabled = 57;
        static final int TRANSACTION_setTestProviderLocation = 56;
        static final int TRANSACTION_startGnssBatch = 33;
        static final int TRANSACTION_stopGnssBatch = 35;
        static final int TRANSACTION_unregisterGnssNmeaCallback = 22;
        static final int TRANSACTION_unregisterGnssStatusCallback = 20;
        static final int TRANSACTION_unregisterLocationListener = 4;
        static final int TRANSACTION_unregisterLocationPendingIntent = 6;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static ILocationManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ILocationManager)) {
                return (ILocationManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getLastLocation";
                case 2:
                    return "getCurrentLocation";
                case 3:
                    return "registerLocationListener";
                case 4:
                    return "unregisterLocationListener";
                case 5:
                    return "registerLocationPendingIntent";
                case 6:
                    return "unregisterLocationPendingIntent";
                case 7:
                    return "injectLocation";
                case 8:
                    return "requestListenerFlush";
                case 9:
                    return "requestPendingIntentFlush";
                case 10:
                    return "requestGeofence";
                case 11:
                    return "removeGeofence";
                case 12:
                    return "geocoderIsPresent";
                case 13:
                    return "getFromLocation";
                case 14:
                    return "getFromLocationName";
                case 15:
                    return "getGnssCapabilities";
                case 16:
                    return "getGnssYearOfHardware";
                case 17:
                    return "getGnssHardwareModelName";
                case 18:
                    return "getGnssAntennaInfos";
                case 19:
                    return "registerGnssStatusCallback";
                case 20:
                    return "unregisterGnssStatusCallback";
                case 21:
                    return "registerGnssNmeaCallback";
                case 22:
                    return "unregisterGnssNmeaCallback";
                case 23:
                    return "addGnssMeasurementsListener";
                case 24:
                    return "removeGnssMeasurementsListener";
                case 25:
                    return "injectGnssMeasurementCorrections";
                case 26:
                    return "addGnssNavigationMessageListener";
                case 27:
                    return "removeGnssNavigationMessageListener";
                case 28:
                    return "addGnssAntennaInfoListener";
                case 29:
                    return "removeGnssAntennaInfoListener";
                case 30:
                    return "addProviderRequestListener";
                case 31:
                    return "removeProviderRequestListener";
                case 32:
                    return "getGnssBatchSize";
                case 33:
                    return "startGnssBatch";
                case 34:
                    return "flushGnssBatch";
                case 35:
                    return "stopGnssBatch";
                case 36:
                    return "hasProvider";
                case 37:
                    return "getAllProviders";
                case 38:
                    return "getProviders";
                case 39:
                    return "getBestProvider";
                case 40:
                    return "getProviderProperties";
                case 41:
                    return "isProviderPackage";
                case 42:
                    return "getProviderPackages";
                case 43:
                    return "setExtraLocationControllerPackage";
                case 44:
                    return "getExtraLocationControllerPackage";
                case 45:
                    return "setExtraLocationControllerPackageEnabled";
                case 46:
                    return "isExtraLocationControllerPackageEnabled";
                case 47:
                    return "isProviderEnabledForUser";
                case 48:
                    return "isLocationEnabledForUser";
                case 49:
                    return "setLocationEnabledForUser";
                case 50:
                    return "isAdasGnssLocationEnabledForUser";
                case 51:
                    return "setAdasGnssLocationEnabledForUser";
                case 52:
                    return "isAutomotiveGnssSuspended";
                case 53:
                    return "setAutomotiveGnssSuspended";
                case 54:
                    return "addTestProvider";
                case 55:
                    return "removeTestProvider";
                case 56:
                    return "setTestProviderLocation";
                case 57:
                    return "setTestProviderEnabled";
                case 58:
                    return "getGnssTimeMillis";
                case 59:
                    return "sendExtraCommand";
                case 60:
                    return "getBackgroundThrottlingWhitelist";
                case 61:
                    return "getIgnoreSettingsAllowlist";
                case 62:
                    return "getAdasAllowlist";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            LastLocationRequest _arg1 = (LastLocationRequest) data.readTypedObject(LastLocationRequest.CREATOR);
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            Location _result = getLastLocation(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            return true;
                        case 2:
                            String _arg02 = data.readString();
                            LocationRequest _arg12 = (LocationRequest) data.readTypedObject(LocationRequest.CREATOR);
                            ILocationCallback _arg22 = ILocationCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg32 = data.readString();
                            String _arg4 = data.readString();
                            String _arg5 = data.readString();
                            data.enforceNoDataAvail();
                            ICancellationSignal _result2 = getCurrentLocation(_arg02, _arg12, _arg22, _arg32, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            return true;
                        case 3:
                            String _arg03 = data.readString();
                            LocationRequest _arg13 = (LocationRequest) data.readTypedObject(LocationRequest.CREATOR);
                            ILocationListener _arg23 = ILocationListener.Stub.asInterface(data.readStrongBinder());
                            String _arg33 = data.readString();
                            String _arg42 = data.readString();
                            String _arg52 = data.readString();
                            data.enforceNoDataAvail();
                            registerLocationListener(_arg03, _arg13, _arg23, _arg33, _arg42, _arg52);
                            reply.writeNoException();
                            return true;
                        case 4:
                            ILocationListener _arg04 = ILocationListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterLocationListener(_arg04);
                            reply.writeNoException();
                            return true;
                        case 5:
                            String _arg05 = data.readString();
                            LocationRequest _arg14 = (LocationRequest) data.readTypedObject(LocationRequest.CREATOR);
                            PendingIntent _arg24 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            String _arg34 = data.readString();
                            String _arg43 = data.readString();
                            data.enforceNoDataAvail();
                            registerLocationPendingIntent(_arg05, _arg14, _arg24, _arg34, _arg43);
                            reply.writeNoException();
                            return true;
                        case 6:
                            PendingIntent _arg06 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            unregisterLocationPendingIntent(_arg06);
                            reply.writeNoException();
                            return true;
                        case 7:
                            Location _arg07 = (Location) data.readTypedObject(Location.CREATOR);
                            data.enforceNoDataAvail();
                            injectLocation(_arg07);
                            reply.writeNoException();
                            return true;
                        case 8:
                            String _arg08 = data.readString();
                            ILocationListener _arg15 = ILocationListener.Stub.asInterface(data.readStrongBinder());
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            requestListenerFlush(_arg08, _arg15, _arg25);
                            reply.writeNoException();
                            return true;
                        case 9:
                            String _arg09 = data.readString();
                            PendingIntent _arg16 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            requestPendingIntentFlush(_arg09, _arg16, _arg26);
                            reply.writeNoException();
                            return true;
                        case 10:
                            Geofence _arg010 = (Geofence) data.readTypedObject(Geofence.CREATOR);
                            PendingIntent _arg17 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            String _arg27 = data.readString();
                            String _arg35 = data.readString();
                            data.enforceNoDataAvail();
                            requestGeofence(_arg010, _arg17, _arg27, _arg35);
                            reply.writeNoException();
                            return true;
                        case 11:
                            PendingIntent _arg011 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            removeGeofence(_arg011);
                            reply.writeNoException();
                            return true;
                        case 12:
                            boolean _result3 = geocoderIsPresent();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            return true;
                        case 13:
                            double _arg012 = data.readDouble();
                            double _arg18 = data.readDouble();
                            int _arg28 = data.readInt();
                            GeocoderParams _arg36 = (GeocoderParams) data.readTypedObject(GeocoderParams.CREATOR);
                            IGeocodeListener _arg44 = IGeocodeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getFromLocation(_arg012, _arg18, _arg28, _arg36, _arg44);
                            reply.writeNoException();
                            return true;
                        case 14:
                            String _arg013 = data.readString();
                            double _arg19 = data.readDouble();
                            double _arg29 = data.readDouble();
                            double _arg37 = data.readDouble();
                            double _arg45 = data.readDouble();
                            int _arg53 = data.readInt();
                            GeocoderParams _arg6 = (GeocoderParams) data.readTypedObject(GeocoderParams.CREATOR);
                            IGeocodeListener _arg7 = IGeocodeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getFromLocationName(_arg013, _arg19, _arg29, _arg37, _arg45, _arg53, _arg6, _arg7);
                            reply.writeNoException();
                            return true;
                        case 15:
                            GnssCapabilities _result4 = getGnssCapabilities();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            return true;
                        case 16:
                            int _result5 = getGnssYearOfHardware();
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            return true;
                        case 17:
                            String _result6 = getGnssHardwareModelName();
                            reply.writeNoException();
                            reply.writeString(_result6);
                            return true;
                        case 18:
                            List<GnssAntennaInfo> _result7 = getGnssAntennaInfos();
                            reply.writeNoException();
                            reply.writeTypedList(_result7, 1);
                            return true;
                        case 19:
                            IGnssStatusListener _arg014 = IGnssStatusListener.Stub.asInterface(data.readStrongBinder());
                            String _arg110 = data.readString();
                            String _arg210 = data.readString();
                            String _arg38 = data.readString();
                            data.enforceNoDataAvail();
                            registerGnssStatusCallback(_arg014, _arg110, _arg210, _arg38);
                            reply.writeNoException();
                            return true;
                        case 20:
                            IGnssStatusListener _arg015 = IGnssStatusListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterGnssStatusCallback(_arg015);
                            reply.writeNoException();
                            return true;
                        case 21:
                            IGnssNmeaListener _arg016 = IGnssNmeaListener.Stub.asInterface(data.readStrongBinder());
                            String _arg111 = data.readString();
                            String _arg211 = data.readString();
                            String _arg39 = data.readString();
                            data.enforceNoDataAvail();
                            registerGnssNmeaCallback(_arg016, _arg111, _arg211, _arg39);
                            reply.writeNoException();
                            return true;
                        case 22:
                            IGnssNmeaListener _arg017 = IGnssNmeaListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterGnssNmeaCallback(_arg017);
                            reply.writeNoException();
                            return true;
                        case 23:
                            GnssMeasurementRequest _arg018 = (GnssMeasurementRequest) data.readTypedObject(GnssMeasurementRequest.CREATOR);
                            IGnssMeasurementsListener _arg112 = IGnssMeasurementsListener.Stub.asInterface(data.readStrongBinder());
                            String _arg212 = data.readString();
                            String _arg310 = data.readString();
                            String _arg46 = data.readString();
                            data.enforceNoDataAvail();
                            addGnssMeasurementsListener(_arg018, _arg112, _arg212, _arg310, _arg46);
                            reply.writeNoException();
                            return true;
                        case 24:
                            IGnssMeasurementsListener _arg019 = IGnssMeasurementsListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeGnssMeasurementsListener(_arg019);
                            reply.writeNoException();
                            return true;
                        case 25:
                            GnssMeasurementCorrections _arg020 = (GnssMeasurementCorrections) data.readTypedObject(GnssMeasurementCorrections.CREATOR);
                            data.enforceNoDataAvail();
                            injectGnssMeasurementCorrections(_arg020);
                            reply.writeNoException();
                            return true;
                        case 26:
                            IGnssNavigationMessageListener _arg021 = IGnssNavigationMessageListener.Stub.asInterface(data.readStrongBinder());
                            String _arg113 = data.readString();
                            String _arg213 = data.readString();
                            String _arg311 = data.readString();
                            data.enforceNoDataAvail();
                            addGnssNavigationMessageListener(_arg021, _arg113, _arg213, _arg311);
                            reply.writeNoException();
                            return true;
                        case 27:
                            IGnssNavigationMessageListener _arg022 = IGnssNavigationMessageListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeGnssNavigationMessageListener(_arg022);
                            reply.writeNoException();
                            return true;
                        case 28:
                            IGnssAntennaInfoListener _arg023 = IGnssAntennaInfoListener.Stub.asInterface(data.readStrongBinder());
                            String _arg114 = data.readString();
                            String _arg214 = data.readString();
                            String _arg312 = data.readString();
                            data.enforceNoDataAvail();
                            addGnssAntennaInfoListener(_arg023, _arg114, _arg214, _arg312);
                            reply.writeNoException();
                            return true;
                        case 29:
                            IGnssAntennaInfoListener _arg024 = IGnssAntennaInfoListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeGnssAntennaInfoListener(_arg024);
                            reply.writeNoException();
                            return true;
                        case 30:
                            IProviderRequestListener _arg025 = IProviderRequestListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addProviderRequestListener(_arg025);
                            reply.writeNoException();
                            return true;
                        case 31:
                            IProviderRequestListener _arg026 = IProviderRequestListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeProviderRequestListener(_arg026);
                            reply.writeNoException();
                            return true;
                        case 32:
                            int _result8 = getGnssBatchSize();
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            return true;
                        case 33:
                            long _arg027 = data.readLong();
                            ILocationListener _arg115 = ILocationListener.Stub.asInterface(data.readStrongBinder());
                            String _arg215 = data.readString();
                            String _arg313 = data.readString();
                            String _arg47 = data.readString();
                            data.enforceNoDataAvail();
                            startGnssBatch(_arg027, _arg115, _arg215, _arg313, _arg47);
                            reply.writeNoException();
                            return true;
                        case 34:
                            flushGnssBatch();
                            reply.writeNoException();
                            return true;
                        case 35:
                            stopGnssBatch();
                            reply.writeNoException();
                            return true;
                        case 36:
                            String _arg028 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result9 = hasProvider(_arg028);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            return true;
                        case 37:
                            List<String> _result10 = getAllProviders();
                            reply.writeNoException();
                            reply.writeStringList(_result10);
                            return true;
                        case 38:
                            Criteria _arg029 = (Criteria) data.readTypedObject(Criteria.CREATOR);
                            boolean _arg116 = data.readBoolean();
                            data.enforceNoDataAvail();
                            List<String> _result11 = getProviders(_arg029, _arg116);
                            reply.writeNoException();
                            reply.writeStringList(_result11);
                            return true;
                        case 39:
                            Criteria _arg030 = (Criteria) data.readTypedObject(Criteria.CREATOR);
                            boolean _arg117 = data.readBoolean();
                            data.enforceNoDataAvail();
                            String _result12 = getBestProvider(_arg030, _arg117);
                            reply.writeNoException();
                            reply.writeString(_result12);
                            return true;
                        case 40:
                            String _arg031 = data.readString();
                            data.enforceNoDataAvail();
                            ProviderProperties _result13 = getProviderProperties(_arg031);
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            return true;
                        case 41:
                            String _arg032 = data.readString();
                            String _arg118 = data.readString();
                            String _arg216 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result14 = isProviderPackage(_arg032, _arg118, _arg216);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            return true;
                        case 42:
                            String _arg033 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result15 = getProviderPackages(_arg033);
                            reply.writeNoException();
                            reply.writeStringList(_result15);
                            return true;
                        case 43:
                            String _arg034 = data.readString();
                            data.enforceNoDataAvail();
                            setExtraLocationControllerPackage(_arg034);
                            reply.writeNoException();
                            return true;
                        case 44:
                            String _result16 = getExtraLocationControllerPackage();
                            reply.writeNoException();
                            reply.writeString(_result16);
                            return true;
                        case 45:
                            boolean _arg035 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setExtraLocationControllerPackageEnabled(_arg035);
                            reply.writeNoException();
                            return true;
                        case 46:
                            boolean _result17 = isExtraLocationControllerPackageEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            return true;
                        case 47:
                            String _arg036 = data.readString();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result18 = isProviderEnabledForUser(_arg036, _arg119);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            return true;
                        case 48:
                            int _arg037 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result19 = isLocationEnabledForUser(_arg037);
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            return true;
                        case 49:
                            boolean _arg038 = data.readBoolean();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            setLocationEnabledForUser(_arg038, _arg120);
                            reply.writeNoException();
                            return true;
                        case 50:
                            int _arg039 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result20 = isAdasGnssLocationEnabledForUser(_arg039);
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            return true;
                        case 51:
                            boolean _arg040 = data.readBoolean();
                            int _arg121 = data.readInt();
                            data.enforceNoDataAvail();
                            setAdasGnssLocationEnabledForUser(_arg040, _arg121);
                            reply.writeNoException();
                            return true;
                        case 52:
                            boolean _result21 = isAutomotiveGnssSuspended();
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            return true;
                        case 53:
                            boolean _arg041 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setAutomotiveGnssSuspended(_arg041);
                            reply.writeNoException();
                            return true;
                        case 54:
                            String _arg042 = data.readString();
                            ProviderProperties _arg122 = (ProviderProperties) data.readTypedObject(ProviderProperties.CREATOR);
                            List<String> _arg217 = data.createStringArrayList();
                            String _arg314 = data.readString();
                            String _arg48 = data.readString();
                            data.enforceNoDataAvail();
                            addTestProvider(_arg042, _arg122, _arg217, _arg314, _arg48);
                            reply.writeNoException();
                            return true;
                        case 55:
                            String _arg043 = data.readString();
                            String _arg123 = data.readString();
                            String _arg218 = data.readString();
                            data.enforceNoDataAvail();
                            removeTestProvider(_arg043, _arg123, _arg218);
                            reply.writeNoException();
                            return true;
                        case 56:
                            String _arg044 = data.readString();
                            Location _arg124 = (Location) data.readTypedObject(Location.CREATOR);
                            String _arg219 = data.readString();
                            String _arg315 = data.readString();
                            data.enforceNoDataAvail();
                            setTestProviderLocation(_arg044, _arg124, _arg219, _arg315);
                            reply.writeNoException();
                            return true;
                        case 57:
                            String _arg045 = data.readString();
                            boolean _arg125 = data.readBoolean();
                            String _arg220 = data.readString();
                            String _arg316 = data.readString();
                            data.enforceNoDataAvail();
                            setTestProviderEnabled(_arg045, _arg125, _arg220, _arg316);
                            reply.writeNoException();
                            return true;
                        case 58:
                            LocationTime _result22 = getGnssTimeMillis();
                            reply.writeNoException();
                            reply.writeTypedObject(_result22, 1);
                            return true;
                        case 59:
                            String _arg046 = data.readString();
                            String _arg126 = data.readString();
                            Bundle _arg221 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            sendExtraCommand(_arg046, _arg126, _arg221);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg221, 1);
                            return true;
                        case 60:
                            String[] _result23 = getBackgroundThrottlingWhitelist();
                            reply.writeNoException();
                            reply.writeStringArray(_result23);
                            return true;
                        case 61:
                            PackageTagsList _result24 = getIgnoreSettingsAllowlist();
                            reply.writeNoException();
                            reply.writeTypedObject(_result24, 1);
                            return true;
                        case 62:
                            PackageTagsList _result25 = getAdasAllowlist();
                            reply.writeNoException();
                            reply.writeTypedObject(_result25, 1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ILocationManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.location.ILocationManager
            public Location getLastLocation(String provider, LastLocationRequest request, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    Location _result = (Location) _reply.readTypedObject(Location.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public ICancellationSignal getCurrentLocation(String provider, LocationRequest request, ILocationCallback callback, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void registerLocationListener(String provider, LocationRequest request, ILocationListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void unregisterLocationListener(ILocationListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void registerLocationPendingIntent(String provider, LocationRequest request, PendingIntent pendingIntent, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(request, 0);
                    _data.writeTypedObject(pendingIntent, 0);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void unregisterLocationPendingIntent(PendingIntent pendingIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pendingIntent, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void injectLocation(Location location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void requestListenerFlush(String provider, ILocationListener listener, int requestCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(requestCode);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void requestPendingIntentFlush(String provider, PendingIntent pendingIntent, int requestCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(pendingIntent, 0);
                    _data.writeInt(requestCode);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void requestGeofence(Geofence geofence, PendingIntent intent, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(geofence, 0);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void removeGeofence(PendingIntent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean geocoderIsPresent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeDouble(latitude);
                    _data.writeDouble(longitude);
                    _data.writeInt(maxResults);
                    _data.writeTypedObject(params, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, IGeocodeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(locationName);
                    _data.writeDouble(lowerLeftLatitude);
                    try {
                        _data.writeDouble(lowerLeftLongitude);
                        try {
                            _data.writeDouble(upperRightLatitude);
                        } catch (Throwable th) {
                            th = th;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
                try {
                    _data.writeDouble(upperRightLongitude);
                    try {
                        _data.writeInt(maxResults);
                        try {
                            _data.writeTypedObject(params, 0);
                            try {
                                _data.writeStrongInterface(listener);
                                try {
                                    this.mRemote.transact(14, _data, _reply, 0);
                                    _reply.readException();
                                    _reply.recycle();
                                    _data.recycle();
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th8) {
                    th = th8;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.location.ILocationManager
            public GnssCapabilities getGnssCapabilities() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    GnssCapabilities _result = (GnssCapabilities) _reply.readTypedObject(GnssCapabilities.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public int getGnssYearOfHardware() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public String getGnssHardwareModelName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public List<GnssAntennaInfo> getGnssAntennaInfos() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    List<GnssAntennaInfo> _result = _reply.createTypedArrayList(GnssAntennaInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void registerGnssStatusCallback(IGnssStatusListener callback, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void unregisterGnssStatusCallback(IGnssStatusListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void registerGnssNmeaCallback(IGnssNmeaListener callback, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void unregisterGnssNmeaCallback(IGnssNmeaListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void addGnssMeasurementsListener(GnssMeasurementRequest request, IGnssMeasurementsListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void removeGnssMeasurementsListener(IGnssMeasurementsListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void injectGnssMeasurementCorrections(GnssMeasurementCorrections corrections) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(corrections, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void addGnssNavigationMessageListener(IGnssNavigationMessageListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void addGnssAntennaInfoListener(IGnssAntennaInfoListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void removeGnssAntennaInfoListener(IGnssAntennaInfoListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void addProviderRequestListener(IProviderRequestListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void removeProviderRequestListener(IProviderRequestListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public int getGnssBatchSize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void startGnssBatch(long periodNanos, ILocationListener listener, String packageName, String attributionTag, String listenerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(periodNanos);
                    _data.writeStrongInterface(listener);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeString(listenerId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void flushGnssBatch() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void stopGnssBatch() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean hasProvider(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public List<String> getAllProviders() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public List<String> getProviders(Criteria criteria, boolean enabledOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(criteria, 0);
                    _data.writeBoolean(enabledOnly);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public String getBestProvider(Criteria criteria, boolean enabledOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(criteria, 0);
                    _data.writeBoolean(enabledOnly);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public ProviderProperties getProviderProperties(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    ProviderProperties _result = (ProviderProperties) _reply.readTypedObject(ProviderProperties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean isProviderPackage(String provider, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public List<String> getProviderPackages(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setExtraLocationControllerPackage(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public String getExtraLocationControllerPackage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setExtraLocationControllerPackageEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean isExtraLocationControllerPackageEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean isProviderEnabledForUser(String provider, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeInt(userId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean isLocationEnabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setLocationEnabledForUser(boolean enabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean isAdasGnssLocationEnabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setAdasGnssLocationEnabledForUser(boolean enabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public boolean isAutomotiveGnssSuspended() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setAutomotiveGnssSuspended(boolean suspended) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(suspended);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void addTestProvider(String name, ProviderProperties properties, List<String> locationTags, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeTypedObject(properties, 0);
                    _data.writeStringList(locationTags);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void removeTestProvider(String provider, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setTestProviderLocation(String provider, Location location, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(location, 0);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void setTestProviderEnabled(String provider, boolean enabled, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeBoolean(enabled);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public LocationTime getGnssTimeMillis() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    LocationTime _result = (LocationTime) _reply.readTypedObject(LocationTime.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public void sendExtraCommand(String provider, String command, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeString(command);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        extras.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public String[] getBackgroundThrottlingWhitelist() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public PackageTagsList getIgnoreSettingsAllowlist() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    PackageTagsList _result = (PackageTagsList) _reply.readTypedObject(PackageTagsList.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationManager
            public PackageTagsList getAdasAllowlist() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    PackageTagsList _result = (PackageTagsList) _reply.readTypedObject(PackageTagsList.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void injectLocation_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAllOf(new String[]{Manifest.C0000permission.LOCATION_HARDWARE, Manifest.C0000permission.ACCESS_FINE_LOCATION}, source);
        }

        protected void setExtraLocationControllerPackage_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        protected void setExtraLocationControllerPackageEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        protected void isAutomotiveGnssSuspended_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CONTROL_AUTOMOTIVE_GNSS, source);
        }

        protected void setAutomotiveGnssSuspended_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CONTROL_AUTOMOTIVE_GNSS, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 61;
        }
    }
}
