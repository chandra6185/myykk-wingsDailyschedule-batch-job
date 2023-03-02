package com.myykk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.myykk.model.CustomerInvoiceCountBean;

@Component
public class CustomerInvoiceRowMapper implements RowMapper<CustomerInvoiceCountBean> {
	private static final Logger log = LoggerFactory.getLogger(CustomerInvoiceRowMapper.class);

	@Override
	public CustomerInvoiceCountBean mapRow(ResultSet rs, int i) throws SQLException {

		CustomerInvoiceCountBean customerInvoiceCountBean = new CustomerInvoiceCountBean();
		if (StringUtils.isNotBlank(rs.getString("customerno"))) {

			customerInvoiceCountBean.setCustomerNo(rs.getString("customerno").trim());
			customerInvoiceCountBean.setInvDate1(rs.getInt("invdate1"));
			customerInvoiceCountBean.setInvoiceCount(rs.getInt("invcount"));

		}
		return customerInvoiceCountBean;
	}
}
