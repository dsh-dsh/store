package com.example.store.services;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.model.enums.PriceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
class PriceServiceTest {

    @Autowired
    private PriceService priceService;
    @Autowired
    private ItemService itemService;


    @Sql(value = "/sql/price/addSomePrices.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPriceListOfItemTest() {
        Item item = itemService.getItemById(7);
        List<Price> priceList = priceService.getPriceListOfItem(item, LocalDate.now());
        assertEquals(2, priceList.size());
        assertEquals(350, priceList.get(0).getValue());
        assertEquals(400, priceList.get(1).getValue());
    }

    @Sql(value = "/sql/price/addSomePrices.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemPriceListTest() {
        Item item = itemService.getItemById(7);
        List<Price> prices = priceService.getItemPriceList(item);
        assertEquals(6, prices.size());
        assertEquals(200, prices.get(0).getValue());
        assertEquals(350, prices.get(5).getValue());
    }

    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateItemPricesTest() {
        Item item = itemService.getItemById(5);
        PriceDTO dto1 = getPriceDTO(400f, PriceType.RETAIL);
        PriceDTO dto2 = getPriceDTO(500f, PriceType.DELIVERY);
        priceService.updateItemPrices(item, List.of(dto1, dto2), LocalDate.now());
        List<Price> prices = priceService.getItemPriceList(item);
        assertEquals(4, prices.size());
        assertEquals(180, prices.get(0).getValue());
        assertEquals(500, prices.get(3).getValue());
    }

    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addNewPriceOnUpdatePriceIfNewPriceLaterThanLastPriceTest() {
        Item item = itemService.getItemById(5);
        PriceDTO dto = getPriceDTO(500f, PriceType.RETAIL);
        List<Price> prices = priceService.getItemPriceList(item);
        priceService.updatePrice(dto, prices, item);
        assertEquals(3, priceService.getItemPriceList(item).size());
    }

    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void notUpdatePriceIfNewPriceBeforeLastPriceTest() {
        Item item = itemService.getItemById(5);
        PriceDTO dto = getPriceDTO(500f, PriceType.RETAIL);
        dto.setDate(946674000000L);
        List<Price> prices = priceService.getItemPriceList(item);
        priceService.updatePrice(dto, prices, item);
        assertEquals(2, priceService.getItemPriceList(item).size());
    }

    @Sql(value = {"/sql/price/after.sql",
            "/sql/price/setPriceToOrigin.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateCurrentPriceIfNewPriceInSameDateTest() {
        Item item = itemService.getItemById(5);
        PriceDTO dto = getPriceDTO(500f, PriceType.RETAIL);
        dto.setDate(1330718400000L); // "2012/03/03"
        List<Price> prices = priceService.getItemPriceList(item);
        priceService.updatePrice(dto, prices, item);
        Price price = priceService.getPriceByItemAndType(item, PriceType.RETAIL, LocalDate.parse("2012-03-03"));
        assertEquals(500, price.getValue());
    }

    @Test
    void getPriceOfTypeTest() {
        Item item = new Item();
        Price priceRetail = new Price(0, item, 100f, LocalDate.now(), PriceType.RETAIL);
        Price priceDelivery = new Price(0, item, 200f, LocalDate.now(), PriceType.DELIVERY);
        List<Price> list = List.of(priceRetail, priceDelivery);
        assertEquals(priceRetail, priceService.getPriceOfType(list, PriceType.RETAIL));
        assertEquals(priceDelivery, priceService.getPriceOfType(list, PriceType.DELIVERY));
    }

    @Sql(value = "/sql/price/addPrices.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPriceByItemAndTypeTest() {
        Item item = itemService.getItemById(5);
        Price retailPrice = priceService.getPriceByItemAndType(item, PriceType.RETAIL, LocalDate.now());
        assertEquals(250.00f, retailPrice.getValue());
        Price deliveryPrice = priceService.getPriceByItemAndType(item, PriceType.DELIVERY, LocalDate.now());
        assertEquals(300.00f, deliveryPrice.getValue());
    }

    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addPricesTest() {
        Item item = itemService.getItemById(7);
        PriceDTO dto1 = getPriceDTO(100f, PriceType.RETAIL);
        PriceDTO dto2 = getPriceDTO(150f, PriceType.DELIVERY);
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setPrices(List.of(dto1, dto2));
        priceService.addPrices(item, itemDTO);
        List<Price> prices = priceService.getPriceListOfItem(item, LocalDate.now());
        assertEquals(2, prices.size());
        assertEquals(100, prices.get(0).getValue());
        assertEquals(150, prices.get(1).getValue());
    }

    @Sql(value = "/sql/price/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNewPriceTest() {
        Item item = itemService.getItemById(7);
        PriceDTO dto = getPriceDTO(100f, PriceType.RETAIL);
        priceService.setNewPrice(item, dto);
        Price price = priceService.getPriceByItemAndType(item, PriceType.RETAIL, LocalDate.now());
        assertNotNull(price);
        assertEquals(100, price.getValue());
    }

    private PriceDTO getPriceDTO(float value, PriceType type) {
        long date = 1643700600000L;
        PriceDTO dto = new PriceDTO();
        dto.setDate(date);
        dto.setType(type.toString());
        dto.setValue(value);
        return dto;
    }
}