package net.akazukin.mapart.event;

import net.akazukin.library.event.EventManager;
import net.akazukin.mapart.manager.CopyrightManager;
import net.akazukin.mapart.manager.TownyAdaptor;
import net.akazukin.mapart.manager.ac.GrimACAdaptor;
import net.akazukin.mapart.manager.ac.MatrixAdaptor;
import net.akazukin.mapart.manager.ac.NCPAdaptor;
import org.bukkit.Bukkit;

public final class MapartEventManager extends EventManager {
    @Override
    public void registerListeners() {
        this.registerListeners(
                new CopyrightManager()
        );
        if (Bukkit.getPluginManager().isPluginEnabled("GrimAC"))
            this.registerListeners(GrimACAdaptor.class);
        if (Bukkit.getPluginManager().isPluginEnabled("NoCheatPlus"))
            this.registerListeners(NCPAdaptor.class);
        if (Bukkit.getPluginManager().isPluginEnabled("Matrix"))
            this.registerListeners(MatrixAdaptor.class);
        if (Bukkit.getPluginManager().isPluginEnabled("Towny"))
            this.registerListeners(TownyAdaptor.class);
    }
}
