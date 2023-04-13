package com.android.internal.widget;

import android.app.Notification;
import android.app.Person;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Icon;
import android.p008os.Bundle;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.util.function.Consumer;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class CallLayout extends FrameLayout {
    private CachingIconView mConversationIconBadgeBg;
    private CachingIconView mConversationIconView;
    private TextView mConversationText;
    private CachingIconView mIcon;
    private Icon mLargeIcon;
    private int mLayoutColor;
    private final PeopleHelper mPeopleHelper;
    private Person mUser;

    public CallLayout(Context context) {
        super(context);
        this.mPeopleHelper = new PeopleHelper();
    }

    public CallLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPeopleHelper = new PeopleHelper();
    }

    public CallLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPeopleHelper = new PeopleHelper();
    }

    public CallLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPeopleHelper = new PeopleHelper();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPeopleHelper.init(getContext());
        this.mConversationText = (TextView) findViewById(C4057R.C4059id.conversation_text);
        this.mConversationIconView = (CachingIconView) findViewById(C4057R.C4059id.conversation_icon);
        this.mIcon = (CachingIconView) findViewById(16908294);
        this.mConversationIconBadgeBg = (CachingIconView) findViewById(C4057R.C4059id.conversation_icon_badge_bg);
        this.mIcon.setOnForceHiddenChangedListener(new Consumer() { // from class: com.android.internal.widget.CallLayout$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CallLayout.this.lambda$onFinishInflate$0((Boolean) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$0(Boolean forceHidden) {
        this.mPeopleHelper.animateViewForceHidden(this.mConversationIconBadgeBg, forceHidden.booleanValue());
    }

    private void updateCallLayout() {
        CharSequence callerName = "";
        String symbol = "";
        Icon icon = null;
        Person person = this.mUser;
        if (person != null) {
            icon = person.getIcon();
            callerName = this.mUser.getName();
            symbol = this.mPeopleHelper.findNamePrefix(callerName, "");
        }
        if (icon == null) {
            icon = this.mLargeIcon;
        }
        if (icon == null) {
            icon = this.mPeopleHelper.createAvatarSymbol(callerName, symbol, this.mLayoutColor);
        }
        this.mConversationIconView.setImageIcon(icon);
    }

    @RemotableViewMethod
    public void setLayoutColor(int color) {
        this.mLayoutColor = color;
    }

    @RemotableViewMethod
    public void setNotificationBackgroundColor(int color) {
        this.mConversationIconBadgeBg.setImageTintList(ColorStateList.valueOf(color));
    }

    @RemotableViewMethod
    public void setLargeIcon(Icon largeIcon) {
        this.mLargeIcon = largeIcon;
    }

    @RemotableViewMethod
    public void setData(Bundle extras) {
        setUser((Person) extras.getParcelable(Notification.EXTRA_CALL_PERSON, Person.class));
        updateCallLayout();
    }

    private void setUser(Person user) {
        this.mUser = user;
    }
}
