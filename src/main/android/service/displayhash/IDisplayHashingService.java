package android.service.displayhash;

import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.view.displayhash.DisplayHash;
/* loaded from: classes3.dex */
public interface IDisplayHashingService extends IInterface {
    public static final String DESCRIPTOR = "android.service.displayhash.IDisplayHashingService";

    void generateDisplayHash(byte[] bArr, HardwareBuffer hardwareBuffer, Rect rect, String str, RemoteCallback remoteCallback) throws RemoteException;

    void getDisplayHashAlgorithms(RemoteCallback remoteCallback) throws RemoteException;

    void getIntervalBetweenRequestsMillis(RemoteCallback remoteCallback) throws RemoteException;

    void verifyDisplayHash(byte[] bArr, DisplayHash displayHash, RemoteCallback remoteCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDisplayHashingService {
        @Override // android.service.displayhash.IDisplayHashingService
        public void generateDisplayHash(byte[] salt, HardwareBuffer buffer, Rect bounds, String hashAlgorithm, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void verifyDisplayHash(byte[] salt, DisplayHash displayHash, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void getDisplayHashAlgorithms(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void getIntervalBetweenRequestsMillis(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDisplayHashingService {
        static final int TRANSACTION_generateDisplayHash = 1;
        static final int TRANSACTION_getDisplayHashAlgorithms = 3;
        static final int TRANSACTION_getIntervalBetweenRequestsMillis = 4;
        static final int TRANSACTION_verifyDisplayHash = 2;

        public Stub() {
            attachInterface(this, IDisplayHashingService.DESCRIPTOR);
        }

        public static IDisplayHashingService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayHashingService.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayHashingService)) {
                return (IDisplayHashingService) iin;
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
                    return "generateDisplayHash";
                case 2:
                    return "verifyDisplayHash";
                case 3:
                    return "getDisplayHashAlgorithms";
                case 4:
                    return "getIntervalBetweenRequestsMillis";
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
                data.enforceInterface(IDisplayHashingService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayHashingService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            byte[] _arg0 = data.createByteArray();
                            HardwareBuffer _arg1 = (HardwareBuffer) data.readTypedObject(HardwareBuffer.CREATOR);
                            Rect _arg2 = (Rect) data.readTypedObject(Rect.CREATOR);
                            String _arg3 = data.readString();
                            RemoteCallback _arg4 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            generateDisplayHash(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            byte[] _arg02 = data.createByteArray();
                            DisplayHash _arg12 = (DisplayHash) data.readTypedObject(DisplayHash.CREATOR);
                            RemoteCallback _arg22 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            verifyDisplayHash(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            RemoteCallback _arg03 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getDisplayHashAlgorithms(_arg03);
                            break;
                        case 4:
                            RemoteCallback _arg04 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getIntervalBetweenRequestsMillis(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDisplayHashingService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayHashingService.DESCRIPTOR;
            }

            @Override // android.service.displayhash.IDisplayHashingService
            public void generateDisplayHash(byte[] salt, HardwareBuffer buffer, Rect bounds, String hashAlgorithm, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayHashingService.DESCRIPTOR);
                    _data.writeByteArray(salt);
                    _data.writeTypedObject(buffer, 0);
                    _data.writeTypedObject(bounds, 0);
                    _data.writeString(hashAlgorithm);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.displayhash.IDisplayHashingService
            public void verifyDisplayHash(byte[] salt, DisplayHash displayHash, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayHashingService.DESCRIPTOR);
                    _data.writeByteArray(salt);
                    _data.writeTypedObject(displayHash, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.displayhash.IDisplayHashingService
            public void getDisplayHashAlgorithms(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayHashingService.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.displayhash.IDisplayHashingService
            public void getIntervalBetweenRequestsMillis(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayHashingService.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
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
