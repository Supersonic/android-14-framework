package com.android.internal.util.function.pooled;

import android.telecom.Logging.Session;
/* loaded from: classes3.dex */
public final class ArgumentPlaceholder<R> {
    static final ArgumentPlaceholder<?> INSTANCE = new ArgumentPlaceholder<>();

    private ArgumentPlaceholder() {
    }

    public String toString() {
        return Session.SESSION_SEPARATION_CHAR_CHILD;
    }
}
