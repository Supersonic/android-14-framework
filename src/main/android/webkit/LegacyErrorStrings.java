package android.webkit;

import android.content.Context;
import android.util.Log;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
class LegacyErrorStrings {
    private static final String LOGTAG = "Http";

    private LegacyErrorStrings() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getString(int errorCode, Context context) {
        return context.getText(getResource(errorCode)).toString();
    }

    private static int getResource(int errorCode) {
        switch (errorCode) {
            case -15:
                return C4057R.string.httpErrorTooManyRequests;
            case -14:
                return C4057R.string.httpErrorFileNotFound;
            case -13:
                return C4057R.string.httpErrorFile;
            case -12:
                return 17039367;
            case -11:
                return C4057R.string.httpErrorFailedSslHandshake;
            case -10:
                return 17039368;
            case -9:
                return C4057R.string.httpErrorRedirectLoop;
            case -8:
                return C4057R.string.httpErrorTimeout;
            case -7:
                return C4057R.string.httpErrorIO;
            case -6:
                return C4057R.string.httpErrorConnect;
            case -5:
                return C4057R.string.httpErrorProxyAuth;
            case -4:
                return C4057R.string.httpErrorAuth;
            case -3:
                return C4057R.string.httpErrorUnsupportedAuthScheme;
            case -2:
                return C4057R.string.httpErrorLookup;
            case -1:
                return C4057R.string.httpError;
            case 0:
                return C4057R.string.httpErrorOk;
            default:
                Log.m104w(LOGTAG, "Using generic message for unknown error code: " + errorCode);
                return C4057R.string.httpError;
        }
    }
}
