package com.android.server.location.altitude;

import android.content.Context;
import android.frameworks.location.altitude.AddMslAltitudeToLocationRequest;
import android.frameworks.location.altitude.AddMslAltitudeToLocationResponse;
import android.frameworks.location.altitude.IAltitudeService;
import android.location.Location;
import android.location.altitude.AltitudeConverter;
import android.os.RemoteException;
import com.android.server.SystemService;
import java.io.IOException;
/* loaded from: classes.dex */
public class AltitudeService extends IAltitudeService.Stub {
    public final AltitudeConverter mAltitudeConverter = new AltitudeConverter();
    public final Context mContext;

    @Override // android.frameworks.location.altitude.IAltitudeService
    public String getInterfaceHash() {
        return "763e0415cde10c922c590396b90bf622636470b1";
    }

    @Override // android.frameworks.location.altitude.IAltitudeService
    public int getInterfaceVersion() {
        return 1;
    }

    public AltitudeService(Context context) {
        this.mContext = context;
    }

    @Override // android.frameworks.location.altitude.IAltitudeService
    public AddMslAltitudeToLocationResponse addMslAltitudeToLocation(AddMslAltitudeToLocationRequest addMslAltitudeToLocationRequest) throws RemoteException {
        Location location = new Location("");
        location.setLatitude(addMslAltitudeToLocationRequest.latitudeDegrees);
        location.setLongitude(addMslAltitudeToLocationRequest.longitudeDegrees);
        location.setAltitude(addMslAltitudeToLocationRequest.altitudeMeters);
        location.setVerticalAccuracyMeters(addMslAltitudeToLocationRequest.verticalAccuracyMeters);
        try {
            this.mAltitudeConverter.addMslAltitudeToLocation(this.mContext, location);
            AddMslAltitudeToLocationResponse addMslAltitudeToLocationResponse = new AddMslAltitudeToLocationResponse();
            addMslAltitudeToLocationResponse.mslAltitudeMeters = location.getMslAltitudeMeters();
            addMslAltitudeToLocationResponse.mslAltitudeAccuracyMeters = location.getMslAltitudeAccuracyMeters();
            return addMslAltitudeToLocationResponse;
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public static final String SERVICE_NAME = IAltitudeService.DESCRIPTOR + "/default";
        public AltitudeService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            AltitudeService altitudeService = new AltitudeService(getContext());
            this.mService = altitudeService;
            publishBinderService(SERVICE_NAME, altitudeService);
        }
    }
}
