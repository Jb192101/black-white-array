package org.jedi_bachelor.blackwhitearray.benchmarks;

import org.jedi_bachelor.blackwhitearray.BlackWhiteArray;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class BlackWhiteArrayBenchmarkRunner {
    private List<Integer> list;

    @Setup(Level.Invocation)
    public void setUp() {
        list = new BlackWhiteArray<>(5, Integer.class);
        list.add(5);
        list.add(2);
    }

    @Benchmark
    public Integer benchmarkGetElementA() {
        return list.get(1);
    }

    @Benchmark
    public void benchmarkAddElement(Blackhole blackhole) {
        list.add(10);
    }

    @Benchmark
    public void benchmarkRemoveElement() {
        list.remove(Integer.valueOf(5));
    }

    @Benchmark
    public void benchmarkIndexOf() {
        list.indexOf(2);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}