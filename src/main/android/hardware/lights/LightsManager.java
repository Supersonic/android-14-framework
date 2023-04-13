package android.hardware.lights;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.Binder;
import android.p008os.IBinder;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
/* loaded from: classes2.dex */
public abstract class LightsManager {
    @SystemApi
    @Deprecated
    public static final int LIGHT_TYPE_MICROPHONE = 8;
    private static final String TAG = "LightsManager";
    private final Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface LightType {
    }

    public abstract LightState getLightState(Light light);

    public abstract List<Light> getLights();

    public abstract LightsSession openSession();

    public abstract LightsSession openSession(int i);

    public LightsManager(Context context) {
        this.mContext = (Context) Preconditions.checkNotNull(context);
    }

    /* loaded from: classes2.dex */
    public static abstract class LightsSession implements AutoCloseable {
        private final IBinder mToken = new Binder();

        @Override // java.lang.AutoCloseable
        public abstract void close();

        public abstract void requestLights(LightsRequest lightsRequest);

        public IBinder getToken() {
            return this.mToken;
        }
    }
}
