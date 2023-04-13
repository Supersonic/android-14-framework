package com.android.server.p011pm;
/* renamed from: com.android.server.pm.ApexSystemServiceInfo */
/* loaded from: classes2.dex */
public final class ApexSystemServiceInfo implements Comparable<ApexSystemServiceInfo> {
    public final int mInitOrder;
    public final String mJarPath;
    public final String mName;

    public ApexSystemServiceInfo(String str, String str2, int i) {
        this.mName = str;
        this.mJarPath = str2;
        this.mInitOrder = i;
    }

    public String getName() {
        return this.mName;
    }

    public String getJarPath() {
        return this.mJarPath;
    }

    @Override // java.lang.Comparable
    public int compareTo(ApexSystemServiceInfo apexSystemServiceInfo) {
        int i = this.mInitOrder;
        int i2 = apexSystemServiceInfo.mInitOrder;
        if (i == i2) {
            return this.mName.compareTo(apexSystemServiceInfo.mName);
        }
        return -Integer.compare(i, i2);
    }
}
