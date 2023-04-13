package android.telephony.ims.aidl;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IOptionsRequestCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface ICapabilityExchangeEventListener extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.ICapabilityExchangeEventListener";

    void onPublishUpdated(SipDetails sipDetails) throws RemoteException;

    void onRemoteCapabilityRequest(Uri uri, List<String> list, IOptionsRequestCallback iOptionsRequestCallback) throws RemoteException;

    void onRequestPublishCapabilities(int i) throws RemoteException;

    void onUnpublish() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICapabilityExchangeEventListener {
        @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
        public void onRequestPublishCapabilities(int publishTriggerType) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
        public void onUnpublish() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
        public void onPublishUpdated(SipDetails details) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
        public void onRemoteCapabilityRequest(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback cb) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICapabilityExchangeEventListener {
        static final int TRANSACTION_onPublishUpdated = 3;
        static final int TRANSACTION_onRemoteCapabilityRequest = 4;
        static final int TRANSACTION_onRequestPublishCapabilities = 1;
        static final int TRANSACTION_onUnpublish = 2;

        public Stub() {
            attachInterface(this, ICapabilityExchangeEventListener.DESCRIPTOR);
        }

        public static ICapabilityExchangeEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICapabilityExchangeEventListener.DESCRIPTOR);
            if (iin != null && (iin instanceof ICapabilityExchangeEventListener)) {
                return (ICapabilityExchangeEventListener) iin;
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
                    return "onRequestPublishCapabilities";
                case 2:
                    return "onUnpublish";
                case 3:
                    return "onPublishUpdated";
                case 4:
                    return "onRemoteCapabilityRequest";
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
                data.enforceInterface(ICapabilityExchangeEventListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICapabilityExchangeEventListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onRequestPublishCapabilities(_arg0);
                            break;
                        case 2:
                            onUnpublish();
                            break;
                        case 3:
                            SipDetails _arg02 = (SipDetails) data.readTypedObject(SipDetails.CREATOR);
                            data.enforceNoDataAvail();
                            onPublishUpdated(_arg02);
                            break;
                        case 4:
                            Uri _arg03 = (Uri) data.readTypedObject(Uri.CREATOR);
                            List<String> _arg1 = data.createStringArrayList();
                            IOptionsRequestCallback _arg2 = IOptionsRequestCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onRemoteCapabilityRequest(_arg03, _arg1, _arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ICapabilityExchangeEventListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICapabilityExchangeEventListener.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
            public void onRequestPublishCapabilities(int publishTriggerType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICapabilityExchangeEventListener.DESCRIPTOR);
                    _data.writeInt(publishTriggerType);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
            public void onUnpublish() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICapabilityExchangeEventListener.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
            public void onPublishUpdated(SipDetails details) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICapabilityExchangeEventListener.DESCRIPTOR);
                    _data.writeTypedObject(details, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ICapabilityExchangeEventListener
            public void onRemoteCapabilityRequest(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICapabilityExchangeEventListener.DESCRIPTOR);
                    _data.writeTypedObject(contactUri, 0);
                    _data.writeStringList(remoteCapabilities);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
