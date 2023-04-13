package com.android.server.hdmi;

import android.util.SparseArray;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes.dex */
public final class HdmiCecStandbyModeHandler {
    public final CecMessageHandler mAborterIncorrectMode;
    public final CecMessageHandler mAborterRefused;
    public final CecMessageHandler mAborterUnrecognizedOpcode;
    public final CecMessageHandler mAutoOnHandler;
    public final CecMessageHandler mBypasser;
    public final CecMessageHandler mBystander;
    public final SparseArray<CecMessageHandler> mCecMessageHandlers = new SparseArray<>();
    public final CecMessageHandler mDefaultHandler;
    public final HdmiCecLocalDevice mDevice;
    public final HdmiControlService mService;
    public final UserControlProcessedHandler mUserControlProcessedHandler;

    /* loaded from: classes.dex */
    public interface CecMessageHandler {
        boolean handle(HdmiCecMessage hdmiCecMessage);
    }

    /* loaded from: classes.dex */
    public static final class Bystander implements CecMessageHandler {
        @Override // com.android.server.hdmi.HdmiCecStandbyModeHandler.CecMessageHandler
        public boolean handle(HdmiCecMessage hdmiCecMessage) {
            return true;
        }

        public Bystander() {
        }
    }

    /* loaded from: classes.dex */
    public static final class Bypasser implements CecMessageHandler {
        @Override // com.android.server.hdmi.HdmiCecStandbyModeHandler.CecMessageHandler
        public boolean handle(HdmiCecMessage hdmiCecMessage) {
            return false;
        }

        public Bypasser() {
        }
    }

    /* loaded from: classes.dex */
    public final class Aborter implements CecMessageHandler {
        public final int mReason;

        public Aborter(int i) {
            this.mReason = i;
        }

        @Override // com.android.server.hdmi.HdmiCecStandbyModeHandler.CecMessageHandler
        public boolean handle(HdmiCecMessage hdmiCecMessage) {
            HdmiCecStandbyModeHandler.this.mService.maySendFeatureAbortCommand(hdmiCecMessage, this.mReason);
            return true;
        }
    }

    /* loaded from: classes.dex */
    public final class AutoOnHandler implements CecMessageHandler {
        public AutoOnHandler() {
        }

        @Override // com.android.server.hdmi.HdmiCecStandbyModeHandler.CecMessageHandler
        public boolean handle(HdmiCecMessage hdmiCecMessage) {
            if (((HdmiCecLocalDeviceTv) HdmiCecStandbyModeHandler.this.mDevice).getAutoWakeup()) {
                return false;
            }
            HdmiCecStandbyModeHandler.this.mAborterRefused.handle(hdmiCecMessage);
            return true;
        }
    }

    /* loaded from: classes.dex */
    public final class UserControlProcessedHandler implements CecMessageHandler {
        public UserControlProcessedHandler() {
        }

        @Override // com.android.server.hdmi.HdmiCecStandbyModeHandler.CecMessageHandler
        public boolean handle(HdmiCecMessage hdmiCecMessage) {
            if (HdmiCecLocalDevice.isPowerOnOrToggleCommand(hdmiCecMessage)) {
                return false;
            }
            if (HdmiCecLocalDevice.isPowerOffOrToggleCommand(hdmiCecMessage)) {
                return true;
            }
            return HdmiCecStandbyModeHandler.this.mAborterIncorrectMode.handle(hdmiCecMessage);
        }
    }

    public final void addCommonHandlers() {
        addHandler(68, this.mUserControlProcessedHandler);
    }

    public final void addTvHandlers() {
        addHandler(130, this.mBystander);
        addHandler(133, this.mBystander);
        addHandler(128, this.mBystander);
        addHandler(129, this.mBystander);
        addHandler(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE, this.mBystander);
        addHandler(54, this.mBystander);
        addHandler(50, this.mBystander);
        addHandler(69, this.mBystander);
        addHandler(0, this.mBystander);
        addHandler(FrameworkStatsLog.f421x729e24be, this.mBystander);
        addHandler(126, this.mBystander);
        addHandler(122, this.mBystander);
        addHandler(131, this.mBypasser);
        addHandler(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__CAN_INTERACT_ACROSS_PROFILES_TRUE, this.mBypasser);
        addHandler(132, this.mBypasser);
        addHandler(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES, this.mBypasser);
        addHandler(70, this.mBypasser);
        addHandler(71, this.mBypasser);
        addHandler(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED, this.mBypasser);
        addHandler(144, this.mBypasser);
        addHandler(FrameworkStatsLog.f383xde3a78eb, this.mBypasser);
        addHandler(143, this.mBypasser);
        addHandler(255, this.mBypasser);
        addHandler(FrameworkStatsLog.f420x89db317, this.mBypasser);
        addHandler(FrameworkStatsLog.f418x97ec91aa, this.mAborterIncorrectMode);
        addHandler(114, this.mAborterIncorrectMode);
        addHandler(4, this.mAutoOnHandler);
        addHandler(13, this.mAutoOnHandler);
        addHandler(10, this.mBystander);
        addHandler(15, this.mAborterIncorrectMode);
        addHandler(FrameworkStatsLog.f392xcd34d435, this.mAborterIncorrectMode);
        addHandler(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_PARAM, this.mAborterIncorrectMode);
    }

    public HdmiCecStandbyModeHandler(HdmiControlService hdmiControlService, HdmiCecLocalDevice hdmiCecLocalDevice) {
        Aborter aborter = new Aborter(0);
        this.mAborterUnrecognizedOpcode = aborter;
        this.mAborterIncorrectMode = new Aborter(1);
        this.mAborterRefused = new Aborter(4);
        this.mAutoOnHandler = new AutoOnHandler();
        Bypasser bypasser = new Bypasser();
        this.mBypasser = bypasser;
        this.mBystander = new Bystander();
        this.mUserControlProcessedHandler = new UserControlProcessedHandler();
        this.mService = hdmiControlService;
        this.mDevice = hdmiCecLocalDevice;
        addCommonHandlers();
        if (hdmiCecLocalDevice.getType() == 0) {
            addTvHandlers();
            this.mDefaultHandler = aborter;
            return;
        }
        this.mDefaultHandler = bypasser;
    }

    public final void addHandler(int i, CecMessageHandler cecMessageHandler) {
        this.mCecMessageHandlers.put(i, cecMessageHandler);
    }

    public boolean handleCommand(HdmiCecMessage hdmiCecMessage) {
        CecMessageHandler cecMessageHandler = this.mCecMessageHandlers.get(hdmiCecMessage.getOpcode());
        if (cecMessageHandler != null) {
            return cecMessageHandler.handle(hdmiCecMessage);
        }
        return this.mDefaultHandler.handle(hdmiCecMessage);
    }
}
