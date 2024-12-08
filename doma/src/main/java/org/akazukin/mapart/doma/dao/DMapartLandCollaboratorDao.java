package org.akazukin.mapart.doma.dao;

import java.util.List;
import java.util.UUID;
import org.akazukin.mapart.doma.entity.DMapartLandCollaborator;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao
public interface DMapartLandCollaboratorDao {
    @Script
    void create();

    @Select
    List<DMapartLandCollaborator> selectAll();

    @Select
    List<DMapartLandCollaborator> selectByLand(long land);

    @Select
    List<DMapartLandCollaborator> selectByPlayer(UUID player);

    @Select
    DMapartLandCollaborator selectByLandAndPlayer(long land, UUID player);

    @Insert
    int insert(DMapartLandCollaborator entity);

    @Update
    int update(DMapartLandCollaborator entity);

    @Delete
    int delete(DMapartLandCollaborator entity);
}
