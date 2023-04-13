package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.ims.internal.ConferenceParticipant;
import com.android.internal.telephony.DriverCall;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public abstract class Call {
    protected final String LOG_TAG = "Call";
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public State mState = State.IDLE;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ArrayList<Connection> mConnections = new ArrayList<>();
    private Object mLock = new Object();

    /* loaded from: classes.dex */
    public enum SrvccState {
        NONE,
        STARTED,
        COMPLETED,
        FAILED,
        CANCELED
    }

    public List<ConferenceParticipant> getConferenceParticipants() {
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public abstract Phone getPhone();

    @UnsupportedAppUsage
    public abstract void hangup() throws CallStateException;

    public abstract void hangup(int i) throws CallStateException;

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public abstract boolean isMultiparty();

    @UnsupportedAppUsage(implicitMember = "values()[Lcom/android/internal/telephony/Call$State;")
    /* loaded from: classes.dex */
    public enum State {
        IDLE,
        ACTIVE,
        HOLDING,
        DIALING,
        ALERTING,
        INCOMING,
        WAITING,
        DISCONNECTED,
        DISCONNECTING;

        @UnsupportedAppUsage
        public boolean isAlive() {
            return (this == IDLE || this == DISCONNECTED || this == DISCONNECTING) ? false : true;
        }

        @UnsupportedAppUsage
        public boolean isRinging() {
            return this == INCOMING || this == WAITING;
        }

        public boolean isDialing() {
            return this == DIALING || this == ALERTING;
        }
    }

    /* renamed from: com.android.internal.telephony.Call$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00001 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$DriverCall$State;

        static {
            int[] iArr = new int[DriverCall.State.values().length];
            $SwitchMap$com$android$internal$telephony$DriverCall$State = iArr;
            try {
                iArr[DriverCall.State.ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.HOLDING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.DIALING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.ALERTING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.INCOMING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.WAITING.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    public static State stateFromDCState(DriverCall.State state) {
        switch (C00001.$SwitchMap$com$android$internal$telephony$DriverCall$State[state.ordinal()]) {
            case 1:
                return State.ACTIVE;
            case 2:
                return State.HOLDING;
            case 3:
                return State.DIALING;
            case 4:
                return State.ALERTING;
            case 5:
                return State.INCOMING;
            case 6:
                return State.WAITING;
            default:
                throw new RuntimeException("illegal call state:" + state);
        }
    }

    @UnsupportedAppUsage
    public ArrayList<Connection> getConnections() {
        ArrayList<Connection> arrayList;
        synchronized (this.mLock) {
            arrayList = (ArrayList) this.mConnections.clone();
        }
        return arrayList;
    }

    public void copyConnectionFrom(Call call) {
        this.mConnections = call.getConnections();
    }

    public int getConnectionsCount() {
        int size;
        synchronized (this.mLock) {
            size = this.mConnections.size();
        }
        return size;
    }

    public String getConnectionSummary() {
        String str;
        synchronized (this.mLock) {
            str = (String) this.mConnections.stream().map(new Function() { // from class: com.android.internal.telephony.Call$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$getConnectionSummary$0;
                    lambda$getConnectionSummary$0 = Call.lambda$getConnectionSummary$0((Connection) obj);
                    return lambda$getConnectionSummary$0;
                }
            }).collect(Collectors.joining(", "));
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getConnectionSummary$0(Connection connection) {
        return connection.getTelecomCallId() + "/objId:" + System.identityHashCode(connection);
    }

    public boolean hasConnection(Connection connection) {
        return connection.getCall() == this;
    }

    public boolean hasConnections() {
        ArrayList<Connection> connections = getConnections();
        return connections != null && connections.size() > 0;
    }

    public void removeConnection(Connection connection) {
        synchronized (this.mLock) {
            this.mConnections.remove(connection);
        }
    }

    public void addConnection(Connection connection) {
        synchronized (this.mLock) {
            this.mConnections.add(connection);
        }
    }

    public void clearConnections() {
        synchronized (this.mLock) {
            this.mConnections.clear();
        }
    }

    @UnsupportedAppUsage
    public State getState() {
        return this.mState;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isIdle() {
        return !getState().isAlive();
    }

    @UnsupportedAppUsage
    public Connection getEarliestConnection() {
        ArrayList<Connection> connections = getConnections();
        Connection connection = null;
        if (connections.size() == 0) {
            return null;
        }
        int size = connections.size();
        long j = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            Connection connection2 = connections.get(i);
            long createTime = connection2.getCreateTime();
            if (createTime < j) {
                connection = connection2;
                j = createTime;
            }
        }
        return connection;
    }

    public long getEarliestCreateTime() {
        ArrayList<Connection> connections = getConnections();
        if (connections.size() == 0) {
            return 0L;
        }
        int size = connections.size();
        long j = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            long createTime = connections.get(i).getCreateTime();
            if (createTime < j) {
                j = createTime;
            }
        }
        return j;
    }

    public long getEarliestConnectTime() {
        ArrayList<Connection> connections = getConnections();
        if (connections.size() == 0) {
            return 0L;
        }
        int size = connections.size();
        long j = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            long connectTime = connections.get(i).getConnectTime();
            if (connectTime < j) {
                j = connectTime;
            }
        }
        return j;
    }

    public boolean isDialingOrAlerting() {
        return getState().isDialing();
    }

    public boolean isRinging() {
        return getState().isRinging();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Connection getLatestConnection() {
        ArrayList<Connection> connections = getConnections();
        Connection connection = null;
        if (connections.size() == 0) {
            return null;
        }
        int size = connections.size();
        long j = 0;
        for (int i = 0; i < size; i++) {
            Connection connection2 = connections.get(i);
            long createTime = connection2.getCreateTime();
            if (createTime > j) {
                connection = connection2;
                j = createTime;
            }
        }
        return connection;
    }

    public void hangupIfAlive() {
        if (getState().isAlive()) {
            try {
                hangup();
            } catch (CallStateException e) {
                Rlog.w("Call", " hangupIfActive: caught " + e);
            }
        }
    }

    public void clearDisconnected() {
        Iterator<Connection> it = getConnections().iterator();
        while (it.hasNext()) {
            Connection next = it.next();
            if (next.getState() == State.DISCONNECTED) {
                removeConnection(next);
            }
        }
        if (getConnectionsCount() == 0) {
            setState(State.IDLE);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setState(State state) {
        this.mState = state;
    }
}
