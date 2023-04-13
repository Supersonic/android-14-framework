package com.android.internal.telephony.data;

import android.telephony.data.DataProfile;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class DataEvaluation {
    private final DataEvaluationReason mDataEvaluationReason;
    private final Set<DataDisallowedReason> mDataDisallowedReasons = new HashSet();
    private DataAllowedReason mDataAllowedReason = DataAllowedReason.NONE;
    private DataProfile mCandidateDataProfile = null;
    private long mEvaluatedTime = 0;

    /* loaded from: classes.dex */
    public enum DataAllowedReason {
        NONE,
        NORMAL,
        IN_VOICE_CALL,
        UNMETERED_USAGE,
        MMS_REQUEST,
        RESTRICTED_REQUEST,
        EMERGENCY_SUPL,
        EMERGENCY_REQUEST
    }

    public DataEvaluation(DataEvaluationReason dataEvaluationReason) {
        this.mDataEvaluationReason = dataEvaluationReason;
    }

    public void addDataDisallowedReason(DataDisallowedReason dataDisallowedReason) {
        this.mDataAllowedReason = DataAllowedReason.NONE;
        this.mDataDisallowedReasons.add(dataDisallowedReason);
        this.mEvaluatedTime = System.currentTimeMillis();
    }

    public void removeDataDisallowedReason(DataDisallowedReason dataDisallowedReason) {
        this.mDataDisallowedReasons.remove(dataDisallowedReason);
        this.mEvaluatedTime = System.currentTimeMillis();
    }

    public void addDataAllowedReason(DataAllowedReason dataAllowedReason) {
        this.mDataDisallowedReasons.clear();
        if (dataAllowedReason.ordinal() > this.mDataAllowedReason.ordinal()) {
            this.mDataAllowedReason = dataAllowedReason;
        }
        this.mEvaluatedTime = System.currentTimeMillis();
    }

    public List<DataDisallowedReason> getDataDisallowedReasons() {
        return new ArrayList(this.mDataDisallowedReasons);
    }

    public DataAllowedReason getDataAllowedReason() {
        return this.mDataAllowedReason;
    }

    public void setCandidateDataProfile(DataProfile dataProfile) {
        this.mCandidateDataProfile = dataProfile;
    }

    public DataProfile getCandidateDataProfile() {
        return this.mCandidateDataProfile;
    }

    public boolean containsDisallowedReasons() {
        return this.mDataDisallowedReasons.size() != 0;
    }

    public boolean contains(DataDisallowedReason dataDisallowedReason) {
        return this.mDataDisallowedReasons.contains(dataDisallowedReason);
    }

    public boolean containsOnly(DataDisallowedReason dataDisallowedReason) {
        return this.mDataDisallowedReasons.size() == 1 && contains(dataDisallowedReason);
    }

    public boolean containsAny(DataDisallowedReason... dataDisallowedReasonArr) {
        for (DataDisallowedReason dataDisallowedReason : dataDisallowedReasonArr) {
            if (this.mDataDisallowedReasons.contains(dataDisallowedReason)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(DataAllowedReason dataAllowedReason) {
        return dataAllowedReason == this.mDataAllowedReason;
    }

    public boolean containsHardDisallowedReasons() {
        for (DataDisallowedReason dataDisallowedReason : this.mDataDisallowedReasons) {
            if (dataDisallowedReason.isHardReason()) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public enum DataEvaluationReason {
        NEW_REQUEST(false),
        DATA_CONFIG_CHANGED(true),
        SIM_LOADED(true),
        SIM_REMOVAL(true),
        DATA_PROFILES_CHANGED(true),
        DATA_SERVICE_STATE_CHANGED(true),
        DATA_ENABLED_CHANGED(true),
        DATA_ENABLED_OVERRIDE_CHANGED(true),
        ROAMING_ENABLED_CHANGED(true),
        VOICE_CALL_ENDED(true),
        DATA_RESTRICTED_CHANGED(true),
        DATA_NETWORK_CAPABILITIES_CHANGED(true),
        EMERGENCY_CALL_CHANGED(true),
        RETRY_AFTER_DISCONNECTED(true),
        DATA_RETRY(false),
        DATA_HANDOVER(true),
        PREFERRED_TRANSPORT_CHANGED(true),
        SLICE_CONFIG_CHANGED(true),
        SRVCC_STATE_CHANGED(true),
        SINGLE_DATA_NETWORK_ARBITRATION(true),
        EXTERNAL_QUERY(false),
        TAC_CHANGED(true);
        
        private final boolean mIsConditionBased;

        public boolean isConditionBased() {
            return this.mIsConditionBased;
        }

        DataEvaluationReason(boolean z) {
            this.mIsConditionBased = z;
        }
    }

    /* loaded from: classes.dex */
    public enum DataDisallowedReason {
        DATA_DISABLED(false),
        ROAMING_DISABLED(false),
        DEFAULT_DATA_UNSELECTED(false),
        NOT_IN_SERVICE(true),
        DATA_CONFIG_NOT_READY(true),
        SIM_NOT_READY(true),
        CONCURRENT_VOICE_DATA_NOT_ALLOWED(true),
        DATA_RESTRICTED_BY_NETWORK(true),
        RADIO_POWER_OFF(true),
        PENDING_TEAR_DOWN_ALL(true),
        RADIO_DISABLED_BY_CARRIER(true),
        DATA_SERVICE_NOT_READY(true),
        NO_SUITABLE_DATA_PROFILE(true),
        DATA_NETWORK_TYPE_NOT_ALLOWED(true),
        CDMA_EMERGENCY_CALLBACK_MODE(true),
        RETRY_SCHEDULED(true),
        DATA_THROTTLED(true),
        DATA_PROFILE_INVALID(true),
        DATA_PROFILE_NOT_PREFERRED(true),
        NOT_ALLOWED_BY_POLICY(true),
        ILLEGAL_STATE(true),
        VOPS_NOT_SUPPORTED(true),
        ONLY_ALLOWED_SINGLE_NETWORK(true),
        DATA_SETTINGS_NOT_READY(true),
        HANDOVER_RETRY_STOPPED(true);
        
        private final boolean mIsHardReason;

        public boolean isHardReason() {
            return this.mIsHardReason;
        }

        DataDisallowedReason(boolean z) {
            this.mIsHardReason = z;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data evaluation: evaluation reason:" + this.mDataEvaluationReason + ", ");
        if (this.mDataDisallowedReasons.size() > 0) {
            sb.append("Data disallowed reasons:");
            for (DataDisallowedReason dataDisallowedReason : this.mDataDisallowedReasons) {
                sb.append(" ");
                sb.append(dataDisallowedReason);
            }
        } else {
            sb.append("Data allowed reason:");
            sb.append(" ");
            sb.append(this.mDataAllowedReason);
        }
        sb.append(", candidate profile=" + this.mCandidateDataProfile);
        sb.append(", time=" + DataUtils.systemTimeToString(this.mEvaluatedTime));
        return sb.toString();
    }
}
