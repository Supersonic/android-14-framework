package com.android.internal.app.chooser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.p008os.UserHandle;
import com.android.internal.app.ResolverActivity;
import java.util.List;
/* loaded from: classes4.dex */
public interface TargetInfo {
    TargetInfo cloneFilledIn(Intent intent, int i);

    List<Intent> getAllSourceIntents();

    Drawable getDisplayIcon(Context context);

    CharSequence getDisplayLabel();

    CharSequence getExtendedInfo();

    ResolveInfo getResolveInfo();

    ComponentName getResolvedComponentName();

    Intent getResolvedIntent();

    boolean isPinned();

    boolean isSuspended();

    boolean start(Activity activity, Bundle bundle);

    boolean startAsCaller(ResolverActivity resolverActivity, Bundle bundle, int i);

    boolean startAsUser(Activity activity, Bundle bundle, UserHandle userHandle);

    static void prepareIntentForCrossProfileLaunch(Intent intent, int targetUserId) {
        int currentUserId = UserHandle.myUserId();
        if (targetUserId != currentUserId) {
            intent.fixUris(currentUserId);
        }
    }
}
