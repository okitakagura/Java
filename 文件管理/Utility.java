package Filesystem;

public class Utility {
	//�������
	static public final int WIDTH = 600;
	static public final int HEIGHT = 500;
	//Ŀ¼����
	static public final char DIR_LEN = 32;
	//32�ĳ����У�16λ���֣�1λ���ͣ�1λ�ļ���С��Ŀ¼��λ���ã���1λFAT��ʼ���
	static public char POS_NAME = 0;
	static public char LEN_OF_NAME = 16;
	static public char POS_TYPE = 16;
	static public char POS_SIZE = 17;
	static public char POS_FAT = 18;
	//��������
	static public char NUM_OF_DATABLOCK = 128;
	//������С
	static public char SIZE_OF_BLOCK = 512;
	//������������Ŀ¼���ļ���Ϣ�������Ŀ��Ĭ��ֻ��һ��������
    static public char NUM_OF_SUBFILE =(char) (SIZE_OF_BLOCK / DIR_LEN);
	
	//FAT����
	static public char FREE_FOR_FAT = 0;
	static public char END_OF_FAT = 1000;
	
	//�洢������
	static public char FILE = 1;
	static public char DIRECTORY = 0;


}
