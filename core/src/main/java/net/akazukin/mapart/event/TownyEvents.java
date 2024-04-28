package net.akazukin.mapart.event;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import lombok.Getter;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public class TownyEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onTownClaim(final TownPreClaimEvent event) {
        MapartPlugin.EVENT_MANAGER.callEvent(event, EventPriority.HIGH);
    }
}
