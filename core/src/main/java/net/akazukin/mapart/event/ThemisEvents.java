package net.akazukin.mapart.event;

import com.gmail.olexorus.themis.api.ViolationEvent;
import lombok.Getter;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter
public class ThemisEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onViolation(final ViolationEvent event) {
        MapartPlugin.EVENT_MANAGER.callEvent(event, net.akazukin.library.event.EventPriority.HIGH);
    }
}
