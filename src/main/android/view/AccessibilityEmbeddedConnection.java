package android.view;

import android.graphics.Matrix;
import android.p008os.IBinder;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityEmbeddedConnection;
import java.lang.ref.WeakReference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public final class AccessibilityEmbeddedConnection extends IAccessibilityEmbeddedConnection.Stub {
    private final Matrix mTmpWindowMatrix = new Matrix();
    private final WeakReference<ViewRootImpl> mViewRootImpl;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityEmbeddedConnection(ViewRootImpl viewRootImpl) {
        this.mViewRootImpl = new WeakReference<>(viewRootImpl);
    }

    @Override // android.view.accessibility.IAccessibilityEmbeddedConnection
    public IBinder associateEmbeddedHierarchy(IBinder host, int hostViewId) {
        ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
        if (viewRootImpl != null) {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(viewRootImpl.mContext);
            viewRootImpl.mAttachInfo.mLeashedParentToken = host;
            viewRootImpl.mAttachInfo.mLeashedParentAccessibilityViewId = hostViewId;
            if (accessibilityManager.isEnabled()) {
                accessibilityManager.associateEmbeddedHierarchy(host, viewRootImpl.mLeashToken);
            }
            return viewRootImpl.mLeashToken;
        }
        return null;
    }

    @Override // android.view.accessibility.IAccessibilityEmbeddedConnection
    public void disassociateEmbeddedHierarchy() {
        ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
        if (viewRootImpl != null) {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(viewRootImpl.mContext);
            viewRootImpl.mAttachInfo.mLeashedParentToken = null;
            viewRootImpl.mAttachInfo.mLeashedParentAccessibilityViewId = -1;
            if (accessibilityManager.isEnabled()) {
                accessibilityManager.disassociateEmbeddedHierarchy(viewRootImpl.mLeashToken);
            }
        }
    }

    @Override // android.view.accessibility.IAccessibilityEmbeddedConnection
    public void setWindowMatrix(float[] matrixValues) {
        ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
        if (viewRootImpl != null) {
            this.mTmpWindowMatrix.setValues(matrixValues);
            if (viewRootImpl.mAttachInfo.mWindowMatrixInEmbeddedHierarchy == null) {
                viewRootImpl.mAttachInfo.mWindowMatrixInEmbeddedHierarchy = new Matrix();
            }
            viewRootImpl.mAttachInfo.mWindowMatrixInEmbeddedHierarchy.set(this.mTmpWindowMatrix);
        }
    }
}
