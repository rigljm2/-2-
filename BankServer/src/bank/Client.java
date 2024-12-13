package bank;

import common.AccountType;
import common.CommandDTO;
import common.ResponseType;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Date;
import java.util.stream.Collectors;


public class Client {
    private AsynchronousSocketChannel clientChannel;
    private ClientHandler handler;
    private List<CustomerVO> customerList;
    private List<ManagerVO> managerList;


    public Client(AsynchronousSocketChannel clientChannel, ClientHandler handler, List<CustomerVO> customerList, List<ManagerVO> managerList) {
        this.clientChannel = clientChannel;
        this.handler = handler;
        this.customerList = customerList;
        this.managerList = managerList;
        receive();
    }

    // 수신 작업: 서버에서 메시지를 받기 위한 메서드
    private void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        clientChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    disconnectClient();
                    return;
                }
                attachment.flip();
                try {
                    // 읽은 데이터 파싱
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    switch (command.getRequestType()) {
                        case VIEW:
                            view(command);
                            break;
                        case VIEW_ACCOUNT:
                            view_account(command);
                            break;
                        case LOGIN:
                            login(command);
                            break;
                        case TRANSFER:
                            transfer(command);
                            break;
                        case DEPOSIT:
                            deposit(command);
                            break;
                        case WITHDRAW:
                            withdraw(command);
                            break;

                        case REGISTER_CUSTOMER:
                            register_customer(command);
                            break;
                        case REGISTER_ACCOUNT:
                            register_account(command);
                            break;
                        case LIST_CUTOMER:
                            list_customer(command);
                            break;
                        case INFO_CUSTOMER:
                            info_customer(command);
                            break;
                        case DEL_CUSTOMER:
                            del_customer(command);
                            break;
                        case LIST_ACCOUNT:
                            list_account(command);
                            break;
                        case INFO_ACCOUNT:
                            info_account(command);
                            break;
                        case DEL_ACCOUNT:
                            del_account(command);
                            break;
                        case LOGIN_MANAGER:
                            login_manager(command);
                            break;
                        default:
                            break;
                    }
                    // 데이터 보낸 후 다시 읽기 모드로
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                    clientChannel.read(byteBuffer, byteBuffer, this);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                disconnectClient();
            }
        });
    }

    // 서버에서 클라이언트로 CommandDTO를 전송하는 메서드 (바이트 배열 전송)
    private void send(CommandDTO commandDTO) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(commandDTO);
            objectOutputStream.flush();
            clientChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 클라이언트 연결 종료 처리
    private void disconnectClient() {
        try {
            clientChannel.close();
            handler.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 로그인 처리
    private synchronized void login(CommandDTO commandDTO) {
        Optional<CustomerVO> customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId()) && Objects.equals(customerVO.getPassword(), commandDTO.getPassword())).findFirst();

        if (customer.isPresent()) {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            String text = customer.get().getName();
            String text2 = "님이 로그인하였습니다.";
            String text3 = text + text2;
            handler.displayInfo(text3);
        } else {
            commandDTO.setResponseType(ResponseType.FAILURE);
        }
        send(commandDTO);
    }

    // 계좌 조회 처리
    private synchronized void view(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream()
                .filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId()))
                .findFirst().get();

        String[] accounts = user.getAccountsNo();

        commandDTO.setUserAccountList(accounts);


        handler.displayInfo(user.getName() + "님의 계좌 정보에 접근합니다. 합계: " + accounts.length);
        send(commandDTO);
    }


    private synchronized void view_account(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        // 데이터 전송

        AccountType accountType = user.getAccount(commandDTO.getUserAccountNo()).getType();
        long balance = user.getAccount(commandDTO.getUserAccountNo()).getBalance();
        Date date = user.getAccount(commandDTO.getUserAccountNo()).getOpenDate();

        commandDTO.setAccountType(accountType);
        commandDTO.setBalance(balance);
        commandDTO.setAccountDate(date);

        handler.displayInfo(user.getName() + "님의 " + commandDTO.getUserAccountNo() + " 계좌 정보를 조회합니다.");
        send(commandDTO);
    }

    private synchronized void transfer(CommandDTO commandDTO) {
        String accNo = commandDTO.getUserAccountNo();
        String receivedNo = commandDTO.getReceivedAccountNo();
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        Optional<CustomerVO> receiverOptional = this.customerList.stream().filter(customerVO -> Arrays.asList(customerVO.getAccountsNo()).contains(receivedNo)).findFirst();

        if (!Arrays.asList(user.getAccountsNo()).contains(accNo)) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (!receiverOptional.isPresent() ) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (accNo.equals(receivedNo)) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (!user.getPassword().equals(commandDTO.getPassword())) {
            commandDTO.setResponseType(ResponseType.WRONG_PASSWORD);
        } else if (user.getAccount(accNo).getBalance() < commandDTO.getAmount()) {
            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
        } else {
            CustomerVO receiver = receiverOptional.get();
            commandDTO.setResponseType(ResponseType.SUCCESS);
            user.getAccount(accNo).setBalance(user.getAccount(accNo).getBalance() - commandDTO.getAmount());
            receiver.getAccount(receivedNo).setBalance(receiver.getAccount(receivedNo).getBalance() + commandDTO.getAmount());
            handler.displayInfo(accNo + " 계좌에서 " + receivedNo + " 계좌로 " + commandDTO.getAmount() + "원 이체합니다.");
        }
        send(commandDTO);
    }




    // 입금 처리
    private synchronized void deposit(CommandDTO commandDTO) {
        String accNo = commandDTO.getUserAccountNo();
        CustomerVO user = this.customerList.stream()
                .filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId()))
                .findFirst().get();

        if (!Arrays.asList(user.getAccountsNo()).contains(accNo)) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else {
            user.getAccount(accNo).setBalance(user.getAccount(accNo).getBalance() + commandDTO.getAmount());
            commandDTO.setResponseType(ResponseType.SUCCESS);
            handler.displayInfo(user.getName() + "님이 " + accNo + " 계좌에 " + commandDTO.getAmount() + "원 입금하였습니다.");
        }
        send(commandDTO);

    }

    // 출금 처리
    private synchronized void withdraw(CommandDTO commandDTO) {
        String accNo = commandDTO.getUserAccountNo();
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();

        if (!Arrays.asList(user.getAccountsNo()).contains(accNo)) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        }
        else if (user.getAccount(accNo).getBalance() < commandDTO.getAmount()) {
            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
        } else {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            user.getAccount(accNo).setBalance(user.getAccount(accNo).getBalance() - commandDTO.getAmount());
            handler.displayInfo(user.getName() + "님이 " + accNo + " 계좌에서 " + commandDTO.getAmount() + "원 출금하였습니다.");
        }
        send(commandDTO);
    }

    //*******************************************************************
    private synchronized void login_manager(CommandDTO commandDTO) {
        Optional<ManagerVO> manager = this.managerList.stream().filter(managerVO -> Objects.equals(managerVO.getId(), commandDTO.getId()) && Objects.equals(managerVO.getPassword(), commandDTO.getPassword())).findFirst();

        if (manager.isPresent()) {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            String text = manager.get().getName();
            String text2 = "님이 관리자 로그인하였습니다.";
            String text3 = text + text2;
            handler.displayInfo(text3);
        } else {
            commandDTO.setResponseType(ResponseType.FAILURE);
        }
        send(commandDTO);
    }

    private synchronized void register_customer(CommandDTO commandDTO) {
        String newId = commandDTO.getnewId();
        String password = commandDTO.getPassword();
        String password2 = commandDTO.getPassword2();
        Optional<CustomerVO> customerOptional = this.customerList.stream().filter(customerVO -> customerVO.getId().equals(newId)).findFirst();

        if (customerOptional.isPresent()) {
            commandDTO.setResponseType(ResponseType.WRONG_ID);
        } else if(!password.equals(password2)){
            commandDTO.setResponseType(ResponseType.WRONG_PASSWORD);
        }
        else {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            customerList.add(new CustomerVO(newId, commandDTO.getName(), password));
            handler.displayInfo("새로운 고객 " + commandDTO.getName() + "님의 정보가 등록되었습니다.");
        }
        send(commandDTO);
    }
    private synchronized void register_account(CommandDTO commandDTO) {
        String newId = commandDTO.getnewId();
        String accountNo = commandDTO.getUserAccountNo();
        AccountType type = commandDTO.getAccountType();
        Optional<CustomerVO> customerOptional = this.customerList.stream().filter(customerVO -> customerVO.getId().equals(newId)).findFirst();
        Optional<CustomerVO> sameAccountOptional = this.customerList.stream().filter(customerVO -> Arrays.asList(customerVO.getAccountsNo()).contains(accountNo)).findFirst();

        if (!customerOptional.isPresent()) {
            commandDTO.setResponseType(ResponseType.WRONG_ID);
        } else if(sameAccountOptional.isPresent()){
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else {
            CustomerVO user = customerOptional.get();
            commandDTO.setResponseType(ResponseType.SUCCESS);
            if(type == AccountType.CHECKING) {
                user.addAccount(new CheckingAccount(user.getName(), accountNo, commandDTO.getAmount(), java.sql.Date.valueOf(LocalDate.now())));
            } else if (type == AccountType.SAVINGS) {
                user.addAccount(new SavingsAccount(user.getName(), accountNo, commandDTO.getAmount(), java.sql.Date.valueOf(LocalDate.now())));
            }
            handler.displayInfo(user.getName() + "님의 " + accountNo + " 계좌가 새로 등록되었습니다. 보유 금액: " +
                    commandDTO.getAmount() + "원");
        }
        send(commandDTO);
    }

    private synchronized void list_customer(CommandDTO commandDTO) {
        List<String> idList = this.customerList.stream().map(CustomerVO::getId).collect(Collectors.toList());
        // 데이터 전송

        commandDTO.setIdList(idList.toArray(new String[0]));

        handler.displayInfo("모든 고객 정보에 접근합니다.");
        send(commandDTO);
    }

    private synchronized void info_customer(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getnewId())).findFirst().get();
        // 데이터 전송

        String name = user.getName();
        String password = user.getPassword();
        String[] accounts = user.getAccountsNo();
        long balance = user.getAccounts().stream().map(AccountVO::getBalance).mapToLong(Long::longValue).sum();

        commandDTO.setName(name);
        commandDTO.setPassword(password);
        commandDTO.setUserAccountList(accounts);
        commandDTO.setBalance(balance);

        handler.displayInfo(commandDTO.getnewId() + "-" + name + "님의 고객 정보를 조회합니다.");
        send(commandDTO);
    }

    private synchronized void del_customer(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getnewId())).findFirst().get();

        commandDTO.setName(user.getName());
        this.customerList.remove(user);

        handler.displayInfo(commandDTO.getnewId() + "-" + user.getName() + "님의 고객 정보를 삭제합니다.");
        send(commandDTO);

    }

    private synchronized void list_account(CommandDTO commandDTO) {
        String[] accounts = this.customerList.stream().map(CustomerVO::getAccountsNo).flatMap(Arrays::stream)
                .toArray(String[]::new);
        // 데이터 전송

        commandDTO.setUserAccountList(accounts);

        handler.displayInfo("모든 계좌 정보에 접근합니다.");
        send(commandDTO);
    }

    private synchronized void info_account(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Arrays.asList(customerVO.getAccountsNo()).contains(commandDTO.getUserAccountNo())).findFirst().get();
        // 데이터 전송

        String name = user.getAccount(commandDTO.getUserAccountNo()).getOwner();
        AccountType accountType = user.getAccount(commandDTO.getUserAccountNo()).getType();
        long balance = user.getAccount(commandDTO.getUserAccountNo()).getBalance();
        Date date = user.getAccount(commandDTO.getUserAccountNo()).getOpenDate();

        commandDTO.setName(name);
        commandDTO.setAccountType(accountType);
        commandDTO.setBalance(balance);
        commandDTO.setAccountDate(date);

        handler.displayInfo(user.getName() + "님의 " + commandDTO.getUserAccountNo() + " 계좌 정보를 조회합니다.");
        send(commandDTO);
    }

    private synchronized void del_account(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Arrays.asList(customerVO.getAccountsNo()).contains(commandDTO.getUserAccountNo())).findFirst().get();

        commandDTO.setName(user.getName());
        AccountVO delAcc = user.getAccounts().stream().filter(a -> a.getAccountNo().equals(commandDTO.getUserAccountNo())).findFirst().get();
        user.deleteAccount(delAcc);

        handler.displayInfo(user.getName() + "님의 " + commandDTO.getUserAccountNo() + " 계좌 정보를 삭제합니다.");
        send(commandDTO);

    }
}