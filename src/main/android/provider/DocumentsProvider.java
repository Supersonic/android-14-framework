package android.provider;

import android.Manifest;
import android.content.ClipDescription;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.content.MimeTypeFilter;
import android.content.UriMatcher;
import android.content.p001pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.ParcelFileDescriptor;
import android.p008os.ParcelableException;
import android.provider.DocumentsContract;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Objects;
import libcore.io.IoUtils;
/* loaded from: classes3.dex */
public abstract class DocumentsProvider extends ContentProvider {
    private static final int MATCH_CHILDREN = 6;
    private static final int MATCH_CHILDREN_TREE = 8;
    private static final int MATCH_DOCUMENT = 5;
    private static final int MATCH_DOCUMENT_TREE = 7;
    private static final int MATCH_RECENT = 3;
    private static final int MATCH_ROOT = 2;
    private static final int MATCH_ROOTS = 1;
    private static final int MATCH_SEARCH = 4;
    private static final String TAG = "DocumentsProvider";
    private String mAuthority;
    private UriMatcher mMatcher;

    public abstract ParcelFileDescriptor openDocument(String str, String str2, CancellationSignal cancellationSignal) throws FileNotFoundException;

    public abstract Cursor queryChildDocuments(String str, String[] strArr, String str2) throws FileNotFoundException;

    public abstract Cursor queryDocument(String str, String[] strArr) throws FileNotFoundException;

