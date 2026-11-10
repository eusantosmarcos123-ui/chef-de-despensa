package com.exemplo.chefdedespensa.data.local;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Converte objetos Date em valores Long (timestamp Unix em milissegundos) e vice-versa,
 * permitindo que o Room armazene datas de validade nas tabelas SQLite.
 */
public class DateConverter {

    @TypeConverter
    public static Date fromTimestamp(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
