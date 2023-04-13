package com.android.internal.widget.helper;

import android.graphics.Canvas;
import android.view.View;
import com.android.internal.C4057R;
import com.android.internal.widget.RecyclerView;
/* loaded from: classes5.dex */
class ItemTouchUIUtilImpl implements ItemTouchUIUtil {
    @Override // com.android.internal.widget.helper.ItemTouchUIUtil
    public void onDraw(Canvas c, RecyclerView recyclerView, View view, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (isCurrentlyActive) {
            Object originalElevation = view.getTag(C4057R.C4059id.item_touch_helper_previous_elevation);
            if (originalElevation == null) {
                Object originalElevation2 = Float.valueOf(view.getElevation());
                float newElevation = findMaxElevation(recyclerView, view) + 1.0f;
                view.setElevation(newElevation);
                view.setTag(C4057R.C4059id.item_touch_helper_previous_elevation, originalElevation2);
            }
        }
        view.setTranslationX(dX);
        view.setTranslationY(dY);
    }

    private float findMaxElevation(RecyclerView recyclerView, View itemView) {
        int childCount = recyclerView.getChildCount();
        float max = 0.0f;
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            if (child != itemView) {
                float elevation = child.getElevation();
                if (elevation > max) {
                    max = elevation;
                }
            }
        }
        return max;
    }

    @Override // com.android.internal.widget.helper.ItemTouchUIUtil
    public void clearView(View view) {
        Object tag = view.getTag(C4057R.C4059id.item_touch_helper_previous_elevation);
        if (tag != null && (tag instanceof Float)) {
            view.setElevation(((Float) tag).floatValue());
        }
        view.setTag(C4057R.C4059id.item_touch_helper_previous_elevation, null);
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
    }

    @Override // com.android.internal.widget.helper.ItemTouchUIUtil
    public void onSelected(View view) {
    }

    @Override // com.android.internal.widget.helper.ItemTouchUIUtil
    public void onDrawOver(Canvas c, RecyclerView recyclerView, View view, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }
}
