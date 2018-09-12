package Filesystem;

public class Utility {
	//界面参数
	static public final int WIDTH = 600;
	static public final int HEIGHT = 500;
	//目录表长度
	static public final char DIR_LEN = 32;
	//32的长度中，16位名字，1位类型，1位文件大小（目录该位不用），1位FAT起始块号
	static public char POS_NAME = 0;
	static public char LEN_OF_NAME = 16;
	static public char POS_TYPE = 16;
	static public char POS_SIZE = 17;
	static public char POS_FAT = 18;
	//盘区个数
	static public char NUM_OF_DATABLOCK = 128;
	//盘区大小
	static public char SIZE_OF_BLOCK = 512;
	//（数据区）子目录的文件信息的最大数目（默认只用一个盘区）
    static public char NUM_OF_SUBFILE =(char) (SIZE_OF_BLOCK / DIR_LEN);
	
	//FAT表标记
	static public char FREE_FOR_FAT = 0;
	static public char END_OF_FAT = 1000;
	
	//存储的类型
	static public char FILE = 1;
	static public char DIRECTORY = 0;


}
