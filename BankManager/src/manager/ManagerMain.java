package manager;

import common.CommandDTO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ManagerMain
        extends JFrame
        implements ActionListener, BankServiceHandler {

    private JLabel labelTitle;
    private JButton btnAddCustomer;
    private JButton btnAddAccount;
    private JButton btnLogin;
    private JButton btnViewAllCustomer;
    private JButton btnViewAllAccount;
    private JButton btnExit;


    PanAddCustomer panAddCustomer;
    PanAddAccount panAddAccount;
    PanViewAllCustomer panViewAllCustomer;
    PanViewAllAccount panViewAllAccount;
    PanLogin panLogin;


    public static String userId;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousSocketChannel channel;


    
    // ManagerMain Class의 생성자, 통신 시작, gui 초기화
    
    public ManagerMain() {
        startClient();
        initGui();
        setVisible(true);
    }

    //initGui(), gui 초기화
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private void initGui() {
        setTitle("Manager GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setMinimumSize(new Dimension(800, 600));

        // 메인 패널 설정
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // 상단 타이틀
        labelTitle = new JLabel("CNU Bank Manager", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 36));
        labelTitle.setForeground(new Color(0, 102, 204));
        mainPanel.add(labelTitle, BorderLayout.NORTH);

        // 패널
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 버튼 간격

        btnAddCustomer = createButton("<html>신규 고객 등록</html>", "새로운 고객을 등록합니다.");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(btnAddCustomer, gbc);

        btnAddAccount = createButton("<html>신규 계좌 생성</html>", "새로운 계좌를 생성합니다.");
        gbc.gridy = 1;
        buttonPanel.add(btnAddAccount, gbc);

        btnLogin = createButton("<html>관리자 로그인</html>", "관리자 계정으로 로그인합니다.");
        gbc.gridy = 2;
        buttonPanel.add(btnLogin, gbc);

        btnViewAllCustomer = createButton("<html>고객 정보<br>조회 및 삭제</html>", "모든 고객 정보를 조회하거나 삭제합니다.");
        gbc.gridy = 3;
        buttonPanel.add(btnViewAllCustomer, gbc);

        btnViewAllAccount = createButton("<html>계좌 정보<br>조회 및 삭제</html>", "모든 계좌 정보를 조회하거나 삭제합니다.");
        gbc.gridy = 4;
        buttonPanel.add(btnViewAllAccount, gbc);

        btnExit = createButton("<html>종료</html>", "프로그램을 종료합니다.");
        gbc.gridy = 5;
        buttonPanel.add(btnExit, gbc);

        mainPanel.add(buttonPanel, BorderLayout.WEST);

        // 중앙 패널(CardLayout) 생성 및 설정
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // 각 화면 패널 추가
        panAddCustomer = new PanAddCustomer(this);
        panAddAccount = new PanAddAccount(this);
        panViewAllCustomer = new PanViewAllCustomer(this);
        panViewAllAccount = new PanViewAllAccount(this);
        panLogin = new PanLogin(this);

        cardPanel.add(panAddCustomer, "addCustomer");
        cardPanel.add(panAddAccount, "addAccount");
        cardPanel.add(panViewAllCustomer, "ViewAllCustomer");
        cardPanel.add(panViewAllAccount, "ViewAllAccount");
        cardPanel.add(panLogin, "Login");

        cardLayout.show(cardPanel, "Login");

    }


    //*******************************************************************
    // Name : actionPerformed
    // Type : Listener
    // Description :  ManagerMain Frame의 버튼 컴포넌트들의 동작을 구현한 부분
    //                아래 코드에서는 각 기능별 화면으로 전환하는 코드가 작성되어있다.
    //*******************************************************************
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnAddCustomer) {
            display("addCustomer");
        } else if (source == btnAddAccount) {
            display("addAccount");
        } else if (source == btnLogin) {
            display("Login");
        } else if (source == btnViewAllCustomer) {
            display("ViewAllCustomer");
            panViewAllCustomer.GetCustomerList();
        } else if (source == btnViewAllAccount) {
            display("ViewAllAccount");
            panViewAllAccount.GetAccountList();
        } else if (source == btnExit) {
            dispose();
        }
    }

    //*******************************************************************
    // Name : display(), setFrameUI()
    // Type : Method
    // Description :  각 화면별 패널의 Visible 여부를 바꾸어주어 화면 전환의 효과를 준다.
    //*******************************************************************
    public void display(String viewName) {

        if (userId == null) {
            if (!viewName.equals("Login") && !viewName.equals("Main")) {
                JOptionPane.showMessageDialog(this, "로그인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 모든 패널을 숨김
        panAddCustomer.setVisible(false);
        panAddAccount.setVisible(false);
        panViewAllCustomer.setVisible(false);
        panViewAllAccount.setVisible(false);
        panLogin.setVisible(false);
        // 메인 UI 토글
        if (viewName.equals("Main")) {
            getContentPane().getComponent(0).setVisible(true);
            return;
        }

        // 특정 패널만 보이게 설정
        switch (viewName) {
            case "addCustomer":
                panAddCustomer.setVisible(true);
                break;
            case "addAccount":
                panAddAccount.setVisible(true);
                break;
            case "ViewAllCustomer":
                panViewAllCustomer.setVisible(true);
                break;
            case "ViewAllAccount":
                panViewAllAccount.setVisible(true);
                break;
            case "Login":
                panLogin.setVisible(true);
                break;
            default:
                getContentPane().getComponent(0).setVisible(true);
        }
    }

    //*******************************************************************
    // Name : startClient()
    // Type : Method
    // Description :  ManagerMain Class 가 가지고있는 소켓을 서버소켓에 접속시킨다
    //*******************************************************************
    private void startClient() {
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory());
            channel = AsynchronousSocketChannel.open(channelGroup);
            channel.connect(new InetSocketAddress("localhost", 5002), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    System.out.println("Bank Server Connected");
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    disconnectServer();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*******************************************************************
    // Name : stopClient(), disconnectServer()
    // Type : Method
    // Description :  ManagerMain Class 가 가지고있는 소켓의 연결을 해제한다.
    //*******************************************************************
    private void stopClient() {
        try {
            if (channelGroup != null && !channelGroup.isShutdown()) {
                channelGroup.shutdownNow();
                channelGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); // 기다리도록 추가
            }
            System.out.println("연결 종료");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void disconnectServer() {
        stopClient();
    }

    //*******************************************************************
    // Name : send()
    // Type : Method
    // Description :  CommandDTO를 매개변수로 하여 서버에 요청 메시지를 전달하는 메소드
    //                CommandDTO Class 에는 ATM 서비스 요청에 필요한 데이터들이 정의 되어 있다.
    //                ManagerMain Class는 BankServiceHandler 인터페이스를 상속하였다.
    //*******************************************************************
    @Override
    public void send(CommandDTO commandDTO, CompletionHandler<Integer, ByteBuffer> handlers) {
        commandDTO.setId(userId);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(commandDTO);
            objectOutputStream.flush();
            channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()), null, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                    channel.read(byteBuffer, byteBuffer, handlers);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    disconnectServer();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*******************************************************************
    // Name : createButton()
    // Type : Method
    // Description :  공통 버튼 생성 메소드로, 스타일과 툴팁을 설정한다.
    //*******************************************************************
    private JButton createButton(String text, String toolTip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(51, 153, 255));
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(200, 50));
        button.setToolTipText(toolTip);
        button.addActionListener(this);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ManagerMain();
        });
    }
}