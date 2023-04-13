package android.view.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes4.dex */
public final class IntFlagMapping {
    private final List<Flag> mFlags = new ArrayList();

    public Set<String> get(int value) {
        Set<String> enabledFlagNames = new HashSet<>(this.mFlags.size());
        for (Flag flag : this.mFlags) {
            if (flag.isEnabledFor(value)) {
                enabledFlagNames.add(flag.mName);
            }
        }
        return Collections.unmodifiableSet(enabledFlagNames);
    }

    public void add(int mask, int target, String name) {
        this.mFlags.add(new Flag(mask, target, name));
    }

    /* loaded from: classes4.dex */
    private static final class Flag {
        private final int mMask;
        private final String mName;
        private final int mTarget;

        private Flag(int mask, int target, String name) {
            this.mTarget = target;
            this.mMask = mask;
            this.mName = (String) Objects.requireNonNull(name);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEnabledFor(int value) {
            return (this.mMask & value) == this.mTarget;
        }
    }
}
