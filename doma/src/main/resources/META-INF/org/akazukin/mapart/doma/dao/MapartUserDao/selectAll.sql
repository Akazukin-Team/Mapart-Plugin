SELECT --/*%expand*/*,
       M_MAPART_USER.PLAYER_UUID
        ,
       MAX_LAND
        ,
       LAND_ID
FROM M_MAPART_USER
         LEFT OUTER JOIN M_MAPART_LAND
                         ON M_MAPART_USER.PLAYER_UUID = M_MAPART_LAND.OWNER_UUID