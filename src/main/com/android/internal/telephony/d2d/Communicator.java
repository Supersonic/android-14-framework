package com.android.internal.telephony.d2d;

import android.telecom.Log;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.d2d.TransportProtocol;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class Communicator implements TransportProtocol.Callback {
    public static final int AUDIO_CODEC_AMR_NB = 3;
    public static final int AUDIO_CODEC_AMR_WB = 2;
    public static final int AUDIO_CODEC_EVS = 1;
    public static final int BATTERY_STATE_CHARGING = 3;
    public static final int BATTERY_STATE_GOOD = 2;
    public static final int BATTERY_STATE_LOW = 1;
    public static final int COVERAGE_GOOD = 2;
    public static final int COVERAGE_POOR = 1;
    public static final int MESSAGE_CALL_AUDIO_CODEC = 2;
    public static final int MESSAGE_CALL_RADIO_ACCESS_TYPE = 1;
    public static final int MESSAGE_DEVICE_BATTERY_STATE = 3;
    public static final int MESSAGE_DEVICE_NETWORK_COVERAGE = 4;
    public static final int RADIO_ACCESS_TYPE_IWLAN = 2;
    public static final int RADIO_ACCESS_TYPE_LTE = 1;
    public static final int RADIO_ACCESS_TYPE_NR = 3;
    private TransportProtocol mActiveTransport;
    private Callback mCallback;
    private boolean mIsNegotiated;
    private boolean mIsNegotiationAttempted;
    private List<TransportProtocol> mTransportProtocols = new ArrayList();

    /* loaded from: classes.dex */
    public interface Callback {
        void onD2DAvailabilitychanged(boolean z);

        void onMessagesReceived(Set<Message> set);
    }

    public static String messageToString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? PhoneConfigurationManager.SSSS : "MESSAGE_DEVICE_NETWORK_COVERAGE" : "MESSAGE_DEVICE_BATTERY_STATE" : "MESSAGE_CALL_AUDIO_CODEC" : "MESSAGE_CALL_RADIO_ACCESS_TYPE";
    }

    public static String valueToString(int i, int i2) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? PhoneConfigurationManager.SSSS : i2 != 1 ? i2 != 2 ? PhoneConfigurationManager.SSSS : "COVERAGE_GOOD" : "COVERAGE_POOR" : i2 != 1 ? i2 != 2 ? i2 != 3 ? PhoneConfigurationManager.SSSS : "BATTERY_STATE_CHARGING" : "BATTERY_STATE_GOOD" : "BATTERY_STATE_LOW" : i2 != 1 ? i2 != 2 ? i2 != 3 ? PhoneConfigurationManager.SSSS : "AUDIO_CODEC_AMR_NB" : "AUDIO_CODEC_AMR_WB" : "AUDIO_CODEC_EVS" : i2 != 1 ? i2 != 2 ? i2 != 3 ? PhoneConfigurationManager.SSSS : "RADIO_ACCESS_TYPE_NR" : "RADIO_ACCESS_TYPE_IWLAN" : "RADIO_ACCESS_TYPE_LTE";
    }

    /* loaded from: classes.dex */
    public static class Message {
        private int mType;
        private int mValue;

        public Message(int i, int i2) {
            this.mType = i;
            this.mValue = i2;
        }

        public int getType() {
            return this.mType;
        }

        public int getValue() {
            return this.mValue;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Message message = (Message) obj;
            return this.mType == message.mType && this.mValue == message.mValue;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mType), Integer.valueOf(this.mValue));
        }

        public String toString() {
            return "Message{mType=" + Communicator.messageToString(this.mType) + ", mValue=" + Communicator.valueToString(this.mType, this.mValue) + '}';
        }
    }

    public Communicator(List<TransportProtocol> list, Callback callback) {
        Log.i(this, "Initializing communicator with transports: %s", new Object[]{list.stream().map(new Function() { // from class: com.android.internal.telephony.d2d.Communicator$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$new$0;
                lambda$new$0 = Communicator.lambda$new$0((TransportProtocol) obj);
                return lambda$new$0;
            }
        }).collect(Collectors.joining(","))});
        this.mTransportProtocols.addAll(list);
        this.mTransportProtocols.forEach(new Consumer() { // from class: com.android.internal.telephony.d2d.Communicator$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Communicator.this.lambda$new$1((TransportProtocol) obj);
            }
        });
        this.mCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$new$0(TransportProtocol transportProtocol) {
        return transportProtocol.getClass().getSimpleName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(TransportProtocol transportProtocol) {
        transportProtocol.setCallback(this);
    }

    public TransportProtocol getActiveTransport() {
        return this.mActiveTransport;
    }

    public void onStateChanged(String str, int i) {
        Log.i(this, "onStateChanged: id=%s, newState=%d", new Object[]{str, Integer.valueOf(i)});
        if (i == 4 && this.mActiveTransport == null && !this.mIsNegotiationAttempted) {
            this.mIsNegotiated = false;
            this.mIsNegotiationAttempted = true;
            Log.i(this, "onStateChanged: call active; negotiate D2D.", new Object[0]);
            negotiateNextProtocol();
        }
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol.Callback
    public void onNegotiationSuccess(TransportProtocol transportProtocol) {
        TransportProtocol transportProtocol2 = this.mActiveTransport;
        if (transportProtocol != transportProtocol2) {
            Log.w(this, "onNegotiationSuccess: ignored - %s negotiated but active transport is %s.", new Object[]{transportProtocol.getClass().getSimpleName(), transportProtocol2 == null ? "none" : transportProtocol2.getClass().getSimpleName()});
        }
        Log.i(this, "onNegotiationSuccess: %s negotiated; setting active.", new Object[]{transportProtocol.getClass().getSimpleName()});
        this.mIsNegotiated = true;
        notifyD2DStatus(true);
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol.Callback
    public void onNegotiationFailed(TransportProtocol transportProtocol) {
        Log.i(this, "onNegotiationFailed: %s failed to negotiate.", new Object[]{transportProtocol.getClass().getSimpleName()});
        this.mIsNegotiated = false;
        negotiateNextProtocol();
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol.Callback
    public void onMessagesReceived(Set<Message> set) {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onMessagesReceived(set);
        }
    }

    public void sendMessages(Set<Message> set) {
        if (this.mActiveTransport == null || !this.mIsNegotiated) {
            Log.w(this, "sendMessages: no active transport", new Object[0]);
            return;
        }
        Log.i(this, "sendMessages: msgs=%d, activeTransport=%s", new Object[]{Integer.valueOf(set.size()), this.mActiveTransport.getClass().getSimpleName()});
        this.mActiveTransport.sendMessages(set);
    }

    private void negotiateNextProtocol() {
        TransportProtocol nextCandidateProtocol = getNextCandidateProtocol();
        this.mActiveTransport = nextCandidateProtocol;
        if (nextCandidateProtocol == null) {
            Log.i(this, "negotiateNextProtocol: no remaining transports.", new Object[0]);
            notifyD2DStatus(false);
            return;
        }
        Log.i(this, "negotiateNextProtocol: trying %s", new Object[]{nextCandidateProtocol.getClass().getSimpleName()});
        this.mActiveTransport.startNegotiation();
    }

    private TransportProtocol getNextCandidateProtocol() {
        int i = 0;
        if (this.mActiveTransport != null) {
            while (true) {
                if (i >= this.mTransportProtocols.size()) {
                    break;
                } else if (this.mTransportProtocols.get(i) == this.mActiveTransport) {
                    int i2 = i + 1;
                    if (i2 < this.mTransportProtocols.size()) {
                        return this.mTransportProtocols.get(i2);
                    }
                } else {
                    i++;
                }
            }
        } else if (this.mTransportProtocols.size() > 0) {
            return this.mTransportProtocols.get(0);
        } else {
            this.mIsNegotiated = false;
        }
        return null;
    }

    private void notifyD2DStatus(boolean z) {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onD2DAvailabilitychanged(z);
        }
    }

    public void setTransportActive(final String str) {
        final Optional<TransportProtocol> findFirst = this.mTransportProtocols.stream().filter(new Predicate() { // from class: com.android.internal.telephony.d2d.Communicator$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setTransportActive$2;
                lambda$setTransportActive$2 = Communicator.lambda$setTransportActive$2(str, (TransportProtocol) obj);
                return lambda$setTransportActive$2;
            }
        }).findFirst();
        if (!findFirst.isPresent()) {
            Log.w(this, "setTransportActive: %s is not a valid transport.", new Object[0]);
            return;
        }
        this.mTransportProtocols.stream().filter(new Predicate() { // from class: com.android.internal.telephony.d2d.Communicator$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setTransportActive$3;
                lambda$setTransportActive$3 = Communicator.lambda$setTransportActive$3(findFirst, (TransportProtocol) obj);
                return lambda$setTransportActive$3;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.d2d.Communicator$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TransportProtocol) obj).forceNotNegotiated();
            }
        });
        findFirst.get().forceNegotiated();
        this.mActiveTransport = findFirst.get();
        this.mIsNegotiated = true;
        Log.i(this, "setTransportActive: %s has been forced active.", new Object[]{str});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setTransportActive$2(String str, TransportProtocol transportProtocol) {
        return transportProtocol.getClass().getSimpleName().equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setTransportActive$3(Optional optional, TransportProtocol transportProtocol) {
        return transportProtocol != optional.get();
    }

    public List<TransportProtocol> getTransportProtocols() {
        return this.mTransportProtocols;
    }
}
