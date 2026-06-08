package com.arttraining.business.constant;

public class BusinessConstant {

    private BusinessConstant() {}

    public static final Integer CUSTOMER_TYPE_PERSONAL = 1;
    public static final Integer CUSTOMER_TYPE_ENTERPRISE = 2;

    public static final Integer CUSTOMER_LEVEL_NORMAL = 1;
    public static final Integer CUSTOMER_LEVEL_QUALITY = 2;
    public static final Integer CUSTOMER_LEVEL_VIP = 3;
    public static final Integer CUSTOMER_LEVEL_STRATEGIC = 4;

    public static final Integer CUSTOMER_SOURCE_STORE = 1;
    public static final Integer CUSTOMER_SOURCE_REFERRAL = 2;
    public static final Integer CUSTOMER_SOURCE_ONLINE = 3;
    public static final Integer CUSTOMER_SOURCE_TELEMARKETING = 4;
    public static final Integer CUSTOMER_SOURCE_OTHER = 5;

    public static final Integer ORDER_TYPE_INKJET = 1;
    public static final Integer ORDER_TYPE_PHOTO = 2;
    public static final Integer ORDER_TYPE_CARVING = 3;
    public static final Integer ORDER_TYPE_PRINTING = 4;
    public static final Integer ORDER_TYPE_SIGN = 5;
    public static final Integer ORDER_TYPE_OTHER = 6;

    public static final Integer ORDER_SOURCE_STORE = 1;
    public static final Integer ORDER_SOURCE_PHONE = 2;
    public static final Integer ORDER_SOURCE_WECHAT = 3;
    public static final Integer ORDER_SOURCE_ONLINE = 4;
    public static final Integer ORDER_SOURCE_REFERRAL = 5;

    public static final Integer ORDER_STATUS_PENDING_CONFIRM = 1;
    public static final Integer ORDER_STATUS_CONFIRMED = 2;
    public static final Integer ORDER_STATUS_PRODUCING = 3;
    public static final Integer ORDER_STATUS_PENDING_REVIEW = 4;
    public static final Integer ORDER_STATUS_COMPLETED = 5;
    public static final Integer ORDER_STATUS_PICKED_UP = 6;
    public static final Integer ORDER_STATUS_CANCELLED = 7;

    public static final Integer PAYMENT_STATUS_UNPAID = 0;
    public static final Integer PAYMENT_STATUS_PARTIAL = 1;
    public static final Integer PAYMENT_STATUS_PAID = 2;

    public static final Integer DELIVERY_TYPE_SELF = 1;
    public static final Integer DELIVERY_TYPE_DELIVERY = 2;
    public static final Integer DELIVERY_TYPE_MAIL = 3;

    public static final Integer CONTRACT_TYPE_ANNUAL = 1;
    public static final Integer CONTRACT_TYPE_PROJECT = 2;
    public static final Integer CONTRACT_TYPE_LONGTERM = 3;

    public static final Integer CONTRACT_STATUS_DRAFT = 0;
    public static final Integer CONTRACT_STATUS_ACTIVE = 1;
    public static final Integer CONTRACT_STATUS_EXPIRING = 2;
    public static final Integer CONTRACT_STATUS_EXPIRED = 3;
    public static final Integer CONTRACT_STATUS_VOID = 4;

    public static final Integer SETTLEMENT_MONTHLY = 1;
    public static final Integer SETTLEMENT_QUARTERLY = 2;
    public static final Integer SETTLEMENT_YEARLY = 3;
    public static final Integer SETTLEMENT_PER_ORDER = 4;

    public static final Integer CONSUME_TYPE_ORDER = 1;
    public static final Integer CONSUME_TYPE_RECHARGE = 2;
    public static final Integer CONSUME_TYPE_REFUND = 3;
    public static final Integer CONSUME_TYPE_OTHER = 4;

    public static final String BIZ_TYPE_CUSTOMER = "customer";
    public static final String BIZ_TYPE_CONTRACT = "contract";
    public static final String BIZ_TYPE_ORDER = "order";

    public static final String OP_TYPE_CREATE = "create";
    public static final String OP_TYPE_UPDATE = "update";
    public static final String OP_TYPE_DELETE = "delete";
    public static final String OP_TYPE_REVIEW = "review";
    public static final String OP_TYPE_STATUS_CHANGE = "status_change";

    public static final Integer URGENT_NO = 0;
    public static final Integer URGENT_YES = 1;

    public static final Integer STATUS_DISABLE = 0;
    public static final Integer STATUS_ENABLE = 1;
}
