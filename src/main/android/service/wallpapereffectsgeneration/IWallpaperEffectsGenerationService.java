package android.service.wallpapereffectsgeneration;

import android.app.wallpapereffectsgeneration.CinematicEffectRequest;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IWallpaperEffectsGenerationService extends IInterface {
    public static final String DESCRIPTOR = "android.service.wallpapereffectsgeneration.IWallpaperEffectsGenerationService";

    void onGenerateCinematicEffect(CinematicEffectRequest cinematicEffectRequest) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWallpaperEffectsGenerationService {
        @Override // android.service.wallpapereffectsgeneration.IWallpaperEffectsGenerationService
        public void onGenerateCinematicEffect(CinematicEffectRequest request) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWallpaperEffectsGenerationService {
        static final int TRANSACTION_onGenerateCinematicEffect = 1;

        public Stub() {
            attachInterface(this, IWallpaperEffectsGenerationService.DESCRIPTOR);
        }

        public static IWallpaperEffectsGenerationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWallpaperEffectsGenerationService.DESCRIPTOR);
            if (iin != null && (iin instanceof IWallpaperEffectsGenerationService)) {
                return (IWallpaperEffectsGenerationService) iin;
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
                    return "onGenerateCinematicEffect";
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
                data.enforceInterface(IWallpaperEffectsGenerationService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWallpaperEffectsGenerationService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            CinematicEffectRequest _arg0 = (CinematicEffectRequest) data.readTypedObject(CinematicEffectRequest.CREATOR);
                            data.enforceNoDataAvail();
                            onGenerateCinematicEffect(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IWallpaperEffectsGenerationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWallpaperEffectsGenerationService.DESCRIPTOR;
            }

            @Override // android.service.wallpapereffectsgeneration.IWallpaperEffectsGenerationService
            public void onGenerateCinematicEffect(CinematicEffectRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWallpaperEffectsGenerationService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
