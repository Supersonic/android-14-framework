package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.telephony.Rlog;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class AdnRecordLoader extends Handler {
    ArrayList<AdnRecord> mAdns;
    int mEf;
    int mExtensionEF;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccFileHandler mFh;
    int mPendingExtLoads;
    String mPin2;
    int mRecordNumber;
    Object mResult;
    Message mUserResponse;

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String getEFPath(int i) {
        if (i == 28474) {
            return "3F007F10";
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecordLoader(IccFileHandler iccFileHandler) {
        super(Looper.getMainLooper());
        this.mFh = iccFileHandler;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loadFromEF(int i, int i2, int i3, Message message) {
        this.mEf = i;
        this.mExtensionEF = i2;
        this.mRecordNumber = i3;
        this.mUserResponse = message;
        this.mFh.loadEFLinearFixed(i, getEFPath(i), i3, obtainMessage(1));
    }

    public void loadAllFromEF(int i, int i2, Message message) {
        this.mEf = i;
        this.mExtensionEF = i2;
        this.mUserResponse = message;
        this.mFh.loadEFLinearFixedAll(i, getEFPath(i), obtainMessage(3));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updateEF(AdnRecord adnRecord, int i, int i2, int i3, String str, Message message) {
        this.mEf = i;
        this.mExtensionEF = i2;
        this.mRecordNumber = i3;
        this.mUserResponse = message;
        this.mPin2 = str;
        this.mFh.getEFLinearRecordSize(i, getEFPath(i), obtainMessage(4, adnRecord));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        try {
            int i = message.what;
            if (i == 1) {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                byte[] bArr = (byte[]) asyncResult.result;
                if (asyncResult.exception != null) {
                    throw new RuntimeException("load failed", asyncResult.exception);
                }
                AdnRecord adnRecord = new AdnRecord(this.mEf, this.mRecordNumber, bArr);
                this.mResult = adnRecord;
                if (adnRecord.hasExtendedRecord()) {
                    this.mPendingExtLoads = 1;
                    this.mFh.loadEFLinearFixed(this.mExtensionEF, adnRecord.mExtRecord, obtainMessage(2, adnRecord));
                }
            } else if (i != 2) {
                int i2 = 0;
                if (i == 3) {
                    AsyncResult asyncResult2 = (AsyncResult) message.obj;
                    ArrayList arrayList = (ArrayList) asyncResult2.result;
                    if (asyncResult2.exception != null) {
                        throw new RuntimeException("load failed", asyncResult2.exception);
                    }
                    ArrayList<AdnRecord> arrayList2 = new ArrayList<>(arrayList.size());
                    this.mAdns = arrayList2;
                    this.mResult = arrayList2;
                    this.mPendingExtLoads = 0;
                    int size = arrayList.size();
                    while (i2 < size) {
                        int i3 = i2 + 1;
                        AdnRecord adnRecord2 = new AdnRecord(this.mEf, i3, (byte[]) arrayList.get(i2));
                        this.mAdns.add(adnRecord2);
                        if (adnRecord2.hasExtendedRecord()) {
                            this.mPendingExtLoads++;
                            this.mFh.loadEFLinearFixed(this.mExtensionEF, adnRecord2.mExtRecord, obtainMessage(2, adnRecord2));
                        }
                        i2 = i3;
                    }
                } else if (i == 4) {
                    AsyncResult asyncResult3 = (AsyncResult) message.obj;
                    AdnRecord adnRecord3 = (AdnRecord) asyncResult3.userObj;
                    if (asyncResult3.exception != null) {
                        throw new RuntimeException("get EF record size failed", asyncResult3.exception);
                    }
                    int[] iArr = (int[]) asyncResult3.result;
                    if (iArr.length != 3 || this.mRecordNumber > iArr[2]) {
                        throw new RuntimeException("get wrong EF record size format", asyncResult3.exception);
                    }
                    byte[] buildAdnString = adnRecord3.buildAdnString(iArr[0]);
                    if (buildAdnString == null) {
                        if (this.mUserResponse.arg1 == 1) {
                            adnRecord3.mAlphaTag = null;
                            buildAdnString = adnRecord3.buildAdnString(iArr[0]);
                        }
                        if (buildAdnString == null) {
                            throw new RuntimeException("wrong ADN format", asyncResult3.exception);
                        }
                    }
                    byte[] bArr2 = buildAdnString;
                    if (this.mUserResponse.arg1 == 1) {
                        IccFileHandler iccFileHandler = this.mFh;
                        int i4 = this.mEf;
                        iccFileHandler.updateEFLinearFixed(i4, getEFPath(i4), this.mRecordNumber, bArr2, this.mPin2, obtainMessage(5, adnRecord3));
                    } else {
                        IccFileHandler iccFileHandler2 = this.mFh;
                        int i5 = this.mEf;
                        iccFileHandler2.updateEFLinearFixed(i5, getEFPath(i5), this.mRecordNumber, bArr2, this.mPin2, obtainMessage(5));
                    }
                    this.mPendingExtLoads = 1;
                } else if (i == 5) {
                    AsyncResult asyncResult4 = (AsyncResult) message.obj;
                    if (asyncResult4.exception != null) {
                        throw new RuntimeException("update EF adn record failed", asyncResult4.exception);
                    }
                    this.mPendingExtLoads = 0;
                    if (this.mUserResponse.arg1 == 1) {
                        this.mResult = asyncResult4.userObj;
                    } else {
                        this.mResult = null;
                    }
                }
            } else {
                AsyncResult asyncResult5 = (AsyncResult) message.obj;
                byte[] bArr3 = (byte[]) asyncResult5.result;
                AdnRecord adnRecord4 = (AdnRecord) asyncResult5.userObj;
                if (asyncResult5.exception == null) {
                    Rlog.d("AdnRecordLoader", "ADN extension EF: 0x" + Integer.toHexString(this.mExtensionEF) + ":" + adnRecord4.mExtRecord + "\n" + IccUtils.bytesToHexString(bArr3));
                    adnRecord4.appendExtRecord(bArr3);
                } else {
                    Rlog.e("AdnRecordLoader", "Failed to read ext record. Clear the number now.");
                    adnRecord4.setNumber(PhoneConfigurationManager.SSSS);
                }
                this.mPendingExtLoads--;
            }
            Message message2 = this.mUserResponse;
            if (message2 == null || this.mPendingExtLoads != 0) {
                return;
            }
            AsyncResult.forMessage(message2).result = this.mResult;
            this.mUserResponse.sendToTarget();
            this.mUserResponse = null;
        } catch (RuntimeException e) {
            Message message3 = this.mUserResponse;
            if (message3 != null) {
                AsyncResult.forMessage(message3).exception = e;
                this.mUserResponse.sendToTarget();
                this.mUserResponse = null;
            }
        }
    }
}
