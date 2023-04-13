package android.app.timedetector;

import android.app.time.ExternalTimeSuggestion;
import android.app.time.ITimeDetectorListener;
import android.app.time.TimeCapabilitiesAndConfig;
import android.app.time.TimeConfiguration;
import android.app.time.TimeState;
import android.app.time.UnixEpochTime;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ITimeDetectorService extends IInterface {
    public static final String DESCRIPTOR = "android.app.timedetector.ITimeDetectorService";

    void addListener(ITimeDetectorListener iTimeDetectorListener) throws RemoteException;

    boolean confirmTime(UnixEpochTime unixEpochTime) throws RemoteException;

    TimeCapabilitiesAndConfig getCapabilitiesAndConfig() throws RemoteException;

    TimeState getTimeState() throws RemoteException;

    UnixEpochTime latestNetworkTime() throws RemoteException;

    void removeListener(ITimeDetectorListener iTimeDetectorListener) throws RemoteException;

    boolean setManualTime(ManualTimeSuggestion manualTimeSuggestion) throws RemoteException;

    void suggestExternalTime(ExternalTimeSuggestion externalTimeSuggestion) throws RemoteException;

    boolean suggestManualTime(ManualTimeSuggestion manualTimeSuggestion) throws RemoteException;

    void suggestTelephonyTime(TelephonyTimeSuggestion telephonyTimeSuggestion) throws RemoteException;

    boolean updateConfiguration(TimeConfiguration timeConfiguration) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ITimeDetectorService {
        @Override // android.app.timedetector.ITimeDetectorService
        public TimeCapabilitiesAndConfig getCapabilitiesAndConfig() throws RemoteException {
            return null;
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public void addListener(ITimeDetectorListener listener) throws RemoteException {
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public void removeListener(ITimeDetectorListener listener) throws RemoteException {
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public boolean updateConfiguration(TimeConfiguration timeConfiguration) throws RemoteException {
            return false;
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public TimeState getTimeState() throws RemoteException {
            return null;
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public boolean confirmTime(UnixEpochTime time) throws RemoteException {
            return false;
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public boolean setManualTime(ManualTimeSuggestion timeZoneSuggestion) throws RemoteException {
            return false;
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public void suggestExternalTime(ExternalTimeSuggestion timeSuggestion) throws RemoteException {
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public boolean suggestManualTime(ManualTimeSuggestion timeSuggestion) throws RemoteException {
            return false;
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public void suggestTelephonyTime(TelephonyTimeSuggestion timeSuggestion) throws RemoteException {
        }

        @Override // android.app.timedetector.ITimeDetectorService
        public UnixEpochTime latestNetworkTime() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITimeDetectorService {
        static final int TRANSACTION_addListener = 2;
        static final int TRANSACTION_confirmTime = 6;
        static final int TRANSACTION_getCapabilitiesAndConfig = 1;
        static final int TRANSACTION_getTimeState = 5;
        static final int TRANSACTION_latestNetworkTime = 11;
        static final int TRANSACTION_removeListener = 3;
        static final int TRANSACTION_setManualTime = 7;
        static final int TRANSACTION_suggestExternalTime = 8;
        static final int TRANSACTION_suggestManualTime = 9;
        static final int TRANSACTION_suggestTelephonyTime = 10;
        static final int TRANSACTION_updateConfiguration = 4;

        public Stub() {
            attachInterface(this, ITimeDetectorService.DESCRIPTOR);
        }

        public static ITimeDetectorService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITimeDetectorService.DESCRIPTOR);
            if (iin != null && (iin instanceof ITimeDetectorService)) {
                return (ITimeDetectorService) iin;
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
                    return "getCapabilitiesAndConfig";
                case 2:
                    return "addListener";
                case 3:
                    return "removeListener";
                case 4:
                    return "updateConfiguration";
                case 5:
                    return "getTimeState";
                case 6:
                    return "confirmTime";
                case 7:
                    return "setManualTime";
                case 8:
                    return "suggestExternalTime";
                case 9:
                    return "suggestManualTime";
                case 10:
                    return "suggestTelephonyTime";
                case 11:
                    return "latestNetworkTime";
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
                data.enforceInterface(ITimeDetectorService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITimeDetectorService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            TimeCapabilitiesAndConfig _result = getCapabilitiesAndConfig();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ITimeDetectorListener _arg0 = ITimeDetectorListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 3:
                            ITimeDetectorListener _arg02 = ITimeDetectorListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeListener(_arg02);
                            reply.writeNoException();
                            break;
                        case 4:
                            TimeConfiguration _arg03 = (TimeConfiguration) data.readTypedObject(TimeConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result2 = updateConfiguration(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            TimeState _result3 = getTimeState();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 6:
                            UnixEpochTime _arg04 = (UnixEpochTime) data.readTypedObject(UnixEpochTime.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = confirmTime(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 7:
                            ManualTimeSuggestion _arg05 = (ManualTimeSuggestion) data.readTypedObject(ManualTimeSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = setManualTime(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            ExternalTimeSuggestion _arg06 = (ExternalTimeSuggestion) data.readTypedObject(ExternalTimeSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            suggestExternalTime(_arg06);
                            reply.writeNoException();
                            break;
                        case 9:
                            ManualTimeSuggestion _arg07 = (ManualTimeSuggestion) data.readTypedObject(ManualTimeSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result6 = suggestManualTime(_arg07);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 10:
                            TelephonyTimeSuggestion _arg08 = (TelephonyTimeSuggestion) data.readTypedObject(TelephonyTimeSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            suggestTelephonyTime(_arg08);
                            reply.writeNoException();
                            break;
                        case 11:
                            UnixEpochTime _result7 = latestNetworkTime();
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ITimeDetectorService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITimeDetectorService.DESCRIPTOR;
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public TimeCapabilitiesAndConfig getCapabilitiesAndConfig() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    TimeCapabilitiesAndConfig _result = (TimeCapabilitiesAndConfig) _reply.readTypedObject(TimeCapabilitiesAndConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public void addListener(ITimeDetectorListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public void removeListener(ITimeDetectorListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public boolean updateConfiguration(TimeConfiguration timeConfiguration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeConfiguration, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public TimeState getTimeState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    TimeState _result = (TimeState) _reply.readTypedObject(TimeState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public boolean confirmTime(UnixEpochTime time) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(time, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public boolean setManualTime(ManualTimeSuggestion timeZoneSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeZoneSuggestion, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public void suggestExternalTime(ExternalTimeSuggestion timeSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeSuggestion, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public boolean suggestManualTime(ManualTimeSuggestion timeSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeSuggestion, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public void suggestTelephonyTime(TelephonyTimeSuggestion timeSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeSuggestion, 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timedetector.ITimeDetectorService
            public UnixEpochTime latestNetworkTime() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeDetectorService.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    UnixEpochTime _result = (UnixEpochTime) _reply.readTypedObject(UnixEpochTime.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
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
