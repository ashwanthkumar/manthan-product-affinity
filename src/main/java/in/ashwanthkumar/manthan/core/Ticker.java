package in.ashwanthkumar.manthan.core;

import com.google.common.base.Preconditions;

public class Ticker {
    private String transactionDate;
    private String customerCode;
    private String productDescription;
    private String invoiceNumber;

    Ticker(String transactionDate, String customerCode, String productDescription, String invoiceNumber) {
        this.transactionDate = transactionDate;
        this.customerCode = customerCode;
        this.productDescription = productDescription;
        this.invoiceNumber = invoiceNumber;
    }

    public static Ticker parse(String line) {
        String[] parts = line.split("\\|");
        Preconditions.checkArgument(parts.length == 4, String.format("Unable to parse %s into Ticker", line));
        return new Ticker(parts[0], parts[1], parts[2], parts[3]);
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }
}
