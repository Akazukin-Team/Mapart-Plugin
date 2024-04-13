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
@Table(name = "M_MAPART_LAND")
public class MUserMapart {
    @Column(name = "PLAYER_UUID")
    @Id
    private UUID playerUuid;

    @Column(name = "MAX_LAND")
    private int maxLand;

    @Column(name = "VERSION_NO")
    @Version
    private long versionNo = -1;
}
