package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsMmTelFeature;
import android.telephony.ims.aidl.IImsRcsFeature;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsServiceControllerListener;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.stub.ImsFeatureConfiguration;
import com.android.ims.internal.IImsFeatureStatusCallback;
/* loaded from: classes3.dex */
public interface IImsServiceController extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsServiceController";

    void addFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) throws RemoteException;

    IImsMmTelFeature createEmergencyOnlyMmTelFeature(int i) throws RemoteException;

    IImsMmTelFeature createMmTelFeature(int i, int i2) throws RemoteException;

    IImsRcsFeature createRcsFeature(int i, int i2) throws RemoteException;

    void disableIms(int i, int i2) throws RemoteException;

    void enableIms(int i, int i2) throws RemoteException;

    IImsConfig getConfig(int i, int i2) throws RemoteException;

    long getImsServiceCapabilities() throws RemoteException;

    IImsRegistration getRegistration(int i, int i2) throws RemoteException;

    ISipTransport getSipTransport(int i) throws RemoteException;

    void notifyImsServiceReadyForFeatureCreation() throws RemoteException;

    ImsFeatureConfiguration querySupportedImsFeatures() throws RemoteException;

    void removeFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) throws RemoteException;

    void removeImsFeature(int i, int i2, boolean z) throws RemoteException;

    void setListener(IImsServiceControllerListener iImsServiceControllerListener) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsServiceController {
        @Override // android.telephony.ims.aidl.IImsServiceController
        public void setListener(IImsServiceControllerListener l) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsMmTelFeature createMmTelFeature(int slotId, int subId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsMmTelFeature createEmergencyOnlyMmTelFeature(int slotId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsRcsFeature createRcsFeature(int slotId, int subId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public ImsFeatureConfiguration querySupportedImsFeatures() throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public long getImsServiceCapabilities() throws RemoteException {
            return 0L;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void addFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void removeFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void notifyImsServiceReadyForFeatureCreation() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void removeImsFeature(int slotId, int featureType, boolean changeSubId) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsConfig getConfig(int slotId, int subId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsRegistration getRegistration(int slotId, int subId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public ISipTransport getSipTransport(int slotId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void enableIms(int slotId, int subId) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void disableIms(int slotId, int subId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsServiceController {
        static final int TRANSACTION_addFeatureStatusCallback = 7;
        static final int TRANSACTION_createEmergencyOnlyMmTelFeature = 3;
        static final int TRANSACTION_createMmTelFeature = 2;
        static final int TRANSACTION_createRcsFeature = 4;
        static final int TRANSACTION_disableIms = 15;
        static final int TRANSACTION_enableIms = 14;
        static final int TRANSACTION_getConfig = 11;
        static final int TRANSACTION_getImsServiceCapabilities = 6;
        static final int TRANSACTION_getRegistration = 12;
        static final int TRANSACTION_getSipTransport = 13;
        static final int TRANSACTION_notifyImsServiceReadyForFeatureCreation = 9;
        static final int TRANSACTION_querySupportedImsFeatures = 5;
        static final int TRANSACTION_removeFeatureStatusCallback = 8;
        static final int TRANSACTION_removeImsFeature = 10;
        static final int TRANSACTION_setListener = 1;

        public Stub() {
            attachInterface(this, IImsServiceController.DESCRIPTOR);
        }

        public static IImsServiceController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsServiceController.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsServiceController)) {
                return (IImsServiceController) iin;
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
                    return "setListener";
                case 2:
                    return "createMmTelFeature";
                case 3:
                    return "createEmergencyOnlyMmTelFeature";
                case 4:
                    return "createRcsFeature";
                case 5:
                    return "querySupportedImsFeatures";
                case 6:
                    return "getImsServiceCapabilities";
                case 7:
                    return "addFeatureStatusCallback";
                case 8:
                    return "removeFeatureStatusCallback";
                case 9:
                    return "notifyImsServiceReadyForFeatureCreation";
                case 10:
                    return "removeImsFeature";
                case 11:
                    return "getConfig";
                case 12:
                    return "getRegistration";
                case 13:
                    return "getSipTransport";
                case 14:
                    return "enableIms";
                case 15:
                    return "disableIms";
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
                data.enforceInterface(IImsServiceController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsServiceController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IImsServiceControllerListener _arg0 = IImsServiceControllerListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsMmTelFeature _result = createMmTelFeature(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsMmTelFeature _result2 = createEmergencyOnlyMmTelFeature(_arg03);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsRcsFeature _result3 = createRcsFeature(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 5:
                            ImsFeatureConfiguration _result4 = querySupportedImsFeatures();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 6:
                            long _result5 = getImsServiceCapabilities();
                            reply.writeNoException();
                            reply.writeLong(_result5);
                            break;
                        case 7:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            IImsFeatureStatusCallback _arg2 = IImsFeatureStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addFeatureStatusCallback(_arg05, _arg13, _arg2);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg06 = data.readInt();
                            int _arg14 = data.readInt();
                            IImsFeatureStatusCallback _arg22 = IImsFeatureStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeFeatureStatusCallback(_arg06, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 9:
                            notifyImsServiceReadyForFeatureCreation();
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            int _arg15 = data.readInt();
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            removeImsFeature(_arg07, _arg15, _arg23);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg08 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsConfig _result6 = getConfig(_arg08, _arg16);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 12:
                            int _arg09 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsRegistration _result7 = getRegistration(_arg09, _arg17);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result7);
                            break;
                        case 13:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            ISipTransport _result8 = getSipTransport(_arg010);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result8);
                            break;
                        case 14:
                            int _arg011 = data.readInt();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            enableIms(_arg011, _arg18);
                            break;
                        case 15:
                            int _arg012 = data.readInt();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            disableIms(_arg012, _arg19);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IImsServiceController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsServiceController.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void setListener(IImsServiceControllerListener l) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeStrongInterface(l);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public IImsMmTelFeature createMmTelFeature(int slotId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(subId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IImsMmTelFeature _result = IImsMmTelFeature.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public IImsMmTelFeature createEmergencyOnlyMmTelFeature(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    IImsMmTelFeature _result = IImsMmTelFeature.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public IImsRcsFeature createRcsFeature(int slotId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(subId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    IImsRcsFeature _result = IImsRcsFeature.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public ImsFeatureConfiguration querySupportedImsFeatures() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    ImsFeatureConfiguration _result = (ImsFeatureConfiguration) _reply.readTypedObject(ImsFeatureConfiguration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public long getImsServiceCapabilities() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void addFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(featureType);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void removeFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(featureType);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void notifyImsServiceReadyForFeatureCreation() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void removeImsFeature(int slotId, int featureType, boolean changeSubId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(featureType);
                    _data.writeBoolean(changeSubId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public IImsConfig getConfig(int slotId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(subId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    IImsConfig _result = IImsConfig.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public IImsRegistration getRegistration(int slotId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(subId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IImsRegistration _result = IImsRegistration.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public ISipTransport getSipTransport(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    ISipTransport _result = ISipTransport.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void enableIms(int slotId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(subId);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsServiceController
            public void disableIms(int slotId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsServiceController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(subId);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
