package android.app;

import android.content.ComponentName;
import android.p008os.Bundle;
import android.util.AndroidRuntimeException;
/* loaded from: classes.dex */
public class RemoteServiceException extends AndroidRuntimeException {
    public RemoteServiceException(String msg) {
        super(msg);
    }

    public RemoteServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /* loaded from: classes.dex */
    public static class ForegroundServiceDidNotStartInTimeException extends RemoteServiceException {
        private static final String KEY_SERVICE_CLASS_NAME = "serviceclassname";
        public static final int TYPE_ID = 1;

        public ForegroundServiceDidNotStartInTimeException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public static Bundle createExtrasForService(ComponentName service) {
            Bundle b = new Bundle();
            b.putString(KEY_SERVICE_CLASS_NAME, service.getClassName());
            return b;
        }

        public static String getServiceClassNameFromExtras(Bundle extras) {
            if (extras == null) {
                return null;
            }
            return extras.getString(KEY_SERVICE_CLASS_NAME);
        }
    }

    /* loaded from: classes.dex */
    public static class CannotPostForegroundServiceNotificationException extends RemoteServiceException {
        public static final int TYPE_ID = 2;

        public CannotPostForegroundServiceNotificationException(String msg) {
            super(msg);
        }
    }

    /* loaded from: classes.dex */
    public static class BadForegroundServiceNotificationException extends RemoteServiceException {
        public static final int TYPE_ID = 3;

        public BadForegroundServiceNotificationException(String msg) {
            super(msg);
        }
    }

    /* loaded from: classes.dex */
    public static class MissingRequestPasswordComplexityPermissionException extends RemoteServiceException {
        public static final int TYPE_ID = 4;

        public MissingRequestPasswordComplexityPermissionException(String msg) {
            super(msg);
        }
    }

    /* loaded from: classes.dex */
    public static class CrashedByAdbException extends RemoteServiceException {
        public static final int TYPE_ID = 5;

        public CrashedByAdbException(String msg) {
            super(msg);
        }
    }
}
