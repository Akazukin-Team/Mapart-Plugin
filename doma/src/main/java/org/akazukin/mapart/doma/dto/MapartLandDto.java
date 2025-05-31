package org.akazukin.mapart.doma.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class MapartLandDto {
    private long landId;
    private long locationId;

    private int size;

    private String name;

    private UUID ownerUUID;
    private UUID[] collaboratorsUUID;

    private long height;
    private long width;

    private Timestamp createdDate;

    private String status;
}
