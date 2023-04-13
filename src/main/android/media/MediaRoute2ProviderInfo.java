package android.media;

import android.media.MediaRoute2Info;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MediaRoute2ProviderInfo implements Parcelable {
    public static final Parcelable.Creator<MediaRoute2ProviderInfo> CREATOR = new Parcelable.Creator<MediaRoute2ProviderInfo>() { // from class: android.media.MediaRoute2ProviderInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaRoute2ProviderInfo createFromParcel(Parcel in) {
            return new MediaRoute2ProviderInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaRoute2ProviderInfo[] newArray(int size) {
            return new MediaRoute2ProviderInfo[size];
        }
    };
    final ArrayMap<String, MediaRoute2Info> mRoutes;
    final String mUniqueId;

    MediaRoute2ProviderInfo(Builder builder) {
        Objects.requireNonNull(builder, "builder must not be null.");
        this.mUniqueId = builder.mUniqueId;
        this.mRoutes = builder.mRoutes;
    }

    MediaRoute2ProviderInfo(Parcel src) {
        this.mUniqueId = src.readString();
        ArrayMap<String, MediaRoute2Info> routes = src.createTypedArrayMap(MediaRoute2Info.CREATOR);
        this.mRoutes = routes == null ? ArrayMap.EMPTY : routes;
    }

    public boolean isValid() {
        if (this.mUniqueId == null) {
            return false;
        }
        int count = this.mRoutes.size();
        for (int i = 0; i < count; i++) {
            MediaRoute2Info route = this.mRoutes.valueAt(i);
            if (route == null || !route.isValid()) {
                return false;
            }
        }
        return true;
    }

    public String getUniqueId() {
        return this.mUniqueId;
    }

    public MediaRoute2Info getRoute(String routeId) {
        return this.mRoutes.get(Objects.requireNonNull(routeId, "routeId must not be null"));
    }

    public Collection<MediaRoute2Info> getRoutes() {
        return this.mRoutes.values();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUniqueId);
        dest.writeTypedArrayMap(this.mRoutes, flags);
    }

    public String toString() {
        StringBuilder result = new StringBuilder().append("MediaRouteProviderInfo { ").append("uniqueId=").append(this.mUniqueId).append(", routes=").append(Arrays.toString(getRoutes().toArray())).append(" }");
        return result.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        final ArrayMap<String, MediaRoute2Info> mRoutes;
        String mUniqueId;

        public Builder() {
            this.mRoutes = new ArrayMap<>();
        }

        public Builder(MediaRoute2ProviderInfo descriptor) {
            Objects.requireNonNull(descriptor, "descriptor must not be null");
            this.mUniqueId = descriptor.mUniqueId;
            this.mRoutes = new ArrayMap<>(descriptor.mRoutes);
        }

        public Builder setUniqueId(String packageName, String uniqueId) {
            if (TextUtils.equals(this.mUniqueId, uniqueId)) {
                return this;
            }
            this.mUniqueId = uniqueId;
            ArrayMap<String, MediaRoute2Info> newRoutes = new ArrayMap<>();
            for (Map.Entry<String, MediaRoute2Info> entry : this.mRoutes.entrySet()) {
                MediaRoute2Info routeWithProviderId = new MediaRoute2Info.Builder(entry.getValue()).setPackageName(packageName).setProviderId(this.mUniqueId).build();
                newRoutes.put(routeWithProviderId.getOriginalId(), routeWithProviderId);
            }
            this.mRoutes.clear();
            this.mRoutes.putAll((ArrayMap<? extends String, ? extends MediaRoute2Info>) newRoutes);
            return this;
        }

        public Builder setSystemRouteProvider(boolean isSystem) {
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                MediaRoute2Info route = this.mRoutes.valueAt(i);
                if (route.isSystemRoute() != isSystem) {
                    this.mRoutes.setValueAt(i, new MediaRoute2Info.Builder(route).setSystemRoute(isSystem).build());
                }
            }
            return this;
        }

        public Builder addRoute(MediaRoute2Info route) {
            Objects.requireNonNull(route, "route must not be null");
            if (this.mRoutes.containsKey(route.getOriginalId())) {
                throw new IllegalArgumentException("A route with the same id is already added");
            }
            if (this.mUniqueId != null) {
                this.mRoutes.put(route.getOriginalId(), new MediaRoute2Info.Builder(route).setProviderId(this.mUniqueId).build());
            } else {
                this.mRoutes.put(route.getOriginalId(), route);
            }
            return this;
        }

        public Builder addRoutes(Collection<MediaRoute2Info> routes) {
            Objects.requireNonNull(routes, "routes must not be null");
            if (!routes.isEmpty()) {
                for (MediaRoute2Info route : routes) {
                    addRoute(route);
                }
            }
            return this;
        }

        public MediaRoute2ProviderInfo build() {
            return new MediaRoute2ProviderInfo(this);
        }
    }
}
