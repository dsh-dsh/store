package com.example.store.factories.docs1s;

import com.example.store.factories.abstraction.DocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.services.CompanyService;
import com.example.store.services.ProjectService;
import com.example.store.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Order1cFactory implements DocFactory {

    protected DocDTO docDTO;

    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;


    @Override
    public OrderDoc addDocument(DocDTO docDTO) {

        OrderDoc orderDoc = new OrderDoc();
        orderDoc.setNumber(docDTO.getNumber());
        orderDoc.setDateTime(LocalDateTime.parse(docDTO.getTime()));
        orderDoc.setDocType(DocumentType.getByValue(docDTO.getDocType()));
        orderDoc.setProject(projectService.getByName(docDTO.getProject().getName()));
        orderDoc.setAuthor(userService.getByEmail(docDTO.getAuthor().getEmail()));
        orderDoc.setIndividual(userService.getByEmail(docDTO.getIndividual().getEmail()));
        orderDoc.setSupplier(companyService.getByName(docDTO.getSupplier().getName()));
        orderDoc.setPaymentType(PaymentType.getByValue(docDTO.getPaymentType()));
        orderDoc.setAmount(docDTO.getAmount());
        orderDoc.setTax(docDTO.getTax());
        orderDoc.setHold(docDTO.isHold());

        return orderDocRepository.save(orderDoc);
    }

    @Override
    public DocInterface updateDocument(DocDTO docDTO) {
        return null;
    }

    @Override
    public void deleteDocument(int docId) {}

    @Override
    public boolean holdDocument(Document document) {return true;}

    @Override
    public void unHoldDocument(Document document){}

}
