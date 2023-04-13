package com.android.server.companion.datatransfer.contextsync;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class CrossDeviceCall {
    public static final AtomicLong sNextId = new AtomicLong(1);
    public final Call mCall;
    public String mCallerDisplayName;
    public byte[] mCallingAppIcon;
    public String mCallingAppName;
    public String mContactDisplayName;
    @VisibleForTesting
    boolean mIsEnterprise;
    public boolean mIsMuted;
    @VisibleForTesting
    boolean mIsOtt;
    public int mStatus = 0;
    public final Set<Integer> mControls = new HashSet();
    public final long mId = sNextId.getAndIncrement();

    public final int convertStateToStatus(int i) {
        if (i != 2) {
            if (i != 3) {
                return i != 4 ? 0 : 2;
            }
            return 3;
        }
        return 1;
    }

    public CrossDeviceCall(PackageManager packageManager, Call call, CallAudioState callAudioState) {
        boolean z = false;
        this.mCall = call;
        String packageName = call != null ? call.getDetails().getAccountHandle().getComponentName().getPackageName() : null;
        this.mIsOtt = call != null && (call.getDetails().getCallCapabilities() & 256) == 256;
        this.mIsEnterprise = call != null && (call.getDetails().getCallProperties() & 32) == 32;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0L));
            this.mCallingAppName = packageManager.getApplicationLabel(applicationInfo).toString();
            this.mCallingAppIcon = renderDrawableToByteArray(packageManager.getApplicationIcon(applicationInfo));
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("CrossDeviceCall", "Could not get application info for package " + packageName, e);
        }
        if (callAudioState != null && callAudioState.isMuted()) {
            z = true;
        }
        this.mIsMuted = z;
        if (call != null) {
            updateCallDetails(call.getDetails());
        }
    }

    public final byte[] renderDrawableToByteArray(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap.getWidth() > 256 || bitmap.getHeight() > 256) {
                Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                byte[] renderBitmapToByteArray = renderBitmapToByteArray(createScaledBitmap);
                createScaledBitmap.recycle();
                return renderBitmapToByteArray;
            }
            return renderBitmapToByteArray(bitmap);
        }
        Bitmap createBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
            drawable.draw(canvas);
            createBitmap.recycle();
            return renderBitmapToByteArray(createBitmap);
        } catch (Throwable th) {
            createBitmap.recycle();
            throw th;
        }
    }

    public final byte[] renderBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void updateMuted(boolean z) {
        this.mIsMuted = z;
        updateCallDetails(this.mCall.getDetails());
    }

    public void updateSilencedIfRinging() {
        if (this.mStatus == 1) {
            this.mStatus = 4;
        }
        this.mControls.remove(3);
    }

    @VisibleForTesting
    public void updateCallDetails(Call.Details details) {
        this.mCallerDisplayName = details.getCallerDisplayName();
        this.mContactDisplayName = details.getContactDisplayName();
        this.mStatus = convertStateToStatus(details.getState());
        this.mControls.clear();
        int i = this.mStatus;
        if (i == 1 || i == 4) {
            this.mControls.add(1);
            this.mControls.add(2);
            if (this.mStatus == 1) {
                this.mControls.add(3);
            }
        }
        int i2 = this.mStatus;
        if (i2 == 2 || i2 == 3) {
            this.mControls.add(6);
            if (details.can(1)) {
                this.mControls.add(Integer.valueOf(this.mStatus == 3 ? 8 : 7));
            }
        }
        if (this.mStatus == 2 && details.can(64)) {
            this.mControls.add(Integer.valueOf(this.mIsMuted ? 5 : 4));
        }
    }

    public long getId() {
        return this.mId;
    }
}
