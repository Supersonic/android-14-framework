package android.hardware.radio;

import android.hardware.radio.ITunerCallback;
import android.hardware.radio.ProgramList;
import android.hardware.radio.RadioManager;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
/* loaded from: classes2.dex */
public interface ITunerCallback extends IInterface {
    void onAntennaState(boolean z) throws RemoteException;

    void onBackgroundScanAvailabilityChange(boolean z) throws RemoteException;

    void onBackgroundScanComplete() throws RemoteException;

    void onConfigFlagUpdated(int i, boolean z) throws RemoteException;

    void onConfigurationChanged(RadioManager.BandConfig bandConfig) throws RemoteException;

    void onCurrentProgramInfoChanged(RadioManager.ProgramInfo programInfo) throws RemoteException;

    void onEmergencyAnnouncement(boolean z) throws RemoteException;

    void onError(int i) throws RemoteException;

    void onParametersUpdated(Map<String, String> map) throws RemoteException;

    void onProgramListChanged() throws RemoteException;

    void onProgramListUpdated(ProgramList.Chunk chunk) throws RemoteException;

    void onTrafficAnnouncement(boolean z) throws RemoteException;

    void onTuneFailed(int i, ProgramSelector programSelector) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ITunerCallback {
        @Override // android.hardware.radio.ITunerCallback
        public void onError(int status) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onTuneFailed(int result, ProgramSelector selector) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onConfigurationChanged(RadioManager.BandConfig config) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onCurrentProgramInfoChanged(RadioManager.ProgramInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onTrafficAnnouncement(boolean active) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onEmergencyAnnouncement(boolean active) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onAntennaState(boolean connected) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onBackgroundScanAvailabilityChange(boolean isAvailable) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onBackgroundScanComplete() throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onProgramListChanged() throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onProgramListUpdated(ProgramList.Chunk chunk) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onConfigFlagUpdated(int flag, boolean value) throws RemoteException {
        }

        @Override // android.hardware.radio.ITunerCallback
        public void onParametersUpdated(Map<String, String> parameters) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITunerCallback {
        public static final String DESCRIPTOR = "android.hardware.radio.ITunerCallback";
        static final int TRANSACTION_onAntennaState = 7;
        static final int TRANSACTION_onBackgroundScanAvailabilityChange = 8;
        static final int TRANSACTION_onBackgroundScanComplete = 9;
        static final int TRANSACTION_onConfigFlagUpdated = 12;
        static final int TRANSACTION_onConfigurationChanged = 3;
        static final int TRANSACTION_onCurrentProgramInfoChanged = 4;
        static final int TRANSACTION_onEmergencyAnnouncement = 6;
        static final int TRANSACTION_onError = 1;
        static final int TRANSACTION_onParametersUpdated = 13;
        static final int TRANSACTION_onProgramListChanged = 10;
        static final int TRANSACTION_onProgramListUpdated = 11;
        static final int TRANSACTION_onTrafficAnnouncement = 5;
        static final int TRANSACTION_onTuneFailed = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITunerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITunerCallback)) {
                return (ITunerCallback) iin;
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
                    return "onError";
                case 2:
                    return "onTuneFailed";
                case 3:
                    return "onConfigurationChanged";
                case 4:
                    return "onCurrentProgramInfoChanged";
                case 5:
                    return "onTrafficAnnouncement";
                case 6:
                    return "onEmergencyAnnouncement";
                case 7:
                    return "onAntennaState";
                case 8:
                    return "onBackgroundScanAvailabilityChange";
                case 9:
                    return "onBackgroundScanComplete";
                case 10:
                    return "onProgramListChanged";
                case 11:
                    return "onProgramListUpdated";
                case 12:
                    return "onConfigFlagUpdated";
                case 13:
                    return "onParametersUpdated";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, final Parcel data, Parcel reply, int flags) throws RemoteException {
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
                            data.enforceNoDataAvail();
                            onError(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            ProgramSelector _arg1 = (ProgramSelector) data.readTypedObject(ProgramSelector.CREATOR);
                            data.enforceNoDataAvail();
                            onTuneFailed(_arg02, _arg1);
                            break;
                        case 3:
                            RadioManager.BandConfig _arg03 = (RadioManager.BandConfig) data.readTypedObject(RadioManager.BandConfig.CREATOR);
                            data.enforceNoDataAvail();
                            onConfigurationChanged(_arg03);
                            break;
                        case 4:
                            RadioManager.ProgramInfo _arg04 = (RadioManager.ProgramInfo) data.readTypedObject(RadioManager.ProgramInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onCurrentProgramInfoChanged(_arg04);
                            break;
                        case 5:
                            boolean _arg05 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onTrafficAnnouncement(_arg05);
                            break;
                        case 6:
                            boolean _arg06 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onEmergencyAnnouncement(_arg06);
                            break;
                        case 7:
                            boolean _arg07 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAntennaState(_arg07);
                            break;
                        case 8:
                            boolean _arg08 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onBackgroundScanAvailabilityChange(_arg08);
                            break;
                        case 9:
                            onBackgroundScanComplete();
                            break;
                        case 10:
                            onProgramListChanged();
                            break;
                        case 11:
                            ProgramList.Chunk _arg09 = (ProgramList.Chunk) data.readTypedObject(ProgramList.Chunk.CREATOR);
                            data.enforceNoDataAvail();
                            onProgramListUpdated(_arg09);
                            break;
                        case 12:
                            int _arg010 = data.readInt();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onConfigFlagUpdated(_arg010, _arg12);
                            break;
                        case 13:
                            int N = data.readInt();
                            final Map<String, String> _arg011 = N < 0 ? null : new HashMap<>();
                            IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.hardware.radio.ITunerCallback$Stub$$ExternalSyntheticLambda0
                                @Override // java.util.function.IntConsumer
                                public final void accept(int i) {
                                    ITunerCallback.Stub.lambda$onTransact$0(Parcel.this, _arg011, i);
                                }
                            });
                            data.enforceNoDataAvail();
                            onParametersUpdated(_arg011);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$0(Parcel data, Map _arg0, int i) {
            String k = data.readString();
            String v = data.readString();
            _arg0.put(k, v);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ITunerCallback {
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

            @Override // android.hardware.radio.ITunerCallback
            public void onError(int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onTuneFailed(int result, ProgramSelector selector) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    _data.writeTypedObject(selector, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onConfigurationChanged(RadioManager.BandConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onCurrentProgramInfoChanged(RadioManager.ProgramInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onTrafficAnnouncement(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onEmergencyAnnouncement(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onAntennaState(boolean connected) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(connected);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onBackgroundScanAvailabilityChange(boolean isAvailable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isAvailable);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onBackgroundScanComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onProgramListChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onProgramListUpdated(ProgramList.Chunk chunk) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(chunk, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onConfigFlagUpdated(int flag, boolean value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag);
                    _data.writeBoolean(value);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITunerCallback
            public void onParametersUpdated(Map<String, String> parameters) throws RemoteException {
                final Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parameters == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(parameters.size());
                        parameters.forEach(new BiConsumer() { // from class: android.hardware.radio.ITunerCallback$Stub$Proxy$$ExternalSyntheticLambda0
                            @Override // java.util.function.BiConsumer
                            public final void accept(Object obj, Object obj2) {
                                ITunerCallback.Stub.Proxy.lambda$onParametersUpdated$0(Parcel.this, (String) obj, (String) obj2);
                            }
                        });
                    }
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$onParametersUpdated$0(Parcel _data, String k, String v) {
                _data.writeString(k);
                _data.writeString(v);
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
