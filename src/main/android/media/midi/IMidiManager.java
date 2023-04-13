package android.media.midi;

import android.bluetooth.BluetoothDevice;
import android.media.midi.IMidiDeviceListener;
import android.media.midi.IMidiDeviceOpenCallback;
import android.media.midi.IMidiDeviceServer;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMidiManager extends IInterface {
    void closeDevice(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    MidiDeviceStatus getDeviceStatus(MidiDeviceInfo midiDeviceInfo) throws RemoteException;

    MidiDeviceInfo[] getDevices() throws RemoteException;

    MidiDeviceInfo[] getDevicesForTransport(int i) throws RemoteException;

    MidiDeviceInfo getServiceDeviceInfo(String str, String str2) throws RemoteException;

    void openBluetoothDevice(IBinder iBinder, BluetoothDevice bluetoothDevice, IMidiDeviceOpenCallback iMidiDeviceOpenCallback) throws RemoteException;

    void openDevice(IBinder iBinder, MidiDeviceInfo midiDeviceInfo, IMidiDeviceOpenCallback iMidiDeviceOpenCallback) throws RemoteException;

    MidiDeviceInfo registerDeviceServer(IMidiDeviceServer iMidiDeviceServer, int i, int i2, String[] strArr, String[] strArr2, Bundle bundle, int i3, int i4) throws RemoteException;

    void registerListener(IBinder iBinder, IMidiDeviceListener iMidiDeviceListener) throws RemoteException;

    void setDeviceStatus(IMidiDeviceServer iMidiDeviceServer, MidiDeviceStatus midiDeviceStatus) throws RemoteException;

    void unregisterDeviceServer(IMidiDeviceServer iMidiDeviceServer) throws RemoteException;

    void unregisterListener(IBinder iBinder, IMidiDeviceListener iMidiDeviceListener) throws RemoteException;

    void updateTotalBytes(IMidiDeviceServer iMidiDeviceServer, int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMidiManager {
        @Override // android.media.midi.IMidiManager
        public MidiDeviceInfo[] getDevices() throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiManager
        public MidiDeviceInfo[] getDevicesForTransport(int transport) throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiManager
        public void registerListener(IBinder clientToken, IMidiDeviceListener listener) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public void unregisterListener(IBinder clientToken, IMidiDeviceListener listener) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public void openDevice(IBinder clientToken, MidiDeviceInfo device, IMidiDeviceOpenCallback callback) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public void openBluetoothDevice(IBinder clientToken, BluetoothDevice bluetoothDevice, IMidiDeviceOpenCallback callback) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public void closeDevice(IBinder clientToken, IBinder deviceToken) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public MidiDeviceInfo registerDeviceServer(IMidiDeviceServer server, int numInputPorts, int numOutputPorts, String[] inputPortNames, String[] outputPortNames, Bundle properties, int type, int defaultProtocol) throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiManager
        public void unregisterDeviceServer(IMidiDeviceServer server) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public MidiDeviceInfo getServiceDeviceInfo(String packageName, String className) throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiManager
        public MidiDeviceStatus getDeviceStatus(MidiDeviceInfo deviceInfo) throws RemoteException {
            return null;
        }

        @Override // android.media.midi.IMidiManager
        public void setDeviceStatus(IMidiDeviceServer server, MidiDeviceStatus status) throws RemoteException {
        }

        @Override // android.media.midi.IMidiManager
        public void updateTotalBytes(IMidiDeviceServer server, int inputBytes, int outputBytes) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMidiManager {
        public static final String DESCRIPTOR = "android.media.midi.IMidiManager";
        static final int TRANSACTION_closeDevice = 7;
        static final int TRANSACTION_getDeviceStatus = 11;
        static final int TRANSACTION_getDevices = 1;
        static final int TRANSACTION_getDevicesForTransport = 2;
        static final int TRANSACTION_getServiceDeviceInfo = 10;
        static final int TRANSACTION_openBluetoothDevice = 6;
        static final int TRANSACTION_openDevice = 5;
        static final int TRANSACTION_registerDeviceServer = 8;
        static final int TRANSACTION_registerListener = 3;
        static final int TRANSACTION_setDeviceStatus = 12;
        static final int TRANSACTION_unregisterDeviceServer = 9;
        static final int TRANSACTION_unregisterListener = 4;
        static final int TRANSACTION_updateTotalBytes = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMidiManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMidiManager)) {
                return (IMidiManager) iin;
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
                    return "getDevices";
                case 2:
                    return "getDevicesForTransport";
                case 3:
                    return "registerListener";
                case 4:
                    return "unregisterListener";
                case 5:
                    return "openDevice";
                case 6:
                    return "openBluetoothDevice";
                case 7:
                    return "closeDevice";
                case 8:
                    return "registerDeviceServer";
                case 9:
                    return "unregisterDeviceServer";
                case 10:
                    return "getServiceDeviceInfo";
                case 11:
                    return "getDeviceStatus";
                case 12:
                    return "setDeviceStatus";
                case 13:
                    return "updateTotalBytes";
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
                            MidiDeviceInfo[] _result = getDevices();
                            reply.writeNoException();
                            reply.writeTypedArray(_result, 1);
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            MidiDeviceInfo[] _result2 = getDevicesForTransport(_arg0);
                            reply.writeNoException();
                            reply.writeTypedArray(_result2, 1);
                            break;
                        case 3:
                            IBinder _arg02 = data.readStrongBinder();
                            IMidiDeviceListener _arg1 = IMidiDeviceListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerListener(_arg02, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            IBinder _arg03 = data.readStrongBinder();
                            IMidiDeviceListener _arg12 = IMidiDeviceListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterListener(_arg03, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            IBinder _arg04 = data.readStrongBinder();
                            MidiDeviceInfo _arg13 = (MidiDeviceInfo) data.readTypedObject(MidiDeviceInfo.CREATOR);
                            IMidiDeviceOpenCallback _arg2 = IMidiDeviceOpenCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            openDevice(_arg04, _arg13, _arg2);
                            reply.writeNoException();
                            break;
                        case 6:
                            IBinder _arg05 = data.readStrongBinder();
                            BluetoothDevice _arg14 = (BluetoothDevice) data.readTypedObject(BluetoothDevice.CREATOR);
                            IMidiDeviceOpenCallback _arg22 = IMidiDeviceOpenCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            openBluetoothDevice(_arg05, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 7:
                            IBinder _arg06 = data.readStrongBinder();
                            IBinder _arg15 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            closeDevice(_arg06, _arg15);
                            reply.writeNoException();
                            break;
                        case 8:
                            IMidiDeviceServer _arg07 = IMidiDeviceServer.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            int _arg23 = data.readInt();
                            String[] _arg3 = data.createStringArray();
                            String[] _arg4 = data.createStringArray();
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg6 = data.readInt();
                            int _arg7 = data.readInt();
                            data.enforceNoDataAvail();
                            MidiDeviceInfo _result3 = registerDeviceServer(_arg07, _arg16, _arg23, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 9:
                            IMidiDeviceServer _arg08 = IMidiDeviceServer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterDeviceServer(_arg08);
                            reply.writeNoException();
                            break;
                        case 10:
                            String _arg09 = data.readString();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            MidiDeviceInfo _result4 = getServiceDeviceInfo(_arg09, _arg17);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 11:
                            MidiDeviceInfo _arg010 = (MidiDeviceInfo) data.readTypedObject(MidiDeviceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            MidiDeviceStatus _result5 = getDeviceStatus(_arg010);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 12:
                            IMidiDeviceServer _arg011 = IMidiDeviceServer.Stub.asInterface(data.readStrongBinder());
                            MidiDeviceStatus _arg18 = (MidiDeviceStatus) data.readTypedObject(MidiDeviceStatus.CREATOR);
                            data.enforceNoDataAvail();
                            setDeviceStatus(_arg011, _arg18);
                            reply.writeNoException();
                            break;
                        case 13:
                            IMidiDeviceServer _arg012 = IMidiDeviceServer.Stub.asInterface(data.readStrongBinder());
                            int _arg19 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            updateTotalBytes(_arg012, _arg19, _arg24);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMidiManager {
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

            @Override // android.media.midi.IMidiManager
            public MidiDeviceInfo[] getDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    MidiDeviceInfo[] _result = (MidiDeviceInfo[]) _reply.createTypedArray(MidiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public MidiDeviceInfo[] getDevicesForTransport(int transport) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(transport);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    MidiDeviceInfo[] _result = (MidiDeviceInfo[]) _reply.createTypedArray(MidiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void registerListener(IBinder clientToken, IMidiDeviceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void unregisterListener(IBinder clientToken, IMidiDeviceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void openDevice(IBinder clientToken, MidiDeviceInfo device, IMidiDeviceOpenCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeTypedObject(device, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void openBluetoothDevice(IBinder clientToken, BluetoothDevice bluetoothDevice, IMidiDeviceOpenCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeTypedObject(bluetoothDevice, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void closeDevice(IBinder clientToken, IBinder deviceToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeStrongBinder(deviceToken);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public MidiDeviceInfo registerDeviceServer(IMidiDeviceServer server, int numInputPorts, int numOutputPorts, String[] inputPortNames, String[] outputPortNames, Bundle properties, int type, int defaultProtocol) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(server);
                    _data.writeInt(numInputPorts);
                    _data.writeInt(numOutputPorts);
                    _data.writeStringArray(inputPortNames);
                    _data.writeStringArray(outputPortNames);
                    _data.writeTypedObject(properties, 0);
                    _data.writeInt(type);
                    _data.writeInt(defaultProtocol);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    MidiDeviceInfo _result = (MidiDeviceInfo) _reply.readTypedObject(MidiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void unregisterDeviceServer(IMidiDeviceServer server) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(server);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public MidiDeviceInfo getServiceDeviceInfo(String packageName, String className) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(className);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    MidiDeviceInfo _result = (MidiDeviceInfo) _reply.readTypedObject(MidiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public MidiDeviceStatus getDeviceStatus(MidiDeviceInfo deviceInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(deviceInfo, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    MidiDeviceStatus _result = (MidiDeviceStatus) _reply.readTypedObject(MidiDeviceStatus.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void setDeviceStatus(IMidiDeviceServer server, MidiDeviceStatus status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(server);
                    _data.writeTypedObject(status, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.midi.IMidiManager
            public void updateTotalBytes(IMidiDeviceServer server, int inputBytes, int outputBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(server);
                    _data.writeInt(inputBytes);
                    _data.writeInt(outputBytes);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
