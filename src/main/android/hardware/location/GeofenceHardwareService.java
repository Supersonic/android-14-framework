package android.hardware.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.location.IGeofenceHardware;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.p008os.Binder;
import android.p008os.IBinder;
/* loaded from: classes2.dex */
public class GeofenceHardwareService extends Service {
    private IBinder mBinder = new IGeofenceHardware.Stub() { // from class: android.hardware.location.GeofenceHardwareService.1
        @Override // android.hardware.location.IGeofenceHardware
        public void setGpsGeofenceHardware(IGpsGeofenceHardware service) {
            GeofenceHardwareService.this.mGeofenceHardwareImpl.setGpsHardwareGeofence(service);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public void setFusedGeofenceHardware(IFusedGeofenceHardware service) {
            GeofenceHardwareService.this.mGeofenceHardwareImpl.setFusedGeofenceHardware(service);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public int[] getMonitoringTypes() {
            super.getMonitoringTypes_enforcePermission();
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.getMonitoringTypes();
        }

        @Override // android.hardware.location.IGeofenceHardware
        public int getStatusOfMonitoringType(int monitoringType) {
            super.getStatusOfMonitoringType_enforcePermission();
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.getStatusOfMonitoringType(monitoringType);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean addCircularFence(int monitoringType, GeofenceHardwareRequestParcelable request, IGeofenceHardwareCallback callback) {
            super.addCircularFence_enforcePermission();
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.addCircularFence(monitoringType, request, callback);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean removeGeofence(int id, int monitoringType) {
            super.removeGeofence_enforcePermission();
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.removeGeofence(id, monitoringType);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean pauseGeofence(int id, int monitoringType) {
            super.pauseGeofence_enforcePermission();
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.pauseGeofence(id, monitoringType);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) {
            super.resumeGeofence_enforcePermission();
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.resumeGeofence(id, monitoringType, monitorTransitions);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
            super.registerForMonitorStateChangeCallback_enforcePermission();
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.registerForMonitorStateChangeCallback(monitoringType, callback);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
            super.unregisterForMonitorStateChangeCallback_enforcePermission();
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.unregisterForMonitorStateChangeCallback(monitoringType, callback);
        }
    };
    private Context mContext;
    private GeofenceHardwareImpl mGeofenceHardwareImpl;

    @Override // android.app.Service
    public void onCreate() {
        this.mContext = this;
        this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mGeofenceHardwareImpl = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPermission(int pid, int uid, int monitoringType) {
        if (this.mGeofenceHardwareImpl.getAllowedResolutionLevel(pid, uid) < this.mGeofenceHardwareImpl.getMonitoringResolutionLevel(monitoringType)) {
            throw new SecurityException("Insufficient permissions to access hardware geofence for type: " + monitoringType);
        }
    }
}
