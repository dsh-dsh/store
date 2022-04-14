package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    private int id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    private String password;

    private String phone;

    @JsonProperty("reg_date")
    private String regDate;

    @JsonProperty("birth_date")
    private String birthDate;

    private String role;

}
