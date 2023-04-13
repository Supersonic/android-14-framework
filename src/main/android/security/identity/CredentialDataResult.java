package android.security.identity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
/* loaded from: classes3.dex */
public abstract class CredentialDataResult {

    /* loaded from: classes3.dex */
    public interface Entries {
        public static final int STATUS_NOT_IN_REQUEST_MESSAGE = 3;
        public static final int STATUS_NOT_REQUESTED = 2;
        public static final int STATUS_NO_ACCESS_CONTROL_PROFILES = 6;
        public static final int STATUS_NO_SUCH_ENTRY = 1;
        public static final int STATUS_OK = 0;
        public static final int STATUS_READER_AUTHENTICATION_FAILED = 5;
        public static final int STATUS_USER_AUTHENTICATION_FAILED = 4;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface Status {
        }

        byte[] getEntry(String str, String str2);

        Collection<String> getEntryNames(String str);

        Collection<String> getNamespaces();

        Collection<String> getRetrievedEntryNames(String str);

        int getStatus(String str, String str2);
    }

    public abstract byte[] getDeviceMac();

    public abstract byte[] getDeviceNameSpaces();

    public abstract Entries getDeviceSignedEntries();

    public abstract Entries getIssuerSignedEntries();

    public abstract byte[] getStaticAuthenticationData();

    public byte[] getDeviceSignature() {
        throw new UnsupportedOperationException();
    }
}
