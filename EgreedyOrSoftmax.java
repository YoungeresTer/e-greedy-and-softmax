package com.xiaobing;

import java.util.ArrayList;
import java.util.Random;
/////
// author:xiaobingscuer
// 实现了4 臂赌博机的 softMax和e-greedy两种平衡探索和利用的方式
// 采用的是增量的方式估计动作的Q-value
////
public class EgreedyOrSoftmax {

	public static void main(String[] args) {

// softMax软最大(模拟退火过程)，增量式
				int ENSEMBLE_SCALE=1000;						// 总体规模，这里可表示成执行动作的次数
				Random rand=new Random();						// 随机数
				int action=0;									// 初始化动作，action=0,1,2,3，共四个动作
				float[] actionQValue=new float[4];				// 动作q-value,即动作的Q值估计
				int[] actionCount=new int[4];					// 对四个动作执行的次数进行分别计数
				float[] actionPosib=new float[4];				// 四个动作的概率估计
				float[] sumActionPosib=new float[4];			// 四个动作的概率累计已方便后面选择动作
				ArrayList<Integer> actionSequence=new ArrayList<>(); // 记录依次执行的动作
				float ExpectedReword=0;							// 期望的奖励估计
				float temperature=0.0001f;						// 温度因子
				int[][] ensembleArry=new int[4][ENSEMBLE_SCALE]; // 不同动作的总体，即不同动作的奖励分布
				
		
				// 初始Q-value 的 乐观估计
				actionQValue[0]=5;
				actionQValue[1]=3;
				actionQValue[2]=6;
				actionQValue[3]=10;
		
				for(int i=0;i<ENSEMBLE_SCALE;i++){				// 初始化不同动作的奖励分布；也可以不用一开始就初始化，可在边探索/利用的同时边采样
					ensembleArry[0][i]=rand.nextInt(1000); 		// 我这里先生成动作可能对应的奖励，在后边顺序拿出这些奖励即可
					ensembleArry[1][i]=rand.nextInt(500);		// 这些奖励分别服从区间为（0,1000）、（0,500）、（300,800）、（500,1000）的分布
					ensembleArry[2][i]=rand.nextInt(500)+300;	// 从这个分布可以看出，执行的动作3的期望奖励是最高的，在750
					ensembleArry[3][i]=rand.nextInt(500)+500;				
				}
				
				for(int j=0;j<ENSEMBLE_SCALE;j++){		
						float actionQValueSum=0;						
						for(int k=0;k<4;k++){
							actionQValueSum+=Math.exp(actionQValue[k]*temperature); // 计算这四个动作Q-value的softMax总和				
						}
						for(int i=0;i<4;i++){
							actionPosib[i]=(float) ((Math.exp(actionQValue[i]*temperature))/actionQValueSum); // 计算这四个动作的softMax概率
						}
						sumActionPosib[0]=actionPosib[0];	
						for(int i=0;i<3;i++){
							sumActionPosib[i+1]=actionPosib[i+1]+sumActionPosib[i];	  // 计算累计概率
						}
						float posib=rand.nextFloat();
						for(int i=0;i<4;i++){
							if(posib<=sumActionPosib[i]){		// 根据softMax概率选择动作
								action=i;						// 选出动作
								break;
							}
						}
						temperature+=0.0001;					// 温度因子每次递增，，温度因子改变方式的选择对期望奖励的收敛有较大影响。
						actionSequence.add(action);				
						actionCount[action]+=1;					// 动作计数   // 执行动作并增量式计算Q-value 和 期望奖励估计
						actionQValue[action]=actionQValue[action]+(ensembleArry[action][j]-actionQValue[action])/actionCount[action];
						ExpectedReword=ExpectedReword+(ensembleArry[action][j]-ExpectedReword)/(j+1);					

						System.out.print(ExpectedReword+" ");   // 输出期望的奖励
				}
				System.out.println();
				for(int i=0;i<ENSEMBLE_SCALE;i++){			
					System.out.print(actionSequence.get(i)+" ");// 输出动作序列
				}
				
				for(int i=0;i<4;i++){
					System.out.println();
					System.out.print("动作"+i+" "+actionQValue[i]+" "+actionCount[i]+" "+actionPosib[i]+" "+sumActionPosib[i]);
					// 输出动作相应的Q-value估计，执行的次数，执行动作的概率，以及概率累积
				}
				
//// e-greedy，增量式
//		int ENSEMBLE_SCALE=1000;
//		Random rand=new Random();
//		int action=0;
//		float[] actionQValue=new float[4];
//		int[] actionCount=new int[4];
//		int[][] ensembleArry=new int[4][ENSEMBLE_SCALE];
//		float explorationPosib=0.05f;  							// 探索的概率
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
//		// 初始Q-value 的 乐观估计
//		actionQValue[0]=5;
//		actionQValue[1]=3;
//		actionQValue[2]=6;
//		actionQValue[3]=10;
//		
//		for(int j=0;j<ENSEMBLE_SCALE;j++){		
//				if(rand.nextFloat()<=explorationPosib){  		// 以概率e进行探索，及随机选择一个动作
//					action=rand.nextInt(4);
//					actionSequence.add(action);
//					actionCount[action]+=1;
//					actionQValue[action]=actionQValue[action]+(ensembleArry[action][j]-actionQValue[action])/actionCount[action];
//					ExpectedReword=ExpectedReword+(ensembleArry[action][j]-ExpectedReword)/(j+1);
//				}else{					
//					action=0;
//					for(int i=0;i<3;i++){ 
//						if(actionQValue[i]<actionQValue[i+1]){  // 利用有最大Q-value的动作
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
//			System.out.print("动作"+i+" "+actionQValue[i]+" "+actionCount[i]);
//		}
		
		
// 从总体中采样，并估计总体的总和，采用增量式在线估计总和和均值
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
//			ensemble[i]=i+1;// 总体为1,2,3，，，1000
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
//		System.out.println("这1000个数的总和："+ensembleSum);
//		System.out.println("采样估计的总和："+ensembleSumEstimate);
//		
//		System.out.println("总和的平均："+ensembleAverage);
//		System.out.println("采样估计的平均："+AverageEstimate);
		
	}

}
