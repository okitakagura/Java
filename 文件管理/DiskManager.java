package Filesystem;

import java.io.Serializable;
import java.util.ArrayList;

public class DiskManager implements Serializable{
	 //FAT��
	private char[]fatTable = new char[Utility.NUM_OF_DATABLOCK];
	 //������
	private char[][] disc = new char[Utility.NUM_OF_DATABLOCK][Utility.SIZE_OF_BLOCK];
	private int nowBLOCK;    //��ǰ��
	private int nowNumb;     //��ǰƫ����
	public DiskManager() {
		//��ʼ��fat��
		for(int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
			fatTable[i] = Utility.FREE_FOR_FAT;
		}
		//��ʼ����Ŀ¼,����ǰ���飬 .����ǰ�ļ���  ..�����ϼ��ļ���
		fatTable[0] = 2;
		clearBlock(0);
		disc[0][Utility.POS_NAME] = '.';
		disc[0][Utility.POS_TYPE] = 0;
		disc[0][Utility.POS_FAT] = 0;

		disc[0][Utility.DIR_LEN] = '.';
		disc[0][Utility.DIR_LEN + 1] = '.';
		disc[0][Utility.DIR_LEN + Utility.LEN_OF_NAME] = 2;
	}
	
	//��ʽ����Ϣ
	public void formatAll() {
		for(int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
			fatTable[i] = Utility.FREE_FOR_FAT;
		}
		fatTable[0] = 2;
		clearBlock(0);
		disc[0][Utility.POS_NAME] = '.';
		disc[0][Utility.POS_TYPE] = 0;
		disc[0][Utility.POS_FAT] = 0;

		disc[0][Utility.DIR_LEN] = '.';
		disc[0][Utility.DIR_LEN + 1] = '.';
		disc[0][Utility.DIR_LEN + Utility.LEN_OF_NAME] = 2;
	}
	
	//��ʽ����Ӧ������������������
	public void clearBlock(int i) {
		for(int j =0; j < Utility.SIZE_OF_BLOCK;j++) {
			disc[i][j] = ' ';
		}
	}
	
	//����������һ�ε�����
	public void reload(ArrayList CurDirs, ArrayList CurFiles) {
		CurDirs.clear();
		CurFiles.clear();
		for(int i = 2; i < Utility.NUM_OF_SUBFILE; i++) {
			//�����ͼ��룬ǰ�����ѱ���ʼ����Ŀ¼��
			if(disc[0][i * Utility.DIR_LEN] != ' ') {
				if(disc[0][i * Utility.DIR_LEN + Utility.POS_TYPE] == Utility.DIRECTORY)
				{
					CurDirs.add(getDirectoryName(0, i));		
				}
				else if(disc[0][i * Utility.DIR_LEN + Utility.POS_TYPE] == Utility.FILE)
				{
					CurFiles.add(getDirectoryName(0, i));
				}
			}
		}
	}
	//�����������������
    public void printDisc() {
		System.out.println("disc:");
		for (int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
			for (int j = 0; j < Utility.SIZE_OF_BLOCK; j++) {
				System.out.print(disc[i][j]);
			}
			System.out.println();
		}
	}
	
    //�õ�����(Ŀ¼����ǰ16λ)
    public String getDirectoryName(int curBlock, int i) {
    	char[]directory = new char[Utility.DIR_LEN];
    	for (int j = 0; j < Utility.DIR_LEN; j++) {
			directory[j] = disc[curBlock][i * Utility.DIR_LEN + j];
		}
    	String name = "";
    	for (int j = 0; j < Utility.LEN_OF_NAME; j++) {
			name += directory[j];
		}
		return name.trim();
    }
    
