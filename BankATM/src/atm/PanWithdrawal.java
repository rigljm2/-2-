package atm;

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
// Name : PanWithdrawal
// Type : Class
// Description :  ��� ȭ�� �г��� ������ Class �̴�.
//*******************************************************************
public class PanWithdrawal extends JPanel implements ActionListener
{
    private JLabel Label_Title;

    private JLabel Label_Account;
    private JTextField Text_Account;

    private JLabel Label_Amount;
    private JTextField Text_Amount;


    private JButton Btn_Transfer;
    private JButton Btn_Close;

    ATMMain MainFrame;

    //*******************************************************************
    // Name : PanWithdrawal()
    // Type : ������
    // Description :  PanDeposite Class�� ������ ����
    //*******************************************************************
    public PanWithdrawal(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  ��� ȭ�� �г��� GUI�� �ʱ�ȭ �ϴ� �޼ҵ� ����
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);


        Label_Title = new JLabel("���");
        Label_Title.setBounds(0,0,480,40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Label_Account = new JLabel("����");
        Label_Account.setBounds(0,95,100,20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Text_Account = new JTextField();
        Text_Account.setBounds(100,95,350,20);
        Text_Account.setEditable(true);
        add(Text_Account);


        Label_Amount = new JLabel("�ݾ�");
        Label_Amount.setBounds(0,145,100,20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100,145,350,20);
        Text_Amount.setEditable(true);
        Text_Amount.setToolTipText("���ڸ� �Է�");
        add(Text_Amount);

        Btn_Transfer = new JButton("���");
        Btn_Transfer.setBounds(100,250,70,20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("���");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  �Ա� ��ư, ��� ��ư�� ������ ����
    //                �Ա�, ��� ���� �� ���� ȭ������ ����ǵ��� ����
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Transfer)
        {
            Withdrawal();
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    //*******************************************************************
    // Name : Withdrawal()
    // Type : Method
    // Description :  ��� ȭ���� �����͸� ������ �ִ� CommandDTO�� �����ϰ�,
    //                ATMMain�� Send ����� ȣ���Ͽ� ������ ��� ��û �޽����� ���� �ϴ� ���.
    //*******************************************************************
    public void Withdrawal() {
        String account = Text_Account.getText();
        long amount = Long.parseLong(Text_Amount.getText());

        CommandDTO commandDTO = new CommandDTO(RequestType.WITHDRAW, account, amount);
        MainFrame.send(commandDTO, new CompletionHandler<Integer, ByteBuffer>()
        {
            @Override
            public void completed(Integer result, ByteBuffer attachment)
            {
                if (result == -1)
                {
                    return;
                }
                attachment.flip();
                try
                {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    SwingUtilities.invokeLater(() ->
                    {
                        String contentText = null;
                        if (command.getResponseType() == ResponseType.SUCCESS)
                        {
                            contentText = "��� �Ǿ����ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.INSUFFICIENT)
                        {

                            contentText = "�ܾ��� �����մϴ�";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.WRONG_ACCOUNT_NO)
                        {

                            contentText = "�߸��� ���¹�ȣ �Դϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            contentText = "ERROR";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
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
