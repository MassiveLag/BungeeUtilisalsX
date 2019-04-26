/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.packet.packets.out;

import com.dbsoftwares.bungeeutilisals.api.bossbar.BossBarAction;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.packet.packets.Packet;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Objects;
import java.util.UUID;

public class PacketPlayOutBossBar extends Packet {

    private UUID uuid;
    private int action;
    private String name;
    private float percent;
    private int color;
    private int overlay;
    private short flags;

    public UUID getUuid() {
        if (uuid == null) {
            throw new IllegalStateException("No boss bar UUID specified");
        }
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getOverlay() {
        return overlay;
    }

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    public short getFlags() {
        return flags;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "BossBar{"
                + "uuid=" + uuid
                + ", action=" + action
                + ", name='" + name + '\''
                + ", percent=" + percent
                + ", color=" + color
                + ", overlay=" + overlay
                + ", flags=" + flags
                + '}';
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.uuid = readUUID(buf);
        this.action = readVarInt(buf);
        if (action == BossBarAction.ADD.getId()) {
            this.name = readString(buf);
            this.percent = buf.readFloat();
            this.color = readVarInt(buf);
            this.overlay = readVarInt(buf);
            this.flags = buf.readUnsignedByte();
        } else if (action == BossBarAction.REMOVE.getId()) {

        } else if (action == BossBarAction.UPDATE_HEALTH.getId()) {
            this.percent = buf.readFloat();
        } else if (action == BossBarAction.UPDATE_TITLE.getId()) {
            this.name = readString(buf);
        } else if (action == BossBarAction.UPDATE_STYLE.getId()) {
            this.color = readVarInt(buf);
            this.overlay = readVarInt(buf);
        } else if (action == BossBarAction.UPDATE_FLAGS.getId()) {
            this.flags = buf.readUnsignedByte();
        } else {
            throw new UnsupportedOperationException("Unknown action " + action);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (uuid == null) {
            throw new IllegalStateException("No boss bar UUID specified");
        }
        writeUUID(uuid, buf);
        writeVarInt(action, buf);

        if (action == BossBarAction.ADD.getId()) {
            if (name == null) {
                throw new IllegalStateException("No name specified!");
            }
            writeString(name, buf);
            buf.writeFloat(percent);
            writeVarInt(color, buf);
            writeVarInt(overlay, buf);
            buf.writeByte(flags);
        } else if (action == BossBarAction.REMOVE.getId()) {

        } else if (action == BossBarAction.UPDATE_HEALTH.getId()) {
            buf.writeFloat(percent);
        } else if (action == BossBarAction.UPDATE_TITLE.getId()) {
            if (name == null) {
                throw new IllegalStateException("No name specified!");
            }
            writeString(name, buf);
        } else if (action == BossBarAction.UPDATE_STYLE.getId()) {
            writeVarInt(color, buf);
            writeVarInt(overlay, buf);
        } else if (action == BossBarAction.UPDATE_FLAGS.getId()) {
            buf.writeByte(flags);
        } else {
            throw new UnsupportedOperationException("Unknown action " + action);
        }
    }

    @Override
    public void handle(User user) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PacketPlayOutBossBar that = (PacketPlayOutBossBar) o;
        return action == that.action &&
                Float.compare(that.percent, percent) == 0 &&
                color == that.color &&
                overlay == that.overlay &&
                flags == that.flags &&
                uuid.equals(that.uuid) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, action, name, percent, color, overlay, flags);
    }
}
