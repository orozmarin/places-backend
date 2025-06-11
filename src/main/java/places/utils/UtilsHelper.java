package places.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.util.CollectionUtils;

public final class UtilsHelper {

    private static ObjectMapper objectMapper;

    private UtilsHelper() {
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        UtilsHelper.objectMapper = objectMapper;
    }

    public static <T> boolean hasItemInList(T element, List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.contains(element);
    }

    public static String generateRandomStringOfLength(int length) {
        String generatedString = new Random().ints(97, 123)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }


    public static String createYearMonthDateString(LocalDateTime localDateTime) {
        return localDateTime.getYear() + "-" + localDateTime.getMonth().getValue() + "-"
                + localDateTime.getDayOfMonth();
    }

    public static String createYearMonthDateString(LocalDate localDate) {
        return localDate.getYear() + "-" + localDate.getMonth().getValue() + "-"
                + localDate.getDayOfMonth();
    }

    public static String createYearMonthString(LocalDateTime localDateTime) {
        return localDateTime.getYear() + "-" + localDateTime.getMonth().getValue();
    }

    public static String createYearMonthString(LocalDate localDate) {
        return localDate.getYear() + "-" + localDate.getMonth().getValue();
    }

    public static String createTimeString(LocalDateTime localDateTime) {
        return localDateTime.getHour() + ":" + 0;
    }

    public static String formatDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    public static String formatDateToDayMonthYear(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    public static String formatDateToDayMonthYear(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    public static String formatDateToYearMonth(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");
        return localDateTime.format(formatter);
    }

    public static String formatDateToMonthDayHourMinute(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM. HH:mm");
        return localDateTime.format(formatter);
    }

    public static String formatDateToYearMonth(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");
        return localDate.format(formatter);
    }

    public static String formatDateToDayMonth(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");
        return localDate.format(formatter);
    }

    public static String formatDateToDayMonth(LocalDateTime localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");
        return localDate.format(formatter);
    }

    public static String formatDateToDay(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return localDate.format(formatter);
    }

    public static String formatDateToTime(LocalDateTime localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localDate.format(formatter);
    }

    public static BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {
        if (BigDecimal.ZERO.equals(total) || BigDecimal.ZERO.equals(value)) {
            return BigDecimal.ZERO;
        }
        return value.divide(total, 2, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
    }

    public static long getDaysBetweenDates(LocalDateTime firstDate, LocalDateTime secondDate) {
        return ChronoUnit.DAYS.between(firstDate, secondDate);
    }

    public static BigDecimal divide(Integer up, Integer down) {
        Validate.notNull(up, "First number must not be null");
        Validate.notNull(down, "Second number must not be null");
        if (down == 0) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return new BigDecimal(up).divide(new BigDecimal(down), 1, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal up, BigDecimal down) {
        Validate.notNull(up, "First number must not be null");
        Validate.notNull(down, "Second number must not be null");
        if (down == BigDecimal.ZERO) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return up.divide(down, 1, RoundingMode.HALF_UP);
    }

    public static <T> String serializeObject(T object) throws JsonProcessingException {
        if (object != null) {
            return objectMapper.writeValueAsString(object);
        } else {
            return "";
        }
    }

    public static <T> T deserializeObject(String objectString, Class<T> objectType) throws JsonProcessingException {
        if (!StringUtils.isEmpty(objectString)) {
            return objectMapper.readValue(objectString, objectType);
        } else {
            return null;
        }
    }

    public static <T> T deserializeObject(String objectString, TypeReference<T> valueTypeRef)
            throws JsonProcessingException {
        if (!StringUtils.isEmpty(objectString)) {
            return objectMapper.readValue(objectString, valueTypeRef);
        } else {
            return null;
        }
    }

    public static boolean stringContainsIgnoreCase(String mainString, String subString) {
        if (!StringUtils.isEmpty(mainString)) {
            return mainString.toLowerCase().contains(subString.toLowerCase());
        } else {
            return false;
        }

    }

    public static String extractFirstLetter(String input) {
        return  (input != null && !input.isEmpty()) ? input.substring(0, 1).toUpperCase() : "";
    }

    public static String extractFirstLetters(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder firstLetters = new StringBuilder();
        String[] words = input.replace("[^a-zA-Z0-9]", "").split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                firstLetters.append(word.charAt(0));
            }
        }

        return firstLetters.toString();
    }
}
