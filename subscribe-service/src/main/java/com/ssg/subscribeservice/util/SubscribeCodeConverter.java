package com.ssg.subscribeservice.util;

import com.ssg.subscribeservice.subsenum.SubscribeCode;

public interface SubscribeCodeConverter {
    SubscribeCode toSubscribeCode(String role);
}
