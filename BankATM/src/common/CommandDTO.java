package common;

import java.io.Serializable;
import java.util.Date;


//*******************************************************************
// # 91
//*******************************************************************
// Name : CommandDTO
// Type : Class
// Description :  ATM 과 Sever 사이의 통신 프로토콜을 정의 하기 위해 필요한 DTO(DataTransferObject)이다.
//                생성자와, 오브젝트 내부 데이터 get, set 동작이 구현되어 있다.
//*******************************************************************

@SuppressWarnings("serial")
public class CommandDTO implements Serializable {
    private RequestType requestType;
    private String name;
    private String id;
    private String[] idList;
    private String newid;
    private String password;
    private String password2;
    private String userAccountNo;
    private String[] userAccountList;
    private String receivedAccountNo;
    private long amount;
    private long balance;
    private AccountType accountType;
    private Date accountDate;
    private ResponseType responseType;

    public CommandDTO() {
    }

    public CommandDTO(RequestType requestType) {
        this.requestType = requestType;
    }

    public CommandDTO(ResponseType responseType) {
        this.responseType = responseType;
    }

    public CommandDTO(RequestType requestType, String userAccountNo) {
        this.requestType = requestType;
        this.userAccountNo = userAccountNo;
    }
    public CommandDTO(int i, RequestType requestType, String newid) {
        this.requestType = requestType;
        this.newid = newid;
    }

    public CommandDTO(RequestType requestType, String userAccountNo, long amount) {
		this.requestType = requestType;
		this.userAccountNo = userAccountNo;
		this.amount = amount;
	}


	public CommandDTO(RequestType requestType, String name, String newid, String password, String password2) {
        this.requestType = requestType;
        this.name = name;
        this.newid = newid;
        this.password = password;
        this.password2 = password2;
    }
    public CommandDTO(RequestType requestType, String id, String password) {
        this.requestType = requestType;
        this.id = id;
        this.password = password;
    }

    public CommandDTO(RequestType requestType, String newid, String userAccountNo, long amount, AccountType accountType) {
        this.requestType = requestType;
        this.newid = newid;
        this.userAccountNo = userAccountNo;
        this.amount = amount;
        this.accountType = accountType;
    }

    public CommandDTO(RequestType requestType, String password, String userAccountNo, String receivedAccountNo, long amount) {
        this.requestType = requestType;
        this.password = password;
        this.userAccountNo = userAccountNo;
        this.receivedAccountNo = receivedAccountNo;
        this.amount = amount;
    }

    public CommandDTO(RequestType requestType, String userAccountNo, String receivedAccountNo, long amount, long balance) {
        this.requestType = requestType;
        this.userAccountNo = userAccountNo;
        this.receivedAccountNo = receivedAccountNo;
        this.amount = amount;
        this.balance = balance;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String[] getIdList() {
        return idList;
    }

    public void setIdList(String[] idList) {
        this.idList = idList;
    }
    public String getnewId() {
        return newid;
    }

    public void setnewId(String newid) {
        this.newid = newid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getUserAccountNo() {
        return userAccountNo;
    }

    public void setUserAccountNo(String userAccountNo) {
        this.userAccountNo = userAccountNo;
    }
    public String[] getUserAccountList() {
        return userAccountList;
    }

    public void setUserAccountList(String[] userAccountList) {
        this.userAccountList = userAccountList;
    }

    public String getReceivedAccountNo() {
        return receivedAccountNo;
    }

    public void setReceivedAccountNo(String receivedAccountNo) {
        this.receivedAccountNo = receivedAccountNo;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    public Date getAccountDate() {
        return accountDate;
    }

    public void setAccountDate(Date accountDate) {
        this.accountDate = accountDate;
    }


    @Override
    public String toString() {
        return "CommandDTO{" +
                "requestType=" + requestType +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", userAccountNo='" + userAccountNo + '\'' +
                ", receivedAccountNo='" + receivedAccountNo + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", responseType=" + responseType +
                '}';
    }
}
