package ru.ok.android.benchmark;

import java.util.concurrent.TimeUnit;
import ru.ok.android.benchmark.Benchmarks.Callback;
import ru.ok.android.onelog.OneLog;
import ru.ok.onelog.builtin.DurationInterval;
import ru.ok.onelog.operation.stream.OperationStreamFactory;
import ru.ok.onelog.operation.stream.StreamOperationType;

public final class StreamBenchmark {
    private static final String[][] DISPLAYED_SEQUENCES;
    private static final String[] LOAD_STREAM_SEQUENCE;
    private static final Callback callback;

    /* renamed from: ru.ok.android.benchmark.StreamBenchmark.1 */
    static class C02311 implements Runnable {
        final /* synthetic */ CheckPoint val$checkPoint;

        C02311(CheckPoint checkPoint) {
            this.val$checkPoint = checkPoint;
        }

        public void run() {
            Benchmarks.findSequences(this.val$checkPoint, StreamBenchmark.DISPLAYED_SEQUENCES, StreamBenchmark.callback);
        }
    }

    /* renamed from: ru.ok.android.benchmark.StreamBenchmark.2 */
    static class C02322 implements Callback {
        C02322() {
        }

        public void onFoundSequence(String[] sequence, CheckPoint[] checkPoints) {
            if (sequence == StreamBenchmark.LOAD_STREAM_SEQUENCE) {
                RequestParam param = checkPoints[0].extra;
                report(StreamOperationType.stream_download, checkPoints, 0, 1, param);
                report(StreamOperationType.stream_parse, checkPoints, 1, 2, param);
                report(StreamOperationType.stream_download_parse, checkPoints, 0, 2, param);
                report(StreamOperationType.stream_save_cache, checkPoints, 2, 3, param);
                report(StreamOperationType.stream_render, checkPoints, 3, 4, param);
                report(StreamOperationType.stream_sync_ui, checkPoints, 4, 5, param);
                report(StreamOperationType.stream_load_api, checkPoints, 0, 5, param);
            }
        }

        private void report(StreamOperationType type, CheckPoint[] checkPoints, int startIndex, int endIndex, RequestParam param) {
            long durationNano = checkPoints[endIndex].time - checkPoints[startIndex].time;
            OneLog.log(OperationStreamFactory.get(type, durationNano, DurationInterval.valueOf(durationNano, TimeUnit.NANOSECONDS), param.requestedChunkSize, param.chunkNumber));
        }
    }

    static class RequestParam {
        final int chunkNumber;
        final int requestedChunkSize;

        RequestParam(int requestedChunkSize, int chunkNumber) {
            this.requestedChunkSize = requestedChunkSize;
            this.chunkNumber = chunkNumber;
        }
    }

    public static int sendRequest(int chunkSize, int chunkNumber) {
        return Benchmarks.checkPoint("stream.send.request", new RequestParam(chunkSize, chunkNumber)).sequenceId;
    }

    public static void receiveResponse(int sequenceId) {
        Benchmarks.checkPoint("stream.receive.response", sequenceId);
    }

    public static void parseResponse(int sequenceId) {
        Benchmarks.checkPoint("stream.parse.response", sequenceId);
    }

    public static void saveToCache(int sequenceId) {
        Benchmarks.checkPoint("stream.save.to.cache", sequenceId);
    }

    public static void generateCards(int sequenceId) {
        Benchmarks.checkPoint("stream.generate.cards", sequenceId);
    }

    public static void display(int sequenceId) {
        Benchmarks.benchmarkBgExecutor.execute(new C02311(Benchmarks.checkPoint("stream.display", sequenceId)));
    }

    static {
        callback = new C02322();
        LOAD_STREAM_SEQUENCE = new String[]{"stream.send.request", "stream.receive.response", "stream.parse.response", "stream.save.to.cache", "stream.generate.cards", "stream.display"};
        DISPLAYED_SEQUENCES = new String[][]{LOAD_STREAM_SEQUENCE};
    }
}
