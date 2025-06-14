package org.akazukin.mapart.doma.repo;

import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dao.MapartUserDao;
import org.akazukin.mapart.doma.dao.MapartUserDaoImpl;
import org.akazukin.mapart.doma.dto.MapartUserDto;
import org.akazukin.mapart.doma.entity.MapartUser;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapartUserRepo {
    private static final MapartUserDao MAPART_USER_DAO = new MapartUserDaoImpl(MapartSQLConfig.singleton());

    public static MapartUserDto selectByPlayer(final UUID player) {
        final List<MapartUser> entities = MAPART_USER_DAO.selectByPlayer(player);
        if (entities.isEmpty()) {
            return null;
        }

        final MapartUserDto dto = new MapartUserDto();
        dto.setPlayerUuid(entities.get(0).getPlayerUuid());
        dto.setMaxLand(entities.get(0).getMaxLand());
        dto.setLandIds(entities.stream().map(MapartUser::getLandId).filter(Objects::nonNull).toArray(Integer[]::new));
        return dto;
    }

    public static List<MapartUserDto> selectAll() {
        return MAPART_USER_DAO.selectAll(Collectors.groupingBy(MapartUser::getPlayerUuid)).values().stream().map(entities -> {
            final MapartUserDto dto = new MapartUserDto();
            dto.setPlayerUuid(entities.get(0).getPlayerUuid());
            dto.setMaxLand(entities.get(0).getMaxLand());
            dto.setLandIds(entities.stream().map(MapartUser::getLandId).filter(Objects::nonNull).toArray(Integer[]::new));
            return dto;
        }).collect(Collectors.toList());
    }
}
