package com.example.store.mappers;

import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class PersonMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(User.class, PersonDTO.class)
                .addMappings(mapper -> mapper.skip(User::getPassword, PersonDTO::setPassword))
                .addMappings(mapper -> mapper.using(dateToLongConverter).map(User::getBirthDate, PersonDTO::setBirthDate));
        modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(mapper -> mapper.using(nameConverter).map(src -> src, UserDTO::setName));
        modelMapper.createTypeMap(PersonDTO.class, User.class)
                .addMappings(mapper -> mapper.using(longToDateTimeConverter).map(PersonDTO::getRegDate, User::setRegTime))
                .addMappings(mapper -> mapper.using(longToDateConverter).map(PersonDTO::getBirthDate, User::setBirthDate))
                .addMappings(mapper -> mapper.skip(PersonDTO::getId, User::setId));
    }

    public PersonDTO mapToPersonDTO(User user) {
        return modelMapper.map(user, PersonDTO.class);
    }

    public UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User mapToUser(PersonDTO personDTO) {
        return modelMapper.map(personDTO, User.class);
    }

}
