package com.uber.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Constants {
    LocalDate END_OF_DATE = LocalDate.of(9999, 12, 31);
    LocalDateTime END_OF_TIME = LocalDateTime.of(9999, 12, 31, 23, 59);
}
