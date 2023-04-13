package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.DelegateRegistrationState;
import android.telephony.ims.FeatureTagState;
import android.telephony.ims.SipDelegateConfiguration;
import android.telephony.ims.SipDelegateImsConfiguration;
import android.telephony.ims.aidl.ISipDelegate;
import java.util.List;
/* loaded from: classes3.dex */
public interface ISipDelegateStateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.ISipDelegateStateCallback";

    void onConfigurationChanged(SipDelegateConfiguration sipDelegateConfiguration) throws RemoteException;

    void onCreated(ISipDelegate iSipDelegate, List<FeatureTagState> list) throws RemoteException;

    void onDestroyed(int i) throws RemoteException;

    void onFeatureTagRegistrationChanged(DelegateRegistrationState delegateRegistrationState) throws RemoteException;

    void onImsConfigurationChanged(SipDelegateImsConfiguration sipDelegateImsConfiguration) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISipDelegateStateCallback {
        @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
        public void onCreated(ISipDelegate c, List<FeatureTagState> deniedFeatureTags) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
        public void onFeatureTagRegistrationChanged(DelegateRegistrationState registrationState) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
        public void onImsConfigurationChanged(SipDelegateImsConfiguration registeredSipConfig) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
        public void onConfigurationChanged(SipDelegateConfiguration registeredSipConfig) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
        public void onDestroyed(int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISipDelegateStateCallback {
        static final int TRANSACTION_onConfigurationChanged = 4;
        static final int TRANSACTION_onCreated = 1;
        static final int TRANSACTION_onDestroyed = 5;
        static final int TRANSACTION_onFeatureTagRegistrationChanged = 2;
        static final int TRANSACTION_onImsConfigurationChanged = 3;

        public Stub() {
            attachInterface(this, ISipDelegateStateCallback.DESCRIPTOR);
        }

        public static ISipDelegateStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISipDelegateStateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISipDelegateStateCallback)) {
                return (ISipDelegateStateCallback) iin;
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
                    return "onCreated";
                case 2:
                    return "onFeatureTagRegistrationChanged";
                case 3:
                    return "onImsConfigurationChanged";
                case 4:
                    return "onConfigurationChanged";
                case 5:
                    return "onDestroyed";
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
                data.enforceInterface(ISipDelegateStateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISipDelegateStateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ISipDelegate _arg0 = ISipDelegate.Stub.asInterface(data.readStrongBinder());
                            List<FeatureTagState> _arg1 = data.createTypedArrayList(FeatureTagState.CREATOR);
                            data.enforceNoDataAvail();
                            onCreated(_arg0, _arg1);
                            break;
                        case 2:
                            DelegateRegistrationState _arg02 = (DelegateRegistrationState) data.readTypedObject(DelegateRegistrationState.CREATOR);
                            data.enforceNoDataAvail();
                            onFeatureTagRegistrationChanged(_arg02);
                            break;
                        case 3:
                            SipDelegateImsConfiguration _arg03 = (SipDelegateImsConfiguration) data.readTypedObject(SipDelegateImsConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            onImsConfigurationChanged(_arg03);
                            break;
                        case 4:
                            SipDelegateConfiguration _arg04 = (SipDelegateConfiguration) data.readTypedObject(SipDelegateConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            onConfigurationChanged(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onDestroyed(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISipDelegateStateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISipDelegateStateCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
            public void onCreated(ISipDelegate c, List<FeatureTagState> deniedFeatureTags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateStateCallback.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    _data.writeTypedList(deniedFeatureTags, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
            public void onFeatureTagRegistrationChanged(DelegateRegistrationState registrationState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(registrationState, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
            public void onImsConfigurationChanged(SipDelegateImsConfiguration registeredSipConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(registeredSipConfig, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
            public void onConfigurationChanged(SipDelegateConfiguration registeredSipConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(registeredSipConfig, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateStateCallback
            public void onDestroyed(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateStateCallback.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
