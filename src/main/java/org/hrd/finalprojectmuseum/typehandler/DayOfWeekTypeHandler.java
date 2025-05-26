package org.hrd.finalprojectmuseum.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DayOfWeekTypeHandler extends BaseTypeHandler<DayOfWeek> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DayOfWeek parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toDatabaseValue());
    }

    @Override
    public DayOfWeek getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? DayOfWeek.fromDatabaseValue(value) : null;
    }

    @Override
    public DayOfWeek getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? DayOfWeek.fromDatabaseValue(value) : null;
    }

    @Override
    public DayOfWeek getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? DayOfWeek.fromDatabaseValue(value) : null;
    }
}