package net.akazukin.mapart.event;

import net.akazukin.library.event.EventManager;
import net.akazukin.mapart.manager.CopyrightManager;
import net.akazukin.mapart.manager.TownyAdaptor;
import net.akazukin.mapart.manager.ac.GrimACAdaptor;
import net.akazukin.mapart.manager.ac.MatrixAdaptor;
import net.akazukin.mapart.manager.ac.NCPAdaptor;
import net.akazukin.mapart.manager.ac.ThemisAdaptor;
import org.bukkit.Bukkit;

public final class MapartEventManager extends EventManager {
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
