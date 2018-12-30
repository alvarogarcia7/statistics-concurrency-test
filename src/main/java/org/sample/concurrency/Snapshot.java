package org.sample.concurrency;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static java.math.BigDecimal.ZERO;

public class Snapshot {

    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal average = BigDecimal.ZERO;
    private int count = 0;
    private PriorityQueue<BigDecimal> max = new PriorityQueue<>(Comparator.<BigDecimal, BigDecimal>comparing(it -> it).reversed());
    private PriorityQueue<BigDecimal> min = new PriorityQueue<>(BigDecimal::compareTo);

    public Snapshot() {
        initialize();
    }

    public void add(Transaction request) {
        if(request.expired()){
            return;
        }
        final BigDecimal requestAmount = request.getAmount();
        sum = sum.add(requestAmount);
        max.add(requestAmount);
        min.add(requestAmount);
        count++;
        average = updatedAverage();
    }

    public void expire(Transaction request) {
        final BigDecimal amount = request.getAmount();
        sum = sum.subtract(amount);
        max.remove(amount);
        min.remove(amount);
        count--;
        average = updatedAverage();
    }

    private BigDecimal updatedAverage() {
        if (count == 0) {
            return ZERO;
        }
        final BigDecimal amount = sum;
        BigDecimal total = new BigDecimal(count);
        return amount.divide(total, MathContext.DECIMAL128);
    }

    public Statistics x() {
        return Statistics.Companion.of(count,
                pair("sum", formatted(sum)),
                pair("avg", formatted(average)),
                pair("max", formatted(peekOrDefault(max, ZERO))),
                pair("min", formatted(peekOrDefault(min, ZERO))));
    }

    @Nullable
    private BigDecimal peekOrDefault(PriorityQueue<BigDecimal> max, BigDecimal defaultValue) {
        final BigDecimal result = max.peek();
        if(result == null){
            return defaultValue;
        }
        return result;
    }

    private Pair<String, String> pair(String a, String b) {
        return new Pair<>(a, b);
    }

    @NotNull
    private BigDecimal sumAsBigDecimal(List<Transaction> values, BigDecimal defaultValue) {
        return values.stream().
                map(Transaction::getAmount)
                .reduce(BigDecimal::add)
                .orElse(defaultValue);
    }

    private String formatted(BigDecimal t) {
        return t.setScale(2, RoundingMode.HALF_UP).toString();
    }


    public void clear() {
        reinitialize();
    }

    private void reinitialize() {
        initialize();
    }

    private void initialize() {
        sum = BigDecimal.ZERO;
        average = BigDecimal.ZERO;
        count = 0;
        max = new PriorityQueue<>(Comparator.<BigDecimal, BigDecimal>comparing(it -> it).reversed());
        min = new PriorityQueue<>(BigDecimal::compareTo);
    }
}
