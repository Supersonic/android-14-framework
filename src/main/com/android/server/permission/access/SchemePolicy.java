package com.android.server.permission.access;

import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.server.p011pm.pkg.PackageState;
/* compiled from: AccessPolicy.kt */
/* loaded from: classes2.dex */
public abstract class SchemePolicy {
    public abstract int getDecision(GetStateScope getStateScope, AccessUri accessUri, AccessUri accessUri2);

    public abstract String getObjectScheme();

    public abstract String getSubjectScheme();

    public void onAppIdAdded(MutateStateScope mutateStateScope, int i) {
    }

    public void onAppIdRemoved(MutateStateScope mutateStateScope, int i) {
    }

    public void onInitialized(MutateStateScope mutateStateScope) {
    }

    public void onPackageAdded(MutateStateScope mutateStateScope, PackageState packageState) {
    }

    public void onPackageInstalled(MutateStateScope mutateStateScope, PackageState packageState, int i) {
    }

    public void onPackageRemoved(MutateStateScope mutateStateScope, String str, int i) {
    }

    public void onPackageUninstalled(MutateStateScope mutateStateScope, String str, int i, int i2) {
    }

    public void onStateMutated(GetStateScope getStateScope) {
    }

    public void onStorageVolumeMounted(MutateStateScope mutateStateScope, String str, boolean z) {
    }

    public void onSystemReady(MutateStateScope mutateStateScope) {
    }

    public void onUserAdded(MutateStateScope mutateStateScope, int i) {
    }

    public void onUserRemoved(MutateStateScope mutateStateScope, int i) {
    }

    public void parseSystemState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState) {
    }

    public void parseUserState(BinaryXmlPullParser binaryXmlPullParser, AccessState accessState, int i) {
    }

    public void serializeSystemState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState) {
    }

    public void serializeUserState(BinaryXmlSerializer binaryXmlSerializer, AccessState accessState, int i) {
    }

    public abstract void setDecision(MutateStateScope mutateStateScope, AccessUri accessUri, AccessUri accessUri2, int i);
}
