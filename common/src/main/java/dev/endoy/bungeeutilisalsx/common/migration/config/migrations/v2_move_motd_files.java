package dev.endoy.bungeeutilisalsx.common.migration.config.migrations;

import com.google.common.io.Files;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.migration.ConfigMigration;

import java.io.File;

public class v2_move_motd_files implements ConfigMigration
{

    private final File motdFile;
    private final File ingameMotdFile;
    private final File motdFolder;

    public v2_move_motd_files()
    {
        this.motdFile = new File( BuX.getInstance().getDataFolder(), "motd.yml" );
        this.ingameMotdFile = new File( BuX.getInstance().getDataFolder(), "ingame-motd.yml" );
        this.motdFolder = new File( BuX.getInstance().getDataFolder(), "motd" );
    }

    @Override
    public boolean shouldRun() throws Exception
    {
        return motdFile.exists() && ingameMotdFile.exists();
    }

    @Override
    public void migrate() throws Exception
    {
        if ( !motdFolder.exists() )
        {
            motdFolder.mkdir();
        }

        Files.move( motdFile, new File( motdFolder, "motd.yml" ) );
        Files.move( ingameMotdFile, new File( motdFolder, "ingame-motd.yml" ) );
    }
}
