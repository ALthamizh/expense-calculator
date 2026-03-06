package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Immutable record representing the JSON output for a monthly period.
 * Contains total income, total expenditure, and a list of transactions.
 */
public record WalletJson(String period,

@JsonProperty("total_income")int totalIncome,

@JsonProperty("total_expenditure")int totalExpenditure,

@JsonProperty("transactions")List<Transactions>transactions){}
