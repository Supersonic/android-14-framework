package android.timezone;

import com.android.i18n.timezone.TimeZoneDataFiles;
import com.android.i18n.timezone.TzDataSetVersion;
import java.io.IOException;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class TzDataSetVersion {
    private final com.android.i18n.timezone.TzDataSetVersion mDelegate;

    public static int currentFormatMajorVersion() {
        return com.android.i18n.timezone.TzDataSetVersion.currentFormatMajorVersion();
    }

    public static int currentFormatMinorVersion() {
        return com.android.i18n.timezone.TzDataSetVersion.currentFormatMinorVersion();
    }

    public static boolean isCompatibleWithThisDevice(TzDataSetVersion tzDataSetVersion) {
        return com.android.i18n.timezone.TzDataSetVersion.isCompatibleWithThisDevice(tzDataSetVersion.mDelegate);
    }

    public static TzDataSetVersion read() throws IOException, TzDataSetException {
        try {
            return new TzDataSetVersion(TimeZoneDataFiles.readTimeZoneModuleVersion());
        } catch (TzDataSetVersion.TzDataSetException e) {
            throw new TzDataSetException(e.getMessage(), e);
        }
    }

    /* loaded from: classes3.dex */
    public static final class TzDataSetException extends Exception {
        public TzDataSetException(String message) {
            super(message);
        }

        public TzDataSetException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private TzDataSetVersion(com.android.i18n.timezone.TzDataSetVersion delegate) {
        this.mDelegate = (com.android.i18n.timezone.TzDataSetVersion) Objects.requireNonNull(delegate);
    }

    public int getFormatMajorVersion() {
        return this.mDelegate.getFormatMajorVersion();
    }

    public int getFormatMinorVersion() {
        return this.mDelegate.getFormatMinorVersion();
    }

    public String getRulesVersion() {
        return this.mDelegate.getRulesVersion();
    }

    public int getRevision() {
        return this.mDelegate.getRevision();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TzDataSetVersion that = (TzDataSetVersion) o;
        return this.mDelegate.equals(that.mDelegate);
    }

    public int hashCode() {
        return Objects.hash(this.mDelegate);
    }

    public String toString() {
        return this.mDelegate.toString();
    }
}
