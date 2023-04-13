package android.p008os;

import android.content.Context;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import android.util.Log;
import com.android.internal.p028os.IDropBoxManagerService;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
/* renamed from: android.os.DropBoxManager */
/* loaded from: classes3.dex */
public class DropBoxManager {
    public static final String ACTION_DROPBOX_ENTRY_ADDED = "android.intent.action.DROPBOX_ENTRY_ADDED";
    public static final String EXTRA_DROPPED_COUNT = "android.os.extra.DROPPED_COUNT";
    public static final String EXTRA_TAG = "tag";
    public static final String EXTRA_TIME = "time";
    private static final int HAS_BYTE_ARRAY = 8;
    public static final int IS_EMPTY = 1;
    public static final int IS_GZIPPED = 4;
    public static final int IS_TEXT = 2;
    private static final String TAG = "DropBoxManager";
    private final Context mContext;
    private final IDropBoxManagerService mService;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.DropBoxManager$Flags */
    /* loaded from: classes3.dex */
    public @interface Flags {
    }

    /* renamed from: android.os.DropBoxManager$Entry */
    /* loaded from: classes3.dex */
    public static class Entry implements Parcelable, Closeable {
        public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator() { // from class: android.os.DropBoxManager.Entry.1
            @Override // android.p008os.Parcelable.Creator
            public Entry[] newArray(int size) {
                return new Entry[size];
            }

            @Override // android.p008os.Parcelable.Creator
            public Entry createFromParcel(Parcel in) {
                String tag = in.readString();
                long millis = in.readLong();
                int flags = in.readInt();
                if ((flags & 8) != 0) {
                    return new Entry(tag, millis, in.createByteArray(), flags & (-9));
                }
                ParcelFileDescriptor pfd = ParcelFileDescriptor.CREATOR.createFromParcel(in);
                return new Entry(tag, millis, pfd, flags);
            }
        };
        private final byte[] mData;
        private final ParcelFileDescriptor mFileDescriptor;
        private final int mFlags;
        private final String mTag;
        private final long mTimeMillis;

        public Entry(String tag, long millis) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = null;
            this.mFileDescriptor = null;
            this.mFlags = 1;
        }

        public Entry(String tag, long millis, String text) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if (text == null) {
                throw new NullPointerException("text == null");
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = text.getBytes(StandardCharsets.UTF_8);
            this.mFileDescriptor = null;
            this.mFlags = 2;
        }

        public Entry(String tag, long millis, byte[] data, int flags) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if (((flags & 1) != 0) != (data == null)) {
                throw new IllegalArgumentException("Bad flags: " + flags);
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = data;
            this.mFileDescriptor = null;
            this.mFlags = flags;
        }

        public Entry(String tag, long millis, ParcelFileDescriptor data, int flags) {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if (((flags & 1) != 0) != (data == null)) {
                throw new IllegalArgumentException("Bad flags: " + flags);
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = null;
            this.mFileDescriptor = data;
            this.mFlags = flags;
        }

        public Entry(String tag, long millis, File data, int flags) throws IOException {
            if (tag == null) {
                throw new NullPointerException("tag == null");
            }
            if ((flags & 1) != 0) {
                throw new IllegalArgumentException("Bad flags: " + flags);
            }
            this.mTag = tag;
            this.mTimeMillis = millis;
            this.mData = null;
            this.mFileDescriptor = ParcelFileDescriptor.open(data, 268435456);
            this.mFlags = flags;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            try {
                ParcelFileDescriptor parcelFileDescriptor = this.mFileDescriptor;
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
            }
        }

        public String getTag() {
            return this.mTag;
        }

        public long getTimeMillis() {
            return this.mTimeMillis;
        }

        public int getFlags() {
            return this.mFlags & (-5);
        }

        public String getText(int maxBytes) {
            if ((this.mFlags & 2) == 0) {
                return null;
            }
            byte[] bArr = this.mData;
            if (bArr != null) {
                return new String(bArr, 0, Math.min(maxBytes, bArr.length));
            }
            InputStream is = null;
            try {
                is = getInputStream();
                if (is == null) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                    return null;
                }
                byte[] buf = new byte[maxBytes];
                int readBytes = 0;
                int n = 0;
                while (n >= 0) {
                    int i = readBytes + n;
                    readBytes = i;
                    if (i >= maxBytes) {
                        break;
                    }
                    n = is.read(buf, readBytes, maxBytes - readBytes);
                }
                String str = new String(buf, 0, readBytes);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                }
                return str;
            } catch (IOException e3) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e4) {
                    }
                }
                return null;
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e5) {
                    }
                }
                throw th;
            }
        }

        public InputStream getInputStream() throws IOException {
            InputStream is;
            if (this.mData != null) {
                is = new ByteArrayInputStream(this.mData);
            } else if (this.mFileDescriptor != null) {
                is = new ParcelFileDescriptor.AutoCloseInputStream(this.mFileDescriptor);
            } else {
                return null;
            }
            return (this.mFlags & 4) != 0 ? new GZIPInputStream(new BufferedInputStream(is)) : is;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return this.mFileDescriptor != null ? 1 : 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this.mTag);
            out.writeLong(this.mTimeMillis);
            if (this.mFileDescriptor != null) {
                out.writeInt(this.mFlags & (-9));
                this.mFileDescriptor.writeToParcel(out, flags);
                return;
            }
            out.writeInt(this.mFlags | 8);
            out.writeByteArray(this.mData);
        }
    }

    public DropBoxManager(Context context, IDropBoxManagerService service) {
        this.mContext = context;
        this.mService = service;
    }

    protected DropBoxManager() {
        this.mContext = null;
        this.mService = null;
    }

    public void addText(String tag, String data) {
        addData(tag, data.getBytes(StandardCharsets.UTF_8), 2);
    }

    public void addData(String tag, byte[] data, int flags) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        try {
            this.mService.addData(tag, data, flags);
        } catch (RemoteException e) {
            if ((e instanceof TransactionTooLargeException) && this.mContext.getApplicationInfo().targetSdkVersion < 24) {
                Log.m109e(TAG, "App sent too much data, so it was ignored", e);
                return;
            }
            throw e.rethrowFromSystemServer();
        }
    }

    public void addFile(String tag, File file, int flags) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        try {
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(file, 268435456);
            this.mService.addFile(tag, pfd, flags);
            if (pfd != null) {
                pfd.close();
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isTagEnabled(String tag) {
        try {
            return this.mService.isTagEnabled(tag);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Entry getNextEntry(String tag, long msec) {
        try {
            return this.mService.getNextEntryWithAttribution(tag, msec, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (SecurityException e2) {
            if (this.mContext.getApplicationInfo().targetSdkVersion >= 28) {
                throw e2;
            }
            Log.m104w(TAG, e2.getMessage());
            return null;
        }
    }
}
