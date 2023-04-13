package android.media.p007tv.interactive;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.p007tv.AdBuffer;
import android.media.p007tv.AdResponse;
import android.media.p007tv.BroadcastInfoResponse;
import android.media.p007tv.TvRecordingInfo;
import android.media.p007tv.TvTrackInfo;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.Surface;
import java.util.List;
/* renamed from: android.media.tv.interactive.ITvInteractiveAppSession */
/* loaded from: classes2.dex */
public interface ITvInteractiveAppSession extends IInterface {
    public static final String DESCRIPTOR = "android.media.tv.interactive.ITvInteractiveAppSession";

    void createBiInteractiveApp(Uri uri, Bundle bundle) throws RemoteException;

    void createMediaView(IBinder iBinder, Rect rect) throws RemoteException;

    void destroyBiInteractiveApp(String str) throws RemoteException;

    void dispatchSurfaceChanged(int i, int i2, int i3) throws RemoteException;

    void notifyAdBufferConsumed(AdBuffer adBuffer) throws RemoteException;

    void notifyAdResponse(AdResponse adResponse) throws RemoteException;

    void notifyBroadcastInfoResponse(BroadcastInfoResponse broadcastInfoResponse) throws RemoteException;

    void notifyContentAllowed() throws RemoteException;

    void notifyContentBlocked(String str) throws RemoteException;

    void notifyError(String str, Bundle bundle) throws RemoteException;

    void notifyRecordingConnectionFailed(String str, String str2) throws RemoteException;

    void notifyRecordingDisconnected(String str, String str2) throws RemoteException;

    void notifyRecordingError(String str, int i) throws RemoteException;

    void notifyRecordingScheduled(String str, String str2) throws RemoteException;

    void notifyRecordingStarted(String str, String str2) throws RemoteException;

    void notifyRecordingStopped(String str) throws RemoteException;

    void notifyRecordingTuned(String str, Uri uri) throws RemoteException;

    void notifySignalStrength(int i) throws RemoteException;

    void notifyTimeShiftCurrentPositionChanged(String str, long j) throws RemoteException;

    void notifyTimeShiftPlaybackParams(PlaybackParams playbackParams) throws RemoteException;

    void notifyTimeShiftStartPositionChanged(String str, long j) throws RemoteException;

    void notifyTimeShiftStatusChanged(String str, int i) throws RemoteException;

    void notifyTrackSelected(int i, String str) throws RemoteException;

    void notifyTracksChanged(List<TvTrackInfo> list) throws RemoteException;

    void notifyTuned(Uri uri) throws RemoteException;

    void notifyTvMessage(String str, Bundle bundle) throws RemoteException;

    void notifyVideoAvailable() throws RemoteException;

    void notifyVideoUnavailable(int i) throws RemoteException;

    void relayoutMediaView(Rect rect) throws RemoteException;

    void release() throws RemoteException;

    void removeMediaView() throws RemoteException;

    void resetInteractiveApp() throws RemoteException;

    void sendAvailableSpeeds(float[] fArr) throws RemoteException;

    void sendCurrentChannelLcn(int i) throws RemoteException;

    void sendCurrentChannelUri(Uri uri) throws RemoteException;

    void sendCurrentTvInputId(String str) throws RemoteException;

    void sendCurrentVideoBounds(Rect rect) throws RemoteException;

    void sendSigningResult(String str, byte[] bArr) throws RemoteException;

    void sendStreamVolume(float f) throws RemoteException;

    void sendTimeShiftMode(int i) throws RemoteException;

    void sendTrackInfoList(List<TvTrackInfo> list) throws RemoteException;

    void sendTvRecordingInfo(TvRecordingInfo tvRecordingInfo) throws RemoteException;

    void sendTvRecordingInfoList(List<TvRecordingInfo> list) throws RemoteException;

    void setSurface(Surface surface) throws RemoteException;

    void setTeletextAppEnabled(boolean z) throws RemoteException;

    void startInteractiveApp() throws RemoteException;

