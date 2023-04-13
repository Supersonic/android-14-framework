package com.android.internal.widget.helper;

import android.graphics.Canvas;
import android.view.View;
import com.android.internal.widget.RecyclerView;
/* loaded from: classes5.dex */
public interface ItemTouchUIUtil {
    void clearView(View view);

    void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z);

    void onDrawOver(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z);

    void onSelected(View view);
}
