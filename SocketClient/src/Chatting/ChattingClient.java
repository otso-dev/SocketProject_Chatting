package Chatting;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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

	private String createRoom;
	private String username;
	private String enterRoomname;
	private boolean joinflag;
	private String Title = "InstargramDemo";

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
		
		ImageIcon icon = new ImageIcon("./ImageFile/icon_instagram.png");
		Image iconImg = icon.getImage();
		Image updateIconDm = iconImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		this.setIconImage(updateIconDm);
		setTitle(Title);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		MainPanel = new JPanel();
		MainPanel.setBorder(null);

		setContentPane(MainPanel);
		mainCard = new CardLayout();
		MainPanel.setLayout(mainCard);
		
		
		
	
		JPanel JoinPanel = new JPanel();
		MainPanel.add(JoinPanel, "JoinPanel");
		JoinPanel.setLayout(null);
		JoinPanel.setBounds(100, 100, 480, 800);

		userNameField = new JTextField();
		userNameField.setBounds(126, 333, 200, 30);
		JoinPanel.add(userNameField);
		userNameField.setColumns(10);
		
		ImageIcon startImage = new ImageIcon("./ImageFile/login1.png");
		Image img = startImage.getImage();
		Image updateImg = img.getScaledInstance(350, 150, Image.SCALE_SMOOTH);
		ImageIcon updateIcon = new ImageIcon(updateImg);
		
		ImageIcon changeIcon = new ImageIcon("./ImageFile/login2.png");
		Image changeImg = changeIcon.getImage();
		Image updateStartImg2 = changeImg.getScaledInstance(350, 150, Image.SCALE_SMOOTH);
		ImageIcon changeIconStart = new ImageIcon(updateStartImg2);
		
		JButton JoinButton = new JButton(updateIcon);
		JoinButton.setBorderPainted(false);//button에 외곽선을 지워줌
		JoinButton.setContentAreaFilled(false);//button안에 내용 채우지 않기
		JoinButton.setFocusPainted(false);//선택 되었을 때 테두리 비활성화
		JoinButton.setRolloverIcon(changeIconStart);
		
		JoinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				try {
					String ip = "127.0.0.1";
					int port = 9090;
					socket = new Socket(ip, port);
					username = userNameField.getText();
					if (!userNameField.getText().isBlank()) {
						ClientRecive clientRecive = new ClientRecive(socket);
						clientRecive.start();
						
						RequestDto<?> reqJoin = RequestDto.<String>builder().resource("join").body(username).build();
						sendRequest(reqJoin);
					}else {
						JOptionPane.showMessageDialog(null, "사용자이름이 공백일 수 없습니다.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} catch (ConnectException e1) {
					JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.", "error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
				mainCard.show(MainPanel, "RoomPanel");
			}

		});
		JoinButton.setBounds(50, 383, 350, 150);
		JoinPanel.add(JoinButton);
		
		
		ImageIcon joinBackImage = new ImageIcon("./ImageFile/Instargram.jpg");
		Image joinImg = joinBackImage.getImage();
		Image updateJoinImg = joinImg.getScaledInstance(480, 800, Image.SCALE_SMOOTH);
		ImageIcon updateJoinIcon = new ImageIcon(updateJoinImg);
		JLabel JoinBackground = new JLabel(updateJoinIcon);
		JoinBackground.setBounds(0, 0, 464, 761);
		JoinPanel.add(JoinBackground);

		JPanel RoomPanel = new JPanel();
		MainPanel.add(RoomPanel, "RoomPanel");
		RoomPanel.setLayout(null);

		JScrollPane ChattingRoomScroll = new JScrollPane();
		ChattingRoomScroll.setBounds(89, 0, 375, 761);
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

		
		ImageIcon createAddImage = new ImageIcon("./ImageFile/add-group.png");
		Image img2 = createAddImage.getImage();
		Image updateImg2 = img2.getScaledInstance(90,90, Image.SCALE_SMOOTH);
		ImageIcon updateIcon2 = new ImageIcon(updateImg2);
		
		JButton CrateRoomButton = new JButton(updateIcon2);
		CrateRoomButton.setBorderPainted(false);
		CrateRoomButton.setContentAreaFilled(false);
		CrateRoomButton.setFocusPainted(false);
		
		CrateRoomButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					createRoom = JOptionPane.showInputDialog(null, "방 제목을 입력하세요", "방 생성", JOptionPane.INFORMATION_MESSAGE);
					enterRoomname = createRoom;
					if (createRoom.isBlank()) {
						JOptionPane.showMessageDialog(null, "방 제목은 공백일 수 없습니다.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					RequestDto<?> reqCreateRoom = RequestDto.<String>builder().resource("createRoom")
															.username(username)
															.createRoomname(createRoom)
															.body(createRoom)
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
		CrateRoomButton.setBounds(0, 100, 90, 90);
		RoomPanel.add(CrateRoomButton);
		
		ImageIcon RoomImg = new ImageIcon("./ImageFile/Instargram.jpg");
		Image Roomimg = RoomImg.getImage();
		Image updateRoomImg = Roomimg.getScaledInstance(480,800, Image.SCALE_SMOOTH);
		ImageIcon updateRoomIcon = new ImageIcon(updateRoomImg);
		
		JLabel roomImage = new JLabel(updateRoomIcon);
		roomImage.setBounds(0, 0, 464, 761);
		RoomPanel.add(roomImage);

		JPanel ChattingPanel = new JPanel();
		MainPanel.add(ChattingPanel, "ChattingPanel");
		ChattingPanel.setLayout(null);

		JScrollPane ChattingScroll = new JScrollPane();
		ChattingScroll.setBounds(0, 66, 464, 624);
		ChattingPanel.add(ChattingScroll);

		ChatArea = new JTextArea();
		ChattingScroll.setViewportView(ChatArea);
		
		
		

		chattingRoomName = new JLabel("");
		chattingRoomName.setBounds(0, 0, 464, 68);
		ChattingPanel.add(chattingRoomName);

		
		
		ImageIcon roomExitImg = new ImageIcon("./ImageFile/exit.png");
		Image img3 = roomExitImg.getImage();
		Image updateImg3 = img3.getScaledInstance(50,50, Image.SCALE_SMOOTH);
		ImageIcon updateIcon3 = new ImageIcon(updateImg3);
		JButton RoomOutButton = new JButton(updateIcon3);
		RoomOutButton.setBorderPainted(false);
		RoomOutButton.setContentAreaFilled(false);
		RoomOutButton.setFocusPainted(false);
		
		RoomOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (createRoom != null) {
					RequestDto<?> reqAllLeaveDto = RequestDto.<String>builder().resource("AllLeave").username(username)
							.body(createRoom).build();
					sendRequest(reqAllLeaveDto);

					RequestDto<?> reqRemoveRoom = RequestDto.<String>builder().resource("removeRoom").body(createRoom)
							.build();
					sendRequest(reqRemoveRoom);
				} else if (createRoom == null) {
					RequestDto<?> reqLeaveDto = RequestDto.<String>builder().resource("leave").username(username)
							.body(enterRoomname).build();
					sendRequest(reqLeaveDto);
				}

				mainCard.show(MainPanel, "RoomPanel");
			}
		});
		RoomOutButton.setBounds(380, 10, 50, 50);
		ChattingPanel.add(RoomOutButton);
		
		
		ImageIcon sendImg = new ImageIcon("./ImageFile/envelope.png");
		Image img4 = sendImg.getImage();
		Image updateImg4 = img4.getScaledInstance(66,60, Image.SCALE_SMOOTH);
		ImageIcon updateIcon4 = new ImageIcon(updateImg4);
		
		ImageIcon sendChangeImg = new ImageIcon("./ImageFile/paper-plane.png");
		Image Changeimg = sendChangeImg.getImage();
		Image updateChangeImg = Changeimg.getScaledInstance(66,60, Image.SCALE_SMOOTH);
		ImageIcon updateChangeIcon = new ImageIcon(updateChangeImg);
		
		JButton SendButton = new JButton(updateIcon4);
		SendButton.setBorderPainted(false);
		SendButton.setContentAreaFilled(false);
		SendButton.setFocusPainted(false);
		
		
		SendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMenssage();
			}
		});
		SendButton.setBounds(388, 692, 76, 69);
		ChattingPanel.add(SendButton);

		JScrollPane MessageScroll = new JScrollPane();
		MessageScroll.setBounds(0, 692, 390, 69);
		ChattingPanel.add(MessageScroll);

		messageInput = new JTextField();
		messageInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMenssage();
					SendButton.setRolloverIcon(updateChangeIcon);
				}
			}
		});
		MessageScroll.setViewportView(messageInput);
		messageInput.setColumns(10);
		
		ImageIcon chattRoomImg = new ImageIcon("./ImageFile/Instargram.jpg");
		Image chattRoomimg = chattRoomImg.getImage();
		Image updatechattRoomImg = chattRoomimg.getScaledInstance(480,800, Image.SCALE_SMOOTH);
		ImageIcon updatechattRoomIcon = new ImageIcon(updatechattRoomImg);
		
		JLabel chatRoomImage = new JLabel(updatechattRoomIcon);
		chatRoomImage.setBounds(0, 0, 464, 761);
		ChattingPanel.add(chatRoomImage);

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
