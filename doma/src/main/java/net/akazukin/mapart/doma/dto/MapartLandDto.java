package net.akazukin.mapart.doma.dto;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Data
public class MapartLandDto {
    private long landId;
    private long locationId;

    private long size;
    
    private String name;

    private UUID ownerUUID;
    private UUID[] collaboratorsUUID;

    private long height;
    private long width;

    private Timestamp createdDate;

    private String status;
}
