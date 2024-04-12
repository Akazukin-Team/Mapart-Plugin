package net.akazukin.mapart.mapart;

import ac.grim.grimac.api.events.FlagEvent;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.worldedit.ChancePattern;
import net.akazukin.library.compat.worldedit.WorldEditCompat;
import net.akazukin.library.compat.worldguard.WorldGuardCompat;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.FileUtils;
import net.akazukin.library.utils.TaskUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.DMapartLandCollaboratorDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartLandDaoImpl;
import net.akazukin.mapart.doma.entity.DMapartLandCollaborator;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.seasar.doma.jdbc.tx.TransactionManager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MapartManager implements Listenable {
    public static World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public static String getWorldName() {
        return "mapart";
    }

    public static boolean removeWorld() {
        final World world = getWorld();
        if (world != null) {
            MapartPlugin.MESSAGE_HELPER.broadcast(I18n.of("library.message.world.removing"));
            Bukkit.unloadWorld(world, false);
            FileUtils.delete(world.getWorldFolder());
            WorldGuardCompat.removeRegion(world);
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                new MMapartLandDaoImpl(MapartSQLConfig.singleton()).deleteAll();
                new DMapartLandCollaboratorDaoImpl(MapartSQLConfig.singleton()).deleteAll();
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
        final TransactionManager tm = MapartSQLConfig.singleton().getTransactionManager();
        return TaskUtils.addSynchronizedTask(() -> tm.required(() -> {
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

                    //addProtectedRegion("mapart-" + j + "-ground", minLoc, maxLoc);
                    addProtectedRegion("mapart-" + j, minLoc.add(0, 1, 0), maxLoc2.add(0, 20, 0));
                    //addProtectedRegion("mapart-" + j + "-sky", minLoc2.add(0, 1, 0), maxLoc2.add(0, 1, 0));
                    //WorldGuardCompat.getRegion(getWorld(), "mapart-" + j + "-ground").setPriority(10);
                    WorldGuardCompat.getRegion(getWorld(), "mapart-" + j).setPriority(10);
                }
                WorldGuardCompat.addMember(getWorld(), "mapart-" + i, player);

                final Location minLoc = new Location(getWorld(), ((loc[0] * 16) + 12) * 16 - 1, minY + 1, ((loc[1] * 16) + 12) * 16 - 1);
                final Location maxLoc = minLoc.clone().add(((landData.getHeight() * -8) * 16) + 1, 0, ((landData.getWidth() * -8) * 16) + 1);
                maxLoc.setY(maxY + 20);
                WorldGuardCompat.createRegion("mapart-" + i + "-area", maxLoc, minLoc);
                WorldGuardCompat.addFlag(getWorld(), "mapart-" + i + "-area", Flags.EXIT, StateFlag.State.DENY);
                //WorldGuardCompat.addFlag(getWorld(), "mapart-" + i + "-area", Flags.BUILD, StateFlag.State.ALLOW);
                WorldGuardCompat.getRegion(getWorld(), "mapart-" + i + "-area").setPriority(9);
                return landData;
            }
            return null;
        }));
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
        WorldGuardCompat.addFlag(getWorld(), name, Flags.SNOW_FALL, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.SNOW_MELT, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.HEALTH_REGEN, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.USE_ANVIL, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.PISTONS, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.WATER_FLOW, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.LAVA_FLOW, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.LIGHTNING, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.FROSTED_ICE_FORM, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.ICE_MELT, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.POTION_SPLASH, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.ITEM_FRAME_ROTATE, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.FIRE_SPREAD, StateFlag.State.DENY);
        WorldGuardCompat.addFlag(getWorld(), name, Flags.ENTRY, StateFlag.State.DENY);
    }

    public static void deleteLand(final int landId, final Runnable doLast) {
        Bukkit.getScheduler().runTaskAsynchronously(MapartPlugin.getPlugin(), () -> {
            WorldGuardCompat.removeAllMembers(MapartManager.getWorld(), "mapart-" + landId);
            resetLand(landId);

            doLast.run();
        });

        TaskUtils.addSynchronizedTask(() -> MapartSQLConfig.singleton().getTransactionManager().required(() -> {
            final MMapartLand land = MMapartLandRepo.select(landId);
            if (land != null) MMapartLandRepo.delete(land);

            final List<DMapartLandCollaborator> collabos = DMapartLandCollaboratorRepo.selectByLand(landId);
            for (final DMapartLandCollaborator collabo : collabos) {
                DMapartLandCollaboratorRepo.delete(collabo);
            }
        }));

        final ProtectedRegion rg = WorldGuardCompat.getRegion(MapartManager.getWorld(), "mapart-" + landId);
        rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())).forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));

        WorldGuardCompat.removeRegion(MapartManager.getWorld(), "mapart-" + landId + "-area");
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
            e.printStackTrace();
        }
    }

    public static void teleportLand(final int landId, final UUID player) {
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

    @Override
    public boolean handleEvents() {
        return true;
    }
}
