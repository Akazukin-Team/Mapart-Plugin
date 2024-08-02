package net.akazukin.mapart.manager.ac;

import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class MatrixAdaptor implements Listenable {

    @EventTarget(bktPriority = EventPriority.NORMAL)
    public void onGrimACFlagEvent(final PlayerChangedWorldEvent event) {
        if (!MapartManager.isMapartWorld(Bukkit.getPlayer(event.getPlayer().getUniqueId()).getWorld()))
            return;

        if (MapartManager.isMapartWorld(event.getFrom())) {
            MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.SCAFFOLD, 6 * 60L * 60L * 1000L);
            MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.MOVE, 6 * 60L * 60L * 1000L);
            MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.INTERACT, 6 * 60L * 60L * 1000L);
        } else if (MapartManager.isMapartWorld(event.getPlayer().getWorld())) {
            MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.SCAFFOLD, 1L);
            MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.MOVE, 1L);
            MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.INTERACT, 1L);
        }
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Matrix");
    }
}
