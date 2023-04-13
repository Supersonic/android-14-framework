package android.hardware.lights;

import android.content.Context;
import android.hardware.lights.ILightsManager;
import android.hardware.lights.LightsManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.CloseGuard;
import com.android.internal.util.Preconditions;
import java.lang.ref.Reference;
import java.util.List;
/* loaded from: classes2.dex */
public final class SystemLightsManager extends LightsManager {
    private static final String TAG = "LightsManager";
    private final ILightsManager mService;

    public SystemLightsManager(Context context) throws ServiceManager.ServiceNotFoundException {
        this(context, ILightsManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.LIGHTS_SERVICE)));
    }

    public SystemLightsManager(Context context, ILightsManager service) {
        super(context);
        this.mService = (ILightsManager) Preconditions.checkNotNull(service);
    }

    @Override // android.hardware.lights.LightsManager
    public List<Light> getLights() {
        try {
            return this.mService.getLights();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.hardware.lights.LightsManager
    public LightState getLightState(Light light) {
        Preconditions.checkNotNull(light);
        try {
            return this.mService.getLightState(light.getId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.hardware.lights.LightsManager
    public LightsManager.LightsSession openSession() {
        try {
            LightsManager.LightsSession session = new SystemLightsSession();
            this.mService.openSession(session.getToken(), 0);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.hardware.lights.LightsManager
    public LightsManager.LightsSession openSession(int priority) {
        try {
            LightsManager.LightsSession session = new SystemLightsSession();
            this.mService.openSession(session.getToken(), priority);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes2.dex */
    public final class SystemLightsSession extends LightsManager.LightsSession implements AutoCloseable {
        private final CloseGuard mCloseGuard;
        private boolean mClosed;

        private SystemLightsSession() {
            CloseGuard closeGuard = new CloseGuard();
            this.mCloseGuard = closeGuard;
            this.mClosed = false;
            closeGuard.open("SystemLightsSession.close");
        }

        @Override // android.hardware.lights.LightsManager.LightsSession
        public void requestLights(LightsRequest request) {
            Preconditions.checkNotNull(request);
            if (!this.mClosed) {
                try {
                    List<Integer> idList = request.getLights();
                    List<LightState> stateList = request.getLightStates();
                    int[] ids = new int[idList.size()];
                    for (int i = 0; i < idList.size(); i++) {
                        ids[i] = idList.get(i).intValue();
                    }
                    int i2 = stateList.size();
                    LightState[] states = new LightState[i2];
                    for (int i3 = 0; i3 < stateList.size(); i3++) {
                        states[i3] = stateList.get(i3);
                    }
                    SystemLightsManager.this.mService.setLightStates(getToken(), ids, states);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }

        @Override // android.hardware.lights.LightsManager.LightsSession, java.lang.AutoCloseable
        public void close() {
            if (!this.mClosed) {
                try {
                    SystemLightsManager.this.mService.closeSession(getToken());
                    this.mClosed = true;
                    this.mCloseGuard.close();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            Reference.reachabilityFence(this);
        }

        protected void finalize() throws Throwable {
            try {
                this.mCloseGuard.warnIfOpen();
                close();
            } finally {
                super.finalize();
            }
        }
    }
}
