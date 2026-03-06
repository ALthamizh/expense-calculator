package model;

/**
 * Immutable record representing a single financial transaction.
 * Records automatically generate constructor, getters, equals, hashCode, and
 * toString.
 */
public record Transactions(String date,int amount,String content){}
