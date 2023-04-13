package android.media.p007tv;

import android.media.AudioPresentation;
import android.media.p007tv.ITvInputSession;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* renamed from: android.media.tv.ITvInputSessionCallback */
/* loaded from: classes2.dex */
public interface ITvInputSessionCallback extends IInterface {
    void onAdBufferConsumed(AdBuffer adBuffer) throws RemoteException;

    void onAdResponse(AdResponse adResponse) throws RemoteException;

    void onAitInfoUpdated(AitInfo aitInfo) throws RemoteException;

    void onAudioPresentationSelected(int i, int i2) throws RemoteException;

    void onAudioPresentationsChanged(List<AudioPresentation> list) throws RemoteException;

    void onAvailableSpeeds(float[] fArr) throws RemoteException;

    void onBroadcastInfoResponse(BroadcastInfoResponse broadcastInfoResponse) throws RemoteException;

    void onChannelRetuned(Uri uri) throws RemoteException;

    void onContentAllowed() throws RemoteException;

    void onContentBlocked(String str) throws RemoteException;

    void onCueingMessageAvailability(boolean z) throws RemoteException;

    void onError(int i) throws RemoteException;

    void onLayoutSurface(int i, int i2, int i3, int i4) throws RemoteException;

    void onRecordingStopped(Uri uri) throws RemoteException;

    void onSessionCreated(ITvInputSession iTvInputSession, IBinder iBinder) throws RemoteException;

    void onSessionEvent(String str, Bundle bundle) throws RemoteException;

    void onSignalStrength(int i) throws RemoteException;

    void onTimeShiftCurrentPositionChanged(long j) throws RemoteException;

    void onTimeShiftMode(int i) throws RemoteException;

    void onTimeShiftStartPositionChanged(long j) throws RemoteException;

    void onTimeShiftStatusChanged(int i) throws RemoteException;

    void onTrackSelected(int i, String str) throws RemoteException;

    void onTracksChanged(List<TvTrackInfo> list) throws RemoteException;

    void onTuned(Uri uri) throws RemoteException;

    void onTvMessage(String str, Bundle bundle) throws RemoteException;

    void onVideoAvailable() throws RemoteException;

    void onVideoUnavailable(int i) throws RemoteException;

