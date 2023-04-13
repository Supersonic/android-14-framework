package android.hardware.camera2.utils;

import android.util.Size;
import com.android.internal.util.Preconditions;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class SizeAreaComparator implements Comparator<Size> {
    @Override // java.util.Comparator
    public int compare(Size size, Size size2) {
        Preconditions.checkNotNull(size, "size must not be null");
        Preconditions.checkNotNull(size2, "size2 must not be null");
        if (size.equals(size2)) {
            return 0;
        }
        long width = size.getWidth();
        long width2 = size2.getWidth();
        long area = size.getHeight() * width;
        long area2 = size2.getHeight() * width2;
        return area == area2 ? width > width2 ? 1 : -1 : area > area2 ? 1 : -1;
    }

    public static Size findLargestByArea(List<Size> sizes) {
        Preconditions.checkNotNull(sizes, "sizes must not be null");
        return (Size) Collections.max(sizes, new SizeAreaComparator());
    }
}
