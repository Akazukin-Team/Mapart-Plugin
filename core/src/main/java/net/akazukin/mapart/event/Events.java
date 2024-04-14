package net.akazukin.mapart.event;

import ac.grim.grimac.api.events.FlagEvent;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onGrimACFlagged(final FlagEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTownClaim(final TownPreClaimEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        callEvent(event, EventPriority.NORMAL);
    }

    @EventHandler
    public void onPlayerWorldChanged(final PlayerChangedWorldEvent event) {
        callEvent(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPlace(final EntityPlaceEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(final BlockFromToEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(final PrepareAnvilEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(final PrepareItemCraftEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockCanBuild(final BlockCanBuildEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemEnchant(final PrepareItemEnchantEvent event) {
        callEvent(event, EventPriority.HIGH);
    }

    private void callEvent(final Event event, final EventPriority priority) {
        MapartPlugin.EVENT_MANAGER.callEvent(event, priority);
    }
}
