package com.android.server.input;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.FeatureFlagUtils;
import android.view.InputDevice;
import com.android.internal.annotations.GuardedBy;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public final class KeyRemapper implements InputManager.InputDeviceListener {
    public final Context mContext;
    @GuardedBy({"mDataStore"})
    public final PersistentDataStore mDataStore;
    public final Handler mHandler;
    public final NativeInputManagerService mNative;

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
    }

    public KeyRemapper(Context context, NativeInputManagerService nativeInputManagerService, PersistentDataStore persistentDataStore, Looper looper) {
        this.mContext = context;
        this.mNative = nativeInputManagerService;
        this.mDataStore = persistentDataStore;
        this.mHandler = new Handler(looper, new Handler.Callback() { // from class: com.android.server.input.KeyRemapper$$ExternalSyntheticLambda1
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean handleMessage;
                handleMessage = KeyRemapper.this.handleMessage(message);
                return handleMessage;
            }
        });
    }

    public void systemRunning() {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        Objects.requireNonNull(inputManager);
        inputManager.registerInputDeviceListener(this, this.mHandler);
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, inputManager.getInputDeviceIds()));
    }

    public void remapKey(int i, int i2) {
        if (supportRemapping()) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 2, i, i2));
        }
    }

    public void clearAllKeyRemappings() {
        if (supportRemapping()) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 3));
        }
    }

    public Map<Integer, Integer> getKeyRemapping() {
        Map<Integer, Integer> keyRemapping;
        if (!supportRemapping()) {
            return new ArrayMap();
        }
        synchronized (this.mDataStore) {
            keyRemapping = this.mDataStore.getKeyRemapping();
        }
        return keyRemapping;
    }

    public final void addKeyRemapping(int i, int i2) {
        int[] inputDeviceIds;
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        Objects.requireNonNull(inputManager);
        for (int i3 : inputManager.getInputDeviceIds()) {
            InputDevice inputDevice = inputManager.getInputDevice(i3);
            if (inputDevice != null && !inputDevice.isVirtual() && inputDevice.isFullKeyboard()) {
                this.mNative.addKeyRemapping(i3, i, i2);
            }
        }
    }

    public final void remapKeyInternal(int i, int i2) {
        addKeyRemapping(i, i2);
        synchronized (this.mDataStore) {
            if (i == i2) {
                this.mDataStore.clearMappedKey(i);
            } else {
                this.mDataStore.remapKey(i, i2);
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    public final void clearAllRemappingsInternal() {
        synchronized (this.mDataStore) {
            for (Integer num : this.mDataStore.getKeyRemapping().keySet()) {
                int intValue = num.intValue();
                this.mDataStore.clearMappedKey(intValue);
                addKeyRemapping(intValue, intValue);
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(final int i) {
        if (supportRemapping()) {
            InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
            Objects.requireNonNull(inputManager);
            InputDevice inputDevice = inputManager.getInputDevice(i);
            if (inputDevice == null || inputDevice.isVirtual() || !inputDevice.isFullKeyboard()) {
                return;
            }
            getKeyRemapping().forEach(new BiConsumer() { // from class: com.android.server.input.KeyRemapper$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    KeyRemapper.this.lambda$onInputDeviceAdded$0(i, (Integer) obj, (Integer) obj2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onInputDeviceAdded$0(int i, Integer num, Integer num2) {
        this.mNative.addKeyRemapping(i, num.intValue(), num2.intValue());
    }

    public final boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            for (int i2 : (int[]) message.obj) {
                onInputDeviceAdded(i2);
            }
            return true;
        } else if (i == 2) {
            remapKeyInternal(message.arg1, message.arg2);
            return true;
        } else if (i != 3) {
            return false;
        } else {
            clearAllRemappingsInternal();
            return true;
        }
    }

    public final boolean supportRemapping() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_new_keyboard_modifier_key");
    }
}
