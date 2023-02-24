package Chatting;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private JLabel labelUsername;
	private JLabel labelChatUsername;
	private JTextArea ChatArea;
	private JPanel userList;
	private DefaultListModel<String> roomListModel;
	private JList<String> roomList;
	
	private Gson gson;
	private Socket socket;
	

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
		
		JButton JoinButton = new JButton(updateImage("./ImageFile/login1.png", 250, 70));
		JoinButton.setBorderPainted(false);//button에 외곽선을 지워줌
		JoinButton.setContentAreaFilled(false);//button안에 내용 채우지 않기
		JoinButton.setFocusPainted(false);//선택 되었을 때 테두리 비활성화
		JoinButton.setRolloverIcon(updateImage("./ImageFile/login2.png", 250, 70));
		
		JoinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				try {
					String ip = "127.0.0.1";
					int port = 9090;
					socket = new Socket(ip, port);
					ClientRecive clientRecive = new ClientRecive(socket);
					clientRecive.start();
					username = userNameField.getText();
					if (!userNameField.getText().isBlank()) {
						
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
			}

		});
		JoinButton.setBounds(100, 374, 250, 70);

		JoinPanel.add(JoinButton);
		
		JLabel JoinBackground = new JLabel(updateImage("./ImageFile/Instargram.jpg", 480, 800));
		JoinBackground.setBounds(0, 0, 464, 761);
		JoinPanel.add(JoinBackground);

		JPanel RoomPanel = new JPanel();
		MainPanel.add(RoomPanel, "RoomPanel");
		RoomPanel.setLayout(null);

		JScrollPane ChattingRoomScroll = new JScrollPane();
		ChattingRoomScroll.setBounds(0, 149, 464, 426);
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

		
		JButton CrateRoomButton = new JButton(updateImage("./ImageFile/add-group.png", 50, 50));
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
		CrateRoomButton.setBounds(12, 606, 50, 50);
		RoomPanel.add(CrateRoomButton);
		
		labelUsername = new JLabel("username");
		labelUsername.setBounds(54, 56, 97, 50);
		RoomPanel.add(labelUsername);
		
		JLabel roomImage = new JLabel(updateImage("./ImageFile/Instagram_Frame.png", 500, 820));
		roomImage.setBounds(0, 0, 464, 761);
		RoomPanel.add(roomImage);
		
		
		ImageIcon userImagfile = new ImageIcon("./ImageFile/user2.png");
		JLabel userImage = new JLabel(userImagfile);
		userImage.setBounds(4, 56, 50, 50);
		RoomPanel.add(userImage);
		

		JPanel ChattingPanel = new JPanel();
		MainPanel.add(ChattingPanel, "ChattingPanel");
		ChattingPanel.setLayout(null);

		JScrollPane ChattingScroll = new JScrollPane();
		ChattingScroll.setBounds(0, 116, 464, 459);
		ChattingPanel.add(ChattingScroll);

		ChatArea = new JTextArea();
		ChattingScroll.setViewportView(ChatArea);
		ChatArea.setEditable(false);
		
		

		chattingRoomName = new JLabel("");
		chattingRoomName.setBounds(202, 38, 166, 68);
		ChattingPanel.add(chattingRoomName);

		
		JButton RoomOutButton = new JButton();
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
		RoomOutButton.setBounds(422, 10, 30, 30);
		ChattingPanel.add(RoomOutButton);
		
		JButton SendButton = new JButton(updateImage("./ImageFile/envelope.png", 66, 60));
		SendButton.setBorderPainted(false);
		SendButton.setContentAreaFilled(false);
		SendButton.setFocusPainted(false);
		SendButton.setRolloverIcon(updateImage("./ImageFile/paper-plane.png", 66, 60));
		
		SendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ChatArea.setCaretPosition(ChatArea.getDocument().getLength());
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
					ChatArea.setCaretPosition(ChatArea.getDocument().getLength());
					sendMenssage();
					
				}
			}
		});
		MessageScroll.setViewportView(messageInput);
		messageInput.setColumns(10);
		
		labelChatUsername = new JLabel("");
		labelChatUsername.setBounds(54, 56, 108, 50);
		ChattingPanel.add(labelChatUsername);
		
		JLabel chatRoomImage = new JLabel(updateImage("./ImageFile/Instagram_Frame.png", 500, 820));
		chatRoomImage.setBounds(0, 0, 464, 761);
		ChattingPanel.add(chatRoomImage);
		
		
		ImageIcon chatuserImagfile = new ImageIcon("./ImageFile/user2.png");
		JLabel chatUserimage = new JLabel(chatuserImagfile);
		chatUserimage.setBounds(4, 56, 50, 50);
		ChattingPanel.add(chatUserimage);
		
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	if (createRoom != null) {
					RequestDto<?> reqAllLeaveDto = RequestDto.<String>builder().resource("AllLeave").username(username)
							.body(createRoom).build();
					sendRequest(reqAllLeaveDto);

					RequestDto<?> reqRemoveRoom = RequestDto.<String>builder().resource("removeRoom").body(createRoom)
							.build();
					sendRequest(reqRemoveRoom);
				}
                System.out.println("클라종료");
            }
        });


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
	
	private ImageIcon updateImage(String file , int width, int hight) {
		ImageIcon image = new ImageIcon(file);
		Image img = image.getImage();
		Image updateImg = img.getScaledInstance(width, hight, Image.SCALE_SMOOTH);
		ImageIcon updateIcon = new ImageIcon(updateImg);
		return updateIcon;
	}
}
