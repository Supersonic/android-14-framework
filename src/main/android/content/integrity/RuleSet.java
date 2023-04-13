package android.content.integrity;

import android.annotation.SystemApi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public class RuleSet {
    private final List<Rule> mRules;
    private final String mVersion;

    private RuleSet(String version, List<Rule> rules) {
        this.mVersion = version;
        this.mRules = Collections.unmodifiableList(rules);
    }

    public String getVersion() {
        return this.mVersion;
    }

    public List<Rule> getRules() {
        return this.mRules;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private List<Rule> mRules = new ArrayList();
        private String mVersion;

        public Builder setVersion(String version) {
            this.mVersion = version;
            return this;
        }

        public Builder addRules(List<Rule> rules) {
            this.mRules.addAll(rules);
            return this;
        }

        public RuleSet build() {
            Objects.requireNonNull(this.mVersion);
            return new RuleSet(this.mVersion, this.mRules);
        }
    }
}
