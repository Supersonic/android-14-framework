package android.p008os;

import android.annotation.SystemApi;
import android.app.ActivityManager;
import android.content.Context;
import android.p008os.BugreportManager;
import android.p008os.IDumpstateListener;
import android.widget.Toast;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
import libcore.io.IoUtils;
/* renamed from: android.os.BugreportManager */
/* loaded from: classes3.dex */
public final class BugreportManager {
    private static final String TAG = "BugreportManager";
    private final IDumpstate mBinder;
    private final Context mContext;

    public BugreportManager(Context context, IDumpstate binder) {
        this.mContext = context;
        this.mBinder = binder;
    }

    /* renamed from: android.os.BugreportManager$BugreportCallback */
    /* loaded from: classes3.dex */
    public static abstract class BugreportCallback {
        public static final int BUGREPORT_ERROR_ANOTHER_REPORT_IN_PROGRESS = 5;
        public static final int BUGREPORT_ERROR_INVALID_INPUT = 1;
        public static final int BUGREPORT_ERROR_NO_BUGREPORT_TO_RETRIEVE = 6;
        public static final int BUGREPORT_ERROR_RUNTIME = 2;
        public static final int BUGREPORT_ERROR_USER_CONSENT_TIMED_OUT = 4;
        public static final int BUGREPORT_ERROR_USER_DENIED_CONSENT = 3;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.os.BugreportManager$BugreportCallback$BugreportErrorCode */
        /* loaded from: classes3.dex */
        public @interface BugreportErrorCode {
        }

        public void onProgress(float progress) {
        }

        public void onError(int errorCode) {
        }

        public void onFinished() {
        }

        @SystemApi
        public void onFinished(String bugreportFile) {
        }

        public void onEarlyReportFinished() {
        }
    }

