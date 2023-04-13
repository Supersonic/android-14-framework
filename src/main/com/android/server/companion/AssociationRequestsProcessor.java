package com.android.server.companion;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.companion.AssociatedDevice;
import android.companion.AssociationInfo;
import android.companion.AssociationRequest;
import android.companion.IAssociationRequestCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.net.MacAddress;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.clipboard.ClipboardService;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
@SuppressLint({"LongLogTag"})
/* loaded from: classes.dex */
public class AssociationRequestsProcessor {
    public static final ComponentName ASSOCIATION_REQUEST_APPROVAL_ACTIVITY = ComponentName.createRelative("com.android.companiondevicemanager", ".CompanionDeviceActivity");
    public final AssociationStoreImpl mAssociationStore;
    public final Context mContext;
    public final ResultReceiver mOnRequestConfirmationReceiver = new ResultReceiver(Handler.getMain()) { // from class: com.android.server.companion.AssociationRequestsProcessor.1
        @Override // android.os.ResultReceiver
        public void onReceiveResult(int i, Bundle bundle) {
            MacAddress macAddress;
            if (i != 0) {
                Slog.w("CDM_AssociationRequestsProcessor", "Unknown result code:" + i);
                return;
            }
            AssociationRequest associationRequest = (AssociationRequest) bundle.getParcelable("association_request", AssociationRequest.class);
            IAssociationRequestCallback asInterface = IAssociationRequestCallback.Stub.asInterface(bundle.getBinder("application_callback"));
            ResultReceiver resultReceiver = (ResultReceiver) bundle.getParcelable("result_receiver", ResultReceiver.class);
            Objects.requireNonNull(associationRequest);
            Objects.requireNonNull(asInterface);
            Objects.requireNonNull(resultReceiver);
            if (associationRequest.isSelfManaged()) {
                macAddress = null;
            } else {
                macAddress = (MacAddress) bundle.getParcelable("mac_address", MacAddress.class);
                Objects.requireNonNull(macAddress);
            }
            AssociationRequestsProcessor.this.processAssociationRequestApproval(associationRequest, asInterface, resultReceiver, macAddress);
        }
    };
    public final PackageManagerInternal mPackageManager;
    public final CompanionDeviceManagerService mService;

    public AssociationRequestsProcessor(CompanionDeviceManagerService companionDeviceManagerService, AssociationStoreImpl associationStoreImpl) {
        this.mContext = companionDeviceManagerService.getContext();
        this.mService = companionDeviceManagerService;
        this.mPackageManager = companionDeviceManagerService.mPackageManagerInternal;
        this.mAssociationStore = associationStoreImpl;
    }

    public void processNewAssociationRequest(AssociationRequest associationRequest, String str, int i, IAssociationRequestCallback iAssociationRequestCallback) {
        Objects.requireNonNull(associationRequest, "Request MUST NOT be null");
        if (associationRequest.isSelfManaged()) {
            Objects.requireNonNull(associationRequest.getDisplayName(), "AssociationRequest.displayName MUST NOT be null.");
        }
        Objects.requireNonNull(str, "Package name MUST NOT be null");
        Objects.requireNonNull(iAssociationRequestCallback, "Callback MUST NOT be null");
        int packageUid = this.mPackageManager.getPackageUid(str, 0L, i);
        PermissionsUtils.enforcePermissionsForAssociation(this.mContext, associationRequest, packageUid);
        PackageUtils.enforceUsesCompanionDeviceFeature(this.mContext, i, str);
        if (associationRequest.isSelfManaged() && !associationRequest.isForceConfirmation() && !willAddRoleHolder(associationRequest, str, i)) {
            createAssociationAndNotifyApplication(associationRequest, str, i, null, iAssociationRequestCallback, null);
            return;
        }
        associationRequest.setPackageName(str);
        associationRequest.setUserId(i);
        associationRequest.setSkipPrompt(mayAssociateWithoutPrompt(str, i));
        Bundle bundle = new Bundle();
        bundle.putParcelable("association_request", associationRequest);
        bundle.putBinder("application_callback", iAssociationRequestCallback.asBinder());
        bundle.putParcelable("result_receiver", Utils.prepareForIpc(this.mOnRequestConfirmationReceiver));
        Intent intent = new Intent();
        intent.setComponent(ASSOCIATION_REQUEST_APPROVAL_ACTIVITY);
        intent.putExtras(bundle);
        try {
            iAssociationRequestCallback.onAssociationPending(createPendingIntent(packageUid, intent));
        } catch (RemoteException unused) {
        }
    }

