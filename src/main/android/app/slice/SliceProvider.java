package android.app.slice;

import android.app.PendingIntent;
import android.app.slice.Slice;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.Process;
import android.p008os.StrictMode;
import android.util.ArraySet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class SliceProvider extends ContentProvider {
    private static final boolean DEBUG = false;
    public static final String EXTRA_BIND_URI = "slice_uri";
    public static final String EXTRA_INTENT = "slice_intent";
    public static final String EXTRA_PKG = "pkg";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_SLICE = "slice";
    public static final String EXTRA_SLICE_DESCENDANTS = "slice_descendants";
    public static final String EXTRA_SUPPORTED_SPECS = "supported_specs";
    public static final String METHOD_GET_DESCENDANTS = "get_descendants";
    public static final String METHOD_GET_PERMISSIONS = "get_permissions";
    public static final String METHOD_MAP_INTENT = "map_slice";
    public static final String METHOD_MAP_ONLY_INTENT = "map_only";
    public static final String METHOD_PIN = "pin";
    public static final String METHOD_SLICE = "bind_slice";
    public static final String METHOD_UNPIN = "unpin";
    private static final long SLICE_BIND_ANR = 2000;
    public static final String SLICE_TYPE = "vnd.android.slice";
    private static final String TAG = "SliceProvider";
    private final Runnable mAnr;
    private final String[] mAutoGrantPermissions;
    private String mCallback;
    private SliceManager mSliceManager;

    public SliceProvider(String... autoGrantPermissions) {
        this.mAnr = new Runnable() { // from class: android.app.slice.SliceProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SliceProvider.this.lambda$new$0();
            }
        };
        this.mAutoGrantPermissions = autoGrantPermissions;
    }

    public SliceProvider() {
        this.mAnr = new Runnable() { // from class: android.app.slice.SliceProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SliceProvider.this.lambda$new$0();
            }
        };
        this.mAutoGrantPermissions = new String[0];
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        this.mSliceManager = (SliceManager) context.getSystemService(SliceManager.class);
    }

    public Slice onBindSlice(Uri sliceUri, Set<SliceSpec> supportedSpecs) {
        return onBindSlice(sliceUri, new ArrayList(supportedSpecs));
    }

    @Deprecated
    public Slice onBindSlice(Uri sliceUri, List<SliceSpec> supportedSpecs) {
        return null;
    }

    public void onSlicePinned(Uri sliceUri) {
    }

    public void onSliceUnpinned(Uri sliceUri) {
    }

    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        return Collections.emptyList();
    }

    public Uri onMapIntentToUri(Intent intent) {
        throw new UnsupportedOperationException("This provider has not implemented intent to uri mapping");
    }

    public PendingIntent onCreatePermissionRequest(Uri sliceUri) {
        return createPermissionPendingIntent(getContext(), sliceUri, getCallingPackage());
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return null;
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final Cursor query(Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final String getType(Uri uri) {
        return SLICE_TYPE;
    }

    @Override // android.content.ContentProvider
    public Bundle call(String method, String arg, Bundle extras) {
        if (method.equals(METHOD_SLICE)) {
            Uri uri = getUriWithoutUserId(validateIncomingUriOrNull((Uri) extras.getParcelable("slice_uri", Uri.class)));
            List<SliceSpec> supportedSpecs = extras.getParcelableArrayList(EXTRA_SUPPORTED_SPECS, SliceSpec.class);
            String callingPackage = getCallingPackage();
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            Slice s = handleBindSlice(uri, supportedSpecs, callingPackage, callingUid, callingPid);
            Bundle b = new Bundle();
            b.putParcelable("slice", s);
            return b;
        } else if (method.equals(METHOD_MAP_INTENT)) {
            Intent intent = (Intent) extras.getParcelable(EXTRA_INTENT, Intent.class);
            if (intent == null) {
                return null;
            }
            Uri uri2 = validateIncomingUriOrNull(onMapIntentToUri(intent));
            List<SliceSpec> supportedSpecs2 = extras.getParcelableArrayList(EXTRA_SUPPORTED_SPECS, SliceSpec.class);
            Bundle b2 = new Bundle();
            if (uri2 != null) {
                Slice s2 = handleBindSlice(uri2, supportedSpecs2, getCallingPackage(), Binder.getCallingUid(), Binder.getCallingPid());
                b2.putParcelable("slice", s2);
            } else {
                b2.putParcelable("slice", null);
            }
            return b2;
        } else if (method.equals(METHOD_MAP_ONLY_INTENT)) {
            Intent intent2 = (Intent) extras.getParcelable(EXTRA_INTENT, Intent.class);
            if (intent2 == null) {
                return null;
            }
            Uri uri3 = validateIncomingUriOrNull(onMapIntentToUri(intent2));
            Bundle b3 = new Bundle();
            b3.putParcelable("slice", uri3);
            return b3;
        } else {
            if (method.equals(METHOD_PIN)) {
                Uri uri4 = getUriWithoutUserId(validateIncomingUriOrNull((Uri) extras.getParcelable("slice_uri", Uri.class)));
                if (Binder.getCallingUid() != 1000) {
                    throw new SecurityException("Only the system can pin/unpin slices");
                }
                handlePinSlice(uri4);
            } else if (method.equals(METHOD_UNPIN)) {
                Uri uri5 = getUriWithoutUserId(validateIncomingUriOrNull((Uri) extras.getParcelable("slice_uri", Uri.class)));
                if (Binder.getCallingUid() != 1000) {
                    throw new SecurityException("Only the system can pin/unpin slices");
                }
                handleUnpinSlice(uri5);
            } else if (method.equals(METHOD_GET_DESCENDANTS)) {
                Uri uri6 = getUriWithoutUserId(validateIncomingUriOrNull((Uri) extras.getParcelable("slice_uri", Uri.class)));
                Bundle b4 = new Bundle();
                b4.putParcelableArrayList(EXTRA_SLICE_DESCENDANTS, new ArrayList<>(handleGetDescendants(uri6)));
                return b4;
            } else if (method.equals(METHOD_GET_PERMISSIONS)) {
                if (Binder.getCallingUid() != 1000) {
                    throw new SecurityException("Only the system can get permissions");
                }
                Bundle b5 = new Bundle();
                b5.putStringArray("result", this.mAutoGrantPermissions);
                return b5;
            }
            return super.call(method, arg, extras);
        }
    }

    private Uri validateIncomingUriOrNull(Uri uri) {
        if (uri == null) {
            return null;
        }
        return validateIncomingUri(uri);
    }

    private Collection<Uri> handleGetDescendants(Uri uri) {
        this.mCallback = "onGetSliceDescendants";
        return onGetSliceDescendants(uri);
    }

    private void handlePinSlice(Uri sliceUri) {
        this.mCallback = "onSlicePinned";
        Handler.getMain().postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            onSlicePinned(sliceUri);
        } finally {
            Handler.getMain().removeCallbacks(this.mAnr);
        }
    }

    private void handleUnpinSlice(Uri sliceUri) {
        this.mCallback = "onSliceUnpinned";
        Handler.getMain().postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            onSliceUnpinned(sliceUri);
        } finally {
            Handler.getMain().removeCallbacks(this.mAnr);
        }
    }

    private Slice handleBindSlice(Uri sliceUri, List<SliceSpec> supportedSpecs, String callingPkg, int callingUid, int callingPid) {
        String pkg = callingPkg != null ? callingPkg : getContext().getPackageManager().getNameForUid(callingUid);
        try {
            this.mSliceManager.enforceSlicePermission(sliceUri, callingPid, callingUid, this.mAutoGrantPermissions);
            this.mCallback = "onBindSlice";
            Handler.getMain().postDelayed(this.mAnr, SLICE_BIND_ANR);
            try {
                return onBindSliceStrict(sliceUri, supportedSpecs);
            } finally {
                Handler.getMain().removeCallbacks(this.mAnr);
            }
        } catch (SecurityException e) {
            return createPermissionSlice(getContext(), sliceUri, pkg);
        }
    }

    public Slice createPermissionSlice(Context context, Uri sliceUri, String callingPackage) {
        this.mCallback = "onCreatePermissionRequest";
        Handler.getMain().postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            PendingIntent action = onCreatePermissionRequest(sliceUri);
            Handler.getMain().removeCallbacks(this.mAnr);
            Slice.Builder parent = new Slice.Builder(sliceUri);
            Slice.Builder childAction = new Slice.Builder(parent).addIcon(Icon.createWithResource(context, (int) C4057R.C4058drawable.ic_permission), null, Collections.emptyList()).addHints(Arrays.asList("title", "shortcut")).addAction(action, new Slice.Builder(parent).build(), null);
            TypedValue tv = new TypedValue();
            new ContextThemeWrapper(context, 16974123).getTheme().resolveAttribute(16843829, tv, true);
            int deviceDefaultAccent = tv.data;
            parent.addSubSlice(new Slice.Builder(sliceUri.buildUpon().appendPath("permission").build()).addIcon(Icon.createWithResource(context, (int) C4057R.C4058drawable.ic_arrow_forward), null, Collections.emptyList()).addText(getPermissionString(context, callingPackage), null, Collections.emptyList()).addInt(deviceDefaultAccent, "color", Collections.emptyList()).addSubSlice(childAction.build(), null).build(), null);
            return parent.addHints(Arrays.asList(Slice.HINT_PERMISSION_REQUEST)).build();
        } catch (Throwable th) {
            Handler.getMain().removeCallbacks(this.mAnr);
            throw th;
        }
    }

    public static PendingIntent createPermissionPendingIntent(Context context, Uri sliceUri, String callingPackage) {
        return PendingIntent.getActivity(context, 0, createPermissionIntent(context, sliceUri, callingPackage), 67108864);
    }

    public static Intent createPermissionIntent(Context context, Uri sliceUri, String callingPackage) {
        Intent intent = new Intent(SliceManager.ACTION_REQUEST_SLICE_PERMISSION);
        intent.setComponent(ComponentName.unflattenFromString(context.getResources().getString(C4057R.string.config_slicePermissionComponent)));
        intent.putExtra("slice_uri", sliceUri);
        intent.putExtra("pkg", callingPackage);
        intent.setData(sliceUri.buildUpon().appendQueryParameter("package", callingPackage).build());
        return intent;
    }

    public static CharSequence getPermissionString(Context context, String callingPackage) {
        PackageManager pm = context.getPackageManager();
        try {
            return context.getString(C4057R.string.slices_permission_request, pm.getApplicationInfo(callingPackage, 0).loadLabel(pm), context.getApplicationInfo().loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unknown calling app", e);
        }
    }

    private Slice onBindSliceStrict(Uri sliceUri, List<SliceSpec> supportedSpecs) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy();
        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().build());
            return onBindSlice(sliceUri, new ArraySet(supportedSpecs));
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Process.sendSignal(Process.myPid(), 3);
        Log.wtf(TAG, "Timed out while handling slice callback " + this.mCallback);
    }
}