    @SystemApi
    public void preDumpUiData() {
        try {
            this.mBinder.preDumpUiData(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(12:1|(3:2|3|(1:5)(1:33))|(8:9|(2:11|12)(1:31)|13|14|15|(1:20)|17|18)|32|(0)(0)|13|14|15|(0)|17|18|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x006c, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x006e, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0077, code lost:
        android.util.Log.wtf(android.p008os.BugreportManager.TAG, "Not able to find /dev/null file: ", r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x007f, code lost:
        libcore.io.IoUtils.closeQuietly(r19);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0082, code lost:
        if (r8 == null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x008d, code lost:
        throw r0.rethrowFromSystemServer();
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:?, code lost:
        return;
     */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0026 A[Catch: all -> 0x0070, FileNotFoundException -> 0x0074, RemoteException -> 0x0086, TRY_LEAVE, TryCatch #4 {RemoteException -> 0x0086, FileNotFoundException -> 0x0074, all -> 0x0070, blocks: (B:3:0x0002, B:13:0x0026), top: B:42:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0085 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0094  */
    @SystemApi
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void startBugreport(ParcelFileDescriptor bugreportFd, ParcelFileDescriptor screenshotFd, BugreportParams params, Executor executor, BugreportCallback callback) {
        ParcelFileDescriptor screenshotFd2;
        boolean deferConsent;
        boolean isScreenshotRequested;
        try {
            try {
                Preconditions.checkNotNull(bugreportFd);
                Preconditions.checkNotNull(params);
                Preconditions.checkNotNull(executor);
                Preconditions.checkNotNull(callback);
                deferConsent = (params.getFlags() & 2) != 0;
            } catch (Throwable th) {
                e = th;
                IoUtils.closeQuietly(bugreportFd);
                if (screenshotFd != null) {
                    IoUtils.closeQuietly(screenshotFd);
                }
                throw e;
            }
        } catch (RemoteException e) {
            e = e;
        } catch (FileNotFoundException e2) {
            e = e2;
            screenshotFd2 = screenshotFd;
        } catch (Throwable th2) {
            e = th2;
            IoUtils.closeQuietly(bugreportFd);
            if (screenshotFd != null) {
            }
            throw e;
        }
        if (screenshotFd == null && !deferConsent) {
            isScreenshotRequested = false;
            if (screenshotFd == null) {
                screenshotFd2 = screenshotFd;
            } else {
                screenshotFd2 = ParcelFileDescriptor.open(new File("/dev/null"), 268435456);
            }
            DumpstateListener dsListener = new DumpstateListener(executor, callback, isScreenshotRequested, deferConsent);
            this.mBinder.startBugreport(-1, this.mContext.getOpPackageName(), bugreportFd.getFileDescriptor(), screenshotFd2.getFileDescriptor(), params.getMode(), params.getFlags(), dsListener, isScreenshotRequested);
            IoUtils.closeQuietly(bugreportFd);
            if (screenshotFd2 == null) {
                return;
            }
            IoUtils.closeQuietly(screenshotFd2);
        }
        isScreenshotRequested = true;
        if (screenshotFd == null) {
        }
        DumpstateListener dsListener2 = new DumpstateListener(executor, callback, isScreenshotRequested, deferConsent);
        this.mBinder.startBugreport(-1, this.mContext.getOpPackageName(), bugreportFd.getFileDescriptor(), screenshotFd2.getFileDescriptor(), params.getMode(), params.getFlags(), dsListener2, isScreenshotRequested);
        IoUtils.closeQuietly(bugreportFd);
        if (screenshotFd2 == null) {
        }
        IoUtils.closeQuietly(screenshotFd2);
    }

    @SystemApi
    public void retrieveBugreport(String bugreportFile, ParcelFileDescriptor bugreportFd, Executor executor, BugreportCallback callback) {
        try {
            try {
                Preconditions.checkNotNull(bugreportFile);
                Preconditions.checkNotNull(bugreportFd);
                Preconditions.checkNotNull(executor);
                Preconditions.checkNotNull(callback);
                DumpstateListener dsListener = new DumpstateListener(executor, callback, false, false);
                this.mBinder.retrieveBugreport(Binder.getCallingUid(), this.mContext.getOpPackageName(), bugreportFd.getFileDescriptor(), bugreportFile, dsListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        } finally {
            IoUtils.closeQuietly(bugreportFd);
        }
    }

    public void startConnectivityBugreport(ParcelFileDescriptor bugreportFd, Executor executor, BugreportCallback callback) {
        startBugreport(bugreportFd, null, new BugreportParams(4), executor, callback);
    }

    public void cancelBugreport() {
        try {
            this.mBinder.cancelBugreport(-1, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void requestBugreport(BugreportParams params, CharSequence shareTitle, CharSequence shareDescription) {
        String title;
        String description = null;
        if (shareTitle == null) {
            title = null;
        } else {
            try {
                title = shareTitle.toString();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        if (shareDescription != null) {
            description = shareDescription.toString();
        }
        ActivityManager.getService().requestBugReportWithDescription(title, description, params.getMode());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.BugreportManager$DumpstateListener */
    /* loaded from: classes3.dex */
    public final class DumpstateListener extends IDumpstateListener.Stub {
        private final BugreportCallback mCallback;
        private final Executor mExecutor;
        private final boolean mIsConsentDeferred;
        private final boolean mIsScreenshotRequested;

        DumpstateListener(Executor executor, BugreportCallback callback, boolean isScreenshotRequested, boolean isConsentDeferred) {
            this.mExecutor = executor;
            this.mCallback = callback;
            this.mIsScreenshotRequested = isScreenshotRequested;
            this.mIsConsentDeferred = isConsentDeferred;
        }

        @Override // android.p008os.IDumpstateListener
        public void onProgress(final int progress) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.os.BugreportManager$DumpstateListener$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        BugreportManager.DumpstateListener.this.lambda$onProgress$0(progress);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProgress$0(int progress) {
            this.mCallback.onProgress(progress);
        }

        @Override // android.p008os.IDumpstateListener
        public void onError(final int errorCode) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.os.BugreportManager$DumpstateListener$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        BugreportManager.DumpstateListener.this.lambda$onError$1(errorCode);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1(int errorCode) {
            this.mCallback.onError(errorCode);
        }

        @Override // android.p008os.IDumpstateListener
        public void onFinished(final String bugreportFile) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                if (this.mIsConsentDeferred) {
                    this.mExecutor.execute(new Runnable() { // from class: android.os.BugreportManager$DumpstateListener$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            BugreportManager.DumpstateListener.this.lambda$onFinished$2(bugreportFile);
                        }
                    });
                } else {
                    this.mExecutor.execute(new Runnable() { // from class: android.os.BugreportManager$DumpstateListener$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            BugreportManager.DumpstateListener.this.lambda$onFinished$3();
                        }
                    });
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFinished$2(String bugreportFile) {
            this.mCallback.onFinished(bugreportFile);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFinished$3() {
            this.mCallback.onFinished();
        }

        @Override // android.p008os.IDumpstateListener
        public void onScreenshotTaken(final boolean success) throws RemoteException {
            if (!this.mIsScreenshotRequested) {
                return;
            }
            Handler mainThreadHandler = new Handler(Looper.getMainLooper());
            mainThreadHandler.post(new Runnable() { // from class: android.os.BugreportManager$DumpstateListener$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BugreportManager.DumpstateListener.this.lambda$onScreenshotTaken$4(success);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onScreenshotTaken$4(boolean success) {
            int message;
            if (success) {
                message = C4057R.string.bugreport_screenshot_success_toast;
            } else {
                message = C4057R.string.bugreport_screenshot_failure_toast;
            }
            Toast.makeText(BugreportManager.this.mContext, message, 1).show();
        }

        @Override // android.p008os.IDumpstateListener
        public void onUiIntensiveBugreportDumpsFinished() throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.os.BugreportManager$DumpstateListener$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        BugreportManager.DumpstateListener.this.lambda$onUiIntensiveBugreportDumpsFinished$5();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUiIntensiveBugreportDumpsFinished$5() {
            this.mCallback.onEarlyReportFinished();
        }
    }
}
