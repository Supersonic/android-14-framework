package com.android.internal.telephony.d2d;

import android.net.Uri;
import android.os.Handler;
import android.telecom.Log;
import android.telephony.ims.RtpHeaderExtension;
import android.telephony.ims.RtpHeaderExtensionType;
import android.util.ArraySet;
import com.android.internal.telephony.BiMap;
import com.android.internal.telephony.d2d.Communicator;
import com.android.internal.telephony.d2d.RtpAdapter;
import com.android.internal.telephony.d2d.Timeouts;
import com.android.internal.telephony.d2d.TransportProtocol;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class RtpTransport implements TransportProtocol, RtpAdapter.Callback {
    private static final BiMap<Integer, Byte> BATTERY_STATE_VALUE_TO_RTP_BITS;
    private static final BiMap<Integer, Byte> CALL_STATE_MSG_TYPE_TO_RTP_BITS;
    private static final BiMap<Integer, Byte> CODEC_VALUE_TO_RTP_BITS;
    private static final BiMap<Integer, Byte> DEVICE_STATE_MSG_TYPE_TO_RTP_BITS;
    private static final BiMap<Integer, Byte> NETWORK_COVERAGE_VALUE_TO_RTP_BITS;
    public static final int PROTOCOL_STATUS_NEGOTIATION_COMPLETE = 3;
    public static final int PROTOCOL_STATUS_NEGOTIATION_FAILED = 4;
    public static final int PROTOCOL_STATUS_NEGOTIATION_REQUIRED = 1;
    public static final int PROTOCOL_STATUS_NEGOTIATION_WAITING_ON_PACKET = 2;
    private static final BiMap<Integer, Byte> RAT_VALUE_TO_RTP_BITS;
    public static final byte RTP_BATTERY_STATE_CHARGING_BITS = 48;
    public static final byte RTP_BATTERY_STATE_GOOD_BITS = 16;
    public static final byte RTP_BATTERY_STATE_LOW_BITS = 0;
    public static final byte RTP_CALL_STATE_MSG_CODEC_BITS = 2;
    public static final byte RTP_CALL_STATE_MSG_RADIO_ACCESS_TYPE_BITS = 1;
    public static final byte RTP_CODEC_VALUE_AMR_NB_BITS = 48;
    public static final byte RTP_CODEC_VALUE_AMR_WB_BITS = 32;
    public static final byte RTP_CODEC_VALUE_EVS_BITS = 16;
    public static final byte RTP_DEVICE_STATE_MSG_BATTERY_BITS = 1;
    public static final byte RTP_DEVICE_STATE_MSG_NETWORK_COVERAGE_BITS = 2;
    public static final byte RTP_NETWORK_COVERAGE_GOOD_BITS = 16;
    public static final byte RTP_NETWORK_COVERAGE_POOR_BITS = 0;
    public static final int RTP_PARAMETER_BIT_OFFSET = 4;
    public static final byte RTP_RAT_VALUE_LTE_BITS = 16;
    public static final byte RTP_RAT_VALUE_NR_BITS = 48;
    public static final byte RTP_RAT_VALUE_WLAN_BITS = 32;
    private TransportProtocol.Callback mCallback;
    private final Handler mHandler;
    private final boolean mIsSdpNegotiationSupported;
    private final RtpAdapter mRtpAdapter;
    private final Timeouts.Adapter mTimeoutsAdapter;
    public static Uri DEVICE_STATE_RTP_HEADER_EXTENSION = Uri.parse("http://develop.android.com/122020/d2dcomm#device-state");
    public static Uri CALL_STATE_RTP_HEADER_EXTENSION = Uri.parse("http://develop.android.com/122020/d2dcomm#call-state");
    public static int DEVICE_STATE_LOCAL_IDENTIFIER = 10;
    public static int CALL_STATE_LOCAL_IDENTIFIER = 11;
    public static RtpHeaderExtensionType DEVICE_STATE_RTP_HEADER_EXTENSION_TYPE = new RtpHeaderExtensionType(DEVICE_STATE_LOCAL_IDENTIFIER, DEVICE_STATE_RTP_HEADER_EXTENSION);
    public static RtpHeaderExtensionType CALL_STATE_RTP_HEADER_EXTENSION_TYPE = new RtpHeaderExtensionType(CALL_STATE_LOCAL_IDENTIFIER, CALL_STATE_RTP_HEADER_EXTENSION);
    private int mProtocolStatus = 1;
    private ArraySet<RtpHeaderExtensionType> mSupportedRtpHeaderExtensionTypes = new ArraySet<>();

    static {
        BiMap<Integer, Byte> biMap = new BiMap<>();
        CALL_STATE_MSG_TYPE_TO_RTP_BITS = biMap;
        BiMap<Integer, Byte> biMap2 = new BiMap<>();
        DEVICE_STATE_MSG_TYPE_TO_RTP_BITS = biMap2;
        biMap.put(1, (byte) 1);
        biMap.put(2, (byte) 2);
        biMap2.put(3, (byte) 1);
        biMap2.put(4, (byte) 2);
        BiMap<Integer, Byte> biMap3 = new BiMap<>();
        RAT_VALUE_TO_RTP_BITS = biMap3;
        biMap3.put(2, (byte) 32);
        biMap3.put(1, (byte) 16);
        biMap3.put(3, (byte) 48);
        BiMap<Integer, Byte> biMap4 = new BiMap<>();
        CODEC_VALUE_TO_RTP_BITS = biMap4;
        biMap4.put(1, (byte) 16);
        biMap4.put(2, (byte) 32);
        biMap4.put(3, (byte) 48);
        BiMap<Integer, Byte> biMap5 = new BiMap<>();
        BATTERY_STATE_VALUE_TO_RTP_BITS = biMap5;
        biMap5.put(1, (byte) 0);
        biMap5.put(2, (byte) 16);
        biMap5.put(3, (byte) 48);
        BiMap<Integer, Byte> biMap6 = new BiMap<>();
        NETWORK_COVERAGE_VALUE_TO_RTP_BITS = biMap6;
        biMap6.put(1, (byte) 0);
        biMap6.put(2, (byte) 16);
    }

    public RtpTransport(RtpAdapter rtpAdapter, Timeouts.Adapter adapter, Handler handler, boolean z) {
        this.mRtpAdapter = rtpAdapter;
        this.mTimeoutsAdapter = adapter;
        this.mHandler = handler;
        this.mIsSdpNegotiationSupported = z;
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void setCallback(TransportProtocol.Callback callback) {
        this.mCallback = callback;
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void startNegotiation() {
        Set<RtpHeaderExtensionType> acceptedRtpHeaderExtensions = this.mRtpAdapter.getAcceptedRtpHeaderExtensions();
        this.mSupportedRtpHeaderExtensionTypes.addAll(acceptedRtpHeaderExtensions);
        Log.i(this, "startNegotiation: supportedExtensions=%s", new Object[]{this.mSupportedRtpHeaderExtensionTypes.stream().map(new Function() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String rtpHeaderExtensionType;
                rtpHeaderExtensionType = ((RtpHeaderExtensionType) obj).toString();
                return rtpHeaderExtensionType;
            }
        }).collect(Collectors.joining(","))});
        if (this.mIsSdpNegotiationSupported) {
            if (acceptedRtpHeaderExtensions.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$startNegotiation$1;
                    lambda$startNegotiation$1 = RtpTransport.lambda$startNegotiation$1((RtpHeaderExtensionType) obj);
                    return lambda$startNegotiation$1;
                }
            }) && acceptedRtpHeaderExtensions.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$startNegotiation$2;
                    lambda$startNegotiation$2 = RtpTransport.lambda$startNegotiation$2((RtpHeaderExtensionType) obj);
                    return lambda$startNegotiation$2;
                }
            })) {
                this.mProtocolStatus = 3;
                Log.i(this, "startNegotiation: header extensions available, negotiation success", new Object[0]);
                notifyProtocolReady();
                return;
            }
            this.mProtocolStatus = 4;
            Log.i(this, "startNegotiation: header extensions not available; negotiation failed", new Object[0]);
            notifyProtocolUnavailable();
            return;
        }
        Log.i(this, "startNegotiation: SDP negotiation not supported; negotiation complete", new Object[0]);
        notifyProtocolReady();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$startNegotiation$1(RtpHeaderExtensionType rtpHeaderExtensionType) {
        return rtpHeaderExtensionType.getUri().equals(DEVICE_STATE_RTP_HEADER_EXTENSION);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$startNegotiation$2(RtpHeaderExtensionType rtpHeaderExtensionType) {
        return rtpHeaderExtensionType.getUri().equals(CALL_STATE_RTP_HEADER_EXTENSION);
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void sendMessages(Set<Communicator.Message> set) {
        Log.i(this, "sendMessages: sending=%s", new Object[]{set});
        this.mRtpAdapter.sendRtpHeaderExtensions((Set) set.stream().map(new Function() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                RtpHeaderExtension lambda$sendMessages$3;
                lambda$sendMessages$3 = RtpTransport.this.lambda$sendMessages$3((Communicator.Message) obj);
                return lambda$sendMessages$3;
            }
        }).collect(Collectors.toSet()));
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void forceNegotiated() {
        ArraySet<RtpHeaderExtensionType> arraySet = this.mSupportedRtpHeaderExtensionTypes;
        if (arraySet == null || arraySet.isEmpty()) {
            this.mSupportedRtpHeaderExtensionTypes.add(DEVICE_STATE_RTP_HEADER_EXTENSION_TYPE);
            this.mSupportedRtpHeaderExtensionTypes.add(CALL_STATE_RTP_HEADER_EXTENSION_TYPE);
        }
        this.mProtocolStatus = 3;
    }

    @Override // com.android.internal.telephony.d2d.TransportProtocol
    public void forceNotNegotiated() {
        this.mProtocolStatus = 1;
    }

    @Override // com.android.internal.telephony.d2d.RtpAdapter.Callback
    public void onRtpHeaderExtensionsReceived(Set<RtpHeaderExtension> set) {
        Set<Communicator.Message> set2 = (Set) set.stream().map(new Function() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Communicator.Message lambda$onRtpHeaderExtensionsReceived$4;
                lambda$onRtpHeaderExtensionsReceived$4 = RtpTransport.this.lambda$onRtpHeaderExtensionsReceived$4((RtpHeaderExtension) obj);
                return lambda$onRtpHeaderExtensionsReceived$4;
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Communicator.Message) obj);
            }
        }).collect(Collectors.toSet());
        if (set2.size() == 0) {
            return;
        }
        this.mCallback.onMessagesReceived(set2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: extractMessage */
    public Communicator.Message lambda$onRtpHeaderExtensionsReceived$4(final RtpHeaderExtension rtpHeaderExtension) {
        int intValue;
        int intValue2;
        Optional findFirst = this.mSupportedRtpHeaderExtensionTypes.stream().filter(new Predicate() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$extractMessage$5;
                lambda$extractMessage$5 = RtpTransport.lambda$extractMessage$5(rtpHeaderExtension, (RtpHeaderExtensionType) obj);
                return lambda$extractMessage$5;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Uri uri;
                uri = ((RtpHeaderExtensionType) obj).getUri();
                return uri;
            }
        }).findFirst();
        if (!findFirst.isPresent()) {
            Log.w(this, "extractMessage: localIdentifier=%d not supported.", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier())});
            return null;
        } else if (rtpHeaderExtension.getExtensionData() == null || rtpHeaderExtension.getExtensionData().length != 1) {
            Log.w(this, "extractMessage: localIdentifier=%d message with invalid data length.", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier())});
            return null;
        } else {
            Uri uri = (Uri) findFirst.get();
            byte b = (byte) (rtpHeaderExtension.getExtensionData()[0] & 15);
            byte b2 = (byte) (rtpHeaderExtension.getExtensionData()[0] & 240);
            if (DEVICE_STATE_RTP_HEADER_EXTENSION.equals(uri)) {
                Integer key = DEVICE_STATE_MSG_TYPE_TO_RTP_BITS.getKey(Byte.valueOf(b));
                if (key == null) {
                    Log.w(this, "extractMessage: localIdentifier=%d message with invalid type %s.", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier()), Integer.toBinaryString(b)});
                    return null;
                }
                intValue = key.intValue();
                if (intValue == 3) {
                    Integer key2 = BATTERY_STATE_VALUE_TO_RTP_BITS.getKey(Byte.valueOf(b2));
                    if (key2 == null) {
                        Log.w(this, "extractMessage: localIdentifier=%d, battery state msg with invalid value=%s", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier()), Integer.toBinaryString(b2)});
                        return null;
                    }
                    intValue2 = key2.intValue();
                } else if (intValue == 4) {
                    Integer key3 = NETWORK_COVERAGE_VALUE_TO_RTP_BITS.getKey(Byte.valueOf(b2));
                    if (key3 == null) {
                        Log.w(this, "extractMessage: localIdentifier=%d, network coverage msg with invalid value=%s", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier()), Integer.toBinaryString(b2)});
                        return null;
                    }
                    intValue2 = key3.intValue();
                } else {
                    Log.w(this, "messageType=%s, value=%s; invalid value", new Object[]{Integer.toBinaryString(b), Integer.toBinaryString(b2)});
                    return null;
                }
            } else if (CALL_STATE_RTP_HEADER_EXTENSION.equals(uri)) {
                Integer key4 = CALL_STATE_MSG_TYPE_TO_RTP_BITS.getKey(Byte.valueOf(b));
                if (key4 == null) {
                    Log.w(this, "extractMessage: localIdentifier=%d, network coverage msg with invalid type=%s", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier()), Integer.toBinaryString(b)});
                    return null;
                }
                intValue = key4.intValue();
                if (intValue == 1) {
                    Integer key5 = RAT_VALUE_TO_RTP_BITS.getKey(Byte.valueOf(b2));
                    if (key5 == null) {
                        Log.w(this, "extractMessage: localIdentifier=%d, rat type msg with invalid value=%s", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier()), Integer.toBinaryString(b2)});
                        return null;
                    }
                    intValue2 = key5.intValue();
                } else if (intValue == 2) {
                    Integer key6 = CODEC_VALUE_TO_RTP_BITS.getKey(Byte.valueOf(b2));
                    if (key6 == null) {
                        Log.w(this, "extractMessage: localIdentifier=%d, audio codec msg with invalid value=%s", new Object[]{Integer.valueOf(rtpHeaderExtension.getLocalIdentifier()), Integer.toBinaryString(b2)});
                        return null;
                    }
                    intValue2 = key6.intValue();
                } else {
                    Log.w(this, "messageType=%s, value=%s; invalid value", new Object[]{Integer.toBinaryString(b), Integer.toBinaryString(b2)});
                    return null;
                }
            } else {
                Log.w(this, "invalid uri=%s", new Object[]{uri});
                return null;
            }
            Log.i(this, "extractMessage: messageType=%s, value=%s --> message=%d, value=%d", new Object[]{Integer.toBinaryString(b), Integer.toBinaryString(b2), Integer.valueOf(intValue), Integer.valueOf(intValue2)});
            return new Communicator.Message(intValue, intValue2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$extractMessage$5(RtpHeaderExtension rtpHeaderExtension, RtpHeaderExtensionType rtpHeaderExtensionType) {
        return rtpHeaderExtensionType.getLocalIdentifier() == rtpHeaderExtension.getLocalIdentifier();
    }

    /* renamed from: generateRtpHeaderExtension */
    public RtpHeaderExtension lambda$sendMessages$3(Communicator.Message message) {
        byte[] bArr = new byte[1];
        int type = message.getType();
        if (type == 1) {
            byte byteValue = (byte) (bArr[0] | CALL_STATE_MSG_TYPE_TO_RTP_BITS.getValue(Integer.valueOf(message.getType())).byteValue());
            bArr[0] = byteValue;
            bArr[0] = (byte) (RAT_VALUE_TO_RTP_BITS.getValue(Integer.valueOf(message.getValue())).byteValue() | byteValue);
            return new RtpHeaderExtension(getRtpHeaderExtensionIdentifier(CALL_STATE_RTP_HEADER_EXTENSION), bArr);
        } else if (type == 2) {
            byte byteValue2 = (byte) (bArr[0] | CALL_STATE_MSG_TYPE_TO_RTP_BITS.getValue(Integer.valueOf(message.getType())).byteValue());
            bArr[0] = byteValue2;
            bArr[0] = (byte) (CODEC_VALUE_TO_RTP_BITS.getValue(Integer.valueOf(message.getValue())).byteValue() | byteValue2);
            return new RtpHeaderExtension(getRtpHeaderExtensionIdentifier(CALL_STATE_RTP_HEADER_EXTENSION), bArr);
        } else if (type == 3) {
            byte byteValue3 = (byte) (bArr[0] | DEVICE_STATE_MSG_TYPE_TO_RTP_BITS.getValue(Integer.valueOf(message.getType())).byteValue());
            bArr[0] = byteValue3;
            bArr[0] = (byte) (BATTERY_STATE_VALUE_TO_RTP_BITS.getValue(Integer.valueOf(message.getValue())).byteValue() | byteValue3);
            return new RtpHeaderExtension(getRtpHeaderExtensionIdentifier(DEVICE_STATE_RTP_HEADER_EXTENSION), bArr);
        } else if (type != 4) {
            return null;
        } else {
            byte byteValue4 = (byte) (bArr[0] | DEVICE_STATE_MSG_TYPE_TO_RTP_BITS.getValue(Integer.valueOf(message.getType())).byteValue());
            bArr[0] = byteValue4;
            bArr[0] = (byte) (NETWORK_COVERAGE_VALUE_TO_RTP_BITS.getValue(Integer.valueOf(message.getValue())).byteValue() | byteValue4);
            return new RtpHeaderExtension(getRtpHeaderExtensionIdentifier(DEVICE_STATE_RTP_HEADER_EXTENSION), bArr);
        }
    }

    private int getRtpHeaderExtensionIdentifier(final Uri uri) {
        return this.mSupportedRtpHeaderExtensionTypes.stream().filter(new Predicate() { // from class: com.android.internal.telephony.d2d.RtpTransport$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getRtpHeaderExtensionIdentifier$7;
                lambda$getRtpHeaderExtensionIdentifier$7 = RtpTransport.lambda$getRtpHeaderExtensionIdentifier$7(uri, (RtpHeaderExtensionType) obj);
                return lambda$getRtpHeaderExtensionIdentifier$7;
            }
        }).findFirst().get().getLocalIdentifier();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getRtpHeaderExtensionIdentifier$7(Uri uri, RtpHeaderExtensionType rtpHeaderExtensionType) {
        return rtpHeaderExtensionType.getUri().equals(uri);
    }

    private void notifyProtocolReady() {
        TransportProtocol.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onNegotiationSuccess(this);
        }
    }

    private void notifyProtocolUnavailable() {
        TransportProtocol.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onNegotiationFailed(this);
        }
    }
}
