package org.sample.concurrency;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jcstress.annotations.State;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@State
public class TransactionRepository {
    private final List<Transaction> values = Collections.synchronizedList(new ArrayList<>());

    public void addTransaction(Transaction request) {

        synchronized (values) {
            this.values.add(request);
        }
    }

    public Statistics statisticsOfLast60Seconds() {
        final List<Transaction> validValues;
        synchronized (values) {
            validValues = values.stream().filter(it -> !it.expired()).collect(Collectors.toList());
        }
        final int size = validValues.size();
        int count = size;
        return Statistics.Companion.of(count,
                pair("sum", sum(validValues, BigDecimal.ZERO)),
                pair("avg", avgOrDefault(validValues, BigDecimal.ZERO)),
                pair("max", maxOrDefault(validValues, BigDecimal.ZERO)),
                pair("min", minOrDefault(validValues, BigDecimal.ZERO)));
    }

    private Pair<String, String> pair(String a, String b) {
        return new Pair<>(a, b);
    }

    private String sum(List<Transaction> values, BigDecimal defaultValue) {
        return formatted(sumAsBigDecimal(values, defaultValue));
    }

    @NotNull
    private BigDecimal sumAsBigDecimal(List<Transaction> values, BigDecimal defaultValue) {
        return values.stream().
                map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElse(defaultValue);
    }

    private String maxOrDefault(List<Transaction> values, BigDecimal defaultValue) {
        if (values.isEmpty()) {
            return formatted(defaultValue);
        } else {
            return formatted(values.stream().reduce(
                    (acc, ele) -> {
                        if (acc.getAmount().compareTo(ele.getAmount()) > 0) {
                            return acc;
                        } else {
                            return ele;
                        }
                    }).get().getAmount());
        }
    }

    private String minOrDefault(List<Transaction> values, BigDecimal defaultValue) {
        if (values.isEmpty()) {
            return formatted(defaultValue);
        } else {
            return formatted(values.stream().reduce(
                    (acc, ele) -> {
                        if (acc.getAmount().compareTo(ele.getAmount()) < 0) {
                            return acc;
                        } else {
                            return ele;
                        }
                    }).get().getAmount());
        }
    }

    private String avgOrDefault(List<Transaction> values, BigDecimal defaultValue) {
        if (values.isEmpty()) {
            return formatted(defaultValue);
        } else {
            final BigDecimal amount = sumAsBigDecimal(values, defaultValue);
            BigDecimal total = new BigDecimal(values.size());
            return formatted(amount.divide(total, MathContext.DECIMAL128));
        }
    }

    private String formatted(BigDecimal t) {
        return t.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public void deleteAllTransactions() {
        synchronized (values) {
            this.values.clear();
        }
    }
}
