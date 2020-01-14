package com.Daslab.flink;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FlinkExecutor {

    private static JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
    private static Integer i = 0;

    public static void startFlinkJob() throws Exception {
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        fsEnv.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 60000));

        String filePath = "/usr/local/comments/stopwords.dat";
        File file = new File(filePath);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        List<String> stopWords = new ArrayList<>();
        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            stopWords.add(temp.trim());
        }

        System.out.println("startFlinkJob1");
        DataStream<WordWithCount> windowCounts = fsEnv
                .socketTextStream("127.0.0.1", 8888)
                .flatMap(new FlatMapFunction<String, WordWithCount>() {
                    @Override
                    public void flatMap(String value, Collector<WordWithCount> out) {
                        System.out.println("startFlinkJob2");
                        List<SegToken> res = jiebaSegmenter.process(value, JiebaSegmenter.SegMode.SEARCH);
                        for (SegToken token : res) {
                            if (!stopWords.contains(token.word.getToken()) && (token.word.getTokenType().startsWith("n") || token.word.getTokenType().startsWith("a")|| token.word.getTokenType().startsWith("v"))) {
                                out.collect(new WordWithCount(token.word.getToken(), 1L));
                            }
                        }
                    }
                })
                .timeWindowAll(Time.seconds(5))
                .process(new MyProcessWindowFunction());
        windowCounts.print();
        windowCounts.addSink(new MySink());
        fsEnv.execute("windowCounts");
    }

//    public static String dataStreamToStr(DataStream<WordWithCount> dataStream) throws IOException {
//        Iterator<WordWithCount> iterator = DataStreamUtils.collect(dataStream);
//        String str = "";
//        while (iterator.hasNext()){
//            WordWithCount wordWithCount = iterator.next();
//            str += "{";
//            str += wordWithCount.toString() + ",";
//        }
//        str = str.substring(0,str.length() - 1) + "}";
//        return str;
//    }

    /**
     * 主要为了存储单词以及单词出现的次数
     */

    public static class WordWithCount implements Serializable {

        public String word;
        public Long count;

        public WordWithCount(){}

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return word + " : " + count;
        }
    }

}