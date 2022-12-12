package com.example.store.mappers;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.documents.DocToPaymentDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocMapper extends MappingConverters {

    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;

    private final ModelMapper modelMapper;

    protected final Converter<ItemDoc, Float> docItemAmountConverter =
            doc -> docItemService.getItemsAmount(doc.getSource());

    protected final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            itemDoc -> docItemService.getItemDTOListByDoc(itemDoc.getSource()) ;

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

    public DocMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        modelMapper.createTypeMap(ItemDoc.class, DocToListDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(ItemDoc::getDateTime, DocToListDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(ItemDoc::getDocType, DocToListDTO::setDocType))
                .addMappings(mapper -> mapper.using(docItemAmountConverter).map(src -> src, DocToListDTO::setAmount));

        modelMapper.createTypeMap(OrderDoc.class, DocToListDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(OrderDoc::getDateTime, DocToListDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(OrderDoc::getDocType, DocToListDTO::setDocType));

        modelMapper.createTypeMap(OrderDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(OrderDoc::getDateTime, DocDTO::setDateTime))
                .addMappings(mapper -> mapper.using(paymentTypeConverter).map(OrderDoc::getPaymentType, DocDTO::setPaymentType))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(OrderDoc::getDocType, DocDTO::setDocType));

        modelMapper.createTypeMap(ItemDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(ItemDoc::getDateTime, DocDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docItemsConverter).map(src -> src, DocDTO::setDocItems))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(ItemDoc::getDocType, DocDTO::setDocType))
                .addMappings(mapper -> mapper.when(isCheck).using(checkInfoConverter).map(src -> src, DocDTO::setCheckInfo));

        modelMapper.createTypeMap(ItemDoc.class, DocToPaymentDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(ItemDoc::getDateTime, DocToPaymentDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(ItemDoc::getDocType, DocToPaymentDTO::setDocType))
                .addMappings(mapper -> mapper.using(docItemAmountConverter).map(src -> src, DocToPaymentDTO::setAmount))
                .addMappings(mapper -> mapper.using(supplierConverter).map(Document::getSupplier, DocToPaymentDTO::setSupplier));
    }

    public DocDTO mapToDocDTO(ItemDoc document) {
        return modelMapper.map(document, DocDTO.class);
    }
    public DocDTO mapToDocDTO(OrderDoc document) {
        return modelMapper.map(document, DocDTO.class);
    }

    public DocToListDTO mapToDocToListDTO(ItemDoc document) {
        return modelMapper.map(document, DocToListDTO.class);
    }
    public DocToListDTO mapToDocToListDTO(OrderDoc document) {
        return modelMapper.map(document, DocToListDTO.class);
    }

    public DocToPaymentDTO mapToDocToPaymentDTO(ItemDoc document) {
        return modelMapper.map(document, DocToPaymentDTO.class);
    }

}
