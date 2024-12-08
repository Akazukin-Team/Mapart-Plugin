package org.akazukin.mapart.doma.dao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import org.akazukin.mapart.doma.entity.MapartLand;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.SelectType;

@Dao
public interface MapartLandDao {
    @Select(strategy = SelectType.COLLECT)
    <R> R selectAll(Collector<MapartLand, ?, R> collector);

    @Select
    List<MapartLand> selectByLand(long landId);

    @Select(strategy = SelectType.COLLECT)
    <R> R selectByPlayer(UUID player, Collector<MapartLand, ?, R> collector);

    @Select
    List<MapartLand> selectByCollaborator(UUID collaborator);
}
