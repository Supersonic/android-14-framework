package android.hardware.p005tv.tuner;

import android.hardware.p005tv.tuner.IFrontendCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.hardware.tv.tuner.IFrontend */
/* loaded from: classes2.dex */
public interface IFrontend extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$tuner$IFrontend".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void close() throws RemoteException;

    int[] getFrontendStatusReadiness(int[] iArr) throws RemoteException;

    String getHardwareInfo() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    FrontendStatus[] getStatus(int[] iArr) throws RemoteException;

    int linkCiCam(int i) throws RemoteException;

    void removeOutputPid(int i) throws RemoteException;

    void scan(FrontendSettings frontendSettings, int i) throws RemoteException;

    void setCallback(IFrontendCallback iFrontendCallback) throws RemoteException;

    void setLnb(int i) throws RemoteException;

    void stopScan() throws RemoteException;

    void stopTune() throws RemoteException;

    void tune(FrontendSettings frontendSettings) throws RemoteException;

    void unlinkCiCam(int i) throws RemoteException;

    /* renamed from: android.hardware.tv.tuner.IFrontend$Default */
    /* loaded from: classes2.dex */
    public static class Default implements IFrontend {
        @Override // android.hardware.p005tv.tuner.IFrontend
        public void setCallback(IFrontendCallback callback) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void tune(FrontendSettings settings) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void stopTune() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void close() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void scan(FrontendSettings settings, int type) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void stopScan() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public FrontendStatus[] getStatus(int[] statusTypes) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void setLnb(int lnbId) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public int linkCiCam(int ciCamId) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void unlinkCiCam(int ciCamId) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public String getHardwareInfo() throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public void removeOutputPid(int pid) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public int[] getFrontendStatusReadiness(int[] statusTypes) throws RemoteException {
            return null;
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.IFrontend
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.hardware.tv.tuner.IFrontend$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IFrontend {
        static final int TRANSACTION_close = 4;
        static final int TRANSACTION_getFrontendStatusReadiness = 13;
        static final int TRANSACTION_getHardwareInfo = 11;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getStatus = 7;
        static final int TRANSACTION_linkCiCam = 9;
        static final int TRANSACTION_removeOutputPid = 12;
        static final int TRANSACTION_scan = 5;
        static final int TRANSACTION_setCallback = 1;
        static final int TRANSACTION_setLnb = 8;
        static final int TRANSACTION_stopScan = 6;
        static final int TRANSACTION_stopTune = 3;
        static final int TRANSACTION_tune = 2;
        static final int TRANSACTION_unlinkCiCam = 10;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IFrontend asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFrontend)) {
                return (IFrontend) iin;
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
                            IFrontendCallback _arg0 = IFrontendCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            FrontendSettings _arg02 = (FrontendSettings) data.readTypedObject(FrontendSettings.CREATOR);
                            data.enforceNoDataAvail();
                            tune(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            stopTune();
                            reply.writeNoException();
                            break;
                        case 4:
                            close();
                            reply.writeNoException();
                            break;
                        case 5:
                            FrontendSettings _arg03 = (FrontendSettings) data.readTypedObject(FrontendSettings.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            scan(_arg03, _arg1);
                            reply.writeNoException();
                            break;
                        case 6:
                            stopScan();
                            reply.writeNoException();
                            break;
                        case 7:
                            int[] _arg04 = data.createIntArray();
                            data.enforceNoDataAvail();
                            FrontendStatus[] _result = getStatus(_arg04);
                            reply.writeNoException();
                            reply.writeTypedArray(_result, 1);
                            break;
                        case 8:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            setLnb(_arg05);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = linkCiCam(_arg06);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            unlinkCiCam(_arg07);
                            reply.writeNoException();
                            break;
                        case 11:
                            String _result3 = getHardwareInfo();
                            reply.writeNoException();
                            reply.writeString(_result3);
                            break;
                        case 12:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            removeOutputPid(_arg08);
                            reply.writeNoException();
                            break;
                        case 13:
                            int[] _arg09 = data.createIntArray();
                            data.enforceNoDataAvail();
                            int[] _result4 = getFrontendStatusReadiness(_arg09);
                            reply.writeNoException();
                            reply.writeIntArray(_result4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.hardware.tv.tuner.IFrontend$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements IFrontend {
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

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void setCallback(IFrontendCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void tune(FrontendSettings settings) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(settings, 0);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method tune is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void stopTune() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stopTune is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void scan(FrontendSettings settings, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(settings, 0);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method scan is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void stopScan() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stopScan is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public FrontendStatus[] getStatus(int[] statusTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeIntArray(statusTypes);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getStatus is unimplemented.");
                    }
                    _reply.readException();
                    FrontendStatus[] _result = (FrontendStatus[]) _reply.createTypedArray(FrontendStatus.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void setLnb(int lnbId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(lnbId);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setLnb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public int linkCiCam(int ciCamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(ciCamId);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method linkCiCam is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void unlinkCiCam(int ciCamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(ciCamId);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method unlinkCiCam is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public String getHardwareInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getHardwareInfo is unimplemented.");
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public void removeOutputPid(int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(pid);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method removeOutputPid is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
            public int[] getFrontendStatusReadiness(int[] statusTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeIntArray(statusTypes);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getFrontendStatusReadiness is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IFrontend
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

            @Override // android.hardware.p005tv.tuner.IFrontend
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
