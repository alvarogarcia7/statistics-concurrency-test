/*
 * Copyright (c) 2017, Red Hat Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sample.concurrency;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jcstress.annotations.*;
import org.sample.concurrency.harness.StringString_Result;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


@JCStressTest
@Outcome(id = "1.00, 1.00", expect = Expect.ACCEPTABLE)
class AverageConcurrencyTest {

    @Actor
    public void actor1(TransactionRepository repository, StringString_Result r) {
        r.r1 = addTransactionAndCalculateAvg(repository);
    }

    @Actor
    public void actor2(TransactionRepository repository, StringString_Result r) {
        r.r2 = addTransactionAndCalculateAvg(repository);
    }

    private String addTransactionAndCalculateAvg(TransactionRepository repository) {
        repository.addTransaction(getRequest());
        return repository.statisticsOfLast60Seconds().get("avg").orNull();
    }

    @NotNull
    private Transaction getRequest() {
        return new Transaction(new BigDecimal("1"), ZonedDateTime.now());
    }
}

@JCStressTest
@Outcome(id = "1.00, 2.00", expect = Expect.ACCEPTABLE, desc = "actor 1 writes before actor 2 reads")
@Outcome(id = "2.00, 2.00", expect = Expect.ACCEPTABLE, desc = "actor 2 writes before actor 1 reads")
class MaxConcurrencyTest {

    @Actor
    public void actor1(TransactionRepository repository, StringString_Result r) {
        repository.addTransaction(getRequest("1"));
        r.r1 = repository.statisticsOfLast60Seconds().get("max").orNull();
    }

    @Actor
    public void actor2(TransactionRepository repository, StringString_Result r) {
        repository.addTransaction(getRequest("2"));
        r.r2 = repository.statisticsOfLast60Seconds().get("max").orNull();
    }

    @NotNull
    private Transaction getRequest(String val) {
        return new Transaction(new BigDecimal(val), ZonedDateTime.now());
    }
}
