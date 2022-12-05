package com.example.store.utils;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String DEFAULT_PERIOD_START = "2000-01-01";
    public static final String DAY_START = "dayStart";
    public static final String CURRENT_TIME = "currentTime";

    public static final long ONE_DAY_LONG = 86400000L;
    public static final int START_DOCUMENT_NUMBER = 1;
    public static final int EMPTY_PROJECT_ID = 1;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");

    public static final String SYSTEM_USER_EMAIL = "system@user.com";
    public static final String ADMIN_ROLE = "администратор";
    public static final String CASHIER_ROLE = "кассир";
    public static final String ACCOUNTANT_ROLE = "бухгалтер";
    public static final String CUSTOMER_ROLE = "покупатель";
    public static final String SYSTEM_ROLE = "система";
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
    public static final String PERIOD_REST_MOVE_DOC_TYPE = "Перенос остатков";

    public static final String STORE_STORE_TYPE = "склад";
    public static final String CAFE_STORE_TYPE = "склад кафе";
    public static final String RETAIL_STORE_TYPE = "розница";

    public static final String TAX_PAYMENT_TYPE = "Оплата налога";
    public static final String CLIENT_PAYMENT_TYPE = "Оплата от покупателя";
    public static final String SUPPLIER_PAYMENT_TYPE = "Оплата поставщику";
    public static final String OTHER_PAYMENT_TYPE = "Прочее";
    public static final String COST_PAYMENT_TYPE = "Расход";
    public static final String SALARY_PAYMENT_TYPE = "Выплата зарплаты";
    public static final String SALE_CASH_PAYMENT_TYPE = "Выручка наличными";
    public static final String SALE_CARD_PAYMENT_TYPE = "Выручка по картам";
    public static final String SALE_QR_PAYMENT_TYPE = "Выручка по QR";
    public static final String SALE_DELIVERY_PAYMENT_TYPE = "Выручка по доставке";

    public static final String CASH_PAYMENT_TYPE = "Оплата наличными";
    public static final String CARD_PAYMENT_TYPE = "Оплата картой";
    public static final String QR_PAYMENT_TYPE = "Оплата QR";
    public static final String DELIVERY_PAYMENT_TYPE = "Оплата через доставку";

    public static final String NET_TYPE = "Нетто";
    public static final String GROSS_TYPE = "Брутто";
    public static final String ENABLE_TYPE = "Используется";

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

    public static final String ERROR_SUBJECT = "Ошибка в приложении";
    public static final String WARNING_SUBJECT = "Предупреждение в приложении";
    public static final String MESSAGE_SUBJECT = "Сообщение из приложения";
    public static final String BAD_REQUEST_MESSAGE = "invalid request";
    public static final String TRANSACTION_FAILED_MESSAGE = "transaction failed";
    public static final String SHORTAGE_OF_ITEM_MESSAGE = "Не достаточный остаток %s, требуется %f в наличии %f";
    public static final String SHORTAGE_OF_ITEMS_IN_DOC_MESSAGE = "В документе %s № %d не достает:%n%s";
    public static final String HOLD_FAILED_MESSAGE = "Проведение документа не удалось";
    public static final String UN_HOLD_FAILED_MESSAGE = "Отмена проведения документа не удалась";
    public static final String NO_DOCUMENT_ITEMS_MESSAGE = "В документе отсутствует товары";
    public static final String NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE = "Существуют более ранние не проведенные документы";
    public static final String HOLDEN_DOCS_EXISTS_AFTER_MESSAGE = "Существуют более поздние проведенные документы";
    public static final String ORDER_DOC_IS_HOLDEN_MESSAGE = "Сначала требуется отменить проведение расходного ордера";
    public static final String WRONG_CREDENTIALS_MESSAGE = "Не правильные логин и пароль - %s";
    public static final String NO_SUCH_USER_MESSAGE = "Пользователь %s отсутствует в системе";
    public static final String NO_SUCH_COMPANY_MESSAGE = "Компания %s отсутствует в системе";
    public static final String NO_SUCH_STORAGE_MESSAGE = "Склад %s отсутствует в системе";
    public static final String NO_SUCH_DOCUMENT_MESSAGE = "Документ не найден";
    public static final String NO_SUCH_DOCUMENT_ITEM_MESSAGE = "Позиция документа не найдена";
    public static final String NO_SUCH_BASE_DOCUMENT_MESSAGE = "Документ-основание не найден";
    public static final String NO_SUCH_CHECK_INFO_MESSAGE = "Информация о чеке ККМ не найдена";
    public static final String NO_SUCH_CODE_ITEM_MESSAGE = "Номенклатура с кодом %s не найдена";
    public static final String NO_SUCH_ITEM_MESSAGE = "Номенклатура не найдена";
    public static final String NO_INGREDIENTS_IN_ITEM_MESSAGE = "В номенклатуре нет ингредиентов";
    public static final String NO_SUCH_INGREDIENT_MESSAGE = "Ингредиента с номером %s не найдено";
    public static final String NO_SUCH_LOT_MESSAGE = "Партия не найдена";
    public static final String NO_SUCH_PROJECT_MESSAGE = "Проект %s отсутствует в системе";
    public static final String NO_SUCH_DINNER_MESSAGE = "no such dinner";
    public static final String OUT_OF_PERIOD_MESSAGE = "Нельзя изменять документы до начала периода %s";
    public static final String NOT_HOLDEN_DOCS_IN_PERIOD_MESSAGE = "Существуют не проведенные документы в периоде";
    public static final String NOT_HOLDEN_CHECKS_EXIST_MESSAGE = "Существуют не проведенные чеки за предыдущие дни. Проведение текущих документов не возможно";
    public static final String NOT_HOLDEN_CHECKS_DOCS_NOT_EXIST_MESSAGE = "Не проведенных чеков не обнаружено.";
    public static final String CHECKS_HOLDING_FAIL_MESSAGE = "После проведения чеков за %s все еще остались не проведенные документы";
    public static final String CHECKS_HOLDING_FAIL_SUBJECT = "Ошибка проведения чеков - %s";
    public static final String DOC_NUMBER_EXISTS_MESSAGE = "Документ %s № %s существует";
    public static final String PORTION_ITEM_MESSAGE = "Не весовой ингредиент %s в весовой номенклатуре %s";
    public static final String NO_DO_INFO_MESSAGE = "В документе нет дополнительной информации";
    public static final String EXISTS_NOT_HOLDEN_CHECK_BEFORE_MESSAGE = "При проведении чеков за %s обнаружены не проведенные чеки за %s";

    public static final String NUMBER_OF_DELETED_DOCS_MESSAGE = "Удалено документов -  %s";

    public static final String DATE_TIME_STRING = "dateTime";

    private Constants() {
    }
}
