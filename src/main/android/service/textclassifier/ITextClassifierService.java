package android.service.textclassifier;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.textclassifier.ITextClassifierCallback;
import android.view.textclassifier.ConversationActions;
import android.view.textclassifier.SelectionEvent;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationContext;
import android.view.textclassifier.TextClassificationSessionId;
import android.view.textclassifier.TextClassifierEvent;
import android.view.textclassifier.TextLanguage;
import android.view.textclassifier.TextLinks;
import android.view.textclassifier.TextSelection;
/* loaded from: classes3.dex */
public interface ITextClassifierService extends IInterface {
    void onClassifyText(TextClassificationSessionId textClassificationSessionId, TextClassification.Request request, ITextClassifierCallback iTextClassifierCallback) throws RemoteException;

    void onConnectedStateChanged(int i) throws RemoteException;

    void onCreateTextClassificationSession(TextClassificationContext textClassificationContext, TextClassificationSessionId textClassificationSessionId) throws RemoteException;

    void onDestroyTextClassificationSession(TextClassificationSessionId textClassificationSessionId) throws RemoteException;

    void onDetectLanguage(TextClassificationSessionId textClassificationSessionId, TextLanguage.Request request, ITextClassifierCallback iTextClassifierCallback) throws RemoteException;

    void onGenerateLinks(TextClassificationSessionId textClassificationSessionId, TextLinks.Request request, ITextClassifierCallback iTextClassifierCallback) throws RemoteException;

    void onSelectionEvent(TextClassificationSessionId textClassificationSessionId, SelectionEvent selectionEvent) throws RemoteException;

    void onSuggestConversationActions(TextClassificationSessionId textClassificationSessionId, ConversationActions.Request request, ITextClassifierCallback iTextClassifierCallback) throws RemoteException;

    void onSuggestSelection(TextClassificationSessionId textClassificationSessionId, TextSelection.Request request, ITextClassifierCallback iTextClassifierCallback) throws RemoteException;

