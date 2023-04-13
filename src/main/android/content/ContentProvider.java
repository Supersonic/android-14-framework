package android.content;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.AppOpsManager;
import android.content.p001pm.PackageManager;
import android.content.p001pm.PathPermission;
import android.content.p001pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioSystem;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Binder;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.ICancellationSignal;
import android.p008os.ParcelFileDescriptor;
import android.p008os.ParcelableException;
import android.p008os.Process;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.internal.util.FrameworkStatsLog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class ContentProvider implements ContentInterface, ComponentCallbacks2 {
    private static final String TAG = "ContentProvider";
    private String[] mAuthorities;
    private String mAuthority;
    private ThreadLocal<AttributionSource> mCallingAttributionSource;
    private Context mContext;
    private boolean mExported;
    private int mMyUid;
    private boolean mNoPerms;
    private PathPermission[] mPathPermissions;
    private String mReadPermission;
    private boolean mSingleUser;
    private Transport mTransport;
    private SparseBooleanArray mUsersRedirectedToOwner;
    private String mWritePermission;

    /* loaded from: classes.dex */
    public interface PipeDataWriter<T> {
        void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, T t);
    }

    public abstract int delete(Uri uri, String str, String[] strArr);

    @Override // android.content.ContentInterface
    public abstract String getType(Uri uri);

    public abstract Uri insert(Uri uri, ContentValues contentValues);

    public abstract boolean onCreate();

    public abstract Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2);

    public abstract int update(Uri uri, ContentValues contentValues, String str, String[] strArr);

    public static boolean isAuthorityRedirectedForCloneProfile(String authority) {
        return "media".equals(authority);
    }

    public ContentProvider() {
        this.mContext = null;
        this.mUsersRedirectedToOwner = new SparseBooleanArray();
        this.mTransport = new Transport();
    }

    public ContentProvider(Context context, String readPermission, String writePermission, PathPermission[] pathPermissions) {
        this.mContext = null;
        this.mUsersRedirectedToOwner = new SparseBooleanArray();
        this.mTransport = new Transport();
        this.mContext = context;
        this.mReadPermission = readPermission;
        this.mWritePermission = writePermission;
        this.mPathPermissions = pathPermissions;
    }

    public static ContentProvider coerceToLocalContentProvider(IContentProvider abstractInterface) {
        if (abstractInterface instanceof Transport) {
            return ((Transport) abstractInterface).getContentProvider();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Transport extends ContentProviderNative {
        volatile ContentInterface mInterface;
        volatile AppOpsManager mAppOpsManager = null;
        volatile int mReadOp = -1;
        volatile int mWriteOp = -1;

        Transport() {
            this.mInterface = ContentProvider.this;
        }

        ContentProvider getContentProvider() {
            return ContentProvider.this;
        }

        @Override // android.content.ContentProviderNative
        public String getProviderName() {
            return getContentProvider().getClass().getName();
        }

        @Override // android.content.IContentProvider
        public Cursor query(AttributionSource attributionSource, Uri uri, String[] projection, Bundle queryArgs, ICancellationSignal cancellationSignal) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            if (enforceReadPermission(attributionSource, uri2) == 0) {
                ContentProvider.traceBegin(1048576L, "query: ", uri2.getAuthority());
                AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
                try {
                    try {
                        return this.mInterface.query(uri2, projection, queryArgs, CancellationSignal.fromTransport(cancellationSignal));
                    } catch (RemoteException e) {
                        throw e.rethrowAsRuntimeException();
                    }
                } finally {
                    ContentProvider.this.setCallingAttributionSource(original);
                    Trace.traceEnd(1048576L);
                }
            } else if (projection != null) {
                return new MatrixCursor(projection, 0);
            } else {
                AttributionSource original2 = ContentProvider.this.setCallingAttributionSource(attributionSource);
                try {
                    try {
                        Cursor cursor = this.mInterface.query(uri2, projection, queryArgs, CancellationSignal.fromTransport(cancellationSignal));
                        if (cursor == null) {
                            return null;
                        }
                        return new MatrixCursor(cursor.getColumnNames(), 0);
                    } catch (RemoteException e2) {
                        throw e2.rethrowAsRuntimeException();
                    }
                } finally {
                    ContentProvider.this.setCallingAttributionSource(original2);
                }
            }
        }

        @Override // android.content.IContentProvider
        public String getType(AttributionSource attributionSource, Uri uri) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            ContentProvider.traceBegin(1048576L, "getType: ", uri2.getAuthority());
            try {
                try {
                    if (checkGetTypePermission(attributionSource, uri2) == 0) {
                        String type = this.mInterface.getType(uri2);
                        if (type != null) {
                            logGetTypeData(Binder.getCallingUid(), uri2, type, true);
                        }
                        return type;
                    }
                    int callingUid = Binder.getCallingUid();
                    long origId = Binder.clearCallingIdentity();
                    try {
                        String type2 = ContentProvider.this.getTypeAnonymous(uri2);
                        if (type2 != null) {
                            logGetTypeData(callingUid, uri2, type2, false);
                        }
                        return type2;
                    } finally {
                        Binder.restoreCallingIdentity(origId);
                    }
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                Trace.traceEnd(1048576L);
            }
        }

        private void logGetTypeData(int callingUid, Uri uri, String type, boolean permissionCheckPassed) {
            if (!permissionCheckPassed) {
                FrameworkStatsLog.write(564, 4, callingUid, uri.getAuthority(), type);
                return;
            }
            try {
                ProviderInfo cpi = ContentProvider.this.mContext.getPackageManager().resolveContentProvider(uri.getAuthority(), PackageManager.ComponentInfoFlags.m190of(128L));
                int callingUserId = UserHandle.getUserId(callingUid);
                Uri userUri = (!ContentProvider.this.mSingleUser || UserHandle.isSameUser(ContentProvider.this.mMyUid, callingUid)) ? uri : ContentProvider.maybeAddUserId(uri, callingUserId);
                if (cpi.forceUriPermissions && this.mInterface.checkUriPermission(uri, callingUid, 1) != 0 && ContentProvider.this.getContext().checkUriPermission(userUri, Binder.getCallingPid(), callingUid, 1) != 0) {
                    FrameworkStatsLog.write(564, 5, callingUid, uri.getAuthority(), type);
                }
            } catch (Exception e) {
            }
        }

        @Override // android.content.IContentProvider
        public void getTypeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) {
            Bundle result = new Bundle();
            try {
                result.putString("result", getType(attributionSource, uri));
            } catch (Exception e) {
                result.putParcelable("error", new ParcelableException(e));
            }
            callback.sendResult(result);
        }

        @Override // android.content.IContentProvider
        public void getTypeAnonymousAsync(Uri uri, RemoteCallback callback) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            Bundle result = new Bundle();
            try {
                result.putString("result", ContentProvider.this.getTypeAnonymous(uri2));
            } catch (Exception e) {
                result.putParcelable("error", new ParcelableException(e));
            }
            callback.sendResult(result);
        }

        @Override // android.content.IContentProvider
        public Uri insert(AttributionSource attributionSource, Uri uri, ContentValues initialValues, Bundle extras) {
            Uri uri2 = ContentProvider.this.validateIncomingUri(uri);
            int userId = ContentProvider.getUserIdFromUri(uri2);
            Uri uri3 = ContentProvider.this.maybeGetUriWithoutUserId(uri2);
            if (enforceWritePermission(attributionSource, uri3) != 0) {
                AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
                try {
                    return ContentProvider.this.rejectInsert(uri3, initialValues);
                } finally {
                    ContentProvider.this.setCallingAttributionSource(original);
                }
            }
            ContentProvider.traceBegin(1048576L, "insert: ", uri3.getAuthority());
            AttributionSource original2 = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return ContentProvider.maybeAddUserId(this.mInterface.insert(uri3, initialValues, extras), userId);
                } finally {
                    ContentProvider.this.setCallingAttributionSource(original2);
                    Trace.traceEnd(1048576L);
                }
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }

        @Override // android.content.IContentProvider
        public int bulkInsert(AttributionSource attributionSource, Uri uri, ContentValues[] initialValues) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            if (enforceWritePermission(attributionSource, uri2) != 0) {
                return 0;
            }
            ContentProvider.traceBegin(1048576L, "bulkInsert: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.bulkInsert(uri2, initialValues);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public ContentProviderResult[] applyBatch(AttributionSource attributionSource, String authority, ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
            ContentProvider.this.validateIncomingAuthority(authority);
            int numOperations = operations.size();
            int[] userIds = new int[numOperations];
            for (int i = 0; i < numOperations; i++) {
                ContentProviderOperation operation = operations.get(i);
                Uri uri = operation.getUri();
                userIds[i] = ContentProvider.getUserIdFromUri(uri);
                Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
                if (!Objects.equals(operation.getUri(), uri2)) {
                    operation = new ContentProviderOperation(operation, uri2);
                    operations.set(i, operation);
                }
                if (operation.isReadOperation() && enforceReadPermission(attributionSource, uri2) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
                if (operation.isWriteOperation() && enforceWritePermission(attributionSource, uri2) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
            }
            ContentProvider.traceBegin(1048576L, "applyBatch: ", authority);
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    ContentProviderResult[] results = this.mInterface.applyBatch(authority, operations);
                    if (results != null) {
                        for (int i2 = 0; i2 < results.length; i2++) {
                            if (userIds[i2] != -2) {
                                results[i2] = new ContentProviderResult(results[i2], userIds[i2]);
                            }
                        }
                    }
                    return results;
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public int delete(AttributionSource attributionSource, Uri uri, Bundle extras) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            if (enforceWritePermission(attributionSource, uri2) != 0) {
                return 0;
            }
            ContentProvider.traceBegin(1048576L, "delete: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.delete(uri2, extras);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public int update(AttributionSource attributionSource, Uri uri, ContentValues values, Bundle extras) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            if (enforceWritePermission(attributionSource, uri2) != 0) {
                return 0;
            }
            ContentProvider.traceBegin(1048576L, "update: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.update(uri2, values, extras);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public ParcelFileDescriptor openFile(AttributionSource attributionSource, Uri uri, String mode, ICancellationSignal cancellationSignal) throws FileNotFoundException {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            enforceFilePermission(attributionSource, uri2, mode);
            ContentProvider.traceBegin(1048576L, "openFile: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.openFile(uri2, mode, CancellationSignal.fromTransport(cancellationSignal));
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openAssetFile(AttributionSource attributionSource, Uri uri, String mode, ICancellationSignal cancellationSignal) throws FileNotFoundException {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            enforceFilePermission(attributionSource, uri2, mode);
            ContentProvider.traceBegin(1048576L, "openAssetFile: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.openAssetFile(uri2, mode, CancellationSignal.fromTransport(cancellationSignal));
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public Bundle call(AttributionSource attributionSource, String authority, String method, String arg, Bundle extras) {
            ContentProvider.this.validateIncomingAuthority(authority);
            Bundle.setDefusable(extras, true);
            ContentProvider.traceBegin(1048576L, "call: ", authority);
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.call(authority, method, arg, extras);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            ContentProvider.traceBegin(1048576L, "getStreamTypes: ", uri2.getAuthority());
            try {
                try {
                    return this.mInterface.getStreamTypes(uri2, mimeTypeFilter);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openTypedAssetFile(AttributionSource attributionSource, Uri uri, String mimeType, Bundle opts, ICancellationSignal cancellationSignal) throws FileNotFoundException {
            Bundle.setDefusable(opts, true);
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            enforceFilePermission(attributionSource, uri2, "r");
            ContentProvider.traceBegin(1048576L, "openTypedAssetFile: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.openTypedAssetFile(uri2, mimeType, opts, CancellationSignal.fromTransport(cancellationSignal));
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public ICancellationSignal createCancellationSignal() {
            return CancellationSignal.createTransport();
        }

        @Override // android.content.IContentProvider
        public Uri canonicalize(AttributionSource attributionSource, Uri uri) {
            Uri uri2 = ContentProvider.this.validateIncomingUri(uri);
            int userId = ContentProvider.getUserIdFromUri(uri2);
            Uri uri3 = ContentProvider.getUriWithoutUserId(uri2);
            if (enforceReadPermission(attributionSource, uri3) != 0) {
                return null;
            }
            ContentProvider.traceBegin(1048576L, "canonicalize: ", uri3.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return ContentProvider.maybeAddUserId(this.mInterface.canonicalize(uri3), userId);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public void canonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) {
            Bundle result = new Bundle();
            try {
                result.putParcelable("result", canonicalize(attributionSource, uri));
            } catch (Exception e) {
                result.putParcelable("error", new ParcelableException(e));
            }
            callback.sendResult(result);
        }

        @Override // android.content.IContentProvider
        public Uri uncanonicalize(AttributionSource attributionSource, Uri uri) {
            Uri uri2 = ContentProvider.this.validateIncomingUri(uri);
            int userId = ContentProvider.getUserIdFromUri(uri2);
            Uri uri3 = ContentProvider.getUriWithoutUserId(uri2);
            if (enforceReadPermission(attributionSource, uri3) != 0) {
                return null;
            }
            ContentProvider.traceBegin(1048576L, "uncanonicalize: ", uri3.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return ContentProvider.maybeAddUserId(this.mInterface.uncanonicalize(uri3), userId);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public void uncanonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) {
            Bundle result = new Bundle();
            try {
                result.putParcelable("result", uncanonicalize(attributionSource, uri));
            } catch (Exception e) {
                result.putParcelable("error", new ParcelableException(e));
            }
            callback.sendResult(result);
        }

        @Override // android.content.IContentProvider
        public boolean refresh(AttributionSource attributionSource, Uri uri, Bundle extras, ICancellationSignal cancellationSignal) throws RemoteException {
            Uri uri2 = ContentProvider.getUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            if (enforceReadPermission(attributionSource, uri2) != 0) {
                return false;
            }
            ContentProvider.traceBegin(1048576L, "refresh: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                return this.mInterface.refresh(uri2, extras, CancellationSignal.fromTransport(cancellationSignal));
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        @Override // android.content.IContentProvider
        public int checkUriPermission(AttributionSource attributionSource, Uri uri, int uid, int modeFlags) {
            Uri uri2 = ContentProvider.this.maybeGetUriWithoutUserId(ContentProvider.this.validateIncomingUri(uri));
            ContentProvider.traceBegin(1048576L, "checkUriPermission: ", uri2.getAuthority());
            AttributionSource original = ContentProvider.this.setCallingAttributionSource(attributionSource);
            try {
                try {
                    return this.mInterface.checkUriPermission(uri2, uid, modeFlags);
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } finally {
                ContentProvider.this.setCallingAttributionSource(original);
                Trace.traceEnd(1048576L);
            }
        }

        private void enforceFilePermission(AttributionSource attributionSource, Uri uri, String mode) throws FileNotFoundException, SecurityException {
            if (mode != null && mode.indexOf(119) != -1) {
                if (enforceWritePermission(attributionSource, uri) != 0) {
                    throw new FileNotFoundException("App op not allowed");
                }
            } else if (enforceReadPermission(attributionSource, uri) != 0) {
                throw new FileNotFoundException("App op not allowed");
            }
        }

        private int enforceReadPermission(AttributionSource attributionSource, Uri uri) throws SecurityException {
            int result = ContentProvider.this.enforceReadPermissionInner(uri, attributionSource);
            if (result != 0) {
                return result;
            }
            if (ContentProvider.this.mTransport.mReadOp != -1 && ContentProvider.this.mTransport.mReadOp != AppOpsManager.permissionToOpCode(ContentProvider.this.mReadPermission)) {
                return PermissionChecker.checkOpForDataDelivery(ContentProvider.this.getContext(), AppOpsManager.opToPublicName(ContentProvider.this.mTransport.mReadOp), attributionSource, null);
            }
            return 0;
        }

        private int enforceWritePermission(AttributionSource attributionSource, Uri uri) throws SecurityException {
            int result = ContentProvider.this.enforceWritePermissionInner(uri, attributionSource);
            if (result != 0) {
                return result;
            }
            if (ContentProvider.this.mTransport.mWriteOp != -1 && ContentProvider.this.mTransport.mWriteOp != AppOpsManager.permissionToOpCode(ContentProvider.this.mWritePermission)) {
                return PermissionChecker.checkOpForDataDelivery(ContentProvider.this.getContext(), AppOpsManager.opToPublicName(ContentProvider.this.mTransport.mWriteOp), attributionSource, null);
            }
            return 0;
        }

        private int checkGetTypePermission(AttributionSource attributionSource, Uri uri) {
            int callingUid = Binder.getCallingUid();
            if (UserHandle.getAppId(callingUid) == 1000 || ContentProvider.this.checkPermission(Manifest.C0000permission.GET_ANY_PROVIDER_TYPE, attributionSource) == 0) {
                return 0;
            }
            try {
                return enforceReadPermission(attributionSource, uri);
            } catch (SecurityException e) {
                return 2;
            }
        }
    }

    boolean checkUser(int pid, int uid, Context context) {
        UserHandle parent;
        int callingUserId = UserHandle.getUserId(uid);
        if (callingUserId == context.getUserId() || this.mSingleUser || context.checkPermission(Manifest.C0000permission.INTERACT_ACROSS_USERS, pid, uid) == 0 || context.checkPermission(Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL, pid, uid) == 0) {
            return true;
        }
        if (isAuthorityRedirectedForCloneProfile(this.mAuthority)) {
            if (this.mUsersRedirectedToOwner.indexOfKey(callingUserId) >= 0) {
                return this.mUsersRedirectedToOwner.get(callingUserId);
            }
            try {
                UserHandle callingUser = UserHandle.getUserHandleForUid(uid);
                Context callingUserContext = this.mContext.createPackageContextAsUser("system", 0, callingUser);
                UserManager um = (UserManager) callingUserContext.getSystemService(UserManager.class);
                if (um != null && um.isCloneProfile() && (parent = um.getProfileParent(callingUser)) != null && parent.equals(Process.myUserHandle())) {
                    this.mUsersRedirectedToOwner.put(callingUserId, true);
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
            this.mUsersRedirectedToOwner.put(callingUserId, false);
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int checkPermission(String permission, AttributionSource attributionSource) {
        if (Binder.getCallingPid() == Process.myPid()) {
            return 0;
        }
        return PermissionChecker.checkPermissionForDataDeliveryFromDataSource(getContext(), permission, -1, new AttributionSource(getContext().getAttributionSource(), attributionSource), null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int enforceReadPermissionInner(Uri uri, AttributionSource attributionSource) throws SecurityException {
        String suffix;
        String missingPerm;
        Context context = getContext();
        int pid = Binder.getCallingPid();
        int uid = Binder.getCallingUid();
        String missingPerm2 = null;
        int strongestResult = 0;
        if (UserHandle.isSameApp(uid, this.mMyUid)) {
            return 0;
        }
        if (this.mExported && checkUser(pid, uid, context)) {
            String componentPerm = getReadPermission();
            if (componentPerm != null) {
                int result = checkPermission(componentPerm, attributionSource);
                if (result == 0) {
                    return 0;
                }
                missingPerm2 = componentPerm;
                strongestResult = Math.max(0, result);
            }
            boolean allowDefaultRead = componentPerm == null;
            PathPermission[] pps = getPathPermissions();
            if (pps != null) {
                String path = uri.getPath();
                for (PathPermission pp : pps) {
                    String pathPerm = pp.getReadPermission();
                    if (pathPerm != null && pp.match(path)) {
                        int result2 = checkPermission(pathPerm, attributionSource);
                        if (result2 == 0) {
                            return 0;
                        }
                        allowDefaultRead = false;
                        missingPerm = pathPerm;
                        strongestResult = Math.max(strongestResult, result2);
                    } else {
                        missingPerm = missingPerm2;
                    }
                    missingPerm2 = missingPerm;
                }
            }
            if (allowDefaultRead) {
                return 0;
            }
        }
        int callingUserId = UserHandle.getUserId(uid);
        Uri userUri = (!this.mSingleUser || UserHandle.isSameUser(this.mMyUid, uid)) ? uri : maybeAddUserId(uri, callingUserId);
        if (context.checkUriPermission(userUri, pid, uid, 1) == 0) {
            return 0;
        }
        if (strongestResult == 1) {
            return 1;
        }
        if (Manifest.C0000permission.MANAGE_DOCUMENTS.equals(this.mReadPermission)) {
            suffix = " requires that you obtain access using ACTION_OPEN_DOCUMENT or related APIs";
        } else if (this.mExported) {
            suffix = " requires " + missingPerm2 + ", or grantUriPermission()";
        } else {
            suffix = " requires the provider be exported, or grantUriPermission()";
        }
        throw new SecurityException("Permission Denial: reading " + getClass().getName() + " uri " + uri + " from pid=" + pid + ", uid=" + uid + suffix);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int enforceWritePermissionInner(Uri uri, AttributionSource attributionSource) throws SecurityException {
        String failReason;
        String missingPerm;
        Context context = getContext();
        int pid = Binder.getCallingPid();
        int uid = Binder.getCallingUid();
        String missingPerm2 = null;
        int strongestResult = 0;
        if (UserHandle.isSameApp(uid, this.mMyUid)) {
            return 0;
        }
        if (this.mExported && checkUser(pid, uid, context)) {
            String componentPerm = getWritePermission();
            if (componentPerm != null) {
                int mode = checkPermission(componentPerm, attributionSource);
                if (mode == 0) {
                    return 0;
                }
                missingPerm2 = componentPerm;
                strongestResult = Math.max(0, mode);
            }
            boolean allowDefaultWrite = componentPerm == null;
            PathPermission[] pps = getPathPermissions();
            if (pps != null) {
                String path = uri.getPath();
                for (PathPermission pp : pps) {
                    String pathPerm = pp.getWritePermission();
                    if (pathPerm != null && pp.match(path)) {
                        int mode2 = checkPermission(pathPerm, attributionSource);
                        if (mode2 == 0) {
                            return 0;
                        }
                        allowDefaultWrite = false;
                        missingPerm = pathPerm;
                        strongestResult = Math.max(strongestResult, mode2);
                    } else {
                        missingPerm = missingPerm2;
                    }
                    missingPerm2 = missingPerm;
                }
            }
            if (allowDefaultWrite) {
                return 0;
            }
        }
        if (context.checkUriPermission(uri, pid, uid, 2) == 0) {
            return 0;
        }
        if (strongestResult == 1) {
            return 1;
        }
        if (this.mExported) {
            failReason = " requires " + missingPerm2 + ", or grantUriPermission()";
        } else {
            failReason = " requires the provider be exported, or grantUriPermission()";
        }
        throw new SecurityException("Permission Denial: writing " + getClass().getName() + " uri " + uri + " from pid=" + pid + ", uid=" + uid + failReason);
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final Context requireContext() {
        Context ctx = getContext();
        if (ctx == null) {
            throw new IllegalStateException("Cannot find context from the provider.");
        }
        return ctx;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AttributionSource setCallingAttributionSource(AttributionSource attributionSource) {
        AttributionSource original = this.mCallingAttributionSource.get();
        this.mCallingAttributionSource.set(attributionSource);
        onCallingPackageChanged();
        return original;
    }

    public final String getCallingPackage() {
        AttributionSource callingAttributionSource = getCallingAttributionSource();
        if (callingAttributionSource != null) {
            return callingAttributionSource.getPackageName();
        }
        return null;
    }

    public final AttributionSource getCallingAttributionSource() {
        AttributionSource attributionSource = this.mCallingAttributionSource.get();
        if (attributionSource != null) {
            this.mTransport.mAppOpsManager.checkPackage(Binder.getCallingUid(), attributionSource.getPackageName());
        }
        return attributionSource;
    }

    public final String getCallingAttributionTag() {
        AttributionSource attributionSource = this.mCallingAttributionSource.get();
        if (attributionSource != null) {
            return attributionSource.getAttributionTag();
        }
        return null;
    }

    @Deprecated
    public final String getCallingFeatureId() {
        return getCallingAttributionTag();
    }

    public final String getCallingPackageUnchecked() {
        AttributionSource attributionSource = this.mCallingAttributionSource.get();
        if (attributionSource != null) {
            return attributionSource.getPackageName();
        }
        return null;
    }

    public void onCallingPackageChanged() {
    }

    /* loaded from: classes.dex */
    public final class CallingIdentity {
        public final long binderToken;
        public final AttributionSource callingAttributionSource;

        public CallingIdentity(long binderToken, AttributionSource attributionSource) {
            this.binderToken = binderToken;
            this.callingAttributionSource = attributionSource;
        }
    }

    public final CallingIdentity clearCallingIdentity() {
        return new CallingIdentity(Binder.clearCallingIdentity(), setCallingAttributionSource(null));
    }

    public final void restoreCallingIdentity(CallingIdentity identity) {
        Binder.restoreCallingIdentity(identity.binderToken);
        this.mCallingAttributionSource.set(identity.callingAttributionSource);
    }

    protected final void setAuthorities(String authorities) {
        if (authorities != null) {
            if (authorities.indexOf(59) == -1) {
                this.mAuthority = authorities;
                this.mAuthorities = null;
                return;
            }
            this.mAuthority = null;
            this.mAuthorities = authorities.split(NavigationBarInflaterView.GRAVITY_SEPARATOR);
        }
    }

    protected final boolean matchesOurAuthorities(String authority) {
        String str = this.mAuthority;
        if (str != null) {
            return str.equals(authority);
        }
        String[] strArr = this.mAuthorities;
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                if (this.mAuthorities[i].equals(authority)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected final void setReadPermission(String permission) {
        this.mReadPermission = permission;
    }

    public final String getReadPermission() {
        return this.mReadPermission;
    }

    protected final void setWritePermission(String permission) {
        this.mWritePermission = permission;
    }

    public final String getWritePermission() {
        return this.mWritePermission;
    }

    protected final void setPathPermissions(PathPermission[] permissions) {
        this.mPathPermissions = permissions;
    }

    public final PathPermission[] getPathPermissions() {
        return this.mPathPermissions;
    }

    public final void setAppOps(int readOp, int writeOp) {
        if (!this.mNoPerms) {
            this.mTransport.mReadOp = readOp;
            this.mTransport.mWriteOp = writeOp;
        }
    }

    public AppOpsManager getAppOpsManager() {
        return this.mTransport.mAppOpsManager;
    }

    public final void setTransportLoggingEnabled(boolean enabled) {
        Transport transport = this.mTransport;
        if (transport == null) {
            return;
        }
        if (enabled) {
            transport.mInterface = new LoggingContentInterface(getClass().getSimpleName(), this);
        } else {
            transport.mInterface = this;
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override // android.content.ContentInterface
    public Cursor query(Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) {
        Bundle queryArgs2 = queryArgs != null ? queryArgs : Bundle.EMPTY;
        String sortClause = queryArgs2.getString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
        if (sortClause == null && queryArgs2.containsKey(ContentResolver.QUERY_ARG_SORT_COLUMNS)) {
            sortClause = ContentResolver.createSqlSortClause(queryArgs2);
        }
        return query(uri, projection, queryArgs2.getString(ContentResolver.QUERY_ARG_SQL_SELECTION), queryArgs2.getStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS), sortClause, cancellationSignal);
    }

    public String getTypeAnonymous(Uri uri) {
        return getType(uri);
    }

    @Override // android.content.ContentInterface
    public Uri canonicalize(Uri url) {
        return null;
    }

    @Override // android.content.ContentInterface
    public Uri uncanonicalize(Uri url) {
        return url;
    }

    @Override // android.content.ContentInterface
    public boolean refresh(Uri uri, Bundle extras, CancellationSignal cancellationSignal) {
        return false;
    }

    @Override // android.content.ContentInterface
    @SystemApi
    public int checkUriPermission(Uri uri, int uid, int modeFlags) {
        return -1;
    }

    public Uri rejectInsert(Uri uri, ContentValues values) {
        return uri.buildUpon().appendPath(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS).build();
    }

    @Override // android.content.ContentInterface
    public Uri insert(Uri uri, ContentValues values, Bundle extras) {
        return insert(uri, values);
    }

    @Override // android.content.ContentInterface
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;
        for (ContentValues contentValues : values) {
            insert(uri, contentValues);
        }
        return numValues;
    }

    @Override // android.content.ContentInterface
    public int delete(Uri uri, Bundle extras) {
        Bundle extras2 = extras != null ? extras : Bundle.EMPTY;
        return delete(uri, extras2.getString(ContentResolver.QUERY_ARG_SQL_SELECTION), extras2.getStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS));
    }

    @Override // android.content.ContentInterface
    public int update(Uri uri, ContentValues values, Bundle extras) {
        Bundle extras2 = extras != null ? extras : Bundle.EMPTY;
        return update(uri, values, extras2.getString(ContentResolver.QUERY_ARG_SQL_SELECTION), extras2.getStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS));
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        throw new FileNotFoundException("No files supported by provider at " + uri);
    }

    @Override // android.content.ContentInterface
    public ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        return openFile(uri, mode);
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        ParcelFileDescriptor fd = openFile(uri, mode);
        if (fd != null) {
            return new AssetFileDescriptor(fd, 0L, -1L);
        }
        return null;
    }

    @Override // android.content.ContentInterface
    public AssetFileDescriptor openAssetFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        return openAssetFile(uri, mode);
    }

    protected final ParcelFileDescriptor openFileHelper(Uri uri, String mode) throws FileNotFoundException {
        Cursor c = query(uri, new String[]{"_data"}, null, null, null);
        int count = c != null ? c.getCount() : 0;
        if (count != 1) {
            if (c != null) {
                c.close();
            }
            if (count == 0) {
                throw new FileNotFoundException("No entry for " + uri);
            }
            throw new FileNotFoundException("Multiple items at " + uri);
        }
        c.moveToFirst();
        int i = c.getColumnIndex("_data");
        String path = i >= 0 ? c.getString(i) : null;
        c.close();
        if (path == null) {
            throw new FileNotFoundException("Column _data not found.");
        }
        int modeBits = ParcelFileDescriptor.parseMode(mode);
        return ParcelFileDescriptor.open(new File(path), modeBits);
    }

    @Override // android.content.ContentInterface
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return null;
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) throws FileNotFoundException {
        if ("*/*".equals(mimeTypeFilter)) {
            return openAssetFile(uri, "r");
        }
        String baseType = getType(uri);
        if (baseType != null && ClipDescription.compareMimeTypes(baseType, mimeTypeFilter)) {
            return openAssetFile(uri, "r");
        }
        throw new FileNotFoundException("Can't open " + uri + " as type " + mimeTypeFilter);
    }

    @Override // android.content.ContentInterface
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws FileNotFoundException {
        return openTypedAssetFile(uri, mimeTypeFilter, opts);
    }

    public <T> ParcelFileDescriptor openPipeHelper(final Uri uri, final String mimeType, final Bundle opts, final T args, final PipeDataWriter<T> func) throws FileNotFoundException {
        try {
            final ParcelFileDescriptor[] fds = ParcelFileDescriptor.createPipe();
            AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() { // from class: android.content.ContentProvider.1
                @Override // android.p008os.AsyncTask
                protected Object doInBackground(Object... params) {
                    func.writeDataToPipe(fds[1], uri, mimeType, opts, args);
                    try {
                        fds[1].close();
                        return null;
                    } catch (IOException e) {
                        Log.m103w(ContentProvider.TAG, "Failure closing pipe", e);
                        return null;
                    }
                }
            };
            Object[] objArr = null;
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            return fds[0];
        } catch (IOException e) {
            throw new FileNotFoundException("failure making pipe");
        }
    }

    protected boolean isTemporary() {
        return false;
    }

    public IContentProvider getIContentProvider() {
        return this.mTransport;
    }

    public void attachInfoForTesting(Context context, ProviderInfo info) {
        attachInfo(context, info, true);
    }

    public void attachInfo(Context context, ProviderInfo info) {
        attachInfo(context, info, false);
    }

    private void attachInfo(Context context, ProviderInfo info, boolean testing) {
        Transport transport;
        this.mNoPerms = testing;
        this.mCallingAttributionSource = new ThreadLocal<>();
        if (this.mContext == null) {
            this.mContext = context;
            if (context != null && (transport = this.mTransport) != null) {
                transport.mAppOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            }
            this.mMyUid = Process.myUid();
            if (info != null) {
                setReadPermission(info.readPermission);
                setWritePermission(info.writePermission);
                setPathPermissions(info.pathPermissions);
                this.mExported = info.exported;
                this.mSingleUser = (info.flags & 1073741824) != 0;
                setAuthorities(info.authority);
            }
            if (Build.IS_DEBUGGABLE) {
                setTransportLoggingEnabled(Log.isLoggable(getClass().getSimpleName(), 2));
            }
            onCreate();
        }
    }

    @Override // android.content.ContentInterface
    public ContentProviderResult[] applyBatch(String authority, ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        return applyBatch(operations);
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        int numOperations = operations.size();
        ContentProviderResult[] results = new ContentProviderResult[numOperations];
        for (int i = 0; i < numOperations; i++) {
            results[i] = operations.get(i).apply(this, results, i);
        }
        return results;
    }

    @Override // android.content.ContentInterface
    public Bundle call(String authority, String method, String arg, Bundle extras) {
        return call(method, arg, extras);
    }

    public Bundle call(String method, String arg, Bundle extras) {
        return null;
    }

    public void shutdown() {
        Log.m104w(TAG, "implement ContentProvider shutdown() to make sure all database connections are gracefully shutdown");
    }

    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("nothing to dump");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void validateIncomingAuthority(String authority) throws SecurityException {
        String message;
        if (!matchesOurAuthorities(getAuthorityWithoutUserId(authority))) {
            String message2 = "The authority " + authority + " does not match the one of the contentProvider: ";
            if (this.mAuthority != null) {
                message = message2 + this.mAuthority;
            } else {
                message = message2 + Arrays.toString(this.mAuthorities);
            }
            throw new SecurityException(message);
        }
    }

    public Uri validateIncomingUri(Uri uri) throws SecurityException {
        int userId;
        String auth = uri.getAuthority();
        if (!this.mSingleUser && (userId = getUserIdFromAuthority(auth, -2)) != -2 && userId != this.mContext.getUserId()) {
            throw new SecurityException("trying to query a ContentProvider in user " + this.mContext.getUserId() + " with a uri belonging to user " + userId);
        }
        validateIncomingAuthority(auth);
        String encodedPath = uri.getEncodedPath();
        if (encodedPath != null && encodedPath.indexOf("//") != -1) {
            Uri normalized = uri.buildUpon().encodedPath(encodedPath.replaceAll("//+", "/")).build();
            Log.m104w(TAG, "Normalized " + uri + " to " + normalized + " to avoid possible security issues");
            return normalized;
        }
        return uri;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri maybeGetUriWithoutUserId(Uri uri) {
        if (this.mSingleUser) {
            return uri;
        }
        return getUriWithoutUserId(uri);
    }

    public static int getUserIdFromAuthority(String auth, int defaultUserId) {
        int end;
        if (auth == null || (end = auth.lastIndexOf(64)) == -1) {
            return defaultUserId;
        }
        String userIdString = auth.substring(0, end);
        try {
            return Integer.parseInt(userIdString);
        } catch (NumberFormatException e) {
            Log.m103w(TAG, "Error parsing userId.", e);
            return -10000;
        }
    }

    public static int getUserIdFromAuthority(String auth) {
        return getUserIdFromAuthority(auth, -2);
    }

    public static int getUserIdFromUri(Uri uri, int defaultUserId) {
        return uri == null ? defaultUserId : getUserIdFromAuthority(uri.getAuthority(), defaultUserId);
    }

    public static int getUserIdFromUri(Uri uri) {
        return getUserIdFromUri(uri, -2);
    }

    public static UserHandle getUserHandleFromUri(Uri uri) {
        return UserHandle.m145of(getUserIdFromUri(uri, Process.myUserHandle().getIdentifier()));
    }

    public static String getAuthorityWithoutUserId(String auth) {
        if (auth == null) {
            return null;
        }
        int end = auth.lastIndexOf(64);
        return auth.substring(end + 1);
    }

    public static Uri getUriWithoutUserId(Uri uri) {
        if (uri == null) {
            return null;
        }
        Uri.Builder builder = uri.buildUpon();
        builder.authority(getAuthorityWithoutUserId(uri.getAuthority()));
        return builder.build();
    }

    public static boolean uriHasUserId(Uri uri) {
        if (uri == null) {
            return false;
        }
        return !TextUtils.isEmpty(uri.getUserInfo());
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static Uri createContentUriForUser(Uri contentUri, UserHandle userHandle) {
        if (!"content".equals(contentUri.getScheme())) {
            throw new IllegalArgumentException(String.format("Given URI [%s] is not a content URI: ", contentUri));
        }
        int userId = userHandle.getIdentifier();
        if (uriHasUserId(contentUri)) {
            if (String.valueOf(userId).equals(contentUri.getUserInfo())) {
                return contentUri;
            }
            throw new IllegalArgumentException(String.format("Given URI [%s] already has a user ID, different from given user handle [%s]", contentUri, Integer.valueOf(userId)));
        }
        Uri.Builder builder = contentUri.buildUpon();
        builder.encodedAuthority("" + userHandle.getIdentifier() + "@" + contentUri.getEncodedAuthority());
        return builder.build();
    }

    public static Uri maybeAddUserId(Uri uri, int userId) {
        if (uri == null) {
            return null;
        }
        if (userId != -2 && "content".equals(uri.getScheme()) && !uriHasUserId(uri)) {
            Uri.Builder builder = uri.buildUpon();
            builder.encodedAuthority("" + userId + "@" + uri.getEncodedAuthority());
            return builder.build();
        }
        return uri;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void traceBegin(long traceTag, String methodName, String subInfo) {
        if (Trace.isTagEnabled(traceTag)) {
            Trace.traceBegin(traceTag, methodName + subInfo);
        }
    }
}
