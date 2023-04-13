package com.google.android.mms.pdu;

import com.google.android.mms.InvalidHeaderValueException;
/* loaded from: classes5.dex */
public class AcknowledgeInd extends GenericPdu {
    public AcknowledgeInd(int mmsVersion, byte[] transactionId) throws InvalidHeaderValueException {
        setMessageType(133);
        setMmsVersion(mmsVersion);
        setTransactionId(transactionId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AcknowledgeInd(PduHeaders headers) {
        super(headers);
    }

    public int getReportAllowed() {
        return this.mPduHeaders.getOctet(145);
    }

    public void setReportAllowed(int value) throws InvalidHeaderValueException {
        this.mPduHeaders.setOctet(value, 145);
    }

    public byte[] getTransactionId() {
        return this.mPduHeaders.getTextString(152);
    }

    public void setTransactionId(byte[] value) {
        this.mPduHeaders.setTextString(value, 152);
    }
}
