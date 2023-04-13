package com.android.server.backup.keyvalue;
/* loaded from: classes.dex */
class AgentException extends BackupException {
    private final boolean mTransitory;

    public static AgentException transitory() {
        return new AgentException(true);
    }

    public static AgentException transitory(Exception exc) {
        return new AgentException(true, exc);
    }

    public static AgentException permanent() {
        return new AgentException(false);
    }

    public static AgentException permanent(Exception exc) {
        return new AgentException(false, exc);
    }

    public AgentException(boolean z) {
        this.mTransitory = z;
    }

    public AgentException(boolean z, Exception exc) {
        super(exc);
        this.mTransitory = z;
    }

    public boolean isTransitory() {
        return this.mTransitory;
    }
}
