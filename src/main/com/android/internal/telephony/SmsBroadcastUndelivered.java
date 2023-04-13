package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.os.PersistableBundle;
import android.os.UserManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import com.android.internal.telephony.cdma.CdmaInboundSmsHandler;
import com.android.internal.telephony.gsm.GsmInboundSmsHandler;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.telephony.Rlog;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes.dex */
public class SmsBroadcastUndelivered {
    private static final String[] PDU_PENDING_MESSAGE_PROJECTION = {"pdu", "sequence", "destination_port", "date", "reference_number", "count", "address", "_id", "message_body", "display_originating_addr", "sub_id"};
    static final Map<Integer, Integer> PDU_PENDING_MESSAGE_PROJECTION_INDEX_MAPPING = Map.ofEntries(Map.entry(0, 0), Map.entry(1, 1), Map.entry(2, 2), Map.entry(3, 3), Map.entry(4, 4), Map.entry(5, 5), Map.entry(6, 6), Map.entry(7, 7), Map.entry(8, 8), Map.entry(9, 9), Map.entry(11, 10));
    private static SmsBroadcastUndelivered instance;
    private final BroadcastReceiver mBroadcastReceiver;
    private final ContentResolver mResolver;

    /* loaded from: classes.dex */
    private class ScanRawTableThread extends Thread {
        private final Context context;

        private ScanRawTableThread(Context context) {
            this.context = context;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            SmsBroadcastUndelivered.scanRawTable(this.context, System.currentTimeMillis() - SmsBroadcastUndelivered.this.getUndeliveredSmsExpirationTime(this.context));
            InboundSmsHandler.cancelNewMessageNotification(this.context);
        }
    }

    public static void initialize(Context context, GsmInboundSmsHandler gsmInboundSmsHandler, CdmaInboundSmsHandler cdmaInboundSmsHandler) {
        if (instance == null) {
            instance = new SmsBroadcastUndelivered(context);
        }
        if (gsmInboundSmsHandler != null) {
            gsmInboundSmsHandler.sendMessage(6);
        }
        if (cdmaInboundSmsHandler != null) {
            cdmaInboundSmsHandler.sendMessage(6);
        }
    }

