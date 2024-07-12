package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.FriendBroadcastJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

public class FriendBroadcastJobHandler
{

    @JobHandler
    void handleFriendBroadcastJob( final FriendBroadcastJob job )
    {
        job.getReceivers().forEach( user -> user.sendLangMessage(
                "friends.broadcast.message",
                MessagePlaceholders.create()
                        .append( "user", job.getSenderName() )
                        .append( "message", job.getMessage() )
        ) );
    }
}
