package android.media.p007tv.interactive;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.p007tv.AdBuffer;
import android.media.p007tv.AdResponse;
import android.media.p007tv.BroadcastInfoResponse;
import android.media.p007tv.TvRecordingInfo;
import android.media.p007tv.TvTrackInfo;
import android.media.p007tv.interactive.ITvInteractiveAppClient;
import android.media.p007tv.interactive.ITvInteractiveAppManagerCallback;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.Surface;
import java.util.List;
/* renamed from: android.media.tv.interactive.ITvInteractiveAppManager */
/* loaded from: classes2.dex */
public interface ITvInteractiveAppManager extends IInterface {
    public static final String DESCRIPTOR = "android.media.tv.interactive.ITvInteractiveAppManager";

    void createBiInteractiveApp(IBinder iBinder, Uri uri, Bundle bundle, int i) throws RemoteException;

    void createMediaView(IBinder iBinder, IBinder iBinder2, Rect rect, int i) throws RemoteException;

    void createSession(ITvInteractiveAppClient iTvInteractiveAppClient, String str, int i, int i2, int i3) throws RemoteException;

    void destroyBiInteractiveApp(IBinder iBinder, String str, int i) throws RemoteException;

    void dispatchSurfaceChanged(IBinder iBinder, int i, int i2, int i3, int i4) throws RemoteException;

    List<AppLinkInfo> getAppLinkInfoList(int i) throws RemoteException;

    List<TvInteractiveAppServiceInfo> getTvInteractiveAppServiceList(int i) throws RemoteException;

    void notifyAdBufferConsumed(IBinder iBinder, AdBuffer adBuffer, int i) throws RemoteException;

    void notifyAdResponse(IBinder iBinder, AdResponse adResponse, int i) throws RemoteException;

    void notifyBroadcastInfoResponse(IBinder iBinder, BroadcastInfoResponse broadcastInfoResponse, int i) throws RemoteException;

    void notifyContentAllowed(IBinder iBinder, int i) throws RemoteException;

    void notifyContentBlocked(IBinder iBinder, String str, int i) throws RemoteException;

    void notifyError(IBinder iBinder, String str, Bundle bundle, int i) throws RemoteException;

    void notifyRecordingConnectionFailed(IBinder iBinder, String str, String str2, int i) throws RemoteException;

    void notifyRecordingDisconnected(IBinder iBinder, String str, String str2, int i) throws RemoteException;

    void notifyRecordingError(IBinder iBinder, String str, int i, int i2) throws RemoteException;

    void notifyRecordingScheduled(IBinder iBinder, String str, String str2, int i) throws RemoteException;

    void notifyRecordingStarted(IBinder iBinder, String str, String str2, int i) throws RemoteException;

    void notifyRecordingStopped(IBinder iBinder, String str, int i) throws RemoteException;

    void notifyRecordingTuned(IBinder iBinder, String str, Uri uri, int i) throws RemoteException;

    void notifySignalStrength(IBinder iBinder, int i, int i2) throws RemoteException;

    void notifyTimeShiftCurrentPositionChanged(IBinder iBinder, String str, long j, int i) throws RemoteException;

    void notifyTimeShiftPlaybackParams(IBinder iBinder, PlaybackParams playbackParams, int i) throws RemoteException;

    void notifyTimeShiftStartPositionChanged(IBinder iBinder, String str, long j, int i) throws RemoteException;

    void notifyTimeShiftStatusChanged(IBinder iBinder, String str, int i, int i2) throws RemoteException;

    void notifyTrackSelected(IBinder iBinder, int i, String str, int i2) throws RemoteException;

    void notifyTracksChanged(IBinder iBinder, List<TvTrackInfo> list, int i) throws RemoteException;

    void notifyTuned(IBinder iBinder, Uri uri, int i) throws RemoteException;

    void notifyTvMessage(IBinder iBinder, String str, Bundle bundle, int i) throws RemoteException;

    void notifyVideoAvailable(IBinder iBinder, int i) throws RemoteException;

    void notifyVideoUnavailable(IBinder iBinder, int i, int i2) throws RemoteException;

    void registerAppLinkInfo(String str, AppLinkInfo appLinkInfo, int i) throws RemoteException;

    void registerCallback(ITvInteractiveAppManagerCallback iTvInteractiveAppManagerCallback, int i) throws RemoteException;

    void relayoutMediaView(IBinder iBinder, Rect rect, int i) throws RemoteException;

    void releaseSession(IBinder iBinder, int i) throws RemoteException;

    void removeMediaView(IBinder iBinder, int i) throws RemoteException;

    void resetInteractiveApp(IBinder iBinder, int i) throws RemoteException;

