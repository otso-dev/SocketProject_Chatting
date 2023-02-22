package Chatting;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import Chatting.Dto.RequestDto;
import Chatting.Dto.ResponseDto;
import Chatting.controller.ClientRecive;
import Chatting.controller.Controller;
import lombok.Data;

@Data
public class ChattingClient extends JFrame {

	private JPanel MainPanel;
	private CardLayout mainCard;
	private JTextField userNameField;
	private JTextField messageInput;
	private JLabel chattingRoomName;
	private JTextArea ChatArea;

	private Gson gson;
	private Socket socket;
	private DefaultListModel<String> roomListModel;
	private JList<String> roomList;

	private String roomname;
	private String username;
	private String enterRoomname;
	private boolean joinflag;

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
					
					if (!userNameField.getText().isBlank()) {
						username = userNameField.getText();
						RequestDto<?> reqJoin = RequestDto.<String>builder().resource("join").body(username).build();
						sendRequest(reqJoin);
						joinflag = true;
						if(joinflag) {
							String ip = "127.0.0.1";
							int port = 9090;
							socket = new Socket(ip, port);
							ClientRecive clientRecive = new ClientRecive(socket);
							clientRecive.start();
						}
					}
					JOptionPane.showMessageDialog(null, "사용자이름이 공백일 수 없습니다.", "error", JOptionPane.ERROR_MESSAGE);
					return;

				} catch (ConnectException e1) {
					JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.", "error", JOptionPane.ERROR_MESSAGE);
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
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {
					String enterRoom = roomList.getSelectedValue();
					// System.out.println(roomList.getSelectedIndex());
					if (enterRoom != null && roomList.getSelectedIndex() != 0) {

						enterRoomname = enterRoom;
						RequestDto<?> reqEnter = RequestDto.<String>builder().resource("enter").username(username)
								.enterRoomname(enterRoom).body(enterRoom).build();
						sendRequest(reqEnter);
						ChatArea.setText("");
						chattingRoomName.setText("제목: " + enterRoom + "의 방");
						mainCard.show(MainPanel, "ChattingPanel");

					}
				}

			}

		});

		JButton CrateRoomButton = new JButton("방 생성");
		CrateRoomButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					roomname = JOptionPane.showInputDialog(null, "방 제목을 입력하세요", "방 생성", JOptionPane.INFORMATION_MESSAGE);
					enterRoomname = roomname;
					if (roomname.isBlank()) {
						JOptionPane.showMessageDialog(null, "방 제목은 공백일 수 없습니다.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					RequestDto<?> reqCreateRoom = RequestDto.<String>builder().resource("createRoom")
															.username(username)
															.createRoomname(roomname)
															.body(roomname)
															.build();
					
					

					sendRequest(reqCreateRoom);
					
				} catch (NullPointerException e2) {
					JOptionPane.showMessageDialog(null, "취소하였습니다.", "exit", JOptionPane.CANCEL_OPTION);
					return;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "취소하였습니다.", "exit", JOptionPane.CANCEL_OPTION);
					return;
				}
			

			}

		});
		CrateRoomButton.setBounds(0, 0, 97, 751);
		RoomPanel.add(CrateRoomButton);

		JPanel ChattingPanel = new JPanel();
		MainPanel.add(ChattingPanel, "ChattingPanel");
		ChattingPanel.setLayout(null);

		JScrollPane ChattingScroll = new JScrollPane();
		ChattingScroll.setBounds(0, 66, 450, 624);
		ChattingPanel.add(ChattingScroll);

		ChatArea = new JTextArea();
		ChattingScroll.setViewportView(ChatArea);

		chattingRoomName = new JLabel("label");
		chattingRoomName.setBounds(0, 0, 450, 68);
		ChattingPanel.add(chattingRoomName);

		JButton RoomOutButton = new JButton("방 나가기");
		RoomOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (roomname != null) {
					RequestDto<?> reqAllLeaveDto = RequestDto.<String>builder().resource("AllLeave").username(username)
							.body(roomname).build();
					sendRequest(reqAllLeaveDto);

					RequestDto<?> reqRemoveRoom = RequestDto.<String>builder().resource("removeRoom").body(roomname)
							.build();
					sendRequest(reqRemoveRoom);
				} else if (roomname == null) {
					RequestDto<?> reqLeaveDto = RequestDto.<String>builder().resource("leave").username(username)
							.body(enterRoomname).build();
					sendRequest(reqLeaveDto);
				}

				mainCard.show(MainPanel, "RoomPanel");
			}
		});
		RoomOutButton.setBounds(345, 23, 97, 23);
		ChattingPanel.add(RoomOutButton);

		JButton SendButton = new JButton("전송");
		SendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMenssage();
			}
		});
		SendButton.setBounds(388, 692, 66, 59);
		ChattingPanel.add(SendButton);

		JScrollPane MessageScroll = new JScrollPane();
		MessageScroll.setBounds(0, 692, 390, 59);
		ChattingPanel.add(MessageScroll);

		messageInput = new JTextField();
		messageInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMenssage();
				}
			}
		});
		MessageScroll.setViewportView(messageInput);
		messageInput.setColumns(10);

	}

	private void sendRequest(RequestDto<?> requestDto) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);

			out.println(gson.toJson(requestDto));
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendMenssage() {
		if (!messageInput.getText().isBlank()) {
			RequestDto<?> messageReqDto = RequestDto.<String>builder().resource("sendMessage").username(username)
					.enterRoomname(enterRoomname).body(messageInput.getText()).build();
			sendRequest(messageReqDto);

		}
		messageInput.setText("");
	}
}
