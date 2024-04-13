package net.akazukin.mapart.doma.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "M_MAPART_LAND")
public class MMapartLand {
    @Column(name = "LAND_ID")
    @Id
    private int landId;

    @Column(name = "OWNER_UUID")
    private UUID ownerUuid;
    @Column(name = "NAME")
    private String name;

    @Column(name = "X")
    private int x;
    @Column(name = "Z")
    private int z;

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
    private long versionNo = -1;
}
