package com.android.internal.telephony;

import android.app.PendingIntent;
import android.net.Uri;
import android.p008os.Bundle;
import com.android.internal.telephony.ISms;
import java.util.List;
/* loaded from: classes3.dex */
public class ISmsImplBase extends ISms.Stub {
    @Override // com.android.internal.telephony.ISms
    public List<SmsRawData> getAllMessagesFromIccEfForSubscriber(int subId, String callingPkg) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean updateMessageOnIccEfForSubscriber(int subId, String callingPkg, int messageIndex, int newStatus, byte[] pdu) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean copyMessageToIccEfForSubscriber(int subId, String callingPkg, int status, byte[] pdu, byte[] smsc) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendDataForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, int destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendTextForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean persistMessageForNonDefaultSmsApp, long messageId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendTextForSubscriberWithOptions(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean persistMessageForNonDefaultSmsApp, int priority, boolean expectMore, int validityPeriod) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void injectSmsPduForSubscriber(int subId, byte[] pdu, String format, PendingIntent receivedIntent) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendMultipartTextForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, boolean persistMessageForNonDefaultSmsApp, long messageId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendMultipartTextForSubscriberWithOptions(int subId, String callingPkg, String callingAttributionTag, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, boolean persistMessageForNonDefaultSmsApp, int priority, boolean expectMore, int validityPeriod) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean enableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean disableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean enableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId, int ranType) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean disableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId, int ranType) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public int getPremiumSmsPermission(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public int getPremiumSmsPermissionForSubscriber(int subId, String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void setPremiumSmsPermission(String packageName, int permission) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void setPremiumSmsPermissionForSubscriber(int subId, String packageName, int permission) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean isImsSmsSupportedForSubscriber(int subId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean isSmsSimPickActivityNeeded(int subId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public int getPreferredSmsSubscription() {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public String getImsSmsFormatForSubscriber(int subId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean isSMSPromptEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendStoredText(int subId, String callingPkg, String callingAttributionTag, Uri messageUri, String scAddress, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void sendStoredMultipartText(int subId, String callingPkg, String callingAttributionTag, Uri messageUri, String scAddress, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public Bundle getCarrierConfigValuesForSubscriber(int subId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public String createAppSpecificSmsToken(int subId, String callingPkg, PendingIntent intent) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public String createAppSpecificSmsTokenWithPackageInfo(int subId, String callingPkg, String prefixes, PendingIntent intent) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void setStorageMonitorMemoryStatusOverride(int subId, boolean storageAvailable) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public void clearStorageMonitorMemoryStatusOverride(int subId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public int checkSmsShortCodeDestination(int subid, String callingPackage, String callingFeatureId, String destAddress, String countryIso) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public String getSmscAddressFromIccEfForSubscriber(int subId, String callingPackage) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean setSmscAddressOnIccEfForSubscriber(String smsc, int subId, String callingPackage) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public int getSmsCapacityOnIccForSubscriber(int subId) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.internal.telephony.ISms
    public boolean resetAllCellBroadcastRanges(int subId) {
        throw new UnsupportedOperationException();
    }
}
