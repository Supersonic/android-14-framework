package com.android.server.incident;

import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.content.AttributionSource;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IIncidentAuthListener;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.permission.PermissionManager;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PendingReports {
    public final AppOpsManager mAppOpsManager;
    public final Context mContext;
    public final Handler mHandler;
    public final Object mLock;
    public int mNextPendingId;
    public final PackageManager mPackageManager;
    public final ArrayList<PendingReportRec> mPending;
    public final PermissionManager mPermissionManager;
    public final RequestQueue mRequestQueue;

    /* loaded from: classes.dex */
    public final class PendingReportRec {
        public long addedRealtime;
        public long addedWalltime;
        public String callingPackage;
        public int flags;

        /* renamed from: id */
        public int f1145id;
        public IIncidentAuthListener listener;
        public String receiverClass;
        public String reportId;

        public PendingReportRec(String str, String str2, String str3, int i, IIncidentAuthListener iIncidentAuthListener) {
            int i2 = PendingReports.this.mNextPendingId;
            PendingReports.this.mNextPendingId = i2 + 1;
            this.f1145id = i2;
            this.callingPackage = str;
            this.flags = i;
            this.listener = iIncidentAuthListener;
            this.addedRealtime = SystemClock.elapsedRealtime();
            this.addedWalltime = System.currentTimeMillis();
            this.receiverClass = str2;
            this.reportId = str3;
        }

        public Uri getUri() {
            Uri.Builder appendQueryParameter = new Uri.Builder().scheme("content").authority("android.os.IncidentManager").path("/pending").appendQueryParameter("id", Integer.toString(this.f1145id)).appendQueryParameter("pkg", this.callingPackage).appendQueryParameter("flags", Integer.toString(this.flags)).appendQueryParameter("t", Long.toString(this.addedWalltime));
            String str = this.receiverClass;
            if (str != null && str.length() > 0) {
                appendQueryParameter.appendQueryParameter("receiver", this.receiverClass);
            }
            String str2 = this.reportId;
            if (str2 != null && str2.length() > 0) {
                appendQueryParameter.appendQueryParameter("r", this.reportId);
            }
            return appendQueryParameter.build();
        }
    }

    public PendingReports(Context context) {
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mRequestQueue = new RequestQueue(handler);
        this.mLock = new Object();
        this.mPending = new ArrayList<>();
        this.mNextPendingId = 1;
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mPermissionManager = (PermissionManager) context.getSystemService(PermissionManager.class);
    }

    public void authorizeReport(final int i, final String str, final String str2, final String str3, final int i2, final IIncidentAuthListener iIncidentAuthListener) {
        this.mRequestQueue.enqueue(iIncidentAuthListener.asBinder(), true, new Runnable() { // from class: com.android.server.incident.PendingReports$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PendingReports.this.lambda$authorizeReport$0(i, str, str2, str3, i2, iIncidentAuthListener);
            }
        });
    }

    public void cancelAuthorization(final IIncidentAuthListener iIncidentAuthListener) {
        this.mRequestQueue.enqueue(iIncidentAuthListener.asBinder(), false, new Runnable() { // from class: com.android.server.incident.PendingReports$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PendingReports.this.lambda$cancelAuthorization$1(iIncidentAuthListener);
            }
        });
    }

    public List<String> getPendingReports() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            int size = this.mPending.size();
            arrayList = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                arrayList.add(this.mPending.get(i).getUri().toString());
            }
        }
        return arrayList;
    }

    public void approveReport(String str) {
        synchronized (this.mLock) {
            PendingReportRec findAndRemovePendingReportRecLocked = findAndRemovePendingReportRecLocked(str);
            if (findAndRemovePendingReportRecLocked == null) {
                Log.e("IncidentCompanionService", "confirmApproved: Couldn't find record for uri: " + str);
                return;
            }
            sendBroadcast();
            Log.i("IncidentCompanionService", "Approved report: " + str);
            try {
                findAndRemovePendingReportRecLocked.listener.onReportApproved();
            } catch (RemoteException e) {
                Log.w("IncidentCompanionService", "Failed calling back for approval for: " + str, e);
            }
        }
    }

    public void denyReport(String str) {
        synchronized (this.mLock) {
            PendingReportRec findAndRemovePendingReportRecLocked = findAndRemovePendingReportRecLocked(str);
            if (findAndRemovePendingReportRecLocked == null) {
                Log.e("IncidentCompanionService", "confirmDenied: Couldn't find record for uri: " + str);
                return;
            }
            sendBroadcast();
            Log.i("IncidentCompanionService", "Denied report: " + str);
            try {
                findAndRemovePendingReportRecLocked.listener.onReportDenied();
            } catch (RemoteException e) {
                Log.w("IncidentCompanionService", "Failed calling back for denial for: " + str, e);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr.length == 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            synchronized (this.mLock) {
                int size = this.mPending.size();
                printWriter.println("mPending: (" + size + ")");
                for (int i = 0; i < size; i++) {
                    PendingReportRec pendingReportRec = this.mPending.get(i);
                    printWriter.println(String.format("  %11d %s: %s", Long.valueOf(pendingReportRec.addedRealtime), simpleDateFormat.format(new Date(pendingReportRec.addedWalltime)), pendingReportRec.getUri().toString()));
                }
            }
        }
    }

    public void onBootCompleted() {
        this.mRequestQueue.start();
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x00b3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0079 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* renamed from: authorizeReportImpl */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void lambda$authorizeReport$0(int i, String str, String str2, String str3, int i2, final IIncidentAuthListener iIncidentAuthListener) {
        boolean z;
        PendingReportRec pendingReportRec;
        if (i != 0 && !isPackageInUid(i, str)) {
            Log.w("IncidentCompanionService", "Calling uid " + i + " doesn't match package " + str);
            denyReportBeforeAddingRec(iIncidentAuthListener, str);
            return;
        }
        final int currentUserIfAdmin = getCurrentUserIfAdmin();
        int userId = UserHandle.getUserId(i);
        if (currentUserIfAdmin == -10000 || !isSameProfileGroupUser(userId, currentUserIfAdmin)) {
            Log.w("IncidentCompanionService", "Calling user " + userId + " doesn't belong to the same profile group of the current admin user " + currentUserIfAdmin);
            denyReportBeforeAddingRec(iIncidentAuthListener, str);
            return;
        }
        final ComponentName approverComponent = getApproverComponent(currentUserIfAdmin);
        if (approverComponent == null) {
            denyReportBeforeAddingRec(iIncidentAuthListener, str);
            return;
        }
        if ((Build.IS_USERDEBUG || Build.IS_ENG) && (i2 & 2) != 0) {
            if (this.mPermissionManager.checkPermissionForDataDelivery("android.permission.CAPTURE_CONSENTLESS_BUGREPORT_ON_USERDEBUG_BUILD", new AttributionSource.Builder(i).setPackageName(str).build(), (String) null) == 0) {
                z = true;
                if (z) {
                    try {
                        PendingReportRec pendingReportRec2 = new PendingReportRec(str, str2, str3, i2, iIncidentAuthListener);
                        Log.d("IncidentCompanionService", "approving consentless report: " + pendingReportRec2.getUri());
                        iIncidentAuthListener.onReportApproved();
                        return;
                    } catch (RemoteException e) {
                        Log.e("IncidentCompanionService", "authorizeReportImpl listener.onReportApproved RemoteException: ", e);
                    }
                }
                synchronized (this.mLock) {
                    this.mPending.add(new PendingReportRec(str, str2, str3, i2, iIncidentAuthListener));
                }
                try {
                    iIncidentAuthListener.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.incident.PendingReports$$ExternalSyntheticLambda2
                        @Override // android.os.IBinder.DeathRecipient
                        public final void binderDied() {
                            PendingReports.this.lambda$authorizeReportImpl$2(iIncidentAuthListener, approverComponent, currentUserIfAdmin);
                        }
                    }, 0);
                } catch (RemoteException unused) {
                    Log.e("IncidentCompanionService", "Remote died while trying to register death listener: " + pendingReportRec.getUri());
                    cancelReportImpl(iIncidentAuthListener, approverComponent, currentUserIfAdmin);
                }
                sendBroadcast(approverComponent, currentUserIfAdmin);
                return;
            }
        }
        z = false;
        if (z) {
        }
        synchronized (this.mLock) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$authorizeReportImpl$2(IIncidentAuthListener iIncidentAuthListener, ComponentName componentName, int i) {
        Log.i("IncidentCompanionService", "Got death notification listener=" + iIncidentAuthListener);
        cancelReportImpl(iIncidentAuthListener, componentName, i);
    }

    /* renamed from: cancelReportImpl */
    public final void lambda$cancelAuthorization$1(IIncidentAuthListener iIncidentAuthListener) {
        int currentUserIfAdmin = getCurrentUserIfAdmin();
        ComponentName approverComponent = getApproverComponent(currentUserIfAdmin);
        if (currentUserIfAdmin == -10000 || approverComponent == null) {
            return;
        }
        cancelReportImpl(iIncidentAuthListener, approverComponent, currentUserIfAdmin);
    }

    public final void cancelReportImpl(IIncidentAuthListener iIncidentAuthListener, ComponentName componentName, int i) {
        synchronized (this.mLock) {
            removePendingReportRecLocked(iIncidentAuthListener);
        }
        sendBroadcast(componentName, i);
    }

    public final void sendBroadcast() {
        ComponentName approverComponent;
        int currentUserIfAdmin = getCurrentUserIfAdmin();
        if (currentUserIfAdmin == -10000 || (approverComponent = getApproverComponent(currentUserIfAdmin)) == null) {
            return;
        }
        sendBroadcast(approverComponent, currentUserIfAdmin);
    }

    public final void sendBroadcast(ComponentName componentName, int i) {
        Intent intent = new Intent("android.intent.action.PENDING_INCIDENT_REPORTS_CHANGED");
        intent.setComponent(componentName);
        intent.addFlags(268435456);
        intent.addFlags(16777216);
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setBackgroundActivityStartsAllowed(true);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(i), "android.permission.APPROVE_INCIDENT_REPORTS", makeBasic.toBundle());
    }

    public final PendingReportRec findAndRemovePendingReportRecLocked(String str) {
        try {
            int parseInt = Integer.parseInt(Uri.parse(str).getQueryParameter("id"));
            Iterator<PendingReportRec> it = this.mPending.iterator();
            while (it.hasNext()) {
                PendingReportRec next = it.next();
                if (next.f1145id == parseInt) {
                    it.remove();
                    return next;
                }
            }
            return null;
        } catch (NumberFormatException unused) {
            Log.w("IncidentCompanionService", "Can't parse id from: " + str);
            return null;
        }
    }

    public final void removePendingReportRecLocked(IIncidentAuthListener iIncidentAuthListener) {
        Iterator<PendingReportRec> it = this.mPending.iterator();
        while (it.hasNext()) {
            PendingReportRec next = it.next();
            if (next.listener.asBinder() == iIncidentAuthListener.asBinder()) {
                Log.i("IncidentCompanionService", "  ...Removed PendingReportRec index=" + it + ": " + next.getUri());
                it.remove();
            }
        }
    }

    public final void denyReportBeforeAddingRec(IIncidentAuthListener iIncidentAuthListener, String str) {
        try {
            iIncidentAuthListener.onReportDenied();
        } catch (RemoteException e) {
            Log.w("IncidentCompanionService", "Failed calling back for denial for " + str, e);
        }
    }

    public final int getCurrentUserIfAdmin() {
        return IncidentCompanionService.getCurrentUserIfAdmin();
    }

    public final ComponentName getApproverComponent(int i) {
        List queryBroadcastReceiversAsUser = this.mPackageManager.queryBroadcastReceiversAsUser(new Intent("android.intent.action.PENDING_INCIDENT_REPORTS_CHANGED"), 1835008, i);
        if (queryBroadcastReceiversAsUser.size() == 1) {
            return ((ResolveInfo) queryBroadcastReceiversAsUser.get(0)).getComponentInfo().getComponentName();
        }
        Log.w("IncidentCompanionService", "Didn't find exactly one BroadcastReceiver to handle android.intent.action.PENDING_INCIDENT_REPORTS_CHANGED. The report will be denied. size=" + queryBroadcastReceiversAsUser.size() + ": matches=" + queryBroadcastReceiversAsUser);
        return null;
    }

    public final boolean isPackageInUid(int i, String str) {
        try {
            this.mAppOpsManager.checkPackage(i, str);
            return true;
        } catch (SecurityException unused) {
            return false;
        }
    }

    public final boolean isSameProfileGroupUser(int i, int i2) {
        return UserManager.get(this.mContext).isSameProfileGroup(i, i2);
    }
}
