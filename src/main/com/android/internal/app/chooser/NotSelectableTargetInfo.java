package com.android.internal.app.chooser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.p008os.Bundle;
import android.p008os.UserHandle;
import android.service.chooser.ChooserTarget;
import com.android.internal.app.ResolverActivity;
import java.util.List;
/* loaded from: classes4.dex */
public abstract class NotSelectableTargetInfo implements ChooserTargetInfo {
    @Override // com.android.internal.app.chooser.TargetInfo
    public Intent getResolvedIntent() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public ComponentName getResolvedComponentName() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean start(Activity activity, Bundle options) {
        return false;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean startAsCaller(ResolverActivity activity, Bundle options, int userId) {
        return false;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean startAsUser(Activity activity, Bundle options, UserHandle user) {
        return false;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public ResolveInfo getResolveInfo() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public CharSequence getDisplayLabel() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public CharSequence getExtendedInfo() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public TargetInfo cloneFilledIn(Intent fillInIntent, int flags) {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public List<Intent> getAllSourceIntents() {
        return null;
    }

    @Override // com.android.internal.app.chooser.ChooserTargetInfo
    public float getModifiedScore() {
        return -0.1f;
    }

    @Override // com.android.internal.app.chooser.ChooserTargetInfo
    public ChooserTarget getChooserTarget() {
        return null;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean isSuspended() {
        return false;
    }

    @Override // com.android.internal.app.chooser.TargetInfo
    public boolean isPinned() {
        return false;
    }
}
