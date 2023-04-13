package android.gsi;

import android.gsi.IProgressCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IImageService extends IInterface {
    public static final int CREATE_IMAGE_DEFAULT = 0;
    public static final int CREATE_IMAGE_READONLY = 1;
    public static final int CREATE_IMAGE_ZERO_FILL = 2;
    public static final String DESCRIPTOR = "android.gsi.IImageService";
    public static final int IMAGE_ERROR = 1;
    public static final int IMAGE_OK = 0;

    boolean backingImageExists(String str) throws RemoteException;

    void createBackingImage(String str, long j, int i, IProgressCallback iProgressCallback) throws RemoteException;

    void deleteBackingImage(String str) throws RemoteException;

    void disableImage(String str) throws RemoteException;

    List<String> getAllBackingImages() throws RemoteException;

    int getAvbPublicKey(String str, AvbPublicKey avbPublicKey) throws RemoteException;

    String getMappedImageDevice(String str) throws RemoteException;

    boolean isImageDisabled(String str) throws RemoteException;

    boolean isImageMapped(String str) throws RemoteException;

    void mapImageDevice(String str, int i, MappedImage mappedImage) throws RemoteException;

    void removeAllImages() throws RemoteException;

    void removeDisabledImages() throws RemoteException;

    void unmapImageDevice(String str) throws RemoteException;

    void zeroFillNewImage(String str, long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IImageService {
        @Override // android.gsi.IImageService
        public void createBackingImage(String name, long size, int flags, IProgressCallback on_progress) throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public void deleteBackingImage(String name) throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public void mapImageDevice(String name, int timeout_ms, MappedImage mapping) throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public void unmapImageDevice(String name) throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public boolean backingImageExists(String name) throws RemoteException {
            return false;
        }

        @Override // android.gsi.IImageService
        public boolean isImageMapped(String name) throws RemoteException {
            return false;
        }

        @Override // android.gsi.IImageService
        public int getAvbPublicKey(String name, AvbPublicKey dst) throws RemoteException {
            return 0;
        }

        @Override // android.gsi.IImageService
        public List<String> getAllBackingImages() throws RemoteException {
            return null;
        }

        @Override // android.gsi.IImageService
        public void zeroFillNewImage(String name, long bytes) throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public void removeAllImages() throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public void disableImage(String name) throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public void removeDisabledImages() throws RemoteException {
        }

        @Override // android.gsi.IImageService
        public boolean isImageDisabled(String name) throws RemoteException {
            return false;
        }

        @Override // android.gsi.IImageService
        public String getMappedImageDevice(String name) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IImageService {
        static final int TRANSACTION_backingImageExists = 5;
        static final int TRANSACTION_createBackingImage = 1;
        static final int TRANSACTION_deleteBackingImage = 2;
        static final int TRANSACTION_disableImage = 11;
        static final int TRANSACTION_getAllBackingImages = 8;
        static final int TRANSACTION_getAvbPublicKey = 7;
        static final int TRANSACTION_getMappedImageDevice = 14;
        static final int TRANSACTION_isImageDisabled = 13;
        static final int TRANSACTION_isImageMapped = 6;
        static final int TRANSACTION_mapImageDevice = 3;
        static final int TRANSACTION_removeAllImages = 10;
        static final int TRANSACTION_removeDisabledImages = 12;
        static final int TRANSACTION_unmapImageDevice = 4;
        static final int TRANSACTION_zeroFillNewImage = 9;

        public Stub() {
            attachInterface(this, IImageService.DESCRIPTOR);
        }

        public static IImageService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImageService.DESCRIPTOR);
            if (iin != null && (iin instanceof IImageService)) {
                return (IImageService) iin;
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
                    return "createBackingImage";
                case 2:
                    return "deleteBackingImage";
                case 3:
                    return "mapImageDevice";
                case 4:
                    return "unmapImageDevice";
                case 5:
                    return "backingImageExists";
                case 6:
                    return "isImageMapped";
                case 7:
                    return "getAvbPublicKey";
                case 8:
                    return "getAllBackingImages";
                case 9:
                    return "zeroFillNewImage";
                case 10:
                    return "removeAllImages";
                case 11:
                    return "disableImage";
                case 12:
                    return "removeDisabledImages";
                case 13:
                    return "isImageDisabled";
                case 14:
                    return "getMappedImageDevice";
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
                data.enforceInterface(IImageService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImageService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            long _arg1 = data.readLong();
                            int _arg2 = data.readInt();
                            IProgressCallback _arg3 = IProgressCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createBackingImage(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            deleteBackingImage(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg12 = data.readInt();
                            MappedImage _arg22 = new MappedImage();
                            data.enforceNoDataAvail();
                            mapImageDevice(_arg03, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg22, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            unmapImageDevice(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = backingImageExists(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = isImageMapped(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            AvbPublicKey _arg13 = new AvbPublicKey();
                            data.enforceNoDataAvail();
                            int _result3 = getAvbPublicKey(_arg07, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            reply.writeTypedObject(_arg13, 1);
                            break;
                        case 8:
                            List<String> _result4 = getAllBackingImages();
                            reply.writeNoException();
                            reply.writeStringList(_result4);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            long _arg14 = data.readLong();
                            data.enforceNoDataAvail();
                            zeroFillNewImage(_arg08, _arg14);
                            reply.writeNoException();
                            break;
                        case 10:
                            removeAllImages();
                            reply.writeNoException();
                            break;
                        case 11:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            disableImage(_arg09);
                            reply.writeNoException();
                            break;
                        case 12:
                            removeDisabledImages();
                            reply.writeNoException();
                            break;
                        case 13:
                            String _arg010 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result5 = isImageDisabled(_arg010);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 14:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            String _result6 = getMappedImageDevice(_arg011);
                            reply.writeNoException();
                            reply.writeString(_result6);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IImageService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImageService.DESCRIPTOR;
            }

            @Override // android.gsi.IImageService
            public void createBackingImage(String name, long size, int flags, IProgressCallback on_progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(size);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(on_progress);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void deleteBackingImage(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void mapImageDevice(String name, int timeout_ms, MappedImage mapping) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(timeout_ms);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        mapping.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void unmapImageDevice(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public boolean backingImageExists(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public boolean isImageMapped(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public int getAvbPublicKey(String name, AvbPublicKey dst) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (_reply.readInt() != 0) {
                        dst.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public List<String> getAllBackingImages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void zeroFillNewImage(String name, long bytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(bytes);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void removeAllImages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void disableImage(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public void removeDisabledImages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public boolean isImageDisabled(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.gsi.IImageService
            public String getMappedImageDevice(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageService.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
