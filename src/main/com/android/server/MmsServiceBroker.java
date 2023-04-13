package com.android.server;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.android.internal.telephony.IMms;
import com.android.internal.telephony.TelephonyPermissions;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.uri.UriGrantsManagerInternal;
import java.util.List;
/* loaded from: classes.dex */
public class MmsServiceBroker extends SystemService {
    public volatile AppOpsManager mAppOpsManager;
    public ServiceConnection mConnection;
    public final Handler mConnectionHandler;
    public Context mContext;
    public volatile PackageManager mPackageManager;
    public volatile IMms mService;
    public final IMms mServiceStubForFailure;
    public volatile TelephonyManager mTelephonyManager;
    public static final ComponentName MMS_SERVICE_COMPONENT = new ComponentName("com.android.mms.service", "com.android.mms.service.MmsService");
    public static final Uri FAKE_SMS_SENT_URI = Uri.parse("content://sms/sent/0");
    public static final Uri FAKE_MMS_SENT_URI = Uri.parse("content://mms/sent/0");
    public static final Uri FAKE_SMS_DRAFT_URI = Uri.parse("content://sms/draft/0");
    public static final Uri FAKE_MMS_DRAFT_URI = Uri.parse("content://mms/draft/0");

    public MmsServiceBroker(Context context) {
        super(context);
        this.mAppOpsManager = null;
        this.mPackageManager = null;
        this.mTelephonyManager = null;
        this.mConnectionHandler = new Handler() { // from class: com.android.server.MmsServiceBroker.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    MmsServiceBroker.this.tryConnecting();
                } else {
                    Slog.e("MmsServiceBroker", "Unknown message");
                }
            }
        };
        this.mConnection = new ServiceConnection() { // from class: com.android.server.MmsServiceBroker.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Slog.i("MmsServiceBroker", "MmsService connected");
                synchronized (MmsServiceBroker.this) {
                    MmsServiceBroker.this.mService = IMms.Stub.asInterface(Binder.allowBlocking(iBinder));
                    MmsServiceBroker.this.notifyAll();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Slog.i("MmsServiceBroker", "MmsService unexpectedly disconnected");
                synchronized (MmsServiceBroker.this) {
                    MmsServiceBroker.this.mService = null;
                    MmsServiceBroker.this.notifyAll();
                }
                MmsServiceBroker.this.mConnectionHandler.sendMessageDelayed(MmsServiceBroker.this.mConnectionHandler.obtainMessage(1), BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
            }
        };
        this.mServiceStubForFailure = new IMms() { // from class: com.android.server.MmsServiceBroker.3
            public Uri addMultimediaMessageDraft(String str, Uri uri) throws RemoteException {
                return null;
            }

            public Uri addTextMessageDraft(String str, String str2, String str3) throws RemoteException {
                return null;
            }

            public boolean archiveStoredConversation(String str, long j, boolean z) throws RemoteException {
                return false;
            }

            public IBinder asBinder() {
                return null;
            }

            public boolean deleteStoredConversation(String str, long j) throws RemoteException {
                return false;
            }

            public boolean deleteStoredMessage(String str, Uri uri) throws RemoteException {
                return false;
            }

            public boolean getAutoPersisting() throws RemoteException {
                return false;
            }

            public Uri importMultimediaMessage(String str, Uri uri, String str2, long j, boolean z, boolean z2) throws RemoteException {
                return null;
            }

            public Uri importTextMessage(String str, String str2, int i, String str3, long j, boolean z, boolean z2) throws RemoteException {
                return null;
            }

            public void setAutoPersisting(String str, boolean z) throws RemoteException {
            }

            public boolean updateStoredMessageStatus(String str, Uri uri, ContentValues contentValues) throws RemoteException {
                return false;
            }

            public void sendMessage(int i, String str, Uri uri, String str2, Bundle bundle, PendingIntent pendingIntent, long j, String str3) throws RemoteException {
                returnPendingIntentWithError(pendingIntent);
            }

            public void downloadMessage(int i, String str, String str2, Uri uri, Bundle bundle, PendingIntent pendingIntent, long j, String str3) throws RemoteException {
                returnPendingIntentWithError(pendingIntent);
            }

            public void sendStoredMessage(int i, String str, Uri uri, Bundle bundle, PendingIntent pendingIntent) throws RemoteException {
                returnPendingIntentWithError(pendingIntent);
            }

            public final void returnPendingIntentWithError(PendingIntent pendingIntent) {
                try {
                    pendingIntent.send(MmsServiceBroker.this.mContext, 1, (Intent) null);
                } catch (PendingIntent.CanceledException e) {
                    Slog.e("MmsServiceBroker", "Failed to return pending intent result", e);
                }
            }
        };
        this.mContext = context;
        this.mService = null;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("imms", new BinderService());
    }

    public void systemRunning() {
        Slog.i("MmsServiceBroker", "Delay connecting to MmsService until an API is called");
    }

    public final void tryConnecting() {
        Slog.i("MmsServiceBroker", "Connecting to MmsService");
        synchronized (this) {
            if (this.mService != null) {
                Slog.d("MmsServiceBroker", "Already connected");
                return;
            }
            Intent intent = new Intent();
            intent.setComponent(MMS_SERVICE_COMPONENT);
            try {
                if (!this.mContext.bindService(intent, this.mConnection, 1)) {
                    Slog.e("MmsServiceBroker", "Failed to bind to MmsService");
                }
            } catch (SecurityException e) {
                Slog.e("MmsServiceBroker", "Forbidden to bind to MmsService", e);
            }
        }
    }

    public final IMms getOrConnectService() {
        synchronized (this) {
            if (this.mService != null) {
                return this.mService;
            }
            Slog.w("MmsServiceBroker", "MmsService not connected. Try connecting...");
            Handler handler = this.mConnectionHandler;
            handler.sendMessage(handler.obtainMessage(1));
            long elapsedRealtime = SystemClock.elapsedRealtime() + 4000;
            for (long j = 4000; j > 0; j = elapsedRealtime - SystemClock.elapsedRealtime()) {
                try {
                    wait(j);
                } catch (InterruptedException e) {
                    Slog.w("MmsServiceBroker", "Connection wait interrupted", e);
                }
                if (this.mService != null) {
                    return this.mService;
                }
            }
            Slog.e("MmsServiceBroker", "Can not connect to MmsService (timed out)");
            return null;
        }
    }

    public final IMms getServiceGuarded() {
        IMms orConnectService = getOrConnectService();
        return orConnectService != null ? orConnectService : this.mServiceStubForFailure;
    }

    public final AppOpsManager getAppOpsManager() {
        if (this.mAppOpsManager == null) {
            this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
        }
        return this.mAppOpsManager;
    }

    /* loaded from: classes.dex */
    public final class BinderService extends IMms.Stub {
        public BinderService() {
        }

        public void sendMessage(int i, String str, Uri uri, String str2, Bundle bundle, PendingIntent pendingIntent, long j, String str3) throws RemoteException {
            Slog.d("MmsServiceBroker", "sendMessage() by " + str);
            MmsServiceBroker.this.mContext.enforceCallingPermission("android.permission.SEND_SMS", "Send MMS message");
            if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(MmsServiceBroker.this.mContext, i, Binder.getCallingUserHandle())) {
                if (TelephonyUtils.isUidForeground(MmsServiceBroker.this.mContext, Binder.getCallingUid())) {
                    TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(MmsServiceBroker.this.mContext, i);
                }
            } else if (MmsServiceBroker.this.getAppOpsManager().noteOp(20, Binder.getCallingUid(), str, str3, (String) null) != 0) {
                Slog.e("MmsServiceBroker", str + " is not allowed to call sendMessage()");
            } else {
                MmsServiceBroker.this.getServiceGuarded().sendMessage(i, str, adjustUriForUserAndGrantPermission(uri, "android.service.carrier.CarrierMessagingService", 1, i), str2, bundle, pendingIntent, j, str3);
            }
        }

        public void downloadMessage(int i, String str, String str2, Uri uri, Bundle bundle, PendingIntent pendingIntent, long j, String str3) throws RemoteException {
            Slog.d("MmsServiceBroker", "downloadMessage() by " + str);
            MmsServiceBroker.this.mContext.enforceCallingPermission("android.permission.RECEIVE_MMS", "Download MMS message");
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(18, Binder.getCallingUid(), str, str3, (String) null) != 0) {
                Slog.e("MmsServiceBroker", str + " is not allowed to call downloadMessage()");
                return;
            }
            MmsServiceBroker.this.getServiceGuarded().downloadMessage(i, str, str2, adjustUriForUserAndGrantPermission(uri, "android.service.carrier.CarrierMessagingService", 3, i), bundle, pendingIntent, j, str3);
        }

        public Uri importTextMessage(String str, String str2, int i, String str3, long j, boolean z, boolean z2) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return MmsServiceBroker.FAKE_SMS_SENT_URI;
            }
            return MmsServiceBroker.this.getServiceGuarded().importTextMessage(str, str2, i, str3, j, z, z2);
        }

        public Uri importMultimediaMessage(String str, Uri uri, String str2, long j, boolean z, boolean z2) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return MmsServiceBroker.FAKE_MMS_SENT_URI;
            }
            return MmsServiceBroker.this.getServiceGuarded().importMultimediaMessage(str, uri, str2, j, z, z2);
        }

        public boolean deleteStoredMessage(String str, Uri uri) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return false;
            }
            return MmsServiceBroker.this.getServiceGuarded().deleteStoredMessage(str, uri);
        }

        public boolean deleteStoredConversation(String str, long j) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return false;
            }
            return MmsServiceBroker.this.getServiceGuarded().deleteStoredConversation(str, j);
        }

        public boolean updateStoredMessageStatus(String str, Uri uri, ContentValues contentValues) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return false;
            }
            return MmsServiceBroker.this.getServiceGuarded().updateStoredMessageStatus(str, uri, contentValues);
        }

        public boolean archiveStoredConversation(String str, long j, boolean z) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return false;
            }
            return MmsServiceBroker.this.getServiceGuarded().archiveStoredConversation(str, j, z);
        }

        public Uri addTextMessageDraft(String str, String str2, String str3) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return MmsServiceBroker.FAKE_SMS_DRAFT_URI;
            }
            return MmsServiceBroker.this.getServiceGuarded().addTextMessageDraft(str, str2, str3);
        }

        public Uri addMultimediaMessageDraft(String str, Uri uri) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return MmsServiceBroker.FAKE_MMS_DRAFT_URI;
            }
            return MmsServiceBroker.this.getServiceGuarded().addMultimediaMessageDraft(str, uri);
        }

        public void sendStoredMessage(int i, String str, Uri uri, Bundle bundle, PendingIntent pendingIntent) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(20, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return;
            }
            MmsServiceBroker.this.getServiceGuarded().sendStoredMessage(i, str, uri, bundle, pendingIntent);
        }

        public void setAutoPersisting(String str, boolean z) throws RemoteException {
            if (MmsServiceBroker.this.getAppOpsManager().noteOp(15, Binder.getCallingUid(), str, (String) null, (String) null) != 0) {
                return;
            }
            MmsServiceBroker.this.getServiceGuarded().setAutoPersisting(str, z);
        }

        public boolean getAutoPersisting() throws RemoteException {
            return MmsServiceBroker.this.getServiceGuarded().getAutoPersisting();
        }

        public final Uri adjustUriForUserAndGrantPermission(Uri uri, String str, int i, int i2) {
            Intent intent = new Intent();
            intent.setData(uri);
            intent.setFlags(i);
            int callingUid = Binder.getCallingUid();
            int callingUserId = UserHandle.getCallingUserId();
            if (callingUserId != 0) {
                uri = ContentProvider.maybeAddUserId(uri, callingUserId);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                UriGrantsManagerInternal uriGrantsManagerInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
                uriGrantsManagerInternal.grantUriPermissionUncheckedFromIntent(uriGrantsManagerInternal.checkGrantUriPermissionFromIntent(intent, callingUid, "com.android.phone", 0), null);
                List carrierPackageNamesForIntentAndPhone = ((TelephonyManager) MmsServiceBroker.this.mContext.getSystemService("phone")).getCarrierPackageNamesForIntentAndPhone(new Intent(str), MmsServiceBroker.this.getPhoneIdFromSubId(i2));
                if (carrierPackageNamesForIntentAndPhone != null && carrierPackageNamesForIntentAndPhone.size() == 1) {
                    uriGrantsManagerInternal.grantUriPermissionUncheckedFromIntent(uriGrantsManagerInternal.checkGrantUriPermissionFromIntent(intent, callingUid, (String) carrierPackageNamesForIntentAndPhone.get(0), 0), null);
                }
                return uri;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final int getPhoneIdFromSubId(int i) {
        SubscriptionInfo activeSubscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service");
        if (subscriptionManager == null || (activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(i)) == null) {
            return -1;
        }
        return activeSubscriptionInfo.getSimSlotIndex();
    }
}
