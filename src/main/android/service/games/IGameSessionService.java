package android.service.games;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.games.IGameSessionController;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes3.dex */
public interface IGameSessionService extends IInterface {
    public static final String DESCRIPTOR = "android.service.games.IGameSessionService";

    void create(IGameSessionController iGameSessionController, CreateGameSessionRequest createGameSessionRequest, GameSessionViewHostConfiguration gameSessionViewHostConfiguration, AndroidFuture androidFuture) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGameSessionService {
        @Override // android.service.games.IGameSessionService
        public void create(IGameSessionController gameSessionController, CreateGameSessionRequest createGameSessionRequest, GameSessionViewHostConfiguration gameSessionViewHostConfiguration, AndroidFuture createGameSessionResultFuture) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGameSessionService {
        static final int TRANSACTION_create = 1;

        public Stub() {
            attachInterface(this, IGameSessionService.DESCRIPTOR);
        }

        public static IGameSessionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGameSessionService.DESCRIPTOR);
            if (iin != null && (iin instanceof IGameSessionService)) {
                return (IGameSessionService) iin;
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
                    return "create";
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
                data.enforceInterface(IGameSessionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGameSessionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IGameSessionController _arg0 = IGameSessionController.Stub.asInterface(data.readStrongBinder());
                            CreateGameSessionRequest _arg1 = (CreateGameSessionRequest) data.readTypedObject(CreateGameSessionRequest.CREATOR);
                            GameSessionViewHostConfiguration _arg2 = (GameSessionViewHostConfiguration) data.readTypedObject(GameSessionViewHostConfiguration.CREATOR);
                            AndroidFuture _arg3 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            create(_arg0, _arg1, _arg2, _arg3);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IGameSessionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGameSessionService.DESCRIPTOR;
            }

            @Override // android.service.games.IGameSessionService
            public void create(IGameSessionController gameSessionController, CreateGameSessionRequest createGameSessionRequest, GameSessionViewHostConfiguration gameSessionViewHostConfiguration, AndroidFuture createGameSessionResultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGameSessionService.DESCRIPTOR);
                    _data.writeStrongInterface(gameSessionController);
                    _data.writeTypedObject(createGameSessionRequest, 0);
                    _data.writeTypedObject(gameSessionViewHostConfiguration, 0);
                    _data.writeTypedObject(createGameSessionResultFuture, 0);
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
