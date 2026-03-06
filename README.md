# 💰 MoneyForward Expense Calculator

A Java command-line tool that reads **MoneyForward wallet CSV exports**, groups transactions by **year and month**, and outputs structured **JSON summaries** — making it easy to analyze your monthly income and expenses.

---

## ✨ Features

- 📄 **CSV Parsing** — Reads MoneyForward wallet exports with Japanese headers (`日付`, `金額`, `内容`)
- 📅 **Monthly Grouping** — Automatically groups transactions by year/month in chronological order
- 💹 **Financial Summary** — Calculates total income and total expenditure per month
- 📊 **JSON Output** — Outputs clean, structured JSON for each month to the console
- 📦 **Executable JAR** — Packaged as a standalone fat JAR using Maven Shade Plugin

---

## 📁 Project Structure

```
expense_calculator/
├── pom.xml
├── src/main/java/
│   ├── moneyforward/
│   │   └── MoneyForwardExamination.java   # Main entry point
│   └── model/
│       ├── WalletCsv.java                 # CSV row model (maps Japanese headers)
│       ├── WalletJson.java                # JSON output model (monthly summary)
│       └── Transactions.java             # Single transaction record
└── src/main/resources/
    └── wallet_input.csv                   # Sample CSV data
```

---

## 📋 Prerequisites

- **Java 17** or higher
- **Apache Maven 3.6+**

---

## 🚀 Getting Started

### 1. Build the project

```bash
mvn clean package
```

### 2. Run the application

```bash
java -jar target/moneyforward-1.0-SNAPSHOT.jar "path/to/your/wallet.csv"
```

> 💡 A sample CSV file is included at `src/main/resources/wallet_input.csv` for testing.

---

## 📥 Input Format (CSV)

The application expects a CSV file with the following Japanese headers:

| Header | Meaning  | Example       |
|--------|----------|---------------|
| `日付`  | Date     | `2023/01/05`  |
| `金額`  | Amount   | `150000` or `-12000` |
| `内容`  | Content  | `給料` (Salary) |

- **Positive amounts** → Income (e.g., salary)
- **Negative amounts** → Expenses (e.g., food, transport)

**Example CSV:**

```csv
日付,金額,内容
2023/01/05,150000,給料
2023/01/10,-12000,食費
2023/01/15,-8500,交通費
2023/01/20,-5000,光熱費
```

---

## 📤 Output Format (JSON)

For each month, the application outputs a JSON object to the console:

```json
{
  "period": "2023/1",
  "total_income": 150000,
  "total_expenditure": -25500,
  "transactions": [
    { "date": "2023/01/05", "amount": 150000, "content": "給料" },
    { "date": "2023/01/10", "amount": -12000, "content": "食費" },
    { "date": "2023/01/15", "amount": -8500, "content": "交通費" },
    { "date": "2023/01/20", "amount": -5000, "content": "光熱費" }
  ]
}
```

---

## 🔧 Tech Stack

| Technology          | Purpose                          |
|---------------------|----------------------------------|
| **Java 17**         | Core language (Records, etc.)    |
| **OpenCSV 5.7.1**   | CSV parsing with bean binding    |
| **Jackson 2.14.2**  | JSON serialization               |
| **Maven Shade**     | Fat JAR packaging                |

---

## 📝 License

This project is for personal/educational use.
