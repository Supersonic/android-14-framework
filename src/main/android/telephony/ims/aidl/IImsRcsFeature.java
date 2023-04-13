package android.telephony.ims.aidl;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.aidl.ICapabilityExchangeEventListener;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IOptionsResponseCallback;
import android.telephony.ims.aidl.IPublishResponseCallback;
import android.telephony.ims.aidl.ISubscribeResponseCallback;
import android.telephony.ims.feature.CapabilityChangeRequest;
import java.util.List;
/* loaded from: classes3.dex */
public interface IImsRcsFeature extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsRcsFeature";

    void addCapabilityCallback(IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void changeCapabilitiesConfiguration(CapabilityChangeRequest capabilityChangeRequest, IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    int getFeatureState() throws RemoteException;

    void publishCapabilities(String str, IPublishResponseCallback iPublishResponseCallback) throws RemoteException;

    void queryCapabilityConfiguration(int i, int i2, IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    int queryCapabilityStatus() throws RemoteException;

    void removeCapabilityCallback(IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void sendOptionsCapabilityRequest(Uri uri, List<String> list, IOptionsResponseCallback iOptionsResponseCallback) throws RemoteException;

    void setCapabilityExchangeEventListener(ICapabilityExchangeEventListener iCapabilityExchangeEventListener) throws RemoteException;

    void subscribeForCapabilities(List<Uri> list, ISubscribeResponseCallback iSubscribeResponseCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsRcsFeature {
        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public int queryCapabilityStatus() throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public int getFeatureState() throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void addCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void removeCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void changeCapabilitiesConfiguration(CapabilityChangeRequest r, IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void queryCapabilityConfiguration(int capability, int radioTech, IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void setCapabilityExchangeEventListener(ICapabilityExchangeEventListener listener) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void publishCapabilities(String pidfXml, IPublishResponseCallback cb) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void subscribeForCapabilities(List<Uri> uris, ISubscribeResponseCallback cb) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsFeature
        public void sendOptionsCapabilityRequest(Uri contactUri, List<String> myCapabilities, IOptionsResponseCallback cb) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsRcsFeature {
        static final int TRANSACTION_addCapabilityCallback = 3;
        static final int TRANSACTION_changeCapabilitiesConfiguration = 5;
        static final int TRANSACTION_getFeatureState = 2;
        static final int TRANSACTION_publishCapabilities = 8;
        static final int TRANSACTION_queryCapabilityConfiguration = 6;
        static final int TRANSACTION_queryCapabilityStatus = 1;
        static final int TRANSACTION_removeCapabilityCallback = 4;
        static final int TRANSACTION_sendOptionsCapabilityRequest = 10;
        static final int TRANSACTION_setCapabilityExchangeEventListener = 7;
        static final int TRANSACTION_subscribeForCapabilities = 9;

        public Stub() {
            attachInterface(this, IImsRcsFeature.DESCRIPTOR);
        }

        public static IImsRcsFeature asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsRcsFeature.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsRcsFeature)) {
                return (IImsRcsFeature) iin;
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
                    return "queryCapabilityStatus";
                case 2:
                    return "getFeatureState";
                case 3:
                    return "addCapabilityCallback";
                case 4:
                    return "removeCapabilityCallback";
                case 5:
                    return "changeCapabilitiesConfiguration";
                case 6:
                    return "queryCapabilityConfiguration";
                case 7:
                    return "setCapabilityExchangeEventListener";
                case 8:
                    return "publishCapabilities";
                case 9:
                    return "subscribeForCapabilities";
                case 10:
                    return "sendOptionsCapabilityRequest";
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
                data.enforceInterface(IImsRcsFeature.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsRcsFeature.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _result = queryCapabilityStatus();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _result2 = getFeatureState();
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            IImsCapabilityCallback _arg0 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addCapabilityCallback(_arg0);
                            break;
                        case 4:
                            IImsCapabilityCallback _arg02 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeCapabilityCallback(_arg02);
                            break;
                        case 5:
                            CapabilityChangeRequest _arg03 = (CapabilityChangeRequest) data.readTypedObject(CapabilityChangeRequest.CREATOR);
                            IImsCapabilityCallback _arg1 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            changeCapabilitiesConfiguration(_arg03, _arg1);
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            IImsCapabilityCallback _arg2 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            queryCapabilityConfiguration(_arg04, _arg12, _arg2);
                            break;
                        case 7:
                            ICapabilityExchangeEventListener _arg05 = ICapabilityExchangeEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCapabilityExchangeEventListener(_arg05);
                            break;
                        case 8:
                            String _arg06 = data.readString();
                            IPublishResponseCallback _arg13 = IPublishResponseCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            publishCapabilities(_arg06, _arg13);
                            break;
                        case 9:
                            List<Uri> _arg07 = data.createTypedArrayList(Uri.CREATOR);
                            ISubscribeResponseCallback _arg14 = ISubscribeResponseCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            subscribeForCapabilities(_arg07, _arg14);
                            break;
                        case 10:
                            Uri _arg08 = (Uri) data.readTypedObject(Uri.CREATOR);
                            List<String> _arg15 = data.createStringArrayList();
                            IOptionsResponseCallback _arg22 = IOptionsResponseCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendOptionsCapabilityRequest(_arg08, _arg15, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IImsRcsFeature {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsRcsFeature.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public int queryCapabilityStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public int getFeatureState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void addCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void removeCapabilityCallback(IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void changeCapabilitiesConfiguration(CapabilityChangeRequest r, IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeTypedObject(r, 0);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void queryCapabilityConfiguration(int capability, int radioTech, IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeInt(capability);
                    _data.writeInt(radioTech);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void setCapabilityExchangeEventListener(ICapabilityExchangeEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void publishCapabilities(String pidfXml, IPublishResponseCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeString(pidfXml);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void subscribeForCapabilities(List<Uri> uris, ISubscribeResponseCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeTypedList(uris, 0);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsFeature
            public void sendOptionsCapabilityRequest(Uri contactUri, List<String> myCapabilities, IOptionsResponseCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRcsFeature.DESCRIPTOR);
                    _data.writeTypedObject(contactUri, 0);
                    _data.writeStringList(myCapabilities);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