    void sendAppLinkCommand(String str, Bundle bundle, int i) throws RemoteException;

    void sendAvailableSpeeds(IBinder iBinder, float[] fArr, int i) throws RemoteException;

    void sendCurrentChannelLcn(IBinder iBinder, int i, int i2) throws RemoteException;

    void sendCurrentChannelUri(IBinder iBinder, Uri uri, int i) throws RemoteException;

    void sendCurrentTvInputId(IBinder iBinder, String str, int i) throws RemoteException;

    void sendCurrentVideoBounds(IBinder iBinder, Rect rect, int i) throws RemoteException;

    void sendSigningResult(IBinder iBinder, String str, byte[] bArr, int i) throws RemoteException;

    void sendStreamVolume(IBinder iBinder, float f, int i) throws RemoteException;

    void sendTimeShiftMode(IBinder iBinder, int i, int i2) throws RemoteException;

    void sendTrackInfoList(IBinder iBinder, List<TvTrackInfo> list, int i) throws RemoteException;

    void sendTvRecordingInfo(IBinder iBinder, TvRecordingInfo tvRecordingInfo, int i) throws RemoteException;

    void sendTvRecordingInfoList(IBinder iBinder, List<TvRecordingInfo> list, int i) throws RemoteException;

    void setSurface(IBinder iBinder, Surface surface, int i) throws RemoteException;

    void setTeletextAppEnabled(IBinder iBinder, boolean z, int i) throws RemoteException;

    void startInteractiveApp(IBinder iBinder, int i) throws RemoteException;

    void stopInteractiveApp(IBinder iBinder, int i) throws RemoteException;

    void unregisterAppLinkInfo(String str, AppLinkInfo appLinkInfo, int i) throws RemoteException;

