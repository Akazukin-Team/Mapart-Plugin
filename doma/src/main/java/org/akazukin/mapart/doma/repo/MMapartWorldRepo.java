package org.akazukin.mapart.doma.repo;

import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dao.MMapartWorldDao;
import org.akazukin.mapart.doma.dao.MMapartWorldDaoImpl;
import org.akazukin.mapart.doma.entity.MMapartWorld;

import java.util.List;

public class MMapartWorldRepo {
    private static final MMapartWorldDao M_MAPART_WORLD_DAO = new MMapartWorldDaoImpl(MapartSQLConfig.singleton());

    public static MMapartWorld select(final long landSize) {
        return MMapartWorldRepo.M_MAPART_WORLD_DAO.selectBySize(landSize);
    }

    public static MMapartWorld select(final String worldName) {
        return MMapartWorldRepo.M_MAPART_WORLD_DAO.selectByName(worldName);
    }

    public static List<MMapartWorld> selectAll() {
        return MMapartWorldRepo.M_MAPART_WORLD_DAO.selectAll();
    }

    public static void save(final MMapartWorld entity) {
        if (entity.getVersionNo() == null) {
            MMapartWorldRepo.M_MAPART_WORLD_DAO.insert(entity);
        } else {
            MMapartWorldRepo.M_MAPART_WORLD_DAO.update(entity);
        }
    }

    public static void delete(final MMapartWorld entity) {
        MMapartWorldRepo.M_MAPART_WORLD_DAO.delete(entity);
    }

    public static void createTable() {
        MMapartWorldRepo.M_MAPART_WORLD_DAO.create();
    }
}
