package com.android.server.updates;
/* loaded from: classes2.dex */
public class SmartSelectionInstallReceiver extends ConfigUpdateInstallReceiver {
    @Override // com.android.server.updates.ConfigUpdateInstallReceiver
    public boolean verifyVersion(int i, int i2) {
        return true;
    }

    public SmartSelectionInstallReceiver() {
        super("/data/misc/textclassifier/", "textclassifier.model", "metadata/classification", "version");
    }
}
