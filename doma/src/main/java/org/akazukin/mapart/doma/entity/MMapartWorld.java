package org.akazukin.mapart.doma.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import java.util.UUID;

@Entity
@Data
@Table(name = "M_MAPART_WORLD")
public class MMapartWorld {
    @Column(name = "LAND_SIZE")
    @Id
    private int landSize;

    @Column(name = "WORLD_NAME")
    private String worldName;
    @Column(name = "WORLD_UUID")
    private UUID uuid;

    @Column(name = "VERSION_NO")
    @Version
    private Long versionNo;
}
