package com.android.internal.infra;

import android.util.SparseArray;
import com.android.internal.util.Preconditions;
/* loaded from: classes4.dex */
public abstract class PerUser<T> extends SparseArray<T> {
    protected abstract T create(int i);

    public T forUser(int userId) {
        return get(userId);
    }

    @Override // android.util.SparseArray
    public T get(int userId) {
        T userState = (T) super.get(userId);
        if (userState != null) {
            return userState;
        }
        T userState2 = (T) Preconditions.checkNotNull(create(userId));
        put(userId, userState2);
        return userState2;
    }
}
