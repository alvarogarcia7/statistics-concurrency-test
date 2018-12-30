package org.sample.concurrency;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class NonConcurrentTest {

    @Test
    public void do_not_count_expired() {
        final TransactionRepository transactionRepository = new TransactionRepository(new StorageAndStatisticsCalculator(0));

        transactionRepository.addTransaction(new Transaction(new BigDecimal("0.00"), ZonedDateTime.now()));
        transactionRepository.addTransaction(new Transaction(new BigDecimal("1.00"), ZonedDateTime.now()));

        assertThat(transactionRepository.statisticsOfLast60Seconds().count(), equalTo(0));
    }

    @Test
    public void count_valid_transactions() {
        final TransactionRepository transactionRepository = new TransactionRepository(new StorageAndStatisticsCalculator(60));

        transactionRepository.addTransaction(new Transaction(new BigDecimal("0.00"), ZonedDateTime.now()));
        transactionRepository.addTransaction(new Transaction(new BigDecimal("1.00"), ZonedDateTime.now()));

        assertThat(transactionRepository.statisticsOfLast60Seconds().count(), equalTo(2));
    }
}
