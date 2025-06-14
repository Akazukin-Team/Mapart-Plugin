package org.akazukin.mapart.event;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import lombok.Getter;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public final class TownyEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onTownClaim(final TownPreClaimEvent event) {
        MapartPlugin.getPlugin().getEventManager().callEvent(TownPreClaimEvent.class, event, EventPriority.HIGH.getSlot());
    }
}
