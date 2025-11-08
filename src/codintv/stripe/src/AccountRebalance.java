/**
 * Created by lguo on 1/10/20.
 */
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class AccountRebalance {

    static class Account {
        private String accountName;
        private Double accountBalance;
        ReentrantLock lock; // ReadWriteLock

        public Account(String name, Double balance) {
            this.accountName = name;
            this.accountBalance = balance;
        }

        public void updateBalance(Double delta) {
            try {
                lock.lock(); // writeLock.lock();
                this.accountBalance += delta;
            } finally {
                lock.unlock();
            }
        }

        public void setBalance(Double newBalance) {
            lock.lock();
            this.accountBalance = newBalance;
            lock.unlock();
        }

        public Double getBalance() {
            lock.lock(); // readLock.lock();
            try {
                return this.accountBalance;
            } finally {
                lock.unlock();
            }
        }

        public String toString() {
            return String.format("Account:%s, %f", accountName, accountBalance);
        }
    }

    public static boolean balance(List<Account> accounts) {
        double sum = 0;
        int count = 0;
        for (Account a : accounts) {
            sum += a.getBalance();
            count++;
        }

        double avg = sum / (double) count;
        if (avg < 100.0)
            return false;

        for (Account a : accounts) {
            a.setBalance(avg);
        }
        return true;
    }

    public static void balance2(List<Account> accounts) {
        int payee = 0;
        for (int curr = 0; curr < accounts.size(); ++curr) {
            if (accounts.get(curr).getBalance() <= 100.0) // compare double equal?
                continue;
            Account payFrom = accounts.get(curr);
            double original = accounts.get(curr).getBalance();
            double extra = accounts.get(curr).getBalance() - 100.0;
            for (; extra > 0.0 && payee < accounts.size(); payee++) {
                if (accounts.get(payee).getBalance() <= 100.0) {
                    Account payto = accounts.get(payee);
                    Double delta = 100.0 - payto.getBalance();
                    payto.updateBalance(extra >= delta ? delta : extra);
                    // log payFrom and payTo
                    System.out.printf("Pay %f from %s to %s\n", delta, payFrom.accountName, payto.accountName);
                    original -= extra >= delta ? delta : extra;
                    extra = extra >= delta ? extra-delta : 0.0;
                    // readjust the payee
                    if (payto.getBalance() < 100.0) {
                        break;
                    }
                }
            }
            payFrom.setBalance(original);
        }
    }

    public static void test() {
        Account a1 = new Account("a1", 105.0);
        Account a2 = new Account("a2", 95.0);
        Account a3 = new Account("a3", 120.0);
        Account a4 = new Account("a4", 90.0);
        Account a5 = new Account("a5", 97.0);
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(a1);
        accounts.add(a2);
        accounts.add(a3);
        accounts.add(a4);
        accounts.add(a5);
        balance2(accounts);
        for (Account a : accounts)
            System.out.println(a.toString() + ", ");
    }

    public static void main(String[] args) {
        System.out.println("Account Rebalance");
        test();
    }
}
