package manager;

import common.AccountType;
import common.CommandDTO;
import common.RequestType;
import common.ResponseType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.List;


//*******************************************************************
// # 05
//*******************************************************************
// Name : PanDeposite
// Type : Class
// Description :  입금 화면 패널을 구현한 Class 이다.
//*******************************************************************
public class PanViewAllCustomer extends JPanel implements ActionListener, ListSelectionListener
{
    private JLabel Label_Customer;
    private JList List_Customer; //qqq
    private JLabel Label_Name; //qqq
    private JTextArea Text_Name; //qqq

    private JLabel Label_Id;
    private  JTextArea Text_Id;
    private JLabel Label_Password; //qqq
    private JTextArea Text_Password; //qqq
    private JLabel Label_Count; //qqq
    private JTextArea Text_Count; //qqq
    private JLabel Label_balance; //qqq
    private JTextArea Text_balance; //qqq

    private JButton Btn_Close, Btn_Delete;
    private JScrollPane sp;

    private JPanel Pan_left, Pan_right;  //qqq

    ManagerMain MainFrame;


    //*******************************************************************
    // # 05-01
    //*******************************************************************
    // Name : PanDeposite()
    // Name : 생성자
    // Description :  PanDeposite Class의 생성자 구현
    //*******************************************************************
    public PanViewAllCustomer(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }

    //*******************************************************************
    // # 05-02
    //*******************************************************************
    // Name : InitGUI
    // Name : Method
    // Description :  입금 화면 패널의 GUI를 초기화 하는 메소드 구현
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        setBounds(0,0,480,320);

        Pan_left = new JPanel();
        Pan_left.setLayout(new BorderLayout(10,0));
        Pan_left.setPreferredSize(new Dimension(240,320));
        //Pan_left.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        add(Pan_left);
        Pan_right = new JPanel();
        Pan_right.setLayout(null);
        Pan_right.setPreferredSize(new Dimension(240,320));
        //  Pan_right.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        add(Pan_right);

        Label_Customer = new JLabel(" ID");
        Label_Customer.setPreferredSize(new Dimension(100, 30));
        //Label_Customer.setBounds(0,0,100,20);
        Label_Customer.setHorizontalAlignment(JLabel.LEFT);
        Pan_left.add(Label_Customer,BorderLayout.NORTH);

        List_Customer = new JList();
        //List_Customer.setBounds(0,0,200,150);
        //List_Customer.setPreferredSize(new Dimension(200,100));
        //List_Customer.setLocation(0,30);
        List_Customer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        List_Customer.setVisibleRowCount(10);
        List_Customer.addListSelectionListener(this);
        sp = new JScrollPane(List_Customer);
        Pan_left.add(sp,BorderLayout.CENTER);



        Label_Name = new JLabel("이름");
        Label_Name.setBounds(20,30,80,20);
        Label_Name.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_Name);

        Text_Name = new JTextArea();
        Text_Name.setBounds(20,50,100,20);
        Text_Name.setEditable(false);
        Text_Name.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_Name);


        Label_Id = new JLabel("아이디");
        Label_Id.setBounds(20,80,100,20);
        Label_Id.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_Id);

        Text_Id = new JTextArea();
        Text_Id.setBounds(20,100,200,20);
        Text_Id.setEditable(false);
        Text_Id.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_Id);


        Label_Password = new JLabel("비밀번호");
        Label_Password.setBounds(20,130,80,20);
        Label_Password.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_Password);

        Text_Password = new JTextArea();
        Text_Password.setBounds(20,150,200,20);
        Text_Password.setEditable(false);
        Text_Password.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_Password);

        Label_balance = new JLabel("총보유금액");
        Label_balance.setBounds(20,180,80,20);
        Label_balance.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_balance);

        Text_balance = new JTextArea();
        Text_balance.setBounds(20,200,120,20);
        Text_balance.setEditable(false);
        Text_balance.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_balance);

        Label_Count = new JLabel("보유계좌수");
        Label_Count.setBounds(160,180,80,20);
        Label_Count.setHorizontalAlignment(JLabel.LEFT);
        Pan_right.add(Label_Count);

        Text_Count = new JTextArea();
        Text_Count.setBounds(160,200,60,20);
        Text_Count.setEditable(false);
        Text_Count.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        Pan_right.add(Text_Count);


        Btn_Delete = new JButton("삭제");
        Btn_Delete.setBounds(20,260,70,20);
        Btn_Delete.addActionListener(this);
        Pan_right.add(Btn_Delete);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(160,260,70,20);
        Btn_Close.addActionListener(this);
        Pan_right.add(Btn_Close);
    }


    //*******************************************************************
    // # 05-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  입금 버튼, 취소 버튼의 동작을 구현
    //                입금, 취소 동작 후 메인 화면으로 변경되도록 구현
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Delete)
        {
            delete((String) List_Customer.getSelectedValue());

            this.setVisible(false);
            MainFrame.display("Main");
        }

        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        try {
            ViewCustomer(((String) List_Customer.getSelectedValue()));

        } catch(RuntimeException o) {
            System.out.print("");
        }
    }
    //*******************************************************************
    // # 05-03
    //*******************************************************************
    // Name : deposit()
    // Type : Method
    // Description :  입금 화면의 데이터를 가지고 있는 CommandDTO를 생성하고,
    //                ManagerMain의 Send 기능을 호출하여 서버에 입금 요청 메시지를 전달 하는 기능.
    //*******************************************************************

    public void GetCustomerList()
    {
        MainFrame.send(new CommandDTO(RequestType.LIST_CUTOMER), new CompletionHandler<Integer, ByteBuffer>() {
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
                    SwingUtilities.invokeLater(() -> {
                        DefaultListModel model = new DefaultListModel();

                        for (String id : command.getIdList()) {
                            model.addElement(id);
                        }
                        List_Customer.setModel(model);
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
    public void delete(String newid) {
        if (newid == null) {
            JOptionPane.showMessageDialog(null, "선택된 항목이 없습니다.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
            return;
        }
        CommandDTO commandDTO = new CommandDTO(0, RequestType.DEL_CUSTOMER, newid);
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

                        contentText = command.getName() +"님의 정보가 삭제되었습니다.";
                        JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);

                    });
                } catch (IOException e)
                {
                    e.printStackTrace();
                } catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment)
            {
            }
        });

    }

    public void ViewCustomer(String newid) {
        if (newid == null) {
            return;
        }
        MainFrame.send(new CommandDTO(0, RequestType.INFO_CUSTOMER, newid), new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public synchronized void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                     ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    SwingUtilities.invokeLater(() -> {
                        Text_Name.setText(command.getName() != null ? command.getName() : "이름 없음");
                        Text_Id.setText(command.getnewId() != null ? command.getnewId() : "ID 없음");
                        Text_Password.setText(command.getPassword() != null ? command.getPassword() : "****");
                        Text_balance.setText(String.valueOf(command.getBalance()));
                        Text_Count.setText(String.valueOf(command.getUserAccountList() != null ? command.getUserAccountList().length : 0));
                    });
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

}
