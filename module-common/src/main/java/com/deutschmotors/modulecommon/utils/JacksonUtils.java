package com.deutschmotors.modulecommon.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class JacksonUtils {
	private static final ObjectMapper mapper = createMapper();
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
		"yyyy-MM-dd'T'HH:mm:ss.SSSX");

	private static ObjectMapper createMapper() {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule
			.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(LOCAL_DATE_TIME_FORMATTER))
			.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER))
			.addSerializer(LocalTime.class, new CustomLocalTimeSerializer());
		javaTimeModule
			.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer())
			.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER))
			.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
		ObjectMapper objectMapper = new ObjectMapper().registerModule(javaTimeModule);
		// 원한는 property 만 역직렬화
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 시간 직렬화 시, timestamp 형식이 아니라 문자열로 직렬화
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}

	static class CustomLocalTimeSerializer extends JsonSerializer<LocalTime> {

		@Override
		public void serialize(LocalTime localTime, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeString(localTime.format(TIME_FORMATTER));
		}
	}

	static class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
		private final DateTimeFormatter offsetFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		private final DateTimeFormatter localDateTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			try {
				String text = p.getText();
				if (isIsoOffsetDateTime(text))
					return LocalDateTime.parse(text, offsetFormat);
				if (isLocalDateTime(text))
					return LocalDateTime.parse(text, localDateTimeFormat);
				else
					return LocalDateTime.parse(text);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}

		private boolean isIsoOffsetDateTime(String text) {
			try {
				offsetFormat.parse(text);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		private boolean isLocalDateTime(String text) {
			try {
				localDateTimeFormat.parse(text);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

	private static <T, R> R convert(T object, Class<R> clazz) {
		try {
			return mapper.convertValue(object, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	public static String toJsonString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> Map<String, Object> toCustomMap(T object) {
		return convert(object, Map.class);
	}

	public static <T> LinkedHashMap<String, Object> toLinkedHashMap(T object) {
		return convert(object, LinkedHashMap.class);
	}

	public static <T> T toObject(String jsonString, Class<T> clazz) {
		try {
			return mapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T toObject(InputStream inputStream, Class<T> clazz) {
		try {
			return mapper.readValue(inputStream, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T toObject(byte[] bytes, Class<T> clazz) {
		try {
			return mapper.readValue(bytes, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	public static ObjectMapper mapper() {
		return mapper;
	}

	public static <T> MultiValueMap<String, Object> toParamMap(T object) {
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
		Map<String, Object> customMap = toCustomMap(object);
		if (ObjectUtils.isEmpty(customMap)) {
			return multiValueMap;
		}
		for (Map.Entry<String, Object> entry : customMap.entrySet()) {
			multiValueMap.add(entry.getKey(), entry.getValue());
		}
		return multiValueMap;
	}

	public String unescape(String escapedString) {
		return escapedString
			.replace("\\n", "\n")
			.replace("\\\"", "\"");
	}

}
