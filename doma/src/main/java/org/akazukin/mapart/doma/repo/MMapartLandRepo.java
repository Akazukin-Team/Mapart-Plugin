package org.akazukin.mapart.doma.repo;

import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dao.MMapartLandDao;
import org.akazukin.mapart.doma.dao.MMapartLandDaoImpl;
import org.akazukin.mapart.doma.entity.MMapartLand;

import java.util.List;
import java.util.UUID;

public class MMapartLandRepo {
    private static final MMapartLandDao M_MAPART_LAND_DAO = new MMapartLandDaoImpl(MapartSQLConfig.singleton());

    public static List<MMapartLand> selectByOwner(final UUID owner) {
        return MMapartLandRepo.M_MAPART_LAND_DAO.selectByOwner(owner);
    }

    public static List<MMapartLand> selectBySize(final int size) {
        return MMapartLandRepo.M_MAPART_LAND_DAO.selectBySize(size);
    }

    public static MMapartLand selectByLand(final long land) {
        return MMapartLandRepo.M_MAPART_LAND_DAO.selectByLand(land);
    }

    public static List<MMapartLand> selectAll() {
        return MMapartLandRepo.M_MAPART_LAND_DAO.selectAll();
    }

    public static void save(final MMapartLand entity) {
        if (entity.getVersionNo() == null) {
            MMapartLandRepo.M_MAPART_LAND_DAO.insert(entity);
        } else {
            MMapartLandRepo.M_MAPART_LAND_DAO.update(entity);
        }
    }

    public static void delete(final MMapartLand entity) {
        MMapartLandRepo.M_MAPART_LAND_DAO.delete(entity);
    }

    public static void createTable() {
        MMapartLandRepo.M_MAPART_LAND_DAO.create();
    }

    public static int getMissingLoc(final long size) {
        return MMapartLandRepo.M_MAPART_LAND_DAO.missingLoc(size);
    }

    public static int getMissingLand() {
        return MMapartLandRepo.M_MAPART_LAND_DAO.missingLand();
    }

    public static MMapartLand selectBySizeAndLocation(final long size, final long locationId) {
        return MMapartLandRepo.M_MAPART_LAND_DAO.selectBySizeAndLocation(size, locationId);
    }
}
