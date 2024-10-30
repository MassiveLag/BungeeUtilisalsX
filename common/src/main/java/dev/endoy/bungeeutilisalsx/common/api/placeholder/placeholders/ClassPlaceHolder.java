package dev.endoy.bungeeutilisalsx.common.api.placeholder.placeholders;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.handler.PlaceHolderEventHandler;

public abstract class ClassPlaceHolder extends PlaceHolder implements PlaceHolderEventHandler
{

    public ClassPlaceHolder( String placeHolder, boolean requiresUser )
    {
        super( placeHolder, requiresUser, null );
        super.setEventHandler( this );
    }
}