package com.zshnb.userservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private RedisTemplate redisTemplate;

    private String DatabaseName = "user_test";

    /**
     * clean redis
     * */
    @BeforeEach
    public void before() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }

    /**
     * clean database environment before every unit test
     * */
    @BeforeEach
    public void truncate() throws SQLException {
        try (final SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Connection sqlSessionConnection = sqlSession.getConnection();
            sqlSessionConnection.prepareStatement("SET FOREIGN_KEY_CHECKS = FALSE;").execute();
            ArrayList<String> tableNames = queryAllTableNames(sqlSessionConnection);
            tableNames.forEach(tableName -> {
                try {
                    PreparedStatement preparedStatement = sqlSessionConnection.prepareStatement("TRUNCATE TABLE " + tableName + ";");
                    preparedStatement.setQueryTimeout(20);
                    preparedStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            sqlSessionConnection.prepareStatement("SET FOREIGN_KEY_CHECKS = TRUE;").execute();
            sqlSession.commit(true);
        }
    }

    private ArrayList<String> queryAllTableNames(Connection sqlSessionConnection) throws SQLException {
        String tableNameColumn = "table_name";
        String queryString = "SELECT %s FROM information_schema.tables WHERE table_type = 'base table' AND table_schema = '%s'";
        String queryAllTableNamesStatement = String.format(queryString, tableNameColumn, DatabaseName);

        ResultSet resultSet = sqlSessionConnection.prepareStatement(queryAllTableNamesStatement).executeQuery();
        ArrayList<String> tableNames = new ArrayList<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString(tableNameColumn);
            tableNames.add(tableName);
        }
        return tableNames;
    }
}
