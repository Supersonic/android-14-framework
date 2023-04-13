package android.media;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class MediaRouterClientState implements Parcelable {
    public static final Parcelable.Creator<MediaRouterClientState> CREATOR = new Parcelable.Creator<MediaRouterClientState>() { // from class: android.media.MediaRouterClientState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaRouterClientState createFromParcel(Parcel in) {
            return new MediaRouterClientState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaRouterClientState[] newArray(int size) {
            return new MediaRouterClientState[size];
        }
    };
    public final ArrayList<RouteInfo> routes;

    public MediaRouterClientState() {
        this.routes = new ArrayList<>();
    }

    MediaRouterClientState(Parcel src) {
        this.routes = src.createTypedArrayList(RouteInfo.CREATOR);
    }

    public RouteInfo getRoute(String id) {
        int count = this.routes.size();
        for (int i = 0; i < count; i++) {
            RouteInfo route = this.routes.get(i);
            if (route.f274id.equals(id)) {
                return route;
            }
        }
        return null;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.routes);
    }

    public String toString() {
        return "MediaRouterClientState{ routes=" + this.routes.toString() + " }";
    }

    /* loaded from: classes2.dex */
    public static final class RouteInfo implements Parcelable {
        public static final Parcelable.Creator<RouteInfo> CREATOR = new Parcelable.Creator<RouteInfo>() { // from class: android.media.MediaRouterClientState.RouteInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RouteInfo createFromParcel(Parcel in) {
                return new RouteInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RouteInfo[] newArray(int size) {
                return new RouteInfo[size];
            }
        };
        public String description;
        public int deviceType;
        public boolean enabled;

        /* renamed from: id */
        public String f274id;
        public String name;
        public int playbackStream;
        public int playbackType;
        public int presentationDisplayId;
        public int statusCode;
        public int supportedTypes;
        public int volume;
        public int volumeHandling;
        public int volumeMax;

        public RouteInfo(String id) {
            this.f274id = id;
            this.enabled = true;
            this.statusCode = 0;
            this.playbackType = 1;
            this.playbackStream = -1;
            this.volumeHandling = 0;
            this.presentationDisplayId = -1;
            this.deviceType = 0;
        }

        public RouteInfo(RouteInfo other) {
            this.f274id = other.f274id;
            this.name = other.name;
            this.description = other.description;
            this.supportedTypes = other.supportedTypes;
            this.enabled = other.enabled;
            this.statusCode = other.statusCode;
            this.playbackType = other.playbackType;
            this.playbackStream = other.playbackStream;
            this.volume = other.volume;
            this.volumeMax = other.volumeMax;
            this.volumeHandling = other.volumeHandling;
            this.presentationDisplayId = other.presentationDisplayId;
            this.deviceType = other.deviceType;
        }

        RouteInfo(Parcel in) {
            this.f274id = in.readString();
            this.name = in.readString();
            this.description = in.readString();
            this.supportedTypes = in.readInt();
            this.enabled = in.readInt() != 0;
            this.statusCode = in.readInt();
            this.playbackType = in.readInt();
            this.playbackStream = in.readInt();
            this.volume = in.readInt();
            this.volumeMax = in.readInt();
            this.volumeHandling = in.readInt();
            this.presentationDisplayId = in.readInt();
            this.deviceType = in.readInt();
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.f274id);
            dest.writeString(this.name);
            dest.writeString(this.description);
            dest.writeInt(this.supportedTypes);
            dest.writeInt(this.enabled ? 1 : 0);
            dest.writeInt(this.statusCode);
            dest.writeInt(this.playbackType);
            dest.writeInt(this.playbackStream);
            dest.writeInt(this.volume);
            dest.writeInt(this.volumeMax);
            dest.writeInt(this.volumeHandling);
            dest.writeInt(this.presentationDisplayId);
            dest.writeInt(this.deviceType);
        }

        public String toString() {
            return "RouteInfo{ id=" + this.f274id + ", name=" + this.name + ", description=" + this.description + ", supportedTypes=0x" + Integer.toHexString(this.supportedTypes) + ", enabled=" + this.enabled + ", statusCode=" + this.statusCode + ", playbackType=" + this.playbackType + ", playbackStream=" + this.playbackStream + ", volume=" + this.volume + ", volumeMax=" + this.volumeMax + ", volumeHandling=" + this.volumeHandling + ", presentationDisplayId=" + this.presentationDisplayId + ", deviceType=" + this.deviceType + " }";
        }
    }
}
