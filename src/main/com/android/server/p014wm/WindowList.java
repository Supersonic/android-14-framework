package com.android.server.p014wm;

import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.server.wm.WindowList */
/* loaded from: classes2.dex */
public class WindowList<E> extends ArrayList<E> {
    public void addFirst(E e) {
        add(0, e);
    }

    public E peekLast() {
        if (size() > 0) {
            return get(size() - 1);
        }
        return null;
    }

    public E peekFirst() {
        if (size() > 0) {
            return get(0);
        }
        return null;
    }
}
