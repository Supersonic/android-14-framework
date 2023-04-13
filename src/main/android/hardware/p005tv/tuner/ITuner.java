package android.hardware.p005tv.tuner;

import android.hardware.p005tv.tuner.IDemux;
import android.hardware.p005tv.tuner.IDescrambler;
import android.hardware.p005tv.tuner.IFrontend;
import android.hardware.p005tv.tuner.ILnb;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.hardware.tv.tuner.ITuner */
/* loaded from: classes2.dex */
public interface ITuner extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$tuner$ITuner".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    DemuxCapabilities getDemuxCaps() throws RemoteException;

    int[] getDemuxIds() throws RemoteException;

    DemuxInfo getDemuxInfo(int i) throws RemoteException;

    int[] getFrontendIds() throws RemoteException;

    FrontendInfo getFrontendInfo(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    int[] getLnbIds() throws RemoteException;

    int getMaxNumberOfFrontends(int i) throws RemoteException;

    boolean isLnaSupported() throws RemoteException;

    IDemux openDemux(int[] iArr) throws RemoteException;

    IDemux openDemuxById(int i) throws RemoteException;

    IDescrambler openDescrambler() throws RemoteException;

    IFrontend openFrontendById(int i) throws RemoteException;

    ILnb openLnbById(int i) throws RemoteException;

    ILnb openLnbByName(String str, int[] iArr) throws RemoteException;

    void setLna(boolean z) throws RemoteException;

    void setMaxNumberOfFrontends(int i, int i2) throws RemoteException;

    /* renamed from: android.hardware.tv.tuner.ITuner$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITuner {
        @Override // android.hardware.p005tv.tuner.ITuner
        public int[] getFrontendIds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public IFrontend openFrontendById(int frontendId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public IDemux openDemux(int[] demuxId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public DemuxCapabilities getDemuxCaps() throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public IDescrambler openDescrambler() throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public FrontendInfo getFrontendInfo(int frontendId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public int[] getLnbIds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public ILnb openLnbById(int lnbId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public ILnb openLnbByName(String lnbName, int[] lnbId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public void setLna(boolean bEnable) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public void setMaxNumberOfFrontends(int frontendType, int maxNumber) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public int getMaxNumberOfFrontends(int frontendType) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public boolean isLnaSupported() throws RemoteException {
            return false;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public int[] getDemuxIds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public IDemux openDemuxById(int demuxId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public DemuxInfo getDemuxInfo(int demuxId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.ITuner
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.hardware.tv.tuner.ITuner$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITuner {
        static final int TRANSACTION_getDemuxCaps = 4;
        static final int TRANSACTION_getDemuxIds = 14;
        static final int TRANSACTION_getDemuxInfo = 16;
        static final int TRANSACTION_getFrontendIds = 1;
        static final int TRANSACTION_getFrontendInfo = 6;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getLnbIds = 7;
        static final int TRANSACTION_getMaxNumberOfFrontends = 12;
        static final int TRANSACTION_isLnaSupported = 13;
        static final int TRANSACTION_openDemux = 3;
        static final int TRANSACTION_openDemuxById = 15;
        static final int TRANSACTION_openDescrambler = 5;
        static final int TRANSACTION_openFrontendById = 2;
        static final int TRANSACTION_openLnbById = 8;
        static final int TRANSACTION_openLnbByName = 9;
        static final int TRANSACTION_setLna = 10;
        static final int TRANSACTION_setMaxNumberOfFrontends = 11;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ITuner asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITuner)) {
                return (ITuner) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int[] _arg0;
            int[] _arg1;
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
                            int[] _result = getFrontendIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            IFrontend _result2 = openFrontendById(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            int _arg0_length = data.readInt();
                            if (_arg0_length < 0) {
                                _arg0 = null;
                            } else {
                                _arg0 = new int[_arg0_length];
                            }
                            data.enforceNoDataAvail();
                            IDemux _result3 = openDemux(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            reply.writeIntArray(_arg0);
                            break;
                        case 4:
                            DemuxCapabilities _result4 = getDemuxCaps();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            IDescrambler _result5 = openDescrambler();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 6:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            FrontendInfo _result6 = getFrontendInfo(_arg03);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 7:
                            int[] _result7 = getLnbIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result7);
                            break;
                        case 8:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            ILnb _result8 = openLnbById(_arg04);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result8);
                            break;
                        case 9:
                            String _arg05 = data.readString();
                            int _arg1_length = data.readInt();
                            if (_arg1_length < 0) {
                                _arg1 = null;
                            } else {
                                _arg1 = new int[_arg1_length];
                            }
                            data.enforceNoDataAvail();
                            ILnb _result9 = openLnbByName(_arg05, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result9);
                            reply.writeIntArray(_arg1);
                            break;
                        case 10:
                            boolean _arg06 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLna(_arg06);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg07 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            setMaxNumberOfFrontends(_arg07, _arg12);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result10 = getMaxNumberOfFrontends(_arg08);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 13:
                            boolean _result11 = isLnaSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 14:
                            int[] _result12 = getDemuxIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result12);
                            break;
                        case 15:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            IDemux _result13 = openDemuxById(_arg09);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result13);
                            break;
                        case 16:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            DemuxInfo _result14 = getDemuxInfo(_arg010);
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.hardware.tv.tuner.ITuner$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITuner {
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

            @Override // android.hardware.p005tv.tuner.ITuner
            public int[] getFrontendIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getFrontendIds is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public IFrontend openFrontendById(int frontendId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendId);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openFrontendById is unimplemented.");
                    }
                    _reply.readException();
                    IFrontend _result = IFrontend.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public IDemux openDemux(int[] demuxId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(demuxId.length);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openDemux is unimplemented.");
                    }
                    _reply.readException();
                    IDemux _result = IDemux.Stub.asInterface(_reply.readStrongBinder());
                    _reply.readIntArray(demuxId);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public DemuxCapabilities getDemuxCaps() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getDemuxCaps is unimplemented.");
                    }
                    _reply.readException();
                    DemuxCapabilities _result = (DemuxCapabilities) _reply.readTypedObject(DemuxCapabilities.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public IDescrambler openDescrambler() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openDescrambler is unimplemented.");
                    }
                    _reply.readException();
                    IDescrambler _result = IDescrambler.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public FrontendInfo getFrontendInfo(int frontendId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendId);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getFrontendInfo is unimplemented.");
                    }
                    _reply.readException();
                    FrontendInfo _result = (FrontendInfo) _reply.readTypedObject(FrontendInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public int[] getLnbIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getLnbIds is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public ILnb openLnbById(int lnbId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(lnbId);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openLnbById is unimplemented.");
                    }
                    _reply.readException();
                    ILnb _result = ILnb.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public ILnb openLnbByName(String lnbName, int[] lnbId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(lnbName);
                    _data.writeInt(lnbId.length);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openLnbByName is unimplemented.");
                    }
                    _reply.readException();
                    ILnb _result = ILnb.Stub.asInterface(_reply.readStrongBinder());
                    _reply.readIntArray(lnbId);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public void setLna(boolean bEnable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(bEnable);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setLna is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public void setMaxNumberOfFrontends(int frontendType, int maxNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendType);
                    _data.writeInt(maxNumber);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setMaxNumberOfFrontends is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public int getMaxNumberOfFrontends(int frontendType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendType);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getMaxNumberOfFrontends is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public boolean isLnaSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method isLnaSupported is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public int[] getDemuxIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getDemuxIds is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public IDemux openDemuxById(int demuxId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(demuxId);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openDemuxById is unimplemented.");
                    }
                    _reply.readException();
                    IDemux _result = IDemux.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
            public DemuxInfo getDemuxInfo(int demuxId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(demuxId);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getDemuxInfo is unimplemented.");
                    }
                    _reply.readException();
                    DemuxInfo _result = (DemuxInfo) _reply.readTypedObject(DemuxInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ITuner
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

            @Override // android.hardware.p005tv.tuner.ITuner
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
