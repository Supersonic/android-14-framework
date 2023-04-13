package android.hardware.radio;

import android.graphics.Bitmap;
import android.hardware.radio.ITuner;
import android.hardware.radio.ProgramList;
import android.hardware.radio.RadioManager;
import android.media.p007tv.interactive.TvInteractiveAppService;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
/* loaded from: classes2.dex */
public interface ITuner extends IInterface {
    void cancel() throws RemoteException;

    void cancelAnnouncement() throws RemoteException;

    void close() throws RemoteException;

    RadioManager.BandConfig getConfiguration() throws RemoteException;

    Bitmap getImage(int i) throws RemoteException;

    Map<String, String> getParameters(List<String> list) throws RemoteException;

    boolean isClosed() throws RemoteException;

    boolean isConfigFlagSet(int i) throws RemoteException;

    boolean isConfigFlagSupported(int i) throws RemoteException;

    boolean isMuted() throws RemoteException;

    void seek(boolean z, boolean z2) throws RemoteException;

    void setConfigFlag(int i, boolean z) throws RemoteException;

    void setConfiguration(RadioManager.BandConfig bandConfig) throws RemoteException;

    void setMuted(boolean z) throws RemoteException;

    Map<String, String> setParameters(Map<String, String> map) throws RemoteException;

    boolean startBackgroundScan() throws RemoteException;

    void startProgramListUpdates(ProgramList.Filter filter) throws RemoteException;

    void step(boolean z, boolean z2) throws RemoteException;

    void stopProgramListUpdates() throws RemoteException;

