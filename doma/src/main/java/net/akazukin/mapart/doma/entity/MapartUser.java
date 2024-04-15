package net.akazukin.mapart.doma.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import java.util.UUID;

@Entity
@Data
@Table(name = "M_MAPART_USER")
public class MapartUser {
    @Column(name = "PLAYER_UUID_")
    @Id
    private UUID playerUuid;

    @Column(name = "MAX_LAND")
    private Integer maxLand;

    @Column(name = "LAND_ID")
    private Integer landId;

    @Column(name = "VERSION_NO")
    @Version
    private long versionNo = -1;
}
