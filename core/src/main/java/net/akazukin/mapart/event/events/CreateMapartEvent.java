package net.akazukin.mapart.event.events;

import net.akazukin.library.event.Event;
import org.bukkit.event.HandlerList;

public class CreateMapartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
