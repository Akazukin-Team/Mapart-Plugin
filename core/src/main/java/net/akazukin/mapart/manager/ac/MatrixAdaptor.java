package net.akazukin.mapart.manager.ac;

import me.rerere.matrix.api.events.PlayerViolationEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

public class MatrixAdaptor implements Listenable {

    @EventTarget(bktPriority = EventPriority.NORMAL)
    public void onPlayerViolation(final PlayerViolationEvent event) {
        if (!MapartManager.isMapartWorld(Bukkit.getPlayer(event.getPlayer().getUniqueId()).getWorld()))
            return;

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Matrix");
    }
}
