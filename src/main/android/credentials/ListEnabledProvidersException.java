package android.credentials;

import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public class ListEnabledProvidersException extends Exception {
    private final String mType;

    public String getType() {
        return this.mType;
    }

    public ListEnabledProvidersException(String type, String message) {
        this(type, message, null);
    }

    public ListEnabledProvidersException(String type, String message, Throwable cause) {
        super(message, cause);
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
    }

    public ListEnabledProvidersException(String type, Throwable cause) {
        this(type, null, cause);
    }

    public ListEnabledProvidersException(String type) {
        this(type, null, null);
    }
}
