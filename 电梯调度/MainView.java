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
	private static int totFloor = 20;  //楼层数
	private static int totList = 5;    //电梯数
	private EleThread[] eleThread;     //电梯线程
	Container cp;
	private Color iniColor1 = new Color(233,222,184);
	private Color pressColor1 = new Color(211,137,157);
	
	JPanel floorPanel = new JPanel();               //楼层面板
	JButton [] floorButton = new JButton[totFloor]; //楼层号
	JButton[] upButton = new JButton[totFloor]; //向上键
	JButton[] downButton = new JButton[totFloor]; //向下键
	int[] upState = new int[totFloor];              //向上键的状态
	int[] downState = new int[totFloor];            //向下键的状态
	JButton text_up,text_down,text_floor;
	JButton [] block = new JButton[3];
	
	
	public MainView() {
		
		cp = this.getContentPane();
		cp.setLayout(new GridLayout(1, totList + 1));
		
		floorPanel.setLayout(new GridLayout(totFloor + 2, 3));//楼层面板分区22行3列
		text_up = new JButton("上");
		text_down = new JButton("下");
		text_floor = new JButton("楼层");
		text_up.setEnabled(false);                        //不可点击
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
		
		//初始化方向键状态
		for(int i = 0; i < totFloor; i++) {
			upState[i] = 0;
			downState[i] = 0;
		}
		
		//设置鼠标点击上下键事件监听
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
			//设置楼层数按钮
			floorButton[i] = new JButton(""+(i + 1));                
			floorButton[i].setBackground(iniColor1);
			floorButton[i].setEnabled(false);
			
			//设置向上键按钮
			upButton[i] = new BasicArrowButton(BasicArrowButton.NORTH);
			upButton[i].setBackground(iniColor1);
			upButton[i].addMouseListener(touchListener);
			//设置向下键按钮
			downButton[i] = new BasicArrowButton(BasicArrowButton.SOUTH);
			downButton[i].setBackground(iniColor1);
			downButton[i].addMouseListener(touchListener);
			
			if(i == floorButton.length - 1) {
				upButton[i].setVisible(false);   //最高层没有向上键
			}
			if(i == 0) {
				downButton[i].setVisible(false); //最底层没有向下键
			}
			floorPanel.add(floorButton[i]);
			floorPanel.add(upButton[i]);
			floorPanel.add(downButton[i]);
		}
		cp.add(floorPanel);       //置入楼层面板
		
		//创建电梯线程
		eleThread = new EleThread[totList];
		for(int i = 0; i < totList; i++) {
			EleThread elev = new EleThread();
			cp.add(elev);
			elev.getthread().start();         //调用EleThread的run方法
			eleThread[i] = elev;
		}
		//调度线程
		Thread schedThread = new Thread(this);
		schedThread.start();
	}
	
	//返回总楼层数
	public static int gettotFloor() {
		return totFloor;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			//向上键被激活
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
			//向下键被激活
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
	//寻找响应向下键的电梯
	private boolean searchListdown(int floor) {
		int elevNo = -1;
		int minDist = totFloor;
		for(int i = 0; i < totList; i++) {
			//只有暂停的和正在下降的且无报警的电梯能响应向下键
			if((eleThread[i].isSTOP() || (eleThread[i].isDOWN() && eleThread[i].getcurFloor() >= floor)) && !eleThread[i].getwarnFlag()) {
				int dist = Math.abs(floor - eleThread[i].getcurFloor()); 
				if(dist < minDist) {//选择距离最小的
					elevNo = i;
					minDist = dist;
				}
			}
	    }
		if(minDist != totFloor) {
			downState[floor] = 5 + elevNo;       //即蕴含了电梯号
			eleThread[elevNo].setList(floor);
			return true;
		}
		else {
			return false;
		}
		
	}

	//寻找响应向上键的电梯
	private boolean searchListup(int floor) {
		int elevNo = -1;
		int minDist = totFloor;
		for(int i = 0; i < totList; i++) {
			//只有暂停的和正在上升的且无报警的电梯能响应向上键
			if((eleThread[i].isSTOP() || (eleThread[i].isUP() && eleThread[i].getcurFloor() <= floor))&& !eleThread[i].getwarnFlag()) {
				int dist = Math.abs(floor - eleThread[i].getcurFloor()); 
				if(dist < minDist) {//选择距离最小的
					elevNo = i;
					minDist = dist;
				}
			}
	    }
		if(minDist != totFloor) {
			upState[floor] = 5 + elevNo;          //即蕴含了电梯号
			eleThread[elevNo].setList(floor);
			return true;
		}
		else {
			return false;
		}
		
	}

}

