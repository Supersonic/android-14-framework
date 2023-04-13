package android.graphics.fonts;

import android.annotation.SystemApi;
import android.p008os.ParcelFileDescriptor;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class FontFileUpdateRequest {
    private final ParcelFileDescriptor mParcelFileDescriptor;
    private final byte[] mSignature;

    public FontFileUpdateRequest(ParcelFileDescriptor parcelFileDescriptor, byte[] signature) {
        Objects.requireNonNull(parcelFileDescriptor);
        Objects.requireNonNull(signature);
        this.mParcelFileDescriptor = parcelFileDescriptor;
        this.mSignature = signature;
    }

    public ParcelFileDescriptor getParcelFileDescriptor() {
        return this.mParcelFileDescriptor;
    }

    public byte[] getSignature() {
        return this.mSignature;
    }
}
