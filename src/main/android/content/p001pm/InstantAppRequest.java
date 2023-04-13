package android.content.p001pm;

import android.content.Intent;
import android.p008os.Bundle;
/* renamed from: android.content.pm.InstantAppRequest */
/* loaded from: classes.dex */
public final class InstantAppRequest {
    public final String callingFeatureId;
    public final String callingPackage;
    public final int[] hostDigestPrefixSecure;
    public final boolean isRequesterInstantApp;
    public final Intent origIntent;
    public final boolean resolveForStart;
    public final String resolvedType;
    public final AuxiliaryResolveInfo responseObj;
    public final String token;
    public final int userId;
    public final Bundle verificationBundle;

    public InstantAppRequest(AuxiliaryResolveInfo responseObj, Intent origIntent, String resolvedType, String callingPackage, String callingFeatureId, boolean isRequesterInstantApp, int userId, Bundle verificationBundle, boolean resolveForStart, int[] hostDigestPrefixSecure, String token) {
        this.responseObj = responseObj;
        this.origIntent = origIntent;
        this.resolvedType = resolvedType;
        this.callingPackage = callingPackage;
        this.callingFeatureId = callingFeatureId;
        this.isRequesterInstantApp = isRequesterInstantApp;
        this.userId = userId;
        this.verificationBundle = verificationBundle;
        this.resolveForStart = resolveForStart;
        this.hostDigestPrefixSecure = hostDigestPrefixSecure;
        this.token = token;
    }
}
