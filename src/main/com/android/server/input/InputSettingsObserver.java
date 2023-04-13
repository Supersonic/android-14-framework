package com.android.server.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.input.InputSettings;
import android.net.Uri;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Log;
import android.view.PointerIcon;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class InputSettingsObserver extends ContentObserver {
    public final Context mContext;
    public final Handler mHandler;
    public final NativeInputManagerService mNative;
    public final Map<Uri, Consumer<String>> mObservers;

    public InputSettingsObserver(Context context, Handler handler, NativeInputManagerService nativeInputManagerService) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
        this.mNative = nativeInputManagerService;
        this.mObservers = Map.ofEntries(Map.entry(Settings.System.getUriFor("pointer_speed"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$0((String) obj);
            }
        }), Map.entry(Settings.System.getUriFor("touchpad_pointer_speed"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$1((String) obj);
            }
        }), Map.entry(Settings.System.getUriFor("touchpad_natural_scrolling"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$2((String) obj);
            }
        }), Map.entry(Settings.System.getUriFor("touchpad_tap_to_click"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$3((String) obj);
            }
        }), Map.entry(Settings.System.getUriFor("touchpad_right_click_zone"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$4((String) obj);
            }
        }), Map.entry(Settings.System.getUriFor("show_touches"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$5((String) obj);
            }
        }), Map.entry(Settings.Secure.getUriFor("accessibility_large_pointer_icon"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$6((String) obj);
            }
        }), Map.entry(Settings.Secure.getUriFor("long_press_timeout"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$7((String) obj);
            }
        }), Map.entry(Settings.Global.getUriFor("maximum_obscuring_opacity_for_touch"), new Consumer() { // from class: com.android.server.input.InputSettingsObserver$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InputSettingsObserver.this.lambda$new$8((String) obj);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str) {
        updateMousePointerSpeed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(String str) {
        updateTouchpadPointerSpeed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(String str) {
        updateTouchpadNaturalScrollingEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(String str) {
        updateTouchpadTapToClickEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(String str) {
        updateTouchpadRightClickZoneEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(String str) {
        updateShowTouches();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(String str) {
        updateAccessibilityLargePointer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(String str) {
        updateMaximumObscuringOpacityForTouch();
    }

    public void registerAndUpdate() {
        for (Uri uri : this.mObservers.keySet()) {
            this.mContext.getContentResolver().registerContentObserver(uri, true, this, -1);
        }
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.input.InputSettingsObserver.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                for (Consumer consumer : InputSettingsObserver.this.mObservers.values()) {
                    consumer.accept("user switched");
                }
            }
        }, new IntentFilter("android.intent.action.USER_SWITCHED"), null, this.mHandler);
        for (Consumer<String> consumer : this.mObservers.values()) {
            consumer.accept("just booted");
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        this.mObservers.get(uri).accept("setting changed");
    }

    public final boolean getBoolean(String str, boolean z) {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), str, z ? 1 : 0, -2) != 0;
    }

    public final int getPointerSpeedValue(String str) {
        return Math.min(Math.max(Settings.System.getIntForUser(this.mContext.getContentResolver(), str, 0, -2), -7), 7);
    }

    public final void updateMousePointerSpeed() {
        this.mNative.setPointerSpeed(getPointerSpeedValue("pointer_speed"));
    }

    public final void updateTouchpadPointerSpeed() {
        this.mNative.setTouchpadPointerSpeed(getPointerSpeedValue("touchpad_pointer_speed"));
    }

    public final void updateTouchpadNaturalScrollingEnabled() {
        this.mNative.setTouchpadNaturalScrollingEnabled(getBoolean("touchpad_natural_scrolling", true));
    }

    public final void updateTouchpadTapToClickEnabled() {
        this.mNative.setTouchpadTapToClickEnabled(getBoolean("touchpad_tap_to_click", true));
    }

    public final void updateTouchpadRightClickZoneEnabled() {
        this.mNative.setTouchpadRightClickZoneEnabled(getBoolean("touchpad_right_click_zone", false));
    }

    public final void updateShowTouches() {
        this.mNative.setShowTouches(getBoolean("show_touches", false));
    }

    public final void updateAccessibilityLargePointer() {
        PointerIcon.setUseLargeIcons(Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_large_pointer_icon", 0, -2) == 1);
        this.mNative.reloadPointerIcons();
    }

    /* renamed from: updateDeepPressStatus */
    public final void lambda$new$7(String str) {
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "long_press_timeout", FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND, -2);
        boolean z = true;
        boolean z2 = DeviceConfig.getBoolean("input_native_boot", "deep_press_enabled", true);
        z = (!z2 || intForUser > 400) ? false : false;
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "Enabling" : "Disabling");
        sb.append(" motion classifier because ");
        sb.append(str);
        sb.append(": feature ");
        sb.append(z2 ? "enabled" : "disabled");
        sb.append(", long press timeout = ");
        sb.append(intForUser);
        Log.i("InputManager", sb.toString());
        this.mNative.setMotionClassifierEnabled(z);
    }

    public final void updateMaximumObscuringOpacityForTouch() {
        float maximumObscuringOpacityForTouch = InputSettings.getMaximumObscuringOpacityForTouch(this.mContext);
        if (maximumObscuringOpacityForTouch < 0.0f || maximumObscuringOpacityForTouch > 1.0f) {
            Log.e("InputManager", "Invalid maximum obscuring opacity " + maximumObscuringOpacityForTouch + ", it should be >= 0 and <= 1, rejecting update.");
            return;
        }
        this.mNative.setMaximumObscuringOpacityForTouch(maximumObscuringOpacityForTouch);
    }
}
