package android.content.p000om;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
/* renamed from: android.content.om.OverlayManagerTransaction */
/* loaded from: classes.dex */
public final class OverlayManagerTransaction implements Parcelable {
    public static final Parcelable.Creator<OverlayManagerTransaction> CREATOR = new Parcelable.Creator<OverlayManagerTransaction>() { // from class: android.content.om.OverlayManagerTransaction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OverlayManagerTransaction createFromParcel(Parcel source) {
            return new OverlayManagerTransaction(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OverlayManagerTransaction[] newArray(int size) {
            return new OverlayManagerTransaction[size];
        }
    };
    private final List<Request> mRequests;
    private final boolean mSelfTargeting;

    private OverlayManagerTransaction(List<Request> requests, boolean selfTargeting) {
        Objects.requireNonNull(requests);
        if (requests.contains(null)) {
            throw new IllegalArgumentException("null request");
        }
        this.mRequests = requests;
        this.mSelfTargeting = selfTargeting;
    }

    public static OverlayManagerTransaction newInstance() {
        return new OverlayManagerTransaction((List<Request>) new ArrayList(), true);
    }

    private OverlayManagerTransaction(Parcel source) {
        int size = source.readInt();
        this.mRequests = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            int request = source.readInt();
            OverlayIdentifier overlay = (OverlayIdentifier) source.readParcelable(null, OverlayIdentifier.class);
            int userId = source.readInt();
            Bundle extras = source.readBundle(null);
            this.mRequests.add(new Request(request, overlay, userId, extras));
        }
        this.mSelfTargeting = false;
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    public Iterator<Request> getRequests() {
        return this.mRequests.iterator();
    }

    public String toString() {
        return String.format("OverlayManagerTransaction { mRequests = %s }", this.mRequests);
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    /* renamed from: android.content.om.OverlayManagerTransaction$Request */
    /* loaded from: classes.dex */
    public static final class Request {
        public static final String BUNDLE_FABRICATED_OVERLAY = "fabricated_overlay";
        public static final int TYPE_REGISTER_FABRICATED = 2;
        public static final int TYPE_SET_DISABLED = 1;
        public static final int TYPE_SET_ENABLED = 0;
        public static final int TYPE_UNREGISTER_FABRICATED = 3;
        public final Bundle extras;
        public final OverlayIdentifier overlay;
        public final int type;
        public final int userId;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.content.om.OverlayManagerTransaction$Request$RequestType */
        /* loaded from: classes.dex */
        @interface RequestType {
        }

        public Request(int type, OverlayIdentifier overlay, int userId) {
            this(type, overlay, userId, null);
        }

        public Request(int type, OverlayIdentifier overlay, int userId, Bundle extras) {
            this.type = type;
            this.overlay = overlay;
            this.userId = userId;
            this.extras = extras;
        }

        public String toString() {
            return String.format(Locale.US, "Request{type=0x%02x (%s), overlay=%s, userId=%d}", Integer.valueOf(this.type), typeToString(), this.overlay, Integer.valueOf(this.userId));
        }

        public String typeToString() {
            int i = this.type;
            switch (i) {
                case 0:
                    return "TYPE_SET_ENABLED";
                case 1:
                    return "TYPE_SET_DISABLED";
                case 2:
                    return "TYPE_REGISTER_FABRICATED";
                case 3:
                    return "TYPE_UNREGISTER_FABRICATED";
                default:
                    return String.format("TYPE_UNKNOWN (0x%02x)", Integer.valueOf(i));
            }
        }
    }

    /* renamed from: android.content.om.OverlayManagerTransaction$Builder */
    /* loaded from: classes.dex */
    public static final class Builder {
        private final List<Request> mRequests = new ArrayList();

        public Builder setEnabled(OverlayIdentifier overlay, boolean enable) {
            return setEnabled(overlay, enable, UserHandle.myUserId());
        }

        public Builder setEnabled(OverlayIdentifier overlay, boolean enable, int userId) {
            Preconditions.checkNotNull(overlay);
            int type = !enable ? 1 : 0;
            this.mRequests.add(new Request(type, overlay, userId));
            return this;
        }

        public Builder registerFabricatedOverlay(FabricatedOverlay overlay) {
            this.mRequests.add(OverlayManagerTransaction.generateRegisterFabricatedOverlayRequest(overlay));
            return this;
        }

        public Builder unregisterFabricatedOverlay(OverlayIdentifier overlay) {
            this.mRequests.add(OverlayManagerTransaction.generateUnRegisterFabricatedOverlayRequest(overlay));
            return this;
        }

        public OverlayManagerTransaction build() {
            return new OverlayManagerTransaction(this.mRequests, false);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int size = this.mRequests.size();
        dest.writeInt(size);
        for (int i = 0; i < size; i++) {
            Request req = this.mRequests.get(i);
            dest.writeInt(req.type);
            dest.writeParcelable(req.overlay, flags);
            dest.writeInt(req.userId);
            dest.writeBundle(req.extras);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Request generateRegisterFabricatedOverlayRequest(FabricatedOverlay overlay) {
        Objects.requireNonNull(overlay);
        Bundle extras = new Bundle();
        extras.putParcelable(Request.BUNDLE_FABRICATED_OVERLAY, overlay.mOverlay);
        return new Request(2, overlay.getIdentifier(), -1, extras);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Request generateUnRegisterFabricatedOverlayRequest(OverlayIdentifier overlayIdentifier) {
        Objects.requireNonNull(overlayIdentifier);
        return new Request(3, overlayIdentifier, -1);
    }

    public void registerFabricatedOverlay(FabricatedOverlay overlay) {
        this.mRequests.add(generateRegisterFabricatedOverlayRequest(overlay));
    }

    public void unregisterFabricatedOverlay(OverlayIdentifier overlay) {
        this.mRequests.add(generateUnRegisterFabricatedOverlayRequest(overlay));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSelfTargeting() {
        return this.mSelfTargeting;
    }
}
