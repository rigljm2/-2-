package bank;

import common.AccountType;

import java.sql.Date;

public class CheckingAccount extends AccountVO{
    SavingsAccount LinkedSavings;
    public CheckingAccount(String owner, String accountNo,  long balance, Date openDate) {
        this.setOwner(owner);
        this.setAccountNo(accountNo);
        this.setType(AccountType.CHECKING);
        this.setBalance(balance);
        this.setOpenDate(openDate);
    }
    public String display() {
        return "CheckingAccount{" +
            "owner='" + getOwner() + '\'' +
                    ", accountNo='" + getAccountNo() + '\'' +
                    ", type=" + getType() +
                    ", linkedAccountNo=" + LinkedSavings.getAccountNo() +
                    ", balance=" + getBalance() +
                    ", openDate=" + getOpenDate() +
                    '}';

    }
}
