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


//*******************************************************************
// # 52
//*******************************************************************
// Name : Client
// Type : Class
// Description :  BankServer�� ���� ��û�� ATM ���ϰ��� ����� ����ϴ� ���� Class ����.
//                �޽��� Receive, Send �� ����, ���, ���� ��ü ���� ����� ���� �Ǿ� �ִ�.
//*******************************************************************
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

    //*******************************************************************
    // # 52-01
    //*******************************************************************
    // Name : receive()
    // Type : method
    // Description :  ������ �޽����� ���۸� CommandDTO Class ���·� ����ȯ ��
    //                ��û ������ �Ľ��Ͽ� �� ��û�� ���� ����� ����
    //*******************************************************************
    private void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        clientChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    disconnectClient();
                    return;
                }
                attachment.flip();
                try {
                    // ���� ������ �Ľ�
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

                        //*************************************************

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
                    // ������ ���� �� �ٽ� �б� ����
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

    //*******************************************************************
    // # 52-02
    //*******************************************************************
    // Name : send()
    // Type : method
    // Description :  ����� ATM ���Ͽ� CommandDTO ���·� �޽����� ����
    //                CommandDTO ���ο� ��û������ ��� ������ ���� �ؾ� �Ѵ�
    //*******************************************************************
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


    //*******************************************************************
    // # 52-03
    //*******************************************************************
    // Name : disconnectClient()
    // Type : method
    // Description :  �ش� ������ ���ܹ߻� Ȥ�� ��� ���н� ���� ���� ��� ����
    //*******************************************************************
    private void disconnectClient() {
        try {
            clientChannel.close();
            handler.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //*******************************************************************
    // # 52-01-01
    //*******************************************************************
    // Name : login()
    // Type : method
    // Description : ATM�� Loging ��û ��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
    private synchronized void login(CommandDTO commandDTO) {
        Optional<CustomerVO> customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId()) && Objects.equals(customerVO.getPassword(), commandDTO.getPassword())).findFirst();

        if (customer.isPresent()) {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            String text = customer.get().getName();
            String text2 = "���� �α����Ͽ����ϴ�.";
            String text3 = text + text2;
            handler.displayInfo(text3);
        } else {
            commandDTO.setResponseType(ResponseType.FAILURE);
        }
        send(commandDTO);
    }

    //*******************************************************************
    // # 52-01-02
    //*******************************************************************
    // Name : view()
    // Type : method
    // Description : ATM�� ���� ��ȸ ��û ��� ����
    //               ���� �ܾ��� ����޽����� ����
    //*******************************************************************
    private synchronized void view(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        // ������ ����
        String[] accounts = user.getAccountsNo();

        commandDTO.setUserAccountList(accounts);


        handler.displayInfo(user.getName() + "���� ���� ������ �����մϴ�. �հ�: " + accounts.length);
        send(commandDTO);
    }

//    private synchronized void view(CommandDTO commandDTO) {
//        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
//        // ������ ����
//        commandDTO.setBalance(customer.getAccount().getBalance());
//        commandDTO.setUserAccountNo(customer.getAccount().getAccountNo());
//        handler.displayInfo(customer.getAccount().getOwner() + "���� ���� �ܾ��� " + customer.getAccount().getBalance() + "�� �Դϴ�.");
//        send(commandDTO);
//    }

    private synchronized void view_account(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        // ������ ����

        AccountType accountType = user.getAccount(commandDTO.getUserAccountNo()).getType();
        long balance = user.getAccount(commandDTO.getUserAccountNo()).getBalance();
        Date date = user.getAccount(commandDTO.getUserAccountNo()).getOpenDate();

        commandDTO.setAccountType(accountType);
        commandDTO.setBalance(balance);
        commandDTO.setAccountDate(date);

        handler.displayInfo(user.getName() + "���� " + commandDTO.getUserAccountNo() + " ���� ������ ��ȸ�մϴ�.");
        send(commandDTO);
    }

    //*******************************************************************
    // # 52-01-03
    //*******************************************************************
    // Name : transfer()
    // Type : method
    // Description : ATM�� ���� ��ü��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
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
            handler.displayInfo(accNo + " ���¿��� " + receivedNo + " ���·� " + commandDTO.getAmount() + "�� ��ü�մϴ�.");
        }
        send(commandDTO);
    }

//    private synchronized void transfer(CommandDTO commandDTO) {
//        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getUserAccountNo())).findFirst().get();
//        Optional<CustomerVO> receiverOptional = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getReceivedAccountNo())).findFirst();
//        if (!receiverOptional.isPresent()) {
//            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
//        } else if (receiverOptional.get().getAccount().getAccountNo().equals(user.getAccount().getAccountNo())) {
//            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
//        } else if (!user.getPassword().equals(commandDTO.getPassword())) {
//            commandDTO.setResponseType(ResponseType.WRONG_PASSWORD);
//        } else if (user.getAccount().getBalance() < commandDTO.getAmount()) {
//            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
//        } else {
//            CustomerVO receiver = receiverOptional.get();
//            commandDTO.setResponseType(ResponseType.SUCCESS);
//            user.getAccount().setBalance(user.getAccount().getBalance() - commandDTO.getAmount());
//            receiver.getAccount().setBalance(receiver.getAccount().getBalance() + commandDTO.getAmount());
//            handler.displayInfo(user.getAccount().getAccountNo() + " ���¿��� " + receiver.getAccount().getAccountNo() + "���·� " + commandDTO.getAmount() + "�� ��ü�Ͽ����ϴ�.");
//        }
//        send(commandDTO);
//    }

    //*******************************************************************
    // # 52-01-04
    //*******************************************************************
    // Name : deposit()
    // Type : method
    // Description : ATM�� ���� �Ա� ��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
    private synchronized void deposit(CommandDTO commandDTO) {
        String accNo = commandDTO.getUserAccountNo();
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();

        if (!Arrays.asList(user.getAccountsNo()).contains(accNo)) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else {
            user.getAccount(accNo).setBalance(user.getAccount(accNo).getBalance() + commandDTO.getAmount());
            commandDTO.setResponseType(ResponseType.SUCCESS);
            handler.displayInfo(user.getName() + "���� " + accNo + " ���¿� " + commandDTO.getAmount() + "�� �Ա��Ͽ����ϴ�.");
        }
        send(commandDTO);

    }

