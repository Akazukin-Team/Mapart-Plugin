package net.akazukin.mapart.doma.repo;

import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.DMapartLandCollaboratorDao;
import net.akazukin.mapart.doma.dao.DMapartLandCollaboratorDaoImpl;
import net.akazukin.mapart.doma.entity.DMapartLandCollaborator;

import java.util.List;
import java.util.UUID;

public class DMapartLandCollaboratorRepo {
    private static final DMapartLandCollaboratorDao D_MAPART_LAND_COLLABORATOR_DAO = new DMapartLandCollaboratorDaoImpl(MapartSQLConfig.singleton());

    public static List<DMapartLandCollaborator> selectByLand(final long land) {
        return D_MAPART_LAND_COLLABORATOR_DAO.selectByLand(land);
    }

    public static List<DMapartLandCollaborator> selectByCollaborator(final UUID player) {
        return D_MAPART_LAND_COLLABORATOR_DAO.selectByPlayer(player);
    }

    public static DMapartLandCollaborator selectByLandAndCollaborator(final long land, final UUID player) {
        return D_MAPART_LAND_COLLABORATOR_DAO.selectByLandAndPlayer(land, player);
    }

    public static void save(final DMapartLandCollaborator entity) {
        if (entity.getVersionNo() <= 0) {
            D_MAPART_LAND_COLLABORATOR_DAO.insert(entity);
        } else {
            D_MAPART_LAND_COLLABORATOR_DAO.update(entity);
        }
    }

    public static void delete(final DMapartLandCollaborator entity) {
        D_MAPART_LAND_COLLABORATOR_DAO.delete(entity);
    }

    public static void createTable() {
        D_MAPART_LAND_COLLABORATOR_DAO.create();
    }

    public static List<DMapartLandCollaborator> selectAll() {
        return D_MAPART_LAND_COLLABORATOR_DAO.selectAll();
    }
}
