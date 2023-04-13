package com.android.server.p014wm;

import android.app.ActivityManagerInternal;
import java.util.function.BiConsumer;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda12 */
/* loaded from: classes2.dex */
public final /* synthetic */ class ActivityTaskManagerService$$ExternalSyntheticLambda12 implements BiConsumer {
    @Override // java.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((ActivityManagerInternal) obj).updateOomLevelsForDisplay(((Integer) obj2).intValue());
    }
}
