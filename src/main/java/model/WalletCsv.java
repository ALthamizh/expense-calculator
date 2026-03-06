package model;

import com.opencsv.bean.CsvBindByName;

public class WalletCsv {

    @CsvBindByName(column = "日付")
    private String date;

    @CsvBindByName(column = "金額")
    private int deposit;

    @CsvBindByName(column = "内容")
    private String content;

    public String getDate() {
        return date;
    }

    public int getDeposit() {
        return deposit;
    }

    public String getContent() {
        return content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WalletCsv{" +
                "date='" + date + '\'' +
                ", deposit=" + deposit +
                ", content='" + content + '\'' +
                '}';
    }
}
