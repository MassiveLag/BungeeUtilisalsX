package dev.endoy.bungeeutilisalsx.common.commands.punishments.removal;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent;
import dev.endoy.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.commands.punishments.PunishmentCommand;

import java.util.List;

public class UnmuteIPCommandCall extends PunishmentCommand
{

    public UnmuteIPCommandCall()
    {
        super( null, false );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        // do nothing
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentRemovalArgs punishmentRemovalArgs = loadRemovalArguments( user, args );

        if ( punishmentRemovalArgs == null )
        {
            user.sendLangMessage( "punishments.unmuteip.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentRemovalArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = punishmentRemovalArgs.getStorage();
        if ( !dao().getPunishmentDao().getMutesDao().isIPMuted( storage.getIp(), punishmentRemovalArgs.getServerOrAll() ).join() )
        {
            user.sendLangMessage( "punishments.unmuteip.not-muted" );
            return;
        }

        if ( punishmentRemovalArgs.launchEvent( UserPunishRemoveEvent.PunishmentRemovalAction.UNMUTEIP ) )
        {
            return;
        }

        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getMutesDao().removeCurrentIPMute(
            storage.getIp(),
            user.getName(),
            punishmentRemovalArgs.getServerOrAll()
        );

        final PunishmentInfo info = new PunishmentInfo();
        info.setUser( punishmentRemovalArgs.getPlayer() );
        info.setId( "-1" );
        info.setExecutedBy( user.getName() );
        info.setRemovedBy( user.getName() );
        info.setServer( punishmentRemovalArgs.getServerOrAll() );

        punishmentRemovalArgs.removeCachedMute();

        user.sendLangMessage( "punishments.unmuteip.executed", executor.getPlaceHolders( info ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BuX.getApi().langBroadcast( "punishments.unmuteip.broadcast", executor.getPlaceHolders( info ) );
            }
            else
            {
                BuX.getApi().langPermissionBroadcast(
                    "punishments.unmuteip.broadcast",
                    ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.unmuteip.broadcast" ),
                    executor.getPlaceHolders( info )
                );
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Removes an IP mute for a given user / IP.";
    }

    @Override
    public String getUsage()
    {
        return "/unmuteip (user / IP) <server>";
    }
}