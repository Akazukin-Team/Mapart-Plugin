package net.akazukin.mapart.manager.ac;

import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;
import me.rerere.matrix.api.events.PlayerViolationEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class MatrixAdaptor implements Listenable {

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onPlayerViolation(final PlayerViolationEvent event) {
        if (!MapartManager.isMapartWorld(event.getPlayer().getWorld()))
            return;

        Bukkit.broadcastMessage(event.getHackType().name());

        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.SCAFFOLD, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.CLICK, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.MOVE, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.INTERACT, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.BADPACKETS, 1000L);

        event.setCancelled(true);
    }

    @EventTarget
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!MapartManager.isMapartWorld(event.getPlayer().getWorld()))
            return;

        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.SCAFFOLD, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.CLICK, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.MOVE, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.INTERACT, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.BADPACKETS, 1000L);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Matrix");
    }
}
