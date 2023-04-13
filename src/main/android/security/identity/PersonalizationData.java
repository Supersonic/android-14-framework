package android.security.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
/* loaded from: classes3.dex */
public class PersonalizationData {
    private LinkedHashMap<String, NamespaceData> mNamespaces;
    private ArrayList<AccessControlProfile> mProfiles;

    private PersonalizationData() {
        this.mProfiles = new ArrayList<>();
        this.mNamespaces = new LinkedHashMap<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Collection<AccessControlProfile> getAccessControlProfiles() {
        return Collections.unmodifiableCollection(this.mProfiles);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Collection<String> getNamespaces() {
        return Collections.unmodifiableCollection(this.mNamespaces.keySet());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NamespaceData getNamespaceData(String namespace) {
        return this.mNamespaces.get(namespace);
    }

    /* loaded from: classes3.dex */
    static class NamespaceData {
        private LinkedHashMap<String, EntryData> mEntries;
        private String mNamespace;

        private NamespaceData(String namespace) {
            this.mEntries = new LinkedHashMap<>();
            this.mNamespace = namespace;
        }

        String getNamespaceName() {
            return this.mNamespace;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Collection<String> getEntryNames() {
            return Collections.unmodifiableCollection(this.mEntries.keySet());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Collection<AccessControlProfileId> getAccessControlProfileIds(String name) {
            EntryData value = this.mEntries.get(name);
            if (value != null) {
                return value.mAccessControlProfileIds;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public byte[] getEntryValue(String name) {
            EntryData value = this.mEntries.get(name);
            if (value != null) {
                return value.mValue;
            }
            return null;
        }
    }

    /* loaded from: classes3.dex */
    private static class EntryData {
        Collection<AccessControlProfileId> mAccessControlProfileIds;
        byte[] mValue;

        EntryData(byte[] value, Collection<AccessControlProfileId> accessControlProfileIds) {
            this.mValue = value;
            this.mAccessControlProfileIds = accessControlProfileIds;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private PersonalizationData mData = new PersonalizationData();

        public Builder putEntry(String namespace, String name, Collection<AccessControlProfileId> accessControlProfileIds, byte[] value) {
            NamespaceData namespaceData = (NamespaceData) this.mData.mNamespaces.get(namespace);
            if (namespaceData == null) {
                namespaceData = new NamespaceData(namespace);
                this.mData.mNamespaces.put(namespace, namespaceData);
            }
            namespaceData.mEntries.put(name, new EntryData(value, accessControlProfileIds));
            return this;
        }

        public Builder addAccessControlProfile(AccessControlProfile profile) {
            this.mData.mProfiles.add(profile);
            return this;
        }

        public PersonalizationData build() {
            return this.mData;
        }
    }
}
