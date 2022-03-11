package com.example.sklad.factories;

import com.example.sklad.model.dto.OrderDTO;
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
public class OrderFactory implements DocFactory {

    private OrderDTO orderDTO;

    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;

    @Override
    public OrderDoc createDocument() {
        return null;
    }

    @Override
    public DocInterface createDocumentFrom1C() {
        if (orderDTO == null) return null;

        OrderDoc orderDoc = new OrderDoc();
        orderDoc.setNumber(orderDTO.getNumber());
        orderDoc.setDateTime(orderDTO.getTime().toLocalDateTime());
        orderDoc.setDocType(DocumentType.getByValue(orderDTO.getDocType()));
        orderDoc.setProject(projectService.getByName(orderDTO.getProject().getName()));
        orderDoc.setAuthor(userService.getByEmail(orderDTO.getAuthor().getEmail()));
        orderDoc.setIndividual(userService.getByEmail(orderDTO.getIndividual().getEmail()));
        orderDoc.setCompany(companyService.getByName(orderDTO.getCompany().getName()));
        orderDoc.setPaymentType(PaymentType.getByValue(orderDTO.getPaymentType()));
        orderDoc.setAmount(orderDTO.getAmount());
        orderDoc.setHold(orderDTO.isHold());

        return orderDocRepository.save(orderDoc);
    }

    public void setOrderDTO(OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
    }

}
