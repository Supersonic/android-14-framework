package com.android.internal.telephony.uicc.euicc;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.service.carrier.CarrierIdentifier;
import android.service.euicc.EuiccProfileInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.UiccAccessRule;
import android.telephony.euicc.EuiccNotification;
import android.telephony.euicc.EuiccRulesAuthTable;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccIoResult;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.PortUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.asn1.Asn1Decoder;
import com.android.internal.telephony.uicc.asn1.Asn1Node;
import com.android.internal.telephony.uicc.asn1.InvalidAsn1DataException;
import com.android.internal.telephony.uicc.asn1.TagNotFoundException;
import com.android.internal.telephony.uicc.euicc.apdu.ApduException;
import com.android.internal.telephony.uicc.euicc.apdu.ApduSender;
import com.android.internal.telephony.uicc.euicc.apdu.ApduSenderResultCallback;
import com.android.internal.telephony.uicc.euicc.apdu.RequestBuilder;
import com.android.internal.telephony.uicc.euicc.apdu.RequestProvider;
import com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback;
import com.android.internal.telephony.uicc.euicc.async.AsyncResultHelper;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class EuiccPort extends UiccPort {
    private static final EuiccSpecVersion SGP22_V_2_0 = new EuiccSpecVersion(2, 0, 0);
    private static final EuiccSpecVersion SGP22_V_2_1 = new EuiccSpecVersion(2, 1, 0);
    private final ApduSender mApduSender;
    private volatile String mEid;
    private EuiccSpecVersion mSpecVersion;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public IccSlotStatus.MultipleEnabledProfilesMode mSupportedMepMode;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ApduExceptionHandler {
        void handleException(Throwable th);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ApduIntermediateResultHandler {
        boolean shouldContinue(IccIoResult iccIoResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ApduRequestBuilder {
        void build(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ApduResponseHandler<T> {
        T handleResult(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$getEuiccInfo1$29(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$getEuiccInfo2$31(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSpecVersion$0(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
    }

    public EuiccPort(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, Object obj, UiccCard uiccCard, IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        super(context, commandsInterface, iccCardStatus, i, obj, uiccCard);
        this.mApduSender = new ApduSender(commandsInterface, "A0000005591010FFFFFFFF8900000100", false);
        if (TextUtils.isEmpty(iccCardStatus.eid)) {
            loge("no eid given in constructor for phone " + i);
        } else {
            this.mEid = iccCardStatus.eid;
            this.mCardId = iccCardStatus.eid;
        }
        this.mSupportedMepMode = multipleEnabledProfilesMode;
    }

    public void getSpecVersion(AsyncResultCallback<EuiccSpecVersion> asyncResultCallback, Handler handler) {
        EuiccSpecVersion euiccSpecVersion = this.mSpecVersion;
        if (euiccSpecVersion != null) {
            AsyncResultHelper.returnResult(euiccSpecVersion, asyncResultCallback, handler);
        } else {
            sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda16
                @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
                public final void build(RequestBuilder requestBuilder) {
                    EuiccPort.lambda$getSpecVersion$0(requestBuilder);
                }
            }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda17
                @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
                public final Object handleResult(byte[] bArr) {
                    EuiccSpecVersion lambda$getSpecVersion$1;
                    lambda$getSpecVersion$1 = EuiccPort.this.lambda$getSpecVersion$1(bArr);
                    return lambda$getSpecVersion$1;
                }
            }, asyncResultCallback, handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ EuiccSpecVersion lambda$getSpecVersion$1(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return this.mSpecVersion;
    }

    @Override // com.android.internal.telephony.uicc.UiccPort
    public void update(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, UiccCard uiccCard) {
        synchronized (this.mLock) {
            if (!TextUtils.isEmpty(iccCardStatus.eid)) {
                this.mEid = iccCardStatus.eid;
            }
            super.update(context, commandsInterface, iccCardStatus, uiccCard);
        }
    }

    public void updateSupportedMepMode(IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        logd("updateSupportedMepMode");
        this.mSupportedMepMode = multipleEnabledProfilesMode;
    }

    public void getAllProfiles(AsyncResultCallback<EuiccProfileInfo[]> asyncResultCallback, Handler handler) {
        final byte[] bArr = this.mSupportedMepMode.isMepMode() ? Tags.EUICC_PROFILE_MEP_TAGS : Tags.EUICC_PROFILE_TAGS;
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda8
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getAllProfiles$2(bArr, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda9
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr2) {
                EuiccProfileInfo[] lambda$getAllProfiles$3;
                lambda$getAllProfiles$3 = EuiccPort.lambda$getAllProfiles$3(bArr2);
                return lambda$getAllProfiles$3;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getAllProfiles$2(byte[] bArr, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48941).addChildAsBytes(92, bArr).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ EuiccProfileInfo[] lambda$getAllProfiles$3(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        List children = new Asn1Decoder(bArr).nextNode().getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS, new int[0]).getChildren(227);
        int size = children.size();
        EuiccProfileInfo[] euiccProfileInfoArr = new EuiccProfileInfo[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            Asn1Node asn1Node = (Asn1Node) children.get(i2);
            if (!asn1Node.hasChild(90, new int[0])) {
                loge("Profile must have an ICCID.");
            } else {
                EuiccProfileInfo.Builder builder = new EuiccProfileInfo.Builder(stripTrailingFs(asn1Node.getChild(90, new int[0]).asBytes()));
                buildProfile(asn1Node, builder);
                euiccProfileInfoArr[i] = builder.build();
                i++;
            }
        }
        return euiccProfileInfoArr;
    }

    public final void getProfile(final String str, AsyncResultCallback<EuiccProfileInfo> asyncResultCallback, Handler handler) {
        final byte[] bArr = this.mSupportedMepMode.isMepMode() ? Tags.EUICC_PROFILE_MEP_TAGS : Tags.EUICC_PROFILE_TAGS;
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda20
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getProfile$4(str, bArr, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda21
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr2) {
                EuiccProfileInfo lambda$getProfile$5;
                lambda$getProfile$5 = EuiccPort.lambda$getProfile$5(bArr2);
                return lambda$getProfile$5;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getProfile$4(String str, byte[] bArr, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48941).addChild(Asn1Node.newBuilder((int) SmsMessage.MAX_USER_DATA_SEPTETS).addChildAsBytes(90, IccUtils.bcdToBytes(padTrailingFs(str))).build()).addChildAsBytes(92, bArr).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ EuiccProfileInfo lambda$getProfile$5(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        List children = new Asn1Decoder(bArr).nextNode().getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS, new int[0]).getChildren(227);
        if (children.isEmpty()) {
            return null;
        }
        Asn1Node asn1Node = (Asn1Node) children.get(0);
        EuiccProfileInfo.Builder builder = new EuiccProfileInfo.Builder(stripTrailingFs(asn1Node.getChild(90, new int[0]).asBytes()));
        buildProfile(asn1Node, builder);
        return builder.build();
    }

    public void disableProfile(final String str, final boolean z, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApduWithSimResetErrorWorkaround(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda28
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$disableProfile$6(str, z, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda29
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$disableProfile$7;
                lambda$disableProfile$7 = EuiccPort.lambda$disableProfile$7(str, bArr);
                return lambda$disableProfile$7;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$disableProfile$6(String str, boolean z, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48946).addChild(Asn1Node.newBuilder((int) SmsMessage.MAX_USER_DATA_SEPTETS).addChildAsBytes(90, IccUtils.bcdToBytes(padTrailingFs(str)))).addChildAsBoolean(129, z).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$disableProfile$7(String str, byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult != 0) {
            if (parseSimpleResult == 2) {
                logd("Profile is already disabled, iccid: " + SubscriptionInfo.givePrintableIccid(str));
                return null;
            }
            throw new EuiccCardErrorException(11, parseSimpleResult);
        }
        return null;
    }

    public void switchToProfile(final String str, final boolean z, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApduWithSimResetErrorWorkaround(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda12
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.this.lambda$switchToProfile$8(str, z, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda13
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$switchToProfile$9;
                lambda$switchToProfile$9 = EuiccPort.lambda$switchToProfile$9(str, bArr);
                return lambda$switchToProfile$9;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToProfile$8(String str, boolean z, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node.Builder addChildAsBoolean = Asn1Node.newBuilder(48945).addChild(Asn1Node.newBuilder((int) SmsMessage.MAX_USER_DATA_SEPTETS).addChildAsBytes(90, IccUtils.bcdToBytes(padTrailingFs(str)))).addChildAsBoolean(129, z);
        if (this.mSupportedMepMode.isMepA1Mode()) {
            addChildAsBoolean.addChildAsInteger(130, PortUtils.convertToHalPortIndex(this.mSupportedMepMode, super.getPortIdx()));
        }
        requestBuilder.addStoreData(addChildAsBoolean.build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$switchToProfile$9(String str, byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult != 0) {
            if (parseSimpleResult == 2) {
                logd("Profile is already enabled, iccid: " + SubscriptionInfo.givePrintableIccid(str));
                return null;
            }
            throw new EuiccCardErrorException(10, parseSimpleResult);
        }
        return null;
    }

    public String getEid() {
        return this.mEid;
    }

    public void getEid(AsyncResultCallback<String> asyncResultCallback, Handler handler) {
        if (this.mEid != null) {
            AsyncResultHelper.returnResult(this.mEid, asyncResultCallback, handler);
        } else {
            sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda22
                @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
                public final void build(RequestBuilder requestBuilder) {
                    EuiccPort.lambda$getEid$10(requestBuilder);
                }
            }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda23
                @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
                public final Object handleResult(byte[] bArr) {
                    String lambda$getEid$11;
                    lambda$getEid$11 = EuiccPort.this.lambda$getEid$11(bArr);
                    return lambda$getEid$11;
                }
            }, asyncResultCallback, handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEid$10(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48958).addChildAsBytes(92, new byte[]{90}).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEid$11(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        String bytesToHexString = IccUtils.bytesToHexString(parseResponse(bArr).getChild(90, new int[0]).asBytes());
        synchronized (this.mLock) {
            this.mEid = bytesToHexString;
            this.mCardId = bytesToHexString;
        }
        return bytesToHexString;
    }

    public void setNickname(final String str, final String str2, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda14
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$setNickname$12(str, str2, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda15
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$setNickname$13;
                lambda$setNickname$13 = EuiccPort.lambda$setNickname$13(bArr);
                return lambda$setNickname$13;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setNickname$12(String str, String str2, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48937).addChildAsBytes(90, IccUtils.bcdToBytes(padTrailingFs(str))).addChildAsString(144, str2).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$setNickname$13(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult == 0) {
            return null;
        }
        throw new EuiccCardErrorException(7, parseSimpleResult);
    }

    public void deleteProfile(final String str, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda34
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$deleteProfile$14(str, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda35
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$deleteProfile$15;
                lambda$deleteProfile$15 = EuiccPort.lambda$deleteProfile$15(bArr);
                return lambda$deleteProfile$15;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$deleteProfile$14(String str, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48947).addChildAsBytes(90, IccUtils.bcdToBytes(padTrailingFs(str))).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$deleteProfile$15(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult == 0) {
            return null;
        }
        throw new EuiccCardErrorException(12, parseSimpleResult);
    }

    public void resetMemory(final int i, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApduWithSimResetErrorWorkaround(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda30
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$resetMemory$16(i, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda31
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$resetMemory$17;
                lambda$resetMemory$17 = EuiccPort.lambda$resetMemory$17(bArr);
                return lambda$resetMemory$17;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$resetMemory$16(int i, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48948).addChildAsBits(130, i).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$resetMemory$17(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult == 0 || parseSimpleResult == 1) {
            return null;
        }
        throw new EuiccCardErrorException(13, parseSimpleResult);
    }

    public void getDefaultSmdpAddress(AsyncResultCallback<String> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda24
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getDefaultSmdpAddress$18(requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda25
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                String lambda$getDefaultSmdpAddress$19;
                lambda$getDefaultSmdpAddress$19 = EuiccPort.lambda$getDefaultSmdpAddress$19(bArr);
                return lambda$getDefaultSmdpAddress$19;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getDefaultSmdpAddress$18(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48956).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getDefaultSmdpAddress$19(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return parseResponse(bArr).getChild(128, new int[0]).asString();
    }

    public void getSmdsAddress(AsyncResultCallback<String> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda2
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getSmdsAddress$20(requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                String lambda$getSmdsAddress$21;
                lambda$getSmdsAddress$21 = EuiccPort.lambda$getSmdsAddress$21(bArr);
                return lambda$getSmdsAddress$21;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSmdsAddress$20(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48956).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getSmdsAddress$21(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return parseResponse(bArr).getChild(129, new int[0]).asString();
    }

    public void setDefaultSmdpAddress(final String str, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$setDefaultSmdpAddress$22(str, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda1
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$setDefaultSmdpAddress$23;
                lambda$setDefaultSmdpAddress$23 = EuiccPort.lambda$setDefaultSmdpAddress$23(bArr);
                return lambda$setDefaultSmdpAddress$23;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setDefaultSmdpAddress$22(String str, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48959).addChildAsString(128, str).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$setDefaultSmdpAddress$23(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult == 0) {
            return null;
        }
        throw new EuiccCardErrorException(14, parseSimpleResult);
    }

    public void getRulesAuthTable(AsyncResultCallback<EuiccRulesAuthTable> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda32
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getRulesAuthTable$24(requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda33
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                EuiccRulesAuthTable lambda$getRulesAuthTable$25;
                lambda$getRulesAuthTable$25 = EuiccPort.lambda$getRulesAuthTable$25(bArr);
                return lambda$getRulesAuthTable$25;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getRulesAuthTable$24(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48963).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ EuiccRulesAuthTable lambda$getRulesAuthTable$25(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        List children = parseResponse(bArr).getChildren((int) SmsMessage.MAX_USER_DATA_SEPTETS);
        EuiccRulesAuthTable.Builder builder = new EuiccRulesAuthTable.Builder(children.size());
        int size = children.size();
        for (int i = 0; i < size; i++) {
            Asn1Node asn1Node = (Asn1Node) children.get(i);
            List children2 = asn1Node.getChild(48, new int[]{161}).getChildren();
            int size2 = children2.size();
            CarrierIdentifier[] carrierIdentifierArr = new CarrierIdentifier[size2];
            for (int i2 = 0; i2 < size2; i2++) {
                carrierIdentifierArr[i2] = buildCarrierIdentifier((Asn1Node) children2.get(i2));
            }
            builder.add(asn1Node.getChild(48, new int[]{128}).asBits(), Arrays.asList(carrierIdentifierArr), asn1Node.getChild(48, new int[]{130}).asBits());
        }
        return builder.build();
    }

    public void getEuiccChallenge(AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda26
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getEuiccChallenge$26(requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda27
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                byte[] lambda$getEuiccChallenge$27;
                lambda$getEuiccChallenge$27 = EuiccPort.lambda$getEuiccChallenge$27(bArr);
                return lambda$getEuiccChallenge$27;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEuiccChallenge$26(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48942).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$getEuiccChallenge$27(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return parseResponse(bArr).getChild(128, new int[0]).asBytes();
    }

    public void getEuiccInfo1(AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda18
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getEuiccInfo1$28(requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda19
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                byte[] lambda$getEuiccInfo1$29;
                lambda$getEuiccInfo1$29 = EuiccPort.lambda$getEuiccInfo1$29(bArr);
                return lambda$getEuiccInfo1$29;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEuiccInfo1$28(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48928).build().toHex());
    }

    public void getEuiccInfo2(AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda39
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$getEuiccInfo2$30(requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda40
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                byte[] lambda$getEuiccInfo2$31;
                lambda$getEuiccInfo2$31 = EuiccPort.lambda$getEuiccInfo2$31(bArr);
                return lambda$getEuiccInfo2$31;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEuiccInfo2$30(RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48930).build().toHex());
    }

    public void authenticateServer(final String str, final byte[] bArr, final byte[] bArr2, final byte[] bArr3, final byte[] bArr4, AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda6
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.this.lambda$authenticateServer$32(str, bArr, bArr2, bArr3, bArr4, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda7
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr5) {
                byte[] lambda$authenticateServer$33;
                lambda$authenticateServer$33 = EuiccPort.lambda$authenticateServer$33(bArr5);
                return lambda$authenticateServer$33;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$authenticateServer$32(String str, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        byte[] deviceId = getDeviceId();
        byte[] bArr5 = new byte[4];
        System.arraycopy(deviceId, 0, bArr5, 0, 4);
        Asn1Node.Builder newBuilder = Asn1Node.newBuilder(161);
        String[] stringArray = getResources().getStringArray(17236147);
        if (stringArray != null) {
            for (String str2 : stringArray) {
                addDeviceCapability(newBuilder, str2);
            }
        } else {
            logd("No device capabilities set.");
        }
        requestBuilder.addStoreData(Asn1Node.newBuilder(48952).addChild(new Asn1Decoder(bArr).nextNode()).addChild(new Asn1Decoder(bArr2).nextNode()).addChild(new Asn1Decoder(bArr3).nextNode()).addChild(new Asn1Decoder(bArr4).nextNode()).addChild(Asn1Node.newBuilder((int) SmsMessage.MAX_USER_DATA_SEPTETS).addChildAsString(128, str).addChild(Asn1Node.newBuilder(161).addChildAsBytes(128, bArr5).addChild(newBuilder).addChildAsBytes(130, deviceId))).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$authenticateServer$33(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node parseResponse = parseResponse(bArr);
        if (parseResponse.hasChild(161, new int[]{2})) {
            throw new EuiccCardErrorException(3, parseResponse.getChild(161, new int[]{2}).asInteger());
        }
        return parseResponse.toBytes();
    }

    public void prepareDownload(final byte[] bArr, final byte[] bArr2, final byte[] bArr3, final byte[] bArr4, AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda41
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$prepareDownload$34(bArr2, bArr3, bArr, bArr4, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda42
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr5) {
                byte[] lambda$prepareDownload$35;
                lambda$prepareDownload$35 = EuiccPort.lambda$prepareDownload$35(bArr5);
                return lambda$prepareDownload$35;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareDownload$34(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node.Builder addChild = Asn1Node.newBuilder(48929).addChild(new Asn1Decoder(bArr).nextNode()).addChild(new Asn1Decoder(bArr2).nextNode());
        if (bArr3 != null) {
            addChild.addChildAsBytes(4, bArr3);
        }
        requestBuilder.addStoreData(addChild.addChild(new Asn1Decoder(bArr4).nextNode()).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$prepareDownload$35(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node parseResponse = parseResponse(bArr);
        if (parseResponse.hasChild(161, new int[]{2})) {
            throw new EuiccCardErrorException(2, parseResponse.getChild(161, new int[]{2}).asInteger());
        }
        return parseResponse.toBytes();
    }

    public void loadBoundProfilePackage(final byte[] bArr, AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda36
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.this.lambda$loadBoundProfilePackage$36(bArr, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda37
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr2) {
                byte[] lambda$loadBoundProfilePackage$37;
                lambda$loadBoundProfilePackage$37 = EuiccPort.lambda$loadBoundProfilePackage$37(bArr2);
                return lambda$loadBoundProfilePackage$37;
            }
        }, new ApduIntermediateResultHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda38
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduIntermediateResultHandler
            public final boolean shouldContinue(IccIoResult iccIoResult) {
                boolean lambda$loadBoundProfilePackage$38;
                lambda$loadBoundProfilePackage$38 = EuiccPort.lambda$loadBoundProfilePackage$38(iccIoResult);
                return lambda$loadBoundProfilePackage$38;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBoundProfilePackage$36(byte[] bArr, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node asn1Node;
        Asn1Node nextNode = new Asn1Decoder(bArr).nextNode();
        int dataLength = nextNode.getDataLength();
        Asn1Node child = nextNode.getChild(48931, new int[0]);
        Asn1Node child2 = nextNode.getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS, new int[0]);
        int encodedLength = child.getEncodedLength() + 0 + child2.getEncodedLength();
        Asn1Node child3 = nextNode.getChild(161, new int[0]);
        List children = child3.getChildren((int) NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT);
        int encodedLength2 = encodedLength + child3.getEncodedLength();
        if (nextNode.hasChild(162, new int[0])) {
            asn1Node = nextNode.getChild(162, new int[0]);
            encodedLength2 += asn1Node.getEncodedLength();
        } else {
            asn1Node = null;
        }
        Asn1Node child4 = nextNode.getChild(163, new int[0]);
        List children2 = child4.getChildren(134);
        int encodedLength3 = encodedLength2 + child4.getEncodedLength();
        if (this.mSpecVersion.compareTo(SGP22_V_2_1) >= 0) {
            if (children2 == null || children2.isEmpty()) {
                throw new EuiccCardException("No profile elements in BPP");
            }
            if (dataLength != encodedLength3) {
                throw new EuiccCardException("Actual BPP length (" + dataLength + ") does not match segmented length (" + encodedLength3 + "), this must be due to a malformed BPP");
            }
        }
        requestBuilder.addStoreData(nextNode.getHeadAsHex() + child.toHex());
        requestBuilder.addStoreData(child2.toHex());
        requestBuilder.addStoreData(child3.getHeadAsHex());
        int size = children.size();
        for (int i = 0; i < size; i++) {
            requestBuilder.addStoreData(((Asn1Node) children.get(i)).toHex());
        }
        if (asn1Node != null) {
            requestBuilder.addStoreData(asn1Node.toHex());
        }
        requestBuilder.addStoreData(child4.getHeadAsHex());
        int size2 = children2.size();
        for (int i2 = 0; i2 < size2; i2++) {
            requestBuilder.addStoreData(((Asn1Node) children2.get(i2)).toHex());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$loadBoundProfilePackage$37(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node parseResponse = parseResponse(bArr);
        if (parseResponse.hasChild(48935, new int[]{162, 161, 129})) {
            Asn1Node child = parseResponse.getChild(48935, new int[]{162, 161, 129});
            throw new EuiccCardErrorException(5, child.asInteger(), child);
        }
        return parseResponse.toBytes();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$loadBoundProfilePackage$38(IccIoResult iccIoResult) {
        byte[] bArr = iccIoResult.payload;
        if (bArr != null && bArr.length > 2) {
            if (((bArr[1] & 255) | ((bArr[0] & 255) << 8)) == 48951) {
                logd("loadBoundProfilePackage failed due to an early error.");
                return false;
            }
        }
        return true;
    }

    public void cancelSession(final byte[] bArr, final int i, AsyncResultCallback<byte[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda10
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$cancelSession$39(bArr, i, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda11
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr2) {
                byte[] lambda$cancelSession$40;
                lambda$cancelSession$40 = EuiccPort.lambda$cancelSession$40(bArr2);
                return lambda$cancelSession$40;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$cancelSession$39(byte[] bArr, int i, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48961).addChildAsBytes(128, bArr).addChildAsInteger(129, i).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$cancelSession$40(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return parseResponseAndCheckSimpleError(bArr, 4).toBytes();
    }

    public void listNotifications(final int i, AsyncResultCallback<EuiccNotification[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda43
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$listNotifications$41(i, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda44
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                EuiccNotification[] lambda$listNotifications$42;
                lambda$listNotifications$42 = EuiccPort.lambda$listNotifications$42(bArr);
                return lambda$listNotifications$42;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$listNotifications$41(int i, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48936).addChildAsBits(129, i).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ EuiccNotification[] lambda$listNotifications$42(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        List children = parseResponseAndCheckSimpleError(bArr, 6).getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS, new int[0]).getChildren();
        int size = children.size();
        EuiccNotification[] euiccNotificationArr = new EuiccNotification[size];
        for (int i = 0; i < size; i++) {
            euiccNotificationArr[i] = createNotification((Asn1Node) children.get(i));
        }
        return euiccNotificationArr;
    }

    public void retrieveNotificationList(final int i, AsyncResultCallback<EuiccNotification[]> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda4
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$retrieveNotificationList$43(i, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda5
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                EuiccNotification[] lambda$retrieveNotificationList$44;
                lambda$retrieveNotificationList$44 = EuiccPort.lambda$retrieveNotificationList$44(bArr);
                return lambda$retrieveNotificationList$44;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$retrieveNotificationList$43(int i, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48939).addChild(Asn1Node.newBuilder((int) SmsMessage.MAX_USER_DATA_SEPTETS).addChildAsBits(129, i)).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ EuiccNotification[] lambda$retrieveNotificationList$44(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        Asn1Node parseResponse = parseResponse(bArr);
        if (parseResponse.hasChild(129, new int[0])) {
            int asInteger = parseResponse.getChild(129, new int[0]).asInteger();
            if (asInteger == 1) {
                return new EuiccNotification[0];
            }
            throw new EuiccCardErrorException(8, asInteger);
        }
        List children = parseResponse.getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS, new int[0]).getChildren();
        int size = children.size();
        EuiccNotification[] euiccNotificationArr = new EuiccNotification[size];
        for (int i = 0; i < size; i++) {
            euiccNotificationArr[i] = createNotification((Asn1Node) children.get(i));
        }
        return euiccNotificationArr;
    }

    public void retrieveNotification(final int i, AsyncResultCallback<EuiccNotification> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda47
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$retrieveNotification$45(i, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda48
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                EuiccNotification lambda$retrieveNotification$46;
                lambda$retrieveNotification$46 = EuiccPort.lambda$retrieveNotification$46(bArr);
                return lambda$retrieveNotification$46;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$retrieveNotification$45(int i, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48939).addChild(Asn1Node.newBuilder((int) SmsMessage.MAX_USER_DATA_SEPTETS).addChildAsInteger(128, i)).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ EuiccNotification lambda$retrieveNotification$46(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        List children = parseResponseAndCheckSimpleError(bArr, 8).getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS, new int[0]).getChildren();
        if (children.size() > 0) {
            return createNotification((Asn1Node) children.get(0));
        }
        return null;
    }

    public void removeNotificationFromList(final int i, AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApdu(newRequestProvider(new ApduRequestBuilder() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda45
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduRequestBuilder
            public final void build(RequestBuilder requestBuilder) {
                EuiccPort.lambda$removeNotificationFromList$47(i, requestBuilder);
            }
        }), new ApduResponseHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda46
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduResponseHandler
            public final Object handleResult(byte[] bArr) {
                Void lambda$removeNotificationFromList$48;
                lambda$removeNotificationFromList$48 = EuiccPort.lambda$removeNotificationFromList$48(bArr);
                return lambda$removeNotificationFromList$48;
            }
        }, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeNotificationFromList$47(int i, RequestBuilder requestBuilder) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        requestBuilder.addStoreData(Asn1Node.newBuilder(48944).addChildAsInteger(128, i).build().toHex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Void lambda$removeNotificationFromList$48(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        int parseSimpleResult = parseSimpleResult(bArr);
        if (parseSimpleResult == 0 || parseSimpleResult == 1) {
            return null;
        }
        throw new EuiccCardErrorException(9, parseSimpleResult);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00d5, code lost:
        if (r1.equals("eutran5gc") == false) goto L14;
     */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void addDeviceCapability(Asn1Node.Builder builder, String str) {
        String[] split = str.split(",");
        char c = 2;
        if (split.length != 2) {
            loge("Invalid device capability item: " + Arrays.toString(split));
            return;
        }
        String trim = split[0].trim();
        String[] split2 = split[1].trim().split("\\.");
        try {
            byte[] bArr = {Integer.valueOf(Integer.parseInt(split2[0])).byteValue(), (split2.length > 1 ? Integer.valueOf(Integer.parseInt(split2[1])) : 0).byteValue(), 0};
            trim.hashCode();
            switch (trim.hashCode()) {
                case -1364987172:
                    if (trim.equals("cdma1x")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1291802661:
                    if (trim.equals("eutran")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1186047466:
                    break;
                case 98781:
                    if (trim.equals("crl")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 102657:
                    if (trim.equals("gsm")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 108971:
                    if (trim.equals("nfc")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 3211390:
                    if (trim.equals("hrpd")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 96487011:
                    if (trim.equals("ehrpd")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 105037709:
                    if (trim.equals("nr5gc")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 105084116:
                    if (trim.equals("nrepc")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 111620384:
                    if (trim.equals("utran")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    builder.addChildAsBytes(130, bArr);
                    return;
                case 1:
                    builder.addChildAsBytes(133, bArr);
                    return;
                case 2:
                    builder.addChildAsBytes(138, bArr);
                    return;
                case 3:
                    builder.addChildAsBytes((int) NetworkStackConstants.ICMPV6_NEIGHBOR_SOLICITATION, bArr);
                    return;
                case 4:
                    builder.addChildAsBytes(128, bArr);
                    return;
                case 5:
                    builder.addChildAsBytes(134, bArr);
                    return;
                case 6:
                    builder.addChildAsBytes(131, bArr);
                    return;
                case 7:
                    builder.addChildAsBytes((int) UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP, bArr);
                    return;
                case '\b':
                    builder.addChildAsBytes(137, bArr);
                    return;
                case '\t':
                    builder.addChildAsBytes((int) NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT, bArr);
                    return;
                case '\n':
                    builder.addChildAsBytes(129, bArr);
                    return;
                default:
                    loge("Invalid device capability name: " + trim);
                    return;
            }
        } catch (NumberFormatException e) {
            loge("Invalid device capability version number.", e);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    protected byte[] getDeviceId() {
        Phone phone = PhoneFactory.getPhone(getPhoneId());
        return phone == null ? new byte[8] : getDeviceId(phone.getDeviceId(), this.mSpecVersion);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public static byte[] getDeviceId(String str, EuiccSpecVersion euiccSpecVersion) {
        byte[] bArr = new byte[8];
        if (euiccSpecVersion.compareTo(SGP22_V_2_1) >= 0) {
            IccUtils.bcdToBytes(str + 'F', bArr);
            int i = bArr[7] & 255;
            bArr[7] = (byte) ((i >>> 4) | (i << 4));
        } else {
            IccUtils.bcdToBytes(str, bArr);
        }
        return bArr;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    protected Resources getResources() {
        return Resources.getSystem();
    }

    private RequestProvider newRequestProvider(final ApduRequestBuilder apduRequestBuilder) {
        return new RequestProvider() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda51
            @Override // com.android.internal.telephony.uicc.euicc.apdu.RequestProvider
            public final void buildRequest(byte[] bArr, RequestBuilder requestBuilder) {
                EuiccPort.this.lambda$newRequestProvider$49(apduRequestBuilder, bArr, requestBuilder);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$newRequestProvider$49(ApduRequestBuilder apduRequestBuilder, byte[] bArr, RequestBuilder requestBuilder) throws Throwable {
        EuiccSpecVersion orExtractSpecVersion = getOrExtractSpecVersion(bArr);
        if (orExtractSpecVersion == null) {
            throw new EuiccCardException("Cannot get eUICC spec version.");
        }
        try {
            if (orExtractSpecVersion.compareTo(SGP22_V_2_0) < 0) {
                throw new EuiccCardException("eUICC spec version is unsupported: " + orExtractSpecVersion);
            }
            apduRequestBuilder.build(requestBuilder);
        } catch (InvalidAsn1DataException | TagNotFoundException e) {
            throw new EuiccCardException("Cannot parse ASN1 to build request.", e);
        }
    }

    private EuiccSpecVersion getOrExtractSpecVersion(byte[] bArr) {
        EuiccSpecVersion euiccSpecVersion = this.mSpecVersion;
        if (euiccSpecVersion != null) {
            return euiccSpecVersion;
        }
        EuiccSpecVersion fromOpenChannelResponse = EuiccSpecVersion.fromOpenChannelResponse(bArr);
        if (fromOpenChannelResponse != null) {
            synchronized (this.mLock) {
                if (this.mSpecVersion == null) {
                    this.mSpecVersion = fromOpenChannelResponse;
                }
            }
        }
        return fromOpenChannelResponse;
    }

    private <T> void sendApdu(RequestProvider requestProvider, ApduResponseHandler<T> apduResponseHandler, final AsyncResultCallback<T> asyncResultCallback, Handler handler) {
        sendApdu(requestProvider, apduResponseHandler, new ApduExceptionHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda52
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduExceptionHandler
            public final void handleException(Throwable th) {
                EuiccPort.lambda$sendApdu$50(AsyncResultCallback.this, th);
            }
        }, null, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendApdu$50(AsyncResultCallback asyncResultCallback, Throwable th) {
        asyncResultCallback.onException(new EuiccCardException("Cannot send APDU.", th));
    }

    private <T> void sendApdu(RequestProvider requestProvider, ApduResponseHandler<T> apduResponseHandler, ApduIntermediateResultHandler apduIntermediateResultHandler, final AsyncResultCallback<T> asyncResultCallback, Handler handler) {
        sendApdu(requestProvider, apduResponseHandler, new ApduExceptionHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda50
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduExceptionHandler
            public final void handleException(Throwable th) {
                EuiccPort.lambda$sendApdu$51(AsyncResultCallback.this, th);
            }
        }, apduIntermediateResultHandler, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendApdu$51(AsyncResultCallback asyncResultCallback, Throwable th) {
        asyncResultCallback.onException(new EuiccCardException("Cannot send APDU.", th));
    }

    private void sendApduWithSimResetErrorWorkaround(RequestProvider requestProvider, ApduResponseHandler<Void> apduResponseHandler, final AsyncResultCallback<Void> asyncResultCallback, Handler handler) {
        sendApdu(requestProvider, apduResponseHandler, new ApduExceptionHandler() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort$$ExternalSyntheticLambda49
            @Override // com.android.internal.telephony.uicc.euicc.EuiccPort.ApduExceptionHandler
            public final void handleException(Throwable th) {
                EuiccPort.lambda$sendApduWithSimResetErrorWorkaround$52(AsyncResultCallback.this, th);
            }
        }, null, asyncResultCallback, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendApduWithSimResetErrorWorkaround$52(AsyncResultCallback asyncResultCallback, Throwable th) {
        if ((th instanceof ApduException) && ((ApduException) th).getApduStatus() == 28416) {
            logi("Sim is refreshed after disabling profile, no response got.");
            asyncResultCallback.onResult(null);
            return;
        }
        asyncResultCallback.onException(new EuiccCardException("Cannot send APDU.", th));
    }

    private <T> void sendApdu(RequestProvider requestProvider, final ApduResponseHandler<T> apduResponseHandler, final ApduExceptionHandler apduExceptionHandler, final ApduIntermediateResultHandler apduIntermediateResultHandler, final AsyncResultCallback<T> asyncResultCallback, Handler handler) {
        this.mApduSender.send(requestProvider, new ApduSenderResultCallback() { // from class: com.android.internal.telephony.uicc.euicc.EuiccPort.1
            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onResult(byte[] bArr) {
                try {
                    asyncResultCallback.onResult(apduResponseHandler.handleResult(bArr));
                } catch (InvalidAsn1DataException | TagNotFoundException e) {
                    AsyncResultCallback asyncResultCallback2 = asyncResultCallback;
                    asyncResultCallback2.onException(new EuiccCardException("Cannot parse response: " + IccUtils.bytesToHexString(bArr), e));
                } catch (EuiccCardException e2) {
                    asyncResultCallback.onException(e2);
                }
            }

            @Override // com.android.internal.telephony.uicc.euicc.apdu.ApduSenderResultCallback
            public boolean shouldContinueOnIntermediateResult(IccIoResult iccIoResult) {
                ApduIntermediateResultHandler apduIntermediateResultHandler2 = apduIntermediateResultHandler;
                if (apduIntermediateResultHandler2 == null) {
                    return true;
                }
                return apduIntermediateResultHandler2.shouldContinue(iccIoResult);
            }

            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onException(Throwable th) {
                apduExceptionHandler.handleException(th);
            }
        }, handler);
    }

    private static void buildProfile(Asn1Node asn1Node, EuiccProfileInfo.Builder builder) throws TagNotFoundException, InvalidAsn1DataException {
        if (asn1Node.hasChild(144, new int[0])) {
            builder.setNickname(asn1Node.getChild(144, new int[0]).asString());
        }
        if (asn1Node.hasChild(145, new int[0])) {
            builder.setServiceProviderName(asn1Node.getChild(145, new int[0]).asString());
        }
        if (asn1Node.hasChild(146, new int[0])) {
            builder.setProfileName(asn1Node.getChild(146, new int[0]).asString());
        }
        if (asn1Node.hasChild(183, new int[0])) {
            builder.setCarrierIdentifier(buildCarrierIdentifier(asn1Node.getChild(183, new int[0])));
        }
        if (asn1Node.hasChild(40816, new int[0])) {
            if (asn1Node.hasChild(40740, new int[0]) && asn1Node.getChild(40740, new int[0]).asInteger() >= 0) {
                builder.setState(1);
            } else {
                builder.setState(asn1Node.getChild(40816, new int[0]).asInteger());
            }
        } else {
            builder.setState(0);
        }
        if (asn1Node.hasChild(149, new int[0])) {
            builder.setProfileClass(asn1Node.getChild(149, new int[0]).asInteger());
        } else {
            builder.setProfileClass(2);
        }
        if (asn1Node.hasChild((int) SmsMessage.MAX_USER_DATA_SEPTETS_WITH_HEADER, new int[0])) {
            builder.setPolicyRules(asn1Node.getChild((int) SmsMessage.MAX_USER_DATA_SEPTETS_WITH_HEADER, new int[0]).asBits());
        }
        if (asn1Node.hasChild(49014, new int[0])) {
            UiccAccessRule[] buildUiccAccessRule = buildUiccAccessRule(asn1Node.getChild(49014, new int[0]).getChildren(226));
            builder.setUiccAccessRule(buildUiccAccessRule != null ? Arrays.asList(buildUiccAccessRule) : null);
        }
    }

    private static CarrierIdentifier buildCarrierIdentifier(Asn1Node asn1Node) throws InvalidAsn1DataException, TagNotFoundException {
        return new CarrierIdentifier(asn1Node.getChild(128, new int[0]).asBytes(), asn1Node.hasChild(129, new int[0]) ? IccUtils.bytesToHexString(asn1Node.getChild(129, new int[0]).asBytes()) : null, asn1Node.hasChild(130, new int[0]) ? IccUtils.bytesToHexString(asn1Node.getChild(130, new int[0]).asBytes()) : null);
    }

    private static UiccAccessRule[] buildUiccAccessRule(List<Asn1Node> list) throws InvalidAsn1DataException, TagNotFoundException {
        if (list.isEmpty()) {
            return null;
        }
        int size = list.size();
        UiccAccessRule[] uiccAccessRuleArr = new UiccAccessRule[size];
        for (int i = 0; i < size; i++) {
            Asn1Node asn1Node = list.get(i);
            Asn1Node child = asn1Node.getChild(225, new int[0]);
            uiccAccessRuleArr[i] = new UiccAccessRule(child.getChild(193, new int[0]).asBytes(), child.hasChild(202, new int[0]) ? child.getChild(202, new int[0]).asString() : null, asn1Node.hasChild(227, new int[]{219}) ? asn1Node.getChild(227, new int[]{219}).asRawLong() : 0L);
        }
        return uiccAccessRuleArr;
    }

    private static EuiccNotification createNotification(Asn1Node asn1Node) throws TagNotFoundException, InvalidAsn1DataException {
        Asn1Node child;
        if (asn1Node.getTag() == 48943) {
            child = asn1Node;
        } else if (asn1Node.getTag() == 48951) {
            child = asn1Node.getChild(48935, new int[]{48943});
        } else {
            child = asn1Node.getChild(48943, new int[0]);
        }
        return new EuiccNotification(child.getChild(128, new int[0]).asInteger(), child.getChild(12, new int[0]).asString(), child.getChild(129, new int[0]).asBits(), asn1Node.getTag() == 48943 ? null : asn1Node.toBytes());
    }

    private static int parseSimpleResult(byte[] bArr) throws EuiccCardException, TagNotFoundException, InvalidAsn1DataException {
        return parseResponse(bArr).getChild(128, new int[0]).asInteger();
    }

    private static Asn1Node parseResponse(byte[] bArr) throws EuiccCardException, InvalidAsn1DataException {
        Asn1Decoder asn1Decoder = new Asn1Decoder(bArr);
        if (!asn1Decoder.hasNextNode()) {
            throw new EuiccCardException("Empty response", null);
        }
        return asn1Decoder.nextNode();
    }

    private static Asn1Node parseResponseAndCheckSimpleError(byte[] bArr, int i) throws EuiccCardException, InvalidAsn1DataException, TagNotFoundException {
        Asn1Node parseResponse = parseResponse(bArr);
        if (parseResponse.hasChild(129, new int[0])) {
            throw new EuiccCardErrorException(i, parseResponse.getChild(129, new int[0]).asInteger());
        }
        return parseResponse;
    }

    private static String stripTrailingFs(byte[] bArr) {
        return IccUtils.stripTrailingFs(IccUtils.bchToString(bArr, 0, bArr.length));
    }

    private static String padTrailingFs(String str) {
        if (TextUtils.isEmpty(str) || str.length() >= 20) {
            return str;
        }
        return str + new String(new char[20 - str.length()]).replace((char) 0, 'F');
    }

    private static void loge(String str) {
        Rlog.e("EuiccPort", str);
    }

    private static void loge(String str, Throwable th) {
        Rlog.e("EuiccPort", str, th);
    }

    private static void logi(String str) {
        Rlog.i("EuiccPort", str);
    }

    private static void logd(String str) {
        Rlog.d("EuiccPort", str);
    }

    @Override // com.android.internal.telephony.uicc.UiccPort
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("EuiccPort:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mEid=" + this.mEid);
        androidUtilIndentingPrintWriter.println("mSupportedMepMode=" + this.mSupportedMepMode);
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
