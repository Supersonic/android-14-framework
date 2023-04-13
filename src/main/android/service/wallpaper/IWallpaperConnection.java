package android.service.wallpaper;

import android.app.WallpaperColors;
import android.graphics.RectF;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.service.wallpaper.IWallpaperEngine;
/* loaded from: classes3.dex */
public interface IWallpaperConnection extends IInterface {
    void attachEngine(IWallpaperEngine iWallpaperEngine, int i) throws RemoteException;

    void engineShown(IWallpaperEngine iWallpaperEngine) throws RemoteException;

    void onLocalWallpaperColorsChanged(RectF rectF, WallpaperColors wallpaperColors, int i) throws RemoteException;

    void onWallpaperColorsChanged(WallpaperColors wallpaperColors, int i) throws RemoteException;

    ParcelFileDescriptor setWallpaper(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWallpaperConnection {
        @Override // android.service.wallpaper.IWallpaperConnection
        public void attachEngine(IWallpaperEngine engine, int displayId) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public void engineShown(IWallpaperEngine engine) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public ParcelFileDescriptor setWallpaper(String name) throws RemoteException {
            return null;
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public void onWallpaperColorsChanged(WallpaperColors colors, int displayId) throws RemoteException {
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public void onLocalWallpaperColorsChanged(RectF area, WallpaperColors colors, int displayId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWallpaperConnection {
        public static final String DESCRIPTOR = "android.service.wallpaper.IWallpaperConnection";
        static final int TRANSACTION_attachEngine = 1;
        static final int TRANSACTION_engineShown = 2;
        static final int TRANSACTION_onLocalWallpaperColorsChanged = 5;
        static final int TRANSACTION_onWallpaperColorsChanged = 4;
        static final int TRANSACTION_setWallpaper = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWallpaperConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWallpaperConnection)) {
                return (IWallpaperConnection) iin;
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
                    return "attachEngine";
                case 2:
                    return "engineShown";
                case 3:
                    return "setWallpaper";
                case 4:
                    return "onWallpaperColorsChanged";
                case 5:
                    return "onLocalWallpaperColorsChanged";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IWallpaperEngine _arg0 = IWallpaperEngine.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            attachEngine(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            IWallpaperEngine _arg02 = IWallpaperEngine.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            engineShown(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result = setWallpaper(_arg03);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 4:
                            WallpaperColors _arg04 = (WallpaperColors) data.readTypedObject(WallpaperColors.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onWallpaperColorsChanged(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            RectF _arg05 = (RectF) data.readTypedObject(RectF.CREATOR);
                            WallpaperColors _arg13 = (WallpaperColors) data.readTypedObject(WallpaperColors.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            onLocalWallpaperColorsChanged(_arg05, _arg13, _arg2);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IWallpaperConnection {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.service.wallpaper.IWallpaperConnection
            public void attachEngine(IWallpaperEngine engine, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(engine);
                    _data.writeInt(displayId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperConnection
            public void engineShown(IWallpaperEngine engine) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(engine);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperConnection
            public ParcelFileDescriptor setWallpaper(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperConnection
            public void onWallpaperColorsChanged(WallpaperColors colors, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(colors, 0);
                    _data.writeInt(displayId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.wallpaper.IWallpaperConnection
            public void onLocalWallpaperColorsChanged(RectF area, WallpaperColors colors, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(area, 0);
                    _data.writeTypedObject(colors, 0);
                    _data.writeInt(displayId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
