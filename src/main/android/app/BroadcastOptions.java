package android.app;

import android.annotation.SystemApi;
import android.app.compat.CompatChanges;
import android.content.IntentFilter;
import android.p008os.Bundle;
import android.p008os.BundleMerger;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public class BroadcastOptions extends ComponentOptions {
    public static final long CHANGE_ALWAYS_DISABLED = 210856463;
    public static final long CHANGE_ALWAYS_ENABLED = 209888056;
    public static final long CHANGE_INVALID = Long.MIN_VALUE;
    public static final int DEFERRAL_POLICY_DEFAULT = 0;
    public static final int DEFERRAL_POLICY_NONE = 1;
    public static final int DEFERRAL_POLICY_UNTIL_ACTIVE = 2;
    public static final int DELIVERY_GROUP_POLICY_ALL = 0;
    public static final int DELIVERY_GROUP_POLICY_MERGED = 2;
    public static final int DELIVERY_GROUP_POLICY_MOST_RECENT = 1;
    private static final int FLAG_ALLOW_BACKGROUND_ACTIVITY_STARTS = 2;
    private static final int FLAG_DONT_SEND_TO_RESTRICTED_APPS = 1;
    private static final int FLAG_INTERACTIVE = 32;
    private static final int FLAG_IS_ALARM_BROADCAST = 8;
    private static final int FLAG_REQUIRE_COMPAT_CHANGE_ENABLED = 4;
    private static final int FLAG_SHARE_IDENTITY = 16;
    private static final String KEY_DEFERRAL_POLICY = "android:broadcast.deferralPolicy";
    private static final String KEY_DELIVERY_GROUP_EXTRAS_MERGER = "android:broadcast.deliveryGroupExtrasMerger";
    private static final String KEY_DELIVERY_GROUP_KEY = "android:broadcast.deliveryGroupMatchingKey";
    private static final String KEY_DELIVERY_GROUP_MATCHING_FILTER = "android:broadcast.deliveryGroupMatchingFilter";
    private static final String KEY_DELIVERY_GROUP_POLICY = "android:broadcast.deliveryGroupPolicy";
    private static final String KEY_FLAGS = "android:broadcast.flags";
    private static final String KEY_ID_FOR_RESPONSE_EVENT = "android:broadcast.idForResponseEvent";
    private static final String KEY_MAX_MANIFEST_RECEIVER_API_LEVEL = "android:broadcast.maxManifestReceiverApiLevel";
    private static final String KEY_MIN_MANIFEST_RECEIVER_API_LEVEL = "android:broadcast.minManifestReceiverApiLevel";
    public static final String KEY_REQUIRE_ALL_OF_PERMISSIONS = "android:broadcast.requireAllOfPermissions";
    private static final String KEY_REQUIRE_COMPAT_CHANGE_ID = "android:broadcast.requireCompatChangeId";
    public static final String KEY_REQUIRE_NONE_OF_PERMISSIONS = "android:broadcast.requireNoneOfPermissions";
    private static final String KEY_TEMPORARY_APP_ALLOWLIST_DURATION = "android:broadcast.temporaryAppAllowlistDuration";
    private static final String KEY_TEMPORARY_APP_ALLOWLIST_REASON = "android:broadcast.temporaryAppAllowlistReason";
    private static final String KEY_TEMPORARY_APP_ALLOWLIST_REASON_CODE = "android:broadcast.temporaryAppAllowlistReasonCode";
    private static final String KEY_TEMPORARY_APP_ALLOWLIST_TYPE = "android:broadcast.temporaryAppAllowlistType";
    @Deprecated
    public static final int TEMPORARY_WHITELIST_TYPE_FOREGROUND_SERVICE_ALLOWED = 0;
    @Deprecated
    public static final int TEMPORARY_WHITELIST_TYPE_FOREGROUND_SERVICE_NOT_ALLOWED = 1;
    private int mDeferralPolicy;
    private BundleMerger mDeliveryGroupExtrasMerger;
    private IntentFilter mDeliveryGroupMatchingFilter;
    private String mDeliveryGroupMatchingKey;
    private int mDeliveryGroupPolicy;
    private int mFlags;
    private long mIdForResponseEvent;
    private int mMaxManifestReceiverApiLevel;
    private int mMinManifestReceiverApiLevel;
    private String[] mRequireAllOfPermissions;
    private long mRequireCompatChangeId;
    private String[] mRequireNoneOfPermissions;
    private long mTemporaryAppAllowlistDuration;
    private String mTemporaryAppAllowlistReason;
    private int mTemporaryAppAllowlistReasonCode;
    private int mTemporaryAppAllowlistType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DeferralPolicy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DeliveryGroupPolicy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public static BroadcastOptions makeBasic() {
        BroadcastOptions opts = new BroadcastOptions();
        return opts;
    }

    public BroadcastOptions() {
        this.mMinManifestReceiverApiLevel = 0;
        this.mMaxManifestReceiverApiLevel = 10000;
        this.mRequireCompatChangeId = Long.MIN_VALUE;
        resetTemporaryAppAllowlist();
    }

    public BroadcastOptions(Bundle opts) {
        super(opts);
        this.mMinManifestReceiverApiLevel = 0;
        this.mMaxManifestReceiverApiLevel = 10000;
        this.mRequireCompatChangeId = Long.MIN_VALUE;
        this.mFlags = opts.getInt(KEY_FLAGS, 0);
        if (opts.containsKey(KEY_TEMPORARY_APP_ALLOWLIST_DURATION)) {
            this.mTemporaryAppAllowlistDuration = opts.getLong(KEY_TEMPORARY_APP_ALLOWLIST_DURATION);
            this.mTemporaryAppAllowlistType = opts.getInt(KEY_TEMPORARY_APP_ALLOWLIST_TYPE);
            this.mTemporaryAppAllowlistReasonCode = opts.getInt(KEY_TEMPORARY_APP_ALLOWLIST_REASON_CODE, 0);
            this.mTemporaryAppAllowlistReason = opts.getString(KEY_TEMPORARY_APP_ALLOWLIST_REASON);
        } else {
            resetTemporaryAppAllowlist();
        }
        this.mMinManifestReceiverApiLevel = opts.getInt(KEY_MIN_MANIFEST_RECEIVER_API_LEVEL, 0);
        this.mMaxManifestReceiverApiLevel = opts.getInt(KEY_MAX_MANIFEST_RECEIVER_API_LEVEL, 10000);
        this.mRequireAllOfPermissions = opts.getStringArray(KEY_REQUIRE_ALL_OF_PERMISSIONS);
        this.mRequireNoneOfPermissions = opts.getStringArray(KEY_REQUIRE_NONE_OF_PERMISSIONS);
        this.mRequireCompatChangeId = opts.getLong(KEY_REQUIRE_COMPAT_CHANGE_ID, Long.MIN_VALUE);
        this.mIdForResponseEvent = opts.getLong(KEY_ID_FOR_RESPONSE_EVENT);
        this.mDeliveryGroupPolicy = opts.getInt(KEY_DELIVERY_GROUP_POLICY, 0);
        this.mDeliveryGroupMatchingKey = opts.getString(KEY_DELIVERY_GROUP_KEY);
        this.mDeliveryGroupExtrasMerger = (BundleMerger) opts.getParcelable(KEY_DELIVERY_GROUP_EXTRAS_MERGER, BundleMerger.class);
        this.mDeliveryGroupMatchingFilter = (IntentFilter) opts.getParcelable(KEY_DELIVERY_GROUP_MATCHING_FILTER, IntentFilter.class);
        this.mDeferralPolicy = opts.getInt(KEY_DEFERRAL_POLICY, 0);
    }

    @SystemApi
    @Deprecated
    public void setTemporaryAppWhitelistDuration(long duration) {
        setTemporaryAppAllowlist(duration, 0, 0, null);
    }

    @SystemApi
    public void setTemporaryAppAllowlist(long duration, int type, int reasonCode, String reason) {
        this.mTemporaryAppAllowlistDuration = duration;
        this.mTemporaryAppAllowlistType = type;
        this.mTemporaryAppAllowlistReasonCode = reasonCode;
        this.mTemporaryAppAllowlistReason = reason;
        if (!isTemporaryAppAllowlistSet()) {
            resetTemporaryAppAllowlist();
        }
    }

    private boolean isTemporaryAppAllowlistSet() {
        return this.mTemporaryAppAllowlistDuration > 0 && this.mTemporaryAppAllowlistType != -1;
    }

    private void resetTemporaryAppAllowlist() {
        this.mTemporaryAppAllowlistDuration = 0L;
        this.mTemporaryAppAllowlistType = -1;
        this.mTemporaryAppAllowlistReasonCode = 0;
        this.mTemporaryAppAllowlistReason = null;
    }

    public long getTemporaryAppAllowlistDuration() {
        return this.mTemporaryAppAllowlistDuration;
    }

    public int getTemporaryAppAllowlistType() {
        return this.mTemporaryAppAllowlistType;
    }

    public int getTemporaryAppAllowlistReasonCode() {
        return this.mTemporaryAppAllowlistReasonCode;
    }

    public String getTemporaryAppAllowlistReason() {
        return this.mTemporaryAppAllowlistReason;
    }

    @Deprecated
    public void setMinManifestReceiverApiLevel(int apiLevel) {
        this.mMinManifestReceiverApiLevel = apiLevel;
    }

    @Deprecated
    public int getMinManifestReceiverApiLevel() {
        return this.mMinManifestReceiverApiLevel;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    @Deprecated
    public void setMaxManifestReceiverApiLevel(int apiLevel) {
        this.mMaxManifestReceiverApiLevel = apiLevel;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    @Deprecated
    public int getMaxManifestReceiverApiLevel() {
        return this.mMaxManifestReceiverApiLevel;
    }

    @SystemApi
    public void setDontSendToRestrictedApps(boolean dontSendToRestrictedApps) {
        if (dontSendToRestrictedApps) {
            this.mFlags |= 1;
        } else {
            this.mFlags &= -2;
        }
    }

    public boolean isDontSendToRestrictedApps() {
        return (this.mFlags & 1) != 0;
    }

    @SystemApi
    public void setBackgroundActivityStartsAllowed(boolean allowBackgroundActivityStarts) {
        if (allowBackgroundActivityStarts) {
            this.mFlags |= 2;
        } else {
            this.mFlags &= -3;
        }
    }

    @Deprecated
    public boolean allowsBackgroundActivityStarts() {
        return (this.mFlags & 2) != 0;
    }

    @SystemApi
    public void setRequireAllOfPermissions(String[] requiredPermissions) {
        this.mRequireAllOfPermissions = requiredPermissions;
    }

    @SystemApi
    public void setRequireNoneOfPermissions(String[] excludedPermissions) {
        this.mRequireNoneOfPermissions = excludedPermissions;
    }

    @SystemApi
    public void setRequireCompatChange(long changeId, boolean enabled) {
        this.mRequireCompatChangeId = changeId;
        if (enabled) {
            this.mFlags |= 4;
        } else {
            this.mFlags &= -5;
        }
    }

    @SystemApi
    public void clearRequireCompatChange() {
        setRequireCompatChange(Long.MIN_VALUE, true);
    }

    public void setAlarmBroadcast(boolean senderIsAlarm) {
        if (senderIsAlarm) {
            this.mFlags |= 8;
        } else {
            this.mFlags &= -9;
        }
    }

    public boolean isAlarmBroadcast() {
        return (this.mFlags & 8) != 0;
    }

    public BroadcastOptions setShareIdentityEnabled(boolean shareIdentityEnabled) {
        if (shareIdentityEnabled) {
            this.mFlags |= 16;
        } else {
            this.mFlags &= -17;
        }
        return this;
    }

    public boolean isShareIdentityEnabled() {
        return (this.mFlags & 16) != 0;
    }

    public boolean isPushMessagingBroadcast() {
        return this.mTemporaryAppAllowlistReasonCode == 101;
    }

    public boolean isPushMessagingOverQuotaBroadcast() {
        return this.mTemporaryAppAllowlistReasonCode == 102;
    }

    public long getRequireCompatChangeId() {
        return this.mRequireCompatChangeId;
    }

    public boolean testRequireCompatChange(int uid) {
        long j = this.mRequireCompatChangeId;
        if (j != Long.MIN_VALUE) {
            boolean requireEnabled = (this.mFlags & 4) != 0;
            return CompatChanges.isChangeEnabled(j, uid) == requireEnabled;
        }
        return true;
    }

    @SystemApi
    public void recordResponseEventWhileInBackground(long id) {
        this.mIdForResponseEvent = id;
    }

    public long getIdForResponseEvent() {
        return this.mIdForResponseEvent;
    }

    @SystemApi
    @Deprecated
    public BroadcastOptions setDeferUntilActive(boolean shouldDefer) {
        if (shouldDefer) {
            setDeferralPolicy(2);
        } else {
            setDeferralPolicy(1);
        }
        return this;
    }

    @SystemApi
    @Deprecated
    public boolean isDeferUntilActive() {
        return this.mDeferralPolicy == 2;
    }

    public BroadcastOptions setDeferralPolicy(int deferralPolicy) {
        this.mDeferralPolicy = deferralPolicy;
        return this;
    }

    public int getDeferralPolicy() {
        return this.mDeferralPolicy;
    }

    public void clearDeferralPolicy() {
        this.mDeferralPolicy = 0;
    }

    public BroadcastOptions setDeliveryGroupPolicy(int policy) {
        this.mDeliveryGroupPolicy = policy;
        return this;
    }

    public int getDeliveryGroupPolicy() {
        return this.mDeliveryGroupPolicy;
    }

    public void clearDeliveryGroupPolicy() {
        this.mDeliveryGroupPolicy = 0;
    }

    public BroadcastOptions setDeliveryGroupMatchingKey(String namespace, String key) {
        Preconditions.checkArgument(!namespace.contains(":"), "namespace should not contain ':'");
        Preconditions.checkArgument(!key.contains(":"), "key should not contain ':'");
        this.mDeliveryGroupMatchingKey = namespace + ":" + key;
        return this;
    }

    public String getDeliveryGroupMatchingKey() {
        return this.mDeliveryGroupMatchingKey;
    }

    public void clearDeliveryGroupMatchingKey() {
        this.mDeliveryGroupMatchingKey = null;
    }

    public BroadcastOptions setDeliveryGroupMatchingFilter(IntentFilter matchingFilter) {
        this.mDeliveryGroupMatchingFilter = (IntentFilter) Objects.requireNonNull(matchingFilter);
        return this;
    }

    public IntentFilter getDeliveryGroupMatchingFilter() {
        return this.mDeliveryGroupMatchingFilter;
    }

    public void clearDeliveryGroupMatchingFilter() {
        this.mDeliveryGroupMatchingFilter = null;
    }

    public BroadcastOptions setDeliveryGroupExtrasMerger(BundleMerger extrasMerger) {
        this.mDeliveryGroupExtrasMerger = (BundleMerger) Objects.requireNonNull(extrasMerger);
        return this;
    }

    public BundleMerger getDeliveryGroupExtrasMerger() {
        return this.mDeliveryGroupExtrasMerger;
    }

    public void clearDeliveryGroupExtrasMerger() {
        this.mDeliveryGroupExtrasMerger = null;
    }

    public BroadcastOptions setInteractive(boolean interactive) {
        if (interactive) {
            this.mFlags |= 32;
        } else {
            this.mFlags &= -33;
        }
        return this;
    }

    public boolean isInteractive() {
        return (this.mFlags & 32) != 0;
    }

    @Override // android.app.ComponentOptions
    @SystemApi
    @Deprecated
    public void setPendingIntentBackgroundActivityLaunchAllowed(boolean allowed) {
        super.setPendingIntentBackgroundActivityLaunchAllowed(allowed);
    }

    @Override // android.app.ComponentOptions
    @SystemApi
    @Deprecated
    public boolean isPendingIntentBackgroundActivityLaunchAllowed() {
        return super.isPendingIntentBackgroundActivityLaunchAllowed();
    }

    @Override // android.app.ComponentOptions
    @SystemApi
    public BroadcastOptions setPendingIntentBackgroundActivityStartMode(int state) {
        super.setPendingIntentBackgroundActivityStartMode(state);
        return this;
    }

    @Override // android.app.ComponentOptions
    @SystemApi
    public int getPendingIntentBackgroundActivityStartMode() {
        return super.getPendingIntentBackgroundActivityStartMode();
    }

    @Override // android.app.ComponentOptions
    public Bundle toBundle() {
        Bundle b = super.toBundle();
        int i = this.mFlags;
        if (i != 0) {
            b.putInt(KEY_FLAGS, i);
        }
        if (isTemporaryAppAllowlistSet()) {
            b.putLong(KEY_TEMPORARY_APP_ALLOWLIST_DURATION, this.mTemporaryAppAllowlistDuration);
            b.putInt(KEY_TEMPORARY_APP_ALLOWLIST_TYPE, this.mTemporaryAppAllowlistType);
            b.putInt(KEY_TEMPORARY_APP_ALLOWLIST_REASON_CODE, this.mTemporaryAppAllowlistReasonCode);
            b.putString(KEY_TEMPORARY_APP_ALLOWLIST_REASON, this.mTemporaryAppAllowlistReason);
        }
        int i2 = this.mMinManifestReceiverApiLevel;
        if (i2 != 0) {
            b.putInt(KEY_MIN_MANIFEST_RECEIVER_API_LEVEL, i2);
        }
        int i3 = this.mMaxManifestReceiverApiLevel;
        if (i3 != 10000) {
            b.putInt(KEY_MAX_MANIFEST_RECEIVER_API_LEVEL, i3);
        }
        String[] strArr = this.mRequireAllOfPermissions;
        if (strArr != null) {
            b.putStringArray(KEY_REQUIRE_ALL_OF_PERMISSIONS, strArr);
        }
        String[] strArr2 = this.mRequireNoneOfPermissions;
        if (strArr2 != null) {
            b.putStringArray(KEY_REQUIRE_NONE_OF_PERMISSIONS, strArr2);
        }
        long j = this.mRequireCompatChangeId;
        if (j != Long.MIN_VALUE) {
            b.putLong(KEY_REQUIRE_COMPAT_CHANGE_ID, j);
        }
        long j2 = this.mIdForResponseEvent;
        if (j2 != 0) {
            b.putLong(KEY_ID_FOR_RESPONSE_EVENT, j2);
        }
        int i4 = this.mDeliveryGroupPolicy;
        if (i4 != 0) {
            b.putInt(KEY_DELIVERY_GROUP_POLICY, i4);
        }
        String str = this.mDeliveryGroupMatchingKey;
        if (str != null) {
            b.putString(KEY_DELIVERY_GROUP_KEY, str);
        }
        if (this.mDeliveryGroupPolicy == 2) {
            BundleMerger bundleMerger = this.mDeliveryGroupExtrasMerger;
            if (bundleMerger != null) {
                b.putParcelable(KEY_DELIVERY_GROUP_EXTRAS_MERGER, bundleMerger);
            } else {
                throw new IllegalStateException("Extras merger cannot be empty when delivery group policy is 'MERGED'");
            }
        }
        IntentFilter intentFilter = this.mDeliveryGroupMatchingFilter;
        if (intentFilter != null) {
            b.putParcelable(KEY_DELIVERY_GROUP_MATCHING_FILTER, intentFilter);
        }
        int i5 = this.mDeferralPolicy;
        if (i5 != 0) {
            b.putInt(KEY_DEFERRAL_POLICY, i5);
        }
        return b;
    }

    public static BroadcastOptions fromBundle(Bundle options) {
        return new BroadcastOptions(options);
    }

    public static BroadcastOptions fromBundleNullable(Bundle options) {
        if (options != null) {
            return new BroadcastOptions(options);
        }
        return null;
    }
}
