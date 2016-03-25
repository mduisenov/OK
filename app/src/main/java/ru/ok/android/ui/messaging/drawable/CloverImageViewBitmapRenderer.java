package ru.ok.android.ui.messaging.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.ui.custom.imageview.MultiUserAvatar;
import ru.ok.android.utils.clover.CloverImageView;
import ru.ok.android.utils.clover.CloverImageView.CloverImageHandler;
import ru.ok.model.UserInfo;

public class CloverImageViewBitmapRenderer {

    /* renamed from: ru.ok.android.ui.messaging.drawable.CloverImageViewBitmapRenderer.1 */
    static class C10441 implements CloverImageHandler {
        final /* synthetic */ IRenderCloverImageViewToBitmapCallback val$callback;
        final /* synthetic */ CloverImageView val$cloverImageView;
        final /* synthetic */ int val$size;

        C10441(int i, IRenderCloverImageViewToBitmapCallback iRenderCloverImageViewToBitmapCallback, CloverImageView cloverImageView) {
            this.val$size = i;
            this.val$callback = iRenderCloverImageViewToBitmapCallback;
            this.val$cloverImageView = cloverImageView;
        }

        public int getSize() {
            return this.val$size;
        }

        public void consumeImage(Bitmap bitmap) {
            this.val$callback.run(bitmap);
            this.val$cloverImageView.onDetachedFromWindow();
        }
    }

    public interface IRenderCloverImageViewToBitmapCallback {
        void run(Bitmap bitmap);
    }

    public static void render(Context context, IRenderCloverImageViewToBitmapCallback callback, Conversation conversation, List<UserInfo> users, int size) {
        String str;
        CloverImageView cloverImageView = new CloverImageView(context);
        cloverImageView.setCloverImageHandler(new C10441(size, callback, cloverImageView));
        cloverImageView.onAttachedToWindow();
        if (conversation.getType() == Type.PRIVATE || conversation.getParticipantsCount() <= 1) {
            str = OdnoklassnikiApplication.getCurrentUser().uid;
        } else {
            str = null;
        }
        cloverImageView.setLeaves(MultiUserAvatar.getLeafInfos(users, null, null, str));
    }
}
