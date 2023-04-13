package android.provider;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.util.ArrayUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;
/* loaded from: classes3.dex */
public class SettingsStringUtil {
    public static final String DELIMITER = ":";

    private SettingsStringUtil() {
    }

    /* loaded from: classes3.dex */
    public static abstract class ColonDelimitedSet<T> extends HashSet<T> {
        protected abstract T itemFromString(String str);

        public ColonDelimitedSet(String colonSeparatedItems) {
            String[] split;
            for (String cn : TextUtils.split(TextUtils.emptyIfNull(colonSeparatedItems), ":")) {
                add(itemFromString(cn));
            }
        }

        protected String itemToString(T item) {
            return String.valueOf(item);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractCollection
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Iterator<T> it = iterator();
            if (it.hasNext()) {
                sb.append(itemToString(it.next()));
                while (it.hasNext()) {
                    sb.append(":");
                    sb.append(itemToString(it.next()));
                }
            }
            return sb.toString();
        }

        /* loaded from: classes3.dex */
        public static class OfStrings extends ColonDelimitedSet<String> {
            public OfStrings(String colonSeparatedItems) {
                super(colonSeparatedItems);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.provider.SettingsStringUtil.ColonDelimitedSet
            public String itemFromString(String s) {
                return s;
            }

            public static String addAll(String delimitedElements, Collection<String> elements) {
                ColonDelimitedSet<String> set = new OfStrings(delimitedElements);
                return set.addAll(elements) ? set.toString() : delimitedElements;
            }

            public static String add(String delimitedElements, String element) {
                ColonDelimitedSet<String> set = new OfStrings(delimitedElements);
                if (set.contains(element)) {
                    return delimitedElements;
                }
                set.add(element);
                return set.toString();
            }

            public static String remove(String delimitedElements, String element) {
                ColonDelimitedSet<String> set = new OfStrings(delimitedElements);
                if (!set.contains(element)) {
                    return delimitedElements;
                }
                set.remove(element);
                return set.toString();
            }

            public static boolean contains(String delimitedElements, String element) {
                String[] elements = TextUtils.split(delimitedElements, ":");
                return ArrayUtils.indexOf(elements, element) != -1;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class ComponentNameSet extends ColonDelimitedSet<ComponentName> {
        public ComponentNameSet(String colonSeparatedPackageNames) {
            super(colonSeparatedPackageNames);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.provider.SettingsStringUtil.ColonDelimitedSet
        public ComponentName itemFromString(String s) {
            return ComponentName.unflattenFromString(s);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.provider.SettingsStringUtil.ColonDelimitedSet
        public String itemToString(ComponentName item) {
            return item != null ? item.flattenToString() : "null";
        }

        public static String add(String delimitedElements, ComponentName element) {
            ComponentNameSet set = new ComponentNameSet(delimitedElements);
            if (set.contains(element)) {
                return delimitedElements;
            }
            set.add(element);
            return set.toString();
        }

        public static String remove(String delimitedElements, ComponentName element) {
            ComponentNameSet set = new ComponentNameSet(delimitedElements);
            if (!set.contains(element)) {
                return delimitedElements;
            }
            set.remove(element);
            return set.toString();
        }

        public static boolean contains(String delimitedElements, ComponentName element) {
            return ColonDelimitedSet.OfStrings.contains(delimitedElements, element.flattenToString());
        }
    }

    /* loaded from: classes3.dex */
    public static class SettingStringHelper {
        private final ContentResolver mContentResolver;
        private final String mSettingName;
        private final int mUserId;

        public SettingStringHelper(ContentResolver contentResolver, String name, int userId) {
            this.mContentResolver = contentResolver;
            this.mUserId = userId;
            this.mSettingName = name;
        }

        public String read() {
            return Settings.Secure.getStringForUser(this.mContentResolver, this.mSettingName, this.mUserId);
        }

        public boolean write(String value) {
            return Settings.Secure.putStringForUser(this.mContentResolver, this.mSettingName, value, this.mUserId);
        }

        public boolean modify(Function<String, String> change) {
            return write(change.apply(read()));
        }
    }
}
