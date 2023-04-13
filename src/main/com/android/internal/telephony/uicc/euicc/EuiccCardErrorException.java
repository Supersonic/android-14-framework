package com.android.internal.telephony.uicc.euicc;

import com.android.internal.telephony.uicc.asn1.Asn1Node;
/* loaded from: classes.dex */
public class EuiccCardErrorException extends EuiccCardException {
    public static final int OPERATION_AUTHENTICATE_SERVER = 3;
    public static final int OPERATION_CANCEL_SESSION = 4;
    public static final int OPERATION_DELETE_PROFILE = 12;
    public static final int OPERATION_DISABLE_PROFILE = 11;
    public static final int OPERATION_GET_PROFILE = 1;
    public static final int OPERATION_LIST_NOTIFICATIONS = 6;
    public static final int OPERATION_LOAD_BOUND_PROFILE_PACKAGE = 5;
    public static final int OPERATION_PREPARE_DOWNLOAD = 2;
    public static final int OPERATION_REMOVE_NOTIFICATION_FROM_LIST = 9;
    public static final int OPERATION_RESET_MEMORY = 13;
    public static final int OPERATION_RETRIEVE_NOTIFICATION = 8;
    public static final int OPERATION_SET_DEFAULT_SMDP_ADDRESS = 14;
    public static final int OPERATION_SET_NICKNAME = 7;
    public static final int OPERATION_SWITCH_TO_PROFILE = 10;
    public static final int OPERATION_UNKNOWN = 0;
    private final int mErrorCode;
    private final Asn1Node mErrorDetails;
    private final int mOperationCode;

    public EuiccCardErrorException(int i, int i2) {
        this.mOperationCode = i;
        this.mErrorCode = i2;
        this.mErrorDetails = null;
    }

    public EuiccCardErrorException(int i, int i2, Asn1Node asn1Node) {
        this.mOperationCode = i;
        this.mErrorCode = i2;
        this.mErrorDetails = asn1Node;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public int getOperationCode() {
        return this.mOperationCode;
    }

    public Asn1Node getErrorDetails() {
        return this.mErrorDetails;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("EuiccCardError: mOperatorCode=");
        sb.append(this.mOperationCode);
        sb.append(", mErrorCode=");
        sb.append(this.mErrorCode);
        sb.append(", errorDetails=");
        Asn1Node asn1Node = this.mErrorDetails;
        sb.append(asn1Node == null ? "null" : asn1Node.toHex());
        return sb.toString();
    }
}
