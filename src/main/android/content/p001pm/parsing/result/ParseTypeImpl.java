package android.content.p001pm.parsing.result;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.parsing.result.ParseInput;
import android.p008os.ServiceManager;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.util.CollectionUtils;
/* renamed from: android.content.pm.parsing.result.ParseTypeImpl */
/* loaded from: classes.dex */
public class ParseTypeImpl implements ParseInput, ParseResult<Object> {
    public static final boolean DEBUG_FILL_STACK_TRACE = false;
    public static final boolean DEBUG_LOG_ON_ERROR = false;
    public static final boolean DEBUG_THROW_ALL_ERRORS = false;
    private static final String TAG = "ParseTypeImpl";
    private ParseInput.Callback mCallback;
    private String mErrorMessage;
    private Exception mException;
    private String mPackageName;
    private Object mResult;
    private int mErrorCode = 1;
    private ArrayMap<Long, String> mDeferredErrors = null;
    private int mTargetSdkVersion = -1;

    public static ParseTypeImpl forParsingWithoutPlatformCompat() {
        return new ParseTypeImpl(new ParseInput.Callback() { // from class: android.content.pm.parsing.result.ParseTypeImpl$$ExternalSyntheticLambda1
            @Override // android.content.p001pm.parsing.result.ParseInput.Callback
            public final boolean isChangeEnabled(long j, String str, int i) {
                return ParseTypeImpl.lambda$forParsingWithoutPlatformCompat$0(j, str, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$forParsingWithoutPlatformCompat$0(long changeId, String packageName, int targetSdkVersion) {
        int gateSdkVersion = ParseInput.DeferredError.getTargetSdkForChange(changeId);
        return gateSdkVersion != -1 && targetSdkVersion > gateSdkVersion;
    }

    public static ParseTypeImpl forDefaultParsing() {
        final IPlatformCompat platformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService(Context.PLATFORM_COMPAT_SERVICE));
        return new ParseTypeImpl(new ParseInput.Callback() { // from class: android.content.pm.parsing.result.ParseTypeImpl$$ExternalSyntheticLambda0
            @Override // android.content.p001pm.parsing.result.ParseInput.Callback
            public final boolean isChangeEnabled(long j, String str, int i) {
                return ParseTypeImpl.lambda$forDefaultParsing$1(IPlatformCompat.this, j, str, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$forDefaultParsing$1(IPlatformCompat platformCompat, long changeId, String packageName, int targetSdkVersion) {
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.packageName = packageName;
        appInfo.targetSdkVersion = targetSdkVersion;
        try {
            return platformCompat.isChangeEnabled(changeId, appInfo);
        } catch (Exception e) {
            Slog.wtf(TAG, "IPlatformCompat query failed", e);
            return true;
        }
    }

    public ParseTypeImpl(ParseInput.Callback callback) {
        this.mCallback = callback;
    }

    public ParseInput reset() {
        this.mResult = null;
        this.mErrorCode = 1;
        this.mErrorMessage = null;
        this.mException = null;
        ArrayMap<Long, String> arrayMap = this.mDeferredErrors;
        if (arrayMap != null) {
            arrayMap.erase();
        }
        this.mTargetSdkVersion = -1;
        return this;
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> success(ResultType result) {
        if (this.mErrorCode != 1) {
            Slog.wtf(TAG, "Cannot set to success after set to error, was " + this.mErrorMessage, this.mException);
        }
        this.mResult = result;
        return this;
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public ParseResult<?> deferError(String parseError, long deferredError) {
        if (this.mTargetSdkVersion != -1) {
            ArrayMap<Long, String> arrayMap = this.mDeferredErrors;
            if (arrayMap != null && arrayMap.containsKey(Long.valueOf(deferredError))) {
                return success(null);
            }
            if (this.mCallback.isChangeEnabled(deferredError, this.mPackageName, this.mTargetSdkVersion)) {
                return error(parseError);
            }
            if (this.mDeferredErrors == null) {
                this.mDeferredErrors = new ArrayMap<>();
            }
            this.mDeferredErrors.put(Long.valueOf(deferredError), null);
            return success(null);
        }
        if (this.mDeferredErrors == null) {
            this.mDeferredErrors = new ArrayMap<>();
        }
        this.mDeferredErrors.putIfAbsent(Long.valueOf(deferredError), parseError);
        return success(null);
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public ParseResult<?> enableDeferredError(String packageName, int targetSdkVersion) {
        this.mPackageName = packageName;
        this.mTargetSdkVersion = targetSdkVersion;
        int size = CollectionUtils.size(this.mDeferredErrors);
        for (int index = size - 1; index >= 0; index--) {
            long changeId = this.mDeferredErrors.keyAt(index).longValue();
            String errorMessage = this.mDeferredErrors.valueAt(index);
            if (this.mCallback.isChangeEnabled(changeId, this.mPackageName, this.mTargetSdkVersion)) {
                return error(errorMessage);
            }
            this.mDeferredErrors.setValueAt(index, null);
        }
        return success(null);
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> skip(String parseError) {
        return error(PackageManager.INSTALL_PARSE_FAILED_SKIPPED, parseError);
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> error(int parseError) {
        return error(parseError, null);
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> error(String parseError) {
        return error(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, parseError);
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> error(int errorCode, String errorMessage) {
        return error(errorCode, errorMessage, null);
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> error(ParseResult<?> intentResult) {
        return error(intentResult.getErrorCode(), intentResult.getErrorMessage(), intentResult.getException());
    }

    @Override // android.content.p001pm.parsing.result.ParseInput
    public <ResultType> ParseResult<ResultType> error(int errorCode, String errorMessage, Exception exception) {
        this.mErrorCode = errorCode;
        this.mErrorMessage = errorMessage;
        this.mException = exception;
        return this;
    }

    @Override // android.content.p001pm.parsing.result.ParseResult
    public Object getResult() {
        return this.mResult;
    }

    @Override // android.content.p001pm.parsing.result.ParseResult
    public boolean isSuccess() {
        return this.mErrorCode == 1;
    }

    @Override // android.content.p001pm.parsing.result.ParseResult
    public boolean isError() {
        return !isSuccess();
    }

    @Override // android.content.p001pm.parsing.result.ParseResult
    public int getErrorCode() {
        return this.mErrorCode;
    }

    @Override // android.content.p001pm.parsing.result.ParseResult
    public String getErrorMessage() {
        return this.mErrorMessage;
    }

    @Override // android.content.p001pm.parsing.result.ParseResult
    public Exception getException() {
        return this.mException;
    }
}
