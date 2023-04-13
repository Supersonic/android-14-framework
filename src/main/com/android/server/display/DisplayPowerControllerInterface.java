package com.android.server.display;

import android.content.pm.ParceledListSlice;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.DisplayManagerInternal;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public interface DisplayPowerControllerInterface {
    void addDisplayBrightnessFollower(DisplayPowerControllerInterface displayPowerControllerInterface);

    void dump(PrintWriter printWriter);

    ParceledListSlice<AmbientBrightnessDayStats> getAmbientBrightnessStats(int i);

    ParceledListSlice<BrightnessChangeEvent> getBrightnessEvents(int i, boolean z);

    BrightnessInfo getBrightnessInfo();

    BrightnessConfiguration getDefaultBrightnessConfiguration();

    int getDisplayId();

    int getLeadDisplayId();

    float getScreenBrightnessSetting();

    void ignoreProximitySensorUntilChanged();

    boolean isProximitySensorAvailable();

    void onBootCompleted();

    void onDisplayChanged(HighBrightnessModeMetadata highBrightnessModeMetadata, int i);

    void onSwitchUser(int i);

    void persistBrightnessTrackerState();

    void removeDisplayBrightnessFollower(DisplayPowerControllerInterface displayPowerControllerInterface);

    boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, boolean z);

    void setAmbientColorTemperatureOverride(float f);

    void setAutoBrightnessLoggingEnabled(boolean z);

    void setAutomaticScreenBrightnessMode(boolean z);

    void setBrightness(float f);

    void setBrightnessConfiguration(BrightnessConfiguration brightnessConfiguration, boolean z);

    void setBrightnessToFollow(float f, float f2, float f3);

    void setDisplayWhiteBalanceLoggingEnabled(boolean z);

    void setTemporaryAutoBrightnessAdjustment(float f);

    void setTemporaryBrightness(float f);

    void stop();
}
