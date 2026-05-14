package com.example.baseb.common.config.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.util.StringUtils;

public class P6spySqlFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (!StringUtils.hasText(sql)) {
            return String.format("[%s] | %d ms | %s", category, elapsed, sql);
        }
        
        sql = formatSql(category, sql);
        return String.format("\n[%s] | %d ms | %s", category, elapsed, sql);
    }

    private String formatSql(String category, String sql) {
        if (Category.STATEMENT.getName().equals(category)) {
            String tmpsql = sql.trim().toLowerCase();
            if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            return sql;
        }
        return sql;
    }
}
