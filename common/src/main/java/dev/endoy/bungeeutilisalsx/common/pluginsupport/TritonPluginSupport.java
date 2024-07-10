package dev.endoy.bungeeutilisalsx.common.pluginsupport;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import com.rexcantor64.triton.api.TritonAPI;
import com.rexcantor64.triton.api.language.Localized;
import com.rexcantor64.triton.api.players.LanguagePlayer;

public class TritonPluginSupport implements PluginSupport
{
    @Override
    public boolean isEnabled()
    {
        return BuX.getInstance().serverOperations().getPlugin( "Triton" ).isPresent();
    }

    @Override
    public void registerPluginSupport()
    {
    }

    public String formatGuiMessage( User user, String message )
    {
        if ( user == null )
        {
            return message;
        }

        LanguagePlayer player = TritonAPI.getInstance().getPlayerManager().get( user.getUuid() );
        Localized localized = player.getLanguage();

        return TritonAPI.getInstance().getMessageParser().translateString( message, localized, TritonAPI.getInstance().getConfig().getGuiSyntax() ).getResult()
                .orElse( message );
    }
}
