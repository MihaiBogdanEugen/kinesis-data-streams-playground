package de.mbe.aws.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mbe.aws.tests.models.StockTrade;
import software.amazon.awssdk.core.SdkBytes;

import java.io.IOException;
import java.util.Base64;

public final class StockTradeSerDeUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static SdkBytes toJsonAsBase64Bytes(final StockTrade stockTrade) throws JsonProcessingException {
        return SdkBytes.fromByteArray(Base64.getEncoder().encode(OBJECT_MAPPER.writeValueAsBytes(stockTrade)));
    }

    public static StockTrade fromJsonAsBase64Bytes(final SdkBytes bytes) throws IOException {
        return OBJECT_MAPPER.readValue(Base64.getDecoder().decode(bytes.asByteArray()), StockTrade.class);
    }
}
