package android.content.integrity;

import android.content.IntentSender;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IAppIntegrityManager extends IInterface {
    public static final String DESCRIPTOR = "android.content.integrity.IAppIntegrityManager";

    String getCurrentRuleSetProvider() throws RemoteException;

    String getCurrentRuleSetVersion() throws RemoteException;

    ParceledListSlice<Rule> getCurrentRules() throws RemoteException;

    List<String> getWhitelistedRuleProviders() throws RemoteException;

    void updateRuleSet(String str, ParceledListSlice<Rule> parceledListSlice, IntentSender intentSender) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAppIntegrityManager {
        @Override // android.content.integrity.IAppIntegrityManager
        public void updateRuleSet(String version, ParceledListSlice<Rule> rules, IntentSender statusReceiver) throws RemoteException {
        }

        @Override // android.content.integrity.IAppIntegrityManager
        public String getCurrentRuleSetVersion() throws RemoteException {
            return null;
        }

        @Override // android.content.integrity.IAppIntegrityManager
        public String getCurrentRuleSetProvider() throws RemoteException {
            return null;
        }

        @Override // android.content.integrity.IAppIntegrityManager
        public ParceledListSlice<Rule> getCurrentRules() throws RemoteException {
            return null;
        }

        @Override // android.content.integrity.IAppIntegrityManager
        public List<String> getWhitelistedRuleProviders() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAppIntegrityManager {
        static final int TRANSACTION_getCurrentRuleSetProvider = 3;
        static final int TRANSACTION_getCurrentRuleSetVersion = 2;
        static final int TRANSACTION_getCurrentRules = 4;
        static final int TRANSACTION_getWhitelistedRuleProviders = 5;
        static final int TRANSACTION_updateRuleSet = 1;

        public Stub() {
            attachInterface(this, IAppIntegrityManager.DESCRIPTOR);
        }

        public static IAppIntegrityManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAppIntegrityManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IAppIntegrityManager)) {
                return (IAppIntegrityManager) iin;
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
                    return "updateRuleSet";
                case 2:
                    return "getCurrentRuleSetVersion";
                case 3:
                    return "getCurrentRuleSetProvider";
                case 4:
                    return "getCurrentRules";
                case 5:
                    return "getWhitelistedRuleProviders";
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
                data.enforceInterface(IAppIntegrityManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAppIntegrityManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            ParceledListSlice<Rule> _arg1 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            IntentSender _arg2 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            updateRuleSet(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _result = getCurrentRuleSetVersion();
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 3:
                            String _result2 = getCurrentRuleSetProvider();
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 4:
                            ParceledListSlice<Rule> _result3 = getCurrentRules();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 5:
                            List<String> _result4 = getWhitelistedRuleProviders();
                            reply.writeNoException();
                            reply.writeStringList(_result4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAppIntegrityManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAppIntegrityManager.DESCRIPTOR;
            }

            @Override // android.content.integrity.IAppIntegrityManager
            public void updateRuleSet(String version, ParceledListSlice<Rule> rules, IntentSender statusReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppIntegrityManager.DESCRIPTOR);
                    _data.writeString(version);
                    _data.writeTypedObject(rules, 0);
                    _data.writeTypedObject(statusReceiver, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.integrity.IAppIntegrityManager
            public String getCurrentRuleSetVersion() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppIntegrityManager.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.integrity.IAppIntegrityManager
            public String getCurrentRuleSetProvider() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppIntegrityManager.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.integrity.IAppIntegrityManager
            public ParceledListSlice<Rule> getCurrentRules() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppIntegrityManager.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice<Rule> _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.integrity.IAppIntegrityManager
            public List<String> getWhitelistedRuleProviders() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppIntegrityManager.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
