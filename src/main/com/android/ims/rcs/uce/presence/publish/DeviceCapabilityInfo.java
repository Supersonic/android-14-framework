package com.android.ims.rcs.uce.presence.publish;

import android.content.Context;
import android.net.Uri;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.feature.MmTelFeature;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Duplex;
import com.android.ims.rcs.uce.util.FeatureTags;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DeviceCapabilityInfo {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "DeviceCapabilityInfo";
    private MmTelFeature.MmTelCapabilities mMmTelCapabilities;
    private int mMmtelNetworkRegType;
    private boolean mMmtelRegistered;
    private boolean mMobileData;
    private Set<ServiceDescription> mPendingPublishCapabilities;
    private boolean mPresenceCapable;
    private int mRcsNetworkRegType;
    private boolean mRcsRegistered;
    private PublishServiceDescTracker mServiceCapRegTracker;
    private final int mSubId;
    private int mTtyPreferredMode;
    private boolean mVtSetting;
    private final LocalLog mLocalLog = new LocalLog(20);
    private final Set<String> mOverrideAddFeatureTags = new ArraySet();
    private final Set<String> mOverrideRemoveFeatureTags = new ArraySet();
    private Set<String> mLastRegistrationFeatureTags = Collections.emptySet();
    private Set<String> mLastRegistrationOverrideFeatureTags = Collections.emptySet();
    private List<Uri> mMmtelAssociatedUris = Collections.emptyList();
    private List<Uri> mRcsAssociatedUris = Collections.emptyList();
    private final Set<ServiceDescription> mLastSuccessfulCapabilities = new ArraySet();

    public DeviceCapabilityInfo(int subId, String[] capToRegistrationMap) {
        this.mSubId = subId;
        this.mServiceCapRegTracker = PublishServiceDescTracker.fromCarrierConfig(capToRegistrationMap);
        reset();
    }

    public synchronized void reset() {
        logd("reset");
        this.mMmtelRegistered = false;
        this.mMmtelNetworkRegType = -1;
        this.mRcsRegistered = false;
        this.mRcsNetworkRegType = -1;
        this.mTtyPreferredMode = 0;
        this.mMobileData = true;
        this.mVtSetting = true;
        this.mMmTelCapabilities = new MmTelFeature.MmTelCapabilities();
        this.mMmtelAssociatedUris = Collections.EMPTY_LIST;
        this.mRcsAssociatedUris = Collections.EMPTY_LIST;
        this.mLastSuccessfulCapabilities.clear();
        this.mPendingPublishCapabilities = null;
    }

    public synchronized boolean updateCapabilityRegistrationTrackerMap(String[] newMap) {
        boolean changed;
        Set<String> oldTags = this.mServiceCapRegTracker.copyRegistrationFeatureTags();
        PublishServiceDescTracker fromCarrierConfig = PublishServiceDescTracker.fromCarrierConfig(newMap);
        this.mServiceCapRegTracker = fromCarrierConfig;
        fromCarrierConfig.updateImsRegistration(this.mLastRegistrationOverrideFeatureTags);
        changed = !oldTags.equals(this.mServiceCapRegTracker.copyRegistrationFeatureTags());
        if (changed) {
            logi("Carrier Config Change resulted in associated FT list change");
        }
        return changed;
    }

    public synchronized boolean isImsRegistered() {
        boolean z;
        if (!this.mMmtelRegistered) {
            z = this.mRcsRegistered;
        }
        return z;
    }

    public synchronized void updateImsMmtelRegistered(int type) {
        StringBuilder builder = new StringBuilder();
        builder.append("IMS MMTEL registered: original state=").append(this.mMmtelRegistered).append(", changes type from ").append(this.mMmtelNetworkRegType).append(" to ").append(type);
        logi(builder.toString());
        if (!this.mMmtelRegistered) {
            this.mMmtelRegistered = true;
        }
        if (this.mMmtelNetworkRegType != type) {
            this.mMmtelNetworkRegType = type;
        }
    }

    public synchronized boolean updateImsMmtelUnregistered() {
        boolean changed;
        logi("IMS MMTEL unregistered: original state=" + this.mMmtelRegistered);
        changed = false;
        if (this.mMmtelRegistered) {
            this.mMmtelRegistered = false;
            changed = true;
        }
        this.mMmtelNetworkRegType = -1;
        this.mLastSuccessfulCapabilities.clear();
        this.mPendingPublishCapabilities = null;
        return changed;
    }

    public synchronized void updateMmTelAssociatedUri(Uri[] uris) {
        int originalSize = this.mMmtelAssociatedUris.size();
        if (uris != null) {
            this.mMmtelAssociatedUris = (List) Arrays.stream(uris).filter(new DeviceCapabilityInfo$$ExternalSyntheticLambda1()).collect(Collectors.toList());
        } else {
            this.mMmtelAssociatedUris.clear();
        }
        int currentSize = this.mMmtelAssociatedUris.size();
        logd("updateMmTelAssociatedUri: size from " + originalSize + " to " + currentSize);
    }

    public synchronized Uri getMmtelAssociatedUri() {
        if (this.mMmtelAssociatedUris.isEmpty()) {
            return null;
        }
        return this.mMmtelAssociatedUris.get(0);
    }

    public synchronized boolean updateImsRcsRegistered(ImsRegistrationAttributes attr) {
        boolean changed;
        Set<String> featureTags;
        StringBuilder builder = new StringBuilder();
        builder.append("IMS RCS registered: original state=").append(this.mRcsRegistered).append(", changes type from ").append(this.mRcsNetworkRegType).append(" to ").append(attr.getTransportType());
        logi(builder.toString());
        changed = false;
        if (!this.mRcsRegistered) {
            this.mRcsRegistered = true;
            changed = true;
        }
        if (this.mRcsNetworkRegType != attr.getTransportType()) {
            this.mRcsNetworkRegType = attr.getTransportType();
            changed = true;
        }
        featureTags = attr.getFeatureTags();
        this.mLastRegistrationFeatureTags = featureTags;
        return changed | updateRegistration(featureTags);
    }

    public synchronized boolean updateImsRcsUnregistered() {
        boolean changed;
        logi("IMS RCS unregistered: original state=" + this.mRcsRegistered);
        changed = false;
        if (this.mRcsRegistered) {
            this.mRcsRegistered = false;
            changed = true;
        }
        Set<String> emptySet = Collections.emptySet();
        this.mLastRegistrationFeatureTags = emptySet;
        updateRegistration(emptySet);
        this.mRcsNetworkRegType = -1;
        this.mLastSuccessfulCapabilities.clear();
        this.mPendingPublishCapabilities = null;
        return changed;
    }

    public synchronized void updateRcsAssociatedUri(Uri[] uris) {
        int originalSize = this.mRcsAssociatedUris.size();
        if (uris != null) {
            this.mRcsAssociatedUris = (List) Arrays.stream(uris).filter(new DeviceCapabilityInfo$$ExternalSyntheticLambda1()).collect(Collectors.toList());
        } else {
            this.mRcsAssociatedUris.clear();
        }
        int currentSize = this.mRcsAssociatedUris.size();
        logd("updateRcsAssociatedUri: size from " + originalSize + " to " + currentSize);
    }

    public synchronized Uri getRcsAssociatedUri() {
        if (this.mRcsAssociatedUris.isEmpty()) {
            return null;
        }
        return this.mRcsAssociatedUris.get(0);
    }

    public synchronized Uri getImsAssociatedUri(boolean preferTelUri) {
        if (preferTelUri) {
            if (!this.mRcsAssociatedUris.isEmpty()) {
                for (Uri rcsAssociatedUri : this.mRcsAssociatedUris) {
                    if ("tel".equalsIgnoreCase(rcsAssociatedUri.getScheme())) {
                        return rcsAssociatedUri;
                    }
                }
            }
            if (!this.mMmtelAssociatedUris.isEmpty()) {
                for (Uri mmtelAssociatedUri : this.mMmtelAssociatedUris) {
                    if ("tel".equalsIgnoreCase(mmtelAssociatedUri.getScheme())) {
                        return mmtelAssociatedUri;
                    }
                }
            }
        }
        if (!this.mRcsAssociatedUris.isEmpty()) {
            return this.mRcsAssociatedUris.get(0);
        } else if (this.mMmtelAssociatedUris.isEmpty()) {
            return null;
        } else {
            return this.mMmtelAssociatedUris.get(0);
        }
    }

    public synchronized boolean addRegistrationOverrideCapabilities(Set<String> featureTags) {
        logd("override - add: " + featureTags);
        this.mOverrideRemoveFeatureTags.removeAll(featureTags);
        this.mOverrideAddFeatureTags.addAll(featureTags);
        return updateRegistration(this.mLastRegistrationFeatureTags);
    }

    public synchronized boolean removeRegistrationOverrideCapabilities(Set<String> featureTags) {
        logd("override - remove: " + featureTags);
        this.mOverrideAddFeatureTags.removeAll(featureTags);
        this.mOverrideRemoveFeatureTags.addAll(featureTags);
        return updateRegistration(this.mLastRegistrationFeatureTags);
    }

    public synchronized boolean clearRegistrationOverrideCapabilities() {
        logd("override - clear");
        this.mOverrideAddFeatureTags.clear();
        this.mOverrideRemoveFeatureTags.clear();
        return updateRegistration(this.mLastRegistrationFeatureTags);
    }

    private boolean updateRegistration(Set<String> baseTags) {
        Set<String> updatedTags = updateImsRegistrationFeatureTags(baseTags);
        if (!this.mLastRegistrationOverrideFeatureTags.equals(updatedTags)) {
            this.mLastRegistrationOverrideFeatureTags = updatedTags;
            this.mServiceCapRegTracker.updateImsRegistration(updatedTags);
            return true;
        }
        return false;
    }

    private synchronized Set<String> updateImsRegistrationFeatureTags(Set<String> featureTags) {
        Set<String> tags;
        tags = new ArraySet<>(featureTags);
        tags.addAll(this.mOverrideAddFeatureTags);
        tags.removeAll(this.mOverrideRemoveFeatureTags);
        return tags;
    }

    public synchronized boolean updateTtyPreferredMode(int ttyMode) {
        if (this.mTtyPreferredMode != ttyMode) {
            logd("TTY preferred mode changes from " + this.mTtyPreferredMode + " to " + ttyMode);
            this.mTtyPreferredMode = ttyMode;
            return true;
        }
        return false;
    }

    public synchronized boolean updateMobileData(boolean mobileData) {
        if (this.mMobileData != mobileData) {
            logd("Mobile data changes from " + this.mMobileData + " to " + mobileData);
            this.mMobileData = mobileData;
            return true;
        }
        return false;
    }

    public synchronized boolean updateVtSetting(boolean vtSetting) {
        if (this.mVtSetting != vtSetting) {
            logd("VT setting changes from " + this.mVtSetting + " to " + vtSetting);
            this.mVtSetting = vtSetting;
            return true;
        }
        return false;
    }

    public synchronized boolean updateMmtelCapabilitiesChanged(MmTelFeature.MmTelCapabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        boolean oldVolteAvailable = isVolteAvailable(this.mMmtelNetworkRegType, this.mMmTelCapabilities);
        boolean oldVoWifiAvailable = isVoWifiAvailable(this.mMmtelNetworkRegType, this.mMmTelCapabilities);
        boolean oldVtAvailable = isVtAvailable(this.mMmtelNetworkRegType, this.mMmTelCapabilities);
        boolean oldViWifiAvailable = isViWifiAvailable(this.mMmtelNetworkRegType, this.mMmTelCapabilities);
        boolean oldCallComposerAvailable = isCallComposerAvailable(this.mMmTelCapabilities);
        boolean volteAvailable = isVolteAvailable(this.mMmtelNetworkRegType, capabilities);
        boolean voWifiAvailable = isVoWifiAvailable(this.mMmtelNetworkRegType, capabilities);
        boolean vtAvailable = isVtAvailable(this.mMmtelNetworkRegType, capabilities);
        boolean viWifiAvailable = isViWifiAvailable(this.mMmtelNetworkRegType, capabilities);
        boolean callComposerAvailable = isCallComposerAvailable(capabilities);
        logd("updateMmtelCapabilitiesChanged: from " + this.mMmTelCapabilities + " to " + capabilities);
        this.mMmTelCapabilities = deepCopyCapabilities(capabilities);
        return (oldVolteAvailable == volteAvailable && oldVoWifiAvailable == voWifiAvailable && oldVtAvailable == vtAvailable && oldViWifiAvailable == viWifiAvailable && oldCallComposerAvailable == callComposerAvailable) ? false : true;
    }

    public synchronized void updatePresenceCapable(boolean isCapable) {
        this.mPresenceCapable = isCapable;
    }

    public synchronized boolean isPresenceCapable() {
        return this.mPresenceCapable;
    }

    public RcsContactUceCapability getChangedPresenceCapability(Context context) {
        if (context == null) {
            return null;
        }
        Set<ServiceDescription> capableFromReg = this.mServiceCapRegTracker.copyRegistrationCapabilities();
        if (!isPresenceCapabilityChanged(capableFromReg)) {
            return null;
        }
        RcsContactUceCapability rcsContactUceCapability = getPresenceCapabilities(context);
        if (rcsContactUceCapability != null) {
            this.mPendingPublishCapabilities = this.mServiceCapRegTracker.copyRegistrationCapabilities();
        }
        return rcsContactUceCapability;
    }

    public void setPresencePublishResult(boolean isSuccess) {
        if (isSuccess) {
            this.mLastSuccessfulCapabilities.clear();
            Set<ServiceDescription> set = this.mPendingPublishCapabilities;
            if (set != null) {
                this.mLastSuccessfulCapabilities.addAll(set);
            }
        }
        this.mPendingPublishCapabilities = null;
    }

    public void resetPresenceCapability() {
        this.mLastSuccessfulCapabilities.clear();
        this.mPendingPublishCapabilities = null;
    }

    public List<RcsContactPresenceTuple> getLastSuccessfulPresenceTuplesWithoutContactUri() {
        List<RcsContactPresenceTuple> presenceTuples = new ArrayList<>();
        if (this.mLastSuccessfulCapabilities.isEmpty()) {
            return presenceTuples;
        }
        for (ServiceDescription capability : this.mLastSuccessfulCapabilities) {
            presenceTuples.add(capability.getTupleBuilder().build());
        }
        return presenceTuples;
    }

    public void addLastSuccessfulServiceDescription(ServiceDescription capability) {
        this.mLastSuccessfulCapabilities.add(capability);
    }

    public boolean isPresenceCapabilityChanged(Set<ServiceDescription> capableFromReg) {
        return this.mLastSuccessfulCapabilities.isEmpty() || !capableFromReg.equals(this.mLastSuccessfulCapabilities);
    }

    private boolean isVolteAvailable(int networkRegType, MmTelFeature.MmTelCapabilities capabilities) {
        return networkRegType == 1 && capabilities.isCapable(1);
    }

    private boolean isVoWifiAvailable(int networkRegType, MmTelFeature.MmTelCapabilities capabilities) {
        return networkRegType == 2 && capabilities.isCapable(1);
    }

    private boolean isVtAvailable(int networkRegType, MmTelFeature.MmTelCapabilities capabilities) {
        return networkRegType == 1 && capabilities.isCapable(2);
    }

    private boolean isViWifiAvailable(int networkRegType, MmTelFeature.MmTelCapabilities capabilities) {
        return networkRegType == 2 && capabilities.isCapable(2);
    }

    private boolean isCallComposerAvailable(MmTelFeature.MmTelCapabilities capabilities) {
        return capabilities.isCapable(16);
    }

    public synchronized RcsContactUceCapability getDeviceCapabilities(int mechanism, Context context) {
        switch (mechanism) {
            case 1:
                RcsContactUceCapability rcsContactUceCapability = getPresenceCapabilities(context);
                if (rcsContactUceCapability != null) {
                    this.mPendingPublishCapabilities = this.mServiceCapRegTracker.copyRegistrationCapabilities();
                }
                return rcsContactUceCapability;
            case 2:
                return getOptionsCapabilities(context);
            default:
                logw("getDeviceCapabilities: invalid mechanism " + mechanism);
                return null;
        }
    }

    private RcsContactUceCapability getPresenceCapabilities(Context context) {
        Uri uri = PublishUtils.getDeviceContactUri(context, this.mSubId, this, true);
        if (uri == null) {
            logw("getPresenceCapabilities: uri is empty");
            return null;
        }
        Set<ServiceDescription> capableFromReg = this.mServiceCapRegTracker.copyRegistrationCapabilities();
        RcsContactUceCapability.PresenceBuilder presenceBuilder = new RcsContactUceCapability.PresenceBuilder(uri, 1, 3);
        ServiceDescription presDescription = getCustomizedDescription(ServiceDescription.SERVICE_DESCRIPTION_PRESENCE, capableFromReg);
        addCapability(presenceBuilder, presDescription.getTupleBuilder(), uri);
        capableFromReg.remove(presDescription);
        ServiceDescription voiceDescription = getCustomizedDescription(ServiceDescription.SERVICE_DESCRIPTION_MMTEL_VOICE, capableFromReg);
        ServiceDescription vtDescription = getCustomizedDescription(ServiceDescription.SERVICE_DESCRIPTION_MMTEL_VOICE_VIDEO, capableFromReg);
        ServiceDescription descToUse = (hasVolteCapability() && hasVtCapability()) ? vtDescription : voiceDescription;
        RcsContactPresenceTuple.ServiceCapabilities servCaps = new RcsContactPresenceTuple.ServiceCapabilities.Builder(hasVolteCapability(), hasVtCapability()).addSupportedDuplexMode(Duplex.DUPLEX_FULL).build();
        addCapability(presenceBuilder, descToUse.getTupleBuilder().setServiceCapabilities(servCaps), uri);
        capableFromReg.remove(voiceDescription);
        capableFromReg.remove(vtDescription);
        ServiceDescription composerDescription = getCustomizedDescription(ServiceDescription.SERVICE_DESCRIPTION_CALL_COMPOSER_MMTEL, capableFromReg);
        if (hasCallComposerCapability()) {
            addCapability(presenceBuilder, composerDescription.getTupleBuilder(), uri);
        }
        capableFromReg.remove(composerDescription);
        for (ServiceDescription capability : capableFromReg) {
            addCapability(presenceBuilder, capability.getTupleBuilder(), uri);
        }
        return presenceBuilder.build();
    }

    private ServiceDescription getCustomizedDescription(final ServiceDescription reference, Set<ServiceDescription> refSet) {
        return refSet.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityInfo$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return DeviceCapabilityInfo.lambda$getCustomizedDescription$0(ServiceDescription.this, (ServiceDescription) obj);
            }
        }).findFirst().orElse(reference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getCustomizedDescription$0(ServiceDescription reference, ServiceDescription s) {
        return s.serviceId.equals(reference.serviceId) && s.version.equals(reference.version);
    }

    private RcsContactUceCapability getOptionsCapabilities(Context context) {
        Uri uri = PublishUtils.getDeviceContactUri(context, this.mSubId, this, false);
        if (uri == null) {
            logw("getOptionsCapabilities: uri is empty");
            return null;
        }
        Set<String> capableFromReg = this.mServiceCapRegTracker.copyRegistrationFeatureTags();
        RcsContactUceCapability.OptionsBuilder optionsBuilder = new RcsContactUceCapability.OptionsBuilder(uri, 1);
        optionsBuilder.setRequestResult(3);
        FeatureTags.addFeatureTags(optionsBuilder, hasVolteCapability(), hasVtCapability(), isPresenceCapable(), hasCallComposerCapability(), capableFromReg);
        return optionsBuilder.build();
    }

    private void addCapability(RcsContactUceCapability.PresenceBuilder presenceBuilder, RcsContactPresenceTuple.Builder tupleBuilder, Uri contactUri) {
        presenceBuilder.addCapabilityTuple(tupleBuilder.setContactUri(contactUri).build());
    }

    private synchronized boolean hasVolteCapability() {
        boolean z;
        MmTelFeature.MmTelCapabilities mmTelCapabilities = this.mMmTelCapabilities;
        if (mmTelCapabilities != null) {
            z = true;
            if (mmTelCapabilities.isCapable(1)) {
            }
        }
        z = false;
        return overrideCapability(FeatureTags.FEATURE_TAG_MMTEL, z);
    }

    private synchronized boolean hasVtCapability() {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        mmTelCapabilities = this.mMmTelCapabilities;
        return overrideCapability("video", mmTelCapabilities != null && mmTelCapabilities.isCapable(2));
    }

    private synchronized boolean hasCallComposerCapability() {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        mmTelCapabilities = this.mMmTelCapabilities;
        return overrideCapability(FeatureTags.FEATURE_TAG_CALL_COMPOSER_VIA_TELEPHONY, mmTelCapabilities != null && mmTelCapabilities.isCapable(16));
    }

    private synchronized boolean overrideCapability(String featureTag, boolean originalCap) {
        if (this.mOverrideRemoveFeatureTags.contains(featureTag)) {
            return false;
        }
        if (this.mOverrideAddFeatureTags.contains(featureTag)) {
            return true;
        }
        return originalCap;
    }

    private synchronized MmTelFeature.MmTelCapabilities deepCopyCapabilities(MmTelFeature.MmTelCapabilities capabilities) {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        mmTelCapabilities = new MmTelFeature.MmTelCapabilities();
        if (capabilities.isCapable(1)) {
            mmTelCapabilities.addCapabilities(1);
        }
        if (capabilities.isCapable(2)) {
            mmTelCapabilities.addCapabilities(2);
        }
        if (capabilities.isCapable(4)) {
            mmTelCapabilities.addCapabilities(4);
        }
        if (capabilities.isCapable(8)) {
            mmTelCapabilities.addCapabilities(8);
        }
        if (capabilities.isCapable(16)) {
            mmTelCapabilities.addCapabilities(16);
        }
        return mmTelCapabilities;
    }

    private void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[D] " + log);
    }

    private void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[I] " + log);
    }

    private void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[W] " + log);
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }

    public void dump(PrintWriter printWriter) {
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("DeviceCapabilityInfo :");
        indentingPrintWriter.increaseIndent();
        this.mServiceCapRegTracker.dump(indentingPrintWriter);
        indentingPrintWriter.println("Log:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }
}
