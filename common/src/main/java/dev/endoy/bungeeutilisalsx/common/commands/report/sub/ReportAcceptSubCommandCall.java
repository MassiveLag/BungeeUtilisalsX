package dev.endoy.bungeeutilisalsx.common.commands.report.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;

public class ReportAcceptSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.report.accept.usage" );
            return;
        }

        if ( !MathUtils.isLong( args.get( 0 ) ) )
        {
            user.sendLangMessage( "no-number" );
            return;
        }

        final long id = Long.parseLong( args.get( 0 ) );
        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final ReportsDao reportsDao = dao.getReportsDao();

        reportsDao.getReport( id ).thenAccept( report ->
        {
            if ( report == null )
            {
                user.sendLangMessage( "general-commands.report.accept.not-found" );
                return;
            }

            report.accept( user.getName() );

            user.sendLangMessage(
                    "general-commands.report.accept.updated",
                    MessagePlaceholders.create().append( "id", id )
            );

            Optional<User> optionalUser = BuX.getApi().getUser( report.getReportedBy() );
            MessagePlaceholders placeholders = MessagePlaceholders.create()
                    .append( report )
                    .append( "staff", user.getName() );

            if ( optionalUser.isPresent() )
            {
                final User target = optionalUser.get();

                target.sendLangMessage(
                        "general-commands.report.deny.accepted",
                        placeholders
                );
            }
            else if ( BuX.getApi().getPlayerUtils().isOnline( report.getReportedBy() ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                        report.getReportedBy(),
                        "general-commands.report.deny.accepted",
                        placeholders
                ) );
            }
            else
            {
                BuX.getApi().getStorageManager().getDao().getOfflineMessageDao().sendOfflineMessage(
                        report.getReportedBy(),
                        new OfflineMessage(
                                null,
                                "general-commands.report.deny.accepted",
                                placeholders
                        )
                );
            }
        } );
    }

    @Override
    public String getDescription()
    {
        return "Accepts a report with a given id. This will notify the reporter.";
    }

    @Override
    public String getUsage()
    {
        return "/report accept (id)";
    }
}
