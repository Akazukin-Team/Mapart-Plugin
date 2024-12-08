package org.akazukin.mapart.event;

import lombok.Getter;
import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public class MatrixEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerViolation(final PlayerViolationEvent event) {
        MapartPlugin.EVENT_MANAGER.callEvent(PlayerViolationEvent.class, event,
                net.akazukin.library.event.EventPriority.HIGH);
    }
}
