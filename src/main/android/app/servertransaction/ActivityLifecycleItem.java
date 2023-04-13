package android.app.servertransaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public abstract class ActivityLifecycleItem extends ActivityTransactionItem {
    public static final int ON_CREATE = 1;
    public static final int ON_DESTROY = 6;
    public static final int ON_PAUSE = 4;
    public static final int ON_RESTART = 7;
    public static final int ON_RESUME = 3;
    public static final int ON_START = 2;
    public static final int ON_STOP = 5;
    public static final int PRE_ON_CREATE = 0;
    public static final int UNDEFINED = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LifecycleState {
    }

    public abstract int getTargetState();

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
    }
}
