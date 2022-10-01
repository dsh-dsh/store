package com.example.store.controllers;

import com.example.store.model.dto.*;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.CalculationService;
import com.example.store.services.ItemService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private CalculationService calculationService;

    @GetMapping("/tree")
    public ResponseEntity<ListResponse<ItemDTOForTree>> getItemTree() {
        List<ItemDTOForTree> list = itemService.getItemDTOTree();
        return ResponseEntity.ok(new ListResponse<>(list));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResponse<ItemDTOForList>> getItemList(@RequestParam(defaultValue = "0") long time) {
        List<ItemDTOForList> list = itemService.getItemDTOList(time);
        return ResponseEntity.ok(new ListResponse<>(list));
    }

    // todo add tests
    @GetMapping("/dirs/list")
    public ResponseEntity<ListResponse<ItemDTO>> getItemDirsList() {
        List<ItemDTO> dirs = itemService.getItemDirList();
        return ResponseEntity.ok(new ListResponse<>(dirs));
    }

    @GetMapping
    public ResponseEntity<Response<ItemDTO>> getItemById(
            @RequestParam long date,
            @RequestParam int id) {
        ItemDTO itemDTO = itemService.getItemDTOById(id, date);
        return ResponseEntity.ok(new Response<>(itemDTO));
    }

    @PostMapping
    public ResponseEntity<Response<String>> addItem(@RequestBody ItemDTO itemDTO) {
        itemService.setNewItem(itemDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PutMapping("/{date}")
    public ResponseEntity<Response<String>> updateItem(
            @PathVariable long date,
            @RequestBody ItemDTO itemDTO) {
        itemService.updateItem(itemDTO, date);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> softDeleteItemById(@PathVariable int id) {
        itemService.softDeleteItem(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @GetMapping("/calculation")
    public ResponseEntity<Response<CalculationDTO>> getCalculation(
            @RequestParam long date,
            @RequestParam int id) {
        CalculationDTO dto = calculationService.getCalculation(id, date);
        return ResponseEntity.ok(new Response<>(dto));
    }

}
