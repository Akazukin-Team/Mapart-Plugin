select --/*%expand*/*,
    case
        when M_MAPART_USER.PLAYER_UUID is not null then M_MAPART_USER.PLAYER_UUID
        when M_MAPART_LAND.OWNER_UUID is not null then M_MAPART_LAND.OWNER_UUID
    end as PLAYER_UUID_, MAX_LAND, LAND_ID
from M_MAPART_USER
full outer join
        M_MAPART_LAND
    on
        M_MAPART_USER.PLAYER_UUID = M_MAPART_LAND.OWNER_UUID
where
    PLAYER_UUID_ = /* player */'00aaa0a0-000a-000a-00aa-00a0000a0a00'
