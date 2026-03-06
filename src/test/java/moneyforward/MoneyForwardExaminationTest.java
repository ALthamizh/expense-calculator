package moneyforward;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.WalletCsv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MoneyForwardExaminationTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream capturedOut;
    private ByteArrayOutputStream capturedErr;

    private String testCsvPath;

    @BeforeEach
    void setUp() {
        capturedOut = new ByteArrayOutputStream();
        capturedErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
        System.setErr(new PrintStream(capturedErr));

        // Resolve test CSV from classpath
        testCsvPath = Objects.requireNonNull(
                getClass().getClassLoader().getResource("test_wallet.csv")).getPath();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // ---- csvToWalletCsv tests ----

    @Test
    void csvToWalletCsv_validFile_parsesAllRows() throws IOException {
        List<WalletCsv> result = MoneyForwardExamination.csvToWalletCsv(testCsvPath);

        assertEquals(6, result.size());
    }

    @Test
    void csvToWalletCsv_validFile_parsesFieldsCorrectly() throws IOException {
        List<WalletCsv> result = MoneyForwardExamination.csvToWalletCsv(testCsvPath);

        WalletCsv first = result.get(0);
        assertEquals("2023/01/05", first.getDate());
        assertEquals(150000, first.getDeposit());
        assertEquals("給料", first.getContent());
    }

    @Test
    void csvToWalletCsv_invalidPath_throwsIOException() {
        assertThrows(IOException.class,
                () -> MoneyForwardExamination.csvToWalletCsv("/nonexistent/path.csv"));
    }

    // ---- groupByYearMonth tests ----

    @Test
    void groupByYearMonth_multipleMonths_groupsCorrectly() throws IOException {
        List<WalletCsv> csvList = MoneyForwardExamination.csvToWalletCsv(testCsvPath);
        Map<YearMonth, List<WalletCsv>> grouped = MoneyForwardExamination.groupByYearMonth(csvList);

        assertEquals(2, grouped.size());
        assertTrue(grouped.containsKey(YearMonth.of(2023, 1)));
        assertTrue(grouped.containsKey(YearMonth.of(2023, 2)));
    }

    @Test
    void groupByYearMonth_correctEntryCount() throws IOException {
        List<WalletCsv> csvList = MoneyForwardExamination.csvToWalletCsv(testCsvPath);
        Map<YearMonth, List<WalletCsv>> grouped = MoneyForwardExamination.groupByYearMonth(csvList);

        assertEquals(3, grouped.get(YearMonth.of(2023, 1)).size());
        assertEquals(3, grouped.get(YearMonth.of(2023, 2)).size());
    }

    @Test
    void groupByYearMonth_chronologicalOrder() throws IOException {
        List<WalletCsv> csvList = MoneyForwardExamination.csvToWalletCsv(testCsvPath);
        Map<YearMonth, List<WalletCsv>> grouped = MoneyForwardExamination.groupByYearMonth(csvList);

        YearMonth[] keys = grouped.keySet().toArray(new YearMonth[0]);
        assertTrue(keys[0].isBefore(keys[1]));
    }

    // ---- printMonthlyJson tests ----

    @Test
    void printMonthlyJson_outputsValidJson() throws Exception {
        List<WalletCsv> csvList = MoneyForwardExamination.csvToWalletCsv(testCsvPath);
        Map<YearMonth, List<WalletCsv>> grouped = MoneyForwardExamination.groupByYearMonth(csvList);

        MoneyForwardExamination.printMonthlyJson(grouped);

        String output = capturedOut.toString();
        String[] lines = output.trim().split("\\R");
        assertEquals(2, lines.length);

        ObjectMapper mapper = new ObjectMapper();
        for (String line : lines) {
            JsonNode node = mapper.readTree(line);
            assertTrue(node.has("period"));
            assertTrue(node.has("total_income"));
            assertTrue(node.has("total_expenditure"));
            assertTrue(node.has("transactions"));
        }
    }

    @Test
    void printMonthlyJson_correctTotals() throws Exception {
        List<WalletCsv> csvList = MoneyForwardExamination.csvToWalletCsv(testCsvPath);
        Map<YearMonth, List<WalletCsv>> grouped = MoneyForwardExamination.groupByYearMonth(csvList);

        MoneyForwardExamination.printMonthlyJson(grouped);

        String output = capturedOut.toString();
        String[] lines = output.trim().split("\\R");

        ObjectMapper mapper = new ObjectMapper();

        // January: income=150000, expenditure=(-12000)+(-8500)=-20500
        JsonNode jan = mapper.readTree(lines[0]);
        assertEquals("2023/1", jan.get("period").asText());
        assertEquals(150000, jan.get("total_income").asInt());
        assertEquals(-20500, jan.get("total_expenditure").asInt());
        assertEquals(3, jan.get("transactions").size());

        // February: income=160000, expenditure=(-15000)+(-6000)=-21000
        JsonNode feb = mapper.readTree(lines[1]);
        assertEquals("2023/2", feb.get("period").asText());
        assertEquals(160000, feb.get("total_income").asInt());
        assertEquals(-21000, feb.get("total_expenditure").asInt());
        assertEquals(3, feb.get("transactions").size());
    }

    @Test
    void printMonthlyJson_transactionFields() throws Exception {
        List<WalletCsv> csvList = MoneyForwardExamination.csvToWalletCsv(testCsvPath);
        Map<YearMonth, List<WalletCsv>> grouped = MoneyForwardExamination.groupByYearMonth(csvList);

        MoneyForwardExamination.printMonthlyJson(grouped);

        String output = capturedOut.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jan = mapper.readTree(output.trim().split("\\R")[0]);

        JsonNode firstTx = jan.get("transactions").get(0);
        assertTrue(firstTx.has("date"));
        assertTrue(firstTx.has("amount"));
        assertTrue(firstTx.has("content"));
    }

    // ---- main method tests ----

    @Test
    void main_noArgs_printsErrorMessage() {
        MoneyForwardExamination.main(new String[] {});

        String errOutput = capturedErr.toString();
        assertTrue(errOutput.contains("Please pass the file path"));
    }

    @Test
    void main_blankArg_printsErrorMessage() {
        MoneyForwardExamination.main(new String[] { "   " });

        String errOutput = capturedErr.toString();
        assertTrue(errOutput.contains("Please pass the file path"));
    }

    @Test
    void main_invalidPath_printsFileError() {
        MoneyForwardExamination.main(new String[] { "/nonexistent/path.csv" });

        String errOutput = capturedErr.toString();
        assertTrue(errOutput.contains("File error"));
    }

    @Test
    void main_validPath_producesJsonOutput() {
        MoneyForwardExamination.main(new String[] { testCsvPath });

        String output = capturedOut.toString();
        assertTrue(output.contains("period"));
        assertTrue(output.contains("total_income"));
    }
}
