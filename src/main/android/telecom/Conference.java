package android.telecom;

import android.annotation.SystemApi;
import android.hardware.gnss.GnssSignalType;
import android.net.Uri;
import android.p008os.Bundle;
import android.telecom.Call;
import android.telecom.Connection;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes3.dex */
public abstract class Conference extends Conferenceable {
    public static final long CONNECT_TIME_NOT_SPECIFIED = 0;
    private Uri mAddress;
    private int mAddressPresentation;
    private CallAudioState mCallAudioState;
    private int mCallDirection;
    private CallEndpoint mCallEndpoint;
    private String mCallerDisplayName;
    private int mCallerDisplayNamePresentation;
    private final List<Connection> mChildConnections;
    private final List<Connection> mConferenceableConnections;
    private long mConnectTimeMillis;
    private int mConnectionCapabilities;
    private final Connection.Listener mConnectionDeathListener;
    private int mConnectionProperties;
    private long mConnectionStartElapsedRealTime;
    private DisconnectCause mDisconnectCause;
    private String mDisconnectMessage;
    private Bundle mExtras;
    private final Object mExtrasLock;
    private boolean mIsMultiparty;
    private final Set<Listener> mListeners = new CopyOnWriteArraySet();
    private PhoneAccountHandle mPhoneAccount;
    private Set<String> mPreviousExtraKeys;
    private boolean mRingbackRequested;
    private int mState;
    private StatusHints mStatusHints;
    private String mTelecomCallId;
    private final List<Connection> mUnmodifiableChildConnections;
    private final List<Connection> mUnmodifiableConferenceableConnections;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static abstract class Listener {
        public void onStateChanged(Conference conference, int oldState, int newState) {
        }

        public void onDisconnected(Conference conference, DisconnectCause disconnectCause) {
        }

        public void onConnectionAdded(Conference conference, Connection connection) {
        }

        public void onConnectionRemoved(Conference conference, Connection connection) {
        }

        public void onConferenceableConnectionsChanged(Conference conference, List<Connection> conferenceableConnections) {
        }

        public void onDestroyed(Conference conference) {
        }

        public void onConnectionCapabilitiesChanged(Conference conference, int connectionCapabilities) {
        }

        public void onConnectionPropertiesChanged(Conference conference, int connectionProperties) {
        }

        public void onVideoStateChanged(Conference c, int videoState) {
        }

        public void onVideoProviderChanged(Conference c, Connection.VideoProvider videoProvider) {
        }

        public void onStatusHintsChanged(Conference conference, StatusHints statusHints) {
        }

        public void onExtrasChanged(Conference c, Bundle extras) {
        }

        public void onExtrasRemoved(Conference c, List<String> keys) {
        }

        public void onConferenceStateChanged(Conference c, boolean isConference) {
        }

        public void onAddressChanged(Conference c, Uri newAddress, int presentation) {
        }

        public void onConnectionEvent(Conference c, String event, Bundle extras) {
        }

        public void onCallerDisplayNameChanged(Conference c, String callerDisplayName, int presentation) {
        }

        public void onCallDirectionChanged(Conference c, int callDirection) {
        }

        public void onRingbackRequested(Conference c, boolean ringback) {
        }
    }

    public Conference(PhoneAccountHandle phoneAccount) {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        this.mChildConnections = copyOnWriteArrayList;
        this.mUnmodifiableChildConnections = Collections.unmodifiableList(copyOnWriteArrayList);
        ArrayList arrayList = new ArrayList();
        this.mConferenceableConnections = arrayList;
        this.mUnmodifiableConferenceableConnections = Collections.unmodifiableList(arrayList);
        this.mState = 1;
        this.mConnectTimeMillis = 0L;
        this.mConnectionStartElapsedRealTime = 0L;
        this.mExtrasLock = new Object();
        this.mRingbackRequested = false;
        this.mIsMultiparty = true;
        this.mConnectionDeathListener = new Connection.Listener() { // from class: android.telecom.Conference.1
            @Override // android.telecom.Connection.Listener
            public void onDestroyed(Connection c) {
                if (Conference.this.mConferenceableConnections.remove(c)) {
                    Conference.this.fireOnConferenceableConnectionsChanged();
                }
            }
        };
        this.mPhoneAccount = phoneAccount;
    }

