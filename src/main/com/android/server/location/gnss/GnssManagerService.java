package com.android.server.location.gnss;

import android.content.Context;
import android.content.Intent;
import android.hardware.location.GeofenceHardwareImpl;
import android.location.FusedBatchOptions;
import android.location.GnssAntennaInfo;
import android.location.GnssCapabilities;
import android.location.GnssMeasurementCorrections;
import android.location.GnssMeasurementRequest;
import android.location.IGnssAntennaInfoListener;
import android.location.IGnssMeasurementsListener;
import android.location.IGnssNavigationMessageListener;
import android.location.IGnssNmeaListener;
import android.location.IGnssStatusListener;
import android.location.IGpsGeofenceHardware;
import android.location.Location;
import android.location.util.identity.CallerIdentity;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.server.FgThread;
import com.android.server.location.gnss.GnssManagerService;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.injector.Injector;
import java.io.FileDescriptor;
import java.util.List;
/* loaded from: classes.dex */
public class GnssManagerService {

    /* renamed from: D */
    public static final boolean f1148D = Log.isLoggable("GnssManager", 3);
    public final GnssCapabilitiesHalModule mCapabilitiesHalModule;
    public final Context mContext;
    public final GnssGeofenceHalModule mGeofenceHalModule;
    public final GnssAntennaInfoProvider mGnssAntennaInfoProvider;
    public final IGpsGeofenceHardware mGnssGeofenceProxy;
    public final GnssLocationProvider mGnssLocationProvider;
    public final GnssMeasurementsProvider mGnssMeasurementsProvider;
    public final GnssMetrics mGnssMetrics;
    public final GnssNative mGnssNative;
    public final GnssNavigationMessageProvider mGnssNavigationMessageProvider;
    public final GnssNmeaProvider mGnssNmeaProvider;
    public final GnssStatusProvider mGnssStatusProvider;

    public GnssManagerService(Context context, Injector injector, GnssNative gnssNative) {
        Context createAttributionContext = context.createAttributionContext("GnssService");
        this.mContext = createAttributionContext;
        this.mGnssNative = gnssNative;
        GnssMetrics gnssMetrics = new GnssMetrics(createAttributionContext, IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats")), gnssNative);
        this.mGnssMetrics = gnssMetrics;
        this.mGnssLocationProvider = new GnssLocationProvider(createAttributionContext, gnssNative, gnssMetrics);
        this.mGnssStatusProvider = new GnssStatusProvider(injector, gnssNative);
        this.mGnssNmeaProvider = new GnssNmeaProvider(injector, gnssNative);
        this.mGnssMeasurementsProvider = new GnssMeasurementsProvider(injector, gnssNative);
        this.mGnssNavigationMessageProvider = new GnssNavigationMessageProvider(injector, gnssNative);
        this.mGnssAntennaInfoProvider = new GnssAntennaInfoProvider(gnssNative);
        this.mGnssGeofenceProxy = new GnssGeofenceProxy(gnssNative);
        this.mGeofenceHalModule = new GnssGeofenceHalModule(gnssNative);
        this.mCapabilitiesHalModule = new GnssCapabilitiesHalModule(gnssNative);
        gnssNative.register();
    }

    public void onSystemReady() {
        this.mGnssLocationProvider.onSystemReady();
    }

    public GnssLocationProvider getGnssLocationProvider() {
        return this.mGnssLocationProvider;
    }

    public void setAutomotiveGnssSuspended(boolean z) {
        this.mGnssLocationProvider.setAutomotiveGnssSuspended(z);
    }

    public boolean isAutomotiveGnssSuspended() {
        return this.mGnssLocationProvider.isAutomotiveGnssSuspended();
    }

    public IGpsGeofenceHardware getGnssGeofenceProxy() {
        return this.mGnssGeofenceProxy;
    }

    public int getGnssYearOfHardware() {
        return this.mGnssNative.getHardwareYear();
    }

    public String getGnssHardwareModelName() {
        return this.mGnssNative.getHardwareModelName();
    }

    public GnssCapabilities getGnssCapabilities() {
        return this.mGnssNative.getCapabilities();
    }

    public List<GnssAntennaInfo> getGnssAntennaInfos() {
        return this.mGnssAntennaInfoProvider.getAntennaInfos();
    }

    public int getGnssBatchSize() {
        return this.mGnssLocationProvider.getBatchSize();
    }

    public void registerGnssStatusCallback(IGnssStatusListener iGnssStatusListener, String str, String str2, String str3) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION", null);
        this.mGnssStatusProvider.addListener(CallerIdentity.fromBinder(this.mContext, str, str2, str3), iGnssStatusListener);
    }

