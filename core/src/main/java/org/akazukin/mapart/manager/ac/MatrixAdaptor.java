package org.akazukin.mapart.manager.ac;

import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;
import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.akazukin.event.EventTarget;
import org.akazukin.event.Listenable;
import org.akazukin.mapart.manager.mapart.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerMoveEvent;

public final class MatrixAdaptor implements Listenable {

    @EventTarget(libraryPriority = 3)
    public void onPlayerViolation(final PlayerViolationEvent event) {
        if (!MapartManager.isMapartWorld(event.getPlayer().getWorld())) {
            return;
        }

        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.SCAFFOLD, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.CLICK, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.MOVE, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.INTERACT, 1000L);
        MatrixAPIProvider.getAPI().tempBypass(event.getPlayer(), HackType.BADPACKETS, 1000L);

        event.setCancelled(true);
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!MapartManager.isMapartWorld(event.getPlayer().getWorld())) {
            return;
        }

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