    @SystemApi
    public final String getTelecomCallId() {
        return this.mTelecomCallId;
    }

    public final void setTelecomCallId(String telecomCallId) {
        this.mTelecomCallId = telecomCallId;
    }

    public final PhoneAccountHandle getPhoneAccountHandle() {
        return this.mPhoneAccount;
    }

    public final List<Connection> getConnections() {
        return this.mUnmodifiableChildConnections;
    }

    public final int getState() {
        return this.mState;
    }

    public final boolean isRingbackRequested() {
        return this.mRingbackRequested;
    }

    public final int getConnectionCapabilities() {
        return this.mConnectionCapabilities;
    }

    public final int getConnectionProperties() {
        return this.mConnectionProperties;
    }

    @SystemApi
    @Deprecated
    public final AudioState getAudioState() {
        return new AudioState(this.mCallAudioState);
    }

    @Deprecated
    public final CallAudioState getCallAudioState() {
        return this.mCallAudioState;
    }

    public final CallEndpoint getCurrentCallEndpoint() {
        return this.mCallEndpoint;
    }

    public Connection.VideoProvider getVideoProvider() {
        return null;
    }

    public int getVideoState() {
        return 0;
    }

    public void onDisconnect() {
    }

    public void onSeparate(Connection connection) {
    }

    public void onMerge(Connection connection) {
    }

    public void onHold() {
    }

    public void onUnhold() {
    }

    public void onMerge() {
    }

    public void onSwap() {
    }

    public void onPlayDtmfTone(char c) {
    }

    public void onStopDtmfTone() {
    }

    @SystemApi
    @Deprecated
    public void onAudioStateChanged(AudioState state) {
    }

    @Deprecated
    public void onCallAudioStateChanged(CallAudioState state) {
    }

    public void onCallEndpointChanged(CallEndpoint callEndpoint) {
    }

    public void onAvailableCallEndpointsChanged(List<CallEndpoint> availableEndpoints) {
    }

    public void onMuteStateChanged(boolean isMuted) {
    }

    public void onConnectionAdded(Connection connection) {
    }

    public void onAddConferenceParticipants(List<Uri> participants) {
    }

    public void onAnswer(int videoState) {
    }

    public final void onAnswer() {
        onAnswer(0);
    }

    public void onReject() {
    }

    public final void setOnHold() {
        setState(5);
    }

    public final void setDialing() {
        setState(3);
    }

    public final void setRinging() {
        setState(2);
    }

    public final void setActive() {
        setRingbackRequested(false);
        setState(4);
    }

    public final void setDisconnected(DisconnectCause disconnectCause) {
        this.mDisconnectCause = disconnectCause;
        setState(6);
        for (Listener l : this.mListeners) {
            l.onDisconnected(this, this.mDisconnectCause);
        }
    }

    public final DisconnectCause getDisconnectCause() {
        return this.mDisconnectCause;
    }

    public final void setConnectionCapabilities(int connectionCapabilities) {
        if (connectionCapabilities != this.mConnectionCapabilities) {
            this.mConnectionCapabilities = connectionCapabilities;
            for (Listener l : this.mListeners) {
                l.onConnectionCapabilitiesChanged(this, this.mConnectionCapabilities);
            }
        }
    }

    public final void setConnectionProperties(int connectionProperties) {
        if (connectionProperties != this.mConnectionProperties) {
            this.mConnectionProperties = connectionProperties;
            for (Listener l : this.mListeners) {
                l.onConnectionPropertiesChanged(this, this.mConnectionProperties);
            }
        }
    }