    void onTextClassifierEvent(TextClassificationSessionId textClassificationSessionId, TextClassifierEvent textClassifierEvent) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextClassifierService {
        @Override // android.service.textclassifier.ITextClassifierService
        public void onSuggestSelection(TextClassificationSessionId sessionId, TextSelection.Request request, ITextClassifierCallback callback) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onClassifyText(TextClassificationSessionId sessionId, TextClassification.Request request, ITextClassifierCallback callback) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onGenerateLinks(TextClassificationSessionId sessionId, TextLinks.Request request, ITextClassifierCallback callback) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onSelectionEvent(TextClassificationSessionId sessionId, SelectionEvent event) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onTextClassifierEvent(TextClassificationSessionId sessionId, TextClassifierEvent event) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onCreateTextClassificationSession(TextClassificationContext context, TextClassificationSessionId sessionId) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onDestroyTextClassificationSession(TextClassificationSessionId sessionId) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onDetectLanguage(TextClassificationSessionId sessionId, TextLanguage.Request request, ITextClassifierCallback callback) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onSuggestConversationActions(TextClassificationSessionId sessionId, ConversationActions.Request request, ITextClassifierCallback callback) throws RemoteException {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onConnectedStateChanged(int connected) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextClassifierService {
        public static final String DESCRIPTOR = "android.service.textclassifier.ITextClassifierService";
        static final int TRANSACTION_onClassifyText = 2;
        static final int TRANSACTION_onConnectedStateChanged = 10;
        static final int TRANSACTION_onCreateTextClassificationSession = 6;
        static final int TRANSACTION_onDestroyTextClassificationSession = 7;
        static final int TRANSACTION_onDetectLanguage = 8;
        static final int TRANSACTION_onGenerateLinks = 3;
        static final int TRANSACTION_onSelectionEvent = 4;
        static final int TRANSACTION_onSuggestConversationActions = 9;
        static final int TRANSACTION_onSuggestSelection = 1;
        static final int TRANSACTION_onTextClassifierEvent = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITextClassifierService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITextClassifierService)) {
                return (ITextClassifierService) iin;
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
                    return "onSuggestSelection";
                case 2:
                    return "onClassifyText";
                case 3:
                    return "onGenerateLinks";
                case 4:
                    return "onSelectionEvent";
                case 5:
                    return "onTextClassifierEvent";
                case 6:
                    return "onCreateTextClassificationSession";
                case 7:
                    return "onDestroyTextClassificationSession";
                case 8:
                    return "onDetectLanguage";
                case 9:
                    return "onSuggestConversationActions";
                case 10:
                    return "onConnectedStateChanged";
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
                            TextClassificationSessionId _arg0 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            TextSelection.Request _arg1 = (TextSelection.Request) data.readTypedObject(TextSelection.Request.CREATOR);
                            ITextClassifierCallback _arg2 = ITextClassifierCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onSuggestSelection(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            TextClassificationSessionId _arg02 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            TextClassification.Request _arg12 = (TextClassification.Request) data.readTypedObject(TextClassification.Request.CREATOR);
                            ITextClassifierCallback _arg22 = ITextClassifierCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onClassifyText(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            TextClassificationSessionId _arg03 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            TextLinks.Request _arg13 = (TextLinks.Request) data.readTypedObject(TextLinks.Request.CREATOR);
                            ITextClassifierCallback _arg23 = ITextClassifierCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onGenerateLinks(_arg03, _arg13, _arg23);
                            break;
                        case 4:
                            TextClassificationSessionId _arg04 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            SelectionEvent _arg14 = (SelectionEvent) data.readTypedObject(SelectionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onSelectionEvent(_arg04, _arg14);
                            break;
                        case 5:
                            TextClassificationSessionId _arg05 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            TextClassifierEvent _arg15 = (TextClassifierEvent) data.readTypedObject(TextClassifierEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onTextClassifierEvent(_arg05, _arg15);
                            break;
                        case 6:
                            TextClassificationContext _arg06 = (TextClassificationContext) data.readTypedObject(TextClassificationContext.CREATOR);
                            TextClassificationSessionId _arg16 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            onCreateTextClassificationSession(_arg06, _arg16);
                            break;
                        case 7:
                            TextClassificationSessionId _arg07 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            onDestroyTextClassificationSession(_arg07);
                            break;
                        case 8:
                            TextClassificationSessionId _arg08 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            TextLanguage.Request _arg17 = (TextLanguage.Request) data.readTypedObject(TextLanguage.Request.CREATOR);
                            ITextClassifierCallback _arg24 = ITextClassifierCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onDetectLanguage(_arg08, _arg17, _arg24);
                            break;
                        case 9:
                            TextClassificationSessionId _arg09 = (TextClassificationSessionId) data.readTypedObject(TextClassificationSessionId.CREATOR);
                            ConversationActions.Request _arg18 = (ConversationActions.Request) data.readTypedObject(ConversationActions.Request.CREATOR);
                            ITextClassifierCallback _arg25 = ITextClassifierCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onSuggestConversationActions(_arg09, _arg18, _arg25);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            onConnectedStateChanged(_arg010);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ITextClassifierService {
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

            @Override // android.service.textclassifier.ITextClassifierService
            public void onSuggestSelection(TextClassificationSessionId sessionId, TextSelection.Request request, ITextClassifierCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onClassifyText(TextClassificationSessionId sessionId, TextClassification.Request request, ITextClassifierCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onGenerateLinks(TextClassificationSessionId sessionId, TextLinks.Request request, ITextClassifierCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onSelectionEvent(TextClassificationSessionId sessionId, SelectionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onTextClassifierEvent(TextClassificationSessionId sessionId, TextClassifierEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onCreateTextClassificationSession(TextClassificationContext context, TextClassificationSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onDestroyTextClassificationSession(TextClassificationSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onDetectLanguage(TextClassificationSessionId sessionId, TextLanguage.Request request, ITextClassifierCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onSuggestConversationActions(TextClassificationSessionId sessionId, ConversationActions.Request request, ITextClassifierCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.textclassifier.ITextClassifierService
            public void onConnectedStateChanged(int connected) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(connected);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
