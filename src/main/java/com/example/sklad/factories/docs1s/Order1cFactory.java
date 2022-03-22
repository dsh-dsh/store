package com.example.sklad.factories.docs1s;

import com.example.sklad.factories.abstraction.DocFactory;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.model.enums.PaymentType;
import com.example.sklad.repositories.OrderDocRepository;
import com.example.sklad.services.CompanyService;
import com.example.sklad.services.ProjectService;
import com.example.sklad.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
//        orderDoc.setDateTime(docDTO.getTime().toLocalDateTime());
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

}
