package com.android.framework.protobuf;

import com.android.framework.protobuf.GeneratedMessageLite;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class ExtensionRegistryLite {
    static final String EXTENSION_CLASS_NAME = "com.android.framework.protobuf.Extension";
    private static volatile ExtensionRegistryLite emptyRegistry;
    private final Map<ObjectIntPair, GeneratedMessageLite.GeneratedExtension<?, ?>> extensionsByNumber;
    private static volatile boolean eagerlyParseMessageSets = false;
    private static boolean doFullRuntimeInheritanceCheck = true;
    static final ExtensionRegistryLite EMPTY_REGISTRY_LITE = new ExtensionRegistryLite(true);

    /* loaded from: classes4.dex */
    private static class ExtensionClassHolder {
        static final Class<?> INSTANCE = resolveExtensionClass();

        private ExtensionClassHolder() {
        }

        static Class<?> resolveExtensionClass() {
            try {
                return Class.forName(ExtensionRegistryLite.EXTENSION_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    public static boolean isEagerlyParseMessageSets() {
        return eagerlyParseMessageSets;
    }

    public static void setEagerlyParseMessageSets(boolean isEagerlyParse) {
        eagerlyParseMessageSets = isEagerlyParse;
    }

    public static ExtensionRegistryLite newInstance() {
        if (doFullRuntimeInheritanceCheck) {
            return ExtensionRegistryFactory.create();
        }
        return new ExtensionRegistryLite();
    }

    public static ExtensionRegistryLite getEmptyRegistry() {
        ExtensionRegistryLite extensionRegistryLite;
        ExtensionRegistryLite result = emptyRegistry;
        if (result == null) {
            synchronized (ExtensionRegistryLite.class) {
                result = emptyRegistry;
                if (result == null) {
                    if (doFullRuntimeInheritanceCheck) {
                        extensionRegistryLite = ExtensionRegistryFactory.createEmpty();
                    } else {
                        extensionRegistryLite = EMPTY_REGISTRY_LITE;
                    }
                    emptyRegistry = extensionRegistryLite;
                    result = extensionRegistryLite;
                }
            }
        }
        return result;
    }

    public ExtensionRegistryLite getUnmodifiable() {
        return new ExtensionRegistryLite(this);
    }

    public <ContainingType extends MessageLite> GeneratedMessageLite.GeneratedExtension<ContainingType, ?> findLiteExtensionByNumber(ContainingType containingTypeDefaultInstance, int fieldNumber) {
        return (GeneratedMessageLite.GeneratedExtension<ContainingType, ?>) this.extensionsByNumber.get(new ObjectIntPair(containingTypeDefaultInstance, fieldNumber));
    }

    public final void add(GeneratedMessageLite.GeneratedExtension<?, ?> extension) {
        this.extensionsByNumber.put(new ObjectIntPair(extension.getContainingTypeDefaultInstance(), extension.getNumber()), extension);
    }

    public final void add(ExtensionLite<?, ?> extension) {
        if (GeneratedMessageLite.GeneratedExtension.class.isAssignableFrom(extension.getClass())) {
            add((GeneratedMessageLite.GeneratedExtension) extension);
        }
        if (!doFullRuntimeInheritanceCheck || !ExtensionRegistryFactory.isFullRegistry(this)) {
            return;
        }
        try {
            getClass().getMethod("add", ExtensionClassHolder.INSTANCE).invoke(this, extension);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Could not invoke ExtensionRegistry#add for %s", extension), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExtensionRegistryLite() {
        this.extensionsByNumber = new HashMap();
    }

    ExtensionRegistryLite(ExtensionRegistryLite other) {
        if (other == EMPTY_REGISTRY_LITE) {
            this.extensionsByNumber = Collections.emptyMap();
        } else {
            this.extensionsByNumber = Collections.unmodifiableMap(other.extensionsByNumber);
        }
    }

    ExtensionRegistryLite(boolean empty) {
        this.extensionsByNumber = Collections.emptyMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class ObjectIntPair {
        private final int number;
        private final Object object;

        ObjectIntPair(Object object, int number) {
            this.object = object;
            this.number = number;
        }

        public int hashCode() {
            return (System.identityHashCode(this.object) * 65535) + this.number;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ObjectIntPair) {
                ObjectIntPair other = (ObjectIntPair) obj;
                return this.object == other.object && this.number == other.number;
            }
            return false;
        }
    }
}
