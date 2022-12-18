package com.example.store.services;

import com.example.store.mappers.DocInfoMapper;
import com.example.store.model.dto.DocInfoDTO;
import com.example.store.model.entities.DocInfo;
import com.example.store.model.entities.documents.Document;
import com.example.store.repositories.DocInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocInfoService {

    @Autowired
    private DocInfoRepository docInfoRepository;
    @Autowired
    private DocInfoMapper docInfoMapper;

    public DocInfo getDocInfoByDocument(Document document) {
        return docInfoRepository.findByDocument(document).orElse(null);
    }

    public DocInfoDTO getDocInfoDTOByDocument(Document document) {
        DocInfo docInfo = getDocInfoByDocument(document);
        return docInfo == null? null : docInfoMapper.mapToDTO(docInfo);
    }

    public void setDocInfo(Document document, DocInfoDTO docInfoDTO) {
        if(docInfoDTO == null) return;
        DocInfo docInfo = docInfoMapper.mapToDocInfo(docInfoDTO);
        docInfo.setDocument(document);
        docInfoRepository.save(docInfo);
    }

}
