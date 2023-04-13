package android.media;

import android.content.res.Resources;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
/* loaded from: classes2.dex */
public final class RoutingSessionInfo implements Parcelable {
    public static final Parcelable.Creator<RoutingSessionInfo> CREATOR = new Parcelable.Creator<RoutingSessionInfo>() { // from class: android.media.RoutingSessionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RoutingSessionInfo createFromParcel(Parcel in) {
            return new RoutingSessionInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RoutingSessionInfo[] newArray(int size) {
            return new RoutingSessionInfo[size];
        }
    };
    private static final String KEY_GROUP_ROUTE = "androidx.mediarouter.media.KEY_GROUP_ROUTE";
    private static final String KEY_VOLUME_HANDLING = "volumeHandling";
    private static final String TAG = "RoutingSessionInfo";
    final String mClientPackageName;
    final Bundle mControlHints;
    final List<String> mDeselectableRoutes;
    final String mId;
    final boolean mIsSystemSession;
    final CharSequence mName;
    final String mOwnerPackageName;
    final String mProviderId;
    final List<String> mSelectableRoutes;
    final List<String> mSelectedRoutes;
    final List<String> mTransferableRoutes;
    final int mVolume;
    final int mVolumeHandling;
    final int mVolumeMax;

    RoutingSessionInfo(Builder builder) {
        Objects.requireNonNull(builder, "builder must not be null.");
        this.mId = builder.mId;
        this.mName = builder.mName;
        this.mOwnerPackageName = builder.mOwnerPackageName;
        this.mClientPackageName = builder.mClientPackageName;
        this.mProviderId = builder.mProviderId;
        List<String> unmodifiableList = Collections.unmodifiableList(convertToUniqueRouteIds(builder.mSelectedRoutes));
        this.mSelectedRoutes = unmodifiableList;
        this.mSelectableRoutes = Collections.unmodifiableList(convertToUniqueRouteIds(builder.mSelectableRoutes));
        this.mDeselectableRoutes = Collections.unmodifiableList(convertToUniqueRouteIds(builder.mDeselectableRoutes));
        this.mTransferableRoutes = Collections.unmodifiableList(convertToUniqueRouteIds(builder.mTransferableRoutes));
        this.mVolumeMax = builder.mVolumeMax;
        this.mVolume = builder.mVolume;
        this.mIsSystemSession = builder.mIsSystemSession;
        boolean volumeAdjustmentForRemoteGroupSessions = Resources.getSystem().getBoolean(C4057R.bool.config_volumeAdjustmentForRemoteGroupSessions);
        int defineVolumeHandling = defineVolumeHandling(builder.mVolumeHandling, unmodifiableList, volumeAdjustmentForRemoteGroupSessions);
        this.mVolumeHandling = defineVolumeHandling;
        this.mControlHints = updateVolumeHandlingInHints(builder.mControlHints, defineVolumeHandling);
    }

    RoutingSessionInfo(Parcel src) {
        String readString = src.readString();
        this.mId = readString;
        Preconditions.checkArgument(!TextUtils.isEmpty(readString));
        this.mName = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(src);
        this.mOwnerPackageName = src.readString();
        this.mClientPackageName = ensureString(src.readString());
        this.mProviderId = src.readString();
        List<String> ensureList = ensureList(src.createStringArrayList());
        this.mSelectedRoutes = ensureList;
        Preconditions.checkArgument(!ensureList.isEmpty());
        this.mSelectableRoutes = ensureList(src.createStringArrayList());
        this.mDeselectableRoutes = ensureList(src.createStringArrayList());
        this.mTransferableRoutes = ensureList(src.createStringArrayList());
        this.mVolumeHandling = src.readInt();
        this.mVolumeMax = src.readInt();
        this.mVolume = src.readInt();
        this.mControlHints = src.readBundle();
        this.mIsSystemSession = src.readBoolean();
    }

