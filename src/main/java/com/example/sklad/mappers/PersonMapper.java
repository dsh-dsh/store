package com.example.sklad.mappers;

import com.example.sklad.model.dto.PersonDTO;
import com.example.sklad.model.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper extends Converters {

    private final ModelMapper modelMapper;

    public PersonMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(User.class, PersonDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(User::getRegTime, PersonDTO::setRegTime))
                .addMappings(mapper -> mapper.using(dateConverter).map(User::getBirthDate, PersonDTO::setBirthDate));
    }

    public PersonDTO mapToUserDTO(User user) {
        return modelMapper.map(user, PersonDTO.class);
    }

}
