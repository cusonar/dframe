package com.doosan.dframe.core.config.security;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.Date;

/**
 * {@link PersistentTokenRepository} 구현체.
 * <p>
 * Deprecated된 {@code JdbcTokenRepositoryImpl}(내부적으로 {@code JdbcDaoSupport}를 상속)을 대체하며,
 * {@link JdbcTemplate}을 생성자로 주입받아 {@code persistent_logins} 테이블에 직접 접근합니다.
 * </p>
 *
 * <p>필요 DDL:</p>
 * <pre>{@code
 * CREATE TABLE persistent_logins (
 *     username  VARCHAR(64) NOT NULL,
 *     series    VARCHAR(64) PRIMARY KEY,
 *     token     VARCHAR(64) NOT NULL,
 *     last_used TIMESTAMP   NOT NULL
 * );
 * }</pre>
 */
public class JdbcPersistentTokenRepository implements PersistentTokenRepository {

    private static final String INSERT_TOKEN_SQL =
            "INSERT INTO persistent_logins (username, series, token, last_used) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_TOKEN_SQL =
            "UPDATE persistent_logins SET token = ?, last_used = ? WHERE series = ?";

    private static final String SELECT_TOKEN_SQL =
            "SELECT username, series, token, last_used FROM persistent_logins WHERE series = ?";

    private static final String DELETE_USER_TOKENS_SQL =
            "DELETE FROM persistent_logins WHERE username = ?";

    private final JdbcTemplate jdbcTemplate;

    public JdbcPersistentTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        jdbcTemplate.update(INSERT_TOKEN_SQL,
                token.getUsername(),
                token.getSeries(),
                token.getTokenValue(),
                token.getDate());
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        jdbcTemplate.update(UPDATE_TOKEN_SQL, tokenValue, lastUsed, series);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            return jdbcTemplate.queryForObject(SELECT_TOKEN_SQL,
                    (rs, rowNum) -> new PersistentRememberMeToken(
                            rs.getString("username"),
                            rs.getString("series"),
                            rs.getString("token"),
                            rs.getTimestamp("last_used")),
                    seriesId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void removeUserTokens(String username) {
        jdbcTemplate.update(DELETE_USER_TOKENS_SQL, username);
    }
}
