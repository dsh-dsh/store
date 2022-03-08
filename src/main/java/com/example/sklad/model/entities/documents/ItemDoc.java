package com.example.sklad.model.entities.documents;

import com.example.sklad.model.entities.Company;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.Storage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ItemDoc extends Document implements DocInterface {

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

    @OneToMany(mappedBy = "itemDoc")
    private Set<DocumentItem> documentItems = new HashSet<>();

}
