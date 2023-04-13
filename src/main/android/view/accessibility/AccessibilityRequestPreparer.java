package android.view.accessibility;

import android.content.Context;
import android.p008os.Bundle;
import android.p008os.Message;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
/* loaded from: classes4.dex */
public abstract class AccessibilityRequestPreparer {
    public static final int REQUEST_TYPE_EXTRA_DATA = 1;
    private final int mAccessibilityViewId;
    private final int mRequestTypes;
    private final WeakReference<View> mViewRef;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface RequestTypes {
    }

    public abstract void onPrepareExtraData(int i, String str, Bundle bundle, Message message);

    public AccessibilityRequestPreparer(View view, int requestTypes) {
        if (!view.isAttachedToWindow()) {
            throw new IllegalStateException("View must be attached to a window");
        }
        this.mViewRef = new WeakReference<>(view);
        this.mAccessibilityViewId = view.getAccessibilityViewId();
        this.mRequestTypes = requestTypes;
        view.addOnAttachStateChangeListener(new ViewAttachStateListener());
    }

    public View getView() {
        return this.mViewRef.get();
    }

    /* loaded from: classes4.dex */
    private class ViewAttachStateListener implements View.OnAttachStateChangeListener {
        private ViewAttachStateListener() {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            Context context = v.getContext();
            if (context != null) {
                ((AccessibilityManager) context.getSystemService(AccessibilityManager.class)).removeAccessibilityRequestPreparer(AccessibilityRequestPreparer.this);
            }
            v.removeOnAttachStateChangeListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAccessibilityViewId() {
        return this.mAccessibilityViewId;
    }
}
