package com.myykk.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myykk.dao.AdminRepository;
import com.myykk.model.CustomerInvoiceCountBean;
@Service
public class WingsDailyJobService {

	private static final Logger log = LoggerFactory.getLogger(WingsDailyJobService.class);
	
	@Autowired
	InvoiceJasperBulkPdfGenerator invoiceJasperBulkPdfGenerator;
	
	@Autowired
	AdminRepository adminRepository;
	
	public void mergePDF() {
        log.info("Getting customers with bundles");
        List<CustomerInvoiceCountBean> custInvCounts = adminRepository.getCustInvCount();
        Map<String, Object>[] invoiceBatch = new HashMap[custInvCounts.size()];
        Map<String, Object>[] invoiceBundlesBatch = new HashMap[custInvCounts.size()];
        Map<String, Object>[] invoicePdfBundlesBatch = new HashMap[custInvCounts.size()];
        
        int count = 0;
        int invoiceBundlesBatchCount = 0;
        int invoicePdfBundlesBatchCount = 0;

        for(CustomerInvoiceCountBean custInvCount: custInvCounts) {
			String custNo = custInvCount.getCustomerNo();
			try {
				log.info("Getting invoice numbers for customer " + custNo);
				List<String> invNos = adminRepository.getInvNos(custNo, custInvCount.getInvDate1());
				String newInvNum = custNo + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
				log.info("Merge invoices for customer " + custNo);
				boolean pdfsMerged = invoiceJasperBulkPdfGenerator.multiplePdfs(newInvNum, invNos);
				if (pdfsMerged) {
					log.info("Invoice merge successful for customer " + custNo);
					Map<String,Object> invoiceValueMap = new HashMap<String, Object>();
					invoiceValueMap.put("vinvnb", newInvNum);
					invoiceValueMap.put("vcustn", custNo);
					invoiceValueMap.put("vinvdat", formatDate(custInvCount.getInvDate1()));
					invoiceValueMap.put("vinvdat1", custInvCount.getInvDate1());
					invoiceValueMap.put("vcrtdtc", new Timestamp(System.currentTimeMillis()));
					//Object[] invoiceValues = new Object[]{newInvNum, custNo, formatDate(custInvCount.getInvDate1()), custInvCount.getInvDate1(), new Timestamp(System.currentTimeMillis())};
					invoiceBatch[count] = invoiceValueMap;
					count++;
					for (String invNo :  invNos) {
						Map<String,Object> invoiceBundleValueMap = new HashMap<String, Object>();
						invoiceBundleValueMap.put("vbndlinv", newInvNum);
						invoiceBundleValueMap.put("vinvnb", invNo);
						invoiceBundlesBatch[invoiceBundlesBatchCount] = invoiceBundleValueMap;
						invoiceBundlesBatchCount++;
					}
					
					Map<String,Object> invoicePdfBundleValueeMap = new HashMap<String, Object>();
					invoicePdfBundleValueeMap.put("vpdfdat", new Timestamp(System.currentTimeMillis()));
					invoicePdfBundleValueeMap.put("vinvnb", newInvNum);
					invoicePdfBundlesBatch[invoicePdfBundlesBatchCount] = invoicePdfBundleValueeMap;
					invoicePdfBundlesBatchCount++;
					//Object[] invoicePdfBundleValues = new Object[]{new Timestamp(System.currentTimeMillis()), newInvNum};
					//invoicePdfBundlesBatch.add(invoicePdfBundleValues);
				}
			} catch(Exception ex){
				log.error("Exception while merging invoices for customer " + custNo, ex);
			}
		}
		if(invoiceBatch.length > 0) {
            log.info("Inserting records with new invoice numbers into VDA36C");
            adminRepository.insertInvoiceRecords(invoiceBatch);
		}
        if(invoiceBundlesBatch.length > 0) {
            log.info("Attaching bundles to the invoices");
            adminRepository.updateInvocieBundles(invoiceBundlesBatch);
        }
        if(invoicePdfBundlesBatch.length > 0) {
            log.info("Updating PDF values into VDA36C for the new invoice numbers");
            adminRepository.updateInvociePdfBundles(invoicePdfBundlesBatch);
        }
	}

	private String formatDate(int invDat) {
        String date = String.valueOf(invDat);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(date, formatter).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

	
}
