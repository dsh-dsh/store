package com.example.store.mappers;

import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.User1CDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.User;
import com.example.store.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class PersonMapper extends MappingConverters {

    @Autowired
    private UserService userService;

    private final ModelMapper modelMapper;

    protected final Converter<Integer, User> userParentConverter =
            src -> getUser(src.getSource());

    protected final Converter<Integer, User> userParentIdConverter =
            src -> getUserById(src.getSource());

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
                .addMappings(mapper -> mapper.using(userParentIdConverter).map(PersonDTO::getParentId, User::setParent))
                .addMappings(mapper -> mapper.skip(PersonDTO::getId, User::setId));
        modelMapper.createTypeMap(User1CDTO.class, User.class)
                .addMappings(mapper -> mapper.map(User1CDTO::getId, User::setId))
                .addMappings(mapper -> mapper.map(User1CDTO::getName, User::setLastName))
                .addMappings(mapper -> mapper.using(userParentConverter).map(User1CDTO::getParentId, User::setParent))
                .addMappings(mapper -> mapper.using(longToDateConverter).map(User1CDTO::getBirthDate, User::setBirthDate));
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

    public User mapToUser(User1CDTO user1CDTO) {
        return modelMapper.map(user1CDTO, User.class);
    }

    private User getUser(int code) {
        if(code == 0) return null;
        return userService.getByCode(code);
    }

    private User getUserById(int userId) {
        if(userId == 0) return null;
        return userService.getById(userId);
    }

}
