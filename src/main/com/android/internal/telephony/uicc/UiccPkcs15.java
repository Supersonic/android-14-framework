package com.android.internal.telephony.uicc;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.UiccCarrierPrivilegeRules;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class UiccPkcs15 extends Handler {
    public static final String AC_OID = "060A2A864886FC6B81480101";
    private FileHandler mFh;
    private Message mLoadedCallback;
    private Pkcs15Selector mPkcs15Selector;
    private UiccProfile mUiccProfile;
    private int mChannelId = -1;
    private List<String> mRules = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FileHandler extends Handler {
        protected static final int EVENT_READ_BINARY_DONE = 102;
        protected static final int EVENT_SELECT_FILE_DONE = 101;
        private Message mCallback;
        private String mFileId;
        final String mPkcs15Path;

        public FileHandler(String str) {
            UiccPkcs15.log("Creating FileHandler, pkcs15Path: " + str);
            this.mPkcs15Path = str;
        }

        public boolean loadFile(String str, Message message) {
            UiccPkcs15.log("loadFile: " + str);
            if (str == null || message == null) {
                return false;
            }
            this.mFileId = str;
            this.mCallback = message;
            selectFile();
            return true;
        }

        private void selectFile() {
            if (UiccPkcs15.this.mChannelId >= 0) {
                UiccPkcs15.this.mUiccProfile.iccTransmitApduLogicalChannel(UiccPkcs15.this.mChannelId, 0, 164, 0, 4, 2, this.mFileId, false, obtainMessage(101));
            } else {
                UiccPkcs15.log("EF based");
            }
        }

        private void readBinary() {
            if (UiccPkcs15.this.mChannelId >= 0) {
                UiccPkcs15.this.mUiccProfile.iccTransmitApduLogicalChannel(UiccPkcs15.this.mChannelId, 0, 176, 0, 0, 0, PhoneConfigurationManager.SSSS, false, obtainMessage(102));
            } else {
                UiccPkcs15.log("EF based");
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object obj;
            UiccPkcs15.log("handleMessage: " + message.what);
            AsyncResult asyncResult = (AsyncResult) message.obj;
            IccException iccException = null;
            if (asyncResult.exception != null || (obj = asyncResult.result) == null) {
                UiccPkcs15.log("Error: " + asyncResult.exception);
                AsyncResult.forMessage(this.mCallback, (Object) null, asyncResult.exception);
                this.mCallback.sendToTarget();
                return;
            }
            int i = message.what;
            if (i == 101) {
                readBinary();
            } else if (i == 102) {
                IccIoResult iccIoResult = (IccIoResult) obj;
                String upperCase = IccUtils.bytesToHexString(iccIoResult.payload).toUpperCase(Locale.US);
                UiccPkcs15.log("IccIoResult: " + iccIoResult + " payload: " + upperCase);
                Message message2 = this.mCallback;
                if (upperCase == null) {
                    iccException = new IccException("Error: null response for " + this.mFileId);
                }
                AsyncResult.forMessage(message2, upperCase, iccException);
                this.mCallback.sendToTarget();
            } else {
                UiccPkcs15.log("Unknown event" + message.what);
            }
        }
    }

    /* loaded from: classes.dex */
    private class Pkcs15Selector extends Handler {
        private Message mCallback;

        public Pkcs15Selector(Message message) {
            this.mCallback = message;
            UiccPkcs15.this.mUiccProfile.iccOpenLogicalChannel("A000000063504B43532D3135", 4, obtainMessage(IccRecords.EVENT_SET_SMSS_RECORD_DONE));
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object obj;
            UiccPkcs15.log("handleMessage: " + message.what);
            if (message.what == 201) {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
                    UiccPkcs15.this.mChannelId = ((int[]) obj)[0];
                    UiccPkcs15.log("mChannelId: " + UiccPkcs15.this.mChannelId);
                    AsyncResult.forMessage(this.mCallback, (Object) null, (Throwable) null);
                } else {
                    UiccPkcs15.log("error: " + asyncResult.exception);
                    AsyncResult.forMessage(this.mCallback, (Object) null, asyncResult.exception);
                }
                this.mCallback.sendToTarget();
                return;
            }
            UiccPkcs15.log("Unknown event" + message.what);
        }
    }

    public UiccPkcs15(UiccProfile uiccProfile, Message message) {
        log("Creating UiccPkcs15");
        this.mUiccProfile = uiccProfile;
        this.mLoadedCallback = message;
        this.mPkcs15Selector = new Pkcs15Selector(obtainMessage(1));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4;
        log("handleMessage: " + message.what);
        AsyncResult asyncResult = (AsyncResult) message.obj;
        switch (message.what) {
            case 1:
                if (asyncResult.exception == null) {
                    FileHandler fileHandler = new FileHandler((String) asyncResult.result);
                    this.mFh = fileHandler;
                    if (fileHandler.loadFile("5031", obtainMessage(2))) {
                        return;
                    }
                    startFromAcrf();
                    return;
                }
                log("select pkcs15 failed: " + asyncResult.exception);
                this.mLoadedCallback.sendToTarget();
                return;
            case 2:
                if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
                    if (this.mFh.loadFile(parseOdf((String) obj), obtainMessage(3))) {
                        return;
                    }
                    startFromAcrf();
                    return;
                }
                startFromAcrf();
                return;
            case 3:
                if (asyncResult.exception == null && (obj2 = asyncResult.result) != null) {
                    if (this.mFh.loadFile(parseDodf((String) obj2), obtainMessage(4))) {
                        return;
                    }
                    startFromAcrf();
                    return;
                }
                startFromAcrf();
                return;
            case 4:
                if (asyncResult.exception == null && (obj3 = asyncResult.result) != null) {
                    if (this.mFh.loadFile(parseAcmf((String) obj3), obtainMessage(5))) {
                        return;
                    }
                    startFromAcrf();
                    return;
                }
                startFromAcrf();
                return;
            case 5:
                if (asyncResult.exception == null && asyncResult.result != null) {
                    this.mRules = new ArrayList();
                    if (this.mFh.loadFile(parseAcrf((String) asyncResult.result), obtainMessage(6))) {
                        return;
                    }
                    cleanUp();
                    return;
                }
                cleanUp();
                return;
            case 6:
                if (asyncResult.exception == null && (obj4 = asyncResult.result) != null) {
                    parseAccf((String) obj4);
                }
                cleanUp();
                return;
            case 7:
                return;
            default:
                Rlog.e("UiccPkcs15", "Unknown event " + message.what);
                return;
        }
    }

    private void startFromAcrf() {
        log("Fallback to use ACRF_PATH");
        if (this.mFh.loadFile("4300", obtainMessage(5))) {
            return;
        }
        cleanUp();
    }

    private void cleanUp() {
        log("cleanUp");
        int i = this.mChannelId;
        if (i >= 0) {
            this.mUiccProfile.iccCloseLogicalChannel(i, false, obtainMessage(7));
            this.mChannelId = -1;
        }
        this.mLoadedCallback.sendToTarget();
    }

    private String parseOdf(String str) {
        try {
            UiccCarrierPrivilegeRules.TLV tlv = new UiccCarrierPrivilegeRules.TLV("A7");
            tlv.parse(str, false);
            String value = tlv.getValue();
            UiccCarrierPrivilegeRules.TLV tlv2 = new UiccCarrierPrivilegeRules.TLV("30");
            UiccCarrierPrivilegeRules.TLV tlv3 = new UiccCarrierPrivilegeRules.TLV("04");
            tlv2.parse(value, true);
            tlv3.parse(tlv2.getValue(), true);
            return tlv3.getValue();
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            log("Error: " + e);
            return null;
        }
    }

    private String parseDodf(String str) {
        while (!str.isEmpty()) {
            UiccCarrierPrivilegeRules.TLV tlv = new UiccCarrierPrivilegeRules.TLV("A1");
            try {
                str = tlv.parse(str, false);
                String parse = new UiccCarrierPrivilegeRules.TLV("30").parse(new UiccCarrierPrivilegeRules.TLV("30").parse(tlv.getValue(), false), false);
                if (parse.startsWith("A0")) {
                    parse = new UiccCarrierPrivilegeRules.TLV("A0").parse(parse, false);
                }
                if (parse.startsWith("A1")) {
                    UiccCarrierPrivilegeRules.TLV tlv2 = new UiccCarrierPrivilegeRules.TLV("A1");
                    tlv2.parse(parse, true);
                    String value = tlv2.getValue();
                    UiccCarrierPrivilegeRules.TLV tlv3 = new UiccCarrierPrivilegeRules.TLV("30");
                    tlv3.parse(value, true);
                    String value2 = tlv3.getValue();
                    UiccCarrierPrivilegeRules.TLV tlv4 = new UiccCarrierPrivilegeRules.TLV("06");
                    tlv4.parse(value2, false);
                    if (tlv4.getValue().equals(AC_OID)) {
                        String parse2 = tlv4.parse(value2, false);
                        UiccCarrierPrivilegeRules.TLV tlv5 = new UiccCarrierPrivilegeRules.TLV("30");
                        UiccCarrierPrivilegeRules.TLV tlv6 = new UiccCarrierPrivilegeRules.TLV("04");
                        tlv5.parse(parse2, true);
                        tlv6.parse(tlv5.getValue(), true);
                        return tlv6.getValue();
                    }
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                log("Error: " + e);
                return null;
            }
        }
        return null;
    }

    private String parseAcmf(String str) {
        try {
            UiccCarrierPrivilegeRules.TLV tlv = new UiccCarrierPrivilegeRules.TLV("30");
            tlv.parse(str, false);
            String value = tlv.getValue();
            UiccCarrierPrivilegeRules.TLV tlv2 = new UiccCarrierPrivilegeRules.TLV("04");
            if (!tlv2.parseLength(value).equals("08")) {
                log("Error: refresh tag in ACMF must be 8.");
                return null;
            }
            String parse = tlv2.parse(value, false);
            UiccCarrierPrivilegeRules.TLV tlv3 = new UiccCarrierPrivilegeRules.TLV("30");
            UiccCarrierPrivilegeRules.TLV tlv4 = new UiccCarrierPrivilegeRules.TLV("04");
            tlv3.parse(parse, true);
            tlv4.parse(tlv3.getValue(), true);
            return tlv4.getValue();
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            log("Error: " + e);
            return null;
        }
    }

    private String parseAcrf(String str) {
        String str2 = null;
        while (!str.isEmpty()) {
            UiccCarrierPrivilegeRules.TLV tlv = new UiccCarrierPrivilegeRules.TLV("30");
            try {
                str = tlv.parse(str, false);
                String value = tlv.getValue();
                if (value.startsWith("A0")) {
                    UiccCarrierPrivilegeRules.TLV tlv2 = new UiccCarrierPrivilegeRules.TLV("A0");
                    UiccCarrierPrivilegeRules.TLV tlv3 = new UiccCarrierPrivilegeRules.TLV("04");
                    UiccCarrierPrivilegeRules.TLV tlv4 = new UiccCarrierPrivilegeRules.TLV("30");
                    UiccCarrierPrivilegeRules.TLV tlv5 = new UiccCarrierPrivilegeRules.TLV("04");
                    String parse = tlv2.parse(value, false);
                    tlv3.parse(tlv2.getValue(), true);
                    if ("FFFFFFFFFFFF".equals(tlv3.getValue())) {
                        tlv4.parse(parse, true);
                        tlv5.parse(tlv4.getValue(), true);
                        str2 = tlv5.getValue();
                    }
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                log("Error: " + e);
            }
        }
        return str2;
    }

    private void parseAccf(String str) {
        while (!str.isEmpty()) {
            UiccCarrierPrivilegeRules.TLV tlv = new UiccCarrierPrivilegeRules.TLV("30");
            UiccCarrierPrivilegeRules.TLV tlv2 = new UiccCarrierPrivilegeRules.TLV("04");
            try {
                str = tlv.parse(str, false);
                tlv2.parse(tlv.getValue(), true);
                if (!tlv2.getValue().isEmpty()) {
                    this.mRules.add(tlv2.getValue());
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                log("Error: " + e);
                return;
            }
        }
    }

    public List<String> getRules() {
        return this.mRules;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
        Rlog.d("UiccPkcs15", str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mRules != null) {
            printWriter.println(" mRules:");
            Iterator<String> it = this.mRules.iterator();
            while (it.hasNext()) {
                printWriter.println("  " + it.next());
            }
        }
    }
}
