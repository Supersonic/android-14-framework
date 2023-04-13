package com.android.server.appop;

import android.os.PackageTagsList;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public interface AppOpsRestrictions {
    boolean clearGlobalRestrictions(Object obj);

    boolean clearUserRestrictions(Object obj);

    boolean clearUserRestrictions(Object obj, Integer num);

    void dumpRestrictions(PrintWriter printWriter, int i, String str, boolean z);

    boolean getGlobalRestriction(Object obj, int i);

    boolean getUserRestriction(Object obj, int i, int i2, String str, String str2, boolean z);

    boolean hasGlobalRestrictions(Object obj);

    boolean hasUserRestrictions(Object obj);

    boolean setGlobalRestriction(Object obj, int i, boolean z);

    boolean setUserRestriction(Object obj, int i, int i2, boolean z, PackageTagsList packageTagsList);
}
