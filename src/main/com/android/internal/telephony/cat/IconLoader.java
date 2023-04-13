package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.android.internal.telephony.uicc.IccFileHandler;
import java.util.HashMap;
/* loaded from: classes.dex */
class IconLoader extends Handler {
    private static IconLoader sLoader;
    private static HandlerThread sThread;
    private Bitmap mCurrentIcon;
    private int mCurrentRecordIndex;
    private Message mEndMsg;
    private byte[] mIconData;
    private Bitmap[] mIcons;
    private HashMap<Integer, Bitmap> mIconsCache;
    private ImageDescriptor mId;
    private int mRecordNumber;
    private int[] mRecordNumbers;
    private IccFileHandler mSimFH;
    private int mState;

    private static int bitToBnW(int i) {
        return i == 1 ? -1 : -16777216;
    }

    private static int getMask(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 3;
            case 3:
                return 7;
            case 4:
                return 15;
            case 5:
                return 31;
            case 6:
                return 63;
            case 7:
                return 127;
            case 8:
                return 255;
            default:
                return 0;
        }
    }

    private IconLoader(Looper looper, IccFileHandler iccFileHandler) {
        super(looper);
        this.mState = 1;
        this.mId = null;
        this.mCurrentIcon = null;
        this.mEndMsg = null;
        this.mIconData = null;
        this.mRecordNumbers = null;
        this.mCurrentRecordIndex = 0;
        this.mIcons = null;
        this.mIconsCache = null;
        this.mSimFH = iccFileHandler;
        this.mIconsCache = new HashMap<>(50);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IconLoader getInstance(Handler handler, IccFileHandler iccFileHandler) {
        IconLoader iconLoader = sLoader;
        if (iconLoader != null) {
            return iconLoader;
        }
        if (iccFileHandler != null) {
            HandlerThread handlerThread = new HandlerThread("Cat Icon Loader");
            sThread = handlerThread;
            handlerThread.start();
            return new IconLoader(sThread.getLooper(), iccFileHandler);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadIcons(int[] iArr, Message message) {
        if (iArr == null || iArr.length == 0 || message == null) {
            return;
        }
        this.mEndMsg = message;
        this.mIcons = new Bitmap[iArr.length];
        this.mRecordNumbers = iArr;
        this.mCurrentRecordIndex = 0;
        this.mState = 2;
        startLoadingIcon(iArr[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loadIcon(int i, Message message) {
        if (message == null) {
            return;
        }
        this.mEndMsg = message;
        this.mState = 1;
        startLoadingIcon(i);
    }

    private void startLoadingIcon(int i) {
        this.mId = null;
        this.mIconData = null;
        this.mCurrentIcon = null;
        this.mRecordNumber = i;
        if (this.mIconsCache.containsKey(Integer.valueOf(i))) {
            this.mCurrentIcon = this.mIconsCache.get(Integer.valueOf(i));
            postIcon();
            return;
        }
        readId();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        try {
            int i = message.what;
            if (i == 1) {
                if (handleImageDescriptor((byte[]) ((AsyncResult) message.obj).result)) {
                    readIconData();
                    return;
                }
                throw new Exception("Unable to parse image descriptor");
            } else if (i != 2) {
                if (i != 3) {
                    return;
                }
                byte[] bArr = this.mIconData;
                this.mCurrentIcon = parseToRGB(bArr, bArr.length, false, (byte[]) ((AsyncResult) message.obj).result);
                this.mIconsCache.put(Integer.valueOf(this.mRecordNumber), this.mCurrentIcon);
                postIcon();
            } else {
                CatLog.m5d(this, "load icon done");
                byte[] bArr2 = (byte[]) ((AsyncResult) message.obj).result;
                int i2 = this.mId.mCodingScheme;
                if (i2 == 17) {
                    this.mCurrentIcon = parseToBnW(bArr2, bArr2.length);
                    this.mIconsCache.put(Integer.valueOf(this.mRecordNumber), this.mCurrentIcon);
                    postIcon();
                } else if (i2 == 33) {
                    this.mIconData = bArr2;
                    readClut();
                } else {
                    CatLog.m5d(this, "else  /postIcon ");
                    postIcon();
                }
            }
        } catch (Exception unused) {
            CatLog.m5d(this, "Icon load failed");
            postIcon();
        }
    }

    private boolean handleImageDescriptor(byte[] bArr) {
        ImageDescriptor parse = ImageDescriptor.parse(bArr, 1);
        this.mId = parse;
        return parse != null;
    }

    private void readClut() {
        int i = this.mIconData[3] * 3;
        Message obtainMessage = obtainMessage(3);
        IccFileHandler iccFileHandler = this.mSimFH;
        int i2 = this.mId.mImageId;
        byte[] bArr = this.mIconData;
        iccFileHandler.loadEFImgTransparent(i2, bArr[4], bArr[5], i, obtainMessage);
    }

    private void readId() {
        if (this.mRecordNumber < 0) {
            this.mCurrentIcon = null;
            postIcon();
            return;
        }
        this.mSimFH.loadEFImgLinearFixed(this.mRecordNumber, obtainMessage(1));
    }

    private void readIconData() {
        Message obtainMessage = obtainMessage(2);
        IccFileHandler iccFileHandler = this.mSimFH;
        ImageDescriptor imageDescriptor = this.mId;
        iccFileHandler.loadEFImgTransparent(imageDescriptor.mImageId, 0, 0, imageDescriptor.mLength, obtainMessage);
    }

    private void postIcon() {
        int i = this.mState;
        if (i == 1) {
            Message message = this.mEndMsg;
            message.obj = this.mCurrentIcon;
            message.sendToTarget();
        } else if (i == 2) {
            Bitmap[] bitmapArr = this.mIcons;
            int i2 = this.mCurrentRecordIndex;
            int i3 = i2 + 1;
            this.mCurrentRecordIndex = i3;
            bitmapArr[i2] = this.mCurrentIcon;
            int[] iArr = this.mRecordNumbers;
            if (i3 < iArr.length) {
                startLoadingIcon(iArr[i3]);
                return;
            }
            Message message2 = this.mEndMsg;
            message2.obj = bitmapArr;
            message2.sendToTarget();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0, types: [int] */
    public static Bitmap parseToBnW(byte[] bArr, int i) {
        int i2 = 0;
        int i3 = bArr[0] & 255;
        int i4 = bArr[1] & 255;
        int i5 = i3 * i4;
        int[] iArr = new int[i5];
        int i6 = 2;
        byte b = 7;
        byte b2 = 0;
        while (i2 < i5) {
            if (i2 % 8 == 0) {
                int i7 = i6 + 1;
                byte b3 = bArr[i6];
                b = 7;
                i6 = i7;
                b2 = b3;
            }
            iArr[i2] = bitToBnW((b2 >> b) & 1);
            i2++;
            b--;
        }
        if (i2 != i5) {
            CatLog.m4d("IconLoader", "parseToBnW; size error");
        }
        return Bitmap.createBitmap(iArr, i3, i4, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap parseToRGB(byte[] bArr, int i, boolean z, byte[] bArr2) {
        int i2 = 0;
        int i3 = bArr[0] & 255;
        int i4 = bArr[1] & 255;
        int i5 = bArr[2] & 255;
        int i6 = 3;
        int i7 = bArr[3] & 255;
        if (true == z) {
            bArr2[i7 - 1] = 0;
        }
        int i8 = i3 * i4;
        int[] iArr = new int[i8];
        int i9 = 8 - i5;
        byte b = bArr[6];
        int mask = getMask(i5);
        boolean z2 = 8 % i5 == 0;
        int i10 = 7;
        int i11 = i9;
        while (i2 < i8) {
            if (i11 < 0) {
                int i12 = i10 + 1;
                byte b2 = bArr[i10];
                i11 = z2 ? i9 : i11 * (-1);
                i10 = i12;
                b = b2;
            }
            int i13 = ((b >> i11) & mask) * i6;
            iArr[i2] = Color.rgb((int) bArr2[i13], (int) bArr2[i13 + 1], (int) bArr2[i13 + 2]);
            i11 -= i5;
            z2 = z2;
            i2++;
            i6 = 3;
        }
        return Bitmap.createBitmap(iArr, i3, i4, Bitmap.Config.ARGB_8888);
    }

    public void dispose() {
        this.mSimFH = null;
        HandlerThread handlerThread = sThread;
        if (handlerThread != null) {
            handlerThread.quit();
            sThread = null;
        }
        this.mIconsCache = null;
        sLoader = null;
    }
}
