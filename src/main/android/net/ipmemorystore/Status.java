package android.net.ipmemorystore;

import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class Status {
    public static final int ERROR_DATABASE_CANNOT_BE_OPENED = -3;
    public static final int ERROR_GENERIC = -1;
    public static final int ERROR_ILLEGAL_ARGUMENT = -2;
    public static final int ERROR_STORAGE = -4;
    public static final int ERROR_UNKNOWN = -5;
    public static final int SUCCESS = 0;
    public final int resultCode;

    public Status(int i) {
        this.resultCode = i;
    }

    @VisibleForTesting
    public Status(StatusParcelable statusParcelable) {
        this(statusParcelable.resultCode);
    }

    public StatusParcelable toParcelable() {
        StatusParcelable statusParcelable = new StatusParcelable();
        statusParcelable.resultCode = this.resultCode;
        return statusParcelable;
    }

    public boolean isSuccess() {
        return this.resultCode == 0;
    }

    public String toString() {
        int i = this.resultCode;
        return i != -4 ? i != -3 ? i != -2 ? i != -1 ? i != 0 ? "Unknown value ?!" : "SUCCESS" : "GENERIC ERROR" : "ILLEGAL ARGUMENT" : "DATABASE CANNOT BE OPENED" : "DATABASE STORAGE ERROR";
    }
}
