package android.media.p007tv;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.p007tv.interactive.TvInteractiveAppService;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.Surface;
/* renamed from: android.media.tv.ITvInputSession */
/* loaded from: classes2.dex */
public interface ITvInputSession extends IInterface {
    void appPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void createOverlayView(IBinder iBinder, Rect rect) throws RemoteException;

    void dispatchSurfaceChanged(int i, int i2, int i3) throws RemoteException;

    void notifyAdBuffer(AdBuffer adBuffer) throws RemoteException;

    void notifyTvMessage(String str, Bundle bundle) throws RemoteException;

    void pauseRecording(Bundle bundle) throws RemoteException;

    void relayoutOverlayView(Rect rect) throws RemoteException;

    void release() throws RemoteException;

    void removeBroadcastInfo(int i) throws RemoteException;

    void removeOverlayView() throws RemoteException;

    void requestAd(AdRequest adRequest) throws RemoteException;

    void requestBroadcastInfo(BroadcastInfoRequest broadcastInfoRequest) throws RemoteException;

    void resumeRecording(Bundle bundle) throws RemoteException;

    void selectAudioPresentation(int i, int i2) throws RemoteException;

    void selectTrack(int i, String str) throws RemoteException;

    void setCaptionEnabled(boolean z) throws RemoteException;

    void setInteractiveAppNotificationEnabled(boolean z) throws RemoteException;

    void setMain(boolean z) throws RemoteException;

    void setSurface(Surface surface) throws RemoteException;

    void setVolume(float f) throws RemoteException;

    void startRecording(Uri uri, Bundle bundle) throws RemoteException;

    void stopRecording() throws RemoteException;

    void timeShiftEnablePositionTracking(boolean z) throws RemoteException;

    void timeShiftPause() throws RemoteException;

    void timeShiftPlay(Uri uri) throws RemoteException;

    void timeShiftResume() throws RemoteException;

    void timeShiftSeekTo(long j) throws RemoteException;

    void timeShiftSetMode(int i) throws RemoteException;

    void timeShiftSetPlaybackParams(PlaybackParams playbackParams) throws RemoteException;

    void tune(Uri uri, Bundle bundle) throws RemoteException;

    void unblockContent(String str) throws RemoteException;

