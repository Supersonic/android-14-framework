package android.hardware.camera2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class ArrayUtils {
    private static final boolean DEBUG = false;
    private static final String TAG = "ArrayUtils";

    public static <T> int getArrayIndex(T[] array, T needle) {
        if (array == null) {
            return -1;
        }
        int index = 0;
        for (T elem : array) {
            if (!Objects.equals(elem, needle)) {
                index++;
            } else {
                return index;
            }
        }
        return -1;
    }

    public static int getArrayIndex(int[] array, int needle) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == needle) {
                return i;
            }
        }
        return -1;
    }

    public static int[] convertStringListToIntArray(List<String> list, String[] convertFrom, int[] convertTo) {
        if (list == null) {
            return null;
        }
        List<Integer> convertedList = convertStringListToIntList(list, convertFrom, convertTo);
        int[] returnArray = new int[convertedList.size()];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = convertedList.get(i).intValue();
        }
        return returnArray;
    }

    public static List<Integer> convertStringListToIntList(List<String> list, String[] convertFrom, int[] convertTo) {
        if (list == null) {
            return null;
        }
        List<Integer> convertedList = new ArrayList<>(list.size());
        for (String str : list) {
            int strIndex = getArrayIndex(convertFrom, str);
            if (strIndex >= 0 && strIndex < convertTo.length) {
                convertedList.add(Integer.valueOf(convertTo[strIndex]));
            }
        }
        return convertedList;
    }

    public static int[] toIntArray(List<Integer> list) {
        if (list == null) {
            return null;
        }
        int[] arr = new int[list.size()];
        int i = 0;
        for (Integer num : list) {
            int elem = num.intValue();
            arr[i] = elem;
            i++;
        }
        return arr;
    }

    public static boolean contains(int[] array, int elem) {
        return getArrayIndex(array, elem) != -1;
    }

    public static <T> boolean contains(T[] array, T elem) {
        return getArrayIndex(array, elem) != -1;
    }

    private ArrayUtils() {
        throw new AssertionError();
    }
}
