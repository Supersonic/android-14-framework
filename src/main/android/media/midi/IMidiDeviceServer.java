package android.media.midi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.io.FileDescriptor;
/* loaded from: classes2.dex */
public interface IMidiDeviceServer extends IInterface {
    void closeDevice() throws RemoteException;

    void closePort(IBinder iBinder) throws RemoteException;

    int connectPorts(IBinder iBinder, FileDescriptor fileDescriptor, int i) throws RemoteException;

    MidiDeviceInfo getDeviceInfo() throws RemoteException;

    FileDescriptor openInputPort(IBinder iBinder, int i) throws RemoteException;

    FileDescriptor openOutputPort(IBinder iBinder, int i) throws RemoteException;

    void setDeviceInfo(MidiDeviceInfo midiDeviceInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMidiDeviceServer {
        @Override // android.media.midi.IMidiDeviceServer
        public FileDescriptor openInputPort(IBinder token, int portNumber) throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiDeviceServer
        public FileDescriptor openOutputPort(IBinder token, int portNumber) throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiDeviceServer
        public void closePort(IBinder token) throws RemoteException {
        }

        @Override // android.media.midi.IMidiDeviceServer
        public void closeDevice() throws RemoteException {
        }

        @Override // android.media.midi.IMidiDeviceServer
        public int connectPorts(IBinder token, FileDescriptor fd, int outputPortNumber) throws RemoteException {
            return 0;
        }

        @Override // android.media.midi.IMidiDeviceServer
        public MidiDeviceInfo getDeviceInfo() throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiDeviceServer
        public void setDeviceInfo(MidiDeviceInfo deviceInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMidiDeviceServer {
        public static final String DESCRIPTOR = "android.media.midi.IMidiDeviceServer";
        static final int TRANSACTION_closeDevice = 4;
        static final int TRANSACTION_closePort = 3;
        static final int TRANSACTION_connectPorts = 5;
        static final int TRANSACTION_getDeviceInfo = 6;
        static final int TRANSACTION_openInputPort = 1;
        static final int TRANSACTION_openOutputPort = 2;
        static final int TRANSACTION_setDeviceInfo = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMidiDeviceServer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMidiDeviceServer)) {
                return (IMidiDeviceServer) iin;
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
                    return "openInputPort";
                case 2:
                    return "openOutputPort";
                case 3:
                    return "closePort";
                case 4:
                    return "closeDevice";
                case 5:
                    return "connectPorts";
                case 6:
                    return "getDeviceInfo";
                case 7:
                    return "setDeviceInfo";
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
                            IBinder _arg0 = data.readStrongBinder();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            FileDescriptor _result = openInputPort(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeRawFileDescriptor(_result);
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            FileDescriptor _result2 = openOutputPort(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeRawFileDescriptor(_result2);
                            break;
                        case 3:
                            IBinder _arg03 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            closePort(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            closeDevice();
                            break;
                        case 5:
                            IBinder _arg04 = data.readStrongBinder();
                            FileDescriptor _arg13 = data.readRawFileDescriptor();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = connectPorts(_arg04, _arg13, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 6:
                            MidiDeviceInfo _result4 = getDeviceInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 7:
                            MidiDeviceInfo _arg05 = (MidiDeviceInfo) data.readTypedObject(MidiDeviceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setDeviceInfo(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMidiDeviceServer {
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

            @Override // android.media.midi.IMidiDeviceServer
            public FileDescriptor openInputPort(IBinder token, int portNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(portNumber);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    FileDescriptor _result = _reply.readRawFileDescriptor();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiDeviceServer
            public FileDescriptor openOutputPort(IBinder token, int portNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(portNumber);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    FileDescriptor _result = _reply.readRawFileDescriptor();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiDeviceServer
            public void closePort(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiDeviceServer
            public void closeDevice() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiDeviceServer
            public int connectPorts(IBinder token, FileDescriptor fd, int outputPortNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeRawFileDescriptor(fd);
                    _data.writeInt(outputPortNumber);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiDeviceServer
            public MidiDeviceInfo getDeviceInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    MidiDeviceInfo _result = (MidiDeviceInfo) _reply.readTypedObject(MidiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiDeviceServer
            public void setDeviceInfo(MidiDeviceInfo deviceInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(deviceInfo, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
