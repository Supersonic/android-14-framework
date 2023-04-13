package com.android.internal.textservice;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesSessionListener;
/* loaded from: classes3.dex */
public interface ITextServicesManager extends IInterface {
    void finishSpellCheckerService(int i, ISpellCheckerSessionListener iSpellCheckerSessionListener) throws RemoteException;

    SpellCheckerInfo getCurrentSpellChecker(int i, String str) throws RemoteException;

    SpellCheckerSubtype getCurrentSpellCheckerSubtype(int i, boolean z) throws RemoteException;

    SpellCheckerInfo[] getEnabledSpellCheckers(int i) throws RemoteException;

    void getSpellCheckerService(int i, String str, String str2, ITextServicesSessionListener iTextServicesSessionListener, ISpellCheckerSessionListener iSpellCheckerSessionListener, Bundle bundle, int i2) throws RemoteException;

    boolean isSpellCheckerEnabled(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextServicesManager {
        @Override // com.android.internal.textservice.ITextServicesManager
        public SpellCheckerInfo getCurrentSpellChecker(int userId, String locale) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.textservice.ITextServicesManager
        public SpellCheckerSubtype getCurrentSpellCheckerSubtype(int userId, boolean allowImplicitlySelectedSubtype) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.textservice.ITextServicesManager
        public void getSpellCheckerService(int userId, String sciId, String locale, ITextServicesSessionListener tsListener, ISpellCheckerSessionListener scListener, Bundle bundle, int supportedAttributes) throws RemoteException {
        }

        @Override // com.android.internal.textservice.ITextServicesManager
        public void finishSpellCheckerService(int userId, ISpellCheckerSessionListener listener) throws RemoteException {
        }

        @Override // com.android.internal.textservice.ITextServicesManager
        public boolean isSpellCheckerEnabled(int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.textservice.ITextServicesManager
        public SpellCheckerInfo[] getEnabledSpellCheckers(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextServicesManager {
        public static final String DESCRIPTOR = "com.android.internal.textservice.ITextServicesManager";
        static final int TRANSACTION_finishSpellCheckerService = 4;
        static final int TRANSACTION_getCurrentSpellChecker = 1;
        static final int TRANSACTION_getCurrentSpellCheckerSubtype = 2;
        static final int TRANSACTION_getEnabledSpellCheckers = 6;
        static final int TRANSACTION_getSpellCheckerService = 3;
        static final int TRANSACTION_isSpellCheckerEnabled = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITextServicesManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITextServicesManager)) {
                return (ITextServicesManager) iin;
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
                    return "getCurrentSpellChecker";
                case 2:
                    return "getCurrentSpellCheckerSubtype";
                case 3:
                    return "getSpellCheckerService";
                case 4:
                    return "finishSpellCheckerService";
                case 5:
                    return "isSpellCheckerEnabled";
                case 6:
                    return "getEnabledSpellCheckers";
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
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            SpellCheckerInfo _result = getCurrentSpellChecker(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            SpellCheckerSubtype _result2 = getCurrentSpellCheckerSubtype(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            String _arg2 = data.readString();
                            ITextServicesSessionListener _arg3 = ITextServicesSessionListener.Stub.asInterface(data.readStrongBinder());
                            ISpellCheckerSessionListener _arg4 = ISpellCheckerSessionListener.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            getSpellCheckerService(_arg03, _arg13, _arg2, _arg3, _arg4, _arg5, _arg6);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            ISpellCheckerSessionListener _arg14 = ISpellCheckerSessionListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            finishSpellCheckerService(_arg04, _arg14);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = isSpellCheckerEnabled(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            SpellCheckerInfo[] _result4 = getEnabledSpellCheckers(_arg06);
                            reply.writeNoException();
                            reply.writeTypedArray(_result4, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITextServicesManager {
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

            @Override // com.android.internal.textservice.ITextServicesManager
            public SpellCheckerInfo getCurrentSpellChecker(int userId, String locale) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(locale);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    SpellCheckerInfo _result = (SpellCheckerInfo) _reply.readTypedObject(SpellCheckerInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.textservice.ITextServicesManager
            public SpellCheckerSubtype getCurrentSpellCheckerSubtype(int userId, boolean allowImplicitlySelectedSubtype) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(allowImplicitlySelectedSubtype);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    SpellCheckerSubtype _result = (SpellCheckerSubtype) _reply.readTypedObject(SpellCheckerSubtype.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.textservice.ITextServicesManager
            public void getSpellCheckerService(int userId, String sciId, String locale, ITextServicesSessionListener tsListener, ISpellCheckerSessionListener scListener, Bundle bundle, int supportedAttributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(sciId);
                    _data.writeString(locale);
                    _data.writeStrongInterface(tsListener);
                    _data.writeStrongInterface(scListener);
                    _data.writeTypedObject(bundle, 0);
                    _data.writeInt(supportedAttributes);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.textservice.ITextServicesManager
            public void finishSpellCheckerService(int userId, ISpellCheckerSessionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.textservice.ITextServicesManager
            public boolean isSpellCheckerEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.textservice.ITextServicesManager
            public SpellCheckerInfo[] getEnabledSpellCheckers(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    SpellCheckerInfo[] _result = (SpellCheckerInfo[]) _reply.createTypedArray(SpellCheckerInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
