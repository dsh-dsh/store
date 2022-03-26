package com.example.store.model.entities.documents;

import com.example.store.model.entities.Company;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.User;
import com.example.store.model.enums.DocumentType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"number", "dateTime"})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="d_type",
        discriminatorType = DiscriminatorType.INTEGER)
public class Document {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int number;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type")
    private DocumentType docType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "individual_id")
    private User individual;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    private Company recipient;

    private boolean isPayed;

    private boolean isHold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_document_id")
    private Document baseDocument;

}
