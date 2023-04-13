package com.android.server.p006am;
/* renamed from: com.android.server.am.ProcessMemInfo */
/* loaded from: classes.dex */
public class ProcessMemInfo {
    public final String adjReason;
    public final String adjType;
    public long memtrack;
    public final String name;
    public final int oomAdj;
    public final int pid;
    public final int procState;
    public long pss;
    public long swapPss;

    public ProcessMemInfo(String str, int i, int i2, int i3, String str2, String str3) {
        this.name = str;
        this.pid = i;
        this.oomAdj = i2;
        this.procState = i3;
        this.adjType = str2;
        this.adjReason = str3;
    }
}
