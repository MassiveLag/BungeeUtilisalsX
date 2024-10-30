package dev.endoy.bungeeutilisalsx.velocity.utils;

import dev.endoy.bungeeutilisalsx.common.motd.MotdConnection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
@AllArgsConstructor
public class VelocityMotdConnection extends MotdConnection
{

    private final int version;
    private final InetSocketAddress remoteIp;
    private final InetSocketAddress virtualHost;

}
