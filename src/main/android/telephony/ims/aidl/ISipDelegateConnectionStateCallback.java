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
public interface ISipDelegateConnectionStateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.ISipDelegateConnectionStateCallback";

    void onConfigurationChanged(SipDelegateConfiguration sipDelegateConfiguration) throws RemoteException;

    void onCreated(ISipDelegate iSipDelegate) throws RemoteException;

    void onDestroyed(int i) throws RemoteException;

    void onFeatureTagStatusChanged(DelegateRegistrationState delegateRegistrationState, List<FeatureTagState> list) throws RemoteException;

    void onImsConfigurationChanged(SipDelegateImsConfiguration sipDelegateImsConfiguration) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISipDelegateConnectionStateCallback {
        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onCreated(ISipDelegate c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onFeatureTagStatusChanged(DelegateRegistrationState registrationState, List<FeatureTagState> deniedFeatureTags) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onImsConfigurationChanged(SipDelegateImsConfiguration registeredSipConfig) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onConfigurationChanged(SipDelegateConfiguration registeredSipConfig) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onDestroyed(int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISipDelegateConnectionStateCallback {
        static final int TRANSACTION_onConfigurationChanged = 4;
        static final int TRANSACTION_onCreated = 1;
        static final int TRANSACTION_onDestroyed = 5;
        static final int TRANSACTION_onFeatureTagStatusChanged = 2;
        static final int TRANSACTION_onImsConfigurationChanged = 3;

        public Stub() {
            attachInterface(this, ISipDelegateConnectionStateCallback.DESCRIPTOR);
        }

        public static ISipDelegateConnectionStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISipDelegateConnectionStateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISipDelegateConnectionStateCallback)) {
                return (ISipDelegateConnectionStateCallback) iin;
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
                    return "onFeatureTagStatusChanged";
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
                data.enforceInterface(ISipDelegateConnectionStateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISipDelegateConnectionStateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ISipDelegate _arg0 = ISipDelegate.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCreated(_arg0);
                            break;
                        case 2:
                            DelegateRegistrationState _arg02 = (DelegateRegistrationState) data.readTypedObject(DelegateRegistrationState.CREATOR);
                            List<FeatureTagState> _arg1 = data.createTypedArrayList(FeatureTagState.CREATOR);
                            data.enforceNoDataAvail();
                            onFeatureTagStatusChanged(_arg02, _arg1);
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

        /* loaded from: classes3.dex */
        private static class Proxy implements ISipDelegateConnectionStateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISipDelegateConnectionStateCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
            public void onCreated(ISipDelegate c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateConnectionStateCallback.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
            public void onFeatureTagStatusChanged(DelegateRegistrationState registrationState, List<FeatureTagState> deniedFeatureTags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateConnectionStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(registrationState, 0);
                    _data.writeTypedList(deniedFeatureTags, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
            public void onImsConfigurationChanged(SipDelegateImsConfiguration registeredSipConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateConnectionStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(registeredSipConfig, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
            public void onConfigurationChanged(SipDelegateConfiguration registeredSipConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateConnectionStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(registeredSipConfig, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
            public void onDestroyed(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateConnectionStateCallback.DESCRIPTOR);
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
