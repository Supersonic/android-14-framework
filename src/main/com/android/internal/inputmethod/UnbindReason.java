package com.android.internal.inputmethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes4.dex */
public @interface UnbindReason {
    public static final int DISCONNECT_IME = 3;
    public static final int NO_IME = 4;
    public static final int SWITCH_CLIENT = 1;
    public static final int SWITCH_IME = 2;
    public static final int SWITCH_IME_FAILED = 5;
    public static final int SWITCH_USER = 6;
    public static final int UNSPECIFIED = 0;
}
