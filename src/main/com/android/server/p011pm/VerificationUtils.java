package com.android.server.p011pm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import com.android.server.backup.BackupAgentTimeoutParameters;
/* renamed from: com.android.server.pm.VerificationUtils */
/* loaded from: classes2.dex */
public final class VerificationUtils {
    public static long getVerificationTimeout(Context context, boolean z) {
        if (z) {
            return getDefaultStreamingVerificationTimeout(context);
        }
        return getDefaultVerificationTimeout(context);
    }

    public static long getDefaultVerificationTimeout(Context context) {
        return Math.max(Settings.Global.getLong(context.getContentResolver(), "verifier_timeout", 10000L), 10000L);
    }

    public static long getDefaultStreamingVerificationTimeout(Context context) {
        return Math.max(Settings.Global.getLong(context.getContentResolver(), "streaming_verifier_timeout", BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS), (long) BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
    }

    public static void broadcastPackageVerified(int i, Uri uri, int i2, String str, int i3, UserHandle userHandle, Context context) {
        Intent intent = new Intent("android.intent.action.PACKAGE_VERIFIED");
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(1);
        intent.putExtra("android.content.pm.extra.VERIFICATION_ID", i);
        intent.putExtra("android.content.pm.extra.VERIFICATION_RESULT", i2);
        if (str != null) {
            intent.putExtra("android.content.pm.extra.VERIFICATION_ROOT_HASH", str);
        }
        intent.putExtra("android.content.pm.extra.DATA_LOADER_TYPE", i3);
        context.sendBroadcastAsUser(intent, userHandle, "android.permission.PACKAGE_VERIFICATION_AGENT");
    }

    public static void processVerificationResponse(int i, PackageVerificationState packageVerificationState, PackageVerificationResponse packageVerificationResponse, String str, PackageManagerService packageManagerService) {
        packageVerificationState.setVerifierResponse(packageVerificationResponse.callerUid, packageVerificationResponse.code);
        if (packageVerificationState.isVerificationComplete()) {
            VerifyingSession verifyingSession = packageVerificationState.getVerifyingSession();
            Uri fromFile = Uri.fromFile(verifyingSession.mOriginInfo.mResolvedFile);
            broadcastPackageVerified(i, fromFile, packageVerificationState.isInstallAllowed() ? packageVerificationResponse.code : -1, null, verifyingSession.getDataLoaderType(), verifyingSession.getUser(), packageManagerService.mContext);
            if (packageVerificationState.isInstallAllowed()) {
                Slog.i("PackageManager", "Continuing with installation of " + fromFile);
            } else {
                String str2 = str + " for " + fromFile;
                Slog.i("PackageManager", str2);
                verifyingSession.setReturnCode(-22, str2);
            }
            if (packageVerificationState.areAllVerificationsComplete()) {
                packageManagerService.mPendingVerification.remove(i);
            }
            Trace.asyncTraceEnd(262144L, "verification", i);
            verifyingSession.handleVerificationFinished();
        }
    }
}
