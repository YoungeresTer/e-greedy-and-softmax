package com.xiaobing;

import java.util.ArrayList;
import java.util.Random;
/////
// author:xiaobingscuer
// ʵ����4 �۶Ĳ����� softMax��e-greedy����ƽ��̽�������õķ�ʽ
// ���õ��������ķ�ʽ���ƶ�����Q-value
////
public class EgreedyOrSoftmax {

	public static void main(String[] args) {

// softMax�����(ģ���˻����)������ʽ
				int ENSEMBLE_SCALE=1000;						// �����ģ������ɱ�ʾ��ִ�ж����Ĵ���
				Random rand=new Random();						// �����
				int action=0;									// ��ʼ��������action=0,1,2,3�����ĸ�����
				float[] actionQValue=new float[4];				// ����q-value,��������Qֵ����
				int[] actionCount=new int[4];					// ���ĸ�����ִ�еĴ������зֱ����
				float[] actionPosib=new float[4];				// �ĸ������ĸ��ʹ���
				float[] sumActionPosib=new float[4];			// �ĸ������ĸ����ۼ��ѷ������ѡ����
				ArrayList<Integer> actionSequence=new ArrayList<>(); // ��¼����ִ�еĶ���
				float ExpectedReword=0;							// �����Ľ�������
				float temperature=0.0001f;						// �¶�����
				int[][] ensembleArry=new int[4][ENSEMBLE_SCALE]; // ��ͬ���������壬����ͬ�����Ľ����ֲ�
				
		
				// ��ʼQ-value �� �ֹ۹���
				actionQValue[0]=5;
				actionQValue[1]=3;
				actionQValue[2]=6;
				actionQValue[3]=10;
		
				for(int i=0;i<ENSEMBLE_SCALE;i++){				// ��ʼ����ͬ�����Ľ����ֲ���Ҳ���Բ���һ��ʼ�ͳ�ʼ�������ڱ�̽��/���õ�ͬʱ�߲���
					ensembleArry[0][i]=rand.nextInt(1000); 		// �����������ɶ������ܶ�Ӧ�Ľ������ں��˳���ó���Щ��������
					ensembleArry[1][i]=rand.nextInt(500);		// ��Щ�����ֱ��������Ϊ��0,1000������0,500������300,800������500,1000���ķֲ�
					ensembleArry[2][i]=rand.nextInt(500)+300;	// ������ֲ����Կ�����ִ�еĶ���3��������������ߵģ���750
					ensembleArry[3][i]=rand.nextInt(500)+500;				
				}
				
				for(int j=0;j<ENSEMBLE_SCALE;j++){		
						float actionQValueSum=0;						
						for(int k=0;k<4;k++){
							actionQValueSum+=Math.exp(actionQValue[k]*temperature); // �������ĸ�����Q-value��softMax�ܺ�				
						}
						for(int i=0;i<4;i++){
							actionPosib[i]=(float) ((Math.exp(actionQValue[i]*temperature))/actionQValueSum); // �������ĸ�������softMax����
						}
						sumActionPosib[0]=actionPosib[0];	
						for(int i=0;i<3;i++){
							sumActionPosib[i+1]=actionPosib[i+1]+sumActionPosib[i];	  // �����ۼƸ���
						}
						float posib=rand.nextFloat();
						for(int i=0;i<4;i++){
							if(posib<=sumActionPosib[i]){		// ����softMax����ѡ����
								action=i;						// ѡ������
								break;
							}
						}
						temperature+=0.0001;					// �¶�����ÿ�ε��������¶����Ӹı䷽ʽ��ѡ������������������нϴ�Ӱ�졣
						actionSequence.add(action);				
						actionCount[action]+=1;					// ��������   // ִ�ж���������ʽ����Q-value �� ������������
						actionQValue[action]=actionQValue[action]+(ensembleArry[action][j]-actionQValue[action])/actionCount[action];
						ExpectedReword=ExpectedReword+(ensembleArry[action][j]-ExpectedReword)/(j+1);					

						System.out.print(ExpectedReword+" ");   // ��������Ľ���
				}
				System.out.println();
				for(int i=0;i<ENSEMBLE_SCALE;i++){			
					System.out.print(actionSequence.get(i)+" ");// �����������
				}
				
				for(int i=0;i<4;i++){
					System.out.println();
					System.out.print("����"+i+" "+actionQValue[i]+" "+actionCount[i]+" "+actionPosib[i]+" "+sumActionPosib[i]);
					// ���������Ӧ��Q-value���ƣ�ִ�еĴ�����ִ�ж����ĸ��ʣ��Լ������ۻ�
				}
				
//// e-greedy������ʽ
//		int ENSEMBLE_SCALE=1000;
//		Random rand=new Random();
//		int action=0;
//		float[] actionQValue=new float[4];
//		int[] actionCount=new int[4];
//		int[][] ensembleArry=new int[4][ENSEMBLE_SCALE];
//		float explorationPosib=0.05f;  							// ̽���ĸ���
//		ArrayList<Integer> actionSequence=new ArrayList<>();
//		float ExpectedReword=0;	
//		
//		for(int i=0;i<ENSEMBLE_SCALE;i++){
//			ensembleArry[0][i]=rand.nextInt(1000); 
//			ensembleArry[1][i]=rand.nextInt(500);
//			ensembleArry[2][i]=rand.nextInt(500)+300;
//			ensembleArry[3][i]=rand.nextInt(500)+500;				
//		}
//		
//		// ��ʼQ-value �� �ֹ۹���
//		actionQValue[0]=5;
//		actionQValue[1]=3;
//		actionQValue[2]=6;
//		actionQValue[3]=10;
//		
//		for(int j=0;j<ENSEMBLE_SCALE;j++){		
//				if(rand.nextFloat()<=explorationPosib){  		// �Ը���e����̽���������ѡ��һ������
//					action=rand.nextInt(4);
//					actionSequence.add(action);
//					actionCount[action]+=1;
//					actionQValue[action]=actionQValue[action]+(ensembleArry[action][j]-actionQValue[action])/actionCount[action];
//					ExpectedReword=ExpectedReword+(ensembleArry[action][j]-ExpectedReword)/(j+1);
//				}else{					
//					action=0;
//					for(int i=0;i<3;i++){ 
//						if(actionQValue[i]<actionQValue[i+1]){  // ���������Q-value�Ķ���
//							action=i+1;
//						}
//					}
//					actionSequence.add(action);
//					actionCount[action]+=1;
//					actionQValue[action]=actionQValue[action]+(ensembleArry[action][j]-actionQValue[action])/actionCount[action];
//					ExpectedReword=ExpectedReword+(ensembleArry[action][j]-ExpectedReword)/(j+1);
//				}
//				
//				System.out.print(ExpectedReword+" ");
//		}
//		System.out.println();
//		for(int i=0;i<ENSEMBLE_SCALE;i++){			
//			System.out.print(actionSequence.get(i)+" ");
//		}		
//		for(int i=0;i<4;i++){
//			System.out.println();
//			System.out.print("����"+i+" "+actionQValue[i]+" "+actionCount[i]);
//		}
		
		
// �������в�����������������ܺͣ���������ʽ���߹����ܺͺ;�ֵ
//		int SAMPLE_SCALE=10;
//		int ENSEMBLE_SCALE=100;
//		int SAMPLE_NUM=10;
//		int scale=ENSEMBLE_SCALE/SAMPLE_SCALE;	
//		int[] ensemble=new int[ENSEMBLE_SCALE];
//
//		Random rand=new Random();
//		int ensembleSum=0;
//		int ensembleSumEstimate=0;
//		float ensembleAverage=0;
//		float AverageEstimate=0;
//		for(int i=0;i<ENSEMBLE_SCALE;i++){
//			ensemble[i]=i+1;// ����Ϊ1,2,3������1000
//			ensembleSum+=ensemble[i];	
//			ensembleAverage=ensembleAverage+(ensemble[i]-ensembleAverage)/(i+1);				
//		}
//		
//		for(int j=0;j<SAMPLE_NUM;j++){
//			int sum=0;
//			float avg=0;
//			for(int i=0;i<SAMPLE_SCALE;i++){
//				int mark=rand.nextInt(ENSEMBLE_SCALE);	
//				sum+=ensemble[mark]*scale;
//				avg=avg+(ensemble[mark]-avg)/(i+1);			
//			}
//			ensembleSumEstimate=ensembleSumEstimate+(sum-ensembleSumEstimate)/(j+1); 
//			AverageEstimate=AverageEstimate+(avg-AverageEstimate)/(j+1); 
//		}
//
//		System.out.println("��1000�������ܺͣ�"+ensembleSum);
//		System.out.println("�������Ƶ��ܺͣ�"+ensembleSumEstimate);
//		
//		System.out.println("�ܺ͵�ƽ����"+ensembleAverage);
//		System.out.println("�������Ƶ�ƽ����"+AverageEstimate);
		
	}

}