    /* renamed from: android.media.tv.ITvInputSessionCallback$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInputSessionCallback {
        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onSessionCreated(ITvInputSession session, IBinder hardwareSessionToken) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onSessionEvent(String name, Bundle args) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onChannelRetuned(Uri channelUri) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onAudioPresentationsChanged(List<AudioPresentation> tvAudioPresentations) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onAudioPresentationSelected(int presentationId, int programId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTracksChanged(List<TvTrackInfo> tracks) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTrackSelected(int type, String trackId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onVideoAvailable() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onVideoUnavailable(int reason) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onContentAllowed() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onContentBlocked(String rating) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onLayoutSurface(int left, int top, int right, int bottom) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTimeShiftStatusChanged(int status) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTimeShiftStartPositionChanged(long timeMs) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTimeShiftCurrentPositionChanged(long timeMs) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onAitInfoUpdated(AitInfo aitInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onSignalStrength(int strength) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onCueingMessageAvailability(boolean available) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTimeShiftMode(int mode) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onAvailableSpeeds(float[] speeds) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTuned(Uri channelUri) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onRecordingStopped(Uri recordedProgramUri) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onError(int error) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onBroadcastInfoResponse(BroadcastInfoResponse response) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onAdResponse(AdResponse response) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onAdBufferConsumed(AdBuffer buffer) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSessionCallback
        public void onTvMessage(String type, Bundle data) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.ITvInputSessionCallback$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInputSessionCallback {
        public static final String DESCRIPTOR = "android.media.tv.ITvInputSessionCallback";
        static final int TRANSACTION_onAdBufferConsumed = 26;
        static final int TRANSACTION_onAdResponse = 25;
        static final int TRANSACTION_onAitInfoUpdated = 16;
        static final int TRANSACTION_onAudioPresentationSelected = 5;
        static final int TRANSACTION_onAudioPresentationsChanged = 4;
        static final int TRANSACTION_onAvailableSpeeds = 20;
        static final int TRANSACTION_onBroadcastInfoResponse = 24;
        static final int TRANSACTION_onChannelRetuned = 3;
        static final int TRANSACTION_onContentAllowed = 10;
        static final int TRANSACTION_onContentBlocked = 11;
        static final int TRANSACTION_onCueingMessageAvailability = 18;
        static final int TRANSACTION_onError = 23;
        static final int TRANSACTION_onLayoutSurface = 12;
        static final int TRANSACTION_onRecordingStopped = 22;
        static final int TRANSACTION_onSessionCreated = 1;
        static final int TRANSACTION_onSessionEvent = 2;
        static final int TRANSACTION_onSignalStrength = 17;
        static final int TRANSACTION_onTimeShiftCurrentPositionChanged = 15;
        static final int TRANSACTION_onTimeShiftMode = 19;
        static final int TRANSACTION_onTimeShiftStartPositionChanged = 14;
        static final int TRANSACTION_onTimeShiftStatusChanged = 13;
        static final int TRANSACTION_onTrackSelected = 7;
        static final int TRANSACTION_onTracksChanged = 6;
        static final int TRANSACTION_onTuned = 21;
        static final int TRANSACTION_onTvMessage = 27;
        static final int TRANSACTION_onVideoAvailable = 8;
        static final int TRANSACTION_onVideoUnavailable = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputSessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInputSessionCallback)) {
                return (ITvInputSessionCallback) iin;
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
                    return "onSessionCreated";
                case 2:
                    return "onSessionEvent";
                case 3:
                    return "onChannelRetuned";
                case 4:
                    return "onAudioPresentationsChanged";
                case 5:
                    return "onAudioPresentationSelected";
                case 6:
                    return "onTracksChanged";
                case 7:
                    return "onTrackSelected";
                case 8:
                    return "onVideoAvailable";
                case 9:
                    return "onVideoUnavailable";
                case 10:
                    return "onContentAllowed";
                case 11:
                    return "onContentBlocked";
                case 12:
                    return "onLayoutSurface";
                case 13:
                    return "onTimeShiftStatusChanged";
                case 14:
                    return "onTimeShiftStartPositionChanged";
                case 15:
                    return "onTimeShiftCurrentPositionChanged";
                case 16:
                    return "onAitInfoUpdated";
                case 17:
                    return "onSignalStrength";
                case 18:
                    return "onCueingMessageAvailability";
                case 19:
                    return "onTimeShiftMode";
                case 20:
                    return "onAvailableSpeeds";
                case 21:
                    return "onTuned";
                case 22:
                    return "onRecordingStopped";
                case 23:
                    return "onError";
                case 24:
                    return "onBroadcastInfoResponse";
                case 25:
                    return "onAdResponse";
                case 26:
                    return "onAdBufferConsumed";
                case 27:
                    return "onTvMessage";
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
                            ITvInputSession _arg0 = ITvInputSession.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg1 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onSessionCreated(_arg0, _arg1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            Bundle _arg12 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onSessionEvent(_arg02, _arg12);
                            break;
                        case 3:
                            Uri _arg03 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            onChannelRetuned(_arg03);
                            break;
                        case 4:
                            List<AudioPresentation> _arg04 = data.createTypedArrayList(AudioPresentation.CREATOR);
                            data.enforceNoDataAvail();
                            onAudioPresentationsChanged(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onAudioPresentationSelected(_arg05, _arg13);
                            break;
                        case 6:
                            List<TvTrackInfo> _arg06 = data.createTypedArrayList(TvTrackInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTracksChanged(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            onTrackSelected(_arg07, _arg14);
                            break;
                        case 8:
                            onVideoAvailable();
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            onVideoUnavailable(_arg08);
                            break;
                        case 10:
                            onContentAllowed();
                            break;
                        case 11:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            onContentBlocked(_arg09);
                            break;
                        case 12:
                            int _arg010 = data.readInt();
                            int _arg15 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onLayoutSurface(_arg010, _arg15, _arg2, _arg3);
                            break;
                        case 13:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            onTimeShiftStatusChanged(_arg011);
                            break;
                        case 14:
                            long _arg012 = data.readLong();
                            data.enforceNoDataAvail();
                            onTimeShiftStartPositionChanged(_arg012);
                            break;
                        case 15:
                            long _arg013 = data.readLong();
                            data.enforceNoDataAvail();
                            onTimeShiftCurrentPositionChanged(_arg013);
                            break;
                        case 16:
                            AitInfo _arg014 = (AitInfo) data.readTypedObject(AitInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onAitInfoUpdated(_arg014);
                            break;
                        case 17:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            onSignalStrength(_arg015);
                            break;
                        case 18:
                            boolean _arg016 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onCueingMessageAvailability(_arg016);
                            break;
                        case 19:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            onTimeShiftMode(_arg017);
                            break;
                        case 20:
                            float[] _arg018 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            onAvailableSpeeds(_arg018);
                            break;
                        case 21:
                            Uri _arg019 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            onTuned(_arg019);
                            break;
                        case 22:
                            Uri _arg020 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            onRecordingStopped(_arg020);
                            break;
                        case 23:
                            int _arg021 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg021);
                            break;
                        case 24:
                            BroadcastInfoResponse _arg022 = (BroadcastInfoResponse) data.readTypedObject(BroadcastInfoResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onBroadcastInfoResponse(_arg022);
                            break;
                        case 25:
                            AdResponse _arg023 = (AdResponse) data.readTypedObject(AdResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onAdResponse(_arg023);
                            break;
                        case 26:
                            AdBuffer _arg024 = (AdBuffer) data.readTypedObject(AdBuffer.CREATOR);
                            data.enforceNoDataAvail();
                            onAdBufferConsumed(_arg024);
                            break;
                        case 27:
                            String _arg025 = data.readString();
                            Bundle _arg16 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onTvMessage(_arg025, _arg16);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.ITvInputSessionCallback$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInputSessionCallback {
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

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onSessionCreated(ITvInputSession session, IBinder hardwareSessionToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeStrongBinder(hardwareSessionToken);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onSessionEvent(String name, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeTypedObject(args, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onChannelRetuned(Uri channelUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(channelUri, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onAudioPresentationsChanged(List<AudioPresentation> tvAudioPresentations) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(tvAudioPresentations, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onAudioPresentationSelected(int presentationId, int programId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presentationId);
                    _data.writeInt(programId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTracksChanged(List<TvTrackInfo> tracks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(tracks, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTrackSelected(int type, String trackId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(trackId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onVideoAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onVideoUnavailable(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onContentAllowed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onContentBlocked(String rating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rating);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onLayoutSurface(int left, int top, int right, int bottom) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(left);
                    _data.writeInt(top);
                    _data.writeInt(right);
                    _data.writeInt(bottom);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTimeShiftStatusChanged(int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTimeShiftStartPositionChanged(long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timeMs);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTimeShiftCurrentPositionChanged(long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timeMs);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onAitInfoUpdated(AitInfo aitInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aitInfo, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onSignalStrength(int strength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strength);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onCueingMessageAvailability(boolean available) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(available);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTimeShiftMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onAvailableSpeeds(float[] speeds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloatArray(speeds);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTuned(Uri channelUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(channelUri, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onRecordingStopped(Uri recordedProgramUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recordedProgramUri, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onError(int error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(error);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onBroadcastInfoResponse(BroadcastInfoResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onAdResponse(AdResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onAdBufferConsumed(AdBuffer buffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(buffer, 0);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSessionCallback
            public void onTvMessage(String type, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 26;
        }
    }
}
