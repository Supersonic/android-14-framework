package com.android.ims.rcs.uce.eab;

import android.telephony.ims.RcsContactUceCapability;
/* loaded from: classes.dex */
public class RcsUceCapabilityBuilderWrapper {
    private final int mMechanism;
    private RcsContactUceCapability.OptionsBuilder mOptionsBuilder;
    private RcsContactUceCapability.PresenceBuilder mPresenceBuilder;

    public RcsUceCapabilityBuilderWrapper(int mechanism) {
        this.mMechanism = mechanism;
    }

    public int getMechanism() {
        return this.mMechanism;
    }

    public void setPresenceBuilder(RcsContactUceCapability.PresenceBuilder presenceBuilder) {
        this.mPresenceBuilder = presenceBuilder;
    }

    public RcsContactUceCapability.PresenceBuilder getPresenceBuilder() {
        return this.mPresenceBuilder;
    }

    public void setOptionsBuilder(RcsContactUceCapability.OptionsBuilder optionsBuilder) {
        this.mOptionsBuilder = optionsBuilder;
    }

    public RcsContactUceCapability.OptionsBuilder getOptionsBuilder() {
        return this.mOptionsBuilder;
    }
}
