package org.akazukin.mapart.doma.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class MapartUserDto {
    private UUID playerUuid;
    private Integer maxLand;
    private Integer[] landIds;
}
