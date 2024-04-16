package net.akazukin.mapart.doma.repo;

import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.MapartLandDao;
import net.akazukin.mapart.doma.dao.MapartLandDaoImpl;
import net.akazukin.mapart.doma.dto.MapartLandDto;
import net.akazukin.mapart.doma.entity.MapartLand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapartLandRepo {
    private static final MapartLandDao MAPART_LAND_DAO = new MapartLandDaoImpl(MapartSQLConfig.singleton());

    public static List<MapartLandDto> selectByPlayer(final UUID player) {
        return MAPART_LAND_DAO.selectByPlayer(player, Collectors.groupingBy(MapartLand::getLandId)).values().stream().map(entities -> {
            final List<UUID> collaborators = new ArrayList<>();
            for (final MapartLand entity : entities) {
                if (entity.getCollaboratorUuid() == null) continue;
                collaborators.add(entity.getCollaboratorUuid());
            }
            final MapartLandDto dto = new MapartLandDto();
            dto.setLandId(entities.get(0).getLandId());
            dto.setName(entities.get(0).getName());
            dto.setOwnerUUID(entities.get(0).getOwnerUuid());
            dto.setHeight(entities.get(0).getHeight());
            dto.setWidth(entities.get(0).getWidth());
            dto.setX(entities.get(0).getX());
            dto.setZ(entities.get(0).getZ());
            dto.setCreatedDate(entities.get(0).getCreatedDate());
            dto.setCollaboratorsUUID(collaborators.toArray(new UUID[0]));
            dto.setStatus(entities.get(0).getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    public static MapartLandDto selectByLand(final long land) {
        final List<MapartLand> entities = MAPART_LAND_DAO.selectByLand(land);
        if (entities.isEmpty()) return null;

        final List<UUID> collaborators = new ArrayList<>();
        for (final MapartLand mapartLand : entities) {
            if (mapartLand.getCollaboratorUuid() == null) continue;
            collaborators.add(mapartLand.getCollaboratorUuid());
        }

        final MapartLandDto dto = new MapartLandDto();
        dto.setLandId(entities.get(0).getLandId());
        dto.setName(entities.get(0).getName());
        dto.setOwnerUUID(entities.get(0).getOwnerUuid());
        dto.setHeight(entities.get(0).getHeight());
        dto.setWidth(entities.get(0).getWidth());
        dto.setX(entities.get(0).getX());
        dto.setZ(entities.get(0).getZ());
        dto.setCreatedDate(entities.get(0).getCreatedDate());
        dto.setCollaboratorsUUID(collaborators.toArray(new UUID[0]));
        dto.setStatus(entities.get(0).getStatus());
        return dto;
    }

    public static List<MapartLand> selectByCollaborator(final UUID collaborator) {
        return MAPART_LAND_DAO.selectByCollaborator(collaborator);
    }

    public static List<MapartLandDto> selectAll() {
        return MAPART_LAND_DAO.selectAll(Collectors.groupingBy(MapartLand::getLandId)).values().stream().map(entities -> {
            final List<UUID> collaborators = new ArrayList<>();
            for (final MapartLand mapartLand : entities) {
                if (mapartLand.getCollaboratorUuid() == null) continue;
                collaborators.add(mapartLand.getCollaboratorUuid());
            }
            final MapartLandDto dto = new MapartLandDto();
            dto.setLandId(entities.get(0).getLandId());
            dto.setName(entities.get(0).getName());
            dto.setOwnerUUID(entities.get(0).getOwnerUuid());
            dto.setHeight(entities.get(0).getHeight());
            dto.setWidth(entities.get(0).getWidth());
            dto.setX(entities.get(0).getX());
            dto.setZ(entities.get(0).getZ());
            dto.setCreatedDate(entities.get(0).getCreatedDate());
            dto.setCollaboratorsUUID(collaborators.toArray(new UUID[0]));
            dto.setStatus(entities.get(0).getStatus());
            return dto;
        }).collect(Collectors.toList());
    }
}
