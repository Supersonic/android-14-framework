package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.util.Log;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class LoggingContentInterface implements ContentInterface {
    private final ContentInterface delegate;
    private final String tag;

    public LoggingContentInterface(String tag, ContentInterface delegate) {
        this.tag = tag;
        this.delegate = delegate;
    }

    /* loaded from: classes.dex */
    private class Logger implements AutoCloseable {

        /* renamed from: sb */
        private final StringBuilder f43sb = new StringBuilder();

        public Logger(String method, Object... args) {
            for (Object arg : args) {
                if (arg instanceof Bundle) {
                    ((Bundle) arg).size();
                }
            }
            this.f43sb.append("callingUid=").append(Binder.getCallingUid()).append(' ');
            this.f43sb.append(method);
            this.f43sb.append('(').append(deepToString(args)).append(')');
        }

        private String deepToString(Object value) {
            if (value != null && value.getClass().isArray()) {
                return Arrays.deepToString((Object[]) value);
            }
            return String.valueOf(value);
        }

        public <T> T setResult(T res) {
            if (res instanceof Cursor) {
                this.f43sb.append('\n');
                DatabaseUtils.dumpCursor((Cursor) res, this.f43sb);
            } else {
                this.f43sb.append(" = ").append(deepToString(res));
            }
            return res;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            Log.m106v(LoggingContentInterface.this.tag, this.f43sb.toString());
        }
    }

    @Override // android.content.ContentInterface
    public Cursor query(Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) throws RemoteException {
        Logger l = new Logger("query", uri, projection, queryArgs, cancellationSignal);
        try {
            try {
                Cursor cursor = (Cursor) l.setResult(this.delegate.query(uri, projection, queryArgs, cancellationSignal));
                l.close();
                return cursor;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public String getType(Uri uri) throws RemoteException {
        Logger l = new Logger("getType", uri);
        try {
            try {
                String str = (String) l.setResult(this.delegate.getType(uri));
                l.close();
                return str;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) throws RemoteException {
        Logger l = new Logger("getStreamTypes", uri, mimeTypeFilter);
        try {
            try {
                String[] strArr = (String[]) l.setResult(this.delegate.getStreamTypes(uri, mimeTypeFilter));
                l.close();
                return strArr;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public Uri canonicalize(Uri uri) throws RemoteException {
        Logger l = new Logger("canonicalize", uri);
        try {
            try {
                Uri uri2 = (Uri) l.setResult(this.delegate.canonicalize(uri));
                l.close();
                return uri2;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public Uri uncanonicalize(Uri uri) throws RemoteException {
        Logger l = new Logger("uncanonicalize", uri);
        try {
            try {
                Uri uri2 = (Uri) l.setResult(this.delegate.uncanonicalize(uri));
                l.close();
                return uri2;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public boolean refresh(Uri uri, Bundle args, CancellationSignal cancellationSignal) throws RemoteException {
        Logger l = new Logger("refresh", uri, args, cancellationSignal);
        try {
            try {
                boolean booleanValue = ((Boolean) l.setResult(Boolean.valueOf(this.delegate.refresh(uri, args, cancellationSignal)))).booleanValue();
                l.close();
                return booleanValue;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public int checkUriPermission(Uri uri, int uid, int modeFlags) throws RemoteException {
        Logger l = new Logger("checkUriPermission", uri, Integer.valueOf(uid), Integer.valueOf(modeFlags));
        try {
            try {
                int intValue = ((Integer) l.setResult(Integer.valueOf(this.delegate.checkUriPermission(uri, uid, modeFlags)))).intValue();
                l.close();
                return intValue;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public Uri insert(Uri uri, ContentValues initialValues, Bundle extras) throws RemoteException {
        Logger l = new Logger("insert", uri, initialValues, extras);
        try {
            try {
                Uri uri2 = (Uri) l.setResult(this.delegate.insert(uri, initialValues, extras));
                l.close();
                return uri2;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public int bulkInsert(Uri uri, ContentValues[] initialValues) throws RemoteException {
        Logger l = new Logger("bulkInsert", uri, initialValues);
        try {
            try {
                int intValue = ((Integer) l.setResult(Integer.valueOf(this.delegate.bulkInsert(uri, initialValues)))).intValue();
                l.close();
                return intValue;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public int delete(Uri uri, Bundle extras) throws RemoteException {
        Logger l = new Logger("delete", uri, extras);
        try {
            try {
                int intValue = ((Integer) l.setResult(Integer.valueOf(this.delegate.delete(uri, extras)))).intValue();
                l.close();
                return intValue;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public int update(Uri uri, ContentValues values, Bundle extras) throws RemoteException {
        Logger l = new Logger("update", uri, values, extras);
        try {
            try {
                int intValue = ((Integer) l.setResult(Integer.valueOf(this.delegate.update(uri, values, extras)))).intValue();
                l.close();
                return intValue;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        Logger l = new Logger("openFile", uri, mode, signal);
        try {
            try {
                ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) l.setResult(this.delegate.openFile(uri, mode, signal));
                l.close();
                return parcelFileDescriptor;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public AssetFileDescriptor openAssetFile(Uri uri, String mode, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        Logger l = new Logger("openAssetFile", uri, mode, signal);
        try {
            try {
                AssetFileDescriptor assetFileDescriptor = (AssetFileDescriptor) l.setResult(this.delegate.openAssetFile(uri, mode, signal));
                l.close();
                return assetFileDescriptor;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        Logger l = new Logger("openTypedAssetFile", uri, mimeTypeFilter, opts, signal);
        try {
            try {
                AssetFileDescriptor assetFileDescriptor = (AssetFileDescriptor) l.setResult(this.delegate.openTypedAssetFile(uri, mimeTypeFilter, opts, signal));
                l.close();
                return assetFileDescriptor;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public ContentProviderResult[] applyBatch(String authority, ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
        Logger l = new Logger("applyBatch", authority, operations);
        try {
            try {
                ContentProviderResult[] contentProviderResultArr = (ContentProviderResult[]) l.setResult(this.delegate.applyBatch(authority, operations));
                l.close();
                return contentProviderResultArr;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // android.content.ContentInterface
    public Bundle call(String authority, String method, String arg, Bundle extras) throws RemoteException {
        Logger l = new Logger("call", authority, method, arg, extras);
        try {
            try {
                Bundle bundle = (Bundle) l.setResult(this.delegate.call(authority, method, arg, extras));
                l.close();
                return bundle;
            } catch (Exception res) {
                l.setResult(res);
                throw res;
            }
        } catch (Throwable th) {
            try {
                l.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }
}
