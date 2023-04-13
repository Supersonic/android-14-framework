package android.service.settings.suggestions;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes3.dex */
public interface ISuggestionService extends IInterface {
    void dismissSuggestion(Suggestion suggestion) throws RemoteException;

    List<Suggestion> getSuggestions() throws RemoteException;

    void launchSuggestion(Suggestion suggestion) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISuggestionService {
        @Override // android.service.settings.suggestions.ISuggestionService
        public List<Suggestion> getSuggestions() throws RemoteException {
            return null;
        }

        @Override // android.service.settings.suggestions.ISuggestionService
        public void dismissSuggestion(Suggestion suggestion) throws RemoteException {
        }

        @Override // android.service.settings.suggestions.ISuggestionService
        public void launchSuggestion(Suggestion suggestion) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISuggestionService {
        public static final String DESCRIPTOR = "android.service.settings.suggestions.ISuggestionService";
        static final int TRANSACTION_dismissSuggestion = 3;
        static final int TRANSACTION_getSuggestions = 2;
        static final int TRANSACTION_launchSuggestion = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISuggestionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISuggestionService)) {
                return (ISuggestionService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 2:
                    return "getSuggestions";
                case 3:
                    return "dismissSuggestion";
                case 4:
                    return "launchSuggestion";
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
                        case 2:
                            List<Suggestion> _result = getSuggestions();
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 3:
                            Suggestion _arg0 = (Suggestion) data.readTypedObject(Suggestion.CREATOR);
                            data.enforceNoDataAvail();
                            dismissSuggestion(_arg0);
                            reply.writeNoException();
                            break;
                        case 4:
                            Suggestion _arg02 = (Suggestion) data.readTypedObject(Suggestion.CREATOR);
                            data.enforceNoDataAvail();
                            launchSuggestion(_arg02);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISuggestionService {
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

            @Override // android.service.settings.suggestions.ISuggestionService
            public List<Suggestion> getSuggestions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    List<Suggestion> _result = _reply.createTypedArrayList(Suggestion.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.settings.suggestions.ISuggestionService
            public void dismissSuggestion(Suggestion suggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(suggestion, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.settings.suggestions.ISuggestionService
            public void launchSuggestion(Suggestion suggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(suggestion, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
