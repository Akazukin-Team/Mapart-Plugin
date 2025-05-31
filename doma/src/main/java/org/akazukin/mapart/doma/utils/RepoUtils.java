package org.akazukin.mapart.doma.utils;

import org.akazukin.mapart.doma.dto.MapartLandDto;
import org.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
import org.akazukin.mapart.doma.repo.MapartLandRepo;

import java.util.UUID;

public class RepoUtils {
    public static MapartLandDto[] getMapartLandsByCollaborator(final UUID collaborator) {
        return DMapartLandCollaboratorRepo.selectByCollaborator(collaborator)
                .stream()
                .map(land -> MapartLandRepo.selectByLand(land.getLandId()))
                .toArray(MapartLandDto[]::new);
    }
}
