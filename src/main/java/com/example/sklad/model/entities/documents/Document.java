package com.example.sklad.model.entities.documents;

import com.example.sklad.model.entities.Project;
import com.example.sklad.model.entities.User;
import com.example.sklad.model.enums.DocumentType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "individual_id")
    private User individual;

    private boolean isPayed;

}
