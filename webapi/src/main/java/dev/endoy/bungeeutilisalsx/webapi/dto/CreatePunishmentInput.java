package dev.endoy.bungeeutilisalsx.webapi.dto;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePunishmentInput
{

    PunishmentType type;
    UUID uuid;
    String user;
    String ip;
    String executedBy;
    String server;
    String reason;
    Long expireTime;
    boolean active;

}
