package com.android.internal.app.chooser;

import android.app.Activity;
import android.p008os.Bundle;
import android.p008os.UserHandle;
import com.android.internal.app.ResolverActivity;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public class MultiDisplayResolveInfo extends DisplayResolveInfo {
    final DisplayResolveInfo mBaseInfo;
    private int mSelected;
    ArrayList<DisplayResolveInfo> mTargetInfos;

    public MultiDisplayResolveInfo(String packageName, DisplayResolveInfo firstInfo) {
        super(firstInfo);
        ArrayList<DisplayResolveInfo> arrayList = new ArrayList<>();
        this.mTargetInfos = arrayList;
        this.mSelected = -1;
        this.mBaseInfo = firstInfo;
        arrayList.add(firstInfo);
    }

    @Override // com.android.internal.app.chooser.DisplayResolveInfo, com.android.internal.app.chooser.TargetInfo
    public CharSequence getExtendedInfo() {
        return null;
    }

    public void addTarget(DisplayResolveInfo target) {
        this.mTargetInfos.add(target);
    }

    public ArrayList<DisplayResolveInfo> getTargets() {
        return this.mTargetInfos;
    }

    public void setSelected(int selected) {
        this.mSelected = selected;
    }

    public DisplayResolveInfo getSelectedTarget() {
        if (hasSelected()) {
            return this.mTargetInfos.get(this.mSelected);
        }
        return null;
    }

    public boolean hasSelected() {
        return this.mSelected >= 0;
    }

    @Override // com.android.internal.app.chooser.DisplayResolveInfo, com.android.internal.app.chooser.TargetInfo
    public boolean start(Activity activity, Bundle options) {
        return this.mTargetInfos.get(this.mSelected).start(activity, options);
    }

    @Override // com.android.internal.app.chooser.DisplayResolveInfo, com.android.internal.app.chooser.TargetInfo
    public boolean startAsCaller(ResolverActivity activity, Bundle options, int userId) {
        return this.mTargetInfos.get(this.mSelected).startAsCaller(activity, options, userId);
    }

    @Override // com.android.internal.app.chooser.DisplayResolveInfo, com.android.internal.app.chooser.TargetInfo
    public boolean startAsUser(Activity activity, Bundle options, UserHandle user) {
        return this.mTargetInfos.get(this.mSelected).startAsUser(activity, options, user);
    }
}
