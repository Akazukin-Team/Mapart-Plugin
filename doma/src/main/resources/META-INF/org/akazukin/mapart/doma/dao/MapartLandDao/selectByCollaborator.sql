select M_MAPART_LAND.LAND_ID,
       SIZE,
       LOCATION_ID,
       OWNER_UUID,
       NAME,
       HEIGHT,
       WIDTH,
       CREATED_DATE,
       STATUS,
       COLLABORATOR_UUID
--/*%expand*/*
from M_MAPART_LAND right inner join D_MAPART_LAND_COLLABORATOR
on M_MAPART_LAND.LAND_ID = D_MAPART_LAND_COLLABORATOR.LAND_ID
where
    COLLABORATOR_UUID = /* collaborator */'00aaa0a0-000a-000a-00aa-00a0000a0a00'
