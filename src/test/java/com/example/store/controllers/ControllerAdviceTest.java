package com.example.store.controllers;

import com.example.store.model.dto.*;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.enums.DocumentType;
import com.example.store.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDocWhenNotExistsTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/docs/controller/advice/test?id=1000"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getProjectWhenNotExistsTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/catalogs/controller/advice/test/project?id=1000"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addDocWithOutProjectTest() throws Exception {

        DocDTO docDTO = setDTOFields(DocumentType.RECEIPT_DOC);
        docDTO.setSupplier(setCompanyDTO(2));
        docDTO.setRecipient(setCompanyDTO(1));
        docDTO.setStorageTo(setStorageDTO(TestService.RECEIPT_FIELDS_ID));
        docDTO.setDocItems(setDocItemDTOList(TestService.ADD_VALUE));
        docDTO.setProject(new ProjectDTO());
        DocRequestDTO requestDTO = setDTO(docDTO);

        this.mockMvc.perform(
                        post("/api/v1/docs/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(String.format(Constants.NO_SUCH_PROJECT_MESSAGE, 0)));
    }


    DocDTO setDTOFields(DocumentType docType) {
        DocDTO dto = new DocDTO();
        dto.setDocType(docType.getValue());
        dto.setDateTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());
        dto.setProject(setProject(0));
        dto.setAuthor(setAuthorDTO(1));
        dto.setPayed(false);
        dto.setHold(false);

        return dto;
    }


    ProjectDTO setProject(int id) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(id);
        return projectDTO;
    }

    UserDTO setAuthorDTO(int id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        return userDTO;
    }

    CompanyDTO setCompanyDTO(int id) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(id);
        return companyDTO;
    }

    StorageDTO setStorageDTO(int id) {
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setId(id);
        return storageDTO;
    }

    static final List<Integer> ADDED_ITEM_IDS = List.of(1, 2, 3, 4);

    List<DocItemDTO> setDocItemDTOList(int value) {
        DocItemDTO first = new DocItemDTO();
        first.setItemId(ADDED_ITEM_IDS.get(0) + value);
        first.setPrice(10.00f * value);
        first.setQuantity(1 + value);
        DocItemDTO second = new DocItemDTO();
        second.setItemId(ADDED_ITEM_IDS.get(1) + value);
        second.setPrice(20.00f * value);
        second.setQuantity(2 + value);
        DocItemDTO third = new DocItemDTO();
        third.setItemId(ADDED_ITEM_IDS.get(2) + value);
        third.setPrice(30.00f * value);
        third.setQuantity(3 + value);
        DocItemDTO forth = new DocItemDTO();
        forth.setItemId(ADDED_ITEM_IDS.get(3) + value);
        forth.setPrice(40.00f * value);
        forth.setQuantity(4 + value);

        return List.of(first, second, third, forth);
    }

    DocRequestDTO setDTO(DocDTO docDTO) {
        DocRequestDTO dto = new DocRequestDTO();
        dto.setDocDTO(docDTO);
        return dto;
    }
}
