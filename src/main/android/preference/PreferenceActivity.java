package android.preference;

import android.animation.LayoutTransition;
import android.app.Fragment;
import android.app.FragmentBreadCrumbs;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
@Deprecated
/* loaded from: classes3.dex */
public abstract class PreferenceActivity extends ListActivity implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragment.OnPreferenceStartFragmentCallback {
    private static final String BACK_STACK_PREFS = ":android:prefs";
    private static final String CUR_HEADER_TAG = ":android:cur_header";
    public static final String EXTRA_NO_HEADERS = ":android:no_headers";
    private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
    private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
    private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
    private static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
    public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";
    public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE = ":android:show_fragment_short_title";
    public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";
    private static final int FIRST_REQUEST_CODE = 100;
    private static final String HEADERS_TAG = ":android:headers";
    public static final long HEADER_ID_UNDEFINED = -1;
    private static final int MSG_BIND_PREFERENCES = 1;
    private static final int MSG_BUILD_HEADERS = 2;
    private static final String PREFERENCES_TAG = ":android:preferences";
    private static final String TAG = "PreferenceActivity";
    private CharSequence mActivityTitle;
    private Header mCurHeader;
    private FragmentBreadCrumbs mFragmentBreadCrumbs;
    private ViewGroup mHeadersContainer;
    private FrameLayout mListFooter;
    private Button mNextButton;
    private PreferenceManager mPreferenceManager;
    private ViewGroup mPrefsContainer;
    private Bundle mSavedInstanceState;
    private boolean mSinglePane;
    private final ArrayList<Header> mHeaders = new ArrayList<>();
    private int mPreferenceHeaderItemResId = 0;
    private boolean mPreferenceHeaderRemoveEmptyIcon = false;
    private Handler mHandler = new Handler() { // from class: android.preference.PreferenceActivity.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            PreferenceActivity preferenceActivity;
            Header mappedHeader;
            switch (msg.what) {
                case 1:
                    PreferenceActivity.this.bindPreferences();
                    return;
                case 2:
                    ArrayList<Header> oldHeaders = new ArrayList<>(PreferenceActivity.this.mHeaders);
                    PreferenceActivity.this.mHeaders.clear();
                    PreferenceActivity preferenceActivity2 = PreferenceActivity.this;
                    preferenceActivity2.onBuildHeaders(preferenceActivity2.mHeaders);
                    if (PreferenceActivity.this.mAdapter instanceof BaseAdapter) {
                        ((BaseAdapter) PreferenceActivity.this.mAdapter).notifyDataSetChanged();
                    }
                    Header header = PreferenceActivity.this.onGetNewHeader();
                    if (header != null && header.fragment != null) {
                        Header mappedHeader2 = PreferenceActivity.this.findBestMatchingHeader(header, oldHeaders);
                        if (mappedHeader2 == null || PreferenceActivity.this.mCurHeader != mappedHeader2) {
                            PreferenceActivity.this.switchToHeader(header);
                            return;
                        }
                        return;
                    } else if (PreferenceActivity.this.mCurHeader != null && (mappedHeader = (preferenceActivity = PreferenceActivity.this).findBestMatchingHeader(preferenceActivity.mCurHeader, PreferenceActivity.this.mHeaders)) != null) {
                        PreferenceActivity.this.setSelectedHeader(mappedHeader);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    };

    /* loaded from: classes3.dex */
    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private LayoutInflater mInflater;
        private int mLayoutResId;
        private boolean mRemoveIconIfEmpty;

        /* loaded from: classes3.dex */
        private static class HeaderViewHolder {
            ImageView icon;
            TextView summary;
            TextView title;

            private HeaderViewHolder() {
            }
        }

        public HeaderAdapter(Context context, List<Header> objects, int layoutResId, boolean removeIconBehavior) {
            super(context, 0, objects);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayoutResId = layoutResId;
            this.mRemoveIconIfEmpty = removeIconBehavior;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            HeaderViewHolder holder;
            if (convertView == null) {
                view = this.mInflater.inflate(this.mLayoutResId, parent, false);
                holder = new HeaderViewHolder();
                holder.icon = (ImageView) view.findViewById(16908294);
                holder.title = (TextView) view.findViewById(16908310);
                holder.summary = (TextView) view.findViewById(16908304);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }
            Header header = getItem(position);
            if (this.mRemoveIconIfEmpty) {
                if (header.iconRes != 0) {
                    holder.icon.setVisibility(0);
                    holder.icon.setImageResource(header.iconRes);
                } else {
                    holder.icon.setVisibility(8);
                }
            } else {
                holder.icon.setImageResource(header.iconRes);
            }
            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());
            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(0);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(8);
            }
            return view;
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class Header implements Parcelable {
        public static final Parcelable.Creator<Header> CREATOR = new Parcelable.Creator<Header>() { // from class: android.preference.PreferenceActivity.Header.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Header createFromParcel(Parcel source) {
                return new Header(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Header[] newArray(int size) {
                return new Header[size];
            }
        };
        public CharSequence breadCrumbShortTitle;
        public int breadCrumbShortTitleRes;
        public CharSequence breadCrumbTitle;
        public int breadCrumbTitleRes;
        public Bundle extras;
        public String fragment;
        public Bundle fragmentArguments;
        public int iconRes;

        /* renamed from: id */
        public long f336id = -1;
        public Intent intent;
        public CharSequence summary;
        public int summaryRes;
        public CharSequence title;
        public int titleRes;

        public Header() {
        }

        public CharSequence getTitle(Resources res) {
            int i = this.titleRes;
            if (i != 0) {
                return res.getText(i);
            }
            return this.title;
        }

        public CharSequence getSummary(Resources res) {
            int i = this.summaryRes;
            if (i != 0) {
                return res.getText(i);
            }
            return this.summary;
        }

        public CharSequence getBreadCrumbTitle(Resources res) {
            int i = this.breadCrumbTitleRes;
            if (i != 0) {
                return res.getText(i);
            }
            return this.breadCrumbTitle;
        }

        public CharSequence getBreadCrumbShortTitle(Resources res) {
            int i = this.breadCrumbShortTitleRes;
            if (i != 0) {
                return res.getText(i);
            }
            return this.breadCrumbShortTitle;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.f336id);
            dest.writeInt(this.titleRes);
            TextUtils.writeToParcel(this.title, dest, flags);
            dest.writeInt(this.summaryRes);
            TextUtils.writeToParcel(this.summary, dest, flags);
            dest.writeInt(this.breadCrumbTitleRes);
            TextUtils.writeToParcel(this.breadCrumbTitle, dest, flags);
            dest.writeInt(this.breadCrumbShortTitleRes);
            TextUtils.writeToParcel(this.breadCrumbShortTitle, dest, flags);
            dest.writeInt(this.iconRes);
            dest.writeString(this.fragment);
            dest.writeBundle(this.fragmentArguments);
            if (this.intent != null) {
                dest.writeInt(1);
                this.intent.writeToParcel(dest, flags);
            } else {
                dest.writeInt(0);
            }
            dest.writeBundle(this.extras);
        }

        public void readFromParcel(Parcel in) {
            this.f336id = in.readLong();
            this.titleRes = in.readInt();
            this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.summaryRes = in.readInt();
            this.summary = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.breadCrumbTitleRes = in.readInt();
            this.breadCrumbTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.breadCrumbShortTitleRes = in.readInt();
            this.breadCrumbShortTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.iconRes = in.readInt();
            this.fragment = in.readString();
            this.fragmentArguments = in.readBundle();
            if (in.readInt() != 0) {
                this.intent = Intent.CREATOR.createFromParcel(in);
            }
            this.extras = in.readBundle();
        }

        Header(Parcel in) {
            readFromParcel(in);
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Header header;
        super.onCreate(savedInstanceState);
        TypedArray sa = obtainStyledAttributes(null, C4057R.styleable.PreferenceActivity, C4057R.attr.preferenceActivityStyle, 0);
        int layoutResId = sa.getResourceId(0, C4057R.layout.preference_list_content);
        this.mPreferenceHeaderItemResId = sa.getResourceId(1, C4057R.layout.preference_header_item);
        this.mPreferenceHeaderRemoveEmptyIcon = sa.getBoolean(2, false);
        sa.recycle();
        setContentView(layoutResId);
        this.mListFooter = (FrameLayout) findViewById(C4057R.C4059id.list_footer);
        this.mPrefsContainer = (ViewGroup) findViewById(C4057R.C4059id.prefs_frame);
        this.mHeadersContainer = (ViewGroup) findViewById(C4057R.C4059id.headers);
        boolean hidingHeaders = onIsHidingHeaders();
        this.mSinglePane = hidingHeaders || !onIsMultiPane();
        String initialFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
        Bundle initialArguments = getIntent().getBundleExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS);
        int initialTitle = getIntent().getIntExtra(EXTRA_SHOW_FRAGMENT_TITLE, 0);
        int initialShortTitle = getIntent().getIntExtra(EXTRA_SHOW_FRAGMENT_SHORT_TITLE, 0);
        this.mActivityTitle = getTitle();
        if (savedInstanceState != null) {
            ArrayList<Header> headers = savedInstanceState.getParcelableArrayList(HEADERS_TAG, Header.class);
            if (headers == null) {
                showBreadCrumbs(getTitle(), null);
            } else {
                this.mHeaders.addAll(headers);
                int curHeader = savedInstanceState.getInt(CUR_HEADER_TAG, -1);
                if (curHeader >= 0 && curHeader < this.mHeaders.size()) {
                    setSelectedHeader(this.mHeaders.get(curHeader));
                } else if (!this.mSinglePane && initialFragment == null) {
                    switchToHeader(onGetInitialHeader());
                }
            }
        } else {
            if (!onIsHidingHeaders()) {
                onBuildHeaders(this.mHeaders);
            }
            if (initialFragment != null) {
                switchToHeader(initialFragment, initialArguments);
            } else if (!this.mSinglePane && this.mHeaders.size() > 0) {
                switchToHeader(onGetInitialHeader());
            }
        }
        if (this.mHeaders.size() > 0) {
            setListAdapter(new HeaderAdapter(this, this.mHeaders, this.mPreferenceHeaderItemResId, this.mPreferenceHeaderRemoveEmptyIcon));
            if (!this.mSinglePane) {
                getListView().setChoiceMode(1);
            }
        }
        if (this.mSinglePane && initialFragment != null && initialTitle != 0) {
            CharSequence initialTitleStr = getText(initialTitle);
            CharSequence initialShortTitleStr = initialShortTitle != 0 ? getText(initialShortTitle) : null;
            showBreadCrumbs(initialTitleStr, initialShortTitleStr);
        }
        if (this.mHeaders.size() == 0 && initialFragment == null) {
            setContentView(C4057R.layout.preference_list_content_single);
            this.mListFooter = (FrameLayout) findViewById(C4057R.C4059id.list_footer);
            this.mPrefsContainer = (ViewGroup) findViewById(C4057R.C4059id.prefs);
            PreferenceManager preferenceManager = new PreferenceManager(this, 100);
            this.mPreferenceManager = preferenceManager;
            preferenceManager.setOnPreferenceTreeClickListener(this);
            this.mHeadersContainer = null;
        } else if (this.mSinglePane) {
            if (initialFragment != null || this.mCurHeader != null) {
                this.mHeadersContainer.setVisibility(8);
            } else {
                this.mPrefsContainer.setVisibility(8);
            }
            ViewGroup container = (ViewGroup) findViewById(C4057R.C4059id.prefs_container);
            container.setLayoutTransition(new LayoutTransition());
        } else if (this.mHeaders.size() > 0 && (header = this.mCurHeader) != null) {
            setSelectedHeader(header);
        }
        Intent intent = getIntent();
        if (intent.getBooleanExtra(EXTRA_PREFS_SHOW_BUTTON_BAR, false)) {
            findViewById(C4057R.C4059id.button_bar).setVisibility(0);
            Button backButton = (Button) findViewById(C4057R.C4059id.back_button);
            backButton.setOnClickListener(new View.OnClickListener() { // from class: android.preference.PreferenceActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PreferenceActivity.this.setResult(0);
                    PreferenceActivity.this.finish();
                }
            });
            Button skipButton = (Button) findViewById(C4057R.C4059id.skip_button);
            skipButton.setOnClickListener(new View.OnClickListener() { // from class: android.preference.PreferenceActivity.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PreferenceActivity.this.setResult(-1);
                    PreferenceActivity.this.finish();
                }
            });
            Button button = (Button) findViewById(C4057R.C4059id.next_button);
            this.mNextButton = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: android.preference.PreferenceActivity.4
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    PreferenceActivity.this.setResult(-1);
                    PreferenceActivity.this.finish();
                }
            });
            if (intent.hasExtra(EXTRA_PREFS_SET_NEXT_TEXT)) {
                String buttonText = intent.getStringExtra(EXTRA_PREFS_SET_NEXT_TEXT);
                if (TextUtils.isEmpty(buttonText)) {
                    this.mNextButton.setVisibility(8);
                } else {
                    this.mNextButton.setText(buttonText);
                }
            }
            if (intent.hasExtra(EXTRA_PREFS_SET_BACK_TEXT)) {
                String buttonText2 = intent.getStringExtra(EXTRA_PREFS_SET_BACK_TEXT);
                if (TextUtils.isEmpty(buttonText2)) {
                    backButton.setVisibility(8);
                } else {
                    backButton.setText(buttonText2);
                }
            }
            if (intent.getBooleanExtra(EXTRA_PREFS_SHOW_SKIP, false)) {
                skipButton.setVisibility(0);
            }
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.mCurHeader != null && this.mSinglePane && getFragmentManager().getBackStackEntryCount() == 0 && getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            this.mCurHeader = null;
            this.mPrefsContainer.setVisibility(8);
            this.mHeadersContainer.setVisibility(0);
            CharSequence charSequence = this.mActivityTitle;
            if (charSequence != null) {
                showBreadCrumbs(charSequence, null);
            }
            getListView().clearChoices();
            return;
        }
        super.onBackPressed();
    }

    public boolean hasHeaders() {
        ViewGroup viewGroup = this.mHeadersContainer;
        return viewGroup != null && viewGroup.getVisibility() == 0;
    }

    public List<Header> getHeaders() {
        return this.mHeaders;
    }

    public boolean isMultiPane() {
        return !this.mSinglePane;
    }

    public boolean onIsMultiPane() {
        boolean preferMultiPane = getResources().getBoolean(C4057R.bool.preferences_prefer_dual_pane);
        return preferMultiPane;
    }

    public boolean onIsHidingHeaders() {
        return getIntent().getBooleanExtra(EXTRA_NO_HEADERS, false);
    }

    public Header onGetInitialHeader() {
        for (int i = 0; i < this.mHeaders.size(); i++) {
            Header h = this.mHeaders.get(i);
            if (h.fragment != null) {
                return h;
            }
        }
        throw new IllegalStateException("Must have at least one header with a fragment");
    }

    public Header onGetNewHeader() {
        return null;
    }

    public void onBuildHeaders(List<Header> target) {
    }

    public void invalidateHeaders() {
        if (!this.mHandler.hasMessages(2)) {
            this.mHandler.sendEmptyMessage(2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x004c, code lost:
        if (r11 != 4) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x004e, code lost:
        r18 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0062, code lost:
        if (android.provider.Downloads.Impl.RequestHeaders.COLUMN_HEADER.equals(r2.getName()) == false) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0064, code lost:
        r13 = new android.preference.PreferenceActivity.Header();
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x006d, code lost:
        r14 = obtainStyledAttributes(r3, com.android.internal.C4057R.styleable.PreferenceHeader);
        r18 = r9;
        r13.f336id = r14.getResourceId(r8, -1);
        r8 = r14.peekValue(r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x007f, code lost:
        if (r8 == null) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0083, code lost:
        if (r8.type != 3) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0087, code lost:
        if (r8.resourceId == 0) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0089, code lost:
        r13.titleRes = r8.resourceId;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x008e, code lost:
        r13.title = r8.string;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0092, code lost:
        r9 = r14.peekValue(3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0097, code lost:
        if (r9 == null) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x009b, code lost:
        if (r9.type != 3) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x009f, code lost:
        if (r9.resourceId == 0) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00a1, code lost:
        r13.summaryRes = r9.resourceId;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00a6, code lost:
        r13.summary = r9.string;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00aa, code lost:
        r9 = r14.peekValue(5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00b0, code lost:
        if (r9 == null) goto L69;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00b4, code lost:
        if (r9.type != 3) goto L69;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00b8, code lost:
        if (r9.resourceId == 0) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00ba, code lost:
        r13.breadCrumbTitleRes = r9.resourceId;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00bf, code lost:
        r13.breadCrumbTitle = r9.string;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00c3, code lost:
        r9 = r14.peekValue(6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00c9, code lost:
        if (r9 == null) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00cd, code lost:
        if (r9.type != 3) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00d1, code lost:
        if (r9.resourceId == 0) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00d3, code lost:
        r13.breadCrumbShortTitleRes = r9.resourceId;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00d8, code lost:
        r13.breadCrumbShortTitle = r9.string;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00dc, code lost:
        r13.iconRes = r14.getResourceId(0, 0);
        r13.fragment = r14.getString(4);
        r14.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00ed, code lost:
        if (r18 != null) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00ef, code lost:
        r9 = new android.p008os.Bundle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00f5, code lost:
        r9 = r18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00f7, code lost:
        r12 = r2.getDepth();
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00fb, code lost:
        r7 = r2.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0101, code lost:
        if (r7 == 1) goto L113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0104, code lost:
        if (r7 != 3) goto L95;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x010a, code lost:
        if (r2.getDepth() <= r12) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x010d, code lost:
        if (r7 == 3) goto L112;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0110, code lost:
        if (r7 != 4) goto L102;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0113, code lost:
        r16 = r2.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x011f, code lost:
        if (r16.equals("extra") == false) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0121, code lost:
        getResources().parseBundleExtra("extra", r3, r9);
        com.android.internal.util.XmlUtils.skipCurrentTag(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0133, code lost:
        if (r16.equals("intent") == false) goto L110;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0135, code lost:
        r13.intent = android.content.Intent.parseIntent(getResources(), r2, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x0140, code lost:
        com.android.internal.util.XmlUtils.skipCurrentTag(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x014b, code lost:
        if (r9.size() <= 0) goto L92;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x014d, code lost:
        r13.fragmentArguments = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x014f, code lost:
        r9 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x0153, code lost:
        r21.add(r13);
        r7 = 2;
        r8 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x015a, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x015d, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0160, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x0163, code lost:
        r18 = r9;
        com.android.internal.util.XmlUtils.skipCurrentTag(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x016c, code lost:
        r9 = r18;
        r7 = 2;
        r8 = 1;
     */
    /* JADX WARN: Removed duplicated region for block: B:134:0x01ea  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void loadHeadersFromResource(int resid, List<Header> target) {
        int i;
        int i2;
        XmlResourceParser parser = null;
        try {
            try {
                try {
                    parser = getResources().getXml(resid);
                    AttributeSet attrs = Xml.asAttributeSet(parser);
                    while (true) {
                        int type = parser.next();
                        i = 2;
                        i2 = 1;
                        if (type == 1 || type == 2) {
                            break;
                        }
                    }
                    String nodeName = parser.getName();
                    try {
                        if (!"preference-headers".equals(nodeName)) {
                            throw new RuntimeException("XML document must start with <preference-headers> tag; found" + nodeName + " at " + parser.getPositionDescription());
                        }
                        Bundle curBundle = null;
                        int outerDepth = parser.getDepth();
                        while (true) {
                            int type2 = parser.next();
                            if (type2 == i2) {
                                break;
                            }
                            if (type2 == 3 && parser.getDepth() <= outerDepth) {
                                break;
                            }
                            Bundle curBundle2 = curBundle;
                            curBundle = curBundle2;
                            i = 2;
                            i2 = 1;
                        }
                        if (parser != null) {
                            parser.close();
                        }
                    } catch (IOException e) {
                        e = e;
                        throw new RuntimeException("Error parsing headers", e);
                    } catch (XmlPullParserException e2) {
                        e = e2;
                        throw new RuntimeException("Error parsing headers", e);
                    }
                } catch (IOException e3) {
                    e = e3;
                } catch (XmlPullParserException e4) {
                    e = e4;
                } catch (Throwable th) {
                    e = th;
                }
            } catch (Throwable th2) {
                e = th2;
                if (parser != null) {
                    parser.close();
                }
                throw e;
            }
        } catch (IOException e5) {
            e = e5;
        } catch (XmlPullParserException e6) {
            e = e6;
        } catch (Throwable th3) {
            e = th3;
        }
    }

    protected boolean isValidFragment(String fragmentName) {
        if (getApplicationInfo().targetSdkVersion >= 19) {
            throw new RuntimeException("Subclasses of PreferenceActivity must override isValidFragment(String) to verify that the Fragment class is valid! " + getClass().getName() + " has not checked if fragment " + fragmentName + " is valid.");
        }
        return true;
    }

    public void setListFooter(View view) {
        this.mListFooter.removeAllViews();
        this.mListFooter.addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager != null) {
            preferenceManager.dispatchActivityStop();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ListActivity, android.app.Activity
    public void onDestroy() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        super.onDestroy();
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager != null) {
            preferenceManager.dispatchActivityDestroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        PreferenceScreen preferenceScreen;
        int index;
        super.onSaveInstanceState(outState);
        if (this.mHeaders.size() > 0) {
            outState.putParcelableArrayList(HEADERS_TAG, this.mHeaders);
            Header header = this.mCurHeader;
            if (header != null && (index = this.mHeaders.indexOf(header)) >= 0) {
                outState.putInt(CUR_HEADER_TAG, index);
            }
        }
        if (this.mPreferenceManager != null && (preferenceScreen = getPreferenceScreen()) != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ListActivity, android.app.Activity
    public void onRestoreInstanceState(Bundle state) {
        Header header;
        Bundle container;
        PreferenceScreen preferenceScreen;
        if (this.mPreferenceManager != null && (container = state.getBundle(PREFERENCES_TAG)) != null && (preferenceScreen = getPreferenceScreen()) != null) {
            preferenceScreen.restoreHierarchyState(container);
            this.mSavedInstanceState = state;
            return;
        }
        super.onRestoreInstanceState(state);
        if (!this.mSinglePane && (header = this.mCurHeader) != null) {
            setSelectedHeader(header);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager != null) {
            preferenceManager.dispatchActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // android.app.ListActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        if (this.mPreferenceManager != null) {
            postBindPreferences();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ListActivity
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!isResumed()) {
            return;
        }
        super.onListItemClick(l, v, position, id);
        if (this.mAdapter != null) {
            Object item = this.mAdapter.getItem(position);
            if (item instanceof Header) {
                onHeaderClick((Header) item, position);
            }
        }
    }

    public void onHeaderClick(Header header, int position) {
        if (header.fragment != null) {
            switchToHeader(header);
        } else if (header.intent != null) {
            startActivity(header.intent);
        }
    }

    public Intent onBuildStartFragmentIntent(String fragmentName, Bundle args, int titleRes, int shortTitleRes) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(this, getClass());
        intent.putExtra(EXTRA_SHOW_FRAGMENT, fragmentName);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_TITLE, titleRes);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_SHORT_TITLE, shortTitleRes);
        intent.putExtra(EXTRA_NO_HEADERS, true);
        return intent;
    }

    public void startWithFragment(String fragmentName, Bundle args, Fragment resultTo, int resultRequestCode) {
        startWithFragment(fragmentName, args, resultTo, resultRequestCode, 0, 0);
    }

    public void startWithFragment(String fragmentName, Bundle args, Fragment resultTo, int resultRequestCode, int titleRes, int shortTitleRes) {
        Intent intent = onBuildStartFragmentIntent(fragmentName, args, titleRes, shortTitleRes);
        if (resultTo == null) {
            startActivity(intent);
        } else {
            resultTo.startActivityForResult(intent, resultRequestCode);
        }
    }

    public void showBreadCrumbs(CharSequence title, CharSequence shortTitle) {
        if (this.mFragmentBreadCrumbs == null) {
            View crumbs = findViewById(16908310);
            try {
                FragmentBreadCrumbs fragmentBreadCrumbs = (FragmentBreadCrumbs) crumbs;
                this.mFragmentBreadCrumbs = fragmentBreadCrumbs;
                if (fragmentBreadCrumbs == null) {
                    if (title != null) {
                        setTitle(title);
                        return;
                    }
                    return;
                }
                if (this.mSinglePane) {
                    fragmentBreadCrumbs.setVisibility(8);
                    View bcSection = findViewById(C4057R.C4059id.breadcrumb_section);
                    if (bcSection != null) {
                        bcSection.setVisibility(8);
                    }
                    setTitle(title);
                }
                this.mFragmentBreadCrumbs.setMaxVisible(2);
                this.mFragmentBreadCrumbs.setActivity(this);
            } catch (ClassCastException e) {
                setTitle(title);
                return;
            }
        }
        View crumbs2 = this.mFragmentBreadCrumbs;
        if (crumbs2.getVisibility() != 0) {
            setTitle(title);
            return;
        }
        this.mFragmentBreadCrumbs.setTitle(title, shortTitle);
        this.mFragmentBreadCrumbs.setParentTitle(null, null, null);
    }

    public void setParentTitle(CharSequence title, CharSequence shortTitle, View.OnClickListener listener) {
        FragmentBreadCrumbs fragmentBreadCrumbs = this.mFragmentBreadCrumbs;
        if (fragmentBreadCrumbs != null) {
            fragmentBreadCrumbs.setParentTitle(title, shortTitle, listener);
        }
    }

    void setSelectedHeader(Header header) {
        this.mCurHeader = header;
        int index = this.mHeaders.indexOf(header);
        if (index >= 0) {
            getListView().setItemChecked(index, true);
        } else {
            getListView().clearChoices();
        }
        showBreadCrumbs(header);
    }

    void showBreadCrumbs(Header header) {
        if (header != null) {
            CharSequence title = header.getBreadCrumbTitle(getResources());
            if (title == null) {
                title = header.getTitle(getResources());
            }
            if (title == null) {
                title = getTitle();
            }
            showBreadCrumbs(title, header.getBreadCrumbShortTitle(getResources()));
            return;
        }
        showBreadCrumbs(getTitle(), null);
    }

    private void switchToHeaderInner(String fragmentName, Bundle args) {
        int i;
        getFragmentManager().popBackStack(BACK_STACK_PREFS, 1);
        if (!isValidFragment(fragmentName)) {
            throw new IllegalArgumentException("Invalid fragment for this activity: " + fragmentName);
        }
        Fragment f = Fragment.instantiate(this, fragmentName, args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (this.mSinglePane) {
            i = 0;
        } else {
            i = 4099;
        }
        transaction.setTransition(i);
        transaction.replace(C4057R.C4059id.prefs, f);
        transaction.commitAllowingStateLoss();
        if (this.mSinglePane && this.mPrefsContainer.getVisibility() == 8) {
            this.mPrefsContainer.setVisibility(0);
            this.mHeadersContainer.setVisibility(8);
        }
    }

    public void switchToHeader(String fragmentName, Bundle args) {
        Header selectedHeader = null;
        int i = 0;
        while (true) {
            if (i >= this.mHeaders.size()) {
                break;
            } else if (!fragmentName.equals(this.mHeaders.get(i).fragment)) {
                i++;
            } else {
                Header selectedHeader2 = this.mHeaders.get(i);
                selectedHeader = selectedHeader2;
                break;
            }
        }
        setSelectedHeader(selectedHeader);
        switchToHeaderInner(fragmentName, args);
    }

    public void switchToHeader(Header header) {
        if (this.mCurHeader == header) {
            getFragmentManager().popBackStack(BACK_STACK_PREFS, 1);
        } else if (header.fragment == null) {
            throw new IllegalStateException("can't switch to header that has no fragment");
        } else {
            switchToHeaderInner(header.fragment, header.fragmentArguments);
            setSelectedHeader(header);
        }
    }

    Header findBestMatchingHeader(Header cur, ArrayList<Header> from) {
        ArrayList<Header> matches = new ArrayList<>();
        for (int j = 0; j < from.size(); j++) {
            Header oh = from.get(j);
            if (cur == oh || (cur.f336id != -1 && cur.f336id == oh.f336id)) {
                matches.clear();
                matches.add(oh);
                break;
            }
            if (cur.fragment != null) {
                if (cur.fragment.equals(oh.fragment)) {
                    matches.add(oh);
                }
            } else if (cur.intent != null) {
                if (cur.intent.equals(oh.intent)) {
                    matches.add(oh);
                }
            } else if (cur.title != null && cur.title.equals(oh.title)) {
                matches.add(oh);
            }
        }
        int NM = matches.size();
        if (NM == 1) {
            return matches.get(0);
        }
        if (NM > 1) {
            for (int j2 = 0; j2 < NM; j2++) {
                Header oh2 = matches.get(j2);
                if (cur.fragmentArguments != null && cur.fragmentArguments.equals(oh2.fragmentArguments)) {
                    return oh2;
                }
                if (cur.extras != null && cur.extras.equals(oh2.extras)) {
                    return oh2;
                }
                if (cur.title != null && cur.title.equals(oh2.title)) {
                    return oh2;
                }
            }
            return null;
        }
        return null;
    }

    public void startPreferenceFragment(Fragment fragment, boolean push) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(C4057R.C4059id.prefs, fragment);
        if (push) {
            transaction.setTransition(4097);
            transaction.addToBackStack(BACK_STACK_PREFS);
        } else {
            transaction.setTransition(4099);
        }
        transaction.commitAllowingStateLoss();
    }

    public void startPreferencePanel(String fragmentClass, Bundle args, int titleRes, CharSequence titleText, Fragment resultTo, int resultRequestCode) {
        Fragment f = Fragment.instantiate(this, fragmentClass, args);
        if (resultTo != null) {
            f.setTargetFragment(resultTo, resultRequestCode);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(C4057R.C4059id.prefs, f);
        if (titleRes != 0) {
            transaction.setBreadCrumbTitle(titleRes);
        } else if (titleText != null) {
            transaction.setBreadCrumbTitle(titleText);
        }
        transaction.setTransition(4097);
        transaction.addToBackStack(BACK_STACK_PREFS);
        transaction.commitAllowingStateLoss();
    }

    public void finishPreferencePanel(Fragment caller, int resultCode, Intent resultData) {
        onBackPressed();
        if (caller != null && caller.getTargetFragment() != null) {
            caller.getTargetFragment().onActivityResult(caller.getTargetRequestCode(), resultCode, resultData);
        }
    }

    @Override // android.preference.PreferenceFragment.OnPreferenceStartFragmentCallback
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(), pref.getTitle(), null, 0);
        return true;
    }

    private void postBindPreferences() {
        if (this.mHandler.hasMessages(1)) {
            return;
        }
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(getListView());
            Bundle bundle = this.mSavedInstanceState;
            if (bundle != null) {
                super.onRestoreInstanceState(bundle);
                this.mSavedInstanceState = null;
            }
        }
    }

    @Deprecated
    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }

    private void requirePreferenceManager() {
        if (this.mPreferenceManager == null) {
            if (this.mAdapter == null) {
                throw new RuntimeException("This should be called after super.onCreate.");
            }
            throw new RuntimeException("Modern two-pane PreferenceActivity requires use of a PreferenceFragment");
        }
    }

    @Deprecated
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        requirePreferenceManager();
        if (this.mPreferenceManager.setPreferences(preferenceScreen) && preferenceScreen != null) {
            postBindPreferences();
            CharSequence title = getPreferenceScreen().getTitle();
            if (title != null) {
                setTitle(title);
            }
        }
    }

    @Deprecated
    public PreferenceScreen getPreferenceScreen() {
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager != null) {
            return preferenceManager.getPreferenceScreen();
        }
        return null;
    }

    @Deprecated
    public void addPreferencesFromIntent(Intent intent) {
        requirePreferenceManager();
        setPreferenceScreen(this.mPreferenceManager.inflateFromIntent(intent, getPreferenceScreen()));
    }

    @Deprecated
    public void addPreferencesFromResource(int preferencesResId) {
        requirePreferenceManager();
        setPreferenceScreen(this.mPreferenceManager.inflateFromResource(this, preferencesResId, getPreferenceScreen()));
    }

    @Override // android.preference.PreferenceManager.OnPreferenceTreeClickListener
    @Deprecated
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return false;
    }

    @Deprecated
    public Preference findPreference(CharSequence key) {
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager == null) {
            return null;
        }
        return preferenceManager.findPreference(key);
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager != null) {
            preferenceManager.dispatchNewIntent(intent);
        }
    }

    protected boolean hasNextButton() {
        return this.mNextButton != null;
    }

    protected Button getNextButton() {
        return this.mNextButton;
    }
}