    private static Bundle updateVolumeHandlingInHints(Bundle controlHints, int volumeHandling) {
        Bundle groupRoute;
        if (controlHints != null && controlHints.containsKey(KEY_GROUP_ROUTE) && (groupRoute = controlHints.getBundle(KEY_GROUP_ROUTE)) != null && groupRoute.containsKey(KEY_VOLUME_HANDLING) && volumeHandling != groupRoute.getInt(KEY_VOLUME_HANDLING)) {
            Bundle newGroupRoute = new Bundle(groupRoute);
            newGroupRoute.putInt(KEY_VOLUME_HANDLING, volumeHandling);
            Bundle newControlHints = new Bundle(controlHints);
            newControlHints.putBundle(KEY_GROUP_ROUTE, newGroupRoute);
            return newControlHints;
        }
        return controlHints;
    }

    private static int defineVolumeHandling(int volumeHandling, List<String> selectedRoutes, boolean volumeAdjustmentForRemoteGroupSessions) {
        if (!volumeAdjustmentForRemoteGroupSessions && selectedRoutes.size() > 1) {
            return 0;
        }
        return volumeHandling;
    }

    private static String ensureString(String str) {
        return str != null ? str : "";
    }

    private static <T> List<T> ensureList(List<? extends T> list) {
        if (list != null) {
            return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    public String getId() {
        if (!TextUtils.isEmpty(this.mProviderId)) {
            return MediaRouter2Utils.toUniqueId(this.mProviderId, this.mId);
        }
        return this.mId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public String getOriginalId() {
        return this.mId;
    }

    public String getOwnerPackageName() {
        return this.mOwnerPackageName;
    }

    public String getClientPackageName() {
        return this.mClientPackageName;
    }

    public String getProviderId() {
        return this.mProviderId;
    }

    public List<String> getSelectedRoutes() {
        return this.mSelectedRoutes;
    }

    public List<String> getSelectableRoutes() {
        return this.mSelectableRoutes;
    }

    public List<String> getDeselectableRoutes() {
        return this.mDeselectableRoutes;
    }

    public List<String> getTransferableRoutes() {
        return this.mTransferableRoutes;
    }

    public int getVolumeHandling() {
        return this.mVolumeHandling;
    }

    public int getVolumeMax() {
        return this.mVolumeMax;
    }

    public int getVolume() {
        return this.mVolume;
    }

    public Bundle getControlHints() {
        return this.mControlHints;
    }

    public boolean isSystemSession() {
        return this.mIsSystemSession;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeCharSequence(this.mName);
        dest.writeString(this.mOwnerPackageName);
        dest.writeString(this.mClientPackageName);
        dest.writeString(this.mProviderId);
        dest.writeStringList(this.mSelectedRoutes);
        dest.writeStringList(this.mSelectableRoutes);
        dest.writeStringList(this.mDeselectableRoutes);
        dest.writeStringList(this.mTransferableRoutes);
        dest.writeInt(this.mVolumeHandling);
        dest.writeInt(this.mVolumeMax);
        dest.writeInt(this.mVolume);
        dest.writeBundle(this.mControlHints);
        dest.writeBoolean(this.mIsSystemSession);
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + TAG);
        String indent = prefix + "  ";
        pw.println(indent + "mId=" + this.mId);
        pw.println(indent + "mName=" + ((Object) this.mName));
        pw.println(indent + "mOwnerPackageName=" + this.mOwnerPackageName);
        pw.println(indent + "mClientPackageName=" + this.mClientPackageName);
        pw.println(indent + "mProviderId=" + this.mProviderId);
        pw.println(indent + "mSelectedRoutes=" + this.mSelectedRoutes);
        pw.println(indent + "mSelectableRoutes=" + this.mSelectableRoutes);
        pw.println(indent + "mDeselectableRoutes=" + this.mDeselectableRoutes);
        pw.println(indent + "mTransferableRoutes=" + this.mTransferableRoutes);
        pw.println(indent + "mVolumeHandling=" + this.mVolumeHandling);
        pw.println(indent + "mVolumeMax=" + this.mVolumeMax);
        pw.println(indent + "mVolume=" + this.mVolume);
        pw.println(indent + "mControlHints=" + this.mControlHints);
        pw.println(indent + "mIsSystemSession=" + this.mIsSystemSession);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RoutingSessionInfo) {
            RoutingSessionInfo other = (RoutingSessionInfo) obj;
            return Objects.equals(this.mId, other.mId) && Objects.equals(this.mName, other.mName) && Objects.equals(this.mOwnerPackageName, other.mOwnerPackageName) && Objects.equals(this.mClientPackageName, other.mClientPackageName) && Objects.equals(this.mProviderId, other.mProviderId) && Objects.equals(this.mSelectedRoutes, other.mSelectedRoutes) && Objects.equals(this.mSelectableRoutes, other.mSelectableRoutes) && Objects.equals(this.mDeselectableRoutes, other.mDeselectableRoutes) && Objects.equals(this.mTransferableRoutes, other.mTransferableRoutes) && this.mVolumeHandling == other.mVolumeHandling && this.mVolumeMax == other.mVolumeMax && this.mVolume == other.mVolume;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mId, this.mName, this.mOwnerPackageName, this.mClientPackageName, this.mProviderId, this.mSelectedRoutes, this.mSelectableRoutes, this.mDeselectableRoutes, this.mTransferableRoutes, Integer.valueOf(this.mVolumeMax), Integer.valueOf(this.mVolumeHandling), Integer.valueOf(this.mVolume));
    }

    public String toString() {
        StringBuilder result = new StringBuilder().append("RoutingSessionInfo{ ").append("sessionId=").append(getId()).append(", name=").append(getName()).append(", clientPackageName=").append(getClientPackageName()).append(", selectedRoutes={").append(String.join(",", getSelectedRoutes())).append("}").append(", selectableRoutes={").append(String.join(",", getSelectableRoutes())).append("}").append(", deselectableRoutes={").append(String.join(",", getDeselectableRoutes())).append("}").append(", transferableRoutes={").append(String.join(",", getTransferableRoutes())).append("}").append(", volumeHandling=").append(getVolumeHandling()).append(", volumeMax=").append(getVolumeMax()).append(", volume=").append(getVolume()).append(" }");
        return result.toString();
    }

    private List<String> convertToUniqueRouteIds(List<String> routeIds) {
        Objects.requireNonNull(routeIds, "RouteIds cannot be null.");
        if (TextUtils.isEmpty(this.mProviderId)) {
            return new ArrayList(routeIds);
        }
        List<String> result = new ArrayList<>();
        for (String routeId : routeIds) {
            result.add(MediaRouter2Utils.toUniqueId(this.mProviderId, routeId));
        }
        return result;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        String mClientPackageName;
        Bundle mControlHints;
        final List<String> mDeselectableRoutes;
        final String mId;
        boolean mIsSystemSession;
        CharSequence mName;
        String mOwnerPackageName;
        String mProviderId;
        final List<String> mSelectableRoutes;
        final List<String> mSelectedRoutes;
        final List<String> mTransferableRoutes;
        int mVolume;
        int mVolumeHandling;
        int mVolumeMax;

        public Builder(String id, String clientPackageName) {
            this.mVolumeHandling = 0;
            if (TextUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id must not be empty");
            }
            this.mId = id;
            this.mClientPackageName = (String) Objects.requireNonNull(clientPackageName, "clientPackageName must not be null");
            this.mSelectedRoutes = new ArrayList();
            this.mSelectableRoutes = new ArrayList();
            this.mDeselectableRoutes = new ArrayList();
            this.mTransferableRoutes = new ArrayList();
        }

        public Builder(RoutingSessionInfo sessionInfo) {
            this.mVolumeHandling = 0;
            Objects.requireNonNull(sessionInfo, "sessionInfo must not be null");
            this.mId = sessionInfo.mId;
            this.mName = sessionInfo.mName;
            this.mClientPackageName = sessionInfo.mClientPackageName;
            this.mProviderId = sessionInfo.mProviderId;
            ArrayList arrayList = new ArrayList(sessionInfo.mSelectedRoutes);
            this.mSelectedRoutes = arrayList;
            ArrayList arrayList2 = new ArrayList(sessionInfo.mSelectableRoutes);
            this.mSelectableRoutes = arrayList2;
            ArrayList arrayList3 = new ArrayList(sessionInfo.mDeselectableRoutes);
            this.mDeselectableRoutes = arrayList3;
            ArrayList arrayList4 = new ArrayList(sessionInfo.mTransferableRoutes);
            this.mTransferableRoutes = arrayList4;
            if (this.mProviderId != null) {
                arrayList.replaceAll(new UnaryOperator() { // from class: android.media.RoutingSessionInfo$Builder$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return MediaRouter2Utils.getOriginalId((String) obj);
                    }
                });
                arrayList2.replaceAll(new UnaryOperator() { // from class: android.media.RoutingSessionInfo$Builder$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return MediaRouter2Utils.getOriginalId((String) obj);
                    }
                });
                arrayList3.replaceAll(new UnaryOperator() { // from class: android.media.RoutingSessionInfo$Builder$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return MediaRouter2Utils.getOriginalId((String) obj);
                    }
                });
                arrayList4.replaceAll(new UnaryOperator() { // from class: android.media.RoutingSessionInfo$Builder$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return MediaRouter2Utils.getOriginalId((String) obj);
                    }
                });
            }
            this.mVolumeHandling = sessionInfo.mVolumeHandling;
            this.mVolumeMax = sessionInfo.mVolumeMax;
            this.mVolume = sessionInfo.mVolume;
            this.mControlHints = sessionInfo.mControlHints;
            this.mIsSystemSession = sessionInfo.mIsSystemSession;
        }

        public Builder setName(CharSequence name) {
            this.mName = name;
            return this;
        }

        public Builder setOwnerPackageName(String packageName) {
            this.mOwnerPackageName = packageName;
            return this;
        }

        public Builder setClientPackageName(String packageName) {
            this.mClientPackageName = packageName;
            return this;
        }

        public Builder setProviderId(String providerId) {
            if (TextUtils.isEmpty(providerId)) {
                throw new IllegalArgumentException("providerId must not be empty");
            }
            this.mProviderId = providerId;
            return this;
        }

        public Builder clearSelectedRoutes() {
            this.mSelectedRoutes.clear();
            return this;
        }

        public Builder addSelectedRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mSelectedRoutes.add(routeId);
            return this;
        }

        public Builder removeSelectedRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mSelectedRoutes.remove(routeId);
            return this;
        }

        public Builder clearSelectableRoutes() {
            this.mSelectableRoutes.clear();
            return this;
        }

        public Builder addSelectableRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mSelectableRoutes.add(routeId);
            return this;
        }

        public Builder removeSelectableRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mSelectableRoutes.remove(routeId);
            return this;
        }

        public Builder clearDeselectableRoutes() {
            this.mDeselectableRoutes.clear();
            return this;
        }

        public Builder addDeselectableRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mDeselectableRoutes.add(routeId);
            return this;
        }

        public Builder removeDeselectableRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mDeselectableRoutes.remove(routeId);
            return this;
        }

        public Builder clearTransferableRoutes() {
            this.mTransferableRoutes.clear();
            return this;
        }

        public Builder addTransferableRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mTransferableRoutes.add(routeId);
            return this;
        }

        public Builder removeTransferableRoute(String routeId) {
            if (TextUtils.isEmpty(routeId)) {
                throw new IllegalArgumentException("routeId must not be empty");
            }
            this.mTransferableRoutes.remove(routeId);
            return this;
        }

        public Builder setVolumeHandling(int volumeHandling) {
            this.mVolumeHandling = volumeHandling;
            return this;
        }

        public Builder setVolumeMax(int volumeMax) {
            this.mVolumeMax = volumeMax;
            return this;
        }

        public Builder setVolume(int volume) {
            this.mVolume = volume;
            return this;
        }

        public Builder setControlHints(Bundle controlHints) {
            this.mControlHints = controlHints;
            return this;
        }

        public Builder setSystemSession(boolean isSystemSession) {
            this.mIsSystemSession = isSystemSession;
            return this;
        }

        public RoutingSessionInfo build() {
            if (this.mSelectedRoutes.isEmpty()) {
                throw new IllegalArgumentException("selectedRoutes must not be empty");
            }
            return new RoutingSessionInfo(this);
        }
    }
}
