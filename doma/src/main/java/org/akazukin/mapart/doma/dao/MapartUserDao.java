package org.akazukin.mapart.doma.dao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import org.akazukin.mapart.doma.entity.MapartUser;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.SelectType;

@Dao
public interface MapartUserDao {
    @Select(strategy = SelectType.COLLECT)
    <R> R selectAll(Collector<MapartUser, ?, R> collector);

    @Select
    List<MapartUser> selectByPlayer(UUID player);
}
