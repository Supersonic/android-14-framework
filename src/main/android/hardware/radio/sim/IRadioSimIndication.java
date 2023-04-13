package android.hardware.radio.sim;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioSimIndication extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$sim$IRadioSimIndication".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void carrierInfoForImsiEncryption(int i) throws RemoteException;

    void cdmaSubscriptionSourceChanged(int i, int i2) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void simPhonebookChanged(int i) throws RemoteException;

    void simPhonebookRecordsReceived(int i, byte b, PhonebookRecordInfo[] phonebookRecordInfoArr) throws RemoteException;

    void simRefresh(int i, SimRefreshResult simRefreshResult) throws RemoteException;

    void simStatusChanged(int i) throws RemoteException;

    void stkEventNotify(int i, String str) throws RemoteException;

    void stkProactiveCommand(int i, String str) throws RemoteException;

    void stkSessionEnd(int i) throws RemoteException;

    void subscriptionStatusChanged(int i, boolean z) throws RemoteException;

    void uiccApplicationsEnablementChanged(int i, boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioSimIndication {
        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void carrierInfoForImsiEncryption(int info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void cdmaSubscriptionSourceChanged(int type, int cdmaSource) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void simPhonebookChanged(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void simPhonebookRecordsReceived(int type, byte status, PhonebookRecordInfo[] records) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void simRefresh(int type, SimRefreshResult refreshResult) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void simStatusChanged(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void stkEventNotify(int type, String cmd) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void stkProactiveCommand(int type, String cmd) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void stkSessionEnd(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void subscriptionStatusChanged(int type, boolean activate) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public void uiccApplicationsEnablementChanged(int type, boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.sim.IRadioSimIndication
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioSimIndication {
        static final int TRANSACTION_carrierInfoForImsiEncryption = 1;
        static final int TRANSACTION_cdmaSubscriptionSourceChanged = 2;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_simPhonebookChanged = 3;
        static final int TRANSACTION_simPhonebookRecordsReceived = 4;
        static final int TRANSACTION_simRefresh = 5;
        static final int TRANSACTION_simStatusChanged = 6;
        static final int TRANSACTION_stkEventNotify = 7;
        static final int TRANSACTION_stkProactiveCommand = 8;
        static final int TRANSACTION_stkSessionEnd = 9;
        static final int TRANSACTION_subscriptionStatusChanged = 10;
        static final int TRANSACTION_uiccApplicationsEnablementChanged = 11;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioSimIndication asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioSimIndication)) {
                return (IRadioSimIndication) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            carrierInfoForImsiEncryption(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            cdmaSubscriptionSourceChanged(_arg02, _arg1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            simPhonebookChanged(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            byte _arg12 = data.readByte();
                            PhonebookRecordInfo[] _arg2 = (PhonebookRecordInfo[]) data.createTypedArray(PhonebookRecordInfo.CREATOR);
                            data.enforceNoDataAvail();
                            simPhonebookRecordsReceived(_arg04, _arg12, _arg2);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            SimRefreshResult _arg13 = (SimRefreshResult) data.readTypedObject(SimRefreshResult.CREATOR);
                            data.enforceNoDataAvail();
                            simRefresh(_arg05, _arg13);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            simStatusChanged(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            stkEventNotify(_arg07, _arg14);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            stkProactiveCommand(_arg08, _arg15);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            stkSessionEnd(_arg09);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            subscriptionStatusChanged(_arg010, _arg16);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            uiccApplicationsEnablementChanged(_arg011, _arg17);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioSimIndication {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void carrierInfoForImsiEncryption(int info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(info);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method carrierInfoForImsiEncryption is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void cdmaSubscriptionSourceChanged(int type, int cdmaSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(cdmaSource);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cdmaSubscriptionSourceChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void simPhonebookChanged(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method simPhonebookChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void simPhonebookRecordsReceived(int type, byte status, PhonebookRecordInfo[] records) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeByte(status);
                    _data.writeTypedArray(records, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method simPhonebookRecordsReceived is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void simRefresh(int type, SimRefreshResult refreshResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(refreshResult, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method simRefresh is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void simStatusChanged(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method simStatusChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void stkEventNotify(int type, String cmd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(cmd);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stkEventNotify is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void stkProactiveCommand(int type, String cmd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(cmd);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stkProactiveCommand is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void stkSessionEnd(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stkSessionEnd is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void subscriptionStatusChanged(int type, boolean activate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeBoolean(activate);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method subscriptionStatusChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public void uiccApplicationsEnablementChanged(int type, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeBoolean(enabled);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method uiccApplicationsEnablementChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.radio.sim.IRadioSimIndication
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
