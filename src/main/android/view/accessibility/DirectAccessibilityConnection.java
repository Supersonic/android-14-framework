package android.view.accessibility;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.graphics.Region;
import android.p008os.Bundle;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.view.accessibility.IAccessibilityManager;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class DirectAccessibilityConnection extends IAccessibilityServiceConnection.Default {
    private static final int FETCH_FLAGS = 384;
    private static final Region INTERACTIVE_REGION = null;
    private final IAccessibilityInteractionConnection mAccessibilityInteractionConnection;
    private final AccessibilityManager mAccessibilityManager;
    private final int mMyProcessId = Process.myPid();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DirectAccessibilityConnection(IAccessibilityInteractionConnection accessibilityInteractionConnection, AccessibilityManager accessibilityManager) {
        this.mAccessibilityInteractionConnection = accessibilityInteractionConnection;
        this.mAccessibilityManager = accessibilityManager;
    }

    @Override // android.accessibilityservice.IAccessibilityServiceConnection.Default, android.accessibilityservice.IAccessibilityServiceConnection
    public String[] findAccessibilityNodeInfoByAccessibilityId(int accessibilityWindowId, long accessibilityNodeId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, long threadId, Bundle arguments) throws RemoteException {
        IAccessibilityManager.WindowTransformationSpec spec = this.mAccessibilityManager.getWindowTransformationSpec(accessibilityWindowId);
        this.mAccessibilityInteractionConnection.findAccessibilityNodeInfoByAccessibilityId(accessibilityNodeId, INTERACTIVE_REGION, interactionId, callback, 384, this.mMyProcessId, threadId, spec.magnificationSpec, spec.transformationMatrix, arguments);
        return new String[0];
    }

    @Override // android.accessibilityservice.IAccessibilityServiceConnection.Default, android.accessibilityservice.IAccessibilityServiceConnection
    public String[] findAccessibilityNodeInfosByText(int accessibilityWindowId, long accessibilityNodeId, String text, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
        IAccessibilityManager.WindowTransformationSpec spec = this.mAccessibilityManager.getWindowTransformationSpec(accessibilityWindowId);
        this.mAccessibilityInteractionConnection.findAccessibilityNodeInfosByText(accessibilityNodeId, text, INTERACTIVE_REGION, interactionId, callback, 384, this.mMyProcessId, threadId, spec.magnificationSpec, spec.transformationMatrix);
        return new String[0];
    }

    @Override // android.accessibilityservice.IAccessibilityServiceConnection.Default, android.accessibilityservice.IAccessibilityServiceConnection
    public String[] findAccessibilityNodeInfosByViewId(int accessibilityWindowId, long accessibilityNodeId, String viewId, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
        IAccessibilityManager.WindowTransformationSpec spec = this.mAccessibilityManager.getWindowTransformationSpec(accessibilityWindowId);
        this.mAccessibilityInteractionConnection.findAccessibilityNodeInfosByViewId(accessibilityNodeId, viewId, INTERACTIVE_REGION, interactionId, callback, 384, this.mMyProcessId, threadId, spec.magnificationSpec, spec.transformationMatrix);
        return new String[0];
    }

    @Override // android.accessibilityservice.IAccessibilityServiceConnection.Default, android.accessibilityservice.IAccessibilityServiceConnection
    public String[] findFocus(int accessibilityWindowId, long accessibilityNodeId, int focusType, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
        IAccessibilityManager.WindowTransformationSpec spec = this.mAccessibilityManager.getWindowTransformationSpec(accessibilityWindowId);
        this.mAccessibilityInteractionConnection.findFocus(accessibilityNodeId, focusType, INTERACTIVE_REGION, interactionId, callback, 384, this.mMyProcessId, threadId, spec.magnificationSpec, spec.transformationMatrix);
        return new String[0];
    }

    @Override // android.accessibilityservice.IAccessibilityServiceConnection.Default, android.accessibilityservice.IAccessibilityServiceConnection
    public String[] focusSearch(int accessibilityWindowId, long accessibilityNodeId, int direction, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
        IAccessibilityManager.WindowTransformationSpec spec = this.mAccessibilityManager.getWindowTransformationSpec(accessibilityWindowId);
        this.mAccessibilityInteractionConnection.focusSearch(accessibilityNodeId, direction, INTERACTIVE_REGION, interactionId, callback, 384, this.mMyProcessId, threadId, spec.magnificationSpec, spec.transformationMatrix);
        return new String[0];
    }

    @Override // android.accessibilityservice.IAccessibilityServiceConnection.Default, android.accessibilityservice.IAccessibilityServiceConnection
    public boolean performAccessibilityAction(int accessibilityWindowId, long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
        this.mAccessibilityInteractionConnection.performAccessibilityAction(accessibilityNodeId, action, arguments, interactionId, callback, 384, this.mMyProcessId, threadId);
        return true;
    }
}
