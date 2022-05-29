package com.example.store.mappers;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import org.modelmapper.Condition;
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

    private final Condition<Document, Document> isCheck =
            doc -> doc.getSource().getDocType().equals(DocumentType.CHECK_DOC);

    private final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            doc -> docItemService.getItemDTOListByDoc(doc.getSource());

    private final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

    public DocMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Document.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(docItemsConverter).map(Document::getThis, DocDTO::setDocItems))
                .addMappings(mapper -> mapper.using(checkInfoConverter).map(Document::getThis, DocDTO::setCheckInfo));
    }

    public DocDTO mapToDocDTO(Document document) {
        return modelMapper.map(document, DocDTO.class);
    }

}
