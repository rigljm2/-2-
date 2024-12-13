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
// Description :  ���� ��ü �г��� ������ Class �̴�.
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
    // Type : ������
    // Description :  PanTransfer Class�� ������ ����
    //*******************************************************************
    public PanAddAccount(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // Owner : InitGUI
    // Type : Method
    // Description :  ���� ��ü ȭ�� �г��� GUI�� �ʱ�ȭ �ϴ� �޼ҵ� ����
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_Id = new JLabel("������ ID");
        Label_Id.setBounds(0,30,100,20);
        Label_Id.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Id);

        Text_Id = new JTextArea();
        Text_Id.setBounds(100,30,350,20);
        Text_Id.setEditable(true);
        add(Text_Id);

        Label_Account = new JLabel("���ο� ���� ��ȣ");
        Label_Account.setBounds(0,80,100,20);
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Account);

        Text_Account = new JTextArea();
        Text_Account.setBounds(100,80,350,20);
        Text_Account.setEditable(true);
        add(Text_Account);

        Label_Type = new JLabel("���� ����");
        Label_Type.setBounds(0,130,100,20);
        Label_Type.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Type);

        String[] items = {"���¿��ݰ���", "���࿹�ݰ���"};
        Box_Type = new JComboBox(items);
        Box_Type.setBounds(100,130,350,20);
        Box_Type.setEditable(false);
        add(Box_Type);

        Label_Amount = new JLabel("�ݾ�");
        Label_Amount.setBounds(0,180,100,20);
        Label_Amount.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Amount);

        Text_Amount = new JTextArea();
        Text_Amount.setBounds(100,180,350,20);
        Text_Amount.setEditable(true);
        add(Text_Amount);

        Btn_Register = new JButton("���");
        Btn_Register.setBounds(100,250,70,20);
        Btn_Register.addActionListener(this);
        add(Btn_Register);
    }


    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  ��ü ��ư, ��� ��ư�� ������ ����
    //                ��ü, ��� ���� �� ���� ȭ������ ����ǵ��� ����
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
    // Description :  ���� ��ü ȭ���� �����͸� ������ �ִ� CommandDTO�� �����ϰ�,
    //                ManagerMain�� Send ����� ȣ���Ͽ� ������ ������ü ��û �޽����� ���� �ϴ� ���.
    //*******************************************************************
    public void Register_Acc()
    {
        String newid = Text_Id.getText();
        String accountNo = Text_Account.getText();
        long amount = Long.parseLong(Text_Amount.getText());
        AccountType type = Box_Type.getSelectedItem().toString().equals("���¿��ݰ���") ? AccountType.CHECKING : AccountType.SAVINGS;

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
                            contentText = "�������� �ʴ� ���̵��Դϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.WRONG_ACCOUNT_NO)
                        {
                            contentText = "����� �� ���� ���¹�ȣ�Դϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.SUCCESS)
                        {
                            contentText = "���ο� ���°� ��ϵǾ����ϴ�.";
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
