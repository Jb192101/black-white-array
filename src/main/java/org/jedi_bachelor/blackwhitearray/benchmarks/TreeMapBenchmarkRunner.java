package org.jedi_bachelor.blackwhitearray.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class TreeMapBenchmarkRunner {
    private Map<Integer, Integer> map;

    @Setup(Level.Invocation)
    public void setUp() {
        map = new TreeMap<>();
        map.put(1, 5);
        map.put(2, 2);
    }

    @Benchmark
    public Integer benchmarkGetElementA() {
        return map.get(1);
    }

    @Benchmark
    public void benchmarkAddElement(Blackhole blackhole) {
        map.put(3, 10);
    }

    @Benchmark
    public void benchmarkRemoveElement() {
        map.remove(Integer.valueOf(2));
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
