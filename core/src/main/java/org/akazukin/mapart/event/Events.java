package org.akazukin.mapart.event;

import org.akazukin.event.IEvents;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class Events extends IEvents<Event> implements Listener {
    public Events() {
        super(MapartPlugin.getPlugin().getEventManager());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        this.callEvent(BlockBreakEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        this.callEvent(EntityDamageEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        this.callEvent(EntityRegainHealthEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.callEvent(PlayerJoinEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onPlayerWorldChanged(final PlayerChangedWorldEvent event) {
        this.callEvent(PlayerChangedWorldEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        this.callEvent(EntityBlockFormEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPlace(final EntityPlaceEvent event) {
        this.callEvent(EntityPlaceEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFrom(final BlockFormEvent event) {
        this.callEvent(BlockFormEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawn(final EntitySpawnEvent event) {
        this.callEvent(EntitySpawnEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        this.callEvent(BlockPhysicsEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        this.callEvent(BlockIgniteEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(final PrepareAnvilEvent event) {
        this.callEvent(PrepareAnvilEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(final PrepareItemCraftEvent event) {
        this.callEvent(PrepareItemCraftEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        this.callEvent(InventoryClickEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockCanBuild(final BlockCanBuildEvent event) {
        this.callEvent(BlockCanBuildEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        this.callEvent(PlayerInteractEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemEnchant(final PrepareItemEnchantEvent event) {
        this.callEvent(PrepareItemEnchantEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler
    public void onPlayerJoinNormal(final PlayerJoinEvent event) {
        this.callEvent(PlayerJoinEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.callEvent(PlayerQuitEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.callEvent(PlayerMoveEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        this.callEvent(VehicleEnterEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosionPrime(final ExplosionPrimeEvent event) {
        this.callEvent(ExplosionPrimeEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        this.callEvent(PlayerTeleportEvent.class, event, EventPriority.NORMAL.getSlot());
    }
}
