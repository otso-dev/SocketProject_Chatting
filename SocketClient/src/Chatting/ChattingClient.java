package Chatting;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import Chatting.Dto.RequestDto;
import Chatting.controller.ClientRecive;
import Chatting.controller.Controller;
import lombok.Data;


@Data
public class ChattingClient extends JFrame {

	private JPanel MainPanel;
	private CardLayout mainCard;
	private JTextField userNameField;
	private JTextField textField;

	private Gson gson;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BufferedReader reader;
	private PrintWriter writer;
	private String roomname;
	private DefaultListModel<String> roomListModel;
	private JList<String> roomList;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Controller.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ChattingClient() {
		this.setVisible(true);
		gson = new Gson();

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
		JoinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				try {
					String username = userNameField.getText();
					if(username.isBlank()) {
						JOptionPane.showMessageDialog(null, "사용자이름이 공백일 수 없습니다.","error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					String ip = "127.0.0.1";
					int port = 9090;
					socket = new Socket(ip, port);
					
					outputStream = socket.getOutputStream();
					writer = new PrintWriter(outputStream, true);
					RequestDto<?> requestDto = new RequestDto<String>("join", username);
					writer.println(gson.toJson(requestDto));
					writer.flush();

					ClientRecive clientRecive = new ClientRecive(socket);
					clientRecive.start();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				mainCard.show(MainPanel, "RoomPanel");
			}

		});
		JoinButton.setBounds(149, 378, 97, 23);
		JoinPanel.add(JoinButton);

		JPanel RoomPanel = new JPanel();
		MainPanel.add(RoomPanel, "RoomPanel");
		RoomPanel.setLayout(null);

		JScrollPane ChattingRoomScroll = new JScrollPane();
		ChattingRoomScroll.setBounds(96, 0, 354, 751);
		RoomPanel.add(ChattingRoomScroll);

		roomListModel = new DefaultListModel<>();
		roomList = new JList<String>(roomListModel);
		ChattingRoomScroll.setViewportView(roomList);

		JButton CrateRoomButton = new JButton("방 생성");
		CrateRoomButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				roomname = JOptionPane.showInputDialog(null, "방 제목을 입력하세요","방 생성",JOptionPane.INFORMATION_MESSAGE);
				RequestDto<?> requestDto = new RequestDto<String>("createRoom",roomname);
				writer.println(gson.toJson(requestDto));
				//System.out.println(requestDto+"ClientReq");
				writer.flush();
			}
		});
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
