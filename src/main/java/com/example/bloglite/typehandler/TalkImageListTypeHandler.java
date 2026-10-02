package com.example.bloglite.typehandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 说说图片列表 JSON 类型处理器
 */
public class TalkImageListTypeHandler
        extends BaseTypeHandler<List<String>> {

    private static final Gson GSON = new Gson();

    private static final Type IMAGE_LIST_TYPE =
            new TypeToken<List<String>>() {
            }.getType();

    /**
     * Java List 写入数据库 JSON 字段
     */
    @Override
    public void setNonNullParameter(
            PreparedStatement preparedStatement,
            int parameterIndex,
            List<String> parameter,
            JdbcType jdbcType
    ) throws SQLException {
        preparedStatement.setString(
                parameterIndex,
                GSON.toJson(parameter)
        );
    }

    /**
     * 根据列名读取 JSON 数组
     */
    @Override
    public List<String> getNullableResult(
            ResultSet resultSet,
            String columnName
    ) throws SQLException {
        return parseImages(resultSet.getString(columnName));
    }

    /**
     * 根据列索引读取 JSON 数组
     */
    @Override
    public List<String> getNullableResult(
            ResultSet resultSet,
            int columnIndex
    ) throws SQLException {
        return parseImages(resultSet.getString(columnIndex));
    }

    /**
     * 从存储过程结果读取 JSON 数组
     */
    @Override
    public List<String> getNullableResult(
            CallableStatement callableStatement,
            int columnIndex
    ) throws SQLException {
        return parseImages(
                callableStatement.getString(columnIndex)
        );
    }

    /**
     * 解析 JSON 数组
     */
    private List<String> parseImages(String images) {
        if (images == null || images.isBlank()) {
            return List.of();
        }

        return GSON.fromJson(images, IMAGE_LIST_TYPE);
    }
}