package net.akazukin.mapart.doma.dao;

import java.util.List;
import java.util.UUID;
import net.akazukin.mapart.doma.entity.MMapartLand;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao
public interface MMapartLandDao {
    @Script
    void create();

    @Select
    List<MMapartLand> selectAll();

    @Select
    MMapartLand selectByLand(long land);

    @Select
    List<MMapartLand> selectByPlayer(UUID player);

    @Insert
    int insert(MMapartLand entity);

    @Update
    int update(MMapartLand entity);

    @Delete
    int delete(MMapartLand entity);
}
