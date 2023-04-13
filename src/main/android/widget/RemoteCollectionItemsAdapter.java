package android.widget;

import android.util.SizeF;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.IntStream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class RemoteCollectionItemsAdapter extends BaseAdapter {
    private RemoteViews.ColorResources mColorResources;
    private RemoteViews.InteractionHandler mInteractionHandler;
    private RemoteViews.RemoteCollectionItems mItems;
    private SparseIntArray mLayoutIdToViewType;
    private final int mViewTypeCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteCollectionItemsAdapter(RemoteViews.RemoteCollectionItems items, RemoteViews.InteractionHandler interactionHandler, RemoteViews.ColorResources colorResources) {
        this.mViewTypeCount = items.getViewTypeCount();
        this.mItems = items;
        this.mInteractionHandler = interactionHandler;
        this.mColorResources = colorResources;
        initLayoutIdToViewType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setData(RemoteViews.RemoteCollectionItems items, RemoteViews.InteractionHandler interactionHandler, RemoteViews.ColorResources colorResources) {
        if (this.mViewTypeCount < items.getViewTypeCount()) {
            throw new IllegalArgumentException("RemoteCollectionItemsAdapter cannot increase view type count after creation");
        }
        this.mItems = items;
        this.mInteractionHandler = interactionHandler;
        this.mColorResources = colorResources;
        initLayoutIdToViewType();
        notifyDataSetChanged();
    }

    private void initLayoutIdToViewType() {
        SparseIntArray previousLayoutIdToViewType = this.mLayoutIdToViewType;
        this.mLayoutIdToViewType = new SparseIntArray(this.mViewTypeCount);
        int[] layoutIds = IntStream.range(0, this.mItems.getItemCount()).map(new IntUnaryOperator() { // from class: android.widget.RemoteCollectionItemsAdapter$$ExternalSyntheticLambda0
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i) {
                int lambda$initLayoutIdToViewType$0;
                lambda$initLayoutIdToViewType$0 = RemoteCollectionItemsAdapter.this.lambda$initLayoutIdToViewType$0(i);
                return lambda$initLayoutIdToViewType$0;
            }
        }).distinct().toArray();
        int length = layoutIds.length;
        int i = this.mViewTypeCount;
        if (length > i) {
            throw new IllegalArgumentException("Collection items uses " + layoutIds.length + " distinct layouts, which is more than view type count of " + this.mViewTypeCount);
        }
        boolean[] processedLayoutIdIndices = new boolean[layoutIds.length];
        final boolean[] assignedViewTypes = new boolean[i];
        if (previousLayoutIdToViewType != null) {
            for (int i2 = 0; i2 < layoutIds.length; i2++) {
                int layoutId = layoutIds[i2];
                int previousViewType = previousLayoutIdToViewType.get(layoutId, -1);
                if (previousViewType >= 0) {
                    this.mLayoutIdToViewType.put(layoutId, previousViewType);
                    processedLayoutIdIndices[i2] = true;
                    assignedViewTypes[previousViewType] = true;
                }
            }
        }
        int lastViewType = -1;
        for (int i3 = 0; i3 < layoutIds.length; i3++) {
            if (!processedLayoutIdIndices[i3]) {
                int layoutId2 = layoutIds[i3];
                int viewType = IntStream.range(lastViewType + 1, layoutIds.length).filter(new IntPredicate() { // from class: android.widget.RemoteCollectionItemsAdapter$$ExternalSyntheticLambda1
                    @Override // java.util.function.IntPredicate
                    public final boolean test(int i4) {
                        return RemoteCollectionItemsAdapter.lambda$initLayoutIdToViewType$1(assignedViewTypes, i4);
                    }
                }).findFirst().orElseThrow(new Supplier() { // from class: android.widget.RemoteCollectionItemsAdapter$$ExternalSyntheticLambda2
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        return RemoteCollectionItemsAdapter.lambda$initLayoutIdToViewType$2();
                    }
                });
                this.mLayoutIdToViewType.put(layoutId2, viewType);
                processedLayoutIdIndices[i3] = true;
                assignedViewTypes[viewType] = true;
                lastViewType = viewType;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$initLayoutIdToViewType$0(int position) {
        return this.mItems.getItemView(position).getLayoutId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$initLayoutIdToViewType$1(boolean[] assignedViewTypes, int type) {
        return !assignedViewTypes[type];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ IllegalStateException lambda$initLayoutIdToViewType$2() {
        return new IllegalStateException("RemoteCollectionItems has more distinct layout ids than its view type count");
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mItems.getItemCount();
    }

    @Override // android.widget.Adapter
    public RemoteViews getItem(int position) {
        return this.mItems.getItemView(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return this.mItems.getItemId(position);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        return this.mLayoutIdToViewType.get(this.mItems.getItemView(position).getLayoutId());
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return this.mViewTypeCount;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return this.mItems.hasStableIds();
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position >= getCount()) {
            return null;
        }
        RemoteViews item = this.mItems.getItemView(position);
        item.addFlags(2);
        View reapplyView = getViewToReapply(convertView, item);
        if (reapplyView != null) {
            try {
                item.reapply(parent.getContext(), reapplyView, this.mInteractionHandler, null, this.mColorResources);
                return reapplyView;
            } catch (RuntimeException e) {
            }
        }
        return item.apply(parent.getContext(), parent, this.mInteractionHandler, (SizeF) null, this.mColorResources);
    }

    private static View getViewToReapply(View convertView, RemoteViews item) {
        if (convertView == null) {
            return null;
        }
        Object layoutIdTag = convertView.getTag(16908312);
        if ((layoutIdTag instanceof Integer) && item.getLayoutId() == ((Integer) layoutIdTag).intValue()) {
            return convertView;
        }
        return null;
    }
}
