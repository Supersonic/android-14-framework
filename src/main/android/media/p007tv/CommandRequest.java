package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.CommandRequest */
/* loaded from: classes2.dex */
public final class CommandRequest extends BroadcastInfoRequest implements Parcelable {
    public static final String ARGUMENT_TYPE_JSON = "json";
    public static final String ARGUMENT_TYPE_XML = "xml";
    public static final Parcelable.Creator<CommandRequest> CREATOR = new Parcelable.Creator<CommandRequest>() { // from class: android.media.tv.CommandRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CommandRequest createFromParcel(Parcel source) {
            source.readInt();
            return CommandRequest.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CommandRequest[] newArray(int size) {
            return new CommandRequest[size];
        }
    };
    private static final int REQUEST_TYPE = 7;
    private final String mArgumentType;
    private final String mArguments;
    private final String mName;
    private final String mNamespace;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CommandRequest createFromParcelBody(Parcel in) {
        return new CommandRequest(in);
    }

    public CommandRequest(int requestId, int option, String namespace, String name, String arguments, String argumentType) {
        super(7, requestId, option);
        this.mNamespace = namespace;
        this.mName = name;
        this.mArguments = arguments;
        this.mArgumentType = argumentType;
    }

    CommandRequest(Parcel source) {
        super(7, source);
        this.mNamespace = source.readString();
        this.mName = source.readString();
        this.mArguments = source.readString();
        this.mArgumentType = source.readString();
    }

    public String getNamespace() {
        return this.mNamespace;
    }

    public String getName() {
        return this.mName;
    }

    public String getArguments() {
        return this.mArguments;
    }

    public String getArgumentType() {
        return this.mArgumentType;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mNamespace);
        dest.writeString(this.mName);
        dest.writeString(this.mArguments);
        dest.writeString(this.mArgumentType);
    }
}
