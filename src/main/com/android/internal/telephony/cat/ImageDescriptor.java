package com.android.internal.telephony.cat;
/* loaded from: classes.dex */
public class ImageDescriptor {
    int mWidth = 0;
    int mHeight = 0;
    int mCodingScheme = 0;
    int mImageId = 0;
    int mHighOffset = 0;
    int mLowOffset = 0;
    int mLength = 0;

    ImageDescriptor() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ImageDescriptor parse(byte[] bArr, int i) {
        ImageDescriptor imageDescriptor = new ImageDescriptor();
        int i2 = i + 1;
        try {
            imageDescriptor.mWidth = bArr[i] & 255;
            int i3 = i2 + 1;
            imageDescriptor.mHeight = bArr[i2] & 255;
            int i4 = i3 + 1;
            imageDescriptor.mCodingScheme = bArr[i3] & 255;
            int i5 = i4 + 1;
            int i6 = i5 + 1;
            imageDescriptor.mImageId = (bArr[i5] & 255) | ((bArr[i4] & 255) << 8);
            int i7 = i6 + 1;
            imageDescriptor.mHighOffset = bArr[i6] & 255;
            int i8 = i7 + 1;
            imageDescriptor.mLowOffset = bArr[i7] & 255;
            imageDescriptor.mLength = (bArr[i8 + 1] & 255) | ((bArr[i8] & 255) << 8);
            CatLog.m4d("ImageDescriptor", "parse; Descriptor : " + imageDescriptor.mWidth + ", " + imageDescriptor.mHeight + ", " + imageDescriptor.mCodingScheme + ", 0x" + Integer.toHexString(imageDescriptor.mImageId) + ", " + imageDescriptor.mHighOffset + ", " + imageDescriptor.mLowOffset + ", " + imageDescriptor.mLength);
            return imageDescriptor;
        } catch (IndexOutOfBoundsException unused) {
            CatLog.m4d("ImageDescriptor", "parse; failed parsing image descriptor");
            return null;
        }
    }
}
