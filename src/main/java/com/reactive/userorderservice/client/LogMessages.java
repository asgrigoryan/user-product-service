package com.reactive.userorderservice.client;

public final class LogMessages {
    private LogMessages() {}

    public static final String CALLING_ORDER_SERVICE = "Calling Order Service for phoneNumber={}";
    public static final String NO_ORDERS_FOUND = "No orders found for phoneNumber={}";
    public static final String FETCHED_ORDERS = "Fetched {} orders for phoneNumber={}";
    public static final String ERROR_FETCHING_ORDERS = "Error fetching orders. uri={} phoneNumber={} cause={}";

    public static final String CALLING_PRODUCT_SERVICE = "Calling Product Service for code={} requestId={}";
    public static final String ERROR_FETCHING_PRODUCTS = "Error fetching products for code={} requestId={}";
    public static final String FETCHED_PRODUCTS = "Fetched {} products for code={} requestId={}";
}
