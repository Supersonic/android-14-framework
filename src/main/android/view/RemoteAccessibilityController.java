package android.view;

import android.graphics.Matrix;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.RemoteAccessibilityController;
import android.view.accessibility.IAccessibilityEmbeddedConnection;
import java.lang.ref.WeakReference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class RemoteAccessibilityController {
    private static final String TAG = "RemoteAccessibilityController";
    private RemoteAccessibilityEmbeddedConnection mConnectionWrapper;
    private int mHostId;
    private View mHostView;
    private Matrix mWindowMatrixForEmbeddedHierarchy = new Matrix();
    private final float[] mMatrixValues = new float[9];

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteAccessibilityController(View v) {
        this.mHostView = v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runOnUiThread(Runnable runnable) {
        Handler h = this.mHostView.getHandler();
        if (h != null && h.getLooper() != Looper.myLooper()) {
            h.post(runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void assosciateHierarchy(IAccessibilityEmbeddedConnection connection, IBinder leashToken, int hostId) {
        this.mHostId = hostId;
        try {
            setRemoteAccessibilityEmbeddedConnection(connection, connection.associateEmbeddedHierarchy(leashToken, hostId));
        } catch (RemoteException e) {
            Log.m112d(TAG, "Error in associateEmbeddedHierarchy " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disassosciateHierarchy() {
        setRemoteAccessibilityEmbeddedConnection(null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean alreadyAssociated(IAccessibilityEmbeddedConnection connection) {
        RemoteAccessibilityEmbeddedConnection remoteAccessibilityEmbeddedConnection = this.mConnectionWrapper;
        if (remoteAccessibilityEmbeddedConnection == null) {
            return false;
        }
        return remoteAccessibilityEmbeddedConnection.mConnection.equals(connection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean connected() {
        return this.mConnectionWrapper != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IBinder getLeashToken() {
        return this.mConnectionWrapper.getLeashToken();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class RemoteAccessibilityEmbeddedConnection implements IBinder.DeathRecipient {
        private final IAccessibilityEmbeddedConnection mConnection;
        private final WeakReference<RemoteAccessibilityController> mController;
        private final IBinder mLeashToken;

        RemoteAccessibilityEmbeddedConnection(RemoteAccessibilityController controller, IAccessibilityEmbeddedConnection connection, IBinder leashToken) {
            this.mController = new WeakReference<>(controller);
            this.mConnection = connection;
            this.mLeashToken = leashToken;
        }

        IAccessibilityEmbeddedConnection getConnection() {
            return this.mConnection;
        }

        IBinder getLeashToken() {
            return this.mLeashToken;
        }

        void linkToDeath() throws RemoteException {
            this.mConnection.asBinder().linkToDeath(this, 0);
        }

        void unlinkToDeath() {
            this.mConnection.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            unlinkToDeath();
            final RemoteAccessibilityController controller = this.mController.get();
            if (controller == null) {
                return;
            }
            controller.runOnUiThread(new Runnable() { // from class: android.view.RemoteAccessibilityController$RemoteAccessibilityEmbeddedConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteAccessibilityController.RemoteAccessibilityEmbeddedConnection.this.lambda$binderDied$0(controller);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$0(RemoteAccessibilityController controller) {
            if (controller.mConnectionWrapper == this) {
                controller.mConnectionWrapper = null;
            }
        }
    }

    private void setRemoteAccessibilityEmbeddedConnection(IAccessibilityEmbeddedConnection connection, IBinder leashToken) {
        try {
            RemoteAccessibilityEmbeddedConnection remoteAccessibilityEmbeddedConnection = this.mConnectionWrapper;
            if (remoteAccessibilityEmbeddedConnection != null) {
                remoteAccessibilityEmbeddedConnection.getConnection().disassociateEmbeddedHierarchy();
                this.mConnectionWrapper.unlinkToDeath();
                this.mConnectionWrapper = null;
            }
            if (connection != null && leashToken != null) {
                RemoteAccessibilityEmbeddedConnection remoteAccessibilityEmbeddedConnection2 = new RemoteAccessibilityEmbeddedConnection(this, connection, leashToken);
                this.mConnectionWrapper = remoteAccessibilityEmbeddedConnection2;
                remoteAccessibilityEmbeddedConnection2.linkToDeath();
            }
        } catch (RemoteException e) {
            Log.m112d(TAG, "Error while setRemoteEmbeddedConnection " + e);
        }
    }

    private RemoteAccessibilityEmbeddedConnection getRemoteAccessibilityEmbeddedConnection() {
        return this.mConnectionWrapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWindowMatrix(Matrix m, boolean force) {
        if (!force && m.equals(this.mWindowMatrixForEmbeddedHierarchy)) {
            return;
        }
        try {
            RemoteAccessibilityEmbeddedConnection wrapper = getRemoteAccessibilityEmbeddedConnection();
            if (wrapper == null) {
                return;
            }
            m.getValues(this.mMatrixValues);
            wrapper.getConnection().setWindowMatrix(this.mMatrixValues);
            this.mWindowMatrixForEmbeddedHierarchy.set(m);
        } catch (RemoteException e) {
            Log.m112d(TAG, "Error while setScreenMatrix " + e);
        }
    }
}
