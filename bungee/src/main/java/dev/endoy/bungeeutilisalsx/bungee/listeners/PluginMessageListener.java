package dev.endoy.bungeeutilisalsx.bungee.listeners;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserPluginMessageReceiveEvent;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class PluginMessageListener implements Listener
{

    public PluginMessageListener()
    {
        ProxyServer.getInstance().registerChannel( "bux:main" );
    }

    @EventHandler
    public void onMainPluginMessage( final PluginMessageEvent event )
    {
        if ( !event.getTag().equalsIgnoreCase( "bux:main" ) )
        {
            return;
        }
        if ( !( event.getReceiver() instanceof ProxiedPlayer ) )
        {
            return;
        }
        final Optional<User> optionalUser = BuX.getApi().getUser( ( (ProxiedPlayer) event.getReceiver() ).getUniqueId() );
        if ( optionalUser.isEmpty() )
        {
            return;
        }
        final UserPluginMessageReceiveEvent userPluginMessageReceiveEvent = new UserPluginMessageReceiveEvent(
            optionalUser.get(),
            event.getTag(),
            event.getData().clone()
        );
        BuX.getApi().getEventLoader().launchEventAsync( userPluginMessageReceiveEvent );
    }
}
