package com.android.internal.telephony;

import android.hardware.radio.messaging.CdmaSmsMessage;
import android.hardware.radio.messaging.IRadioMessagingIndication;
import android.os.AsyncResult;
import android.telephony.SmsMessage;
import com.android.internal.telephony.uicc.IccUtils;
/* loaded from: classes.dex */
public class MessagingIndication extends IRadioMessagingIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public MessagingIndication(RIL ril) {
        this.mRil = ril;
    }

    public void cdmaNewSms(int i, CdmaSmsMessage cdmaSmsMessage) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1020);
        }
        SmsMessage smsMessage = new SmsMessage(RILUtils.convertHalCdmaSmsMessage(cdmaSmsMessage));
        Registrant registrant = this.mRil.mCdmaSmsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, smsMessage, (Throwable) null));
        }
    }

    public void cdmaRuimSmsStorageFull(int i) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1022);
        }
        Registrant registrant = this.mRil.mIccSmsFullRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant();
        }
    }

    public void newBroadcastSms(int i, byte[] bArr) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogvRet(1021, IccUtils.bytesToHexString(bArr));
        }
        Registrant registrant = this.mRil.mGsmBroadcastSmsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, bArr, (Throwable) null));
        }
    }

    public void newSms(int i, byte[] bArr) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1003);
        }
        com.android.internal.telephony.gsm.SmsMessage createFromPdu = com.android.internal.telephony.gsm.SmsMessage.createFromPdu(bArr);
        Registrant registrant = this.mRil.mGsmSmsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, createFromPdu == null ? null : new SmsMessage(createFromPdu), (Throwable) null));
        }
    }

    public void newSmsOnSim(int i, int i2) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1005);
        }
        Registrant registrant = this.mRil.mSmsOnSimRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void newSmsStatusReport(int i, byte[] bArr) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1004);
        }
        Registrant registrant = this.mRil.mSmsStatusRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, bArr, (Throwable) null));
        }
    }

    public void simSmsStorageFull(int i) {
        this.mRil.processIndication(2, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1016);
        }
        Registrant registrant = this.mRil.mIccSmsFullRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant();
        }
    }
}
