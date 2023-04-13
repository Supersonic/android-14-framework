package com.android.server;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.FunctionalUtils;
import com.android.server.utils.Slogf;
import java.io.IOException;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class MasterClearReceiver extends BroadcastReceiver {
    public boolean mWipeEsims;
    public boolean mWipeExternalStorage;

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE") && !"google.com".equals(intent.getStringExtra("from"))) {
            Slog.w("MasterClear", "Ignoring master clear request -- not from trusted server.");
            return;
        }
        if ("android.intent.action.MASTER_CLEAR".equals(intent.getAction())) {
            Slog.w("MasterClear", "The request uses the deprecated Intent#ACTION_MASTER_CLEAR, Intent#ACTION_FACTORY_RESET should be used instead.");
        }
        if (intent.hasExtra("android.intent.extra.FORCE_MASTER_CLEAR")) {
            Slog.w("MasterClear", "The request uses the deprecated Intent#EXTRA_FORCE_MASTER_CLEAR, Intent#EXTRA_FORCE_FACTORY_RESET should be used instead.");
        }
        String string = context.getString(17039935);
        if ("android.intent.action.FACTORY_RESET".equals(intent.getAction()) && !TextUtils.isEmpty(string)) {
            Slog.i("MasterClear", "Re-directing intent to " + string);
            intent.setPackage(string).setComponent(null);
            context.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
            return;
        }
        final boolean booleanExtra = intent.getBooleanExtra("shutdown", false);
        final String stringExtra = intent.getStringExtra("android.intent.extra.REASON");
        this.mWipeExternalStorage = intent.getBooleanExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", false);
        this.mWipeEsims = intent.getBooleanExtra("com.android.internal.intent.extra.WIPE_ESIMS", false);
        final boolean z = intent.getBooleanExtra("android.intent.extra.FORCE_MASTER_CLEAR", false) || intent.getBooleanExtra("android.intent.extra.FORCE_FACTORY_RESET", false);
        final int sendingUserId = getSendingUserId();
        if (sendingUserId != 0 && !UserManager.isHeadlessSystemUserMode()) {
            Slogf.m12w("MasterClear", "ACTION_FACTORY_RESET received on a non-system user %d, WIPING THE USER!!", Integer.valueOf(sendingUserId));
            if (((Boolean) Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.MasterClearReceiver$$ExternalSyntheticLambda0
                public final Object getOrThrow() {
                    Boolean lambda$onReceive$0;
                    lambda$onReceive$0 = MasterClearReceiver.this.lambda$onReceive$0(context, sendingUserId, stringExtra);
                    return lambda$onReceive$0;
                }
            })).booleanValue()) {
                return;
            }
            Slogf.m24e("MasterClear", "Failed to wipe user %d", Integer.valueOf(sendingUserId));
            return;
        }
        Slog.w("MasterClear", "!!! FACTORY RESET !!!");
        Thread thread = new Thread("Reboot") { // from class: com.android.server.MasterClearReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Slog.i("MasterClear", "Calling RecoverySystem.rebootWipeUserData(context, shutdown=" + booleanExtra + ", reason=" + stringExtra + ", forceWipe=" + z + ", wipeEsims=" + MasterClearReceiver.this.mWipeEsims + ")");
                    RecoverySystem.rebootWipeUserData(context, booleanExtra, stringExtra, z, MasterClearReceiver.this.mWipeEsims);
                    Slog.wtf("MasterClear", "Still running after master clear?!");
                } catch (IOException e) {
                    Slog.e("MasterClear", "Can't perform master clear/factory reset", e);
                } catch (SecurityException e2) {
                    Slog.e("MasterClear", "Can't perform master clear/factory reset", e2);
                }
            }
        };
        if (this.mWipeExternalStorage) {
            Slog.i("MasterClear", "Wiping external storage on async task");
            new WipeDataTask(context, thread).execute(new Void[0]);
            return;
        }
        Slog.i("MasterClear", "NOT wiping external storage; starting thread " + thread.getName());
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$onReceive$0(Context context, int i, String str) throws Exception {
        return Boolean.valueOf(wipeUser(context, i, str));
    }

    public final boolean wipeUser(Context context, int i, String str) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        if (!UserManager.isRemoveResultSuccessful(userManager.removeUserWhenPossible(UserHandle.of(i), false))) {
            Slogf.m24e("MasterClear", "Can't remove user %d", Integer.valueOf(i));
            return false;
        }
        if (getCurrentForegroundUserId() == i) {
            try {
                if (!ActivityManager.getService().switchUser(0)) {
                    Slogf.m12w("MasterClear", "Can't switch from current user %d, user will get removed when it is stopped.", Integer.valueOf(i));
                }
            } catch (RemoteException unused) {
                Slogf.m12w("MasterClear", "Can't switch from current user %d, user will get removed when it is stopped.", Integer.valueOf(i));
            }
        }
        if (userManager.isManagedProfile(i)) {
            sendWipeProfileNotification(context, str);
        }
        return true;
    }

    public final void sendWipeProfileNotification(Context context, String str) {
        ((NotificationManager) context.getSystemService(NotificationManager.class)).notify(1001, new Notification.Builder(context, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17301642).setContentTitle(getWorkProfileDeletedTitle(context)).setContentText(str).setColor(context.getColor(17170460)).setStyle(new Notification.BigTextStyle().bigText(str)).build());
    }

    public final String getWorkProfileDeletedTitle(final Context context) {
        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString("Core.WORK_PROFILE_DELETED_TITLE", new Supplier() { // from class: com.android.server.MasterClearReceiver$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String string;
                string = context.getString(17041803);
                return string;
            }
        });
    }

    public final int getCurrentForegroundUserId() {
        try {
            return ActivityManager.getCurrentUser();
        } catch (Exception e) {
            Slogf.m25e("MasterClear", "Can't get current user", e);
            return -10000;
        }
    }

    /* loaded from: classes.dex */
    public class WipeDataTask extends AsyncTask<Void, Void, Void> {
        public final Thread mChainedTask;
        public final Context mContext;
        public final ProgressDialog mProgressDialog;

        public WipeDataTask(Context context, Thread thread) {
            this.mContext = context;
            this.mChainedTask = thread;
            this.mProgressDialog = new ProgressDialog(context, 16974843);
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            this.mProgressDialog.setIndeterminate(true);
            this.mProgressDialog.getWindow().setType(2003);
            this.mProgressDialog.setMessage(this.mContext.getText(17041374));
            this.mProgressDialog.show();
        }

        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Slog.w("MasterClear", "Wiping adoptable disks");
            if (MasterClearReceiver.this.mWipeExternalStorage) {
                ((StorageManager) this.mContext.getSystemService("storage")).wipeAdoptableDisks();
                return null;
            }
            return null;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            this.mProgressDialog.dismiss();
            this.mChainedTask.start();
        }
    }
}
