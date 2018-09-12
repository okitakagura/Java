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
	private static int totFloor;    //楼层数
	private int direct;             //移动方向，0为暂停，1为向上，-1为向下
	private int curFloor;           //当前楼层
	private int goaFloor;           //目标楼层
	private boolean runStart;       //电梯开始
	private boolean warnFlag;       //警报键状态
	private boolean[] numbState;    //电梯内数字键状态
	private Thread thread;          //线程
	//设置颜色参数
	private Color iniColor = new Color(190,224,211);
	private Color pressColor = new Color(250,184,136);
	private Color floorColor0 = new Color(154,154,154);
	private Color floorColor1 = new Color(215,94,94);
	//电梯内按键
	JPanel listkeyPanel0 = new JPanel();
	JPanel listkeyPanel1 = new JPanel();
	//电梯移动面板
	JPanel []listmovePanel;
	
	JButton[] numbButton;           //数字键
	JButton[] floorBlock;           //电梯移动块
	JButton open,close,showcurFloor,showlistState,warning,cancelwarn;
	
	public EleThread() {
		totFloor = MainView.gettotFloor();
		//初始所有电梯停靠在一楼
		direct = 0;                           
		curFloor = 0;
		goaFloor = 0;
		runStart = false;
		warnFlag = false;
		numbButton = new JButton[totFloor];
		floorBlock = new JButton[totFloor];
		//电梯移动面板初始化
		listmovePanel = new JPanel[totFloor];
		for(int i = 0; i < totFloor; i++) {
			listmovePanel[i] = new JPanel();
			listmovePanel[i].setLayout(new GridLayout(1, 2));
		}
		//电梯内数字键状态初始化
		numbState = new boolean[totFloor];  
		for(int i = 0; i < totFloor; i++) {
			numbState[i] = false;
		}
		//产生线程
		thread = new Thread(this);
		
		
		//面板布局
		setLayout(new GridLayout(totFloor + 2, 1));
		listkeyPanel0.setLayout(new GridLayout(1, 3));
		listkeyPanel1.setLayout(new GridLayout(1, 3));
		open = new JButton("<>");                         //开门键
		close = new JButton("><");                        //关门键
		warning = new JButton("!");                       //报警键
		cancelwarn = new JButton("X");                    //解除报警键
		warning.setEnabled(true);
		cancelwarn.setEnabled(false);
		showcurFloor = new JButton();                     //显示电梯所在楼层
		showcurFloor.setBackground(new Color(218,197,252));
		showcurFloor.setText(""+(curFloor + 1));
		showlistState = new JButton();                    //显示电梯状态
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
		
		//设置电梯内数字键的监听
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
						//若电梯正在上行，则已选择的最高楼层为目标楼层
						if(direct == 1) {
							goaFloor = 0;
							for (int j = totFloor - 1; j >= 0; j--) {
								if (numbState[j]) {
									goaFloor = j;
									break;
								}
							}
						}
						//若电梯正在下降，则已选择的最低楼层为目标楼层。
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
		//布局电梯移动面板
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
		//开门的线程
		class opendoorThread extends Thread{
			public opendoorThread() {
				start();
			}
			public void run() {
					floorBlock[curFloor].setBackground(Color.orange);
					showlistState.setText("开门");
					showlistState.setBackground(Color.orange);
					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					floorBlock[curFloor].setBackground(floorColor1);
					showlistState.setText("关门");
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
		//开门按钮监听
        open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(direct == 0) {
				    new opendoorThread();	
				}
			}
        	
        });
        //关门的 线程
        class closedoorThread extends Thread{
        	public closedoorThread() {
        		start();
        	}
        	public void run() {
        		floorBlock[curFloor].setBackground(floorColor1);
				showlistState.setText("关门");
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
        //关门按钮监听
        close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(direct == 0) {
			         new closedoorThread();
				}
				
			}
		});
        //报警按钮监听，电梯停止运行
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
        //取消报警，电梯正常运行
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
			//电梯正好停靠在请求层
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
    //电梯向上
	public void listup() throws InterruptedException {
		int oldFloor = curFloor;
		for(int i = curFloor + 1; i <= goaFloor; i++) {
				Thread.sleep(600);
				floorBlock[i].setBackground(floorColor1);
				if(i > oldFloor) {
					floorBlock[i - 1].setBackground(floorColor0);
				}
				//到达目标楼层
				if(numbState[i]) {
					floorBlock[i].setBackground(Color.orange);
					showlistState.setText("开门");
					showlistState.setBackground(Color.orange);
					Thread.sleep(900);
					floorBlock[i].setBackground(floorColor1);
					showlistState.setText("关门");
					showlistState.setBackground(null);
					Thread.sleep(900);
				}
				curFloor = i;
				showcurFloor.setText(""+(curFloor + 1));
			
		}
		setnumbState();            //数字键状态重清
	}
	//电梯向下
	public void listdown() throws InterruptedException {
		int oldFloor = curFloor;
		for(int i = curFloor - 1; i >= goaFloor; i--) {
				Thread.sleep(600);
				floorBlock[i].setBackground(floorColor1);
				if(i < oldFloor) {
					floorBlock[i + 1].setBackground(floorColor0);
				}
				//到达目标楼层
				if(numbState[i]) {
					floorBlock[i].setBackground(Color.orange);
					showlistState.setText("开门");
					showlistState.setBackground(Color.orange);
					Thread.sleep(900);
					floorBlock[i].setBackground(floorColor1);
					showlistState.setText("关门");
					showlistState.setBackground(null);
					Thread.sleep(900);
				}
				curFloor = i;
				showcurFloor.setText(""+(curFloor + 1));
		
		}
		setnumbState();             //数字键状态重清
	}
	//电梯在当前层响应
	public void listcurrent() throws InterruptedException{
		floorBlock[goaFloor].setBackground(Color.orange);
		showlistState.setText("开门");
		showlistState.setBackground(Color.orange);
		Thread.sleep(900);
		floorBlock[goaFloor].setBackground(floorColor1);
		showlistState.setText("关门");
		showlistState.setBackground(null);
		Thread.sleep(900);
		setnumbState();
	}
	//重清数字键状态
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
	//返回线程
	public Thread getthread() {
		return thread;
	}
	//返回电梯状态
	public boolean isUP() {
		return (direct == 1);
	}
	public boolean isDOWN() {
		return (direct == -1);
	}
	public boolean isSTOP() {
		return (direct == 0);
	}
	
	//返回当前电梯所在楼层
	public int getcurFloor() {
		return curFloor;
	}
	
	public boolean getwarnFlag() {
		return warnFlag;
	}
	//设置电梯响应上下键的状态
	public void setList(int floor) {
	if(warnFlag == false) {
		runStart = true;
		//此时电梯未运行
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
		//此时电梯正在运行，并且请求层变成目标楼层
		else if((direct == 1 && floor > goaFloor) || (direct == -1 && floor < goaFloor)) {
			goaFloor = floor;
			numbState[floor] = true;
			floorBlock[floor].setBackground(new Color(148,103,133));
		}
		//此时电梯正在运行，但目标楼层没有改变
		else if((direct == 1 && floor > curFloor) || (direct == -1 && floor < curFloor)) {
			numbState[floor] = true;
			floorBlock[floor].setBackground(new Color(148,103,133));
		}
	 }
   }
}


