package dev.endoy.bungeeutilisalsx.common.redis;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.events.redis.RedisMessageEvent;
import io.lettuce.core.pubsub.RedisPubSubAdapter;

public class PubSubListener extends RedisPubSubAdapter<String, String>
{

    @Override
    public void message( String channel, String message )
    {
        BuX.getApi().getEventLoader().launchEvent( new RedisMessageEvent( channel, message ) );
    }
}