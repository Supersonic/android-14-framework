package android.text.method;

import android.content.Context;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;
/* loaded from: classes3.dex */
public class AllCapsTransformationMethod implements TransformationMethod2 {
    private static final String TAG = "AllCapsTransformationMethod";
    private boolean mEnabled;
    private Locale mLocale;

    public AllCapsTransformationMethod(Context context) {
        this.mLocale = context.getResources().getConfiguration().getLocales().get(0);
    }

    @Override // android.text.method.TransformationMethod
    public CharSequence getTransformation(CharSequence source, View view) {
        if (!this.mEnabled) {
            Log.m104w(TAG, "Caller did not enable length changes; not transforming text");
            return source;
        } else if (source == null) {
            return null;
        } else {
            Locale locale = null;
            if (view instanceof TextView) {
                locale = ((TextView) view).getTextLocale();
            }
            if (locale == null) {
                locale = this.mLocale;
            }
            boolean copySpans = source instanceof Spanned;
            return TextUtils.toUpperCase(locale, source, copySpans);
        }
    }

    @Override // android.text.method.TransformationMethod
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
    }

    @Override // android.text.method.TransformationMethod2
    public void setLengthChangesAllowed(boolean allowLengthChanges) {
        this.mEnabled = allowLengthChanges;
    }
}
