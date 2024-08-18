package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import com.google.common.collect.Lists;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FriendsConfig extends Config
{

    @Getter
    private final List<ServerGroup> disabledSwitchMessageServers = Lists.newArrayList();

    public FriendsConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        disabledSwitchMessageServers.clear();
    }

    @Override
    public void setup()
    {
        disabledSwitchMessageServers.addAll(
            config.getStringList( "ignore-for-switch" )
                .stream()
                .map( str -> ConfigFiles.SERVERGROUPS.getServer( str ) )
                .flatMap( Optional::stream )
                .collect( Collectors.toList() )
        );
    }

    public boolean isDisabledServerSwitch( final String serverName )
    {
        if ( serverName == null )
        {
            return false;
        }
        return disabledSwitchMessageServers.stream().anyMatch( group -> group.isInGroup( serverName ) );
    }
}