    @UnsupportedAppUsage
    private SmsBroadcastUndelivered(Context context) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SmsBroadcastUndelivered.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Rlog.d("SmsBroadcastUndelivered", "Received broadcast " + intent.getAction());
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    new ScanRawTableThread(context2).start();
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mResolver = context.getContentResolver();
        if (((UserManager) context.getSystemService("user")).isUserUnlocked()) {
            new ScanRawTableThread(context).start();
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void scanRawTable(Context context, long j) {
        StringBuilder sb;
        Cursor query;
        Rlog.d("SmsBroadcastUndelivered", "scanning raw table for undelivered messages");
        long nanoTime = System.nanoTime();
        ContentResolver contentResolver = context.getContentResolver();
        HashMap hashMap = new HashMap(4);
        HashSet hashSet = new HashSet(4);
        Cursor cursor = null;
        try {
            try {
                query = contentResolver.query(InboundSmsHandler.sRawUri, PDU_PENDING_MESSAGE_PROJECTION, "deleted = 0", null, null);
            } catch (SQLException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
        } catch (SQLException e2) {
            e = e2;
            cursor = query;
            Rlog.e("SmsBroadcastUndelivered", "error reading pending SMS messages", e);
            if (cursor != null) {
                cursor.close();
            }
            sb = new StringBuilder();
            sb.append("finished scanning raw table in ");
            sb.append((System.nanoTime() - nanoTime) / TimeUtils.NANOS_PER_MS);
            sb.append(" ms");
            Rlog.d("SmsBroadcastUndelivered", sb.toString());
        } catch (Throwable th2) {
            th = th2;
            cursor = query;
            if (cursor != null) {
                cursor.close();
            }
            Rlog.d("SmsBroadcastUndelivered", "finished scanning raw table in " + ((System.nanoTime() - nanoTime) / TimeUtils.NANOS_PER_MS) + " ms");
            throw th;
        }
        if (query == null) {
            Rlog.e("SmsBroadcastUndelivered", "error getting pending message cursor");
            if (query != null) {
                query.close();
            }
            Rlog.d("SmsBroadcastUndelivered", "finished scanning raw table in " + ((System.nanoTime() - nanoTime) / TimeUtils.NANOS_PER_MS) + " ms");
            return;
        }
        boolean isCurrentFormat3gpp2 = InboundSmsHandler.isCurrentFormat3gpp2();
        while (query.moveToNext()) {
            try {
            } catch (IllegalArgumentException e3) {
                e = e3;
            }
            try {
                InboundSmsTracker makeInboundSmsTracker = TelephonyComponentFactory.getInstance().inject(InboundSmsTracker.class.getName()).makeInboundSmsTracker(context, query, isCurrentFormat3gpp2);
                if (makeInboundSmsTracker.getMessageCount() == 1) {
                    broadcastSms(makeInboundSmsTracker);
                } else {
                    SmsReferenceKey smsReferenceKey = new SmsReferenceKey(makeInboundSmsTracker);
                    Integer num = (Integer) hashMap.get(smsReferenceKey);
                    if (num == null) {
                        hashMap.put(smsReferenceKey, 1);
                        if (makeInboundSmsTracker.getTimestamp() < j) {
                            hashSet.add(smsReferenceKey);
                        }
                    } else {
                        int intValue = num.intValue() + 1;
                        if (intValue == makeInboundSmsTracker.getMessageCount()) {
                            Rlog.d("SmsBroadcastUndelivered", "found complete multi-part message");
                            broadcastSms(makeInboundSmsTracker);
                            hashSet.remove(smsReferenceKey);
                        } else {
                            hashMap.put(smsReferenceKey, Integer.valueOf(intValue));
                        }
                    }
                }
            } catch (IllegalArgumentException e4) {
                e = e4;
                Rlog.e("SmsBroadcastUndelivered", "error loading SmsTracker: " + e);
            }
        }
        Phone phone = PhoneFactory.getPhone(0);
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            SmsReferenceKey smsReferenceKey2 = (SmsReferenceKey) it.next();
            int delete = contentResolver.delete(InboundSmsHandler.sRawUriPermanentDelete, smsReferenceKey2.getDeleteWhere(), smsReferenceKey2.getDeleteWhereArgs());
            if (delete == 0) {
                Rlog.e("SmsBroadcastUndelivered", "No rows were deleted from raw table!");
            } else {
                Rlog.d("SmsBroadcastUndelivered", "Deleted " + delete + " rows from raw table for incomplete " + smsReferenceKey2.mMessageCount + " part message");
            }
            if (delete > 0) {
                TelephonyMetrics.getInstance().writeDroppedIncomingMultipartSms(0, smsReferenceKey2.mFormat, delete, smsReferenceKey2.mMessageCount);
                if (phone != null) {
                    phone.getSmsStats().onDroppedIncomingMultipartSms(smsReferenceKey2.mIs3gpp2, delete, smsReferenceKey2.mMessageCount);
                }
            }
        }
        query.close();
        sb = new StringBuilder();
        sb.append("finished scanning raw table in ");
        sb.append((System.nanoTime() - nanoTime) / TimeUtils.NANOS_PER_MS);
        sb.append(" ms");
        Rlog.d("SmsBroadcastUndelivered", sb.toString());
    }

    private static void broadcastSms(InboundSmsTracker inboundSmsTracker) {
        int phoneId;
        int subId = inboundSmsTracker.getSubId();
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            phoneId = SubscriptionManagerService.getInstance().getPhoneId(subId);
        } else {
            phoneId = SubscriptionController.getInstance().getPhoneId(subId);
        }
        if (!SubscriptionManager.isValidPhoneId(phoneId)) {
            Rlog.e("SmsBroadcastUndelivered", "broadcastSms: ignoring message; no phone found for subId " + subId);
            return;
        }
        Phone phone = PhoneFactory.getPhone(phoneId);
        if (phone == null) {
            Rlog.e("SmsBroadcastUndelivered", "broadcastSms: ignoring message; no phone found for subId " + subId + " phoneId " + phoneId);
            return;
        }
        InboundSmsHandler inboundSmsHandler = phone.getInboundSmsHandler(inboundSmsTracker.is3gpp2());
        if (inboundSmsHandler != null) {
            inboundSmsHandler.sendMessage(2, inboundSmsTracker);
            return;
        }
        Rlog.e("SmsBroadcastUndelivered", "null handler for " + inboundSmsTracker.getFormat() + " format, can't deliver.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getUndeliveredSmsExpirationTime(Context context) {
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService("carrier_config")).getConfigForSubId(SubscriptionManager.getDefaultSmsSubscriptionId());
        if (configForSubId != null) {
            return configForSubId.getLong("undelivered_sms_message_expiration_time", 604800000L);
        }
        return 604800000L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SmsReferenceKey {
        final String mAddress;
        final String mFormat;
        final boolean mIs3gpp2;
        final int mMessageCount;
        final String mQuery;
        final int mReferenceNumber;

        SmsReferenceKey(InboundSmsTracker inboundSmsTracker) {
            this.mAddress = inboundSmsTracker.getAddress();
            this.mReferenceNumber = inboundSmsTracker.getReferenceNumber();
            this.mMessageCount = inboundSmsTracker.getMessageCount();
            this.mQuery = inboundSmsTracker.getQueryForSegments();
            this.mIs3gpp2 = inboundSmsTracker.is3gpp2();
            this.mFormat = inboundSmsTracker.getFormat();
        }

        String[] getDeleteWhereArgs() {
            return new String[]{this.mAddress, Integer.toString(this.mReferenceNumber), Integer.toString(this.mMessageCount)};
        }

        String getDeleteWhere() {
            return this.mQuery;
        }

        public int hashCode() {
            return (((this.mReferenceNumber * 31) + this.mMessageCount) * 31) + this.mAddress.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof SmsReferenceKey) {
                SmsReferenceKey smsReferenceKey = (SmsReferenceKey) obj;
                return smsReferenceKey.mAddress.equals(this.mAddress) && smsReferenceKey.mReferenceNumber == this.mReferenceNumber && smsReferenceKey.mMessageCount == this.mMessageCount;
            }
            return false;
        }
    }
}
