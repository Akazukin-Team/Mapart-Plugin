package net.akazukin.mapart.doma.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class MapartLandDto {
    private int landId;
    private String name;

    private UUID ownerUUID;
    private UUID[] collaboratorsUUID;

    private long x;
    private long z;

    private long height;
    private long width;

    private Timestamp createdDate;
}
