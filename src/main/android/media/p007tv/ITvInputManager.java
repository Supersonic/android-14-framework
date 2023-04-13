package android.media.p007tv;

import android.content.AttributionSource;
import android.content.Intent;
import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.p007tv.ITvInputClient;
import android.media.p007tv.ITvInputHardware;
import android.media.p007tv.ITvInputHardwareCallback;
import android.media.p007tv.ITvInputManagerCallback;
import android.media.p007tv.interactive.TvInteractiveAppService;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.view.Surface;
import java.util.List;
/* renamed from: android.media.tv.ITvInputManager */
/* loaded from: classes2.dex */
public interface ITvInputManager extends IInterface {
    ITvInputHardware acquireTvInputHardware(int i, ITvInputHardwareCallback iTvInputHardwareCallback, TvInputInfo tvInputInfo, int i2, String str, int i3) throws RemoteException;

    void addBlockedRating(String str, int i) throws RemoteException;

    void addHardwareDevice(int i) throws RemoteException;

    boolean captureFrame(String str, Surface surface, TvStreamConfig tvStreamConfig, int i) throws RemoteException;

    void createOverlayView(IBinder iBinder, IBinder iBinder2, Rect rect, int i) throws RemoteException;

    void createSession(ITvInputClient iTvInputClient, String str, AttributionSource attributionSource, boolean z, int i, int i2) throws RemoteException;

    void dispatchSurfaceChanged(IBinder iBinder, int i, int i2, int i3, int i4) throws RemoteException;

    List<String> getAvailableExtensionInterfaceNames(String str, int i) throws RemoteException;

    List<TvStreamConfig> getAvailableTvStreamConfigList(String str, int i) throws RemoteException;

    List<String> getBlockedRatings(int i) throws RemoteException;

    int getClientPid(String str) throws RemoteException;

    int getClientPriority(int i, String str) throws RemoteException;

    List<TunedInfo> getCurrentTunedInfos(int i) throws RemoteException;

    List<DvbDeviceInfo> getDvbDeviceList() throws RemoteException;

    IBinder getExtensionInterface(String str, String str2, int i) throws RemoteException;

    List<TvInputHardwareInfo> getHardwareList() throws RemoteException;

