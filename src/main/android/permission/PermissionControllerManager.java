package android.permission;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.UserHandle;
import android.permission.IPermissionController;
import android.permission.PermissionControllerManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.infra.RemoteStream;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.p028os.BackgroundThread;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import libcore.util.EmptyArray;
@SystemApi
/* loaded from: classes3.dex */
public final class PermissionControllerManager {
    private static final int CHUNK_SIZE = 4096;
    public static final int COUNT_ONLY_WHEN_GRANTED = 1;
    public static final int COUNT_WHEN_SYSTEM = 2;
    @SystemApi
    public static final int HIBERNATION_ELIGIBILITY_ELIGIBLE = 0;
    @SystemApi
    public static final int HIBERNATION_ELIGIBILITY_EXEMPT_BY_SYSTEM = 1;
    @SystemApi
    public static final int HIBERNATION_ELIGIBILITY_EXEMPT_BY_USER = 2;
    @SystemApi
    public static final int HIBERNATION_ELIGIBILITY_UNKNOWN = -1;
    public static final int REASON_INSTALLER_POLICY_VIOLATION = 2;
    public static final int REASON_MALWARE = 1;
    private static final long REQUEST_TIMEOUT_MILLIS = 60000;
    private static final long UNBIND_TIMEOUT_MILLIS = 10000;
    private final Context mContext;
    private final Handler mHandler;
    private final ServiceConnector<IPermissionController> mRemoteService;
    private static final String TAG = PermissionControllerManager.class.getSimpleName();
    private static final Object sLock = new Object();
    private static ArrayMap<Pair<Integer, Thread>, ServiceConnector<IPermissionController>> sRemoteServices = new ArrayMap<>(1);

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CountPermissionAppsFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface HibernationEligibilityFlag {
    }

    /* loaded from: classes3.dex */
    public interface OnCountPermissionAppsResultCallback {
        void onCountPermissionApps(int i);
    }

    /* loaded from: classes3.dex */
    public interface OnGetAppPermissionResultCallback {
        void onGetAppPermissions(List<RuntimePermissionPresentationInfo> list);
    }

    /* loaded from: classes3.dex */
    public interface OnPermissionUsageResultCallback {
        void onPermissionUsageResult(List<RuntimePermissionUsageInfo> list);
    }

    /* loaded from: classes3.dex */
    public static abstract class OnRevokeRuntimePermissionsCallback {
        public abstract void onRevokeRuntimePermissions(Map<String, List<String>> map);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Reason {
    }

    public PermissionControllerManager(Context context, final Handler handler) {
        synchronized (sLock) {
            try {
                Pair<Integer, Thread> key = new Pair<>(Integer.valueOf(context.getUserId()), handler.getLooper().getThread());
                ServiceConnector<IPermissionController> remoteService = sRemoteServices.get(key);
                if (remoteService == null) {
                    Intent intent = new Intent(PermissionControllerService.SERVICE_INTERFACE);
                    String pkgName = context.getPackageManager().getPermissionControllerPackageName();
                    intent.setPackage(pkgName);
                    ResolveInfo serviceInfo = context.getPackageManager().resolveService(intent, 0);
                    if (serviceInfo == null) {
                        String errorMsg = "No PermissionController package (" + pkgName + ") for user " + context.getUserId();
                        Log.wtf(TAG, errorMsg);
                        throw new IllegalStateException(errorMsg);
                    }
                    remoteService = new ServiceConnector.Impl<IPermissionController>(ActivityThread.currentApplication(), new Intent(PermissionControllerService.SERVICE_INTERFACE).setComponent(serviceInfo.getComponentInfo().getComponentName()), 0, context.getUserId(), new Function() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda38
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return IPermissionController.Stub.asInterface((IBinder) obj);
                        }
                    }) { // from class: android.permission.PermissionControllerManager.1
                        @Override // com.android.internal.infra.ServiceConnector.Impl
                        protected Handler getJobHandler() {
                            return handler;
                        }

                        @Override // com.android.internal.infra.ServiceConnector.Impl
                        protected long getRequestTimeoutMs() {
                            return 60000L;
                        }

                        @Override // com.android.internal.infra.ServiceConnector.Impl
                        protected long getAutoDisconnectTimeoutMs() {
                            return 10000L;
                        }
                    };
                    sRemoteServices.put(key, remoteService);
                }
                this.mRemoteService = remoteService;
            } catch (Throwable th) {
                th = th;
                while (true) {
                    try {
                        break;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
                throw th;
            }
        }
        this.mContext = context;
        this.mHandler = handler;
    }

