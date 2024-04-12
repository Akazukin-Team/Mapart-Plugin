package net.akazukin.mapart.event;

import ac.grim.grimac.api.events.FlagEvent;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
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

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        callEvent(event, EventPriority.NORMAL);
    }

    @EventHandler
    public void onPlayerWorldChanged(final PlayerChangedWorldEvent event) {
        callEvent(event, EventPriority.NORMAL);
    }

    private void callEvent(final Event event, final EventPriority priority) {
        MapartPlugin.EVENT_MANAGER.callEvent(event, priority);
    }
}
