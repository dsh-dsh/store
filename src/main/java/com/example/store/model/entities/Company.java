package com.example.store.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "inn")
public class Company  implements EntityInterface {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(nullable = false)
    private String inn;

    private int kpp;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    private boolean isMine;
    private boolean isNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Company parent;

    private int code;
    private String phone;
    private String email;

    @Override
    public int getParentId() {
        return parent.getId();
    }
}
