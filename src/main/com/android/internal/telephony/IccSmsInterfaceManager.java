package com.android.internal.telephony;

import android.annotation.RequiresPermission;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.emergency.EmergencyNumber;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.SmsDispatchersController;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import com.android.internal.telephony.uicc.IccConstants;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class IccSmsInterfaceManager {
    protected static final int EVENT_SET_BROADCAST_ACTIVATION_DONE = 3;
    protected static final int EVENT_SET_BROADCAST_CONFIG_DONE = 4;
    public static final int SMS_MESSAGE_PERIOD_NOT_SPECIFIED = -1;
    public static final int SMS_MESSAGE_PRIORITY_NOT_SPECIFIED = -1;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final AppOpsManager mAppOps;
    private CdmaBroadcastRangeManager mCdmaBroadcastRangeManager;
    private final LocalLog mCellBroadcastLocalLog;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CellBroadcastRangeManager mCellBroadcastRangeManager;
    @UnsupportedAppUsage
    protected final Context mContext;
    @VisibleForTesting
    public SmsDispatchersController mDispatchersController;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected Handler mHandler;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected Phone mPhone;
    private PhoneFactoryProxy mPhoneFactoryProxy;
    private SmsPermissions mSmsPermissions;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface PhoneFactoryProxy {
        Phone getDefaultPhone();

        Phone getPhone(int i);

        Phone[] getPhones();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Request {
        Object mResult;
        AtomicBoolean mStatus;

        private Request() {
            this.mStatus = new AtomicBoolean(false);
            this.mResult = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IccSmsInterfaceManager(Phone phone) {
        this(phone, phone.getContext(), (AppOpsManager) phone.getContext().getSystemService("appops"), new SmsDispatchersController(phone, phone.mSmsStorageMonitor, phone.mSmsUsageMonitor), new SmsPermissions(phone, phone.getContext(), (AppOpsManager) phone.getContext().getSystemService("appops")));
    }

    @VisibleForTesting
    public IccSmsInterfaceManager(Phone phone, Context context, AppOpsManager appOpsManager, SmsDispatchersController smsDispatchersController, SmsPermissions smsPermissions) {
        this.mCellBroadcastRangeManager = new CellBroadcastRangeManager();
        this.mCdmaBroadcastRangeManager = new CdmaBroadcastRangeManager();
        this.mCellBroadcastLocalLog = new LocalLog(64);
        this.mHandler = new Handler() { // from class: com.android.internal.telephony.IccSmsInterfaceManager.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Request request = (Request) asyncResult.userObj;
                Object obj = null;
                switch (message.what) {
                    case 1:
                        if (asyncResult.exception == null) {
                            obj = IccSmsInterfaceManager.this.buildValidRawData((ArrayList) asyncResult.result);
                            IccSmsInterfaceManager.this.markMessagesAsRead((ArrayList) asyncResult.result);
                        } else if (Rlog.isLoggable("SMS", 3)) {
                            IccSmsInterfaceManager.this.loge("Cannot load Sms records");
                        }
                        notifyPending(request, obj);
                        return;
                    case 2:
                    case 3:
                    case 4:
                    case 6:
                        notifyPending(request, Boolean.valueOf(asyncResult.exception == null));
                        return;
                    case 5:
                        if (asyncResult.exception == null) {
                            obj = (String) asyncResult.result;
                        } else {
                            IccSmsInterfaceManager.this.loge("Cannot read SMSC");
                        }
                        notifyPending(request, obj);
                        return;
                    default:
                        return;
                }
            }

            private void notifyPending(Request request, Object obj) {
                if (request != null) {
                    synchronized (request) {
                        request.mResult = obj;
                        request.mStatus.set(true);
                        request.notifyAll();
                    }
                }
            }
        };
        this.mPhoneFactoryProxy = new PhoneFactoryProxy() { // from class: com.android.internal.telephony.IccSmsInterfaceManager.2
            @Override // com.android.internal.telephony.IccSmsInterfaceManager.PhoneFactoryProxy
            public Phone getPhone(int i) {
                return PhoneFactory.getPhone(i);
            }

            @Override // com.android.internal.telephony.IccSmsInterfaceManager.PhoneFactoryProxy
            public Phone getDefaultPhone() {
                return PhoneFactory.getDefaultPhone();
            }

            @Override // com.android.internal.telephony.IccSmsInterfaceManager.PhoneFactoryProxy
            public Phone[] getPhones() {
                return PhoneFactory.getPhones();
            }
        };
        this.mPhone = phone;
        this.mContext = context;
        this.mAppOps = appOpsManager;
        this.mDispatchersController = smsDispatchersController;
        this.mSmsPermissions = smsPermissions;
    }

    @VisibleForTesting
    public void setPhoneFactoryProxy(PhoneFactoryProxy phoneFactoryProxy) {
        this.mPhoneFactoryProxy = phoneFactoryProxy;
    }

    private void enforceNotOnHandlerThread(String str) {
        if (Looper.myLooper() != this.mHandler.getLooper()) {
            return;
        }
        throw new RuntimeException("This method " + str + " will deadlock if called from the handler's thread.");
    }

    protected void markMessagesAsRead(ArrayList<byte[]> arrayList) {
        if (arrayList == null) {
            return;
        }
        IccFileHandler iccFileHandler = this.mPhone.getIccFileHandler();
        if (iccFileHandler == null) {
            if (Rlog.isLoggable("SMS", 3)) {
                loge("markMessagesAsRead - aborting, no icc card present.");
                return;
            }
            return;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            byte[] bArr = arrayList.get(i);
            if ((bArr[0] & 7) == 3) {
                int length = bArr.length - 1;
                byte[] bArr2 = new byte[length];
                System.arraycopy(bArr, 1, bArr2, 0, length);
                int i2 = i + 1;
                iccFileHandler.updateEFLinearFixed(IccConstants.EF_SMS, i2, makeSmsRecordData(1, bArr2), null, null);
                if (Rlog.isLoggable("SMS", 3)) {
                    log("SMS " + i2 + " marked as read");
                }
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void enforceReceiveAndSend(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.RECEIVE_SMS", str);
        this.mContext.enforceCallingOrSelfPermission("android.permission.SEND_SMS", str);
    }

    private void enforceAccessMessageOnICC(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_MESSAGES_ON_ICC", str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean updateMessageOnIccEf(String str, int i, int i2, byte[] bArr) {
        log("updateMessageOnIccEf: index=" + i + " status=" + i2 + " ==> (" + Arrays.toString(bArr) + ")");
        enforceReceiveAndSend("Updating message on Icc");
        enforceAccessMessageOnICC("Updating message on Icc");
        enforceNotOnHandlerThread("updateMessageOnIccEf");
        if (this.mAppOps.noteOp("android:write_icc_sms", Binder.getCallingUid(), str) != 0) {
            return false;
        }
        Request request = new Request();
        synchronized (request) {
            Message obtainMessage = this.mHandler.obtainMessage(2, request);
            if ((i2 & 1) == 0) {
                if (1 == this.mPhone.getPhoneType()) {
                    this.mPhone.mCi.deleteSmsOnSim(i, obtainMessage);
                } else {
                    this.mPhone.mCi.deleteSmsOnRuim(i, obtainMessage);
                }
            } else {
                IccFileHandler iccFileHandler = this.mPhone.getIccFileHandler();
                if (iccFileHandler == null) {
                    obtainMessage.recycle();
                    return false;
                }
                iccFileHandler.updateEFLinearFixed(IccConstants.EF_SMS, i, makeSmsRecordData(i2, bArr), null, obtainMessage);
            }
            waitForResult(request);
            return ((Boolean) request.mResult).booleanValue();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean copyMessageToIccEf(String str, int i, byte[] bArr, byte[] bArr2) {
        log("copyMessageToIccEf: status=" + i + " ==> pdu=(" + Arrays.toString(bArr) + "), smsc=(" + Arrays.toString(bArr2) + ")");
        enforceReceiveAndSend("Copying message to Icc");
        enforceNotOnHandlerThread("copyMessageToIccEf");
        if (this.mAppOps.noteOp("android:write_icc_sms", Binder.getCallingUid(), str) != 0) {
            return false;
        }
        Request request = new Request();
        synchronized (request) {
            Message obtainMessage = this.mHandler.obtainMessage(2, request);
            if (1 == this.mPhone.getPhoneType()) {
                this.mPhone.mCi.writeSmsToSim(i, IccUtils.bytesToHexString(bArr2), IccUtils.bytesToHexString(bArr), obtainMessage);
            } else {
                this.mPhone.mCi.writeSmsToRuim(i, bArr, obtainMessage);
            }
            waitForResult(request);
        }
        return ((Boolean) request.mResult).booleanValue();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<SmsRawData> getAllMessagesFromIccEf(String str) {
        log("getAllMessagesFromEF");
        this.mContext.enforceCallingOrSelfPermission("android.permission.RECEIVE_SMS", "Reading messages from Icc");
        enforceAccessMessageOnICC("Reading messages from Icc");
        enforceNotOnHandlerThread("getAllMessagesFromIccEf");
        if (this.mAppOps.noteOp("android:read_icc_sms", Binder.getCallingUid(), str) != 0) {
            return new ArrayList();
        }
        Request request = new Request();
        synchronized (request) {
            IccFileHandler iccFileHandler = this.mPhone.getIccFileHandler();
            if (iccFileHandler == null) {
                loge("Cannot load Sms records. No icc card?");
                return null;
            }
            iccFileHandler.loadEFLinearFixedAll(IccConstants.EF_SMS, this.mHandler.obtainMessage(1, request));
            waitForResult(request);
            return (List) request.mResult;
        }
    }

    public void sendDataWithSelfPermissions(String str, String str2, String str3, String str4, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z) {
        if (!this.mSmsPermissions.checkCallingOrSelfCanSendSms(str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(pendingIntent);
        } else {
            sendDataInternal(str, str3, str4, i, bArr, pendingIntent, pendingIntent2, z);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public void sendData(String str, String str2, String str3, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        sendData(str, null, str2, str3, i, bArr, pendingIntent, pendingIntent2);
    }

    public void sendData(String str, String str2, String str3, String str4, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        if (!this.mSmsPermissions.checkCallingCanSendSms(str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(pendingIntent);
        } else {
            sendDataInternal(str, str3, str4, i, bArr, pendingIntent, pendingIntent2, false);
        }
    }

    private void sendDataInternal(String str, String str2, String str3, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z) {
        if (Rlog.isLoggable("SMS", 2)) {
            log("sendData: destAddr=" + str2 + " scAddr=" + str3 + " destPort=" + i + " data='" + HexDump.toHexString(bArr) + "' sentIntent=" + pendingIntent + " deliveryIntent=" + pendingIntent2 + " isForVVM=" + z);
        }
        this.mDispatchersController.sendData(str, filterDestAddress(str2), str3, i, bArr, pendingIntent, pendingIntent2, z);
    }

    public void sendText(String str, String str2, String str3, String str4, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, long j, boolean z2) {
        sendTextInternal(str, str2, str3, str4, pendingIntent, pendingIntent2, z, -1, false, -1, false, j, z2);
    }

    public void sendTextWithSelfPermissions(String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, boolean z2) {
        if (!this.mSmsPermissions.checkCallingOrSelfCanSendSms(str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(pendingIntent);
        } else {
            sendTextInternal(str, str3, str4, str5, pendingIntent, pendingIntent2, z, -1, false, -1, z2, 0L);
        }
    }

    private void sendTextInternal(String str, String str2, String str3, String str4, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, int i, boolean z2, int i2, boolean z3, long j) {
        sendTextInternal(str, str2, str3, str4, pendingIntent, pendingIntent2, z, i, z2, i2, z3, j, false);
    }

    private void sendTextInternal(String str, String str2, String str3, String str4, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, int i, boolean z2, int i2, boolean z3, long j, boolean z4) {
        if (Rlog.isLoggable("SMS", 2)) {
            log("sendText: destAddr=" + str2 + " scAddr=" + str3 + " text='" + str4 + "' sentIntent=" + pendingIntent + " deliveryIntent=" + pendingIntent2 + " priority=" + i + " expectMore=" + z2 + " validityPeriod=" + i2 + " isForVVM=" + z3 + " " + SmsController.formatCrossStackMessageId(j));
        }
        notifyIfOutgoingEmergencySms(str2);
        this.mDispatchersController.sendText(filterDestAddress(str2), str3, str4, pendingIntent, pendingIntent2, null, str, z, i, z2, i2, z3, j, z4);
    }

    public void sendTextWithOptions(String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, int i, boolean z2, int i2) {
        if (!this.mSmsPermissions.checkCallingCanSendText(z, str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(pendingIntent);
        } else {
            sendTextInternal(str, str3, str4, str5, pendingIntent, pendingIntent2, z, i, z2, i2, false, 0L);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void injectSmsPdu(byte[] bArr, String str, final PendingIntent pendingIntent) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            this.mSmsPermissions.enforceCallerIsImsAppOrCarrierApp("injectSmsPdu");
        }
        if (Rlog.isLoggable("SMS", 2)) {
            log("pdu: " + IccUtils.bytesToHexString(bArr) + "\n format=" + str + "\n receivedIntent=" + pendingIntent);
        }
        this.mDispatchersController.injectSmsPdu(bArr, str, false, new SmsDispatchersController.SmsInjectionCallback() { // from class: com.android.internal.telephony.IccSmsInterfaceManager$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.SmsDispatchersController.SmsInjectionCallback
            public final void onSmsInjectedResult(int i) {
                IccSmsInterfaceManager.this.lambda$injectSmsPdu$0(pendingIntent, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$injectSmsPdu$0(PendingIntent pendingIntent, int i) {
        if (pendingIntent != null) {
            try {
                pendingIntent.send(i);
            } catch (PendingIntent.CanceledException unused) {
                loge("receivedIntent cancelled.");
            }
        }
    }

    public void sendMultipartText(String str, String str2, String str3, String str4, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3, boolean z, long j) {
        sendMultipartTextWithOptions(str, str2, str3, str4, list, list2, list3, z, -1, false, -1, j);
    }

    public void sendMultipartTextWithOptions(String str, String str2, String str3, String str4, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3, boolean z, int i, boolean z2, int i2, long j) {
        String concat;
        if (!this.mSmsPermissions.checkCallingCanSendText(z, str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(list2);
            return;
        }
        int i3 = 0;
        if (Rlog.isLoggable("SMS", 2)) {
            Iterator<String> it = list.iterator();
            int i4 = 0;
            while (it.hasNext()) {
                log("sendMultipartTextWithOptions: destAddr=" + str3 + ", srAddr=" + str4 + ", part[" + i4 + "]=" + it.next() + " " + SmsController.formatCrossStackMessageId(j));
                i4++;
            }
        }
        notifyIfOutgoingEmergencySms(str3);
        String filterDestAddress = filterDestAddress(str3);
        if (list.size() > 1 && list.size() < 10 && !SmsMessage.hasEmsSupport()) {
            while (i3 < list.size()) {
                String str5 = list.get(i3);
                if (SmsMessage.shouldAppendPageNumberAsPrefix()) {
                    concat = String.valueOf(i3 + 1) + '/' + list.size() + ' ' + str5;
                } else {
                    concat = str5.concat(' ' + String.valueOf(i3 + 1) + '/' + list.size());
                }
                String str6 = concat;
                PendingIntent pendingIntent = null;
                PendingIntent pendingIntent2 = (list2 == null || list2.size() <= i3) ? null : list2.get(i3);
                if (list3 != null && list3.size() > i3) {
                    pendingIntent = list3.get(i3);
                }
                this.mDispatchersController.sendText(filterDestAddress, str4, str6, pendingIntent2, pendingIntent, null, str, z, i, z2, i2, false, j);
                i3++;
            }
            return;
        }
        this.mDispatchersController.sendMultipartText(filterDestAddress, str4, (ArrayList) list, (ArrayList) list2, (ArrayList) list3, null, str, z, i, z2, i2, j);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getPremiumSmsPermission(String str) {
        return this.mDispatchersController.getPremiumSmsPermission(str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setPremiumSmsPermission(String str, int i) {
        this.mDispatchersController.setPremiumSmsPermission(str, i);
    }

    protected ArrayList<SmsRawData> buildValidRawData(ArrayList<byte[]> arrayList) {
        int size = arrayList.size();
        ArrayList<SmsRawData> arrayList2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if ((arrayList.get(i)[0] & 1) == 0) {
                arrayList2.add(null);
            } else {
                arrayList2.add(new SmsRawData(arrayList.get(i)));
            }
        }
        return arrayList2;
    }

    protected byte[] makeSmsRecordData(int i, byte[] bArr) {
        byte[] bArr2 = 1 == this.mPhone.getPhoneType() ? new byte[176] : new byte[255];
        bArr2[0] = (byte) (i & 7);
        System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
        for (int length = bArr.length + 1; length < bArr2.length; length++) {
            bArr2[length] = -1;
        }
        return bArr2;
    }

    public String getSmscAddressFromIccEf(String str) {
        if (this.mSmsPermissions.checkCallingOrSelfCanGetSmscAddress(str, "getSmscAddressFromIccEf")) {
            enforceNotOnHandlerThread("getSmscAddressFromIccEf");
            Request request = new Request();
            synchronized (request) {
                this.mPhone.mCi.getSmscAddress(this.mHandler.obtainMessage(5, request));
                waitForResult(request);
            }
            return (String) request.mResult;
        }
        return null;
    }

    public boolean setSmscAddressOnIccEf(String str, String str2) {
        if (this.mSmsPermissions.checkCallingOrSelfCanSetSmscAddress(str, "setSmscAddressOnIccEf")) {
            enforceNotOnHandlerThread("setSmscAddressOnIccEf");
            Request request = new Request();
            synchronized (request) {
                this.mPhone.mCi.setSmscAddress(str2, this.mHandler.obtainMessage(6, request));
                waitForResult(request);
            }
            return ((Boolean) request.mResult).booleanValue();
        }
        return false;
    }

    public boolean enableCellBroadcast(int i, int i2) {
        return enableCellBroadcastRange(i, i, i2);
    }

    public boolean disableCellBroadcast(int i, int i2) {
        return disableCellBroadcastRange(i, i, i2);
    }

    public boolean enableCellBroadcastRange(int i, int i2, int i3) {
        Context context = this.mContext;
        context.enforceCallingPermission("android.permission.RECEIVE_EMERGENCY_BROADCAST", "enabling cell broadcast range [" + i + "-" + i2 + "]. ranType=" + i3);
        if (i3 == 1) {
            return enableGsmBroadcastRange(i, i2);
        }
        if (i3 == 2) {
            return enableCdmaBroadcastRange(i, i2);
        }
        throw new IllegalArgumentException("Not a supported RAN Type");
    }

    public boolean disableCellBroadcastRange(int i, int i2, int i3) {
        Context context = this.mContext;
        context.enforceCallingPermission("android.permission.RECEIVE_EMERGENCY_BROADCAST", "disabling cell broadcast range [" + i + "-" + i2 + "]. ranType=" + i3);
        if (i3 == 1) {
            return disableGsmBroadcastRange(i, i2);
        }
        if (i3 == 2) {
            return disableCdmaBroadcastRange(i, i2);
        }
        throw new IllegalArgumentException("Not a supported RAN Type");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized boolean enableGsmBroadcastRange(int i, int i2) {
        this.mContext.enforceCallingPermission("android.permission.RECEIVE_EMERGENCY_BROADCAST", "Enabling cell broadcast SMS");
        if (!this.mCellBroadcastRangeManager.enableRange(i, i2, this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            String str = "Failed to add GSM cell broadcast channels range " + i + " to " + i2;
            log(str);
            this.mCellBroadcastLocalLog.log(str);
            return false;
        }
        String str2 = "Added GSM cell broadcast channels range " + i + " to " + i2;
        log(str2);
        this.mCellBroadcastLocalLog.log(str2);
        setCellBroadcastActivation(this.mCellBroadcastRangeManager.isEmpty() ? false : true);
        return true;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized boolean disableGsmBroadcastRange(int i, int i2) {
        this.mContext.enforceCallingPermission("android.permission.RECEIVE_EMERGENCY_BROADCAST", "Disabling cell broadcast SMS");
        if (!this.mCellBroadcastRangeManager.disableRange(i, i2, this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            String str = "Failed to remove GSM cell broadcast channels range " + i + " to " + i2;
            log(str);
            this.mCellBroadcastLocalLog.log(str);
            return false;
        }
        String str2 = "Removed GSM cell broadcast channels range " + i + " to " + i2;
        log(str2);
        this.mCellBroadcastLocalLog.log(str2);
        setCellBroadcastActivation(this.mCellBroadcastRangeManager.isEmpty() ? false : true);
        return true;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized boolean enableCdmaBroadcastRange(int i, int i2) {
        this.mContext.enforceCallingPermission("android.permission.RECEIVE_EMERGENCY_BROADCAST", "Enabling cdma broadcast SMS");
        if (!this.mCdmaBroadcastRangeManager.enableRange(i, i2, this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            String str = "Failed to add cdma broadcast channels range " + i + " to " + i2;
            log(str);
            this.mCellBroadcastLocalLog.log(str);
            return false;
        }
        String str2 = "Added cdma broadcast channels range " + i + " to " + i2;
        log(str2);
        this.mCellBroadcastLocalLog.log(str2);
        setCdmaBroadcastActivation(this.mCdmaBroadcastRangeManager.isEmpty() ? false : true);
        return true;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized boolean disableCdmaBroadcastRange(int i, int i2) {
        this.mContext.enforceCallingPermission("android.permission.RECEIVE_EMERGENCY_BROADCAST", "Disabling cell broadcast SMS");
        if (!this.mCdmaBroadcastRangeManager.disableRange(i, i2, this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            String str = "Failed to remove cdma broadcast channels range " + i + " to " + i2;
            log(str);
            this.mCellBroadcastLocalLog.log(str);
            return false;
        }
        String str2 = "Removed cdma broadcast channels range " + i + " to " + i2;
        log(str2);
        this.mCellBroadcastLocalLog.log(str2);
        setCdmaBroadcastActivation(this.mCdmaBroadcastRangeManager.isEmpty() ? false : true);
        return true;
    }

    @RequiresPermission("android.permission.MODIFY_CELL_BROADCASTS")
    public void resetAllCellBroadcastRanges() {
        this.mContext.enforceCallingPermission("android.permission.MODIFY_CELL_BROADCASTS", "resetAllCellBroadcastRanges");
        this.mCdmaBroadcastRangeManager.clearRanges();
        this.mCellBroadcastRangeManager.clearRanges();
        log("Cell broadcast ranges reset.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CellBroadcastRangeManager extends IntRangeManager {
        private ArrayList<SmsBroadcastConfigInfo> mConfigList = new ArrayList<>();

        CellBroadcastRangeManager() {
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void startUpdate() {
            this.mConfigList.clear();
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void addRange(int i, int i2, boolean z) {
            this.mConfigList.add(new SmsBroadcastConfigInfo(i, i2, 0, 255, z));
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected boolean finishUpdate() {
            if (this.mConfigList.isEmpty()) {
                return true;
            }
            ArrayList<SmsBroadcastConfigInfo> arrayList = this.mConfigList;
            return IccSmsInterfaceManager.this.setCellBroadcastConfig((SmsBroadcastConfigInfo[]) arrayList.toArray(new SmsBroadcastConfigInfo[arrayList.size()]));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CdmaBroadcastRangeManager extends IntRangeManager {
        private ArrayList<CdmaSmsBroadcastConfigInfo> mConfigList = new ArrayList<>();

        CdmaBroadcastRangeManager() {
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void startUpdate() {
            this.mConfigList.clear();
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void addRange(int i, int i2, boolean z) {
            this.mConfigList.add(new CdmaSmsBroadcastConfigInfo(i, i2, 1, z));
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected boolean finishUpdate() {
            if (this.mConfigList.isEmpty()) {
                return true;
            }
            ArrayList<CdmaSmsBroadcastConfigInfo> arrayList = this.mConfigList;
            return IccSmsInterfaceManager.this.setCdmaBroadcastConfig((CdmaSmsBroadcastConfigInfo[]) arrayList.toArray(new CdmaSmsBroadcastConfigInfo[arrayList.size()]));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean setCellBroadcastConfig(SmsBroadcastConfigInfo[] smsBroadcastConfigInfoArr) {
        log("Calling setGsmBroadcastConfig with " + smsBroadcastConfigInfoArr.length + " configurations");
        enforceNotOnHandlerThread("setCellBroadcastConfig");
        Request request = new Request();
        synchronized (request) {
            this.mPhone.mCi.setGsmBroadcastConfig(smsBroadcastConfigInfoArr, this.mHandler.obtainMessage(4, request));
            waitForResult(request);
        }
        return ((Boolean) request.mResult).booleanValue();
    }

    private boolean setCellBroadcastActivation(boolean z) {
        log("Calling setCellBroadcastActivation(" + z + ')');
        enforceNotOnHandlerThread("setCellBroadcastConfig");
        Request request = new Request();
        synchronized (request) {
            this.mPhone.mCi.setGsmBroadcastActivation(z, this.mHandler.obtainMessage(3, request));
            waitForResult(request);
        }
        return ((Boolean) request.mResult).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean setCdmaBroadcastConfig(CdmaSmsBroadcastConfigInfo[] cdmaSmsBroadcastConfigInfoArr) {
        log("Calling setCdmaBroadcastConfig with " + cdmaSmsBroadcastConfigInfoArr.length + " configurations");
        enforceNotOnHandlerThread("setCdmaBroadcastConfig");
        Request request = new Request();
        synchronized (request) {
            this.mPhone.mCi.setCdmaBroadcastConfig(cdmaSmsBroadcastConfigInfoArr, this.mHandler.obtainMessage(4, request));
            waitForResult(request);
        }
        return ((Boolean) request.mResult).booleanValue();
    }

    private boolean setCdmaBroadcastActivation(boolean z) {
        log("Calling setCdmaBroadcastActivation(" + z + ")");
        enforceNotOnHandlerThread("setCdmaBroadcastActivation");
        Request request = new Request();
        synchronized (request) {
            this.mPhone.mCi.setCdmaBroadcastActivation(z, this.mHandler.obtainMessage(3, request));
            waitForResult(request);
        }
        return ((Boolean) request.mResult).booleanValue();
    }

    @UnsupportedAppUsage
    protected void log(String str) {
        Rlog.d("IccSmsInterfaceManager", str);
    }

    protected void loge(String str) {
        Rlog.e("IccSmsInterfaceManager", str);
    }

    protected void loge(String str, Throwable th) {
        Rlog.e("IccSmsInterfaceManager", str, th);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isImsSmsSupported() {
        return this.mDispatchersController.isIms();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getImsSmsFormat() {
        return this.mDispatchersController.getImsSmsFormat();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public void sendStoredText(String str, Uri uri, String str2, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        sendStoredText(str, null, uri, str2, pendingIntent, pendingIntent2);
    }

    public void sendStoredText(String str, String str2, Uri uri, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        if (!this.mSmsPermissions.checkCallingCanSendSms(str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(pendingIntent);
            return;
        }
        if (Rlog.isLoggable("SMS", 2)) {
            log("sendStoredText: scAddr=" + str3 + " messageUri=" + uri + " sentIntent=" + pendingIntent + " deliveryIntent=" + pendingIntent2);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (!isFailedOrDraft(contentResolver, uri)) {
            loge("sendStoredText: not FAILED or DRAFT message");
            returnUnspecifiedFailure(pendingIntent);
            return;
        }
        String[] loadTextAndAddress = loadTextAndAddress(contentResolver, uri);
        if (loadTextAndAddress == null) {
            loge("sendStoredText: can not load text");
            returnUnspecifiedFailure(pendingIntent);
            return;
        }
        notifyIfOutgoingEmergencySms(loadTextAndAddress[1]);
        String filterDestAddress = filterDestAddress(loadTextAndAddress[1]);
        loadTextAndAddress[1] = filterDestAddress;
        this.mDispatchersController.sendText(filterDestAddress, str3, loadTextAndAddress[0], pendingIntent, pendingIntent2, uri, str, true, -1, false, -1, false, 0L);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public void sendStoredMultipartText(String str, Uri uri, String str2, List<PendingIntent> list, List<PendingIntent> list2) {
        sendStoredMultipartText(str, null, uri, str2, list, list2);
    }

    public void sendStoredMultipartText(String str, String str2, Uri uri, String str3, List<PendingIntent> list, List<PendingIntent> list2) {
        String concat;
        List<PendingIntent> list3 = list;
        List<PendingIntent> list4 = list2;
        if (!this.mSmsPermissions.checkCallingCanSendSms(str, str2, "Sending SMS message")) {
            returnUnspecifiedFailure(list3);
            return;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (!isFailedOrDraft(contentResolver, uri)) {
            loge("sendStoredMultipartText: not FAILED or DRAFT message");
            returnUnspecifiedFailure(list3);
            return;
        }
        String[] loadTextAndAddress = loadTextAndAddress(contentResolver, uri);
        if (loadTextAndAddress == null) {
            loge("sendStoredMultipartText: can not load text");
            returnUnspecifiedFailure(list3);
            return;
        }
        ArrayList<String> divideMessage = SmsManager.getDefault().divideMessage(loadTextAndAddress[0]);
        if (divideMessage != null) {
            char c = 1;
            if (divideMessage.size() >= 1) {
                notifyIfOutgoingEmergencySms(loadTextAndAddress[1]);
                loadTextAndAddress[1] = filterDestAddress(loadTextAndAddress[1]);
                if (divideMessage.size() > 1 && divideMessage.size() < 10 && !SmsMessage.hasEmsSupport()) {
                    int i = 0;
                    while (i < divideMessage.size()) {
                        String str4 = divideMessage.get(i);
                        if (SmsMessage.shouldAppendPageNumberAsPrefix()) {
                            concat = String.valueOf(i + 1) + '/' + divideMessage.size() + ' ' + str4;
                        } else {
                            concat = str4.concat(' ' + String.valueOf(i + 1) + '/' + divideMessage.size());
                        }
                        String str5 = concat;
                        PendingIntent pendingIntent = null;
                        PendingIntent pendingIntent2 = (list3 == null || list.size() <= i) ? null : list3.get(i);
                        if (list4 != null && list2.size() > i) {
                            pendingIntent = list4.get(i);
                        }
                        this.mDispatchersController.sendText(loadTextAndAddress[c], str3, str5, pendingIntent2, pendingIntent, uri, str, true, -1, false, -1, false, 0L);
                        i++;
                        list3 = list;
                        list4 = list2;
                        c = c;
                        divideMessage = divideMessage;
                    }
                    return;
                }
                this.mDispatchersController.sendMultipartText(loadTextAndAddress[1], str3, divideMessage, (ArrayList) list, (ArrayList) list2, uri, str, true, -1, false, -1, 0L);
                return;
            }
        }
        loge("sendStoredMultipartText: can not divide text");
        returnUnspecifiedFailure(list3);
    }

    public int getSmsCapacityOnIcc(String str, String str2) {
        boolean checkCallingOrSelfReadPhoneState = TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, this.mPhone.getSubId(), str, str2, "getSmsCapacityOnIcc");
        int i = 0;
        if (checkCallingOrSelfReadPhoneState) {
            if (this.mPhone.getIccRecordsLoaded()) {
                UiccProfile uiccProfileForPhone = UiccController.getInstance().getUiccProfileForPhone(this.mPhone.getPhoneId());
                if (uiccProfileForPhone != null) {
                    i = uiccProfileForPhone.getIccRecords().getSmsCapacityOnIcc();
                } else {
                    loge("uiccProfile is null");
                }
            } else {
                loge("getSmsCapacityOnIcc - aborting, no icc card present.");
            }
            log("getSmsCapacityOnIcc().numberOnIcc = " + i);
            return i;
        }
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0031, code lost:
        if (r4 != null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x003c, code lost:
        if (r4 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x003e, code lost:
        r4.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0041, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0044, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean isFailedOrDraft(ContentResolver contentResolver, Uri uri) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z = true;
        Cursor cursor = null;
        try {
            try {
                cursor = contentResolver.query(uri, new String[]{"type"}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int i = cursor.getInt(0);
                    if (i != 3 && i != 5) {
                        z = false;
                    }
                    cursor.close();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return z;
                }
            } catch (SQLiteException e) {
                this.loge("isFailedOrDraft: query message type failed", e);
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x003a, code lost:
        if (r13 != null) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0046, code lost:
        if (r13 == null) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0048, code lost:
        r13.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x004b, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x004e, code lost:
        return null;
     */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0053  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private String[] loadTextAndAddress(ContentResolver contentResolver, Uri uri) {
        Cursor cursor;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Cursor cursor2 = null;
        try {
            cursor = contentResolver.query(uri, new String[]{"body", "address"}, null, null, null);
            if (cursor != null) {
                try {
                    try {
                        if (cursor.moveToFirst()) {
                            String[] strArr = {cursor.getString(0), cursor.getString(1)};
                            cursor.close();
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return strArr;
                        }
                    } catch (SQLiteException e) {
                        e = e;
                        loge("loadText: query message text failed", e);
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            }
        } catch (SQLiteException e2) {
            e = e2;
            cursor = null;
        } catch (Throwable th2) {
            th = th2;
            if (cursor2 != null) {
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @VisibleForTesting
    public void notifyIfOutgoingEmergencySms(String str) {
        EmergencyNumber emergencyNumber;
        Phone[] phones = this.mPhoneFactoryProxy.getPhones();
        EmergencyNumber emergencyNumber2 = this.mPhone.getEmergencyNumberTracker().getEmergencyNumber(str);
        if (emergencyNumber2 != null) {
            this.mPhone.notifyOutgoingEmergencySms(emergencyNumber2);
        } else if (phones.length > 1) {
            for (Phone phone : phones) {
                if (phone.getPhoneId() != this.mPhone.getPhoneId() && (emergencyNumber = phone.getEmergencyNumberTracker().getEmergencyNumber(str)) != null) {
                    this.mPhone.notifyOutgoingEmergencySms(emergencyNumber);
                    return;
                }
            }
        }
    }

    private void returnUnspecifiedFailure(PendingIntent pendingIntent) {
        if (pendingIntent != null) {
            try {
                pendingIntent.send(1);
            } catch (PendingIntent.CanceledException unused) {
            }
        }
    }

    private void returnUnspecifiedFailure(List<PendingIntent> list) {
        if (list == null) {
            return;
        }
        for (PendingIntent pendingIntent : list) {
            returnUnspecifiedFailure(pendingIntent);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String filterDestAddress(String str) {
        String filterDestAddr = SmsNumberUtils.filterDestAddr(this.mContext, this.mPhone.getSubId(), str);
        return filterDestAddr != null ? filterDestAddr : str;
    }

    private void waitForResult(Request request) {
        synchronized (request) {
            while (!request.mStatus.get()) {
                try {
                    request.wait();
                } catch (InterruptedException unused) {
                    log("Interrupted while waiting for result");
                }
            }
        }
    }

    public InboundSmsHandler getInboundSmsHandler(boolean z) {
        return this.mDispatchersController.getInboundSmsHandler(z);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Enabled GSM channels: " + this.mCellBroadcastRangeManager);
        printWriter.println("Enabled CDMA channels: " + this.mCdmaBroadcastRangeManager);
        printWriter.println("CellBroadcast log:");
        this.mCellBroadcastLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("SMS dispatcher controller log:");
        this.mDispatchersController.dump(fileDescriptor, printWriter, strArr);
        printWriter.flush();
    }
}
