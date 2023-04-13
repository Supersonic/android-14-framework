package com.android.server.apphibernation;
/* loaded from: classes.dex */
public abstract class AppHibernationManagerInternal {
    public abstract boolean isHibernatingForUser(String str, int i);

    public abstract boolean isHibernatingGlobally(String str);

    public abstract boolean isOatArtifactDeletionEnabled();

    public abstract void setHibernatingForUser(String str, int i, boolean z);

    public abstract void setHibernatingGlobally(String str, boolean z);
}
