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
@DiscriminatorValue("1")
public class ItemDoc extends Document implements DocInterface {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_from_id")
    private Storage storageFrom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_to_id")
    private Storage storageTo;

    @OneToMany(mappedBy = "itemDoc", fetch = FetchType.LAZY)
    private Set<DocumentItem> documentItems = new HashSet<>();

    public ItemDoc getThis() {
        return this;
    }

}
