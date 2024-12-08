package org.akazukin.mapart.doma.dao;

import java.util.List;
import org.akazukin.mapart.doma.entity.MMapartWorld;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao
public interface MMapartWorldDao {
    @Script
    void create();

    @Select
    List<MMapartWorld> selectAll();

    @Select
    MMapartWorld selectBySize(long land);

    @Select
    MMapartWorld selectByName(String worldName);

    @Insert
    int insert(MMapartWorld entity);

    @Update
    int update(MMapartWorld entity);

    @Delete
    int delete(MMapartWorld entity);
}