    private void enforceSomePermissionsGrantedToSelf(String... requiredPermissions) {
        for (String requiredPermission : requiredPermissions) {
            if (this.mContext.checkSelfPermission(requiredPermission) == 0) {
                return;
            }
        }
        throw new SecurityException("At lest one of the following permissions is required: " + Arrays.toString(requiredPermissions));
    }

    public void revokeRuntimePermissions(final Map<String, List<String>> request, final boolean doDryRun, final int reason, Executor executor, final OnRevokeRuntimePermissionsCallback callback) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        Preconditions.checkNotNull(request);
        for (Map.Entry<String, List<String>> appRequest : request.entrySet()) {
            Preconditions.checkNotNull(appRequest.getKey());
            Preconditions.checkCollectionElementsNotNull(appRequest.getValue(), "permissions");
        }
        enforceSomePermissionsGrantedToSelf(Manifest.C0000permission.REVOKE_RUNTIME_PERMISSIONS);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda21
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                CompletableFuture lambda$revokeRuntimePermissions$0;
                lambda$revokeRuntimePermissions$0 = PermissionControllerManager.this.lambda$revokeRuntimePermissions$0(request, doDryRun, reason, (IPermissionController) obj);
                return lambda$revokeRuntimePermissions$0;
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda22
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$revokeRuntimePermissions$1(PermissionControllerManager.OnRevokeRuntimePermissionsCallback.this, (Map) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$revokeRuntimePermissions$0(Map request, boolean doDryRun, int reason, IPermissionController service) throws Exception {
        Bundle bundledizedRequest = new Bundle();
        for (Map.Entry<String, List<String>> appRequest : request.entrySet()) {
            bundledizedRequest.putStringArrayList(appRequest.getKey(), new ArrayList<>(appRequest.getValue()));
        }
        AndroidFuture<Map<String, List<String>>> revokeRuntimePermissionsResult = new AndroidFuture<>();
        service.revokeRuntimePermissions(bundledizedRequest, doDryRun, reason, this.mContext.getPackageName(), revokeRuntimePermissionsResult);
        return revokeRuntimePermissionsResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$revokeRuntimePermissions$1(OnRevokeRuntimePermissionsCallback callback, Map revoked, Throwable err) {
        long token = Binder.clearCallingIdentity();
        try {
            if (err != null) {
                Log.m109e(TAG, "Failure when revoking runtime permissions " + revoked, err);
                callback.onRevokeRuntimePermissions(Collections.emptyMap());
            } else {
                callback.onRevokeRuntimePermissions(revoked);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void setRuntimePermissionGrantStateByDeviceAdmin(final String callerPackageName, final AdminPermissionControlParams params, Executor executor, final Consumer<Boolean> callback) {
        Preconditions.checkStringNotEmpty(callerPackageName);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        Objects.requireNonNull(params, "Admin control params must not be null.");
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda30
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$setRuntimePermissionGrantStateByDeviceAdmin$2(callerPackageName, params, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda31
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$setRuntimePermissionGrantStateByDeviceAdmin$3(callerPackageName, callback, (Boolean) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$setRuntimePermissionGrantStateByDeviceAdmin$2(String callerPackageName, AdminPermissionControlParams params, IPermissionController service) throws Exception {
        AndroidFuture<Boolean> setRuntimePermissionGrantStateResult = new AndroidFuture<>();
        service.setRuntimePermissionGrantStateByDeviceAdminFromParams(callerPackageName, params, setRuntimePermissionGrantStateResult);
        return setRuntimePermissionGrantStateResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setRuntimePermissionGrantStateByDeviceAdmin$3(String callerPackageName, Consumer callback, Boolean setRuntimePermissionGrantStateResult, Throwable err) {
        long token = Binder.clearCallingIdentity();
        try {
            if (err != null) {
                Log.m109e(TAG, "Error setting permissions state for device admin " + callerPackageName, err);
                callback.accept(false);
            } else {
                callback.accept(Boolean.valueOf(Boolean.TRUE.equals(setRuntimePermissionGrantStateResult)));
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void getRuntimePermissionBackup(final UserHandle user, Executor executor, final Consumer<byte[]> callback) {
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        enforceSomePermissionsGrantedToSelf(Manifest.C0000permission.GET_RUNTIME_PERMISSIONS);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda8
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                CompletableFuture receiveBytes;
                receiveBytes = RemoteStream.receiveBytes(new FunctionalUtils.ThrowingConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda2
                    @Override // com.android.internal.util.FunctionalUtils.ThrowingConsumer
                    public final void acceptOrThrow(Object obj2) {
                        IPermissionController.this.getRuntimePermissionBackup(r2, (ParcelFileDescriptor) obj2);
                    }
                });
                return receiveBytes;
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda9
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getRuntimePermissionBackup$6(callback, (byte[]) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getRuntimePermissionBackup$6(Consumer callback, byte[] bytes, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error getting permission backup", err);
            callback.accept(EmptyArray.BYTE);
            return;
        }
        callback.accept(bytes);
    }

    public void stageAndApplyRuntimePermissionsBackup(final byte[] backup, final UserHandle user) {
        Preconditions.checkNotNull(backup);
        Preconditions.checkNotNull(user);
        enforceSomePermissionsGrantedToSelf(Manifest.C0000permission.GRANT_RUNTIME_PERMISSIONS, Manifest.C0000permission.RESTORE_RUNTIME_PERMISSIONS);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda34
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                CompletableFuture sendBytes;
                sendBytes = RemoteStream.sendBytes(new FunctionalUtils.ThrowingConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda5
                    @Override // com.android.internal.util.FunctionalUtils.ThrowingConsumer
                    public final void acceptOrThrow(Object obj2) {
                        IPermissionController.this.stageAndApplyRuntimePermissionsBackup(r2, (ParcelFileDescriptor) obj2);
                    }
                }, backup);
                return sendBytes;
            }
        }).whenComplete((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda35
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$stageAndApplyRuntimePermissionsBackup$9((Void) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$stageAndApplyRuntimePermissionsBackup$9(Void nullResult, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error sending permission backup", err);
        }
    }

    public void applyStagedRuntimePermissionBackup(final String packageName, final UserHandle user, Executor executor, final Consumer<Boolean> callback) {
        Preconditions.checkNotNull(packageName);
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        enforceSomePermissionsGrantedToSelf(Manifest.C0000permission.GRANT_RUNTIME_PERMISSIONS, Manifest.C0000permission.RESTORE_RUNTIME_PERMISSIONS);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda32
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$applyStagedRuntimePermissionBackup$10(packageName, user, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda33
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$applyStagedRuntimePermissionBackup$11(packageName, callback, (Boolean) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$applyStagedRuntimePermissionBackup$10(String packageName, UserHandle user, IPermissionController service) throws Exception {
        AndroidFuture<Boolean> applyStagedRuntimePermissionBackupResult = new AndroidFuture<>();
        service.applyStagedRuntimePermissionBackup(packageName, user, applyStagedRuntimePermissionBackupResult);
        return applyStagedRuntimePermissionBackupResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$applyStagedRuntimePermissionBackup$11(String packageName, Consumer callback, Boolean applyStagedRuntimePermissionBackupResult, Throwable err) {
        long token = Binder.clearCallingIdentity();
        try {
            if (err != null) {
                Log.m109e(TAG, "Error restoring delayed permissions for " + packageName, err);
                callback.accept(true);
            } else {
                callback.accept(Boolean.valueOf(Boolean.TRUE.equals(applyStagedRuntimePermissionBackupResult)));
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void dump(final FileDescriptor fd, final String[] args) {
        try {
            this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda39
                @Override // com.android.internal.infra.ServiceConnector.Job
                public final Object run(Object obj) {
                    CompletableFuture runAsync;
                    runAsync = AndroidFuture.runAsync(FunctionalUtils.uncheckExceptions(new FunctionalUtils.ThrowingRunnable() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda15
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            IPermissionController.this.asBinder().dump(r2, r3);
                        }
                    }), BackgroundThread.getExecutor());
                    return runAsync;
                }
            }).get(60000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.m109e(TAG, "Could not get dump", e);
        }
    }

    public void getAppPermissions(final String packageName, final OnGetAppPermissionResultCallback callback, Handler handler) {
        Preconditions.checkNotNull(packageName);
        Preconditions.checkNotNull(callback);
        final Handler finalHandler = handler != null ? handler : this.mHandler;
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda0
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getAppPermissions$14(packageName, (IPermissionController) obj);
            }
        }).whenComplete((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Handler.this.post(new Runnable() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda40
                    @Override // java.lang.Runnable
                    public final void run() {
                        PermissionControllerManager.lambda$getAppPermissions$15(r1, r2, r3);
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getAppPermissions$14(String packageName, IPermissionController service) throws Exception {
        AndroidFuture<List<RuntimePermissionPresentationInfo>> getAppPermissionsResult = new AndroidFuture<>();
        service.getAppPermissions(packageName, getAppPermissionsResult);
        return getAppPermissionsResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getAppPermissions$15(Throwable err, OnGetAppPermissionResultCallback callback, List getAppPermissionsResult) {
        if (err != null) {
            Log.m109e(TAG, "Error getting app permission", err);
            callback.onGetAppPermissions(Collections.emptyList());
            return;
        }
        callback.onGetAppPermissions(CollectionUtils.emptyIfNull(getAppPermissionsResult));
    }

    public void revokeRuntimePermission(final String packageName, final String permissionName) {
        Preconditions.checkNotNull(packageName);
        Preconditions.checkNotNull(permissionName);
        this.mRemoteService.run(new ServiceConnector.VoidJob() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda29
            @Override // com.android.internal.infra.ServiceConnector.VoidJob
            public final void runNoResult(Object obj) {
                ((IPermissionController) obj).revokeRuntimePermission(packageName, permissionName);
            }
        });
    }

    public void countPermissionApps(final List<String> permissionNames, final int flags, final OnCountPermissionAppsResultCallback callback, Handler handler) {
        Preconditions.checkCollectionElementsNotNull(permissionNames, "permissionNames");
        Preconditions.checkFlagsArgument(flags, 3);
        Preconditions.checkNotNull(callback);
        final Handler finalHandler = handler != null ? handler : this.mHandler;
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda12
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$countPermissionApps$18(permissionNames, flags, (IPermissionController) obj);
            }
        }).whenComplete((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda13
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Handler.this.post(new Runnable() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda14
                    @Override // java.lang.Runnable
                    public final void run() {
                        PermissionControllerManager.lambda$countPermissionApps$19(r1, r2, r3);
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$countPermissionApps$18(List permissionNames, int flags, IPermissionController service) throws Exception {
        AndroidFuture<Integer> countPermissionAppsResult = new AndroidFuture<>();
        service.countPermissionApps(permissionNames, flags, countPermissionAppsResult);
        return countPermissionAppsResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$countPermissionApps$19(Throwable err, OnCountPermissionAppsResultCallback callback, Integer countPermissionAppsResult) {
        if (err != null) {
            Log.m109e(TAG, "Error counting permission apps", err);
            callback.onCountPermissionApps(0);
            return;
        }
        callback.onCountPermissionApps(countPermissionAppsResult.intValue());
    }

    public void getPermissionUsages(final boolean countSystem, final long numMillis, Executor executor, final OnPermissionUsageResultCallback callback) {
        Preconditions.checkArgumentNonnegative(numMillis);
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda25
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getPermissionUsages$21(countSystem, numMillis, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda26
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getPermissionUsages$22(PermissionControllerManager.OnPermissionUsageResultCallback.this, (List) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getPermissionUsages$21(boolean countSystem, long numMillis, IPermissionController service) throws Exception {
        AndroidFuture<List<RuntimePermissionUsageInfo>> getPermissionUsagesResult = new AndroidFuture<>();
        service.getPermissionUsages(countSystem, numMillis, getPermissionUsagesResult);
        return getPermissionUsagesResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getPermissionUsages$22(OnPermissionUsageResultCallback callback, List getPermissionUsagesResult, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error getting permission usages", err);
            callback.onPermissionUsageResult(Collections.emptyList());
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            callback.onPermissionUsageResult(CollectionUtils.emptyIfNull(getPermissionUsagesResult));
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void grantOrUpgradeDefaultRuntimePermissions(Executor executor, final Consumer<Boolean> callback) {
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda6
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$grantOrUpgradeDefaultRuntimePermissions$23((IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda7
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$grantOrUpgradeDefaultRuntimePermissions$24(callback, (Boolean) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$grantOrUpgradeDefaultRuntimePermissions$23(IPermissionController service) throws Exception {
        AndroidFuture<Boolean> grantOrUpgradeDefaultRuntimePermissionsResult = new AndroidFuture<>();
        service.grantOrUpgradeDefaultRuntimePermissions(grantOrUpgradeDefaultRuntimePermissionsResult);
        return grantOrUpgradeDefaultRuntimePermissionsResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$grantOrUpgradeDefaultRuntimePermissions$24(Consumer callback, Boolean grantOrUpgradeDefaultRuntimePermissionsResult, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error granting or upgrading runtime permissions", err);
            callback.accept(false);
            return;
        }
        callback.accept(Boolean.valueOf(Boolean.TRUE.equals(grantOrUpgradeDefaultRuntimePermissionsResult)));
    }

    public void getPrivilegesDescriptionStringForProfile(final String profileName, Executor executor, final Consumer<CharSequence> callback) {
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda18
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getPrivilegesDescriptionStringForProfile$25(profileName, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda19
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getPrivilegesDescriptionStringForProfile$26(callback, (String) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getPrivilegesDescriptionStringForProfile$25(String profileName, IPermissionController service) throws Exception {
        AndroidFuture<String> future = new AndroidFuture<>();
        service.getPrivilegesDescriptionStringForProfile(profileName, future);
        return future;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getPrivilegesDescriptionStringForProfile$26(Consumer callback, String description, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error from getPrivilegesDescriptionStringForProfile", err);
            callback.accept(null);
            return;
        }
        callback.accept(description);
    }

    public void updateUserSensitive() {
        updateUserSensitiveForApp(-1);
    }

    public void updateUserSensitiveForApp(final int uid) {
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda27
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$updateUserSensitiveForApp$27(uid, (IPermissionController) obj);
            }
        }).whenComplete((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda28
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$updateUserSensitiveForApp$28(uid, (Void) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$updateUserSensitiveForApp$27(int uid, IPermissionController service) throws Exception {
        AndroidFuture<Void> future = new AndroidFuture<>();
        service.updateUserSensitiveForApp(uid, future);
        return future;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateUserSensitiveForApp$28(int uid, Void res, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error updating user_sensitive flags for uid " + uid, err);
        }
    }

    public void notifyOneTimePermissionSessionTimeout(final String packageName) {
        this.mRemoteService.run(new ServiceConnector.VoidJob() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda20
            @Override // com.android.internal.infra.ServiceConnector.VoidJob
            public final void runNoResult(Object obj) {
                ((IPermissionController) obj).notifyOneTimePermissionSessionTimeout(packageName);
            }
        });
    }

    public void getPlatformPermissionsForGroup(final String permissionGroupName, Executor executor, final Consumer<List<String>> callback) {
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda3
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getPlatformPermissionsForGroup$30(permissionGroupName, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getPlatformPermissionsForGroup$31(permissionGroupName, callback, (List) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getPlatformPermissionsForGroup$30(String permissionGroupName, IPermissionController service) throws Exception {
        AndroidFuture<List<String>> future = new AndroidFuture<>();
        service.getPlatformPermissionsForGroup(permissionGroupName, future);
        return future;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getPlatformPermissionsForGroup$31(String permissionGroupName, Consumer callback, List result, Throwable err) {
        long token = Binder.clearCallingIdentity();
        try {
            if (err != null) {
                Log.m109e(TAG, "Failed to get permissions of " + permissionGroupName, err);
                callback.accept(new ArrayList());
            } else {
                callback.accept(result);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void getGroupOfPlatformPermission(final String permissionName, Executor executor, final Consumer<String> callback) {
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda10
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getGroupOfPlatformPermission$32(permissionName, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda11
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getGroupOfPlatformPermission$33(permissionName, callback, (String) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getGroupOfPlatformPermission$32(String permissionName, IPermissionController service) throws Exception {
        AndroidFuture<String> future = new AndroidFuture<>();
        service.getGroupOfPlatformPermission(permissionName, future);
        return future;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getGroupOfPlatformPermission$33(String permissionName, Consumer callback, String result, Throwable err) {
        long token = Binder.clearCallingIdentity();
        try {
            if (err != null) {
                Log.m109e(TAG, "Failed to get group of " + permissionName, err);
                callback.accept(null);
            } else {
                callback.accept(result);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void getUnusedAppCount(Executor executor, final IntConsumer callback) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda36
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getUnusedAppCount$34((IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda37
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getUnusedAppCount$35(callback, (Integer) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getUnusedAppCount$34(IPermissionController service) throws Exception {
        AndroidFuture<Integer> unusedAppCountResult = new AndroidFuture<>();
        service.getUnusedAppCount(unusedAppCountResult);
        return unusedAppCountResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getUnusedAppCount$35(IntConsumer callback, Integer count, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error getting unused app count", err);
            callback.accept(0);
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            callback.accept(count.intValue());
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void getHibernationEligibility(final String packageName, Executor executor, final IntConsumer callback) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(callback);
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda16
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$getHibernationEligibility$36(packageName, (IPermissionController) obj);
            }
        }).whenCompleteAsync((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda17
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$getHibernationEligibility$37(callback, (Integer) obj, (Throwable) obj2);
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$getHibernationEligibility$36(String packageName, IPermissionController service) throws Exception {
        AndroidFuture<Integer> eligibilityResult = new AndroidFuture<>();
        service.getHibernationEligibility(packageName, eligibilityResult);
        return eligibilityResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getHibernationEligibility$37(IntConsumer callback, Integer eligibility, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Error getting hibernation eligibility", err);
            callback.accept(-1);
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            callback.accept(eligibility.intValue());
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void revokeSelfPermissionsOnKill(final String packageName, final List<String> permissions) {
        this.mRemoteService.postAsync(new ServiceConnector.Job() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda23
            @Override // com.android.internal.infra.ServiceConnector.Job
            public final Object run(Object obj) {
                return PermissionControllerManager.lambda$revokeSelfPermissionsOnKill$38(packageName, permissions, (IPermissionController) obj);
            }
        }).whenComplete((BiConsumer<? super R, ? super Throwable>) new BiConsumer() { // from class: android.permission.PermissionControllerManager$$ExternalSyntheticLambda24
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PermissionControllerManager.lambda$revokeSelfPermissionsOnKill$39(permissions, packageName, (Void) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CompletableFuture lambda$revokeSelfPermissionsOnKill$38(String packageName, List permissions, IPermissionController service) throws Exception {
        AndroidFuture<Void> callback = new AndroidFuture<>();
        service.revokeSelfPermissionsOnKill(packageName, permissions, callback);
        return callback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$revokeSelfPermissionsOnKill$39(List permissions, String packageName, Void result, Throwable err) {
        if (err != null) {
            Log.m109e(TAG, "Failed to self revoke " + String.join(",", permissions) + " for package " + packageName, err);
        }
    }
}
