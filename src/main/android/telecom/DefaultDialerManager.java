package android.telecom;

import android.app.ActivityManager;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Binder;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class DefaultDialerManager {
    private static final String TAG = "DefaultDialerManager";

    public static boolean setDefaultDialerApplication(Context context, String packageName) {
        return setDefaultDialerApplication(context, packageName, ActivityManager.getCurrentUser());
    }

    public static boolean setDefaultDialerApplication(Context context, String packageName, int user) {
        long identity = Binder.clearCallingIdentity();
        try {
            try {
                final CompletableFuture<Void> future = new CompletableFuture<>();
                Consumer<Boolean> callback = new Consumer() { // from class: android.telecom.DefaultDialerManager$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DefaultDialerManager.lambda$setDefaultDialerApplication$0(future, (Boolean) obj);
                    }
                };
                ((RoleManager) context.getSystemService(RoleManager.class)).addRoleHolderAsUser("android.app.role.DIALER", packageName, 0, UserHandle.m145of(user), AsyncTask.THREAD_POOL_EXECUTOR, callback);
                future.get(5L, TimeUnit.SECONDS);
                Binder.restoreCallingIdentity(identity);
                return true;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Slog.m95e(TAG, "Failed to set default dialer to " + packageName + " for user " + user, e);
                Binder.restoreCallingIdentity(identity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setDefaultDialerApplication$0(CompletableFuture future, Boolean successful) {
        if (successful.booleanValue()) {
            future.complete(null);
        } else {
            future.completeExceptionally(new RuntimeException());
        }
    }

    public static String getDefaultDialerApplication(Context context) {
        return getDefaultDialerApplication(context, context.getUserId());
    }

    public static String getDefaultDialerApplication(Context context, int user) {
        long identity = Binder.clearCallingIdentity();
        try {
            return (String) CollectionUtils.firstOrNull((List<Object>) ((RoleManager) context.getSystemService(RoleManager.class)).getRoleHoldersAsUser("android.app.role.DIALER", UserHandle.m145of(user)));
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public static List<String> getInstalledDialerApplications(Context context, int userId) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivitiesAsUser(intent, 0, userId);
        List<String> packageNames = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && !packageNames.contains(activityInfo.packageName) && resolveInfo.targetUserId == -2) {
                packageNames.add(activityInfo.packageName);
            }
        }
        Intent dialIntentWithTelScheme = new Intent(Intent.ACTION_DIAL);
        dialIntentWithTelScheme.setData(Uri.fromParts(PhoneAccount.SCHEME_TEL, "", null));
        return filterByIntent(context, packageNames, dialIntentWithTelScheme, userId);
    }

    public static List<String> getInstalledDialerApplications(Context context) {
        return getInstalledDialerApplications(context, Process.myUserHandle().getIdentifier());
    }

    public static boolean isDefaultOrSystemDialer(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        TelecomManager tm = getTelecomManager(context);
        return packageName.equals(tm.getDefaultDialerPackage()) || packageName.equals(tm.getSystemDialerPackage());
    }

    private static List<String> filterByIntent(Context context, List<String> packageNames, Intent intent, int userId) {
        if (packageNames == null || packageNames.isEmpty()) {
            return new ArrayList();
        }
        List<String> result = new ArrayList<>();
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivitiesAsUser(intent, 0, userId);
        int length = resolveInfoList.size();
        for (int i = 0; i < length; i++) {
            ActivityInfo info = resolveInfoList.get(i).activityInfo;
            if (info != null && packageNames.contains(info.packageName) && !result.contains(info.packageName)) {
                result.add(info.packageName);
            }
        }
        return result;
    }

    private static TelecomManager getTelecomManager(Context context) {
        return (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
    }
}
