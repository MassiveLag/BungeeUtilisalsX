package dev.endoy.bungeeutilisalsx.spigot;

import dev.endoy.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import dev.endoy.bungeeutilisalsx.common.BootstrapUtil;
import dev.endoy.bungeeutilisalsx.common.api.utils.Platform;
import dev.endoy.bungeeutilisalsx.common.api.utils.reflection.UrlLibraryClassLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    @Getter
    private static Bootstrap instance;
    private AbstractBungeeUtilisalsX abstractBungeeUtilisalsX;

    @Override
    public void onEnable()
    {
        instance = this;

        Platform.setCurrentPlatform( Platform.SPIGOT );
        BootstrapUtil.loadLibraries( this.getDataFolder(), new UrlLibraryClassLoader(), getLogger() );

        abstractBungeeUtilisalsX = new BungeeUtilisalsX();
        abstractBungeeUtilisalsX.initialize();
    }

    @Override
    public void onDisable()
    {
        abstractBungeeUtilisalsX.shutdown();
    }
}