package com.android.server.timedetector;

import android.content.Context;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import com.android.internal.annotations.GuardedBy;
import com.android.server.timezonedetector.StateChangeListener;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
/* loaded from: classes2.dex */
public final class ServerFlags {
    @GuardedBy({"SLOCK"})
    public static ServerFlags sInstance;
    @GuardedBy({"mListeners"})
    public final ArrayMap<StateChangeListener, HashSet<String>> mListeners = new ArrayMap<>();
    public static final Optional<Boolean> OPTIONAL_TRUE = Optional.of(Boolean.TRUE);
    public static final Optional<Boolean> OPTIONAL_FALSE = Optional.of(Boolean.FALSE);
    public static final Object SLOCK = new Object();

    public ServerFlags(Context context) {
        DeviceConfig.addOnPropertiesChangedListener("system_time", context.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.timedetector.ServerFlags$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                ServerFlags.this.handlePropertiesChanged(properties);
            }
        });
    }

    public static ServerFlags getInstance(Context context) {
        ServerFlags serverFlags;
        synchronized (SLOCK) {
            if (sInstance == null) {
                sInstance = new ServerFlags(context);
            }
            serverFlags = sInstance;
        }
        return serverFlags;
    }

    public final void handlePropertiesChanged(DeviceConfig.Properties properties) {
        ArrayList<StateChangeListener> arrayList;
        synchronized (this.mListeners) {
            arrayList = new ArrayList(this.mListeners.size());
            for (Map.Entry<StateChangeListener, HashSet<String>> entry : this.mListeners.entrySet()) {
                if (containsAny(entry.getValue(), properties.getKeyset())) {
                    arrayList.add(entry.getKey());
                }
            }
        }
        for (StateChangeListener stateChangeListener : arrayList) {
            stateChangeListener.onChange();
        }
    }

    public static boolean containsAny(Set<String> set, Iterable<String> iterable) {
        for (String str : iterable) {
            if (set.contains(str)) {
                return true;
            }
        }
        return false;
    }

    public void addListener(StateChangeListener stateChangeListener, Set<String> set) {
        Objects.requireNonNull(stateChangeListener);
        Objects.requireNonNull(set);
        HashSet<String> hashSet = new HashSet<>(set);
        synchronized (this.mListeners) {
            this.mListeners.put(stateChangeListener, hashSet);
        }
    }

    public Optional<String> getOptionalString(String str) {
        return Optional.ofNullable(DeviceConfig.getProperty("system_time", str));
    }

    public Optional<String[]> getOptionalStringArray(String str) {
        Optional<String> optionalString = getOptionalString(str);
        if (!optionalString.isPresent()) {
            return Optional.empty();
        }
        String str2 = optionalString.get();
        if ("_[]_".equals(str2)) {
            return Optional.of(new String[0]);
        }
        return Optional.of(str2.split(","));
    }

    public Optional<Instant> getOptionalInstant(String str) {
        String property = DeviceConfig.getProperty("system_time", str);
        if (property == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Instant.ofEpochMilli(Long.parseLong(property)));
        } catch (NumberFormatException | DateTimeException unused) {
            return Optional.empty();
        }
    }

    public Optional<Boolean> getOptionalBoolean(String str) {
        return parseOptionalBoolean(DeviceConfig.getProperty("system_time", str));
    }

    public static Optional<Boolean> parseOptionalBoolean(String str) {
        if (str == null) {
            return Optional.empty();
        }
        return Boolean.parseBoolean(str) ? OPTIONAL_TRUE : OPTIONAL_FALSE;
    }

    public boolean getBoolean(String str, boolean z) {
        return DeviceConfig.getBoolean("system_time", str, z);
    }

    public Duration getDurationFromMillis(String str, Duration duration) {
        long j = DeviceConfig.getLong("system_time", str, -1L);
        return j < 0 ? duration : Duration.ofMillis(j);
    }
}
