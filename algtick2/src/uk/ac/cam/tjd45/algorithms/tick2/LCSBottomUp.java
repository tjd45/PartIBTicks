package uk.ac.cam.tjd45.algorithms.tick2;

import uk.ac.cam.rkh23.Algorithms.Tick2.LCSFinder;

public class LCSBottomUp extends LCSFinder{

	public LCSBottomUp(String s1, String s2) {
		super(s1, s2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLCSLength() {
		if ((this.mString1.length()==0)||(this.mString2.length()==0))
			return 0;
		else
		{
			int LCS = 0;
			this.mTable = new int[this.mString1.length()+1][this.mString2.length()+1];
			char[] string1 = this.mString1.toCharArray();
			char[] string2 = this.mString2.toCharArray();

			for (int i = 0; i<string1.length+1;i++){
				for (int j = 0; j<string2.length+1;j++)
					this.mTable[i][j]=0;
			};

			for (int j = 0; j<string2.length;j++){
				for (int i = 0; i<string1.length;i++){

					if (string1[i]==string2[j]){

						this.mTable[i+1][j+1]=1+this.mTable[i][j];

					}else{
						this.mTable[i+1][j+1]=Math.max(this.mTable[i][j+1],this.mTable[i+1][j]);;
					}
				}

			};

			int[][] newmTable = new int[this.mString1.length()][this.mString2.length()];

			for (int i = 1;i<this.mString1.length()+1;i++){
				for (int j = 1;j<this.mString2.length()+1;j++){
					newmTable[i-1][j-1]=this.mTable[i][j];
				}
			}

			this.mTable=newmTable;

			for (int j = 0;j<this.mString2.length();j++){
				for (int i = 0;i<this.mString1.length();i++){

					System.out.print(/*" ("+string1[i-1]+","+string2[j-1]+"):"+*/this.mTable[i][j]);
				}
				System.out.println();
			};


			return this.mTable[this.mString1.length()-1][this.mString2.length()-1];
		}

	}

	@Override
	public String getLCSString() {
		String LCS = "";
		if ((this.mString1.length()==0)||(this.mString2.length()==0))
			return LCS;
		else{
			int i = this.mString1.length()-1;
			int j = this.mString2.length()-1;
			char[] string1 = this.mString1.toCharArray();

			int current = this.mTable[i][j];

			while (current >0 ){
				if ((j-1 > -1)&&(i-1>-1)){
					if (this.mTable[i][j-1] != this.mTable[i][j]){
						if (this.mTable[i-1][j] != this.mTable[i][j]){
							LCS = string1[i] + LCS;
							i--;
							j--;
							current = this.mTable[i][j];
						}else
							i--;
					}else
						j--;
				}else{
					if((i-1<0)&&(j-1<0)){
						LCS = string1[i]+LCS;
						current = 0;
					}else{
					if((j-1 < 0)&&(i-1 > -1)){
					if(this.mTable[i-1][j] != this.mTable[i][j]){
						LCS = string1[i] + LCS;
						i--;
						current = this.mTable[i][j];
					}else
						i--;
				}else{if((i-1 < 0)&&(j-1 > -1)){
					if(this.mTable[i][j-1] != this.mTable[i][j]){
						LCS = string1[i] + LCS;
						j--;
						current = this.mTable[i][j];
					}else
						j--;

				}

				}
					}

				}


			}

			return LCS;
		}
	}

	public static void main(String[] args){
		LCSBottomUp x = new LCSBottomUp("ABBA","CACA");
		System.out.println(x.getLCSLength());
		System.out.println(x.getLCSString());

	}

}
