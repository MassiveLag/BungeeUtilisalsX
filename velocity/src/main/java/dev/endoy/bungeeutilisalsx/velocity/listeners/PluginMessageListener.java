package dev.endoy.bungeeutilisalsx.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserPluginMessageReceiveEvent;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.velocity.Bootstrap;

import java.util.Optional;

public class PluginMessageListener
{

    private static final MinecraftChannelIdentifier MAIN_CHANNEL = MinecraftChannelIdentifier.create( "bux", "main" );

    public PluginMessageListener()
    {
        Bootstrap.getInstance().getProxyServer().getChannelRegistrar().register( MAIN_CHANNEL );
    }

    @Subscribe
    public void onMainPluginMessage( final PluginMessageEvent event )
    {
        if ( !event.getIdentifier().equals( MAIN_CHANNEL ) )
        {
            return;
        }
        if ( !( event.getTarget() instanceof Player ) )
        {
            return;
        }
        final Optional<User> optionalUser = BuX.getApi().getUser( ( (Player) event.getTarget() ).getUniqueId() );
        if ( optionalUser.isEmpty() )
        {
            return;
        }
        final UserPluginMessageReceiveEvent userPluginMessageReceiveEvent = new UserPluginMessageReceiveEvent(
                optionalUser.get(),
                event.getIdentifier().getId(),
                event.getData().clone()
        );
        BuX.getApi().getEventLoader().launchEventAsync( userPluginMessageReceiveEvent );
    }
}
