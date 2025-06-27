package com.ssg.paymentservice.common.util;

public interface IdGenerator {
    String billingId();
    String paymentHistoryId();
    String OrderId();
}
