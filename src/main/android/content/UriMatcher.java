package android.content;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class UriMatcher {
    private static final int EXACT = 0;
    public static final int NO_MATCH = -1;
    private static final int NUMBER = 1;
    private static final int TEXT = 2;
    private ArrayList<UriMatcher> mChildren;
    private int mCode;
    private final String mText;
    private final int mWhich;

    public UriMatcher(int code) {
        this.mCode = code;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    private UriMatcher(int which, String text) {
        this.mCode = -1;
        this.mWhich = which;
        this.mChildren = new ArrayList<>();
        this.mText = text;
    }

    public void addURI(String authority, String path, int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code " + code + " is invalid: it must be positive");
        }
        String[] tokens = null;
        if (path != null) {
            String newPath = path;
            if (path.length() > 1 && path.charAt(0) == '/') {
                newPath = path.substring(1);
            }
            tokens = newPath.split("/");
        }
        int numTokens = tokens != null ? tokens.length : 0;
        UriMatcher node = this;
        int i = -1;
        while (i < numTokens) {
            String token = i < 0 ? authority : tokens[i];
            ArrayList<UriMatcher> children = node.mChildren;
            int numChildren = children.size();
            int j = 0;
            while (true) {
                if (j >= numChildren) {
                    break;
                }
                UriMatcher child = children.get(j);
                if (!token.equals(child.mText)) {
                    j++;
                } else {
                    node = child;
                    break;
                }
            }
            if (j == numChildren) {
                UriMatcher child2 = createChild(token);
                node.mChildren.add(child2);
                node = child2;
            }
            i++;
        }
        node.mCode = code;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static UriMatcher createChild(String token) {
        char c;
        switch (token.hashCode()) {
            case 35:
                if (token.equals("#")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 42:
                if (token.equals("*")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return new UriMatcher(1, "#");
            case 1:
                return new UriMatcher(2, "*");
            default:
                return new UriMatcher(0, token);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x006b A[LOOP:0: B:9:0x0015->B:40:0x006b, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0069 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int match(Uri uri) {
        int k;
        List<String> pathSegments = uri.getPathSegments();
        int li = pathSegments.size();
        UriMatcher node = this;
        if (li == 0 && uri.getAuthority() == null) {
            return this.mCode;
        }
        int i = -1;
        while (i < li) {
            String u = i < 0 ? uri.getAuthority() : pathSegments.get(i);
            ArrayList<UriMatcher> list = node.mChildren;
            if (list != null) {
                node = null;
                int lj = list.size();
                for (int j = 0; j < lj; j++) {
                    UriMatcher n = list.get(j);
                    switch (n.mWhich) {
                        case 0:
                            if (n.mText.equals(u)) {
                                node = n;
                                break;
                            }
                            break;
                        case 1:
                            int lk = u.length();
                            while (true) {
                                if (k < lk) {
                                    char c = u.charAt(k);
                                    k = (c >= '0' && c <= '9') ? k + 1 : 0;
                                } else {
                                    node = n;
                                    break;
                                }
                            }
                            break;
                        case 2:
                            node = n;
                            break;
                    }
                    if (node != null) {
                        if (node == null) {
                            i++;
                        } else {
                            return -1;
                        }
                    }
                }
                if (node == null) {
                }
            } else {
                int i2 = node.mCode;
                return i2;
            }
        }
        int i22 = node.mCode;
        return i22;
    }
}
