package com.dbsoftwares.bungeeutilisalsx.bungee.utils;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LanguageUtils
{

    public static void sendLangMessage( final ProxiedPlayer player, final String path )
    {
        sendLangMessage( (CommandSender) player, path );
    }

    public static void sendLangMessage( final ProxiedPlayer player, final String path, final Object... placeholders )
    {
        sendLangMessage( (CommandSender) player, path, placeholders );
    }

    public static void sendLangMessage( final CommandSender sender, final String path )
    {
        sendLangMessage( BuX.getApi().getLanguageManager(), BuX.getInstance().getName(), sender, path );
    }

    public static void sendLangMessage( final CommandSender sender, final String path, final Object... placeholders )
    {
        sendLangMessage( BuX.getApi().getLanguageManager(), BuX.getInstance().getName(), sender, path, placeholders );
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final CommandSender sender, final String path )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, sender.getName() );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                sender.sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            sender.sendMessage( Utils.format( config.getString( path ) ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final CommandSender sender, final String path, final Object... placeholders )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, sender.getName() );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                message = Utils.replacePlaceHolders( null, message, placeholders );

                sender.sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            String message = Utils.replacePlaceHolders( null, config.getString( path ), placeholders );

            sender.sendMessage( Utils.format( message ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final User user, final String path )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, user );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                user.sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            user.sendMessage( Utils.format( config.getString( path ) ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final User user, final String path, final Object... placeholders )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, user );

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isSection( path ) )
        {
            // section detected, assuming this is a message to be handled by MessageBuilder (hover / focus events)
            final TextComponent component = MessageBuilder.buildMessage( user, config.getSection( path ), placeholders );

            user.sendMessage( component );
        }
        else if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                message = Utils.replacePlaceHolders( user, message, placeholders );
                user.sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            String message = Utils.replacePlaceHolders( user, config.getString( path ), placeholders );
            user.sendMessage( Utils.format( message ) );
        }
    }
}