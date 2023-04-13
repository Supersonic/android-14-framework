package android.webkit;

import java.io.Serializable;
/* loaded from: classes4.dex */
public abstract class WebBackForwardList implements Cloneable, Serializable {
    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: clone */
    public abstract WebBackForwardList m5670clone();

    public abstract int getCurrentIndex();

    public abstract WebHistoryItem getCurrentItem();

    public abstract WebHistoryItem getItemAtIndex(int i);

    public abstract int getSize();
}
