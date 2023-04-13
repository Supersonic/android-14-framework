package android.telephony.mbms.vendor;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.mbms.IMbmsStreamingSessionCallback;
import android.telephony.mbms.IStreamingServiceCallback;
import android.telephony.mbms.MbmsStreamingSessionCallback;
import android.telephony.mbms.StreamingServiceCallback;
import android.telephony.mbms.StreamingServiceInfo;
import android.telephony.mbms.vendor.IMbmsStreamingService;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public class MbmsStreamingServiceBase extends IMbmsStreamingService.Stub {
    public int initialize(MbmsStreamingSessionCallback callback, int subscriptionId) throws RemoteException {
        return 0;
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService
    public final int initialize(final IMbmsStreamingSessionCallback callback, final int subscriptionId) throws RemoteException {
        if (callback == null) {
            throw new NullPointerException("Callback must not be null");
        }
        final int uid = Binder.getCallingUid();
        int result = initialize(new MbmsStreamingSessionCallback() { // from class: android.telephony.mbms.vendor.MbmsStreamingServiceBase.1
            @Override // android.telephony.mbms.MbmsStreamingSessionCallback
            public void onError(int errorCode, String message) {
                try {
                    if (errorCode == -1) {
                        throw new IllegalArgumentException("Middleware cannot send an unknown error.");
                    }
                    callback.onError(errorCode, message);
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }

            @Override // android.telephony.mbms.MbmsStreamingSessionCallback
            public void onStreamingServicesUpdated(List<StreamingServiceInfo> services) {
                try {
                    callback.onStreamingServicesUpdated(services);
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }

            @Override // android.telephony.mbms.MbmsStreamingSessionCallback
            public void onMiddlewareReady() {
                try {
                    callback.onMiddlewareReady();
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }
        }, subscriptionId);
        if (result == 0) {
            callback.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: android.telephony.mbms.vendor.MbmsStreamingServiceBase.2
                @Override // android.p008os.IBinder.DeathRecipient
                public void binderDied() {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }, 0);
        }
        return result;
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService
    public int requestUpdateStreamingServices(int subscriptionId, List<String> serviceClasses) throws RemoteException {
        return 0;
    }

    public int startStreaming(int subscriptionId, String serviceId, StreamingServiceCallback callback) throws RemoteException {
        return 0;
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService
    public int startStreaming(final int subscriptionId, String serviceId, final IStreamingServiceCallback callback) throws RemoteException {
        if (callback == null) {
            throw new NullPointerException("Callback must not be null");
        }
        final int uid = Binder.getCallingUid();
        int result = startStreaming(subscriptionId, serviceId, new StreamingServiceCallback() { // from class: android.telephony.mbms.vendor.MbmsStreamingServiceBase.3
            @Override // android.telephony.mbms.StreamingServiceCallback
            public void onError(int errorCode, String message) {
                try {
                    if (errorCode == -1) {
                        throw new IllegalArgumentException("Middleware cannot send an unknown error.");
                    }
                    callback.onError(errorCode, message);
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }

            @Override // android.telephony.mbms.StreamingServiceCallback
            public void onStreamStateUpdated(int state, int reason) {
                try {
                    callback.onStreamStateUpdated(state, reason);
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }

            @Override // android.telephony.mbms.StreamingServiceCallback
            public void onMediaDescriptionUpdated() {
                try {
                    callback.onMediaDescriptionUpdated();
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }

            @Override // android.telephony.mbms.StreamingServiceCallback
            public void onBroadcastSignalStrengthUpdated(int signalStrength) {
                try {
                    callback.onBroadcastSignalStrengthUpdated(signalStrength);
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }

            @Override // android.telephony.mbms.StreamingServiceCallback
            public void onStreamMethodUpdated(int methodType) {
                try {
                    callback.onStreamMethodUpdated(methodType);
                } catch (RemoteException e) {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }
        });
        if (result == 0) {
            callback.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: android.telephony.mbms.vendor.MbmsStreamingServiceBase.4
                @Override // android.p008os.IBinder.DeathRecipient
                public void binderDied() {
                    MbmsStreamingServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                }
            }, 0);
        }
        return result;
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService
    public Uri getPlaybackUri(int subscriptionId, String serviceId) throws RemoteException {
        return null;
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService
    public void stopStreaming(int subscriptionId, String serviceId) throws RemoteException {
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService
    public void dispose(int subscriptionId) throws RemoteException {
    }

    public void onAppCallbackDied(int uid, int subscriptionId) {
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService.Stub, android.p008os.IInterface
    @SystemApi
    public IBinder asBinder() {
        return super.asBinder();
    }

    @Override // android.telephony.mbms.vendor.IMbmsStreamingService.Stub, android.p008os.Binder
    @SystemApi
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return super.onTransact(code, data, reply, flags);
    }
}
