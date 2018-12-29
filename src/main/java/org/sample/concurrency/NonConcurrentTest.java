package org.sample.concurrency;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class NonConcurrentTest {

    public static void main(String[] args) {
        final TransactionRepository transactionRepository = new TransactionRepository();

        transactionRepository.addTransaction(new Transaction(new BigDecimal("0.00"), ZonedDateTime.now()));

        transactionRepository.statisticsOfLast60Seconds();
    }
}
