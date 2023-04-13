package com.android.server.slice;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.slice.DirtyTracker;
import com.android.server.slice.SliceProviderPermissions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes2.dex */
public class SlicePermissionManager implements DirtyTracker {
    public final String ATT_VERSION;
    public final ArrayMap<PkgUser, SliceClientPermissions> mCachedClients;
    public final ArrayMap<PkgUser, SliceProviderPermissions> mCachedProviders;
    public final Context mContext;
    public final ArraySet<DirtyTracker.Persistable> mDirty;
    public final Handler mHandler;
    public final File mSliceDir;

    public static /* synthetic */ void lambda$writeBackup$0(DirtyTracker.Persistable persistable) {
    }

    @VisibleForTesting
    public SlicePermissionManager(Context context, Looper looper, File file) {
        this.ATT_VERSION = "version";
        this.mCachedProviders = new ArrayMap<>();
        this.mCachedClients = new ArrayMap<>();
        this.mDirty = new ArraySet<>();
        this.mContext = context;
        this.mHandler = new HandlerC1613H(looper);
        this.mSliceDir = file;
    }

    public SlicePermissionManager(Context context, Looper looper) {
        this(context, looper, new File(Environment.getDataDirectory(), "system/slice"));
    }

    public void grantFullAccess(String str, int i) {
        getClient(new PkgUser(str, i)).setHasFullAccess(true);
    }

    public void grantSliceAccess(String str, int i, String str2, int i2, Uri uri) {
        PkgUser pkgUser = new PkgUser(str, i);
        PkgUser pkgUser2 = new PkgUser(str2, i2);
        getClient(pkgUser).grantUri(uri, pkgUser2);
        getProvider(pkgUser2).getOrCreateAuthority(ContentProvider.getUriWithoutUserId(uri).getAuthority()).addPkg(pkgUser);
    }

    public void revokeSliceAccess(String str, int i, String str2, int i2, Uri uri) {
        PkgUser pkgUser = new PkgUser(str, i);
        getClient(pkgUser).revokeUri(uri, new PkgUser(str2, i2));
    }

    public void removePkg(String str, int i) {
        PkgUser pkgUser = new PkgUser(str, i);
        for (SliceProviderPermissions.SliceAuthority sliceAuthority : getProvider(pkgUser).getAuthorities()) {
            for (PkgUser pkgUser2 : sliceAuthority.getPkgs()) {
                getClient(pkgUser2).removeAuthority(sliceAuthority.getAuthority(), i);
            }
        }
        getClient(pkgUser).clear();
        this.mHandler.obtainMessage(3, pkgUser).sendToTarget();
    }

    public String[] getAllPackagesGranted(String str) {
        ArraySet arraySet = new ArraySet();
        for (SliceProviderPermissions.SliceAuthority sliceAuthority : getProvider(new PkgUser(str, 0)).getAuthorities()) {
            for (PkgUser pkgUser : sliceAuthority.getPkgs()) {
                arraySet.add(pkgUser.mPkg);
            }
        }
        return (String[]) arraySet.toArray(new String[arraySet.size()]);
    }

    public boolean hasFullAccess(String str, int i) {
        return getClient(new PkgUser(str, i)).hasFullAccess();
    }

    public boolean hasPermission(String str, int i, Uri uri) {
        SliceClientPermissions client = getClient(new PkgUser(str, i));
        return client.hasFullAccess() || client.hasPermission(ContentProvider.getUriWithoutUserId(uri), ContentProvider.getUserIdFromUri(uri, i));
    }

    @Override // com.android.server.slice.DirtyTracker
    public void onPersistableDirty(DirtyTracker.Persistable persistable) {
        this.mHandler.removeMessages(2);
        this.mHandler.obtainMessage(1, persistable).sendToTarget();
        this.mHandler.sendEmptyMessageDelayed(2, 500L);
    }

    public void writeBackup(XmlSerializer xmlSerializer) throws IOException, XmlPullParserException {
        String[] list;
        DirtyTracker.Persistable persistable;
        synchronized (this) {
            xmlSerializer.startTag(null, "slice-access-list");
            xmlSerializer.attribute(null, "version", String.valueOf(2));
            DirtyTracker dirtyTracker = new DirtyTracker() { // from class: com.android.server.slice.SlicePermissionManager$$ExternalSyntheticLambda0
                @Override // com.android.server.slice.DirtyTracker
                public final void onPersistableDirty(DirtyTracker.Persistable persistable2) {
                    SlicePermissionManager.lambda$writeBackup$0(persistable2);
                }
            };
            if (this.mHandler.hasMessages(2)) {
                this.mHandler.removeMessages(2);
                handlePersist();
            }
            for (String str : new File(this.mSliceDir.getAbsolutePath()).list()) {
                ParserHolder parser = getParser(str);
                while (true) {
                    if (parser.parser.getEventType() == 1) {
                        persistable = null;
                        break;
                    } else if (parser.parser.getEventType() == 2) {
                        if ("client".equals(parser.parser.getName())) {
                            persistable = SliceClientPermissions.createFrom(parser.parser, dirtyTracker);
                        } else {
                            persistable = SliceProviderPermissions.createFrom(parser.parser, dirtyTracker);
                        }
                    } else {
                        parser.parser.next();
                    }
                }
                if (persistable != null) {
                    persistable.writeTo(xmlSerializer);
                } else {
                    Slog.w("SlicePermissionManager", "Invalid or empty slice permissions file: " + str);
                }
                if (parser != null) {
                    parser.close();
                }
            }
            xmlSerializer.endTag(null, "slice-access-list");
        }
    }

