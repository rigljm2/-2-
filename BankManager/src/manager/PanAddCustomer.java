package manager;

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
// # 03
//*******************************************************************
// Name : PanViewAccount
// Type : Class
// Description :  계좌조회 화면 패널을 구현한 Class 이다.
//*******************************************************************
public class PanAddCustomer extends JPanel implements ActionListener
{
    private JLabel Label_Name;
    private  JTextArea Text_Name;
    private JLabel Label_Id;
    private  JTextArea Text_Id;
    private JLabel Label_Password;
    private  JTextArea Text_Password;
    private JLabel Label_Password2;
    private  JTextArea Text_Password2;

    private JButton Btn_Register;
    private JButton Btn_Close;

    ManagerMain MainFrame;
    
    //*******************************************************************
    // # 03-01
    //*******************************************************************
    // Name : PanViewAccount()
    // Type : 생성자
    // Description :  PanViewAccount Class의 생성자 구현
    //*******************************************************************
    public PanAddCustomer(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // # 03-02
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  계좌조회 화면 패널의 GUI를 초기화 하는 메소드 구현
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_Name = new JLabel("이름");
        Label_Name.setBounds(0,30,100,20);
        Label_Name.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Name);

        Text_Name = new JTextArea();
        Text_Name.setBounds(100,30,350,20);
        Text_Name.setEditable(true);
        add(Text_Name);

        Label_Id = new JLabel("아이디");
        Label_Id.setBounds(0,80,100,20);
        Label_Id.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Id);

        Text_Id = new JTextArea();
        Text_Id.setBounds(100,80,350,20);
        Text_Id.setEditable(true);
        add(Text_Id);

        Label_Password = new JLabel("비밀번호");
        Label_Password.setBounds(0,130,100,20);
        Label_Password.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Password);

        Text_Password = new JTextArea();
        Text_Password.setBounds(100,130,350,20);
        Text_Password.setEditable(true);
        add(Text_Password);

        Label_Password2 = new JLabel("비밀번호 재확인");
        Label_Password2.setBounds(0,180,100,20);
        Label_Password2.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Password2);

        Text_Password2 = new JTextArea();
        Text_Password2.setBounds(100,180,350,20);
        Text_Password2.setEditable(true);
        add(Text_Password2);

        Btn_Register = new JButton("등록");
        Btn_Register.setBounds(100,250,70,20);
        Btn_Register.addActionListener(this);
        add(Btn_Register);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    //*******************************************************************
    // # 03-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  취소 버튼의 동작을 구현
    //                취소 동작 후 메인 화면으로 변경되도록 구현
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Register) {
            Register_Cus();
            this.setVisible(false);
            MainFrame.display("Main");
        }
    } 
    
    //*******************************************************************
    // # 03-03
    //*******************************************************************
    // Name : GetBalance()
    // Type : Method
    // Description :  ManagerMain의 Send 기능을 호출하여 서버에 계좌조회 요청 메시지를 전달 하는 기능.
    //*******************************************************************
    public void Register_Cus()
    {
        String name = Text_Name.getText();
        String newid = Text_Id.getText();
        String password = Text_Password.getText();
        String password2 = Text_Password2.getText();
        MainFrame.send(new CommandDTO(RequestType.REGISTER_CUSTOMER, name, newid, password, password2), new CompletionHandler<Integer, ByteBuffer>() {
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
                        String contentText = null;

                        if(command.getResponseType() == ResponseType.WRONG_ID) {
                            contentText = "사용할 수 없는 아이디 입니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if(command.getResponseType() == ResponseType.WRONG_PASSWORD){
                            contentText = "비밀번호를 확인해주세요.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if(command.getResponseType() == ResponseType.SUCCESS) {
                            contentText = "새로운 고객 정보가 등록되었습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
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
