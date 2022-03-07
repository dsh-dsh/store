package com.example.sklad.model.entities;

import com.example.sklad.model.entities.documents.ItemMoveDoc;
import com.example.sklad.model.enums.StorageType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Storage {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private StorageType type;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ItemMoveDoc> itemMoveDocs;
}