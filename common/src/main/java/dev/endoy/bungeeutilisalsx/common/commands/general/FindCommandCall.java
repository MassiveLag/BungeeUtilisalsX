package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.StaffUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class FindCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.find.usage" );
            return;
        }

        if ( StaffUtils.isHidden( args.get( 0 ) ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        final IProxyServer server = BuX.getApi().getPlayerUtils().findPlayer( args.get( 0 ) );

        if ( server == null )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        user.sendLangMessage(
                "general-commands.find.message",
                MessagePlaceholders.create()
                        .append( "user", args.get( 0 ) )
                        .append( "server", server.getName() ) );
    }

    @Override
    public String getDescription()
    {
        return "Finds the server the given user currently is in.";
    }

    @Override
    public String getUsage()
    {
        return "/find (user)";
    }
}
