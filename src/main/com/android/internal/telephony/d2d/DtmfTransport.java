package com.android.internal.telephony.d2d;

import android.telecom.Log;
import android.util.ArraySet;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.BiMap;
import com.android.internal.telephony.d2d.Communicator;
import com.android.internal.telephony.d2d.Timeouts;
import com.android.internal.telephony.d2d.TransportProtocol;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DtmfTransport implements TransportProtocol {
    public static final String DMTF_PROBE_MESSAGE = "AAD";
    public static final String DMTF_PROTOCOL_VERSION = "A";
    public static final String DTMF_MESSAGE_BATERY = "C";
    public static final String DTMF_MESSAGE_BATTERY_CHARGING = "C";
    public static final String DTMF_MESSAGE_BATTERY_GOOD = "B";
    public static final String DTMF_MESSAGE_BATTERY_LOW = "A";
    public static final String DTMF_MESSAGE_CODEC = "B";
    public static final String DTMF_MESSAGE_CODEC_AMR_NB = "C";
    public static final String DTMF_MESSAGE_CODEC_AMR_WB = "B";
    public static final String DTMF_MESSAGE_CODEC_EVS = "A";
    public static final char DTMF_MESSAGE_DELIMITER = 'D';
    public static final String DTMF_MESSAGE_RAT = "A";
    public static final String DTMF_MESSAGE_RAT_IWLAN = "B";
    public static final String DTMF_MESSAGE_RAT_LTE = "A";
    public static final String DTMF_MESSAGE_RAT_NR = "C";
    public static final String DTMF_MESSAGE_SERVICE = "AA";
    public static final String DTMF_MESSAGE_SERVICE_GOOD = "A";
    public static final String DTMF_MESSAGE_SERVICE_POOR = "B";
    public static final char DTMF_MESSAGE_START = 'A';
    public static final BiMap<Pair<String, String>, Communicator.Message> DTMF_TO_MESSAGE;
    public static final int RECEIVE_STATE_IDLE = 0;
    public static final int RECEIVE_STATE_MESSAGE_TYPE = 1;
    public static final int RECEIVE_STATE_MESSAGE_VALUE = 2;
    public static final int STATE_IDLE = 0;
    public static final int STATE_NEGOTIATED = 2;
    public static final int STATE_NEGOTIATING = 1;
    public static final int STATE_NEGOTIATION_FAILED = 3;
    private TransportProtocol.Callback mCallback;
    private ScheduledFuture<?> mDigitSendScheduledFuture;
    private final DtmfAdapter mDtmfAdapter;
    private final long mDtmfDurationFuzzMillis;
    private ScheduledFuture<?> mDtmfMessageTimeoutFuture;
    private final long mDurationOfDtmfMessageMillis;
    private final long mIntervalBetweenDigitsMillis;
    private char[] mMessageToSend;
    private ScheduledFuture<?> mNegotiationFuture;
    private final long mNegotiationTimeoutMillis;
    private String mProtocolVersion;
    private final ScheduledExecutorService mScheduledExecutorService;
    private int mTransportState = 0;
    private StringBuffer mProbeDigits = new StringBuffer();
    private int mMessageReceiveState = 0;
    private StringBuffer mMessageTypeDigits = new StringBuffer();
    private StringBuffer mMessageValueDigits = new StringBuffer();
    private final ConcurrentLinkedQueue<char[]> mPendingMessages = new ConcurrentLinkedQueue<>();
    private Object mProbeLock = new Object();
    private Object mDtmfMessageTimeoutLock = new Object();
    private Object mDigitSendLock = new Object();
    private Object mNegotiationLock = new Object();
    private Object mDigitsLock = new Object();
    private int mCharToSend = 0;
    private Random mRandom = new Random();

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void forceNegotiated() {
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void forceNotNegotiated() {
    }

    static {
        BiMap<Pair<String, String>, Communicator.Message> biMap = new BiMap<>();
        DTMF_TO_MESSAGE = biMap;
        biMap.put(new Pair<>("A", "A"), new Communicator.Message(1, 1));
        biMap.put(new Pair<>("A", "B"), new Communicator.Message(1, 2));
        biMap.put(new Pair<>("A", "C"), new Communicator.Message(1, 3));
        biMap.put(new Pair<>("B", "A"), new Communicator.Message(2, 1));
        biMap.put(new Pair<>("B", "B"), new Communicator.Message(2, 2));
        biMap.put(new Pair<>("B", "C"), new Communicator.Message(2, 3));
        biMap.put(new Pair<>("C", "A"), new Communicator.Message(3, 1));
        biMap.put(new Pair<>("C", "B"), new Communicator.Message(3, 2));
        biMap.put(new Pair<>("C", "C"), new Communicator.Message(3, 3));
        biMap.put(new Pair<>(DTMF_MESSAGE_SERVICE, "A"), new Communicator.Message(4, 2));
        biMap.put(new Pair<>(DTMF_MESSAGE_SERVICE, "B"), new Communicator.Message(4, 1));
    }

    public DtmfTransport(DtmfAdapter dtmfAdapter, Timeouts.Adapter adapter, ScheduledExecutorService scheduledExecutorService) {
        this.mDtmfAdapter = dtmfAdapter;
        this.mIntervalBetweenDigitsMillis = adapter.getDtmfMinimumIntervalMillis();
        this.mDurationOfDtmfMessageMillis = adapter.getMaxDurationOfDtmfMessageMillis();
        this.mDtmfDurationFuzzMillis = adapter.getDtmfDurationFuzzMillis();
        this.mNegotiationTimeoutMillis = adapter.getDtmfNegotiationTimeoutMillis();
        this.mScheduledExecutorService = scheduledExecutorService;
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void setCallback(TransportProtocol.Callback callback) {
        this.mCallback = callback;
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void startNegotiation() {
        if (this.mTransportState != 0) {
            Log.w(this, "startNegotiation: can't start negotiation as not idle.", new Object[0]);
            return;
        }
        this.mTransportState = 1;
        Log.i(this, "startNegotiation: starting negotiation.", new Object[0]);
        this.mPendingMessages.offer(DMTF_PROBE_MESSAGE.toCharArray());
        maybeScheduleMessageSend();
        scheduleNegotiationTimeout();
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void sendMessages(Set<Communicator.Message> set) {
        for (Communicator.Message message : set) {
            char[] messageDigits = getMessageDigits(message);
            if (messageDigits != null) {
                Log.i(this, "sendMessages: queueing message: %s", new Object[]{String.valueOf(messageDigits)});
                this.mPendingMessages.offer(messageDigits);
            }
        }
        if (this.mPendingMessages.size() > 0) {
            maybeScheduleMessageSend();
        }
    }

    private void maybeScheduleMessageSend() {
        synchronized (this.mDigitSendLock) {
            if (this.mMessageToSend == null && this.mDigitSendScheduledFuture == null) {
                char[] poll = this.mPendingMessages.poll();
                this.mMessageToSend = poll;
                this.mCharToSend = 0;
                if (poll != null) {
                    Log.i(this, "maybeScheduleMessageSend: toSend=%s", new Object[]{String.valueOf(poll)});
                    this.mDigitSendScheduledFuture = this.mScheduledExecutorService.scheduleAtFixedRate(new Runnable() { // from class: com.android.internal.telephony.d2d.DtmfTransport$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            DtmfTransport.this.lambda$maybeScheduleMessageSend$0();
                        }
                    }, this.mDurationOfDtmfMessageMillis + getDtmfDurationFuzzMillis(), this.mIntervalBetweenDigitsMillis, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    private long getDtmfDurationFuzzMillis() {
        if (this.mDtmfDurationFuzzMillis == 0) {
            return 0L;
        }
        return this.mRandom.nextLong() % this.mDtmfDurationFuzzMillis;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleDtmfSend */
    public void lambda$maybeScheduleMessageSend$0() {
        int i = this.mCharToSend;
        char[] cArr = this.mMessageToSend;
        if (i < cArr.length) {
            if (this.mDtmfAdapter != null) {
                Log.i(this, "handleDtmfSend: char=%c", new Object[]{Character.valueOf(cArr[i])});
                this.mDtmfAdapter.sendDtmf(this.mMessageToSend[this.mCharToSend]);
            }
            int i2 = this.mCharToSend + 1;
            this.mCharToSend = i2;
            if (i2 == this.mMessageToSend.length) {
                Log.i(this, "handleDtmfSend: done", new Object[0]);
                synchronized (this.mDigitSendLock) {
                    this.mMessageToSend = null;
                    this.mDigitSendScheduledFuture.cancel(false);
                    this.mDigitSendScheduledFuture = null;
                    if (this.mTransportState == 2) {
                        maybeScheduleMessageSend();
                    }
                }
            }
        }
    }

    public int getTransportState() {
        return this.mTransportState;
    }

    public void onDtmfReceived(char c) {
        if (c < 'A' || c > 'D') {
            Log.i(this, "onDtmfReceived: digit = %c ; invalid digit; not in A-D", new Object[0]);
        } else if (this.mTransportState == 1) {
            synchronized (this.mProbeLock) {
                this.mProbeDigits.append(c);
            }
            if (c == 'D') {
                Log.i(this, "onDtmfReceived: received message %s", new Object[]{this.mProbeDigits});
                handleProbeMessage();
            }
        } else {
            handleReceivedDigit(c);
        }
    }

    private void handleProbeMessage() {
        String stringBuffer;
        synchronized (this.mProbeLock) {
            stringBuffer = this.mProbeDigits.toString();
            if (this.mProbeDigits.length() > 0) {
                StringBuffer stringBuffer2 = this.mProbeDigits;
                stringBuffer2.delete(0, stringBuffer2.length());
            }
        }
        cancelNegotiationTimeout();
        if (stringBuffer.startsWith(String.valueOf((char) DTMF_MESSAGE_START)) && stringBuffer.endsWith(String.valueOf((char) DTMF_MESSAGE_DELIMITER)) && stringBuffer.length() > 2) {
            this.mProtocolVersion = stringBuffer.substring(1, stringBuffer.length() - 1);
            Log.i(this, "handleProbeMessage: got valid probe, remote version %s negotiated.", new Object[]{stringBuffer});
            negotiationSucceeded();
        } else {
            Log.i(this, "handleProbeMessage: got invalid probe %s - negotiation failed.", new Object[]{stringBuffer});
            negotiationFailed();
        }
        cancelNegotiationTimeout();
    }

    private void scheduleNegotiationTimeout() {
        synchronized (this.mNegotiationLock) {
            this.mNegotiationFuture = this.mScheduledExecutorService.schedule(new Runnable() { // from class: com.android.internal.telephony.d2d.DtmfTransport$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DtmfTransport.this.lambda$scheduleNegotiationTimeout$1();
                }
            }, this.mNegotiationTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void cancelNegotiationTimeout() {
        Log.i(this, "cancelNegotiationTimeout", new Object[0]);
        synchronized (this.mNegotiationLock) {
            ScheduledFuture<?> scheduledFuture = this.mNegotiationFuture;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            this.mNegotiationFuture = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleNegotiationTimeout */
    public void lambda$scheduleNegotiationTimeout$1() {
        Log.i(this, "handleNegotiationTimeout: no probe received, negotiation timeout.", new Object[0]);
        synchronized (this.mNegotiationLock) {
            this.mNegotiationFuture = null;
        }
        negotiationFailed();
    }

    private void negotiationFailed() {
        this.mTransportState = 3;
        Log.i(this, "notifyNegotiationFailed", new Object[0]);
        TransportProtocol.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onNegotiationFailed(this);
        }
    }

    private void negotiationSucceeded() {
        this.mTransportState = 2;
        Log.i(this, "negotiationSucceeded", new Object[0]);
        TransportProtocol.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onNegotiationSuccess(this);
        }
    }

    private void handleReceivedDigit(char c) {
        String stringBuffer;
        String stringBuffer2;
        int i = this.mMessageReceiveState;
        if (i == 0) {
            if (c == 'A') {
                Log.i(this, "handleReceivedDigit: digit = %c ; message timeout started.", new Object[]{Character.valueOf(c)});
                this.mMessageReceiveState = 1;
                scheduleDtmfMessageTimeout();
                return;
            }
            Log.w(this, "handleReceivedDigit: digit = %c ; unexpected start digit, ignoring.", new Object[]{Character.valueOf(c)});
        } else if (c != 'D') {
            synchronized (this.mDigitsLock) {
                int i2 = this.mMessageReceiveState;
                if (i2 == 1) {
                    this.mMessageTypeDigits.append(c);
                    Log.i(this, "handleReceivedDigit: typeDigit = %c ; msg = %s", new Object[]{Character.valueOf(c), this.mMessageTypeDigits.toString()});
                } else if (i2 == 2) {
                    this.mMessageValueDigits.append(c);
                    Log.i(this, "handleReceivedDigit: valueDigit = %c ; value = %s", new Object[]{Character.valueOf(c), this.mMessageValueDigits.toString()});
                }
            }
        } else if (i == 1) {
            Log.i(this, "handleReceivedDigit: digit = %c ; msg = %s ; awaiting value.", new Object[]{Character.valueOf(c), this.mMessageTypeDigits.toString()});
            this.mMessageReceiveState = 2;
        } else if (i == 2) {
            maybeCancelDtmfMessageTimeout();
            synchronized (this.mDigitsLock) {
                stringBuffer = this.mMessageTypeDigits.toString();
                stringBuffer2 = this.mMessageValueDigits.toString();
            }
            Log.i(this, "handleReceivedDigit: digit = %c ; msg = %s ; value = %s ; full msg", new Object[]{Character.valueOf(c), stringBuffer, stringBuffer2});
            handleIncomingMessage(stringBuffer, stringBuffer2);
            resetIncomingMessage();
        }
    }

    private void scheduleDtmfMessageTimeout() {
        synchronized (this.mDtmfMessageTimeoutLock) {
            maybeCancelDtmfMessageTimeout();
            this.mDtmfMessageTimeoutFuture = this.mScheduledExecutorService.schedule(new Runnable() { // from class: com.android.internal.telephony.d2d.DtmfTransport$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DtmfTransport.this.lambda$scheduleDtmfMessageTimeout$2();
                }
            }, this.mDurationOfDtmfMessageMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void maybeCancelDtmfMessageTimeout() {
        synchronized (this.mDtmfMessageTimeoutLock) {
            if (this.mDtmfMessageTimeoutFuture != null) {
                Log.i(this, "scheduleDtmfMessageTimeout: timeout pending; cancelling", new Object[0]);
                this.mDtmfMessageTimeoutFuture.cancel(false);
                this.mDtmfMessageTimeoutFuture = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleDtmfMessageTimeout */
    public void lambda$scheduleDtmfMessageTimeout$2() {
        maybeCancelDtmfMessageTimeout();
        Log.i(this, "handleDtmfMessageTimeout: timeout receiving DTMF string; got %s/%s so far", new Object[]{this.mMessageTypeDigits.toString(), this.mMessageValueDigits.toString()});
        resetIncomingMessage();
    }

    @VisibleForTesting
    public char[] getMessageDigits(Communicator.Message message) {
        Pair<String, String> key = DTMF_TO_MESSAGE.getKey(message);
        if (key == null) {
            return null;
        }
        return (DTMF_MESSAGE_START + ((String) key.first) + DTMF_MESSAGE_DELIMITER + ((String) key.second) + DTMF_MESSAGE_DELIMITER).toCharArray();
    }

    @VisibleForTesting
    public Communicator.Message extractMessage(String str, String str2) {
        return DTMF_TO_MESSAGE.getValue(new Pair<>(str, str2));
    }

    private void handleIncomingMessage(String str, String str2) {
        Communicator.Message extractMessage = extractMessage(str, str2);
        if (extractMessage == null) {
            Log.w(this, "handleIncomingMessage: msgDigits = %s, msgValueDigits = %s; invalid msg", new Object[]{str, str2});
            return;
        }
        Log.i(this, "handleIncomingMessage: msgDigits = %s, msgValueDigits = %s", new Object[]{str, str2});
        ArraySet arraySet = new ArraySet(1);
        arraySet.add(extractMessage);
        TransportProtocol.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onMessagesReceived(arraySet);
        }
    }

    private void resetIncomingMessage() {
        this.mMessageReceiveState = 0;
        synchronized (this.mDigitsLock) {
            if (this.mMessageTypeDigits.length() != 0) {
                StringBuffer stringBuffer = this.mMessageTypeDigits;
                stringBuffer.delete(0, stringBuffer.length());
            }
            if (this.mMessageValueDigits.length() != 0) {
                StringBuffer stringBuffer2 = this.mMessageValueDigits;
                stringBuffer2.delete(0, stringBuffer2.length());
            }
        }
    }
}
