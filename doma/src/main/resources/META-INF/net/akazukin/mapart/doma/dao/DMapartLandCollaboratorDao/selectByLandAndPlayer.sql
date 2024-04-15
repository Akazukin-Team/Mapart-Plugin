select
    /*%expand*/*
from
    D_MAPART_LAND_COLLABORATOR
where
        LAND_ID = /* land */0
    and
        COLLABORATOR_UUID = /* player */'00aaa0a0-000a-000a-00aa-00a0000a0a00'
