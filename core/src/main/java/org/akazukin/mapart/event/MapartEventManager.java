package org.akazukin.mapart.event;

import org.akazukin.library.event.EventManager;
import org.akazukin.mapart.manager.CopyrightManager;
import org.akazukin.mapart.manager.TownyAdaptor;
import org.akazukin.mapart.manager.ac.GrimACAdaptor;
import org.akazukin.mapart.manager.ac.MatrixAdaptor;
import org.akazukin.mapart.manager.ac.NCPAdaptor;
import org.akazukin.mapart.manager.ac.ThemisAdaptor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public final class MapartEventManager extends EventManager<Event> {
    @Override
    public void registerListeners() {
        this.registerListeners(
                new CopyrightManager()
        );
        if (Bukkit.getPluginManager().getPlugin("GrimAC") != null)
            this.registerListeners(GrimACAdaptor.class);
        if (Bukkit.getPluginManager().getPlugin("NoCheatPlus") != null)
            this.registerListeners(NCPAdaptor.class);
        if (Bukkit.getPluginManager().getPlugin("Matrix") != null)
            this.registerListeners(MatrixAdaptor.class);
        if (Bukkit.getPluginManager().getPlugin("Themis") != null)
            this.registerListeners(ThemisAdaptor.class);
        if (Bukkit.getPluginManager().getPlugin("Towny") != null)
            this.registerListeners(TownyAdaptor.class);
    }
}
