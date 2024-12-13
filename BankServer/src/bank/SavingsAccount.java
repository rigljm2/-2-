package bank;

import common.AccountType;

import java.sql.Date;

public class SavingsAccount extends AccountVO{
    double InterestRate;
    long MaxTransferAmountToChecking;
    public SavingsAccount(String owner, String accountNo,  long balance, Date openDate) {
        this.setOwner(owner);
        this.setAccountNo(accountNo);
        this.setType(AccountType.SAVINGS);
        this.setBalance(balance);
        this.setOpenDate(openDate);
    }
    @Override
    public String display() {
        return "SavingsAccount{" +
            "owner='" + getOwner() + '\'' +
                    ", accountNo='" + getAccountNo() + '\'' +
                    ", type=" + getType() +
                    ", interestRate= " + InterestRate +
                    ", maxTransferAmountToChecking= " + MaxTransferAmountToChecking +
                    ", balance=" + getBalance() +
                    ", openDate=" + getOpenDate() +
                    '}';
    }
}
