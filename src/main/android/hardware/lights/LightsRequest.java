package android.hardware.lights;

import android.annotation.SystemApi;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public final class LightsRequest {
    final List<Integer> mLightIds;
    final List<LightState> mLightStates;
    final Map<Light, LightState> mRequests;

    private LightsRequest(Map<Light, LightState> requests) {
        HashMap hashMap = new HashMap();
        this.mRequests = hashMap;
        this.mLightIds = new ArrayList();
        this.mLightStates = new ArrayList();
        hashMap.putAll(requests);
        List<Light> lights = new ArrayList<>(hashMap.keySet());
        for (int i = 0; i < lights.size(); i++) {
            Light light = lights.get(i);
            this.mLightIds.add(i, Integer.valueOf(light.getId()));
            this.mLightStates.add(i, this.mRequests.get(light));
        }
    }

    public List<Integer> getLights() {
        return this.mLightIds;
    }

    public List<LightState> getLightStates() {
        return this.mLightStates;
    }

    public Map<Light, LightState> getLightsAndStates() {
        return this.mRequests;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        final Map<Light, LightState> mChanges = new HashMap();

        public Builder addLight(Light light, LightState state) {
            Preconditions.checkNotNull(light);
            Preconditions.checkNotNull(state);
            this.mChanges.put(light, state);
            return this;
        }

        @SystemApi
        @Deprecated
        public Builder setLight(Light light, LightState state) {
            return addLight(light, state);
        }

        public Builder clearLight(Light light) {
            Preconditions.checkNotNull(light);
            this.mChanges.put(light, null);
            return this;
        }

        public LightsRequest build() {
            return new LightsRequest(this.mChanges);
        }
    }
}
