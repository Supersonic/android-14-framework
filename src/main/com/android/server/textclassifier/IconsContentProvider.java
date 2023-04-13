package com.android.server.textclassifier;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.textclassifier.IconsUriHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes2.dex */
public final class IconsContentProvider extends ContentProvider {
    public final ContentProvider.PipeDataWriter<Pair<IconsUriHelper.ResourceInfo, Integer>> mWriter = new ContentProvider.PipeDataWriter() { // from class: com.android.server.textclassifier.IconsContentProvider$$ExternalSyntheticLambda0
        @Override // android.content.ContentProvider.PipeDataWriter
        public final void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, Object obj) {
            IconsContentProvider.this.lambda$new$0(parcelFileDescriptor, uri, str, bundle, (Pair) obj);
        }
    };

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "image/png";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, Pair pair) {
        try {
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
            IconsUriHelper.ResourceInfo resourceInfo = (IconsUriHelper.ResourceInfo) pair.first;
            getBitmap(Icon.createWithResource(resourceInfo.packageName, resourceInfo.f1155id).loadDrawableAsUser(getContext(), ((Integer) pair.second).intValue())).compress(Bitmap.CompressFormat.PNG, 100, autoCloseOutputStream);
            autoCloseOutputStream.close();
        } catch (Exception e) {
            Log.e("IconsContentProvider", "Error retrieving icon for uri: " + uri, e);
        }
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) {
        IconsUriHelper.ResourceInfo resourceInfo = IconsUriHelper.getInstance().getResourceInfo(uri);
        if (resourceInfo == null) {
            Log.e("IconsContentProvider", "No icon found for uri: " + uri);
            return null;
        }
        try {
            return openPipeHelper(uri, "image/png", null, new Pair(resourceInfo, Integer.valueOf(UserHandle.getCallingUserId())), this.mWriter);
        } catch (IOException e) {
            Log.e("IconsContentProvider", "Error opening pipe helper for icon at uri: " + uri, e);
            return null;
        }
    }

    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            throw new IllegalStateException("The icon is zero-sized");
        }
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    @VisibleForTesting
    public static boolean sameIcon(Drawable drawable, Drawable drawable2) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        getBitmap(drawable).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        getBitmap(drawable2).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
        return Arrays.equals(byteArrayOutputStream.toByteArray(), byteArrayOutputStream2.toByteArray());
    }
}
