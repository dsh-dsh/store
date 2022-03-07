package com.example.sklad.model.entities.documents;

import com.example.sklad.model.entities.Company;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.Storage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class ItemMoveDoc extends Document implements DocInterface {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    private Company recipient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storageTo_id")
    private Storage storageFrom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storageTo_id", insertable = false, updatable = false)
    private Storage storageTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_id", nullable = false)
    private Storage storage;

    @OneToMany(mappedBy = "itemMoveDoc")
    private Set<DocumentItem> documentItems = new HashSet<>();

}
