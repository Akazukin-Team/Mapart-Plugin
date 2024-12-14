package org.akazukin.mapart.event;

import ac.grim.grimac.api.events.FlagEvent;
import lombok.Getter;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public class GrimACEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onFlag(final FlagEvent event) {
        MapartPlugin.getPlugin().getEventManager().callEvent(FlagEvent.class, event, org.akazukin.library.event.EventPriority.HIGH);
    }
}
