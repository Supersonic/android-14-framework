package com.android.internal.widget;

import android.app.ActivityManager;
import android.app.Notification;
import android.view.View;
import com.android.internal.widget.MessagingLinearLayout;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes5.dex */
public interface MessagingMessage extends MessagingLinearLayout.MessagingChild {
    public static final String IMAGE_MIME_TYPE_PREFIX = "image/";

    MessagingMessageState getState();

    int getVisibility();

    void setVisibility(int i);

    static MessagingMessage createMessage(IMessagingLayout layout, Notification.MessagingStyle.Message m, ImageResolver resolver) {
        if (hasImage(m) && !ActivityManager.isLowRamDeviceStatic()) {
            return MessagingImageMessage.createMessage(layout, m, resolver);
        }
        return MessagingTextMessage.createMessage(layout, m);
    }

    static void dropCache() {
        MessagingTextMessage.dropCache();
        MessagingImageMessage.dropCache();
    }

    static boolean hasImage(Notification.MessagingStyle.Message m) {
        return (m.getDataUri() == null || m.getDataMimeType() == null || !m.getDataMimeType().startsWith(IMAGE_MIME_TYPE_PREFIX)) ? false : true;
    }

    default boolean setMessage(Notification.MessagingStyle.Message message) {
        getState().setMessage(message);
        return true;
    }

    default Notification.MessagingStyle.Message getMessage() {
        return getState().getMessage();
    }

    default boolean sameAs(Notification.MessagingStyle.Message message) {
        Notification.MessagingStyle.Message ownMessage = getMessage();
        if (message == null || ownMessage == null) {
            return message == ownMessage;
        } else if (Objects.equals(message.getText(), ownMessage.getText()) && Objects.equals(message.getSender(), ownMessage.getSender())) {
            boolean hasRemoteInputHistoryChanged = message.isRemoteInputHistory() != ownMessage.isRemoteInputHistory();
            return (hasRemoteInputHistoryChanged || Objects.equals(Long.valueOf(message.getTimestamp()), Long.valueOf(ownMessage.getTimestamp()))) && Objects.equals(message.getDataMimeType(), ownMessage.getDataMimeType()) && Objects.equals(message.getDataUri(), ownMessage.getDataUri());
        } else {
            return false;
        }
    }

    default boolean sameAs(MessagingMessage message) {
        return sameAs(message.getMessage());
    }

    default void removeMessage(ArrayList<MessagingLinearLayout.MessagingChild> toRecycle) {
        getGroup().removeMessage(this, toRecycle);
    }

    default void setMessagingGroup(MessagingGroup group) {
        getState().setGroup(group);
    }

    default void setIsHistoric(boolean isHistoric) {
        getState().setIsHistoric(isHistoric);
    }

    default MessagingGroup getGroup() {
        return getState().getGroup();
    }

    default void setIsHidingAnimated(boolean isHiding) {
        getState().setIsHidingAnimated(isHiding);
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    default boolean isHidingAnimated() {
        return getState().isHidingAnimated();
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    default void hideAnimated() {
        setIsHidingAnimated(true);
        getGroup().performRemoveAnimation(getView(), new Runnable() { // from class: com.android.internal.widget.MessagingMessage$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MessagingMessage.this.lambda$hideAnimated$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* synthetic */ default void lambda$hideAnimated$0() {
        setIsHidingAnimated(false);
    }

    default boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    default void recycle() {
        getState().recycle();
    }

    default View getView() {
        return (View) this;
    }

    default void setColor(int textColor) {
    }
}
