package com.myykk.model;

public class CustomerInvoiceCountBean {
	
	private String customerNo;
	private Integer invDate1;
	private Integer invoiceCount;

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public Integer getInvDate1() {
		return invDate1;
	}

	public void setInvDate1(Integer invDate1) {
		this.invDate1 = invDate1;
	}

	public Integer getInvoiceCount() {
		return invoiceCount;
	}

	public void setInvoiceCount(Integer invoiceCount) {
		this.invoiceCount = invoiceCount;
	}
}
