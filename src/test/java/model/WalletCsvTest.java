package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletCsvTest {

    @Test
    void gettersAndSetters() {
        WalletCsv csv = new WalletCsv();
        csv.setDate("2023/01/05");
        csv.setDeposit(150000);
        csv.setContent("給料");

        assertEquals("2023/01/05", csv.getDate());
        assertEquals(150000, csv.getDeposit());
        assertEquals("給料", csv.getContent());
    }

    @Test
    void defaultValues() {
        WalletCsv csv = new WalletCsv();

        assertNull(csv.getDate());
        assertEquals(0, csv.getDeposit());
        assertNull(csv.getContent());
    }

    @Test
    void settersOverwriteValues() {
        WalletCsv csv = new WalletCsv();
        csv.setDate("2023/01/05");
        csv.setDeposit(150000);
        csv.setContent("給料");

        csv.setDate("2023/02/10");
        csv.setDeposit(-5000);
        csv.setContent("食費");

        assertEquals("2023/02/10", csv.getDate());
        assertEquals(-5000, csv.getDeposit());
        assertEquals("食費", csv.getContent());
    }

    @Test
    void toStringFormat() {
        WalletCsv csv = new WalletCsv();
        csv.setDate("2023/01/05");
        csv.setDeposit(150000);
        csv.setContent("給料");

        String str = csv.toString();

        assertTrue(str.contains("WalletCsv{"));
        assertTrue(str.contains("2023/01/05"));
        assertTrue(str.contains("150000"));
        assertTrue(str.contains("給料"));
    }
}
