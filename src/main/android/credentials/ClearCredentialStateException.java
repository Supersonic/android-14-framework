package android.credentials;

import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public class ClearCredentialStateException extends Exception {
    public static final String TYPE_UNKNOWN = "android.credentials.ClearCredentialStateException.TYPE_UNKNOWN";
    private final String mType;

    public String getType() {
        return this.mType;
    }

    public ClearCredentialStateException(String type, String message) {
        this(type, message, null);
    }

    public ClearCredentialStateException(String type, String message, Throwable cause) {
        super(message, cause);
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
    }

    public ClearCredentialStateException(String type, Throwable cause) {
        this(type, null, cause);
    }

    public ClearCredentialStateException(String type) {
        this(type, null, null);
    }
}