    /* renamed from: android.media.tv.ITvInputSession$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInputSession {
        @Override // android.media.p007tv.ITvInputSession
        public void release() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void setMain(boolean isMain) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void setSurface(Surface surface) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void dispatchSurfaceChanged(int format, int width, int height) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void setVolume(float volume) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void tune(Uri channelUri, Bundle params) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void setCaptionEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void selectAudioPresentation(int presentationId, int programId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void selectTrack(int type, String trackId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void setInteractiveAppNotificationEnabled(boolean enable) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void appPrivateCommand(String action, Bundle data) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void createOverlayView(IBinder windowToken, Rect frame) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void relayoutOverlayView(Rect frame) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void removeOverlayView() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void unblockContent(String unblockedRating) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftPlay(Uri recordedProgramUri) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftPause() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftResume() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftSeekTo(long timeMs) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftSetPlaybackParams(PlaybackParams params) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftSetMode(int mode) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void timeShiftEnablePositionTracking(boolean enable) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void startRecording(Uri programUri, Bundle params) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void stopRecording() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void pauseRecording(Bundle params) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void resumeRecording(Bundle params) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void requestBroadcastInfo(BroadcastInfoRequest request) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void removeBroadcastInfo(int id) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void requestAd(AdRequest request) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void notifyAdBuffer(AdBuffer buffer) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputSession
        public void notifyTvMessage(String type, Bundle data) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.ITvInputSession$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInputSession {
        public static final String DESCRIPTOR = "android.media.tv.ITvInputSession";
        static final int TRANSACTION_appPrivateCommand = 11;
        static final int TRANSACTION_createOverlayView = 12;
        static final int TRANSACTION_dispatchSurfaceChanged = 4;
        static final int TRANSACTION_notifyAdBuffer = 30;
        static final int TRANSACTION_notifyTvMessage = 31;
        static final int TRANSACTION_pauseRecording = 25;
        static final int TRANSACTION_relayoutOverlayView = 13;
        static final int TRANSACTION_release = 1;
        static final int TRANSACTION_removeBroadcastInfo = 28;
        static final int TRANSACTION_removeOverlayView = 14;
        static final int TRANSACTION_requestAd = 29;
        static final int TRANSACTION_requestBroadcastInfo = 27;
        static final int TRANSACTION_resumeRecording = 26;
        static final int TRANSACTION_selectAudioPresentation = 8;
        static final int TRANSACTION_selectTrack = 9;
        static final int TRANSACTION_setCaptionEnabled = 7;
        static final int TRANSACTION_setInteractiveAppNotificationEnabled = 10;
        static final int TRANSACTION_setMain = 2;
        static final int TRANSACTION_setSurface = 3;
        static final int TRANSACTION_setVolume = 5;
        static final int TRANSACTION_startRecording = 23;
        static final int TRANSACTION_stopRecording = 24;
        static final int TRANSACTION_timeShiftEnablePositionTracking = 22;
        static final int TRANSACTION_timeShiftPause = 17;
        static final int TRANSACTION_timeShiftPlay = 16;
        static final int TRANSACTION_timeShiftResume = 18;
        static final int TRANSACTION_timeShiftSeekTo = 19;
        static final int TRANSACTION_timeShiftSetMode = 21;
        static final int TRANSACTION_timeShiftSetPlaybackParams = 20;
        static final int TRANSACTION_tune = 6;
        static final int TRANSACTION_unblockContent = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInputSession)) {
                return (ITvInputSession) iin;
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
                    return "release";
                case 2:
                    return "setMain";
                case 3:
                    return "setSurface";
                case 4:
                    return "dispatchSurfaceChanged";
                case 5:
                    return "setVolume";
                case 6:
                    return TvInteractiveAppService.PLAYBACK_COMMAND_TYPE_TUNE;
                case 7:
                    return "setCaptionEnabled";
                case 8:
                    return "selectAudioPresentation";
                case 9:
                    return "selectTrack";
                case 10:
                    return "setInteractiveAppNotificationEnabled";
                case 11:
                    return "appPrivateCommand";
                case 12:
                    return "createOverlayView";
                case 13:
                    return "relayoutOverlayView";
                case 14:
                    return "removeOverlayView";
                case 15:
                    return "unblockContent";
                case 16:
                    return "timeShiftPlay";
                case 17:
                    return "timeShiftPause";
                case 18:
                    return "timeShiftResume";
                case 19:
                    return "timeShiftSeekTo";
                case 20:
                    return "timeShiftSetPlaybackParams";
                case 21:
                    return "timeShiftSetMode";
                case 22:
                    return "timeShiftEnablePositionTracking";
                case 23:
                    return "startRecording";
                case 24:
                    return "stopRecording";
                case 25:
                    return "pauseRecording";
                case 26:
                    return "resumeRecording";
                case 27:
                    return "requestBroadcastInfo";
                case 28:
                    return "removeBroadcastInfo";
                case 29:
                    return "requestAd";
                case 30:
                    return "notifyAdBuffer";
                case 31:
                    return "notifyTvMessage";
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
                            release();
                            break;
                        case 2:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMain(_arg0);
                            break;
                        case 3:
                            Surface _arg02 = (Surface) data.readTypedObject(Surface.CREATOR);
                            data.enforceNoDataAvail();
                            setSurface(_arg02);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchSurfaceChanged(_arg03, _arg1, _arg2);
                            break;
                        case 5:
                            float _arg04 = data.readFloat();
                            data.enforceNoDataAvail();
                            setVolume(_arg04);
                            break;
                        case 6:
                            Uri _arg05 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg12 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            tune(_arg05, _arg12);
                            break;
                        case 7:
                            boolean _arg06 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setCaptionEnabled(_arg06);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            selectAudioPresentation(_arg07, _arg13);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            selectTrack(_arg08, _arg14);
                            break;
                        case 10:
                            boolean _arg09 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setInteractiveAppNotificationEnabled(_arg09);
                            break;
                        case 11:
                            String _arg010 = data.readString();
                            Bundle _arg15 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            appPrivateCommand(_arg010, _arg15);
                            break;
                        case 12:
                            IBinder _arg011 = data.readStrongBinder();
                            Rect _arg16 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            createOverlayView(_arg011, _arg16);
                            break;
                        case 13:
                            Rect _arg012 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            relayoutOverlayView(_arg012);
                            break;
                        case 14:
                            removeOverlayView();
                            break;
                        case 15:
                            String _arg013 = data.readString();
                            data.enforceNoDataAvail();
                            unblockContent(_arg013);
                            break;
                        case 16:
                            Uri _arg014 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            timeShiftPlay(_arg014);
                            break;
                        case 17:
                            timeShiftPause();
                            break;
                        case 18:
                            timeShiftResume();
                            break;
                        case 19:
                            long _arg015 = data.readLong();
                            data.enforceNoDataAvail();
                            timeShiftSeekTo(_arg015);
                            break;
                        case 20:
                            PlaybackParams _arg016 = (PlaybackParams) data.readTypedObject(PlaybackParams.CREATOR);
                            data.enforceNoDataAvail();
                            timeShiftSetPlaybackParams(_arg016);
                            break;
                        case 21:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftSetMode(_arg017);
                            break;
                        case 22:
                            boolean _arg018 = data.readBoolean();
                            data.enforceNoDataAvail();
                            timeShiftEnablePositionTracking(_arg018);
                            break;
                        case 23:
                            Uri _arg019 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg17 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startRecording(_arg019, _arg17);
                            break;
                        case 24:
                            stopRecording();
                            break;
                        case 25:
                            Bundle _arg020 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            pauseRecording(_arg020);
                            break;
                        case 26:
                            Bundle _arg021 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            resumeRecording(_arg021);
                            break;
                        case 27:
                            BroadcastInfoRequest _arg022 = (BroadcastInfoRequest) data.readTypedObject(BroadcastInfoRequest.CREATOR);
                            data.enforceNoDataAvail();
                            requestBroadcastInfo(_arg022);
                            break;
                        case 28:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            removeBroadcastInfo(_arg023);
                            break;
                        case 29:
                            AdRequest _arg024 = (AdRequest) data.readTypedObject(AdRequest.CREATOR);
                            data.enforceNoDataAvail();
                            requestAd(_arg024);
                            break;
                        case 30:
                            AdBuffer _arg025 = (AdBuffer) data.readTypedObject(AdBuffer.CREATOR);
                            data.enforceNoDataAvail();
                            notifyAdBuffer(_arg025);
                            break;
                        case 31:
                            String _arg026 = data.readString();
                            Bundle _arg18 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyTvMessage(_arg026, _arg18);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.ITvInputSession$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInputSession {
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

            @Override // android.media.p007tv.ITvInputSession
            public void release() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void setMain(boolean isMain) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isMain);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void setSurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void dispatchSurfaceChanged(int format, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(format);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void setVolume(float volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(volume);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void tune(Uri channelUri, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(channelUri, 0);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void setCaptionEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void selectAudioPresentation(int presentationId, int programId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presentationId);
                    _data.writeInt(programId);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void selectTrack(int type, String trackId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(trackId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void setInteractiveAppNotificationEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void appPrivateCommand(String action, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void createOverlayView(IBinder windowToken, Rect frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    _data.writeTypedObject(frame, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void relayoutOverlayView(Rect frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(frame, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void removeOverlayView() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void unblockContent(String unblockedRating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(unblockedRating);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftPlay(Uri recordedProgramUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recordedProgramUri, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftPause() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftResume() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftSeekTo(long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timeMs);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftSetPlaybackParams(PlaybackParams params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftSetMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void timeShiftEnablePositionTracking(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void startRecording(Uri programUri, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(programUri, 0);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void stopRecording() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void pauseRecording(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void resumeRecording(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void requestBroadcastInfo(BroadcastInfoRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void removeBroadcastInfo(int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void requestAd(AdRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void notifyAdBuffer(AdBuffer buffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(buffer, 0);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputSession
            public void notifyTvMessage(String type, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 30;
        }
    }
}
