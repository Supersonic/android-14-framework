package com.android.ims.rcs.uce.presence.publish;

import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IRcsUcePublishStateCallback;
import com.android.ims.rcs.uce.ControllerBase;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.util.Set;
/* loaded from: classes.dex */
public interface PublishController extends ControllerBase {
    public static final int PUBLISH_TRIGGER_CARRIER_CONFIG_CHANGED = 15;
    public static final int PUBLISH_TRIGGER_MMTEL_CAPABILITY_CHANGE = 8;
    public static final int PUBLISH_TRIGGER_MMTEL_RCS_UNREGISTERED = 16;
    public static final int PUBLISH_TRIGGER_MMTEL_REGISTERED = 6;
    public static final int PUBLISH_TRIGGER_MMTEL_UNREGISTERED = 7;
    public static final int PUBLISH_TRIGGER_MMTEL_URI_CHANGE = 9;
    public static final int PUBLISH_TRIGGER_MOBILE_DATA_CHANGE = 4;
    public static final int PUBLISH_TRIGGER_OVERRIDE_CAPS = 14;
    public static final int PUBLISH_TRIGGER_PROVISIONING_CHANGE = 13;
    public static final int PUBLISH_TRIGGER_RCS_REGISTERED = 10;
    public static final int PUBLISH_TRIGGER_RCS_UNREGISTERED = 11;
    public static final int PUBLISH_TRIGGER_RCS_URI_CHANGE = 12;
    public static final int PUBLISH_TRIGGER_RETRY = 2;
    public static final int PUBLISH_TRIGGER_SERVICE = 1;
    public static final int PUBLISH_TRIGGER_TTY_PREFERRED_CHANGE = 3;
    public static final int PUBLISH_TRIGGER_VT_SETTING_CHANGE = 5;

    /* loaded from: classes.dex */
    public interface PublishControllerCallback {
        void clearRequestCanceledTimer();

        void notifyPendingPublishRequest();

        void onRequestCommandError(PublishRequestResponse publishRequestResponse);

        void onRequestNetworkResp(PublishRequestResponse publishRequestResponse);

        void refreshDeviceState(int i, String str);

        void requestPublishFromInternal(int i);

        void setupRequestCanceledTimer(long j, long j2);

        void updateImsUnregistered();

        void updatePublishRequestResult(int i, Instant instant, String str, SipDetails sipDetails);

        void updatePublishThrottle(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PublishTriggerType {
    }

    RcsContactUceCapability addRegistrationOverrideCapabilities(Set<String> set);

    RcsContactUceCapability clearRegistrationOverrideCapabilities();

    void clearResetDeviceStateTimer();

    void dump(PrintWriter printWriter);

    RcsContactUceCapability getDeviceCapabilities(int i);

    String getLastPidfXml();

    RcsContactUceCapability getLatestRcsContactUceCapability();

    int getUcePublishState(boolean z);

    void onPublishUpdated(SipDetails sipDetails);

    void onUnpublish();

    void registerPublishStateCallback(IRcsUcePublishStateCallback iRcsUcePublishStateCallback, boolean z);

    RcsContactUceCapability removeRegistrationOverrideCapabilities(Set<String> set);

    void requestPublishCapabilitiesFromService(int i);

    void setupResetDeviceStateTimer(long j);

    void unregisterPublishStateCallback(IRcsUcePublishStateCallback iRcsUcePublishStateCallback);
}
