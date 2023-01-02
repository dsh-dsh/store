package com.example.store.controllers;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.documents.DocToPaymentDTO;
import com.example.store.model.dto.requests.DocItemDTORequest;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.dto.requests.FixShortagesRequest;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.DocCrudService;
import com.example.store.services.DocItemService;
import com.example.store.services.HoldDocsService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/docs")
public class DocController {

    @Autowired
    private DocCrudService docCrudService;
    @Autowired
    private HoldDocsService holdDocsService;
    @Autowired
    private DocItemService docItemService;

    @GetMapping("/list")
    public ResponseEntity<ListResponse<DocToListDTO>> getDocuments(
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "0") long end) {
        List<DocToListDTO> docToListDTOS = docCrudService.getDocumentsByFilter(filter, start, end);
        return ResponseEntity.ok(new ListResponse<>(docToListDTOS));
    }

    @GetMapping
    public ResponseEntity<Response<DocDTO>> getDocumentById(
            @RequestParam int id,
            @RequestParam boolean copy) {
        DocDTO docDTO = docCrudService.getDocDTOById(id, copy);
        return ResponseEntity.ok(new Response<>(docDTO));
    }

    @GetMapping("/move/from/request")
    public ResponseEntity<Response<DocDTO>> getMoveDocFromRequest(@RequestParam int id) {
        DocDTO docDTO = docCrudService.getMoveDocFromRequest(id);
        return ResponseEntity.ok(new Response<>(docDTO));
    }

    @GetMapping("/new/number")
    public ResponseEntity<Response<Integer>> getNewDocNumber(@RequestParam String type) {
        int newDocNumber = docCrudService.getNewDocNumber(type);
        return ResponseEntity.ok(new Response<>(newDocNumber));
    }

    @PostMapping("/{saveTime}")
    public ResponseEntity<Response<String>> addDocument(
            @PathVariable String saveTime,
            @RequestBody DocRequestDTO docRequestDTO) {
        docCrudService.addDocument(docRequestDTO.getDocDTO(), saveTime);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    // todo add tests
    @PostMapping("/relative/{docId}")
    public ResponseEntity<Response<String>> addRelativeDocuments(
            @PathVariable int docId,
            @RequestBody ItemDocListRequestDTO itemDocListRequestDTO) {
        docCrudService.addRelativeDocuments(itemDocListRequestDTO, docId);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PutMapping("/{saveTime}")
    public ResponseEntity<Response<String>> updateDocument(
            @PathVariable String saveTime,
            @RequestBody DocRequestDTO docRequestDTO) {
        docCrudService.updateDocument(docRequestDTO.getDocDTO(), saveTime);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @DeleteMapping
    public ResponseEntity<Response<String>> softDeleteDocument(@RequestBody DocRequestDTO docRequestDTO) {
        docCrudService.softDeleteDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @DeleteMapping("/hard/delete")
    public ResponseEntity<Response<String>> hardDeleteDocuments() {
        Response<String> response = docCrudService.hardDeleteDocuments();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/hold/{id}")
    public ResponseEntity<Response<String>> holdDocument(@PathVariable int id) {
        docCrudService.holdDocument(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/un/hold/{id}")
    public ResponseEntity<Response<String>> unHoldDocument(@PathVariable int id) {
        docCrudService.unHoldDocument(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/hold/serial/{id}")
    public ResponseEntity<Response<String>> serialHoldDocument(@PathVariable int id) {
        docCrudService.serialHoldDocuments(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @GetMapping("/controller/advice/test")
    public ResponseEntity<Response<DocDTO>> getDocumentForControllerAdviceTest(@RequestParam int id) {
        DocDTO docDTO = docCrudService.getDocDTOForControllerAdviceTest(id);
        return ResponseEntity.ok(new Response<>(docDTO));
    }

    @PostMapping("/add/payment/{id}")
    public ResponseEntity<Response<String>> addPayment(@PathVariable int id) {
        docCrudService.addPayment(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/add/payments/{supplier}")
    public ResponseEntity<Response<String>> addSupplierPayments(@PathVariable String supplier) {
        docCrudService.addSupplierPayments(supplier);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/delete/payment/{id}")
    public ResponseEntity<Response<String>> deletePayment(@PathVariable int id) {
        docCrudService.deletePayment(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/item/ingredients")
    public ResponseEntity<Response<DocItemDTORequest>> getIngredientsOfItems(
            @RequestBody DocItemDTORequest request) {
        DocItemDTORequest response = docItemService.getIngredientsOfItems(request.getDocItemDTOList());
        return ResponseEntity.ok(new Response<>(response));
    }

    @GetMapping("/to/pay")
    public ResponseEntity<ListResponse<DocToPaymentDTO>> getDocsToPay(
            @RequestParam(defaultValue = "0") int companyId) {
        List<DocToPaymentDTO> docToListDTOS = docCrudService.getDocsDTOToPay(companyId);
        return ResponseEntity.ok(new ListResponse<>(docToListDTOS));
    }

    // todo add tests
    @GetMapping("/relative/doc/ids")
    public ResponseEntity<ListResponse<Integer>> getRelativeDocIds(
            @RequestParam(defaultValue = "0") int docId) {
        List<Integer> docToListDTOS = docCrudService.getRelativeDocIds(docId);
        return ResponseEntity.ok(new ListResponse<>(docToListDTOS));
    }

    @PostMapping("/fix/shortages")
    public ResponseEntity<Response<String>> fixShortagesAndHold (
            @RequestBody FixShortagesRequest request) {
        docCrudService.fixShortagesAndHold(request);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }
}
