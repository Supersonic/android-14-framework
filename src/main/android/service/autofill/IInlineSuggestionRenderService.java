package android.service.autofill;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.service.autofill.IInlineSuggestionUiCallback;
/* loaded from: classes3.dex */
public interface IInlineSuggestionRenderService extends IInterface {
    public static final String DESCRIPTOR = "android.service.autofill.IInlineSuggestionRenderService";

    void destroySuggestionViews(int i, int i2) throws RemoteException;

    void getInlineSuggestionsRendererInfo(RemoteCallback remoteCallback) throws RemoteException;

    void renderSuggestion(IInlineSuggestionUiCallback iInlineSuggestionUiCallback, InlinePresentation inlinePresentation, int i, int i2, IBinder iBinder, int i3, int i4, int i5) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IInlineSuggestionRenderService {
        @Override // android.service.autofill.IInlineSuggestionRenderService
        public void renderSuggestion(IInlineSuggestionUiCallback callback, InlinePresentation presentation, int width, int height, IBinder hostInputToken, int displayId, int userId, int sessionId) throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionRenderService
        public void getInlineSuggestionsRendererInfo(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.autofill.IInlineSuggestionRenderService
        public void destroySuggestionViews(int userId, int sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IInlineSuggestionRenderService {
        static final int TRANSACTION_destroySuggestionViews = 3;
        static final int TRANSACTION_getInlineSuggestionsRendererInfo = 2;
        static final int TRANSACTION_renderSuggestion = 1;

        public Stub() {
            attachInterface(this, IInlineSuggestionRenderService.DESCRIPTOR);
        }

        public static IInlineSuggestionRenderService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInlineSuggestionRenderService.DESCRIPTOR);
            if (iin != null && (iin instanceof IInlineSuggestionRenderService)) {
                return (IInlineSuggestionRenderService) iin;
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
                    return "renderSuggestion";
                case 2:
                    return "getInlineSuggestionsRendererInfo";
                case 3:
                    return "destroySuggestionViews";
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
                data.enforceInterface(IInlineSuggestionRenderService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInlineSuggestionRenderService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IInlineSuggestionUiCallback _arg0 = IInlineSuggestionUiCallback.Stub.asInterface(data.readStrongBinder());
                            InlinePresentation _arg1 = (InlinePresentation) data.readTypedObject(InlinePresentation.CREATOR);
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            IBinder _arg4 = data.readStrongBinder();
                            int _arg5 = data.readInt();
                            int _arg6 = data.readInt();
                            int _arg7 = data.readInt();
                            data.enforceNoDataAvail();
                            renderSuggestion(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            break;
                        case 2:
                            RemoteCallback _arg02 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getInlineSuggestionsRendererInfo(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            destroySuggestionViews(_arg03, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IInlineSuggestionRenderService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInlineSuggestionRenderService.DESCRIPTOR;
            }

            @Override // android.service.autofill.IInlineSuggestionRenderService
            public void renderSuggestion(IInlineSuggestionUiCallback callback, InlinePresentation presentation, int width, int height, IBinder hostInputToken, int displayId, int userId, int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionRenderService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(presentation, 0);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeStrongBinder(hostInputToken);
                    _data.writeInt(displayId);
                    _data.writeInt(userId);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionRenderService
            public void getInlineSuggestionsRendererInfo(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionRenderService.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IInlineSuggestionRenderService
            public void destroySuggestionViews(int userId, int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInlineSuggestionRenderService.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
