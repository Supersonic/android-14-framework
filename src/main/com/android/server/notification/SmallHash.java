package com.android.server.notification;

import android.p005os.IInstalld;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SmallHash {
    public static int hash(String str) {
        return hash(Objects.hashCode(str));
    }

    public static int hash(int i) {
        return Math.floorMod(i, (int) IInstalld.FLAG_FORCE);
    }
}
