package net.akazukin.mapart.doma.entity;

import java.util.UUID;
import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

@Entity
@Data
@Table(name = "M_MAPART_WORLD")
public class MMapartWorld {
    @Column(name = "LAND_SIZE")
    @Id
    private long landSize;

    @Column(name = "WORLD_NAME")
    private String worldName;
    @Column(name = "WORLD_UUID")
    private UUID uuid;

    @Column(name = "VERSION_NO")
    @Version
    private long versionNo = -1;
}