    public abstract Cursor queryRoots(String[] strArr) throws FileNotFoundException;

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        registerAuthority(info.authority);
        if (!info.exported) {
            throw new SecurityException("Provider must be exported");
        }
        if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grantUriPermissions");
        }
        if (!Manifest.C0000permission.MANAGE_DOCUMENTS.equals(info.readPermission) || !Manifest.C0000permission.MANAGE_DOCUMENTS.equals(info.writePermission)) {
            throw new SecurityException("Provider must be protected by MANAGE_DOCUMENTS");
        }
        super.attachInfo(context, info);
    }

    @Override // android.content.ContentProvider
    public void attachInfoForTesting(Context context, ProviderInfo info) {
        registerAuthority(info.authority);
        super.attachInfoForTesting(context, info);
    }

    private void registerAuthority(String authority) {
        this.mAuthority = authority;
        UriMatcher uriMatcher = new UriMatcher(-1);
        this.mMatcher = uriMatcher;
        uriMatcher.addURI(this.mAuthority, "root", 1);
        this.mMatcher.addURI(this.mAuthority, "root/*", 2);
        this.mMatcher.addURI(this.mAuthority, "root/*/recent", 3);
        this.mMatcher.addURI(this.mAuthority, "root/*/search", 4);
        this.mMatcher.addURI(this.mAuthority, "document/*", 5);
        this.mMatcher.addURI(this.mAuthority, "document/*/children", 6);
        this.mMatcher.addURI(this.mAuthority, "tree/*/document/*", 7);
        this.mMatcher.addURI(this.mAuthority, "tree/*/document/*/children", 8);
    }

    public boolean isChildDocument(String parentDocumentId, String documentId) {
        return false;
    }

    private void enforceTreeForExtraUris(Bundle extras) {
        enforceTree((Uri) extras.getParcelable("uri", Uri.class));
        enforceTree((Uri) extras.getParcelable(DocumentsContract.EXTRA_PARENT_URI, Uri.class));
        enforceTree((Uri) extras.getParcelable(DocumentsContract.EXTRA_TARGET_URI, Uri.class));
    }

    private void enforceTree(Uri documentUri) {
        if (documentUri != null && DocumentsContract.isTreeUri(documentUri)) {
            String parent = DocumentsContract.getTreeDocumentId(documentUri);
            String child = DocumentsContract.getDocumentId(documentUri);
            if (!Objects.equals(parent, child) && !isChildDocument(parent, child)) {
                throw new SecurityException("Document " + child + " is not a descendant of " + parent);
            }
        }
    }

    private Uri validateIncomingNullableUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        return validateIncomingUri(uri);
    }

    public String createDocument(String parentDocumentId, String mimeType, String displayName) throws FileNotFoundException {
        throw new UnsupportedOperationException("Create not supported");
    }

    public String renameDocument(String documentId, String displayName) throws FileNotFoundException {
        throw new UnsupportedOperationException("Rename not supported");
    }

    public void deleteDocument(String documentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("Delete not supported");
    }

    public String copyDocument(String sourceDocumentId, String targetParentDocumentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("Copy not supported");
    }

    public String moveDocument(String sourceDocumentId, String sourceParentDocumentId, String targetParentDocumentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("Move not supported");
    }

    public void removeDocument(String documentId, String parentDocumentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("Remove not supported");
    }

    public DocumentsContract.Path findDocumentPath(String parentDocumentId, String childDocumentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("findDocumentPath not supported.");
    }

    public IntentSender createWebLinkIntent(String documentId, Bundle options) throws FileNotFoundException {
        throw new UnsupportedOperationException("createWebLink is not supported.");
    }

    public Cursor queryRecentDocuments(String rootId, String[] projection) throws FileNotFoundException {
        throw new UnsupportedOperationException("Recent not supported");
    }

    public Cursor queryRecentDocuments(String rootId, String[] projection, Bundle queryArgs, CancellationSignal signal) throws FileNotFoundException {
        Preconditions.checkNotNull(rootId, "rootId can not be null");
        Cursor c = queryRecentDocuments(rootId, projection);
        Bundle extras = new Bundle();
        c.setExtras(extras);
        extras.putStringArray(ContentResolver.EXTRA_HONORED_ARGS, new String[0]);
        return c;
    }

    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, Bundle queryArgs) throws FileNotFoundException {
        return queryChildDocuments(parentDocumentId, projection, getSortClause(queryArgs));
    }

    public Cursor queryChildDocumentsForManage(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
        throw new UnsupportedOperationException("Manage not supported");
    }

    public Cursor querySearchDocuments(String rootId, String query, String[] projection) throws FileNotFoundException {
        throw new UnsupportedOperationException("Search not supported");
    }

    public Cursor querySearchDocuments(String rootId, String[] projection, Bundle queryArgs) throws FileNotFoundException {
        Preconditions.checkNotNull(rootId, "rootId can not be null");
        Preconditions.checkNotNull(queryArgs, "queryArgs can not be null");
        return querySearchDocuments(rootId, DocumentsContract.getSearchDocumentsQuery(queryArgs), projection);
    }

    public void ejectRoot(String rootId) {
        throw new UnsupportedOperationException("Eject not supported");
    }

    public Bundle getDocumentMetadata(String documentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("Metadata not supported");
    }

    public String getDocumentType(String documentId) throws FileNotFoundException {
        Cursor cursor = queryDocument(documentId, null);
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return cursor.getString(cursor.getColumnIndexOrThrow("mime_type"));
        } finally {
            IoUtils.closeQuietly(cursor);
        }
    }

    public AssetFileDescriptor openDocumentThumbnail(String documentId, Point sizeHint, CancellationSignal signal) throws FileNotFoundException {
        throw new UnsupportedOperationException("Thumbnails not supported");
    }

    public AssetFileDescriptor openTypedDocument(String documentId, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws FileNotFoundException {
        throw new FileNotFoundException("The requested MIME type is not supported.");
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Pre-Android-O query format not supported.");
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        throw new UnsupportedOperationException("Pre-Android-O query format not supported.");
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final Cursor query(Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) {
        try {
            switch (this.mMatcher.match(uri)) {
                case 1:
                    return queryRoots(projection);
                case 2:
                default:
                    throw new UnsupportedOperationException("Unsupported Uri " + uri);
                case 3:
                    return queryRecentDocuments(DocumentsContract.getRootId(uri), projection, queryArgs, cancellationSignal);
                case 4:
                    return querySearchDocuments(DocumentsContract.getRootId(uri), projection, queryArgs);
                case 5:
                case 7:
                    enforceTree(uri);
                    return queryDocument(DocumentsContract.getDocumentId(uri), projection);
                case 6:
                case 8:
                    enforceTree(uri);
                    if (DocumentsContract.isManageMode(uri)) {
                        return queryChildDocumentsForManage(DocumentsContract.getDocumentId(uri), projection, getSortClause(queryArgs));
                    }
                    return queryChildDocuments(DocumentsContract.getDocumentId(uri), projection, queryArgs);
            }
        } catch (FileNotFoundException e) {
            Log.m103w(TAG, "Failed during query", e);
            return null;
        }
    }

    private static String getSortClause(Bundle queryArgs) {
        Bundle queryArgs2 = queryArgs != null ? queryArgs : Bundle.EMPTY;
        String sortClause = queryArgs2.getString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
        if (sortClause == null && queryArgs2.containsKey(ContentResolver.QUERY_ARG_SORT_COLUMNS)) {
            return ContentResolver.createSqlSortClause(queryArgs2);
        }
        return sortClause;
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final String getType(Uri uri) {
        String str = null;
        try {
            switch (this.mMatcher.match(uri)) {
                case 2:
                    return DocumentsContract.Root.MIME_TYPE_ITEM;
                case 5:
                case 7:
                    enforceTree(uri);
                    str = getDocumentType(DocumentsContract.getDocumentId(uri));
                    return str;
                default:
                    return null;
            }
        } catch (FileNotFoundException e) {
            Log.m103w(TAG, "Failed during getType", e);
            return str;
        }
    }

    @Override // android.content.ContentProvider
    public final String getTypeAnonymous(Uri uri) {
        switch (this.mMatcher.match(uri)) {
            case 2:
                return DocumentsContract.Root.MIME_TYPE_ITEM;
            default:
                return null;
        }
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public Uri canonicalize(Uri uri) {
        Context context = getContext();
        switch (this.mMatcher.match(uri)) {
            case 7:
                enforceTree(uri);
                Uri narrowUri = DocumentsContract.buildDocumentUri(uri.getAuthority(), DocumentsContract.getDocumentId(uri));
                int modeFlags = getCallingOrSelfUriPermissionModeFlags(context, uri);
                context.grantUriPermission(getCallingPackage(), narrowUri, modeFlags);
                return narrowUri;
            default:
                return null;
        }
    }

    private static int getCallingOrSelfUriPermissionModeFlags(Context context, Uri uri) {
        int modeFlags = 0;
        if (context.checkCallingOrSelfUriPermission(uri, 1) == 0) {
            modeFlags = 0 | 1;
        }
        if (context.checkCallingOrSelfUriPermission(uri, 2) == 0) {
            modeFlags |= 2;
        }
        if (context.checkCallingOrSelfUriPermission(uri, 65) == 0) {
            return modeFlags | 64;
        }
        return modeFlags;
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Insert not supported");
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not supported");
    }

    @Override // android.content.ContentProvider
    public Bundle call(String method, String arg, Bundle extras) {
        if (!method.startsWith("android:")) {
            return super.call(method, arg, extras);
        }
        try {
            return callUnchecked(method, arg, extras);
        } catch (FileNotFoundException e) {
            throw new ParcelableException(e);
        }
    }

    private Bundle callUnchecked(String method, String arg, Bundle extras) throws FileNotFoundException {
        String parentDocumentId;
        Context context = getContext();
        Bundle out = new Bundle();
        enforceTreeForExtraUris(extras);
        Uri extraUri = validateIncomingNullableUri((Uri) extras.getParcelable("uri", Uri.class));
        Uri extraTargetUri = validateIncomingNullableUri((Uri) extras.getParcelable(DocumentsContract.EXTRA_TARGET_URI, Uri.class));
        Uri extraParentUri = validateIncomingNullableUri((Uri) extras.getParcelable(DocumentsContract.EXTRA_PARENT_URI, Uri.class));
        if (DocumentsContract.METHOD_EJECT_ROOT.equals(method)) {
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            String rootId = DocumentsContract.getRootId(extraUri);
            ejectRoot(rootId);
            return out;
        }
        String authority = extraUri.getAuthority();
        String documentId = DocumentsContract.getDocumentId(extraUri);
        if (!this.mAuthority.equals(authority)) {
            throw new SecurityException("Requested authority " + authority + " doesn't match provider " + this.mAuthority);
        }
        if (DocumentsContract.METHOD_IS_CHILD_DOCUMENT.equals(method)) {
            enforceReadPermissionInner(extraUri, getCallingAttributionSource());
            String childAuthority = extraTargetUri.getAuthority();
            String childId = DocumentsContract.getDocumentId(extraTargetUri);
            out.putBoolean("result", this.mAuthority.equals(childAuthority) && isChildDocument(documentId, childId));
        } else if (DocumentsContract.METHOD_CREATE_DOCUMENT.equals(method)) {
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            String mimeType = extras.getString("mime_type");
            String displayName = extras.getString("_display_name");
            out.putParcelable("uri", DocumentsContract.buildDocumentUriMaybeUsingTree(extraUri, createDocument(documentId, mimeType, displayName)));
        } else if (DocumentsContract.METHOD_CREATE_WEB_LINK_INTENT.equals(method)) {
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            Bundle options = extras.getBundle(DocumentsContract.EXTRA_OPTIONS);
            IntentSender intentSender = createWebLinkIntent(documentId, options);
            out.putParcelable("result", intentSender);
        } else if (DocumentsContract.METHOD_RENAME_DOCUMENT.equals(method)) {
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            String displayName2 = extras.getString("_display_name");
            String newDocumentId = renameDocument(documentId, displayName2);
            if (newDocumentId != null) {
                Uri newDocumentUri = DocumentsContract.buildDocumentUriMaybeUsingTree(extraUri, newDocumentId);
                if (!DocumentsContract.isTreeUri(newDocumentUri)) {
                    int modeFlags = getCallingOrSelfUriPermissionModeFlags(context, extraUri);
                    context.grantUriPermission(getCallingPackage(), newDocumentUri, modeFlags);
                }
                out.putParcelable("uri", newDocumentUri);
                revokeDocumentPermission(documentId);
            }
        } else if (DocumentsContract.METHOD_DELETE_DOCUMENT.equals(method)) {
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            deleteDocument(documentId);
            revokeDocumentPermission(documentId);
        } else if (DocumentsContract.METHOD_COPY_DOCUMENT.equals(method)) {
            String targetId = DocumentsContract.getDocumentId(extraTargetUri);
            enforceReadPermissionInner(extraUri, getCallingAttributionSource());
            enforceWritePermissionInner(extraTargetUri, getCallingAttributionSource());
            String newDocumentId2 = copyDocument(documentId, targetId);
            if (newDocumentId2 != null) {
                Uri newDocumentUri2 = DocumentsContract.buildDocumentUriMaybeUsingTree(extraUri, newDocumentId2);
                if (!DocumentsContract.isTreeUri(newDocumentUri2)) {
                    int modeFlags2 = getCallingOrSelfUriPermissionModeFlags(context, extraUri);
                    context.grantUriPermission(getCallingPackage(), newDocumentUri2, modeFlags2);
                }
                out.putParcelable("uri", newDocumentUri2);
            }
        } else if (DocumentsContract.METHOD_MOVE_DOCUMENT.equals(method)) {
            String parentSourceId = DocumentsContract.getDocumentId(extraParentUri);
            String targetId2 = DocumentsContract.getDocumentId(extraTargetUri);
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            enforceReadPermissionInner(extraParentUri, getCallingAttributionSource());
            enforceWritePermissionInner(extraTargetUri, getCallingAttributionSource());
            String newDocumentId3 = moveDocument(documentId, parentSourceId, targetId2);
            if (newDocumentId3 != null) {
                Uri newDocumentUri3 = DocumentsContract.buildDocumentUriMaybeUsingTree(extraUri, newDocumentId3);
                if (!DocumentsContract.isTreeUri(newDocumentUri3)) {
                    int modeFlags3 = getCallingOrSelfUriPermissionModeFlags(context, extraUri);
                    context.grantUriPermission(getCallingPackage(), newDocumentUri3, modeFlags3);
                }
                out.putParcelable("uri", newDocumentUri3);
            }
        } else if (DocumentsContract.METHOD_REMOVE_DOCUMENT.equals(method)) {
            String parentSourceId2 = DocumentsContract.getDocumentId(extraParentUri);
            enforceReadPermissionInner(extraParentUri, getCallingAttributionSource());
            enforceWritePermissionInner(extraUri, getCallingAttributionSource());
            removeDocument(documentId, parentSourceId2);
        } else if (DocumentsContract.METHOD_FIND_DOCUMENT_PATH.equals(method)) {
            boolean isTreeUri = DocumentsContract.isTreeUri(extraUri);
            if (isTreeUri) {
                enforceReadPermissionInner(extraUri, getCallingAttributionSource());
            } else {
                getContext().enforceCallingPermission(Manifest.C0000permission.MANAGE_DOCUMENTS, null);
            }
            if (isTreeUri) {
                parentDocumentId = DocumentsContract.getTreeDocumentId(extraUri);
            } else {
                parentDocumentId = null;
            }
            DocumentsContract.Path path = findDocumentPath(parentDocumentId, documentId);
            if (isTreeUri) {
                if (!Objects.equals(path.getPath().get(0), parentDocumentId)) {
                    Log.wtf(TAG, "Provider doesn't return path from the tree root. Expected: " + parentDocumentId + " found: " + path.getPath().get(0));
                    LinkedList<String> docs = new LinkedList<>(path.getPath());
                    while (docs.size() > 1 && !Objects.equals(docs.getFirst(), parentDocumentId)) {
                        docs.removeFirst();
                    }
                    path = new DocumentsContract.Path(null, docs);
                }
                if (path.getRootId() != null) {
                    Log.wtf(TAG, "Provider returns root id :" + path.getRootId() + " unexpectedly. Erase root id.");
                    path = new DocumentsContract.Path(null, path.getPath());
                }
            }
            out.putParcelable("result", path);
        } else if (DocumentsContract.METHOD_GET_DOCUMENT_METADATA.equals(method)) {
            return getDocumentMetadata(documentId);
        } else {
            throw new UnsupportedOperationException("Method not supported " + method);
        }
        return out;
    }

    public final void revokeDocumentPermission(String documentId) {
        Context context = getContext();
        context.revokeUriPermission(DocumentsContract.buildDocumentUri(this.mAuthority, documentId), -1);
        context.revokeUriPermission(DocumentsContract.buildTreeDocumentUri(this.mAuthority, documentId), -1);
    }

    @Override // android.content.ContentProvider
    public final ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        enforceTree(uri);
        return openDocument(DocumentsContract.getDocumentId(uri), mode, null);
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        enforceTree(uri);
        return openDocument(DocumentsContract.getDocumentId(uri), mode, signal);
    }

    @Override // android.content.ContentProvider
    public final AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        enforceTree(uri);
        ParcelFileDescriptor fd = openDocument(DocumentsContract.getDocumentId(uri), mode, null);
        if (fd != null) {
            return new AssetFileDescriptor(fd, 0L, -1L);
        }
        return null;
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final AssetFileDescriptor openAssetFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        enforceTree(uri);
        ParcelFileDescriptor fd = openDocument(DocumentsContract.getDocumentId(uri), mode, signal);
        if (fd != null) {
            return new AssetFileDescriptor(fd, 0L, -1L);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) throws FileNotFoundException {
        return openTypedAssetFileImpl(uri, mimeTypeFilter, opts, null);
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws FileNotFoundException {
        return openTypedAssetFileImpl(uri, mimeTypeFilter, opts, signal);
    }

    public String[] getDocumentStreamTypes(String documentId, String mimeTypeFilter) {
        Cursor cursor = null;
        try {
            cursor = queryDocument(documentId, null);
            if (cursor.moveToFirst()) {
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow("mime_type"));
                long flags = cursor.getLong(cursor.getColumnIndexOrThrow("flags"));
                if ((512 & flags) == 0 && mimeType != null && MimeTypeFilter.matches(mimeType, mimeTypeFilter)) {
                    return new String[]{mimeType};
                }
            }
            return null;
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            IoUtils.closeQuietly(cursor);
        }
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        enforceTree(uri);
        return getDocumentStreamTypes(DocumentsContract.getDocumentId(uri), mimeTypeFilter);
    }

    private final AssetFileDescriptor openTypedAssetFileImpl(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws FileNotFoundException {
        enforceTree(uri);
        String documentId = DocumentsContract.getDocumentId(uri);
        if (opts != null && opts.containsKey(ContentResolver.EXTRA_SIZE)) {
            Point sizeHint = (Point) opts.getParcelable(ContentResolver.EXTRA_SIZE, Point.class);
            return openDocumentThumbnail(documentId, sizeHint, signal);
        } else if ("*/*".equals(mimeTypeFilter)) {
            return openAssetFile(uri, "r");
        } else {
            String baseType = getType(uri);
            if (baseType != null && ClipDescription.compareMimeTypes(baseType, mimeTypeFilter)) {
                return openAssetFile(uri, "r");
            }
            return openTypedDocument(documentId, mimeTypeFilter, opts, signal);
        }
    }
}
