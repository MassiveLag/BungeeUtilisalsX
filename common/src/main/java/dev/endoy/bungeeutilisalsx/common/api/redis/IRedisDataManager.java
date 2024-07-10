package dev.endoy.bungeeutilisalsx.common.api.redis;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.concurrent.TimeUnit;

public interface IRedisDataManager
{

    void loadRedisUser( User user );

    void unloadRedisUser( User user );

    long getAmountOfOnlineUsersOnDomain( String domain );

    PartyDataManager getRedisPartyDataManager();

    boolean attemptShedLock( String type, int period, TimeUnit unit );

    default boolean attemptShedLock( String type, int period, dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit unit )
    {
        return attemptShedLock( type, period, unit.toJavaTimeUnit() );
    }

}
