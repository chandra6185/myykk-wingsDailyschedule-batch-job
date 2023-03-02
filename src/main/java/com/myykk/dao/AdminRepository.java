package com.myykk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.myykk.model.CustomerInvoiceCountBean;


@Repository
public class AdminRepository {

	private static final Logger log = LoggerFactory.getLogger(AdminRepository.class);
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String selectCustomerInvoiceQuery;
	private final String selectInvoiceNumbersByCustomerQuery;
	
	public AdminRepository(NamedParameterJdbcTemplate jdbcTemplate,
							  @Value("${select.customer.invoice}") String selectCustomerInvoiceQuery,
							  @Value("${select.invoicenumner.by.customer}") String selectInvoiceNumbersByCustomerQuery
							) {
		this.jdbcTemplate = jdbcTemplate;
		this.selectCustomerInvoiceQuery = selectCustomerInvoiceQuery;
		this.selectInvoiceNumbersByCustomerQuery = selectInvoiceNumbersByCustomerQuery;
	}

	public List<CustomerInvoiceCountBean> getCustInvCount() {
		return jdbcTemplate.query(selectCustomerInvoiceQuery, new MapSqlParameterSource(), new CustomerInvoiceRowMapper());
	}

	public List<String> getInvNos(String custNo, Integer invDate1) {
		
		List<String> data = jdbcTemplate.query(selectInvoiceNumbersByCustomerQuery, new MapSqlParameterSource().addValue("vcustn", custNo).addValue("vinvdat1", invDate1),
				new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				});

		return data;
	}

	public void insertInvoiceRecords(Map<String, Object>[] invoiceBatch) {
		jdbcTemplate.batchUpdate(selectCustomerInvoiceQuery, invoiceBatch);
		
	}

	public void updateInvocieBundles(Map<String, Object>[] invoiceBundlesBatch) {
		// TODO Auto-generated method stub
		
	}

	public void updateInvociePdfBundles(Map<String, Object>[] invoicePdfBundlesBatch) {
		// TODO Auto-generated method stub
		
	}

}
