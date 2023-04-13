package com.android.server.backup;

import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public final class SetUtils {
    public static <T> Set<T> union(Set<T> set, Set<T> set2) {
        HashSet hashSet = new HashSet(set);
        hashSet.addAll(set2);
        return hashSet;
    }
}
