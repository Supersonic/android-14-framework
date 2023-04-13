package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import java.util.List;
/* loaded from: classes3.dex */
public interface IRcsUceControllerCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IRcsUceControllerCallback";

    void onCapabilitiesReceived(List<RcsContactUceCapability> list) throws RemoteException;

    void onComplete(SipDetails sipDetails) throws RemoteException;

    void onError(int i, long j, SipDetails sipDetails) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRcsUceControllerCallback {
        @Override // android.telephony.ims.aidl.IRcsUceControllerCallback
        public void onCapabilitiesReceived(List<RcsContactUceCapability> contactCapabilities) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IRcsUceControllerCallback
        public void onComplete(SipDetails details) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IRcsUceControllerCallback
        public void onError(int errorCode, long retryAfterMilliseconds, SipDetails details) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRcsUceControllerCallback {
        static final int TRANSACTION_onCapabilitiesReceived = 1;
        static final int TRANSACTION_onComplete = 2;
        static final int TRANSACTION_onError = 3;

        public Stub() {
            attachInterface(this, IRcsUceControllerCallback.DESCRIPTOR);
        }

        public static IRcsUceControllerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRcsUceControllerCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IRcsUceControllerCallback)) {
                return (IRcsUceControllerCallback) iin;
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
                    return "onCapabilitiesReceived";
                case 2:
                    return "onComplete";
                case 3:
                    return "onError";
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
                data.enforceInterface(IRcsUceControllerCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRcsUceControllerCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<RcsContactUceCapability> _arg0 = data.createTypedArrayList(RcsContactUceCapability.CREATOR);
                            data.enforceNoDataAvail();
                            onCapabilitiesReceived(_arg0);
                            break;
                        case 2:
                            SipDetails _arg02 = (SipDetails) data.readTypedObject(SipDetails.CREATOR);
                            data.enforceNoDataAvail();
                            onComplete(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            long _arg1 = data.readLong();
                            SipDetails _arg2 = (SipDetails) data.readTypedObject(SipDetails.CREATOR);
                            data.enforceNoDataAvail();
                            onError(_arg03, _arg1, _arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRcsUceControllerCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRcsUceControllerCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IRcsUceControllerCallback
            public void onCapabilitiesReceived(List<RcsContactUceCapability> contactCapabilities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsUceControllerCallback.DESCRIPTOR);
                    _data.writeTypedList(contactCapabilities, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IRcsUceControllerCallback
            public void onComplete(SipDetails details) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsUceControllerCallback.DESCRIPTOR);
                    _data.writeTypedObject(details, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IRcsUceControllerCallback
            public void onError(int errorCode, long retryAfterMilliseconds, SipDetails details) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsUceControllerCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    _data.writeLong(retryAfterMilliseconds);
                    _data.writeTypedObject(details, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