    public PendingIntent buildAssociationCancellationIntent(String str, int i) {
        Objects.requireNonNull(str, "Package name MUST NOT be null");
        PackageUtils.enforceUsesCompanionDeviceFeature(this.mContext, i, str);
        int packageUid = this.mPackageManager.getPackageUid(str, 0L, i);
        Bundle bundle = new Bundle();
        bundle.putBoolean("cancel_confirmation", true);
        Intent intent = new Intent();
        intent.setComponent(ASSOCIATION_REQUEST_APPROVAL_ACTIVITY);
        intent.putExtras(bundle);
        return createPendingIntent(packageUid, intent);
    }

    public final void processAssociationRequestApproval(AssociationRequest associationRequest, IAssociationRequestCallback iAssociationRequestCallback, ResultReceiver resultReceiver, MacAddress macAddress) {
        String packageName = associationRequest.getPackageName();
        int userId = associationRequest.getUserId();
        try {
            PermissionsUtils.enforcePermissionsForAssociation(this.mContext, associationRequest, this.mPackageManager.getPackageUid(packageName, 0L, userId));
            createAssociationAndNotifyApplication(associationRequest, packageName, userId, macAddress, iAssociationRequestCallback, resultReceiver);
        } catch (SecurityException e) {
            try {
                iAssociationRequestCallback.onFailure(e.getMessage());
            } catch (RemoteException unused) {
            }
        }
    }

