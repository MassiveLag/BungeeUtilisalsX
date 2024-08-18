package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.party.Party;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.PartyConfig.PartyRole;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartySetRoleSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 2 )
        {
            user.sendLangMessage( "party.setrole.usage" );
            return;
        }
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( optionalParty.isEmpty() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Party party = optionalParty.get();

        if ( !party.isOwner( user.getUuid() ) )
        {
            user.sendLangMessage( "party.setrole.not-allowed" );
            return;
        }
        final String targetName = args.get( 0 );
        final String roleName = args.get( 1 );

        party.getPartyMembers()
            .stream()
            .filter( m -> m.getUserName().equalsIgnoreCase( targetName ) || m.getNickName().equalsIgnoreCase( targetName ) )
            .findFirst()
            .ifPresentOrElse( member ->
            {
                ConfigFiles.PARTY_CONFIG.findPartyRole( roleName ).ifPresentOrElse( role ->
                {
                    BuX.getInstance().getPartyManager().setPartyMemberRole( party, member, role );

                    user.sendLangMessage(
                        "party.setrole.role-updated",
                        MessagePlaceholders.create()
                            .append( "user", member.getUserName() )
                            .append( "role", role.getName() )
                    );

                    BuX.getInstance().getPartyManager().languageBroadcastToParty(
                        party,
                        "party.setrole.role-updated-broadcast",
                        MessagePlaceholders.create()
                            .append( "user", member.getUserName() )
                            .append( "role", role.getName() )
                    );
                }, () -> user.sendLangMessage(
                    "party.setrole.incorrect-role",
                    MessagePlaceholders.create()
                        .append( "roles", ConfigFiles.PARTY_CONFIG.getPartyRoles().stream().map( PartyRole::getName ).collect( Collectors.joining( ", " ) ) )
                ) );
            }, () -> user.sendLangMessage( "party.setrole.not-in-party" ) );
    }

    @Override
    public String getDescription()
    {
        return "Warps all party members to your current server.";
    }

    @Override
    public String getUsage()
    {
        return "/party setrole (user) (role)";
    }
}
