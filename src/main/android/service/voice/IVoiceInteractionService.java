package android.service.voice;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.app.IVoiceActionCheckCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IVoiceInteractionService extends IInterface {
    void detectorRemoteExceptionOccurred(IBinder iBinder, int i) throws RemoteException;

    void getActiveServiceSupportedActions(List<String> list, IVoiceActionCheckCallback iVoiceActionCheckCallback) throws RemoteException;

    void launchVoiceAssistFromKeyguard() throws RemoteException;

    void prepareToShowSession(Bundle bundle, int i) throws RemoteException;

    void ready() throws RemoteException;

    void showSessionFailed(Bundle bundle) throws RemoteException;

    void shutdown() throws RemoteException;

    void soundModelsChanged() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IVoiceInteractionService {
        @Override // android.service.voice.IVoiceInteractionService
        public void ready() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void soundModelsChanged() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void shutdown() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void launchVoiceAssistFromKeyguard() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void getActiveServiceSupportedActions(List<String> voiceActions, IVoiceActionCheckCallback callback) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void prepareToShowSession(Bundle args, int flags) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void showSessionFailed(Bundle args) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void detectorRemoteExceptionOccurred(IBinder token, int detectorType) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IVoiceInteractionService {
        public static final String DESCRIPTOR = "android.service.voice.IVoiceInteractionService";
        static final int TRANSACTION_detectorRemoteExceptionOccurred = 8;
        static final int TRANSACTION_getActiveServiceSupportedActions = 5;
        static final int TRANSACTION_launchVoiceAssistFromKeyguard = 4;
        static final int TRANSACTION_prepareToShowSession = 6;
        static final int TRANSACTION_ready = 1;
        static final int TRANSACTION_showSessionFailed = 7;
        static final int TRANSACTION_shutdown = 3;
        static final int TRANSACTION_soundModelsChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoiceInteractionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVoiceInteractionService)) {
                return (IVoiceInteractionService) iin;
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
                    return "ready";
                case 2:
                    return "soundModelsChanged";
                case 3:
                    return "shutdown";
                case 4:
                    return "launchVoiceAssistFromKeyguard";
                case 5:
                    return "getActiveServiceSupportedActions";
                case 6:
                    return "prepareToShowSession";
                case 7:
                    return "showSessionFailed";
                case 8:
                    return "detectorRemoteExceptionOccurred";
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
                            ready();
                            break;
                        case 2:
                            soundModelsChanged();
                            break;
                        case 3:
                            shutdown();
                            break;
                        case 4:
                            launchVoiceAssistFromKeyguard();
                            break;
                        case 5:
                            List<String> _arg0 = data.createStringArrayList();
                            IVoiceActionCheckCallback _arg1 = IVoiceActionCheckCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getActiveServiceSupportedActions(_arg0, _arg1);
                            break;
                        case 6:
                            Bundle _arg02 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            prepareToShowSession(_arg02, _arg12);
                            break;
                        case 7:
                            Bundle _arg03 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            showSessionFailed(_arg03);
                            break;
                        case 8:
                            IBinder _arg04 = data.readStrongBinder();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            detectorRemoteExceptionOccurred(_arg04, _arg13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IVoiceInteractionService {
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

            @Override // android.service.voice.IVoiceInteractionService
            public void ready() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void soundModelsChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void shutdown() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void launchVoiceAssistFromKeyguard() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void getActiveServiceSupportedActions(List<String> voiceActions, IVoiceActionCheckCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(voiceActions);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void prepareToShowSession(Bundle args, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(args, 0);
                    _data.writeInt(flags);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void showSessionFailed(Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(args, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionService
            public void detectorRemoteExceptionOccurred(IBinder token, int detectorType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(detectorType);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
