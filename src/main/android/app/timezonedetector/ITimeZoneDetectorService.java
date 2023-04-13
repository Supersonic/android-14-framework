package android.app.timezonedetector;

import android.app.time.ITimeZoneDetectorListener;
import android.app.time.TimeZoneCapabilitiesAndConfig;
import android.app.time.TimeZoneConfiguration;
import android.app.time.TimeZoneState;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ITimeZoneDetectorService extends IInterface {
    public static final String DESCRIPTOR = "android.app.timezonedetector.ITimeZoneDetectorService";

    void addListener(ITimeZoneDetectorListener iTimeZoneDetectorListener) throws RemoteException;

    boolean confirmTimeZone(String str) throws RemoteException;

    TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig() throws RemoteException;

    TimeZoneState getTimeZoneState() throws RemoteException;

    void removeListener(ITimeZoneDetectorListener iTimeZoneDetectorListener) throws RemoteException;

    boolean setManualTimeZone(ManualTimeZoneSuggestion manualTimeZoneSuggestion) throws RemoteException;

    boolean suggestManualTimeZone(ManualTimeZoneSuggestion manualTimeZoneSuggestion) throws RemoteException;

    void suggestTelephonyTimeZone(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion) throws RemoteException;

    boolean updateConfiguration(TimeZoneConfiguration timeZoneConfiguration) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ITimeZoneDetectorService {
        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig() throws RemoteException {
            return null;
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public void addListener(ITimeZoneDetectorListener listener) throws RemoteException {
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public void removeListener(ITimeZoneDetectorListener listener) throws RemoteException {
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public boolean updateConfiguration(TimeZoneConfiguration configuration) throws RemoteException {
            return false;
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public TimeZoneState getTimeZoneState() throws RemoteException {
            return null;
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public boolean confirmTimeZone(String timeZoneId) throws RemoteException {
            return false;
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public boolean setManualTimeZone(ManualTimeZoneSuggestion timeZoneSuggestion) throws RemoteException {
            return false;
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public boolean suggestManualTimeZone(ManualTimeZoneSuggestion timeZoneSuggestion) throws RemoteException {
            return false;
        }

        @Override // android.app.timezonedetector.ITimeZoneDetectorService
        public void suggestTelephonyTimeZone(TelephonyTimeZoneSuggestion timeZoneSuggestion) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITimeZoneDetectorService {
        static final int TRANSACTION_addListener = 2;
        static final int TRANSACTION_confirmTimeZone = 6;
        static final int TRANSACTION_getCapabilitiesAndConfig = 1;
        static final int TRANSACTION_getTimeZoneState = 5;
        static final int TRANSACTION_removeListener = 3;
        static final int TRANSACTION_setManualTimeZone = 7;
        static final int TRANSACTION_suggestManualTimeZone = 8;
        static final int TRANSACTION_suggestTelephonyTimeZone = 9;
        static final int TRANSACTION_updateConfiguration = 4;

        public Stub() {
            attachInterface(this, ITimeZoneDetectorService.DESCRIPTOR);
        }

        public static ITimeZoneDetectorService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITimeZoneDetectorService.DESCRIPTOR);
            if (iin != null && (iin instanceof ITimeZoneDetectorService)) {
                return (ITimeZoneDetectorService) iin;
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
                    return "getTimeZoneState";
                case 6:
                    return "confirmTimeZone";
                case 7:
                    return "setManualTimeZone";
                case 8:
                    return "suggestManualTimeZone";
                case 9:
                    return "suggestTelephonyTimeZone";
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
                data.enforceInterface(ITimeZoneDetectorService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITimeZoneDetectorService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            TimeZoneCapabilitiesAndConfig _result = getCapabilitiesAndConfig();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ITimeZoneDetectorListener _arg0 = ITimeZoneDetectorListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 3:
                            ITimeZoneDetectorListener _arg02 = ITimeZoneDetectorListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeListener(_arg02);
                            reply.writeNoException();
                            break;
                        case 4:
                            TimeZoneConfiguration _arg03 = (TimeZoneConfiguration) data.readTypedObject(TimeZoneConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result2 = updateConfiguration(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            TimeZoneState _result3 = getTimeZoneState();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 6:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = confirmTimeZone(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 7:
                            ManualTimeZoneSuggestion _arg05 = (ManualTimeZoneSuggestion) data.readTypedObject(ManualTimeZoneSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = setManualTimeZone(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            ManualTimeZoneSuggestion _arg06 = (ManualTimeZoneSuggestion) data.readTypedObject(ManualTimeZoneSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result6 = suggestManualTimeZone(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 9:
                            TelephonyTimeZoneSuggestion _arg07 = (TelephonyTimeZoneSuggestion) data.readTypedObject(TelephonyTimeZoneSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            suggestTelephonyTimeZone(_arg07);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ITimeZoneDetectorService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITimeZoneDetectorService.DESCRIPTOR;
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    TimeZoneCapabilitiesAndConfig _result = (TimeZoneCapabilitiesAndConfig) _reply.readTypedObject(TimeZoneCapabilitiesAndConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public void addListener(ITimeZoneDetectorListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public void removeListener(ITimeZoneDetectorListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public boolean updateConfiguration(TimeZoneConfiguration configuration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(configuration, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public TimeZoneState getTimeZoneState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    TimeZoneState _result = (TimeZoneState) _reply.readTypedObject(TimeZoneState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public boolean confirmTimeZone(String timeZoneId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    _data.writeString(timeZoneId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public boolean setManualTimeZone(ManualTimeZoneSuggestion timeZoneSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
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

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public boolean suggestManualTimeZone(ManualTimeZoneSuggestion timeZoneSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeZoneSuggestion, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.timezonedetector.ITimeZoneDetectorService
            public void suggestTelephonyTimeZone(TelephonyTimeZoneSuggestion timeZoneSuggestion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITimeZoneDetectorService.DESCRIPTOR);
                    _data.writeTypedObject(timeZoneSuggestion, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
