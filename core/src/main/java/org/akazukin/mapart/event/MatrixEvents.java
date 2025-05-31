package org.akazukin.mapart.event;

import lombok.Getter;
import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public final class MatrixEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerViolation(final PlayerViolationEvent event) {
        MapartPlugin.getPlugin().getEventManager().callEvent(PlayerViolationEvent.class, event, EventPriority.HIGH.getSlot());
    }
}
