package com.example.sklad.mappers;

import com.example.sklad.model.dto.PersonDTO;
import com.example.sklad.model.entities.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class PersonMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    private final Condition<String, String> passwordExists = str -> !str.getSource().equals("");

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(User.class, PersonDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(User::getRegTime, PersonDTO::setRegDate))
                .addMappings(mapper -> mapper.using(dateConverter).map(User::getBirthDate, PersonDTO::setBirthDate))
                .addMappings(mapper -> mapper.skip(User::getPassword, PersonDTO::setPassword));
        modelMapper.createTypeMap(PersonDTO.class, User.class)
                .addMappings(mapper -> mapper.using(longToTime).map(PersonDTO::getRegDate, User::setRegTime))
                .addMappings(mapper -> mapper.using(longToDate).map(PersonDTO::getBirthDate, User::setBirthDate))
                .addMappings(mapper -> mapper.skip(PersonDTO::getId, User::setId));
//                .addMappings(mapper -> mapper.when(passwordExists).map());
    }

    public PersonDTO mapToUserDTO(User user) {
        return modelMapper.map(user, PersonDTO.class);
    }

    public User mapToUser(PersonDTO personDTO) {
        return modelMapper.map(personDTO, User.class);
    }

}
