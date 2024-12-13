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
// Description : ��� ���� ������ ��ȸ�ϰ� ������ �� �ִ� �г��� ������ Ŭ�����Դϴ�.
//*******************************************************************
public class PanViewAllAccount extends JPanel implements ActionListener, ListSelectionListener
{
    private JLabel labelAccount;
    private JList<String> listAccount; // ���� ���
    private JButton btnDelete;
    private JScrollPane scrollPane;
    private JPanel panLeft;

    private ManagerMain mainFrame;

    //*******************************************************************
    // Name : PanViewAllAccount()
    // Type : Constructor
    // Description : PanViewAllAccount Ŭ������ ������ ����
    //*******************************************************************
    public PanViewAllAccount(ManagerMain parent)
    {
        this.mainFrame = parent;
        initGUI();
    }

    //*******************************************************************
    // Name : initGUI
    // Type : Method
    // Description : ��� ���� ��ȸ �г��� GUI�� �ʱ�ȭ�ϴ� �޼ҵ��Դϴ�.
    //*******************************************************************
    private void initGUI()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ���� �г� ����
        panLeft = new JPanel(new BorderLayout(10, 10));
        panLeft.setPreferredSize(new Dimension(300, 400));
        panLeft.setBorder(BorderFactory.createTitledBorder("���� ���"));

        labelAccount = new JLabel("���� ���");
        labelAccount.setFont(new Font("Malgun Gothic", Font.BOLD, 16)); // Unicode ���� ��Ʈ ���
        labelAccount.setHorizontalAlignment(JLabel.CENTER);
        panLeft.add(labelAccount, BorderLayout.NORTH);

        listAccount = new JList<>();
        listAccount.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAccount.setVisibleRowCount(10);
        listAccount.addListSelectionListener(this);
        listAccount.setFont(new Font("Malgun Gothic", Font.PLAIN, 14)); // Unicode ���� ��Ʈ ���
        scrollPane = new JScrollPane(listAccount);
        panLeft.add(scrollPane, BorderLayout.CENTER);

        // ��ư �г� ����
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnDelete = new JButton("����");
        btnDelete.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFocusPainted(false);
        btnDelete.setPreferredSize(new Dimension(70, 25));
        btnDelete.addActionListener(this);
        btnDelete.setToolTipText("������ ���¸� �����մϴ�.");


        buttonPanel.add(btnDelete);

        panLeft.add(buttonPanel, BorderLayout.SOUTH);

        add(panLeft, BorderLayout.CENTER);
    }

    //*******************************************************************
    // Name : actionPerformed
    // Type : Listener
    // Description : ���� ��ư�� �ݱ� ��ư�� ������ �����մϴ�.
    //*******************************************************************
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDelete)
        {
            String selectedValue = listAccount.getSelectedValue();
            String accNo = (selectedValue == null) ? null : selectedValue.replaceAll("[^0-9]", "");
            delete(accNo);
            // ���� �� ���� ȭ������ ��ȯ
            this.setVisible(false);
            mainFrame.display("Main");
        }
    }

    //*******************************************************************
    // Name : valueChanged
    // Type : Listener
    // Description : ���� ��Ͽ��� �׸��� ���õ� �� �ش� ������ �� ������ ǥ���մϴ�.
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
    // Description : ��� ���� ����� �����κ��� ��û�ϰ�, ����� ������Ʈ�մϴ�.
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
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "���� ����� �ҷ����� ���� ������ �߻��߽��ϴ�.", "����", JOptionPane.ERROR_MESSAGE));
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "���� ����� �ҷ����� �� �����߽��ϴ�.", "����", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    //*******************************************************************
    // Name : ViewAccount
    // Type : Method
    // Description : ���õ� ������ �� ������ �����κ��� ��û�ϰ�, UI�� ǥ���մϴ�.
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
                        // �� ���� ǥ��
                        String name = command.getName();
                        String account = command.getUserAccountNo();
                        AccountType type = command.getAccountType();
                        long bal = command.getBalance();
                        Date date = command.getAccountDate();

                        String accountInfo = "�̸�: " + (name != null ? name : "���� ����") + "\n" +
                                "���¹�ȣ: " + (account != null ? account : "���� ����") + "\n" +
                                "��������: " + (type != null ? (type == AccountType.CHECKING ? "���¿��ݰ���" : "���࿹�ݰ���") : "���� ����") + "\n" +
                                "�ܾ�: " + BankUtils.displayBalance(bal) + "��\n" +
                                "���»�����: " + (date != null ? date.toString() : "���� ����");

                        JOptionPane.showMessageDialog(PanViewAllAccount.this, accountInfo, "���� ����", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (IOException | ClassNotFoundException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "���� ������ �ҷ����� ���� ������ �߻��߽��ϴ�.", "����", JOptionPane.ERROR_MESSAGE));
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "���� ������ �ҷ����� �� �����߽��ϴ�.", "����", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    //*******************************************************************
    // Name : delete
    // Type : Method
    // Description : ���õ� ���¸� ���� ��û�ϰ�, ����� ����ڿ��� �˸��ϴ�.
    //*******************************************************************
    private void delete(String accNo) {
        if (accNo == null) {
            JOptionPane.showMessageDialog(this, "���õ� �׸��� �����ϴ�.", "����", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "������ ���¸� �����Ͻðڽ��ϱ�?", "Ȯ��", JOptionPane.YES_NO_OPTION);
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
                        String contentText = command.getName() + "���� " + BankUtils.displayAccountNo(accNo) + " ���� ������ �����Ǿ����ϴ�.";
                        JOptionPane.showMessageDialog(PanViewAllAccount.this, contentText, "����", JOptionPane.INFORMATION_MESSAGE);
                        GetAccountList(); // ��� ����
                    });
                } catch (IOException | ClassNotFoundException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "���� ���� ���� ������ �߻��߽��ϴ�.", "����", JOptionPane.ERROR_MESSAGE));
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PanViewAllAccount.this, "���� ������ �����߽��ϴ�.", "����", JOptionPane.ERROR_MESSAGE));
            }
        });
    }
}
