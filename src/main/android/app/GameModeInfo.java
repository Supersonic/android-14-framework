package android.app;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import java.util.Arrays;
import java.util.Map;
@SystemApi
/* loaded from: classes.dex */
public final class GameModeInfo implements Parcelable {
    public static final Parcelable.Creator<GameModeInfo> CREATOR = new Parcelable.Creator<GameModeInfo>() { // from class: android.app.GameModeInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameModeInfo createFromParcel(Parcel in) {
            return new GameModeInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameModeInfo[] newArray(int size) {
            return new GameModeInfo[size];
        }
    };
    private final int mActiveGameMode;
    private final int[] mAvailableGameModes;
    private final Map<Integer, GameModeConfiguration> mConfigMap;
    private final boolean mIsDownscalingAllowed;
    private final boolean mIsFpsOverrideAllowed;
    private final int[] mOverriddenGameModes;

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private int mActiveGameMode;
        private boolean mIsDownscalingAllowed;
        private boolean mIsFpsOverrideAllowed;
        private int[] mAvailableGameModes = new int[0];
        private int[] mOverriddenGameModes = new int[0];
        private Map<Integer, GameModeConfiguration> mConfigMap = new ArrayMap();

        public Builder setAvailableGameModes(int[] availableGameModes) {
            this.mAvailableGameModes = availableGameModes;
            return this;
        }

        public Builder setOverriddenGameModes(int[] overriddenGameModes) {
            this.mOverriddenGameModes = overriddenGameModes;
            return this;
        }

        public Builder setActiveGameMode(int activeGameMode) {
            this.mActiveGameMode = activeGameMode;
            return this;
        }

        public Builder setDownscalingAllowed(boolean allowed) {
            this.mIsDownscalingAllowed = allowed;
            return this;
        }

        public Builder setFpsOverrideAllowed(boolean allowed) {
            this.mIsFpsOverrideAllowed = allowed;
            return this;
        }

        public Builder setGameModeConfiguration(int gameMode, GameModeConfiguration gameModeConfiguration) {
            this.mConfigMap.put(Integer.valueOf(gameMode), gameModeConfiguration);
            return this;
        }

        public GameModeInfo build() {
            return new GameModeInfo(this.mActiveGameMode, this.mAvailableGameModes, this.mOverriddenGameModes, this.mIsDownscalingAllowed, this.mIsFpsOverrideAllowed, this.mConfigMap);
        }
    }

    public GameModeInfo(int activeGameMode, int[] availableGameModes) {
        this(activeGameMode, availableGameModes, new int[0], true, true, new ArrayMap());
    }

    private GameModeInfo(int activeGameMode, int[] availableGameModes, int[] overriddenGameModes, boolean isDownscalingAllowed, boolean isFpsOverrideAllowed, Map<Integer, GameModeConfiguration> configMap) {
        this.mActiveGameMode = activeGameMode;
        this.mAvailableGameModes = Arrays.copyOf(availableGameModes, availableGameModes.length);
        this.mOverriddenGameModes = Arrays.copyOf(overriddenGameModes, overriddenGameModes.length);
        this.mIsDownscalingAllowed = isDownscalingAllowed;
        this.mIsFpsOverrideAllowed = isFpsOverrideAllowed;
        this.mConfigMap = configMap;
    }

    public GameModeInfo(Parcel in) {
        this.mActiveGameMode = in.readInt();
        this.mAvailableGameModes = in.createIntArray();
        this.mOverriddenGameModes = in.createIntArray();
        this.mIsDownscalingAllowed = in.readBoolean();
        this.mIsFpsOverrideAllowed = in.readBoolean();
        ArrayMap arrayMap = new ArrayMap();
        this.mConfigMap = arrayMap;
        in.readMap(arrayMap, getClass().getClassLoader(), Integer.class, GameModeConfiguration.class);
    }

    public int getActiveGameMode() {
        return this.mActiveGameMode;
    }

    public int[] getAvailableGameModes() {
        int[] iArr = this.mAvailableGameModes;
        return Arrays.copyOf(iArr, iArr.length);
    }

    public int[] getOverriddenGameModes() {
        int[] iArr = this.mOverriddenGameModes;
        return Arrays.copyOf(iArr, iArr.length);
    }

    public GameModeConfiguration getGameModeConfiguration(int gameMode) {
        return this.mConfigMap.get(Integer.valueOf(gameMode));
    }

    public boolean isDownscalingAllowed() {
        return this.mIsDownscalingAllowed;
    }

    public boolean isFpsOverrideAllowed() {
        return this.mIsFpsOverrideAllowed;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mActiveGameMode);
        dest.writeIntArray(this.mAvailableGameModes);
        dest.writeIntArray(this.mOverriddenGameModes);
        dest.writeBoolean(this.mIsDownscalingAllowed);
        dest.writeBoolean(this.mIsFpsOverrideAllowed);
        dest.writeMap(this.mConfigMap);
    }
}
