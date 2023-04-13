package com.android.internal.util;

import android.p008os.Parcel;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public interface Parcelling<T> {
    void parcel(T t, Parcel parcel, int i);

    T unparcel(Parcel parcel);

    /* loaded from: classes3.dex */
    public static class Cache {
        private static ArrayMap<Class, Parcelling> sCache = new ArrayMap<>();

        private Cache() {
        }

        public static <P extends Parcelling<?>> P get(Class<P> clazz) {
            return (P) sCache.get(clazz);
        }

        public static <P extends Parcelling<?>> P put(P parcelling) {
            sCache.put(parcelling.getClass(), parcelling);
            return parcelling;
        }

        public static <P extends Parcelling<?>> P getOrCreate(Class<P> clazz) {
            P cached = (P) get(clazz);
            if (cached != null) {
                return cached;
            }
            try {
                return (P) put(clazz.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* loaded from: classes3.dex */
    public interface BuiltIn {

        /* loaded from: classes3.dex */
        public static class ForInternedString implements Parcelling<String> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(String item, Parcel dest, int parcelFlags) {
                dest.writeString(item);
            }

            @Override // com.android.internal.util.Parcelling
            public String unparcel(Parcel source) {
                return TextUtils.safeIntern(source.readString());
            }
        }

        /* loaded from: classes3.dex */
        public static class ForInternedStringArray implements Parcelling<String[]> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(String[] item, Parcel dest, int parcelFlags) {
                dest.writeStringArray(item);
            }

            @Override // com.android.internal.util.Parcelling
            public String[] unparcel(Parcel source) {
                String[] array = source.readStringArray();
                if (array != null) {
                    int size = ArrayUtils.size(array);
                    for (int index = 0; index < size; index++) {
                        array[index] = TextUtils.safeIntern(array[index]);
                    }
                }
                return array;
            }
        }

        /* loaded from: classes3.dex */
        public static class ForInternedStringList implements Parcelling<List<String>> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(List<String> item, Parcel dest, int parcelFlags) {
                dest.writeStringList(item);
            }

            @Override // com.android.internal.util.Parcelling
            public List<String> unparcel(Parcel source) {
                ArrayList<String> list = source.createStringArrayList();
                if (list != null) {
                    int size = list.size();
                    for (int index = 0; index < size; index++) {
                        list.set(index, list.get(index).intern());
                    }
                }
                return CollectionUtils.emptyIfNull(list);
            }
        }

        /* loaded from: classes3.dex */
        public static class ForInternedStringValueMap implements Parcelling<Map<String, String>> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(Map<String, String> item, Parcel dest, int parcelFlags) {
                dest.writeMap(item);
            }

            @Override // com.android.internal.util.Parcelling
            public Map<String, String> unparcel(Parcel source) {
                ArrayMap<String, String> map = new ArrayMap<>();
                source.readMap(map, String.class.getClassLoader());
                for (int index = 0; index < map.size(); index++) {
                    map.setValueAt(index, TextUtils.safeIntern(map.valueAt(index)));
                }
                return map;
            }
        }

        /* loaded from: classes3.dex */
        public static class ForStringSet implements Parcelling<Set<String>> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(Set<String> item, Parcel dest, int parcelFlags) {
                if (item == null) {
                    dest.writeInt(-1);
                    return;
                }
                dest.writeInt(item.size());
                for (String string : item) {
                    dest.writeString(string);
                }
            }

            @Override // com.android.internal.util.Parcelling
            public Set<String> unparcel(Parcel source) {
                int size = source.readInt();
                if (size < 0) {
                    return Collections.emptySet();
                }
                Set<String> set = new ArraySet<>();
                for (int count = 0; count < size; count++) {
                    set.add(source.readString());
                }
                return set;
            }
        }

        /* loaded from: classes3.dex */
        public static class ForInternedStringSet implements Parcelling<Set<String>> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(Set<String> item, Parcel dest, int parcelFlags) {
                if (item == null) {
                    dest.writeInt(-1);
                    return;
                }
                dest.writeInt(item.size());
                for (String string : item) {
                    dest.writeString(string);
                }
            }

            @Override // com.android.internal.util.Parcelling
            public Set<String> unparcel(Parcel source) {
                int size = source.readInt();
                if (size < 0) {
                    return Collections.emptySet();
                }
                Set<String> set = new ArraySet<>();
                for (int count = 0; count < size; count++) {
                    set.add(TextUtils.safeIntern(source.readString()));
                }
                return set;
            }
        }

        /* loaded from: classes3.dex */
        public static class ForInternedStringArraySet implements Parcelling<ArraySet<String>> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(ArraySet<String> item, Parcel dest, int parcelFlags) {
                if (item == null) {
                    dest.writeInt(-1);
                    return;
                }
                dest.writeInt(item.size());
                Iterator<String> it = item.iterator();
                while (it.hasNext()) {
                    String string = it.next();
                    dest.writeString(string);
                }
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.internal.util.Parcelling
            public ArraySet<String> unparcel(Parcel source) {
                int size = source.readInt();
                if (size < 0) {
                    return null;
                }
                ArraySet<String> set = new ArraySet<>();
                for (int count = 0; count < size; count++) {
                    set.add(TextUtils.safeIntern(source.readString()));
                }
                return set;
            }
        }

        /* loaded from: classes3.dex */
        public static class ForBoolean implements Parcelling<Boolean> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(Boolean item, Parcel dest, int parcelFlags) {
                if (item == null) {
                    dest.writeInt(1);
                } else if (!item.booleanValue()) {
                    dest.writeInt(0);
                } else {
                    dest.writeInt(-1);
                }
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.internal.util.Parcelling
            public Boolean unparcel(Parcel source) {
                switch (source.readInt()) {
                    case -1:
                        return Boolean.TRUE;
                    case 0:
                        return Boolean.FALSE;
                    case 1:
                        return null;
                    default:
                        throw new IllegalStateException("Malformed Parcel reading Boolean: " + source);
                }
            }
        }

        /* loaded from: classes3.dex */
        public static class ForPattern implements Parcelling<Pattern> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(Pattern item, Parcel dest, int parcelFlags) {
                dest.writeString(item == null ? null : item.pattern());
            }

            @Override // com.android.internal.util.Parcelling
            public Pattern unparcel(Parcel source) {
                String s = source.readString();
                if (s == null) {
                    return null;
                }
                return Pattern.compile(s);
            }
        }

        /* loaded from: classes3.dex */
        public static class ForUUID implements Parcelling<UUID> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(UUID item, Parcel dest, int parcelFlags) {
                dest.writeString(item == null ? null : item.toString());
            }

            @Override // com.android.internal.util.Parcelling
            public UUID unparcel(Parcel source) {
                String string = source.readString();
                if (string == null) {
                    return null;
                }
                return UUID.fromString(string);
            }
        }

        /* loaded from: classes3.dex */
        public static class ForInstant implements Parcelling<Instant> {
            @Override // com.android.internal.util.Parcelling
            public void parcel(Instant item, Parcel dest, int parcelFlags) {
                dest.writeLong(item == null ? Long.MIN_VALUE : item.getEpochSecond());
                dest.writeInt(item == null ? Integer.MIN_VALUE : item.getNano());
            }

            @Override // com.android.internal.util.Parcelling
            public Instant unparcel(Parcel source) {
                long epochSecond = source.readLong();
                int afterNano = source.readInt();
                if (epochSecond == Long.MIN_VALUE) {
                    return null;
                }
                return Instant.ofEpochSecond(epochSecond, afterNano);
            }
        }
    }
}
