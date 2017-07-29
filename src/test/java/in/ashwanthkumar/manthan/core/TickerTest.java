package in.ashwanthkumar.manthan.core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TickerTest {

    @Test
    public void shouldParseLineIntoTicker() {
        Ticker ticker = Ticker.parse("20150301|94072|AFSSWI1115:DARK BROWN:L|2073-102-1022198-2015-03-01");
        assertThat(ticker.getTransactionDate(), is("20150301"));
        assertThat(ticker.getCustomerCode(), is("94072"));
        assertThat(ticker.getProductDescription(), is("AFSSWI1115:DARK BROWN:L"));
        assertThat(ticker.getInvoiceNumber(), is("2073-102-1022198-2015-03-01"));
    }
}
