package android.app.slice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
/* loaded from: classes.dex */
public class SliceQuery {
    private static final String TAG = "SliceQuery";

    public static SliceItem getPrimaryIcon(Slice slice) {
        SliceItem icon;
        for (SliceItem item : slice.getItems()) {
            if (Objects.equals(item.getFormat(), SliceItem.FORMAT_IMAGE)) {
                return item;
            }
            if (!compareTypes(item, "slice") || !item.hasHint(Slice.HINT_LIST)) {
                if (!item.hasHint(Slice.HINT_ACTIONS) && !item.hasHint(Slice.HINT_LIST_ITEM) && !compareTypes(item, "action") && (icon = find(item, SliceItem.FORMAT_IMAGE)) != null) {
                    return icon;
                }
            }
        }
        return null;
    }

    public static SliceItem findNotContaining(SliceItem container, List<SliceItem> list) {
        SliceItem ret = null;
        while (ret == null && list.size() != 0) {
            SliceItem remove = list.remove(0);
            if (!contains(container, remove)) {
                ret = remove;
            }
        }
        return ret;
    }

    private static boolean contains(SliceItem container, final SliceItem item) {
        if (container == null || item == null) {
            return false;
        }
        return stream(container).filter(new Predicate() { // from class: android.app.slice.SliceQuery$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SliceQuery.lambda$contains$0(SliceItem.this, (SliceItem) obj);
            }
        }).findAny().isPresent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$contains$0(SliceItem item, SliceItem s) {
        return s == item;
    }

    public static List<SliceItem> findAll(SliceItem s, String type) {
        return findAll(s, type, (String[]) null, (String[]) null);
    }

    public static List<SliceItem> findAll(SliceItem s, String type, String hints, String nonHints) {
        return findAll(s, type, new String[]{hints}, new String[]{nonHints});
    }

    public static List<SliceItem> findAll(SliceItem s, final String type, final String[] hints, final String[] nonHints) {
        return (List) stream(s).filter(new Predicate() { // from class: android.app.slice.SliceQuery$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SliceQuery.lambda$findAll$1(type, hints, nonHints, (SliceItem) obj);
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$findAll$1(String type, String[] hints, String[] nonHints, SliceItem item) {
        return compareTypes(item, type) && item.hasHints(hints) && !item.hasAnyHints(nonHints);
    }

    public static SliceItem find(Slice s, String type, String hints, String nonHints) {
        return find(s, type, new String[]{hints}, new String[]{nonHints});
    }

    public static SliceItem find(Slice s, String type) {
        return find(s, type, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem s, String type) {
        return find(s, type, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem s, String type, String hints, String nonHints) {
        return find(s, type, new String[]{hints}, new String[]{nonHints});
    }

    public static SliceItem find(Slice s, String type, String[] hints, String[] nonHints) {
        List<String> h = s.getHints();
        return find(new SliceItem(s, "slice", (String) null, (String[]) h.toArray(new String[h.size()])), type, hints, nonHints);
    }

    public static SliceItem find(SliceItem s, final String type, final String[] hints, final String[] nonHints) {
        return stream(s).filter(new Predicate() { // from class: android.app.slice.SliceQuery$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SliceQuery.lambda$find$2(type, hints, nonHints, (SliceItem) obj);
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$find$2(String type, String[] hints, String[] nonHints, SliceItem item) {
        return compareTypes(item, type) && item.hasHints(hints) && !item.hasAnyHints(nonHints);
    }

    public static Stream<SliceItem> stream(SliceItem slice) {
        final Queue<SliceItem> items = new LinkedList<>();
        items.add(slice);
        Iterator<SliceItem> iterator = new Iterator<SliceItem>() { // from class: android.app.slice.SliceQuery.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return items.size() != 0;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public SliceItem next() {
                SliceItem item = (SliceItem) items.poll();
                if (SliceQuery.compareTypes(item, "slice") || SliceQuery.compareTypes(item, "action")) {
                    items.addAll(item.getSlice().getItems());
                }
                return item;
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    public static boolean compareTypes(SliceItem item, String desiredType) {
        int typeLength = desiredType.length();
        if (typeLength == 3 && desiredType.equals("*/*")) {
            return true;
        }
        if (item.getSubType() == null && desiredType.indexOf(47) < 0) {
            return item.getFormat().equals(desiredType);
        }
        return (item.getFormat() + "/" + item.getSubType()).matches(desiredType.replaceAll("\\*", ".*"));
    }
}
