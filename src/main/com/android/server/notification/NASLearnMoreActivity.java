package com.android.server.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
/* loaded from: classes2.dex */
public class NASLearnMoreActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        showLearnMoreDialog();
    }

    public final void showLearnMoreDialog() {
        AlertDialog create = new AlertDialog.Builder(this).setMessage(17040825).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.server.notification.NASLearnMoreActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                NASLearnMoreActivity.this.finish();
            }
        }).create();
        create.getWindow().setType(2003);
        create.show();
    }
}
