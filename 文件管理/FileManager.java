package Filesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
//�ļ��ĸ��ֲ���

public class FileManager {
	public static void main(String[] args) {
	MainFrame frame = new MainFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.show();
	}
}

class MainFrame extends JFrame{
	public MainFrame() {
		setSize(Utility.WIDTH,Utility.HEIGHT);
		Container contentPane = getContentPane();
		MainPanel panel = new MainPanel();
		contentPane.add(panel);
		setTitle("FileManagment");
		setResizable(false);
	}
	
	class MainPanel extends JPanel{
		private String path = new String("MyRoot:");     //��ǰ·��
		private JTextField dicText;                      //��ǰ·����
		private int curBlock;                            //��ǰ���
		private ArrayList curDirs = new ArrayList();     //��ǰ������Ŀ¼
		private ArrayList curFiles = new ArrayList();    //��ǰ�����ļ�
		private JComboBox combDir;                       //��Ŀ¼ѡ��
		private JComboBox combFile;                      //�ļ�ѡ��
		private FileEditor fileEditor;                   //�ļ��༭��
		private DiskManager disk;                        //���̹���
		private Color buttonColor = new Color(233,222,184);//��ť��ɫ
		
		public MainPanel() {
			setLayout(null);
			
			//��ǰĿ¼����
			JLabel cudirLabel = new JLabel("��ǰĿ¼");
			cudirLabel.setSize(100,20);
			cudirLabel.setLocation(200,10);
			add(cudirLabel);
			//��ʾ��ǰ·��
			dicText = new JTextField(path, 50);
			dicText.setSize(200,20);
			dicText.setLocation(200, 30);
			dicText.setEditable(false);
			add(dicText);
			
			//��ǰĿ¼�µ��ļ�
			JLabel fileLabel = new JLabel("�ļ�");
			fileLabel.setSize(100,20);
			fileLabel.setLocation(40,160);
			add(fileLabel);
			combFile = new JComboBox();
			combFile.setSize(200,20);
			combFile.setLocation(40,180);
			add(combFile);
			//��ǰĿ¼�µ���Ŀ¼
			JLabel dirLabel = new JLabel("�ļ���");
			dirLabel.setSize(100,20);
			dirLabel.setLocation(320,160);
			add(dirLabel);
			combDir = new JComboBox();
			combDir.setSize(200,20);
			combDir.setLocation(320,180);
			add(combDir);
			
			//������ť
			JButton exitButton = new JButton("�˳�ϵͳ");
			exitButton.addActionListener(new exitListener());
			exitButton.setSize(100,25);
			exitButton.setLocation(250,220);
			add(exitButton);
			
			JButton createFile = new JButton("�����ļ�");
			createFile.setSize(100,30);
			createFile.setLocation(40, 70);
	        createFile.setBackground(null);
			createFile.addActionListener(new createFileListener());
			createFile.setBackground(buttonColor);;
			add(createFile);
			
			JButton createDir = new JButton("������Ŀ¼");
			createDir.setSize(100,30);
			createDir.setLocation(320, 70);
		    createDir.setBackground(buttonColor);   
			createDir.addActionListener(new createDirListener());
			add(createDir);
			
			JButton delFile = new JButton("ɾ���ļ�");
			delFile.setSize(100,30);
			delFile.setLocation(180, 70);
			delFile.setBackground(buttonColor);
			delFile.addActionListener(new delFileListener());
			add(delFile);
			
			JButton delDir = new JButton("ɾ����Ŀ¼");
			delDir.setSize(100,30);
			delDir.setLocation(460, 70);
			delDir.setBackground(buttonColor);
			delDir.addActionListener(new delDirListener());
			add(delDir);
			
			JButton nextDir = new JButton("��һ��");
			nextDir.setSize(100,30);
			nextDir.setLocation(320, 120);
			nextDir.setBackground(buttonColor);
			nextDir.addActionListener(new nextDirListener());
			add(nextDir);
			
			JButton lastDir = new JButton("��һ��");
			lastDir.setSize(100,30);
			lastDir.setLocation(460, 120);
			lastDir.setBackground(buttonColor);
			lastDir.addActionListener(new lastDirListener());
			add(lastDir);
			
			JButton format = new JButton("��ʽ��");
			format.setSize(100,30);
			format.setLocation(40, 120);
			format.setBackground(buttonColor);
			format.addActionListener(new formatListener());
			add(format);
			
			JButton editFile = new JButton("�༭�ļ�");
			editFile.setSize(100,30);
			editFile.setLocation(180, 120);
			editFile.setBackground(buttonColor);
			editFile.addActionListener(new editFileListener());
			add(editFile);
		
			//��һ���˳�ʱ������ļ�ϵͳ���ݻָ���������
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream("filemanager.fy"));
				disk = (DiskManager) in.readObject();
				in.close();
				disk.reload(curDirs, curFiles);
				//ѡ������������
				for(int i = 0; i < curDirs.size(); i++) {
					combDir.addItem(curDirs.get(i));
				}
				for(int i = 0; i < curFiles.size(); i++) {
					combFile.addItem(curFiles.get(i));
				}
				disk.printDisc();
				if(disk == null)
				{
					disk = new DiskManager();
				}
			} catch (IOException err) {
				disk = new DiskManager();
				System.out.println(err.getMessage());
			} catch (ClassNotFoundException err) {
				System.out.println(err.getMessage());
			}
		}
		//�˳�ϵͳʱ�����ļ�ϵͳ�е����ݣ��Ա��´λָ�
		class exitListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("filemanager.fy"));
					out.writeObject(disk);   //��DiskManager�ౣ��
					out.close();
					System.exit(0);
					}catch(IOException err) {
					System.out.println(err.getMessage());
				}
			}
					
		}
		
		//�����ļ��ļ���
		class createFileListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fileName = JOptionPane.showInputDialog(null,"�������ļ�����\n");
				if(fileName == null) {
					return;
				}
				else {
					fileName = fileName.trim();
					if(fileName.length() > 16) {
						JOptionPane.showMessageDialog(null, "�ļ���ӦС��16���ַ�","����",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//�ж��Ƿ�Ϊ��
				if(fileName.equals("")) {
					JOptionPane.showMessageDialog(null, "�������ļ���","����",JOptionPane.WARNING_MESSAGE);
					return;
				}
				//�ж��ڵ�ǰĿ¼�ļ��Ƿ��ظ�
				for(int i = 0; i < curFiles.size(); i++) {
					if(fileName.equals(curFiles.get(i))) {
						JOptionPane.showMessageDialog(null, "�ļ��Ѿ�����","����",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//�������ļ�
				if(disk.addFile(curBlock,fileName)) {
					combFile.addItem(fileName);
					curFiles.add(fileName);
				}
				else {
					JOptionPane.showMessageDialog(null, "����������","����",JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}
		
		//������Ŀ¼�ļ���
		class createDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dirName = JOptionPane.showInputDialog(null,"��������Ŀ¼����\n");
				if(dirName == null) {
					return;
				}
				else {
					dirName = dirName.trim();
					if(dirName.length() > 16) {
						JOptionPane.showMessageDialog(null, "��Ŀ¼��ӦС��16���ַ�","����",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//�ж��Ƿ�Ϊ��
				if(dirName.equals("")) {
					JOptionPane.showMessageDialog(null, "��������Ŀ¼��","����",JOptionPane.WARNING_MESSAGE);
					return;
				}
				//�ж��ڵ�ǰĿ¼�ļ��Ƿ��ظ�
				for(int i = 0; i < curDirs.size(); i++) {
					if(dirName.equals(curDirs.get(i))) {
						JOptionPane.showMessageDialog(null, "��Ŀ¼�Ѿ�����","����",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//�������ļ�
				if(disk.addDirectory(curBlock,dirName)) {
					combDir.addItem(dirName);
					curDirs.add(dirName);
				}
				else {
					JOptionPane.showMessageDialog(null, "����������","����",JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}
		
		//ɾ���ļ��ļ���
		class delFileListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fileName = (String)combFile.getSelectedItem(); //��ѡ����ȷ����
				//ѡ��Ϊ��ʱ
				if(fileName == null || fileName.equals("")) {
					    JOptionPane.showMessageDialog(null, "��ѡ��һ���ļ�");
					    return;
				}
				else if(fileName != null && !fileName.equals("")) {
					disk.delFile(curBlock,fileName);
					curFiles.remove(fileName);
					combFile.removeItem(fileName);
				}
			}
		}
		
		//ɾ����Ŀ¼�ļ���
		class delDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dirName = (String)combDir.getSelectedItem(); //��ѡ����ȷ����
				//ѡ��Ϊ��ʱ
			    if(dirName == null || dirName.equals("")) {
			    	JOptionPane.showMessageDialog(null, "��ѡ���ļ���");
			    	return;
			    }
			    else if(dirName != null && !dirName.equals("")) {
					disk.delDirectory(curBlock,dirName);
					curDirs.remove(dirName);
					combDir.removeItem(dirName);
				}
			}
		}
		
		//��һ���ļ���
		class nextDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			    String dirName = (String) combDir.getSelectedItem(); //��ѡ����ȷ����
			    //ѡ��Ϊ��ʱ
			    if(dirName == null || dirName.equals("")) {
			    	JOptionPane.showMessageDialog(null, "��ѡ���ļ���");
			    	return;
			    }
			    path = path + "\\" + dirName;  //·������
			    curBlock = disk.nextDirectory(curBlock,dirName,curDirs,curFiles); //��ǰ�����
			    dicText.setText(path);
			    combDir.removeAllItems();   //ѡ����չʾ��һ�����ļ����ļ���
				for(int i = 0; i < curDirs.size(); i++) {
					combDir.addItem((String)curDirs.get(i));
				}
				combFile.removeAllItems();
				for(int i = 0; i < curFiles.size(); i++) {
					combFile.addItem((String)curFiles.get(i));
				}
			}
		}
		
		//��һ���ļ���
		class lastDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int block = disk.lastDirectory(curBlock,curDirs,curFiles);
				if(block == -1) {
					return;
				}
				curBlock = block;
				int index = path.lastIndexOf("\\"); //�ҵ����һ�γ���\\��λ��
				path = path.substring(0, index); //��һ��Ŀ¼
				dicText.setText(path);
				combDir.removeAllItems();  //ѡ����չʾ��һ�����ļ����ļ���
				for(int i = 0; i < curDirs.size(); i++) {
					combDir.addItem((String)curDirs.get(i));
				}
				combFile.removeAllItems();
				for(int i = 0; i < curFiles.size(); i++) {
					combFile.addItem((String)curFiles.get(i));
				}
			}
		}
		
		//��ʽ���ļ���
		class formatListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				disk.formatAll();
				path = new String("MyRoot:");
				dicText.setText(path);
				curDirs.clear();
				curFiles.clear();
				combDir.removeAllItems();
				combFile.removeAllItems();
			}
		}
		
		//�༭�ļ��ļ���
		class editFileListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 String fileName = (String) combFile.getSelectedItem(); //��ѡ����ȷ����
				  //ѡ��Ϊ��ʱ
				 if(fileName == null || fileName.equals("")) {
				    JOptionPane.showMessageDialog(null, "��ѡ��һ���ļ�");
				    return;
				 }
				 fileEditor = new FileEditor(null, fileName); //�����༭��
				 String Text = disk.getFileContent(curBlock, fileName);
				 fileEditor.textArea.setText(Text);
				 fileEditor.show();
				   
			}
		}
		//�ļ��༭��
		class FileEditor extends JDialog{
			JTextArea textArea = new JTextArea();
			JButton saveButton = new JButton("����");
			JButton cancelButton = new JButton("ȡ��");
			String filename;
			public FileEditor(JFrame frame, String name) {
				super(frame,name,true);
				setSize(400, 300);
				setLocation(400,200);
				setResizable(false);
				this.filename = name;
				textArea.setBackground(Color.WHITE);
				textArea.setLineWrap(true);
				
				saveButton.setBackground(buttonColor);
				cancelButton.setBackground(buttonColor);
				saveButton.addActionListener(new saveListener());
				cancelButton.addActionListener(new cancelListener());
                //���ù�����
				JScrollPane spEdit =
					new JScrollPane(
						textArea,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

				JPanel btPanel = new JPanel();
				btPanel.add(saveButton);
				btPanel.add(cancelButton);

				Container container = getContentPane();
				container.setLayout(new BorderLayout());
				container.add(btPanel, BorderLayout.SOUTH);
				container.add(spEdit);
			}
			//���水ť
			class saveListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String text = textArea.getText().trim();
					if(disk.saveFile(curBlock,filename,text)) {
						dispose();
						return;	
					}
				}
			}
			//ȡ����ť
			class cancelListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
					return;
				}
			}
		
		}
	}

}
	