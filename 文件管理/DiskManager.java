package Filesystem;

import java.io.Serializable;
import java.util.ArrayList;

public class DiskManager implements Serializable{
	 //FAT表
	private char[]fatTable = new char[Utility.NUM_OF_DATABLOCK];
	 //数据区
	private char[][] disc = new char[Utility.NUM_OF_DATABLOCK][Utility.SIZE_OF_BLOCK];
	private int nowBLOCK;    //当前块
	private int nowNumb;     //当前偏移量
	public DiskManager() {
		//初始化fat表
		for(int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
			fatTable[i] = Utility.FREE_FOR_FAT;
		}
		//初始化根目录,利用前两块， .代表当前文件夹  ..代表上级文件夹
		fatTable[0] = 2;
		clearBlock(0);
		disc[0][Utility.POS_NAME] = '.';
		disc[0][Utility.POS_TYPE] = 0;
		disc[0][Utility.POS_FAT] = 0;

		disc[0][Utility.DIR_LEN] = '.';
		disc[0][Utility.DIR_LEN + 1] = '.';
		disc[0][Utility.DIR_LEN + Utility.LEN_OF_NAME] = 2;
	}
	
	//格式化信息
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
	
	//格式化相应的数据区的盘区内容
	public void clearBlock(int i) {
		for(int j =0; j < Utility.SIZE_OF_BLOCK;j++) {
			disc[i][j] = ' ';
		}
	}
	
	//重新载入上一次的数据
	public void reload(ArrayList CurDirs, ArrayList CurFiles) {
		CurDirs.clear();
		CurFiles.clear();
		for(int i = 2; i < Utility.NUM_OF_SUBFILE; i++) {
			//按类型加入，前两块已被初始化根目录。
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
	//输出数据区所有数据
    public void printDisc() {
		System.out.println("disc:");
		for (int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
			for (int j = 0; j < Utility.SIZE_OF_BLOCK; j++) {
				System.out.print(disc[i][j]);
			}
			System.out.println();
		}
	}
	
    //得到名字(目录表项前16位)
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
    
    //定位
    public void locDir(int curBlock, String name) {
    	int i;
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(name.equals(getDirectoryName(curBlock, i))) { //找到文件
    			break;
    		}
    	}
    	nowBLOCK = curBlock;
    	nowNumb = i;
    }
    
    //得到文件内容
    public String getFileContent(int curBlock, String fileName) {
    	int i;
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(fileName.equals(getDirectoryName(curBlock, i)) 
    		  && disc[curBlock][i*Utility.DIR_LEN + Utility.POS_TYPE] == Utility.FILE) {
    			break;
    		}
    	}
    	//根据FAT链接表来找到文件内容
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
    	//找不到文件
    	else {
    		return "";
    	}
    }
    
    //加入新文件
    public boolean addFile(int curBlock,String name) {
    	int i;
    	//找到数据区的空闲块
    	for(i = 0; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(disc[curBlock][i*Utility.DIR_LEN] == ' ') {
    			break;
    		}
    	}
    	//分配文件信息
    	if(i != Utility.NUM_OF_SUBFILE) {
    		for(int j = 0; j < name.length(); j++) {
    			disc[curBlock][i*Utility.DIR_LEN + j] = name.charAt(j);
    		}
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_TYPE] = Utility.FILE;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_SIZE] = 0;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_FAT] = (char) assignBlock();
    		return true;
    	}
    	//无空闲块
    	else {
    		return false;
    	}
    }
    //加入新子目录
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
    	//分配子目录信息
    	if(i != Utility.NUM_OF_SUBFILE) {
    		for(int j = 0; j < name.length(); j++) {
    			disc[curBlock][i*Utility.DIR_LEN + j] = name.charAt(j);
    		}
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_TYPE] = Utility.DIRECTORY;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_SIZE] = 0;
    		disc[curBlock][i*Utility.DIR_LEN + Utility.POS_FAT] = (char)freeBlock;
    		//利用前两块， .代表当前文件夹  ..代表上级文件夹
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
    
    //分配fat表中空闲的块
    public int assignBlock() {
    	for(int i = 0; i < Utility.NUM_OF_DATABLOCK; i++) {
    		if(fatTable[i] == Utility.FREE_FOR_FAT) {
    			fatTable[i] = Utility.END_OF_FAT;
    			//清空相应数据区
    			for(int j = 0; j < Utility.SIZE_OF_BLOCK; j++) {
    				disc[i][j] = ' ';
    			}
    			return i;
    		}
    	}
    	//FAT表已满
    	return -1;
    }
    
    //删除目录(按目录名字)
    public void delDirectory(int curBlock,String dirName) {
    	locDir(curBlock,dirName);
    	int block = disc[nowBLOCK][nowNumb * Utility.DIR_LEN + Utility.POS_FAT];
    	delDirectory(block);
    	clearDirectory(nowBLOCK,nowNumb);
    }
    //删除目录（按盘区）
    public void delDirectory(int block) {
    	for(int i = 2; i < Utility.NUM_OF_SUBFILE; i++) {
    		if(disc[block][i * Utility.DIR_LEN] == ' ') {
    			continue;
    		}
    		//目录下有文件
    		if(disc[block][i * Utility.DIR_LEN + Utility.POS_TYPE] == Utility.FILE) {
    			delFile(block,i);
    		}
    		//目录下有子目录
    		if(disc[block][i * Utility.DIR_LEN + Utility.POS_TYPE] == Utility.DIRECTORY) {
    			int subBlock = disc[block][i * Utility.DIR_LEN + Utility.POS_FAT];
    			delDirectory(subBlock);
    		}
    	}
    	fatTable[block] = Utility.FREE_FOR_FAT;   //FAT表设置为空闲
    }
    
    //删除文件(按文件名字)
    public void delFile(int curBlock, String fileName) {
    	locDir(curBlock, fileName);
    	delFile(nowBLOCK,nowNumb);
    }
    //删除文件(按位置)
    public void delFile(int block,int numb) {
    	delBlocks((int)disc[block][numb * Utility.DIR_LEN + Utility.POS_FAT]);
    	clearDirectory(block,numb);
    }
    //删除单条目录文件信息
    public void clearDirectory(int block, int numb) {
    	for(int i = 0; i < Utility.DIR_LEN; i++) {
    		disc[block][numb * Utility.DIR_LEN + i] = ' ';
    	}
    }
    //递归删除相应的fat表中的块
    public void delBlocks(int blockNumb) {
    	if(fatTable[blockNumb] != Utility.FREE_FOR_FAT 
    		&& fatTable[blockNumb] != Utility.END_OF_FAT) {
    		delBlocks((int)fatTable[blockNumb]);
    	}
    	fatTable[blockNumb] = Utility.FREE_FOR_FAT;
    }
    
    //找到下一级目录块
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

	//找到上一级目录快
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
			return -1;   //已经是根目录
		}
	}
	
	//保存文件
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
    	}//一块盘区的算法
    	
    	return true;
	}
}
