package com.android.server.p006am;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.p005os.IInstalld;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
/* renamed from: com.android.server.am.BaseErrorDialog */
/* loaded from: classes.dex */
public class BaseErrorDialog extends AlertDialog {
    public boolean mConsuming;
    public Handler mHandler;
    public BroadcastReceiver mReceiver;

    public BaseErrorDialog(Context context) {
        super(context, 16974814);
        this.mConsuming = true;
        this.mHandler = new Handler() { // from class: com.android.server.am.BaseErrorDialog.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 0) {
                    BaseErrorDialog.this.mConsuming = false;
                    BaseErrorDialog.this.setEnabled(true);
                } else if (i == 1) {
                    BaseErrorDialog.this.setEnabled(false);
                }
            }
        };
        context.assertRuntimeOverlayThemable();
        getWindow().setType(2003);
        getWindow().setFlags(IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.setTitle("Error Dialog");
        getWindow().setAttributes(attributes);
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        this.mHandler.sendEmptyMessage(1);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(0), 1000L);
        if (this.mReceiver == null) {
            this.mReceiver = new BroadcastReceiver() { // from class: com.android.server.am.BaseErrorDialog.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                        BaseErrorDialog.this.closeDialog();
                    }
                }
            };
            getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"), 2);
        }
    }

    @Override // android.app.Dialog
    public void onStop() {
        super.onStop();
        if (this.mReceiver != null) {
            try {
                getContext().unregisterReceiver(this.mReceiver);
            } catch (IllegalArgumentException e) {
                Slog.e("BaseErrorDialog", "unregisterReceiver threw exception: " + e.getMessage());
            }
            this.mReceiver = null;
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.mConsuming) {
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public final void setEnabled(boolean z) {
        Button button = (Button) findViewById(16908313);
        if (button != null) {
            button.setEnabled(z);
        }
        Button button2 = (Button) findViewById(16908314);
        if (button2 != null) {
            button2.setEnabled(z);
        }
        Button button3 = (Button) findViewById(16908315);
        if (button3 != null) {
            button3.setEnabled(z);
        }
    }

    public void closeDialog() {
        if (((AlertDialog) this).mCancelable) {
            cancel();
        } else {
            dismiss();
        }
    }
}
