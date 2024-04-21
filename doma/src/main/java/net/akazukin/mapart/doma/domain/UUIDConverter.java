package net.akazukin.mapart.doma.domain;

import java.util.UUID;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class UUIDConverter implements DomainConverter<UUID, String> {
    @Override
    public String fromDomainToValue(final UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID fromValueToDomain(final String s) {
        if (s == null) return null;
        return UUID.fromString(s);
    }
}
