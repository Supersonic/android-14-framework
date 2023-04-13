package android.app;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.app.IActivityManager;
import android.app.IUiAutomationConnection;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.permission.IPermissionManager;
import android.util.Log;
import android.util.Pair;
import android.view.IWindowManager;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.WindowAnimationFrameStats;
import android.view.WindowContentFrameStats;
import android.view.accessibility.IAccessibilityManager;
import android.window.ScreenCapture;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public final class UiAutomationConnection extends IUiAutomationConnection.Stub {
    private static final int INITIAL_FROZEN_ROTATION_UNSPECIFIED = -1;
    private static final String TAG = "UiAutomationConnection";
    private IAccessibilityServiceClient mClient;
    private boolean mIsShutdown;
    private int mOwningUid;
    private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
    private final IAccessibilityManager mAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
    private final IPermissionManager mPermissionManager = IPermissionManager.Stub.asInterface(ServiceManager.getService("permissionmgr"));
    private final IActivityManager mActivityManager = IActivityManager.Stub.asInterface(ServiceManager.getService("activity"));
    private final Object mLock = new Object();
    private final Binder mToken = new Binder();
    private int mInitialFrozenRotation = -1;

    @Override // android.app.IUiAutomationConnection
    public void connect(IAccessibilityServiceClient client, int flags) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null!");
        }
        synchronized (this.mLock) {
            throwIfShutdownLocked();
            if (isConnectedLocked()) {
                throw new IllegalStateException("Already connected.");
            }
            this.mOwningUid = Binder.getCallingUid();
            registerUiTestAutomationServiceLocked(client, flags);
            storeRotationStateLocked();
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void disconnect() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            if (!isConnectedLocked()) {
                throw new IllegalStateException("Already disconnected.");
            }
            this.mOwningUid = -1;
            unregisterUiTestAutomationServiceLocked();
            restoreRotationStateLocked();
        }
    }

    @Override // android.app.IUiAutomationConnection
    public boolean injectInputEvent(InputEvent event, boolean sync, boolean waitForAnimations) {
        boolean syncTransactionsBefore;
        boolean syncTransactionsAfter;
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            syncTransactionsBefore = keyEvent.getAction() == 0;
            syncTransactionsAfter = keyEvent.getAction() == 1;
        } else {
            MotionEvent motionEvent = (MotionEvent) event;
            syncTransactionsBefore = motionEvent.getAction() == 0 || motionEvent.isFromSource(8194);
            syncTransactionsAfter = motionEvent.getAction() == 1;
        }
        long identity = Binder.clearCallingIdentity();
        if (syncTransactionsBefore) {
            try {
                this.mWindowManager.syncInputTransactions(waitForAnimations);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return false;
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
        boolean result = InputManager.getInstance().injectInputEvent(event, sync ? 2 : 0);
        if (syncTransactionsAfter) {
            this.mWindowManager.syncInputTransactions(waitForAnimations);
        }
        return result;
    }

    @Override // android.app.IUiAutomationConnection
    public void injectInputEventToInputFilter(InputEvent event) throws RemoteException {
        this.mAccessibilityManager.injectInputEventToInputFilter(event);
    }

    @Override // android.app.IUiAutomationConnection
    public void syncInputTransactions(boolean waitForAnimations) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        try {
            this.mWindowManager.syncInputTransactions(waitForAnimations);
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.IUiAutomationConnection
    public boolean setRotation(int rotation) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            if (rotation == -2) {
                this.mWindowManager.thawRotation();
            } else {
                this.mWindowManager.freezeRotation(rotation);
            }
            Binder.restoreCallingIdentity(identity);
            return true;
        } catch (RemoteException e) {
            Binder.restoreCallingIdentity(identity);
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    @Override // android.app.IUiAutomationConnection
    public Bitmap takeScreenshot(Rect crop) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        Bitmap bitmap = null;
        try {
            ScreenCapture.CaptureArgs captureArgs = new ScreenCapture.CaptureArgs.Builder().setSourceCrop(crop).build();
            Pair<ScreenCapture.ScreenCaptureListener, ScreenCapture.ScreenshotSync> syncScreenCapture = ScreenCapture.createSyncCaptureListener();
            this.mWindowManager.captureDisplay(0, captureArgs, syncScreenCapture.first);
            ScreenCapture.ScreenshotHardwareBuffer screenshotBuffer = syncScreenCapture.second.get();
            if (screenshotBuffer != null) {
                bitmap = screenshotBuffer.asBitmap();
            }
            return bitmap;
        } catch (RemoteException re) {
            re.rethrowAsRuntimeException();
            return null;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public Bitmap takeSurfaceControlScreenshot(SurfaceControl surfaceControl) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            ScreenCapture.ScreenshotHardwareBuffer captureBuffer = ScreenCapture.captureLayers(new ScreenCapture.LayerCaptureArgs.Builder(surfaceControl).setChildrenOnly(false).build());
            if (captureBuffer == null) {
                return null;
            }
            return captureBuffer.asBitmap();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public boolean clearWindowContentFrameStats(int windowId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        int callingUserId = UserHandle.getCallingUserId();
        long identity = Binder.clearCallingIdentity();
        try {
            IBinder token = this.mAccessibilityManager.getWindowToken(windowId, callingUserId);
            if (token != null) {
                return this.mWindowManager.clearWindowContentFrameStats(token);
            }
            Binder.restoreCallingIdentity(identity);
            return false;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public WindowContentFrameStats getWindowContentFrameStats(int windowId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        int callingUserId = UserHandle.getCallingUserId();
        long identity = Binder.clearCallingIdentity();
        try {
            IBinder token = this.mAccessibilityManager.getWindowToken(windowId, callingUserId);
            if (token != null) {
                return this.mWindowManager.getWindowContentFrameStats(token);
            }
            Binder.restoreCallingIdentity(identity);
            return null;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void clearWindowAnimationFrameStats() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            SurfaceControl.clearAnimationFrameStats();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public WindowAnimationFrameStats getWindowAnimationFrameStats() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            WindowAnimationFrameStats stats = new WindowAnimationFrameStats();
            SurfaceControl.getAnimationFrameStats(stats);
            return stats;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void grantRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPermissionManager.grantRuntimePermission(packageName, permission, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void revokeRuntimePermission(String packageName, String permission, int userId) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPermissionManager.revokeRuntimePermission(packageName, permission, userId, null);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void adoptShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mActivityManager.startDelegateShellPermissionIdentity(uid, permissions);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void dropShellPermissionIdentity() throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mActivityManager.stopDelegateShellPermissionIdentity();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public List<String> getAdoptedShellPermissions() throws RemoteException {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            return this.mActivityManager.getDelegatedShellPermissions();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* loaded from: classes.dex */
    public class Repeater implements Runnable {
        private final InputStream readFrom;
        private final OutputStream writeTo;

        public Repeater(InputStream readFrom, OutputStream writeTo) {
            this.readFrom = readFrom;
            this.writeTo = writeTo;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                byte[] buffer = new byte[8192];
                while (true) {
                    int readByteCount = this.readFrom.read(buffer);
                    if (readByteCount < 0) {
                        break;
                    }
                    this.writeTo.write(buffer, 0, readByteCount);
                    this.writeTo.flush();
                }
            } catch (IOException e) {
            } catch (Throwable th) {
                IoUtils.closeQuietly(this.readFrom);
                IoUtils.closeQuietly(this.writeTo);
                throw th;
            }
            IoUtils.closeQuietly(this.readFrom);
            IoUtils.closeQuietly(this.writeTo);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void executeShellCommand(String command, ParcelFileDescriptor sink, ParcelFileDescriptor source) throws RemoteException {
        executeShellCommandWithStderr(command, sink, source, null);
    }

    @Override // android.app.IUiAutomationConnection
    public void executeShellCommandWithStderr(String command, final ParcelFileDescriptor sink, final ParcelFileDescriptor source, final ParcelFileDescriptor stderrSink) throws RemoteException {
        Thread readFromProcess;
        Thread writeToProcess;
        Thread readStderrFromProcess;
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        try {
            final Process process = Runtime.getRuntime().exec(command);
            if (sink != null) {
                InputStream sink_in = process.getInputStream();
                OutputStream sink_out = new FileOutputStream(sink.getFileDescriptor());
                Thread readFromProcess2 = new Thread(new Repeater(sink_in, sink_out));
                readFromProcess2.start();
                readFromProcess = readFromProcess2;
            } else {
                readFromProcess = null;
            }
            if (source != null) {
                OutputStream source_out = process.getOutputStream();
                InputStream source_in = new FileInputStream(source.getFileDescriptor());
                Thread writeToProcess2 = new Thread(new Repeater(source_in, source_out));
                writeToProcess2.start();
                writeToProcess = writeToProcess2;
            } else {
                writeToProcess = null;
            }
            if (stderrSink != null) {
                InputStream sink_in2 = process.getErrorStream();
                OutputStream sink_out2 = new FileOutputStream(stderrSink.getFileDescriptor());
                Thread readStderrFromProcess2 = new Thread(new Repeater(sink_in2, sink_out2));
                readStderrFromProcess2.start();
                readStderrFromProcess = readStderrFromProcess2;
            } else {
                readStderrFromProcess = null;
            }
            final Thread thread = writeToProcess;
            final Thread thread2 = readFromProcess;
            final Thread thread3 = readStderrFromProcess;
            Thread cleanup = new Thread(new Runnable() { // from class: android.app.UiAutomationConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Thread thread4 = thread;
                        if (thread4 != null) {
                            thread4.join();
                        }
                        Thread thread5 = thread2;
                        if (thread5 != null) {
                            thread5.join();
                        }
                        Thread thread6 = thread3;
                        if (thread6 != null) {
                            thread6.join();
                        }
                    } catch (InterruptedException e) {
                        Log.m110e(UiAutomationConnection.TAG, "At least one of the threads was interrupted");
                    }
                    IoUtils.closeQuietly(sink);
                    IoUtils.closeQuietly(source);
                    IoUtils.closeQuietly(stderrSink);
                    process.destroy();
                }
            });
            cleanup.start();
        } catch (IOException exc) {
            throw new RuntimeException("Error running shell command '" + command + "'", exc);
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void shutdown() {
        synchronized (this.mLock) {
            if (isConnectedLocked()) {
                throwIfCalledByNotTrustedUidLocked();
            }
            throwIfShutdownLocked();
            this.mIsShutdown = true;
            if (isConnectedLocked()) {
                disconnect();
            }
        }
    }

    private void registerUiTestAutomationServiceLocked(IAccessibilityServiceClient client, int flags) {
        IAccessibilityManager manager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = -1;
        info.feedbackType = 16;
        info.flags |= 65554;
        info.setCapabilities(11);
        if ((flags & 4) == 0) {
            info.setAccessibilityTool(true);
        }
        try {
            manager.registerUiTestAutomationService(this.mToken, client, info, flags);
            this.mClient = client;
        } catch (RemoteException re) {
            throw new IllegalStateException("Error while registering UiTestAutomationService.", re);
        }
    }

    private void unregisterUiTestAutomationServiceLocked() {
        IAccessibilityManager manager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
        try {
            manager.unregisterUiTestAutomationService(this.mClient);
            this.mClient = null;
        } catch (RemoteException re) {
            throw new IllegalStateException("Error while unregistering UiTestAutomationService", re);
        }
    }

    private void storeRotationStateLocked() {
        try {
            if (this.mWindowManager.isRotationFrozen()) {
                this.mInitialFrozenRotation = this.mWindowManager.getDefaultDisplayRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private void restoreRotationStateLocked() {
        try {
            int i = this.mInitialFrozenRotation;
            if (i != -1) {
                this.mWindowManager.freezeRotation(i);
            } else {
                this.mWindowManager.thawRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private boolean isConnectedLocked() {
        return this.mClient != null;
    }

    private void throwIfShutdownLocked() {
        if (this.mIsShutdown) {
            throw new IllegalStateException("Connection shutdown!");
        }
    }

    private void throwIfNotConnectedLocked() {
        if (!isConnectedLocked()) {
            throw new IllegalStateException("Not connected!");
        }
    }

    private void throwIfCalledByNotTrustedUidLocked() {
        int callingUid = Binder.getCallingUid();
        int i = this.mOwningUid;
        if (callingUid != i && i != 1000 && callingUid != 0) {
            throw new SecurityException("Calling from not trusted UID!");
        }
    }
}