    public void unregisterGnssStatusCallback(IGnssStatusListener iGnssStatusListener) {
        this.mGnssStatusProvider.removeListener(iGnssStatusListener);
    }

    public void registerGnssNmeaCallback(IGnssNmeaListener iGnssNmeaListener, String str, String str2, String str3) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION", null);
        this.mGnssNmeaProvider.addListener(CallerIdentity.fromBinder(this.mContext, str, str2, str3), iGnssNmeaListener);
    }

    public void unregisterGnssNmeaCallback(IGnssNmeaListener iGnssNmeaListener) {
        this.mGnssNmeaProvider.removeListener(iGnssNmeaListener);
    }

    public void addGnssMeasurementsListener(GnssMeasurementRequest gnssMeasurementRequest, IGnssMeasurementsListener iGnssMeasurementsListener, String str, String str2, String str3) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION", null);
        if (gnssMeasurementRequest.isCorrelationVectorOutputsEnabled()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", null);
        }
        this.mGnssMeasurementsProvider.addListener(gnssMeasurementRequest, CallerIdentity.fromBinder(this.mContext, str, str2, str3), iGnssMeasurementsListener);
    }

    public void injectGnssMeasurementCorrections(GnssMeasurementCorrections gnssMeasurementCorrections) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", null);
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION", null);
        if (this.mGnssNative.injectMeasurementCorrections(gnssMeasurementCorrections)) {
            return;
        }
        Log.w("GnssManager", "failed to inject GNSS measurement corrections");
    }

    public void removeGnssMeasurementsListener(IGnssMeasurementsListener iGnssMeasurementsListener) {
        this.mGnssMeasurementsProvider.removeListener(iGnssMeasurementsListener);
    }

    public void addGnssNavigationMessageListener(IGnssNavigationMessageListener iGnssNavigationMessageListener, String str, String str2, String str3) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION", null);
        this.mGnssNavigationMessageProvider.addListener(CallerIdentity.fromBinder(this.mContext, str, str2, str3), iGnssNavigationMessageListener);
    }

    public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener iGnssNavigationMessageListener) {
        this.mGnssNavigationMessageProvider.removeListener(iGnssNavigationMessageListener);
    }

    public void addGnssAntennaInfoListener(IGnssAntennaInfoListener iGnssAntennaInfoListener, String str, String str2, String str3) {
        this.mGnssAntennaInfoProvider.addListener(CallerIdentity.fromBinder(this.mContext, str, str2, str3), iGnssAntennaInfoListener);
    }

    public void removeGnssAntennaInfoListener(IGnssAntennaInfoListener iGnssAntennaInfoListener) {
        this.mGnssAntennaInfoProvider.removeListener(iGnssAntennaInfoListener);
    }

    public void sendNiResponse(int i, int i2) {
        try {
            this.mGnssLocationProvider.getNetInitiatedListener().sendNiResponse(i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void dump(FileDescriptor fileDescriptor, IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        if (strArr.length > 0 && strArr[0].equals("--gnssmetrics")) {
            indentingPrintWriter.append(this.mGnssMetrics.dumpGnssMetricsAsProtoString());
            return;
        }
        indentingPrintWriter.println("Capabilities: " + this.mGnssNative.getCapabilities());
        indentingPrintWriter.println("GNSS Hardware Model Name: " + getGnssHardwareModelName());
        if (this.mGnssStatusProvider.isSupported()) {
            indentingPrintWriter.println("Status Provider:");
            indentingPrintWriter.increaseIndent();
            this.mGnssStatusProvider.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mGnssMeasurementsProvider.isSupported()) {
            indentingPrintWriter.println("Measurements Provider:");
            indentingPrintWriter.increaseIndent();
            this.mGnssMeasurementsProvider.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mGnssNavigationMessageProvider.isSupported()) {
            indentingPrintWriter.println("Navigation Message Provider:");
            indentingPrintWriter.increaseIndent();
            this.mGnssNavigationMessageProvider.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mGnssAntennaInfoProvider.isSupported()) {
            indentingPrintWriter.println("Antenna Info Provider:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Antenna Infos: " + this.mGnssAntennaInfoProvider.getAntennaInfos());
            this.mGnssAntennaInfoProvider.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
        }
        GnssPowerStats powerStats = this.mGnssNative.getPowerStats();
        if (powerStats != null) {
            indentingPrintWriter.println("Last Power Stats:");
            indentingPrintWriter.increaseIndent();
            powerStats.dump(fileDescriptor, indentingPrintWriter, strArr, this.mGnssNative.getCapabilities());
            indentingPrintWriter.decreaseIndent();
        }
    }

    /* loaded from: classes.dex */
    public class GnssCapabilitiesHalModule implements GnssNative.BaseCallbacks {
        @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
        public void onHalRestarted() {
        }

        public GnssCapabilitiesHalModule(GnssNative gnssNative) {
            gnssNative.addBaseCallbacks(this);
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
        public void onCapabilitiesChanged(GnssCapabilities gnssCapabilities, GnssCapabilities gnssCapabilities2) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                GnssManagerService.this.mContext.sendBroadcastAsUser(new Intent("android.location.action.GNSS_CAPABILITIES_CHANGED").putExtra("android.location.extra.GNSS_CAPABILITIES", gnssCapabilities2).addFlags(1073741824).addFlags(268435456), UserHandle.ALL);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* loaded from: classes.dex */
    public class GnssGeofenceHalModule implements GnssNative.GeofenceCallbacks {
        public GeofenceHardwareImpl mGeofenceHardwareImpl;

        public final int translateGeofenceStatus(int i) {
            if (i != -149) {
                if (i != 0) {
                    if (i != 100) {
                        switch (i) {
                            case -103:
                                return 4;
                            case -102:
                                return 3;
                            case -101:
                                return 2;
                            default:
                                return -1;
                        }
                    }
                    return 1;
                }
                return 0;
            }
            return 5;
        }

        public GnssGeofenceHalModule(GnssNative gnssNative) {
            gnssNative.setGeofenceCallbacks(this);
        }

        public final synchronized GeofenceHardwareImpl getGeofenceHardware() {
            if (this.mGeofenceHardwareImpl == null) {
                this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(GnssManagerService.this.mContext);
            }
            return this.mGeofenceHardwareImpl;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReportGeofenceTransition$0(int i, Location location, int i2, long j) {
            getGeofenceHardware().reportGeofenceTransition(i, location, i2, j, 0, FusedBatchOptions.SourceTechnologies.GNSS);
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.GeofenceCallbacks
        public void onReportGeofenceTransition(final int i, final Location location, final int i2, final long j) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.gnss.GnssManagerService$GnssGeofenceHalModule$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    GnssManagerService.GnssGeofenceHalModule.this.lambda$onReportGeofenceTransition$0(i, location, i2, j);
                }
            });
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.GeofenceCallbacks
        public void onReportGeofenceStatus(final int i, final Location location) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.gnss.GnssManagerService$GnssGeofenceHalModule$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    GnssManagerService.GnssGeofenceHalModule.this.lambda$onReportGeofenceStatus$1(i, location);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReportGeofenceStatus$1(int i, Location location) {
            getGeofenceHardware().reportGeofenceMonitorStatus(0, i == 2 ? 0 : 1, location, FusedBatchOptions.SourceTechnologies.GNSS);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReportGeofenceAddStatus$2(int i, int i2) {
            getGeofenceHardware().reportGeofenceAddStatus(i, translateGeofenceStatus(i2));
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.GeofenceCallbacks
        public void onReportGeofenceAddStatus(final int i, final int i2) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.gnss.GnssManagerService$GnssGeofenceHalModule$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    GnssManagerService.GnssGeofenceHalModule.this.lambda$onReportGeofenceAddStatus$2(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReportGeofenceRemoveStatus$3(int i, int i2) {
            getGeofenceHardware().reportGeofenceRemoveStatus(i, translateGeofenceStatus(i2));
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.GeofenceCallbacks
        public void onReportGeofenceRemoveStatus(final int i, final int i2) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.gnss.GnssManagerService$GnssGeofenceHalModule$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    GnssManagerService.GnssGeofenceHalModule.this.lambda$onReportGeofenceRemoveStatus$3(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReportGeofencePauseStatus$4(int i, int i2) {
            getGeofenceHardware().reportGeofencePauseStatus(i, translateGeofenceStatus(i2));
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.GeofenceCallbacks
        public void onReportGeofencePauseStatus(final int i, final int i2) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.gnss.GnssManagerService$GnssGeofenceHalModule$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    GnssManagerService.GnssGeofenceHalModule.this.lambda$onReportGeofencePauseStatus$4(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReportGeofenceResumeStatus$5(int i, int i2) {
            getGeofenceHardware().reportGeofenceResumeStatus(i, translateGeofenceStatus(i2));
        }

        @Override // com.android.server.location.gnss.hal.GnssNative.GeofenceCallbacks
        public void onReportGeofenceResumeStatus(final int i, final int i2) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.gnss.GnssManagerService$GnssGeofenceHalModule$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GnssManagerService.GnssGeofenceHalModule.this.lambda$onReportGeofenceResumeStatus$5(i, i2);
                }
            });
        }
    }
}
