package android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Map;
/* loaded from: classes4.dex */
public class SimpleExpandableListAdapter extends BaseExpandableListAdapter {
    private List<? extends List<? extends Map<String, ?>>> mChildData;
    private String[] mChildFrom;
    private int mChildLayout;
    private int[] mChildTo;
    private int mCollapsedGroupLayout;
    private int mExpandedGroupLayout;
    private List<? extends Map<String, ?>> mGroupData;
    private String[] mGroupFrom;
    private int[] mGroupTo;
    private LayoutInflater mInflater;
    private int mLastChildLayout;

    public SimpleExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        this(context, groupData, groupLayout, groupLayout, groupFrom, groupTo, childData, childLayout, childLayout, childFrom, childTo);
    }

    public SimpleExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        this(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout, childLayout, childFrom, childTo);
    }

    public SimpleExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        this.mGroupData = groupData;
        this.mExpandedGroupLayout = expandedGroupLayout;
        this.mCollapsedGroupLayout = collapsedGroupLayout;
        this.mGroupFrom = groupFrom;
        this.mGroupTo = groupTo;
        this.mChildData = childData;
        this.mChildLayout = childLayout;
        this.mLastChildLayout = lastChildLayout;
        this.mChildFrom = childFrom;
        this.mChildTo = childTo;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int groupPosition, int childPosition) {
        return this.mChildData.get(groupPosition).get(childPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newChildView(isLastChild, parent);
        } else {
            v = convertView;
        }
        bindView(v, this.mChildData.get(groupPosition).get(childPosition), this.mChildFrom, this.mChildTo);
        return v;
    }

    public View newChildView(boolean isLastChild, ViewGroup parent) {
        return this.mInflater.inflate(isLastChild ? this.mLastChildLayout : this.mChildLayout, parent, false);
    }

    private void bindView(View view, Map<String, ?> data, String[] from, int[] to) {
        int len = to.length;
        for (int i = 0; i < len; i++) {
            TextView v = (TextView) view.findViewById(to[i]);
            if (v != null) {
                v.setText((String) data.get(from[i]));
            }
        }
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int groupPosition) {
        return this.mChildData.get(groupPosition).size();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int groupPosition) {
        return this.mGroupData.get(groupPosition);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.mGroupData.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newGroupView(isExpanded, parent);
        } else {
            v = convertView;
        }
        bindView(v, this.mGroupData.get(groupPosition), this.mGroupFrom, this.mGroupTo);
        return v;
    }

    public View newGroupView(boolean isExpanded, ViewGroup parent) {
        return this.mInflater.inflate(isExpanded ? this.mExpandedGroupLayout : this.mCollapsedGroupLayout, parent, false);
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }
}
