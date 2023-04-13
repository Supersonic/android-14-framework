package com.android.server.p011pm.verify.domain;

import android.content.Context;
import android.os.Binder;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxy;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationEnforcer */
/* loaded from: classes2.dex */
public class DomainVerificationEnforcer {
    public Callback mCallback;
    public final Context mContext;

    /* renamed from: com.android.server.pm.verify.domain.DomainVerificationEnforcer$Callback */
    /* loaded from: classes2.dex */
    public interface Callback {
        boolean doesUserExist(int i);

        boolean filterAppAccess(String str, int i, int i2);
    }

    public DomainVerificationEnforcer(Context context) {
        this.mContext = context;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void assertInternal(int i) {
        if (i == 0 || i == 1000 || i == 2000) {
            return;
        }
        throw new SecurityException("Caller " + i + " is not allowed to change internal state");
    }

    public void assertApprovedQuerent(int i, DomainVerificationProxy domainVerificationProxy) {
        if (i == 0 || i == 1000 || i == 2000) {
            return;
        }
        if (!domainVerificationProxy.isCallerVerifier(i)) {
            Context context = this.mContext;
            int callingPid = Binder.getCallingPid();
            context.enforcePermission("android.permission.DUMP", callingPid, i, "Caller " + i + " is not allowed to query domain verification state");
            return;
        }
        Context context2 = this.mContext;
        int callingPid2 = Binder.getCallingPid();
        context2.enforcePermission("android.permission.QUERY_ALL_PACKAGES", callingPid2, i, "Caller " + i + " does not hold android.permission.QUERY_ALL_PACKAGES");
    }

    public void assertApprovedVerifier(int i, DomainVerificationProxy domainVerificationProxy) throws SecurityException {
        if (i != 0 && i != 1000 && i != 2000) {
            int callingPid = Binder.getCallingPid();
            boolean z = false;
            if (this.mContext.checkPermission("android.permission.DOMAIN_VERIFICATION_AGENT", callingPid, i) != 0) {
                r0 = this.mContext.checkPermission("android.permission.INTENT_FILTER_VERIFICATION_AGENT", callingPid, i) == 0;
                if (!r0) {
                    throw new SecurityException("Caller " + i + " does not hold android.permission.DOMAIN_VERIFICATION_AGENT");
                }
                z = r0;
            }
            if (!z) {
                Context context = this.mContext;
                context.enforcePermission("android.permission.QUERY_ALL_PACKAGES", callingPid, i, "Caller " + i + " does not hold android.permission.QUERY_ALL_PACKAGES");
            }
            r0 = domainVerificationProxy.isCallerVerifier(i);
        }
        if (r0) {
            return;
        }
        throw new SecurityException("Caller " + i + " is not the approved domain verification agent");
    }

    public boolean assertApprovedUserStateQuerent(int i, int i2, String str, int i3) throws SecurityException {
        if (i2 != i3) {
            this.mContext.enforcePermission("android.permission.INTERACT_ACROSS_USERS", Binder.getCallingPid(), i, "Caller is not allowed to edit other users");
        }
        if (!this.mCallback.doesUserExist(i2)) {
            throw new SecurityException("User " + i2 + " does not exist");
        } else if (!this.mCallback.doesUserExist(i3)) {
            throw new SecurityException("User " + i3 + " does not exist");
        } else {
            return !this.mCallback.filterAppAccess(str, i, i3);
        }
    }

    public boolean assertApprovedUserSelector(int i, int i2, String str, int i3) throws SecurityException {
        if (i2 != i3) {
            this.mContext.enforcePermission("android.permission.INTERACT_ACROSS_USERS", Binder.getCallingPid(), i, "Caller is not allowed to edit other users");
        }
        this.mContext.enforcePermission("android.permission.UPDATE_DOMAIN_VERIFICATION_USER_SELECTION", Binder.getCallingPid(), i, "Caller is not allowed to edit user selections");
        if (!this.mCallback.doesUserExist(i2)) {
            throw new SecurityException("User " + i2 + " does not exist");
        } else if (this.mCallback.doesUserExist(i3)) {
            if (str == null) {
                return true;
            }
            return !this.mCallback.filterAppAccess(str, i, i3);
        } else {
            throw new SecurityException("User " + i3 + " does not exist");
        }
    }

    public boolean callerIsLegacyUserSelector(int i, int i2, String str, int i3) {
        this.mContext.enforcePermission("android.permission.SET_PREFERRED_APPLICATIONS", Binder.getCallingPid(), i, "Caller is not allowed to edit user state");
        if (i2 == i3 || this.mContext.checkPermission("android.permission.INTERACT_ACROSS_USERS", Binder.getCallingPid(), i) == 0) {
            if (!this.mCallback.doesUserExist(i2)) {
                throw new SecurityException("User " + i2 + " does not exist");
            } else if (!this.mCallback.doesUserExist(i3)) {
                throw new SecurityException("User " + i3 + " does not exist");
            } else {
                return !this.mCallback.filterAppAccess(str, i, i3);
            }
        }
        return false;
    }

    public boolean callerIsLegacyUserQuerent(int i, int i2, String str, int i3) {
        if (i2 != i3) {
            this.mContext.enforcePermission("android.permission.INTERACT_ACROSS_USERS_FULL", Binder.getCallingPid(), i, "Caller is not allowed to edit other users");
        }
        if (!this.mCallback.doesUserExist(i2)) {
            throw new SecurityException("User " + i2 + " does not exist");
        } else if (!this.mCallback.doesUserExist(i3)) {
            throw new SecurityException("User " + i3 + " does not exist");
        } else {
            return !this.mCallback.filterAppAccess(str, i, i3);
        }
    }

    public void assertOwnerQuerent(int i, int i2, int i3) {
        int callingPid = Binder.getCallingPid();
        if (i2 != i3) {
            this.mContext.enforcePermission("android.permission.INTERACT_ACROSS_USERS", callingPid, i, "Caller is not allowed to query other users");
        }
        Context context = this.mContext;
        context.enforcePermission("android.permission.QUERY_ALL_PACKAGES", callingPid, i, "Caller " + i + " does not hold android.permission.QUERY_ALL_PACKAGES");
        this.mContext.enforcePermission("android.permission.UPDATE_DOMAIN_VERIFICATION_USER_SELECTION", callingPid, i, "Caller is not allowed to query user selections");
        if (!this.mCallback.doesUserExist(i2)) {
            throw new SecurityException("User " + i2 + " does not exist");
        } else if (this.mCallback.doesUserExist(i3)) {
        } else {
            throw new SecurityException("User " + i3 + " does not exist");
        }
    }
}
