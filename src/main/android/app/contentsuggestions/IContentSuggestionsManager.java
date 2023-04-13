package android.app.contentsuggestions;

import android.app.contentsuggestions.IClassificationsCallback;
import android.app.contentsuggestions.ISelectionsCallback;
import android.graphics.Bitmap;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.p028os.IResultReceiver;
/* loaded from: classes.dex */
public interface IContentSuggestionsManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.contentsuggestions.IContentSuggestionsManager";

    void classifyContentSelections(int i, ClassificationsRequest classificationsRequest, IClassificationsCallback iClassificationsCallback) throws RemoteException;

    void isEnabled(int i, IResultReceiver iResultReceiver) throws RemoteException;

    void notifyInteraction(int i, String str, Bundle bundle) throws RemoteException;

    void provideContextBitmap(int i, Bitmap bitmap, Bundle bundle) throws RemoteException;

    void provideContextImage(int i, int i2, Bundle bundle) throws RemoteException;

    void resetTemporaryService(int i) throws RemoteException;

    void setDefaultServiceEnabled(int i, boolean z) throws RemoteException;

    void setTemporaryService(int i, String str, int i2) throws RemoteException;

    void suggestContentSelections(int i, SelectionsRequest selectionsRequest, ISelectionsCallback iSelectionsCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IContentSuggestionsManager {
        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void provideContextImage(int userId, int taskId, Bundle imageContextRequestExtras) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void provideContextBitmap(int userId, Bitmap bitmap, Bundle imageContextRequestExtras) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void suggestContentSelections(int userId, SelectionsRequest request, ISelectionsCallback callback) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void classifyContentSelections(int userId, ClassificationsRequest request, IClassificationsCallback callback) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void notifyInteraction(int userId, String requestId, Bundle interaction) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void isEnabled(int userId, IResultReceiver receiver) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void resetTemporaryService(int userId) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void setTemporaryService(int userId, String serviceName, int duration) throws RemoteException {
        }

        @Override // android.app.contentsuggestions.IContentSuggestionsManager
        public void setDefaultServiceEnabled(int userId, boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IContentSuggestionsManager {
        static final int TRANSACTION_classifyContentSelections = 4;
        static final int TRANSACTION_isEnabled = 6;
        static final int TRANSACTION_notifyInteraction = 5;
        static final int TRANSACTION_provideContextBitmap = 2;
        static final int TRANSACTION_provideContextImage = 1;
        static final int TRANSACTION_resetTemporaryService = 7;
        static final int TRANSACTION_setDefaultServiceEnabled = 9;
        static final int TRANSACTION_setTemporaryService = 8;
        static final int TRANSACTION_suggestContentSelections = 3;

        public Stub() {
            attachInterface(this, IContentSuggestionsManager.DESCRIPTOR);
        }

        public static IContentSuggestionsManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IContentSuggestionsManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IContentSuggestionsManager)) {
                return (IContentSuggestionsManager) iin;
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
                    return "provideContextImage";
                case 2:
                    return "provideContextBitmap";
                case 3:
                    return "suggestContentSelections";
                case 4:
                    return "classifyContentSelections";
                case 5:
                    return "notifyInteraction";
                case 6:
                    return "isEnabled";
                case 7:
                    return "resetTemporaryService";
                case 8:
                    return "setTemporaryService";
                case 9:
                    return "setDefaultServiceEnabled";
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
                data.enforceInterface(IContentSuggestionsManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IContentSuggestionsManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            provideContextImage(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            Bitmap _arg12 = (Bitmap) data.readTypedObject(Bitmap.CREATOR);
                            Bundle _arg22 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            provideContextBitmap(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            SelectionsRequest _arg13 = (SelectionsRequest) data.readTypedObject(SelectionsRequest.CREATOR);
                            ISelectionsCallback _arg23 = ISelectionsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            suggestContentSelections(_arg03, _arg13, _arg23);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            ClassificationsRequest _arg14 = (ClassificationsRequest) data.readTypedObject(ClassificationsRequest.CREATOR);
                            IClassificationsCallback _arg24 = IClassificationsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            classifyContentSelections(_arg04, _arg14, _arg24);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg15 = data.readString();
                            Bundle _arg25 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyInteraction(_arg05, _arg15, _arg25);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            IResultReceiver _arg16 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            isEnabled(_arg06, _arg16);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            resetTemporaryService(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg17 = data.readString();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setTemporaryService(_arg08, _arg17, _arg26);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            boolean _arg18 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDefaultServiceEnabled(_arg09, _arg18);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IContentSuggestionsManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IContentSuggestionsManager.DESCRIPTOR;
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void provideContextImage(int userId, int taskId, Bundle imageContextRequestExtras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(taskId);
                    _data.writeTypedObject(imageContextRequestExtras, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void provideContextBitmap(int userId, Bitmap bitmap, Bundle imageContextRequestExtras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeTypedObject(bitmap, 0);
                    _data.writeTypedObject(imageContextRequestExtras, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void suggestContentSelections(int userId, SelectionsRequest request, ISelectionsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void classifyContentSelections(int userId, ClassificationsRequest request, IClassificationsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void notifyInteraction(int userId, String requestId, Bundle interaction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(requestId);
                    _data.writeTypedObject(interaction, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void isEnabled(int userId, IResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void resetTemporaryService(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void setTemporaryService(int userId, String serviceName, int duration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(serviceName);
                    _data.writeInt(duration);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.contentsuggestions.IContentSuggestionsManager
            public void setDefaultServiceEnabled(int userId, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentSuggestionsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
