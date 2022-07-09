package com.example.store.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingDTO {
    private UserDTO user;
    private String type;
    private int property;
}
