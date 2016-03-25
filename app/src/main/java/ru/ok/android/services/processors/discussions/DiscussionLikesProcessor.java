package ru.ok.android.services.processors.discussions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.discussions.data.UsersLikesParcelable;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.java.api.json.discussions.JsonUsersLikesParser;
import ru.ok.java.api.request.discussions.DiscussionLikesRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.Discussion;

public final class DiscussionLikesProcessor extends CommandProcessor {
    private static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = DiscussionLikesProcessor.class.getName();
    }

    public static String commandName(Discussion discussion) {
        return BASE_COMMAND_NAME + "/" + discussion.id;
    }

    public DiscussionLikesProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, Discussion discussion, String anchor) {
        intent.putExtra("DISCUSSION_ID", discussion.id);
        intent.putExtra("DISCUSSION_TYPE", discussion.type);
        intent.putExtra("ANCHOR", anchor);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        outBundle.putParcelable("USERS", UsersLikesParcelable.fromResponse(new JsonUsersLikesParser(this._transportProvider.execJsonHttpMethod(new DiscussionLikesRequest(data.getStringExtra("DISCUSSION_ID"), data.getStringExtra("DISCUSSION_TYPE"), data.getStringExtra("ANCHOR"), PagingDirection.FORWARD, 50, "user.first_name,user.last_name,user.gender,user.can_vcall,user.can_vmail,user.online,user." + DeviceUtils.getUserAvatarPicFieldName().getName())).getResultAsObject()).parse(), 50));
        return 1;
    }

    public static boolean isIt(String commandName, Discussion discussion) {
        return TextUtils.equals(commandName, commandName(discussion));
    }
}
