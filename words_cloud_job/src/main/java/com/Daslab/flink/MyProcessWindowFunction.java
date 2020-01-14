package com.Daslab.flink;

import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.PriorityQueue;

public class MyProcessWindowFunction
        extends ProcessAllWindowFunction<FlinkExecutor.WordWithCount, FlinkExecutor.WordWithCount, TimeWindow> implements Serializable {

    public static String flinkResult = "null";
    @Override
    public void process(Context context, Iterable<FlinkExecutor.WordWithCount> words, Collector<FlinkExecutor.WordWithCount> out) throws Exception {
        HashMap<String, FlinkExecutor.WordWithCount> countMap = new HashMap<>();
        for (FlinkExecutor.WordWithCount w : words) {
            if (countMap.containsKey(w.word)) {
                FlinkExecutor.WordWithCount temp = new FlinkExecutor.WordWithCount(w.word, countMap.get(w.word).count + w.count);
                countMap.put(w.word, temp);
            } else {
                countMap.put(w.word, w);
            }
        }

        System.out.println("process1");
        PriorityQueue<FlinkExecutor.WordWithCount> countQueue = topK(countMap, 20);
        String str = "{";
        for (FlinkExecutor.WordWithCount wordWithCount : countQueue){
            out.collect(wordWithCount);
            str += wordWithCount.toString() + ",";
        }
        flinkResult = str.substring(0,str.length() - 1) + "}";
        System.out.println(flinkResult);
    }

    private PriorityQueue<FlinkExecutor.WordWithCount> topK(HashMap<String, FlinkExecutor.WordWithCount> countMap, int i) {
        PriorityQueue<FlinkExecutor.WordWithCount> ans = new PriorityQueue<>(((o1, o2) -> o1.count.compareTo(o2.count)));
        for (FlinkExecutor.WordWithCount value : countMap.values()) {
            if (ans.size() < i) {
                ans.add(value);
            } else if (value.count > ans.peek().count) {
                ans.add(value);
                ans.poll();
            } else {
                continue;
            }
        }
        return ans;
    }

}