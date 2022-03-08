package com.example.sklad.model.entities;

import com.example.sklad.model.enums.StorageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Storage {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private StorageType type;

//    @OneToMany(fetch = FetchType.LAZY)
//    private List<ItemMoveDoc> itemMoveDocs;
}