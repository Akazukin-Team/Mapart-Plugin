package org.akazukin.mapart.doma.repo;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dao.MapartLandDao;
import org.akazukin.mapart.doma.dao.MapartLandDaoImpl;
import org.akazukin.mapart.doma.dto.MapartLandDto;
import org.akazukin.mapart.doma.entity.MapartLand;

public class MapartLandRepo {
    private static final MapartLandDao MAPART_LAND_DAO = new MapartLandDaoImpl(MapartSQLConfig.singleton());

    public static List<MapartLandDto> selectByPlayer(final UUID player) {
        return MapartLandRepo.MAPART_LAND_DAO
                .selectByPlayer(player, Collectors.groupingBy(MapartLand::getLandId))
                .values()
                .stream()
                .map(MapartLandRepo::get)
                .collect(Collectors.toList());
    }

    public static MapartLandDto get(final List<MapartLand> entities) {
        if (entities.isEmpty()) return null;

        final MapartLandDto dto = new MapartLandDto();
        dto.setLandId(entities.get(0).getLandId());
        dto.setLocationId(entities.get(0).getLocationId());
        dto.setName(entities.get(0).getName());
        dto.setOwnerUUID(entities.get(0).getOwnerUuid());
        dto.setSize(entities.get(0).getSize());
        dto.setHeight(entities.get(0).getHeight());
        dto.setWidth(entities.get(0).getWidth());
        dto.setCreatedDate(entities.get(0).getCreatedDate());
        dto.setCollaboratorsUUID(
                entities
                        .stream()
                        .map(MapartLand::getCollaboratorUuid)
                        .filter(Objects::nonNull)
                        .toArray(UUID[]::new)
        );
        dto.setStatus(entities.get(0).getStatus());
        return dto;
    }

    public static MapartLandDto selectByLand(final long land) {
        return MapartLandRepo.get(MapartLandRepo.MAPART_LAND_DAO.selectByLand(land));
    }

    public static List<MapartLand> selectByCollaborator(final UUID collaborator) {
        return MapartLandRepo.MAPART_LAND_DAO.selectByCollaborator(collaborator);
    }

    public static List<MapartLandDto> selectAll() {
        return MapartLandRepo.MAPART_LAND_DAO
                .selectAll(Collectors.groupingBy(MapartLand::getLandId))
                .values()
                .stream()
                .map(MapartLandRepo::get)
                .collect(Collectors.toList());
    }
}
