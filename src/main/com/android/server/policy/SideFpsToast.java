package com.android.server.policy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
/* loaded from: classes2.dex */
public class SideFpsToast extends Dialog {
    public SideFpsToast(Context context) {
        super(context);
    }

    @Override // android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(17367324);
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.0f;
        attributes.flags |= 2;
        attributes.gravity = 80;
        window.setAttributes(attributes);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        Button button = (Button) findViewById(16909662);
        if (button != null) {
            button.setOnClickListener(onClickListener);
        }
    }
}
