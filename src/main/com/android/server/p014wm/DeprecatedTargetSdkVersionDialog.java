package com.android.server.p014wm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.view.Window;
import com.android.server.p014wm.AppWarnings;
import com.android.server.utils.AppInstallerUtil;
/* renamed from: com.android.server.wm.DeprecatedTargetSdkVersionDialog */
/* loaded from: classes2.dex */
public class DeprecatedTargetSdkVersionDialog extends AppWarnings.BaseDialog {
    public DeprecatedTargetSdkVersionDialog(final AppWarnings appWarnings, final Context context, ApplicationInfo applicationInfo) {
        super(appWarnings, applicationInfo.packageName);
        AlertDialog.Builder title = new AlertDialog.Builder(context).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.server.wm.DeprecatedTargetSdkVersionDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DeprecatedTargetSdkVersionDialog.this.lambda$new$0(appWarnings, dialogInterface, i);
            }
        }).setMessage(context.getString(17040124)).setTitle(applicationInfo.loadSafeLabel(context.getPackageManager(), 1000.0f, 5));
        final Intent createIntent = AppInstallerUtil.createIntent(context, applicationInfo.packageName);
        if (createIntent != null) {
            title.setNeutralButton(17040123, new DialogInterface.OnClickListener() { // from class: com.android.server.wm.DeprecatedTargetSdkVersionDialog$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    context.startActivity(createIntent);
                }
            });
        }
        AlertDialog create = title.create();
        this.mDialog = create;
        create.create();
        Window window = this.mDialog.getWindow();
        window.setType(2002);
        window.getAttributes().setTitle("DeprecatedTargetSdkVersionDialog");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(AppWarnings appWarnings, DialogInterface dialogInterface, int i) {
        appWarnings.setPackageFlag(this.mPackageName, 4, true);
    }
}
