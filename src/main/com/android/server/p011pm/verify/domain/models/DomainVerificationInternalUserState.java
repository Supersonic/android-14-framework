package com.android.server.p011pm.verify.domain.models;

import android.annotation.NonNull;
import android.annotation.UserIdInt;
import android.util.ArraySet;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
import java.util.Set;
/* renamed from: com.android.server.pm.verify.domain.models.DomainVerificationInternalUserState */
/* loaded from: classes2.dex */
public class DomainVerificationInternalUserState {
    public final ArraySet<String> mEnabledHosts;
    public boolean mLinkHandlingAllowed;
    public final int mUserId;

    public DomainVerificationInternalUserState(int i) {
        this.mLinkHandlingAllowed = true;
        this.mUserId = i;
        this.mEnabledHosts = new ArraySet<>();
    }

    public DomainVerificationInternalUserState addHosts(ArraySet<String> arraySet) {
        this.mEnabledHosts.addAll((ArraySet<? extends String>) arraySet);
        return this;
    }

    public DomainVerificationInternalUserState addHosts(Set<String> set) {
        this.mEnabledHosts.addAll(set);
        return this;
    }

    public DomainVerificationInternalUserState removeHost(String str) {
        this.mEnabledHosts.remove(str);
        return this;
    }

    public DomainVerificationInternalUserState removeHosts(Set<String> set) {
        this.mEnabledHosts.removeAll(set);
        return this;
    }

    public DomainVerificationInternalUserState retainHosts(Set<String> set) {
        this.mEnabledHosts.retainAll(set);
        return this;
    }

    public DomainVerificationInternalUserState(int i, ArraySet<String> arraySet, boolean z) {
        this.mLinkHandlingAllowed = true;
        this.mUserId = i;
        AnnotationValidations.validate(UserIdInt.class, (UserIdInt) null, i);
        this.mEnabledHosts = arraySet;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, arraySet);
        this.mLinkHandlingAllowed = z;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public ArraySet<String> getEnabledHosts() {
        return this.mEnabledHosts;
    }

    public boolean isLinkHandlingAllowed() {
        return this.mLinkHandlingAllowed;
    }

    public DomainVerificationInternalUserState setLinkHandlingAllowed(boolean z) {
        this.mLinkHandlingAllowed = z;
        return this;
    }

    public String toString() {
        return "DomainVerificationInternalUserState { userId = " + this.mUserId + ", enabledHosts = " + this.mEnabledHosts + ", linkHandlingAllowed = " + this.mLinkHandlingAllowed + " }";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DomainVerificationInternalUserState domainVerificationInternalUserState = (DomainVerificationInternalUserState) obj;
        return this.mUserId == domainVerificationInternalUserState.mUserId && Objects.equals(this.mEnabledHosts, domainVerificationInternalUserState.mEnabledHosts) && this.mLinkHandlingAllowed == domainVerificationInternalUserState.mLinkHandlingAllowed;
    }

    public int hashCode() {
        return ((((this.mUserId + 31) * 31) + Objects.hashCode(this.mEnabledHosts)) * 31) + Boolean.hashCode(this.mLinkHandlingAllowed);
    }
}
