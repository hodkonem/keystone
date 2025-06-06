package ru.itwizardry.micro.auth.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itwizardry.micro.common.jwt.entities.Role;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        return Role.valueOf(dbData);
    }
}
