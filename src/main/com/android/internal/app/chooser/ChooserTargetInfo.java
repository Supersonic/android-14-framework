package com.android.internal.app.chooser;

import android.service.chooser.ChooserTarget;
import android.text.TextUtils;
/* loaded from: classes4.dex */
public interface ChooserTargetInfo extends TargetInfo {
    ChooserTarget getChooserTarget();

    float getModifiedScore();

    default boolean isSimilar(ChooserTargetInfo other) {
        if (other == null) {
            return false;
        }
        ChooserTarget ct1 = getChooserTarget();
        ChooserTarget ct2 = other.getChooserTarget();
        if (ct1 == null || ct2 == null || !ct1.getComponentName().equals(ct2.getComponentName()) || !TextUtils.equals(getDisplayLabel(), other.getDisplayLabel()) || !TextUtils.equals(getExtendedInfo(), other.getExtendedInfo())) {
            return false;
        }
        return true;
    }
}
