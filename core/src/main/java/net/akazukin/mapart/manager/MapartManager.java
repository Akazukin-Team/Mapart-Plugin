package net.akazukin.mapart.manager;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nullable;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.worldedit.ChancePattern;
import net.akazukin.library.compat.worldedit.WorldEditCompat;
import net.akazukin.library.compat.worldguard.WorldGuardCompat;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.manager.PlayerManager;
import net.akazukin.library.utils.FileUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dto.MapartLandDto;
import net.akazukin.mapart.doma.entity.DMapartLandCollaborator;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.entity.MMapartUser;
import net.akazukin.mapart.doma.entity.MMapartWorld;
import net.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
import net.akazukin.mapart.doma.repo.MMapartUserRepo;
import net.akazukin.mapart.doma.repo.MMapartWorldRepo;
import net.akazukin.mapart.doma.repo.MapartLandRepo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapartManager implements Listenable {
    public final static Map<Long, MapartManager> singletons = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(MapartManager.class);
    @Getter
    private static final Map<UUID, Location> lastPos = new HashMap<>();
    public static int MAP_SIZE = 8;

    @Getter
    private final long size;

    private MapartManager(final long size) {
        this.size = size;

        MapartPlugin.EVENT_MANAGER.registerListener(this);
    }

    @Nullable
    public static MapartManager singleton(final long size) {
        if (!MapartManager.singletons.containsKey(size)) MapartManager.singletons.put(size, new MapartManager(size));
        return MapartManager.singletons.get(size);
    }

    public static boolean isMapartWorld(final World world) {
        return MapartManager.singletons.values().stream().anyMatch(mgr -> {
            final World w = mgr.getWorld();
            return w != null && Objects.equals(w.getUID(), world.getUID());
        });
    }

    public World getWorld() {
        final MMapartWorld w = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartWorldRepo.select(this.size));
        if (w == null || w.getUuid() == null) return null;
        return Bukkit.getWorld(w.getUuid());
    }

    @Nullable
    public static MapartManager singleton(final UUID world) {
        final Optional<MapartManager> opt = singletons.values().stream()
                .filter(w -> {
                    final World w2 = w.getWorld();
                    return w2 != null && Objects.equals(w2.getUID(), world);
                }).findFirst();
        return opt.orElse(null);
    }

    public static void addCollaborator(final int landId, final UUID... players) {
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

                return MMapartWorldRepo.select(MMapartLandRepo.select(landId).getSize());
            });

            WorldGuardCompat.addMember(
                    Bukkit.getWorld(world.getUuid()),
                    "mapart-" + landId,
                    player);
        });
    }

    public static MapartLandDto getLandData(final long landId) {
        return MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MapartLandRepo.selectByLand(landId));
    }

    public static void removeCollaborator(final int landId, final UUID... players) {
        Arrays.stream(players).forEach(player -> {
            final MMapartWorld world = MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final DMapartLandCollaborator collabo =
                        DMapartLandCollaboratorRepo.selectByLandAndCollaborator(landId, player);
                if (collabo != null) {
                    DMapartLandCollaboratorRepo.delete(collabo);
                }

                return MMapartWorldRepo.select(MMapartLandRepo.select(landId).getSize());
            });

            WorldGuardCompat.removeMember(
                    Bukkit.getWorld(world.getUuid()),
                    "mapart-" + landId,
                    player);
        });
    }

    public String getWorldName() {
        return MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getString("world") + "-x" + this.size;
    }

    public void addProtectedRegion(final String name, final Location min, final Location max) {
        WorldGuardCompat.createRegion(name, min, max);

        WorldGuardCompat.addFlag(this.getWorld(), name, Flags.INTERACT, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(this.getWorld(), name, Flags.USE, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(this.getWorld(), name, Flags.ITEM_FRAME_ROTATE, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(this.getWorld(), name, Flags.ENTRY, StateFlag.State.DENY);
    }

    public World generateWorld() {
        if (this.getWorld() == null) {
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.generating"));

            final World world = MapartPlugin.COMPAT.createMapartWorld(this);
            if (world == null) {
                MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.generate.failed"));
            } else {
                MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                    final MMapartWorld e = MMapartWorldRepo.select(this.size);
                    if (e != null) MMapartWorldRepo.delete(e);

                    final MMapartWorld e2 = new MMapartWorld();
                    e2.setLandSize(this.size);
                    e2.setWorldName(world.getName());
                    e2.setUuid(world.getUID());
                    MMapartWorldRepo.save(e2);
                });
                world.setAutoSave(true);
                MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.generate.success"));
                return world;
            }
        } else {
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.alreadyExists"));
        }
        return null;
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

        final int landId =
                MapartSQLConfig.singleton().getTransactionManager().required(MMapartLandRepo::getMissingLand);
        final int locId = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartLandRepo.getMissingLoc(this.size));
        final int[] loc = MapartManager.getLocation(locId);

        final MMapartLand landData = MapartSQLConfig.singleton().getTransactionManager().required(() -> {
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


        final int minY = LibraryPlugin.COMPAT.getMinHeight(this.getWorld());
        final int maxY = this.getWorld().getMaxHeight();

        int i2 = (int) Math.sqrt(landData.getLandId());
        if (i2 != 0 && i2 % 2 == 0) i2--;
        if (i2 == 0) i2 = 1;
        i2 += 4;
        for (long j = landData.getLocationId(); j < ((long) i2 * i2); j++) {
            if (WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + j) != null) continue;

            final int[] loc2 = MapartManager.getLocation((int) j);
            final Location maxLoc = new Location(this.getWorld(),
                    ((loc2[0] * (this.size * MapartManager.MAP_SIZE)) - 4) * 16,
                    maxY + 50,
                    ((loc2[1] * (this.size * MapartManager.MAP_SIZE)) - 4) * 16
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
            WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + j, Flags.ITEM_FRAME_ROTATE, StateFlag.State.DENY);
            WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + j, Flags.ENTRY, StateFlag.State.DENY);
            WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + j, Flags.ITEM_PICKUP, StateFlag.State.DENY);

            WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + j).setPriority(10);
        }
        WorldGuardCompat.addMember(this.getWorld(), "mapart-" + locId, player);

        final Location maxLoc = new Location(this.getWorld(),
                ((loc[0] * (this.size * MapartManager.MAP_SIZE)) - 4) * 16,
                maxY + 10,
                ((loc[1] * (this.size * MapartManager.MAP_SIZE)) - 4) * 16
        );
        final Location minLoc = maxLoc.clone().add(
                -((landData.getHeight() * MapartManager.MAP_SIZE) * 16),
                0,
                -((landData.getWidth() * MapartManager.MAP_SIZE) * 16)
        );

        maxLoc.add(-1, 0, -1);
        minLoc.setY(minY + 1);

        WorldGuardCompat.createRegion("mapart-" + locId + "-area", maxLoc, minLoc);
        WorldGuardCompat.addFlag(this.getWorld(), "mapart-" + locId + "-area", Flags.EXIT,
                StateFlag.State.DENY);
        WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + locId + "-area").setPriority(9);

        return landData;
    }

    public static int[] getLocation(final long locId) {
        long i2 = (int) Math.floor(Math.sqrt(locId));
        if (i2 != 0 && i2 % 2 == 0) i2--;
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
        return new int[]{(int) x, (int) z};
    }

    public void deleteLand(final int locId, final Runnable doLast) {
        WorldGuardCompat.removeAllMembers(this.getWorld(), "mapart-" + locId);

        MapartSQLConfig.singleton().getTransactionManager().required(() -> {
            final MMapartLand land = MMapartLandRepo.select(locId);
            if (land != null) MMapartLandRepo.delete(land);

            final List<DMapartLandCollaborator> collabos = DMapartLandCollaboratorRepo.selectByLand(locId);
            for (final DMapartLandCollaborator collabo : collabos) {
                DMapartLandCollaboratorRepo.delete(collabo);
            }
        });

        final ProtectedRegion rg = WorldGuardCompat.getRegion(this.getWorld(), "mapart-" + locId);
        rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())).forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));

        WorldGuardCompat.removeRegion(this.getWorld(), "mapart-" + locId + "-area");

        this.cleanLand(locId, doLast);
    }

    public void cleanLand(final int locId, final Runnable doLast) {
        Bukkit.getScheduler().runTaskAsynchronously(MapartPlugin.getPlugin(), () -> {
            this.resetLand(locId);
            doLast.run();
        });
    }

    public void resetLand(final int locId) {
        final int[] loc = MapartManager.getLocation(locId);
        final int minY = LibraryPlugin.COMPAT.getMinHeight(this.getWorld());
        final int maxY = this.getWorld().getMaxHeight();
        final ExecutorService pool =
                Executors.newFixedThreadPool(MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("workers" +
                        ".thread"));
        for (int i = 0; i < this.size * MapartManager.MAP_SIZE; i++) {
            for (int i2 = 0; i2 < this.size * MapartManager.MAP_SIZE; i2++) {
                if (!this.getWorld().isChunkLoaded(loc[0] + i, loc[1] + i2)) continue;
                final int finalI = i;
                final int finalI1 = i2;
                pool.execute(() -> {

                    final Location maxLoc = new Location(this.getWorld(),
                            ((loc[0] * (this.size * MapartManager.MAP_SIZE)) - 4 - finalI) * 16,
                            maxY,
                            ((loc[1] * (this.size * MapartManager.MAP_SIZE)) - 4 - finalI1) * 16
                    );
                    final Location minLoc = maxLoc.clone().add(
                            -16,
                            0,
                            -16
                    );

                    maxLoc.add(-1, 0, -1);
                    minLoc.setY(minY + 1);

                    WorldEditCompat.fill(
                            minLoc, maxLoc,
                            new ChancePattern(Material.AIR.createBlockData(), 1)
                    );
                });
            }
        }
        pool.shutdown();
        try {
            pool.awaitTermination(30L, TimeUnit.MINUTES);
        } catch (final InterruptedException e) {
            MapartPlugin.getLogManager().log(Level.SEVERE, "Failed to reset land", e);
        }
    }

    public void teleportLand(final long landId, final UUID player, final boolean isForce) {
        final Player p = Bukkit.getPlayer(player);
        if (p == null) return;

        if (!isForce && MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("teleport.cooltime") != -1) {
            final long lastDmg = PlayerManager.SINGLETON.getLastDamageTick(player);
            final long lastMoved = PlayerManager.SINGLETON.getLastMovedTick(player);
            final long lastPos = PlayerManager.SINGLETON.getLastPosTick(player);
            final long lastRot = PlayerManager.SINGLETON.getLastRotatedTick(player);
            final long lastInteract = PlayerManager.SINGLETON.getLastInteractTick(player);
            if (lastDmg != -1 && lastDmg <= 10 * 20) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("library.message.teleport.combating"));
                return;
            } else if (lastMoved != -1 && lastMoved <= 10 * 20 ||
                    lastInteract != -1 && lastInteract <= 10 * 20) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("library.message.teleport.dontMove"));
                return;
            }
        }

        final int[] loc = MapartManager.getLocation(landId);

        MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("library.message.teleporting"));
        Bukkit.getPlayer(player).teleport(
                new Location(
                        this.getWorld(),
                        ((loc[0] * (this.size * MapartManager.MAP_SIZE)) - 4) * 16 - 0.5,
                        LibraryPlugin.COMPAT.getMinHeight(this.getWorld()) + 1,
                        ((loc[1] * (this.size * MapartManager.MAP_SIZE)) - 4) * 16 - 0.5
                )
        );
    }

    public boolean removeWorld() {
        final World world = this.getWorld();
        if (world != null) {
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.removing"));
            Bukkit.unloadWorld(world, false);
            FileUtils.delete(world.getWorldFolder());
            WorldGuardCompat.removeRegion(world);
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                MMapartLandRepo.selectAll().forEach(MMapartLandRepo::delete);
                DMapartLandCollaboratorRepo.selectAll().forEach(DMapartLandCollaboratorRepo::delete);
            });
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.removed"));
            return true;
        } else {
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.notFound"));
        }
        return false;
    }

    @EventTarget
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (this.getWorld() == null || event.getPlayer().getWorld().getUID() != this.getWorld().getUID()) return;
        event.getPlayer().setAllowFlight(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (this.getWorld() == null || event.getEntered().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (this.getWorld() == null) return;

        if (event.getPlayer().getWorld().getUID() == this.getWorld().getUID()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT ||
                    event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);
            } else {
                MapartManager.lastPos.remove(event.getPlayer().getUniqueId());
            }
        } else if (event.getFrom().getWorld().getUID() != this.getWorld().getUID()) {
            MapartManager.lastPos.put(event.getPlayer().getUniqueId(), event.getFrom());
        }
    }

    @EventTarget
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        if (this.getWorld() == null) return;

        if (event.getFrom().getUID() == this.getWorld().getUID()) {
            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                event.getPlayer().setFlying(false);
                event.getPlayer().setAllowFlight(false);
            }
        } else if (event.getPlayer().getWorld().getUID() == this.getWorld().getUID()) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        if (event.getBlock().getY() == LibraryPlugin.COMPAT.getMinHeight(this.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockExplode(final ExplosionPrimeEvent event) {
        if (this.getWorld() == null || event.getEntity().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (this.getWorld() == null || event.getEntity().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (this.getWorld() == null || event.getEntity().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    /*@EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockRedstone(final BlockRedstoneEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setNewCurrent(event.getOldCurrent());
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockPiston(final BlockPistonEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }*/

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockFrom(final BlockFormEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityPlace(final EntityPlaceEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (this.getWorld() == null || event.getClickedBlock() == null || event.getClickedBlock().getWorld().getUID() != this.getWorld().getUID())
            return;
        if (!event.isCancelled()) return;
        MapartManager.onPlayerInteract_(event);
    }

    public static void onPlayerInteract_(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Class<?> data;
            try {
                data = event.getClickedBlock().getBlockData().getMaterial().getData();
            } catch (final IllegalArgumentException ignore) {
                data = event.getClickedBlock().getBlockData().getMaterial().data;
            }

            switch (data.getName()) {
                case "": {
                    event.setCancelled(true);
                    return;
                }
            }

            switch (event.getClickedBlock().getBlockData().getMaterial().name()) {
                case "ANVIL":
                case "CHIPPED_ANVIL":
                case "DAMAGED_ANVIL": {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockCanBuild(final BlockCanBuildEvent event) {
        if (this.getWorld() == null || event.getBlock().getWorld().getUID() != this.getWorld().getUID()) return;
        MapartManager.onBlockCanBuild_(event);
    }

    private static void onBlockCanBuild_(final BlockCanBuildEvent event) {
        if (!event.isBuildable()) return;

        Class<?> data;
        try {
            data = event.getMaterial().getData();
        } catch (final IllegalArgumentException ignore) {
            data = event.getMaterial().data;
        }

        if (data != null)
            switch (data.getName()) {
                case "org.bukkit.material.Sign":
                case "org.bukkit.block.data.type.Sign":
                case "org.bukkit.block.data.type.WallSign":
                case "org.bukkit.block.data.type.WallHangingSign":
                case "org.bukkit.block.data.type.HangingSign":

                case "org.bukkit.material.Banner":
                case "org.bukkit.block.Banner":

                case "org.bukkit.material.Crops":
                case "org.bukkit.block.data.type.Bamboo":
                case "org.bukkit.block.data.Ageable":
                case "org.bukkit.material.Vine":
                case "org.bukkit.block.data.type.CaveVinesPlant":
                case "org.bukkit.block.data.type.CaveVines":
                case "org.bukkit.material.NetherWarts":

                case "org.bukkit.material.Sapling":
                case "org.bukkit.block.data.type.Sapling":

                case "org.bukkit.block.data.type.Stairs":
                case "org.bukkit.block.data.type.Slab":

                case "org.bukkit.material.Furnace":
                case "org.bukkit.block.data.type.Furnace":
                case "org.bukkit.block.data.type.Campfire":
                case "org.bukkit.block.data.type.Fire":

                case "org.bukkit.block.data.type.BrewingStand":
                case "org.bukkit.block.data.type.Candle":
                case "org.bukkit.block.data.Lightable":

                case "org.bukkit.material.RedstoneWire":
                case "org.bukkit.block.data.type.RedstoneWire":

                case "org.bukkit.material.Hopper":
                case "org.bukkit.block.data.type.Hopper":

                case "org.bukkit.block.data.type.Observer":
                case "org.bukkit.material.Observer":

                case "org.bukkit.block.data.type.TrapDoor":
                case "org.bukkit.block.data.type.Gate":
                case "org.bukkit.block.data.type.Fence":

                case "org.bukkit.block.data.type.GlassPane":

                case "org.bukkit.block.data.type.Comparator":
                case "org.bukkit.block.data.type.Piston":
                case "org.bukkit.block.data.type.Repeater":
                case "org.bukkit.block.data.type.Dispenser":
                case "org.bukkit.block.data.type.Switch":
                case "org.bukkit.block.data.type.DaylightDetector":
                case "org.bukkit.block.data.type.LightningRod":
                case "org.bukkit.block.data.type.NoteBlock":
                case "org.bukkit.block.data.Powerable":
                case "org.bukkit.block.data.AnaloguePowerable":
                case "org.bukkit.material.Leaves":
                case "org.bukkit.material.Dispenser":
                case "org.bukkit.material.PoweredRail":
                case "org.bukkit.material.DetectorRail":
                case "org.bukkit.material.PistonBaseMaterial":
                case "org.bukkit.material.PistonExtensionMaterial":
                case "org.bukkit.material.TripwireHook":
                case "org.bukkit.material.Tripwire":
                case "org.bukkit.material.Comparator":
                case "org.bukkit.material.PressurePlate":
                case "org.bukkit.material.RedstoneTorch":
                case "org.bukkit.material.Button":
                case "org.bukkit.block.data.type.RedstoneRail":
                case "org.bukkit.block.data.Rail":
                case "org.bukkit.material.TrapDoor":
                case "org.bukkit.material.Gate":

                case "org.bukkit.block.data.type.AmethystCluster":

                case "org.bukkit.material.LongGrass":
                case "org.bukkit.material.Torch":
                case "org.bukkit.material.Stairs":
                case "org.bukkit.material.Ladder":

                case "org.bukkit.block.data.type.Barrel":
                case "org.bukkit.material.Chest":
                case "org.bukkit.block.data.type.Chest":
                case "org.bukkit.block.data.type.EnderChest":
                case "org.bukkit.material.EnderChest":

                case "org.bukkit.material.Door":
                case "org.bukkit.block.data.type.Door":

                case "org.bukkit.material.Bed":
                case "org.bukkit.block.data.type.Bed": {
                    event.setBuildable(false);
                    return;
                }
            }

        switch (event.getMaterial().name()) {
            case "BEACON":
            case "LAVA":
            case "WATER":

            case "BLACK_BANNER":
            case "BLACK_WALL_BANNER":
            case "BLUE_BANNER":
            case "BLUE_WALL_BANNER":
            case "CYAN_BANNER":
            case "CYAN_WALL_BANNER":
            case "GRAY_BANNER":
            case "GRAY_WALL_BANNER":
            case "GREEN_BANNER":
            case "GREEN_WALL_BANNER":
            case "LIGHT_BLUE_BANNER":
            case "LIGHT_BLUE_WALL_BANNER":
            case "LIGHT_GRAY_BANNER":
            case "LIGHT_GRAY_WALL_BANNER":
            case "LIME_BANNER":
            case "LIME_WALL_BANNER":
            case "MAGENTA_BANNER":
            case "MAGENTA_WALL_BANNER":
            case "PINK_BANNER":
            case "PINK_WALL_BANNER":
            case "YELLOW_BANNER":
            case "YELLOW_WALL_BANNER":
            case "WHITE_BANNER":
            case "WHITE_WALL_BANNER":
            case "PURPLE_BANNER":
            case "PURPLE_WALL_BANNER":
            case "ORANGE_BANNER":
            case "ORANGE_WALL_BANNER":
            case "BARREL":

            /*case "ACACIA_SIGN":
            case "ACACIA_WALL_SIGN":
            case "OAK_SIGN":
            case "OAK_WALL_SIGN":
            case "SPRUCE_SIGN":
            case "SPRUCE_WALL_SIGN":
            case "BIRCH_SIGN":
            case "BIRCH_WALL_SIGN":
            case "CRIMSON_SIGN":
            case "CRIMSON_WALL_SIGN":
            case "DARK_OAK_SIGN":
            case "DARK_OAK_WALL_SIGN":
            case "JUNGLE_SIGN":
            case "JUNGLE_WALL_SIGN":
            case "WARPED_SIGN":
            case "WARPED_WALL_SIGN":*/
            {
                event.setBuildable(false);
            }
        }
    }

    @EventTarget
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (this.getWorld() == null || event.getPlayer().getWorld().getUID() != this.getWorld().getUID()) return;
        MapartManager.onPlayerQuit_(event);
    }

    private static void onPlayerQuit_(final PlayerQuitEvent event) {
        if (MapartManager.lastPos.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().teleport(MapartManager.lastPos.get(event.getPlayer().getUniqueId()));
            MapartManager.lastPos.remove(event.getPlayer().getUniqueId());
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
