package dev.endoy.bungeeutilisalsx.velocity.listeners;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.language.Language;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.configuration.api.IConfiguration;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;
import java.util.UUID;

public class PunishmentListener
{

    @Subscribe
    public void onLogin( final LoginEvent event )
    {
        final UUID uuid = event.getPlayer().getUniqueId();
        final String ip = Utils.getIP( event.getPlayer().getRemoteAddress() );

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            System.out.printf( "Checking ban for UUID: %s and IP: %s for server ALL%n", uuid.toString(), ip );
        }

        final String kickReason = getKickReasonIfBanned( uuid, ip, "ALL" );

        if ( kickReason != null )
        {
            event.setResult( ResultedEvent.ComponentResult.denied( Utils.format( kickReason ) ));
        }
    }

    @Subscribe( order = PostOrder.LAST )
    public void onConnect( final ServerPreConnectEvent event )
    {
        event.getResult().getServer().ifPresent( target ->
        {
            final Player player = event.getPlayer();
            final String ip = Utils.getIP( player.getRemoteAddress() );
            final String kickReason = getKickReasonIfBanned( player.getUniqueId(), ip, target.getServerInfo().getName() );

            if ( ConfigFiles.CONFIG.isDebug() )
            {
                System.out.printf(
                        "Checking ban for UUID: %s and IP: %s for server %s%n",
                        player.getUniqueId().toString(),
                        ip,
                        target.getServerInfo().getName()
                );
            }

            if ( kickReason != null )
            {
                // If current server is null, we're assuming the player just joined the network and tries to join a server he is banned on, kicking instead ...
                if ( event.getPlayer().getCurrentServer().isEmpty() )
                {
                    player.disconnect( Utils.format( kickReason ) );
                }
                else
                {
                    event.setResult( ServerPreConnectEvent.ServerResult.denied() );
                    player.sendMessage( Utils.format( kickReason ) );
                }
            }
        } );
    }

    private String getKickReasonIfBanned( final UUID uuid, final String ip, final String server )
    {
        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final Optional<UserStorage> optionalStorage = dao.getUserDao().getUserData( uuid ).join();

        if ( optionalStorage.isEmpty() )
        {
            return null;
        }
        final UserStorage storage = optionalStorage.get();
        final Language language = storage.getLanguage() == null ? BuX.getApi().getLanguageManager().getDefaultLanguage() : storage.getLanguage();
        final IConfiguration config = BuX.getApi().getLanguageManager().getConfig(
                BuX.getInstance().getName(), language
        ).getConfig();

        final BansDao bansDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao();
        PunishmentInfo info = bansDao.getCurrentBan( uuid, server ).join();
        if ( info == null )
        {
            info = bansDao.getCurrentIPBan( ip, server ).join();
        }

        if ( info == null )
        {
            return null;
        }

        if ( info.isExpired() )
        {
            if ( info.getType().equals( PunishmentType.TEMPBAN ) )
            {
                bansDao.removeCurrentBan( uuid, "EXPIRED", server );
            }
            else
            {
                bansDao.removeCurrentIPBan( ip, "EXPIRED", server );
            }
            return null;
        }

        String kick = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( info.getReason() ) )
        {
            kick = Utils.formatList(
                    BuX.getApi().getPunishmentExecutor().searchTemplate( config, info.getType(), info.getReason() ),
                    "\n"
            );
        }
        if ( kick == null )
        {
            kick = Utils.formatList( config.getStringList( "punishments." + info.getType().toString().toLowerCase() + ".kick" ), "\n" );
        }
        kick = BuX.getApi().getPunishmentExecutor().setPlaceHolders( kick, info );
        return kick;
    }
}