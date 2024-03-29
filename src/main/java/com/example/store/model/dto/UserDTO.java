package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private int id;
    private int code;
    private String email;
    private String name;


    public UserDTO(int id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
