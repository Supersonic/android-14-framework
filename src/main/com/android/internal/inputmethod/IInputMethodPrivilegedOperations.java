package com.android.internal.inputmethod;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes4.dex */
public interface IInputMethodPrivilegedOperations extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputMethodPrivilegedOperations";

    void applyImeVisibilityAsync(IBinder iBinder, boolean z, ImeTracker.Token token) throws RemoteException;

    void createInputContentUriToken(Uri uri, String str, AndroidFuture androidFuture) throws RemoteException;

    void hideMySoftInput(int i, int i2, AndroidFuture androidFuture) throws RemoteException;

    void notifyUserActionAsync() throws RemoteException;

    void onStylusHandwritingReady(int i, int i2) throws RemoteException;

    void reportFullscreenModeAsync(boolean z) throws RemoteException;

    void reportStartInputAsync(IBinder iBinder) throws RemoteException;

    void resetStylusHandwriting(int i) throws RemoteException;

    void setImeWindowStatusAsync(int i, int i2) throws RemoteException;

    void setInputMethod(String str, AndroidFuture androidFuture) throws RemoteException;

    void setInputMethodAndSubtype(String str, InputMethodSubtype inputMethodSubtype, AndroidFuture androidFuture) throws RemoteException;

    void shouldOfferSwitchingToNextInputMethod(AndroidFuture androidFuture) throws RemoteException;

    void showMySoftInput(int i, AndroidFuture androidFuture) throws RemoteException;

    void switchToNextInputMethod(boolean z, AndroidFuture androidFuture) throws RemoteException;

    void switchToPreviousInputMethod(AndroidFuture androidFuture) throws RemoteException;

    void updateStatusIconAsync(String str, int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputMethodPrivilegedOperations {
        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void setImeWindowStatusAsync(int vis, int backDisposition) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void reportStartInputAsync(IBinder startInputToken) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void createInputContentUriToken(Uri contentUri, String packageName, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void reportFullscreenModeAsync(boolean fullscreen) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void setInputMethod(String id, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void setInputMethodAndSubtype(String id, InputMethodSubtype subtype, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void hideMySoftInput(int flags, int reason, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void showMySoftInput(int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void updateStatusIconAsync(String packageName, int iconId) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void switchToPreviousInputMethod(AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void switchToNextInputMethod(boolean onlyCurrentIme, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void shouldOfferSwitchingToNextInputMethod(AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void notifyUserActionAsync() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void applyImeVisibilityAsync(IBinder showOrHideInputToken, boolean setVisible, ImeTracker.Token statsToken) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void onStylusHandwritingReady(int requestId, int pid) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
        public void resetStylusHandwriting(int requestId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputMethodPrivilegedOperations {
        static final int TRANSACTION_applyImeVisibilityAsync = 14;
        static final int TRANSACTION_createInputContentUriToken = 3;
        static final int TRANSACTION_hideMySoftInput = 7;
        static final int TRANSACTION_notifyUserActionAsync = 13;
        static final int TRANSACTION_onStylusHandwritingReady = 15;
        static final int TRANSACTION_reportFullscreenModeAsync = 4;
        static final int TRANSACTION_reportStartInputAsync = 2;
        static final int TRANSACTION_resetStylusHandwriting = 16;
        static final int TRANSACTION_setImeWindowStatusAsync = 1;
        static final int TRANSACTION_setInputMethod = 5;
        static final int TRANSACTION_setInputMethodAndSubtype = 6;
        static final int TRANSACTION_shouldOfferSwitchingToNextInputMethod = 12;
        static final int TRANSACTION_showMySoftInput = 8;
        static final int TRANSACTION_switchToNextInputMethod = 11;
        static final int TRANSACTION_switchToPreviousInputMethod = 10;
        static final int TRANSACTION_updateStatusIconAsync = 9;

        public Stub() {
            attachInterface(this, IInputMethodPrivilegedOperations.DESCRIPTOR);
        }

        public static IInputMethodPrivilegedOperations asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputMethodPrivilegedOperations.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodPrivilegedOperations)) {
                return (IInputMethodPrivilegedOperations) iin;
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
                    return "setImeWindowStatusAsync";
                case 2:
                    return "reportStartInputAsync";
                case 3:
                    return "createInputContentUriToken";
                case 4:
                    return "reportFullscreenModeAsync";
                case 5:
                    return "setInputMethod";
                case 6:
                    return "setInputMethodAndSubtype";
                case 7:
                    return "hideMySoftInput";
                case 8:
                    return "showMySoftInput";
                case 9:
                    return "updateStatusIconAsync";
                case 10:
                    return "switchToPreviousInputMethod";
                case 11:
                    return "switchToNextInputMethod";
                case 12:
                    return "shouldOfferSwitchingToNextInputMethod";
                case 13:
                    return "notifyUserActionAsync";
                case 14:
                    return "applyImeVisibilityAsync";
                case 15:
                    return "onStylusHandwritingReady";
                case 16:
                    return "resetStylusHandwriting";
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
                data.enforceInterface(IInputMethodPrivilegedOperations.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            setImeWindowStatusAsync(_arg0, _arg1);
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            reportStartInputAsync(_arg02);
                            break;
                        case 3:
                            Uri _arg03 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg12 = data.readString();
                            AndroidFuture _arg2 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            createInputContentUriToken(_arg03, _arg12, _arg2);
                            break;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportFullscreenModeAsync(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            AndroidFuture _arg13 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            setInputMethod(_arg05, _arg13);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            InputMethodSubtype _arg14 = (InputMethodSubtype) data.readTypedObject(InputMethodSubtype.CREATOR);
                            AndroidFuture _arg22 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            setInputMethodAndSubtype(_arg06, _arg14, _arg22);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg15 = data.readInt();
                            AndroidFuture _arg23 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            hideMySoftInput(_arg07, _arg15, _arg23);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            AndroidFuture _arg16 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            showMySoftInput(_arg08, _arg16);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            updateStatusIconAsync(_arg09, _arg17);
                            break;
                        case 10:
                            AndroidFuture _arg010 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            switchToPreviousInputMethod(_arg010);
                            break;
                        case 11:
                            boolean _arg011 = data.readBoolean();
                            AndroidFuture _arg18 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            switchToNextInputMethod(_arg011, _arg18);
                            break;
                        case 12:
                            AndroidFuture _arg012 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            shouldOfferSwitchingToNextInputMethod(_arg012);
                            break;
                        case 13:
                            notifyUserActionAsync();
                            break;
                        case 14:
                            IBinder _arg013 = data.readStrongBinder();
                            boolean _arg19 = data.readBoolean();
                            ImeTracker.Token _arg24 = (ImeTracker.Token) data.readTypedObject(ImeTracker.Token.CREATOR);
                            data.enforceNoDataAvail();
                            applyImeVisibilityAsync(_arg013, _arg19, _arg24);
                            break;
                        case 15:
                            int _arg014 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            onStylusHandwritingReady(_arg014, _arg110);
                            break;
                        case 16:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            resetStylusHandwriting(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IInputMethodPrivilegedOperations {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputMethodPrivilegedOperations.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void setImeWindowStatusAsync(int vis, int backDisposition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeInt(vis);
                    _data.writeInt(backDisposition);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void reportStartInputAsync(IBinder startInputToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeStrongBinder(startInputToken);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void createInputContentUriToken(Uri contentUri, String packageName, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeTypedObject(contentUri, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void reportFullscreenModeAsync(boolean fullscreen) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeBoolean(fullscreen);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void setInputMethod(String id, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void setInputMethodAndSubtype(String id, InputMethodSubtype subtype, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeTypedObject(subtype, 0);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void hideMySoftInput(int flags, int reason, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(reason);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void showMySoftInput(int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void updateStatusIconAsync(String packageName, int iconId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(iconId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void switchToPreviousInputMethod(AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void switchToNextInputMethod(boolean onlyCurrentIme, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeBoolean(onlyCurrentIme);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void shouldOfferSwitchingToNextInputMethod(AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void notifyUserActionAsync() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void applyImeVisibilityAsync(IBinder showOrHideInputToken, boolean setVisible, ImeTracker.Token statsToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeStrongBinder(showOrHideInputToken);
                    _data.writeBoolean(setVisible);
                    _data.writeTypedObject(statsToken, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void onStylusHandwritingReady(int requestId, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeInt(pid);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodPrivilegedOperations
            public void resetStylusHandwriting(int requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodPrivilegedOperations.DESCRIPTOR);
                    _data.writeInt(requestId);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 15;
        }
    }
}
