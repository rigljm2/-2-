package atm;

import common.AccountType;
import common.CommandDTO;
import common.RequestType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Date;

//*******************************************************************
// Name : PanViewAccount
// Type : Class
// Description :  계좌조회 화면 패널을 구현한 Class 이다.
//*******************************************************************
public class PanViewAccount extends JPanel implements ActionListener, ListSelectionListener
{
    private JLabel Label_Account;
    private JList List_Account; //qqq
    private JLabel Label_Type; //qqq
    private JTextArea Text_Type; //qqq
    private JLabel Label_balance;
    private  JTextArea Text_balance;
    private JLabel Label_Date; //qqq
    private JTextArea Text_Date; //qqq

    private JButton Btn_Close;
    private JScrollPane sp;
    
    private JPanel Pan_left, Pan_right;  //qqq

    ATMMain MainFrame;
    
    //*******************************************************************
    // Name : PanViewAccount()
    // Type : 생성자
    // Description :  PanViewAccount Class의 생성자 구현
    //*******************************************************************
    public PanViewAccount(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  계좌조회 화면 패널의 GUI를 초기화 하는 메소드 구현
    //*******************************************************************
    private void InitGUI() // qqq
    {
        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        setBounds(0,0,480,320);
        
        Pan_left = new JPanel();
        Pan_left.setLayout(new BorderLayout(10,0));
        Pan_left.setPreferredSize(new Dimension(240,320));
        add(Pan_left);
        Pan_right = new JPanel();
        Pan_right.setLayout(null);
        Pan_right.setPreferredSize(new Dimension(240,320));
        add(Pan_right);

        Label_Account = new JLabel(" 계좌 번호");
        Label_Account.setPreferredSize(new Dimension(100, 30));
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        Pan_left.add(Label_Account,BorderLayout.NORTH);

        List_Account = new JList();
        List_Account.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        List_Account.setVisibleRowCount(10);
        List_Account.addListSelectionListener(this);
        sp = new JScrollPane(List_Account);
        Pan_left.add(sp,BorderLayout.CENTER);



        Label_Type = new JLabel("계좌 유형");
        Label_Type.setBounds(20,130,80,20);
        Label_Type.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_Type);
        
        Text_Type = new JTextArea();
        Text_Type.setBounds(20,150,100,20);
        Text_Type.setEditable(false);
        Text_Type.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_Type);
        
        
        Label_balance = new JLabel("잔액");
        Label_balance.setBounds(20,55,100,20);
        Label_balance.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_balance);

        Text_balance = new JTextArea();
        Text_balance.setBounds(20,75,200,20);
        Text_balance.setEditable(false);
        Text_balance.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_balance);


        Label_Date = new JLabel("생성일");
        Label_Date.setBounds(20,205,80,20);
        Label_Date.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_Date);

        Text_Date = new JTextArea();
        Text_Date.setBounds(20,225,100,20);
        Text_Date.setEditable(false);
        Text_Date.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_Date);

        
        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(160,260,70,20);
        Btn_Close.addActionListener(this);
        Pan_right.add(Btn_Close);
    }

    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  취소 버튼의 동작을 구현
    //                취소 동작 후 메인 화면으로 변경되도록 구현
    //*******************************************************************
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    //*******************************************************************
    @Override
    public void valueChanged(ListSelectionEvent e) {
        try {
            GetBalance(((String) List_Account.getSelectedValue()).replaceAll("[^0-9]", ""));

        } catch(RuntimeException o) {
            System.out.print("");
        }
    }
    //*******************************************************************
    // Name : GetBalance()
    // Type : Method
    // Description :  ATMMain의 Send 기능을 호출하여 서버에 계좌조회 요청 메시지를 전달 하는 기능.
    //*******************************************************************

    public void GetBalance(String accNo)
    {
        MainFrame.send(new CommandDTO(RequestType.VIEW_ACCOUNT, accNo), new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public synchronized void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    SwingUtilities.invokeLater(() -> {
                        AccountType type = command.getAccountType();
                        long bal = command.getBalance();
                        Date date = command.getAccountDate();

                        Text_Type.setText(type == AccountType.CHECKING ? "당좌예금계좌" : "저축예금계좌");
                        Text_balance.setText(BankUtils.displayBalance(bal) + "원");
                        Text_Date.setText(date.toString());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }
}
