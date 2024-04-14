package net.akazukin.mapart.event;

import net.akazukin.library.event.EventManager;
import net.akazukin.mapart.manager.CopyrightManager;
import net.akazukin.mapart.manager.MapartManager;

public final class MapartEventManager extends EventManager {
    @Override
    public void registerListeners() {
        registerListeners(
                new MapartManager(),
                new CopyrightManager()
        );
    }
}
