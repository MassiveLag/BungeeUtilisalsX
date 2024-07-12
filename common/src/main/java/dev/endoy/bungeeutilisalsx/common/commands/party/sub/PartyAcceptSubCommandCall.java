package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyInvite;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyManager;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyMember;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PartyAcceptSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PartyManager partyManager = BuX.getInstance().getPartyManager();

        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.accept.usage" );
            return;
        }

        final Optional<Party> optionalParty = partyManager.getCurrentPartyFor( user.getName() );

        // Already in party checks
        if ( optionalParty.isPresent() )
        {
            if ( ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "allow-invites-to-members-already-in-party" ) )
            {
                PartyLeaveSubCommandCall.leaveParty( optionalParty.get(), user );
            }
            else
            {
                user.sendLangMessage( "party.accept.already-in-party" );
                return;
            }
        }

        // Check invite
        final Optional<Party> optionalInviterParty = partyManager.getCurrentPartyFor( args.get( 0 ) );
        if ( optionalInviterParty.isEmpty() )
        {
            user.sendLangMessage( "party.accept.no-party", MessagePlaceholders.create().append( "user", args.get( 0 ) ) );
            return;
        }

        final Party inviterParty = optionalInviterParty.get();
        final Optional<PartyInvite> optionalInvite = inviterParty.getSentInvites()
                .stream()
                .filter( invite -> invite.getInvitee().equals( user.getUuid() ) )
                .findAny();

        if ( optionalInvite.isEmpty() )
        {
            user.sendLangMessage( "party.accept.not-invited-to-party", MessagePlaceholders.create().append( "user", args.get( 0 ) ) );
            return;
        }

        if ( inviterParty.isFull() )
        {
            user.sendLangMessage(
                    "party.other-party-full",
                    MessagePlaceholders.create().append( "user", inviterParty.getOwner().getNickName() )
            );
            return;
        }

        // Add to party and remove invite
        final PartyInvite invite = optionalInvite.get();
        partyManager.removeInvitationFromParty( inviterParty, invite );

        final PartyMember partyMember = new PartyMember(
                user.getUuid(),
                user.getName(),
                new Date(),
                user.getName(),
                ConfigFiles.PARTY_CONFIG.getDefaultRole(),
                false,
                false,
                false
        );

        partyManager.addMemberToParty( inviterParty, partyMember );
        user.sendLangMessage(
                "party.accept.accepted",
                MessagePlaceholders.create().append( "user", args.get( 0 ) )
        );
        partyManager.languageBroadcastToParty(
                inviterParty,
                "party.accept.joined-party",
                MessagePlaceholders.create().append( "user", user.getName() )
        );
    }

    @Override
    public String getDescription()
    {
        return "Accepts the party invite from a certain user.";
    }

    @Override
    public String getUsage()
    {
        return "/party accept (user)";
    }
}
