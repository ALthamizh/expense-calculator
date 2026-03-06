package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionsTest {

    @Test
    void constructorAndAccessors() {
        Transactions tx = new Transactions("2023/01/05", 150000, "給料");

        assertEquals("2023/01/05", tx.date());
        assertEquals(150000, tx.amount());
        assertEquals("給料", tx.content());
    }

    @Test
    void equalsSameValues() {
        Transactions tx1 = new Transactions("2023/01/05", 150000, "給料");
        Transactions tx2 = new Transactions("2023/01/05", 150000, "給料");

        assertEquals(tx1, tx2);
        assertEquals(tx1.hashCode(), tx2.hashCode());
    }

    @Test
    void notEqualsDifferentValues() {
        Transactions tx1 = new Transactions("2023/01/05", 150000, "給料");
        Transactions tx2 = new Transactions("2023/01/10", -12000, "食費");

        assertNotEquals(tx1, tx2);
    }

    @Test
    void toStringContainsFieldValues() {
        Transactions tx = new Transactions("2023/01/05", 150000, "給料");
        String str = tx.toString();

        assertTrue(str.contains("2023/01/05"));
        assertTrue(str.contains("150000"));
        assertTrue(str.contains("給料"));
    }
}
