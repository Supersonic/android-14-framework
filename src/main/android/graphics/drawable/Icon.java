package android.graphics.drawable;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.graphics.PorterDuff;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.NtpTrustedTime;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Icon implements Parcelable {
    public static final int MIN_ASHMEM_ICON_SIZE = 131072;
    private static final String TAG = "Icon";
    public static final int TYPE_ADAPTIVE_BITMAP = 5;
    public static final int TYPE_BITMAP = 1;
    public static final int TYPE_DATA = 3;
    public static final int TYPE_RESOURCE = 2;
    public static final int TYPE_URI = 4;
    public static final int TYPE_URI_ADAPTIVE_BITMAP = 6;
    private static final int VERSION_STREAM_SERIALIZER = 1;
    private BlendMode mBlendMode;
    private boolean mCachedAshmem;
    private int mInt1;
    private int mInt2;
    private Object mObj1;
    private String mString1;
    private ColorStateList mTintList;
    private final int mType;
    static final BlendMode DEFAULT_BLEND_MODE = Drawable.DEFAULT_BLEND_MODE;
    public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() { // from class: android.graphics.drawable.Icon.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };

    /* loaded from: classes.dex */
    public @interface IconType {
    }

    /* loaded from: classes.dex */
    public interface OnDrawableLoadedListener {
        void onDrawableLoaded(Drawable drawable);
    }

    public int getType() {
        return this.mType;
    }

    public Bitmap getBitmap() {
        int i = this.mType;
        if (i != 1 && i != 5) {
            throw new IllegalStateException("called getBitmap() on " + this);
        }
        return (Bitmap) this.mObj1;
    }

    private void setBitmap(Bitmap b) {
        if (b.isMutable()) {
            this.mObj1 = b.copy(b.getConfig(), false);
        } else {
            this.mObj1 = b;
        }
        this.mCachedAshmem = false;
    }

    public int getDataLength() {
        int i;
        if (this.mType != 3) {
            throw new IllegalStateException("called getDataLength() on " + this);
        }
        synchronized (this) {
            i = this.mInt1;
        }
        return i;
    }

    public int getDataOffset() {
        int i;
        if (this.mType != 3) {
            throw new IllegalStateException("called getDataOffset() on " + this);
        }
        synchronized (this) {
            i = this.mInt2;
        }
        return i;
    }

    public byte[] getDataBytes() {
        byte[] bArr;
        if (this.mType != 3) {
            throw new IllegalStateException("called getDataBytes() on " + this);
        }
        synchronized (this) {
            bArr = (byte[]) this.mObj1;
        }
        return bArr;
    }

    public Resources getResources() {
        if (this.mType != 2) {
            throw new IllegalStateException("called getResources() on " + this);
        }
        return (Resources) this.mObj1;
    }

    public String getResPackage() {
        if (this.mType != 2) {
            throw new IllegalStateException("called getResPackage() on " + this);
        }
        return this.mString1;
    }

    public int getResId() {
        if (this.mType != 2) {
            throw new IllegalStateException("called getResId() on " + this);
        }
        return this.mInt1;
    }

    public String getUriString() {
        int i = this.mType;
        if (i != 4 && i != 6) {
            throw new IllegalStateException("called getUriString() on " + this);
        }
        return this.mString1;
    }

    public Uri getUri() {
        return Uri.parse(getUriString());
    }

    private static final String typeToString(int x) {
        switch (x) {
            case 1:
                return "BITMAP";
            case 2:
                return "RESOURCE";
            case 3:
                return "DATA";
            case 4:
                return "URI";
            case 5:
                return "BITMAP_MASKABLE";
            case 6:
                return "URI_MASKABLE";
            default:
                return "UNKNOWN";
        }
    }

    public void loadDrawableAsync(Context context, Message andThen) {
        if (andThen.getTarget() == null) {
            throw new IllegalArgumentException("callback message must have a target handler");
        }
        new LoadDrawableTask(context, andThen).runAsync();
    }

    public void loadDrawableAsync(Context context, OnDrawableLoadedListener listener, Handler handler) {
        new LoadDrawableTask(context, handler, listener).runAsync();
    }

    public Drawable loadDrawable(Context context) {
        Drawable result = loadDrawableInner(context);
        if (result != null && hasTint()) {
            result.mutate();
            result.setTintList(this.mTintList);
            result.setTintBlendMode(this.mBlendMode);
        }
        return result;
    }

    private Drawable loadDrawableInner(Context context) {
        switch (this.mType) {
            case 1:
                return new BitmapDrawable(context.getResources(), getBitmap());
            case 2:
                if (getResources() == null) {
                    String resPackage = getResPackage();
                    if (TextUtils.isEmpty(resPackage)) {
                        resPackage = context.getPackageName();
                    }
                    if ("android".equals(resPackage)) {
                        this.mObj1 = Resources.getSystem();
                    } else {
                        PackageManager pm = context.getPackageManager();
                        try {
                            ApplicationInfo ai = pm.getApplicationInfo(resPackage, 9216);
                            if (ai != null) {
                                this.mObj1 = pm.getResourcesForApplication(ai);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.m109e(TAG, String.format("Unable to find pkg=%s for icon %s", resPackage, this), e);
                            break;
                        }
                    }
                }
                try {
                    return getResources().getDrawable(getResId(), context.getTheme());
                } catch (RuntimeException e2) {
                    Log.m109e(TAG, String.format("Unable to load resource 0x%08x from pkg=%s", Integer.valueOf(getResId()), getResPackage()), e2);
                    break;
                }
                break;
            case 3:
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(getDataBytes(), getDataOffset(), getDataLength()));
            case 4:
                InputStream is = getUriInputStream(context);
                if (is != null) {
                    return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(is));
                }
                break;
            case 5:
                return new AdaptiveIconDrawable((Drawable) null, new BitmapDrawable(context.getResources(), getBitmap()));
            case 6:
                InputStream is2 = getUriInputStream(context);
                if (is2 != null) {
                    return new AdaptiveIconDrawable((Drawable) null, new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(is2)));
                }
                break;
        }
        return null;
    }

    private InputStream getUriInputStream(Context context) {
        Uri uri = getUri();
        String scheme = uri.getScheme();
        if ("content".equals(scheme) || "file".equals(scheme)) {
            try {
                return context.getContentResolver().openInputStream(uri);
            } catch (Exception e) {
                Log.m103w(TAG, "Unable to load image from URI: " + uri, e);
                return null;
            }
        }
        try {
            return new FileInputStream(new File(this.mString1));
        } catch (FileNotFoundException e2) {
            Log.m103w(TAG, "Unable to load image from path: " + uri, e2);
            return null;
        }
    }

    public Drawable loadDrawableAsUser(Context context, int userId) {
        Context userContext;
        if (this.mType == 2) {
            String resPackage = getResPackage();
            if (TextUtils.isEmpty(resPackage)) {
                resPackage = context.getPackageName();
            }
            if (getResources() == null && !getResPackage().equals("android")) {
                if (context.getUserId() == userId) {
                    userContext = context;
                } else {
                    int flags = (UserHandle.isSameApp(context.getApplicationInfo().uid, Process.myUid()) ? 1 : 0) | 4;
                    userContext = context.createContextAsUser(UserHandle.m145of(userId), flags);
                }
                PackageManager pm = userContext.getPackageManager();
                try {
                    this.mObj1 = pm.getResourcesForApplication(resPackage);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.m109e(TAG, String.format("Unable to find pkg=%s user=%d", getResPackage(), Integer.valueOf(userId)), e);
                }
            }
        }
        return loadDrawable(context);
    }

    public void convertToAshmem() {
        int i = this.mType;
        if ((i == 1 || i == 5) && getBitmap().isMutable() && getBitmap().getAllocationByteCount() >= 131072) {
            setBitmap(getBitmap().asShared());
        }
        this.mCachedAshmem = true;
    }

    public void writeToStream(OutputStream stream) throws IOException {
        DataOutputStream dataStream = new DataOutputStream(stream);
        dataStream.writeInt(1);
        dataStream.writeByte(this.mType);
        switch (this.mType) {
            case 1:
            case 5:
                getBitmap().compress(Bitmap.CompressFormat.PNG, 100, dataStream);
                return;
            case 2:
                dataStream.writeUTF(getResPackage());
                dataStream.writeInt(getResId());
                return;
            case 3:
                dataStream.writeInt(getDataLength());
                dataStream.write(getDataBytes(), getDataOffset(), getDataLength());
                return;
            case 4:
            case 6:
                dataStream.writeUTF(getUriString());
                return;
            default:
                return;
        }
    }

    private Icon(int mType) {
        this.mBlendMode = Drawable.DEFAULT_BLEND_MODE;
        this.mCachedAshmem = false;
        this.mType = mType;
    }

    public static Icon createFromStream(InputStream stream) throws IOException {
        DataInputStream inputStream = new DataInputStream(stream);
        int version = inputStream.readInt();
        if (version >= 1) {
            int type = inputStream.readByte();
            switch (type) {
                case 1:
                    return createWithBitmap(BitmapFactory.decodeStream(inputStream));
                case 2:
                    String packageName = inputStream.readUTF();
                    int resId = inputStream.readInt();
                    return createWithResource(packageName, resId);
                case 3:
                    int length = inputStream.readInt();
                    byte[] data = new byte[length];
                    inputStream.read(data, 0, length);
                    return createWithData(data, 0, length);
                case 4:
                    String uriOrPath = inputStream.readUTF();
                    return createWithContentUri(uriOrPath);
                case 5:
                    return createWithAdaptiveBitmap(BitmapFactory.decodeStream(inputStream));
                case 6:
                    String uri = inputStream.readUTF();
                    return createWithAdaptiveBitmapContentUri(uri);
                default:
                    return null;
            }
        }
        return null;
    }

    public boolean sameAs(Icon otherIcon) {
        if (otherIcon == this) {
            return true;
        }
        if (this.mType != otherIcon.getType()) {
            return false;
        }
        switch (this.mType) {
            case 1:
            case 5:
                return getBitmap() == otherIcon.getBitmap();
            case 2:
                return getResId() == otherIcon.getResId() && Objects.equals(getResPackage(), otherIcon.getResPackage());
            case 3:
                return getDataLength() == otherIcon.getDataLength() && getDataOffset() == otherIcon.getDataOffset() && Arrays.equals(getDataBytes(), otherIcon.getDataBytes());
            case 4:
            case 6:
                return Objects.equals(getUriString(), otherIcon.getUriString());
            default:
                return false;
        }
    }

    public static Icon createWithResource(Context context, int resId) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        Icon rep = new Icon(2);
        rep.mInt1 = resId;
        rep.mString1 = context.getPackageName();
        return rep;
    }

    public static Icon createWithResource(Resources res, int resId) {
        if (res == null) {
            throw new IllegalArgumentException("Resource must not be null.");
        }
        Icon rep = new Icon(2);
        rep.mInt1 = resId;
        rep.mString1 = res.getResourcePackageName(resId);
        return rep;
    }

    public static Icon createWithResource(String resPackage, int resId) {
        if (resPackage == null) {
            throw new IllegalArgumentException("Resource package name must not be null.");
        }
        Icon rep = new Icon(2);
        rep.mInt1 = resId;
        rep.mString1 = resPackage;
        return rep;
    }

    public static Icon createWithBitmap(Bitmap bits) {
        if (bits == null) {
            throw new IllegalArgumentException("Bitmap must not be null.");
        }
        Icon rep = new Icon(1);
        rep.setBitmap(bits);
        return rep;
    }

    public static Icon createWithAdaptiveBitmap(Bitmap bits) {
        if (bits == null) {
            throw new IllegalArgumentException("Bitmap must not be null.");
        }
        Icon rep = new Icon(5);
        rep.setBitmap(bits);
        return rep;
    }

    public static Icon createWithData(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null.");
        }
        Icon rep = new Icon(3);
        rep.mObj1 = data;
        rep.mInt1 = length;
        rep.mInt2 = offset;
        return rep;
    }

    public static Icon createWithContentUri(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri must not be null.");
        }
        Icon rep = new Icon(4);
        rep.mString1 = uri;
        return rep;
    }

    public static Icon createWithContentUri(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri must not be null.");
        }
        return createWithContentUri(uri.toString());
    }

    public static Icon createWithAdaptiveBitmapContentUri(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri must not be null.");
        }
        Icon rep = new Icon(6);
        rep.mString1 = uri;
        return rep;
    }

    public static Icon createWithAdaptiveBitmapContentUri(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri must not be null.");
        }
        return createWithAdaptiveBitmapContentUri(uri.toString());
    }

    public Icon setTint(int tint) {
        return setTintList(ColorStateList.valueOf(tint));
    }

    public Icon setTintList(ColorStateList tintList) {
        this.mTintList = tintList;
        return this;
    }

    public ColorStateList getTintList() {
        return this.mTintList;
    }

    public Icon setTintMode(PorterDuff.Mode mode) {
        this.mBlendMode = BlendMode.fromValue(mode.nativeInt);
        return this;
    }

    public Icon setTintBlendMode(BlendMode mode) {
        this.mBlendMode = mode;
        return this;
    }

    public BlendMode getTintBlendMode() {
        return this.mBlendMode;
    }

    public boolean hasTint() {
        return (this.mTintList == null && this.mBlendMode == DEFAULT_BLEND_MODE) ? false : true;
    }

    public static Icon createWithFilePath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null.");
        }
        Icon rep = new Icon(4);
        rep.mString1 = path;
        return rep;
    }

    public String toString() {
        int[] colors;
        StringBuilder sb = new StringBuilder("Icon(typ=").append(typeToString(this.mType));
        switch (this.mType) {
            case 1:
            case 5:
                sb.append(" size=").append(getBitmap().getWidth()).append("x").append(getBitmap().getHeight());
                break;
            case 2:
                sb.append(" pkg=").append(getResPackage()).append(" id=").append(String.format("0x%08x", Integer.valueOf(getResId())));
                break;
            case 3:
                sb.append(" len=").append(getDataLength());
                if (getDataOffset() != 0) {
                    sb.append(" off=").append(getDataOffset());
                    break;
                }
                break;
            case 4:
            case 6:
                sb.append(" uri=").append(getUriString());
                break;
        }
        if (this.mTintList != null) {
            sb.append(" tint=");
            String sep = "";
            for (int c : this.mTintList.getColors()) {
                sb.append(String.format("%s0x%08x", sep, Integer.valueOf(c)));
                sep = NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER;
            }
        }
        if (this.mBlendMode != DEFAULT_BLEND_MODE) {
            sb.append(" mode=").append(this.mBlendMode);
        }
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int i = this.mType;
        return (i == 1 || i == 5 || i == 3) ? 1 : 0;
    }

    private Icon(Parcel in) {
        this(in.readInt());
        switch (this.mType) {
            case 1:
            case 5:
                Bitmap bits = Bitmap.CREATOR.createFromParcel(in);
                this.mObj1 = bits;
                break;
            case 2:
                String pkg = in.readString();
                int resId = in.readInt();
                this.mString1 = pkg;
                this.mInt1 = resId;
                break;
            case 3:
                int len = in.readInt();
                byte[] a = in.readBlob();
                if (len != a.length) {
                    throw new RuntimeException("internal unparceling error: blob length (" + a.length + ") != expected length (" + len + NavigationBarInflaterView.KEY_CODE_END);
                }
                this.mInt1 = len;
                this.mObj1 = a;
                break;
            case 4:
            case 6:
                String uri = in.readString();
                this.mString1 = uri;
                break;
            default:
                throw new RuntimeException("invalid " + getClass().getSimpleName() + " type in parcel: " + this.mType);
        }
        if (in.readInt() == 1) {
            this.mTintList = ColorStateList.CREATOR.createFromParcel(in);
        }
        this.mBlendMode = BlendMode.fromValue(in.readInt());
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        switch (this.mType) {
            case 1:
            case 5:
                if (!this.mCachedAshmem) {
                    this.mObj1 = ((Bitmap) this.mObj1).asShared();
                    this.mCachedAshmem = true;
                }
                getBitmap().writeToParcel(dest, flags);
                break;
            case 2:
                dest.writeString(getResPackage());
                dest.writeInt(getResId());
                break;
            case 3:
                dest.writeInt(getDataLength());
                dest.writeBlob(getDataBytes(), getDataOffset(), getDataLength());
                break;
            case 4:
            case 6:
                dest.writeString(getUriString());
                break;
        }
        if (this.mTintList == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            this.mTintList.writeToParcel(dest, flags);
        }
        dest.writeInt(BlendMode.toValue(this.mBlendMode));
    }

    public static Bitmap scaleDownIfNecessary(Bitmap bitmap, int maxWidth, int maxHeight) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth > maxWidth || bitmapHeight > maxHeight) {
            float scale = Math.min(maxWidth / bitmapWidth, maxHeight / bitmapHeight);
            return Bitmap.createScaledBitmap(bitmap, Math.max(1, (int) (bitmapWidth * scale)), Math.max(1, (int) (bitmapHeight * scale)), true);
        }
        return bitmap;
    }

    public void scaleDownIfNecessary(int maxWidth, int maxHeight) {
        int i = this.mType;
        if (i != 1 && i != 5) {
            return;
        }
        Bitmap bitmap = getBitmap();
        setBitmap(scaleDownIfNecessary(bitmap, maxWidth, maxHeight));
    }

    /* loaded from: classes.dex */
    private class LoadDrawableTask implements Runnable {
        final Context mContext;
        final Message mMessage;

        public LoadDrawableTask(Context context, Handler handler, final OnDrawableLoadedListener listener) {
            this.mContext = context;
            this.mMessage = Message.obtain(handler, new Runnable() { // from class: android.graphics.drawable.Icon.LoadDrawableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    listener.onDrawableLoaded((Drawable) LoadDrawableTask.this.mMessage.obj);
                }
            });
        }

        public LoadDrawableTask(Context context, Message message) {
            this.mContext = context;
            this.mMessage = message;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mMessage.obj = Icon.this.loadDrawable(this.mContext);
            this.mMessage.sendToTarget();
        }

        public void runAsync() {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(this);
        }
    }
}
