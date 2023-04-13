package android.telephony.mbms.vendor;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.mbms.GroupCallCallback;
import android.telephony.mbms.IGroupCallCallback;
import android.telephony.mbms.IMbmsGroupCallSessionCallback;
import android.telephony.mbms.MbmsGroupCallSessionCallback;
import android.telephony.mbms.vendor.IMbmsGroupCallService;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public class MbmsGroupCallServiceBase extends Service {
    private final IBinder mInterface = new IMbmsGroupCallService.Stub() { // from class: android.telephony.mbms.vendor.MbmsGroupCallServiceBase.1
        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public int initialize(final IMbmsGroupCallSessionCallback callback, final int subscriptionId) throws RemoteException {
            if (callback == null) {
                throw new NullPointerException("Callback must not be null");
            }
            final int uid = Binder.getCallingUid();
            int result = MbmsGroupCallServiceBase.this.initialize(new MbmsGroupCallSessionCallback() { // from class: android.telephony.mbms.vendor.MbmsGroupCallServiceBase.1.1
                @Override // android.telephony.mbms.MbmsGroupCallSessionCallback
                public void onError(int errorCode, String message) {
                    try {
                        if (errorCode == -1) {
                            throw new IllegalArgumentException("Middleware cannot send an unknown error.");
                        }
                        callback.onError(errorCode, message);
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }

                @Override // android.telephony.mbms.MbmsGroupCallSessionCallback
                public void onAvailableSaisUpdated(List currentSais, List availableSais) {
                    try {
                        callback.onAvailableSaisUpdated(currentSais, availableSais);
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }

                @Override // android.telephony.mbms.MbmsGroupCallSessionCallback
                public void onServiceInterfaceAvailable(String interfaceName, int index) {
                    try {
                        callback.onServiceInterfaceAvailable(interfaceName, index);
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }

                @Override // android.telephony.mbms.MbmsGroupCallSessionCallback
                public void onMiddlewareReady() {
                    try {
                        callback.onMiddlewareReady();
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }
            }, subscriptionId);
            if (result == 0) {
                callback.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: android.telephony.mbms.vendor.MbmsGroupCallServiceBase.1.2
                    @Override // android.p008os.IBinder.DeathRecipient
                    public void binderDied() {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }, 0);
            }
            return result;
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public void stopGroupCall(int subId, long tmgi) {
            MbmsGroupCallServiceBase.this.stopGroupCall(subId, tmgi);
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public void updateGroupCall(int subscriptionId, long tmgi, List saiList, List frequencyList) {
            MbmsGroupCallServiceBase.this.updateGroupCall(subscriptionId, tmgi, saiList, frequencyList);
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public int startGroupCall(final int subscriptionId, long tmgi, List saiList, List frequencyList, final IGroupCallCallback callback) throws RemoteException {
            if (callback == null) {
                throw new NullPointerException("Callback must not be null");
            }
            final int uid = Binder.getCallingUid();
            int result = MbmsGroupCallServiceBase.this.startGroupCall(subscriptionId, tmgi, saiList, frequencyList, new GroupCallCallback() { // from class: android.telephony.mbms.vendor.MbmsGroupCallServiceBase.1.3
                @Override // android.telephony.mbms.GroupCallCallback
                public void onError(int errorCode, String message) {
                    try {
                        if (errorCode == -1) {
                            throw new IllegalArgumentException("Middleware cannot send an unknown error.");
                        }
                        callback.onError(errorCode, message);
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }

                @Override // android.telephony.mbms.GroupCallCallback
                public void onGroupCallStateChanged(int state, int reason) {
                    try {
                        callback.onGroupCallStateChanged(state, reason);
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }

                @Override // android.telephony.mbms.GroupCallCallback
                public void onBroadcastSignalStrengthUpdated(int signalStrength) {
                    try {
                        callback.onBroadcastSignalStrengthUpdated(signalStrength);
                    } catch (RemoteException e) {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }
            });
            if (result == 0) {
                callback.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: android.telephony.mbms.vendor.MbmsGroupCallServiceBase.1.4
                    @Override // android.p008os.IBinder.DeathRecipient
                    public void binderDied() {
                        MbmsGroupCallServiceBase.this.onAppCallbackDied(uid, subscriptionId);
                    }
                }, 0);
            }
            return result;
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public void dispose(int subId) throws RemoteException {
            MbmsGroupCallServiceBase.this.dispose(subId);
        }
    };

    public int initialize(MbmsGroupCallSessionCallback callback, int subscriptionId) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int startGroupCall(int subscriptionId, long tmgi, List<Integer> saiList, List<Integer> frequencyList, GroupCallCallback callback) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void stopGroupCall(int subscriptionId, long tmgi) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void updateGroupCall(int subscriptionId, long tmgi, List<Integer> saiList, List<Integer> frequencyList) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void dispose(int subscriptionId) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void onAppCallbackDied(int uid, int subscriptionId) {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mInterface;
    }
}
