package com.android.internal.widget;

import android.app.Notification;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class MessagingTextMessage extends ImageFloatingTextView implements MessagingMessage {
    private static final MessagingPool<MessagingTextMessage> sInstancePool = new MessagingPool<>(20);
    private final MessagingMessageState mState;

    public MessagingTextMessage(Context context) {
        super(context);
        this.mState = new MessagingMessageState(this);
    }

    public MessagingTextMessage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = new MessagingMessageState(this);
    }

    public MessagingTextMessage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mState = new MessagingMessageState(this);
    }

    public MessagingTextMessage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mState = new MessagingMessageState(this);
    }

    @Override // com.android.internal.widget.MessagingMessage
    public MessagingMessageState getState() {
        return this.mState;
    }

    @Override // com.android.internal.widget.MessagingMessage
    public boolean setMessage(Notification.MessagingStyle.Message message) {
        super.setMessage(message);
        setText(message.getText());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MessagingMessage createMessage(IMessagingLayout layout, Notification.MessagingStyle.Message m) {
        MessagingLinearLayout messagingLinearLayout = layout.getMessagingLinearLayout();
        MessagingTextMessage createdMessage = sInstancePool.acquire();
        if (createdMessage == null) {
            createdMessage = (MessagingTextMessage) LayoutInflater.from(layout.getContext()).inflate(C4057R.layout.notification_template_messaging_text_message, (ViewGroup) messagingLinearLayout, false);
            createdMessage.addOnLayoutChangeListener(MessagingLayout.MESSAGING_PROPERTY_ANIMATOR);
        }
        createdMessage.setMessage(m);
        return createdMessage;
    }

    @Override // com.android.internal.widget.MessagingMessage, com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public void recycle() {
        super.recycle();
        sInstancePool.release((MessagingPool<MessagingTextMessage>) this);
    }

    public static void dropCache() {
        sInstancePool.clear();
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public int getMeasuredType() {
        Layout layout;
        boolean measuredTooSmall = getMeasuredHeight() < (getLayoutHeight() + getPaddingTop()) + getPaddingBottom();
        if ((!measuredTooSmall || getLineCount() > 1) && (layout = getLayout()) != null) {
            return layout.getEllipsisCount(layout.getLineCount() - 1) > 0 ? 1 : 0;
        }
        return 2;
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public void setMaxDisplayedLines(int lines) {
        setMaxLines(lines);
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public int getConsumedLines() {
        return getLineCount();
    }

    public int getLayoutHeight() {
        Layout layout = getLayout();
        if (layout == null) {
            return 0;
        }
        return layout.getHeight();
    }

    @Override // com.android.internal.widget.MessagingMessage
    public void setColor(int color) {
        setTextColor(color);
    }
}
