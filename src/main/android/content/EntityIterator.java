package android.content;

import java.util.Iterator;
/* loaded from: classes.dex */
public interface EntityIterator extends Iterator<Entity> {
    void close();

    void reset();
}
