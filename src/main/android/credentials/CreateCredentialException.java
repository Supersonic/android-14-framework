package android.credentials;

import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public class CreateCredentialException extends Exception {
    public static final String TYPE_INTERRUPTED = "android.credentials.CreateCredentialException.TYPE_INTERRUPTED";
    public static final String TYPE_NO_CREATE_OPTIONS = "android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS";
    public static final String TYPE_UNKNOWN = "android.credentials.CreateCredentialException.TYPE_UNKNOWN";
    public static final String TYPE_USER_CANCELED = "android.credentials.CreateCredentialException.TYPE_USER_CANCELED";
    private final String mType;

    public String getType() {
        return this.mType;
    }

    public CreateCredentialException(String type, String message) {
        this(type, message, null);
    }

    public CreateCredentialException(String type, String message, Throwable cause) {
        super(message, cause);
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
    }

    public CreateCredentialException(String type, Throwable cause) {
        this(type, null, cause);
    }

    public CreateCredentialException(String type) {
        this(type, null, null);
    }
}
