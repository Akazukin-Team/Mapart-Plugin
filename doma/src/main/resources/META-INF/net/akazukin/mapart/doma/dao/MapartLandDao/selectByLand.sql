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
from M_MAPART_LAND
         left outer join D_MAPART_LAND_COLLABORATOR
                         on M_MAPART_LAND.LAND_ID = D_MAPART_LAND_COLLABORATOR.LAND_ID
where M_MAPART_LAND.LAND_ID = /* landId */0
