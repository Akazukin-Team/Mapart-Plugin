select M_MAPART_LAND.LAND_ID,
       OWNER_UUID,
       NAME,
       X,
       Z,
       HEIGHT,
       WIDTH,
       CREATED_DATE,
       STATUS,
       COLLABORATOR_UUID
--/*%expand*/*
from M_MAPART_LAND
         left outer join
     D_MAPART_LAND_COLLABORATOR
     on
         M_MAPART_LAND.LAND_ID = D_MAPART_LAND_COLLABORATOR.LAND_ID
where OWNER_UUID = /* player */'00aaa0a0-000a-000a-00aa-00a0000a0a00'
