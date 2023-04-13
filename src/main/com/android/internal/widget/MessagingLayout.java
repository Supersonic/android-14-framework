package com.android.internal.widget;

import android.app.Notification;
import android.app.Person;
import android.app.RemoteInputHistoryItem;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.p008os.Bundle;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import com.android.internal.util.ContrastColorUtil;
import com.android.internal.widget.MessagingLinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class MessagingLayout extends FrameLayout implements ImageMessageConsumer, IMessagingLayout {
    private static final float COLOR_SHIFT_AMOUNT = 60.0f;
    private ArrayList<MessagingGroup> mAddedGroups;
    private Icon mAvatarReplacement;
    private CharSequence mConversationTitle;
    private ArrayList<MessagingGroup> mGroups;
    private List<MessagingMessage> mHistoricMessages;
    private MessagingLinearLayout mImageMessageContainer;
    private ImageResolver mImageResolver;
    private boolean mIsCollapsed;
    private boolean mIsOneToOne;
    private int mLayoutColor;
    private int mMessageTextColor;
    private List<MessagingMessage> mMessages;
    private Rect mMessagingClipRect;
    private MessagingLinearLayout mMessagingLinearLayout;
    private CharSequence mNameReplacement;
    private final PeopleHelper mPeopleHelper;
    private ImageView mRightIconView;
    private int mSenderTextColor;
    private boolean mShowHistoricMessages;
    private ArrayList<MessagingLinearLayout.MessagingChild> mToRecycle;
    private Person mUser;
    public static final Interpolator LINEAR_OUT_SLOW_IN = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator FAST_OUT_LINEAR_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final View.OnLayoutChangeListener MESSAGING_PROPERTY_ANIMATOR = new MessagingPropertyAnimator();

    public MessagingLayout(Context context) {
        super(context);
        this.mPeopleHelper = new PeopleHelper();
        this.mMessages = new ArrayList();
        this.mHistoricMessages = new ArrayList();
        this.mGroups = new ArrayList<>();
        this.mAddedGroups = new ArrayList<>();
        this.mToRecycle = new ArrayList<>();
    }

    public MessagingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPeopleHelper = new PeopleHelper();
        this.mMessages = new ArrayList();
        this.mHistoricMessages = new ArrayList();
        this.mGroups = new ArrayList<>();
        this.mAddedGroups = new ArrayList<>();
        this.mToRecycle = new ArrayList<>();
    }

    public MessagingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPeopleHelper = new PeopleHelper();
        this.mMessages = new ArrayList();
        this.mHistoricMessages = new ArrayList();
        this.mGroups = new ArrayList<>();
        this.mAddedGroups = new ArrayList<>();
        this.mToRecycle = new ArrayList<>();
    }

    public MessagingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPeopleHelper = new PeopleHelper();
        this.mMessages = new ArrayList();
        this.mHistoricMessages = new ArrayList();
        this.mGroups = new ArrayList<>();
        this.mAddedGroups = new ArrayList<>();
        this.mToRecycle = new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPeopleHelper.init(getContext());
        this.mMessagingLinearLayout = (MessagingLinearLayout) findViewById(C4057R.C4059id.notification_messaging);
        this.mImageMessageContainer = (MessagingLinearLayout) findViewById(C4057R.C4059id.conversation_image_message_container);
        this.mRightIconView = (ImageView) findViewById(C4057R.C4059id.right_icon);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int size = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.mMessagingClipRect = new Rect(0, 0, size, size);
        setMessagingClippingDisabled(false);
    }

    @RemotableViewMethod
    public void setAvatarReplacement(Icon icon) {
        this.mAvatarReplacement = icon;
    }

    @RemotableViewMethod
    public void setNameReplacement(CharSequence nameReplacement) {
        this.mNameReplacement = nameReplacement;
    }

    @RemotableViewMethod
    public void setIsCollapsed(boolean isCollapsed) {
        this.mIsCollapsed = isCollapsed;
    }

    @RemotableViewMethod
    public void setLargeIcon(Icon largeIcon) {
    }

    @RemotableViewMethod
    public void setConversationTitle(CharSequence conversationTitle) {
        this.mConversationTitle = conversationTitle;
    }

    @RemotableViewMethod
    public void setData(Bundle extras) {
        Parcelable[] messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES);
        List<Notification.MessagingStyle.Message> newMessages = Notification.MessagingStyle.Message.getMessagesFromBundleArray(messages);
        Parcelable[] histMessages = extras.getParcelableArray(Notification.EXTRA_HISTORIC_MESSAGES);
        List<Notification.MessagingStyle.Message> newHistoricMessages = Notification.MessagingStyle.Message.getMessagesFromBundleArray(histMessages);
        setUser((Person) extras.getParcelable(Notification.EXTRA_MESSAGING_PERSON, Person.class));
        RemoteInputHistoryItem[] history = (RemoteInputHistoryItem[]) extras.getParcelableArray(Notification.EXTRA_REMOTE_INPUT_HISTORY_ITEMS, RemoteInputHistoryItem.class);
        addRemoteInputHistoryToMessages(newMessages, history);
        boolean showSpinner = extras.getBoolean(Notification.EXTRA_SHOW_REMOTE_INPUT_SPINNER, false);
        bind(newMessages, newHistoricMessages, showSpinner);
    }

    @Override // com.android.internal.widget.ImageMessageConsumer
    public void setImageResolver(ImageResolver resolver) {
        this.mImageResolver = resolver;
    }

    private void addRemoteInputHistoryToMessages(List<Notification.MessagingStyle.Message> newMessages, RemoteInputHistoryItem[] remoteInputHistory) {
        if (remoteInputHistory == null || remoteInputHistory.length == 0) {
            return;
        }
        for (int i = remoteInputHistory.length - 1; i >= 0; i--) {
            RemoteInputHistoryItem historyMessage = remoteInputHistory[i];
            Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(historyMessage.getText(), 0L, null, true);
            if (historyMessage.getUri() != null) {
                message.setData(historyMessage.getMimeType(), historyMessage.getUri());
            }
            newMessages.add(message);
        }
    }

    private void bind(List<Notification.MessagingStyle.Message> newMessages, List<Notification.MessagingStyle.Message> newHistoricMessages, boolean showSpinner) {
        List<MessagingMessage> historicMessages = createMessages(newHistoricMessages, true);
        List<MessagingMessage> messages = createMessages(newMessages, false);
        ArrayList<MessagingGroup> oldGroups = new ArrayList<>(this.mGroups);
        addMessagesToGroups(historicMessages, messages, showSpinner);
        removeGroups(oldGroups);
        for (MessagingMessage message : this.mMessages) {
            message.removeMessage(this.mToRecycle);
        }
        for (MessagingMessage historicMessage : this.mHistoricMessages) {
            historicMessage.removeMessage(this.mToRecycle);
        }
        this.mMessages = messages;
        this.mHistoricMessages = historicMessages;
        updateHistoricMessageVisibility();
        updateTitleAndNamesDisplay();
        this.mPeopleHelper.maybeHideFirstSenderName(this.mGroups, this.mIsOneToOne, this.mConversationTitle);
        updateImageMessages();
        Iterator<MessagingLinearLayout.MessagingChild> it = this.mToRecycle.iterator();
        while (it.hasNext()) {
            MessagingLinearLayout.MessagingChild child = it.next();
            child.recycle();
        }
        this.mToRecycle.clear();
    }

    private void updateImageMessages() {
        ImageView imageView;
        View newMessage = null;
        if (this.mImageMessageContainer == null) {
            return;
        }
        if (this.mIsCollapsed && !this.mGroups.isEmpty()) {
            ArrayList<MessagingGroup> arrayList = this.mGroups;
            MessagingGroup messagingGroup = arrayList.get(arrayList.size() - 1);
            MessagingImageMessage isolatedMessage = messagingGroup.getIsolatedMessage();
            if (isolatedMessage != null) {
                newMessage = isolatedMessage.getView();
            }
        }
        View previousMessage = this.mImageMessageContainer.getChildAt(0);
        if (previousMessage != newMessage) {
            this.mImageMessageContainer.removeView(previousMessage);
            if (newMessage != null) {
                this.mImageMessageContainer.addView(newMessage);
            }
        }
        this.mImageMessageContainer.setVisibility(newMessage == null ? 8 : 0);
        if (newMessage != null && (imageView = this.mRightIconView) != null && imageView.getDrawable() != null) {
            this.mRightIconView.setImageDrawable(null);
            this.mRightIconView.setVisibility(8);
        }
    }

    private void removeGroups(ArrayList<MessagingGroup> oldGroups) {
        int size = oldGroups.size();
        for (int i = 0; i < size; i++) {
            final MessagingGroup group = oldGroups.get(i);
            if (!this.mGroups.contains(group)) {
                List<MessagingMessage> messages = group.getMessages();
                boolean wasShown = group.isShown();
                this.mMessagingLinearLayout.removeView(group);
                if (wasShown && !MessagingLinearLayout.isGone(group)) {
                    this.mMessagingLinearLayout.addTransientView(group, 0);
                    group.removeGroupAnimated(new Runnable() { // from class: com.android.internal.widget.MessagingLayout$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagingLayout.this.lambda$removeGroups$0(group);
                        }
                    });
                } else {
                    this.mToRecycle.add(group);
                }
                this.mMessages.removeAll(messages);
                this.mHistoricMessages.removeAll(messages);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeGroups$0(MessagingGroup group) {
        this.mMessagingLinearLayout.removeTransientView(group);
        group.recycle();
    }

    private void updateTitleAndNamesDisplay() {
        Map<CharSequence, String> uniqueNames = this.mPeopleHelper.mapUniqueNamesToPrefix(this.mGroups);
        ArrayMap<CharSequence, Icon> cachedAvatars = new ArrayMap<>();
        for (int i = 0; i < this.mGroups.size(); i++) {
            MessagingGroup group = this.mGroups.get(i);
            boolean isOwnMessage = group.getSender() == this.mUser;
            CharSequence senderName = group.getSenderName();
            if (group.needsGeneratedAvatar() && !TextUtils.isEmpty(senderName) && (!this.mIsOneToOne || this.mAvatarReplacement == null || isOwnMessage)) {
                String symbol = uniqueNames.get(senderName);
                Icon cachedIcon = group.getAvatarSymbolIfMatching(senderName, symbol, this.mLayoutColor);
                if (cachedIcon != null) {
                    cachedAvatars.put(senderName, cachedIcon);
                }
            }
        }
        for (int i2 = 0; i2 < this.mGroups.size(); i2++) {
            MessagingGroup group2 = this.mGroups.get(i2);
            CharSequence senderName2 = group2.getSenderName();
            if (group2.needsGeneratedAvatar() && !TextUtils.isEmpty(senderName2)) {
                if (this.mIsOneToOne && this.mAvatarReplacement != null && group2.getSender() != this.mUser) {
                    group2.setAvatar(this.mAvatarReplacement);
                } else {
                    Icon cachedIcon2 = cachedAvatars.get(senderName2);
                    if (cachedIcon2 == null) {
                        cachedIcon2 = createAvatarSymbol(senderName2, uniqueNames.get(senderName2), this.mLayoutColor);
                        cachedAvatars.put(senderName2, cachedIcon2);
                    }
                    group2.setCreatedAvatar(cachedIcon2, senderName2, uniqueNames.get(senderName2), this.mLayoutColor);
                }
            }
        }
    }

    public Icon createAvatarSymbol(CharSequence senderName, String symbol, int layoutColor) {
        return this.mPeopleHelper.createAvatarSymbol(senderName, symbol, layoutColor);
    }

    private int findColor(CharSequence senderName, int layoutColor) {
        double luminance = ContrastColorUtil.calculateLuminance(layoutColor);
        float shift = ((Math.abs(senderName.hashCode()) % 5) / 4.0f) - 0.5f;
        return ContrastColorUtil.getShiftedColor(layoutColor, (int) (60.0f * ((float) (((float) (shift + Math.max(0.30000001192092896d - luminance, 0.0d))) - Math.max(0.30000001192092896d - (1.0d - luminance), 0.0d)))));
    }

    private String findNameSplit(String existingName) {
        String[] split = existingName.split(" ");
        if (split.length > 1) {
            return Character.toString(split[0].charAt(0)) + Character.toString(split[1].charAt(0));
        }
        return existingName.substring(0, 1);
    }

    @RemotableViewMethod
    public void setLayoutColor(int color) {
        this.mLayoutColor = color;
    }

    @RemotableViewMethod
    public void setIsOneToOne(boolean oneToOne) {
        this.mIsOneToOne = oneToOne;
    }

    @RemotableViewMethod
    public void setSenderTextColor(int color) {
        this.mSenderTextColor = color;
    }

    @RemotableViewMethod
    public void setNotificationBackgroundColor(int color) {
    }

    @RemotableViewMethod
    public void setMessageTextColor(int color) {
        this.mMessageTextColor = color;
    }

    public void setUser(Person user) {
        this.mUser = user;
        if (user.getIcon() == null) {
            Icon userIcon = Icon.createWithResource(getContext(), (int) C4057R.C4058drawable.messaging_user);
            userIcon.setTint(this.mLayoutColor);
            this.mUser = this.mUser.toBuilder().setIcon(userIcon).build();
        }
    }

    private void addMessagesToGroups(List<MessagingMessage> historicMessages, List<MessagingMessage> messages, boolean showSpinner) {
        List<List<MessagingMessage>> groups = new ArrayList<>();
        List<Person> senders = new ArrayList<>();
        findGroups(historicMessages, messages, groups, senders);
        createGroupViews(groups, senders, showSpinner);
    }

    private void createGroupViews(List<List<MessagingMessage>> groups, List<Person> senders, boolean showSpinner) {
        int i;
        this.mGroups.clear();
        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            List<MessagingMessage> group = groups.get(groupIndex);
            MessagingGroup newGroup = null;
            boolean z = true;
            for (int messageIndex = group.size() - 1; messageIndex >= 0; messageIndex--) {
                MessagingMessage message = group.get(messageIndex);
                newGroup = message.getGroup();
                if (newGroup != null) {
                    break;
                }
            }
            if (newGroup == null) {
                newGroup = MessagingGroup.createGroup(this.mMessagingLinearLayout);
                this.mAddedGroups.add(newGroup);
            } else if (newGroup.getParent() != this.mMessagingLinearLayout) {
                throw new IllegalStateException("group parent was " + newGroup.getParent() + " but expected " + this.mMessagingLinearLayout);
            }
            if (this.mIsCollapsed) {
                i = 2;
            } else {
                i = 0;
            }
            newGroup.setImageDisplayLocation(i);
            newGroup.setIsInConversation(false);
            newGroup.setLayoutColor(this.mLayoutColor);
            newGroup.setTextColors(this.mSenderTextColor, this.mMessageTextColor);
            Person sender = senders.get(groupIndex);
            CharSequence nameOverride = null;
            if (sender != this.mUser && this.mNameReplacement != null) {
                nameOverride = this.mNameReplacement;
            }
            newGroup.setSingleLine(this.mIsCollapsed);
            newGroup.setShowingAvatar(!this.mIsCollapsed);
            newGroup.setSender(sender, nameOverride);
            newGroup.setSending((groupIndex == groups.size() - 1 && showSpinner) ? false : false);
            this.mGroups.add(newGroup);
            if (this.mMessagingLinearLayout.indexOfChild(newGroup) != groupIndex) {
                this.mMessagingLinearLayout.removeView(newGroup);
                this.mMessagingLinearLayout.addView(newGroup, groupIndex);
            }
            newGroup.setMessages(group);
        }
    }

    private void findGroups(List<MessagingMessage> historicMessages, List<MessagingMessage> messages, List<List<MessagingMessage>> groups, List<Person> senders) {
        MessagingMessage message;
        CharSequence currentSenderKey = null;
        List<MessagingMessage> currentGroup = null;
        int histSize = historicMessages.size();
        for (int i = 0; i < messages.size() + histSize; i++) {
            if (i < histSize) {
                message = historicMessages.get(i);
            } else {
                message = messages.get(i - histSize);
            }
            boolean isNewGroup = currentGroup == null;
            CharSequence key = null;
            Person sender = message.getMessage() == null ? null : message.getMessage().getSenderPerson();
            if (sender != null) {
                key = sender.getKey() == null ? sender.getName() : sender.getKey();
            }
            if ((true ^ TextUtils.equals(key, currentSenderKey)) | isNewGroup) {
                currentGroup = new ArrayList<>();
                groups.add(currentGroup);
                if (sender == null) {
                    sender = this.mUser;
                }
                senders.add(sender);
                currentSenderKey = key;
            }
            currentGroup.add(message);
        }
    }

    private List<MessagingMessage> createMessages(List<Notification.MessagingStyle.Message> newMessages, boolean historic) {
        List<MessagingMessage> result = new ArrayList<>();
        for (int i = 0; i < newMessages.size(); i++) {
            Notification.MessagingStyle.Message m = newMessages.get(i);
            MessagingMessage message = findAndRemoveMatchingMessage(m);
            if (message == null) {
                message = MessagingMessage.createMessage(this, m, this.mImageResolver);
            }
            message.setIsHistoric(historic);
            result.add(message);
        }
        return result;
    }

    private MessagingMessage findAndRemoveMatchingMessage(Notification.MessagingStyle.Message m) {
        for (int i = 0; i < this.mMessages.size(); i++) {
            MessagingMessage existing = this.mMessages.get(i);
            if (existing.sameAs(m)) {
                this.mMessages.remove(i);
                return existing;
            }
        }
        for (int i2 = 0; i2 < this.mHistoricMessages.size(); i2++) {
            MessagingMessage existing2 = this.mHistoricMessages.get(i2);
            if (existing2.sameAs(m)) {
                this.mHistoricMessages.remove(i2);
                return existing2;
            }
        }
        return null;
    }

    public void showHistoricMessages(boolean show) {
        this.mShowHistoricMessages = show;
        updateHistoricMessageVisibility();
    }

    private void updateHistoricMessageVisibility() {
        int numHistoric = this.mHistoricMessages.size();
        int i = 0;
        while (true) {
            int i2 = 0;
            if (i >= numHistoric) {
                break;
            }
            MessagingMessage existing = this.mHistoricMessages.get(i);
            if (!this.mShowHistoricMessages) {
                i2 = 8;
            }
            existing.setVisibility(i2);
            i++;
        }
        int numGroups = this.mGroups.size();
        for (int i3 = 0; i3 < numGroups; i3++) {
            MessagingGroup group = this.mGroups.get(i3);
            int visibleChildren = 0;
            List<MessagingMessage> messages = group.getMessages();
            int numGroupMessages = messages.size();
            for (int j = 0; j < numGroupMessages; j++) {
                MessagingMessage message = messages.get(j);
                if (message.getVisibility() != 8) {
                    visibleChildren++;
                }
            }
            if (visibleChildren > 0 && group.getVisibility() == 8) {
                group.setVisibility(0);
            } else if (visibleChildren == 0 && group.getVisibility() != 8) {
                group.setVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!this.mAddedGroups.isEmpty()) {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.internal.widget.MessagingLayout.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    Iterator it = MessagingLayout.this.mAddedGroups.iterator();
                    while (it.hasNext()) {
                        MessagingGroup group = (MessagingGroup) it.next();
                        if (group.isShown()) {
                            MessagingPropertyAnimator.fadeIn(group.getAvatar());
                            MessagingPropertyAnimator.fadeIn(group.getSenderView());
                            MessagingPropertyAnimator.startLocalTranslationFrom(group, group.getHeight(), MessagingLayout.LINEAR_OUT_SLOW_IN);
                        }
                    }
                    MessagingLayout.this.mAddedGroups.clear();
                    MessagingLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    @Override // com.android.internal.widget.IMessagingLayout
    public MessagingLinearLayout getMessagingLinearLayout() {
        return this.mMessagingLinearLayout;
    }

    public ViewGroup getImageMessageContainer() {
        return this.mImageMessageContainer;
    }

    @Override // com.android.internal.widget.IMessagingLayout
    public ArrayList<MessagingGroup> getMessagingGroups() {
        return this.mGroups;
    }

    @Override // com.android.internal.widget.IMessagingLayout
    public void setMessagingClippingDisabled(boolean clippingDisabled) {
        this.mMessagingLinearLayout.setClipBounds(clippingDisabled ? null : this.mMessagingClipRect);
    }
}
