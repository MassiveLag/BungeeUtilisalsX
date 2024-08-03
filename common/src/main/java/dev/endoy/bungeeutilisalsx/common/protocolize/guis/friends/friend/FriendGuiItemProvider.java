package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friend;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendData;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.List;
import java.util.Optional;

public class FriendGuiItemProvider implements PageableItemProvider
{

    private final ItemPage[] pages;

    public FriendGuiItemProvider( final User user, final FriendGuiConfig config, final List<FriendData> friends )
    {
        final int itemsPerPage = config.getItems().stream()
            .filter( item -> ( (FriendGuiConfigItem) item ).isFriendItem() )
            .mapToInt( item -> item.getSlots().size() )
            .sum();
        int pages = (int) Math.ceil( (double) friends.size() / (double) itemsPerPage );
        if ( pages == 0 )
        {
            pages = 1;
        }
        this.pages = new ItemPage[pages];

        for ( int i = 0; i < pages; i++ )
        {
            final int max = ( i + 1 ) * itemsPerPage;

            this.pages[i] = new FriendItemPage(
                user,
                i,
                pages,
                config,
                friends.isEmpty() ? friends : friends.size() <= max ? friends : friends.subList( i * itemsPerPage, max )
            );
        }
    }

    @Override
    public Optional<GuiItem> getItemAtSlot( final int page, final int rawSlot )
    {
        return this.getItemContents( page ).getItem( rawSlot );
    }

    @Override
    public ItemPage getItemContents( int page )
    {
        if ( page == 0 )
        {
            page = 1;
        }
        if ( page > pages.length )
        {
            page = pages.length;
        }
        return pages[page - 1];
    }

    @Override
    public int getPageAmount()
    {
        return pages.length;
    }
}
