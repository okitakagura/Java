import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicArrowButton;

public  class MainView extends JFrame implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int totFloor = 20;  //¥����
	private static int totList = 5;    //������
	private EleThread[] eleThread;     //�����߳�
	Container cp;
	private Color iniColor1 = new Color(233,222,184);
	private Color pressColor1 = new Color(211,137,157);
	
	JPanel floorPanel = new JPanel();               //¥�����
	JButton [] floorButton = new JButton[totFloor]; //¥���
	JButton[] upButton = new JButton[totFloor]; //���ϼ�
	JButton[] downButton = new JButton[totFloor]; //���¼�
	int[] upState = new int[totFloor];              //���ϼ���״̬
	int[] downState = new int[totFloor];            //���¼���״̬
	JButton text_up,text_down,text_floor;
	JButton [] block = new JButton[3];
	
	
	public MainView() {
		
		cp = this.getContentPane();
		cp.setLayout(new GridLayout(1, totList + 1));
		
		floorPanel.setLayout(new GridLayout(totFloor + 2, 3));//¥��������22��3��
		text_up = new JButton("��");
		text_down = new JButton("��");
		text_floor = new JButton("¥��");
		text_up.setEnabled(false);                        //���ɵ��
		text_down.setEnabled(false);
		text_floor.setEnabled(false);
		for(int i = 0; i < 3; i++) {
			block[i] = new JButton();
			block[i].setVisible(false);
			floorPanel.add(block[i]);
		}
		floorPanel.add(text_floor);
		floorPanel.add(text_up);
		floorPanel.add(text_down);
		
		//��ʼ�������״̬
		for(int i = 0; i < totFloor; i++) {
			upState[i] = 0;
			downState[i] = 0;
		}
		
		//������������¼��¼�����
		class moveButtonAction extends MouseAdapter implements MouseListener{
			public void mousePressed(MouseEvent e) {
				for(int i = 0; i < upButton.length; i++) {
					if(e.getSource() == upButton[i]) {
						upButton[i].setBackground(pressColor1);
						upState[i] = 1;
					}
					if(e.getSource() == downButton[i]) {
						downButton[i].setBackground(pressColor1);
						downState[i] = 1;
					}
				}
			}
		}
		MouseListener touchListener = new moveButtonAction();
		
		for(int i = floorButton.length - 1; i >= 0; i--) {
			//����¥������ť
			floorButton[i] = new JButton(""+(i + 1));                
			floorButton[i].setBackground(iniColor1);
			floorButton[i].setEnabled(false);
			
			//�������ϼ���ť
			upButton[i] = new BasicArrowButton(BasicArrowButton.NORTH);
			upButton[i].setBackground(iniColor1);
			upButton[i].addMouseListener(touchListener);
			//�������¼���ť
			downButton[i] = new BasicArrowButton(BasicArrowButton.SOUTH);
			downButton[i].setBackground(iniColor1);
			downButton[i].addMouseListener(touchListener);
			
			if(i == floorButton.length - 1) {
				upButton[i].setVisible(false);   //��߲�û�����ϼ�
			}
			if(i == 0) {
				downButton[i].setVisible(false); //��ײ�û�����¼�
			}
			floorPanel.add(floorButton[i]);
			floorPanel.add(upButton[i]);
			floorPanel.add(downButton[i]);
		}
		cp.add(floorPanel);       //����¥�����
		
		//���������߳�
		eleThread = new EleThread[totList];
		for(int i = 0; i < totList; i++) {
			EleThread elev = new EleThread();
			cp.add(elev);
			elev.getthread().start();         //����EleThread��run����
			eleThread[i] = elev;
		}
		//�����߳�
		Thread schedThread = new Thread(this);
		schedThread.start();
	}
	
	//������¥����
	public static int gettotFloor() {
		return totFloor;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			//���ϼ�������
			for(int i = 0; i < totFloor; i++) {
				if(upState[i] == 1) {
					searchListup(i);
				}
				if(upState[i] >= 5) {
					if(i == eleThread[upState[i] - 5].getcurFloor()) {
						upState[i] = 0;
						upButton[i].setBackground(iniColor1);
					}
				}
			}
			//���¼�������
			for(int i = 0; i < totFloor; i++) {
				if(downState[i] == 1) {
					searchListdown(i);
				}
				if(downState[i] >= 5) {
					if(i == eleThread[downState[i] - 5].getcurFloor()) {
						downState[i] = 0;
						downButton[i].setBackground(iniColor1);
					}
				}
			}
		}
	}
	//Ѱ����Ӧ���¼��ĵ���
	private boolean searchListdown(int floor) {
		int elevNo = -1;
		int minDist = totFloor;
		for(int i = 0; i < totList; i++) {
			//ֻ����ͣ�ĺ������½������ޱ����ĵ�������Ӧ���¼�
			if((eleThread[i].isSTOP() || (eleThread[i].isDOWN() && eleThread[i].getcurFloor() >= floor)) && !eleThread[i].getwarnFlag()) {
				int dist = Math.abs(floor - eleThread[i].getcurFloor()); 
				if(dist < minDist) {//ѡ�������С��
					elevNo = i;
					minDist = dist;
				}
			}
	    }
		if(minDist != totFloor) {
			downState[floor] = 5 + elevNo;       //���̺��˵��ݺ�
			eleThread[elevNo].setList(floor);
			return true;
		}
		else {
			return false;
		}
		
	}

	//Ѱ����Ӧ���ϼ��ĵ���
	private boolean searchListup(int floor) {
		int elevNo = -1;
		int minDist = totFloor;
		for(int i = 0; i < totList; i++) {
			//ֻ����ͣ�ĺ��������������ޱ����ĵ�������Ӧ���ϼ�
			if((eleThread[i].isSTOP() || (eleThread[i].isUP() && eleThread[i].getcurFloor() <= floor))&& !eleThread[i].getwarnFlag()) {
				int dist = Math.abs(floor - eleThread[i].getcurFloor()); 
				if(dist < minDist) {//ѡ�������С��
					elevNo = i;
					minDist = dist;
				}
			}
	    }
		if(minDist != totFloor) {
			upState[floor] = 5 + elevNo;          //���̺��˵��ݺ�
			eleThread[elevNo].setList(floor);
			return true;
		}
		else {
			return false;
		}
		
	}

}

