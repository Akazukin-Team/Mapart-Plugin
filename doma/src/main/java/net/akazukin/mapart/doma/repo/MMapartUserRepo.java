package net.akazukin.mapart.doma.repo;

import java.util.List;
import java.util.UUID;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.MMapartUserDao;
import net.akazukin.mapart.doma.dao.MMapartUserDaoImpl;
import net.akazukin.mapart.doma.entity.MMapartUser;

public class MMapartUserRepo {
    private static final MMapartUserDao M_MAPART_USER_DAO = new MMapartUserDaoImpl(MapartSQLConfig.singleton());

    public static MMapartUser selectByPlayer(final UUID player) {
        return M_MAPART_USER_DAO.selectByPlayer(player);
    }

    public static List<MMapartUser> selectAll() {
        return M_MAPART_USER_DAO.selectAll();
    }

    public static void save(final MMapartUser entity) {
        if (entity.getVersionNo() <= 0) {
            M_MAPART_USER_DAO.insert(entity);
        } else {
            M_MAPART_USER_DAO.update(entity);
        }
    }

    public static void delete(final MMapartUser entity) {
        M_MAPART_USER_DAO.delete(entity);
    }

    public static void createTable() {
        M_MAPART_USER_DAO.create();
    }

    public static void insert(final MMapartUser entity) {
        M_MAPART_USER_DAO.insert(entity);
    }
}
