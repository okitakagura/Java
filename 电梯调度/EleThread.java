import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class EleThread extends JPanel implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int totFloor;    //¥����
	private int direct;             //�ƶ�����0Ϊ��ͣ��1Ϊ���ϣ�-1Ϊ����
	private int curFloor;           //��ǰ¥��
	private int goaFloor;           //Ŀ��¥��
	private boolean runStart;       //���ݿ�ʼ
	private boolean warnFlag;       //������״̬
	private boolean[] numbState;    //���������ּ�״̬
	private Thread thread;          //�߳�
	//������ɫ����
	private Color iniColor = new Color(190,224,211);
	private Color pressColor = new Color(250,184,136);
	private Color floorColor0 = new Color(154,154,154);
	private Color floorColor1 = new Color(215,94,94);
	//�����ڰ���
	JPanel listkeyPanel0 = new JPanel();
	JPanel listkeyPanel1 = new JPanel();
	//�����ƶ����
	JPanel []listmovePanel;
	
	JButton[] numbButton;           //���ּ�
	JButton[] floorBlock;           //�����ƶ���
	JButton open,close,showcurFloor,showlistState,warning,cancelwarn;
	
	public EleThread() {
		totFloor = MainView.gettotFloor();
		//��ʼ���е���ͣ����һ¥
		direct = 0;                           
		curFloor = 0;
		goaFloor = 0;
		runStart = false;
		warnFlag = false;
		numbButton = new JButton[totFloor];
		floorBlock = new JButton[totFloor];
		//�����ƶ�����ʼ��
		listmovePanel = new JPanel[totFloor];
		for(int i = 0; i < totFloor; i++) {
			listmovePanel[i] = new JPanel();
			listmovePanel[i].setLayout(new GridLayout(1, 2));
		}
		//���������ּ�״̬��ʼ��
		numbState = new boolean[totFloor];  
		for(int i = 0; i < totFloor; i++) {
			numbState[i] = false;
		}
		//�����߳�
		thread = new Thread(this);
		
		
		//��岼��
		setLayout(new GridLayout(totFloor + 2, 1));
		listkeyPanel0.setLayout(new GridLayout(1, 3));
		listkeyPanel1.setLayout(new GridLayout(1, 3));
		open = new JButton("<>");                         //���ż�
		close = new JButton("><");                        //���ż�
		warning = new JButton("!");                       //������
		cancelwarn = new JButton("X");                    //���������
		warning.setEnabled(true);
		cancelwarn.setEnabled(false);
		showcurFloor = new JButton();                     //��ʾ��������¥��
		showcurFloor.setBackground(new Color(218,197,252));
		showcurFloor.setText(""+(curFloor + 1));
		showlistState = new JButton();                    //��ʾ����״̬
		showlistState.setText("STOP");
		showlistState.setBackground(null);
		listkeyPanel0.add(showcurFloor);
		listkeyPanel0.add(showlistState);
		listkeyPanel0.add(warning);
		listkeyPanel1.add(open);
		listkeyPanel1.add(close);
		listkeyPanel1.add(cancelwarn);
		this.add(listkeyPanel0);
		this.add(listkeyPanel1);
		
		//���õ��������ּ��ļ���
		class numbButtonAction extends MouseAdapter implements MouseListener{
			public void mousePressed(MouseEvent e) {
			if(warnFlag == false) {
				runStart = true;
				for(int i = 0; i < totFloor; i++) {
					if(e.getSource() == numbButton[i]) {
						numbState[i] = true;
						numbButton[i].setBackground(pressColor);
						if(direct == 0) {
							goaFloor = i;
						}
						//�������������У�����ѡ������¥��ΪĿ��¥��
						if(direct == 1) {
							goaFloor = 0;
							for (int j = totFloor - 1; j >= 0; j--) {
								if (numbState[j]) {
									goaFloor = j;
									break;
								}
							}
						}
						//�����������½�������ѡ������¥��ΪĿ��¥�㡣
						if(direct == -1) {
							goaFloor = 0;
							for (int j = 0; j < totFloor; j++) {
								if (numbState[j]) {
									goaFloor = j;
									break;
								}
							}
						}
					}
				}
			 }
			}
		}
		//���ֵ����ƶ����
		MouseListener numbListener = new numbButtonAction();
		for(int i = totFloor - 1; i >= 0; i--) {
			numbButton[i] = new JButton("" + (i+1));
			numbButton[i].setBackground(iniColor);
			numbButton[i].addMouseListener(numbListener);
			floorBlock[i] = new JButton();
			floorBlock[i].setEnabled(false);
			floorBlock[i].setBackground(floorColor0);
			listmovePanel[i].add(numbButton[i]);
			listmovePanel[i].add(floorBlock[i]);
			this.add(listmovePanel[i]);
		}
		floorBlock[curFloor].setBackground(floorColor1);
		//���ŵ��߳�
		class opendoorThread extends Thread{
			public opendoorThread() {
				start();
			}
			public void run() {
					floorBlock[curFloor].setBackground(Color.orange);
					showlistState.setText("����");
					showlistState.setBackground(Color.orange);
					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					floorBlock[curFloor].setBackground(floorColor1);
					showlistState.setText("����");
					showlistState.setBackground(null);
					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					showlistState.setText("STOP");
					showlistState.setBackground(null);
			}
		}
		//���Ű�ť����
        open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(direct == 0) {
				    new opendoorThread();	
				}
			}
        	
        });
        //���ŵ� �߳�
        class closedoorThread extends Thread{
        	public closedoorThread() {
        		start();
        	}
        	public void run() {
        		floorBlock[curFloor].setBackground(floorColor1);
				showlistState.setText("����");
				showlistState.setBackground(null);
				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showlistState.setText("STOP");
				showlistState.setBackground(null);
        	}
        }
        //���Ű�ť����
        close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(direct == 0) {
			         new closedoorThread();
				}
				
			}
		});
        //������ť����������ֹͣ����
        warning.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(direct == 0) {
					warnFlag = true;
					warning.setEnabled(false);
					cancelwarn.setEnabled(true);
					warning.setBackground(Color.red);
					warning.setText("Alert!");
					for(int i = 0; i < totFloor; i++)
					{
						numbButton[i].setEnabled(false);
					}
				}
			}
		});
        //ȡ��������������������
        cancelwarn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				warnFlag = false;
				cancelwarn.setEnabled(false);
				warning.setEnabled(true);
				warning.setBackground(null);
				warning.setText("!");
				for(int i = 0; i < totFloor; i++)
				{
					numbButton[i].setEnabled(true);
				}
				
			}
		});
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(direct != 0) {
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				direct = 0;
				runStart = false;
				showlistState.setText("STOP");
				showlistState.setBackground(null);
			}
			if(goaFloor > curFloor) {
				direct = 1;
				showlistState.setText("up");
				showlistState.setBackground(Color.red);
				try {
					listup();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				direct = 0;
				showlistState.setText("STOP");
				showlistState.setBackground(null);
			}
			else if(goaFloor < curFloor) { 
				direct = -1;
				showlistState.setText("down");
				showlistState.setBackground(Color.red);
				try {
					listdown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				direct = 0;
				showlistState.setText("STOP");
				showlistState.setBackground(null);
			}
			//��������ͣ���������
			else if(goaFloor == curFloor && runStart == true) {
				direct = 0;
				try {
					listcurrent();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showlistState.setText("STOP");
				showlistState.setBackground(null);
			}
		}
	}
    //��������
	public void listup() throws InterruptedException {
		int oldFloor = curFloor;
		for(int i = curFloor + 1; i <= goaFloor; i++) {
				Thread.sleep(600);
				floorBlock[i].setBackground(floorColor1);
				if(i > oldFloor) {
					floorBlock[i - 1].setBackground(floorColor0);
				}
				//����Ŀ��¥��
				if(numbState[i]) {
					floorBlock[i].setBackground(Color.orange);
					showlistState.setText("����");
					showlistState.setBackground(Color.orange);
					Thread.sleep(900);
					floorBlock[i].setBackground(floorColor1);
					showlistState.setText("����");
					showlistState.setBackground(null);
					Thread.sleep(900);
				}
				curFloor = i;
				showcurFloor.setText(""+(curFloor + 1));
			
		}
		setnumbState();            //���ּ�״̬����
	}
	//��������
	public void listdown() throws InterruptedException {
		int oldFloor = curFloor;
		for(int i = curFloor - 1; i >= goaFloor; i--) {
				Thread.sleep(600);
				floorBlock[i].setBackground(floorColor1);
				if(i < oldFloor) {
					floorBlock[i + 1].setBackground(floorColor0);
				}
				//����Ŀ��¥��
				if(numbState[i]) {
					floorBlock[i].setBackground(Color.orange);
					showlistState.setText("����");
					showlistState.setBackground(Color.orange);
					Thread.sleep(900);
					floorBlock[i].setBackground(floorColor1);
					showlistState.setText("����");
					showlistState.setBackground(null);
					Thread.sleep(900);
				}
				curFloor = i;
				showcurFloor.setText(""+(curFloor + 1));
		
		}
		setnumbState();             //���ּ�״̬����
	}
	//�����ڵ�ǰ����Ӧ
	public void listcurrent() throws InterruptedException{
		floorBlock[goaFloor].setBackground(Color.orange);
		showlistState.setText("����");
		showlistState.setBackground(Color.orange);
		Thread.sleep(900);
		floorBlock[goaFloor].setBackground(floorColor1);
		showlistState.setText("����");
		showlistState.setBackground(null);
		Thread.sleep(900);
		setnumbState();
	}
	//�������ּ�״̬
	private void setnumbState() {
		// TODO Auto-generated method stub
		runStart = false;
		for(int i = 0; i < totFloor; i++) {
			if(numbState[i]) {
				numbState[i] = false;
				numbButton[i].setBackground(iniColor);
			}
		}
	}
	//�����߳�
	public Thread getthread() {
		return thread;
	}
	//���ص���״̬
	public boolean isUP() {
		return (direct == 1);
	}
	public boolean isDOWN() {
		return (direct == -1);
	}
	public boolean isSTOP() {
		return (direct == 0);
	}
	
	//���ص�ǰ��������¥��
	public int getcurFloor() {
		return curFloor;
	}
	
	public boolean getwarnFlag() {
		return warnFlag;
	}
	//���õ�����Ӧ���¼���״̬
	public void setList(int floor) {
	if(warnFlag == false) {
		runStart = true;
		//��ʱ����δ����
		if(direct == 0) {
			goaFloor = floor;
			numbState[floor] = true;
			floorBlock[floor].setBackground(new Color(148,103,133));
			if(curFloor > goaFloor) {
				direct = -1;
				showlistState.setText("down");
				showlistState.setBackground(Color.red);
			}
			else if(curFloor < goaFloor) {
				direct = 1;
				showlistState.setText("up");
				showlistState.setBackground(Color.red);
			}
		}
		//��ʱ�����������У������������Ŀ��¥��
		else if((direct == 1 && floor > goaFloor) || (direct == -1 && floor < goaFloor)) {
			goaFloor = floor;
			numbState[floor] = true;
			floorBlock[floor].setBackground(new Color(148,103,133));
		}
		//��ʱ�����������У���Ŀ��¥��û�иı�
		else if((direct == 1 && floor > curFloor) || (direct == -1 && floor < curFloor)) {
			numbState[floor] = true;
			floorBlock[floor].setBackground(new Color(148,103,133));
		}
	 }
   }
}


