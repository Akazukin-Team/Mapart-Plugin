package org.akazukin.mapart.doma.dao;

import java.util.List;
import java.util.UUID;
import org.akazukin.mapart.doma.entity.MMapartUser;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao
public interface MMapartUserDao {
    @Script
    void create();

    @Select
    List<MMapartUser> selectAll();

    @Select
    MMapartUser selectByPlayer(UUID player);

    @Insert
    int insert(MMapartUser entity);

    @Update
    int update(MMapartUser entity);

    @Delete
    int delete(MMapartUser entity);
}
