package net.akazukin.mapart.event;

import ac.grim.grimac.api.events.FlagEvent;
import lombok.Getter;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public class GrimACEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onFlag(final FlagEvent event) {
        MapartPlugin.EVENT_MANAGER.callEvent(FlagEvent.class, event, net.akazukin.library.event.EventPriority.HIGH);
    }
}
