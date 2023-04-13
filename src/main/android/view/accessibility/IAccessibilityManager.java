package android.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.app.RemoteAction;
import android.p008os.BadParcelableException;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.view.IWindow;
import android.view.InputEvent;
import android.view.MagnificationSpec;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityManagerClient;
import android.view.accessibility.IWindowMagnificationConnection;
import java.util.List;
/* loaded from: classes4.dex */
public interface IAccessibilityManager extends IInterface {
    int addAccessibilityInteractionConnection(IWindow iWindow, IBinder iBinder, IAccessibilityInteractionConnection iAccessibilityInteractionConnection, String str, int i) throws RemoteException;

    long addClient(IAccessibilityManagerClient iAccessibilityManagerClient, int i) throws RemoteException;

    void associateEmbeddedHierarchy(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    void disassociateEmbeddedHierarchy(IBinder iBinder) throws RemoteException;

    List<String> getAccessibilityShortcutTargets(int i) throws RemoteException;

    int getAccessibilityWindowId(IBinder iBinder) throws RemoteException;

    List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i, int i2) throws RemoteException;

    int getFocusColor() throws RemoteException;

    int getFocusStrokeWidth() throws RemoteException;

    List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int i) throws RemoteException;

    long getRecommendedTimeoutMillis() throws RemoteException;

    IBinder getWindowToken(int i, int i2) throws RemoteException;

    WindowTransformationSpec getWindowTransformationSpec(int i) throws RemoteException;

    void injectInputEventToInputFilter(InputEvent inputEvent) throws RemoteException;

    void interrupt(int i) throws RemoteException;

    boolean isAudioDescriptionByDefaultEnabled() throws RemoteException;

    boolean isSystemAudioCaptioningUiEnabled(int i) throws RemoteException;

    void notifyAccessibilityButtonClicked(int i, String str) throws RemoteException;

    void notifyAccessibilityButtonVisibilityChanged(boolean z) throws RemoteException;

    void performAccessibilityShortcut(String str) throws RemoteException;

    boolean registerProxyForDisplay(IAccessibilityServiceClient iAccessibilityServiceClient, int i) throws RemoteException;

    void registerSystemAction(RemoteAction remoteAction, int i) throws RemoteException;

    void registerUiTestAutomationService(IBinder iBinder, IAccessibilityServiceClient iAccessibilityServiceClient, AccessibilityServiceInfo accessibilityServiceInfo, int i) throws RemoteException;

    void removeAccessibilityInteractionConnection(IWindow iWindow) throws RemoteException;

    boolean removeClient(IAccessibilityManagerClient iAccessibilityManagerClient, int i) throws RemoteException;

    void sendAccessibilityEvent(AccessibilityEvent accessibilityEvent, int i) throws RemoteException;

    boolean sendFingerprintGesture(int i) throws RemoteException;

    void setAccessibilityWindowAttributes(int i, int i2, int i3, AccessibilityWindowAttributes accessibilityWindowAttributes) throws RemoteException;

    void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection iAccessibilityInteractionConnection) throws RemoteException;

    void setSystemAudioCaptioningEnabled(boolean z, int i) throws RemoteException;

    void setSystemAudioCaptioningUiEnabled(boolean z, int i) throws RemoteException;

    void setWindowMagnificationConnection(IWindowMagnificationConnection iWindowMagnificationConnection) throws RemoteException;

    boolean startFlashNotificationEvent(String str, int i, String str2) throws RemoteException;

    boolean startFlashNotificationSequence(String str, int i, IBinder iBinder) throws RemoteException;

    boolean stopFlashNotificationSequence(String str) throws RemoteException;

    boolean unregisterProxyForDisplay(int i) throws RemoteException;

    void unregisterSystemAction(int i) throws RemoteException;

    void unregisterUiTestAutomationService(IAccessibilityServiceClient iAccessibilityServiceClient) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAccessibilityManager {
        @Override // android.view.accessibility.IAccessibilityManager
        public void interrupt(int userId) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void sendAccessibilityEvent(AccessibilityEvent uiEvent, int userId) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public long addClient(IAccessibilityManagerClient client, int userId) throws RemoteException {
            return 0L;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean removeClient(IAccessibilityManagerClient client, int userId) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int userId) throws RemoteException {
            return null;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackType, int userId) throws RemoteException {
            return null;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public int addAccessibilityInteractionConnection(IWindow windowToken, IBinder leashToken, IAccessibilityInteractionConnection connection, String packageName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void removeAccessibilityInteractionConnection(IWindow windowToken) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection connection) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void registerUiTestAutomationService(IBinder owner, IAccessibilityServiceClient client, AccessibilityServiceInfo info, int flags) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void unregisterUiTestAutomationService(IAccessibilityServiceClient client) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public IBinder getWindowToken(int windowId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void notifyAccessibilityButtonClicked(int displayId, String targetName) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void notifyAccessibilityButtonVisibilityChanged(boolean available) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void performAccessibilityShortcut(String targetName) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public List<String> getAccessibilityShortcutTargets(int shortcutType) throws RemoteException {
            return null;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean sendFingerprintGesture(int gestureKeyCode) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public int getAccessibilityWindowId(IBinder windowToken) throws RemoteException {
            return 0;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public long getRecommendedTimeoutMillis() throws RemoteException {
            return 0L;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void registerSystemAction(RemoteAction action, int actionId) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void unregisterSystemAction(int actionId) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void setWindowMagnificationConnection(IWindowMagnificationConnection connection) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void associateEmbeddedHierarchy(IBinder host, IBinder embedded) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void disassociateEmbeddedHierarchy(IBinder token) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public int getFocusStrokeWidth() throws RemoteException {
            return 0;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public int getFocusColor() throws RemoteException {
            return 0;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean isAudioDescriptionByDefaultEnabled() throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void setSystemAudioCaptioningEnabled(boolean isEnabled, int userId) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean isSystemAudioCaptioningUiEnabled(int userId) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void setSystemAudioCaptioningUiEnabled(boolean isEnabled, int userId) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void setAccessibilityWindowAttributes(int displayId, int windowId, int userId, AccessibilityWindowAttributes attributes) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean registerProxyForDisplay(IAccessibilityServiceClient proxy, int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean unregisterProxyForDisplay(int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public void injectInputEventToInputFilter(InputEvent event) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean startFlashNotificationSequence(String opPkg, int reason, IBinder token) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean stopFlashNotificationSequence(String opPkg) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public boolean startFlashNotificationEvent(String opPkg, int reason, String reasonPkg) throws RemoteException {
            return false;
        }

        @Override // android.view.accessibility.IAccessibilityManager
        public WindowTransformationSpec getWindowTransformationSpec(int windowId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAccessibilityManager {
        public static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityManager";
        static final int TRANSACTION_addAccessibilityInteractionConnection = 7;
        static final int TRANSACTION_addClient = 3;
        static final int TRANSACTION_associateEmbeddedHierarchy = 23;
        static final int TRANSACTION_disassociateEmbeddedHierarchy = 24;
        static final int TRANSACTION_getAccessibilityShortcutTargets = 16;
        static final int TRANSACTION_getAccessibilityWindowId = 18;
        static final int TRANSACTION_getEnabledAccessibilityServiceList = 6;
        static final int TRANSACTION_getFocusColor = 26;
        static final int TRANSACTION_getFocusStrokeWidth = 25;
        static final int TRANSACTION_getInstalledAccessibilityServiceList = 5;
        static final int TRANSACTION_getRecommendedTimeoutMillis = 19;
        static final int TRANSACTION_getWindowToken = 12;
        static final int TRANSACTION_getWindowTransformationSpec = 38;
        static final int TRANSACTION_injectInputEventToInputFilter = 34;
        static final int TRANSACTION_interrupt = 1;
        static final int TRANSACTION_isAudioDescriptionByDefaultEnabled = 27;
        static final int TRANSACTION_isSystemAudioCaptioningUiEnabled = 29;
        static final int TRANSACTION_notifyAccessibilityButtonClicked = 13;
        static final int TRANSACTION_notifyAccessibilityButtonVisibilityChanged = 14;
        static final int TRANSACTION_performAccessibilityShortcut = 15;
        static final int TRANSACTION_registerProxyForDisplay = 32;
        static final int TRANSACTION_registerSystemAction = 20;
        static final int TRANSACTION_registerUiTestAutomationService = 10;
        static final int TRANSACTION_removeAccessibilityInteractionConnection = 8;
        static final int TRANSACTION_removeClient = 4;
        static final int TRANSACTION_sendAccessibilityEvent = 2;
        static final int TRANSACTION_sendFingerprintGesture = 17;
        static final int TRANSACTION_setAccessibilityWindowAttributes = 31;
        static final int TRANSACTION_setPictureInPictureActionReplacingConnection = 9;
        static final int TRANSACTION_setSystemAudioCaptioningEnabled = 28;
        static final int TRANSACTION_setSystemAudioCaptioningUiEnabled = 30;
        static final int TRANSACTION_setWindowMagnificationConnection = 22;
        static final int TRANSACTION_startFlashNotificationEvent = 37;
        static final int TRANSACTION_startFlashNotificationSequence = 35;
        static final int TRANSACTION_stopFlashNotificationSequence = 36;
        static final int TRANSACTION_unregisterProxyForDisplay = 33;
        static final int TRANSACTION_unregisterSystemAction = 21;
        static final int TRANSACTION_unregisterUiTestAutomationService = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityManager)) {
                return (IAccessibilityManager) iin;
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
                    return "interrupt";
                case 2:
                    return "sendAccessibilityEvent";
                case 3:
                    return "addClient";
                case 4:
                    return "removeClient";
                case 5:
                    return "getInstalledAccessibilityServiceList";
                case 6:
                    return "getEnabledAccessibilityServiceList";
                case 7:
                    return "addAccessibilityInteractionConnection";
                case 8:
                    return "removeAccessibilityInteractionConnection";
                case 9:
                    return "setPictureInPictureActionReplacingConnection";
                case 10:
                    return "registerUiTestAutomationService";
                case 11:
                    return "unregisterUiTestAutomationService";
                case 12:
                    return "getWindowToken";
                case 13:
                    return "notifyAccessibilityButtonClicked";
                case 14:
                    return "notifyAccessibilityButtonVisibilityChanged";
                case 15:
                    return "performAccessibilityShortcut";
                case 16:
                    return "getAccessibilityShortcutTargets";
                case 17:
                    return "sendFingerprintGesture";
                case 18:
                    return "getAccessibilityWindowId";
                case 19:
                    return "getRecommendedTimeoutMillis";
                case 20:
                    return "registerSystemAction";
                case 21:
                    return "unregisterSystemAction";
                case 22:
                    return "setWindowMagnificationConnection";
                case 23:
                    return "associateEmbeddedHierarchy";
                case 24:
                    return "disassociateEmbeddedHierarchy";
                case 25:
                    return "getFocusStrokeWidth";
                case 26:
                    return "getFocusColor";
                case 27:
                    return "isAudioDescriptionByDefaultEnabled";
                case 28:
                    return "setSystemAudioCaptioningEnabled";
                case 29:
                    return "isSystemAudioCaptioningUiEnabled";
                case 30:
                    return "setSystemAudioCaptioningUiEnabled";
                case 31:
                    return "setAccessibilityWindowAttributes";
                case 32:
                    return "registerProxyForDisplay";
                case 33:
                    return "unregisterProxyForDisplay";
                case 34:
                    return "injectInputEventToInputFilter";
                case 35:
                    return "startFlashNotificationSequence";
                case 36:
                    return "stopFlashNotificationSequence";
                case 37:
                    return "startFlashNotificationEvent";
                case 38:
                    return "getWindowTransformationSpec";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            interrupt(_arg0);
                            break;
                        case 2:
                            AccessibilityEvent _arg02 = (AccessibilityEvent) data.readTypedObject(AccessibilityEvent.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            sendAccessibilityEvent(_arg02, _arg1);
                            break;
                        case 3:
                            IAccessibilityManagerClient _arg03 = IAccessibilityManagerClient.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result = addClient(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeLong(_result);
                            break;
                        case 4:
                            IAccessibilityManagerClient _arg04 = IAccessibilityManagerClient.Stub.asInterface(data.readStrongBinder());
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = removeClient(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            List<AccessibilityServiceInfo> _result3 = getInstalledAccessibilityServiceList(_arg05);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            List<AccessibilityServiceInfo> _result4 = getEnabledAccessibilityServiceList(_arg06, _arg14);
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        case 7:
                            IWindow _arg07 = IWindow.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg15 = data.readStrongBinder();
                            IAccessibilityInteractionConnection _arg2 = IAccessibilityInteractionConnection.Stub.asInterface(data.readStrongBinder());
                            String _arg3 = data.readString();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = addAccessibilityInteractionConnection(_arg07, _arg15, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 8:
                            IWindow _arg08 = IWindow.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeAccessibilityInteractionConnection(_arg08);
                            reply.writeNoException();
                            break;
                        case 9:
                            IBinder _arg09 = data.readStrongBinder();
                            IAccessibilityInteractionConnection _arg010 = IAccessibilityInteractionConnection.Stub.asInterface(_arg09);
                            data.enforceNoDataAvail();
                            setPictureInPictureActionReplacingConnection(_arg010);
                            reply.writeNoException();
                            break;
                        case 10:
                            IBinder _arg011 = data.readStrongBinder();
                            IAccessibilityServiceClient _arg16 = IAccessibilityServiceClient.Stub.asInterface(data.readStrongBinder());
                            AccessibilityServiceInfo _arg22 = (AccessibilityServiceInfo) data.readTypedObject(AccessibilityServiceInfo.CREATOR);
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            registerUiTestAutomationService(_arg011, _arg16, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 11:
                            IAccessibilityServiceClient _arg012 = IAccessibilityServiceClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterUiTestAutomationService(_arg012);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg013 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            IBinder _result6 = getWindowToken(_arg013, _arg17);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result6);
                            break;
                        case 13:
                            int _arg014 = data.readInt();
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            notifyAccessibilityButtonClicked(_arg014, _arg18);
                            reply.writeNoException();
                            break;
                        case 14:
                            boolean _arg015 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyAccessibilityButtonVisibilityChanged(_arg015);
                            reply.writeNoException();
                            break;
                        case 15:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            performAccessibilityShortcut(_arg016);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result7 = getAccessibilityShortcutTargets(_arg017);
                            reply.writeNoException();
                            reply.writeStringList(_result7);
                            break;
                        case 17:
                            int _arg018 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = sendFingerprintGesture(_arg018);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 18:
                            IBinder _arg019 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result9 = getAccessibilityWindowId(_arg019);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 19:
                            long _result10 = getRecommendedTimeoutMillis();
                            reply.writeNoException();
                            reply.writeLong(_result10);
                            break;
                        case 20:
                            RemoteAction _arg020 = (RemoteAction) data.readTypedObject(RemoteAction.CREATOR);
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            registerSystemAction(_arg020, _arg19);
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterSystemAction(_arg021);
                            break;
                        case 22:
                            IBinder _arg022 = data.readStrongBinder();
                            IWindowMagnificationConnection _arg023 = IWindowMagnificationConnection.Stub.asInterface(_arg022);
                            data.enforceNoDataAvail();
                            setWindowMagnificationConnection(_arg023);
                            break;
                        case 23:
                            IBinder _arg024 = data.readStrongBinder();
                            IBinder _arg110 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            associateEmbeddedHierarchy(_arg024, _arg110);
                            reply.writeNoException();
                            break;
                        case 24:
                            IBinder _arg025 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            disassociateEmbeddedHierarchy(_arg025);
                            reply.writeNoException();
                            break;
                        case 25:
                            int _result11 = getFocusStrokeWidth();
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 26:
                            int _result12 = getFocusColor();
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 27:
                            boolean _result13 = isAudioDescriptionByDefaultEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 28:
                            boolean _arg026 = data.readBoolean();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            setSystemAudioCaptioningEnabled(_arg026, _arg111);
                            reply.writeNoException();
                            break;
                        case 29:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result14 = isSystemAudioCaptioningUiEnabled(_arg027);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 30:
                            boolean _arg028 = data.readBoolean();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            setSystemAudioCaptioningUiEnabled(_arg028, _arg112);
                            reply.writeNoException();
                            break;
                        case 31:
                            int _arg029 = data.readInt();
                            int _arg113 = data.readInt();
                            int _arg23 = data.readInt();
                            AccessibilityWindowAttributes _arg33 = (AccessibilityWindowAttributes) data.readTypedObject(AccessibilityWindowAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            setAccessibilityWindowAttributes(_arg029, _arg113, _arg23, _arg33);
                            break;
                        case 32:
                            IAccessibilityServiceClient _arg030 = IAccessibilityServiceClient.Stub.asInterface(data.readStrongBinder());
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = registerProxyForDisplay(_arg030, _arg114);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 33:
                            int _arg031 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result16 = unregisterProxyForDisplay(_arg031);
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 34:
                            InputEvent _arg032 = (InputEvent) data.readTypedObject(InputEvent.CREATOR);
                            data.enforceNoDataAvail();
                            injectInputEventToInputFilter(_arg032);
                            reply.writeNoException();
                            break;
                        case 35:
                            String _arg033 = data.readString();
                            int _arg115 = data.readInt();
                            IBinder _arg24 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            boolean _result17 = startFlashNotificationSequence(_arg033, _arg115, _arg24);
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 36:
                            String _arg034 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result18 = stopFlashNotificationSequence(_arg034);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        case 37:
                            String _arg035 = data.readString();
                            int _arg116 = data.readInt();
                            String _arg25 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result19 = startFlashNotificationEvent(_arg035, _arg116, _arg25);
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            break;
                        case 38:
                            int _arg036 = data.readInt();
                            data.enforceNoDataAvail();
                            WindowTransformationSpec _result20 = getWindowTransformationSpec(_arg036);
                            reply.writeNoException();
                            reply.writeTypedObject(_result20, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAccessibilityManager {
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

            @Override // android.view.accessibility.IAccessibilityManager
            public void interrupt(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void sendAccessibilityEvent(AccessibilityEvent uiEvent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uiEvent, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public long addClient(IAccessibilityManagerClient client, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean removeClient(IAccessibilityManagerClient client, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<AccessibilityServiceInfo> _result = _reply.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(feedbackType);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    List<AccessibilityServiceInfo> _result = _reply.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public int addAccessibilityInteractionConnection(IWindow windowToken, IBinder leashToken, IAccessibilityInteractionConnection connection, String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(windowToken);
                    _data.writeStrongBinder(leashToken);
                    _data.writeStrongInterface(connection);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void removeAccessibilityInteractionConnection(IWindow windowToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(windowToken);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection connection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(connection);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void registerUiTestAutomationService(IBinder owner, IAccessibilityServiceClient client, AccessibilityServiceInfo info, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(owner);
                    _data.writeStrongInterface(client);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(flags);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void unregisterUiTestAutomationService(IAccessibilityServiceClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public IBinder getWindowToken(int windowId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(windowId);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void notifyAccessibilityButtonClicked(int displayId, String targetName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeString(targetName);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void notifyAccessibilityButtonVisibilityChanged(boolean available) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(available);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void performAccessibilityShortcut(String targetName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetName);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public List<String> getAccessibilityShortcutTargets(int shortcutType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(shortcutType);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean sendFingerprintGesture(int gestureKeyCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(gestureKeyCode);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public int getAccessibilityWindowId(IBinder windowToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public long getRecommendedTimeoutMillis() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void registerSystemAction(RemoteAction action, int actionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(action, 0);
                    _data.writeInt(actionId);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void unregisterSystemAction(int actionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actionId);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void setWindowMagnificationConnection(IWindowMagnificationConnection connection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(connection);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void associateEmbeddedHierarchy(IBinder host, IBinder embedded) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(host);
                    _data.writeStrongBinder(embedded);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void disassociateEmbeddedHierarchy(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public int getFocusStrokeWidth() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public int getFocusColor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean isAudioDescriptionByDefaultEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void setSystemAudioCaptioningEnabled(boolean isEnabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isEnabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean isSystemAudioCaptioningUiEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void setSystemAudioCaptioningUiEnabled(boolean isEnabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isEnabled);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void setAccessibilityWindowAttributes(int displayId, int windowId, int userId, AccessibilityWindowAttributes attributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(windowId);
                    _data.writeInt(userId);
                    _data.writeTypedObject(attributes, 0);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean registerProxyForDisplay(IAccessibilityServiceClient proxy, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(proxy);
                    _data.writeInt(displayId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean unregisterProxyForDisplay(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void injectInputEventToInputFilter(InputEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean startFlashNotificationSequence(String opPkg, int reason, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(opPkg);
                    _data.writeInt(reason);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean stopFlashNotificationSequence(String opPkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(opPkg);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean startFlashNotificationEvent(String opPkg, int reason, String reasonPkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(opPkg);
                    _data.writeInt(reason);
                    _data.writeString(reasonPkg);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public WindowTransformationSpec getWindowTransformationSpec(int windowId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(windowId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    WindowTransformationSpec _result = (WindowTransformationSpec) _reply.readTypedObject(WindowTransformationSpec.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 37;
        }
    }

    /* loaded from: classes4.dex */
    public static class WindowTransformationSpec implements Parcelable {
        public static final Parcelable.Creator<WindowTransformationSpec> CREATOR = new Parcelable.Creator<WindowTransformationSpec>() { // from class: android.view.accessibility.IAccessibilityManager.WindowTransformationSpec.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public WindowTransformationSpec createFromParcel(Parcel _aidl_source) {
                WindowTransformationSpec _aidl_out = new WindowTransformationSpec();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public WindowTransformationSpec[] newArray(int _aidl_size) {
                return new WindowTransformationSpec[_aidl_size];
            }
        };
        public MagnificationSpec magnificationSpec;
        public float[] transformationMatrix;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeFloatArray(this.transformationMatrix);
            _aidl_parcel.writeTypedObject(this.magnificationSpec, _aidl_flag);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.transformationMatrix = _aidl_parcel.createFloatArray();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.magnificationSpec = (MagnificationSpec) _aidl_parcel.readTypedObject(MagnificationSpec.CREATOR);
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            int _mask = 0 | describeContents(this.magnificationSpec);
            return _mask;
        }

        private int describeContents(Object _v) {
            if (_v == null || !(_v instanceof Parcelable)) {
                return 0;
            }
            return ((Parcelable) _v).describeContents();
        }
    }
}
