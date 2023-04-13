package com.android.server.display;

import android.hardware.display.BrightnessInfo;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IThermalEventListener;
import android.os.IThermalService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Temperature;
import android.provider.DeviceConfig;
import android.provider.DeviceConfigInterface;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.BrightnessThrottler;
import com.android.server.display.DisplayDeviceConfig;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class BrightnessThrottler {
    public float mBrightnessCap;
    public int mBrightnessMaxReason;
    public HashMap<String, DisplayDeviceConfig.BrightnessThrottlingData> mBrightnessThrottlingDataOverride;
    public String mBrightnessThrottlingDataString;
    public DisplayDeviceConfig.BrightnessThrottlingData mDdcThrottlingData;
    public final DeviceConfigInterface mDeviceConfig;
    public final Handler mDeviceConfigHandler;
    public final DeviceConfigListener mDeviceConfigListener;
    public final Handler mHandler;
    public final Injector mInjector;
    public final SkinThermalStatusObserver mSkinThermalStatusObserver;
    public final Runnable mThrottlingChangeCallback;
    public DisplayDeviceConfig.BrightnessThrottlingData mThrottlingData;
    public int mThrottlingStatus;
    public String mUniqueDisplayId;

    public BrightnessThrottler(Handler handler, DisplayDeviceConfig.BrightnessThrottlingData brightnessThrottlingData, Runnable runnable, String str) {
        this(new Injector(), handler, handler, brightnessThrottlingData, runnable, str);
    }

    @VisibleForTesting
    public BrightnessThrottler(Injector injector, Handler handler, Handler handler2, DisplayDeviceConfig.BrightnessThrottlingData brightnessThrottlingData, Runnable runnable, String str) {
        this.mBrightnessCap = 1.0f;
        this.mBrightnessMaxReason = 0;
        this.mBrightnessThrottlingDataOverride = new HashMap<>(1);
        this.mInjector = injector;
        this.mHandler = handler;
        this.mDeviceConfigHandler = handler2;
        this.mThrottlingData = brightnessThrottlingData;
        this.mDdcThrottlingData = brightnessThrottlingData;
        this.mThrottlingChangeCallback = runnable;
        this.mSkinThermalStatusObserver = new SkinThermalStatusObserver(injector, handler);
        this.mUniqueDisplayId = str;
        this.mDeviceConfig = injector.getDeviceConfig();
        this.mDeviceConfigListener = new DeviceConfigListener();
        resetThrottlingData(this.mThrottlingData, this.mUniqueDisplayId);
    }

    public boolean deviceSupportsThrottling() {
        return this.mThrottlingData != null;
    }

    public float getBrightnessCap() {
        return this.mBrightnessCap;
    }

    public int getBrightnessMaxReason() {
        return this.mBrightnessMaxReason;
    }

    public boolean isThrottled() {
        return this.mBrightnessMaxReason != 0;
    }

    public void stop() {
        this.mSkinThermalStatusObserver.stopObserving();
        this.mDeviceConfig.removeOnPropertiesChangedListener(this.mDeviceConfigListener);
        this.mBrightnessCap = 1.0f;
        this.mBrightnessMaxReason = 0;
        this.mThrottlingStatus = -1;
    }

    public final void resetThrottlingData() {
        resetThrottlingData(this.mDdcThrottlingData, this.mUniqueDisplayId);
    }

    public void resetThrottlingData(DisplayDeviceConfig.BrightnessThrottlingData brightnessThrottlingData, String str) {
        stop();
        this.mUniqueDisplayId = str;
        this.mDdcThrottlingData = brightnessThrottlingData;
        this.mDeviceConfigListener.startListening();
        reloadBrightnessThrottlingDataOverride();
        this.mThrottlingData = this.mBrightnessThrottlingDataOverride.getOrDefault(this.mUniqueDisplayId, brightnessThrottlingData);
        if (deviceSupportsThrottling()) {
            this.mSkinThermalStatusObserver.startObserving();
        }
    }

    public final float verifyAndConstrainBrightnessCap(float f) {
        if (f < 0.0f) {
            Slog.e("BrightnessThrottler", "brightness " + f + " is lower than the minimum possible brightness 0.0");
            f = 0.0f;
        }
        if (f > 1.0f) {
            Slog.e("BrightnessThrottler", "brightness " + f + " is higher than the maximum possible brightness 1.0");
            return 1.0f;
        }
        return f;
    }

    public final void thermalStatusChanged(int i) {
        if (this.mThrottlingStatus != i) {
            this.mThrottlingStatus = i;
            updateThrottling();
        }
    }

    public final void updateThrottling() {
        if (deviceSupportsThrottling()) {
            float f = 1.0f;
            int i = 0;
            if (this.mThrottlingStatus != -1) {
                for (DisplayDeviceConfig.BrightnessThrottlingData.ThrottlingLevel throttlingLevel : this.mThrottlingData.throttlingLevels) {
                    if (throttlingLevel.thermalStatus > this.mThrottlingStatus) {
                        break;
                    }
                    f = throttlingLevel.brightness;
                    i = 1;
                }
            }
            if (this.mBrightnessCap == f && this.mBrightnessMaxReason == i) {
                return;
            }
            this.mBrightnessCap = verifyAndConstrainBrightnessCap(f);
            this.mBrightnessMaxReason = i;
            Runnable runnable = this.mThrottlingChangeCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public void dump(final PrintWriter printWriter) {
        this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.display.BrightnessThrottler$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BrightnessThrottler.this.lambda$dump$0(printWriter);
            }
        }, 1000L);
    }

    /* renamed from: dumpLocal */
    public final void lambda$dump$0(PrintWriter printWriter) {
        printWriter.println("BrightnessThrottler:");
        printWriter.println("  mThrottlingData=" + this.mThrottlingData);
        printWriter.println("  mDdcThrottlingData=" + this.mDdcThrottlingData);
        printWriter.println("  mUniqueDisplayId=" + this.mUniqueDisplayId);
        printWriter.println("  mThrottlingStatus=" + this.mThrottlingStatus);
        printWriter.println("  mBrightnessCap=" + this.mBrightnessCap);
        printWriter.println("  mBrightnessMaxReason=" + BrightnessInfo.briMaxReasonToString(this.mBrightnessMaxReason));
        printWriter.println("  mBrightnessThrottlingDataOverride=" + this.mBrightnessThrottlingDataOverride);
        printWriter.println("  mBrightnessThrottlingDataString=" + this.mBrightnessThrottlingDataString);
        this.mSkinThermalStatusObserver.dump(printWriter);
    }

    public final String getBrightnessThrottlingDataString() {
        return this.mDeviceConfig.getString("display_manager", "brightness_throttling_data", (String) null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0068  */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean parseAndSaveData(String str, HashMap<String, DisplayDeviceConfig.BrightnessThrottlingData> hashMap) {
        int i;
        String[] split = str.split(",");
        int i2 = 1;
        try {
            String str2 = split[0];
            i = 2;
            try {
                int parseInt = Integer.parseInt(split[1]);
                ArrayList arrayList = new ArrayList(parseInt);
                int i3 = 0;
                while (i3 < parseInt) {
                    int i4 = i + 1;
                    try {
                        int i5 = i4 + 1;
                        try {
                            arrayList.add(new DisplayDeviceConfig.BrightnessThrottlingData.ThrottlingLevel(parseThermalStatus(split[i]), parseBrightness(split[i4])));
                            i3++;
                            i = i5;
                        } catch (UnknownThermalStatusException | IndexOutOfBoundsException | NumberFormatException e) {
                            e = e;
                            i2 = i5;
                            Slog.e("BrightnessThrottler", "Throttling data is invalid array: '" + str + "'", e);
                            i = i2;
                            i2 = 0;
                            if (i == split.length) {
                            }
                        }
                    } catch (UnknownThermalStatusException | IndexOutOfBoundsException | NumberFormatException e2) {
                        e = e2;
                        i2 = i4;
                    }
                }
                hashMap.put(str2, DisplayDeviceConfig.BrightnessThrottlingData.create(arrayList));
            } catch (UnknownThermalStatusException | IndexOutOfBoundsException | NumberFormatException e3) {
                e = e3;
                i2 = i;
            }
        } catch (UnknownThermalStatusException | IndexOutOfBoundsException | NumberFormatException e4) {
            e = e4;
        }
        if (i == split.length) {
            return false;
        }
        return i2;
    }

    public void reloadBrightnessThrottlingDataOverride() {
        boolean z = true;
        HashMap<String, DisplayDeviceConfig.BrightnessThrottlingData> hashMap = new HashMap<>(1);
        this.mBrightnessThrottlingDataString = getBrightnessThrottlingDataString();
        this.mBrightnessThrottlingDataOverride.clear();
        String str = this.mBrightnessThrottlingDataString;
        if (str != null) {
            String[] split = str.split(";");
            int length = split.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                } else if (!parseAndSaveData(split[i], hashMap)) {
                    z = false;
                    break;
                } else {
                    i++;
                }
            }
            if (z) {
                this.mBrightnessThrottlingDataOverride.putAll(hashMap);
                hashMap.clear();
                return;
            }
            return;
        }
        Slog.w("BrightnessThrottler", "DeviceConfig BrightnessThrottlingData is null");
    }

    /* loaded from: classes.dex */
    public class DeviceConfigListener implements DeviceConfig.OnPropertiesChangedListener {
        public Executor mExecutor;

        public DeviceConfigListener() {
            this.mExecutor = new HandlerExecutor(BrightnessThrottler.this.mDeviceConfigHandler);
        }

        public void startListening() {
            BrightnessThrottler.this.mDeviceConfig.addOnPropertiesChangedListener("display_manager", this.mExecutor, this);
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            BrightnessThrottler.this.reloadBrightnessThrottlingDataOverride();
            BrightnessThrottler.this.resetThrottlingData();
        }
    }

    public final float parseBrightness(String str) throws NumberFormatException {
        float parseFloat = Float.parseFloat(str);
        if (parseFloat < 0.0f || parseFloat > 1.0f) {
            throw new NumberFormatException("Brightness constraint value out of bounds.");
        }
        return parseFloat;
    }

    public final int parseThermalStatus(String str) throws UnknownThermalStatusException {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -905723276:
                if (str.equals("severe")) {
                    c = 0;
                    break;
                }
                break;
            case -618857213:
                if (str.equals("moderate")) {
                    c = 1;
                    break;
                }
                break;
            case -169343402:
                if (str.equals("shutdown")) {
                    c = 2;
                    break;
                }
                break;
            case 3387192:
                if (str.equals("none")) {
                    c = 3;
                    break;
                }
                break;
            case 102970646:
                if (str.equals("light")) {
                    c = 4;
                    break;
                }
                break;
            case 1629013393:
                if (str.equals("emergency")) {
                    c = 5;
                    break;
                }
                break;
            case 1952151455:
                if (str.equals("critical")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 6;
            case 3:
                return 0;
            case 4:
                return 1;
            case 5:
                return 5;
            case 6:
                return 4;
            default:
                throw new UnknownThermalStatusException("Invalid Thermal Status: " + str);
        }
    }

    /* loaded from: classes.dex */
    public static class UnknownThermalStatusException extends Exception {
        public UnknownThermalStatusException(String str) {
            super(str);
        }
    }

    /* loaded from: classes.dex */
    public final class SkinThermalStatusObserver extends IThermalEventListener.Stub {
        public final Handler mHandler;
        public final Injector mInjector;
        public boolean mStarted;
        public IThermalService mThermalService;

        public SkinThermalStatusObserver(Injector injector, Handler handler) {
            this.mInjector = injector;
            this.mHandler = handler;
        }

        public void notifyThrottling(final Temperature temperature) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.display.BrightnessThrottler$SkinThermalStatusObserver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BrightnessThrottler.SkinThermalStatusObserver.this.lambda$notifyThrottling$0(temperature);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyThrottling$0(Temperature temperature) {
            BrightnessThrottler.this.thermalStatusChanged(temperature.getStatus());
        }

        public void startObserving() {
            if (this.mStarted) {
                return;
            }
            IThermalService thermalService = this.mInjector.getThermalService();
            this.mThermalService = thermalService;
            if (thermalService == null) {
                Slog.e("BrightnessThrottler", "Could not observe thermal status. Service not available");
                return;
            }
            try {
                thermalService.registerThermalEventListenerWithType(this, 3);
                this.mStarted = true;
            } catch (RemoteException e) {
                Slog.e("BrightnessThrottler", "Failed to register thermal status listener", e);
            }
        }

        public void stopObserving() {
            if (this.mStarted) {
                try {
                    this.mThermalService.unregisterThermalEventListener(this);
                    this.mStarted = false;
                } catch (RemoteException e) {
                    Slog.e("BrightnessThrottler", "Failed to unregister thermal status listener", e);
                }
                this.mThermalService = null;
            }
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("  SkinThermalStatusObserver:");
            printWriter.println("    mStarted: " + this.mStarted);
            if (this.mThermalService != null) {
                printWriter.println("    ThermalService available");
            } else {
                printWriter.println("    ThermalService not available");
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Injector {
        public IThermalService getThermalService() {
            return IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
        }

        public DeviceConfigInterface getDeviceConfig() {
            return DeviceConfigInterface.REAL;
        }
    }
}
