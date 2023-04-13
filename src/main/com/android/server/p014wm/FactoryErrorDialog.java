package com.android.server.p014wm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import com.android.server.p006am.BaseErrorDialog;
/* renamed from: com.android.server.wm.FactoryErrorDialog */
/* loaded from: classes2.dex */
public final class FactoryErrorDialog extends BaseErrorDialog {
    public final Handler mHandler;

    @Override // com.android.server.p006am.BaseErrorDialog
    public void closeDialog() {
    }

    public FactoryErrorDialog(Context context, CharSequence charSequence) {
        super(context);
        Handler handler = new Handler() { // from class: com.android.server.wm.FactoryErrorDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                throw new RuntimeException("Rebooting from failed factory test");
            }
        };
        this.mHandler = handler;
        setCancelable(false);
        setTitle(context.getText(17040293));
        setMessage(charSequence);
        setButton(-1, context.getText(17040296), handler.obtainMessage(0));
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.setTitle("Factory Error");
        getWindow().setAttributes(attributes);
    }
}
