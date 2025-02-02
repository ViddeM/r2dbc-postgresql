/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.postgresql.codec;

import io.r2dbc.postgresql.client.EncodedParameter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static io.r2dbc.postgresql.client.EncodedParameter.NULL_VALUE;
import static io.r2dbc.postgresql.client.ParameterAssert.assertThat;
import static io.r2dbc.postgresql.codec.PostgresqlObjectId.MONEY;
import static io.r2dbc.postgresql.codec.PostgresqlObjectId.TIMESTAMPTZ;
import static io.r2dbc.postgresql.codec.PostgresqlObjectId.VARCHAR;
import static io.r2dbc.postgresql.message.Format.FORMAT_BINARY;
import static io.r2dbc.postgresql.message.Format.FORMAT_TEXT;
import static io.r2dbc.postgresql.util.ByteBufUtils.encode;
import static io.r2dbc.postgresql.util.TestByteBufAllocator.TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Unit tests for {@link OffsetDateTimeCodec}.
 */
final class OffsetDateTimeCodecUnitTests {

    private static final int dataType = TIMESTAMPTZ.getObjectId();

    @Test
    void constructorNoByteBufAllocator() {
        assertThatIllegalArgumentException().isThrownBy(() -> new OffsetDateTimeCodec(null))
            .withMessage("byteBufAllocator must not be null");
    }

    @Test
    void decode() {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2018-11-05T00:16:00.899797+09:00");

        assertThat(new OffsetDateTimeCodec(TEST).decode(encode(TEST, "2018-11-05 00:16:00.899797+09:00"), dataType, FORMAT_TEXT, OffsetDateTime.class))
            .isEqualTo(offsetDateTime);
        assertThat(new OffsetDateTimeCodec(TEST).decode(encode(TEST, "2018-11-05 00:16:00.899797+09:00:00"), dataType, FORMAT_TEXT, OffsetDateTime.class))
                .isEqualTo(offsetDateTime);
        assertThat(new OffsetDateTimeCodec(TEST).decode(encode(TEST, "2018-11-05 00:16:00.899797+09"), dataType, FORMAT_TEXT, OffsetDateTime.class))
            .isEqualTo(offsetDateTime);
    }

    @Test
    void decodeUTC() {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2018-11-05T00:16:00.899797+00:00");

        assertThat(new OffsetDateTimeCodec(TEST).decode(encode(TEST, "2018-11-05 00:16:00.899797+00:00"), dataType, FORMAT_TEXT, OffsetDateTime.class))
            .isEqualTo(offsetDateTime);
        assertThat(new OffsetDateTimeCodec(TEST).decode(encode(TEST, "2018-11-05 00:16:00.899797+00:00:00"), dataType, FORMAT_TEXT, OffsetDateTime.class))
                .isEqualTo(offsetDateTime);
        assertThat(new OffsetDateTimeCodec(TEST).decode(encode(TEST, "2018-11-05 00:16:00.899797+00"), dataType, FORMAT_TEXT, OffsetDateTime.class))
            .isEqualTo(offsetDateTime);
    }

    @Test
    void decodeNoByteBuf() {
        assertThat(new OffsetDateTimeCodec(TEST).decode(null, dataType, FORMAT_TEXT, OffsetDateTime.class)).isNull();
    }

    @Test
    void doCanDecode() {
        OffsetDateTimeCodec codec = new OffsetDateTimeCodec(TEST);

        assertThat(codec.doCanDecode(TIMESTAMPTZ, FORMAT_BINARY)).isTrue();
        assertThat(codec.doCanDecode(MONEY, FORMAT_TEXT)).isFalse();
        assertThat(codec.doCanDecode(TIMESTAMPTZ, FORMAT_TEXT)).isTrue();
    }

    @Test
    void doCanDecodeNoFormat() {
        assertThatIllegalArgumentException().isThrownBy(() -> new OffsetDateTimeCodec(TEST).doCanDecode(VARCHAR, null))
            .withMessage("format must not be null");
    }

    @Test
    void doCanDecodeNoType() {
        assertThatIllegalArgumentException().isThrownBy(() -> new OffsetDateTimeCodec(TEST).doCanDecode(null, FORMAT_TEXT))
            .withMessage("type must not be null");
    }

    @Test
    void doEncode() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now();

        assertThat(new OffsetDateTimeCodec(TEST).doEncode(offsetDateTime))
            .hasFormat(FORMAT_TEXT)
            .hasType(TIMESTAMPTZ.getObjectId())
            .hasValue(encode(TEST, offsetDateTime.toString()));
    }

    @Test
    void doEncodeNoValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new OffsetDateTimeCodec(TEST).doEncode(null))
            .withMessage("value must not be null");
    }

    @Test
    void encodeNull() {
        assertThat(new OffsetDateTimeCodec(TEST).encodeNull())
            .isEqualTo(new EncodedParameter(FORMAT_TEXT, TIMESTAMPTZ.getObjectId(), NULL_VALUE));
    }

}
