package com.arius.ecommerce.config;

public class AppConstants {

    public static final String SORT_DIR = "asc";
    public static final String SORT_PRODUCTS_BY = "productId";
    public static final String SORT_CATEGORY_BY = "categoryId";
    public static final String SORT_ORDER_BY = "orderId";
    public static final String SORT_USER_BY = "userId";
    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "12";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String[] PUBLIC_URLS = {"/api/auth/**", "/api/public/**"};
    public static final String[] ADMIN_URLS = {"/api/admin/**"};
    public static final String[] USER_URLS = {"/api/user/**"};
    public static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 1 day
    public static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 1 month
    public static final String accessKey = "0W/nxMQNBP4H4VmAXtTVlFLOfci3QGU4mf6CUf4tXQnCf0xOwlSSUHXlUdykreEcnklktHo9E6Qc42albAX51Q==";
    public static final String refreshKey = "lNvZJvw71nGNzjLohy6UkuTspMO2uG30RWrRWhJpV6xrp1czOIFNeJcbJ8i9yRHKjeRc7F2uqNxkiCB0IAmG/w==";
    public static final String DOMAIN = "localhost";
    public static final int DEFAULT_SEARCH_SIZE = 100;
}
