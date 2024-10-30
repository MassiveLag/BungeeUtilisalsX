package dev.endoy.bungeeutilisalsx.common.api.placeholder.event;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.placeholders.PlaceHolder;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode( callSuper = true )
public class InputPlaceHolderEvent extends PlaceHolderEvent
{

    private final String argument;

    public InputPlaceHolderEvent( User user, PlaceHolder placeHolder, String message, String argument )
    {
        super( user, placeHolder, message );

        this.argument = argument;
    }
}