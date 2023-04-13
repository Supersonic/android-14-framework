package com.android.server.sensorprivacy;

import android.os.Handler;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.sensorprivacy.SensorPrivacyStateController;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SensorPrivacyStateControllerImpl extends SensorPrivacyStateController {
    public static SensorPrivacyStateControllerImpl sInstance;
    public SensorPrivacyStateController.SensorPrivacyListener mListener;
    public Handler mListenerHandler;
    public PersistedState mPersistedState = PersistedState.fromFile("sensor_privacy_impl.xml");

    public static SensorPrivacyStateController getInstance() {
        if (sInstance == null) {
            sInstance = new SensorPrivacyStateControllerImpl();
        }
        return sInstance;
    }

    public SensorPrivacyStateControllerImpl() {
        persistAll();
    }

    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController
    public SensorState getStateLocked(int i, int i2, int i3) {
        SensorState state = this.mPersistedState.getState(i, i2, i3);
        if (state != null) {
            return new SensorState(state);
        }
        return getDefaultSensorState();
    }

    public static SensorState getDefaultSensorState() {
        return new SensorState(false);
    }

    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController
    public void setStateLocked(int i, int i2, int i3, boolean z, Handler handler, SensorPrivacyStateController.SetStateResultCallback setStateResultCallback) {
        SensorState state = this.mPersistedState.getState(i, i2, i3);
        if (state == null) {
            if (!z) {
                SensorPrivacyStateController.sendSetStateCallback(handler, setStateResultCallback, false);
                return;
            } else if (z) {
                SensorState sensorState = new SensorState(true);
                this.mPersistedState.setState(i, i2, i3, sensorState);
                notifyStateChangeLocked(i, i2, i3, sensorState);
                SensorPrivacyStateController.sendSetStateCallback(handler, setStateResultCallback, true);
                return;
            }
        }
        if (state.setEnabled(z)) {
            notifyStateChangeLocked(i, i2, i3, state);
            SensorPrivacyStateController.sendSetStateCallback(handler, setStateResultCallback, true);
            return;
        }
        SensorPrivacyStateController.sendSetStateCallback(handler, setStateResultCallback, false);
    }

    public final void notifyStateChangeLocked(int i, int i2, int i3, SensorState sensorState) {
        Handler handler = this.mListenerHandler;
        if (handler != null && this.mListener != null) {
            handler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyStateControllerImpl$$ExternalSyntheticLambda1
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((SensorPrivacyStateController.SensorPrivacyListener) obj).onSensorPrivacyChanged(((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (SensorState) obj5);
                }
            }, this.mListener, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), new SensorState(sensorState)));
        }
        schedulePersistLocked();
    }

    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController
    public void setSensorPrivacyListenerLocked(Handler handler, SensorPrivacyStateController.SensorPrivacyListener sensorPrivacyListener) {
        Objects.requireNonNull(handler);
        Objects.requireNonNull(sensorPrivacyListener);
        if (this.mListener != null) {
            throw new IllegalStateException("Listener is already set");
        }
        this.mListener = sensorPrivacyListener;
        this.mListenerHandler = handler;
    }

    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController
    public void schedulePersistLocked() {
        this.mPersistedState.schedulePersist();
    }

    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController
    public void forEachStateLocked(final SensorPrivacyStateController.SensorPrivacyStateConsumer sensorPrivacyStateConsumer) {
        PersistedState persistedState = this.mPersistedState;
        Objects.requireNonNull(sensorPrivacyStateConsumer);
        persistedState.forEachKnownState(new QuadConsumer() { // from class: com.android.server.sensorprivacy.SensorPrivacyStateControllerImpl$$ExternalSyntheticLambda0
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                SensorPrivacyStateController.SensorPrivacyStateConsumer.this.accept(((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (SensorState) obj4);
            }
        });
    }

    @Override // com.android.server.sensorprivacy.SensorPrivacyStateController
    public void dumpLocked(DualDumpOutputStream dualDumpOutputStream) {
        this.mPersistedState.dump(dualDumpOutputStream);
    }
}
