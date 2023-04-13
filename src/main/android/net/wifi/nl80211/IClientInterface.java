package android.net.wifi.nl80211;

import android.net.wifi.nl80211.ISendMgmtFrameEvent;
import android.net.wifi.nl80211.IWifiScannerImpl;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IClientInterface extends IInterface {
    public static final String DESCRIPTOR = "android.net.wifi.nl80211.IClientInterface";

    void SendMgmtFrame(byte[] bArr, ISendMgmtFrameEvent iSendMgmtFrameEvent, int i) throws RemoteException;

    String getInterfaceName() throws RemoteException;

    byte[] getMacAddress() throws RemoteException;

    int[] getPacketCounters() throws RemoteException;

    IWifiScannerImpl getWifiScannerImpl() throws RemoteException;

    int[] signalPoll() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IClientInterface {
        @Override // android.net.wifi.nl80211.IClientInterface
        public int[] getPacketCounters() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IClientInterface
        public int[] signalPoll() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IClientInterface
        public byte[] getMacAddress() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IClientInterface
        public String getInterfaceName() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IClientInterface
        public IWifiScannerImpl getWifiScannerImpl() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IClientInterface
        public void SendMgmtFrame(byte[] frame, ISendMgmtFrameEvent callback, int mcs) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IClientInterface {
        static final int TRANSACTION_SendMgmtFrame = 6;
        static final int TRANSACTION_getInterfaceName = 4;
        static final int TRANSACTION_getMacAddress = 3;
        static final int TRANSACTION_getPacketCounters = 1;
        static final int TRANSACTION_getWifiScannerImpl = 5;
        static final int TRANSACTION_signalPoll = 2;

        public Stub() {
            attachInterface(this, IClientInterface.DESCRIPTOR);
        }

        public static IClientInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IClientInterface.DESCRIPTOR);
            if (iin != null && (iin instanceof IClientInterface)) {
                return (IClientInterface) iin;
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
                    return "getPacketCounters";
                case 2:
                    return "signalPoll";
                case 3:
                    return "getMacAddress";
                case 4:
                    return "getInterfaceName";
                case 5:
                    return "getWifiScannerImpl";
                case 6:
                    return "SendMgmtFrame";
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
                data.enforceInterface(IClientInterface.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IClientInterface.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int[] _result = getPacketCounters();
                            reply.writeNoException();
                            reply.writeIntArray(_result);
                            break;
                        case 2:
                            int[] _result2 = signalPoll();
                            reply.writeNoException();
                            reply.writeIntArray(_result2);
                            break;
                        case 3:
                            byte[] _result3 = getMacAddress();
                            reply.writeNoException();
                            reply.writeByteArray(_result3);
                            break;
                        case 4:
                            String _result4 = getInterfaceName();
                            reply.writeNoException();
                            reply.writeString(_result4);
                            break;
                        case 5:
                            IWifiScannerImpl _result5 = getWifiScannerImpl();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 6:
                            byte[] _arg0 = data.createByteArray();
                            ISendMgmtFrameEvent _arg1 = ISendMgmtFrameEvent.Stub.asInterface(data.readStrongBinder());
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            SendMgmtFrame(_arg0, _arg1, _arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IClientInterface {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IClientInterface.DESCRIPTOR;
            }

            @Override // android.net.wifi.nl80211.IClientInterface
            public int[] getPacketCounters() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IClientInterface.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IClientInterface
            public int[] signalPoll() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IClientInterface.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IClientInterface
            public byte[] getMacAddress() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IClientInterface.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IClientInterface
            public String getInterfaceName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IClientInterface.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IClientInterface
            public IWifiScannerImpl getWifiScannerImpl() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IClientInterface.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    IWifiScannerImpl _result = IWifiScannerImpl.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IClientInterface
            public void SendMgmtFrame(byte[] frame, ISendMgmtFrameEvent callback, int mcs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IClientInterface.DESCRIPTOR);
                    _data.writeByteArray(frame);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(mcs);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
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
