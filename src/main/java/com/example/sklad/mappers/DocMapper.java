package com.example.sklad.mappers;

import com.example.sklad.model.dto.CheckInfoDTO;
import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.dto.documents.OrderDTO;
import com.example.sklad.model.entities.CheckInfo;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.services.CheckInfoService;
import com.example.sklad.services.DocItemService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocMapper extends MappingConverters {

    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;

    private final ModelMapper modelMapper;

    protected final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            doc -> docItemService.getItemDTOListByDoc(doc.getSource());

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

    public DocMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(ItemDoc.class, DocDTO.class)
//                .addMappings(mapper -> mapper.using(dateTimeConverter).map(ItemDoc::getDateTime, DocDTO::setTime))
                .addMappings(mapper -> mapper.using(docItemsConverter).map(ItemDoc::getThis, DocDTO::setDocItems))
                .addMappings(mapper -> mapper.using(checkInfoConverter).map(ItemDoc::getThis, DocDTO::setCheckInfo));
    }

    public DocDTO mapToDocDTO(ItemDoc itemDoc) {
        return modelMapper.map(itemDoc, DocDTO.class);
    }

}
