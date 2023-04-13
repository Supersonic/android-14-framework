package com.android.server.p006am;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.internal.annotations.GuardedBy;
import com.android.server.backup.BackupAgentTimeoutParameters;
/* renamed from: com.android.server.am.UserSwitchingDialog */
/* loaded from: classes.dex */
public class UserSwitchingDialog extends AlertDialog implements ViewTreeObserver.OnWindowShownListener {
    public final Context mContext;
    public final Handler mHandler;
    public final UserInfo mNewUser;
    public final UserInfo mOldUser;
    public final ActivityManagerService mService;
    @GuardedBy({"this"})
    public boolean mStartedUser;
    public final String mSwitchingFromSystemUserMessage;
    public final String mSwitchingToSystemUserMessage;
    public final int mUserId;

    public UserSwitchingDialog(ActivityManagerService activityManagerService, Context context, UserInfo userInfo, UserInfo userInfo2, boolean z, String str, String str2) {
        super(context);
        this.mHandler = new Handler() { // from class: com.android.server.am.UserSwitchingDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                Slog.w("ActivityManagerUserSwitchingDialog", "user switch window not shown in 3000 ms");
                UserSwitchingDialog.this.startUser();
            }
        };
        this.mContext = context;
        this.mService = activityManagerService;
        this.mUserId = userInfo2.id;
        this.mOldUser = userInfo;
        this.mNewUser = userInfo2;
        this.mSwitchingFromSystemUserMessage = str;
        this.mSwitchingToSystemUserMessage = str2;
        inflateContent();
        if (z) {
            getWindow().setType(2010);
        }
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.privateFlags = 272;
        getWindow().setAttributes(attributes);
    }

    public void inflateContent() {
        String str;
        String string;
        setCancelable(false);
        Resources resources = getContext().getResources();
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(17367373, (ViewGroup) null);
        if (UserManager.isDeviceInDemoMode(this.mContext)) {
            if (this.mOldUser.isDemo()) {
                string = resources.getString(17040120);
            } else {
                string = resources.getString(17040121);
            }
        } else {
            if (this.mOldUser.id == 0) {
                str = this.mSwitchingFromSystemUserMessage;
            } else {
                str = this.mNewUser.id == 0 ? this.mSwitchingToSystemUserMessage : null;
            }
            string = str == null ? resources.getString(17041706, this.mNewUser.name) : str;
            textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, getContext().getDrawable(17302885), (Drawable) null, (Drawable) null);
        }
        textView.setAccessibilityPaneTitle(string);
        textView.setText(string);
        setView(textView);
    }

    @Override // android.app.Dialog
    public void show() {
        Slog.d("ActivityManagerUserSwitchingDialog", "show called");
        super.show();
        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.getViewTreeObserver().addOnWindowShownListener(this);
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
    }

    public void onWindowShown() {
        Slog.d("ActivityManagerUserSwitchingDialog", "onWindowShown called");
        startUser();
    }

    public void startUser() {
        synchronized (this) {
            if (!this.mStartedUser) {
                Slog.i("ActivityManagerUserSwitchingDialog", "starting user " + this.mUserId);
                this.mService.mUserController.startUserInForeground(this.mUserId);
                dismiss();
                this.mStartedUser = true;
                View decorView = getWindow().getDecorView();
                if (decorView != null) {
                    decorView.getViewTreeObserver().removeOnWindowShownListener(this);
                }
                this.mHandler.removeMessages(1);
            } else {
                Slog.i("ActivityManagerUserSwitchingDialog", "user " + this.mUserId + " already started");
            }
        }
    }
}
