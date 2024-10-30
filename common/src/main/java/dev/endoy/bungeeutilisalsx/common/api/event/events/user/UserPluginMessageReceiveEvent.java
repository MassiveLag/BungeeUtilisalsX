package dev.endoy.bungeeutilisalsx.common.api.event.events.user;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * This event will be executed when a plugin message is received.
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class UserPluginMessageReceiveEvent extends AbstractEvent
{

    private final User user;
    private final String channel;
    private final byte[] message;

}