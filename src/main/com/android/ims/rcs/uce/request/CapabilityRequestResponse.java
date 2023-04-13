package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.telephony.ims.RcsContactTerminatedReason;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParserUtils;
import com.android.ims.rcs.uce.presence.publish.PublishController;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class CapabilityRequestResponse {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "CapabilityRequestResp";
    private Optional<Integer> mRequestInternalError = Optional.empty();
    private Optional<Integer> mCommandError = Optional.empty();
    private Optional<Integer> mNetworkRespSipCode = Optional.empty();
    private Optional<String> mReasonPhrase = Optional.empty();
    private Optional<Integer> mReasonHeaderCause = Optional.empty();
    private Optional<String> mReasonHeaderText = Optional.empty();
    private Optional<String> mTerminatedReason = Optional.empty();
    private Optional<Long> mRetryAfterMillis = Optional.of(0L);
    private List<RcsContactUceCapability> mTerminatedResource = new ArrayList();
    private List<RcsContactUceCapability> mCachedCapabilityList = new ArrayList();
    private List<RcsContactUceCapability> mUpdatedCapabilityList = new ArrayList();
    private Set<String> mRemoteCaps = new HashSet();
    private Map<Uri, Boolean> mContactCapsReceived = new HashMap();
    private Optional<SipDetails> mSipDetails = Optional.empty();

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setRequestContacts$0(Uri contact) {
        this.mContactCapsReceived.put(contact, Boolean.FALSE);
    }

    public synchronized void setRequestContacts(List<Uri> contactUris) {
        contactUris.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CapabilityRequestResponse.this.lambda$setRequestContacts$0((Uri) obj);
            }
        });
        Log.d(LOG_TAG, "setRequestContacts: size=" + this.mContactCapsReceived.size());
    }

    public synchronized List<Uri> getNotReceiveCapabilityUpdatedContact() {
        return (List) this.mContactCapsReceived.entrySet().stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean equals;
                equals = Objects.equals(((Map.Entry) obj).getValue(), Boolean.FALSE);
                return equals;
            }
        }).map(new Function() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (Uri) ((Map.Entry) obj).getKey();
            }
        }).collect(Collectors.toList());
    }

    public synchronized boolean haveAllRequestCapsUpdatedBeenReceived() {
        return !this.mContactCapsReceived.containsValue(Boolean.FALSE);
    }

    public synchronized void setRequestInternalError(int errorCode) {
        this.mRequestInternalError = Optional.of(Integer.valueOf(errorCode));
    }

    public synchronized Optional<Integer> getRequestInternalError() {
        return this.mRequestInternalError;
    }

    public synchronized void setCommandError(int commandError) {
        this.mCommandError = Optional.of(Integer.valueOf(commandError));
    }

    public synchronized Optional<Integer> getCommandError() {
        return this.mCommandError;
    }

    public synchronized void setNetworkResponseCode(int sipCode, String reason) {
        this.mNetworkRespSipCode = Optional.of(Integer.valueOf(sipCode));
        this.mReasonPhrase = Optional.ofNullable(reason);
    }

    public synchronized void setSipDetails(SipDetails details) {
        setNetworkResponseCode(details.getResponseCode(), details.getResponsePhrase());
        if (details.getReasonHeaderCause() != 0) {
            this.mReasonHeaderCause = Optional.of(Integer.valueOf(details.getReasonHeaderCause()));
        } else {
            this.mReasonHeaderCause = Optional.empty();
        }
        if (TextUtils.isEmpty(details.getReasonHeaderText())) {
            this.mReasonHeaderText = Optional.empty();
        } else {
            this.mReasonHeaderText = Optional.ofNullable(details.getReasonHeaderText());
        }
        this.mSipDetails = Optional.ofNullable(details);
    }

    public synchronized Optional<Integer> getNetworkRespSipCode() {
        return this.mNetworkRespSipCode;
    }

    public synchronized Optional<String> getReasonPhrase() {
        return this.mReasonPhrase;
    }

    public synchronized Optional<Integer> getReasonHeaderCause() {
        return this.mReasonHeaderCause;
    }

    public synchronized Optional<String> getReasonHeaderText() {
        return this.mReasonHeaderText;
    }

    public Optional<Integer> getResponseSipCode() {
        if (this.mReasonHeaderCause.isPresent()) {
            return this.mReasonHeaderCause;
        }
        return this.mNetworkRespSipCode;
    }

    public Optional<String> getResponseReason() {
        if (this.mReasonPhrase.isPresent()) {
            return this.mReasonPhrase;
        }
        return this.mReasonHeaderText;
    }

    public synchronized void setTerminated(String reason, long retryAfterMillis) {
        this.mTerminatedReason = Optional.ofNullable(reason);
        this.mRetryAfterMillis = Optional.of(Long.valueOf(retryAfterMillis));
    }

    public synchronized String getTerminatedReason() {
        return this.mTerminatedReason.orElse("");
    }

    public synchronized long getRetryAfterMillis() {
        return this.mRetryAfterMillis.orElse(0L).longValue();
    }

    public Optional<SipDetails> getSipDetails() {
        return this.mSipDetails;
    }

    public synchronized void addCachedCapabilities(List<RcsContactUceCapability> capabilityList) {
        this.mCachedCapabilityList.addAll(capabilityList);
        updateCapsReceivedFlag(capabilityList);
    }

    private synchronized void updateCapsReceivedFlag(List<RcsContactUceCapability> updatedCapList) {
        for (RcsContactUceCapability updatedCap : updatedCapList) {
            Uri updatedUri = updatedCap.getContactUri();
            if (updatedUri != null) {
                String updatedUriStr = updatedUri.toString();
                for (Map.Entry<Uri, Boolean> contactCapEntry : this.mContactCapsReceived.entrySet()) {
                    String number = UceUtils.getContactNumber(contactCapEntry.getKey());
                    if (!TextUtils.isEmpty(number) && updatedUriStr.contains(number)) {
                        contactCapEntry.setValue(true);
                    }
                }
            }
        }
    }

    public synchronized void removeCachedContactCapabilities() {
        this.mCachedCapabilityList.clear();
    }

    public synchronized List<RcsContactUceCapability> getCachedContactCapability() {
        return Collections.unmodifiableList(this.mCachedCapabilityList);
    }

    public synchronized void addUpdatedCapabilities(List<RcsContactUceCapability> capabilityList) {
        this.mUpdatedCapabilityList.addAll(capabilityList);
        updateCapsReceivedFlag(capabilityList);
    }

    public synchronized void removeUpdatedCapabilities(List<RcsContactUceCapability> capList) {
        this.mUpdatedCapabilityList.removeAll(capList);
    }

    public synchronized List<RcsContactUceCapability> getUpdatedContactCapability() {
        return Collections.unmodifiableList(this.mUpdatedCapabilityList);
    }

    public synchronized void addTerminatedResource(List<RcsContactTerminatedReason> resourceList) {
        List<RcsContactUceCapability> capabilityList = (List) resourceList.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((RcsContactTerminatedReason) obj);
            }
        }).map(new Function() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                RcsContactUceCapability terminatedCapability;
                terminatedCapability = PidfParserUtils.getTerminatedCapability(r1.getContactUri(), ((RcsContactTerminatedReason) obj).getReason());
                return terminatedCapability;
            }
        }).collect(Collectors.toList());
        this.mTerminatedResource.addAll(capabilityList);
        updateCapsReceivedFlag(capabilityList);
    }

    public synchronized void removeTerminatedResources(List<RcsContactUceCapability> resourceList) {
        this.mTerminatedResource.removeAll(resourceList);
    }

    public synchronized List<RcsContactUceCapability> getTerminatedResources() {
        return Collections.unmodifiableList(this.mTerminatedResource);
    }

    public synchronized void setRemoteCapabilities(Set<String> remoteCaps) {
        if (remoteCaps != null) {
            remoteCaps.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((String) obj);
                }
            }).forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    CapabilityRequestResponse.this.lambda$setRemoteCapabilities$3((String) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setRemoteCapabilities$3(String capability) {
        this.mRemoteCaps.add(capability);
    }

    public synchronized Set<String> getRemoteCapability() {
        return Collections.unmodifiableSet(this.mRemoteCaps);
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0033, code lost:
        if (getReasonHeaderCause().filter(new com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda3()).isPresent() != false) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized boolean isNetworkResponseOK() {
        Optional<Integer> respSipCode = getNetworkRespSipCode();
        if (respSipCode.filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CapabilityRequestResponse.lambda$isNetworkResponseOK$4((Integer) obj);
            }
        }).isPresent()) {
            if (getReasonHeaderCause().isPresent()) {
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isNetworkResponseOK$4(Integer c) {
        return c.intValue() == 200 || c.intValue() == 202;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isNetworkResponseOK$5(Integer c) {
        return c.intValue() == 200;
    }

    public synchronized boolean isRequestForbidden() {
        if (getReasonHeaderCause().isPresent()) {
            return getReasonHeaderCause().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return CapabilityRequestResponse.lambda$isRequestForbidden$6((Integer) obj);
                }
            }).isPresent();
        }
        return getNetworkRespSipCode().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequestResponse$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CapabilityRequestResponse.lambda$isRequestForbidden$7((Integer) obj);
            }
        }).isPresent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isRequestForbidden$6(Integer c) {
        return c.intValue() == 403;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isRequestForbidden$7(Integer c) {
        return c.intValue() == 403;
    }

    public synchronized boolean isNotFound() {
        Optional<Integer> respSipCode = Optional.empty();
        if (getReasonHeaderCause().isPresent()) {
            respSipCode = getReasonHeaderCause();
        } else if (getNetworkRespSipCode().isPresent()) {
            respSipCode = getNetworkRespSipCode();
        }
        if (respSipCode.isPresent()) {
            int sipCode = respSipCode.get().intValue();
            if (sipCode == 404 || sipCode == 405 || sipCode == 604) {
                return true;
            }
        }
        return false;
    }

    public static int getCapabilityErrorFromCommandError(int cmdError) {
        switch (cmdError) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 7:
            case PublishController.PUBLISH_TRIGGER_RCS_REGISTERED /* 10 */:
                return 1;
            case 4:
                return 9;
            case 5:
                return 10;
            case 6:
                return 11;
            case 8:
                return 7;
            case 9:
                return 12;
            default:
                return 1;
        }
    }

    public static int getCapabilityErrorFromSipCode(CapabilityRequestResponse response) {
        int sipError;
        String respReason;
        if (response.getReasonHeaderCause().isPresent()) {
            sipError = response.getReasonHeaderCause().get().intValue();
            respReason = response.getReasonHeaderText().orElse("");
        } else {
            sipError = response.getNetworkRespSipCode().orElse(-1).intValue();
            respReason = response.getReasonPhrase().orElse("");
        }
        return NetworkSipCode.getCapabilityErrorFromSipCode(sipError, respReason, 2);
    }

    public synchronized String toString() {
        StringBuilder builder;
        builder = new StringBuilder();
        return builder.append("RequestInternalError=").append(this.mRequestInternalError.orElse(-1)).append(", CommandErrorCode=").append(this.mCommandError.orElse(-1)).append(", NetworkResponseCode=").append(this.mNetworkRespSipCode.orElse(-1)).append(", NetworkResponseReason=").append(this.mReasonPhrase.orElse("")).append(", ReasonHeaderCause=").append(this.mReasonHeaderCause.orElse(-1)).append(", ReasonHeaderText=").append(this.mReasonHeaderText.orElse("")).append(", TerminatedReason=").append(this.mTerminatedReason.orElse("")).append(", RetryAfterMillis=").append(this.mRetryAfterMillis.orElse(0L)).append(", Terminated resource size=" + this.mTerminatedResource.size()).append(", cached capability size=" + this.mCachedCapabilityList.size()).append(", Updated capability size=" + this.mUpdatedCapabilityList.size()).append(", RemoteCaps size=" + this.mRemoteCaps.size()).toString();
    }
}
