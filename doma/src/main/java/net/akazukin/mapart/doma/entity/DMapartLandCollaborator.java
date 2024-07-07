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
@Table(name = "D_MAPART_LAND_COLLABORATOR")
public class DMapartLandCollaborator {
    @Column(name = "LAND_ID")
    @Id
    private long landId;

    @Column(name = "COLLABORATOR_UUID")
    @Id
    private UUID collaboratorUuid;

    @Column(name = "VERSION_NO")
    @Version
    private Long versionNo;
}
