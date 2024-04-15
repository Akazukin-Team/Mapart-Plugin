package net.akazukin.mapart.doma.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class MapartUserDto {
    private UUID playerUuid;
    private Integer maxLand;
    private Integer[] landIds;
}
