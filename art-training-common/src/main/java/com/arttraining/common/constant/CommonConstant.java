package com.arttraining.common.constant;

public class CommonConstant {

    private CommonConstant() {}

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String ROLE_SUPER_ADMIN = "super_admin";
    public static final String ROLE_GROUP_ADMIN = "group_admin";
    public static final String ROLE_CAMPUS_PRINCIPAL = "campus_principal";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_CONSULTANT = "consultant";
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_FINANCE = "finance";

    public static final Integer USER_TYPE_SUPER_ADMIN = 0;
    public static final Integer USER_TYPE_GROUP_ADMIN = 1;
    public static final Integer USER_TYPE_CAMPUS_PRINCIPAL = 2;
    public static final Integer USER_TYPE_TEACHER = 3;
    public static final Integer USER_TYPE_CONSULTANT = 4;
    public static final Integer USER_TYPE_STUDENT = 5;
    public static final Integer USER_TYPE_FINANCE = 6;

    public static final Integer STATUS_ENABLE = 1;
    public static final Integer STATUS_DISABLE = 0;

    public static final Integer DELETED_NO = 0;
    public static final Integer DELETED_YES = 1;

    public static final Long ROOT_PARENT_ID = 0L;

    public static final String CAMPUS_ID = "campusId";
    public static final String CAMPUS_CODE = "campusCode";
}
