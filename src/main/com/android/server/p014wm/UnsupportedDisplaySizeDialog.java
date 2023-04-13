package com.android.server.p014wm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.android.server.p014wm.AppWarnings;
/* renamed from: com.android.server.wm.UnsupportedDisplaySizeDialog */
/* loaded from: classes2.dex */
public class UnsupportedDisplaySizeDialog extends AppWarnings.BaseDialog {
    public UnsupportedDisplaySizeDialog(final AppWarnings appWarnings, Context context, ApplicationInfo applicationInfo) {
        super(appWarnings, applicationInfo.packageName);
        AlertDialog create = new AlertDialog.Builder(context).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setMessage(context.getString(17041675, applicationInfo.loadSafeLabel(context.getPackageManager(), 1000.0f, 5))).setView(17367372).create();
        this.mDialog = create;
        create.create();
        Window window = this.mDialog.getWindow();
        window.setType(2002);
        window.getAttributes().setTitle("UnsupportedDisplaySizeDialog");
        CheckBox checkBox = (CheckBox) this.mDialog.findViewById(16908789);
        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.server.wm.UnsupportedDisplaySizeDialog$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                UnsupportedDisplaySizeDialog.this.lambda$new$0(appWarnings, compoundButton, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(AppWarnings appWarnings, CompoundButton compoundButton, boolean z) {
        appWarnings.setPackageFlag(this.mPackageName, 1, !z);
    }
}
