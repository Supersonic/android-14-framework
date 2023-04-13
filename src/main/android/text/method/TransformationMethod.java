package android.text.method;

import android.graphics.Rect;
import android.view.View;
/* loaded from: classes3.dex */
public interface TransformationMethod {
    CharSequence getTransformation(CharSequence charSequence, View view);

    void onFocusChanged(View view, CharSequence charSequence, boolean z, int i, Rect rect);
}
