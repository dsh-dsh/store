package com.example.store.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SettingDTOList {
    private UserDTO user;
    private List<SettingDTO> settings;

}
