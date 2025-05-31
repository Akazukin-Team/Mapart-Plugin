package org.akazukin.mapart.manager.mapart;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.world.WorldData;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(callSuper = true)
public final class MapartWorldData extends WorldData {
    private final int size;

    public MapartWorldData(final UUID uid, final String name, final int size) {
        super(uid, name);
        this.size = size;
    }
}
