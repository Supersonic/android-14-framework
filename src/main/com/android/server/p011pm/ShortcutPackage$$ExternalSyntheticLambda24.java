package com.android.server.p011pm;

import android.content.pm.ShortcutInfo;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.pm.ShortcutPackage$$ExternalSyntheticLambda24 */
/* loaded from: classes2.dex */
public final /* synthetic */ class ShortcutPackage$$ExternalSyntheticLambda24 implements Predicate {
    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((ShortcutInfo) obj).isNonManifestVisible();
    }
}
