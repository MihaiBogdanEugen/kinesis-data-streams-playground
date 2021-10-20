package de.mbe.aws.tests.producer.api;

import de.mbe.aws.tests.StockTradeGenerator;
import de.mbe.aws.tests.StockTradeSerDeUtils;
import de.mbe.aws.tests.models.StockTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.StreamStatus;

public final class StockTradesAPIAsyncProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockTradesAPIAsyncProducer.class);
    private static final String STREAM_NAME = "stocks";
    private static final int SHARD_COUNT = 5;
    private static int DESCRIBE_STREAM_MAX_RETRIES = 10;

    private static final LocalStackContainer LOCALSTACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.12.19.1"))
            .withServices(LocalStackContainer.Service.KINESIS);

    static {
        LOCALSTACK.start();
    }

    private static void createStream(final KinesisAsyncClient kinesisClient) {
        try {
            kinesisClient.createStream(builder -> builder
                    .streamName(STREAM_NAME)
                    .shardCount(SHARD_COUNT)
                    .build()).get();
        } catch (final Exception error) {
            LOGGER.error("Cannot create stream `{}` with {} shards", STREAM_NAME, SHARD_COUNT, error);
            System.exit(1);
        }

        LOGGER.info("Stream `{}` with {} shards was created successfully", STREAM_NAME, SHARD_COUNT);
    }

    private static void describeStream(final KinesisAsyncClient kinesisClient) {
        try {
            final var describeStreamResponse = kinesisClient.describeStream(builder -> builder
                    .streamName(STREAM_NAME)
                    .build()).get();

            final var streamDescription = describeStreamResponse.streamDescription();
            final var streamStatus = streamDescription.streamStatus();
            final var actualShardCount = streamDescription.shards().size();

            LOGGER.info("Stream `{}` is {} and has {} shards", STREAM_NAME, streamStatus, actualShardCount);

            if (streamStatus == StreamStatus.CREATING) {
                DESCRIBE_STREAM_MAX_RETRIES--;
                if (DESCRIBE_STREAM_MAX_RETRIES > 0) {
                    Thread.sleep(100);
                    describeStream(kinesisClient);
                }
            } else {
                if (streamStatus != StreamStatus.ACTIVE || SHARD_COUNT != actualShardCount) {
                    System.exit(1);
                }
            }

        } catch (final Exception error) {
            LOGGER.error("Cannot describe stream `{}`", STREAM_NAME, error);
            System.exit(1);
        }
    }

    private static void putRecord(final StockTrade stockTrade, final KinesisAsyncClient kinesisClient) {
        try {
            final var payload = StockTradeSerDeUtils.toJsonAsBase64Bytes(stockTrade);
            final var putRecordResponse = kinesisClient.putRecord(builder -> builder
                    .streamName(STREAM_NAME)
                    .partitionKey(stockTrade.stockSymbol().name())
                    .data(payload)
                    .build()).get();
            LOGGER.info("Record `{}` was put in stream `{}`, with sequence number {} and shard ID {}", stockTrade, STREAM_NAME, putRecordResponse.sequenceNumber(), putRecordResponse.shardId());
        } catch (final Exception error) {
            LOGGER.error("Cannot put record `{}` in stream `{}`", stockTrade, STREAM_NAME, error);
            System.exit(1);
        }
    }

    public static void main(final String[] args) throws InterruptedException {

        LOGGER.info("StockTrades API Async Producer");

        final var kinesisAsyncClient = KinesisAsyncClient.builder()
                .endpointOverride(LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.KINESIS))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        LOCALSTACK.getAccessKey(), LOCALSTACK.getSecretKey()
                )))
                .region(Region.of(LOCALSTACK.getRegion()))
                .build();

        LOGGER.info("KinesisClient created successfully");

        createStream(kinesisAsyncClient);

        describeStream(kinesisAsyncClient);

        for(int i = 0; i < 1000; i++) {
            putRecord(StockTradeGenerator.getRandomStockTrade(), kinesisAsyncClient);
            Thread.sleep(5);
        }
    }
}
