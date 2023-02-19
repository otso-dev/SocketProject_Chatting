package Chatting;

import java.awt.CardLayout;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
import controller.ClientRecive;
import controller.Controller;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChattingClient extends JFrame {

	private JPanel MainPanel;
	private CardLayout mainCard;
	private Socket socket;	
	private JTextField name;
	private JTextField textField;
	private Gson gson;
	private DefaultListModel<String> roomModel;
	private JList<String> roomList;
	
	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the frame.
	 */
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
		
		name = new JTextField();
		name.setBounds(138, 333, 116, 21);
		JoinPanel.add(name);
		name.setColumns(10);
		
		JButton JoinButton = new JButton("접속");
		JoinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String ip = "127.0.0.1";
				int port = 9090;
				try {
					String userId = name.getText();
					
					socket = new Socket(ip,port);
					
					RequestDto<?> requestDto = new RequestDto<String>("join", null, null, userId );
					sendRequest(requestDto);
						
					ClientRecive clientRecive = new ClientRecive(socket);
					clientRecive.start();
					
					mainCard.show(MainPanel, "RoomPanel");
					
				
					
		
					
					
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "서버에 접속할 수 없습니다.", "접속실패", JOptionPane.ERROR_MESSAGE);
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

		roomModel = new DefaultListModel<>();
		roomList = new JList<String>(roomModel);
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String roomEnter = roomList.getSelectedValue();
				if(e.getClickCount() ==  1) {
					if (roomEnter != null) {
						
						
					}
				}
				
			}
		});
		ChattingRoomScroll.setViewportView(roomList);
		
		
		
		JButton CrateRoomButton = new JButton("방 생성");
		CrateRoomButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String room = JOptionPane.showInputDialog( null, "방 제목을 여기에 입력", "방 생성하기",JOptionPane.INFORMATION_MESSAGE);
				if (room != null && !room.isEmpty()) {
				
					try {
					RequestDto<?> requestDto = new RequestDto<String>("roomCreate", null, room, null);
						sendRequest(requestDto);
						
						ClientRecive clientRecive = new ClientRecive(socket);
						clientRecive.start();
								
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					
				}
				
				
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
		RoomOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainCard.show(MainPanel, "RoomPanel");
				clearFields(null);
				
				
			}
		});
		RoomOutButton.setBounds(345, 17, 97, 23);
		ChattingPanel.add(RoomOutButton);
		
		JButton SendButton = new JButton("전송");
		SendButton.setBounds(388, 692, 66, 59);
		ChattingPanel.add(SendButton);
		
		JScrollPane MessageScroll = new JScrollPane();
		MessageScroll.setBounds(0, 692, 390, 59);
		ChattingPanel.add(MessageScroll);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()  == KeyEvent.VK_ENTER) {
					//메시지 보냄
				}
			}
		});
		MessageScroll.setViewportView(textField);
		textField.setColumns(10);
		
		
	}
	private void clearFields(	List<JTextArea> textArea) {
		for(JTextArea field : textArea) {
			if(field.getText().isEmpty()) {
				continue;
			}
			field.setText("");
		}
	}
	
	private void sendRequest(RequestDto<?> requestDto) throws IOException {
		OutputStream outputStream;
		outputStream =  socket.getOutputStream();
		
		PrintWriter write = new PrintWriter(outputStream,true);
		write.println(gson.toJson(requestDto));
		write.flush();
		write.close(); 
	}
}
