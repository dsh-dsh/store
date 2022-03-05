package com.example.sklad.model.entities.documents;

import com.example.sklad.model.entities.Company;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.Storage;
import com.example.sklad.model.entities.User;
import com.example.sklad.model.enums.DocumentType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Document {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    private boolean isPayed;

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
    @JoinColumn(name = "storageTo_id")
    private Storage storageTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_id", nullable = false)
    private Storage storage;

    @OneToMany(mappedBy = "document")
    private Set<DocumentItem> documentItems = new HashSet<>();

}
