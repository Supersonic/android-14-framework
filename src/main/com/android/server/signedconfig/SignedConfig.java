package com.android.server.signedconfig;

import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class SignedConfig {
    public final List<PerSdkConfig> perSdkConfig;
    public final int version;

    /* loaded from: classes2.dex */
    public static class PerSdkConfig {
        public final int maxSdk;
        public final int minSdk;
        public final Map<String, String> values;

        public PerSdkConfig(int i, int i2, Map<String, String> map) {
            this.minSdk = i;
            this.maxSdk = i2;
            this.values = Collections.unmodifiableMap(map);
        }
    }

    public SignedConfig(int i, List<PerSdkConfig> list) {
        this.version = i;
        this.perSdkConfig = Collections.unmodifiableList(list);
    }

    public PerSdkConfig getMatchingConfig(int i) {
        for (PerSdkConfig perSdkConfig : this.perSdkConfig) {
            if (perSdkConfig.minSdk <= i && i <= perSdkConfig.maxSdk) {
                return perSdkConfig;
            }
        }
        return null;
    }

    public static SignedConfig parse(String str, Set<String> set, Map<String, Map<String, String>> map) throws InvalidConfigException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            int i = jSONObject.getInt("version");
            JSONArray jSONArray = jSONObject.getJSONArray("config");
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                arrayList.add(parsePerSdkConfig(jSONArray.getJSONObject(i2), set, map));
            }
            return new SignedConfig(i, arrayList);
        } catch (JSONException e) {
            throw new InvalidConfigException("Could not parse JSON", e);
        }
    }

    public static CharSequence quoted(Object obj) {
        if (obj == null) {
            return "null";
        }
        return "\"" + obj + "\"";
    }

    @VisibleForTesting
    public static PerSdkConfig parsePerSdkConfig(JSONObject jSONObject, Set<String> set, Map<String, Map<String, String>> map) throws JSONException, InvalidConfigException {
        int i = jSONObject.getInt("min_sdk");
        int i2 = jSONObject.getInt("max_sdk");
        JSONObject jSONObject2 = jSONObject.getJSONObject("values");
        HashMap hashMap = new HashMap();
        for (String str : jSONObject2.keySet()) {
            Object obj = jSONObject2.get(str);
            String obj2 = (obj == JSONObject.NULL || obj == null) ? null : obj.toString();
            if (!set.contains(str)) {
                throw new InvalidConfigException("Config key " + str + " is not allowed");
            }
            if (map.containsKey(str)) {
                Map<String, String> map2 = map.get(str);
                if (!map2.containsKey(obj2)) {
                    throw new InvalidConfigException("Config key " + str + " contains unsupported value " + ((Object) quoted(obj2)));
                }
                obj2 = map2.get(obj2);
            }
            hashMap.put(str, obj2);
        }
        return new PerSdkConfig(i, i2, hashMap);
    }
}
