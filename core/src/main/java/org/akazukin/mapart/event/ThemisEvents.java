package org.akazukin.mapart.event;

import com.gmail.olexorus.themis.api.ViolationEvent;
import lombok.Getter;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public final class ThemisEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onViolation(final ViolationEvent event) {
        MapartPlugin.getPlugin().getEventManager().callEvent(ViolationEvent.class, event, EventPriority.HIGH.getSlot());
    }
}
