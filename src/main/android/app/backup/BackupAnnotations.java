package android.app.backup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class BackupAnnotations {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BackupDestination {
        public static final int ADB_BACKUP = 2;
        public static final int CLOUD = 0;
        public static final int DEVICE_TRANSFER = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface OperationType {
        public static final int BACKUP = 0;
        public static final int RESTORE = 1;
        public static final int UNKNOWN = -1;
    }
}
