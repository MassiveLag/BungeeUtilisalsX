package dev.endoy.bungeeutilisalsx.common.storage.data.mongo.dao.punishment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoMutesDao implements MutesDao
{

    @Override
    public CompletableFuture<Boolean> isMuted( final UUID uuid, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
            );
            if ( PunishmentDao.useServerPunishments() )
            {
                filters.add( Filters.eq( "server", server ) );
            }

            return db()
                .getCollection( PunishmentType.MUTE.getTable() )
                .find( Filters.and( filters ) )
                .limit( 1 )
                .iterator()
                .hasNext();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> isIPMuted( final String ip, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" )
            );
            if ( PunishmentDao.useServerPunishments() )
            {
                filters.add( Filters.eq( "server", server ) );
            }

            return db()
                .getCollection( PunishmentType.MUTE.getTable() )
                .find( Filters.and( filters ) )
                .limit( 1 )
                .iterator()
                .hasNext();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertMute( final UUID uuid,
                                                         final String user,
                                                         final String ip,
                                                         final String reason,
                                                         final String server,
                                                         final boolean active,
                                                         final String executedby )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueMuteId();
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
            data.put( "type", PunishmentType.MUTE.toString() );
            data.put( "uuid", uuid.toString() );
            data.put( "user", user );
            data.put( "ip", ip );
            data.put( "reason", reason );
            data.put( "server", server );
            data.put( "date", new Date() );
            data.put( "duration", -1 );
            data.put( "active", active );
            data.put( "executed_by", executedby );
            data.put( "removed", false );
            data.put( "removed_by", null );
            data.put( "punishmentaction_status", false );
            data.put( "punishment_uid", punishmentUid );

            db().getCollection( PunishmentType.MUTE.getTable() ).insertOne( new Document( data ) );
            return PunishmentDao.buildPunishmentInfo(
                PunishmentType.MUTE,
                uuid,
                user,
                ip,
                reason,
                server,
                executedby,
                new Date(),
                -1,
                active,
                null,
                punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertIPMute( final UUID uuid,
                                                           final String user,
                                                           final String ip,
                                                           final String reason,
                                                           final String server,
                                                           final boolean active,
                                                           final String executedby )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueMuteId();
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
            data.put( "type", PunishmentType.IPMUTE.toString() );
            data.put( "uuid", uuid.toString() );
            data.put( "user", user );
            data.put( "ip", ip );
            data.put( "reason", reason );
            data.put( "server", server );
            data.put( "date", new Date() );
            data.put( "duration", -1 );
            data.put( "active", active );
            data.put( "executed_by", executedby );
            data.put( "removed", false );
            data.put( "removed_by", null );
            data.put( "punishmentaction_status", false );
            data.put( "punishment_uid", punishmentUid );

            db().getCollection( PunishmentType.IPMUTE.getTable() ).insertOne( new Document( data ) );
            return PunishmentDao.buildPunishmentInfo(
                PunishmentType.IPMUTE,
                uuid,
                user,
                ip,
                reason,
                server,
                executedby,
                new Date(),
                -1,
                active,
                null,
                punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertTempMute( final UUID uuid,
                                                             final String user,
                                                             final String ip,
                                                             final String reason,
                                                             final String server,
                                                             final boolean active,
                                                             final String executedby,
                                                             final long duration )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueMuteId();
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
            data.put( "type", PunishmentType.TEMPMUTE.toString() );
            data.put( "uuid", uuid.toString() );
            data.put( "user", user );
            data.put( "ip", ip );
            data.put( "reason", reason );
            data.put( "server", server );
            data.put( "date", new Date() );
            data.put( "duration", duration );
            data.put( "active", active );
            data.put( "executed_by", executedby );
            data.put( "removed", false );
            data.put( "removed_by", null );
            data.put( "punishmentaction_status", false );
            data.put( "punishment_uid", punishmentUid );

            db().getCollection( PunishmentType.TEMPMUTE.getTable() ).insertOne( new Document( data ) );
            return PunishmentDao.buildPunishmentInfo(
                PunishmentType.TEMPMUTE,
                uuid,
                user,
                ip,
                reason,
                server,
                executedby,
                new Date(),
                duration,
                active,
                null,
                punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertTempIPMute( final UUID uuid,
                                                               final String user,
                                                               final String ip,
                                                               final String reason,
                                                               final String server,
                                                               final boolean active,
                                                               final String executedby,
                                                               final long duration )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueMuteId();
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
            data.put( "type", PunishmentType.IPTEMPMUTE.toString() );
            data.put( "uuid", uuid.toString() );
            data.put( "user", user );
            data.put( "ip", ip );
            data.put( "reason", reason );
            data.put( "server", server );
            data.put( "date", new Date() );
            data.put( "duration", duration );
            data.put( "active", active );
            data.put( "executed_by", executedby );
            data.put( "removed", false );
            data.put( "removed_by", null );
            data.put( "punishmentaction_status", false );
            data.put( "punishment_uid", punishmentUid );

            db().getCollection( PunishmentType.IPTEMPMUTE.getTable() ).insertOne( new Document( data ) );
            return PunishmentDao.buildPunishmentInfo(
                PunishmentType.IPTEMPMUTE,
                uuid,
                user,
                ip,
                reason,
                server,
                executedby,
                new Date(),
                duration,
                active,
                null,
                punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getCurrentMute( final UUID uuid, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
            );
            if ( PunishmentDao.useServerPunishments() )
            {
                filters.add( Filters.eq( "server", serverName ) );
            }

            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final Document document = collection.find( Filters.and( filters ) ).first();

            if ( document != null )
            {
                return this.buildPunishmentInfo( document );
            }

            return null;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getCurrentIPMute( final String ip, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" )
            );
            if ( PunishmentDao.useServerPunishments() )
            {
                filters.add( Filters.eq( "server", serverName ) );
            }

            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final Document document = collection.find( Filters.and( filters ) ).first();

            if ( document != null )
            {
                return this.buildPunishmentInfo( document );
            }

            return null;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeCurrentMute( final UUID uuid, final String removedBy, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
            );
            if ( PunishmentDao.useServerPunishments() )
            {
                filters.add( Filters.eq( "server", server ) );
            }
            final MongoCollection<Document> coll = db().getCollection( PunishmentType.MUTE.getTable() );

            // updateMany, this if for some reason multiple mutes would be active at the same time.
            coll.updateMany(
                Filters.and( filters ),
                Updates.combine(
                    Updates.set( "active", false ),
                    Updates.set( "removed", true ),
                    Updates.set( "removed_by", removedBy ),
                    Updates.set( "removed_at", new Date() )
                )
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeCurrentIPMute( final String ip, final String removedBy, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" )
            );
            if ( PunishmentDao.useServerPunishments() )
            {
                filters.add( Filters.eq( "server", server ) );
            }
            final MongoCollection<Document> coll = db().getCollection( PunishmentType.MUTE.getTable() );

            // updateMany, this if for some reason multiple mutes would be active at the same time.
            coll.updateMany(
                Filters.and( filters ),
                Updates.combine(
                    Updates.set( "active", false ),
                    Updates.set( "removed", true ),
                    Updates.set( "removed_by", removedBy ),
                    Updates.set( "removed_at", new Date() )
                )
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getMutes( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.regex( "type", "^(?!IP.*$).*" )
            ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getMutes( final UUID uuid, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.regex( "type", "^(?!IP.*$).*" ),
                Filters.eq( "server", serverName )
            ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getMutesExecutedBy( final String name )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.eq( "executed_by", name ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getIPMutes( final String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.IPMUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "ip", ip ),
                Filters.regex( "type", "IP*" )
            ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getIPMutes( final String ip, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.IPMUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "ip", ip ),
                Filters.regex( "type", "IP*" ),
                Filters.eq( "server", serverName )
            ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getRecentMutes( final int limit )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final FindIterable<Document> documents = collection.find().limit( Math.min( limit, 200 ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getById( final String id )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final Document document = collection.find( Filters.eq( "_id", id ) ).first();

            if ( document != null )
            {
                return this.buildPunishmentInfo( document );
            }

            return null;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getByPunishmentId( final String punishmentUid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final Document document = collection.find( Filters.eq( "punishment_uid", punishmentUid ) ).first();

            if ( document != null )
            {
                return this.buildPunishmentInfo( document );
            }

            return null;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getActiveMutes( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.MUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
            ) );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getActiveIPMutes( final String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.IPMUTE.getTable() );
            final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" ) )
            );

            for ( Document document : documents )
            {
                punishments.add( this.buildPunishmentInfo( document ) );
            }
            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> isPunishmentUidFound( final String puid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
            final Document document = collection.find( Filters.eq( "punishment_uid", puid ) ).first();

            return document != null;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Integer> softDeleteSince( final String user, final String removedBy, final Date date )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "executed_by", user ),
                Filters.gte( "date", date )
            );
            final MongoCollection<Document> coll = db().getCollection( PunishmentType.MUTE.getTable() );

            return (int) coll.updateMany(
                Filters.and( filters ),
                Updates.combine(
                    Updates.set( "active", false ),
                    Updates.set( "removed", true ),
                    Updates.set( "removed_by", removedBy ),
                    Updates.set( "removed_at", new Date() )
                )
            ).getModifiedCount();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Integer> hardDeleteSince( final String user, final Date date )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "executed_by", user ),
                Filters.gte( "date", date )
            );
            final MongoCollection<Document> coll = db().getCollection( PunishmentType.MUTE.getTable() );

            return (int) coll.deleteMany( Filters.and( filters ) ).getDeletedCount();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }

    private PunishmentInfo buildPunishmentInfo( final Document document )
    {
        final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.MUTE );

        final String id = document.getObjectId( "_id" ).toString();
        final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
        final String user = document.getString( "user" );
        final String ip = document.getString( "ip" );
        final String reason = document.getString( "reason" );
        final String server = document.getString( "server" );
        final String executedby = document.getString( "executed_by" );
        final Date date = document.getDate( "date" );
        final long time = ( (Number) document.get( "duration" ) ).longValue();
        final boolean active = document.getBoolean( "active" );
        final String removedby = document.getString( "removed_by" );
        final String punishmentUid = document.getString( "punishment_uid" );

        return PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
    }
}
