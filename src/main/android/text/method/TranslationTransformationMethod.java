package android.text.method;

import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.translation.TranslationResponseValue;
import android.view.translation.ViewTranslationRequest;
import android.view.translation.ViewTranslationResponse;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public class TranslationTransformationMethod implements TransformationMethod2 {
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    private static final String TAG = "TranslationTransformationMethod";
    private boolean mAllowLengthChanges;
    private TransformationMethod mOriginalTranslationMethod;
    private final ViewTranslationResponse mTranslationResponse;

    public TranslationTransformationMethod(ViewTranslationResponse response, TransformationMethod method) {
        this.mTranslationResponse = response;
        this.mOriginalTranslationMethod = method;
    }

    public TransformationMethod getOriginalTransformationMethod() {
        return this.mOriginalTranslationMethod;
    }

    public ViewTranslationResponse getViewTranslationResponse() {
        return this.mTranslationResponse;
    }

    @Override // android.text.method.TransformationMethod
    public CharSequence getTransformation(CharSequence source, View view) {
        CharSequence translatedText;
        if (!this.mAllowLengthChanges) {
            Log.m104w(TAG, "Caller did not enable length changes; not transforming to translated text");
            return source;
        }
        TranslationResponseValue value = this.mTranslationResponse.getValue(ViewTranslationRequest.ID_TEXT);
        if (value.getStatusCode() == 0) {
            translatedText = value.getText();
        } else {
            translatedText = "";
        }
        if (TextUtils.isEmpty(translatedText) || isWhitespace(translatedText.toString())) {
            return source;
        }
        return translatedText;
    }

    @Override // android.text.method.TransformationMethod
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
    }

    @Override // android.text.method.TransformationMethod2
    public void setLengthChangesAllowed(boolean allowLengthChanges) {
        this.mAllowLengthChanges = allowLengthChanges;
    }

    private boolean isWhitespace(String text) {
        return PATTERN_WHITESPACE.matcher(text.substring(0, text.length())).matches();
    }
}
