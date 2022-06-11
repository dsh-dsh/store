package com.example.store.mappers;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.DocItemService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DocMapper extends MappingConverters {

    @Autowired
    private DocItemService docItemService;

    private final ModelMapper modelMapper;

    private final Converter<DocumentType, String> docTypeConverter = type -> type.getSource().getValue();
    private final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            itemDoc -> docItemService.getItemDTOListByDoc(itemDoc.getSource()) ;

    public DocMapper() {
        this.modelMapper = new ModelMapper();

        modelMapper.createTypeMap(OrderDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(docTypeConverter).map(OrderDoc::getDocType, DocDTO::setDocType))
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(OrderDoc::getDateTime, DocDTO::setTime));

        modelMapper.createTypeMap(ItemDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(docItemsConverter).map(src -> src, DocDTO::setDocItems))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(ItemDoc::getDocType, DocDTO::setDocType))
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(ItemDoc::getDateTime, DocDTO::setTime))
                .addMappings(mapper -> mapper.when(isCheck).using(checkInfoConverter).map(src -> src, DocDTO::setCheckInfo));
    }

    public DocDTO mapToDocDTO(ItemDoc document) {
        return modelMapper.map(document, DocDTO.class);
    }
    public DocDTO mapToDocDTO(OrderDoc document) {
        return modelMapper.map(document, DocDTO.class);
    }

}
