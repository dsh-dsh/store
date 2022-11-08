package com.example.store.model.entities;

import com.example.store.model.entities.documents.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "doc_info")
public class DocInfo {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "supplier_doc_number")
    private String supplierDocNumber;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
}
