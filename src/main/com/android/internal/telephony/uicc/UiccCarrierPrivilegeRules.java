package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.telephony.UiccAccessRule;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class UiccCarrierPrivilegeRules extends Handler {
    private int mAIDInUse;
    private List<UiccAccessRule> mAccessRules;
    private int mChannelId;
    private boolean mCheckedRules;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Message mLoadedCallback;
    private int mRetryCount;
    private final Runnable mRetryRunnable;
    private String mRules;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private AtomicInteger mState;
    private LocalLog mStatusMessage;
    private UiccPkcs15 mUiccPkcs15;
    private UiccProfile mUiccProfile;

    private String getStateString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "UNKNOWN" : "STATE_ERROR" : "STATE_LOADED" : "STATE_LOADING";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
    }

    /* loaded from: classes.dex */
    public static class TLV {
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private Integer length;
        private String lengthBytes;
        private String tag;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private String value;

        public TLV(String str) {
            this.tag = str;
        }

        public String getValue() {
            String str = this.value;
            return str == null ? PhoneConfigurationManager.SSSS : str;
        }

        public String parseLength(String str) {
            int length = this.tag.length();
            int i = length + 2;
            int parseInt = Integer.parseInt(str.substring(length, i), 16);
            if (parseInt < 128) {
                this.length = Integer.valueOf(parseInt * 2);
                this.lengthBytes = str.substring(length, i);
            } else {
                int i2 = ((parseInt - 128) * 2) + i;
                this.length = Integer.valueOf(Integer.parseInt(str.substring(i, i2), 16) * 2);
                this.lengthBytes = str.substring(length, i2);
            }
            UiccCarrierPrivilegeRules.log("TLV parseLength length=" + this.length + "lenghtBytes: " + this.lengthBytes);
            return this.lengthBytes;
        }

        public String parse(String str, boolean z) {
            UiccCarrierPrivilegeRules.log("Parse TLV: " + this.tag);
            if (!str.startsWith(this.tag)) {
                throw new IllegalArgumentException("Tags don't match.");
            }
            int length = this.tag.length();
            if (length + 2 > str.length()) {
                throw new IllegalArgumentException("No length.");
            }
            parseLength(str);
            int length2 = length + this.lengthBytes.length();
            UiccCarrierPrivilegeRules.log("index=" + length2 + " length=" + this.length + "data.length=" + str.length());
            int length3 = str.length() - (this.length.intValue() + length2);
            if (length3 >= 0) {
                if (z && length3 != 0) {
                    throw new IllegalArgumentException("Did not consume all.");
                }
                this.value = str.substring(length2, this.length.intValue() + length2);
                UiccCarrierPrivilegeRules.log("Got TLV: " + this.tag + "," + this.length + "," + this.value);
                return str.substring(length2 + this.length.intValue());
            }
            throw new IllegalArgumentException("Not enough data.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openChannel(int i) {
        this.mUiccProfile.iccOpenLogicalChannel(i == 0 ? "A00000015144414300" : "A00000015141434C00", 0, obtainMessage(1, 0, i, null));
    }

    public UiccCarrierPrivilegeRules(UiccProfile uiccProfile, Message message) {
        this.mStatusMessage = new LocalLog(64);
        this.mCheckedRules = false;
        this.mRetryRunnable = new Runnable() { // from class: com.android.internal.telephony.uicc.UiccCarrierPrivilegeRules.1
            @Override // java.lang.Runnable
            public void run() {
                UiccCarrierPrivilegeRules uiccCarrierPrivilegeRules = UiccCarrierPrivilegeRules.this;
                uiccCarrierPrivilegeRules.openChannel(uiccCarrierPrivilegeRules.mAIDInUse);
            }
        };
        log("Creating UiccCarrierPrivilegeRules");
        this.mUiccProfile = uiccProfile;
        this.mState = new AtomicInteger(0);
        this.mStatusMessage.log("Not loaded.");
        this.mLoadedCallback = message;
        this.mRules = PhoneConfigurationManager.SSSS;
        this.mAccessRules = new ArrayList();
        this.mAIDInUse = 0;
        openChannel(0);
    }

    @VisibleForTesting
    public UiccCarrierPrivilegeRules(List<UiccAccessRule> list) {
        this.mStatusMessage = new LocalLog(64);
        this.mCheckedRules = false;
        this.mRetryRunnable = new Runnable() { // from class: com.android.internal.telephony.uicc.UiccCarrierPrivilegeRules.1
            @Override // java.lang.Runnable
            public void run() {
                UiccCarrierPrivilegeRules uiccCarrierPrivilegeRules = UiccCarrierPrivilegeRules.this;
                uiccCarrierPrivilegeRules.openChannel(uiccCarrierPrivilegeRules.mAIDInUse);
            }
        };
        this.mAccessRules = list;
        this.mState = new AtomicInteger(1);
        this.mRules = PhoneConfigurationManager.SSSS;
        this.mStatusMessage.log("Loaded from test rules.");
    }

    public boolean areCarrierPriviligeRulesLoaded() {
        return this.mState.get() != 0;
    }

    public boolean hasCarrierPrivilegeRules() {
        List<UiccAccessRule> list;
        return (this.mState.get() == 0 || (list = this.mAccessRules) == null || list.size() <= 0) ? false : true;
    }

    public List<String> getPackageNames() {
        ArrayList arrayList = new ArrayList();
        List<UiccAccessRule> list = this.mAccessRules;
        if (list != null) {
            for (UiccAccessRule uiccAccessRule : list) {
                if (!TextUtils.isEmpty(uiccAccessRule.getPackageName())) {
                    arrayList.add(uiccAccessRule.getPackageName());
                }
            }
        }
        return arrayList;
    }

    public List<UiccAccessRule> getAccessRules() {
        List<UiccAccessRule> list = this.mAccessRules;
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    public int getCarrierPrivilegeStatus(Signature signature, String str) {
        int i = this.mState.get();
        if (i == 0) {
            return -1;
        }
        if (i == 2) {
            return -2;
        }
        for (UiccAccessRule uiccAccessRule : this.mAccessRules) {
            int carrierPrivilegeStatus = uiccAccessRule.getCarrierPrivilegeStatus(signature, str);
            if (carrierPrivilegeStatus != 0) {
                return carrierPrivilegeStatus;
            }
        }
        return 0;
    }

    public int getCarrierPrivilegeStatus(PackageManager packageManager, String str) {
        try {
            if (!hasCarrierPrivilegeRules()) {
                int i = this.mState.get();
                if (i == 0) {
                    return -1;
                }
                return i == 2 ? -2 : 0;
            }
            return getCarrierPrivilegeStatus(packageManager.getPackageInfo(str, 671121408));
        } catch (PackageManager.NameNotFoundException unused) {
            log("Package " + str + " not found for carrier privilege status check");
            return 0;
        }
    }

    public int getCarrierPrivilegeStatus(PackageInfo packageInfo) {
        int i = this.mState.get();
        if (i == 0) {
            return -1;
        }
        if (i == 2) {
            return -2;
        }
        for (UiccAccessRule uiccAccessRule : this.mAccessRules) {
            int carrierPrivilegeStatus = uiccAccessRule.getCarrierPrivilegeStatus(packageInfo);
            if (carrierPrivilegeStatus != 0) {
                return carrierPrivilegeStatus;
            }
        }
        return 0;
    }

    public int getCarrierPrivilegeStatusForCurrentTransaction(PackageManager packageManager) {
        return getCarrierPrivilegeStatusForUid(packageManager, Binder.getCallingUid());
    }

    public int getCarrierPrivilegeStatusForUid(PackageManager packageManager, int i) {
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                int carrierPrivilegeStatus = getCarrierPrivilegeStatus(packageManager, str);
                if (carrierPrivilegeStatus != 0) {
                    return carrierPrivilegeStatus;
                }
            }
        }
        return 0;
    }

    public static boolean shouldRetry(AsyncResult asyncResult, int i) {
        int i2;
        Throwable th = asyncResult.exception;
        if (!(th instanceof CommandException) || i >= 2) {
            return false;
        }
        CommandException.Error commandError = ((CommandException) th).getCommandError();
        Object obj = asyncResult.result;
        int[] iArr = (int[]) obj;
        if (obj == null || iArr.length != 3) {
            i2 = 0;
        } else {
            i2 = Integer.parseInt(IccUtils.bytesToHexString(new byte[]{(byte) iArr[1], (byte) iArr[2]}), 16);
            log("status code: " + String.valueOf(i2));
        }
        return commandError == CommandException.Error.MISSING_RESOURCE || (commandError == CommandException.Error.NO_SUCH_ELEMENT && i2 == 27013) || (commandError == CommandException.Error.INTERNAL_ERR && i2 == 27033);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        Object obj;
        Object obj2;
        byte[] bArr;
        this.mAIDInUse = message.arg2;
        int i = message.what;
        if (i == 1) {
            log("EVENT_OPEN_LOGICAL_CHANNEL_DONE");
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
                int i2 = ((int[]) obj)[0];
                this.mChannelId = i2;
                this.mUiccProfile.iccTransmitApduLogicalChannel(i2, 128, 202, 255, 64, 0, PhoneConfigurationManager.SSSS, false, obtainMessage(2, i2, this.mAIDInUse));
            } else if (shouldRetry(asyncResult, this.mRetryCount)) {
                log("should retry");
                this.mRetryCount++;
                removeCallbacks(this.mRetryRunnable);
                postDelayed(this.mRetryRunnable, 5000L);
            } else {
                if (this.mAIDInUse == 0) {
                    this.mRules = PhoneConfigurationManager.SSSS;
                    openChannel(1);
                }
                if (this.mAIDInUse == 1) {
                    if (this.mCheckedRules) {
                        updateState(1, "Success!");
                        return;
                    }
                    log("No ARA, try ARF next.");
                    Throwable th = asyncResult.exception;
                    if ((th instanceof CommandException) && ((CommandException) th).getCommandError() != CommandException.Error.NO_SUCH_ELEMENT) {
                        updateStatusMessage("No ARA due to " + ((CommandException) asyncResult.exception).getCommandError());
                    }
                    this.mUiccPkcs15 = new UiccPkcs15(this.mUiccProfile, obtainMessage(4));
                }
            }
        } else if (i == 2) {
            log("EVENT_TRANSMIT_LOGICAL_CHANNEL_DONE");
            AsyncResult asyncResult2 = (AsyncResult) message.obj;
            if (asyncResult2.exception == null && (obj2 = asyncResult2.result) != null) {
                IccIoResult iccIoResult = (IccIoResult) obj2;
                if (iccIoResult.sw1 == 144 && iccIoResult.sw2 == 0 && (bArr = iccIoResult.payload) != null && bArr.length > 0) {
                    try {
                        this.mRules += IccUtils.bytesToHexString(iccIoResult.payload).toUpperCase(Locale.US);
                        if (isDataComplete()) {
                            this.mAccessRules.addAll(parseRules(this.mRules));
                            if (this.mAIDInUse == 0) {
                                this.mCheckedRules = true;
                            } else {
                                updateState(1, "Success!");
                            }
                        } else {
                            UiccProfile uiccProfile = this.mUiccProfile;
                            int i3 = this.mChannelId;
                            uiccProfile.iccTransmitApduLogicalChannel(i3, 128, 202, 255, 96, 0, PhoneConfigurationManager.SSSS, false, obtainMessage(2, i3, this.mAIDInUse));
                            return;
                        }
                    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        if (this.mAIDInUse == 1) {
                            updateState(2, "Error parsing rules: " + e);
                        }
                    }
                } else if (this.mAIDInUse == 1) {
                    updateState(2, "Invalid response: payload=" + IccUtils.bytesToHexString(iccIoResult.payload) + " sw1=" + iccIoResult.sw1 + " sw2=" + iccIoResult.sw2);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Error reading value from SIM via ");
                sb.append(this.mAIDInUse == 0 ? "ARAD" : "ARAM");
                sb.append(" due to ");
                String sb2 = sb.toString();
                Throwable th2 = asyncResult2.exception;
                String str = th2 instanceof CommandException ? sb2 + "error code : " + ((CommandException) th2).getCommandError() : sb2 + "unknown exception : " + asyncResult2.exception.getMessage();
                if (this.mAIDInUse == 0) {
                    updateStatusMessage(str);
                } else {
                    updateState(2, str);
                }
            }
            this.mUiccProfile.iccCloseLogicalChannel(this.mChannelId, false, obtainMessage(3, 0, this.mAIDInUse));
            this.mChannelId = -1;
        } else if (i == 3) {
            log("EVENT_CLOSE_LOGICAL_CHANNEL_DONE");
            if (this.mAIDInUse == 0) {
                this.mRules = PhoneConfigurationManager.SSSS;
                openChannel(1);
            }
        } else if (i == 4) {
            log("EVENT_PKCS15_READ_DONE");
            UiccPkcs15 uiccPkcs15 = this.mUiccPkcs15;
            if (uiccPkcs15 == null || uiccPkcs15.getRules() == null) {
                updateState(2, "No ARA or ARF.");
                return;
            }
            for (String str2 : this.mUiccPkcs15.getRules()) {
                this.mAccessRules.add(new UiccAccessRule(IccUtils.hexStringToBytes(str2), PhoneConfigurationManager.SSSS, 0L));
            }
            updateState(1, "Success!");
        } else {
            Rlog.e("UiccCarrierPrivilegeRules", "Unknown event " + message.what);
        }
    }

    private boolean isDataComplete() {
        log("isDataComplete mRules:" + this.mRules);
        if (this.mRules.startsWith("FF40")) {
            TLV tlv = new TLV("FF40");
            String parseLength = tlv.parseLength(this.mRules);
            log("isDataComplete lengthBytes: " + parseLength);
            if (this.mRules.length() == 4 + parseLength.length() + tlv.length.intValue()) {
                log("isDataComplete yes");
                return true;
            }
            log("isDataComplete no");
            return false;
        }
        throw new IllegalArgumentException("Tags don't match.");
    }

    private static List<UiccAccessRule> parseRules(String str) {
        log("Got rules: " + str);
        TLV tlv = new TLV("FF40");
        tlv.parse(str, true);
        String str2 = tlv.value;
        ArrayList arrayList = new ArrayList();
        while (!str2.isEmpty()) {
            TLV tlv2 = new TLV("E2");
            str2 = tlv2.parse(str2, false);
            UiccAccessRule parseRefArdo = parseRefArdo(tlv2.value);
            if (parseRefArdo != null) {
                arrayList.add(parseRefArdo);
            } else {
                Rlog.e("UiccCarrierPrivilegeRules", "Skip unrecognized rule." + tlv2.value);
            }
        }
        return arrayList;
    }

    private static UiccAccessRule parseRefArdo(String str) {
        String parse;
        String str2;
        String str3;
        log("Got rule: " + str);
        String str4 = null;
        String str5 = null;
        while (!str.isEmpty()) {
            if (str.startsWith("E1")) {
                TLV tlv = new TLV("E1");
                str = tlv.parse(str, false);
                TLV tlv2 = new TLV("C1");
                if (tlv.value.startsWith("4F")) {
                    TLV tlv3 = new TLV("4F");
                    String parse2 = tlv3.parse(tlv.value, false);
                    if (!tlv3.lengthBytes.equals("06") || !tlv3.value.equals("FFFFFFFFFFFF") || parse2.isEmpty() || !parse2.startsWith("C1")) {
                        return null;
                    }
                    parse = tlv2.parse(parse2, false);
                    str2 = tlv2.value;
                } else if (!tlv.value.startsWith("C1")) {
                    return null;
                } else {
                    parse = tlv2.parse(tlv.value, false);
                    str2 = tlv2.value;
                }
                if (parse.isEmpty()) {
                    str3 = null;
                } else if (!parse.startsWith("CA")) {
                    return null;
                } else {
                    TLV tlv4 = new TLV("CA");
                    tlv4.parse(parse, true);
                    str3 = new String(IccUtils.hexStringToBytes(tlv4.value));
                }
                String str6 = str2;
                str5 = str3;
                str4 = str6;
            } else if (str.startsWith("E3")) {
                TLV tlv5 = new TLV("E3");
                str = tlv5.parse(str, false);
                String str7 = tlv5.value;
                while (!str7.isEmpty() && !str7.startsWith("DB")) {
                    str7 = new TLV(str7.substring(0, 2)).parse(str7, false);
                }
                if (str7.isEmpty()) {
                    return null;
                }
                new TLV("DB").parse(str7, true);
            } else {
                throw new RuntimeException("Invalid Rule type");
            }
        }
        return new UiccAccessRule(IccUtils.hexStringToBytes(str4), str5, 0L);
    }

    private void updateState(int i, String str) {
        this.mState.set(i);
        Message message = this.mLoadedCallback;
        if (message != null) {
            message.sendToTarget();
        }
        updateStatusMessage(str);
    }

    private void updateStatusMessage(String str) {
        this.mStatusMessage.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("UiccCarrierPrivilegeRules:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mState=" + getStateString(this.mState.get()));
        androidUtilIndentingPrintWriter.println("mStatusMessage=");
        this.mStatusMessage.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        if (this.mAccessRules != null) {
            androidUtilIndentingPrintWriter.println("mAccessRules: ");
            androidUtilIndentingPrintWriter.increaseIndent();
            Iterator<UiccAccessRule> it = this.mAccessRules.iterator();
            while (it.hasNext()) {
                androidUtilIndentingPrintWriter.println("  rule='" + it.next() + "'");
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
        } else {
            androidUtilIndentingPrintWriter.println(" mAccessRules: null");
        }
        if (this.mUiccPkcs15 != null) {
            androidUtilIndentingPrintWriter.println(" mUiccPkcs15: " + this.mUiccPkcs15);
            this.mUiccPkcs15.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        } else {
            androidUtilIndentingPrintWriter.println(" mUiccPkcs15: null");
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.flush();
    }
}
