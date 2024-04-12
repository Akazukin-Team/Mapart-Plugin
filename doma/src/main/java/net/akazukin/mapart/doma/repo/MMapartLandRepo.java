package net.akazukin.mapart.doma.repo;

import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.MMapartLandDao;
import net.akazukin.mapart.doma.dao.MMapartLandDaoImpl;
import net.akazukin.mapart.doma.entity.MMapartLand;

import java.util.List;
import java.util.UUID;

public class MMapartLandRepo {
    private static final MMapartLandDao M_MAPART_LAND_DAO = new MMapartLandDaoImpl(MapartSQLConfig.singleton());

    public static List<MMapartLand> select(final UUID player) {
        return M_MAPART_LAND_DAO.selectByPlayer(player);
    }

    public static MMapartLand select(final long land) {
        return M_MAPART_LAND_DAO.selectByLand(land);
    }

    public static List<MMapartLand> selectAll() {
        return M_MAPART_LAND_DAO.selectAll();
    }

    public static void save(final MMapartLand entity) {
        if (entity.getVersionNo() <= 0) {
            M_MAPART_LAND_DAO.insert(entity);
        } else {
            M_MAPART_LAND_DAO.update(entity);
        }
    }

    public static void delete(final MMapartLand entity) {
        M_MAPART_LAND_DAO.delete(entity);
    }

    public static void createTable() {
        M_MAPART_LAND_DAO.create();
    }

    public static void insert(final MMapartLand entity) {
        M_MAPART_LAND_DAO.insert(entity);
    }
}
