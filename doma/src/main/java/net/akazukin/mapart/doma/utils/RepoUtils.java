package net.akazukin.mapart.doma.utils;

import java.util.UUID;
import net.akazukin.mapart.doma.dto.MapartLandDto;
import net.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
import net.akazukin.mapart.doma.repo.MapartLandRepo;

public class RepoUtils {
    public static MapartLandDto[] getMapartLandsByCollaborator(final UUID collaborator) {
        return DMapartLandCollaboratorRepo.selectByCollaborator(collaborator)
                .stream()
                .map(land -> MapartLandRepo.selectByLand(land.getLandId()))
                .toArray(MapartLandDto[]::new);
    }
}
