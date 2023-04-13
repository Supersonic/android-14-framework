package com.android.internal.telephony;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
/* loaded from: classes.dex */
public class PackageBasedTokenUtil {
    private static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    public static String generateToken(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        String generatePackageBasedToken = generatePackageBasedToken(packageManager, str);
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(128)) {
            String str2 = packageInfo.packageName;
            if (!str.equals(str2) && generatePackageBasedToken.equals(generatePackageBasedToken(packageManager, str2))) {
                Log.e("PackageBasedTokenUtil", "token collides with other installed app.");
                generatePackageBasedToken = null;
            }
        }
        return generatePackageBasedToken;
    }

    private static String generatePackageBasedToken(PackageManager packageManager, String str) {
        try {
            Signature[] signatureArr = packageManager.getPackageInfo(str, 64).signatures;
            if (signatureArr == null) {
                Log.e("PackageBasedTokenUtil", "The certificates is missing.");
                return null;
            }
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                Charset charset = CHARSET_UTF_8;
                messageDigest.update(str.getBytes(charset));
                messageDigest.update(" ".getBytes(charset));
                for (Signature signature : signatureArr) {
                    messageDigest.update(signature.toCharsString().getBytes(CHARSET_UTF_8));
                }
                return Base64.encodeToString(Arrays.copyOf(messageDigest.digest(), 9), 3).substring(0, 11);
            } catch (NoSuchAlgorithmException e) {
                Log.e("PackageBasedTokenUtil", "NoSuchAlgorithmException" + e);
                return null;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("PackageBasedTokenUtil", "Failed to find package with package name: " + str);
            return null;
        }
    }
}
