package com.android.server.app;

import android.content.ComponentName;
import android.service.games.IGameSession;
import android.view.SurfaceControlViewHost;
import java.util.Objects;
/* loaded from: classes.dex */
public final class GameSessionRecord {
    public final IGameSession mIGameSession;
    public final ComponentName mRootComponentName;
    public final State mState;
    public final SurfaceControlViewHost.SurfacePackage mSurfacePackage;
    public final int mTaskId;

    /* loaded from: classes.dex */
    public enum State {
        NO_GAME_SESSION_REQUESTED,
        GAME_SESSION_REQUESTED,
        GAME_SESSION_ATTACHED,
        GAME_SESSION_ENDED_PROCESS_DEATH
    }

    public static GameSessionRecord awaitingGameSessionRequest(int i, ComponentName componentName) {
        return new GameSessionRecord(i, State.NO_GAME_SESSION_REQUESTED, componentName, null, null);
    }

    public GameSessionRecord(int i, State state, ComponentName componentName, IGameSession iGameSession, SurfaceControlViewHost.SurfacePackage surfacePackage) {
        this.mTaskId = i;
        this.mState = state;
        this.mRootComponentName = componentName;
        this.mIGameSession = iGameSession;
        this.mSurfacePackage = surfacePackage;
    }

    public boolean isAwaitingGameSessionRequest() {
        return this.mState == State.NO_GAME_SESSION_REQUESTED;
    }

    public GameSessionRecord withGameSessionRequested() {
        return new GameSessionRecord(this.mTaskId, State.GAME_SESSION_REQUESTED, this.mRootComponentName, null, null);
    }

    public boolean isGameSessionRequested() {
        return this.mState == State.GAME_SESSION_REQUESTED;
    }

    public GameSessionRecord withGameSession(IGameSession iGameSession, SurfaceControlViewHost.SurfacePackage surfacePackage) {
        Objects.requireNonNull(iGameSession);
        return new GameSessionRecord(this.mTaskId, State.GAME_SESSION_ATTACHED, this.mRootComponentName, iGameSession, surfacePackage);
    }

    public GameSessionRecord withGameSessionEndedOnProcessDeath() {
        return new GameSessionRecord(this.mTaskId, State.GAME_SESSION_ENDED_PROCESS_DEATH, this.mRootComponentName, null, null);
    }

    public boolean isGameSessionEndedForProcessDeath() {
        return this.mState == State.GAME_SESSION_ENDED_PROCESS_DEATH;
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public ComponentName getComponentName() {
        return this.mRootComponentName;
    }

    public IGameSession getGameSession() {
        return this.mIGameSession;
    }

    public SurfaceControlViewHost.SurfacePackage getSurfacePackage() {
        return this.mSurfacePackage;
    }

    public String toString() {
        return "GameSessionRecord{mTaskId=" + this.mTaskId + ", mState=" + this.mState + ", mRootComponentName=" + this.mRootComponentName + ", mIGameSession=" + this.mIGameSession + ", mSurfacePackage=" + this.mSurfacePackage + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GameSessionRecord) {
            GameSessionRecord gameSessionRecord = (GameSessionRecord) obj;
            return this.mTaskId == gameSessionRecord.mTaskId && this.mState == gameSessionRecord.mState && this.mRootComponentName.equals(gameSessionRecord.mRootComponentName) && Objects.equals(this.mIGameSession, gameSessionRecord.mIGameSession) && Objects.equals(this.mSurfacePackage, gameSessionRecord.mSurfacePackage);
        }
        return false;
    }

    public int hashCode() {
        Integer valueOf = Integer.valueOf(this.mTaskId);
        State state = this.mState;
        return Objects.hash(valueOf, state, this.mRootComponentName, this.mIGameSession, state, this.mSurfacePackage);
    }
}
