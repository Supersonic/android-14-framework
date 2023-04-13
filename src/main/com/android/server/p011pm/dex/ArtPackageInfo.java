package com.android.server.p011pm.dex;

import java.util.List;
/* renamed from: com.android.server.pm.dex.ArtPackageInfo */
/* loaded from: classes2.dex */
public class ArtPackageInfo {
    public final List<String> mCodePaths;
    public final List<String> mInstructionSets;
    public final String mOatDir;
    public final String mPackageName;

    public ArtPackageInfo(String str, List<String> list, List<String> list2, String str2) {
        this.mPackageName = str;
        this.mInstructionSets = list;
        this.mCodePaths = list2;
        this.mOatDir = str2;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public List<String> getInstructionSets() {
        return this.mInstructionSets;
    }

    public List<String> getCodePaths() {
        return this.mCodePaths;
    }

    public String getOatDir() {
        return this.mOatDir;
    }
}
