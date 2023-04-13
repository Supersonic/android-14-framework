package com.android.server.app;

import android.content.ComponentName;
import android.os.UserHandle;
import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes.dex */
public final class GameServiceConfiguration {
    public final GameServiceComponentConfiguration mGameServiceComponentConfiguration;
    public final String mPackageName;

    public GameServiceConfiguration(String str, GameServiceComponentConfiguration gameServiceComponentConfiguration) {
        Objects.requireNonNull(str);
        this.mPackageName = str;
        this.mGameServiceComponentConfiguration = gameServiceComponentConfiguration;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public GameServiceComponentConfiguration getGameServiceComponentConfiguration() {
        return this.mGameServiceComponentConfiguration;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GameServiceConfiguration) {
            GameServiceConfiguration gameServiceConfiguration = (GameServiceConfiguration) obj;
            return TextUtils.equals(this.mPackageName, gameServiceConfiguration.mPackageName) && Objects.equals(this.mGameServiceComponentConfiguration, gameServiceConfiguration.mGameServiceComponentConfiguration);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mPackageName, this.mGameServiceComponentConfiguration);
    }

    public String toString() {
        return "GameServiceConfiguration{packageName=" + this.mPackageName + ", gameServiceComponentConfiguration=" + this.mGameServiceComponentConfiguration + '}';
    }

    /* loaded from: classes.dex */
    public static final class GameServiceComponentConfiguration {
        public final ComponentName mGameServiceComponentName;
        public final ComponentName mGameSessionServiceComponentName;
        public final UserHandle mUserHandle;

        public GameServiceComponentConfiguration(UserHandle userHandle, ComponentName componentName, ComponentName componentName2) {
            Objects.requireNonNull(userHandle);
            Objects.requireNonNull(componentName);
            Objects.requireNonNull(componentName2);
            this.mUserHandle = userHandle;
            this.mGameServiceComponentName = componentName;
            this.mGameSessionServiceComponentName = componentName2;
        }

        public UserHandle getUserHandle() {
            return this.mUserHandle;
        }

        public ComponentName getGameServiceComponentName() {
            return this.mGameServiceComponentName;
        }

        public ComponentName getGameSessionServiceComponentName() {
            return this.mGameSessionServiceComponentName;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof GameServiceComponentConfiguration) {
                GameServiceComponentConfiguration gameServiceComponentConfiguration = (GameServiceComponentConfiguration) obj;
                return this.mUserHandle.equals(gameServiceComponentConfiguration.mUserHandle) && this.mGameServiceComponentName.equals(gameServiceComponentConfiguration.mGameServiceComponentName) && this.mGameSessionServiceComponentName.equals(gameServiceComponentConfiguration.mGameSessionServiceComponentName);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mUserHandle, this.mGameServiceComponentName, this.mGameSessionServiceComponentName);
        }

        public String toString() {
            return "GameServiceComponentConfiguration{userHandle=" + this.mUserHandle + ", gameServiceComponentName=" + this.mGameServiceComponentName + ", gameSessionServiceComponentName=" + this.mGameSessionServiceComponentName + "}";
        }
    }
}
