package android.app;

import android.p008os.IBinder;
import com.android.internal.util.Preconditions;
import java.util.List;
/* loaded from: classes.dex */
public class BackgroundStartPrivileges {
    private final boolean mAllowsBackgroundActivityStarts;
    private final boolean mAllowsBackgroundForegroundServiceStarts;
    private final IBinder mOriginatingToken;
    public static final BackgroundStartPrivileges NONE = new BackgroundStartPrivileges(false, false, null);
    public static final BackgroundStartPrivileges ALLOW_BAL = new BackgroundStartPrivileges(true, true, null);
    public static final BackgroundStartPrivileges ALLOW_FGS = new BackgroundStartPrivileges(false, true, null);

    private BackgroundStartPrivileges(boolean allowsBackgroundActivityStarts, boolean allowsBackgroundForegroundServiceStarts, IBinder originatingToken) {
        Preconditions.checkArgument(!allowsBackgroundActivityStarts || allowsBackgroundForegroundServiceStarts, "backgroundActivityStarts implies bgFgServiceStarts");
        this.mAllowsBackgroundActivityStarts = allowsBackgroundActivityStarts;
        this.mAllowsBackgroundForegroundServiceStarts = allowsBackgroundForegroundServiceStarts;
        this.mOriginatingToken = originatingToken;
    }

    public static BackgroundStartPrivileges allowBackgroundActivityStarts(IBinder originatingToken) {
        if (originatingToken == null) {
            return ALLOW_BAL;
        }
        return new BackgroundStartPrivileges(true, true, originatingToken);
    }

    public BackgroundStartPrivileges merge(BackgroundStartPrivileges other) {
        BackgroundStartPrivileges backgroundStartPrivileges = NONE;
        if (other == backgroundStartPrivileges || other == null) {
            return this;
        }
        if (this == backgroundStartPrivileges) {
            return other;
        }
        boolean allowsBackgroundFgsStarts = false;
        boolean allowsBackgroundActivityStarts = allowsBackgroundActivityStarts() || other.allowsBackgroundActivityStarts();
        if (allowsBackgroundFgsStarts() || other.allowsBackgroundFgsStarts()) {
            allowsBackgroundFgsStarts = true;
        }
        IBinder iBinder = this.mOriginatingToken;
        if (iBinder == other.mOriginatingToken) {
            if (this.mAllowsBackgroundActivityStarts == allowsBackgroundActivityStarts && this.mAllowsBackgroundForegroundServiceStarts == allowsBackgroundFgsStarts) {
                return this;
            }
            if (other.mAllowsBackgroundActivityStarts == allowsBackgroundActivityStarts && other.mAllowsBackgroundForegroundServiceStarts == allowsBackgroundFgsStarts) {
                return other;
            }
            return new BackgroundStartPrivileges(allowsBackgroundActivityStarts, allowsBackgroundFgsStarts, iBinder);
        } else if (allowsBackgroundActivityStarts) {
            return ALLOW_BAL;
        } else {
            if (allowsBackgroundFgsStarts) {
                return ALLOW_FGS;
            }
            return backgroundStartPrivileges;
        }
    }

    public static BackgroundStartPrivileges merge(List<BackgroundStartPrivileges> list) {
        if (list == null || list.isEmpty()) {
            BackgroundStartPrivileges current = NONE;
            return current;
        }
        BackgroundStartPrivileges current2 = list.get(0);
        int i = list.size();
        while (true) {
            int i2 = i - 1;
            if (i > 1) {
                current2 = current2.merge(list.get(i2));
                i = i2;
            } else {
                return current2;
            }
        }
    }

    public boolean allowsBackgroundActivityStarts() {
        return this.mAllowsBackgroundActivityStarts;
    }

    public boolean allowsBackgroundFgsStarts() {
        return this.mAllowsBackgroundForegroundServiceStarts;
    }

    public boolean allowsAny() {
        return this.mAllowsBackgroundActivityStarts || this.mAllowsBackgroundForegroundServiceStarts;
    }

    public boolean allowsNothing() {
        return !allowsAny();
    }

    public IBinder getOriginatingToken() {
        return this.mOriginatingToken;
    }

    public String toString() {
        return "BackgroundStartPrivileges[allowsBackgroundActivityStarts=" + this.mAllowsBackgroundActivityStarts + ", allowsBackgroundForegroundServiceStarts=" + this.mAllowsBackgroundForegroundServiceStarts + ", originatingToken=" + this.mOriginatingToken + ']';
    }
}
