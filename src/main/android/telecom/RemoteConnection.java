package android.telecom;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.BadParcelableException;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telecom.Connection;
import android.telecom.Logging.Session;
import android.telecom.RemoteConnection;
import android.telecom.VideoProfile;
import android.view.Surface;
import com.android.internal.telecom.IConnectionService;
import com.android.internal.telecom.IVideoCallback;
import com.android.internal.telecom.IVideoProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes3.dex */
public final class RemoteConnection {
    private Uri mAddress;
    private int mAddressPresentation;
    private final Set<CallbackRecord> mCallbackRecords = Collections.newSetFromMap(new ConcurrentHashMap(8, 0.9f, 1));
    private String mCallerDisplayName;
    private int mCallerDisplayNamePresentation;
    private String mCallingPackageAbbreviation;
    private RemoteConference mConference;
    private final List<RemoteConnection> mConferenceableConnections;
    private boolean mConnected;
    private int mConnectionCapabilities;
    private final String mConnectionId;
    private int mConnectionProperties;
    private IConnectionService mConnectionService;
    private DisconnectCause mDisconnectCause;
    private Bundle mExtras;
    private boolean mIsVoipAudioMode;
    private boolean mRingbackRequested;
    private int mState;
    private StatusHints mStatusHints;
    private final List<RemoteConnection> mUnmodifiableconferenceableConnections;
    private VideoProvider mVideoProvider;
    private int mVideoState;

    /* loaded from: classes3.dex */
    public static abstract class Callback {
        public void onStateChanged(RemoteConnection connection, int state) {
        }

        public void onDisconnected(RemoteConnection connection, DisconnectCause disconnectCause) {
        }

        public void onRingbackRequested(RemoteConnection connection, boolean ringback) {
        }

        public void onConnectionCapabilitiesChanged(RemoteConnection connection, int connectionCapabilities) {
        }

        public void onConnectionPropertiesChanged(RemoteConnection connection, int connectionProperties) {
        }

        public void onPostDialWait(RemoteConnection connection, String remainingPostDialSequence) {
        }

        public void onPostDialChar(RemoteConnection connection, char nextChar) {
        }

        public void onVoipAudioChanged(RemoteConnection connection, boolean isVoip) {
        }

        public void onStatusHintsChanged(RemoteConnection connection, StatusHints statusHints) {
        }

        public void onAddressChanged(RemoteConnection connection, Uri address, int presentation) {
        }

        public void onCallerDisplayNameChanged(RemoteConnection connection, String callerDisplayName, int presentation) {
        }

        public void onVideoStateChanged(RemoteConnection connection, int videoState) {
        }

        public void onDestroyed(RemoteConnection connection) {
        }

        public void onConferenceableConnectionsChanged(RemoteConnection connection, List<RemoteConnection> conferenceableConnections) {
        }

        public void onVideoProviderChanged(RemoteConnection connection, VideoProvider videoProvider) {
        }

        public void onConferenceChanged(RemoteConnection connection, RemoteConference conference) {
        }

        public void onExtrasChanged(RemoteConnection connection, Bundle extras) {
        }

        public void onConnectionEvent(RemoteConnection connection, String event, Bundle extras) {
        }

        public void onRttInitiationSuccess(RemoteConnection connection) {
        }

        public void onRttInitiationFailure(RemoteConnection connection, int reason) {
        }

        public void onRttSessionRemotelyTerminated(RemoteConnection connection) {
        }

        public void onRemoteRttRequest(RemoteConnection connection) {
        }
    }

    /* loaded from: classes3.dex */
    public static class VideoProvider {
        private final Set<Callback> mCallbacks;
        private final String mCallingPackage;
        private final int mTargetSdkVersion;
        private final IVideoCallback mVideoCallbackDelegate;
        private final VideoCallbackServant mVideoCallbackServant;
        private final IVideoProvider mVideoProviderBinder;

