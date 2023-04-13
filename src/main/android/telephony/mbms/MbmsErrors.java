package android.telephony.mbms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class MbmsErrors {
    public static final int ERROR_MIDDLEWARE_LOST = 3;
    public static final int ERROR_MIDDLEWARE_NOT_BOUND = 2;
    public static final int ERROR_NO_UNIQUE_MIDDLEWARE = 1;
    public static final int SUCCESS = 0;
    public static final int UNKNOWN = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MbmsError {
    }

    /* loaded from: classes3.dex */
    public static class InitializationErrors {
        public static final int ERROR_APP_PERMISSIONS_NOT_GRANTED = 102;
        public static final int ERROR_DUPLICATE_INITIALIZE = 101;
        public static final int ERROR_UNABLE_TO_INITIALIZE = 103;

        private InitializationErrors() {
        }
    }

    /* loaded from: classes3.dex */
    public static class GeneralErrors {
        public static final int ERROR_CARRIER_CHANGE_NOT_ALLOWED = 207;
        public static final int ERROR_IN_E911 = 204;
        public static final int ERROR_MIDDLEWARE_NOT_YET_READY = 201;
        public static final int ERROR_MIDDLEWARE_TEMPORARILY_UNAVAILABLE = 203;
        public static final int ERROR_NOT_CONNECTED_TO_HOME_CARRIER_LTE = 205;
        public static final int ERROR_OUT_OF_MEMORY = 202;
        public static final int ERROR_UNABLE_TO_READ_SIM = 206;

        private GeneralErrors() {
        }
    }

    /* loaded from: classes3.dex */
    public static class StreamingErrors {
        public static final int ERROR_CONCURRENT_SERVICE_LIMIT_REACHED = 301;
        public static final int ERROR_DUPLICATE_START_STREAM = 303;
        public static final int ERROR_UNABLE_TO_START_SERVICE = 302;

        private StreamingErrors() {
        }
    }

    /* loaded from: classes3.dex */
    public static class DownloadErrors {
        public static final int ERROR_CANNOT_CHANGE_TEMP_FILE_ROOT = 401;
        public static final int ERROR_MALFORMED_SERVICE_ANNOUNCEMENT = 404;
        public static final int ERROR_UNKNOWN_DOWNLOAD_REQUEST = 402;
        public static final int ERROR_UNKNOWN_FILE_INFO = 403;

        private DownloadErrors() {
        }
    }

    /* loaded from: classes3.dex */
    public static class GroupCallErrors {
        public static final int ERROR_DUPLICATE_START_GROUP_CALL = 502;
        public static final int ERROR_UNABLE_TO_START_SERVICE = 501;

        private GroupCallErrors() {
        }
    }

    private MbmsErrors() {
    }
}
