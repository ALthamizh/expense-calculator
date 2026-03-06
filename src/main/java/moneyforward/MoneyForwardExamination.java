package moneyforward;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import model.Transactions;
import model.WalletCsv;
import model.WalletJson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Reads a wallet CSV file, groups transactions by year/month,
 * and outputs JSON summaries with income, expenditure, and transaction details.
 *
 * @author thamizh
 *         Date: 2023/12/17 17:30 JST
 */
public class MoneyForwardExamination {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Loads data from a CSV file and maps each row into a WalletCsv object.
     *
     * @param path CSV file path
     * @return list of parsed WalletCsv objects
     * @throws IOException if the file is missing or unreadable
     */
    static List<WalletCsv> csvToWalletCsv(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return new CsvToBeanBuilder<WalletCsv>(br)
                    .withType(WalletCsv.class)
                    .build()
                    .parse();
        }
    }

    /**
     * Groups CSV records by YearMonth in a single O(n) pass.
     * Parses the date string into LocalDate and uses YearMonth as the grouping key.
     * TreeMap ensures chronological output order.
     *
     * @param csvList the parsed CSV records
     * @return a TreeMap of YearMonth → list of WalletCsv entries
     */
    static Map<YearMonth, List<WalletCsv>> groupByYearMonth(List<WalletCsv> csvList) {
        Map<YearMonth, List<WalletCsv>> grouped = new TreeMap<>();

        for (WalletCsv csv : csvList) {
            LocalDate date = LocalDate.parse(csv.getDate(), DATE_FORMATTER);
            YearMonth ym = YearMonth.from(date);
            grouped.computeIfAbsent(ym, k -> new ArrayList<>()).add(csv);
        }

        return grouped;
    }

    /**
     * Processes grouped data and prints JSON output for each month.
     * Computes total income, total expenditure, and transaction list in a single
     * pass
     * per month group — no redundant streaming.
     *
     * @param groupedData YearMonth-grouped wallet entries
     * @throws JsonProcessingException if JSON serialization fails
     */
    static void printMonthlyJson(Map<YearMonth, List<WalletCsv>> groupedData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        for (var entry : groupedData.entrySet()) {
            YearMonth ym = entry.getKey();
            List<WalletCsv> monthEntries = entry.getValue();

            int totalIncome = 0;
            int totalExpenditure = 0;
            List<Transactions> transactions = new ArrayList<>(monthEntries.size());

            // Single pass: accumulate income, expenditure, and build transactions list
            for (WalletCsv csv : monthEntries) {
                int deposit = csv.getDeposit();
                if (deposit > 0) {
                    totalIncome += deposit;
                } else {
                    totalExpenditure += deposit;
                }
                transactions.add(new Transactions(csv.getDate(), deposit, csv.getContent()));
            }

            String period = ym.getYear() + "/" + ym.getMonthValue();

            WalletJson walletJson = new WalletJson(period, totalIncome, totalExpenditure, transactions);
            System.out.println(objectMapper.writeValueAsString(walletJson));
        }
    }

    /**
     * Entry point. Expects a CSV file path as the first command-line argument.
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0].isBlank()) {
            System.err.println("Please pass the file path as a command line argument!");
            return;
        }

        try {
            List<WalletCsv> csvList = csvToWalletCsv(args[0]);
            Map<YearMonth, List<WalletCsv>> grouped = groupByYearMonth(csvList);
            printMonthlyJson(grouped);
        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
        }
    }
}