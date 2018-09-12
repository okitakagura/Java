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
//文件的各种操作

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
		private String path = new String("MyRoot:");     //当前路径
		private JTextField dicText;                      //当前路径框
		private int curBlock;                            //当前块号
		private ArrayList curDirs = new ArrayList();     //当前所有子目录
		private ArrayList curFiles = new ArrayList();    //当前所有文件
		private JComboBox combDir;                       //子目录选择
		private JComboBox combFile;                      //文件选择
		private FileEditor fileEditor;                   //文件编辑框
		private DiskManager disk;                        //磁盘管理
		private Color buttonColor = new Color(233,222,184);//按钮颜色
		
		public MainPanel() {
			setLayout(null);
			
			//当前目录设置
			JLabel cudirLabel = new JLabel("当前目录");
			cudirLabel.setSize(100,20);
			cudirLabel.setLocation(200,10);
			add(cudirLabel);
			//显示当前路径
			dicText = new JTextField(path, 50);
			dicText.setSize(200,20);
			dicText.setLocation(200, 30);
			dicText.setEditable(false);
			add(dicText);
			
			//当前目录下的文件
			JLabel fileLabel = new JLabel("文件");
			fileLabel.setSize(100,20);
			fileLabel.setLocation(40,160);
			add(fileLabel);
			combFile = new JComboBox();
			combFile.setSize(200,20);
			combFile.setLocation(40,180);
			add(combFile);
			//当前目录下的子目录
			JLabel dirLabel = new JLabel("文件夹");
			dirLabel.setSize(100,20);
			dirLabel.setLocation(320,160);
			add(dirLabel);
			combDir = new JComboBox();
			combDir.setSize(200,20);
			combDir.setLocation(320,180);
			add(combDir);
			
			//操作按钮
			JButton exitButton = new JButton("退出系统");
			exitButton.addActionListener(new exitListener());
			exitButton.setSize(100,25);
			exitButton.setLocation(250,220);
			add(exitButton);
			
			JButton createFile = new JButton("创建文件");
			createFile.setSize(100,30);
			createFile.setLocation(40, 70);
	        createFile.setBackground(null);
			createFile.addActionListener(new createFileListener());
			createFile.setBackground(buttonColor);;
			add(createFile);
			
			JButton createDir = new JButton("创建子目录");
			createDir.setSize(100,30);
			createDir.setLocation(320, 70);
		    createDir.setBackground(buttonColor);   
			createDir.addActionListener(new createDirListener());
			add(createDir);
			
			JButton delFile = new JButton("删除文件");
			delFile.setSize(100,30);
			delFile.setLocation(180, 70);
			delFile.setBackground(buttonColor);
			delFile.addActionListener(new delFileListener());
			add(delFile);
			
			JButton delDir = new JButton("删除子目录");
			delDir.setSize(100,30);
			delDir.setLocation(460, 70);
			delDir.setBackground(buttonColor);
			delDir.addActionListener(new delDirListener());
			add(delDir);
			
			JButton nextDir = new JButton("下一级");
			nextDir.setSize(100,30);
			nextDir.setLocation(320, 120);
			nextDir.setBackground(buttonColor);
			nextDir.addActionListener(new nextDirListener());
			add(nextDir);
			
			JButton lastDir = new JButton("上一级");
			lastDir.setSize(100,30);
			lastDir.setLocation(460, 120);
			lastDir.setBackground(buttonColor);
			lastDir.addActionListener(new lastDirListener());
			add(lastDir);
			
			JButton format = new JButton("格式化");
			format.setSize(100,30);
			format.setLocation(40, 120);
			format.setBackground(buttonColor);
			format.addActionListener(new formatListener());
			add(format);
			
			JButton editFile = new JButton("编辑文件");
			editFile.setSize(100,30);
			editFile.setLocation(180, 120);
			editFile.setBackground(buttonColor);
			editFile.addActionListener(new editFileListener());
			add(editFile);
		
			//上一次退出时保存的文件系统内容恢复到磁盘上
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream("filemanager.fy"));
				disk = (DiskManager) in.readObject();
				in.close();
				disk.reload(curDirs, curFiles);
				//选择条重新载入
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
		//退出系统时保存文件系统中的内容，以便下次恢复
		class exitListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("filemanager.fy"));
					out.writeObject(disk);   //将DiskManager类保存
					out.close();
					System.exit(0);
					}catch(IOException err) {
					System.out.println(err.getMessage());
				}
			}
					
		}
		
		//创建文件的监听
		class createFileListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fileName = JOptionPane.showInputDialog(null,"请输入文件名：\n");
				if(fileName == null) {
					return;
				}
				else {
					fileName = fileName.trim();
					if(fileName.length() > 16) {
						JOptionPane.showMessageDialog(null, "文件名应小于16个字符","提醒",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//判断是否为空
				if(fileName.equals("")) {
					JOptionPane.showMessageDialog(null, "请输入文件名","提醒",JOptionPane.WARNING_MESSAGE);
					return;
				}
				//判断在当前目录文件是否重复
				for(int i = 0; i < curFiles.size(); i++) {
					if(fileName.equals(curFiles.get(i))) {
						JOptionPane.showMessageDialog(null, "文件已经存在","提醒",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//加入新文件
				if(disk.addFile(curBlock,fileName)) {
					combFile.addItem(fileName);
					curFiles.add(fileName);
				}
				else {
					JOptionPane.showMessageDialog(null, "该盘区已满","提醒",JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}
		
		//创建子目录的监听
		class createDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dirName = JOptionPane.showInputDialog(null,"请输入子目录名：\n");
				if(dirName == null) {
					return;
				}
				else {
					dirName = dirName.trim();
					if(dirName.length() > 16) {
						JOptionPane.showMessageDialog(null, "子目录名应小于16个字符","提醒",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//判断是否为空
				if(dirName.equals("")) {
					JOptionPane.showMessageDialog(null, "请输入子目录名","提醒",JOptionPane.WARNING_MESSAGE);
					return;
				}
				//判断在当前目录文件是否重复
				for(int i = 0; i < curDirs.size(); i++) {
					if(dirName.equals(curDirs.get(i))) {
						JOptionPane.showMessageDialog(null, "子目录已经存在","提醒",JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//加入新文件
				if(disk.addDirectory(curBlock,dirName)) {
					combDir.addItem(dirName);
					curDirs.add(dirName);
				}
				else {
					JOptionPane.showMessageDialog(null, "该盘区已满","提醒",JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}
		
		//删除文件的监听
		class delFileListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fileName = (String)combFile.getSelectedItem(); //由选择条确定项
				//选择为空时
				if(fileName == null || fileName.equals("")) {
					    JOptionPane.showMessageDialog(null, "请选择一个文件");
					    return;
				}
				else if(fileName != null && !fileName.equals("")) {
					disk.delFile(curBlock,fileName);
					curFiles.remove(fileName);
					combFile.removeItem(fileName);
				}
			}
		}
		
		//删除子目录的监听
		class delDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dirName = (String)combDir.getSelectedItem(); //由选择条确定项
				//选择为空时
			    if(dirName == null || dirName.equals("")) {
			    	JOptionPane.showMessageDialog(null, "请选择文件夹");
			    	return;
			    }
			    else if(dirName != null && !dirName.equals("")) {
					disk.delDirectory(curBlock,dirName);
					curDirs.remove(dirName);
					combDir.removeItem(dirName);
				}
			}
		}
		
		//下一级的监听
		class nextDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			    String dirName = (String) combDir.getSelectedItem(); //由选择条确定项
			    //选择为空时
			    if(dirName == null || dirName.equals("")) {
			    	JOptionPane.showMessageDialog(null, "请选择文件夹");
			    	return;
			    }
			    path = path + "\\" + dirName;  //路径更新
			    curBlock = disk.nextDirectory(curBlock,dirName,curDirs,curFiles); //当前块更新
			    dicText.setText(path);
			    combDir.removeAllItems();   //选择条展示下一级的文件及文件夹
				for(int i = 0; i < curDirs.size(); i++) {
					combDir.addItem((String)curDirs.get(i));
				}
				combFile.removeAllItems();
				for(int i = 0; i < curFiles.size(); i++) {
					combFile.addItem((String)curFiles.get(i));
				}
			}
		}
		
		//上一级的监听
		class lastDirListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int block = disk.lastDirectory(curBlock,curDirs,curFiles);
				if(block == -1) {
					return;
				}
				curBlock = block;
				int index = path.lastIndexOf("\\"); //找到最后一次出现\\的位置
				path = path.substring(0, index); //上一级目录
				dicText.setText(path);
				combDir.removeAllItems();  //选择条展示上一级的文件及文件夹
				for(int i = 0; i < curDirs.size(); i++) {
					combDir.addItem((String)curDirs.get(i));
				}
				combFile.removeAllItems();
				for(int i = 0; i < curFiles.size(); i++) {
					combFile.addItem((String)curFiles.get(i));
				}
			}
		}
		
		//格式化的监听
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
		
		//编辑文件的监听
		class editFileListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 String fileName = (String) combFile.getSelectedItem(); //由选择条确定项
				  //选择为空时
				 if(fileName == null || fileName.equals("")) {
				    JOptionPane.showMessageDialog(null, "请选择一个文件");
				    return;
				 }
				 fileEditor = new FileEditor(null, fileName); //跳出编辑框
				 String Text = disk.getFileContent(curBlock, fileName);
				 fileEditor.textArea.setText(Text);
				 fileEditor.show();
				   
			}
		}
		//文件编辑框
		class FileEditor extends JDialog{
			JTextArea textArea = new JTextArea();
			JButton saveButton = new JButton("保存");
			JButton cancelButton = new JButton("取消");
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
                //设置滚动条
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
			//保存按钮
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
			//取消按钮
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
	