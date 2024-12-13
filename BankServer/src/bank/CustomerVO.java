package bank;

import java.io.Serializable;
import java.sql.Array;
import java.util.*;

//*******************************************************************
// Name : CustomerVO
// Type : Class
// Description :  고객정보를 정의 하기 위해 필요한 VO(ValueObject)이다.
//                생성자와, 오브젝트 내부 데이터 get, set 동작이 구현되어 있다.
//                오브젝트 형태로 txt에 저장할수 있도록 implements Serializable를 통해
//                직렬화 되어있다.
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