        /* loaded from: classes3.dex */
        public static abstract class Callback {
            public void onSessionModifyRequestReceived(VideoProvider videoProvider, VideoProfile videoProfile) {
            }

            public void onSessionModifyResponseReceived(VideoProvider videoProvider, int status, VideoProfile requestedProfile, VideoProfile responseProfile) {
            }

            public void onCallSessionEvent(VideoProvider videoProvider, int event) {
            }

            public void onPeerDimensionsChanged(VideoProvider videoProvider, int width, int height) {
            }

            public void onCallDataUsageChanged(VideoProvider videoProvider, long dataUsage) {
            }

            public void onCameraCapabilitiesChanged(VideoProvider videoProvider, VideoProfile.CameraCapabilities cameraCapabilities) {
            }

            public void onVideoQualityChanged(VideoProvider videoProvider, int videoQuality) {
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public VideoProvider(IVideoProvider videoProviderBinder, String callingPackage, int targetSdkVersion) {
            IVideoCallback iVideoCallback = new IVideoCallback() { // from class: android.telecom.RemoteConnection.VideoProvider.1
                @Override // com.android.internal.telecom.IVideoCallback
                public void receiveSessionModifyRequest(VideoProfile videoProfile) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onSessionModifyRequestReceived(VideoProvider.this, videoProfile);
                    }
                }

                @Override // com.android.internal.telecom.IVideoCallback
                public void receiveSessionModifyResponse(int status, VideoProfile requestedProfile, VideoProfile responseProfile) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onSessionModifyResponseReceived(VideoProvider.this, status, requestedProfile, responseProfile);
                    }
                }