    void stopInteractiveApp() throws RemoteException;

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppSession$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInteractiveAppSession {
        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void startInteractiveApp() throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void stopInteractiveApp() throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void resetInteractiveApp() throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void createBiInteractiveApp(Uri biIAppUri, Bundle params) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void destroyBiInteractiveApp(String biIAppId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void setTeletextAppEnabled(boolean enable) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendCurrentVideoBounds(Rect bounds) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendCurrentChannelUri(Uri channelUri) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendCurrentChannelLcn(int lcn) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendStreamVolume(float volume) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendTrackInfoList(List<TvTrackInfo> tracks) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendCurrentTvInputId(String inputId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendTimeShiftMode(int mode) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendAvailableSpeeds(float[] speeds) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendSigningResult(String signingId, byte[] result) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendTvRecordingInfo(TvRecordingInfo recordingInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void sendTvRecordingInfoList(List<TvRecordingInfo> recordingInfoList) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyError(String errMsg, Bundle params) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTimeShiftPlaybackParams(PlaybackParams params) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTimeShiftStatusChanged(String inputId, int status) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTimeShiftStartPositionChanged(String inputId, long timeMs) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTimeShiftCurrentPositionChanged(String inputId, long timeMs) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingConnectionFailed(String recordingId, String inputId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingDisconnected(String recordingId, String inputId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingTuned(String recordingId, Uri channelUri) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingError(String recordingId, int err) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingScheduled(String recordingId, String requestId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void release() throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTuned(Uri channelUri) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTrackSelected(int type, String trackId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTracksChanged(List<TvTrackInfo> tracks) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyVideoAvailable() throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyVideoUnavailable(int reason) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyContentAllowed() throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyContentBlocked(String rating) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifySignalStrength(int strength) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingStarted(String recordingId, String requestId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyRecordingStopped(String recordingId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyTvMessage(String type, Bundle data) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void setSurface(Surface surface) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void dispatchSurfaceChanged(int format, int width, int height) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyBroadcastInfoResponse(BroadcastInfoResponse response) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyAdResponse(AdResponse response) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void notifyAdBufferConsumed(AdBuffer buffer) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void createMediaView(IBinder windowToken, Rect frame) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void relayoutMediaView(Rect frame) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
        public void removeMediaView() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppSession$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInteractiveAppSession {
        static final int TRANSACTION_createBiInteractiveApp = 4;
        static final int TRANSACTION_createMediaView = 45;
        static final int TRANSACTION_destroyBiInteractiveApp = 5;
        static final int TRANSACTION_dispatchSurfaceChanged = 41;
        static final int TRANSACTION_notifyAdBufferConsumed = 44;
        static final int TRANSACTION_notifyAdResponse = 43;
        static final int TRANSACTION_notifyBroadcastInfoResponse = 42;
        static final int TRANSACTION_notifyContentAllowed = 34;
        static final int TRANSACTION_notifyContentBlocked = 35;
        static final int TRANSACTION_notifyError = 18;
        static final int TRANSACTION_notifyRecordingConnectionFailed = 23;
        static final int TRANSACTION_notifyRecordingDisconnected = 24;
        static final int TRANSACTION_notifyRecordingError = 26;
        static final int TRANSACTION_notifyRecordingScheduled = 27;
        static final int TRANSACTION_notifyRecordingStarted = 37;
        static final int TRANSACTION_notifyRecordingStopped = 38;
        static final int TRANSACTION_notifyRecordingTuned = 25;
        static final int TRANSACTION_notifySignalStrength = 36;
        static final int TRANSACTION_notifyTimeShiftCurrentPositionChanged = 22;
        static final int TRANSACTION_notifyTimeShiftPlaybackParams = 19;
        static final int TRANSACTION_notifyTimeShiftStartPositionChanged = 21;
        static final int TRANSACTION_notifyTimeShiftStatusChanged = 20;
        static final int TRANSACTION_notifyTrackSelected = 30;
        static final int TRANSACTION_notifyTracksChanged = 31;
        static final int TRANSACTION_notifyTuned = 29;
        static final int TRANSACTION_notifyTvMessage = 39;
        static final int TRANSACTION_notifyVideoAvailable = 32;
        static final int TRANSACTION_notifyVideoUnavailable = 33;
        static final int TRANSACTION_relayoutMediaView = 46;
        static final int TRANSACTION_release = 28;
        static final int TRANSACTION_removeMediaView = 47;
        static final int TRANSACTION_resetInteractiveApp = 3;
        static final int TRANSACTION_sendAvailableSpeeds = 14;
        static final int TRANSACTION_sendCurrentChannelLcn = 9;
        static final int TRANSACTION_sendCurrentChannelUri = 8;
        static final int TRANSACTION_sendCurrentTvInputId = 12;
        static final int TRANSACTION_sendCurrentVideoBounds = 7;
        static final int TRANSACTION_sendSigningResult = 15;
        static final int TRANSACTION_sendStreamVolume = 10;
        static final int TRANSACTION_sendTimeShiftMode = 13;
        static final int TRANSACTION_sendTrackInfoList = 11;
        static final int TRANSACTION_sendTvRecordingInfo = 16;
        static final int TRANSACTION_sendTvRecordingInfoList = 17;
        static final int TRANSACTION_setSurface = 40;
        static final int TRANSACTION_setTeletextAppEnabled = 6;
        static final int TRANSACTION_startInteractiveApp = 1;
        static final int TRANSACTION_stopInteractiveApp = 2;

        public Stub() {
            attachInterface(this, ITvInteractiveAppSession.DESCRIPTOR);
        }

        public static ITvInteractiveAppSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITvInteractiveAppSession.DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInteractiveAppSession)) {
                return (ITvInteractiveAppSession) iin;
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
                    return "startInteractiveApp";
                case 2:
                    return "stopInteractiveApp";
                case 3:
                    return "resetInteractiveApp";
                case 4:
                    return "createBiInteractiveApp";
                case 5:
                    return "destroyBiInteractiveApp";
                case 6:
                    return "setTeletextAppEnabled";
                case 7:
                    return "sendCurrentVideoBounds";
                case 8:
                    return "sendCurrentChannelUri";
                case 9:
                    return "sendCurrentChannelLcn";
                case 10:
                    return "sendStreamVolume";
                case 11:
                    return "sendTrackInfoList";
                case 12:
                    return "sendCurrentTvInputId";
                case 13:
                    return "sendTimeShiftMode";
                case 14:
                    return "sendAvailableSpeeds";
                case 15:
                    return "sendSigningResult";
                case 16:
                    return "sendTvRecordingInfo";
                case 17:
                    return "sendTvRecordingInfoList";
                case 18:
                    return "notifyError";
                case 19:
                    return "notifyTimeShiftPlaybackParams";
                case 20:
                    return "notifyTimeShiftStatusChanged";
                case 21:
                    return "notifyTimeShiftStartPositionChanged";
                case 22:
                    return "notifyTimeShiftCurrentPositionChanged";
                case 23:
                    return "notifyRecordingConnectionFailed";
                case 24:
                    return "notifyRecordingDisconnected";
                case 25:
                    return "notifyRecordingTuned";
                case 26:
                    return "notifyRecordingError";
                case 27:
                    return "notifyRecordingScheduled";
                case 28:
                    return "release";
                case 29:
                    return "notifyTuned";
                case 30:
                    return "notifyTrackSelected";
                case 31:
                    return "notifyTracksChanged";
                case 32:
                    return "notifyVideoAvailable";
                case 33:
                    return "notifyVideoUnavailable";
                case 34:
                    return "notifyContentAllowed";
                case 35:
                    return "notifyContentBlocked";
                case 36:
                    return "notifySignalStrength";
                case 37:
                    return "notifyRecordingStarted";
                case 38:
                    return "notifyRecordingStopped";
                case 39:
                    return "notifyTvMessage";
                case 40:
                    return "setSurface";
                case 41:
                    return "dispatchSurfaceChanged";
                case 42:
                    return "notifyBroadcastInfoResponse";
                case 43:
                    return "notifyAdResponse";
                case 44:
                    return "notifyAdBufferConsumed";
                case 45:
                    return "createMediaView";
                case 46:
                    return "relayoutMediaView";
                case 47:
                    return "removeMediaView";
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
                data.enforceInterface(ITvInteractiveAppSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITvInteractiveAppSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            startInteractiveApp();
                            break;
                        case 2:
                            stopInteractiveApp();
                            break;
                        case 3:
                            resetInteractiveApp();
                            break;
                        case 4:
                            Uri _arg0 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            createBiInteractiveApp(_arg0, _arg1);
                            break;
                        case 5:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            destroyBiInteractiveApp(_arg02);
                            break;
                        case 6:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setTeletextAppEnabled(_arg03);
                            break;
                        case 7:
                            Rect _arg04 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            sendCurrentVideoBounds(_arg04);
                            break;
                        case 8:
                            Uri _arg05 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            sendCurrentChannelUri(_arg05);
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            sendCurrentChannelLcn(_arg06);
                            break;
                        case 10:
                            float _arg07 = data.readFloat();
                            data.enforceNoDataAvail();
                            sendStreamVolume(_arg07);
                            break;
                        case 11:
                            List<TvTrackInfo> _arg08 = data.createTypedArrayList(TvTrackInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendTrackInfoList(_arg08);
                            break;
                        case 12:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            sendCurrentTvInputId(_arg09);
                            break;
                        case 13:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTimeShiftMode(_arg010);
                            break;
                        case 14:
                            float[] _arg011 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            sendAvailableSpeeds(_arg011);
                            break;
                        case 15:
                            String _arg012 = data.readString();
                            byte[] _arg12 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendSigningResult(_arg012, _arg12);
                            break;
                        case 16:
                            TvRecordingInfo _arg013 = (TvRecordingInfo) data.readTypedObject(TvRecordingInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendTvRecordingInfo(_arg013);
                            break;
                        case 17:
                            List<TvRecordingInfo> _arg014 = data.createTypedArrayList(TvRecordingInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendTvRecordingInfoList(_arg014);
                            break;
                        case 18:
                            String _arg015 = data.readString();
                            Bundle _arg13 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyError(_arg015, _arg13);
                            break;
                        case 19:
                            PlaybackParams _arg016 = (PlaybackParams) data.readTypedObject(PlaybackParams.CREATOR);
                            data.enforceNoDataAvail();
                            notifyTimeShiftPlaybackParams(_arg016);
                            break;
                        case 20:
                            String _arg017 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTimeShiftStatusChanged(_arg017, _arg14);
                            break;
                        case 21:
                            String _arg018 = data.readString();
                            long _arg15 = data.readLong();
                            data.enforceNoDataAvail();
                            notifyTimeShiftStartPositionChanged(_arg018, _arg15);
                            break;
                        case 22:
                            String _arg019 = data.readString();
                            long _arg16 = data.readLong();
                            data.enforceNoDataAvail();
                            notifyTimeShiftCurrentPositionChanged(_arg019, _arg16);
                            break;
                        case 23:
                            String _arg020 = data.readString();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            notifyRecordingConnectionFailed(_arg020, _arg17);
                            break;
                        case 24:
                            String _arg021 = data.readString();
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            notifyRecordingDisconnected(_arg021, _arg18);
                            break;
                        case 25:
                            String _arg022 = data.readString();
                            Uri _arg19 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            notifyRecordingTuned(_arg022, _arg19);
                            break;
                        case 26:
                            String _arg023 = data.readString();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingError(_arg023, _arg110);
                            break;
                        case 27:
                            String _arg024 = data.readString();
                            String _arg111 = data.readString();
                            data.enforceNoDataAvail();
                            notifyRecordingScheduled(_arg024, _arg111);
                            break;
                        case 28:
                            release();
                            break;
                        case 29:
                            Uri _arg025 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            notifyTuned(_arg025);
                            break;
                        case 30:
                            int _arg026 = data.readInt();
                            String _arg112 = data.readString();
                            data.enforceNoDataAvail();
                            notifyTrackSelected(_arg026, _arg112);
                            break;
                        case 31:
                            List<TvTrackInfo> _arg027 = data.createTypedArrayList(TvTrackInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyTracksChanged(_arg027);
                            break;
                        case 32:
                            notifyVideoAvailable();
                            break;
                        case 33:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyVideoUnavailable(_arg028);
                            break;
                        case 34:
                            notifyContentAllowed();
                            break;
                        case 35:
                            String _arg029 = data.readString();
                            data.enforceNoDataAvail();
                            notifyContentBlocked(_arg029);
                            break;
                        case 36:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            notifySignalStrength(_arg030);
                            break;
                        case 37:
                            String _arg031 = data.readString();
                            String _arg113 = data.readString();
                            data.enforceNoDataAvail();
                            notifyRecordingStarted(_arg031, _arg113);
                            break;
                        case 38:
                            String _arg032 = data.readString();
                            data.enforceNoDataAvail();
                            notifyRecordingStopped(_arg032);
                            break;
                        case 39:
                            String _arg033 = data.readString();
                            Bundle _arg114 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyTvMessage(_arg033, _arg114);
                            break;
                        case 40:
                            Surface _arg034 = (Surface) data.readTypedObject(Surface.CREATOR);
                            data.enforceNoDataAvail();
                            setSurface(_arg034);
                            break;
                        case 41:
                            int _arg035 = data.readInt();
                            int _arg115 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchSurfaceChanged(_arg035, _arg115, _arg2);
                            break;
                        case 42:
                            BroadcastInfoResponse _arg036 = (BroadcastInfoResponse) data.readTypedObject(BroadcastInfoResponse.CREATOR);
                            data.enforceNoDataAvail();
                            notifyBroadcastInfoResponse(_arg036);
                            break;
                        case 43:
                            AdResponse _arg037 = (AdResponse) data.readTypedObject(AdResponse.CREATOR);
                            data.enforceNoDataAvail();
                            notifyAdResponse(_arg037);
                            break;
                        case 44:
                            AdBuffer _arg038 = (AdBuffer) data.readTypedObject(AdBuffer.CREATOR);
                            data.enforceNoDataAvail();
                            notifyAdBufferConsumed(_arg038);
                            break;
                        case 45:
                            IBinder _arg039 = data.readStrongBinder();
                            Rect _arg116 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            createMediaView(_arg039, _arg116);
                            break;
                        case 46:
                            Rect _arg040 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            relayoutMediaView(_arg040);
                            break;
                        case 47:
                            removeMediaView();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.interactive.ITvInteractiveAppSession$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInteractiveAppSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITvInteractiveAppSession.DESCRIPTOR;
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void startInteractiveApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void stopInteractiveApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void resetInteractiveApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void createBiInteractiveApp(Uri biIAppUri, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(biIAppUri, 0);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void destroyBiInteractiveApp(String biIAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(biIAppId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void setTeletextAppEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendCurrentVideoBounds(Rect bounds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(bounds, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendCurrentChannelUri(Uri channelUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(channelUri, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendCurrentChannelLcn(int lcn) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeInt(lcn);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendStreamVolume(float volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeFloat(volume);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendTrackInfoList(List<TvTrackInfo> tracks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedList(tracks, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendCurrentTvInputId(String inputId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(inputId);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendTimeShiftMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendAvailableSpeeds(float[] speeds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeFloatArray(speeds);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendSigningResult(String signingId, byte[] result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(signingId);
                    _data.writeByteArray(result);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendTvRecordingInfo(TvRecordingInfo recordingInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(recordingInfo, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void sendTvRecordingInfoList(List<TvRecordingInfo> recordingInfoList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedList(recordingInfoList, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyError(String errMsg, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(errMsg);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTimeShiftPlaybackParams(PlaybackParams params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTimeShiftStatusChanged(String inputId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeInt(status);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTimeShiftStartPositionChanged(String inputId, long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeLong(timeMs);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTimeShiftCurrentPositionChanged(String inputId, long timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeLong(timeMs);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingConnectionFailed(String recordingId, String inputId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    _data.writeString(inputId);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingDisconnected(String recordingId, String inputId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    _data.writeString(inputId);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingTuned(String recordingId, Uri channelUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    _data.writeTypedObject(channelUri, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingError(String recordingId, int err) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    _data.writeInt(err);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingScheduled(String recordingId, String requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    _data.writeString(requestId);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void release() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTuned(Uri channelUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(channelUri, 0);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTrackSelected(int type, String trackId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(trackId);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTracksChanged(List<TvTrackInfo> tracks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedList(tracks, 0);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyVideoAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyVideoUnavailable(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyContentAllowed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(34, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyContentBlocked(String rating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(rating);
                    this.mRemote.transact(35, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifySignalStrength(int strength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeInt(strength);
                    this.mRemote.transact(36, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingStarted(String recordingId, String requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    _data.writeString(requestId);
                    this.mRemote.transact(37, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyRecordingStopped(String recordingId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(recordingId);
                    this.mRemote.transact(38, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyTvMessage(String type, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(39, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void setSurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    this.mRemote.transact(40, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void dispatchSurfaceChanged(int format, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeInt(format);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(41, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyBroadcastInfoResponse(BroadcastInfoResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(42, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyAdResponse(AdResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(43, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void notifyAdBufferConsumed(AdBuffer buffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(buffer, 0);
                    this.mRemote.transact(44, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void createMediaView(IBinder windowToken, Rect frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    _data.writeTypedObject(frame, 0);
                    this.mRemote.transact(45, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void relayoutMediaView(Rect frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    _data.writeTypedObject(frame, 0);
                    this.mRemote.transact(46, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppSession
            public void removeMediaView() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppSession.DESCRIPTOR);
                    this.mRemote.transact(47, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 46;
        }
    }
}
