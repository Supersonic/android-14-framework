package android.security.identity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
@Deprecated
/* loaded from: classes3.dex */
public abstract class ResultData {
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

    public abstract byte[] getAuthenticatedData();

    public abstract byte[] getEntry(String str, String str2);

    public abstract Collection<String> getEntryNames(String str);

    public abstract byte[] getMessageAuthenticationCode();

    public abstract Collection<String> getNamespaces();

    public abstract Collection<String> getRetrievedEntryNames(String str);

    public abstract byte[] getStaticAuthenticationData();

    public abstract int getStatus(String str, String str2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getSignature() {
        throw new UnsupportedOperationException();
    }
}