                @Override // com.android.internal.telecom.IVideoCallback
                public void handleCallSessionEvent(int event) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onCallSessionEvent(VideoProvider.this, event);
                    }
                }

                @Override // com.android.internal.telecom.IVideoCallback
                public void changePeerDimensions(int width, int height) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onPeerDimensionsChanged(VideoProvider.this, width, height);
                    }
                }

                @Override // com.android.internal.telecom.IVideoCallback
                public void changeCallDataUsage(long dataUsage) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onCallDataUsageChanged(VideoProvider.this, dataUsage);
                    }
                }

                @Override // com.android.internal.telecom.IVideoCallback
                public void changeCameraCapabilities(VideoProfile.CameraCapabilities cameraCapabilities) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onCameraCapabilitiesChanged(VideoProvider.this, cameraCapabilities);
                    }
                }

                @Override // com.android.internal.telecom.IVideoCallback
                public void changeVideoQuality(int videoQuality) {
                    for (Callback l : VideoProvider.this.mCallbacks) {
                        l.onVideoQualityChanged(VideoProvider.this, videoQuality);
                    }
                }

                @Override // android.p008os.IInterface
                public IBinder asBinder() {
                    return null;
                }
            };
            this.mVideoCallbackDelegate = iVideoCallback;
            VideoCallbackServant videoCallbackServant = new VideoCallbackServant(iVideoCallback);
            this.mVideoCallbackServant = videoCallbackServant;
            this.mCallbacks = Collections.newSetFromMap(new ConcurrentHashMap(8, 0.9f, 1));
            this.mVideoProviderBinder = videoProviderBinder;
            this.mCallingPackage = callingPackage;
            this.mTargetSdkVersion = targetSdkVersion;
            try {
                videoProviderBinder.addVideoCallback(videoCallbackServant.getStub().asBinder());
            } catch (RemoteException e) {
            }
        }

        public void registerCallback(Callback l) {
            this.mCallbacks.add(l);
        }

        public void unregisterCallback(Callback l) {
            this.mCallbacks.remove(l);
        }

        public void setCamera(String cameraId) {
            try {
                this.mVideoProviderBinder.setCamera(cameraId, this.mCallingPackage, this.mTargetSdkVersion);
            } catch (RemoteException e) {
            }
        }

        public void setPreviewSurface(Surface surface) {
            try {
                this.mVideoProviderBinder.setPreviewSurface(surface);
            } catch (RemoteException e) {
            }
        }

        public void setDisplaySurface(Surface surface) {
            try {
                this.mVideoProviderBinder.setDisplaySurface(surface);
            } catch (RemoteException e) {
            }
        }

        public void setDeviceOrientation(int rotation) {
            try {
                this.mVideoProviderBinder.setDeviceOrientation(rotation);
            } catch (RemoteException e) {
            }
        }

        public void setZoom(float value) {
            try {
                this.mVideoProviderBinder.setZoom(value);
            } catch (RemoteException e) {
            }
        }

        public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) {
            try {
                this.mVideoProviderBinder.sendSessionModifyRequest(fromProfile, toProfile);
            } catch (RemoteException e) {
            }
        }

        public void sendSessionModifyResponse(VideoProfile responseProfile) {
            try {
                this.mVideoProviderBinder.sendSessionModifyResponse(responseProfile);
            } catch (RemoteException e) {
            }
        }

        public void requestCameraCapabilities() {
            try {
                this.mVideoProviderBinder.requestCameraCapabilities();
            } catch (RemoteException e) {
            }
        }

        public void requestCallDataUsage() {
            try {
                this.mVideoProviderBinder.requestCallDataUsage();
            } catch (RemoteException e) {
            }
        }

        public void setPauseImage(Uri uri) {
            try {
                this.mVideoProviderBinder.setPauseImage(uri);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteConnection(String id, IConnectionService connectionService, ConnectionRequest request) {
        ArrayList arrayList = new ArrayList();
        this.mConferenceableConnections = arrayList;
        this.mUnmodifiableconferenceableConnections = Collections.unmodifiableList(arrayList);
        this.mState = 1;
        this.mConnectionId = id;
        this.mConnectionService = connectionService;
        this.mConnected = true;
        this.mState = 0;
        if (request != null && request.getExtras() != null && request.getExtras().containsKey(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME)) {
            String callingPackage = request.getExtras().getString(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME);
            this.mCallingPackageAbbreviation = Log.getPackageAbbreviation(callingPackage);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteConnection(String callId, IConnectionService connectionService, ParcelableConnection connection, String callingPackage, int targetSdkVersion) {
        ArrayList arrayList = new ArrayList();
        this.mConferenceableConnections = arrayList;
        this.mUnmodifiableconferenceableConnections = Collections.unmodifiableList(arrayList);
        this.mState = 1;
        this.mConnectionId = callId;
        this.mConnectionService = connectionService;
        this.mConnected = true;
        this.mState = connection.getState();
        this.mDisconnectCause = connection.getDisconnectCause();
        this.mRingbackRequested = connection.isRingbackRequested();
        this.mConnectionCapabilities = connection.getConnectionCapabilities();
        this.mConnectionProperties = connection.getConnectionProperties();
        this.mVideoState = connection.getVideoState();
        IVideoProvider videoProvider = connection.getVideoProvider();
        if (videoProvider != null) {
            this.mVideoProvider = new VideoProvider(videoProvider, callingPackage, targetSdkVersion);
        } else {
            this.mVideoProvider = null;
        }
        this.mIsVoipAudioMode = connection.getIsVoipAudioMode();
        this.mStatusHints = connection.getStatusHints();
        this.mAddress = connection.getHandle();
        this.mAddressPresentation = connection.getHandlePresentation();
        this.mCallerDisplayName = connection.getCallerDisplayName();
        this.mCallerDisplayNamePresentation = connection.getCallerDisplayNamePresentation();
        this.mConference = null;
        putExtras(connection.getExtras());
        Bundle newExtras = new Bundle();
        newExtras.putString(Connection.EXTRA_ORIGINAL_CONNECTION_ID, callId);
        putExtras(newExtras);
        this.mCallingPackageAbbreviation = Log.getPackageAbbreviation(callingPackage);
    }

    RemoteConnection(DisconnectCause disconnectCause) {
        ArrayList arrayList = new ArrayList();
        this.mConferenceableConnections = arrayList;
        this.mUnmodifiableconferenceableConnections = Collections.unmodifiableList(arrayList);
        this.mState = 1;
        this.mConnectionId = "NULL";
        this.mConnected = false;
        this.mState = 6;
        this.mDisconnectCause = disconnectCause;
    }

    public void registerCallback(Callback callback) {
        registerCallback(callback, new Handler());
    }

    public void registerCallback(Callback callback, Handler handler) {
        unregisterCallback(callback);
        if (callback != null && handler != null) {
            this.mCallbackRecords.add(new CallbackRecord(callback, handler));
        }
    }

    public void unregisterCallback(Callback callback) {
        if (callback != null) {
            for (CallbackRecord record : this.mCallbackRecords) {
                if (record.getCallback() == callback) {
                    this.mCallbackRecords.remove(record);
                    return;
                }
            }
        }
    }

    public int getState() {
        return this.mState;
    }

    public DisconnectCause getDisconnectCause() {
        return this.mDisconnectCause;
    }

    public int getConnectionCapabilities() {
        return this.mConnectionCapabilities;
    }

    public int getConnectionProperties() {
        return this.mConnectionProperties;
    }

    public boolean isVoipAudioMode() {
        return this.mIsVoipAudioMode;
    }

    public StatusHints getStatusHints() {
        return this.mStatusHints;
    }

    public Uri getAddress() {
        return this.mAddress;
    }

    public int getAddressPresentation() {
        return this.mAddressPresentation;
    }

    public CharSequence getCallerDisplayName() {
        return this.mCallerDisplayName;
    }

    public int getCallerDisplayNamePresentation() {
        return this.mCallerDisplayNamePresentation;
    }

    public int getVideoState() {
        return this.mVideoState;
    }

    public final VideoProvider getVideoProvider() {
        return this.mVideoProvider;
    }

    public final Bundle getExtras() {
        return this.mExtras;
    }

    public boolean isRingbackRequested() {
        return this.mRingbackRequested;
    }

    public void abort() {
        Log.startSession("RC.a", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.abort(this.mConnectionId, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void answer() {
        Log.startSession("RC.an", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.answer(this.mConnectionId, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void answer(int videoState) {
        Log.startSession("RC.an2", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.answerVideo(this.mConnectionId, videoState, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void reject() {
        Log.startSession("RC.r", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.reject(this.mConnectionId, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void hold() {
        Log.startSession("RC.h", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.hold(this.mConnectionId, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void unhold() {
        Log.startSession("RC.u", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.unhold(this.mConnectionId, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void disconnect() {
        Log.startSession("RC.d", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.disconnect(this.mConnectionId, Log.getExternalSession(this.mCallingPackageAbbreviation));
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void playDtmfTone(char digit) {
        Log.startSession("RC.pDT", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.playDtmfTone(this.mConnectionId, digit, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void stopDtmfTone() {
        Log.startSession("RC.sDT", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.stopDtmfTone(this.mConnectionId, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void postDialContinue(boolean proceed) {
        Log.startSession("RC.pDC", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.onPostDialContinue(this.mConnectionId, proceed, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void pullExternalCall() {
        Log.startSession("RC.pEC", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.pullExternalCall(this.mConnectionId, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void addConferenceParticipants(List<Uri> participants) {
        try {
            if (this.mConnected) {
                this.mConnectionService.addConferenceParticipants(this.mConnectionId, participants, null);
            }
        } catch (RemoteException e) {
        }
    }

    @SystemApi
    @Deprecated
    public void setAudioState(AudioState state) {
        setCallAudioState(new CallAudioState(state));
    }

    public void setCallAudioState(CallAudioState state) {
        Log.startSession("RC.sCAS", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.onCallAudioStateChanged(this.mConnectionId, state, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void startRtt(Connection.RttTextStream rttTextStream) {
        Log.startSession("RC.sR", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.startRtt(this.mConnectionId, rttTextStream.getFdFromInCall(), rttTextStream.getFdToInCall(), null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void stopRtt() {
        Log.startSession("RC.stR", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.stopRtt(this.mConnectionId, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    @SystemApi
    public void onCallFilteringCompleted(Connection.CallFilteringCompletionInfo completionInfo) {
        Log.startSession("RC.oCFC", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                this.mConnectionService.onCallFilteringCompleted(this.mConnectionId, completionInfo, null);
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public void sendRttUpgradeResponse(Connection.RttTextStream rttTextStream) {
        Log.startSession("RC.sRUR", getActiveOwnerInfo());
        try {
            if (this.mConnected) {
                if (rttTextStream == null) {
                    this.mConnectionService.respondToRttUpgradeRequest(this.mConnectionId, null, null, null);
                } else {
                    this.mConnectionService.respondToRttUpgradeRequest(this.mConnectionId, rttTextStream.getFdFromInCall(), rttTextStream.getFdToInCall(), null);
                }
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Log.endSession();
            throw th;
        }
        Log.endSession();
    }

    public List<RemoteConnection> getConferenceableConnections() {
        return this.mUnmodifiableconferenceableConnections;
    }

    public RemoteConference getConference() {
        return this.mConference;
    }

    private String getActiveOwnerInfo() {
        Session.Info info = Log.getExternalSession();
        if (info == null) {
            return null;
        }
        return info.ownerInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getId() {
        return this.mConnectionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IConnectionService getConnectionService() {
        return this.mConnectionService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setState(final int state) {
        if (this.mState != state) {
            this.mState = state;
            for (CallbackRecord record : this.mCallbackRecords) {
                final Callback callback = record.getCallback();
                record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.1
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onStateChanged(connection, state);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDisconnected(final DisconnectCause disconnectCause) {
        if (this.mState != 6) {
            this.mState = 6;
            this.mDisconnectCause = disconnectCause;
            for (CallbackRecord record : this.mCallbackRecords) {
                final Callback callback = record.getCallback();
                record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.2
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onDisconnected(connection, disconnectCause);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRingbackRequested(final boolean ringback) {
        if (this.mRingbackRequested != ringback) {
            this.mRingbackRequested = ringback;
            for (CallbackRecord record : this.mCallbackRecords) {
                final Callback callback = record.getCallback();
                record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.3
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onRingbackRequested(connection, ringback);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConnectionCapabilities(final int connectionCapabilities) {
        this.mConnectionCapabilities = connectionCapabilities;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.4
                @Override // java.lang.Runnable
                public void run() {
                    callback.onConnectionCapabilitiesChanged(connection, connectionCapabilities);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConnectionProperties(final int connectionProperties) {
        this.mConnectionProperties = connectionProperties;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.5
                @Override // java.lang.Runnable
                public void run() {
                    callback.onConnectionPropertiesChanged(connection, connectionProperties);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDestroyed() {
        if (!this.mCallbackRecords.isEmpty()) {
            if (this.mState != 6) {
                setDisconnected(new DisconnectCause(1, "Connection destroyed."));
            }
            for (CallbackRecord record : this.mCallbackRecords) {
                final Callback callback = record.getCallback();
                record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.6
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onDestroyed(connection);
                    }
                });
            }
            this.mCallbackRecords.clear();
            this.mConnected = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPostDialWait(final String remainingDigits) {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.7
                @Override // java.lang.Runnable
                public void run() {
                    callback.onPostDialWait(connection, remainingDigits);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPostDialChar(final char nextChar) {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.8
                @Override // java.lang.Runnable
                public void run() {
                    callback.onPostDialChar(connection, nextChar);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVideoState(final int videoState) {
        this.mVideoState = videoState;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.9
                @Override // java.lang.Runnable
                public void run() {
                    callback.onVideoStateChanged(connection, videoState);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVideoProvider(final VideoProvider videoProvider) {
        this.mVideoProvider = videoProvider;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.10
                @Override // java.lang.Runnable
                public void run() {
                    callback.onVideoProviderChanged(connection, videoProvider);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIsVoipAudioMode(final boolean isVoip) {
        this.mIsVoipAudioMode = isVoip;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.11
                @Override // java.lang.Runnable
                public void run() {
                    callback.onVoipAudioChanged(connection, isVoip);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStatusHints(final StatusHints statusHints) {
        this.mStatusHints = statusHints;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.12
                @Override // java.lang.Runnable
                public void run() {
                    callback.onStatusHintsChanged(connection, statusHints);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAddress(final Uri address, final int presentation) {
        this.mAddress = address;
        this.mAddressPresentation = presentation;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.13
                @Override // java.lang.Runnable
                public void run() {
                    callback.onAddressChanged(connection, address, presentation);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCallerDisplayName(final String callerDisplayName, final int presentation) {
        this.mCallerDisplayName = callerDisplayName;
        this.mCallerDisplayNamePresentation = presentation;
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.14
                @Override // java.lang.Runnable
                public void run() {
                    callback.onCallerDisplayNameChanged(connection, callerDisplayName, presentation);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConferenceableConnections(List<RemoteConnection> conferenceableConnections) {
        this.mConferenceableConnections.clear();
        this.mConferenceableConnections.addAll(conferenceableConnections);
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.15
                @Override // java.lang.Runnable
                public void run() {
                    callback.onConferenceableConnectionsChanged(connection, RemoteConnection.this.mUnmodifiableconferenceableConnections);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConference(final RemoteConference conference) {
        if (this.mConference != conference) {
            this.mConference = conference;
            for (CallbackRecord record : this.mCallbackRecords) {
                final Callback callback = record.getCallback();
                record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.16
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onConferenceChanged(connection, conference);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putExtras(Bundle extras) {
        if (extras == null) {
            return;
        }
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        try {
            this.mExtras.putAll(extras);
        } catch (BadParcelableException bpe) {
            Log.m131w(this, "putExtras: could not unmarshal extras; exception = " + bpe, new Object[0]);
        }
        notifyExtrasChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeExtras(List<String> keys) {
        if (this.mExtras == null || keys == null || keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            this.mExtras.remove(key);
        }
        notifyExtrasChanged();
    }

    private void notifyExtrasChanged() {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.17
                @Override // java.lang.Runnable
                public void run() {
                    callback.onExtrasChanged(connection, RemoteConnection.this.mExtras);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onConnectionEvent(final String event, final Bundle extras) {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection.18
                @Override // java.lang.Runnable
                public void run() {
                    callback.onConnectionEvent(connection, event, extras);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRttInitiationSuccess() {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteConnection.Callback.this.onRttInitiationSuccess(connection);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRttInitiationFailure(final int reason) {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteConnection.Callback.this.onRttInitiationFailure(connection, reason);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRttSessionRemotelyTerminated() {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteConnection.Callback.this.onRttSessionRemotelyTerminated(connection);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRemoteRttRequest() {
        for (CallbackRecord record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.RemoteConnection$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteConnection.Callback.this.onRemoteRttRequest(connection);
                }
            });
        }
    }

    public static RemoteConnection failure(DisconnectCause disconnectCause) {
        return new RemoteConnection(disconnectCause);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class CallbackRecord extends Callback {
        private final Callback mCallback;
        private final Handler mHandler;

        public CallbackRecord(Callback callback, Handler handler) {
            this.mCallback = callback;
            this.mHandler = handler;
        }

        public Callback getCallback() {
            return this.mCallback;
        }

        public Handler getHandler() {
            return this.mHandler;
        }
    }
}