    void unregisterCallback(ITvInteractiveAppManagerCallback iTvInteractiveAppManagerCallback, int i) throws RemoteException;

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppManager$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInteractiveAppManager {
        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public List<TvInteractiveAppServiceInfo> getTvInteractiveAppServiceList(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public List<AppLinkInfo> getAppLinkInfoList(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void registerAppLinkInfo(String tiasId, AppLinkInfo info, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void unregisterAppLinkInfo(String tiasId, AppLinkInfo info, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendAppLinkCommand(String tiasId, Bundle command, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void startInteractiveApp(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void stopInteractiveApp(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void resetInteractiveApp(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void createBiInteractiveApp(IBinder sessionToken, Uri biIAppUri, Bundle params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void destroyBiInteractiveApp(IBinder sessionToken, String biIAppId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void setTeletextAppEnabled(IBinder sessionToken, boolean enable, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendCurrentVideoBounds(IBinder sessionToken, Rect bounds, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendCurrentChannelUri(IBinder sessionToken, Uri channelUri, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendCurrentChannelLcn(IBinder sessionToken, int lcn, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendStreamVolume(IBinder sessionToken, float volume, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendTrackInfoList(IBinder sessionToken, List<TvTrackInfo> tracks, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendCurrentTvInputId(IBinder sessionToken, String inputId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendTimeShiftMode(IBinder sessionToken, int mode, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendAvailableSpeeds(IBinder sessionToken, float[] speeds, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendSigningResult(IBinder sessionToken, String signingId, byte[] result, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendTvRecordingInfo(IBinder sessionToken, TvRecordingInfo recordingInfo, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void sendTvRecordingInfoList(IBinder sessionToken, List<TvRecordingInfo> recordingInfoList, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyError(IBinder sessionToken, String errMsg, Bundle params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTimeShiftPlaybackParams(IBinder sessionToken, PlaybackParams params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTimeShiftStatusChanged(IBinder sessionToken, String inputId, int status, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTimeShiftStartPositionChanged(IBinder sessionToken, String inputId, long timeMs, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTimeShiftCurrentPositionChanged(IBinder sessionToken, String inputId, long timeMs, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingConnectionFailed(IBinder sessionToken, String recordingId, String inputId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingDisconnected(IBinder sessionToken, String recordingId, String inputId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingTuned(IBinder sessionToken, String recordingId, Uri channelUri, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingError(IBinder sessionToken, String recordingId, int err, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingScheduled(IBinder sessionToken, String recordingId, String requestId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void createSession(ITvInteractiveAppClient client, String iAppServiceId, int type, int seq, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void releaseSession(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTuned(IBinder sessionToken, Uri channelUri, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTrackSelected(IBinder sessionToken, int type, String trackId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTracksChanged(IBinder sessionToken, List<TvTrackInfo> tracks, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyVideoAvailable(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyVideoUnavailable(IBinder sessionToken, int reason, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyContentAllowed(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyContentBlocked(IBinder sessionToken, String rating, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifySignalStrength(IBinder sessionToken, int stength, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingStarted(IBinder sessionToken, String recordingId, String requestId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyRecordingStopped(IBinder sessionToken, String recordingId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyTvMessage(IBinder sessionToken, String type, Bundle data, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void setSurface(IBinder sessionToken, Surface surface, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void dispatchSurfaceChanged(IBinder sessionToken, int format, int width, int height, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyBroadcastInfoResponse(IBinder sessionToken, BroadcastInfoResponse response, int UserId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyAdResponse(IBinder sessionToken, AdResponse response, int UserId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void notifyAdBufferConsumed(IBinder sessionToken, AdBuffer buffer, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void createMediaView(IBinder sessionToken, IBinder windowToken, Rect frame, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void relayoutMediaView(IBinder sessionToken, Rect frame, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void removeMediaView(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void registerCallback(ITvInteractiveAppManagerCallback callback, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
        public void unregisterCallback(ITvInteractiveAppManagerCallback callback, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppManager$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInteractiveAppManager {
        static final int TRANSACTION_createBiInteractiveApp = 9;
        static final int TRANSACTION_createMediaView = 51;
        static final int TRANSACTION_createSession = 33;
        static final int TRANSACTION_destroyBiInteractiveApp = 10;
        static final int TRANSACTION_dispatchSurfaceChanged = 47;
        static final int TRANSACTION_getAppLinkInfoList = 2;
        static final int TRANSACTION_getTvInteractiveAppServiceList = 1;
        static final int TRANSACTION_notifyAdBufferConsumed = 50;
        static final int TRANSACTION_notifyAdResponse = 49;
        static final int TRANSACTION_notifyBroadcastInfoResponse = 48;
        static final int TRANSACTION_notifyContentAllowed = 40;
        static final int TRANSACTION_notifyContentBlocked = 41;
        static final int TRANSACTION_notifyError = 23;
        static final int TRANSACTION_notifyRecordingConnectionFailed = 28;
        static final int TRANSACTION_notifyRecordingDisconnected = 29;
        static final int TRANSACTION_notifyRecordingError = 31;
        static final int TRANSACTION_notifyRecordingScheduled = 32;
        static final int TRANSACTION_notifyRecordingStarted = 43;
        static final int TRANSACTION_notifyRecordingStopped = 44;
        static final int TRANSACTION_notifyRecordingTuned = 30;
        static final int TRANSACTION_notifySignalStrength = 42;
        static final int TRANSACTION_notifyTimeShiftCurrentPositionChanged = 27;
        static final int TRANSACTION_notifyTimeShiftPlaybackParams = 24;
        static final int TRANSACTION_notifyTimeShiftStartPositionChanged = 26;
        static final int TRANSACTION_notifyTimeShiftStatusChanged = 25;
        static final int TRANSACTION_notifyTrackSelected = 36;
        static final int TRANSACTION_notifyTracksChanged = 37;
        static final int TRANSACTION_notifyTuned = 35;
        static final int TRANSACTION_notifyTvMessage = 45;
        static final int TRANSACTION_notifyVideoAvailable = 38;
        static final int TRANSACTION_notifyVideoUnavailable = 39;
        static final int TRANSACTION_registerAppLinkInfo = 3;
        static final int TRANSACTION_registerCallback = 54;
        static final int TRANSACTION_relayoutMediaView = 52;
        static final int TRANSACTION_releaseSession = 34;
        static final int TRANSACTION_removeMediaView = 53;
        static final int TRANSACTION_resetInteractiveApp = 8;
        static final int TRANSACTION_sendAppLinkCommand = 5;
        static final int TRANSACTION_sendAvailableSpeeds = 19;
        static final int TRANSACTION_sendCurrentChannelLcn = 14;
        static final int TRANSACTION_sendCurrentChannelUri = 13;
        static final int TRANSACTION_sendCurrentTvInputId = 17;
        static final int TRANSACTION_sendCurrentVideoBounds = 12;
        static final int TRANSACTION_sendSigningResult = 20;
        static final int TRANSACTION_sendStreamVolume = 15;
        static final int TRANSACTION_sendTimeShiftMode = 18;
        static final int TRANSACTION_sendTrackInfoList = 16;
        static final int TRANSACTION_sendTvRecordingInfo = 21;
        static final int TRANSACTION_sendTvRecordingInfoList = 22;
        static final int TRANSACTION_setSurface = 46;
        static final int TRANSACTION_setTeletextAppEnabled = 11;
        static final int TRANSACTION_startInteractiveApp = 6;
        static final int TRANSACTION_stopInteractiveApp = 7;
        static final int TRANSACTION_unregisterAppLinkInfo = 4;
        static final int TRANSACTION_unregisterCallback = 55;

        public Stub() {
            attachInterface(this, ITvInteractiveAppManager.DESCRIPTOR);
        }

        public static ITvInteractiveAppManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITvInteractiveAppManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInteractiveAppManager)) {
                return (ITvInteractiveAppManager) iin;
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
                    return "getTvInteractiveAppServiceList";
                case 2:
                    return "getAppLinkInfoList";
                case 3:
                    return "registerAppLinkInfo";
                case 4:
                    return "unregisterAppLinkInfo";
                case 5:
                    return "sendAppLinkCommand";
                case 6:
                    return "startInteractiveApp";
                case 7:
                    return "stopInteractiveApp";
                case 8:
                    return "resetInteractiveApp";
                case 9:
                    return "createBiInteractiveApp";
                case 10:
                    return "destroyBiInteractiveApp";
                case 11:
                    return "setTeletextAppEnabled";
                case 12:
                    return "sendCurrentVideoBounds";
                case 13:
                    return "sendCurrentChannelUri";
                case 14:
                    return "sendCurrentChannelLcn";
                case 15:
                    return "sendStreamVolume";
                case 16:
                    return "sendTrackInfoList";
                case 17:
                    return "sendCurrentTvInputId";
                case 18:
                    return "sendTimeShiftMode";
                case 19:
                    return "sendAvailableSpeeds";
                case 20:
                    return "sendSigningResult";
                case 21:
                    return "sendTvRecordingInfo";
                case 22:
                    return "sendTvRecordingInfoList";
                case 23:
                    return "notifyError";
                case 24:
                    return "notifyTimeShiftPlaybackParams";
                case 25:
                    return "notifyTimeShiftStatusChanged";
                case 26:
                    return "notifyTimeShiftStartPositionChanged";
                case 27:
                    return "notifyTimeShiftCurrentPositionChanged";
                case 28:
                    return "notifyRecordingConnectionFailed";
                case 29:
                    return "notifyRecordingDisconnected";
                case 30:
                    return "notifyRecordingTuned";
                case 31:
                    return "notifyRecordingError";
                case 32:
                    return "notifyRecordingScheduled";
                case 33:
                    return "createSession";
                case 34:
                    return "releaseSession";
                case 35:
                    return "notifyTuned";
                case 36:
                    return "notifyTrackSelected";
                case 37:
                    return "notifyTracksChanged";
                case 38:
                    return "notifyVideoAvailable";
                case 39:
                    return "notifyVideoUnavailable";
                case 40:
                    return "notifyContentAllowed";
                case 41:
                    return "notifyContentBlocked";
                case 42:
                    return "notifySignalStrength";
                case 43:
                    return "notifyRecordingStarted";
                case 44:
                    return "notifyRecordingStopped";
                case 45:
                    return "notifyTvMessage";
                case 46:
                    return "setSurface";
                case 47:
                    return "dispatchSurfaceChanged";
                case 48:
                    return "notifyBroadcastInfoResponse";
                case 49:
                    return "notifyAdResponse";
                case 50:
                    return "notifyAdBufferConsumed";
                case 51:
                    return "createMediaView";
                case 52:
                    return "relayoutMediaView";
                case 53:
                    return "removeMediaView";
                case 54:
                    return "registerCallback";
                case 55:
                    return "unregisterCallback";
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
                data.enforceInterface(ITvInteractiveAppManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITvInteractiveAppManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            List<TvInteractiveAppServiceInfo> _result = getTvInteractiveAppServiceList(_arg0);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            List<AppLinkInfo> _result2 = getAppLinkInfoList(_arg02);
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            AppLinkInfo _arg1 = (AppLinkInfo) data.readTypedObject(AppLinkInfo.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            registerAppLinkInfo(_arg03, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            AppLinkInfo _arg12 = (AppLinkInfo) data.readTypedObject(AppLinkInfo.CREATOR);
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterAppLinkInfo(_arg04, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            Bundle _arg13 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            sendAppLinkCommand(_arg05, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 6:
                            IBinder _arg06 = data.readStrongBinder();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            startInteractiveApp(_arg06, _arg14);
                            reply.writeNoException();
                            break;
                        case 7:
                            IBinder _arg07 = data.readStrongBinder();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            stopInteractiveApp(_arg07, _arg15);
                            reply.writeNoException();
                            break;
                        case 8:
                            IBinder _arg08 = data.readStrongBinder();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            resetInteractiveApp(_arg08, _arg16);
                            reply.writeNoException();
                            break;
                        case 9:
                            IBinder _arg09 = data.readStrongBinder();
                            Uri _arg17 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg24 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            createBiInteractiveApp(_arg09, _arg17, _arg24, _arg3);
                            reply.writeNoException();
                            break;
                        case 10:
                            IBinder _arg010 = data.readStrongBinder();
                            String _arg18 = data.readString();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            destroyBiInteractiveApp(_arg010, _arg18, _arg25);
                            reply.writeNoException();
                            break;
                        case 11:
                            IBinder _arg011 = data.readStrongBinder();
                            boolean _arg19 = data.readBoolean();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setTeletextAppEnabled(_arg011, _arg19, _arg26);
                            reply.writeNoException();
                            break;
                        case 12:
                            IBinder _arg012 = data.readStrongBinder();
                            Rect _arg110 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            sendCurrentVideoBounds(_arg012, _arg110, _arg27);
                            reply.writeNoException();
                            break;
                        case 13:
                            IBinder _arg013 = data.readStrongBinder();
                            Uri _arg111 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            sendCurrentChannelUri(_arg013, _arg111, _arg28);
                            reply.writeNoException();
                            break;
                        case 14:
                            IBinder _arg014 = data.readStrongBinder();
                            int _arg112 = data.readInt();
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            sendCurrentChannelLcn(_arg014, _arg112, _arg29);
                            reply.writeNoException();
                            break;
                        case 15:
                            IBinder _arg015 = data.readStrongBinder();
                            float _arg113 = data.readFloat();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            sendStreamVolume(_arg015, _arg113, _arg210);
                            reply.writeNoException();
                            break;
                        case 16:
                            IBinder _arg016 = data.readStrongBinder();
                            List<TvTrackInfo> _arg114 = data.createTypedArrayList(TvTrackInfo.CREATOR);
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTrackInfoList(_arg016, _arg114, _arg211);
                            reply.writeNoException();
                            break;
                        case 17:
                            IBinder _arg017 = data.readStrongBinder();
                            String _arg115 = data.readString();
                            int _arg212 = data.readInt();
                            data.enforceNoDataAvail();
                            sendCurrentTvInputId(_arg017, _arg115, _arg212);
                            reply.writeNoException();
                            break;
                        case 18:
                            IBinder _arg018 = data.readStrongBinder();
                            int _arg116 = data.readInt();
                            int _arg213 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTimeShiftMode(_arg018, _arg116, _arg213);
                            reply.writeNoException();
                            break;
                        case 19:
                            IBinder _arg019 = data.readStrongBinder();
                            float[] _arg117 = data.createFloatArray();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            sendAvailableSpeeds(_arg019, _arg117, _arg214);
                            reply.writeNoException();
                            break;
                        case 20:
                            IBinder _arg020 = data.readStrongBinder();
                            String _arg118 = data.readString();
                            byte[] _arg215 = data.createByteArray();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            sendSigningResult(_arg020, _arg118, _arg215, _arg32);
                            reply.writeNoException();
                            break;
                        case 21:
                            IBinder _arg021 = data.readStrongBinder();
                            TvRecordingInfo _arg119 = (TvRecordingInfo) data.readTypedObject(TvRecordingInfo.CREATOR);
                            int _arg216 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTvRecordingInfo(_arg021, _arg119, _arg216);
                            reply.writeNoException();
                            break;
                        case 22:
                            IBinder _arg022 = data.readStrongBinder();
                            List<TvRecordingInfo> _arg120 = data.createTypedArrayList(TvRecordingInfo.CREATOR);
                            int _arg217 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTvRecordingInfoList(_arg022, _arg120, _arg217);
                            reply.writeNoException();
                            break;
                        case 23:
                            IBinder _arg023 = data.readStrongBinder();
                            String _arg121 = data.readString();
                            Bundle _arg218 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyError(_arg023, _arg121, _arg218, _arg33);
                            reply.writeNoException();
                            break;
                        case 24:
                            IBinder _arg024 = data.readStrongBinder();
                            PlaybackParams _arg122 = (PlaybackParams) data.readTypedObject(PlaybackParams.CREATOR);
                            int _arg219 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTimeShiftPlaybackParams(_arg024, _arg122, _arg219);
                            reply.writeNoException();
                            break;
                        case 25:
                            IBinder _arg025 = data.readStrongBinder();
                            String _arg123 = data.readString();
                            int _arg220 = data.readInt();
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTimeShiftStatusChanged(_arg025, _arg123, _arg220, _arg34);
                            reply.writeNoException();
                            break;
                        case 26:
                            IBinder _arg026 = data.readStrongBinder();
                            String _arg124 = data.readString();
                            long _arg221 = data.readLong();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTimeShiftStartPositionChanged(_arg026, _arg124, _arg221, _arg35);
                            reply.writeNoException();
                            break;
                        case 27:
                            IBinder _arg027 = data.readStrongBinder();
                            String _arg125 = data.readString();
                            long _arg222 = data.readLong();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTimeShiftCurrentPositionChanged(_arg027, _arg125, _arg222, _arg36);
                            reply.writeNoException();
                            break;
                        case 28:
                            IBinder _arg028 = data.readStrongBinder();
                            String _arg126 = data.readString();
                            String _arg223 = data.readString();
                            int _arg37 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingConnectionFailed(_arg028, _arg126, _arg223, _arg37);
                            reply.writeNoException();
                            break;
                        case 29:
                            IBinder _arg029 = data.readStrongBinder();
                            String _arg127 = data.readString();
                            String _arg224 = data.readString();
                            int _arg38 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingDisconnected(_arg029, _arg127, _arg224, _arg38);
                            reply.writeNoException();
                            break;
                        case 30:
                            IBinder _arg030 = data.readStrongBinder();
                            String _arg128 = data.readString();
                            Uri _arg225 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingTuned(_arg030, _arg128, _arg225, _arg39);
                            reply.writeNoException();
                            break;
                        case 31:
                            IBinder _arg031 = data.readStrongBinder();
                            String _arg129 = data.readString();
                            int _arg226 = data.readInt();
                            int _arg310 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingError(_arg031, _arg129, _arg226, _arg310);
                            reply.writeNoException();
                            break;
                        case 32:
                            IBinder _arg032 = data.readStrongBinder();
                            String _arg130 = data.readString();
                            String _arg227 = data.readString();
                            int _arg311 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingScheduled(_arg032, _arg130, _arg227, _arg311);
                            reply.writeNoException();
                            break;
                        case 33:
                            IBinder _arg033 = data.readStrongBinder();
                            ITvInteractiveAppClient _arg034 = ITvInteractiveAppClient.Stub.asInterface(_arg033);
                            String _arg131 = data.readString();
                            int _arg228 = data.readInt();
                            int _arg312 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            createSession(_arg034, _arg131, _arg228, _arg312, _arg4);
                            reply.writeNoException();
                            break;
                        case 34:
                            IBinder _arg035 = data.readStrongBinder();
                            int _arg132 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseSession(_arg035, _arg132);
                            reply.writeNoException();
                            break;
                        case 35:
                            IBinder _arg036 = data.readStrongBinder();
                            Uri _arg133 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg229 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTuned(_arg036, _arg133, _arg229);
                            reply.writeNoException();
                            break;
                        case 36:
                            IBinder _arg037 = data.readStrongBinder();
                            int _arg134 = data.readInt();
                            String _arg230 = data.readString();
                            int _arg313 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTrackSelected(_arg037, _arg134, _arg230, _arg313);
                            reply.writeNoException();
                            break;
                        case 37:
                            IBinder _arg038 = data.readStrongBinder();
                            List<TvTrackInfo> _arg135 = data.createTypedArrayList(TvTrackInfo.CREATOR);
                            int _arg231 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTracksChanged(_arg038, _arg135, _arg231);
                            reply.writeNoException();
                            break;
                        case 38:
                            IBinder _arg039 = data.readStrongBinder();
                            int _arg136 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyVideoAvailable(_arg039, _arg136);
                            reply.writeNoException();
                            break;
                        case 39:
                            IBinder _arg040 = data.readStrongBinder();
                            int _arg137 = data.readInt();
                            int _arg232 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyVideoUnavailable(_arg040, _arg137, _arg232);
                            reply.writeNoException();
                            break;
                        case 40:
                            IBinder _arg041 = data.readStrongBinder();
                            int _arg138 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyContentAllowed(_arg041, _arg138);
                            reply.writeNoException();
                            break;
                        case 41:
                            IBinder _arg042 = data.readStrongBinder();
                            String _arg139 = data.readString();
                            int _arg233 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyContentBlocked(_arg042, _arg139, _arg233);
                            reply.writeNoException();
                            break;
                        case 42:
                            IBinder _arg043 = data.readStrongBinder();
                            int _arg140 = data.readInt();
                            int _arg234 = data.readInt();
                            data.enforceNoDataAvail();
                            notifySignalStrength(_arg043, _arg140, _arg234);
                            reply.writeNoException();
                            break;
                        case 43:
                            IBinder _arg044 = data.readStrongBinder();
                            String _arg141 = data.readString();
                            String _arg235 = data.readString();
                            int _arg314 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingStarted(_arg044, _arg141, _arg235, _arg314);
                            reply.writeNoException();
                            break;
                        case 44:
                            IBinder _arg045 = data.readStrongBinder();
                            String _arg142 = data.readString();
                            int _arg236 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRecordingStopped(_arg045, _arg142, _arg236);
                            reply.writeNoException();
                            break;
                        case 45:
                            IBinder _arg046 = data.readStrongBinder();
                            String _arg143 = data.readString();
                            Bundle _arg237 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg315 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTvMessage(_arg046, _arg143, _arg237, _arg315);
                            reply.writeNoException();
                            break;
                        case 46:
                            IBinder _arg047 = data.readStrongBinder();
                            Surface _arg144 = (Surface) data.readTypedObject(Surface.CREATOR);
                            int _arg238 = data.readInt();
                            data.enforceNoDataAvail();
                            setSurface(_arg047, _arg144, _arg238);
                            reply.writeNoException();
                            break;
                        case 47:
                            IBinder _arg048 = data.readStrongBinder();
                            int _arg145 = data.readInt();
                            int _arg239 = data.readInt();
                            int _arg316 = data.readInt();
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchSurfaceChanged(_arg048, _arg145, _arg239, _arg316, _arg42);
                            reply.writeNoException();
                            break;
                        case 48:
                            IBinder _arg049 = data.readStrongBinder();
                            BroadcastInfoResponse _arg146 = (BroadcastInfoResponse) data.readTypedObject(BroadcastInfoResponse.CREATOR);
                            int _arg240 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyBroadcastInfoResponse(_arg049, _arg146, _arg240);
                            reply.writeNoException();
                            break;
                        case 49:
                            IBinder _arg050 = data.readStrongBinder();
                            AdResponse _arg147 = (AdResponse) data.readTypedObject(AdResponse.CREATOR);
                            int _arg241 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAdResponse(_arg050, _arg147, _arg241);
                            reply.writeNoException();
                            break;
                        case 50:
                            IBinder _arg051 = data.readStrongBinder();
                            AdBuffer _arg148 = (AdBuffer) data.readTypedObject(AdBuffer.CREATOR);
                            int _arg242 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAdBufferConsumed(_arg051, _arg148, _arg242);
                            reply.writeNoException();
                            break;
                        case 51:
                            IBinder _arg052 = data.readStrongBinder();
                            IBinder _arg149 = data.readStrongBinder();
                            Rect _arg243 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg317 = data.readInt();
                            data.enforceNoDataAvail();
                            createMediaView(_arg052, _arg149, _arg243, _arg317);
                            reply.writeNoException();
                            break;
                        case 52:
                            IBinder _arg053 = data.readStrongBinder();
                            Rect _arg150 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg244 = data.readInt();
                            data.enforceNoDataAvail();
                            relayoutMediaView(_arg053, _arg150, _arg244);
                            reply.writeNoException();
                            break;
                        case 53:
                            IBinder _arg054 = data.readStrongBinder();
                            int _arg151 = data.readInt();
                            data.enforceNoDataAvail();
                            removeMediaView(_arg054, _arg151);
                            reply.writeNoException();
                            break;
                        case 54:
                            ITvInteractiveAppManagerCallback _arg055 = ITvInteractiveAppManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg152 = data.readInt();
                            data.enforceNoDataAvail();
                            registerCallback(_arg055, _arg152);
                            reply.writeNoException();
                            break;
                        case 55:
                            ITvInteractiveAppManagerCallback _arg056 = ITvInteractiveAppManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg153 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg056, _arg153);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.media.tv.interactive.ITvInteractiveAppManager$Stub$Proxy */
        /* loaded from: classes2.dex */
        public static class Proxy implements ITvInteractiveAppManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITvInteractiveAppManager.DESCRIPTOR;
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public List<TvInteractiveAppServiceInfo> getTvInteractiveAppServiceList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<TvInteractiveAppServiceInfo> _result = _reply.createTypedArrayList(TvInteractiveAppServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public List<AppLinkInfo> getAppLinkInfoList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    List<AppLinkInfo> _result = _reply.createTypedArrayList(AppLinkInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void registerAppLinkInfo(String tiasId, AppLinkInfo info, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeString(tiasId);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void unregisterAppLinkInfo(String tiasId, AppLinkInfo info, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeString(tiasId);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendAppLinkCommand(String tiasId, Bundle command, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeString(tiasId);
                    _data.writeTypedObject(command, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void startInteractiveApp(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void stopInteractiveApp(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void resetInteractiveApp(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void createBiInteractiveApp(IBinder sessionToken, Uri biIAppUri, Bundle params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(biIAppUri, 0);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void destroyBiInteractiveApp(IBinder sessionToken, String biIAppId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(biIAppId);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void setTeletextAppEnabled(IBinder sessionToken, boolean enable, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeBoolean(enable);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendCurrentVideoBounds(IBinder sessionToken, Rect bounds, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(bounds, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendCurrentChannelUri(IBinder sessionToken, Uri channelUri, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(channelUri, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendCurrentChannelLcn(IBinder sessionToken, int lcn, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(lcn);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendStreamVolume(IBinder sessionToken, float volume, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeFloat(volume);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendTrackInfoList(IBinder sessionToken, List<TvTrackInfo> tracks, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedList(tracks, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendCurrentTvInputId(IBinder sessionToken, String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendTimeShiftMode(IBinder sessionToken, int mode, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(mode);
                    _data.writeInt(userId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendAvailableSpeeds(IBinder sessionToken, float[] speeds, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeFloatArray(speeds);
                    _data.writeInt(userId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendSigningResult(IBinder sessionToken, String signingId, byte[] result, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(signingId);
                    _data.writeByteArray(result);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendTvRecordingInfo(IBinder sessionToken, TvRecordingInfo recordingInfo, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(recordingInfo, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void sendTvRecordingInfoList(IBinder sessionToken, List<TvRecordingInfo> recordingInfoList, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedList(recordingInfoList, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyError(IBinder sessionToken, String errMsg, Bundle params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(errMsg);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTimeShiftPlaybackParams(IBinder sessionToken, PlaybackParams params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTimeShiftStatusChanged(IBinder sessionToken, String inputId, int status, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(inputId);
                    _data.writeInt(status);
                    _data.writeInt(userId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTimeShiftStartPositionChanged(IBinder sessionToken, String inputId, long timeMs, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(inputId);
                    _data.writeLong(timeMs);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTimeShiftCurrentPositionChanged(IBinder sessionToken, String inputId, long timeMs, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(inputId);
                    _data.writeLong(timeMs);
                    _data.writeInt(userId);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingConnectionFailed(IBinder sessionToken, String recordingId, String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingDisconnected(IBinder sessionToken, String recordingId, String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingTuned(IBinder sessionToken, String recordingId, Uri channelUri, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeTypedObject(channelUri, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingError(IBinder sessionToken, String recordingId, int err, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeInt(err);
                    _data.writeInt(userId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingScheduled(IBinder sessionToken, String recordingId, String requestId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeString(requestId);
                    _data.writeInt(userId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void createSession(ITvInteractiveAppClient client, String iAppServiceId, int type, int seq, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(iAppServiceId);
                    _data.writeInt(type);
                    _data.writeInt(seq);
                    _data.writeInt(userId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void releaseSession(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTuned(IBinder sessionToken, Uri channelUri, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(channelUri, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTrackSelected(IBinder sessionToken, int type, String trackId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(type);
                    _data.writeString(trackId);
                    _data.writeInt(userId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTracksChanged(IBinder sessionToken, List<TvTrackInfo> tracks, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedList(tracks, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyVideoAvailable(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyVideoUnavailable(IBinder sessionToken, int reason, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(reason);
                    _data.writeInt(userId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyContentAllowed(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyContentBlocked(IBinder sessionToken, String rating, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(rating);
                    _data.writeInt(userId);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifySignalStrength(IBinder sessionToken, int stength, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(stength);
                    _data.writeInt(userId);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingStarted(IBinder sessionToken, String recordingId, String requestId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeString(requestId);
                    _data.writeInt(userId);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyRecordingStopped(IBinder sessionToken, String recordingId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(recordingId);
                    _data.writeInt(userId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyTvMessage(IBinder sessionToken, String type, Bundle data, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(type);
                    _data.writeTypedObject(data, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void setSurface(IBinder sessionToken, Surface surface, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(surface, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void dispatchSurfaceChanged(IBinder sessionToken, int format, int width, int height, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(format);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(userId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyBroadcastInfoResponse(IBinder sessionToken, BroadcastInfoResponse response, int UserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(response, 0);
                    _data.writeInt(UserId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyAdResponse(IBinder sessionToken, AdResponse response, int UserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(response, 0);
                    _data.writeInt(UserId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void notifyAdBufferConsumed(IBinder sessionToken, AdBuffer buffer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(buffer, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void createMediaView(IBinder sessionToken, IBinder windowToken, Rect frame, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeStrongBinder(windowToken);
                    _data.writeTypedObject(frame, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void relayoutMediaView(IBinder sessionToken, Rect frame, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(frame, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void removeMediaView(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void registerCallback(ITvInteractiveAppManagerCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManager
            public void unregisterCallback(ITvInteractiveAppManagerCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 54;
        }
    }
}
