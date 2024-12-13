package bank;

import java.io.Serializable;
import java.sql.Array;
import java.util.*;

//*******************************************************************
// Name : CustomerVO
// Type : Class
// Description :  �������� ���� �ϱ� ���� �ʿ��� VO(ValueObject)�̴�.
//                �����ڿ�, ������Ʈ ���� ������ get, set ������ �����Ǿ� �ִ�.
//                ������Ʈ ���·� txt�� �����Ҽ� �ֵ��� implements Serializable�� ����
//                ����ȭ �Ǿ��ִ�.
//*******************************************************************
public class CustomerVO implements Serializable {
    private String id;
    private String name;
    private String password;
    private String address;
    private String phone;
    private List<AccountVO> accounts; // qqq

    public CustomerVO() {
    }

    public CustomerVO(String id, String name, String password) { // qqq
        this.accounts = new ArrayList<AccountVO>();
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public CustomerVO(String id, String name, String password, AccountVO account) { // qqq
        this.accounts = new ArrayList<AccountVO>();
        this.id = id;
        this.name = name;
        this.password = password;
        this.accounts.add(account);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public List<AccountVO> getAccounts() {return new ArrayList<>(accounts);} // qqq

    public String[] getAccountsNo() {
        return accounts.stream().map(AccountVO::getAccountNo).toArray(String[]::new);
    }
    public AccountVO getAccount(String num) {

        AccountVO account = accounts.stream().filter(a -> a.getAccountNo().equals(num)).findFirst().get();
        return account;
    }

    public void addAccount(AccountVO account) {
        this.accounts.add(account);
    }
    public void deleteAccount(AccountVO account) {
        this.accounts.remove(account);
    }


    @Override
    public String toString() {
        return "CustomerVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", account=" + accounts +
                '}';
    }
}


