package com.example.store.utils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Constants {

    public static final int START_DOCUMENT_NUMBER = 1;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");

    public static final String SYSTEM_USER_EMAIL = "system@user.com";
    public static final String ADMIN_ROLE = "администратор";
    public static final String CASHIER_ROLE = "кассир";
    public static final String ACCOUNTANT_ROLE = "бухгалтер";
    public static final String CUSTOMER_ROLE = "покупатель";
    public static final String SYSTEM_ROLE = "покупатель";
    public static final String NONE_ROLE = "";

    public static final String CHECK_DOC_TYPE = "Чек ККМ";
    public static final String POSTING_DOC_TYPE = "Поступление";
    public static final String RECEIPT_DOC_TYPE = "Оприходование";
    public static final String MOVEMENT_DOC_TYPE = "Перемещение";
    public static final String WRITE_OFF_DOC_TYPE = "Списание";
    public static final String REQUEST_DOC_TYPE = "Заявка";
    public static final String WITHDRAW_ORDER_DOC_TYPE = "ПКО";
    public static final String CREDIT_ORDER_DOC_TYPE = "Расходный кассовый ордер";
    public static final String INVENTORY_DOC_TYPE = "Инвентаризация";

    public static final String STORE_STORE_TYPE = "склад";
    public static final String CAFE_STORE_TYPE = "склад кафе";
    public static final String RETAIL_STORE_TYPE = "розница";

    public static final String TAX_PAYMENT_TYPE = "Оплата налога";
    public static final String CLIENT_PAYMENT_TYPE = "Оплата от покупателя";
    public static final String SUPPLIER_PAYMENT_TYPE = "Оплата поставщику";
    public static final String OTHER_PAYMENT_TYPE = "Прочее";
    public static final String SALARY_PAYMENT_TYPE = "Выплата заработной платы";
    public static final String SALE_CASH_PAYMENT_TYPE = "Выручка наличными";
    public static final String SALE_CARD_PAYMENT_TYPE = "Выручка по картам";

    public static final String RETAIL_PRICE_TYPE = "Розничная";
    public static final String DELIVERY_PRICE_TYPE = "На доставку";

    public static final String KG = "кг.";
    public static final String LITER = "л.";
    public static final String PIECE = "шт.";
    public static final String PORTION = "порция";

    public static final String ENUM_NONE = "---";

    public static final String NONE = "";
    public static final String OK = "ok";

    public static final String KITCHEN = "Кухня";
    public static final String BAR = "Бар";

    public static final String BAD_REQUEST_MESSAGE = "invalid request";
    public static final String TRANSACTION_FAILED_MESSAGE = "transaction failed";
    public static final String SHORTAGE_OF_ITEM_MESSAGE = "there is shortage of item ";
    public static final String HOLD_FAILED_MESSAGE = "hold failed";
    public static final String UN_HOLD_FORBIDDEN_MESSAGE = "hold cancellation forbidden";
    public static final String NO_DOCUMENT_ITEMS_MESSAGE = "no items in document";
    public static final String NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE = "not holden documents exists before document";
    public static final String WRONG_CREDENTIALS_MESSAGE = "wrong credentials";
    public static final String NO_SUCH_USER_MESSAGE = "No such user";
    public static final String NO_SUCH_COMPANY_MESSAGE = "No such company";
    public static final String NO_SUCH_STORAGE_MESSAGE = "no such storage";
    public static final String NO_SUCH_DOCUMENT_MESSAGE = "no such document";
    public static final String NO_SUCH_DOCUMENT_ITEM_MESSAGE = "no such document item";
    public static final String NO_SUCH_BASE_DOCUMENT_MESSAGE = "no such base document";
    public static final String NO_SUCH_CHECK_INFO_MESSAGE = "no such check info";
    public static final String NO_SUCH_ITEM_MESSAGE = "no such item";
    public static final String NO_SUCH_LOT_MESSAGE = "no such lot";
    public static final String NO_SUCH_PROJECT_MESSAGE = "no such project";
    public static final String NO_SUCH_LOT_MOVEMENT_MESSAGE = "no such lot movement";
    public static final String NO_SUCH_DINNER_MESSAGE = "no such dinner";

    public static final String NUMBER_OF_DELETED_DOCS_MESSAGE = "Удалено документов -  %s";



    public static final String NET_TYPE = "Нетто";
    public static final String GROSS_TYPE = "Брутто";
    public static final String ENABLE_TYPE = "Используется";

    public static final List<Integer> INGREDIENTS_PARENT_IDS = List.of(3);  // items from ingredients (id = 3)

    public static final String DATE_TIME_STRING = "dateTime";

    private Constants() {
    }
}
