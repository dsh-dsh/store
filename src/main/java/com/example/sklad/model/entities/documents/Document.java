package com.example.sklad.model.entities.documents;

import com.example.sklad.model.entities.Project;
import com.example.sklad.model.entities.User;
import com.example.sklad.model.enums.DocumentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Document {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long number;

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

    private boolean isHold;

}
