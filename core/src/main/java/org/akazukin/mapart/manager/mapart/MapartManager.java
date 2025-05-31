package org.akazukin.mapart.manager.mapart;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.worldguard.WorldGuardCompat;
import org.akazukin.library.manager.PlayerManager;
import org.akazukin.library.utils.WorldUtils;
import org.akazukin.library.world.WorldData;
import org.akazukin.library.worldedit.EditSession;
import org.akazukin.library.worldedit.Vec2i;
import org.akazukin.library.worldedit.Vec3i;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dto.MapartLandDto;
import org.akazukin.mapart.doma.entity.DMapartLandCollaborator;
import org.akazukin.mapart.doma.entity.MMapartLand;
import org.akazukin.mapart.doma.entity.MMapartUser;
import org.akazukin.mapart.doma.entity.MMapartWorld;
import org.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
import org.akazukin.mapart.doma.repo.MMapartLandRepo;
import org.akazukin.mapart.doma.repo.MMapartUserRepo;
import org.akazukin.mapart.doma.repo.MMapartWorldRepo;
import org.akazukin.mapart.doma.repo.MapartLandRepo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class MapartManager {
    public final static Map<Integer, MapartManager> singletons = new ConcurrentHashMap<>();
    public final static Map<UUID, Long> LAST_DELETED = new HashMap<>();
    public final static List<UUID> CLEANING = new ArrayList<>();
    public static int MAP_SIZE = 8;

    @Getter
    final int size;
    @Getter
    final MapartWorldListener listener;
    MapartWorldData worldData;

    private MapartManager(final int size) {
        this.size = size;
        this.listener = new MapartWorldListener(this);

        MapartPlugin.getPlugin().getEventManager().registerListener(this.listener);
    }

    public static MapartManager singleton(final int size) {
        if (!MapartManager.singletons.containsKey(size)) {
            MapartManager.singletons.put(size, new MapartManager(size));
        }
        return MapartManager.singletons.get(size);
    }

    public static boolean isMapartWorld(final World world) {
        return MapartManager.singletons.values().stream().anyMatch(mgr -> {
            final World w = mgr.getWorld();
            return w != null && Objects.equals(w.getUID(), world.getUID());
        });
    }

    public World getWorld() {
        final WorldData worldData = this.getWorldData();

        final World w = WorldUtils.getWorld(this.worldData);
        if (w != null) {
            if (worldData.equalsBkt(w)) {
                return w;
            }
            this.removeWorld();
        }

        final World w3 = WorldUtils.getOrLoadWorld(worldData, this::generateWorld);

        if (w3 != null) {
            this.worldData = new MapartWorldData(w3.getUID(), w3.getName(), this.size);
        }
        return w3;
    }

    public boolean removeWorld() {
        if (this.worldData != null) {
            WorldUtils.deleteWorld(this.worldData);
            this.worldData = null;

            MapartPlugin.getPlugin().getMessageHelper().broadcast(I18n.of("library.message.world.removing"));
            MapartSQLConfig.singleton().getTransactionManager().required(() ->
                    MMapartLandRepo.selectBySize(this.size).forEach(e -> {
                        MMapartLandRepo.delete(e);
                        DMapartLandCollaboratorRepo.selectByLand(e.getLandId()).forEach(DMapartLandCollaboratorRepo::delete);
                    })
            );
            MapartPlugin.getPlugin().getMessageHelper().broadcast(I18n.of("library.message.world.removed"));
            return true;
        } else {
            MapartPlugin.getPlugin().getMessageHelper().broadcast(I18n.of("library.message.world.notFound"));
        }
        return false;
    }

    public MapartWorldData getWorldData() {
        if (this.worldData == null) {
            this.worldData = getWorldDataFromSQL(this.size);
        }
        if (this.worldData == null) {
            this.worldData = new MapartWorldData(null,
                    MapartPlugin.getPlugin()
                            .getConfigUtils().getConfig("config.yaml")
                            .getString("world") + "-x" + this.size,
                    this.size);
        }
        return this.worldData;
    }

    public static MapartWorldData getWorldDataFromSQL(final int size) {
        final MMapartWorld w = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartWorldRepo.select(size));
        if (w == null) {
            return null;
        }

        return new MapartWorldData(w.getUuid(), w.getWorldName(), size);
    }

    public World generateWorld() {
        MapartPlugin.getPlugin().getMessageHelper().broadcast(I18n.of("library.message.world.generating"));
        final World w = MapartPlugin.getPlugin().getCompat().createMapartWorld(this.getWorldData());
        if (w == null) {
            MapartPlugin.getPlugin().getMessageHelper().broadcast(I18n.of("library.message.world.generate.failed"));
            return null;
        } else {
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartWorld e = MMapartWorldRepo.select(this.size);
                if (e != null) {
                    MMapartWorldRepo.delete(e);
                }

                final MMapartWorld e2 = new MMapartWorld();
                e2.setLandSize(this.size);
                e2.setWorldName(w.getName());
                e2.setUuid(w.getUID());
                MMapartWorldRepo.save(e2);
            });
            w.setAutoSave(true);
            MapartPlugin.getPlugin().getMessageHelper().broadcast(I18n.of("library.message.world.generate.success"));
            return w;
        }
    }

    @Nullable
    public static MapartManager singleton(final UUID world) {
        return MapartManager.singletons.values().stream()
                .filter(w -> {
                    final World w2 = w.getWorld();
                    return w2 != null && Objects.equals(w2.getUID(), world);
                }).findFirst()
                .orElse(null);
    }

    public static void addCollaborator(final long landId, final UUID... players) {
        Arrays.stream(players).forEach(player -> {
            final MMapartWorld world = MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                if (MMapartUserRepo.selectByPlayer(player) == null) {
                    final MMapartUser e = new MMapartUser();
                    e.setPlayerUuid(player);
                    e.setMaxLand(null);
                    MMapartUserRepo.save(e);
                }

                final DMapartLandCollaborator entity = new DMapartLandCollaborator();
                entity.setLandId(landId);
                entity.setCollaboratorUuid(player);
                DMapartLandCollaboratorRepo.save(entity);

                return MMapartWorldRepo.select(MMapartLandRepo.selectByLand(landId).getSize());
            });
            final MMapartLand land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                    MMapartLandRepo.selectByLand(landId));

            WorldGuardCompat.addMember(
                    Bukkit.getWorld(world.getUuid()),
                    "mapart-" + land.getLocationId(),
                    player);
        });
    }

    public static MapartLandDto getLandData(final long landId) {
        return MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MapartLandRepo.selectByLand(landId));
    }

    public static void removeCollaborator(final long landId, final UUID... players) {
        Arrays.stream(players).forEach(player -> {
            final MMapartWorld world = MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final DMapartLandCollaborator collabo =
                        DMapartLandCollaboratorRepo.selectByLandAndCollaborator(landId, player);
                if (collabo != null) {
                    DMapartLandCollaboratorRepo.delete(collabo);
                }

                return MMapartWorldRepo.select(MMapartLandRepo.selectByLand(landId).getSize());
            });
            final MMapartLand land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                    MMapartLandRepo.selectByLand(landId));

            WorldGuardCompat.removeMember(
                    Bukkit.getWorld(world.getUuid()),
                    "mapart-" + land.getLocationId(),
                    player);
        });
    }

    public static boolean canRemove(final UUID player) {
        if (MapartManager.CLEANING.contains(player)) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(player, I18n.of("mapart.land.cleaningNow"));
            return false;
        }
        final long ct = MapartPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getLong("cooltime.clean");
        if (MapartManager.LAST_DELETED.containsKey(player) &&
                System.currentTimeMillis() - MapartManager.LAST_DELETED.get(player) <= ct * 1000) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(player, I18n.of("mapart.land.cleanCooltime", ct));
            return false;
        }
        return true;
    }

    public boolean isMapartWorld_(final World world) {
        final World w = this.getWorld();
        return w != null && Objects.equals(w.getUID(), world.getUID());
    }

    public MMapartLand lent(final UUID player, final String name, final int height, final int width) {
        MapartSQLConfig.singleton().getTransactionManager().required(() -> {
            if (MMapartUserRepo.selectByPlayer(player) == null) {
                final MMapartUser e = new MMapartUser();
                e.setPlayerUuid(player);
                e.setMaxLand(null);
                MMapartUserRepo.save(e);
            }
        });

        final MMapartLand landData;
        synchronized (this) {
            final int landId = MapartSQLConfig.singleton()
                    .getTransactionManager()
                    .required(MMapartLandRepo::getMissingLand);
            final int locId = MapartSQLConfig.singleton()
                    .getTransactionManager()
                    .required(() -> MMapartLandRepo.getMissingLoc(this.size));

            landData = MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand landData_ = new MMapartLand();
                landData_.setLandId(landId);
                landData_.setSize(this.size);
                landData_.setLocationId(locId);
                landData_.setOwnerUuid(player);
                landData_.setName(name);
                landData_.setHeight(height);
                landData_.setWidth(width);
                landData_.setCreateDate(Timestamp.from(Instant.now()));
                landData_.setStatus("A");

                MMapartLandRepo.save(landData_);
                return landData_;
            });
            final Vec2i loc = MapartManager.getLocation(landData.getLocationId());


            final int minY = LibraryPlugin.getPlugin().getCompat().getMinHeight(this.getWorld());
            final int maxY = this.getWorld().getMaxHeight();

            int i2 = (int) Math.sqrt(landData.getLandId());
            if (i2 != 0 && i2 % 2 == 0) {
                i2--;
            }
            if (i2 == 0) {
                i2 = 1;
            }
            i2 += 4;
            for (long j = landData.getLocationId(); j < ((long) i2 * i2); j++) {
                if (WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + j) != null) {
                    continue;
                }

                final Vec2i loc2 = MapartManager.getLocation((int) j);
                final Location maxLoc = new Location(this.getWorld(),
                        ((loc2.getX() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16,
                        maxY + 50,
                        ((loc2.getY() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16
                );
                final Location minLoc = maxLoc.clone().add(
                        -((this.size * MapartManager.MAP_SIZE) * 16),
                        0,
                        -((this.size * MapartManager.MAP_SIZE) * 16)
                );

                maxLoc.add(-1, 0, -1);
                minLoc.setY(minY - 50);


                WorldGuardCompat.createRegion("mapart-" + j, minLoc, maxLoc);

                WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + j, Flags.INTERACT, StateFlag.State.DENY);
                WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + j, Flags.USE, StateFlag.State.DENY);
                WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + j, Flags.ENTRY, StateFlag.State.DENY);

                WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + j).setPriority(10);
            }
            WorldGuardCompat.addMember(this.getWorld(), "mapart-" + landData.getLocationId(), player);

            final Location maxLoc = new Location(this.getWorld(),
                    ((loc.getX() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16,
                    maxY + 10,
                    ((loc.getY() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16
            );
            final Location minLoc = maxLoc.clone().add(
                    -((landData.getHeight() * MapartManager.MAP_SIZE) * 16),
                    0,
                    -((landData.getWidth() * MapartManager.MAP_SIZE) * 16)
            );

            maxLoc.add(-1, 0, -1);
            minLoc.setY(minY + 1);

            WorldGuardCompat.createRegion("mapart-" + landData.getLocationId() + "-area", maxLoc, minLoc);
            WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + landData.getLocationId() + "-area", Flags.EXIT,
                    StateFlag.State.DENY);
            WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + landData.getLocationId() + "-area").setPriority(9);
        }

        return landData;
    }

    public static Vec2i getLocation(final long locId) {
        long i2 = (int) Math.floor(Math.sqrt(locId));
        if (i2 != 0 && i2 % 2 == 0) {
            i2--;
        }
        final long i3 = locId - (i2 * i2);
        final long pos = (i2 + 1) / 2;
        long x = pos;
        long z = 0;
        for (long n = 0; n < i3; n++) {
            if (n < (pos)) {
                z--;
            } else if (n < (pos * 3)) {
                x--;
            } else if (n < ((pos * 5))) {
                z++;
            } else if (n < ((pos * 7))) {
                x++;
            } else {
                z--;
            }
        }
        return new Vec2i((int) x, (int) z);
    }

    public void deleteLand(final long locId, final Runnable doLast) {
        this.cleanLand(locId, doLast);

        WorldGuardCompat.removeAllMembers(this.getWorld(), "mapart-" + locId);

        synchronized (this) {
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand land = MMapartLandRepo.selectBySizeAndLocation(this.size, locId);
                if (land == null) {
                    return;
                }

                MMapartLandRepo.delete(land);
                final List<DMapartLandCollaborator> collabos = DMapartLandCollaboratorRepo.selectByLand(land.getLandId());
                for (final DMapartLandCollaborator collabo : collabos) {
                    DMapartLandCollaboratorRepo.delete(collabo);
                }
            });
        }

        final ProtectedRegion rg = WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + locId);
        rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())).forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));

        WorldGuardCompat.removeRegion(this.getWorld(), "mapart-" + locId + "-area");
    }

    public void cleanLand(final long locId, final Runnable doLast) {
        Bukkit.getScheduler().runTaskAsynchronously(MapartPlugin.getPlugin(), () -> {
            this.resetLand(locId);
            doLast.run();
        });
    }

    public void resetLand(final long locId) {
        final Vec2i loc = MapartManager.getLocation(locId);
        final World w = this.getWorld();
        final int min = LibraryPlugin.getPlugin().getCompat().getMinHeight(w);

        final Vec3i maxLoc_ = new Vec3i(
                ((loc.getX() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16,
                Math.min(w.getMaxHeight() - 1,
                        min + MapartPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getInt("land.height")),
                ((loc.getY() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16
        );

        final Vec3i minLoc_ = maxLoc_.clone();
        minLoc_.plus(
                -(this.size * MapartManager.MAP_SIZE) * 16,
                0,
                -(this.size * MapartManager.MAP_SIZE) * 16
        );
        maxLoc_.plus(-1, 0, -1);
        minLoc_.setY(min + 1);

        System.out.println("EditSession: " + minLoc_ + " " + maxLoc_);

        final EditSession session = new EditSession(
                MapartPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getInt("thread"),
                w);
        session.setBlock(
                maxLoc_, minLoc_,
                LibraryPlugin.getPlugin().getCompat().getNMSNewBlockData(Material.AIR, (byte) 0)
        );
        session.complete();
    }

    public boolean teleportLand(final long locId, final Player player, final boolean isForce) {
        if (!isForce) {
            final long lastDmg = PlayerManager.SINGLETON.getLastDamageTick(player);
            final long lastPos = PlayerManager.SINGLETON.getLastPosTick(player);
            final long lastInteract = PlayerManager.SINGLETON.getLastInteractTick(player);

            final int sec = MapartPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getInt("cooltime.teleport");

            if (lastDmg != -1 && lastDmg <= sec * 20L) {
                MapartPlugin.getPlugin().getMessageHelper().sendMessage(player, I18n.of("library.message.teleport.combating", sec));
                return false;
            } else if ((lastPos != -1 && lastPos <= sec * 20L) ||
                    (lastInteract != -1 && lastInteract <= sec * 20L)) {
                MapartPlugin.getPlugin().getMessageHelper().sendMessage(player, I18n.of("library.message.teleport.dontMove", sec));
                return false;
            }
        }

        final World w = this.getWorld();
        if (w == null) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(player, I18n.of("library.message.world.notFound"));
            return false;
        }

        MapartPlugin.getPlugin().getMessageHelper().sendMessage(player, I18n.of("library.message.teleporting"));

        final Vec2i loc = MapartManager.getLocation(locId);
        player.teleport(
                new Location(
                        w,
                        ((loc.getX() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16 - 0.5,
                        LibraryPlugin.getPlugin().getCompat().getMinHeight(w) + 1,
                        ((loc.getY() * (this.size * MapartManager.MAP_SIZE)) - 4) * 16 - 0.5
                )
        );

        return true;
    }
}
