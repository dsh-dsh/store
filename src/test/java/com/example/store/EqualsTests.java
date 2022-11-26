package com.example.store;

import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EqualsTests {

    @Test
    void equalsDocumentTest() {
        LocalDateTime time = LocalDateTime.now();

        assertNotEquals(getDocument(1,1, time), getDocument(2,2, time));
        assertNotEquals(getDocument(1,1, time), getDocument(2,1, time.plusSeconds(1)));
        assertEquals(getDocument(1,1, time), getDocument(2,1, time));
    }

    @Test
    void equalsCheckInfoTest() {
        assertNotEquals(getCheckInfo(1, 1L), getCheckInfo(2, 2L));
        assertNotEquals(getCheckInfo(1, 1L), getCheckInfo(1, 2L));
        assertNotEquals(getCheckInfo(1, 1L), getCheckInfo(2, 1L));
        assertEquals(getCheckInfo(1, 1L), getCheckInfo(1, 1L));
    }

    @Test
    void equalsCompanyTest() {
        assertNotEquals(getCompany("123"), getCompany("321"));
        assertEquals(getCompany("123"), getCompany("123"));
    }

    @Test
    void equalsDocumentItemTest() {
        ItemDoc itemDoc1 = getItemDoc(1,1, LocalDateTime.now());
        ItemDoc itemDoc2 = getItemDoc(2,2, LocalDateTime.now().plusSeconds(1));
        Item item1 = getItem("123", LocalDateTime.now(), 123);
        Item item2 = getItem("321", LocalDateTime.now().plusSeconds(1), 321);

        assertNotEquals(getDocumentItem(itemDoc1, item1), getDocumentItem(itemDoc1, item2));
        assertNotEquals(getDocumentItem(itemDoc1, item1), getDocumentItem(itemDoc2, item1));
        assertNotEquals(getDocumentItem(itemDoc1, item1), getDocumentItem(itemDoc2, item2));
        assertEquals(getDocumentItem(itemDoc1, item1), getDocumentItem(itemDoc1, item1));
    }

    @Test
    void equalsIngredientTest() {
        Item parent1 = getItem("p123", LocalDateTime.now(), 12);
        Item parent2 = getItem("p321", LocalDateTime.now().plusSeconds(1), 34);
        Item child1 = getItem("ch123", LocalDateTime.now().plusSeconds(2), 56);
        Item child2 = getItem("ch321", LocalDateTime.now().plusSeconds(3), 78);

        assertNotEquals(getIngredient(parent1, child1), getIngredient(parent2, child2));
        assertNotEquals(getIngredient(parent1, child2), getIngredient(parent2, child2));
        assertNotEquals(getIngredient(parent2, child1), getIngredient(parent2, child2));
        assertEquals(getIngredient(parent2, child2), getIngredient(parent2, child2));
    }

    @Test
    void equalsItemTest() {
        Item item1 = getItem("123", LocalDateTime.now(), 123);
        Item item2 = getItem("321", LocalDateTime.now().plusSeconds(1), 321);

        assertNotEquals(item1, item2);
        assertNotEquals(item2, item1);
        assertEquals(item1, item1);
    }

    @Test
    void equalsLotTest() {
        ItemDoc itemDoc1 = getItemDoc(1,1, LocalDateTime.now());
        Item item1 = getItem("123", LocalDateTime.now(), 123);
        Item item2 = getItem("321", LocalDateTime.now().plusSeconds(1), 321);
        DocumentItem documentItem1 = getDocumentItem(itemDoc1, item1);
        DocumentItem documentItem2 = getDocumentItem(itemDoc1, item2);
        LocalDateTime time = LocalDateTime.now();

        assertNotEquals(getLot(documentItem1, time), getLot(documentItem2, time));
        assertNotEquals(getLot(documentItem1, time), getLot(documentItem1, time.plusSeconds(1)));
        assertEquals(getLot(documentItem1, time), getLot(documentItem1, time));
    }

    @Test
    void equalsLotMoveTest() {
        ItemDoc itemDoc1 = getItemDoc(1,1, LocalDateTime.now());
        Item item1 = getItem("123", LocalDateTime.now(), 123);
        Item item2 = getItem("321", LocalDateTime.now().plusSeconds(1), 321);
        DocumentItem documentItem1 = getDocumentItem(itemDoc1, item1);
        DocumentItem documentItem2 = getDocumentItem(itemDoc1, item2);
        LocalDateTime time = LocalDateTime.now();
        Lot lot1 = getLot(documentItem1, time);
        Lot lot2 = getLot(documentItem2, time.plusSeconds(1));

        assertNotEquals(getLotMove(lot1, time), getLotMove(lot2, time.plusSeconds(1)));
        assertEquals(getLotMove(lot1, time), getLotMove(lot1, time));
    }

    @Test
    void equalsPriceTest() {
        Item item1 = getItem("123", LocalDateTime.now(), 123);
        Item item2 = getItem("321", LocalDateTime.now().plusSeconds(1), 321);
        LocalDate date = LocalDate.now();

        assertNotEquals(getPrice(item1, date), getPrice(item2, date));
        assertNotEquals(getPrice(item1, date), getPrice(item1, date.plusDays(1)));
        assertEquals(getPrice(item1, date), getPrice(item1, date));
    }

    @Test
    void equalsUserTest() {
        assertNotEquals(getUser("user@mail.com"), getUser("user.two@mail.com"));
        assertEquals(getUser("user@mail.com"), getUser("user@mail.com"));
    }

    User getUser(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    Price getPrice(Item item, LocalDate date) {
        Price price = new Price();
        price.setItem(item);
        price.setDate(date);
        return price;
    }

    LotMovement getLotMove(Lot lot, LocalDateTime time) {
        LotMovement lotMove = new LotMovement();
        lotMove.setLot(lot);
        lotMove.setMovementTime(time);
        return lotMove;
    }

    Lot getLot(DocumentItem documentItem, LocalDateTime time) {
        Lot lot = new Lot();
        lot.setDocumentItem(documentItem);
        lot.setLotTime(time);
        return lot;
    }

    Ingredient getIngredient(Item parent, Item child) {
        Ingredient ingredient = new Ingredient();
        ingredient.setParent(parent);
        ingredient.setChild(child);
        return ingredient;
    }

    DocumentItem getDocumentItem(ItemDoc itemDoc, Item item) {
        DocumentItem documentItem = new DocumentItem();
        documentItem.setItemDoc(itemDoc);
        documentItem.setItem(item);
        return documentItem;
    }

    Item getItem(String name, LocalDateTime time, int number) {
        Item item = new Item();
        item.setName(name);
        item.setRegTime(time);
        item.setNumber(number);
        return item;
    }

    Company getCompany(String inn) {
        Company company = new Company();
        company.setInn(inn);
        return company;
    }

    CheckInfo getCheckInfo(int checkNumber, long cashRegisterNumber) {
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setCheckNumber(checkNumber);
        checkInfo.setCashRegisterNumber(cashRegisterNumber);
        return checkInfo;
    }

    Document getDocument(int id, int number, LocalDateTime time) {
        Document doc = new Document();
        doc.setId(id);
        doc.setNumber(number);
        doc.setDateTime(time);
        return doc;
    }

    ItemDoc getItemDoc(int id, int number, LocalDateTime time) {
        ItemDoc doc = new ItemDoc();
        doc.setId(id);
        doc.setNumber(number);
        doc.setDateTime(time);
        return doc;
    }

}
