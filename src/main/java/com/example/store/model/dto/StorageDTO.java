package com.example.store.model.dto;

import com.example.store.model.entities.Storage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageDTO {

    private int id;
    private String name;
    private String type;

    public StorageDTO(Storage storage) {
        this.id = storage.getId();
        this.name = storage.getName();
        this.type = storage.getType().toString();
    }
}
