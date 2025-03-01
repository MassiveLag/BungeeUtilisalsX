package dev.endoy.bungeeutilisalsx.velocity;

import dev.endoy.bungeeutilisalsx.common.*;
import dev.endoy.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcer;
import dev.endoy.bungeeutilisalsx.common.api.utils.Platform;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;
import dev.endoy.bungeeutilisalsx.common.commands.CommandManager;
import dev.endoy.bungeeutilisalsx.common.event.EventLoader;
import dev.endoy.bungeeutilisalsx.common.language.PluginLanguageManager;
import dev.endoy.bungeeutilisalsx.common.punishment.PunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.serverbalancer.SimpleServerBalancer;
import dev.endoy.bungeeutilisalsx.velocity.listeners.*;
import dev.endoy.bungeeutilisalsx.velocity.utils.player.RedisPlayerUtils;
import dev.endoy.bungeeutilisalsx.velocity.utils.player.VelocityPlayerUtils;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final ServerOperationsApi serverOperationsApi = new VelocityOperationsApi();
    private final CommandManager commandManager = new CommandManager();
    private final IPluginDescription pluginDescription = new VelocityPluginDescription();
    private final List<StaffUser> staffMembers = new ArrayList<>();

    @Override
    public void initialize()
    {
        super.initialize();
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        SimpleServerBalancer simpleServerBalancer = null;

        if ( ConfigFiles.SERVER_BALANCER_CONFIG.isEnabled() )
        {
            simpleServerBalancer = new SimpleServerBalancer();
            simpleServerBalancer.setup();
        }

        return new BuXApi(
                new PluginLanguageManager(),
                new EventLoader(),
                new PunishmentHelper(),
                ConfigFiles.CONFIG.getConfig().getBoolean( "multi-proxy.enabled" )
                        ? new RedisPlayerUtils()
                        : new VelocityPlayerUtils(),
                simpleServerBalancer
        );
    }

    @Override
    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    @Override
    protected void registerListeners()
    {
        Bootstrap.getInstance().getProxyServer().getEventManager().register(
                Bootstrap.getInstance(), new UserChatListener()
        );
        Bootstrap.getInstance().getProxyServer().getEventManager().register(
                Bootstrap.getInstance(), new UserConnectionListener()
        );
        Bootstrap.getInstance().getProxyServer().getEventManager().register(
                Bootstrap.getInstance(), new PluginMessageListener()
        );

        if ( ConfigFiles.PUNISHMENT_CONFIG.isEnabled() )
        {
            Bootstrap.getInstance().getProxyServer().getEventManager().register(
                    Bootstrap.getInstance(), new PunishmentListener()
            );
        }

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            Bootstrap.getInstance().getProxyServer().getEventManager().register(
                    Bootstrap.getInstance(), new MotdPingListener()
            );
        }
    }

    @Override
    protected void registerPluginSupports()
    {
        super.registerPluginSupports();
    }

    @Override
    public ServerOperationsApi serverOperations()
    {
        return serverOperationsApi;
    }

    @Override
    public File getDataFolder()
    {
        return Bootstrap.getInstance().getDataFolder();
    }

    @Override
    public String getVersion()
    {
        return pluginDescription.getVersion();
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return staffMembers;
    }

    @Override
    public IPluginDescription getDescription()
    {
        return pluginDescription;
    }

    @Override
    public Logger getLogger()
    {
        return Bootstrap.getInstance().getLogger();
    }

    @Override
    public Platform getPlatform()
    {
        return Platform.VELOCITYPOWERED;
    }

    @Override
    protected void registerMetrics()
    {
        final Metrics metrics = Bootstrap.getInstance().createMetrics();

        metrics.addCustomChart( new SimplePie(
                "configurations/punishments",
                () -> ConfigFiles.PUNISHMENT_CONFIG.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "motds",
                () -> ConfigFiles.MOTD.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "ingame_motds",
                () -> ConfigFiles.MOTD.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "friends",
                () -> ConfigFiles.FRIENDS_CONFIG.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "actionbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.ACTIONBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "title_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TITLE ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "bossbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.BOSSBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "chat_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.CHAT ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "tab_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TAB ) ? "enabled" : "disabled"
        ) );
// TODO: add chart "serverbalancer"
//        metrics.addCustomChart( new SimplePie(
//                "hubbalancer",
//                () -> this.getApi().getHubBalancer() != null ? "enabled" : "disabled"
//        ) );
        metrics.addCustomChart( new SimplePie(
                "protocolize",
                () -> this.isProtocolizeEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new AdvancedPie(
                "player_versions",
                () -> BuX.getApi().getUsers()
                        .stream()
                        .map( u -> u.getVersion().toString() )
                        .collect( Collectors.groupingBy( Function.identity(), Collectors.summingInt( it -> 1 ) ) )
        ) );
    }
}
