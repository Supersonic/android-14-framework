package android.accessibilityservice;

import android.content.p001pm.ParceledListSlice;
import android.graphics.Region;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.window.ScreenCapture;
import java.util.List;
/* loaded from: classes.dex */
public interface IAccessibilityServiceConnection extends IInterface {
    void attachAccessibilityOverlayToDisplay(int i, SurfaceControl surfaceControl) throws RemoteException;

    void attachAccessibilityOverlayToWindow(int i, SurfaceControl surfaceControl) throws RemoteException;

    void disableSelf() throws RemoteException;

    void dispatchGesture(int i, ParceledListSlice parceledListSlice, int i2) throws RemoteException;

    String[] findAccessibilityNodeInfoByAccessibilityId(int i, long j, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, long j2, Bundle bundle) throws RemoteException;

    String[] findAccessibilityNodeInfosByText(int i, long j, String str, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    String[] findAccessibilityNodeInfosByViewId(int i, long j, String str, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    String[] findFocus(int i, long j, int i2, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    String[] focusSearch(int i, long j, int i2, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    Region getCurrentMagnificationRegion(int i) throws RemoteException;

    List<AccessibilityServiceInfo> getInstalledAndEnabledServices() throws RemoteException;

    float getMagnificationCenterX(int i) throws RemoteException;

    float getMagnificationCenterY(int i) throws RemoteException;

    MagnificationConfig getMagnificationConfig(int i) throws RemoteException;

    Region getMagnificationRegion(int i) throws RemoteException;

    float getMagnificationScale(int i) throws RemoteException;

    IBinder getOverlayWindowToken(int i) throws RemoteException;

    AccessibilityServiceInfo getServiceInfo() throws RemoteException;

    int getSoftKeyboardShowMode() throws RemoteException;

    List<AccessibilityNodeInfo.AccessibilityAction> getSystemActions() throws RemoteException;

    AccessibilityWindowInfo getWindow(int i) throws RemoteException;

    int getWindowIdForLeashToken(IBinder iBinder) throws RemoteException;

    AccessibilityWindowInfo.WindowListSparseArray getWindows() throws RemoteException;

    boolean isAccessibilityButtonAvailable() throws RemoteException;

    boolean isFingerprintGestureDetectionAvailable() throws RemoteException;

    void logTrace(long j, String str, long j2, String str2, int i, long j3, int i2, Bundle bundle) throws RemoteException;

    void onDoubleTap(int i) throws RemoteException;

    void onDoubleTapAndHold(int i) throws RemoteException;

    boolean performAccessibilityAction(int i, long j, int i2, Bundle bundle, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    boolean performGlobalAction(int i) throws RemoteException;

    void requestDelegating(int i) throws RemoteException;

    void requestDragging(int i, int i2) throws RemoteException;

    void requestTouchExploration(int i) throws RemoteException;

    boolean resetCurrentMagnification(int i, boolean z) throws RemoteException;

    boolean resetMagnification(int i, boolean z) throws RemoteException;

    void sendGesture(int i, ParceledListSlice parceledListSlice) throws RemoteException;

    void setAnimationScale(float f) throws RemoteException;

    void setAttributionTag(String str) throws RemoteException;

    void setCacheEnabled(boolean z) throws RemoteException;

    void setFocusAppearance(int i, int i2) throws RemoteException;

    void setGestureDetectionPassthroughRegion(int i, Region region) throws RemoteException;

    int setInputMethodEnabled(String str, boolean z) throws RemoteException;

    void setInstalledAndEnabledServices(List<AccessibilityServiceInfo> list) throws RemoteException;

    void setMagnificationCallbackEnabled(int i, boolean z) throws RemoteException;

    boolean setMagnificationConfig(int i, MagnificationConfig magnificationConfig, boolean z) throws RemoteException;

    void setOnKeyEventResult(boolean z, int i) throws RemoteException;

    void setServiceDetectsGesturesEnabled(int i, boolean z) throws RemoteException;

    void setServiceInfo(AccessibilityServiceInfo accessibilityServiceInfo) throws RemoteException;

    void setSoftKeyboardCallbackEnabled(boolean z) throws RemoteException;

    boolean setSoftKeyboardShowMode(int i) throws RemoteException;

    void setTouchExplorationPassthroughRegion(int i, Region region) throws RemoteException;

    boolean switchToInputMethod(String str) throws RemoteException;

    void takeScreenshot(int i, RemoteCallback remoteCallback) throws RemoteException;

    void takeScreenshotOfWindow(int i, int i2, ScreenCapture.ScreenCaptureListener screenCaptureListener, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAccessibilityServiceConnection {
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setServiceInfo(AccessibilityServiceInfo info) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setAttributionTag(String attributionTag) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public String[] findAccessibilityNodeInfoByAccessibilityId(int accessibilityWindowId, long accessibilityNodeId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, long threadId, Bundle arguments) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public String[] findAccessibilityNodeInfosByText(int accessibilityWindowId, long accessibilityNodeId, String text, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public String[] findAccessibilityNodeInfosByViewId(int accessibilityWindowId, long accessibilityNodeId, String viewId, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public String[] findFocus(int accessibilityWindowId, long accessibilityNodeId, int focusType, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public String[] focusSearch(int accessibilityWindowId, long accessibilityNodeId, int direction, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean performAccessibilityAction(int accessibilityWindowId, long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public AccessibilityWindowInfo getWindow(int windowId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public AccessibilityWindowInfo.WindowListSparseArray getWindows() throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public AccessibilityServiceInfo getServiceInfo() throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean performGlobalAction(int action) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public List<AccessibilityNodeInfo.AccessibilityAction> getSystemActions() throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void disableSelf() throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setOnKeyEventResult(boolean handled, int sequence) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public MagnificationConfig getMagnificationConfig(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public float getMagnificationScale(int displayId) throws RemoteException {
            return 0.0f;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public float getMagnificationCenterX(int displayId) throws RemoteException {
            return 0.0f;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public float getMagnificationCenterY(int displayId) throws RemoteException {
            return 0.0f;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public Region getMagnificationRegion(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public Region getCurrentMagnificationRegion(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean resetMagnification(int displayId, boolean animate) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean resetCurrentMagnification(int displayId, boolean animate) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean setMagnificationConfig(int displayId, MagnificationConfig config, boolean animate) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setMagnificationCallbackEnabled(int displayId, boolean enabled) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean setSoftKeyboardShowMode(int showMode) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public int getSoftKeyboardShowMode() throws RemoteException {
            return 0;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setSoftKeyboardCallbackEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean switchToInputMethod(String imeId) throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public int setInputMethodEnabled(String imeId, boolean enabled) throws RemoteException {
            return 0;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean isAccessibilityButtonAvailable() throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void sendGesture(int sequence, ParceledListSlice gestureSteps) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void dispatchGesture(int sequence, ParceledListSlice gestureSteps, int displayId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean isFingerprintGestureDetectionAvailable() throws RemoteException {
            return false;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public IBinder getOverlayWindowToken(int displayid) throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public int getWindowIdForLeashToken(IBinder token) throws RemoteException {
            return 0;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void takeScreenshot(int displayId, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void takeScreenshotOfWindow(int accessibilityWindowId, int interactionId, ScreenCapture.ScreenCaptureListener listener, IAccessibilityInteractionConnectionCallback callback) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setGestureDetectionPassthroughRegion(int displayId, Region region) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setTouchExplorationPassthroughRegion(int displayId, Region region) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setFocusAppearance(int strokeWidth, int color) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setCacheEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void logTrace(long timestamp, String where, long loggingTypes, String callingParams, int processId, long threadId, int callingUid, Bundle serializedCallingStackInBundle) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setServiceDetectsGesturesEnabled(int displayId, boolean mode) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void requestTouchExploration(int displayId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void requestDragging(int displayId, int pointerId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void requestDelegating(int displayId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void onDoubleTap(int displayId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void onDoubleTapAndHold(int displayId) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setAnimationScale(float scale) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setInstalledAndEnabledServices(List<AccessibilityServiceInfo> infos) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public List<AccessibilityServiceInfo> getInstalledAndEnabledServices() throws RemoteException {
            return null;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void attachAccessibilityOverlayToDisplay(int displayId, SurfaceControl sc) throws RemoteException {
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void attachAccessibilityOverlayToWindow(int accessibilityWindowId, SurfaceControl sc) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAccessibilityServiceConnection {
        public static final String DESCRIPTOR = "android.accessibilityservice.IAccessibilityServiceConnection";
        static final int TRANSACTION_attachAccessibilityOverlayToDisplay = 53;
        static final int TRANSACTION_attachAccessibilityOverlayToWindow = 54;
        static final int TRANSACTION_disableSelf = 14;
        static final int TRANSACTION_dispatchGesture = 33;
        static final int TRANSACTION_findAccessibilityNodeInfoByAccessibilityId = 3;
        static final int TRANSACTION_findAccessibilityNodeInfosByText = 4;
        static final int TRANSACTION_findAccessibilityNodeInfosByViewId = 5;
        static final int TRANSACTION_findFocus = 6;
        static final int TRANSACTION_focusSearch = 7;
        static final int TRANSACTION_getCurrentMagnificationRegion = 21;
        static final int TRANSACTION_getInstalledAndEnabledServices = 52;
        static final int TRANSACTION_getMagnificationCenterX = 18;
        static final int TRANSACTION_getMagnificationCenterY = 19;
        static final int TRANSACTION_getMagnificationConfig = 16;
        static final int TRANSACTION_getMagnificationRegion = 20;
        static final int TRANSACTION_getMagnificationScale = 17;
        static final int TRANSACTION_getOverlayWindowToken = 35;
        static final int TRANSACTION_getServiceInfo = 11;
        static final int TRANSACTION_getSoftKeyboardShowMode = 27;
        static final int TRANSACTION_getSystemActions = 13;
        static final int TRANSACTION_getWindow = 9;
        static final int TRANSACTION_getWindowIdForLeashToken = 36;
        static final int TRANSACTION_getWindows = 10;
        static final int TRANSACTION_isAccessibilityButtonAvailable = 31;
        static final int TRANSACTION_isFingerprintGestureDetectionAvailable = 34;
        static final int TRANSACTION_logTrace = 43;
        static final int TRANSACTION_onDoubleTap = 48;
        static final int TRANSACTION_onDoubleTapAndHold = 49;
        static final int TRANSACTION_performAccessibilityAction = 8;
        static final int TRANSACTION_performGlobalAction = 12;
        static final int TRANSACTION_requestDelegating = 47;
        static final int TRANSACTION_requestDragging = 46;
        static final int TRANSACTION_requestTouchExploration = 45;
        static final int TRANSACTION_resetCurrentMagnification = 23;
        static final int TRANSACTION_resetMagnification = 22;
        static final int TRANSACTION_sendGesture = 32;
        static final int TRANSACTION_setAnimationScale = 50;
        static final int TRANSACTION_setAttributionTag = 2;
        static final int TRANSACTION_setCacheEnabled = 42;
        static final int TRANSACTION_setFocusAppearance = 41;
        static final int TRANSACTION_setGestureDetectionPassthroughRegion = 39;
        static final int TRANSACTION_setInputMethodEnabled = 30;
        static final int TRANSACTION_setInstalledAndEnabledServices = 51;
        static final int TRANSACTION_setMagnificationCallbackEnabled = 25;
        static final int TRANSACTION_setMagnificationConfig = 24;
        static final int TRANSACTION_setOnKeyEventResult = 15;
        static final int TRANSACTION_setServiceDetectsGesturesEnabled = 44;
        static final int TRANSACTION_setServiceInfo = 1;
        static final int TRANSACTION_setSoftKeyboardCallbackEnabled = 28;
        static final int TRANSACTION_setSoftKeyboardShowMode = 26;
        static final int TRANSACTION_setTouchExplorationPassthroughRegion = 40;
        static final int TRANSACTION_switchToInputMethod = 29;
        static final int TRANSACTION_takeScreenshot = 37;
        static final int TRANSACTION_takeScreenshotOfWindow = 38;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityServiceConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityServiceConnection)) {
                return (IAccessibilityServiceConnection) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "setServiceInfo";
                case 2:
                    return "setAttributionTag";
                case 3:
                    return "findAccessibilityNodeInfoByAccessibilityId";
                case 4:
                    return "findAccessibilityNodeInfosByText";
                case 5:
                    return "findAccessibilityNodeInfosByViewId";
                case 6:
                    return "findFocus";
                case 7:
                    return "focusSearch";
                case 8:
                    return "performAccessibilityAction";
                case 9:
                    return "getWindow";
                case 10:
                    return "getWindows";
                case 11:
                    return "getServiceInfo";
                case 12:
                    return "performGlobalAction";
                case 13:
                    return "getSystemActions";
                case 14:
                    return "disableSelf";
                case 15:
                    return "setOnKeyEventResult";
                case 16:
                    return "getMagnificationConfig";
                case 17:
                    return "getMagnificationScale";
                case 18:
                    return "getMagnificationCenterX";
                case 19:
                    return "getMagnificationCenterY";
                case 20:
                    return "getMagnificationRegion";
                case 21:
                    return "getCurrentMagnificationRegion";
                case 22:
                    return "resetMagnification";
                case 23:
                    return "resetCurrentMagnification";
                case 24:
                    return "setMagnificationConfig";
                case 25:
                    return "setMagnificationCallbackEnabled";
                case 26:
                    return "setSoftKeyboardShowMode";
                case 27:
                    return "getSoftKeyboardShowMode";
                case 28:
                    return "setSoftKeyboardCallbackEnabled";
                case 29:
                    return "switchToInputMethod";
                case 30:
                    return "setInputMethodEnabled";
                case 31:
                    return "isAccessibilityButtonAvailable";
                case 32:
                    return "sendGesture";
                case 33:
                    return "dispatchGesture";
                case 34:
                    return "isFingerprintGestureDetectionAvailable";
                case 35:
                    return "getOverlayWindowToken";
                case 36:
                    return "getWindowIdForLeashToken";
                case 37:
                    return "takeScreenshot";
                case 38:
                    return "takeScreenshotOfWindow";
                case 39:
                    return "setGestureDetectionPassthroughRegion";
                case 40:
                    return "setTouchExplorationPassthroughRegion";
                case 41:
                    return "setFocusAppearance";
                case 42:
                    return "setCacheEnabled";
                case 43:
                    return "logTrace";
                case 44:
                    return "setServiceDetectsGesturesEnabled";
                case 45:
                    return "requestTouchExploration";
                case 46:
                    return "requestDragging";
                case 47:
                    return "requestDelegating";
                case 48:
                    return "onDoubleTap";
                case 49:
                    return "onDoubleTapAndHold";
                case 50:
                    return "setAnimationScale";
                case 51:
                    return "setInstalledAndEnabledServices";
                case 52:
                    return "getInstalledAndEnabledServices";
                case 53:
                    return "attachAccessibilityOverlayToDisplay";
                case 54:
                    return "attachAccessibilityOverlayToWindow";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AccessibilityServiceInfo _arg0 = (AccessibilityServiceInfo) data.readTypedObject(AccessibilityServiceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setServiceInfo(_arg0);
                            reply.writeNoException();
                            return true;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            setAttributionTag(_arg02);
                            reply.writeNoException();
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            long _arg1 = data.readLong();
                            int _arg2 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg3 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg4 = data.readInt();
                            long _arg5 = data.readLong();
                            Bundle _arg6 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            String[] _result = findAccessibilityNodeInfoByAccessibilityId(_arg03, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeStringArray(_result);
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            long _arg12 = data.readLong();
                            String _arg22 = data.readString();
                            int _arg32 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg42 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg52 = data.readLong();
                            data.enforceNoDataAvail();
                            String[] _result2 = findAccessibilityNodeInfosByText(_arg04, _arg12, _arg22, _arg32, _arg42, _arg52);
                            reply.writeNoException();
                            reply.writeStringArray(_result2);
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            long _arg13 = data.readLong();
                            String _arg23 = data.readString();
                            int _arg33 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg43 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg53 = data.readLong();
                            data.enforceNoDataAvail();
                            String[] _result3 = findAccessibilityNodeInfosByViewId(_arg05, _arg13, _arg23, _arg33, _arg43, _arg53);
                            reply.writeNoException();
                            reply.writeStringArray(_result3);
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            long _arg14 = data.readLong();
                            int _arg24 = data.readInt();
                            int _arg34 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg44 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg54 = data.readLong();
                            data.enforceNoDataAvail();
                            String[] _result4 = findFocus(_arg06, _arg14, _arg24, _arg34, _arg44, _arg54);
                            reply.writeNoException();
                            reply.writeStringArray(_result4);
                            return true;
                        case 7:
                            int _arg07 = data.readInt();
                            long _arg15 = data.readLong();
                            int _arg25 = data.readInt();
                            int _arg35 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg45 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg55 = data.readLong();
                            data.enforceNoDataAvail();
                            String[] _result5 = focusSearch(_arg07, _arg15, _arg25, _arg35, _arg45, _arg55);
                            reply.writeNoException();
                            reply.writeStringArray(_result5);
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            long _arg16 = data.readLong();
                            int _arg26 = data.readInt();
                            Bundle _arg36 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg46 = data.readInt();
                            IAccessibilityInteractionConnectionCallback _arg56 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg62 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result6 = performAccessibilityAction(_arg08, _arg16, _arg26, _arg36, _arg46, _arg56, _arg62);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            AccessibilityWindowInfo _result7 = getWindow(_arg09);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            return true;
                        case 10:
                            AccessibilityWindowInfo.WindowListSparseArray _result8 = getWindows();
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            return true;
                        case 11:
                            AccessibilityServiceInfo _result9 = getServiceInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            return true;
                        case 12:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = performGlobalAction(_arg010);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            return true;
                        case 13:
                            List<AccessibilityNodeInfo.AccessibilityAction> _result11 = getSystemActions();
                            reply.writeNoException();
                            reply.writeTypedList(_result11, 1);
                            return true;
                        case 14:
                            disableSelf();
                            reply.writeNoException();
                            return true;
                        case 15:
                            boolean _arg011 = data.readBoolean();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            setOnKeyEventResult(_arg011, _arg17);
                            return true;
                        case 16:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            MagnificationConfig _result12 = getMagnificationConfig(_arg012);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            return true;
                        case 17:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result13 = getMagnificationScale(_arg013);
                            reply.writeNoException();
                            reply.writeFloat(_result13);
                            return true;
                        case 18:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result14 = getMagnificationCenterX(_arg014);
                            reply.writeNoException();
                            reply.writeFloat(_result14);
                            return true;
                        case 19:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result15 = getMagnificationCenterY(_arg015);
                            reply.writeNoException();
                            reply.writeFloat(_result15);
                            return true;
                        case 20:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            Region _result16 = getMagnificationRegion(_arg016);
                            reply.writeNoException();
                            reply.writeTypedObject(_result16, 1);
                            return true;
                        case 21:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            Region _result17 = getCurrentMagnificationRegion(_arg017);
                            reply.writeNoException();
                            reply.writeTypedObject(_result17, 1);
                            return true;
                        case 22:
                            int _arg018 = data.readInt();
                            boolean _arg18 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result18 = resetMagnification(_arg018, _arg18);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            return true;
                        case 23:
                            int _arg019 = data.readInt();
                            boolean _arg19 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result19 = resetCurrentMagnification(_arg019, _arg19);
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            return true;
                        case 24:
                            int _arg020 = data.readInt();
                            MagnificationConfig _arg110 = (MagnificationConfig) data.readTypedObject(MagnificationConfig.CREATOR);
                            boolean _arg27 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result20 = setMagnificationConfig(_arg020, _arg110, _arg27);
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            return true;
                        case 25:
                            int _arg021 = data.readInt();
                            boolean _arg111 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMagnificationCallbackEnabled(_arg021, _arg111);
                            reply.writeNoException();
                            return true;
                        case 26:
                            int _arg022 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result21 = setSoftKeyboardShowMode(_arg022);
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            return true;
                        case 27:
                            int _result22 = getSoftKeyboardShowMode();
                            reply.writeNoException();
                            reply.writeInt(_result22);
                            return true;
                        case 28:
                            boolean _arg023 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSoftKeyboardCallbackEnabled(_arg023);
                            reply.writeNoException();
                            return true;
                        case 29:
                            String _arg024 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result23 = switchToInputMethod(_arg024);
                            reply.writeNoException();
                            reply.writeBoolean(_result23);
                            return true;
                        case 30:
                            String _arg025 = data.readString();
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result24 = setInputMethodEnabled(_arg025, _arg112);
                            reply.writeNoException();
                            reply.writeInt(_result24);
                            return true;
                        case 31:
                            boolean _result25 = isAccessibilityButtonAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            return true;
                        case 32:
                            int _arg026 = data.readInt();
                            ParceledListSlice _arg113 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            data.enforceNoDataAvail();
                            sendGesture(_arg026, _arg113);
                            reply.writeNoException();
                            return true;
                        case 33:
                            int _arg027 = data.readInt();
                            ParceledListSlice _arg114 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchGesture(_arg027, _arg114, _arg28);
                            reply.writeNoException();
                            return true;
                        case 34:
                            boolean _result26 = isFingerprintGestureDetectionAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result26);
                            return true;
                        case 35:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            IBinder _result27 = getOverlayWindowToken(_arg028);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result27);
                            return true;
                        case 36:
                            IBinder _arg029 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result28 = getWindowIdForLeashToken(_arg029);
                            reply.writeNoException();
                            reply.writeInt(_result28);
                            return true;
                        case 37:
                            int _arg030 = data.readInt();
                            RemoteCallback _arg115 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            takeScreenshot(_arg030, _arg115);
                            reply.writeNoException();
                            return true;
                        case 38:
                            int _arg031 = data.readInt();
                            int _arg116 = data.readInt();
                            ScreenCapture.ScreenCaptureListener _arg29 = (ScreenCapture.ScreenCaptureListener) data.readTypedObject(ScreenCapture.ScreenCaptureListener.CREATOR);
                            IAccessibilityInteractionConnectionCallback _arg37 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            takeScreenshotOfWindow(_arg031, _arg116, _arg29, _arg37);
                            reply.writeNoException();
                            return true;
                        case 39:
                            int _arg032 = data.readInt();
                            Region _arg117 = (Region) data.readTypedObject(Region.CREATOR);
                            data.enforceNoDataAvail();
                            setGestureDetectionPassthroughRegion(_arg032, _arg117);
                            reply.writeNoException();
                            return true;
                        case 40:
                            int _arg033 = data.readInt();
                            Region _arg118 = (Region) data.readTypedObject(Region.CREATOR);
                            data.enforceNoDataAvail();
                            setTouchExplorationPassthroughRegion(_arg033, _arg118);
                            reply.writeNoException();
                            return true;
                        case 41:
                            int _arg034 = data.readInt();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            setFocusAppearance(_arg034, _arg119);
                            reply.writeNoException();
                            return true;
                        case 42:
                            boolean _arg035 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setCacheEnabled(_arg035);
                            reply.writeNoException();
                            return true;
                        case 43:
                            long _arg036 = data.readLong();
                            String _arg120 = data.readString();
                            long _arg210 = data.readLong();
                            String _arg38 = data.readString();
                            int _arg47 = data.readInt();
                            long _arg57 = data.readLong();
                            int _arg63 = data.readInt();
                            Bundle _arg7 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            logTrace(_arg036, _arg120, _arg210, _arg38, _arg47, _arg57, _arg63, _arg7);
                            return true;
                        case 44:
                            int _arg037 = data.readInt();
                            boolean _arg121 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setServiceDetectsGesturesEnabled(_arg037, _arg121);
                            reply.writeNoException();
                            return true;
                        case 45:
                            int _arg038 = data.readInt();
                            data.enforceNoDataAvail();
                            requestTouchExploration(_arg038);
                            reply.writeNoException();
                            return true;
                        case 46:
                            int _arg039 = data.readInt();
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            requestDragging(_arg039, _arg122);
                            reply.writeNoException();
                            return true;
                        case 47:
                            int _arg040 = data.readInt();
                            data.enforceNoDataAvail();
                            requestDelegating(_arg040);
                            reply.writeNoException();
                            return true;
                        case 48:
                            int _arg041 = data.readInt();
                            data.enforceNoDataAvail();
                            onDoubleTap(_arg041);
                            reply.writeNoException();
                            return true;
                        case 49:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            onDoubleTapAndHold(_arg042);
                            reply.writeNoException();
                            return true;
                        case 50:
                            float _arg043 = data.readFloat();
                            data.enforceNoDataAvail();
                            setAnimationScale(_arg043);
                            reply.writeNoException();
                            return true;
                        case 51:
                            List<AccessibilityServiceInfo> _arg044 = data.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setInstalledAndEnabledServices(_arg044);
                            reply.writeNoException();
                            return true;
                        case 52:
                            List<AccessibilityServiceInfo> _result29 = getInstalledAndEnabledServices();
                            reply.writeNoException();
                            reply.writeTypedList(_result29, 1);
                            return true;
                        case 53:
                            int _arg045 = data.readInt();
                            SurfaceControl _arg123 = (SurfaceControl) data.readTypedObject(SurfaceControl.CREATOR);
                            data.enforceNoDataAvail();
                            attachAccessibilityOverlayToDisplay(_arg045, _arg123);
                            reply.writeNoException();
                            return true;
                        case 54:
                            int _arg046 = data.readInt();
                            SurfaceControl _arg124 = (SurfaceControl) data.readTypedObject(SurfaceControl.CREATOR);
                            data.enforceNoDataAvail();
                            attachAccessibilityOverlayToWindow(_arg046, _arg124);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAccessibilityServiceConnection {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setServiceInfo(AccessibilityServiceInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setAttributionTag(String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public String[] findAccessibilityNodeInfoByAccessibilityId(int accessibilityWindowId, long accessibilityNodeId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, long threadId, Bundle arguments) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(flags);
                    _data.writeLong(threadId);
                    _data.writeTypedObject(arguments, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public String[] findAccessibilityNodeInfosByText(int accessibilityWindowId, long accessibilityNodeId, String text, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeString(text);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(threadId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public String[] findAccessibilityNodeInfosByViewId(int accessibilityWindowId, long accessibilityNodeId, String viewId, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeString(viewId);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(threadId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public String[] findFocus(int accessibilityWindowId, long accessibilityNodeId, int focusType, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(focusType);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(threadId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public String[] focusSearch(int accessibilityWindowId, long accessibilityNodeId, int direction, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(direction);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(threadId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean performAccessibilityAction(int accessibilityWindowId, long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(action);
                    _data.writeTypedObject(arguments, 0);
                    _data.writeInt(interactionId);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(threadId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public AccessibilityWindowInfo getWindow(int windowId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(windowId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    AccessibilityWindowInfo _result = (AccessibilityWindowInfo) _reply.readTypedObject(AccessibilityWindowInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public AccessibilityWindowInfo.WindowListSparseArray getWindows() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    AccessibilityWindowInfo.WindowListSparseArray _result = (AccessibilityWindowInfo.WindowListSparseArray) _reply.readTypedObject(AccessibilityWindowInfo.WindowListSparseArray.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public AccessibilityServiceInfo getServiceInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    AccessibilityServiceInfo _result = (AccessibilityServiceInfo) _reply.readTypedObject(AccessibilityServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean performGlobalAction(int action) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(action);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public List<AccessibilityNodeInfo.AccessibilityAction> getSystemActions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    List<AccessibilityNodeInfo.AccessibilityAction> _result = _reply.createTypedArrayList(AccessibilityNodeInfo.AccessibilityAction.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void disableSelf() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setOnKeyEventResult(boolean handled, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(handled);
                    _data.writeInt(sequence);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public MagnificationConfig getMagnificationConfig(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    MagnificationConfig _result = (MagnificationConfig) _reply.readTypedObject(MagnificationConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public float getMagnificationScale(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public float getMagnificationCenterX(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public float getMagnificationCenterY(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public Region getMagnificationRegion(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    Region _result = (Region) _reply.readTypedObject(Region.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public Region getCurrentMagnificationRegion(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    Region _result = (Region) _reply.readTypedObject(Region.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean resetMagnification(int displayId, boolean animate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(animate);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean resetCurrentMagnification(int displayId, boolean animate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(animate);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean setMagnificationConfig(int displayId, MagnificationConfig config, boolean animate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(config, 0);
                    _data.writeBoolean(animate);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setMagnificationCallbackEnabled(int displayId, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean setSoftKeyboardShowMode(int showMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showMode);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public int getSoftKeyboardShowMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setSoftKeyboardCallbackEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean switchToInputMethod(String imeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(imeId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public int setInputMethodEnabled(String imeId, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(imeId);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean isAccessibilityButtonAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void sendGesture(int sequence, ParceledListSlice gestureSteps) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sequence);
                    _data.writeTypedObject(gestureSteps, 0);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void dispatchGesture(int sequence, ParceledListSlice gestureSteps, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sequence);
                    _data.writeTypedObject(gestureSteps, 0);
                    _data.writeInt(displayId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean isFingerprintGestureDetectionAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public IBinder getOverlayWindowToken(int displayid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayid);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public int getWindowIdForLeashToken(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void takeScreenshot(int displayId, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void takeScreenshotOfWindow(int accessibilityWindowId, int interactionId, ScreenCapture.ScreenCaptureListener listener, IAccessibilityInteractionConnectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeInt(interactionId);
                    _data.writeTypedObject(listener, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setGestureDetectionPassthroughRegion(int displayId, Region region) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(region, 0);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setTouchExplorationPassthroughRegion(int displayId, Region region) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(region, 0);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setFocusAppearance(int strokeWidth, int color) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strokeWidth);
                    _data.writeInt(color);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setCacheEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void logTrace(long timestamp, String where, long loggingTypes, String callingParams, int processId, long threadId, int callingUid, Bundle serializedCallingStackInBundle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timestamp);
                    try {
                        _data.writeString(where);
                    } catch (Throwable th) {
                        th = th;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeLong(loggingTypes);
                    try {
                        _data.writeString(callingParams);
                        try {
                            _data.writeInt(processId);
                            try {
                                _data.writeLong(threadId);
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(callingUid);
                        try {
                            _data.writeTypedObject(serializedCallingStackInBundle, 0);
                            try {
                                this.mRemote.transact(43, _data, null, 1);
                                _data.recycle();
                            } catch (Throwable th6) {
                                th = th6;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th9) {
                    th = th9;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setServiceDetectsGesturesEnabled(int displayId, boolean mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(mode);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void requestTouchExploration(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void requestDragging(int displayId, int pointerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(pointerId);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void requestDelegating(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void onDoubleTap(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void onDoubleTapAndHold(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setAnimationScale(float scale) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(scale);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setInstalledAndEnabledServices(List<AccessibilityServiceInfo> infos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(infos, 0);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public List<AccessibilityServiceInfo> getInstalledAndEnabledServices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    List<AccessibilityServiceInfo> _result = _reply.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void attachAccessibilityOverlayToDisplay(int displayId, SurfaceControl sc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(sc, 0);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void attachAccessibilityOverlayToWindow(int accessibilityWindowId, SurfaceControl sc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeTypedObject(sc, 0);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 53;
        }
    }
}
