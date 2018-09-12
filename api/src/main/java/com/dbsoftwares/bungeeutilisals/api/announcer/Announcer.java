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

package com.dbsoftwares.bungeeutilisals.api.announcer;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.math.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Data
public abstract class Announcer {

    @Getter
    private static Map<AnnouncementType, Announcer> announcers = Maps.newHashMap();

    protected IConfiguration configuration;
    private ScheduledTask task;
    private AnnouncementType type;
    private Map<Announcement, Boolean> announcements = Maps.newHashMap();
    private Iterator<Announcement> announcementIterator;
    private boolean enabled;
    private TimeUnit unit;
    private int delay;
    private boolean random;

    public Announcer(AnnouncementType type) {
        this.type = type;

        File folder = new File(BUCore.getApi().getPlugin().getDataFolder(), "announcer");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, type.toString().toLowerCase() + ".yml");
        if (!file.exists()) {
            IConfiguration.createDefaultFile(
                    BUCore.getApi().getPlugin().getResourceAsStream(
                            "announcers/" + type.toString().toLowerCase() + ".yml"
                    ), file
            );
        }

        configuration = IConfiguration.loadYamlConfiguration(file);
        enabled = configuration.getBoolean("enabled");
        unit = TimeUnit.valueOfOrElse(configuration.getString("delay.unit"), TimeUnit.SECONDS);
        delay = configuration.getInteger("delay.time");
        random = configuration.getBoolean("random");
    }

    @SafeVarargs
    public static void registerAnnouncers(Class<? extends Announcer>... classes) {
        for (Class<? extends Announcer> clazz : classes) {
            try {
                Announcer announcer = clazz.newInstance();

                if (announcer.isEnabled()) {
                    announcer.loadAnnouncements();
                    announcer.start();

                    BUCore.log("Loading " + announcer.getType().toString().toLowerCase() + " announcements ...");
                }

                announcers.put(announcer.getType(), announcer);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (task != null) {
            throw new IllegalStateException("Announcer is already running.");
        }

        task = ProxyServer.getInstance().getScheduler().schedule(
                BUCore.getApi().getPlugin(),
                new Runnable() {

                    private Announcement previous;

                    @Override
                    public void run() {
                        if (previous != null) {
                            previous.clear();
                        }
                        Announcement next = (random ? getRandomAnnouncement() : getNextAnnouncement());
                        next.send();
                        previous = next;
                    }
                },
                delay,
                delay,
                unit.toJavaTimeUnit()
        );
    }

    public void stop() {
        if (task == null) {
            return;
        }
        task.cancel();
        task = null;
    }

    public void addAnnouncement(Announcement announcement) {
        announcements.put(announcement, false);
    }

    private Announcement getRandomAnnouncement() {
        if (!announcements.containsValue(false)) { // finished Announcement rotation, restarting it
            announcements.replaceAll((key, value) -> false);
        }

        List<Announcement> announcementsKeys = Lists.newArrayList();
        announcements.forEach((key, value) -> {
            if (!value) {
                announcementsKeys.add(key);
            }
        });

        Announcement random = MathUtils.getRandomFromList(announcementsKeys);
        announcements.put(random, true);
        return random;
    }

    private Announcement getNextAnnouncement() {
        if (announcementIterator == null || !announcementIterator.hasNext()) {
            announcementIterator = announcements.keySet().iterator();
        }
        return announcementIterator.next();
    }

    public abstract void loadAnnouncements();

    public void reload() {
        try {
            configuration.reload();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        stop();
        announcements.clear();
        announcementIterator = null;

        enabled = configuration.getBoolean("enabled");
        unit = TimeUnit.valueOfOrElse(configuration.getString("delay.unit"), TimeUnit.SECONDS);
        delay = configuration.getInteger("delay.time");
        random = configuration.getBoolean("random");

        if (enabled) {
            loadAnnouncements();
            start();
        }
    }
}