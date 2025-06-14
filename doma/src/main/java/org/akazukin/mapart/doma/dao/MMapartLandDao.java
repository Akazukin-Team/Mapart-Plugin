package org.akazukin.mapart.doma.dao;

import org.akazukin.mapart.doma.entity.MMapartLand;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface MMapartLandDao {
    @Script
    void create();

    @Select
    List<MMapartLand> selectAll();

    @Select
    MMapartLand selectByLand(long land);

    @Select
    List<MMapartLand> selectByOwner(UUID player);

    @Select
    List<MMapartLand> selectBySize(int size);

    @Select
    int missingLoc(long size);

    @Select
    int missingLand();

    @Insert
    int insert(MMapartLand entity);

    @Update
    int update(MMapartLand entity);

    @Delete
    int delete(MMapartLand entity);

    @Select
    MMapartLand selectBySizeAndLocation(long size, long locationId);
}