//    private synchronized void deposit(CommandDTO commandDTO) {
//        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getUserAccountNo())).findFirst().get();
//        user.getAccount().setBalance(user.getAccount().getBalance() + commandDTO.getAmount());
//        commandDTO.setResponseType(ResponseType.SUCCESS);
//        handler.displayInfo(user.getName() + "���� " + user.getAccount().getAccountNo() + " ���¿� " + commandDTO.getAmount() + "�� �Ա��Ͽ����ϴ�.");
//        send(commandDTO);
//    }

    //*******************************************************************
    // # 52-01-05
    //*******************************************************************
    // Name : withdraw()
    // Type : method
    // Description : ATM�� ���� ��� ��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
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
            handler.displayInfo(user.getName() + "���� " + accNo + " ���¿��� " + commandDTO.getAmount() + "�� ����Ͽ����ϴ�.");
        }
        send(commandDTO);
    }

    //*******************************************************************
    private synchronized void login_manager(CommandDTO commandDTO) {
        Optional<ManagerVO> manager = this.managerList.stream().filter(managerVO -> Objects.equals(managerVO.getId(), commandDTO.getId()) && Objects.equals(managerVO.getPassword(), commandDTO.getPassword())).findFirst();

        if (manager.isPresent()) {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            String text = manager.get().getName();
            String text2 = "���� ������ �α����Ͽ����ϴ�.";
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
            handler.displayInfo("���ο� �� " + commandDTO.getName() + "���� ������ ��ϵǾ����ϴ�.");
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
            handler.displayInfo(user.getName() + "���� " + accountNo + " ���°� ���� ��ϵǾ����ϴ�. ���� �ݾ�: " +
                    commandDTO.getAmount() + "��");
        }
        send(commandDTO);
    }

    private synchronized void list_customer(CommandDTO commandDTO) {
        List<String> idList = this.customerList.stream().map(CustomerVO::getId).collect(Collectors.toList());
        // ������ ����

        commandDTO.setIdList(idList.toArray(new String[0]));

        handler.displayInfo("��� �� ������ �����մϴ�.");
        send(commandDTO);
    }

    private synchronized void info_customer(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getnewId())).findFirst().get();
        // ������ ����

        String name = user.getName();
        String password = user.getPassword();
        String[] accounts = user.getAccountsNo();
        long balance = user.getAccounts().stream().map(AccountVO::getBalance).mapToLong(Long::longValue).sum();

        commandDTO.setName(name);
        commandDTO.setPassword(password);
        commandDTO.setUserAccountList(accounts);
        commandDTO.setBalance(balance);

        handler.displayInfo(commandDTO.getnewId() + "-" + name + "���� �� ������ ��ȸ�մϴ�.");
        send(commandDTO);
    }

    private synchronized void del_customer(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getnewId())).findFirst().get();

        commandDTO.setName(user.getName());
        this.customerList.remove(user);

        handler.displayInfo(commandDTO.getnewId() + "-" + user.getName() + "���� �� ������ �����մϴ�.");
        send(commandDTO);

    }

    private synchronized void list_account(CommandDTO commandDTO) {
        String[] accounts = this.customerList.stream().map(CustomerVO::getAccountsNo).flatMap(Arrays::stream)
                .toArray(String[]::new);
        // ������ ����

        commandDTO.setUserAccountList(accounts);

        handler.displayInfo("��� ���� ������ �����մϴ�.");
        send(commandDTO);
    }

    private synchronized void info_account(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Arrays.asList(customerVO.getAccountsNo()).contains(commandDTO.getUserAccountNo())).findFirst().get();
        // ������ ����

        String name = user.getAccount(commandDTO.getUserAccountNo()).getOwner();
        AccountType accountType = user.getAccount(commandDTO.getUserAccountNo()).getType();
        long balance = user.getAccount(commandDTO.getUserAccountNo()).getBalance();
        Date date = user.getAccount(commandDTO.getUserAccountNo()).getOpenDate();

        commandDTO.setName(name);
        commandDTO.setAccountType(accountType);
        commandDTO.setBalance(balance);
        commandDTO.setAccountDate(date);

        handler.displayInfo(user.getName() + "���� " + commandDTO.getUserAccountNo() + " ���� ������ ��ȸ�մϴ�.");
        send(commandDTO);
    }

    private synchronized void del_account(CommandDTO commandDTO) {
        CustomerVO user = this.customerList.stream().filter(customerVO -> Arrays.asList(customerVO.getAccountsNo()).contains(commandDTO.getUserAccountNo())).findFirst().get();

        commandDTO.setName(user.getName());
        AccountVO delAcc = user.getAccounts().stream().filter(a -> a.getAccountNo().equals(commandDTO.getUserAccountNo())).findFirst().get();
        user.deleteAccount(delAcc);

        handler.displayInfo(user.getName() + "���� " + commandDTO.getUserAccountNo() + " ���� ������ �����մϴ�.");
        send(commandDTO);

    }
}
