package az.his.android.persist;

import java.util.Date;

public class Transaction {
    private final int amount;
    private final int catId;
    private final Date stamp;

    public Transaction(int amount, int catId, long stamp) {
        this.amount = amount;
        this.catId = catId;
        this.stamp = new Date(stamp);
    }

    public int getAmount() {
        return amount;
    }

    public int getCatId() {
        return catId;
    }

    public Date getStamp() {
        return stamp;
    }
}
