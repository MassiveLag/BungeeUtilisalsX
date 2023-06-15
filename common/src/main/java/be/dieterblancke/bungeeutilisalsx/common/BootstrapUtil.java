package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.LibraryClassLoader;
import be.dieterblancke.bungeeutilisalsx.common.library.Library;
import be.dieterblancke.bungeeutilisalsx.common.library.StandardLibrary;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.logging.Logger;

public class BootstrapUtil
{

    @Getter
    private static File dataFolder;

    @SneakyThrows
    public static void loadLibraries( File dataFolder,
                                      LibraryClassLoader libraryClassLoader,
                                      Logger logger )
    {
        BootstrapUtil.dataFolder = dataFolder;

        logger.info( "Loading libraries ..." );

        for ( StandardLibrary standardLibrary : StandardLibrary.values() )
        {
            Library library = standardLibrary.getLibrary();

            if ( library.isToLoad() && !library.isPresent() )
            {
                library.load( dataFolder, libraryClassLoader, logger );
            }
        }
        logger.info( "Libraries have been loaded." );
    }
}
