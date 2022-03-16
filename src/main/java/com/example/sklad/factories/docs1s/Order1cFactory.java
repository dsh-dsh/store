package com.example.sklad.factories.docs1s;

import com.example.sklad.factories.abstraction.DocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
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

    protected ItemDocDTO itemDocDTO;

    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;


    @Override
    public OrderDoc addDocument(ItemDocDTO itemDocDTO) {

        OrderDoc orderDoc = new OrderDoc();
        orderDoc.setNumber(itemDocDTO.getNumber());
        orderDoc.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        orderDoc.setDocType(DocumentType.getByValue(itemDocDTO.getDocType()));
        orderDoc.setProject(projectService.getByName(itemDocDTO.getProject().getName()));
        orderDoc.setAuthor(userService.getByEmail(itemDocDTO.getAuthor().getEmail()));
        orderDoc.setIndividual(userService.getByEmail(itemDocDTO.getIndividual().getEmail()));
        orderDoc.setSupplier(companyService.getByName(itemDocDTO.getSupplier().getName()));
        orderDoc.setPaymentType(PaymentType.getByValue(itemDocDTO.getPaymentType()));
        orderDoc.setAmount(itemDocDTO.getAmount());
        orderDoc.setTax(itemDocDTO.getTax());
        orderDoc.setHold(itemDocDTO.isHold());

        return orderDocRepository.save(orderDoc);
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        return null;
    }

}
