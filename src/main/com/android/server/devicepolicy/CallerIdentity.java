package com.android.server.devicepolicy;

import android.content.ComponentName;
import android.os.UserHandle;
/* loaded from: classes.dex */
public final class CallerIdentity {
    public final ComponentName mComponentName;
    public final String mPackageName;
    public final int mUid;

    public CallerIdentity(int i, String str, ComponentName componentName) {
        this.mUid = i;
        this.mPackageName = str;
        this.mComponentName = componentName;
    }

    public int getUid() {
        return this.mUid;
    }

    public int getUserId() {
        return UserHandle.getUserId(this.mUid);
    }

    public UserHandle getUserHandle() {
        return UserHandle.getUserHandleForUid(this.mUid);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public boolean hasAdminComponent() {
        return this.mComponentName != null;
    }

    public boolean hasPackage() {
        return this.mPackageName != null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CallerIdentity[uid=");
        sb.append(this.mUid);
        if (this.mPackageName != null) {
            sb.append(", pkg=");
            sb.append(this.mPackageName);
        }
        if (this.mComponentName != null) {
            sb.append(", cmp=");
            sb.append(this.mComponentName.flattenToShortString());
        }
        sb.append("]");
        return sb.toString();
    }
}
