package android.view;

import android.content.Context;
import android.util.Log;
/* loaded from: classes4.dex */
public abstract class ActionProvider {
    private static final String TAG = "ActionProvider";
    private SubUiVisibilityListener mSubUiVisibilityListener;
    private VisibilityListener mVisibilityListener;

    /* loaded from: classes4.dex */
    public interface SubUiVisibilityListener {
        void onSubUiVisibilityChanged(boolean z);
    }

    /* loaded from: classes4.dex */
    public interface VisibilityListener {
        void onActionProviderVisibilityChanged(boolean z);
    }

    @Deprecated
    public abstract View onCreateActionView();

    public ActionProvider(Context context) {
    }

    public View onCreateActionView(MenuItem forItem) {
        return onCreateActionView();
    }

    public boolean overridesItemVisibility() {
        return false;
    }

    public boolean isVisible() {
        return true;
    }

    public void refreshVisibility() {
        if (this.mVisibilityListener != null && overridesItemVisibility()) {
            this.mVisibilityListener.onActionProviderVisibilityChanged(isVisible());
        }
    }

    public boolean onPerformDefaultAction() {
        return false;
    }

    public boolean hasSubMenu() {
        return false;
    }

    public void onPrepareSubMenu(SubMenu subMenu) {
    }

    public void subUiVisibilityChanged(boolean isVisible) {
        SubUiVisibilityListener subUiVisibilityListener = this.mSubUiVisibilityListener;
        if (subUiVisibilityListener != null) {
            subUiVisibilityListener.onSubUiVisibilityChanged(isVisible);
        }
    }

    public void setSubUiVisibilityListener(SubUiVisibilityListener listener) {
        this.mSubUiVisibilityListener = listener;
    }

    public void setVisibilityListener(VisibilityListener listener) {
        if (this.mVisibilityListener != null) {
            Log.m104w(TAG, "setVisibilityListener: Setting a new ActionProvider.VisibilityListener when one is already set. Are you reusing this " + getClass().getSimpleName() + " instance while it is still in use somewhere else?");
        }
        this.mVisibilityListener = listener;
    }

    public void reset() {
        this.mVisibilityListener = null;
        this.mSubUiVisibilityListener = null;
    }
}