    public final boolean addConnection(Connection connection) {
        Log.m139d(this, "Connection=%s, connection=", connection);
        if (connection != null && !this.mChildConnections.contains(connection) && connection.setConference(this)) {
            this.mChildConnections.add(connection);
            onConnectionAdded(connection);
            for (Listener l : this.mListeners) {
                l.onConnectionAdded(this, connection);
            }
            return true;
        }
        return false;
    }

    public final void removeConnection(Connection connection) {
        Log.m139d(this, "removing %s from %s", connection, this.mChildConnections);
        if (connection != null && this.mChildConnections.remove(connection)) {
            connection.resetConference();
            for (Listener l : this.mListeners) {
                l.onConnectionRemoved(this, connection);
            }
        }
    }

    public final void setConferenceableConnections(List<Connection> conferenceableConnections) {
        clearConferenceableList();
        for (Connection c : conferenceableConnections) {
            if (!this.mConferenceableConnections.contains(c)) {
                c.addConnectionListener(this.mConnectionDeathListener);
                this.mConferenceableConnections.add(c);
            }
        }
        fireOnConferenceableConnectionsChanged();
    }

    public final void setRingbackRequested(boolean ringback) {
        if (this.mRingbackRequested != ringback) {
            this.mRingbackRequested = ringback;
            for (Listener l : this.mListeners) {
                l.onRingbackRequested(this, ringback);
            }
        }
    }

    public final void setVideoState(Connection c, int videoState) {
        Log.m139d(this, "setVideoState Conference: %s Connection: %s VideoState: %s", this, c, Integer.valueOf(videoState));
        for (Listener l : this.mListeners) {
            l.onVideoStateChanged(this, videoState);
        }
    }

