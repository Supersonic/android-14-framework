package com.android.internal.org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes4.dex */
public class CompositeAlgorithmSpec implements AlgorithmParameterSpec {
    private final List<String> algorithmNames;
    private final List<AlgorithmParameterSpec> parameterSpecs;

    /* loaded from: classes4.dex */
    public static class Builder {
        private List<String> algorithmNames = new ArrayList();
        private List<AlgorithmParameterSpec> parameterSpecs = new ArrayList();

        public Builder add(String algorithmName) {
            this.algorithmNames.add(algorithmName);
            this.parameterSpecs.add(null);
            return this;
        }

        public Builder add(String algorithmName, AlgorithmParameterSpec parameterSpec) {
            this.algorithmNames.add(algorithmName);
            this.parameterSpecs.add(parameterSpec);
            return this;
        }

        public CompositeAlgorithmSpec build() {
            if (this.algorithmNames.isEmpty()) {
                throw new IllegalStateException("cannot call build with no algorithm names added");
            }
            return new CompositeAlgorithmSpec(this);
        }
    }

    public CompositeAlgorithmSpec(Builder builder) {
        this.algorithmNames = Collections.unmodifiableList(new ArrayList(builder.algorithmNames));
        this.parameterSpecs = Collections.unmodifiableList(new ArrayList(builder.parameterSpecs));
    }

    public List<String> getAlgorithmNames() {
        return this.algorithmNames;
    }

    public List<AlgorithmParameterSpec> getParameterSpecs() {
        return this.parameterSpecs;
    }
}
