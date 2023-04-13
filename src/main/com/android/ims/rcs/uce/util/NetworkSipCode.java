package com.android.ims.rcs.uce.util;
/* loaded from: classes.dex */
public class NetworkSipCode {
    public static final String SIP_ACCEPTED = "Accepted";
    public static final String SIP_BAD_REQUEST = "Bad Request";
    public static final int SIP_CODE_ACCEPTED = 202;
    public static final int SIP_CODE_BAD_EVENT = 489;
    public static final int SIP_CODE_BAD_REQUEST = 400;
    public static final int SIP_CODE_BUSY = 486;
    public static final int SIP_CODE_BUSY_EVERYWHERE = 600;
    public static final int SIP_CODE_DECLINE = 603;
    public static final int SIP_CODE_DOES_NOT_EXIST_ANYWHERE = 604;
    public static final int SIP_CODE_FORBIDDEN = 403;
    public static final int SIP_CODE_INTERVAL_TOO_BRIEF = 423;
    public static final int SIP_CODE_METHOD_NOT_ALLOWED = 405;
    public static final int SIP_CODE_NOT_FOUND = 404;
    public static final int SIP_CODE_OK = 200;
    public static final int SIP_CODE_REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int SIP_CODE_REQUEST_TIMEOUT = 408;
    public static final int SIP_CODE_SERVER_INTERNAL_ERROR = 500;
    public static final int SIP_CODE_SERVER_TIMEOUT = 504;
    public static final int SIP_CODE_SERVICE_UNAVAILABLE = 503;
    public static final int SIP_CODE_TEMPORARILY_UNAVAILABLE = 480;
    public static final String SIP_INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String SIP_NOT_AUTHORIZED_FOR_PRESENCE = "not authorized for presence";
    public static final String SIP_NOT_REGISTERED = "User not registered";
    public static final String SIP_OK = "OK";
    public static final String SIP_SERVICE_UNAVAILABLE = "Service Unavailable";

    public static int getCapabilityErrorFromSipCode(int sipCode, String reason, int requestType) {
        switch (sipCode) {
            case SIP_CODE_FORBIDDEN /* 403 */:
            case SIP_CODE_SERVER_TIMEOUT /* 504 */:
                if (requestType == 1) {
                    return 5;
                }
                if (SIP_NOT_REGISTERED.equalsIgnoreCase(reason)) {
                    return 4;
                }
                if (SIP_NOT_AUTHORIZED_FOR_PRESENCE.equalsIgnoreCase(reason)) {
                    return 5;
                }
                return 6;
            case SIP_CODE_NOT_FOUND /* 404 */:
                if (requestType == 1) {
                    return 5;
                }
                return 7;
            case SIP_CODE_REQUEST_TIMEOUT /* 408 */:
                return 9;
            case SIP_CODE_INTERVAL_TOO_BRIEF /* 423 */:
                return 1;
            case SIP_CODE_BAD_EVENT /* 489 */:
                return 6;
            case SIP_CODE_SERVER_INTERNAL_ERROR /* 500 */:
            case SIP_CODE_SERVICE_UNAVAILABLE /* 503 */:
                return 12;
            default:
                return 1;
        }
    }
}