    List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int i) throws RemoteException;

    TvInputInfo getTvInputInfo(String str, int i) throws RemoteException;

    List<TvInputInfo> getTvInputList(int i) throws RemoteException;

    int getTvInputState(String str, int i) throws RemoteException;

    boolean isParentalControlsEnabled(int i) throws RemoteException;

    boolean isRatingBlocked(String str, int i) throws RemoteException;

    boolean isSingleSessionActive(int i) throws RemoteException;

    void notifyAdBuffer(IBinder iBinder, AdBuffer adBuffer, int i) throws RemoteException;

    void notifyTvMessage(IBinder iBinder, String str, Bundle bundle, int i) throws RemoteException;

    ParcelFileDescriptor openDvbDevice(DvbDeviceInfo dvbDeviceInfo, int i) throws RemoteException;

    void pauseRecording(IBinder iBinder, Bundle bundle, int i) throws RemoteException;

    void registerCallback(ITvInputManagerCallback iTvInputManagerCallback, int i) throws RemoteException;

    void relayoutOverlayView(IBinder iBinder, Rect rect, int i) throws RemoteException;

    void releaseSession(IBinder iBinder, int i) throws RemoteException;

    void releaseTvInputHardware(int i, ITvInputHardware iTvInputHardware, int i2) throws RemoteException;

    void removeBlockedRating(String str, int i) throws RemoteException;

    void removeBroadcastInfo(IBinder iBinder, int i, int i2) throws RemoteException;

    void removeHardwareDevice(int i) throws RemoteException;

    void removeOverlayView(IBinder iBinder, int i) throws RemoteException;

    void requestAd(IBinder iBinder, AdRequest adRequest, int i) throws RemoteException;

    void requestBroadcastInfo(IBinder iBinder, BroadcastInfoRequest broadcastInfoRequest, int i) throws RemoteException;

    void requestChannelBrowsable(Uri uri, int i) throws RemoteException;

    void resumeRecording(IBinder iBinder, Bundle bundle, int i) throws RemoteException;

    void selectAudioPresentation(IBinder iBinder, int i, int i2, int i3) throws RemoteException;

    void selectTrack(IBinder iBinder, int i, String str, int i2) throws RemoteException;

    void sendAppPrivateCommand(IBinder iBinder, String str, Bundle bundle, int i) throws RemoteException;

    void sendTvInputNotifyIntent(Intent intent, int i) throws RemoteException;

    void setCaptionEnabled(IBinder iBinder, boolean z, int i) throws RemoteException;

    void setInteractiveAppNotificationEnabled(IBinder iBinder, boolean z, int i) throws RemoteException;

    void setMainSession(IBinder iBinder, int i) throws RemoteException;

    void setParentalControlsEnabled(boolean z, int i) throws RemoteException;

    void setSurface(IBinder iBinder, Surface surface, int i) throws RemoteException;

    void setVolume(IBinder iBinder, float f, int i) throws RemoteException;

    void startRecording(IBinder iBinder, Uri uri, Bundle bundle, int i) throws RemoteException;

    void stopRecording(IBinder iBinder, int i) throws RemoteException;

    void timeShiftEnablePositionTracking(IBinder iBinder, boolean z, int i) throws RemoteException;

    void timeShiftPause(IBinder iBinder, int i) throws RemoteException;

    void timeShiftPlay(IBinder iBinder, Uri uri, int i) throws RemoteException;

    void timeShiftResume(IBinder iBinder, int i) throws RemoteException;

    void timeShiftSeekTo(IBinder iBinder, long j, int i) throws RemoteException;

    void timeShiftSetMode(IBinder iBinder, int i, int i2) throws RemoteException;

    void timeShiftSetPlaybackParams(IBinder iBinder, PlaybackParams playbackParams, int i) throws RemoteException;

    void tune(IBinder iBinder, Uri uri, Bundle bundle, int i) throws RemoteException;

    void unblockContent(IBinder iBinder, String str, int i) throws RemoteException;

    void unregisterCallback(ITvInputManagerCallback iTvInputManagerCallback, int i) throws RemoteException;

    void updateTvInputInfo(TvInputInfo tvInputInfo, int i) throws RemoteException;

    /* renamed from: android.media.tv.ITvInputManager$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInputManager {
        @Override // android.media.p007tv.ITvInputManager
        public List<TvInputInfo> getTvInputList(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public TvInputInfo getTvInputInfo(String inputId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void updateTvInputInfo(TvInputInfo inputInfo, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public int getTvInputState(String inputId, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<String> getAvailableExtensionInterfaceNames(String inputId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public IBinder getExtensionInterface(String inputId, String name, int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void registerCallback(ITvInputManagerCallback callback, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void unregisterCallback(ITvInputManagerCallback callback, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public boolean isParentalControlsEnabled(int userId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void setParentalControlsEnabled(boolean enabled, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public boolean isRatingBlocked(String rating, int userId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<String> getBlockedRatings(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void addBlockedRating(String rating, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void removeBlockedRating(String rating, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void createSession(ITvInputClient client, String inputId, AttributionSource tvAppAttributionSource, boolean isRecordingSession, int seq, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void releaseSession(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public int getClientPid(String sessionId) throws RemoteException {
            return 0;
        }

        @Override // android.media.p007tv.ITvInputManager
        public int getClientPriority(int useCase, String sessionId) throws RemoteException {
            return 0;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void setMainSession(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void setSurface(IBinder sessionToken, Surface surface, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void dispatchSurfaceChanged(IBinder sessionToken, int format, int width, int height, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void setVolume(IBinder sessionToken, float volume, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void tune(IBinder sessionToken, Uri channelUri, Bundle params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void setCaptionEnabled(IBinder sessionToken, boolean enabled, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void selectTrack(IBinder sessionToken, int type, String trackId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void selectAudioPresentation(IBinder sessionToken, int presentationId, int programId, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void setInteractiveAppNotificationEnabled(IBinder sessionToken, boolean enabled, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void sendAppPrivateCommand(IBinder sessionToken, String action, Bundle data, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void createOverlayView(IBinder sessionToken, IBinder windowToken, Rect frame, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void relayoutOverlayView(IBinder sessionToken, Rect frame, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void removeOverlayView(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void unblockContent(IBinder sessionToken, String unblockedRating, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftPlay(IBinder sessionToken, Uri recordedProgramUri, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftPause(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftResume(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftSeekTo(IBinder sessionToken, long timeMs, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftSetPlaybackParams(IBinder sessionToken, PlaybackParams params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftSetMode(IBinder sessionToken, int mode, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void timeShiftEnablePositionTracking(IBinder sessionToken, boolean enable, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<TunedInfo> getCurrentTunedInfos(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void startRecording(IBinder sessionToken, Uri programUri, Bundle params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void stopRecording(IBinder sessionToken, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void pauseRecording(IBinder sessionToken, Bundle params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void resumeRecording(IBinder sessionToken, Bundle params, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void requestBroadcastInfo(IBinder sessionToken, BroadcastInfoRequest request, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void removeBroadcastInfo(IBinder sessionToken, int id, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void requestAd(IBinder sessionToken, AdRequest request, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void notifyAdBuffer(IBinder sessionToken, AdBuffer buffer, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void notifyTvMessage(IBinder sessionToken, String type, Bundle data, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<TvInputHardwareInfo> getHardwareList() throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public ITvInputHardware acquireTvInputHardware(int deviceId, ITvInputHardwareCallback callback, TvInputInfo info, int userId, String tvInputSessionId, int priorityHint) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void releaseTvInputHardware(int deviceId, ITvInputHardware hardware, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<TvStreamConfig> getAvailableTvStreamConfigList(String inputId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public boolean captureFrame(String inputId, Surface surface, TvStreamConfig config, int userId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.ITvInputManager
        public boolean isSingleSessionActive(int userId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.ITvInputManager
        public List<DvbDeviceInfo> getDvbDeviceList() throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo info, int device) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputManager
        public void sendTvInputNotifyIntent(Intent intent, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void requestChannelBrowsable(Uri channelUri, int userId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void addHardwareDevice(int deviceId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManager
        public void removeHardwareDevice(int deviceId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.ITvInputManager$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInputManager {
        public static final String DESCRIPTOR = "android.media.tv.ITvInputManager";
        static final int TRANSACTION_acquireTvInputHardware = 52;
        static final int TRANSACTION_addBlockedRating = 14;
        static final int TRANSACTION_addHardwareDevice = 61;
        static final int TRANSACTION_captureFrame = 55;
        static final int TRANSACTION_createOverlayView = 30;
        static final int TRANSACTION_createSession = 16;
        static final int TRANSACTION_dispatchSurfaceChanged = 22;
        static final int TRANSACTION_getAvailableExtensionInterfaceNames = 5;
        static final int TRANSACTION_getAvailableTvStreamConfigList = 54;
        static final int TRANSACTION_getBlockedRatings = 13;
        static final int TRANSACTION_getClientPid = 18;
        static final int TRANSACTION_getClientPriority = 19;
        static final int TRANSACTION_getCurrentTunedInfos = 41;
        static final int TRANSACTION_getDvbDeviceList = 57;
        static final int TRANSACTION_getExtensionInterface = 6;
        static final int TRANSACTION_getHardwareList = 51;
        static final int TRANSACTION_getTvContentRatingSystemList = 7;
        static final int TRANSACTION_getTvInputInfo = 2;
        static final int TRANSACTION_getTvInputList = 1;
        static final int TRANSACTION_getTvInputState = 4;
        static final int TRANSACTION_isParentalControlsEnabled = 10;
        static final int TRANSACTION_isRatingBlocked = 12;
        static final int TRANSACTION_isSingleSessionActive = 56;
        static final int TRANSACTION_notifyAdBuffer = 49;
        static final int TRANSACTION_notifyTvMessage = 50;
        static final int TRANSACTION_openDvbDevice = 58;
        static final int TRANSACTION_pauseRecording = 44;
        static final int TRANSACTION_registerCallback = 8;
        static final int TRANSACTION_relayoutOverlayView = 31;
        static final int TRANSACTION_releaseSession = 17;
        static final int TRANSACTION_releaseTvInputHardware = 53;
        static final int TRANSACTION_removeBlockedRating = 15;
        static final int TRANSACTION_removeBroadcastInfo = 47;
        static final int TRANSACTION_removeHardwareDevice = 62;
        static final int TRANSACTION_removeOverlayView = 32;
        static final int TRANSACTION_requestAd = 48;
        static final int TRANSACTION_requestBroadcastInfo = 46;
        static final int TRANSACTION_requestChannelBrowsable = 60;
        static final int TRANSACTION_resumeRecording = 45;
        static final int TRANSACTION_selectAudioPresentation = 27;
        static final int TRANSACTION_selectTrack = 26;
        static final int TRANSACTION_sendAppPrivateCommand = 29;
        static final int TRANSACTION_sendTvInputNotifyIntent = 59;
        static final int TRANSACTION_setCaptionEnabled = 25;
        static final int TRANSACTION_setInteractiveAppNotificationEnabled = 28;
        static final int TRANSACTION_setMainSession = 20;
        static final int TRANSACTION_setParentalControlsEnabled = 11;
        static final int TRANSACTION_setSurface = 21;
        static final int TRANSACTION_setVolume = 23;
        static final int TRANSACTION_startRecording = 42;
        static final int TRANSACTION_stopRecording = 43;
        static final int TRANSACTION_timeShiftEnablePositionTracking = 40;
        static final int TRANSACTION_timeShiftPause = 35;
        static final int TRANSACTION_timeShiftPlay = 34;
        static final int TRANSACTION_timeShiftResume = 36;
        static final int TRANSACTION_timeShiftSeekTo = 37;
        static final int TRANSACTION_timeShiftSetMode = 39;
        static final int TRANSACTION_timeShiftSetPlaybackParams = 38;
        static final int TRANSACTION_tune = 24;
        static final int TRANSACTION_unblockContent = 33;
        static final int TRANSACTION_unregisterCallback = 9;
        static final int TRANSACTION_updateTvInputInfo = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInputManager)) {
                return (ITvInputManager) iin;
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
                    return "getTvInputList";
                case 2:
                    return "getTvInputInfo";
                case 3:
                    return "updateTvInputInfo";
                case 4:
                    return "getTvInputState";
                case 5:
                    return "getAvailableExtensionInterfaceNames";
                case 6:
                    return "getExtensionInterface";
                case 7:
                    return "getTvContentRatingSystemList";
                case 8:
                    return "registerCallback";
                case 9:
                    return "unregisterCallback";
                case 10:
                    return "isParentalControlsEnabled";
                case 11:
                    return "setParentalControlsEnabled";
                case 12:
                    return "isRatingBlocked";
                case 13:
                    return "getBlockedRatings";
                case 14:
                    return "addBlockedRating";
                case 15:
                    return "removeBlockedRating";
                case 16:
                    return "createSession";
                case 17:
                    return "releaseSession";
                case 18:
                    return "getClientPid";
                case 19:
                    return "getClientPriority";
                case 20:
                    return "setMainSession";
                case 21:
                    return "setSurface";
                case 22:
                    return "dispatchSurfaceChanged";
                case 23:
                    return "setVolume";
                case 24:
                    return TvInteractiveAppService.PLAYBACK_COMMAND_TYPE_TUNE;
                case 25:
                    return "setCaptionEnabled";
                case 26:
                    return "selectTrack";
                case 27:
                    return "selectAudioPresentation";
                case 28:
                    return "setInteractiveAppNotificationEnabled";
                case 29:
                    return "sendAppPrivateCommand";
                case 30:
                    return "createOverlayView";
                case 31:
                    return "relayoutOverlayView";
                case 32:
                    return "removeOverlayView";
                case 33:
                    return "unblockContent";
                case 34:
                    return "timeShiftPlay";
                case 35:
                    return "timeShiftPause";
                case 36:
                    return "timeShiftResume";
                case 37:
                    return "timeShiftSeekTo";
                case 38:
                    return "timeShiftSetPlaybackParams";
                case 39:
                    return "timeShiftSetMode";
                case 40:
                    return "timeShiftEnablePositionTracking";
                case 41:
                    return "getCurrentTunedInfos";
                case 42:
                    return "startRecording";
                case 43:
                    return "stopRecording";
                case 44:
                    return "pauseRecording";
                case 45:
                    return "resumeRecording";
                case 46:
                    return "requestBroadcastInfo";
                case 47:
                    return "removeBroadcastInfo";
                case 48:
                    return "requestAd";
                case 49:
                    return "notifyAdBuffer";
                case 50:
                    return "notifyTvMessage";
                case 51:
                    return "getHardwareList";
                case 52:
                    return "acquireTvInputHardware";
                case 53:
                    return "releaseTvInputHardware";
                case 54:
                    return "getAvailableTvStreamConfigList";
                case 55:
                    return "captureFrame";
                case 56:
                    return "isSingleSessionActive";
                case 57:
                    return "getDvbDeviceList";
                case 58:
                    return "openDvbDevice";
                case 59:
                    return "sendTvInputNotifyIntent";
                case 60:
                    return "requestChannelBrowsable";
                case 61:
                    return "addHardwareDevice";
                case 62:
                    return "removeHardwareDevice";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            List<TvInputInfo> _result = getTvInputList(_arg0);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            TvInputInfo _result2 = getTvInputInfo(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            TvInputInfo _arg03 = (TvInputInfo) data.readTypedObject(TvInputInfo.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            updateTvInputInfo(_arg03, _arg12);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = getTvInputState(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result4 = getAvailableExtensionInterfaceNames(_arg05, _arg14);
                            reply.writeNoException();
                            reply.writeStringList(_result4);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg15 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            IBinder _result5 = getExtensionInterface(_arg06, _arg15, _arg2);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result5);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            List<TvContentRatingSystemInfo> _result6 = getTvContentRatingSystemList(_arg07);
                            reply.writeNoException();
                            reply.writeTypedList(_result6, 1);
                            break;
                        case 8:
                            ITvInputManagerCallback _arg08 = ITvInputManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            registerCallback(_arg08, _arg16);
                            reply.writeNoException();
                            break;
                        case 9:
                            ITvInputManagerCallback _arg09 = ITvInputManagerCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg09, _arg17);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = isParentalControlsEnabled(_arg010);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 11:
                            boolean _arg011 = data.readBoolean();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            setParentalControlsEnabled(_arg011, _arg18);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = isRatingBlocked(_arg012, _arg19);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result9 = getBlockedRatings(_arg013);
                            reply.writeNoException();
                            reply.writeStringList(_result9);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            addBlockedRating(_arg014, _arg110);
                            reply.writeNoException();
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            removeBlockedRating(_arg015, _arg111);
                            reply.writeNoException();
                            break;
                        case 16:
                            IBinder _arg016 = data.readStrongBinder();
                            ITvInputClient _arg017 = ITvInputClient.Stub.asInterface(_arg016);
                            String _arg112 = data.readString();
                            AttributionSource _arg22 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            createSession(_arg017, _arg112, _arg22, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            break;
                        case 17:
                            IBinder _arg018 = data.readStrongBinder();
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseSession(_arg018, _arg113);
                            reply.writeNoException();
                            break;
                        case 18:
                            String _arg019 = data.readString();
                            data.enforceNoDataAvail();
                            int _result10 = getClientPid(_arg019);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 19:
                            int _arg020 = data.readInt();
                            String _arg114 = data.readString();
                            data.enforceNoDataAvail();
                            int _result11 = getClientPriority(_arg020, _arg114);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 20:
                            IBinder _arg021 = data.readStrongBinder();
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            setMainSession(_arg021, _arg115);
                            reply.writeNoException();
                            break;
                        case 21:
                            IBinder _arg022 = data.readStrongBinder();
                            Surface _arg116 = (Surface) data.readTypedObject(Surface.CREATOR);
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            setSurface(_arg022, _arg116, _arg23);
                            reply.writeNoException();
                            break;
                        case 22:
                            IBinder _arg023 = data.readStrongBinder();
                            int _arg117 = data.readInt();
                            int _arg24 = data.readInt();
                            int _arg32 = data.readInt();
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchSurfaceChanged(_arg023, _arg117, _arg24, _arg32, _arg42);
                            reply.writeNoException();
                            break;
                        case 23:
                            IBinder _arg024 = data.readStrongBinder();
                            float _arg118 = data.readFloat();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            setVolume(_arg024, _arg118, _arg25);
                            reply.writeNoException();
                            break;
                        case 24:
                            IBinder _arg025 = data.readStrongBinder();
                            Uri _arg119 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg26 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            tune(_arg025, _arg119, _arg26, _arg33);
                            reply.writeNoException();
                            break;
                        case 25:
                            IBinder _arg026 = data.readStrongBinder();
                            boolean _arg120 = data.readBoolean();
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            setCaptionEnabled(_arg026, _arg120, _arg27);
                            reply.writeNoException();
                            break;
                        case 26:
                            IBinder _arg027 = data.readStrongBinder();
                            int _arg121 = data.readInt();
                            String _arg28 = data.readString();
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            selectTrack(_arg027, _arg121, _arg28, _arg34);
                            reply.writeNoException();
                            break;
                        case 27:
                            IBinder _arg028 = data.readStrongBinder();
                            int _arg122 = data.readInt();
                            int _arg29 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            selectAudioPresentation(_arg028, _arg122, _arg29, _arg35);
                            reply.writeNoException();
                            break;
                        case 28:
                            IBinder _arg029 = data.readStrongBinder();
                            boolean _arg123 = data.readBoolean();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            setInteractiveAppNotificationEnabled(_arg029, _arg123, _arg210);
                            reply.writeNoException();
                            break;
                        case 29:
                            IBinder _arg030 = data.readStrongBinder();
                            String _arg124 = data.readString();
                            Bundle _arg211 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            sendAppPrivateCommand(_arg030, _arg124, _arg211, _arg36);
                            reply.writeNoException();
                            break;
                        case 30:
                            IBinder _arg031 = data.readStrongBinder();
                            IBinder _arg125 = data.readStrongBinder();
                            Rect _arg212 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg37 = data.readInt();
                            data.enforceNoDataAvail();
                            createOverlayView(_arg031, _arg125, _arg212, _arg37);
                            reply.writeNoException();
                            break;
                        case 31:
                            IBinder _arg032 = data.readStrongBinder();
                            Rect _arg126 = (Rect) data.readTypedObject(Rect.CREATOR);
                            int _arg213 = data.readInt();
                            data.enforceNoDataAvail();
                            relayoutOverlayView(_arg032, _arg126, _arg213);
                            reply.writeNoException();
                            break;
                        case 32:
                            IBinder _arg033 = data.readStrongBinder();
                            int _arg127 = data.readInt();
                            data.enforceNoDataAvail();
                            removeOverlayView(_arg033, _arg127);
                            reply.writeNoException();
                            break;
                        case 33:
                            IBinder _arg034 = data.readStrongBinder();
                            String _arg128 = data.readString();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            unblockContent(_arg034, _arg128, _arg214);
                            reply.writeNoException();
                            break;
                        case 34:
                            IBinder _arg035 = data.readStrongBinder();
                            Uri _arg129 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg215 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftPlay(_arg035, _arg129, _arg215);
                            reply.writeNoException();
                            break;
                        case 35:
                            IBinder _arg036 = data.readStrongBinder();
                            int _arg130 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftPause(_arg036, _arg130);
                            reply.writeNoException();
                            break;
                        case 36:
                            IBinder _arg037 = data.readStrongBinder();
                            int _arg131 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftResume(_arg037, _arg131);
                            reply.writeNoException();
                            break;
                        case 37:
                            IBinder _arg038 = data.readStrongBinder();
                            long _arg132 = data.readLong();
                            int _arg216 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftSeekTo(_arg038, _arg132, _arg216);
                            reply.writeNoException();
                            break;
                        case 38:
                            IBinder _arg039 = data.readStrongBinder();
                            PlaybackParams _arg133 = (PlaybackParams) data.readTypedObject(PlaybackParams.CREATOR);
                            int _arg217 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftSetPlaybackParams(_arg039, _arg133, _arg217);
                            reply.writeNoException();
                            break;
                        case 39:
                            IBinder _arg040 = data.readStrongBinder();
                            int _arg134 = data.readInt();
                            int _arg218 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftSetMode(_arg040, _arg134, _arg218);
                            reply.writeNoException();
                            break;
                        case 40:
                            IBinder _arg041 = data.readStrongBinder();
                            boolean _arg135 = data.readBoolean();
                            int _arg219 = data.readInt();
                            data.enforceNoDataAvail();
                            timeShiftEnablePositionTracking(_arg041, _arg135, _arg219);
                            reply.writeNoException();
                            break;
                        case 41:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            List<TunedInfo> _result12 = getCurrentTunedInfos(_arg042);
                            reply.writeNoException();
                            reply.writeTypedList(_result12, 1);
                            break;
                        case 42:
                            IBinder _arg043 = data.readStrongBinder();
                            Uri _arg136 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg220 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg38 = data.readInt();
                            data.enforceNoDataAvail();
                            startRecording(_arg043, _arg136, _arg220, _arg38);
                            reply.writeNoException();
                            break;
                        case 43:
                            IBinder _arg044 = data.readStrongBinder();
                            int _arg137 = data.readInt();
                            data.enforceNoDataAvail();
                            stopRecording(_arg044, _arg137);
                            reply.writeNoException();
                            break;
                        case 44:
                            IBinder _arg045 = data.readStrongBinder();
                            Bundle _arg138 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg221 = data.readInt();
                            data.enforceNoDataAvail();
                            pauseRecording(_arg045, _arg138, _arg221);
                            reply.writeNoException();
                            break;
                        case 45:
                            IBinder _arg046 = data.readStrongBinder();
                            Bundle _arg139 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg222 = data.readInt();
                            data.enforceNoDataAvail();
                            resumeRecording(_arg046, _arg139, _arg222);
                            reply.writeNoException();
                            break;
                        case 46:
                            IBinder _arg047 = data.readStrongBinder();
                            BroadcastInfoRequest _arg140 = (BroadcastInfoRequest) data.readTypedObject(BroadcastInfoRequest.CREATOR);
                            int _arg223 = data.readInt();
                            data.enforceNoDataAvail();
                            requestBroadcastInfo(_arg047, _arg140, _arg223);
                            reply.writeNoException();
                            break;
                        case 47:
                            IBinder _arg048 = data.readStrongBinder();
                            int _arg141 = data.readInt();
                            int _arg224 = data.readInt();
                            data.enforceNoDataAvail();
                            removeBroadcastInfo(_arg048, _arg141, _arg224);
                            reply.writeNoException();
                            break;
                        case 48:
                            IBinder _arg049 = data.readStrongBinder();
                            AdRequest _arg142 = (AdRequest) data.readTypedObject(AdRequest.CREATOR);
                            int _arg225 = data.readInt();
                            data.enforceNoDataAvail();
                            requestAd(_arg049, _arg142, _arg225);
                            reply.writeNoException();
                            break;
                        case 49:
                            IBinder _arg050 = data.readStrongBinder();
                            AdBuffer _arg143 = (AdBuffer) data.readTypedObject(AdBuffer.CREATOR);
                            int _arg226 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAdBuffer(_arg050, _arg143, _arg226);
                            reply.writeNoException();
                            break;
                        case 50:
                            IBinder _arg051 = data.readStrongBinder();
                            String _arg144 = data.readString();
                            Bundle _arg227 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyTvMessage(_arg051, _arg144, _arg227, _arg39);
                            reply.writeNoException();
                            break;
                        case 51:
                            List<TvInputHardwareInfo> _result13 = getHardwareList();
                            reply.writeNoException();
                            reply.writeTypedList(_result13, 1);
                            break;
                        case 52:
                            int _arg052 = data.readInt();
                            ITvInputHardwareCallback _arg145 = ITvInputHardwareCallback.Stub.asInterface(data.readStrongBinder());
                            TvInputInfo _arg228 = (TvInputInfo) data.readTypedObject(TvInputInfo.CREATOR);
                            int _arg310 = data.readInt();
                            String _arg43 = data.readString();
                            int _arg52 = data.readInt();
                            data.enforceNoDataAvail();
                            ITvInputHardware _result14 = acquireTvInputHardware(_arg052, _arg145, _arg228, _arg310, _arg43, _arg52);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result14);
                            break;
                        case 53:
                            int _arg053 = data.readInt();
                            ITvInputHardware _arg146 = ITvInputHardware.Stub.asInterface(data.readStrongBinder());
                            int _arg229 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseTvInputHardware(_arg053, _arg146, _arg229);
                            reply.writeNoException();
                            break;
                        case 54:
                            String _arg054 = data.readString();
                            int _arg147 = data.readInt();
                            data.enforceNoDataAvail();
                            List<TvStreamConfig> _result15 = getAvailableTvStreamConfigList(_arg054, _arg147);
                            reply.writeNoException();
                            reply.writeTypedList(_result15, 1);
                            break;
                        case 55:
                            String _arg055 = data.readString();
                            Surface _arg148 = (Surface) data.readTypedObject(Surface.CREATOR);
                            TvStreamConfig _arg230 = (TvStreamConfig) data.readTypedObject(TvStreamConfig.CREATOR);
                            int _arg311 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result16 = captureFrame(_arg055, _arg148, _arg230, _arg311);
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 56:
                            int _arg056 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result17 = isSingleSessionActive(_arg056);
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 57:
                            List<DvbDeviceInfo> _result18 = getDvbDeviceList();
                            reply.writeNoException();
                            reply.writeTypedList(_result18, 1);
                            break;
                        case 58:
                            DvbDeviceInfo _arg057 = (DvbDeviceInfo) data.readTypedObject(DvbDeviceInfo.CREATOR);
                            int _arg149 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result19 = openDvbDevice(_arg057, _arg149);
                            reply.writeNoException();
                            reply.writeTypedObject(_result19, 1);
                            break;
                        case 59:
                            Intent _arg058 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg150 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTvInputNotifyIntent(_arg058, _arg150);
                            reply.writeNoException();
                            break;
                        case 60:
                            Uri _arg059 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg151 = data.readInt();
                            data.enforceNoDataAvail();
                            requestChannelBrowsable(_arg059, _arg151);
                            reply.writeNoException();
                            break;
                        case 61:
                            int _arg060 = data.readInt();
                            data.enforceNoDataAvail();
                            addHardwareDevice(_arg060);
                            reply.writeNoException();
                            break;
                        case 62:
                            int _arg061 = data.readInt();
                            data.enforceNoDataAvail();
                            removeHardwareDevice(_arg061);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.media.tv.ITvInputManager$Stub$Proxy */
        /* loaded from: classes2.dex */
        public static class Proxy implements ITvInputManager {
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

            @Override // android.media.p007tv.ITvInputManager
            public List<TvInputInfo> getTvInputList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<TvInputInfo> _result = _reply.createTypedArrayList(TvInputInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public TvInputInfo getTvInputInfo(String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    TvInputInfo _result = (TvInputInfo) _reply.readTypedObject(TvInputInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void updateTvInputInfo(TvInputInfo inputInfo, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(inputInfo, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public int getTvInputState(String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<String> getAvailableExtensionInterfaceNames(String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public IBinder getExtensionInterface(String inputId, String name, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeString(name);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    List<TvContentRatingSystemInfo> _result = _reply.createTypedArrayList(TvContentRatingSystemInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void registerCallback(ITvInputManagerCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void unregisterCallback(ITvInputManagerCallback callback, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public boolean isParentalControlsEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void setParentalControlsEnabled(boolean enabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public boolean isRatingBlocked(String rating, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rating);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<String> getBlockedRatings(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void addBlockedRating(String rating, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rating);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void removeBlockedRating(String rating, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rating);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void createSession(ITvInputClient client, String inputId, AttributionSource tvAppAttributionSource, boolean isRecordingSession, int seq, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(inputId);
                    _data.writeTypedObject(tvAppAttributionSource, 0);
                    _data.writeBoolean(isRecordingSession);
                    _data.writeInt(seq);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void releaseSession(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public int getClientPid(String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sessionId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public int getClientPriority(int useCase, String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(useCase);
                    _data.writeString(sessionId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void setMainSession(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void setSurface(IBinder sessionToken, Surface surface, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(surface, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void dispatchSurfaceChanged(IBinder sessionToken, int format, int width, int height, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(format);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void setVolume(IBinder sessionToken, float volume, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeFloat(volume);
                    _data.writeInt(userId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void tune(IBinder sessionToken, Uri channelUri, Bundle params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(channelUri, 0);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void setCaptionEnabled(IBinder sessionToken, boolean enabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeBoolean(enabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void selectTrack(IBinder sessionToken, int type, String trackId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(type);
                    _data.writeString(trackId);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void selectAudioPresentation(IBinder sessionToken, int presentationId, int programId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(presentationId);
                    _data.writeInt(programId);
                    _data.writeInt(userId);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void setInteractiveAppNotificationEnabled(IBinder sessionToken, boolean enabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeBoolean(enabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void sendAppPrivateCommand(IBinder sessionToken, String action, Bundle data, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(action);
                    _data.writeTypedObject(data, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void createOverlayView(IBinder sessionToken, IBinder windowToken, Rect frame, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeStrongBinder(windowToken);
                    _data.writeTypedObject(frame, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void relayoutOverlayView(IBinder sessionToken, Rect frame, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(frame, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void removeOverlayView(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void unblockContent(IBinder sessionToken, String unblockedRating, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(unblockedRating);
                    _data.writeInt(userId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftPlay(IBinder sessionToken, Uri recordedProgramUri, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(recordedProgramUri, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftPause(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftResume(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftSeekTo(IBinder sessionToken, long timeMs, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeLong(timeMs);
                    _data.writeInt(userId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftSetPlaybackParams(IBinder sessionToken, PlaybackParams params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftSetMode(IBinder sessionToken, int mode, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(mode);
                    _data.writeInt(userId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void timeShiftEnablePositionTracking(IBinder sessionToken, boolean enable, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeBoolean(enable);
                    _data.writeInt(userId);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<TunedInfo> getCurrentTunedInfos(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    List<TunedInfo> _result = _reply.createTypedArrayList(TunedInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void startRecording(IBinder sessionToken, Uri programUri, Bundle params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(programUri, 0);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void stopRecording(IBinder sessionToken, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(userId);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void pauseRecording(IBinder sessionToken, Bundle params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void resumeRecording(IBinder sessionToken, Bundle params, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void requestBroadcastInfo(IBinder sessionToken, BroadcastInfoRequest request, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void removeBroadcastInfo(IBinder sessionToken, int id, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeInt(id);
                    _data.writeInt(userId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void requestAd(IBinder sessionToken, AdRequest request, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void notifyAdBuffer(IBinder sessionToken, AdBuffer buffer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeTypedObject(buffer, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void notifyTvMessage(IBinder sessionToken, String type, Bundle data, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sessionToken);
                    _data.writeString(type);
                    _data.writeTypedObject(data, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<TvInputHardwareInfo> getHardwareList() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    List<TvInputHardwareInfo> _result = _reply.createTypedArrayList(TvInputHardwareInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public ITvInputHardware acquireTvInputHardware(int deviceId, ITvInputHardwareCallback callback, TvInputInfo info, int userId, String tvInputSessionId, int priorityHint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(userId);
                    _data.writeString(tvInputSessionId);
                    _data.writeInt(priorityHint);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    ITvInputHardware _result = ITvInputHardware.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void releaseTvInputHardware(int deviceId, ITvInputHardware hardware, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(hardware);
                    _data.writeInt(userId);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<TvStreamConfig> getAvailableTvStreamConfigList(String inputId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeInt(userId);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    List<TvStreamConfig> _result = _reply.createTypedArrayList(TvStreamConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public boolean captureFrame(String inputId, Surface surface, TvStreamConfig config, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeTypedObject(surface, 0);
                    _data.writeTypedObject(config, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public boolean isSingleSessionActive(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public List<DvbDeviceInfo> getDvbDeviceList() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    List<DvbDeviceInfo> _result = _reply.createTypedArrayList(DvbDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo info, int device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(device);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void sendTvInputNotifyIntent(Intent intent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void requestChannelBrowsable(Uri channelUri, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(channelUri, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void addHardwareDevice(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManager
            public void removeHardwareDevice(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 61;
        }
    }
}
