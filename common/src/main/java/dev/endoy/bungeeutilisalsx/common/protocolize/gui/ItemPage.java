package dev.endoy.bungeeutilisalsx.common.protocolize.gui;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.TriConsumer;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiAction;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiActionType;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.handlers.CancelClickHandler;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.handlers.CloseClickHandler;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.handlers.NextPageClickHandler;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.handlers.PreviousPageClickHandler;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.ClickableGuiItem;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemPage
{

    private final GuiItem[] items;

    public ItemPage( final int slots )
    {
        items = new GuiItem[slots];
    }

    public void setRange( final GuiItem item, final int start, final int end )
    {
        this.setRange( item, start, end, false );
    }

    public void setRange( final GuiItem item, final int start, final int end, final boolean clone )
    {
        for ( int i = start; i < end; i++ )
        {
            this.setItem( i, clone ? item.copy() : item );
        }
    }

    public void setItem( final int slot, final GuiItem item )
    {
        items[slot] = item;
    }

    public void removeItem( final int slot )
    {
        this.setItem( slot, null );
    }

    public Optional<GuiItem> getItem( final int slot )
    {
        if ( slot < 0 || slot >= items.length )
        {
            return Optional.empty();
        }
        return Optional.ofNullable( items[slot] );
    }

    public void populateTo( final Inventory inventory )
    {
        for ( int i = 0; i < items.length; i++ )
        {
            if ( items[i] != null )
            {
                inventory.item( i, items[i].asItemStack() );
            }
        }
    }

    protected boolean shouldShow( final User user, final int page, final int max, final GuiConfigItem item )
    {
        if ( item.getShowIf().equalsIgnoreCase( "has-previous-page" ) )
        {
            return page > 0;
        }
        else if ( item.getShowIf().equalsIgnoreCase( "has-next-page" ) )
        {
            return page < max - 1;
        }
        else
        {
            return true;
        }
    }

    protected GuiItem getGuiItem( final User player, final GuiConfigItem item )
    {
        return this.getGuiItem( player, item, MessagePlaceholders.empty() );
    }

    protected GuiItem getGuiItem( final User player, final GuiConfigItem item, final HasMessagePlaceholders placeholders )
    {
        final ItemStack itemStack = item.getItem().buildItem( player, placeholders );

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }

    protected GuiItem getGuiItem( final GuiAction action, final GuiAction rightAction, final ItemStack itemStack )
    {
        return this.getGuiItem( action, rightAction, itemStack, MessagePlaceholders.empty() );
    }

    protected GuiItem getGuiItem( final GuiAction action, final GuiAction rightAction, final ItemStack itemStack, final HasMessagePlaceholders placeholders )
    {
        final ClickableGuiItem clickableGuiItem = new ClickableGuiItem( itemStack )
            .addHandler( ClickType.LEFT_CLICK, this.getClickHandler( action, placeholders ) );

        if ( rightAction != null && rightAction.isSet() )
        {
            clickableGuiItem.addHandler( ClickType.RIGHT_CLICK, this.getClickHandler( rightAction, placeholders ) );
        }

        return clickableGuiItem;
    }

    private TriConsumer<Gui, User, InventoryClick> getClickHandler( final GuiAction guiAction, final HasMessagePlaceholders placeholders )
    {
        final String action = Utils.replacePlaceHolders( guiAction.getAction().trim(), placeholders );

        if ( guiAction.getType() == GuiActionType.INPUT )
        {
            return this.getInputClickHandler( guiAction, action );
        }
        else
        {
            return this.getCommandClickHandler( action );
        }
    }

    private TriConsumer<Gui, User, InventoryClick> getCommandClickHandler( final String action )
    {
        if ( action.contains( "close" ) )
        {
            return new CloseClickHandler();
        }
        else if ( action.equalsIgnoreCase( "previous-page" ) )
        {
            return new PreviousPageClickHandler();
        }
        else if ( action.equalsIgnoreCase( "next-page" ) )
        {
            return new NextPageClickHandler();
        }
        else if ( action.contains( "open:" ) )
        {
            final String[] args = action.replace( "open:", "" ).trim().split( " " );
            final String guiName = args[0];

            return ( gui, player, event ) ->
            {
                event.cancelled( true );
                BuX.getInstance().getProtocolizeManager().getGuiManager().openGui(
                    player, guiName, Arrays.copyOfRange( args, 1, args.length )
                );
            };
        }
        else if ( action.contains( "execute:" ) )
        {
            final String command = action.replace( "execute:", "" ).trim();

            return ( gui, user, event ) ->
            {
                event.cancelled( true );
                BuX.getInstance().getProtocolizeManager().closeInventory( user );
                user.executeCommand( command );
            };
        }
        else
        {
            return new CancelClickHandler();
        }
    }

    private TriConsumer<Gui, User, InventoryClick> getInputClickHandler( final GuiAction guiAction, final String action )
    {
        return ( gui, user, event ) ->
        {
            event.cancelled( true );
            user.sendLangMessage( guiAction.getConfigSection().getString( "languagePath" ) );
            BuX.getInstance().getProtocolizeManager().closeInventory( user );

            final List<Consumer<String>> consumers = user.getStorage().getDataOrPut( UserStorageKey.CHAT_CONSUMERS, () -> new ArrayList<>() );
            consumers.add( ( str ) ->
            {
                if ( !str.trim().equalsIgnoreCase( "cancel" ) )
                {
                    final TriConsumer<Gui, User, InventoryClick> handler = this.getCommandClickHandler(
                        action.replace( "{output}", str )
                    );

                    handler.accept( gui, user, event );
                }
                else
                {
                    user.sendLangMessage( "gui.input.cancel" );
                }
            } );
        };
    }
}
