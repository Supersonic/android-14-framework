package com.android.server.p011pm;

import android.content.IIntentReceiver;
import android.os.Bundle;
import android.util.SparseArray;
/* renamed from: com.android.server.pm.PackageSender */
/* loaded from: classes2.dex */
public interface PackageSender {
    void notifyPackageRemoved(String str, int i);

    void sendPackageBroadcast(String str, String str2, Bundle bundle, int i, String str3, IIntentReceiver iIntentReceiver, int[] iArr, int[] iArr2, SparseArray<int[]> sparseArray, Bundle bundle2);
}