    public void readRestore(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        synchronized (this) {
            while (true) {
                if ((xmlPullParser.getEventType() != 2 || !"slice-access-list".equals(xmlPullParser.getName())) && xmlPullParser.getEventType() != 1) {
                    xmlPullParser.next();
                }
            }
            if (XmlUtils.readIntAttribute(xmlPullParser, "version", 0) < 2) {
                return;
            }
            while (xmlPullParser.getEventType() != 1) {
                if (xmlPullParser.getEventType() == 2) {
                    if ("client".equals(xmlPullParser.getName())) {
                        SliceClientPermissions createFrom = SliceClientPermissions.createFrom(xmlPullParser, this);
                        synchronized (this.mCachedClients) {
                            this.mCachedClients.put(createFrom.getPkg(), createFrom);
                        }
                        onPersistableDirty(createFrom);
                        Handler handler = this.mHandler;
                        handler.sendMessageDelayed(handler.obtainMessage(4, createFrom.getPkg()), BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                    } else if ("provider".equals(xmlPullParser.getName())) {
                        SliceProviderPermissions createFrom2 = SliceProviderPermissions.createFrom(xmlPullParser, this);
                        synchronized (this.mCachedProviders) {
                            this.mCachedProviders.put(createFrom2.getPkg(), createFrom2);
                        }
                        onPersistableDirty(createFrom2);
                        Handler handler2 = this.mHandler;
                        handler2.sendMessageDelayed(handler2.obtainMessage(5, createFrom2.getPkg()), BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                    } else {
                        xmlPullParser.next();
                    }
                } else {
                    xmlPullParser.next();
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x0062 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final SliceClientPermissions getClient(PkgUser pkgUser) {
        SliceClientPermissions sliceClientPermissions;
        synchronized (this.mCachedClients) {
            sliceClientPermissions = this.mCachedClients.get(pkgUser);
        }
        if (sliceClientPermissions == null) {
            try {
                ParserHolder parser = getParser(SliceClientPermissions.getFileName(pkgUser));
                try {
                    SliceClientPermissions createFrom = SliceClientPermissions.createFrom(parser.parser, this);
                    synchronized (this.mCachedClients) {
                        this.mCachedClients.put(pkgUser, createFrom);
                    }
                    Handler handler = this.mHandler;
                    handler.sendMessageDelayed(handler.obtainMessage(4, pkgUser), BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                    if (parser != null) {
                        parser.close();
                    }
                    return createFrom;
                } catch (Throwable th) {
                    if (parser != null) {
                        try {
                            parser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException unused) {
                sliceClientPermissions = new SliceClientPermissions(pkgUser, this);
                synchronized (this.mCachedClients) {
                    this.mCachedClients.put(pkgUser, sliceClientPermissions);
                }
                return sliceClientPermissions;
            } catch (IOException e) {
                Log.e("SlicePermissionManager", "Can't read client", e);
                sliceClientPermissions = new SliceClientPermissions(pkgUser, this);
                synchronized (this.mCachedClients) {
                }
            } catch (XmlPullParserException e2) {
                Log.e("SlicePermissionManager", "Can't read client", e2);
                sliceClientPermissions = new SliceClientPermissions(pkgUser, this);
                synchronized (this.mCachedClients) {
                }
            }
        }
        return sliceClientPermissions;
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x0062 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final SliceProviderPermissions getProvider(PkgUser pkgUser) {
        SliceProviderPermissions sliceProviderPermissions;
        synchronized (this.mCachedProviders) {
            sliceProviderPermissions = this.mCachedProviders.get(pkgUser);
        }
        if (sliceProviderPermissions == null) {
            try {
                ParserHolder parser = getParser(SliceProviderPermissions.getFileName(pkgUser));
                try {
                    SliceProviderPermissions createFrom = SliceProviderPermissions.createFrom(parser.parser, this);
                    synchronized (this.mCachedProviders) {
                        this.mCachedProviders.put(pkgUser, createFrom);
                    }
                    Handler handler = this.mHandler;
                    handler.sendMessageDelayed(handler.obtainMessage(5, pkgUser), BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                    if (parser != null) {
                        parser.close();
                    }
                    return createFrom;
                } catch (Throwable th) {
                    if (parser != null) {
                        try {
                            parser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException unused) {
                sliceProviderPermissions = new SliceProviderPermissions(pkgUser, this);
                synchronized (this.mCachedProviders) {
                    this.mCachedProviders.put(pkgUser, sliceProviderPermissions);
                }
                return sliceProviderPermissions;
            } catch (IOException e) {
                Log.e("SlicePermissionManager", "Can't read provider", e);
                sliceProviderPermissions = new SliceProviderPermissions(pkgUser, this);
                synchronized (this.mCachedProviders) {
                }
            } catch (XmlPullParserException e2) {
                Log.e("SlicePermissionManager", "Can't read provider", e2);
                sliceProviderPermissions = new SliceProviderPermissions(pkgUser, this);
                synchronized (this.mCachedProviders) {
                }
            }
        }
        return sliceProviderPermissions;
    }

    public final ParserHolder getParser(String str) throws FileNotFoundException, XmlPullParserException {
        AtomicFile file = getFile(str);
        ParserHolder parserHolder = new ParserHolder();
        parserHolder.input = file.openRead();
        parserHolder.parser = XmlPullParserFactory.newInstance().newPullParser();
        parserHolder.parser.setInput(parserHolder.input, Xml.Encoding.UTF_8.name());
        return parserHolder;
    }

    public final AtomicFile getFile(String str) {
        if (!this.mSliceDir.exists()) {
            this.mSliceDir.mkdir();
        }
        return new AtomicFile(new File(this.mSliceDir, str));
    }

    @VisibleForTesting
    public void handlePersist() {
        synchronized (this) {
            Iterator<DirtyTracker.Persistable> it = this.mDirty.iterator();
            while (it.hasNext()) {
                DirtyTracker.Persistable next = it.next();
                AtomicFile file = getFile(next.getFileName());
                try {
                    FileOutputStream startWrite = file.startWrite();
                    try {
                        XmlSerializer newSerializer = XmlPullParserFactory.newInstance().newSerializer();
                        newSerializer.setOutput(startWrite, Xml.Encoding.UTF_8.name());
                        next.writeTo(newSerializer);
                        newSerializer.flush();
                        file.finishWrite(startWrite);
                    } catch (IOException | RuntimeException | XmlPullParserException e) {
                        Slog.w("SlicePermissionManager", "Failed to save access file, restoring backup", e);
                        file.failWrite(startWrite);
                    }
                } catch (IOException e2) {
                    Slog.w("SlicePermissionManager", "Failed to save access file", e2);
                    return;
                }
            }
            this.mDirty.clear();
        }
    }

    @VisibleForTesting
    public void addDirtyImmediate(DirtyTracker.Persistable persistable) {
        this.mDirty.add(persistable);
    }

    public final void handleRemove(PkgUser pkgUser) {
        getFile(SliceClientPermissions.getFileName(pkgUser)).delete();
        getFile(SliceProviderPermissions.getFileName(pkgUser)).delete();
        this.mDirty.remove(this.mCachedClients.remove(pkgUser));
        this.mDirty.remove(this.mCachedProviders.remove(pkgUser));
    }

    /* renamed from: com.android.server.slice.SlicePermissionManager$H */
    /* loaded from: classes2.dex */
    public final class HandlerC1613H extends Handler {
        public HandlerC1613H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                SlicePermissionManager.this.mDirty.add((DirtyTracker.Persistable) message.obj);
            } else if (i == 2) {
                SlicePermissionManager.this.handlePersist();
            } else if (i == 3) {
                SlicePermissionManager.this.handleRemove((PkgUser) message.obj);
            } else if (i == 4) {
                synchronized (SlicePermissionManager.this.mCachedClients) {
                    SlicePermissionManager.this.mCachedClients.remove(message.obj);
                }
            } else if (i != 5) {
            } else {
                synchronized (SlicePermissionManager.this.mCachedProviders) {
                    SlicePermissionManager.this.mCachedProviders.remove(message.obj);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class PkgUser {
        public final String mPkg;
        public final int mUserId;

        public PkgUser(String str, int i) {
            this.mPkg = str;
            this.mUserId = i;
        }

        public PkgUser(String str) throws IllegalArgumentException {
            try {
                String[] split = str.split("@", 2);
                this.mPkg = split[0];
                this.mUserId = Integer.parseInt(split[1]);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public String getPkg() {
            return this.mPkg;
        }

        public int getUserId() {
            return this.mUserId;
        }

        public int hashCode() {
            return this.mPkg.hashCode() + this.mUserId;
        }

        public boolean equals(Object obj) {
            if (getClass().equals(obj != null ? obj.getClass() : null)) {
                PkgUser pkgUser = (PkgUser) obj;
                return Objects.equals(pkgUser.mPkg, this.mPkg) && pkgUser.mUserId == this.mUserId;
            }
            return false;
        }

        public String toString() {
            return String.format("%s@%d", this.mPkg, Integer.valueOf(this.mUserId));
        }
    }

    /* loaded from: classes2.dex */
    public class ParserHolder implements AutoCloseable {
        public InputStream input;
        public XmlPullParser parser;

        public ParserHolder() {
        }

        @Override // java.lang.AutoCloseable
        public void close() throws IOException {
            this.input.close();
        }
    }
}
