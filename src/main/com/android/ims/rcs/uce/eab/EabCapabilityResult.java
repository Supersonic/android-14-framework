package com.android.ims.rcs.uce.eab;

import android.net.Uri;
import android.telephony.ims.RcsContactUceCapability;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class EabCapabilityResult {
    public static final int EAB_CONTACT_EXPIRED_FAILURE = 2;
    public static final int EAB_CONTACT_NOT_FOUND_FAILURE = 3;
    public static final int EAB_CONTROLLER_DESTROYED_FAILURE = 1;
    public static final int EAB_QUERY_SUCCESSFUL = 0;
    private final RcsContactUceCapability mContactCapabilities;
    private final Uri mContactUri;
    private final int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface QueryResult {
    }

    public EabCapabilityResult(Uri contactUri, int status, RcsContactUceCapability capabilities) {
        this.mStatus = status;
        this.mContactUri = contactUri;
        this.mContactCapabilities = capabilities;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public Uri getContact() {
        return this.mContactUri;
    }

    public RcsContactUceCapability getContactCapabilities() {
        return this.mContactCapabilities;
    }
}