    void tune(ProgramSelector programSelector) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ITuner {
        @Override // android.hardware.radio.ITuner
        public void close() throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public boolean isClosed() throws RemoteException {
            return false;
        }

        @Override // android.hardware.radio.ITuner
        public void setConfiguration(RadioManager.BandConfig config) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public RadioManager.BandConfig getConfiguration() throws RemoteException {
            return null;
        }

        @Override // android.hardware.radio.ITuner
        public void setMuted(boolean mute) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public boolean isMuted() throws RemoteException {
            return false;
        }

        @Override // android.hardware.radio.ITuner
        public void step(boolean directionDown, boolean skipSubChannel) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public void seek(boolean directionDown, boolean skipSubChannel) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public void tune(ProgramSelector selector) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public void cancel() throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public void cancelAnnouncement() throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public Bitmap getImage(int id) throws RemoteException {
            return null;
        }

        @Override // android.hardware.radio.ITuner
        public boolean startBackgroundScan() throws RemoteException {
            return false;
        }

        @Override // android.hardware.radio.ITuner
        public void startProgramListUpdates(ProgramList.Filter filter) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public void stopProgramListUpdates() throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public boolean isConfigFlagSupported(int flag) throws RemoteException {
            return false;
        }

        @Override // android.hardware.radio.ITuner
        public boolean isConfigFlagSet(int flag) throws RemoteException {
            return false;
        }

        @Override // android.hardware.radio.ITuner
        public void setConfigFlag(int flag, boolean value) throws RemoteException {
        }

        @Override // android.hardware.radio.ITuner
        public Map<String, String> setParameters(Map<String, String> parameters) throws RemoteException {
            return null;
        }

        @Override // android.hardware.radio.ITuner
        public Map<String, String> getParameters(List<String> keys) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITuner {
        public static final String DESCRIPTOR = "android.hardware.radio.ITuner";
        static final int TRANSACTION_cancel = 10;
        static final int TRANSACTION_cancelAnnouncement = 11;
        static final int TRANSACTION_close = 1;
        static final int TRANSACTION_getConfiguration = 4;
        static final int TRANSACTION_getImage = 12;
        static final int TRANSACTION_getParameters = 20;
        static final int TRANSACTION_isClosed = 2;
        static final int TRANSACTION_isConfigFlagSet = 17;
        static final int TRANSACTION_isConfigFlagSupported = 16;
        static final int TRANSACTION_isMuted = 6;
        static final int TRANSACTION_seek = 8;
        static final int TRANSACTION_setConfigFlag = 18;
        static final int TRANSACTION_setConfiguration = 3;
        static final int TRANSACTION_setMuted = 5;
        static final int TRANSACTION_setParameters = 19;
        static final int TRANSACTION_startBackgroundScan = 13;
        static final int TRANSACTION_startProgramListUpdates = 14;
        static final int TRANSACTION_step = 7;
        static final int TRANSACTION_stopProgramListUpdates = 15;
        static final int TRANSACTION_tune = 9;

        public Stub() {
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

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "close";
                case 2:
                    return "isClosed";
                case 3:
                    return "setConfiguration";
                case 4:
                    return "getConfiguration";
                case 5:
                    return "setMuted";
                case 6:
                    return "isMuted";
                case 7:
                    return "step";
                case 8:
                    return "seek";
                case 9:
                    return TvInteractiveAppService.PLAYBACK_COMMAND_TYPE_TUNE;
                case 10:
                    return "cancel";
                case 11:
                    return "cancelAnnouncement";
                case 12:
                    return "getImage";
                case 13:
                    return "startBackgroundScan";
                case 14:
                    return "startProgramListUpdates";
                case 15:
                    return "stopProgramListUpdates";
                case 16:
                    return "isConfigFlagSupported";
                case 17:
                    return "isConfigFlagSet";
                case 18:
                    return "setConfigFlag";
                case 19:
                    return "setParameters";
                case 20:
                    return "getParameters";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, final Parcel data, final Parcel reply, int flags) throws RemoteException {
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
                            close();
                            reply.writeNoException();
                            break;
                        case 2:
                            boolean _result = isClosed();
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 3:
                            RadioManager.BandConfig _arg0 = (RadioManager.BandConfig) data.readTypedObject(RadioManager.BandConfig.CREATOR);
                            data.enforceNoDataAvail();
                            setConfiguration(_arg0);
                            reply.writeNoException();
                            break;
                        case 4:
                            RadioManager.BandConfig _result2 = getConfiguration();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMuted(_arg02);
                            reply.writeNoException();
                            break;
                        case 6:
                            boolean _result3 = isMuted();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 7:
                            boolean _arg03 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            step(_arg03, _arg1);
                            reply.writeNoException();
                            break;
                        case 8:
                            boolean _arg04 = data.readBoolean();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            seek(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 9:
                            ProgramSelector _arg05 = (ProgramSelector) data.readTypedObject(ProgramSelector.CREATOR);
                            data.enforceNoDataAvail();
                            tune(_arg05);
                            reply.writeNoException();
                            break;
                        case 10:
                            cancel();
                            reply.writeNoException();
                            break;
                        case 11:
                            cancelAnnouncement();
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            Bitmap _result4 = getImage(_arg06);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 13:
                            boolean _result5 = startBackgroundScan();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 14:
                            ProgramList.Filter _arg07 = (ProgramList.Filter) data.readTypedObject(ProgramList.Filter.CREATOR);
                            data.enforceNoDataAvail();
                            startProgramListUpdates(_arg07);
                            reply.writeNoException();
                            break;
                        case 15:
                            stopProgramListUpdates();
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = isConfigFlagSupported(_arg08);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 17:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = isConfigFlagSet(_arg09);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 18:
                            int _arg010 = data.readInt();
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setConfigFlag(_arg010, _arg13);
                            reply.writeNoException();
                            break;
                        case 19:
                            int N = data.readInt();
                            final Map<String, String> _arg011 = N < 0 ? null : new HashMap<>();
                            IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.hardware.radio.ITuner$Stub$$ExternalSyntheticLambda0
                                @Override // java.util.function.IntConsumer
                                public final void accept(int i) {
                                    ITuner.Stub.lambda$onTransact$0(Parcel.this, _arg011, i);
                                }
                            });
                            data.enforceNoDataAvail();
                            Map<String, String> _result8 = setParameters(_arg011);
                            reply.writeNoException();
                            if (_result8 == null) {
                                reply.writeInt(-1);
                                break;
                            } else {
                                reply.writeInt(_result8.size());
                                _result8.forEach(new BiConsumer() { // from class: android.hardware.radio.ITuner$Stub$$ExternalSyntheticLambda1
                                    @Override // java.util.function.BiConsumer
                                    public final void accept(Object obj, Object obj2) {
                                        ITuner.Stub.lambda$onTransact$1(Parcel.this, (String) obj, (String) obj2);
                                    }
                                });
                                break;
                            }
                        case 20:
                            List<String> _arg012 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            Map<String, String> _result9 = getParameters(_arg012);
                            reply.writeNoException();
                            if (_result9 == null) {
                                reply.writeInt(-1);
                                break;
                            } else {
                                reply.writeInt(_result9.size());
                                _result9.forEach(new BiConsumer() { // from class: android.hardware.radio.ITuner$Stub$$ExternalSyntheticLambda2
                                    @Override // java.util.function.BiConsumer
                                    public final void accept(Object obj, Object obj2) {
                                        ITuner.Stub.lambda$onTransact$2(Parcel.this, (String) obj, (String) obj2);
                                    }
                                });
                                break;
                            }
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

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$1(Parcel reply, String k, String v) {
            reply.writeString(k);
            reply.writeString(v);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$2(Parcel reply, String k, String v) {
            reply.writeString(k);
            reply.writeString(v);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ITuner {
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

            @Override // android.hardware.radio.ITuner
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public boolean isClosed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void setConfiguration(RadioManager.BandConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public RadioManager.BandConfig getConfiguration() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    RadioManager.BandConfig _result = (RadioManager.BandConfig) _reply.readTypedObject(RadioManager.BandConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void setMuted(boolean mute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(mute);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public boolean isMuted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void step(boolean directionDown, boolean skipSubChannel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(directionDown);
                    _data.writeBoolean(skipSubChannel);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void seek(boolean directionDown, boolean skipSubChannel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(directionDown);
                    _data.writeBoolean(skipSubChannel);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void tune(ProgramSelector selector) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(selector, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void cancel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void cancelAnnouncement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public Bitmap getImage(int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public boolean startBackgroundScan() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void startProgramListUpdates(ProgramList.Filter filter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(filter, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void stopProgramListUpdates() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public boolean isConfigFlagSupported(int flag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public boolean isConfigFlagSet(int flag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public void setConfigFlag(int flag, boolean value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag);
                    _data.writeBoolean(value);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ITuner
            public Map<String, String> setParameters(Map<String, String> parameters) throws RemoteException {
                final Parcel _data = Parcel.obtain(asBinder());
                final Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parameters == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(parameters.size());
                        parameters.forEach(new BiConsumer() { // from class: android.hardware.radio.ITuner$Stub$Proxy$$ExternalSyntheticLambda0
                            @Override // java.util.function.BiConsumer
                            public final void accept(Object obj, Object obj2) {
                                ITuner.Stub.Proxy.lambda$setParameters$0(Parcel.this, (String) obj, (String) obj2);
                            }
                        });
                    }
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    int N = _reply.readInt();
                    final Map<String, String> _result = N < 0 ? null : new HashMap<>();
                    IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.hardware.radio.ITuner$Stub$Proxy$$ExternalSyntheticLambda1
                        @Override // java.util.function.IntConsumer
                        public final void accept(int i) {
                            ITuner.Stub.Proxy.lambda$setParameters$1(Parcel.this, _result, i);
                        }
                    });
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$setParameters$0(Parcel _data, String k, String v) {
                _data.writeString(k);
                _data.writeString(v);
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$setParameters$1(Parcel _reply, Map _result, int i) {
                String k = _reply.readString();
                String v = _reply.readString();
                _result.put(k, v);
            }

            @Override // android.hardware.radio.ITuner
            public Map<String, String> getParameters(List<String> keys) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                final Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(keys);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    int N = _reply.readInt();
                    final Map<String, String> _result = N < 0 ? null : new HashMap<>();
                    IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.hardware.radio.ITuner$Stub$Proxy$$ExternalSyntheticLambda2
                        @Override // java.util.function.IntConsumer
                        public final void accept(int i) {
                            ITuner.Stub.Proxy.lambda$getParameters$2(Parcel.this, _result, i);
                        }
                    });
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$getParameters$2(Parcel _reply, Map _result, int i) {
                String k = _reply.readString();
                String v = _reply.readString();
                _result.put(k, v);
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 19;
        }
    }
}
