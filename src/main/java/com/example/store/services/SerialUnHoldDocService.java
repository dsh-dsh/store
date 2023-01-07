package com.example.store.services;

import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SerialUnHoldDocService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private User systemUser;
    @Autowired
    @Qualifier("blockingUserIds")
    private List<Integer> blockingUserIds;

    // todo add tests

    public void unHold(Document document) {
        document = getFirstDocIfCheckDocInSequence(document);
        LocalDateTime from = document.getDateTime();
        documentRepository.setIsHold(false, from);
        lotMoveRepository.deleteLotMovements(from);
        lotRepository.deleteLots(from);
        documentRepository.softDeleteDocs(systemUser.getId(), from);
    }

    protected Document getFirstDocIfCheckDocInSequence(Document document) {
        List<Document> documents = getSequence(document);
        Document check = null;
        if (isCheckDocInSequence(document, documents)) {
            LocalDateTime from = document.getDateTime().toLocalDate().atStartOfDay();
            LocalDateTime to = from.plusDays(1);
            check = documentRepository.findFistCheckDocOfDay(blockingUserIds, from, to).orElse(null);
        }
        if(check != null && check.getDateTime().isBefore(document.getDateTime())) {
            return check;
        } else {
            return document;
        }
    }

    protected List<Document> getSequence(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndDateTimeAfter(true, document.getDateTime(),
                        Sort.by(Constants.DATE_TIME_STRING));
        documents.add(0, document);
        return documents;
    }

    protected boolean isCheckDocInSequence(Document document, List<Document> documents) {
        boolean checkDocInSequence = false;
        for(Document doc : documents) {
            if(!doc.getDateTime().toLocalDate().equals(document.getDateTime().toLocalDate())) break;
            int authorId = doc.getAuthor().getId();
            for (int id : blockingUserIds) {
                if(authorId == id) {
                    checkDocInSequence = true;
                    break;
                }
            }
        }
        return checkDocInSequence;
    }

}
