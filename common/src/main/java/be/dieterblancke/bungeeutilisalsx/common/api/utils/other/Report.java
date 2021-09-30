/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.api.utils.other;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Report
{

    private final long id;
    private final UUID uuid;
    private final String userName;
    private final String reportedBy;
    private final Date date;
    private final String server;
    private final String reason;
    private boolean handled;
    private boolean accepted;

    public void accept( final String accepter )
    {
        BuX.getInstance().getAbstractStorageManager().getDao().getReportsDao().handleReport( id, true );

        BuX.getApi().getStorageManager().getDao().getOfflineMessageDao().sendOfflineMessage(
                reportedBy,
                new OfflineMessage(
                        null,
                        "general-commands.report.accept.accepted",
                        "{id}", id,
                        "{reported}", userName,
                        "{staff}", accepter
                )
        );
    }
}
