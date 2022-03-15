package com.example.sklad.model.entities;

import com.example.sklad.model.enums.StorageType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "name")
public class Storage {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private StorageType type;

//    @OneToMany(fetch = FetchType.LAZY)
//    private List<ItemMoveDoc> itemMoveDocs;
}