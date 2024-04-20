package net.akazukin.mapart.manager;

import ac.grim.grimac.api.events.FlagEvent;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
import net.akazukin.mapart.doma.entity.DMapartLandCollaborator;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MapartManager implements Listenable {
    public final static MapartManager SINGLETON = new MapartManager();

    private static final Logger log = LoggerFactory.getLogger(MapartManager.class);

    @Getter
    private final Map<UUID, Location> lastPos = new HashMap<>();

    public static World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public static String getWorldName() {
        return MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getString("world");
    }

    public static boolean removeWorld() {
        final World world = getWorld();
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
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.notfound"));
        }
        return false;
    }

    public static World generateWorld() {
        if (getWorld() == null) {
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.generating"));

            final World world = MapartPlugin.COMPAT.createMapartWorld();
            if (world == null || getWorld() == null) {
                MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.generate.failed"));
            } else {
                world.setAutoSave(true);
                MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.generate.success"));
                return world;
            }
        }
        MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.alreadyExists"));
        return null;
    }

    public static MMapartLand lent(final UUID player, final String name, final int height, final int width) {
        return MapartSQLConfig.singleton().getTransactionManager().required(() -> {
            final List<Integer> landIds = MMapartLandRepo.selectAll().stream().map(MMapartLand::getLandId).collect(Collectors.toList());
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if (landIds.contains(i)) continue;
                final int[] loc = getLocation(i);

                final MMapartLand landData = new MMapartLand();
                landData.setLandId(i);
                landData.setOwnerUuid(player);
                landData.setName(name);
                landData.setX(loc[0]);
                landData.setZ(loc[1]);
                landData.setHeight(height);
                landData.setWidth(width);
                landData.setCreateDate(Timestamp.from(Instant.now()));
                landData.setStatus("A");

                MMapartLandRepo.save(landData);


                final int minY = LibraryPlugin.COMPAT.getMinHeight(getWorld());
                final int maxY = getWorld().getMaxHeight();

                int i2 = (int) Math.sqrt(landData.getLandId());
                if (i2 != 0 && i2 % 2 == 0) i2--;
                if (i2 == 0) i2 = 1;
                i2 += 4;
                for (int j = landData.getLandId(); j < (i2 * i2); j++) {
                    if (WorldGuardCompat.getRegion(getWorld(), "mapart-" + j) != null) continue;

                    final int[] loc2 = getLocation(j);
                    final Location minLoc = new Location(getWorld(), ((loc2[0] * 16) - 4) * 16, minY, ((loc2[1] * 16) - 4) * 16);
                    final Location maxLoc = minLoc.clone().add(((2 * 8) * 16) - 1, 0, ((2 * 8) * 16) - 1);
                    final Location minLoc2 = minLoc.clone();
                    final Location maxLoc2 = maxLoc.clone();
                    minLoc2.setY(maxY);
                    maxLoc2.setY(maxY);

                    addProtectedRegion("mapart-" + j, minLoc.add(0, 1, 0), maxLoc2.add(0, 20, 0));
                    WorldGuardCompat.getRegion(getWorld(), "mapart-" + j).setPriority(10);
                }
                WorldGuardCompat.addMember(getWorld(), "mapart-" + i, player);

                final Location minLoc = new Location(getWorld(), ((loc[0] * 16) + 12) * 16 - 1, minY + 1, ((loc[1] * 16) + 12) * 16 - 1);
                final Location maxLoc = minLoc.clone().add(((landData.getHeight() * -8) * 16) + 1, 0, ((landData.getWidth() * -8) * 16) + 1);
                maxLoc.setY(maxY + 20);
                WorldGuardCompat.createRegion("mapart-" + i + "-area", maxLoc, minLoc);
                WorldGuardCompat.addFlag(getWorld(), "mapart-" + i + "-area", Flags.EXIT, StateFlag.State.DENY);
                WorldGuardCompat.getRegion(getWorld(), "mapart-" + i + "-area").setPriority(9);

                return landData;
            }
            return null;
        });
    }

    public static int[] getLocation(final int landId) {
        int i2 = (int) Math.floor(Math.sqrt(landId));
        if (i2 != 0 && i2 % 2 == 0) i2--;
        final int i3 = landId - (i2 * i2);
        final int pos = (i2 + 1) / 2;
        int x = pos;
        int z = 0;
        for (int n = 0; n < i3; n++) {
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
        return new int[]{x, z};
    }

    public static void addProtectedRegion(final String name, final Location min, final Location max) {
        WorldGuardCompat.createRegion(name, min, max);

        WorldGuardCompat.addFlag(getWorld(), name, Flags.INTERACT, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.USE, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.ITEM_FRAME_ROTATE, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.ENTRY, StateFlag.State.DENY);
    }

    public static void deleteLand(final int landId, final Runnable doLast) {
        WorldGuardCompat.removeAllMembers(MapartManager.getWorld(), "mapart-" + landId);

        MapartSQLConfig.singleton().getTransactionManager().required(() -> {
            final MMapartLand land = MMapartLandRepo.select(landId);
            if (land != null) MMapartLandRepo.delete(land);

            final List<DMapartLandCollaborator> collabos = DMapartLandCollaboratorRepo.selectByLand(landId);
            for (final DMapartLandCollaborator collabo : collabos) {
                DMapartLandCollaboratorRepo.delete(collabo);
            }
        });

        final ProtectedRegion rg = WorldGuardCompat.getRegion(MapartManager.getWorld(), "mapart-" + landId);
        rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())).forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));

        WorldGuardCompat.removeRegion(MapartManager.getWorld(), "mapart-" + landId + "-area");

        cleanLand(landId, doLast);
    }

    public static void cleanLand(final int landId, final Runnable doLast) {
        Bukkit.getScheduler().runTaskAsynchronously(MapartPlugin.getPlugin(), () -> {
            resetLand(landId);
            doLast.run();
        });
    }

    public static void resetLand(final int landId) {
        final int[] loc = getLocation(landId);
        final ExecutorService pool = Executors.newFixedThreadPool(MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("workers.thread"));
        for (int i = 0; i < 16; i++) {
            for (int i2 = 0; i2 < 16; i2++) {
                if (!MapartManager.getWorld().isChunkLoaded(loc[0] + i, loc[1] + i2)) continue;
                final int finalI = i;
                final int finalI1 = i2;
                pool.execute(() -> {
                    final Location minLoc = new Location(
                            MapartManager.getWorld(),
                            ((loc[0] * 16) + finalI - 4) * 16,
                            LibraryPlugin.COMPAT.getMinHeight(MapartManager.getWorld()) + 1,
                            ((loc[1] * 16) + finalI1 - 4) * 16
                    );
                    final Location maxLoc = minLoc.clone().add(15, 0, 15);
                    maxLoc.setY(MapartManager.getWorld().getMaxHeight());

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

    public static void teleportLand(final int landId, final UUID player, final boolean isForce) {
        final Player p = Bukkit.getPlayer(player);
        if (p == null) return;

        if (!isForce) {
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

        final int[] loc = getLocation(landId);

        MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("library.message.teleporting"));
        Bukkit.getPlayer(player).teleport(
                new Location(
                        MapartManager.getWorld(),
                        ((loc[0] * 16) + 12) * 16 - 0.5,
                        LibraryPlugin.COMPAT.getMinHeight(MapartManager.getWorld()) + 1,
                        ((loc[1] * 16) + 12) * 16 - 0.5
                )
        );
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onGrimACFlagEvent(final FlagEvent event) {
        if (Bukkit.getPlayer(event.getPlayer().getUniqueId()).getWorld().getUID() != getWorld().getUID()) return;

        switch (event.getCheck().getCheckName()) {
            case "GroundSpoof":
            case "Simulation":
            case "PositionPlace":
            case "Post": {
                event.setCancelled(true);
            }
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onTownClaimEvent(final TownPreClaimEvent event) {
        if (event.getTownBlock().getWorld().getBukkitWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getUID() == getWorld().getUID()) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (event.getEntered().getWorld().getUID() != getWorld().getUID()) return;
        event.setCancelled(true);
    }

    @EventTarget
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getPlayer().getWorld().getUID() == getWorld().getUID()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT ||
                    event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
            ) {
                event.setCancelled(true);
            } else {
                this.lastPos.remove(event.getPlayer().getUniqueId());
            }
        } else if (event.getFrom().getWorld().getUID() != getWorld().getUID()) {
            this.lastPos.put(event.getPlayer().getUniqueId(), event.getFrom());
        }
    }

    @EventTarget
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        if (event.getFrom().getUID() == getWorld().getUID()) {
            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                event.getPlayer().setFlying(false);
                event.getPlayer().setAllowFlight(false);
            }
        } else if (event.getPlayer().getWorld().getUID() == getWorld().getUID()) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        if (event.getBlock().getY() == LibraryPlugin.COMPAT.getMinHeight(getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockExplode(final ExplosionPrimeEvent event) {
        if (event.getEntity().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (event.getEntity().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity().getWorld().getUID() != getWorld().getUID()) return;

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
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onBlockFrom(final BlockFormEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityPlace(final EntityPlaceEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.isCancelled()) return;

        if (event.getClickedBlock() != null &&
                event.getClickedBlock().getWorld().getUID() != getWorld().getUID() &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK
        ) {
            Class<?> data = null;
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
        if (event.getBlock().getWorld().getUID() != getWorld().getUID()) return;
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
        if (event.getPlayer().getWorld().getUID() != getWorld().getUID()) return;

        if (this.lastPos.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().teleport(this.lastPos.get(event.getPlayer().getUniqueId()));
            this.lastPos.remove(event.getPlayer().getUniqueId());
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
