package org.akazukin.mapart.doma.entity;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

@Entity
@Data
@Table(name = "M_MAPART_LAND")
public class MMapartLand {
    @Column(name = "LAND_ID")
    @Id
    private long landId;

    @Column(name = "LOCATION_ID")
    private long locationId;

    @Column(name = "OWNER_UUID")
    private UUID ownerUuid;
    @Column(name = "NAME")
    private String name;

    @Column(name = "SIZE")
    private long size;

    @Column(name = "HEIGHT")
    private int height;
    @Column(name = "WIDTH")
    private int width;

    @Column(name = "CREATED_DATE")
    private Timestamp createDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "VERSION_NO")
    @Version
    private Long versionNo;
}
