package net.akazukin.mapart.doma.repo;

import java.util.List;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.MMapartWorldDao;
import net.akazukin.mapart.doma.dao.MMapartWorldDaoImpl;
import net.akazukin.mapart.doma.entity.MMapartWorld;

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
        if (entity.getVersionNo() <= 0) {
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
