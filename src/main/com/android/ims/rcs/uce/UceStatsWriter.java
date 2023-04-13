package com.android.ims.rcs.uce;

import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class UceStatsWriter {
    public static final int INCOMING_OPTION_EVENT = 2;
    public static final int OUTGOING_OPTION_EVENT = 3;
    public static final int PUBLISH_EVENT = 0;
    public static final int SUBSCRIBE_EVENT = 1;
    private static UceStatsWriter sInstance = null;
    private UceStatsCallback mCallBack;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface UceEventType {
    }

    /* loaded from: classes.dex */
    public interface UceStatsCallback {
        void onImsRegistrationFeatureTagStats(int i, List<String> list, int i2);

        void onImsRegistrationServiceDescStats(int i, List<String> list, List<String> list2, int i2);

        void onPresenceNotifyEvent(int i, long j, List<RcsContactUceCapability> list);

        void onStoreCompleteImsRegistrationFeatureTagStats(int i);

        void onStoreCompleteImsRegistrationServiceDescStats(int i);

        void onSubscribeResponse(int i, long j, int i2);

        void onSubscribeTerminated(int i, long j, String str);

        void onUceEvent(int i, int i2, boolean z, int i3, int i4);
    }

    public static UceStatsWriter init(UceStatsCallback callback) {
        UceStatsWriter uceStatsWriter;
        synchronized (UceStatsWriter.class) {
            if (sInstance == null) {
                sInstance = new UceStatsWriter(callback);
            }
            uceStatsWriter = sInstance;
        }
        return uceStatsWriter;
    }

    public static UceStatsWriter getInstance() {
        UceStatsWriter uceStatsWriter;
        synchronized (UceStatsWriter.class) {
            uceStatsWriter = sInstance;
        }
        return uceStatsWriter;
    }

    public void setImsRegistrationFeatureTagStats(int subId, List<String> featureTagList, int registrationTech) {
        UceStatsCallback uceStatsCallback = this.mCallBack;
        if (uceStatsCallback == null) {
            return;
        }
        uceStatsCallback.onImsRegistrationFeatureTagStats(subId, featureTagList, registrationTech);
    }

    public void setStoreCompleteImsRegistrationFeatureTagStats(int subId) {
        UceStatsCallback uceStatsCallback = this.mCallBack;
        if (uceStatsCallback == null) {
            return;
        }
        uceStatsCallback.onStoreCompleteImsRegistrationFeatureTagStats(subId);
    }

    public void setImsRegistrationServiceDescStats(int subId, List<RcsContactPresenceTuple> tupleList, int registrationTech) {
        if (this.mCallBack == null) {
            return;
        }
        ArrayList<String> svcId = new ArrayList<>();
        ArrayList<String> svcVersion = new ArrayList<>();
        for (RcsContactPresenceTuple tuple : tupleList) {
            svcId.add(tuple.getServiceId());
            svcVersion.add(tuple.getServiceVersion());
        }
        this.mCallBack.onImsRegistrationServiceDescStats(subId, svcId, svcVersion, registrationTech);
    }

    public void setSubscribeResponse(int subId, long taskId, int networkResponse) {
        UceStatsCallback uceStatsCallback = this.mCallBack;
        if (uceStatsCallback != null) {
            uceStatsCallback.onSubscribeResponse(subId, taskId, networkResponse);
        }
    }

    public void setUceEvent(int subId, int type, boolean successful, int commandCode, int networkResponse) {
        UceStatsCallback uceStatsCallback = this.mCallBack;
        if (uceStatsCallback != null) {
            uceStatsCallback.onUceEvent(subId, type, successful, commandCode, networkResponse);
        }
    }

    public void setPresenceNotifyEvent(int subId, long taskId, List<RcsContactUceCapability> updatedCapList) {
        if (this.mCallBack == null || updatedCapList == null || updatedCapList.isEmpty()) {
            return;
        }
        this.mCallBack.onPresenceNotifyEvent(subId, taskId, updatedCapList);
    }

    public void setSubscribeTerminated(int subId, long taskId, String reason) {
        UceStatsCallback uceStatsCallback = this.mCallBack;
        if (uceStatsCallback != null) {
            uceStatsCallback.onSubscribeTerminated(subId, taskId, reason);
        }
    }

    public void setUnPublish(int subId) {
        UceStatsCallback uceStatsCallback = this.mCallBack;
        if (uceStatsCallback != null) {
            uceStatsCallback.onStoreCompleteImsRegistrationServiceDescStats(subId);
        }
    }

    protected UceStatsWriter(UceStatsCallback callback) {
        this.mCallBack = callback;
    }
}
