package model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletJsonTest {

    @Test
    void constructorAndAccessors() {
        List<Transactions> txList = List.of(
                new Transactions("2023/01/05", 150000, "給料"),
                new Transactions("2023/01/10", -12000, "食費"));

        WalletJson json = new WalletJson("2023/1", 150000, -12000, txList);

        assertEquals("2023/1", json.period());
        assertEquals(150000, json.totalIncome());
        assertEquals(-12000, json.totalExpenditure());
        assertEquals(2, json.transactions().size());
    }

    @Test
    void emptyTransactionsList() {
        WalletJson json = new WalletJson("2023/3", 0, 0, Collections.emptyList());

        assertEquals("2023/3", json.period());
        assertEquals(0, json.totalIncome());
        assertEquals(0, json.totalExpenditure());
        assertTrue(json.transactions().isEmpty());
    }

    @Test
    void equalsSameValues() {
        List<Transactions> txList = List.of(new Transactions("2023/01/05", 150000, "給料"));

        WalletJson json1 = new WalletJson("2023/1", 150000, 0, txList);
        WalletJson json2 = new WalletJson("2023/1", 150000, 0, txList);

        assertEquals(json1, json2);
        assertEquals(json1.hashCode(), json2.hashCode());
    }

    @Test
    void notEqualsDifferentValues() {
        List<Transactions> txList = List.of(new Transactions("2023/01/05", 150000, "給料"));

        WalletJson json1 = new WalletJson("2023/1", 150000, 0, txList);
        WalletJson json2 = new WalletJson("2023/2", 160000, -5000, txList);

        assertNotEquals(json1, json2);
    }

    @Test
    void toStringContainsFieldValues() {
        List<Transactions> txList = List.of(new Transactions("2023/01/05", 150000, "給料"));
        WalletJson json = new WalletJson("2023/1", 150000, -12000, txList);

        String str = json.toString();
        assertTrue(str.contains("2023/1"));
        assertTrue(str.contains("150000"));
    }
}
