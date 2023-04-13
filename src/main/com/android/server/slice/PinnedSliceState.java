package com.android.server.slice;

import android.app.slice.SliceSpec;
import android.content.ContentProviderClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class PinnedSliceState {
    public final Object mLock;
    public final String mPkg;
    public final SliceManagerService mService;
    public boolean mSlicePinned;
    public final Uri mUri;
    @GuardedBy({"mLock"})
    public final ArraySet<String> mPinnedPkgs = new ArraySet<>();
    @GuardedBy({"mLock"})
    public final ArrayMap<IBinder, ListenerInfo> mListeners = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public SliceSpec[] mSupportedSpecs = null;
    public final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.slice.PinnedSliceState$$ExternalSyntheticLambda0
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            PinnedSliceState.this.handleRecheckListeners();
        }
    };

    public static /* synthetic */ boolean lambda$mergeSpecs$1(SliceSpec sliceSpec) {
        return sliceSpec != null;
    }

    public PinnedSliceState(SliceManagerService sliceManagerService, Uri uri, String str) {
        this.mService = sliceManagerService;
        this.mUri = uri;
        this.mPkg = str;
        this.mLock = sliceManagerService.getLock();
    }

    public String getPkg() {
        return this.mPkg;
    }

    public SliceSpec[] getSpecs() {
        return this.mSupportedSpecs;
    }

    public void mergeSpecs(final SliceSpec[] sliceSpecArr) {
        synchronized (this.mLock) {
            SliceSpec[] sliceSpecArr2 = this.mSupportedSpecs;
            if (sliceSpecArr2 == null) {
                this.mSupportedSpecs = sliceSpecArr;
            } else {
                this.mSupportedSpecs = (SliceSpec[]) Arrays.asList(sliceSpecArr2).stream().map(new Function() { // from class: com.android.server.slice.PinnedSliceState$$ExternalSyntheticLambda3
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        SliceSpec lambda$mergeSpecs$0;
                        lambda$mergeSpecs$0 = PinnedSliceState.this.lambda$mergeSpecs$0(sliceSpecArr, (SliceSpec) obj);
                        return lambda$mergeSpecs$0;
                    }
                }).filter(new Predicate() { // from class: com.android.server.slice.PinnedSliceState$$ExternalSyntheticLambda4
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$mergeSpecs$1;
                        lambda$mergeSpecs$1 = PinnedSliceState.lambda$mergeSpecs$1((SliceSpec) obj);
                        return lambda$mergeSpecs$1;
                    }
                }).toArray(new IntFunction() { // from class: com.android.server.slice.PinnedSliceState$$ExternalSyntheticLambda5
                    @Override // java.util.function.IntFunction
                    public final Object apply(int i) {
                        SliceSpec[] lambda$mergeSpecs$2;
                        lambda$mergeSpecs$2 = PinnedSliceState.lambda$mergeSpecs$2(i);
                        return lambda$mergeSpecs$2;
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SliceSpec lambda$mergeSpecs$0(SliceSpec[] sliceSpecArr, SliceSpec sliceSpec) {
        SliceSpec findSpec = findSpec(sliceSpecArr, sliceSpec.getType());
        if (findSpec == null) {
            return null;
        }
        return findSpec.getRevision() < sliceSpec.getRevision() ? findSpec : sliceSpec;
    }

    public static /* synthetic */ SliceSpec[] lambda$mergeSpecs$2(int i) {
        return new SliceSpec[i];
    }

    public final SliceSpec findSpec(SliceSpec[] sliceSpecArr, String str) {
        for (SliceSpec sliceSpec : sliceSpecArr) {
            if (Objects.equals(sliceSpec.getType(), str)) {
                return sliceSpec;
            }
        }
        return null;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void destroy() {
        setSlicePinned(false);
    }

    public final void setSlicePinned(boolean z) {
        synchronized (this.mLock) {
            if (this.mSlicePinned == z) {
                return;
            }
            this.mSlicePinned = z;
            if (z) {
                this.mService.getHandler().post(new Runnable() { // from class: com.android.server.slice.PinnedSliceState$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PinnedSliceState.this.handleSendPinned();
                    }
                });
            } else {
                this.mService.getHandler().post(new Runnable() { // from class: com.android.server.slice.PinnedSliceState$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        PinnedSliceState.this.handleSendUnpinned();
                    }
                });
            }
        }
    }

    public void pin(String str, SliceSpec[] sliceSpecArr, IBinder iBinder) {
        synchronized (this.mLock) {
            this.mListeners.put(iBinder, new ListenerInfo(iBinder, str, true, Binder.getCallingUid(), Binder.getCallingPid()));
            try {
                iBinder.linkToDeath(this.mDeathRecipient, 0);
            } catch (RemoteException unused) {
            }
            mergeSpecs(sliceSpecArr);
            setSlicePinned(true);
        }
    }

    public boolean unpin(String str, IBinder iBinder) {
        synchronized (this.mLock) {
            iBinder.unlinkToDeath(this.mDeathRecipient, 0);
            this.mListeners.remove(iBinder);
        }
        return !hasPinOrListener();
    }

    @VisibleForTesting
    public boolean hasPinOrListener() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mPinnedPkgs.isEmpty() && this.mListeners.isEmpty()) ? false : true;
        }
        return z;
    }

    public ContentProviderClient getClient() {
        ContentProviderClient acquireUnstableContentProviderClient = this.mService.getContext().getContentResolver().acquireUnstableContentProviderClient(this.mUri);
        if (acquireUnstableContentProviderClient == null) {
            return null;
        }
        acquireUnstableContentProviderClient.setDetectNotResponding(5000L);
        return acquireUnstableContentProviderClient;
    }

    public final void checkSelfRemove() {
        if (hasPinOrListener()) {
            return;
        }
        this.mService.removePinnedSlice(this.mUri);
    }

    public final void handleRecheckListeners() {
        if (hasPinOrListener()) {
            synchronized (this.mLock) {
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    if (!this.mListeners.valueAt(size).token.isBinderAlive()) {
                        this.mListeners.removeAt(size);
                    }
                }
                checkSelfRemove();
            }
        }
    }

    public final void handleSendPinned() {
        ContentProviderClient client = getClient();
        if (client == null) {
            if (client != null) {
                client.close();
                return;
            }
            return;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable("slice_uri", this.mUri);
            try {
                client.call("pin", null, bundle);
            } catch (Exception e) {
                Log.w("PinnedSliceState", "Unable to contact " + this.mUri, e);
            }
            client.close();
        } catch (Throwable th) {
            try {
                client.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public final void handleSendUnpinned() {
        ContentProviderClient client = getClient();
        if (client == null) {
            if (client != null) {
                client.close();
                return;
            }
            return;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable("slice_uri", this.mUri);
            try {
                client.call("unpin", null, bundle);
            } catch (Exception e) {
                Log.w("PinnedSliceState", "Unable to contact " + this.mUri, e);
            }
            client.close();
        } catch (Throwable th) {
            try {
                client.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* loaded from: classes2.dex */
    public class ListenerInfo {
        public int callingPid;
        public int callingUid;
        public boolean hasPermission;
        public String pkg;
        public IBinder token;

        public ListenerInfo(IBinder iBinder, String str, boolean z, int i, int i2) {
            this.token = iBinder;
            this.pkg = str;
            this.hasPermission = z;
            this.callingUid = i;
            this.callingPid = i2;
        }
    }
}
