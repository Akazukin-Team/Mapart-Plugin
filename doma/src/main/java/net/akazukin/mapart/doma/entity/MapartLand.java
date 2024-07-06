package net.akazukin.mapart.doma.entity;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;

@Entity
@Data
public class MapartLand {
    @Column(name = "LAND_ID")
    @Id
    private int landId;

    @Column(name = "LOCATION_ID")
    private int locationId;

    @Column(name = "SIZE")
    private long size;

    @Column(name = "NAME")
    private String name;
    @Column(name = "OWNER_UUID")
    private UUID ownerUuid;

    @Column(name = "HEIGHT")
    private long height;
    @Column(name = "WIDTH")
    private long width;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "COLLABORATOR_UUID")
    private UUID collaboratorUuid;
}
