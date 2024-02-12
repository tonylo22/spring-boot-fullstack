package com.example.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper underTest = new CustomerRowMapper();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("age")).thenReturn(19);
        when(rs.getString("name")).thenReturn("foo");
        when(rs.getString("email")).thenReturn("foo@bar.com");

        Customer actual = underTest.mapRow(rs, 1);
        Customer expected = new Customer(1, "foo", "foo@bar.com", 19);
        assertThat(actual).isEqualTo(expected);
    }
}