    public final void createAssociationAndNotifyApplication(AssociationRequest associationRequest, String str, int i, MacAddress macAddress, IAssociationRequestCallback iAssociationRequestCallback, ResultReceiver resultReceiver) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            createAssociation(i, str, macAddress, associationRequest.getDisplayName(), associationRequest.getDeviceProfile(), associationRequest.getAssociatedDevice(), associationRequest.isSelfManaged(), iAssociationRequestCallback, resultReceiver);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void createAssociation(final int i, final String str, MacAddress macAddress, CharSequence charSequence, final String str2, AssociatedDevice associatedDevice, boolean z, final IAssociationRequestCallback iAssociationRequestCallback, final ResultReceiver resultReceiver) {
        final AssociationInfo associationInfo = new AssociationInfo(this.mService.getNewAssociationIdForPackage(i, str), i, str, macAddress, charSequence, str2, associatedDevice, z, false, false, System.currentTimeMillis(), Long.MAX_VALUE, -1);
        if (str2 != null) {
            RolesUtils.addRoleHolderForAssociation(this.mService.getContext(), associationInfo, new Consumer() { // from class: com.android.server.companion.AssociationRequestsProcessor$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AssociationRequestsProcessor.this.lambda$createAssociation$0(associationInfo, str2, iAssociationRequestCallback, resultReceiver, i, str, (Boolean) obj);
                }
            });
            return;
        }
        addAssociationToStore(associationInfo, null);
        sendCallbackAndFinish(associationInfo, iAssociationRequestCallback, resultReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createAssociation$0(AssociationInfo associationInfo, String str, IAssociationRequestCallback iAssociationRequestCallback, ResultReceiver resultReceiver, int i, String str2, Boolean bool) {
        if (bool.booleanValue()) {
            addAssociationToStore(associationInfo, str);
            sendCallbackAndFinish(associationInfo, iAssociationRequestCallback, resultReceiver);
            return;
        }
        Slog.e("CDM_AssociationRequestsProcessor", "Failed to add u" + i + "\\" + str2 + " to the list of " + str + " holders.");
        sendCallbackAndFinish(null, iAssociationRequestCallback, resultReceiver);
    }

    public void enableSystemDataSync(int i, int i2) {
        AssociationInfo associationById = this.mAssociationStore.getAssociationById(i);
        this.mAssociationStore.updateAssociation(AssociationInfo.builder(associationById).setSystemDataSyncFlags(associationById.getSystemDataSyncFlags() | i2).build());
    }

    public void disableSystemDataSync(int i, int i2) {
        AssociationInfo associationById = this.mAssociationStore.getAssociationById(i);
        this.mAssociationStore.updateAssociation(AssociationInfo.builder(associationById).setSystemDataSyncFlags(associationById.getSystemDataSyncFlags() & (~i2)).build());
    }

    public final void addAssociationToStore(AssociationInfo associationInfo, String str) {
        Slog.i("CDM_AssociationRequestsProcessor", "New CDM association created=" + associationInfo);
        this.mAssociationStore.addAssociation(associationInfo);
        this.mService.updateSpecialAccessPermissionForAssociatedPackage(associationInfo);
        MetricUtils.logCreateAssociation(str);
    }

    public final void sendCallbackAndFinish(AssociationInfo associationInfo, IAssociationRequestCallback iAssociationRequestCallback, ResultReceiver resultReceiver) {
        if (associationInfo == null) {
            if (iAssociationRequestCallback != null) {
                try {
                    iAssociationRequestCallback.onFailure("internal_error");
                } catch (RemoteException unused) {
                }
            }
            if (resultReceiver != null) {
                resultReceiver.send(3, new Bundle());
                return;
            }
            return;
        }
        if (iAssociationRequestCallback != null) {
            try {
                iAssociationRequestCallback.onAssociationCreated(associationInfo);
            } catch (RemoteException unused2) {
            }
        }
        if (resultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("association", associationInfo);
            resultReceiver.send(0, bundle);
        }
    }

    public final boolean willAddRoleHolder(AssociationRequest associationRequest, final String str, final int i) {
        final String deviceProfile = associationRequest.getDeviceProfile();
        if (deviceProfile == null) {
            return false;
        }
        return !((Boolean) Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.companion.AssociationRequestsProcessor$$ExternalSyntheticLambda1
            public final Object getOrThrow() {
                Boolean lambda$willAddRoleHolder$1;
                lambda$willAddRoleHolder$1 = AssociationRequestsProcessor.this.lambda$willAddRoleHolder$1(i, str, deviceProfile);
                return lambda$willAddRoleHolder$1;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$willAddRoleHolder$1(int i, String str, String str2) throws Exception {
        return Boolean.valueOf(RolesUtils.isRoleHolder(this.mContext, i, str, str2));
    }

    public final PendingIntent createPendingIntent(int i, Intent intent) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return PendingIntent.getActivityAsUser(this.mContext, i, intent, 1409286144, null, UserHandle.CURRENT);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean mayAssociateWithoutPrompt(String str, int i) {
        String[] stringArray = this.mContext.getResources().getStringArray(17236015);
        boolean z = false;
        if (!ArrayUtils.contains(stringArray, str)) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        Iterator<AssociationInfo> it = this.mAssociationStore.getAssociationsForPackage(i, str).iterator();
        int i2 = 0;
        while (true) {
            if (it.hasNext()) {
                if ((currentTimeMillis - it.next().getTimeApprovedMs() < ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) && (i2 = i2 + 1) >= 5) {
                    Slog.w("CDM_AssociationRequestsProcessor", "Too many associations: " + str + " already associated " + i2 + " devices within the last " + ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS + "ms");
                    return false;
                }
            } else {
                String[] stringArray2 = this.mContext.getResources().getStringArray(17236014);
                HashSet hashSet = new HashSet();
                for (int i3 = 0; i3 < stringArray.length; i3++) {
                    if (stringArray[i3].equals(str)) {
                        hashSet.add(stringArray2[i3].replaceAll(XmlUtils.STRING_ARRAY_SEPARATOR, ""));
                    }
                }
                String[] computeSignaturesSha256Digests = android.util.PackageUtils.computeSignaturesSha256Digests(this.mPackageManager.getPackage(str).getSigningDetails().getSignatures());
                int length = computeSignaturesSha256Digests.length;
                int i4 = 0;
                while (true) {
                    if (i4 >= length) {
                        break;
                    } else if (hashSet.contains(computeSignaturesSha256Digests[i4])) {
                        z = true;
                        break;
                    } else {
                        i4++;
                    }
                }
                if (!z) {
                    Slog.w("CDM_AssociationRequestsProcessor", "Certificate mismatch for allowlisted package " + str);
                }
                return z;
            }
        }
    }
}
