package org.akazukin.mapart.manager.mapart;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.event.EventTarget;
import org.akazukin.event.Listenable;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class MapartWorldListener implements Listenable {
    public final static Map<Integer, MapartWorldListener> singletons = new ConcurrentHashMap<>();
    public final static Map<UUID, Long> LAST_DELETED = new HashMap<>();
    public final static List<UUID> CLEANING = new ArrayList<>();
    @Getter
    private static final Map<UUID, Location> lastPos = new HashMap<>();
    private final MapartManager mapartManager;

    public MapartWorldListener(final MapartManager mapartManager) {
        this.mapartManager = mapartManager;
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getPlayer().getWorld())) {
            return;
        }
        event.getPlayer().setAllowFlight(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getEntered().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (this.mapartManager.isMapartWorld_(event.getPlayer().getWorld())) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT ||
                    event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);
            } else {
                MapartWorldListener.lastPos.remove(event.getPlayer().getUniqueId());
            }
        } else if (this.mapartManager.isMapartWorld_(event.getFrom().getWorld())) {
            MapartWorldListener.lastPos.put(event.getPlayer().getUniqueId(), event.getFrom());
        }
    }

    @EventTarget(libraryPriority = 2)
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        if (this.mapartManager.isMapartWorld_(event.getFrom())) {
            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                event.getPlayer().setFlying(false);
                event.getPlayer().setAllowFlight(false);
            }
        } else if (this.mapartManager.isMapartWorld_(event.getPlayer().getWorld())) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        if (event.getBlock().getY() == LibraryPlugin.getPlugin().getCompat().getMinHeight(this.mapartManager.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockExplode(final ExplosionPrimeEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getEntity().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getEntity().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getEntity().getWorld())) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventTarget(libraryPriority = 3)
    public void onEntitySpawn(final EntitySpawnEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getEntity().getWorld())) {
            return;
        }
        if (event.getEntity() instanceof Item) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockRedstone(final BlockRedstoneEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }

        event.setNewCurrent(event.getOldCurrent());
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockPiston(final BlockPistonEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onBlockFrom(final BlockFormEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onEntityPlace(final EntityPlaceEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 3)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getPlayer().getWorld())) {
            return;
        }
        if (!event.isCancelled()) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }

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

    @EventTarget(libraryPriority = 3)
    public void onBlockCanBuild(final BlockCanBuildEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getBlock().getWorld())) {
            return;
        }
        if (!event.isBuildable()) {
            return;
        }

        if (event.getBlock().getY() >
                MapartPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getInt("land.height") +
                        LibraryPlugin.getPlugin().getCompat().getMinHeight(event.getBlock().getWorld())
        ) {
            event.setBuildable(false);
            return;
        }

        Class<?> data;
        try {
            data = event.getMaterial().getData();
        } catch (final IllegalArgumentException ignore) {
            data = event.getMaterial().data;
        }

        if (data != null) {
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
        }

        switch (event.getMaterial().name()) {
            case "ANVIL":
            case "CHIPPED_ANVIL":
            case "DAMAGED_ANVIL":

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

    @EventTarget(libraryPriority = 2)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (!this.mapartManager.isMapartWorld_(event.getPlayer().getWorld())) {
            return;
        }
        MapartWorldListener.teleportLastPos(event.getPlayer());
    }

    public static boolean teleportLastPos(final Player p) {
        if (lastPos.containsKey(p.getUniqueId())) {
            p.teleport(MapartWorldListener.lastPos.get(p.getUniqueId()));
            MapartWorldListener.lastPos.remove(p.getUniqueId());
            return true;
        }
        return false;
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
