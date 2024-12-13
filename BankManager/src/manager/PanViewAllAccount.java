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

//*******************************************************************
// Name : PanViewAllAccount
// Type : Class
// Description : 모든 계좌 정보를 조회하고 삭제할 수 있는 패널을 구현한 클래스입니다.
//*******************************************************************
public class PanViewAllAccount extends JPanel implements ActionListener, ListSelectionListener
{
    private JLabel labelAccount;
    private JList<String> listAccount; // 계좌 목록
    private JButton btnDelete;
    private JScrollPane scrollPane;
    private JPanel panLeft;

    private ManagerMain mainFrame;

    //*******************************************************************
    // Name : PanViewAllAccount()
    // Type : Constructor
    // Description : PanViewAllAccount 클래스의 생성자 구현
    //*******************************************************************
    public PanViewAllAccount(ManagerMain parent)
    {
        this.mainFrame = parent;
        initGUI();
    }

    //*******************************************************************
    // Name : initGUI
    // Type : Method
    // Description : 모든 계좌 조회 패널의 GUI를 초기화하는 메소드입니다.
    //*******************************************************************
    private void initGUI()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 좌측 패널 설정
        panLeft = new JPanel(new BorderLayout(10, 10));
        panLeft.setPreferredSize(new Dimension(300, 400));
        panLeft.setBorder(BorderFactory.createTitledBorder("계좌 목록"));

        labelAccount = new JLabel("계좌 목록");
        labelAccount.setFont(new Font("Malgun Gothic", Font.BOLD, 16)); // Unicode 지원 폰트 사용
        labelAccount.setHorizontalAlignment(JLabel.CENTER);
        panLeft.add(labelAccount, BorderLayout.NORTH);

        listAccount = new JList<>();
        listAccount.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAccount.setVisibleRowCount(10);
        listAccount.addListSelectionListener(this);
        listAccount.setFont(new Font("Malgun Gothic", Font.PLAIN, 14)); // Unicode 지원 폰트 사용
        scrollPane = new JScrollPane(listAccount);
        panLeft.add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnDelete = new JButton("삭제");
        btnDelete.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFocusPainted(false);
        btnDelete.setPreferredSize(new Dimension(70, 25));
        btnDelete.addActionListener(this);
        btnDelete.setToolTipText("선택한 계좌를 삭제합니다.");


        buttonPanel.add(btnDelete);

        panLeft.add(buttonPanel, BorderLayout.SOUTH);

        add(panLeft, BorderLayout.CENTER);
    }

    //*******************************************************************
    // Name : actionPerformed
    // Type : Listener
    // Description : 삭제 버튼과 닫기 버튼의 동작을 구현합니다.
    //*******************************************************************
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDelete)
        {
            String selectedValue = listAccount.getSelectedValue();
            String accNo = (selectedValue == null) ? null : selectedValue.replaceAll("[^0-9]", "");
            delete(accNo);
            // 삭제 후 메인 화면으로 전환
            this.setVisible(false);
            mainFrame.display("Main");
        }
    }

    //*******************************************************************
    // Name : valueChanged
    // Type : Listener
    // Description : 계좌 목록에서 항목이 선택될 때 해당 계좌의 상세 정보를 표시합니다.
    //*******************************************************************
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedValue = listAccount.getSelectedValue();
            if (selectedValue != null) {
                String accNo = selectedValue.replaceAll("[^0-9]", "");
                viewAccount(accNo);
            }
        }
    }

    //*******************************************************************
    // Name : GetAccountList
    // Type : Method
    // Description : 모든 계좌 목록을 서버로부터 요청하고, 목록을 업데이트합니다.
    //*******************************************************************
    public void GetAccountList()
    {
        mainFrame.send(new CommandDTO(RequestType.LIST_ACCOUNT), new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try (ByteArrayInputStream bais = new ByteArrayInputStream(attachment.array());
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    CommandDTO command = (CommandDTO) ois.readObject();
                    SwingUtilities.invokeLater(() -> {
                        DefaultListModel<String> model = new DefaultListModel<>();

                        if (command.getUserAccountList() != null) {
                            for (String account : command.getUserAccountList()) {
                                model.addElement(BankUtils.displayAccountNo(account));
                            }
                        }
                        listAccount.setModel(model);
                    });
                } catch (IOException | ClassNotFoundException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "계좌 목록을 불러오는 도중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "계좌 목록을 불러오는 데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    //*******************************************************************
    // Name : ViewAccount
    // Type : Method
    // Description : 선택된 계좌의 상세 정보를 서버로부터 요청하고, UI에 표시합니다.
    //*******************************************************************
    private void viewAccount(String accNo) {
        if (accNo == null) {
            return;
        }
        mainFrame.send(new CommandDTO(RequestType.INFO_ACCOUNT, accNo), new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try (ByteArrayInputStream bais = new ByteArrayInputStream(attachment.array());
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    CommandDTO command = (CommandDTO) ois.readObject();
                    SwingUtilities.invokeLater(() -> {
                        // 상세 정보 표시
                        String name = command.getName();
                        String account = command.getUserAccountNo();
                        AccountType type = command.getAccountType();
                        long bal = command.getBalance();
                        Date date = command.getAccountDate();

                        String accountInfo = "이름: " + (name != null ? name : "정보 없음") + "\n" +
                                "계좌번호: " + (account != null ? account : "정보 없음") + "\n" +
                                "계좌유형: " + (type != null ? (type == AccountType.CHECKING ? "당좌예금계좌" : "저축예금계좌") : "정보 없음") + "\n" +
                                "잔액: " + BankUtils.displayBalance(bal) + "원\n" +
                                "계좌생성일: " + (date != null ? date.toString() : "정보 없음");

                        JOptionPane.showMessageDialog(PanViewAllAccount.this, accountInfo, "계좌 정보", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (IOException | ClassNotFoundException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "계좌 정보를 불러오는 도중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "계좌 정보를 불러오는 데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    //*******************************************************************
    // Name : delete
    // Type : Method
    // Description : 선택된 계좌를 삭제 요청하고, 결과를 사용자에게 알립니다.
    //*******************************************************************
    private void delete(String accNo) {
        if (accNo == null) {
            JOptionPane.showMessageDialog(this, "선택된 항목이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "정말로 계좌를 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        CommandDTO commandDTO = new CommandDTO(RequestType.DEL_ACCOUNT, accNo);
        mainFrame.send(commandDTO, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try (ByteArrayInputStream bais = new ByteArrayInputStream(attachment.array());
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    CommandDTO command = (CommandDTO) ois.readObject();
                    SwingUtilities.invokeLater(() -> {
                        String contentText = command.getName() + "님의 " + BankUtils.displayAccountNo(accNo) + " 계좌 정보가 삭제되었습니다.";
                        JOptionPane.showMessageDialog(PanViewAllAccount.this, contentText, "성공", JOptionPane.INFORMATION_MESSAGE);
                        GetAccountList(); // 목록 갱신
                    });
                } catch (IOException | ClassNotFoundException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "계좌 삭제 도중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "계좌 삭제에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
            }
        });
    }
}
