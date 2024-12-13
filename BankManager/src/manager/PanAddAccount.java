package manager;

import common.AccountType;
import common.CommandDTO;
import common.RequestType;
import common.ResponseType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

//*******************************************************************
// Name : PanTransfer
// Type : Class
// Description :  계좌 이체 패널을 구현한 Class 이다.
//*******************************************************************

public class PanAddAccount extends JPanel implements ActionListener
{
    private JLabel Label_Id;
    private  JTextArea Text_Id;
    private JLabel Label_Account;
    private  JTextArea Text_Account;
    private JLabel Label_Type;
    private  JComboBox Box_Type;
    private JLabel Label_Amount;
    private  JTextArea Text_Amount;

    private JButton Btn_Register;

    ManagerMain MainFrame;

    //*******************************************************************
    // Owner : PanTransfer()
    // Type : 생성자
    // Description :  PanTransfer Class의 생성자 구현
    //*******************************************************************
    public PanAddAccount(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // Owner : InitGUI
    // Type : Method
    // Description :  계좌 이체 화면 패널의 GUI를 초기화 하는 메소드 구현
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_Id = new JLabel("소유주 ID");
        Label_Id.setBounds(0,30,100,20);
        Label_Id.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Id);

        Text_Id = new JTextArea();
        Text_Id.setBounds(100,30,350,20);
        Text_Id.setEditable(true);
        add(Text_Id);

        Label_Account = new JLabel("새로운 계좌 번호");
        Label_Account.setBounds(0,80,100,20);
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Account);

        Text_Account = new JTextArea();
        Text_Account.setBounds(100,80,350,20);
        Text_Account.setEditable(true);
        add(Text_Account);

        Label_Type = new JLabel("계좌 유형");
        Label_Type.setBounds(0,130,100,20);
        Label_Type.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Type);

        String[] items = {"당좌예금계좌", "저축예금계좌"};
        Box_Type = new JComboBox(items);
        Box_Type.setBounds(100,130,350,20);
        Box_Type.setEditable(false);
        add(Box_Type);

        Label_Amount = new JLabel("금액");
        Label_Amount.setBounds(0,180,100,20);
        Label_Amount.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Amount);

        Text_Amount = new JTextArea();
        Text_Amount.setBounds(100,180,350,20);
        Text_Amount.setEditable(true);
        add(Text_Amount);

        Btn_Register = new JButton("등록");
        Btn_Register.setBounds(100,250,70,20);
        Btn_Register.addActionListener(this);
        add(Btn_Register);
    }


    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  이체 버튼, 취소 버튼의 동작을 구현
    //                이체, 취소 동작 후 메인 화면으로 변경되도록 구현
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Register)
        {
            Register_Acc();
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    //*******************************************************************
    // Name : Transfer()
    // Type : Method
    // Description :  계좌 이체 화면의 데이터를 가지고 있는 CommandDTO를 생성하고,
    //                ManagerMain의 Send 기능을 호출하여 서버에 계좌이체 요청 메시지를 전달 하는 기능.
    //*******************************************************************
    public void Register_Acc()
    {
        String newid = Text_Id.getText();
        String accountNo = Text_Account.getText();
        long amount = Long.parseLong(Text_Amount.getText());
        AccountType type = Box_Type.getSelectedItem().toString().equals("당좌예금계좌") ? AccountType.CHECKING : AccountType.SAVINGS;

        CommandDTO commandDTO = new CommandDTO(RequestType.REGISTER_ACCOUNT, newid, accountNo, amount, type);
        MainFrame.send(commandDTO, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    SwingUtilities.invokeLater(() ->
                    {
                        String contentText = null;

                        if (command.getResponseType() == ResponseType.WRONG_ID)
                        {
                            contentText = "존재하지 않는 아이디입니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.WRONG_ACCOUNT_NO)
                        {
                            contentText = "사용할 수 없는 계좌번호입니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.SUCCESS)
                        {
                            contentText = "새로운 계좌가 등록되었습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

}
