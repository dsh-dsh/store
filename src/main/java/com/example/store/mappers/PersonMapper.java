package com.example.store.mappers;

import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class PersonMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    private final Condition<String, String> passwordExists = str -> !str.getSource().equals("");
    private final Converter<User, String> nameConverter = user -> user.getSource().getLastName();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(User.class, PersonDTO.class)
                .addMappings(mapper -> mapper.skip(User::getPassword, PersonDTO::setPassword));
        modelMapper.createTypeMap(PersonDTO.class, User.class)
                .addMappings(mapper -> mapper.using(stringToDateTime).map(PersonDTO::getRegDate, User::setRegTime))
                .addMappings(mapper -> mapper.using(stringToDate).map(PersonDTO::getBirthDate, User::setBirthDate))
                .addMappings(mapper -> mapper.skip(PersonDTO::getId, User::setId));
    }

    public PersonDTO mapToUserDTO(User user) {
        return modelMapper.map(user, PersonDTO.class);
    }

    public User mapToUser(PersonDTO personDTO) {
        return modelMapper.map(personDTO, User.class);
    }

}
