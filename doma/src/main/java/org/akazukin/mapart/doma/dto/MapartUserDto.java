package org.akazukin.mapart.doma.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MapartUserDto {
    private UUID playerUuid;
    private Integer maxLand;
    private Integer[] landIds;
}
