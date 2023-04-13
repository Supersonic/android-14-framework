package android.app.wallpapereffectsgeneration;

import android.app.wallpapereffectsgeneration.ICinematicEffectListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IWallpaperEffectsGenerationManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager";

    void generateCinematicEffect(CinematicEffectRequest cinematicEffectRequest, ICinematicEffectListener iCinematicEffectListener) throws RemoteException;

    void returnCinematicEffectResponse(CinematicEffectResponse cinematicEffectResponse) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IWallpaperEffectsGenerationManager {
        @Override // android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager
        public void generateCinematicEffect(CinematicEffectRequest request, ICinematicEffectListener listener) throws RemoteException {
        }

        @Override // android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager
        public void returnCinematicEffectResponse(CinematicEffectResponse response) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IWallpaperEffectsGenerationManager {
        static final int TRANSACTION_generateCinematicEffect = 1;
        static final int TRANSACTION_returnCinematicEffectResponse = 2;

        public Stub() {
            attachInterface(this, IWallpaperEffectsGenerationManager.DESCRIPTOR);
        }

        public static IWallpaperEffectsGenerationManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWallpaperEffectsGenerationManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IWallpaperEffectsGenerationManager)) {
                return (IWallpaperEffectsGenerationManager) iin;
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
                    return "generateCinematicEffect";
                case 2:
                    return "returnCinematicEffectResponse";
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
                data.enforceInterface(IWallpaperEffectsGenerationManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWallpaperEffectsGenerationManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            CinematicEffectRequest _arg0 = (CinematicEffectRequest) data.readTypedObject(CinematicEffectRequest.CREATOR);
                            ICinematicEffectListener _arg1 = ICinematicEffectListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            generateCinematicEffect(_arg0, _arg1);
                            break;
                        case 2:
                            CinematicEffectResponse _arg02 = (CinematicEffectResponse) data.readTypedObject(CinematicEffectResponse.CREATOR);
                            data.enforceNoDataAvail();
                            returnCinematicEffectResponse(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IWallpaperEffectsGenerationManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWallpaperEffectsGenerationManager.DESCRIPTOR;
            }

            @Override // android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager
            public void generateCinematicEffect(CinematicEffectRequest request, ICinematicEffectListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWallpaperEffectsGenerationManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager
            public void returnCinematicEffectResponse(CinematicEffectResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWallpaperEffectsGenerationManager.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
