package com.android.server.p011pm;

import android.os.Bundle;
import android.os.UserManager;
import android.util.IntArray;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.BundleUtils;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.server.pm.RestrictionsSet */
/* loaded from: classes2.dex */
public class RestrictionsSet {
    public final SparseArray<Bundle> mUserRestrictions = new SparseArray<>(0);

    public boolean updateRestrictions(int i, Bundle bundle) {
        if (!UserRestrictionsUtils.areEqual(this.mUserRestrictions.get(i), bundle)) {
            if (!BundleUtils.isEmpty(bundle)) {
                this.mUserRestrictions.put(i, bundle);
            } else {
                this.mUserRestrictions.delete(i);
            }
            return true;
        }
        return false;
    }

    public boolean removeRestrictionsForAllUsers(String str) {
        boolean z = false;
        for (int i = 0; i < this.mUserRestrictions.size(); i++) {
            Bundle valueAt = this.mUserRestrictions.valueAt(i);
            if (UserRestrictionsUtils.contains(valueAt, str)) {
                valueAt.remove(str);
                z = true;
            }
        }
        return z;
    }

    public List<UserManager.EnforcingUser> getEnforcingUsers(String str, int i) {
        ArrayList arrayList = new ArrayList();
        if (getRestrictionsNonNull(i).containsKey(str)) {
            arrayList.add(new UserManager.EnforcingUser(i, 4));
        }
        if (getRestrictionsNonNull(-1).containsKey(str)) {
            arrayList.add(new UserManager.EnforcingUser(-1, 2));
        }
        return arrayList;
    }

    public Bundle getRestrictions(int i) {
        return this.mUserRestrictions.get(i);
    }

    public Bundle getRestrictionsNonNull(int i) {
        return UserRestrictionsUtils.nonNull(this.mUserRestrictions.get(i));
    }

    public boolean remove(int i) {
        boolean contains = this.mUserRestrictions.contains(i);
        this.mUserRestrictions.remove(i);
        return contains;
    }

    public void removeAllRestrictions() {
        this.mUserRestrictions.clear();
    }

    public IntArray getUserIds() {
        IntArray intArray = new IntArray(this.mUserRestrictions.size());
        for (int i = 0; i < this.mUserRestrictions.size(); i++) {
            intArray.add(this.mUserRestrictions.keyAt(i));
        }
        return intArray;
    }

    @VisibleForTesting
    public int size() {
        return this.mUserRestrictions.size();
    }

    @VisibleForTesting
    public int keyAt(int i) {
        return this.mUserRestrictions.keyAt(i);
    }

    @VisibleForTesting
    public Bundle valueAt(int i) {
        return this.mUserRestrictions.valueAt(i);
    }
}
