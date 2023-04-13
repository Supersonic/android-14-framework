package android.telephony.ims.aidl;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.DelegateRequest;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.telephony.ims.aidl.IRcsUceControllerCallback;
import android.telephony.ims.aidl.IRcsUcePublishStateCallback;
import android.telephony.ims.aidl.ISipDelegate;
import android.telephony.ims.aidl.ISipDelegateConnectionStateCallback;
import android.telephony.ims.aidl.ISipDelegateMessageCallback;
import com.android.ims.internal.IImsServiceFeatureCallback;
import com.android.internal.telephony.IIntegerConsumer;
import com.android.internal.telephony.ISipDialogStateCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IImsRcsController extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsRcsController";

    void createSipDelegate(int i, DelegateRequest delegateRequest, String str, ISipDelegateConnectionStateCallback iSipDelegateConnectionStateCallback, ISipDelegateMessageCallback iSipDelegateMessageCallback) throws RemoteException;

    void destroySipDelegate(int i, ISipDelegate iSipDelegate, int i2) throws RemoteException;

    void getImsRcsRegistrationState(int i, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void getImsRcsRegistrationTransportType(int i, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    int getUcePublishState(int i) throws RemoteException;

    boolean isAvailable(int i, int i2, int i3) throws RemoteException;

    boolean isCapable(int i, int i2, int i3) throws RemoteException;

    boolean isSipDelegateSupported(int i) throws RemoteException;

    boolean isUceSettingEnabled(int i, String str, String str2) throws RemoteException;

    void registerImsRegistrationCallback(int i, IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException;

    void registerRcsAvailabilityCallback(int i, IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void registerRcsFeatureCallback(int i, IImsServiceFeatureCallback iImsServiceFeatureCallback) throws RemoteException;

    void registerSipDialogStateCallback(int i, ISipDialogStateCallback iSipDialogStateCallback) throws RemoteException;

    void registerUcePublishStateCallback(int i, IRcsUcePublishStateCallback iRcsUcePublishStateCallback) throws RemoteException;

    void requestAvailability(int i, String str, String str2, Uri uri, IRcsUceControllerCallback iRcsUceControllerCallback) throws RemoteException;

    void requestCapabilities(int i, String str, String str2, List<Uri> list, IRcsUceControllerCallback iRcsUceControllerCallback) throws RemoteException;

    void setUceSettingEnabled(int i, boolean z) throws RemoteException;

    void triggerNetworkRegistration(int i, ISipDelegate iSipDelegate, int i2, String str) throws RemoteException;

    void unregisterImsFeatureCallback(IImsServiceFeatureCallback iImsServiceFeatureCallback) throws RemoteException;

    void unregisterImsRegistrationCallback(int i, IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException;

    void unregisterRcsAvailabilityCallback(int i, IImsCapabilityCallback iImsCapabilityCallback) throws RemoteException;

    void unregisterSipDialogStateCallback(int i, ISipDialogStateCallback iSipDialogStateCallback) throws RemoteException;

    void unregisterUcePublishStateCallback(int i, IRcsUcePublishStateCallback iRcsUcePublishStateCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsRcsController {
        @Override // android.telephony.ims.aidl.IImsRcsController
        public void registerImsRegistrationCallback(int subId, IImsRegistrationCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void unregisterImsRegistrationCallback(int subId, IImsRegistrationCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void getImsRcsRegistrationState(int subId, IIntegerConsumer consumer) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void getImsRcsRegistrationTransportType(int subId, IIntegerConsumer consumer) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void registerRcsAvailabilityCallback(int subId, IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void unregisterRcsAvailabilityCallback(int subId, IImsCapabilityCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public boolean isCapable(int subId, int capability, int radioTech) throws RemoteException {
            return false;
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public boolean isAvailable(int subId, int capability, int radioTech) throws RemoteException {
            return false;
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void requestCapabilities(int subId, String callingPackage, String callingFeatureId, List<Uri> contactNumbers, IRcsUceControllerCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void requestAvailability(int subId, String callingPackage, String callingFeatureId, Uri contactNumber, IRcsUceControllerCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public int getUcePublishState(int subId) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public boolean isUceSettingEnabled(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
            return false;
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void setUceSettingEnabled(int subId, boolean isEnabled) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void registerUcePublishStateCallback(int subId, IRcsUcePublishStateCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void unregisterUcePublishStateCallback(int subId, IRcsUcePublishStateCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public boolean isSipDelegateSupported(int subId) throws RemoteException {
            return false;
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void createSipDelegate(int subId, DelegateRequest request, String packageName, ISipDelegateConnectionStateCallback delegateState, ISipDelegateMessageCallback delegateMessage) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void destroySipDelegate(int subId, ISipDelegate connection, int reason) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void triggerNetworkRegistration(int subId, ISipDelegate connection, int sipCode, String sipReason) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void registerSipDialogStateCallback(int subId, ISipDialogStateCallback cb) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void unregisterSipDialogStateCallback(int subId, ISipDialogStateCallback cb) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void registerRcsFeatureCallback(int slotId, IImsServiceFeatureCallback callback) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRcsController
        public void unregisterImsFeatureCallback(IImsServiceFeatureCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsRcsController {
        static final int TRANSACTION_createSipDelegate = 17;
        static final int TRANSACTION_destroySipDelegate = 18;
        static final int TRANSACTION_getImsRcsRegistrationState = 3;
        static final int TRANSACTION_getImsRcsRegistrationTransportType = 4;
        static final int TRANSACTION_getUcePublishState = 11;
        static final int TRANSACTION_isAvailable = 8;
        static final int TRANSACTION_isCapable = 7;
        static final int TRANSACTION_isSipDelegateSupported = 16;
        static final int TRANSACTION_isUceSettingEnabled = 12;
        static final int TRANSACTION_registerImsRegistrationCallback = 1;
        static final int TRANSACTION_registerRcsAvailabilityCallback = 5;
        static final int TRANSACTION_registerRcsFeatureCallback = 22;
        static final int TRANSACTION_registerSipDialogStateCallback = 20;
        static final int TRANSACTION_registerUcePublishStateCallback = 14;
        static final int TRANSACTION_requestAvailability = 10;
        static final int TRANSACTION_requestCapabilities = 9;
        static final int TRANSACTION_setUceSettingEnabled = 13;
        static final int TRANSACTION_triggerNetworkRegistration = 19;
        static final int TRANSACTION_unregisterImsFeatureCallback = 23;
        static final int TRANSACTION_unregisterImsRegistrationCallback = 2;
        static final int TRANSACTION_unregisterRcsAvailabilityCallback = 6;
        static final int TRANSACTION_unregisterSipDialogStateCallback = 21;
        static final int TRANSACTION_unregisterUcePublishStateCallback = 15;

        public Stub() {
            attachInterface(this, IImsRcsController.DESCRIPTOR);
        }

        public static IImsRcsController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsRcsController.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsRcsController)) {
                return (IImsRcsController) iin;
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
                    return "registerImsRegistrationCallback";
                case 2:
                    return "unregisterImsRegistrationCallback";
                case 3:
                    return "getImsRcsRegistrationState";
                case 4:
                    return "getImsRcsRegistrationTransportType";
                case 5:
                    return "registerRcsAvailabilityCallback";
                case 6:
                    return "unregisterRcsAvailabilityCallback";
                case 7:
                    return "isCapable";
                case 8:
                    return "isAvailable";
                case 9:
                    return "requestCapabilities";
                case 10:
                    return "requestAvailability";
                case 11:
                    return "getUcePublishState";
                case 12:
                    return "isUceSettingEnabled";
                case 13:
                    return "setUceSettingEnabled";
                case 14:
                    return "registerUcePublishStateCallback";
                case 15:
                    return "unregisterUcePublishStateCallback";
                case 16:
                    return "isSipDelegateSupported";
                case 17:
                    return "createSipDelegate";
                case 18:
                    return "destroySipDelegate";
                case 19:
                    return "triggerNetworkRegistration";
                case 20:
                    return "registerSipDialogStateCallback";
                case 21:
                    return "unregisterSipDialogStateCallback";
                case 22:
                    return "registerRcsFeatureCallback";
                case 23:
                    return "unregisterImsFeatureCallback";
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
                data.enforceInterface(IImsRcsController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsRcsController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            IImsRegistrationCallback _arg1 = IImsRegistrationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerImsRegistrationCallback(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            IImsRegistrationCallback _arg12 = IImsRegistrationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterImsRegistrationCallback(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            IIntegerConsumer _arg13 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getImsRcsRegistrationState(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            IIntegerConsumer _arg14 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getImsRcsRegistrationTransportType(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            IImsCapabilityCallback _arg15 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerRcsAvailabilityCallback(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            IImsCapabilityCallback _arg16 = IImsCapabilityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterRcsAvailabilityCallback(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = isCapable(_arg07, _arg17, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg18 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = isAvailable(_arg08, _arg18, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            String _arg19 = data.readString();
                            String _arg23 = data.readString();
                            List<Uri> _arg3 = data.createTypedArrayList(Uri.CREATOR);
                            IRcsUceControllerCallback _arg4 = IRcsUceControllerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestCapabilities(_arg09, _arg19, _arg23, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            String _arg110 = data.readString();
                            String _arg24 = data.readString();
                            Uri _arg32 = (Uri) data.readTypedObject(Uri.CREATOR);
                            IRcsUceControllerCallback _arg42 = IRcsUceControllerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestAvailability(_arg010, _arg110, _arg24, _arg32, _arg42);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = getUcePublishState(_arg011);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            String _arg111 = data.readString();
                            String _arg25 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = isUceSettingEnabled(_arg012, _arg111, _arg25);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setUceSettingEnabled(_arg013, _arg112);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            IRcsUcePublishStateCallback _arg113 = IRcsUcePublishStateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerUcePublishStateCallback(_arg014, _arg113);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            IRcsUcePublishStateCallback _arg114 = IRcsUcePublishStateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterUcePublishStateCallback(_arg015, _arg114);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = isSipDelegateSupported(_arg016);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            DelegateRequest _arg115 = (DelegateRequest) data.readTypedObject(DelegateRequest.CREATOR);
                            String _arg26 = data.readString();
                            ISipDelegateConnectionStateCallback _arg33 = ISipDelegateConnectionStateCallback.Stub.asInterface(data.readStrongBinder());
                            ISipDelegateMessageCallback _arg43 = ISipDelegateMessageCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createSipDelegate(_arg017, _arg115, _arg26, _arg33, _arg43);
                            reply.writeNoException();
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            ISipDelegate _arg116 = ISipDelegate.Stub.asInterface(data.readStrongBinder());
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            destroySipDelegate(_arg018, _arg116, _arg27);
                            reply.writeNoException();
                            break;
                        case 19:
                            int _arg019 = data.readInt();
                            ISipDelegate _arg117 = ISipDelegate.Stub.asInterface(data.readStrongBinder());
                            int _arg28 = data.readInt();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            triggerNetworkRegistration(_arg019, _arg117, _arg28, _arg34);
                            reply.writeNoException();
                            break;
                        case 20:
                            int _arg020 = data.readInt();
                            ISipDialogStateCallback _arg118 = ISipDialogStateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerSipDialogStateCallback(_arg020, _arg118);
                            reply.writeNoException();
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            ISipDialogStateCallback _arg119 = ISipDialogStateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterSipDialogStateCallback(_arg021, _arg119);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            IImsServiceFeatureCallback _arg120 = IImsServiceFeatureCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerRcsFeatureCallback(_arg022, _arg120);
                            reply.writeNoException();
                            break;
                        case 23:
                            IImsServiceFeatureCallback _arg023 = IImsServiceFeatureCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterImsFeatureCallback(_arg023);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IImsRcsController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsRcsController.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void registerImsRegistrationCallback(int subId, IImsRegistrationCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void unregisterImsRegistrationCallback(int subId, IImsRegistrationCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void getImsRcsRegistrationState(int subId, IIntegerConsumer consumer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(consumer);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void getImsRcsRegistrationTransportType(int subId, IIntegerConsumer consumer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(consumer);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void registerRcsAvailabilityCallback(int subId, IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void unregisterRcsAvailabilityCallback(int subId, IImsCapabilityCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public boolean isCapable(int subId, int capability, int radioTech) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(capability);
                    _data.writeInt(radioTech);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public boolean isAvailable(int subId, int capability, int radioTech) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(capability);
                    _data.writeInt(radioTech);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void requestCapabilities(int subId, String callingPackage, String callingFeatureId, List<Uri> contactNumbers, IRcsUceControllerCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedList(contactNumbers, 0);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void requestAvailability(int subId, String callingPackage, String callingFeatureId, Uri contactNumber, IRcsUceControllerCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(contactNumber, 0);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public int getUcePublishState(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public boolean isUceSettingEnabled(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void setUceSettingEnabled(int subId, boolean isEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeBoolean(isEnabled);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void registerUcePublishStateCallback(int subId, IRcsUcePublishStateCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void unregisterUcePublishStateCallback(int subId, IRcsUcePublishStateCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public boolean isSipDelegateSupported(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void createSipDelegate(int subId, DelegateRequest request, String packageName, ISipDelegateConnectionStateCallback delegateState, ISipDelegateMessageCallback delegateMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(delegateState);
                    _data.writeStrongInterface(delegateMessage);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void destroySipDelegate(int subId, ISipDelegate connection, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(connection);
                    _data.writeInt(reason);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void triggerNetworkRegistration(int subId, ISipDelegate connection, int sipCode, String sipReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(connection);
                    _data.writeInt(sipCode);
                    _data.writeString(sipReason);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void registerSipDialogStateCallback(int subId, ISipDialogStateCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void unregisterSipDialogStateCallback(int subId, ISipDialogStateCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void registerRcsFeatureCallback(int slotId, IImsServiceFeatureCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRcsController
            public void unregisterImsFeatureCallback(IImsServiceFeatureCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRcsController.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 22;
        }
    }
}
