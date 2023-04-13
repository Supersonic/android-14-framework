package com.android.internal.inputmethod;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IInputMethodClient extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputMethodClient";

    void onBindAccessibilityService(InputBindResult inputBindResult, int i) throws RemoteException;

    void onBindMethod(InputBindResult inputBindResult) throws RemoteException;

    void onUnbindAccessibilityService(int i, int i2) throws RemoteException;

    void onUnbindMethod(int i, int i2) throws RemoteException;

    void reportFullscreenMode(boolean z) throws RemoteException;

    void scheduleStartInputIfNecessary(boolean z) throws RemoteException;

    void setActive(boolean z, boolean z2) throws RemoteException;

    void setImeTraceEnabled(boolean z) throws RemoteException;

    void setInteractive(boolean z, boolean z2) throws RemoteException;

    void throwExceptionFromSystem(String str) throws RemoteException;

    void updateVirtualDisplayToScreenMatrix(int i, float[] fArr) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputMethodClient {
        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void onBindMethod(InputBindResult res) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void onBindAccessibilityService(InputBindResult res, int id) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void onUnbindMethod(int sequence, int unbindReason) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void onUnbindAccessibilityService(int sequence, int id) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void setActive(boolean active, boolean fullscreen) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void setInteractive(boolean active, boolean fullscreen) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void scheduleStartInputIfNecessary(boolean fullscreen) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void reportFullscreenMode(boolean fullscreen) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void updateVirtualDisplayToScreenMatrix(int bindSequence, float[] matrixValues) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void setImeTraceEnabled(boolean enabled) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodClient
        public void throwExceptionFromSystem(String message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputMethodClient {
        static final int TRANSACTION_onBindAccessibilityService = 2;
        static final int TRANSACTION_onBindMethod = 1;
        static final int TRANSACTION_onUnbindAccessibilityService = 4;
        static final int TRANSACTION_onUnbindMethod = 3;
        static final int TRANSACTION_reportFullscreenMode = 8;
        static final int TRANSACTION_scheduleStartInputIfNecessary = 7;
        static final int TRANSACTION_setActive = 5;
        static final int TRANSACTION_setImeTraceEnabled = 10;
        static final int TRANSACTION_setInteractive = 6;
        static final int TRANSACTION_throwExceptionFromSystem = 11;
        static final int TRANSACTION_updateVirtualDisplayToScreenMatrix = 9;

        public Stub() {
            attachInterface(this, IInputMethodClient.DESCRIPTOR);
        }

        public static IInputMethodClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputMethodClient.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodClient)) {
                return (IInputMethodClient) iin;
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
                    return "onBindMethod";
                case 2:
                    return "onBindAccessibilityService";
                case 3:
                    return "onUnbindMethod";
                case 4:
                    return "onUnbindAccessibilityService";
                case 5:
                    return "setActive";
                case 6:
                    return "setInteractive";
                case 7:
                    return "scheduleStartInputIfNecessary";
                case 8:
                    return "reportFullscreenMode";
                case 9:
                    return "updateVirtualDisplayToScreenMatrix";
                case 10:
                    return "setImeTraceEnabled";
                case 11:
                    return "throwExceptionFromSystem";
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
                data.enforceInterface(IInputMethodClient.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputMethodClient.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            InputBindResult _arg0 = (InputBindResult) data.readTypedObject(InputBindResult.CREATOR);
                            data.enforceNoDataAvail();
                            onBindMethod(_arg0);
                            break;
                        case 2:
                            InputBindResult _arg02 = (InputBindResult) data.readTypedObject(InputBindResult.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onBindAccessibilityService(_arg02, _arg1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onUnbindMethod(_arg03, _arg12);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onUnbindAccessibilityService(_arg04, _arg13);
                            break;
                        case 5:
                            boolean _arg05 = data.readBoolean();
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setActive(_arg05, _arg14);
                            break;
                        case 6:
                            boolean _arg06 = data.readBoolean();
                            boolean _arg15 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setInteractive(_arg06, _arg15);
                            break;
                        case 7:
                            boolean _arg07 = data.readBoolean();
                            data.enforceNoDataAvail();
                            scheduleStartInputIfNecessary(_arg07);
                            break;
                        case 8:
                            boolean _arg08 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportFullscreenMode(_arg08);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            float[] _arg16 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            updateVirtualDisplayToScreenMatrix(_arg09, _arg16);
                            break;
                        case 10:
                            boolean _arg010 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setImeTraceEnabled(_arg010);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            throwExceptionFromSystem(_arg011);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IInputMethodClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputMethodClient.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void onBindMethod(InputBindResult res) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeTypedObject(res, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void onBindAccessibilityService(InputBindResult res, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeTypedObject(res, 0);
                    _data.writeInt(id);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void onUnbindMethod(int sequence, int unbindReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeInt(sequence);
                    _data.writeInt(unbindReason);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void onUnbindAccessibilityService(int sequence, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeInt(sequence);
                    _data.writeInt(id);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void setActive(boolean active, boolean fullscreen) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeBoolean(active);
                    _data.writeBoolean(fullscreen);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void setInteractive(boolean active, boolean fullscreen) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeBoolean(active);
                    _data.writeBoolean(fullscreen);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void scheduleStartInputIfNecessary(boolean fullscreen) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeBoolean(fullscreen);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void reportFullscreenMode(boolean fullscreen) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeBoolean(fullscreen);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void updateVirtualDisplayToScreenMatrix(int bindSequence, float[] matrixValues) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeInt(bindSequence);
                    _data.writeFloatArray(matrixValues);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void setImeTraceEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodClient
            public void throwExceptionFromSystem(String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodClient.DESCRIPTOR);
                    _data.writeString(message);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
