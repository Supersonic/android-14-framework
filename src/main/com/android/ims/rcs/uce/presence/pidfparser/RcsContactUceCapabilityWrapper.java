package com.android.ims.rcs.uce.presence.pidfparser;

import android.net.Uri;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RcsContactUceCapabilityWrapper {
    private final Uri mContactUri;
    private Uri mEntityUri;
    private final int mRequestResult;
    private final int mSourceType;
    private final List<RcsContactPresenceTuple> mPresenceTuples = new ArrayList();
    private boolean mIsMalformed = false;

    public RcsContactUceCapabilityWrapper(Uri contact, int sourceType, int requestResult) {
        this.mContactUri = contact;
        this.mSourceType = sourceType;
        this.mRequestResult = requestResult;
    }

    public void addCapabilityTuple(RcsContactPresenceTuple tuple) {
        this.mPresenceTuples.add(tuple);
    }

    public void setMalformedContents() {
        this.mIsMalformed = true;
    }

    public void setEntityUri(Uri entityUri) {
        this.mEntityUri = entityUri;
    }

    public boolean isMalformed() {
        return this.mIsMalformed && this.mPresenceTuples.isEmpty();
    }

    public Uri getEntityUri() {
        return this.mEntityUri;
    }

    public RcsContactUceCapability toRcsContactUceCapability() {
        RcsContactUceCapability.PresenceBuilder presenceBuilder = new RcsContactUceCapability.PresenceBuilder(this.mContactUri, this.mSourceType, this.mRequestResult);
        presenceBuilder.addCapabilityTuples(this.mPresenceTuples);
        presenceBuilder.setEntityUri(this.mEntityUri);
        return presenceBuilder.build();
    }
}