    public final void setVideoProvider(Connection c, Connection.VideoProvider videoProvider) {
        Log.m139d(this, "setVideoProvider Conference: %s Connection: %s VideoState: %s", this, c, videoProvider);
        for (Listener l : this.mListeners) {
            l.onVideoProviderChanged(this, videoProvider);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void fireOnConferenceableConnectionsChanged() {
        for (Listener l : this.mListeners) {
            l.onConferenceableConnectionsChanged(this, getConferenceableConnections());
        }
    }

    public final List<Connection> getConferenceableConnections() {
        return this.mUnmodifiableConferenceableConnections;
    }

    public final void destroy() {
        Log.m139d(this, "destroying conference : %s", this);
        for (Connection connection : this.mChildConnections) {
            Log.m139d(this, "removing connection %s", connection);
            removeConnection(connection);
        }
        if (this.mState != 6) {
            Log.m139d(this, "setting to disconnected", new Object[0]);
            setDisconnected(new DisconnectCause(2));
        }
        for (Listener l : this.mListeners) {
            l.onDestroyed(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Conference addListener(Listener listener) {
        this.mListeners.add(listener);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Conference removeListener(Listener listener) {
        this.mListeners.remove(listener);
        return this;
    }

    @SystemApi
    public Connection getPrimaryConnection() {
        List<Connection> list = this.mUnmodifiableChildConnections;
        if (list == null || list.isEmpty()) {
            return null;
        }
        return this.mUnmodifiableChildConnections.get(0);
    }

    @SystemApi
    @Deprecated
    public final void setConnectTimeMillis(long connectTimeMillis) {
        setConnectionTime(connectTimeMillis);
    }

    public final void setConnectionTime(long connectionTimeMillis) {
        this.mConnectTimeMillis = connectionTimeMillis;
    }

    @Deprecated
    public final void setConnectionStartElapsedRealTime(long connectionStartElapsedRealTime) {
        setConnectionStartElapsedRealtimeMillis(connectionStartElapsedRealTime);
    }

    public final void setConnectionStartElapsedRealtimeMillis(long connectionStartElapsedRealTime) {
        this.mConnectionStartElapsedRealTime = connectionStartElapsedRealTime;
    }

    @SystemApi
    @Deprecated
    public final long getConnectTimeMillis() {
        return getConnectionTime();
    }

    public final long getConnectionTime() {
        return this.mConnectTimeMillis;
    }

    public final long getConnectionStartElapsedRealtimeMillis() {
        return this.mConnectionStartElapsedRealTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setCallAudioState(CallAudioState state) {
        Log.m139d(this, "setCallAudioState %s", state);
        this.mCallAudioState = state;
        onAudioStateChanged(getAudioState());
        onCallAudioStateChanged(state);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setCallEndpoint(CallEndpoint endpoint) {
        Log.m139d(this, "setCallEndpoint %s", endpoint);
        this.mCallEndpoint = endpoint;
        onCallEndpointChanged(endpoint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setAvailableCallEndpoints(List<CallEndpoint> availableEndpoints) {
        Log.m139d(this, "setAvailableCallEndpoints", new Object[0]);
        onAvailableCallEndpointsChanged(availableEndpoints);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setMuteState(boolean isMuted) {
        Log.m139d(this, "setMuteState %s", Boolean.valueOf(isMuted));
        onMuteStateChanged(isMuted);
    }

    private void setState(int newState) {
        if (this.mState != newState) {
            int oldState = this.mState;
            this.mState = newState;
            for (Listener l : this.mListeners) {
                l.onStateChanged(this, oldState, newState);
            }
        }
    }

    /* loaded from: classes3.dex */
    private static class FailureSignalingConference extends Conference {
        private boolean mImmutable;

        public FailureSignalingConference(DisconnectCause disconnectCause, PhoneAccountHandle phoneAccount) {
            super(phoneAccount);
            this.mImmutable = false;
            setDisconnected(disconnectCause);
            this.mImmutable = true;
        }

        public void checkImmutable() {
            if (this.mImmutable) {
                throw new UnsupportedOperationException("Conference is immutable");
            }
        }
    }

    public static Conference createFailedConference(DisconnectCause disconnectCause, PhoneAccountHandle phoneAccount) {
        return new FailureSignalingConference(disconnectCause, phoneAccount);
    }

    private final void clearConferenceableList() {
        for (Connection c : this.mConferenceableConnections) {
            c.removeConnectionListener(this.mConnectionDeathListener);
        }
        this.mConferenceableConnections.clear();
    }

    public String toString() {
        Locale locale = Locale.US;
        Object[] objArr = new Object[6];
        objArr[0] = Connection.stateToString(this.mState);
        objArr[1] = Call.Details.capabilitiesToString(this.mConnectionCapabilities);
        objArr[2] = Integer.valueOf(getVideoState());
        objArr[3] = getVideoProvider();
        objArr[4] = isRingbackRequested() ? GnssSignalType.CODE_TYPE_Y : GnssSignalType.CODE_TYPE_N;
        objArr[5] = super.toString();
        return String.format(locale, "[State: %s,Capabilites: %s, VideoState: %s, VideoProvider: %s,isRingbackRequested: %s, ThisObject %s]", objArr);
    }

    public final void setStatusHints(StatusHints statusHints) {
        this.mStatusHints = statusHints;
        for (Listener l : this.mListeners) {
            l.onStatusHintsChanged(this, statusHints);
        }
    }

    public final StatusHints getStatusHints() {
        return this.mStatusHints;
    }

    public final void setExtras(Bundle extras) {
        synchronized (this.mExtrasLock) {
            putExtras(extras);
            if (this.mPreviousExtraKeys != null) {
                List<String> toRemove = new ArrayList<>();
                for (String oldKey : this.mPreviousExtraKeys) {
                    if (extras == null || !extras.containsKey(oldKey)) {
                        toRemove.add(oldKey);
                    }
                }
                if (!toRemove.isEmpty()) {
                    removeExtras(toRemove);
                }
            }
            if (this.mPreviousExtraKeys == null) {
                this.mPreviousExtraKeys = new ArraySet();
            }
            this.mPreviousExtraKeys.clear();
            if (extras != null) {
                this.mPreviousExtraKeys.addAll(extras.keySet());
            }
        }
    }

    public final void putExtras(Bundle extras) {
        Bundle listenersBundle;
        if (extras == null) {
            return;
        }
        synchronized (this.mExtrasLock) {
            if (this.mExtras == null) {
                this.mExtras = new Bundle();
            }
            this.mExtras.putAll(extras);
            listenersBundle = new Bundle(this.mExtras);
        }
        for (Listener l : this.mListeners) {
            l.onExtrasChanged(this, new Bundle(listenersBundle));
        }
    }

    public final void putExtra(String key, boolean value) {
        Bundle newExtras = new Bundle();
        newExtras.putBoolean(key, value);
        putExtras(newExtras);
    }

    public final void putExtra(String key, int value) {
        Bundle newExtras = new Bundle();
        newExtras.putInt(key, value);
        putExtras(newExtras);
    }

    public final void putExtra(String key, String value) {
        Bundle newExtras = new Bundle();
        newExtras.putString(key, value);
        putExtras(newExtras);
    }

    public final void removeExtras(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        synchronized (this.mExtrasLock) {
            if (this.mExtras != null) {
                for (String key : keys) {
                    this.mExtras.remove(key);
                }
            }
        }
        List<String> unmodifiableKeys = Collections.unmodifiableList(keys);
        for (Listener l : this.mListeners) {
            l.onExtrasRemoved(this, unmodifiableKeys);
        }
    }

    public final void removeExtras(String... keys) {
        removeExtras(Arrays.asList(keys));
    }

    public final Bundle getExtras() {
        return this.mExtras;
    }

    public void onExtrasChanged(Bundle extras) {
    }

    @SystemApi
    public void setConferenceState(boolean isConference) {
        this.mIsMultiparty = isConference;
        for (Listener l : this.mListeners) {
            l.onConferenceStateChanged(this, isConference);
        }
    }

    public final void setCallDirection(int callDirection) {
        Log.m139d(this, "setDirection %d", Integer.valueOf(callDirection));
        this.mCallDirection = callDirection;
        for (Listener l : this.mListeners) {
            l.onCallDirectionChanged(this, callDirection);
        }
    }

    public boolean isMultiparty() {
        return this.mIsMultiparty;
    }

    @SystemApi
    public final void setAddress(Uri address, int presentation) {
        Log.m139d(this, "setAddress %s", address);
        this.mAddress = address;
        this.mAddressPresentation = presentation;
        for (Listener l : this.mListeners) {
            l.onAddressChanged(this, address, presentation);
        }
    }

    public final Uri getAddress() {
        return this.mAddress;
    }

    public final int getAddressPresentation() {
        return this.mAddressPresentation;
    }

    public final String getCallerDisplayName() {
        return this.mCallerDisplayName;
    }

    public final int getCallerDisplayNamePresentation() {
        return this.mCallerDisplayNamePresentation;
    }

    public final int getCallDirection() {
        return this.mCallDirection;
    }

    @SystemApi
    public final void setCallerDisplayName(String callerDisplayName, int presentation) {
        Log.m139d(this, "setCallerDisplayName %s", callerDisplayName);
        this.mCallerDisplayName = callerDisplayName;
        this.mCallerDisplayNamePresentation = presentation;
        for (Listener l : this.mListeners) {
            l.onCallerDisplayNameChanged(this, callerDisplayName, presentation);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void handleExtrasChanged(Bundle extras) {
        Bundle b = null;
        synchronized (this.mExtrasLock) {
            this.mExtras = extras;
            if (extras != null) {
                b = new Bundle(this.mExtras);
            }
        }
        onExtrasChanged(b);
    }

    public void sendConferenceEvent(String event, Bundle extras) {
        for (Listener l : this.mListeners) {
            l.onConnectionEvent(this, event, extras);
        }
    }
}