    //��λ
    public void locDir(int curBlock, String name) {
    	int i;
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(name.equals(getDirectoryName(curBlock, i))) { //�ҵ��ļ�
    			break;
    		}
    	}
    	nowBLOCK = curBlock;
    	nowNumb = i;
    }
    
    //�õ��ļ�����
    public String getFileContent(int curBlock, String fileName) {
    	int i;
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(fileName.equals(getDirectoryName(curBlock, i)) 
    		  && disc[curBlock][i*Utility.DIR_LEN + Utility.POS_TYPE] == Utility.FILE) {
    			break;
    		}
    	}
    	//����FAT���ӱ����ҵ��ļ�����
    	if(i != Utility.NUM_OF_SUBFILE) {
    		int textBlockNum = (int)disc[curBlock][i*Utility.DIR_LEN + Utility.POS_FAT];
    		String text = "";
    		do {
        		for(int j = 0; j < 256; j++) {
        			text += disc[textBlockNum][j];
        		}
        		textBlockNum = fatTable[textBlockNum];
        	}while(textBlockNum != Utility.END_OF_FAT);
        	return text.trim();
    	}
    	//�Ҳ����ļ�
    	else {
    		return "";
    	}
    }
    
    //�������ļ�
    public boolean addFile(int curBlock,String name) {
    	int i;
    	//�ҵ��������Ŀ��п�
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(disc[curBlock][i*Utility.DIR_LEN] == ' ') {
    			break;
    		}
    	}
    	//�����ļ���Ϣ
    	if(i != Utility.NUM_OF_SUBFILE) {
    		for(int j = 0; j < name.length(); j++) {
    			disc[curBlock][i*Utility.DIR_LEN + j] = name.charAt(j);
    		}
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_TYPE] = Utility.FILE;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_SIZE] = 0;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_FAT] = (char) assignBlock();
    		return true;
    	}
    	//�޿��п�
    	else {
    		return false;
    	}
    }
    //��������Ŀ¼
    public boolean addDirectory(int curBlock,String name) {
    	int freeBlock = assignBlock();
    	if(freeBlock == -1) {
    		return false;
    	}
    	int i;
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(disc[curBlock][i*Utility.DIR_LEN] == ' ') {
    			break;
    		}
    	}
    	//������Ŀ¼��Ϣ
    	if(i != Utility.NUM_OF_SUBFILE) {
    		for(int j = 0; j < name.length(); j++) {
    			disc[curBlock][i*Utility.DIR_LEN + j] = name.charAt(j);
    		}
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_TYPE] = Utility.DIRECTORY;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_SIZE] = 0;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_FAT] = (char)freeBlock;
    		//����ǰ���飬 .����ǰ�ļ���  ..�����ϼ��ļ���
    		disc[freeBlock][Utility.POS_NAME] = '.';
        	disc[freeBlock][Utility.POS_TYPE] =Utility.DIRECTORY;
        	disc[freeBlock][Utility.POS_FAT] = (char)freeBlock;
        	disc[freeBlock][Utility.DIR_LEN] = '.';
        	disc[freeBlock][Utility.DIR_LEN + 1]='.';
        	disc[freeBlock][Utility.DIR_LEN + Utility.POS_TYPE] = Utility.DIRECTORY;
        	disc[freeBlock][Utility.DIR_LEN + Utility.POS_FAT] = (char)curBlock;
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    //����fat���п��еĿ�
    public int assignBlock() {
    	for(int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
    		if(fatTable[i] == Utility.FREE_FOR_FAT) {
    			fatTable[i] = Utility.END_OF_FAT;
    			//�����Ӧ������
    			for(int j = 0; j < Utility.SIZE_OF_BLOCK; j++) {
    				disc[i][j] = ' ';
    			}
    			return i;
    		}
    	}
    	//FAT������
    	return -1;
    }
    
    //ɾ��Ŀ¼(��Ŀ¼����)
    public void delDirectory(int curBlock,String dirName) {
    	locDir(curBlock,dirName);
    	int block = disc[nowBLOCK][nowNumb * Utility.DIR_LEN + Utility.POS_FAT];
    	delDirectory(block);
    	clearDirectory(nowBLOCK,nowNumb);
    }
    //ɾ��Ŀ¼����������
    public void delDirectory(int block) {
    	for(int i = 2; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(disc[block][i * Utility.DIR_LEN] == ' ') {
    			continue;
    		}
    		//Ŀ¼�����ļ�
    		if(disc[block][i * Utility.DIR_LEN + Utility.POS_TYPE] == Utility.FILE) {
    			delFile(block,i);
    		}
    		//Ŀ¼������Ŀ¼
    		if(disc[block][i * Utility.DIR_LEN + Utility.POS_TYPE] == Utility.DIRECTORY) {
    			int subBlock = disc[block][i * Utility.DIR_LEN + Utility.POS_FAT];
    			delDirectory(subBlock);
    		}
    	}
    	fatTable[block] = Utility.FREE_FOR_FAT;   //FAT������Ϊ����
    }
    
    //ɾ���ļ�(���ļ�����)
    public void delFile(int curBlock, String fileName) {
    	locDir(curBlock, fileName);
    	delFile(nowBLOCK,nowNumb);
    }
    //ɾ���ļ�(��λ��)
    public void delFile(int block,int numb) {
    	delBlocks((int)disc[block][numb * Utility.DIR_LEN + Utility.POS_FAT]);
    	clearDirectory(block,numb);
    }
    //ɾ������Ŀ¼�ļ���Ϣ
    public void clearDirectory(int block, int numb) {
    	for(int i = 0; i < Utility.DIR_LEN; i++) {
    		disc[block][numb * Utility.DIR_LEN + i] = ' ';
    	}
    }
    //�ݹ�ɾ����Ӧ��fat���еĿ�
    public void delBlocks(int blockNumb) {
    	if(fatTable[blockNumb] != Utility.FREE_FOR_FAT 
    		&& fatTable[blockNumb] != Utility.END_OF_FAT) {
    		delBlocks((int)fatTable[blockNumb]);
    	}
    	fatTable[blockNumb] = Utility.FREE_FOR_FAT;
    }
    
    //�ҵ���һ��Ŀ¼��
	public int nextDirectory(int curBlock, String dirName, ArrayList curDirs, ArrayList curFiles) {
		locDir(curBlock, dirName);
		int newCurBlock = disc[nowBLOCK][nowNumb * Utility.DIR_LEN + Utility.POS_FAT];
		curDirs.clear();
		curFiles.clear();
		for(int i = 2; i < Utility.NUM_OF_SUBFILE; i++) {
			String name = getDirectoryName(newCurBlock, i);
			if(name != "" && disc[newCurBlock][i*Utility.DIR_LEN + Utility.POS_TYPE]==Utility.DIRECTORY) {
				curDirs.add(name);
			}
			else if(name != "" && disc[newCurBlock][i*Utility.DIR_LEN + Utility.POS_TYPE]==Utility.FILE) {
				curFiles.add(name);
			}
		}
		return newCurBlock;
	}

	//�ҵ���һ��Ŀ¼��
	public int lastDirectory(int curBlock, ArrayList curDirs, ArrayList curFiles) {
		if(disc[curBlock][Utility.DIR_LEN + Utility.POS_TYPE] == Utility.DIRECTORY) {
			int newCurBlock = disc[curBlock][Utility.DIR_LEN + Utility.POS_FAT];
			curDirs.clear();
			curFiles.clear();
			for(int i = 2; i < Utility.NUM_OF_SUBFILE; i++) {
				String name = getDirectoryName(newCurBlock, i);
				if(name != "" && disc[newCurBlock][i*Utility.DIR_LEN + Utility.POS_TYPE]==Utility.DIRECTORY) {
					curDirs.add(name);
				}
				else if(name != "" && disc[newCurBlock][i*Utility.DIR_LEN + Utility.POS_TYPE]==Utility.FILE) {
					curFiles.add(name);
				}
			}
			return newCurBlock;
		}
		else {
			return -1;   //�Ѿ��Ǹ�Ŀ¼
		}
	}
	
	//�����ļ�
	public boolean saveFile(int curBlock,String fileName, String text) {
		int i;
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(fileName.equals(getDirectoryName(curBlock, i))) {
    			break;
    		}
    	}
    	disc[curBlock][i*Utility.DIR_LEN + Utility.POS_SIZE] = (char)text.length();
    	int textBlockNo = (int) disc[curBlock][i*Utility.DIR_LEN + Utility.POS_FAT];
    	
    	if(fatTable[textBlockNo] != Utility.FREE_FOR_FAT && fatTable[textBlockNo] != Utility.END_OF_FAT) {
    		delBlocks((int)fatTable[textBlockNo]);
    	}
    	for(int j = 0; j <text.length(); j++) {
    		disc[textBlockNo][j] = text.charAt(j);
    	}//һ���������㷨
    	
    	return true;
	}
}
