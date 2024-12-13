package common;


//*******************************************************************
// # 92
//*******************************************************************
// Name : RequestType
// Type : Enum
// Description :  ATM 이 Server에 요청할 기능을 Enum으로 나타낸 열거형 데이터를 구현
//                생성자와, 오브젝트 내부 데이터 get, set 동작이 구현되어 있다.
//*******************************************************************
public enum RequestType {
    VIEW("계좌항목 조회", 10),
    VIEW_ACCOUNT("계좌정보 조회", 15),
    TRANSFER("계좌이체", 20),
    EXCHANGE("환전", 25),
    DEPOSIT("입금", 30),
    WITHDRAW("출금", 40),
    LOGIN("로그인", 50),

    //***************************************
    REGISTER_CUSTOMER("고객 등록", 60),
    REGISTER_ACCOUNT("계좌 등록", 70),
    LIST_CUTOMER("고객 리스트", 75),
    INFO_CUSTOMER("특정 고객 정보", 80),
    DEL_CUSTOMER("고객 삭제", 90),
    LIST_ACCOUNT("계좌 리스트", 105),
    INFO_ACCOUNT("특정 계좌 정보", 100),
    DEL_ACCOUNT("계좌 삭제", 110),
    LOGIN_MANAGER("관리자 로그인",120),

    BANK_INFO("은행 정보", 99);

    private String name;
    private int number;

    RequestType(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
