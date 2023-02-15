package Chatting;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextArea;

public class ChattingClient extends JFrame {

	private JPanel MainPanel;
	private CardLayout mainCard;
	private Socket socket;
	private JTextField userNameField;
	private JTextField textField;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChattingClient frame = new ChattingClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChattingClient() {
		
		try {
			socket = new Socket("127.0.0.1",9090);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		MainPanel = new JPanel();
		MainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(MainPanel);
		mainCard = new CardLayout();
		MainPanel.setLayout(mainCard);
		
		JPanel JoinPanel = new JPanel();
		MainPanel.add(JoinPanel, "JoinPanel");
		JoinPanel.setLayout(null);
		
		userNameField = new JTextField();
		userNameField.setBounds(138, 333, 116, 21);
		JoinPanel.add(userNameField);
		userNameField.setColumns(10);
		
		JButton JoinButton = new JButton("접속");
		JoinButton.setBounds(149, 378, 97, 23);
		JoinPanel.add(JoinButton);
		
		JPanel RoomPanel = new JPanel();
		MainPanel.add(RoomPanel, "RoomPanel");
		RoomPanel.setLayout(null);
		
		JScrollPane ChattingRoomScroll = new JScrollPane();
		ChattingRoomScroll.setBounds(96, 0, 354, 751);
		RoomPanel.add(ChattingRoomScroll);
		
		JList list = new JList();
		ChattingRoomScroll.setViewportView(list);
		
		JButton CrateRoomButton = new JButton("방 생성");
		CrateRoomButton.setBounds(0, 0, 97, 751);
		RoomPanel.add(CrateRoomButton);
		
		JPanel ChattingPanel = new JPanel();
		MainPanel.add(ChattingPanel, "ChattingPanel");
		ChattingPanel.setLayout(null);
		
		JScrollPane ChattingScroll = new JScrollPane();
		ChattingScroll.setBounds(0, 50, 450, 640);
		ChattingPanel.add(ChattingScroll);
		
		JTextArea ChatArea = new JTextArea();
		ChattingScroll.setViewportView(ChatArea);
		
		JButton RoomOutButton = new JButton("방 나가기");
		RoomOutButton.setBounds(345, 17, 97, 23);
		ChattingPanel.add(RoomOutButton);
		
		JButton SendButton = new JButton("전송");
		SendButton.setBounds(388, 692, 66, 59);
		ChattingPanel.add(SendButton);
		
		JScrollPane MessageScroll = new JScrollPane();
		MessageScroll.setBounds(0, 692, 390, 59);
		ChattingPanel.add(MessageScroll);
		
		textField = new JTextField();
		MessageScroll.setViewportView(textField);
		textField.setColumns(10);
		
		
	}
}
