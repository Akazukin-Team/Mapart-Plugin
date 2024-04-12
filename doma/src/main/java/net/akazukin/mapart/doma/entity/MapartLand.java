package net.akazukin.mapart.doma.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
//@Table(name = "M_MAPART_LAND")
public class MapartLand {
    @Column(name = "LAND_ID")
    @Id
    private int landId;

    @Column(name = "NAME")
    private String name;
    @Column(name = "OWNER_UUID")
    private UUID ownerUuid;

    @Column(name = "X")
    private long x;
    @Column(name = "Z")
    private long z;

    @Column(name = "HEIGHT")
    private long height;
    @Column(name = "WIDTH")
    private long width;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "COLLABORATOR_UUID")
    private UUID collaboratorUuid;
}
